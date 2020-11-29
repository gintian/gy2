package com.hjsj.hrms.module.gz.salarytemplate.transaction;

import com.hjsj.hrms.module.gz.salarytemplate.businessobject.ProcessMonitorBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 薪资流程监控交易类
 * @createtime
 * @author
 *
 */
public class SendMonitorTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			String salaryid = this.getFormHM().get("salaryid")==null?"":PubFunc.decrypt((String) this.getFormHM().get("salaryid"));
			ArrayList sendlist = this.getFormHM().get("sendlist")==null?null:(ArrayList) this.getFormHM().get("sendlist");
			String username = this.getFormHM().get("username")==null?"":(String) this.getFormHM().get("username");
			String fullname = this.getFormHM().get("fullname")==null?"":(String) this.getFormHM().get("fullname");
			String sp_flag = this.getFormHM().get("sp_flag")==null?"":(String) this.getFormHM().get("sp_flag");//当前填报状态
			String imodule = this.getFormHM().get("imodule")==null?"":(String) this.getFormHM().get("imodule");
			String content = this.getFormHM().get("content")==null?"":(String) this.getFormHM().get("content");
			ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.getFrameconn(),this.userView,imodule,salaryid);
			String msg = processMonitorBo.sendMessage(sendlist, sp_flag, username, fullname, content);
			
			this.getFormHM().put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
