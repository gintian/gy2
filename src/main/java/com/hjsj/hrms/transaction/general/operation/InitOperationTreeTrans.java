package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitOperationTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String statid="all";
		String operationcode="all";
		if(reqhm.containsKey("operationcode")){
			operationcode=(String) reqhm.get("operationcode");
		}
		if(reqhm.containsKey("statid")){
			statid=(String) reqhm.get("statid");
		}
		String target = "mil_body";
		String xml = "/servlet/OperationTree?params=root&statid="+statid+"&target="+ target+"&operationcode="+ operationcode;
		TreeItemView treeItem = new TreeItemView();
		
		treeItem.setName("root");
		treeItem.setIcon("/images/add_all.gif");
		treeItem.setTarget(target);
		treeItem.setRootdesc(ResourceFactory.getProperty("system.operation.type"));
		treeItem.setText(ResourceFactory.getProperty("system.operation.type"));
		treeItem.setLoadChieldAction(xml);
		treeItem.setAction("/general/operation/showtable.do?b_query=link&operationcode=-1");
		hm.put("treecode",treeItem.toJS());
	}

}
