package com.hjsj.hrms.transaction.kq.kqself.card;

import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectAppPeoTrans extends IBusiness {


	public void execute() throws GeneralException {
		//显示补刷卡人员报审数据的审批人的列表
		String str_app = (String) this.getFormHM().get("str_app");
		String str1 [] = str_app.split("~");
		ArrayList app_e0122 = new ArrayList();
		ArrayList app_a0101 = new ArrayList();
		ArrayList app_account = new ArrayList();
		for(int i=0;i<str1.length;i++){
			String str2 [] = str1[i].split("`");
			app_e0122.add(str2[0] != null ? AdminCode.getCodeName("UM", str2[0]) : "");
			app_a0101.add(str2[1]);
			app_account.add(str2[2]);
		}
		this.getFormHM().put("app_e0122", app_e0122);
		this.getFormHM().put("app_a0101", app_a0101);
		this.getFormHM().put("app_account", app_account);
	}	

}
