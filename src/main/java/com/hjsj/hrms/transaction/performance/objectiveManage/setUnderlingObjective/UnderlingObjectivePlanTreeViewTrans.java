package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class UnderlingObjectivePlanTreeViewTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
//			if(this.userView.getA0100()==null||this.userView.getA0100().equals(""))
//				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			String a0100 = this.userView.getA0100();
			String posid=this.userView.getUserPosId();
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc= "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))?"KPI表格":ResourceFactory.getProperty("org.performance.card");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/servlet/performance/UnderlingObjectiveServlet?year=-1&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100+"&flag=view"));
		    treeItem.setAction("javascript:void(0)");
		    treeItem.setTarget("mil_body");
		    this.getFormHM().put("tree",treeItem.toJS());
		    
		    HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		    String returnflag=(String)hm.get("returnflag");
		    this.getFormHM().put("returnflag",returnflag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
