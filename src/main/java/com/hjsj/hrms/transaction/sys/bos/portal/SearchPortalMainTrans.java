/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class SearchPortalMainTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String portal_id = (String)hm.get("parentid");
        String _opt = (String)hm.get("opt");
        Document doc =null;
        if(this.getFormHM().get("portal_dom")!=null){
			 doc = (Document)this.getFormHM().get("portal_dom");
		}else{
		PortalMainBo fbo = new PortalMainBo();
		 doc =fbo.getDocument();
		}
        PortalMainBo portalbo = new PortalMainBo(this.getFrameconn());
        ArrayList list =portalbo.getPortalContent(portal_id,doc,_opt);
        this.getFormHM().put("portalMainlist", list);
        this.getFormHM().remove("parentid");
//        this.getFormHM().remove("_opt");
        this.getFormHM().put("opt", _opt);
        this.getFormHM().put("parentid", portal_id);
        this.getFormHM().put("portal_dom", doc);
//        this.getFormHM().put("precodeitemid", portal_id);
//        this.getFormHM().put("codeitemid", portal_id);
//        this.getFormHM().put("codeitemdesc", portal_id);

	}
	
}
