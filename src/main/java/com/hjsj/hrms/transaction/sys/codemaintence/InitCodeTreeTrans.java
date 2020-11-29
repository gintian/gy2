/**
 * 
 */
package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author yuxiaochun
 * 
 */
public class InitCodeTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
		if(map.get("codesetid")!=null&&!"".equals((String)map.get("codesetid")))
		{
			
			String codesetid=(String)map.get("codesetid");
			map.remove("codesetid");
			String object_type=(String)map.get("object_type");
			String historyDate=(String)map.get("historyDate");
			TreeItemView treeItem = new TreeItemView();
	    	treeItem.setName("root");
	    	treeItem.setIcon("/images/add_all.gif");
	    	treeItem.setTarget("mil_body");
	    	String rootdesc ="能力素质分类";
	    	treeItem.setRootdesc(rootdesc);
	    	treeItem.setLoadChieldAction("/servlet/codesettree?flag=3&parentid=-1&codesetid="+codesetid+"&fromflag=2");
	    	treeItem.setAction("/pos/posbusiness/searchposbusinesslist.do?b_query=link&full=1&a_code=70&fromflag=2&object_type="+object_type+"&historyDate="+historyDate);
    		this.getFormHM().put("treecode", treeItem.toJS());
		}else{
	    	TreeItemView treeItem = new TreeItemView();
	    	treeItem.setName("root");
	    	treeItem.setIcon("/images/add_all.gif");
	    	treeItem.setTarget("mil_body");
	    	String rootdesc = ResourceFactory.getProperty("codemaintence.root");
	    	treeItem.setRootdesc(rootdesc);
	    	treeItem.setLoadChieldAction("/servlet/codesettree?flag=2&fromflag=1");
	    	treeItem.setAction("/system/codemaintence/codetree.do?b_search=link&status=0&categories=");
		
    		this.getFormHM().put("treecode", treeItem.toJS());
		}
	}
}
