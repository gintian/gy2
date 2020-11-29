/*
 * 创建日期 2005-7-4
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author luangaojiong
 * 领导查询考核左边树交易类
 */
public class LoadInfoOrgtreeTrans extends IBusiness {

	/**
	 *	入口程序
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		treeItem.setAction("");
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=1&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
			treeItem.setLoadChieldAction("/common/org/loadtree?params=root&parentid=00&issuperuser=0&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		//System.out.println("org" + treeItem.toJS());
		this.getFormHM().put("treeCode",treeItem.toJS());
	

	}

}
