package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class EditMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		Document doc = null;
		if (this.getFormHM().get("menu_dom") != null) {
			doc = (Document) this.getFormHM().get("menu_dom");
		} else {

		}
		String menuid = (String) hm.get("menuid");

		MenuMainBo menubo = new MenuMainBo(this.getFrameconn(),this.getUserView());
		LazyDynaBean a_bean = menubo.getMenuName(menuid, doc);
		String menu_name = (String) a_bean.get("codeitemdesc");
		String menu_url = (String) a_bean.get("codeitemurl");
		String menu_icon = (String) a_bean.get("codeitemicon");
		String menu_func_id = (String) a_bean.get("codeitemfunc_id");
		String menu_target = (String) a_bean.get("codeitemtarget");
		String menuhide = (String) a_bean.get("menuhide");
		
		String validate=(String)a_bean.get("validate");   
		
		this.getFormHM().put("codeitemid", menuid);
		this.getFormHM().put("precodeitemid", menuid);
		this.getFormHM().put("codeitemdesc", menu_name);
		this.getFormHM().put("codeitemurl", menu_url);
		this.getFormHM().put("codeitemicon", menu_icon);
		this.getFormHM().put("codeitemfunc_id", menu_func_id);
		this.getFormHM().put("codeitemtarget", menu_target);
		this.getFormHM().put("editmenuhide", menuhide);
		
		ArrayList menuhidelist = new ArrayList();	
		CommonData obj=new CommonData("true","是");
		menuhidelist.add(obj);
		 obj=new CommonData("false","否");
		 menuhidelist.add(obj);
	 this.getFormHM().put("menuhidelist", menuhidelist);	
	//changxy 20160621 界面需要的下拉参数与显示值
		this.getFormHM().put("validate", validate);
	 	ArrayList validateList=new ArrayList();
	 	CommonData objvi=new CommonData("false","否");
	 	validateList.add(objvi);
	 	objvi=new CommonData("true","是");
	 	validateList.add(objvi);
	 this.getFormHM().put("validateList", validateList);
	}

}