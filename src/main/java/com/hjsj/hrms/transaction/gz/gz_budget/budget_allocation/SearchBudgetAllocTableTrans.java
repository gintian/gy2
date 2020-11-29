package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title: SearchBudgetAllocTableTrans.java</p>
 * <p>Description>: 获得预算分配数据界面</p>
 * <p>Company: HJSJ</p>
 * <p>Create Time: Oct 18, 2012</p>
 * @version: 5.0
 * @author genglz
 */
public class SearchBudgetAllocTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b0110="";
			if(hm.get("a_code")!=null)
			{
				b0110 = ((String)hm.get("a_code")).substring(2);
			}
			
			BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
			String tabName = (String)sysbo.getSysValueMap().get("ysze_set");
			Integer budget_id = allocBo.getCurrentBudgetId(); 
			String currentBudgetDesc = allocBo.getBudgetDesc(budget_id);
			LazyDynaBean bean = allocBo.getBudgetStatus(budget_id);
			String budgetStatusDesc = (String)bean.get("statusDesc");
			String budgetStatus = (String)bean.get("status");
			
			bean=allocBo.getAppealStatus(b0110, budget_id.toString());
			String amountDesc="";
			if ("1".equals(bean.get("exists")))  // 预算单位
			{   
				String ze=(String)sysbo.getSysValueMap().get("ysze_ze_menu");
				FieldItem fld=DataDictionary.getFieldItem(ze);
				if(fld!=null)
					amountDesc = fld.getItemdesc();
				else
					amountDesc = ResourceFactory.getProperty("gz.budget.budgeting.zonge");
				amountDesc += "："+((Double)bean.get("amount")).toString();               
			}			
			
			ArrayList fieldList=allocBo.getFieldList(b0110,budget_id);
			String sql=allocBo.getSql(b0110,budget_id,fieldList);
		 
			this.getFormHM().clear();
			this.getFormHM().put("tab_name", tabName);
			
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("sql",sql);
			this.getFormHM().put("b0110", b0110);
			this.getFormHM().put("amountDesc", amountDesc);
			this.getFormHM().put("budgetStatusDesc", budgetStatusDesc);
			this.getFormHM().put("budgetStatus",budgetStatus);
			this.getFormHM().put("currentBudgetDesc",currentBudgetDesc);
			this.getFormHM().put("budget_id", budget_id.toString());
			this.getFormHM().put("selectedUnit", b0110);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
