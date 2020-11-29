package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class EditPortalSaveMainTrans extends IBusiness {

	public void execute() throws GeneralException {
	
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");      
		 String portal_id="";
		 String parentid="";
			String portal_name="";
			String portal_func_id="";
			String portal_icon="";
			String portal_url="";
			String portal_target="";
			
			try {
				portal_name = hm.get("portal_name")==null?"":new String(hm.get("portal_name").toString().getBytes("iso-8859-1"),"GB2312");
				portal_id = hm.get("portal_id")==null?"":new String(hm.get("portal_id").toString().getBytes("iso-8859-1"),"GB2312");
				parentid = hm.get("parentid")==null?"":new String(hm.get("parentid").toString().getBytes("iso-8859-1"),"GB2312");
				portal_func_id = hm.get("portal_func_id")==null?"":new String(hm.get("portal_func_id").toString().getBytes("iso-8859-1"),"GB2312");
				portal_icon = hm.get("portal_icon")==null?"":new String(hm.get("portal_icon").toString().getBytes("iso-8859-1"),"GB2312");
				portal_url = hm.get("portal_url")==null?"":new String(hm.get("portal_url").toString().getBytes("iso-8859-1"),"GB2312");
				portal_target = hm.get("portal_target")==null?"":new String(hm.get("portal_target").toString().getBytes("iso-8859-1"),"GB2312");
		} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		  Document doc = (Document)this.getFormHM().get("portal_dom");
		PortalMainBo portalbo = new PortalMainBo(this.getFrameconn());
	//	portalbo.editPortalContent(portal_id,portal_name,parentid,portal_func_id,portal_icon,portal_url,portal_target,colnum,colwidth,height,hide,priv,opt,doc);
		this.getFormHM().put("portal_dom", doc);
		//this.getFormHM().put("portal_id",portal_id);
		//this.getFormHM().put("new_portal_name",name);
	}
//	public void executeSession(String portal_id,String name, String parentid,Document doc){
//		PortalMainBo portalbo = new PortalMainBo();
//		portalbo.editPortalContent(portal_id,name,parentid,doc);
//	}


}