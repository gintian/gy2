/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:报表归档数据分析</p>
 * <p>Description:改变报表后的连动</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 5, 2006:9:27:02 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportChangeTabidAnalyseTrans extends IBusiness {
	
	/**
	 * 改变报表后的数据连动
	 * 1 获得改变后的表号
	 * 2 根据表号连动修改
	 */
	public void execute() throws GeneralException {
		HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String tabid = (String)this.getFormHM().get("tabid");       //报表表号
		String codeFlag = (String)this.getFormHM().get("unitCode"); //填报单位编号
		String reportTabid = (String)this.getFormHM().get("reportTabid");
		String reportSortID=(String)this.getFormHM().get("reportSortID");
		String columnflag="";
		ArrayList reportYearList = new ArrayList();        //年集合
		ArrayList reportCounitidList = new ArrayList();    //次数集合
		ArrayList reportWeekList=new ArrayList();
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		ReportPDBAnalyse rpda = new ReportPDBAnalyse(this.getFrameconn());
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
		
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		RecordVo selfVo=null;
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			selfVo=ttorganization.getSelfUnit(userView.getS_userName());
		else
			selfVo=ttorganization.getSelfUnit(userView.getUserName());
		
		ArrayList reportList=rpda.getReportList(reportSortID,selfVo.getString("unitcode"));
		this.getFormHM().put("reportList" , reportList);
		
		boolean isExist=false;
		for(int i=0;i<reportList.size();i++)
		{
			CommonData data=(CommonData)reportList.get(i);
			if(data.getDataValue().equalsIgnoreCase(tabid))
				isExist=true;
		}
		if(!isExist)	
		{	if(reportList.size()>0)
			{
			    tabid=((CommonData)reportList.get(0)).getDataValue();
			}
		}
		/**
		 * 周归挡表当变换月份时,执行特殊操作
		 */
		if(map.get("opt")!=null&& "count".equals((String)map.get("opt")))
		{
			rpda.setCountid((String)this.getFormHM().get("reportCount"));
			rpda.setYearid((String)this.getFormHM().get("reportYearid"));
			map.remove("opt");
		}
		if(map.get("opt")!=null&& "year".equals((String)map.get("opt")))
		{
			rpda.setYearid((String)this.getFormHM().get("reportYearid"));
			map.remove("opt");
		}
		TnameBo tbo =null;
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			tbo=new TnameBo(this.getFrameconn(),reportTabid,_userview.getUserId(),_userview.getUserName(),"temp");
		else
			tbo=new TnameBo(this.getFrameconn(),reportTabid,userView.getUserId(),userView.getUserName(),"temp");
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			rpda.changeReportTabid(reportTabid,codeFlag,_userview.getUserId(),_userview.getUserName(),tbo);
		else
			rpda.changeReportTabid(reportTabid,codeFlag,userView.getUserId(),userView.getUserName(),tbo);
		reportYearList = rpda.getReportYearidList();
		reportCounitidList = rpda.getReportCountidList();
		reportWeekList=rpda.getReportWeekList();
	
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			columnflag=	rpda.tableColumnChange(reportTabid, _userview.getUserId(),_userview.getUserName(),tbo);
		else
			columnflag=rpda.tableColumnChange(reportTabid, userView.getUserId(),userView.getUserName(),tbo);
		
		String rows=String.valueOf(tbo.getColInfoBGrid().size());
		String cols=String.valueOf(tbo.getRowInfoBGrid().size());
		this.getFormHM().put("rows",rows);
		this.getFormHM().put("cols",cols);
		this.getFormHM().put("reportTypes",rpda.getReportTypes());
		this.getFormHM().put("reportYearList" , reportYearList);
		this.getFormHM().put("reportCounitidList" ,reportCounitidList);
		this.getFormHM().put("reportWeekList",reportWeekList);
		this.getFormHM().put("reportCountInfo" , rpda.getReportCountInfo());
		this.getFormHM().put("reportHtml" ,rpda.getReportHtml());
		this.getFormHM().put("columnflag" ,columnflag);
		String reportState = rpda.getReportState();
		this.getFormHM().put("reportState",reportState);
		
		this.getFormHM().put("reportYearid", rpda.getYearid());
		this.getFormHM().put("reportCount", rpda.getCountid());
		//存在归档数据
		if("null".equals(reportState)){
		/*	ArrayList list = rpda.getChartDBList();
			this.getFormHM().put("list",list);
			this.getFormHM().put("chartTitle" ,rpda.getReportGridTitle());*/
			this.getFormHM().put("chartFlag" ,"yes");
		}else{
			this.getFormHM().put("chartFlag" ,"no");
		}

		//不重新设置默认值
		this.getFormHM().put("optionFlag","no");
		this.getFormHM().put("currentReport",tabid);
		this.getFormHM().put("reportTabid",reportTabid);
		((HashMap)(this.getFormHM().get("requestPamaHM"))).put("code","");
	}

}
