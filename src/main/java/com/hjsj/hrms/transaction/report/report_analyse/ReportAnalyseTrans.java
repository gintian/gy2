/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportAnalyseHtmlBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 3, 2006:1:10:59 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportAnalyseTrans extends IBusiness {

	/**
	 * 分析： 首先通过填报单位编号获得当前用户所负责的报表组
	 *       对默认的报表分析（报表集合的第一个报表）
	 */
	public void execute() throws GeneralException {
		
		//填报单位ID
		String unitCode = (String)this.getFormHM().get("unitCode");
		String currentReport = (String)this.getFormHM().get("currentReport");
		String reportTabid = (String)this.getFormHM().get("reportTabid");
		String reportHeight = (String)this.getFormHM().get("reportHeight");
		//如果填报单位编号为空
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//liuy 2014-8-29 begin
		String browse = (String)hm.get("b_browse");//员工总量变化趋势参数
		if(browse!=null){//因为和报表浏览走同一个类，所以在取得值后清空hashmap中的值
			hm.remove("b_browse");
		}//liuy 2014-8-29 end
		String backdate =(String)hm.get("backdate");
		this.getFormHM().put("backdate2", backdate);
		hm.remove("backdate");
		/***为了解决bug添加****/
		String unitcode2=(String)hm.get("ucode");
		String dmlyearid=(String)hm.get("dmlyearid");
		if(unitcode2!=null&&unitcode2.length()!=0){
			unitCode=unitcode2;
		}
		String dmlcountid=(String)hm.get("dmlcountid");
		String dmlweekid=(String)hm.get("dmlweekid");
		/*************/
		String init=(String)hm.get("init");
		hm.remove("init");
//		获取当前用户的对应的填报单位编号
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		ttorganization.setValidedateflag("1");
		ttorganization.setBackdate(backdate);
		//维护归档表的结构提示标志
		String columnflag="";
		RecordVo selfVo=null;
		if(userView.getStatus()==4&&userView.getS_userName()!=null)
			selfVo = ttorganization.getSelfUnit3(userView.getS_userName());
		else
			selfVo=ttorganization.getSelfUnit(userView.getUserName());
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(unitCode == null || "".equals(unitCode)||(init!=null&& "init".equals(init))){
			if(userView.isSuper_admin()){
				Calendar d=Calendar.getInstance();
				StringBuffer ext_sql = new StringBuffer();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				String sql = "select unitcode  from tt_organization where unitcode = parentid "+ext_sql+" order by unitid";
				try {
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						unitCode = this.frowset.getString("unitcode");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}else{
				if(selfVo!=null)
					unitCode = selfVo.getString("unitcode");
			}
			currentReport="";
			reportTabid ="";
		}
	
		ArrayList reportList = new ArrayList(); 
		ArrayList reportYearList = new ArrayList();
		ArrayList reportCounitidList = new ArrayList();
		ArrayList reportWeekList=new ArrayList();
		
		ReportPDBAnalyse rpda = new ReportPDBAnalyse(this.getFrameconn());
		//liuy 2014-8-29 begin
		if(browse != null){//改变生成报表类的BrowseFlag的值，控制生成报表时的上间距
			rpda.setBrowseFlag(true);
		}//liuy 2014-8-29 end
		UserView _userview=null;
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
		{
			_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
			try
			{
				_userview.canLogin();
				rpda.setUserView(_userview);
			}
			catch(Exception e)
			{
				
			}
		}
		else
			rpda.setUserView(this.userView);
		
		//rpda.changeReportUnitTree(unitCode , userView.getUserId(),userView.getUserName());
		if(selfVo==null)
		{
			this.getFormHM().put("reportExist","no");
			return;
		}
		ArrayList reportSortList=rpda.getReportSortList(selfVo.getString("unitcode"));
		if(reportSortList.size()==0)
		{
				this.getFormHM().put("reportExist","no");
				return;
		}
		String reportSortID=(String)this.getFormHM().get("reportSortID");
		if((reportSortID==null|| "".equals(reportSortID))&&reportSortList.size()>0)
		{
				reportSortID=((CommonData)reportSortList.get(0)).getDataValue();				
		}
		
		this.getFormHM().put("reportSortList",reportSortList);
		this.getFormHM().put("reportSortID",reportSortID==null?"":reportSortID);
		if(reportSortID!=null&&!"".equals(reportSortID))
		{
			reportList=rpda.getReportList(reportSortID,selfVo.getString("unitcode"));
		}
		else
			reportList = rpda.getReportList();
		
		boolean isExist=false;
		for(int i=0; i<reportList.size();i++ ){
			CommonData cd = (CommonData) reportList.get(i);
			if(cd.getDataValue().equalsIgnoreCase(currentReport))
				isExist=true;
		}
		if(isExist==false)
		{
			if(reportList.size()>0)
			{
				reportTabid=((CommonData)reportList.get(0)).getDataValue();
				currentReport=((CommonData)reportList.get(0)).getDataValue();
			}
			/*else
			{
				currentReport="";
			}*/
		}
			//liuy 2014-9-1 begin 实现员工总量变化趋势选中指定报表
			String tabid = (String)hm.get("tabid");
			if(StringUtils.isNotBlank(tabid)&&!"null".equalsIgnoreCase(tabid)){//因为和报表浏览走同一个类，所以在取得值后清空hashmap中的值
				hm.remove("tabid");
				reportTabid = tabid;
				if(reportTabid!=null){					
					boolean flag=getReportFlag(reportTabid);
					if(!flag){
						throw new GeneralException("配置的报表不存在！");
					}
				}
				ArrayList yearList = this.getYearList(tabid, dao);
				this.getFormHM().put("yearList",yearList);//保存近几年
				this.getFormHM().put("years2",this.getYearList(yearList.size()+"", tabid));//保存近几年
			}
			//liuy 2014-9-1 end
			TnameBo tbo =null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				tbo=new TnameBo(this.getFrameconn(),reportTabid,_userview.getUserId(),_userview.getUserName(),"temp");
			else
				tbo=new TnameBo(this.getFrameconn(),reportTabid,userView.getUserId(),userView.getUserName(),"temp");
	//	rpda.changeReportUnitTree(unitCode , userView.getUserId(),userView.getUserName(),currentReport,reportSortID);
		//liuy 2014-9-10 判断是否有甲行和编号列 begin
		ArrayList colInfoBGridList = tbo.getColInfoBGrid();
		ArrayList rowInfoBGridList = tbo.getRowInfoBGrid();
		String colFlag = "false";
		String rowFlag = "false";
		for(int i = 0;i<colInfoBGridList.size();i++){
			RecordVo colVo = (RecordVo) colInfoBGridList.get(i);
			if (colVo.getInt("flag1") == 4){
				colFlag = "true";
				break;
			}
		}
		for(int i = 0;i<rowInfoBGridList.size();i++){
			RecordVo rowVo = (RecordVo) rowInfoBGridList.get(i);
			if (rowVo.getInt("flag1") == 4){
				rowFlag = "true";
				break;
			}
		}
		this.getFormHM().put("colFlag",colFlag);
		this.getFormHM().put("rowFlag",rowFlag);
		//liuy 2014-9-10 判断是否有甲行和编号列  end
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			rpda.changeReportTabid(reportTabid,unitCode,_userview.getUserId(),_userview.getUserName(),tbo);
		else
			rpda.changeReportTabid(reportTabid,unitCode,userView.getUserId(),userView.getUserName(),tbo);
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			columnflag=	rpda.tableColumnChange(reportTabid, _userview.getUserId(),_userview.getUserName(),tbo);
		else
			columnflag=rpda.tableColumnChange(reportTabid, userView.getUserId(),userView.getUserName(),tbo);
		reportYearList = rpda.getReportYearidList();
		reportCounitidList = rpda.getReportCountidList();
		reportWeekList=rpda.getReportWeekList();
		
		
		String rows=String.valueOf(tbo.getColInfoBGrid().size());
		String cols=String.valueOf(tbo.getRowInfoBGrid().size());
		hm.remove("ucode");
		//liuy 2014-9-1 begin 查询得到报表的总高度
		reportHeight = rpda.getRepoetAnalyseHeigh(reportTabid);
		this.getFormHM().put("reportHeight", reportHeight);
		//this.getFormHM().put("reportTabid", reportTabid);
		//liuy 2014-9-1 end
		this.getFormHM().put("selfUnitcode",unitCode);
		this.getFormHM().put("rows",rows);
		this.getFormHM().put("cols",cols);
		this.getFormHM().put("codeFlag",unitCode);
		this.getFormHM().put("reportTypes",rpda.getReportTypes());
		this.getFormHM().put("reportList",reportList);
		this.getFormHM().put("reportYearList" , reportYearList);
		this.getFormHM().put("reportCounitidList" ,reportCounitidList);
		this.getFormHM().put("reportWeekList",reportWeekList);
		this.getFormHM().put("reportCountInfo" , rpda.getReportCountInfo());
		this.getFormHM().put("columnflag" ,columnflag);
		int reportflag=rpda.getReportFlag(reportTabid);
		if((dmlyearid!=null&&dmlyearid.length()!=0)||(dmlcountid!=null&&dmlcountid.length()!=0)||dmlweekid!=null&&dmlweekid.length()!=0){
			ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.getFrameconn());
			rahbo.setUserView(userView);
			if(dmlweekid!=null&&dmlweekid.length()!=0){
				rahbo.setWeekid(dmlweekid);
			}
			if(dmlcountid==null){
				if(reportflag==2){
					dmlcountid="1";
				}else{
					dmlcountid="0";
				}
				
			}
			this.getFormHM().put("reportHtml" ,rahbo.creatHtmlView(unitCode ,reportTabid , dmlyearid , dmlcountid ,tbo ,String.valueOf(reportflag)));
			this.getFormHM().put("reportYearid", dmlyearid);
			this.getFormHM().put("reportCount", dmlcountid);
			this.getFormHM().put("weekid", dmlweekid);
		}else{
			this.getFormHM().put("reportHtml" ,rpda.getReportHtml());
		}
		
		
		String reportState = rpda.getReportState();
		this.getFormHM().put("reportState",reportState);
		
		
		//存在归档数据
		if("null".equals(reportState)){
			ArrayList list = rpda.getChartDBList();
			this.getFormHM().put("list",list);
			this.getFormHM().put("chartTitle" ,rpda.getReportGridTitle());
			this.getFormHM().put("chartFlag" ,"yes");
		}else{
			this.getFormHM().put("chartFlag" ,"no");
		}
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&&!"dxt".equals(dxt))
			hm.remove("returnvalue");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
		//控制报表下拉列表的默认值
		this.getFormHM().put("optionFlag","yes");
	
		this.getFormHM().put("reportExist",rpda.getReportExist());
		this.getFormHM().put("currentReport",/*rpda.getCurrentReport()*/reportTabid);
		this.getFormHM().put("reportTabid",reportTabid);
		((HashMap)(this.getFormHM().get("requestPamaHM"))).put("code","");
	}
	
	/**
	 * 判断报表是否存在
	 * @param reportTabid
	 * @return
	 */
	private boolean getReportFlag(String reportTabid){
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			String sqlName="select * from tname where tabid=?";
			ArrayList<String> value = new ArrayList<String>();
			value.add(reportTabid);
			this.frowset=dao.search(sqlName, value);
			if(this.frowset.next())
			{
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private ArrayList getYearList(String tabid,ContentDAO dao){
		ArrayList list = new ArrayList();
		try {
			String appDate = userView.getAppdate().substring(0,4);
			StringBuffer sql = new StringBuffer();
			sql.append(" select count(yearid) years from");
			sql.append(" (select distinct yearid from ta_");
			sql.append(tabid);
			//sql.append(" where yearid>");
			//sql.append(Integer.parseInt(appDate)-5);
			sql.append(") y");
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				int years = Integer.parseInt(this.frowset.getString("years"));
				years = years>5? 5:years;//近五年 wangb 20190822 bug 52241
				ArrayList yearList = new ArrayList();
				for(int i=1; i<=years; i++){
					CommonData vo=new CommonData(i+"","最近"+ i +"年");
					list.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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

}
