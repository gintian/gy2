/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchOrgEmployTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 200612:02:06 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchOrgEmployTrans extends IBusiness {

	public void execute() throws GeneralException {
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/group.gif");	
		treeItem.setTarget("il_body");
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
		treeItem.setTitle(rootdesc);
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setText(rootdesc); 
	    //treeItem.setLoadChieldAction("/system/logonuser/search_user_servlet?level0=0&groupid="+groupid);
	    treeItem.setAction("javascript:void(0)");	   
	    try
	    {
		    this.getFormHM().put("usertree",treeItem.toJS());
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }
	}

}