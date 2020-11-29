/*
 * Created on 2006-1-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
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
public class LoadSelectOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		String kind="2";
		//treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setTitle(rootdesc);
	    treeItem.setText(rootdesc);
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&treetype=employee&action=javascript:void(0)&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=employee&action=javascript:void(0)&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&treetype=employee&action=javascript:void(0)&manageprive=" + userView.getManagePrivCode() + "no");
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	  	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("selectTreeCode","Global.closeAction=\"savecode();\";" + treeItem.toJS());
	}

}
