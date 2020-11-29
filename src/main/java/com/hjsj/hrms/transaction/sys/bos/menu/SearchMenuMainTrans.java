/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import javax.servlet.ServletContext;
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
public class SearchMenuMainTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String menu_id = (String)hm.get("parentid");
        Document doc =null;
        if(this.getFormHM().get("menu_dom")!=null){
			 doc = (Document)this.getFormHM().get("menu_dom");
		}else{
		MenuMainBo fbo = new MenuMainBo();
		 doc =fbo.getDocument();
		}
        MenuMainBo menubo = new MenuMainBo(this.getFrameconn(), this.userView);
	    ServletContext context = SystemConfig.getServletContext();
		EncryptLockClient lock = (EncryptLockClient)context.getAttribute("lock");
//        ArrayList list =menubo.getMenuContent(menu_id,doc);
		ArrayList list =menubo.getMenuContent(menu_id,doc,lock);//添加EncryptLockClient 参数 wangb 20170727 29856
        this.getFormHM().put("menuMainlist", list);
        this.getFormHM().remove("parentid");
        this.getFormHM().put("parentid", menu_id);
//        this.getFormHM().put("precodeitemid", menu_id);
//        this.getFormHM().put("codeitemid", menu_id);
//        this.getFormHM().put("codeitemdesc", menu_id);

	}
	
}
