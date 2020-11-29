package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class SaveMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");      
		 String menu_id="";
		 String parentid="";
			String menu_name="";
			String menu_func_id="";
			String menu_icon="";
			String menu_url="";
			String menu_target="";
			String menuhide = "";

			try {
				menu_name = hm.get("menu_name")==null?"":new String(hm.get("menu_name").toString().getBytes("iso-8859-1"),"GB2312");
				menu_id = hm.get("menu_id")==null?"":new String(hm.get("menu_id").toString().getBytes("iso-8859-1"),"GB2312");
				parentid = hm.get("parentid")==null?"":new String(hm.get("parentid").toString().getBytes("iso-8859-1"),"GB2312");
				
				menu_func_id = hm.get("menu_func_id")==null?"":new String(hm.get("menu_func_id").toString().getBytes("iso-8859-1"),"GB2312");
				menu_icon = hm.get("menu_icon")==null?"":new String(hm.get("menu_icon").toString().getBytes("iso-8859-1"),"GB2312");
				menu_url = hm.get("menu_url")==null?"":new String(hm.get("menu_url").toString().getBytes("iso-8859-1"),"GB2312");
				menu_target = hm.get("menu_target")==null?"":new String(hm.get("menu_target").toString().getBytes("iso-8859-1"),"GB2312");
				menuhide = hm.get("menuhide")==null?"":new String(hm.get("menuhide").toString().getBytes("iso-8859-1"),"GB2312");

			
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		MenuMainBo menubo = new MenuMainBo(this.getFrameconn());
		  Document doc = (Document)this.getFormHM().get("menu_dom");
		menubo.addMenuContent(menu_id,menu_name,parentid,menu_func_id,menu_icon,menu_url,menu_target,menuhide,doc);
	//	menubo.addMenuContent(menu_id,name,parentid);
	//	this.getFormHM().put("new_menu_id",menu_id);
	//	this.getFormHM().put("new_menu_name",name);
		

	}
//	public void executeSession(String menu_id,String name, String parentid,Document doc){
//		MenuMainBo menubo = new MenuMainBo();
//		menubo.addMenuContent(menu_id,name,parentid,doc);
//	}

}