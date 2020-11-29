/**
 * 
 */
package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Owner
 *
 */
public class BrowseReturnLoadTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		TreeItemView treeItem=new TreeItemView();
		//System.out.println("wlh" + querycond);
		/*String infokind=(String)this.getFormHM().get("infokind");
		if(infokind==null || infokind!=null && infokind.trim().length()==0)
			infokind="1";
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		treeItem.setTarget("mil_body");
		String rootdesc=ResourceFactory.getProperty("tree.statroot.desc");
	    treeItem.setRootdesc(rootdesc);
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    treeItem.setLoadChieldAction("/com/workbench/stat/statitemtree?tablename=sname&parentid=1&target=mil_body&infokind=" + infokind + "&querycond=" + querycond);
		treeItem.setAction("javascript:void(0)");
	  	this.getFormHM().put("treeCode",treeItem.toJS());
	  	this.getFormHM().put("infokind",infokind);*/
	}

}
