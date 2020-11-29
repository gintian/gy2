package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.ykcard.RecordConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EmpCardSalaryShowTrans  extends IBusiness{
	
	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		String pre = (String)hm.get("userbase");
		hm.remove("flag");
		if("infoself".equalsIgnoreCase(flag))
		{
			if(this.userView.getA0100()==null||this.userView.getA0100().length()<=0)
			{
				throw new GeneralException("","非自助平台用户!","","");
			}
			
			pre=this.userView.getDbname();
		}
		this.getFormHM().put("a0100",userView.getA0100());
		this.getFormHM().put("b0110",userView.getUserOrgId());
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("pre",pre);
		RecordConstant recordConstant=new RecordConstant(this.getFrameconn());
		String str_value=recordConstant.searchConstant();
		this.getFormHM().put("recardconstant",str_value);
	}

	
}
