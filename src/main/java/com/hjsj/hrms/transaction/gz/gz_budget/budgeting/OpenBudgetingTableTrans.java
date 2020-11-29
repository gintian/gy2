package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenBudgetingTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String a_code=(String) hm.get("a_code");
			String tabid=(String)this.getFormHM().get("tab_id");
			String brefresh=(String)hm.get("brefresh");
			if ("true".equals(brefresh)){
				String requestTabid=(String)hm.get("tabid");	
				if (!"root".equals(requestTabid) && !("".equals(requestTabid))){
					tabid=requestTabid;	
				}
				hm.remove("brefresh");	
			}
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,tabid);
			String tableName=bo.getTableName();
			ArrayList fieldlist = bo.getFieldList();
			String sql = bo.getSQL();
			String tab_name = bo.getTab_Name(tabid);
			String infoStr=bo.getInfoStr(tab_name);
			String canImport=bo.getCanImports();
			this.userView.getHm().put("budgeting_tabid", tabid);

			this.getFormHM().put("fieldlist",fieldlist);
			this.getFormHM().put("sql", sql);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("tab_id",tabid);
			this.getFormHM().put("tab_type",bo.getGz_budget_tabVo().getInt("tab_type")+"");
			this.getFormHM().put("infoStr", infoStr);
			this.getFormHM().put("canImport",canImport);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
