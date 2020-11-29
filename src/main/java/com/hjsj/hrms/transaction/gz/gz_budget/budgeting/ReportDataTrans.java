package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetMCInitBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ReportDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String flag=(String)this.getFormHM().get("flag");
			String info="";
			String strError="";
			if ((flag==null)||("".equals(flag))){ flag="0";}
			
			if ("0".equals(flag)){	//默认 报批
				String tab_id=(String)this.getFormHM().get("tab_id");
				BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,tab_id);
				info = bo.reportData();
				this.getFormHM().put("info",info);
				this.getFormHM().put("tab_id",tab_id);
			}
			else if ("initperson".equals(flag)){//名册初始化
				this.getFormHM().remove("flag");
				BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
				BudgetMCInitBo mcInitBo=new BudgetMCInitBo(this.frameconn,this.userView,Integer.parseInt(bo.getIndex()),bo.getUnitcode());				
				if (mcInitBo.appendPerson()) {
					info ="true";					
				}
				else{
					info ="false";
					strError =mcInitBo.getLastError();
					
				}
				this.getFormHM().put("info",info);
				this.getFormHM().put("error",strError);

			}
			else if ("calc".equals(flag)){//计算
				this.getFormHM().remove("flag");
				String formulaid=(String)this.getFormHM().get("formulaid");
				BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
				BudgetFormulaListBo formulaListBo=new BudgetFormulaListBo(this.frameconn,this.userView);
				formulaListBo.setBudgetIdx(Integer.parseInt(bo.getIndex()));
				formulaListBo.setBudgetUnit(bo.getUnitcode());
				
				if (formulaListBo.execFormula(Integer.parseInt(formulaid))) {
					info ="true";					
				}
				else{
					info ="false";
					strError =formulaListBo.getLastErrorMsg();
					
				}
				this.getFormHM().put("info",info);
				this.getFormHM().put("error",strError);
			}
			if ("checkreportstatus".equals(flag)){	//默认 报批
				BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
				info="false";
				if (bo.CanCalc()){
					info="true";
				}
				this.getFormHM().put("info",info);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
