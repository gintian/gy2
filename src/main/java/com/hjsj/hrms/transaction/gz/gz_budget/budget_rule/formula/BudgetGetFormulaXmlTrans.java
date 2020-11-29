package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaXmlBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class BudgetGetFormulaXmlTrans extends IBusiness {
	
	private String formula_id="";		
	public void execute() throws GeneralException {		
			try {
				HashMap hm = this.getFormHM();
				formula_id = (String)hm.get("formula_id");
				formula_id = formula_id!=null&&formula_id.length()>0?formula_id:"0";
			
				BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn,this.formula_id);
				
				if ((String)hm.get("rowcolflag") != null)//行列公式标志
					hm.put("rowcolflag",XmlBo.getRowcolflag());	
				
				if ((String)hm.get("rowrange") != null)//行范围
				   hm.put("rowrange",SafeCode.encode(XmlBo.getRowrange()));	
				
				if ((String)hm.get("colrange") != null)//列范围
					   hm.put("colrange",XmlBo.getColrange());	
				
				if ((String)hm.get("formulacontent") != null)//公式内容
					   hm.put("formulacontent",SafeCode.encode(XmlBo.getFormulacontent()));	
				
				if ((String)hm.get("tj_where") != null)// 统计条件
					   hm.put("tj_where",SafeCode.encode(XmlBo.getTj_where()));	
			
			}catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			}
		}
}
