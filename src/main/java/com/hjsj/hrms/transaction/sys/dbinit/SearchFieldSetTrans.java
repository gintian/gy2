/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:查询信息集交易</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:1:27:27 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchFieldSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("mil_body");
		String rootdesc=ResourceFactory.getProperty("system.infor.set");
		
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc); 
	    treeItem.setLoadChieldAction("/servlet/fieldsettree?flag=1");
	    treeItem.setAction("/system/dbinit/inforlist.do?b_query=link");	   
	    try
	    {
		    this.getFormHM().put("bs_tree",treeItem.toJS());	    	
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }	

	}

}
