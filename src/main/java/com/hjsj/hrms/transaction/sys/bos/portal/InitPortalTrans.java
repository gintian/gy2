package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;

import java.util.ArrayList;
public class InitPortalTrans extends IBusiness {

	public void execute() throws GeneralException {
		Document doc=null;
		if(this.getFormHM().get("portal_dom")!=null){
			 doc = (Document)this.getFormHM().get("portal_dom");
		}else{
		PortalMainBo fbo = new PortalMainBo();
		 doc =fbo.getDocument();
		 //System.out.println("获得初始化doc"+doc);
		}
		this.getFormHM().put("portal_dom", doc);
		ArrayList numlist = new ArrayList();	
		CommonData obj=new CommonData("1","1");
		numlist.add(obj);
		 obj=new CommonData("2","2");
		 numlist.add(obj);
		 obj=new CommonData("3","3");
		 numlist.add(obj);
			ArrayList hidelist = new ArrayList();	
			 obj=new CommonData("false","是");
			 hidelist.add(obj);
			 obj=new CommonData("true","否");
			 hidelist.add(obj);
			 ArrayList privlist = new ArrayList();	
			 obj=new CommonData("true","是");
			 privlist.add(obj);
			 obj=new CommonData("false","否");
			 privlist.add(obj);
		 this.getFormHM().put("numlist", numlist);
		 this.getFormHM().put("hidelist", hidelist);
		 this.getFormHM().put("privlist", privlist);
	}
//	public Document getDocument(){
//		if(this.getFormHM().get("portal_dom")!=null){
//			System.out.println("portal_dom:"+this.getFormHM().get("portal_dom"));
//			Document doc = (Document)this.getFormHM().get("portal_dom");
//			return doc;
//		}else{
//		PortalMainBo fbo = new PortalMainBo();
//		Document doc =fbo.getDocument();
//		return doc;
//		}
//	}

}
