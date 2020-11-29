package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;

import java.util.HashMap;

public class EditPortalMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		Document doc = null;
		if (this.getFormHM().get("portal_dom") != null) {
			doc = (Document) this.getFormHM().get("portal_dom");
		} else {

		}
		String portalid = (String) hm.get("portalid");
		String opt =  (String) hm.get("opt");
		PortalMainBo portalbo = new PortalMainBo(this.getFrameconn(),this.userView);
		LazyDynaBean a_bean = portalbo.getPortalName(portalid,opt, doc);
		String portal_name = (String) a_bean.get("codeitemdesc");
		String portal_url = (String) a_bean.get("codeitemurl");
		String portal_icon = (String) a_bean.get("codeitemicon");
		String portal_func_id = (String) a_bean.get("codeitemfunc_id");
		String portal_target = (String) a_bean.get("codeitemtarget");
		String columns = (String) a_bean.get("columns");
		String colwidth = (String) a_bean.get("colwidth");
		String height = (String) a_bean.get("codeitemheight");
		String hide = (String) a_bean.get("codeitemhide");
		String priv = (String) a_bean.get("codeitempriv");
		String colwidths = (String) a_bean.get("colwidths");
		this.getFormHM().put("codeitemid", portalid);
		this.getFormHM().put("precodeitemid", portalid);
		this.getFormHM().put("codeitemdesc", portal_name);
		this.getFormHM().put("codeitemurl", portal_url);
		this.getFormHM().put("codeitemicon", portal_icon);
		this.getFormHM().put("codeitemfunc_id", portal_func_id);
		this.getFormHM().put("codeitemtarget", portal_target);
		this.getFormHM().put("colnum", columns);
		this.getFormHM().put("colwidth", colwidth);
		this.getFormHM().put("opt", opt);
		this.getFormHM().put("height", height);
		this.getFormHM().put("hide", hide);
		this.getFormHM().put("priv", priv);
		this.getFormHM().put("colwidths", colwidths);
		this.getFormHM().put("portal_dom", doc);

	}

}