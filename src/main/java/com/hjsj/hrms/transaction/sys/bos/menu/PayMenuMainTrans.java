package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class PayMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		Document doc = null;
		if (this.getFormHM().get("menu_dom") != null) {
			doc = (Document) this.getFormHM().get("menu_dom");
		} else {

		}
		String menuid = (String) hm.get("menuid");

		MenuMainBo menubo = new MenuMainBo(this.getFrameconn());
		ArrayList sortList = menubo.getMenuList(menuid, doc);
		
		
			this.getFormHM().put("sortlist",sortList);
		///hm.put("sortlist",sortList(tabid));
			this.getFormHM().put("menuid",menuid);
	}

}