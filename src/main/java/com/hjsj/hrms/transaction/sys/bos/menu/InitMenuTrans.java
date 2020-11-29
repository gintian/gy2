package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;

import java.util.ArrayList;
public class InitMenuTrans extends IBusiness {

	public void execute() throws GeneralException {
		Document doc=null;
		if(this.getFormHM().get("menu_dom")!=null){
			 doc = (Document)this.getFormHM().get("menu_dom");
		}else{
		MenuMainBo fbo = new MenuMainBo();
		 doc =fbo.getDocument();
		}
		this.getFormHM().put("menu_dom", doc);
		ArrayList menuhidelist = new ArrayList();	
		CommonData obj=new CommonData("true","是");
		menuhidelist.add(obj);
		 obj=new CommonData("false","否");
		 menuhidelist.add(obj);
	 this.getFormHM().put("menuhidelist", menuhidelist);	
	 //changxy 界面二级验证需要的参数
	 	ArrayList validateList=new ArrayList();
	 	CommonData objvi=new CommonData("false","否");
	 	validateList.add(objvi);
	 	objvi=new CommonData("true","是");
	 	validateList.add(objvi);
	 this.getFormHM().put("validateList", validateList);
	}
//	public Document getDocument(){
//		if(this.getFormHM().get("menu_dom")!=null){
//			System.out.println("menu_dom:"+this.getFormHM().get("menu_dom"));
//			Document doc = (Document)this.getFormHM().get("menu_dom");
//			return doc;
//		}else{
//		MenuMainBo fbo = new MenuMainBo();
//		Document doc =fbo.getDocument();
//		return doc;
//		}
//	}

}
