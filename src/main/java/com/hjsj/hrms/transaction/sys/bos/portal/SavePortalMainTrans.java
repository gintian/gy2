package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class SavePortalMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");      
		 String portal_id="";
		 String parentid="";
			String portal_name="";
			String portal_func_id="";
			String portal_icon="";
			String portal_url="";
			String portal_target="";
			String colnum="";
			String colwitdh="";
			String height="";
			String hide ="";
			String priv ="";
			String opt ="";
			try {
				portal_name = hm.get("portal_name")==null?"":new String(hm.get("portal_name").toString().getBytes("iso-8859-1"),"GB2312");
				portal_id = hm.get("portal_id")==null?"":new String(hm.get("portal_id").toString().getBytes("iso-8859-1"),"GB2312");
				parentid = hm.get("parentid")==null?"":new String(hm.get("parentid").toString().getBytes("iso-8859-1"),"GB2312");
				
				portal_func_id = hm.get("portal_func_id")==null?"":new String(hm.get("portal_func_id").toString().getBytes("iso-8859-1"),"GB2312");
				portal_icon = hm.get("portal_icon")==null?"":new String(hm.get("portal_icon").toString().getBytes("iso-8859-1"),"GB2312");
				portal_url = hm.get("portal_url")==null?"":new String(hm.get("portal_url").toString().getBytes("iso-8859-1"),"GB2312");
				portal_target = hm.get("portal_target")==null?"":new String(hm.get("portal_target").toString().getBytes("iso-8859-1"),"GB2312");
				colnum = hm.get("colnum")==null?"":(String)hm.get("colnum");
				colwitdh =  SafeCode.decode((String)hm.get("colwitdh"));
				height =  SafeCode.decode((String)hm.get("height"));
				hide =  SafeCode.decode((String)hm.get("hide"));
				priv = SafeCode.decode((String)hm.get("priv"));
				opt =  SafeCode.decode((String)hm.get("opt"));
			
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		PortalMainBo portalbo = new PortalMainBo(this.getFrameconn());
		  Document doc = (Document)this.getFormHM().get("portal_dom");
		portalbo.addPortalContent(portal_id,portal_name,parentid,portal_func_id,portal_icon,portal_url,portal_target,colnum,colwitdh,height,hide,priv,opt,doc);
		this.getFormHM().put("portal_dom", doc);

	}

}