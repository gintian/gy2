/*
 * Created on 2006-3-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;


import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchHistoryOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		// 如果是新历史机构图，走新的action
		String isyfiles = (String)this.getFormHM().get("isyfiles");
		String action="searchhistoryorgmaps.do";
		  if(isyfiles != null && "1".equals(isyfiles)){
			  action = "showyFilesOrgMap.do";
		  }
		hm.remove("isyfiles");
		String target=(String)hm.get("target");
		target="mil_body";
		treeItem.setName("root");		
		treeItem.setIcon("/images/open.png");	
		treeItem.setTarget(target);
		String rootdesc=ResourceFactory.getProperty("general.inform.org.historyorg");
	    treeItem.setRootdesc(rootdesc);
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    treeItem.setAction("javascript:void(0)");
	    
	   if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/general/inform/org/loadhistroycatalogtree?issuperuser=1&action=" + action + "&target=" + target + "&manageprive="/* + userView.getManagePrivCode() + userView.getManagePrivCodeValue()*/);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/general/inform/org/loadhistroycatalogtree?issuperuser=0&action=" + action + "&target=" + target + "&manageprive="/*+ userView.getManagePrivCode() + userView.getManagePrivCodeValue()*/);
		    else
			    treeItem.setLoadChieldAction("/general/inform/org/loadhistroycatalogtree?issuperuser=0&action=" + action + "&target=" + target + "&manageprive="/*+ userView.getManagePrivCode() + "no"*/);
		}	   
	    cat.debug(treeItem.toJS());
	    this.getFormHM().put("treeCode",treeItem.toJS());   
	    this.getFormHM().put("link", "his");
	    this.getFormHM().put("ishistory", "true");
	}
}
