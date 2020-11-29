package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchBudgetingTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String modelflag=(String)map.get("modelflag");
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
			if (!bo.HaveZeParameter()){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.budgeting.ysparam")));	
			}
			bo.canEnter();
			if(!"".equals(bo.getErrorMessage()))
				throw GeneralExceptionHandler.Handle(new Exception(bo.getErrorMessage()+"！"));
			if(!"".equals(bo.getErrorMessage()))
				throw GeneralExceptionHandler.Handle(new Exception(bo.getErrorMessage()+"！"));
			//初始化数据。
			bo.initAllData();
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setTarget("mil_body");
			String rootdesc=ResourceFactory.getProperty("gz.budget.budgeting.table");		   
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
			treeItem.setIcon("/images/add_all.gif");
		    treeItem.setLoadChieldAction("/gz/LoadOtherTreeServlet?modelflag="+modelflag+"&flag=");
		    treeItem.setAction("javascript:void(0)");
		    this.getFormHM().put("treeJs", treeItem.toJS());
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
