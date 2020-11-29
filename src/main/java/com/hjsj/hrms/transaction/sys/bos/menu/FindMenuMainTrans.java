package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class FindMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String menu_id = "";
		String parentid = "";
		String menu_name = "";
		String menu_func_id = "";
		String menu_icon = "";
		String menu_url = "";
		String menu_target = "";
		String premenu_id = "";
		String menuhide = "";
		String validate="";
		
		menu_name = SafeCode.decode((String)hm.get("menu_name"));
		menu_id =  SafeCode.decode((String)hm.get("menu_id"));
		parentid =  SafeCode.decode((String)hm.get("parentid"));
		premenu_id =  SafeCode.decode((String)hm.get("premenu_id"));

		menu_func_id = SafeCode.decode((String)hm.get("menu_func_id"));
		menu_icon =  SafeCode.decode((String)hm.get("menu_icon"));
		menu_url =  SafeCode.decode((String)hm.get("menu_url"));
		menu_url = PubFunc.keyWord_reback(menu_url);
		menu_target =  SafeCode.decode((String)hm.get("menu_target"));
		menuhide =  SafeCode.decode((String)hm.get("menuhide"));
		
		validate=SafeCode.decode((String)hm.get("validate"));
		
		if (hm.get("b_findadd") != null && "add".equals(hm.get("b_findadd"))) {
			this.getFormHM().remove("addmenu_id");
			this.getFormHM().remove("addmenu_name");
			this.getFormHM().remove("addcodeitemurl");
			this.getFormHM().remove("addcodeitemicon");
			this.getFormHM().remove("addcodeitemfunc_id");
			this.getFormHM().remove("addcodeitemtarget");
			this.getFormHM().remove("addmenuhide");
			
			this.getFormHM().put("addmenu_id", menu_id);
			this.getFormHM().put("addmenu_name", menu_name);
			this.getFormHM().put("addcodeitemurl", menu_url);
			this.getFormHM().put("addcodeitemicon", menu_icon);
			this.getFormHM().put("addcodeitemfunc_id", menu_func_id);
			this.getFormHM().put("addcodeitemtarget", menu_target);
			this.getFormHM().put("addmenuhide", menuhide);
			
			
		}
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))) {
			this.getFormHM().remove("editmenu_id");
			this.getFormHM().remove("editmenu_name");
			this.getFormHM().remove("editcodeitemurl");
			this.getFormHM().remove("editcodeitemicon");
			this.getFormHM().remove("editcodeitemfunc_id");
			this.getFormHM().remove("editcodeitemtarget");
			this.getFormHM().remove("editmenuhide");
			
			this.getFormHM().put("editmenu_id", menu_id);
			this.getFormHM().put("editmenu_name", menu_name);
			this.getFormHM().put("editcodeitemurl", menu_url);
			this.getFormHM().put("editcodeitemicon", menu_icon);
			this.getFormHM().put("editcodeitemfunc_id", menu_func_id);
			this.getFormHM().put("editcodeitemtarget", menu_target);
			this.getFormHM().put("editmenuhide", menuhide);
		
			
		}

		Document doc = (Document) this.getFormHM().get("menu_dom");
		MenuMainBo menubo = new MenuMainBo(this.getFrameconn(),this.getUserView());
		boolean menuflag = menubo.isExist(menu_id, doc);
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))
				&& menuflag && premenu_id.equals(menu_id)) {
			menuflag = false;
		}
		if (hm.get("b_findadd") != null && "add".equals(hm.get("b_findadd"))
				&& !menuflag) {
			// 执行增加操作 ,//增加validate参数 20160621 changxy
			menubo.addMenuContent(menu_id, menu_name, parentid, menu_func_id,
					menu_icon, menu_url, menu_target,menuhide, doc,validate);
		}
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))
				&& !menuflag) {
			// 执行修改操作   //增加validate参数 20160621 changxy
			menubo.editMenuContent(menu_id, menu_name, premenu_id,
					menu_func_id, menu_icon, menu_url, menu_target,menuhide, doc,validate);
		}

		this.getFormHM().put("menuflag", String.valueOf(menuflag));
		hm.remove("b_findadd");
		hm.remove("b_findedit");
	}
	// public boolean executeSession(String menu_id,Document doc){
	// //
	// System.out.println("FindMenuMainTrans.getFormHM:"+this.getFormHM().get("menu_dom"));
	// MenuMainBo menubo = new MenuMainBo();
	// return menubo.isExist(menu_id,doc);
	// }

}