/**
 * 
 */
package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class DelMenuMainAllTrans extends IBusiness {
    /**
	 */


	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	ArrayList lawidlst=new ArrayList();
	ArrayList lawfathidlst=new ArrayList();
	
	
	public void execute() throws GeneralException {
		String a_base_id = (String)this.getFormHM().get("a_base_id");
        if(a_base_id==null|| "".equals(a_base_id)){
        	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
            a_base_id=(String)hm.get("a_base_id");
        }
      //  MenuMainBo menubo = new MenuMainBo(this.getFrameconn());
      //  menubo.delMenuAllById(a_base_id);
	} 
	public void executeSession(String menu_id,Document doc){
		MenuMainBo menubo = new MenuMainBo();
		menubo.delMenuAllById(menu_id,doc);
	}
	public void dragNode(String frommenu_id,String tomenu_id,Document doc){
		MenuMainBo menubo = new MenuMainBo();
		menubo.dragNode(frommenu_id,tomenu_id,doc);
	}

}
