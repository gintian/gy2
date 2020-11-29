/**
 * <p>Title:</p> 
 * <p>Description:薪资预算-计算公式-统计条件</p> 
 * <p>Company:HJHJ</p> 
 * <p>Create time:${date}:${time}</p> 
 * <p>@version: 5.0</p>
 * <p>@author wangrd</p>
*/

package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class FormulaConditionTrans extends IBusiness {
	private String formula_id="";
	private String tj_type="";
	private String tab_id="0";
	private String formula_type="";//公式类别；1、2、3、4、5 录入 计算、导入、人数、和，
	private String tab_type="";//计划表类别；1 总额、2 名册、3 用工、4 其他
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();		
		String formula_id = (String)hm.get("formula_id");
		formula_id=formula_id!=null&&formula_id.length()>0?formula_id:"0";
		String tj_type = (String)hm.get("tj_type");
		tj_type=tj_type!=null&&tj_type.length()>0?tj_type:"tjwhere";		
		
		try {
			BudgetFormulaBo Bo = new BudgetFormulaBo(this.frameconn, formula_id);
			formula_type = Bo.getFormula_type();
			tab_type = Bo.getTab_type();
			
			hm.put("formula_id", formula_id);
			hm.put("tj_type", tj_type);
			
			String cType = (String) hm.get("cType");
			cType = cType != null && cType.length() > 0 ? cType : "";
			if ("".equals(cType)) { // 返回子集列表、统计条件
				if ("tjwhere".equalsIgnoreCase(tj_type)){

					hm.put("cond_setlist", Bo.GetFieldList_calc(this.userView));		

					BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn, formula_id);
					hm.put("cond_value", XmlBo.getTj_where());									
				} else {					
					BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn, formula_id);
					hm.put("cond_value", XmlBo.getRowrange());				
					hm.put("cond_setlist", Bo.GetEmptyList());
				}	
				hm.put("cond_setid", "");	
			} else if ("colitem".equals(cType)) {// 返回指标列表
				String fieldsetid = "";
				fieldsetid = (String) hm.get("fieldsetid");
				hm.put("cond_itemlist", Bo.GetColItemList(fieldsetid,this.userView));
			} else if ("codeitem".equals(cType)) {// 返回代码列表
				String itemid = "";
				itemid = (String) hm.get("itemid");
				hm.put("cond_codelist", Bo.GetCodeList(this.userView,itemid,Bo.getCodeSetid()));
			} else if ("savecond".equals(cType)) {// 保存统计条件
				String cond_value = "";
				cond_value = (String) hm.get("cond_value");
				cond_value=SafeCode.decode(cond_value);
				cond_value=PubFunc.keyWord_reback(cond_value);
				BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn, formula_id);
				XmlBo.setTj_where(cond_value);

			} else if ("curTabColItem".equals(cType)) {// 返回当前预算表列指标
				String fieldsetid = Bo.getTab_id();
				hm.put("cond_itemlist", Bo.GetColItemList(fieldsetid,this.userView));

			} else if ("saverow".equals(cType)) {// 保存行范围 即计算条件
			  String cond_value = "";
		      cond_value = (String) hm.get("cond_value");
		      cond_value=SafeCode.decode(cond_value);
		      cond_value=PubFunc.keyWord_reback(cond_value);
			  BudgetFormulaXmlBo XmlBo = new BudgetFormulaXmlBo(this.frameconn, formula_id);
			  XmlBo.setRowrange(cond_value);
		  }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
