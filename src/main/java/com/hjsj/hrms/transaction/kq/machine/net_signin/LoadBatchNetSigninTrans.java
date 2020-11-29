package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * @author Owner
 *
 */
public class LoadBatchNetSigninTrans  extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList objlist=(ArrayList)this.getFormHM().get("objlist");
		String nbase=(String)this.getFormHM().get("nbase");
		String registerdate=(String)this.getFormHM().get("workdate"); //页面传来时间			
		String singin_flag=(String)this.getFormHM().get("singin_flag");		
//		if(nbase==null||nbase.length()<=0)
//			throw GeneralExceptionHandler.Handle(new GeneralException("人员库前缀为空，错误！"));
		if(objlist==null||objlist.size()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("人员库前缀为空，错误！"));
		if(registerdate==null||registerdate.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("工作日期为空，错误！"));
		if(registerdate!=null&&registerdate.length()>0)
			registerdate=registerdate.replaceAll("-", ".");
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field");
		if(sdao_count_field==null||sdao_count_field.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("没有指定上岛签到指标，错误！"));		
		EmpNetSignin empNetSignin = new EmpNetSignin(this.userView,this.getFrameconn());
		String workdate=empNetSignin.getWork_date(); //取得当前系统时间
		//上岛签到不受时间控制
//		if(!registerdate.equals(workdate))
//		{
//			throw GeneralExceptionHandler.Handle(new GeneralException("工作日期，必须选择当前日期！"));
//		}
//		boolean isCorrect=empNetSignin.batchLoadNetSignin(objlist, nbase, workdate, singin_flag, sdao_count_field);
		boolean isCorrect=empNetSignin.batchLoadNetSignin(objlist, nbase, registerdate, singin_flag, sdao_count_field);
		String mess= empNetSignin.getSignmess();
		if(isCorrect)
		{
			this.getFormHM().put("flag", "ok");
			if("0".equals(singin_flag))
			{
				if(mess==null||mess.length()<=0)
					this.getFormHM().put("mess", "上岛签到成功");
				else
					this.getFormHM().put("mess", mess+"上岛签到失败");
			}
			  
			else
			  this.getFormHM().put("mess", "成功取消上岛签到");
		}
		else
			this.getFormHM().put("flag", "no");
		this.getFormHM().put("signin_type", "batch");
		this.getFormHM().put("signin_flag", singin_flag);
	}

}
