package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetPrivTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String order_by=(String)hm.get("order_by");
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String priv=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			if(priv.length()==0)
				priv="0";
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String flag="0";
			String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
			if(manager!=null&&manager.trim().length()>0)
			{
				if(!this.userView.getUserName().equalsIgnoreCase(manager))
				{
					if(orgid.length()>0||deptid.length()>0)
						flag="1";
				}
			}
			// 判断短信配置参数设置是否正确，不正确则不显示【发送短信】按钮。默认为显示。
			String dxFlag = "1";
			AutoSendEMailBo ase=new AutoSendEMailBo(this.getFrameconn());
			String mobile_field=ase.getMobileField();
			if(mobile_field == null || "".equals(mobile_field))
				dxFlag = "0";
			// 判断微信配置参数设置是否正确，不正确则不显示【发送微信】按钮。默认为显示。
			String wxFlag = "1";
			String corpid = (String) ConstantParamter.getAttribute("wx","corpid");
			if(corpid == null || "".equals(corpid))
				wxFlag = "0";
			
			this.getFormHM().put("showUnitCodeTree",flag); 
			this.getFormHM().put("priv",priv);
			this.getFormHM().put("itemid", "all");
			this.getFormHM().put("optValue","0");
			this.getFormHM().put("order_by",order_by);
			this.getFormHM().put("dxFlag",dxFlag);// 短信参数配置
			this.getFormHM().put("wxFlag",wxFlag);// 微信参数配置
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
