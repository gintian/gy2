package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitMsgDtlTrans 
 * 类描述：薪资发放-发放通知生成通知按钮
 * 创建人：sunming
 * 创建时间：2015-7-7
 * @version
 */
public class DeleteMsgTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException
	{try
	{
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String templateId=(String)this.getFormHM().get("templateId");
		String selectid=(String)this.getFormHM().get("selectid");
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		SendMsgBo bo = new SendMsgBo(this.getFrameconn(),  userView);
		if(StringUtils.isBlank(manager))
			bo.deletePersonFromEmail_content(selectid,templateId,this.userView.getUserName(),salaryid);
		else
			bo.deletePersonFromEmail_content(selectid,templateId,manager,salaryid);
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	}
		
		
	}

}
