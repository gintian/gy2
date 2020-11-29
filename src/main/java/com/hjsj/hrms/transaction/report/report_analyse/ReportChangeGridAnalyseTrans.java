/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:报表单击格图表联动</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 8, 2006:9:15:05 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportChangeGridAnalyseTrans extends IBusiness {


	public void execute() throws GeneralException {
		String type = "";
		type = (String)this.getFormHM().get("type");
		String selectType = "";//得到页面的选中类型（点、行、列）
		selectType = (String)this.getFormHM().get("selectType");
		if("manager".equals(type)){//判断是否是走Ajax过来的
			//获取当前用户的对应的填报单位编号
			String unitCode = "";
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			ttorganization.setValidedateflag("1");
			ttorganization.setBackdate("");
			RecordVo selfVo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null){
				selfVo = ttorganization.getSelfUnit3(userView.getS_userName());
			}else{
				selfVo=ttorganization.getSelfUnit(userView.getUserName());
			}
			if(selfVo!=null){
				unitCode = selfVo.getString("unitcode");
			}
			String tabid = (String)this.getFormHM().get("tabid");
			String char_type=(String)this.getFormHM().get("char_type");//1:柱状图 2：线状图
			String years = (String)this.getFormHM().get("years");//近几年
			years=StringUtils.isEmpty(years)?"1":years;
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			}
			String showFlag=(String)this.getFormHM().get("showFlag");
			String r_c="";
			r_c=(String)this.getFormHM().get("rc");
			if(r_c!=null&&!"null".equals(r_c))//判断选中的点是否为空
			{
				String w="";
				String h="";
				w = (String)this.getFormHM().get("w");
				h = (String)this.getFormHM().get("h");
		
				HashMap dataMap=new HashMap();
				String appDate = userView.getAppdate().substring(0,4);
				ArrayList yearList = new ArrayList();
				for(int i=0; i<Integer.parseInt(years); i++){
					yearList.add((Integer.parseInt(appDate)-i));
				}
				ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
				rpdba.changeReportSelectGrids(unitCode ,tabid ,r_c,selectType,yearList);
				this.getFormHM().put("chartWidth",w);
				this.getFormHM().put("chartHeight",h);
				//实例化这里的char_type并没有用，因为这里生成图时直接取的数据源
				EchartsBo chartbo=new EchartsBo("",1, Integer.parseInt(w),Integer.parseInt(h),"true");
				chartbo.setNumDecimals(0);		
				chartbo.setBg_color("#F4F7F7");
				chartbo.setChartpnl("showChart");
				if(r_c.split("/").length>20){
					chartbo.setXangle(60);
				}else if(r_c.split("/").length>9){
					chartbo.setXangle(45);
				}else{
					chartbo.setXangle(0);	
				}
				chartbo.setIsneedsum("true");
				chartbo.setTooltip_enabled("true");
				//直接得到数据源
				String showReportChart = "";
				if("2".equals(char_type))//折线图
					showReportChart=chartbo.outEchartLineXml(rpdba.getChartDBmap(),"","");
				else if("1".equals(char_type))//分组柱状图
					showReportChart=chartbo.outEchartGalleryBarXml(rpdba.getChartDBList(),"","");
				showReportChart=SafeCode.encode(showReportChart);
				this.getFormHM().put("showReport",showReportChart);
			}
			
		}else {
			HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
			String unitCode = (String)this.getFormHM().get("unitCode");
			String tabid = (String)this.getFormHM().get("tabid");
			String char_type=(String)this.getFormHM().get("char_type");  // 1:柱状图 2：线状图
			
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			
			String row ="";
			String col="";
			String unitcodes="";
			String showFlag=(String)this.getFormHM().get("showFlag");
			String r_c=(String)map.get("rc");
			if(r_c!=null&&!"null".equals(r_c))
			{
				r_c = PubFunc.keyWord_reback(r_c);  //add by wangchaoqun on 2014-9-16
				if(r_c.length()<3||("2".equals(showFlag)&&r_c.indexOf("/")!=-1))
				{
					String w = (String)map.get("w");
					String h = (String)map.get("h");
					this.getFormHM().put("chartTitle","");
					this.getFormHM().put("chartFlag","no");
					this.getFormHM().put("chartWidth",w);
					this.getFormHM().put("chartHeight",h);
				}
				else
				{
					if("2".equals(showFlag))  //按单位分析
					{
						unitcodes=(String)map.get("unitcodes");
						String[] temp=r_c.split(",");
						row =temp[0];
						col =temp[1];;
					}
					String w = (String)map.get("w");
					String h = (String)map.get("h");
					
			
					HashMap dataMap=new HashMap();
					ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
					if("2".equals(showFlag))
					{
							rpdba.changeReportGrid(unitcodes,tabid,row,col);
					}
					else if("1".equals(showFlag))
					{
						    rpdba.changeReportGrid2(unitCode ,tabid ,r_c);
					}
					
					this.getFormHM().put("chartTitle",rpdba.getReportGridTitle());
					if("1".equals(char_type))
						this.getFormHM().put("list" ,rpdba.getChartDBList());
					else
						this.getFormHM().put("dataMap" ,rpdba.getChartDBmap());
					
					this.getFormHM().put("chartFlag","yes");
					if("2".equals(char_type))
						this.getFormHM().put("chartType", "11");  //柱状图
					else if("1".equals(char_type))
						this.getFormHM().put("chartType", "299");  //分组柱状图 update by xiegh on date 20180604 报表管理里面的柱状图以指标分组 
					this.getFormHM().put("chartWidth",w);
					this.getFormHM().put("chartHeight",h);
				}
			}
			else
			{
				String w = (String)map.get("w");
				String h = (String)map.get("h");
				this.getFormHM().put("chartTitle","");
				this.getFormHM().put("chartFlag","no");
				this.getFormHM().put("chartWidth",w);
				this.getFormHM().put("chartHeight",h);
			}
		}
		
	}

}
