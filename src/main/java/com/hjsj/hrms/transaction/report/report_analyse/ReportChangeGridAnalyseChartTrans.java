package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 报表切换选择内容，实时更新分析图
 * <p>Title: ReportChangeGridAnalyseChartTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Sep 12, 2015 2:36:27 PM</p>
 * @author liuy
 * @version 7.x
 */
public class ReportChangeGridAnalyseChartTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String r_c = (String)this.getFormHM().get("rc");//选中内容
		if(StringUtils.isNotEmpty(r_c))//判断是否选中内容
		{
			String selectType =  (String)this.getFormHM().get("selectType");//得到页面的选中类型（点、行、列）
			String unitCode = this.getUnitCode();//填报单位编号
			String tabid = (String)this.getFormHM().get("tabid");//报表编号
			String chart_type=(String)this.getFormHM().get("char_type");//1:分组柱状图 2：折线图
			
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))//是否有此表资源权限
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			
			String years = (String)this.getFormHM().get("years");//近几年
			years=StringUtils.isEmpty(years)?"1":years;//为空默认为近一年
			String width = (String)this.getFormHM().get("w");//统计图宽度
			String height = (String)this.getFormHM().get("h");//统计图高度
			/*add by xiegh on 20180614 项目：38312 切换年份时  点击行数据没有根据当前的年份对图表进行刷新  */
			String yearid = (String)this.getFormHM().get("yearid");
			ArrayList yearList = new ArrayList();
			if(StringUtils.isEmpty(yearid))
				yearList = getYearList(years, tabid);
			else
				yearList.add(Integer.parseInt(yearid));
			String showReportChart = this.getShowReportChart(unitCode, tabid, r_c, selectType, 
					yearList, Integer.parseInt(chart_type), Integer.parseInt(width), Integer.parseInt(height));
			
			this.getFormHM().put("chartWidth",width);
			this.getFormHM().put("chartHeight",height);
			this.getFormHM().put("showReport",showReportChart);
		}
	}
	
	/**
	 * 获取当前用户的对应的填报单位编号
	 * @return
	 */
	private String getUnitCode(){
		String unitCode = "";
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		ttorganization.setValidedateflag("1");
		ttorganization.setBackdate("");
		RecordVo selfVo=null;
		if(userView.getStatus()==4&&userView.getS_userName()!=null)
			selfVo = ttorganization.getSelfUnit3(userView.getS_userName());
		else
			selfVo=ttorganization.getSelfUnit(userView.getUserName());
		if(selfVo!=null)
			unitCode = selfVo.getString("unitcode");
		return unitCode;
	}
	
	/**
	 * 根据years参数和业务日期得到近几年的具体几年
	 * @param years
	 * @return
	 */
	private ArrayList getYearList(String years){
		ArrayList yearList = new ArrayList();
		String appDate = userView.getAppdate().substring(0,4);//业务日期年
		for(int i=0; i<Integer.parseInt(years); i++){
			yearList.add((Integer.parseInt(appDate)-i));
		}
		return yearList;
	}
	/**
	 * 根据years参数和业务日期得到数据库中最近几年是具体的 哪几年
	 * @param years
	 * @return
	 */
	private ArrayList getYearList(String years, String tabid){
		ArrayList yearList = new ArrayList();
		try {
			ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.frameconn);
			String reportTypes = String.valueOf(rpdba.getReportFlag(tabid));
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct yearid");
			
			if("6".equals(reportTypes))
				sql.append(",weekid");
			
			sql.append(" from ta_"+tabid);
			sql.append(" order by yearid desc");
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql.toString());
			int i = 0;
			while (i < Integer.parseInt(years) && this.frowset.next()) {
				String year = this.frowset.getString("yearid");
				yearList.add(Integer.parseInt(year));
				i++;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return yearList;
	}
	
	
	
	/**
	 * 生成图表数据源
	 * @param unitCode
	 * @param tabid
	 * @param r_c
	 * @param selectType
	 * @param yearList
	 * @param chart_type
	 * @param width
	 * @param height
	 * @return
	 */
	private String getShowReportChart(String unitCode, String tabid, String r_c, String selectType, 
			ArrayList yearList, int chart_type, int width, int height){
		
		String showReportChart = "";//图表数据源
		
		ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.frameconn);
		try {
			rpdba.changeReportSelectGrids(unitCode ,tabid ,r_c,selectType,yearList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//实例化这里的char_type并没有用，因为这里生成图时直接取的数据源
		EchartsBo chartbo=new EchartsBo("",chart_type, width,height,"true");
		chartbo.setNumDecimals(2);//小数点位数
		chartbo.setBg_color("#F4F7F7");
		chartbo.setChartpnl("showChart");
		if(r_c.split("/").length>20){
			chartbo.setXangle(60);
		}else if(r_c.split("/").length>9){
			chartbo.setXangle(45);
		}else{
			chartbo.setXangle(0);	
		}
		chartbo.setIsneedsum("false");
		chartbo.setTooltip_enabled("true");

		if(chart_type == 11 || chart_type == 1)
			showReportChart=chartbo.outEchartGalleryBarXml(rpdba.getChartDBList(),"","");
		else if(chart_type == 27 || chart_type == 2)
			showReportChart=chartbo.outEchartLineXml(rpdba.getChartDBmap(),"","");
		showReportChart=SafeCode.encode(showReportChart);//加密
		return showReportChart;
	}

}
