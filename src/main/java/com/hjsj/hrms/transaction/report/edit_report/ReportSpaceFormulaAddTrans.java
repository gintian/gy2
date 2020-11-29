/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 15, 2006:5:16:08 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportSpaceFormulaAddTrans  extends IBusiness{


	public void execute() throws GeneralException {
		try {
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String tabid = (String)hm.get("tabid");
		 String username = SafeCode.decode((String) hm.get("username"));
			if(username==null|| "".equals(username)){
				username = this.userView.getUserName();
			}
			userView=new UserView(username, this.frameconn); 
			userView.canLogin();

		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		String rt = "";
		String temp = (String)hm.get("flag");
		if("1".equals(temp)){
			rt = "tb";
		}else{
			rt = "tt_";
		}
		
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("expid", "");		
		this.getFormHM().put("cname" ,"");
		this.getFormHM().put("lexpr","");
		this.getFormHM().put("rexpr","");
		this.getFormHM().put("colrow","");
		//this.getFormHM().put("tabid","");
		this.getFormHM().put("rt",rt);
		
		this.getFormHM().put("returnflag",(String)hm.get("returnflag"));
		this.getFormHM().put("status",(String)hm.get("status"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
