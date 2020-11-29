package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class BudgetExamTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag=(String)this.getFormHM().get("flag"); //1:批准   2：驳回 4报批 上报
			String b0110=(String)this.getFormHM().get("b0110");
			String budget_id=(String)this.getFormHM().get("budget_id");
			BudgetSysBo bo=new BudgetSysBo(this.getFrameconn(),this.userView);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),this.userView);
			
			
			if("4".equals(flag)){
				examBo.budgetReporting(budget_id, b0110);
			}
		
			
			HashMap sysOptionMap=bo.getSysValueMap(); 
			String sql = "select "
					+ (String) sysOptionMap.get("ysze_status_menu")
					+ " from  " + (String) sysOptionMap.get("ysze_set")
					+ " where "
					+ (String) sysOptionMap.get("ysze_idx_menu") + "="
					+ budget_id + " and b0110='" + b0110 + "'";
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				String status = this.frowset.getString(1);
				if (status == null)		status = "";
				sql = "update " + (String) sysOptionMap.get("ysze_set")
						+ " set "
						+ (String) sysOptionMap.get("ysze_status_menu")
						+ "=";

				if ("4".equals(flag)) {
					if ("01".equals(status) || "07".equals(status)|| "04".equals(status)) {
						sql += "'02'";
					} else {
						throw GeneralExceptionHandler.Handle(new Exception(
								"只能对起草|驳回|未报的单位执行上报操作!"));
					}
				} else if ("1".equals(flag)) {
					if ("02".equals(status)) {
						sql += "'03'";
					} else {
						throw GeneralExceptionHandler.Handle(new Exception(
								"只能对已报批的单位执行批准操作!"));
					}
				} else if ("2".equals(flag)) {
					if ("02".equals(status) || "03".equals(status)) {
						sql += "'07'";
					} else {
						throw GeneralExceptionHandler.Handle(new Exception(
								"只能对已批|已报批的单位执行驳回操作!"));
					}

				}
				sql += " where "+ (String) sysOptionMap.get("ysze_idx_menu")
					+ "=" + budget_id + " and b0110='" + b0110
					+ "'";
				dao.update(sql);
			}
			else {
				throw GeneralExceptionHandler.Handle(new Exception(
				"此单位不是预算单位!"));
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
