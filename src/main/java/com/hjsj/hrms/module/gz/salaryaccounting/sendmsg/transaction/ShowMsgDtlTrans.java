package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SendMsgTrans 
 * 类描述：发送通知
 * 创建人：sunming
 * 创建时间：2015-7-8
 * @version
 */
public class ShowMsgDtlTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String id=(String)this.getFormHM().get("id");
			String a0100=(String)this.getFormHM().get("a0100");
			a0100 = SafeCode.decode(a0100);
			String[] temp=a0100.split("~");
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			SendMsgBo bo = new SendMsgBo(this.getFrameconn(), userView);
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				 tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				 tableName=manager+"_salary_"+salaryid;
			if(temp.length!=3)
				throw GeneralExceptionHandler.Handle(new Exception("仅能查看新版本生成的邮件内容!"));
			HashMap hm=bo.getBrowseEmailContent(id,temp[0].substring(3),temp[0].substring(0,3),tableName,temp[1],salaryid,temp[2]);
			ArrayList attachlist = bo.getBrowseEmailAttach(id);
			this.getFormHM().put("subject",(String)hm.get("subject"));
			this.getFormHM().put("address",(String)hm.get("address"));
			this.getFormHM().put("content",(String)hm.get("content"));
			this.getFormHM().put("a0101",(String)hm.get("a0101"));
			this.getFormHM().put("attachlist",attachlist);
			this.getFormHM().put("attachSize",attachlist==null?"0":(attachlist.size()+""));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
