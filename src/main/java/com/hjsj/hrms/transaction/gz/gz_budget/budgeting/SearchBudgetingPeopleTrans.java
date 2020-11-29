package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查员工
 * @author Administrator
 *
 */
public class SearchBudgetingPeopleTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b0110="";
			String ss=(String) hm.get("a_code");
			if(ss!=null&&ss.length()>0){
				b0110 = ((String)hm.get("a_code")).substring(2);
			}
			String personname=(String) hm.get("personname");
			if(personname==null){
				personname="";
			}
			personname= SafeCode.decode(personname);
			personname=PubFunc.keyWord_reback(personname);
			hm.remove("personname");
			String tabid=(String)this.getFormHM().get("tab_id");
			BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,tabid);
			String tableName=bo.getTableName();
			ArrayList fieldlist = bo.getFieldList();
			String sql = bo.getSQL(b0110,personname);
			String tab_name = bo.getTab_Name(tabid);
			String infoStr=bo.getInfoStr(tab_name);
			String canImport=bo.getCanImports();
			String unitSpflag=bo.getUnitSpflag();
			BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			Integer budget_id = allocBo.getCurrentBudgetId(); 
			this.userView.getHm().put("budgeting_tabid", tabid);
			this.getFormHM().put("fieldlist",fieldlist);
			this.getFormHM().put("sql", sql);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("tab_id",tabid);
			this.getFormHM().put("tab_type",bo.getGz_budget_tabVo().getInt("tab_type")+"");
			this.getFormHM().put("infoStr", infoStr);
			this.getFormHM().put("canImport",canImport);
			this.getFormHM().put("budget_id", budget_id.toString());
			this.getFormHM().put("unitSpflag",unitSpflag);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
