package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title: SearchChannelTreeTrans</p>
 * <p>Description:频道树</p>
 * <p>Company:hjsj</p>
 * <p>Create time:2007-04-07 8:24:45 am</p>
 * @author lizhenwei
 * @version 1.0
 */

public class ChannelTreeTrans extends IBusiness{
	public void execute() throws GeneralException{
		String groupid=this.userView.getGroupId();
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("mil_body");
		String rootdesc=ResourceFactory.getProperty("lable.content.channel");
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc); 
	    treeItem.setLoadChieldAction("/system/channel/search_channel_servlet?parent_id=-1");
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
		    this.getFormHM().put("contentChannelTree",treeItem.toJS());	    	
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }
		
	}

}
