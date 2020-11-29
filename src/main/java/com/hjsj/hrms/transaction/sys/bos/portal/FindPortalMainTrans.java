package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class FindPortalMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String portal_id = "";
		String parentid = "";
		String portal_name = "";
		String portal_func_id = "";
		String portal_icon = "";
		String portal_url = "";
		String portal_target = "";
		String preportal_id = "";
		String colnum = "";
		String colwidth="";
		String height="";
		String hide ="";
		String priv ="";
		String opt ="";
			portal_name = SafeCode.decode((String)hm.get("portal_name"));
			portal_id =  SafeCode.decode((String)hm.get("portal_id"));
			parentid =  SafeCode.decode((String)hm.get("parentid"));
			preportal_id =  SafeCode.decode((String)hm.get("preportal_id"));
			colnum = SafeCode.decode((String)hm.get("colnum"));
			portal_func_id = SafeCode.decode((String)hm.get("portal_func_id"));
			portal_icon =  SafeCode.decode((String)hm.get("portal_icon"));
			portal_url =  SafeCode.decode((String)hm.get("portal_url"));
			portal_url=PubFunc.keyWord_reback(portal_url);
			portal_target =  SafeCode.decode((String)hm.get("portal_target"));
			colnum =  SafeCode.decode((String)hm.get("colnum"));
			colwidth =  SafeCode.decode((String)hm.get("colwidth"));
			height =  SafeCode.decode((String)hm.get("height"));
			hide =  SafeCode.decode((String)hm.get("hide"));
			priv = SafeCode.decode((String)hm.get("priv"));
			opt =  SafeCode.decode((String)hm.get("opt"));

		if (hm.get("b_findadd") != null && "add".equals(hm.get("b_findadd"))) {
			this.getFormHM().remove("addportal_id");
			this.getFormHM().remove("addportal_name");
			this.getFormHM().remove("addcodeitemurl");
			this.getFormHM().remove("addcodeitemicon");
			this.getFormHM().remove("addcodeitemfunc_id");
			this.getFormHM().remove("addcodeitemtarget");
			this.getFormHM().put("addportal_id", portal_id);
			this.getFormHM().put("addportal_name", portal_name);
			this.getFormHM().put("addcodeitemurl", portal_url);
			this.getFormHM().put("addcodeitemicon", portal_icon);
			this.getFormHM().put("addcodeitemfunc_id", portal_func_id);
			this.getFormHM().put("addcodeitemtarget", portal_target);
			this.getFormHM().put("colnum", colnum);
			this.getFormHM().put("portalid", portal_id);
			this.getFormHM().put("parentid", parentid);

		}
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))) {
			this.getFormHM().remove("editportal_id");
			this.getFormHM().remove("editportal_name");
			this.getFormHM().remove("editcodeitemurl");
			this.getFormHM().remove("editcodeitemicon");
			this.getFormHM().remove("editcodeitemfunc_id");
			this.getFormHM().remove("editcodeitemtarget");
			this.getFormHM().put("editportal_id", portal_id);
			this.getFormHM().put("editportal_name", portal_name);
			this.getFormHM().put("editcodeitemurl", portal_url);
			this.getFormHM().put("editcodeitemicon", portal_icon);
			this.getFormHM().put("editcodeitemfunc_id", portal_func_id);
			this.getFormHM().put("editcodeitemtarget", portal_target);
			this.getFormHM().put("colnum", colnum);
			this.getFormHM().put("parentid", parentid);

		}
		
		Document doc = (Document) this.getFormHM().get("portal_dom");
		PortalMainBo portalbo = new PortalMainBo(this.userView);
		//boolean portalflag = portalbo.isExist(portal_id,opt, doc);
		String addoredit="";
		if(hm.get("b_findadd") != null && "add".equals(hm.get("b_findadd"))){
			addoredit = "1";
		}else if(hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))){
			addoredit = "2";
		}
		boolean portalflag = portalbo.isExist(portal_id,opt, doc,addoredit);
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))
				&& portalflag && preportal_id.equals(portal_id)) {
			portalflag = false;
		}
		if (hm.get("b_findadd") != null && "add".equals(hm.get("b_findadd"))
				&& !portalflag) {
			// 执行增加操作
			portalbo.addPortalContent(portal_id, portal_name, parentid, portal_func_id,
					portal_icon, portal_url, portal_target,colnum,colwidth,height,hide,priv,opt, doc);
		}
		if (hm.get("b_findedit") != null && "edit".equals(hm.get("b_findedit"))
				&& !portalflag) {
			// 执行修改操作
			portalbo.editPortalContent(portal_id, portal_name, preportal_id,
					portal_func_id, portal_icon, portal_url, portal_target,colnum,colwidth,height,hide,priv,opt, doc);
		}

		this.getFormHM().put("portalflag", String.valueOf(portalflag));
		hm.remove("b_findadd");
		hm.remove("b_findedit");
		this.getFormHM().put("portal_dom", doc);
	}

}