/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.tree;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");
		String fieldname=(String)hm.get("fieldname");
		String rootdesc="";
		String type=(String)hm.get("type");
		treeItem.setAction("");
		if("UN".equals(type)|| "UM".equals(type) || "@K".equals(type))
		{
			if("UN".equals(type))
				rootdesc=ResourceFactory.getProperty("tree.unroot.undesc");
			if("UM".equals(type))
				rootdesc=ResourceFactory.getProperty("tree.umroot.umdesc");
			if("@K".equals(type))
				rootdesc=ResourceFactory.getProperty("tree.kkroot.kkdesc");
		}
		else
		{
			rootdesc=ResourceFactory.getProperty("tree.coderoot.coderootdesc");
		}
		 treeItem.setRootdesc(rootdesc);
		 treeItem.setText(rootdesc);
		 treeItem.setTitle(rootdesc);
		treeItem.setLoadChieldAction("/common/tree/loadtree.jsp?params=root&parentid=00&fieldname=" + fieldname + "&type=" + type);
		this.getFormHM().put("treeCode",treeItem.toJS());
	}

}
