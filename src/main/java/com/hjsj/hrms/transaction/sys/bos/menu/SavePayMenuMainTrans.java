package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class SavePayMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		Document	doc = (Document) this.getFormHM().get("menu_dom");
		String menuid = (String) this.getFormHM().get("menuid");
		HashMap hm  = (HashMap) this.getFormHM().get("requestPamaHM");
		String sorting = (String)hm.get("sorting");
		MenuMainBo menubo = new MenuMainBo(this.getFrameconn());
		menubo.saveSortMenuList(menuid,sorting, doc);
		
		
			this.getFormHM().put("sorting",sorting);
	}

}