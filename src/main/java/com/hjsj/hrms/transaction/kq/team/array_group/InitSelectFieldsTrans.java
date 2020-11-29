package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitSelectFieldsTrans  extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap)this.getFormHM();	
		String group_id = (String)hm.get("group_id");
		this.getFormHM().put("group_id", group_id);
		
		// 通过考勤的权限方法获取
		ManagePrivCode managePrivCode = new ManagePrivCode(this.userView, this.frameconn);
		String temp = managePrivCode.getPrivOrgId();
		this.getFormHM().put("orgparentcode",temp.replaceAll(",", "`"));
	}
	
}
