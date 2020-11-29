package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;

public class CheckBudgetTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 LazyDynaBean codesetbean = (LazyDynaBean) this.getFormHM().get("vo");//获得前台页面传来的对象
			 String yearnum=(String) codesetbean.get("yearnum");//获得前台弹出页面传来的值
			 String budgettype=(String) codesetbean.get("budgettype");
			 String sql="select budgettype from gz_budget_index where yearnum='"+yearnum+"'";
			 RowSet rs=dao.search(sql);
			 String budget_type="";
			 while(rs.next()){
				 budget_type=budget_type+rs.getString("budgettype")+",";
			 }
			 String checkBudgetType="";
			 if(budget_type.indexOf(budgettype)!=-1){
				 if("1".equals(budgettype)){
					 checkBudgetType=yearnum+"年已有年初预算，不能再新建年初预算！";
				 }else if("2".equals(budgettype)){
					 checkBudgetType=yearnum+"年已有年中预算，不能再新建年中预算！";
				 }
			 }
			 this.getFormHM().put("checkBudgetType", checkBudgetType);

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}	

}
