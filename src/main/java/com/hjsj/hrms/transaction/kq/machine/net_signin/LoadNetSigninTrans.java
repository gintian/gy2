package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class LoadNetSigninTrans  extends IBusiness {

	public void execute() throws GeneralException {
		String a0100=(String)this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(a0100);
		String nbase=(String)this.getFormHM().get("nbase");
		nbase = PubFunc.decrypt(nbase);
		String registerdate=(String)this.getFormHM().get("workdate");//前台传来的时间
		String singin_flag=(String)this.getFormHM().get("singin_flag");
		if(a0100==null||a0100.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("人员编号为空，错误！"));
		if(nbase==null||nbase.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("人员库前缀为空，错误！"));
		if(registerdate==null||registerdate.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("工作日期为空，错误！"));
		if(registerdate!=null&&registerdate.length()>0)
			registerdate=registerdate.replaceAll("-", ".");
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field");
		if(sdao_count_field==null||sdao_count_field.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("没有指定上岛签到指标，错误！"));
		EmpNetSignin empNetSignin = new EmpNetSignin(this.userView,this.getFrameconn());
		String workdate=empNetSignin.getWork_date();//取得系统当前时间
		//上岛签到不受时间控制
//		if(!registerdate.equals(workdate))
//		{
//			throw GeneralExceptionHandler.Handle(new GeneralException("工作日期，必须选择当前日期！"));
//		}
//		boolean isCorrect=empNetSignin.oneLoadNetSigninTrans(a0100, nbase, workdate, singin_flag, sdao_count_field);
		boolean isCorrect=empNetSignin.oneLoadNetSigninTrans(a0100, nbase, registerdate, singin_flag, sdao_count_field);
		if(isCorrect)
		{
			this.getFormHM().put("flag", "ok");
			if("0".equals(singin_flag))
			  this.getFormHM().put("mess", "上岛签到成功");
			else
				this.getFormHM().put("mess", "成功取消上岛签到");
		}
			
		else
			this.getFormHM().put("flag", "no");
		this.getFormHM().put("signin_type", "one");
		this.getFormHM().put("signin_flag", singin_flag);
		nbase = PubFunc.encrypt(nbase);
		this.getFormHM().put("nbase", nbase);
	}


}