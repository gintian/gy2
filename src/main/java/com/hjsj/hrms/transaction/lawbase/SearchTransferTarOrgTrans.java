/*
 * Created on 2006-1-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:SearchTransferTarOrgTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 14, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchTransferTarOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		// TODO Auto-generated method stub
       //String ishavedept=(String)this.getFormHM().get("ishavedept");
       String treetype="noum";
      /* if("UN".equalsIgnoreCase(ishavedept))
       	treetype="noum";
       else
       	treetype="org"; */   //org(不显示职位）,duty,employee,noum(表示不显示部门职位)
		TreeItemView treeItem=new TreeItemView();
	    String action="javascript:void(0)"; 
		String target="mil_body";
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		//String kind="2";
		treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no");
			/*if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";*/
		}
	    treeItem.setAction(action);
	    cat.debug("-----treeCode------>" + treeItem.toJS());
	    this.getFormHM().put("tarTreeCode",treeItem.toJS());	 	 
	   
	}
}
