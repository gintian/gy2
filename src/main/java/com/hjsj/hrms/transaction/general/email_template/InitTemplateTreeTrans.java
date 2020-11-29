package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitTemplateTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/close.png");	
			treeItem.setTarget("il_body");
			String rootdesc=ResourceFactory.getProperty("menu.gz.template");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/servlet/general/EmailTemplateTree?templatesetid=-1");
		    treeItem.setAction("javascript:void(0)");
		    this.getFormHM().put("treeJS",treeItem.toJS());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
