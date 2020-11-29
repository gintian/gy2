package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;

import java.util.HashMap;

public class AddPortalMainTrans extends IBusiness {

	public void execute() throws GeneralException {
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String parentid=(String)hm.get("portalid");
		String opt=(String)hm.get("opt");
		if("root".equals(parentid)){
			parentid="-1";
		}
		Document doc = null;
		if (this.getFormHM().get("portal_dom") != null) {
			doc = (Document) this.getFormHM().get("portal_dom");
		} else {

		}
		PortalMainBo portalbo = new PortalMainBo(this.getFrameconn());
		LazyDynaBean a_bean = portalbo.getPortalName(parentid,opt, doc);
		String columns = (String) a_bean.get("columns");
		String colwidths=(String) a_bean.get("colwidths");
		String columnsuper =(String) a_bean.get("columnsuper");
			this.getFormHM().put("parentid", parentid);
			this.getFormHM().put("opt", opt);
			this.getFormHM().put("colnum", columns);
			this.getFormHM().put("colwidths", colwidths);
			this.getFormHM().put("columnsuper", columnsuper);
			this.getFormHM().put("portal_dom", doc);
			this.getFormHM().put("editcodeitemicon", "");
			
			
			
	}


}
