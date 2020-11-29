package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 *  
 * <p>Title:ValidateBudgetExamTrans.java</p>
 * <p>Description>:校验是否有薪资预算数据</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 14, 2012 4:09:49 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author:dengc
 */
public class ValidateBudgetExamTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn()); 
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			BudgetExamBo Exambo = new BudgetExamBo(this.frameconn, userView);
			String rootUnitcode =Exambo.getTopUn();
			
			String budget_id="";
			String flag = (String) hm.get("flag");//预算历史模块按钮显示的控制标记 flag=2
			hm.remove("flag");
			if(flag==null || "".equals(flag)) flag="1";
			if ("2".equals(flag)) {	//预算历史
				budget_id = (String) hm.get("budget_id");
				hm.remove("budget_id");
			}
			else {
				this.frowset=dao.search("select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3)");
				if(this.frowset.next())	{
					budget_id=this.frowset.getString("budget_id");
				}
			}
			if (budget_id==null) budget_id="0";

			if("0".equals(budget_id))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.budgeting.noys")));

			String rootunitstatus =Exambo.getUnitStatus(rootUnitcode, budget_id);
			this.getFormHM().put("budget_id", budget_id);
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("rootunitcode", rootUnitcode);
			this.getFormHM().put("rootunitstatus", rootunitstatus);
			
			
			 
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
			if(!"".equals(bo.getErrorMessage()))
				throw GeneralExceptionHandler.Handle(new Exception(bo.getErrorMessage()+"！"));
			bo.canEnter();
			 
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setTarget("mil_body");
			String rootdesc=ResourceFactory.getProperty("gz.budget.budgeting.table");		   
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
			treeItem.setIcon("/images/add_all.gif");
			if ("1".equals(flag)){
				treeItem.setLoadChieldAction("/gz/LoadOtherTreeServlet?modelflag=2&flag=");
			}
			else {						
				treeItem.setLoadChieldAction("/gz/LoadOtherTreeServlet?modelflag=4&budget_id="+ budget_id+"&flag=");
			}

		    treeItem.setAction("javascript:void(0)");
		    this.getFormHM().put("treeJs", treeItem.toJS());
			this.getFormHM().put("tab_id","init");
			
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
