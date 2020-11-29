/*
 * Created on 2005-7-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadStatTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		TreeItemView treeItem=new TreeItemView();
	
		String querycond=(String)this.getFormHM().get("querycond");
		//System.out.println("wlh" + querycond);
		String infokind=(String)this.getFormHM().get("infokind");
		if(infokind==null || infokind!=null && infokind.trim().length()==0)
			infokind="1";
		treeItem.setName("root");		
		treeItem.setIcon("/images/open.png");	
		treeItem.setTarget("mmil_body");
		String rootdesc=ResourceFactory.getProperty("tree.statroot.desc");
	    treeItem.setRootdesc(rootdesc);
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
        {
	    	treeItem.setLoadChieldAction("/com/workbench/stat/history/statitemtree?tablename=sname&parentid=1&target=mmil_body&infokind=" + infokind );
        }else
	        treeItem.setLoadChieldAction("/com/workbench/stat/history/statitemtree?tablename=sname&parentid=1&target=mmil_body&infokind=" + infokind );
		treeItem.setAction("javascript:void(0)");
	  	this.getFormHM().put("stattreeCode",treeItem.toJS());
	  	this.getFormHM().put("infokind",infokind);
	}

}
