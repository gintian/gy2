/*
 * Created on 2005-7-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.tree;

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
		//liuy 2014-12-6 5443： bi领导桌面：人力资源情况/人员结构和占比分析,把所有的常用统计都显示出来了，不对  start
		String crossshow = (String)this.getFormHM().get("crossshow");
		crossshow=crossshow==null?"":crossshow;
		String categories = (String)this.getFormHM().get("categories");
		categories=categories==null?"":categories;
		//liuy 2014-12-6 end
		TreeItemView treeItem=new TreeItemView();
	
		String querycond=(String)this.getFormHM().get("querycond");
		//System.out.println("wlh" + querycond);
		String infokind=(String)this.getFormHM().get("infokind");
		if(infokind==null || infokind!=null && infokind.trim().length()==0)
			infokind="1";
		treeItem.setName("root");		
		treeItem.setIcon("/images/open.png");	
		treeItem.setTarget("mil_body");
		String rootdesc="";
		/*Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{*/
			rootdesc=ResourceFactory.getProperty("tree.statroot.desc");
		//}
		treeItem.setTitle(rootdesc);
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    if(querycond==null||querycond.length()<=0|| "null".equals(querycond))
        {
	    	treeItem.setLoadChieldAction("/com/workbench/stat/statitemtree?tablename=sname&parentid=1&target=mil_body&infokind=" + infokind +"&crossshow=" + crossshow +"&categories=" + categories);
        }else
	        treeItem.setLoadChieldAction("/com/workbench/stat/statitemtree?tablename=sname&parentid=1&target=mil_body&infokind=" + infokind +"&crossshow=" + crossshow +"&categories=" + categories);
		treeItem.setAction("javascript:void(0)");
	  	this.getFormHM().put("stattreeCode",treeItem.toJS());
	  	this.getFormHM().put("infokind",infokind);
	}

}
