package com.hjsj.hrms.transaction.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecrateBo;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitBugetExecTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn()); 
			ArrayList budgetYearList= new ArrayList();
			ArrayList budgetMonthList= new ArrayList();
			String budgetYear="";
			String budgetMonth="";
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String flag_init = (String) hm.get("b_init");
			
			BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.frameconn,this.userView);
			if ("init".equals(flag_init)){//初始化 需要检查预算、初始年度、月度列表
				//删除临时表，避免脏数据
				ExecBO.dropTempTable();				
				//取得预算年度列表 
				budgetYearList= ExecBO.getPublishBudgetYearList();					
				if (budgetYearList.size()<1 ){
					throw GeneralExceptionHandler.Handle(new Exception("没有已发布的预算！"));
				}
				budgetYear= ((CommonData)budgetYearList.get(0)).getDataValue();

				//取得预算月度列表 				
				for (int i=1 ;i<=12;i++){
					CommonData datavo = new CommonData(String.valueOf(i),String.valueOf(i)+"月");	
					budgetMonthList.add(datavo);
				}							
				budgetMonth="1";
				
				this.getFormHM().put("budgetYearList", budgetYearList);	
				this.getFormHM().put("budgetMonthList", budgetMonthList);	
				this.getFormHM().put("budgetYear", budgetYear);	
				this.getFormHM().put("budgetMonth", budgetMonth);	
			}
			else {//按年月刷新				
				budgetYear= (String)this.getFormHM().get("budgetYear");
				budgetMonth= (String)this.getFormHM().get("budgetMonth");	
				
			}		
			
			if (!"resubmitmonth".equals(flag_init)){	//不是按月刷新
				//取得当前年度内最近的预算
				int budget_id = ExecBO.getBudgetIdx(Integer.parseInt(budgetYear));
				int Actualbudget_id = ExecBO.getActualIdx(Integer.parseInt(budgetYear),true);
				if (budget_id==-1){
					
					throw GeneralExceptionHandler.Handle(new Exception("当前年份没有已发布的预算！"));	
				}
				this.getFormHM().put("budget_id", budget_id+"");	

				
				//取得当前用户顶级机构 用于机构树
				BudgetExamBo Exambo = new BudgetExamBo(this.frameconn, userView);
				String rootUnitcode =Exambo.getTopUn();
				this.getFormHM().put("rootUnitCode", rootUnitcode);						 

				//取得预算表树 
				TreeItemView treeItem=new TreeItemView();
				treeItem.setName("root");
				treeItem.setRootdesc("root");
				treeItem.setTitle("root");
				treeItem.setTarget("mil_body");
				String rootdesc=ResourceFactory.getProperty("gz.budget.budgeting.table");		   
			    treeItem.setRootdesc(rootdesc);
				treeItem.setText(rootdesc); 
				treeItem.setIcon("/images/add_all.gif");
			    treeItem.setLoadChieldAction("/gz/LoadOtherTreeServlet?modelflag=3&budget_id="+ String.valueOf(Actualbudget_id)+"&flag=");
			    treeItem.setAction("javascript:void(0)");
			    this.getFormHM().put("treeJs", treeItem.toJS());
				this.getFormHM().put("tab_id","init");	
				
				
			}

			
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
