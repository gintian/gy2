/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 2, 2006:9:20:39 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportAnalyseTrans extends IBusiness {

	/**
	 * 构造器
	 */
	public ReportAnalyseTrans() {
		super();
	}

	public void execute() throws GeneralException {
		try{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String tabid = (String)hm.get("tabid");
		this.getFormHM().put("reportTabid", tabid);
		String codeid  = (String)hm.get("code");
		String scopeid = (String)hm.get("scopeid");
		String username = (String)hm.get("username");
		String obj1 = (String)hm.get("obj1");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		this.frowset=dao.search("select tsortid,xmlstyle from tname where tabid="+tabid);
		String use_scope_cond="0";
		if(this.frowset.next())
		{
			//<use_scope_cond>0</use_scope_cond>
			String scope_cond = Sql_switcher.readMemo(this.frowset, "xmlstyle");
			if(scope_cond!=null&&scope_cond.indexOf("<use_scope_cond>")!=-1&&scope_cond.indexOf("</use_scope_cond>")!=-1){
				use_scope_cond= scope_cond.substring(scope_cond.indexOf("<use_scope_cond>")+16,scope_cond.indexOf("</use_scope_cond>"));
				
			}
			
		}
		this.getFormHM().put("use_scope_cond", use_scope_cond);
		this.getFormHM().put("showFlag", "1");
		this.getFormHM().put("username", username);
		this.getFormHM().put("obj1", obj1);
		this.getFormHM().put("tabid", tabid);
//		//存在归档数据
//		ReportPDBAnalyse rpda = new ReportPDBAnalyse(this.getFrameconn());
//		rpda.setScopeid(scopeid);
//		UserView _userview=null;
//		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
//		{
//			_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
//			try
//			{
//				_userview.canLogin();
//				rpda.setUserView(_userview);
//			}
//			catch(Exception e)
//			{
//				
//			}
//		}
//		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
//			rpda.changeReportTabid(tabid,codeid,_userview.getUserId(),_userview.getUserName());
//		else
//			rpda.changeReportTabid(tabid,codeid,userView.getUserId(),userView.getUserName());
//		String reportState = rpda.getReportState();
//		if(reportState.equals("null")){
//		/*	ArrayList list = rpda.getChartDBList();
//			this.getFormHM().put("list",list);
//			this.getFormHM().put("chartTitle" ,rpda.getReportGridTitle());*/
//			this.getFormHM().put("chartFlag" ,"yes");
//		}else{
//			this.getFormHM().put("chartFlag" ,"no");
//		}
//		this.getFormHM().put("scopeid", scopeid);
//		this.getFormHM().put("unitCode", codeid);
		}catch(Exception e){
			
		}
		
	}

}
