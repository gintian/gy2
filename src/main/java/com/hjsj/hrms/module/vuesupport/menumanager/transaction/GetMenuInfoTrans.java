package com.hjsj.hrms.module.vuesupport.menumanager.transaction;

import com.hjsj.hrms.module.vuesupport.menumanager.businessobject.MenuManagerService;
import com.hjsj.hrms.module.vuesupport.menumanager.businessobject.impl.MenuManagerServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

public class GetMenuInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String menu_id = (String) this.getFormHM().get("menu_id");
		MenuManagerService menuManagerService = new MenuManagerServiceImpl(this.frameconn,userView);
		try {
			Map menuMap = menuManagerService.getMenuAllData(menu_id);
			this.formHM.put("menu_data", menuMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
