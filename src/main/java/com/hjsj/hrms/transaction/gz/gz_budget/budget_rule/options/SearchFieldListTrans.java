package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.options;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchFieldListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String fieldsetid = SafeCode.decode((String)this.getFormHM().get("fieldsetid"));
			fieldsetid = PubFunc.keyWord_reback(fieldsetid);//获得子集
			String setType = SafeCode.decode((String)this.getFormHM().get("setType"));  //1:预算总额设置   2：预算参数设置
			//获得相应指标的列表
			BudgetSysBo bo = new BudgetSysBo(this.getFrameconn(),this.userView);
			if("1".equals(setType)){//如果是预算总额设置
				ArrayList budgetIndexList = bo.getBudgetIndexList(fieldsetid);
				this.getFormHM().put("budgetIndexList", budgetIndexList);
				ArrayList budgetTotalList = bo.getBudgetTotalList(fieldsetid);
				this.getFormHM().put("budgetTotalList", budgetTotalList);
				ArrayList spStatusList = bo.getSpStatusList(fieldsetid);
				this.getFormHM().put("spStatusList", spStatusList);
			}else if("0".equals(setType)){//如果是预算参数设置
				ArrayList budgetIndexFieldList = bo.getBudgetIndexFieldList(fieldsetid);
				this.getFormHM().put("budgetIndexFieldList", budgetIndexFieldList);
				ArrayList employeeList = bo.getEmployeeList(fieldsetid);
				this.getFormHM().put("employeeList", employeeList);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
