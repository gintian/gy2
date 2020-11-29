package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;


public class BudgetSaveFormulaXmlTrans extends IBusiness {	
	private String formula_id="";
	private String rowrange="";//行范围
	private String colrange="";//列范围
	private String formulacontent="";//公式内容
	private String tj_where="";// 统计条件	
	public void execute() throws GeneralException {			
		try {
			HashMap hm = this.getFormHM();
			formula_id = (String)hm.get("formula_id");
			formula_id = formula_id!=null&&formula_id.length()>0?formula_id:"0";
		
			BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn,this.formula_id);	

			if ((String)hm.get("rowcolflag") != null){//行列公式标志
				rowrange = (String)hm.get("rowcolflag");
				rowrange = rowrange!=null&&rowrange.length()>0?rowrange:"1";	
				XmlBo.setRowcolflag(rowrange);
			}	
			
			if ((String)hm.get("rowrange") != null){//行范围
				rowrange = (String)hm.get("rowrange");
				rowrange = rowrange!=null&&rowrange.length()>0?rowrange:" ";	
				rowrange=SafeCode.decode(rowrange);
				rowrange=PubFunc.keyWord_reback(rowrange);
				XmlBo.setRowrange(rowrange);
			}	
			
			if ((String)hm.get("colrange") != null){//列范围
				colrange = (String)hm.get("colrange");
				colrange = colrange!=null&&colrange.length()>0?colrange:" ";	
				XmlBo.setColrange(colrange);
			}	
			
			if ((String)hm.get("formulacontent") != null){//公式内容
				formulacontent = (String)hm.get("formulacontent");
				formulacontent = formulacontent!=null&&formulacontent.length()>0?formulacontent:" ";	
				formulacontent=SafeCode.decode(formulacontent);
				formulacontent=PubFunc.keyWord_reback(formulacontent);
				XmlBo.setFormulacontent(formulacontent);
			}	
			
			if ((String)hm.get("tj_where") != null){// 统计条件
				tj_where = (String)hm.get("tj_where");				
				tj_where = tj_where!=null&&tj_where.length()>0?tj_where:" ";	
				tj_where=SafeCode.decode(tj_where);
				tj_where=PubFunc.keyWord_reback(tj_where);
				XmlBo.setTj_where(tj_where);
			}				
		
		}catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

}
