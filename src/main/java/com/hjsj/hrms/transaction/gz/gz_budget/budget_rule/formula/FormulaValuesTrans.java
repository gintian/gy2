/**
 * <p>Title:</p> 
 * <p>Description:薪资预算-计算公式-指标列表</p> 
 * <p>Company:HJHJ</p> 
 * <p>Create time:${date}:${time}</p> 
 * <p>@version: 5.0</p>
 * <p>@author wangrd</p>
*/
package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula.BudgetFormulaXmlBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaListBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FormulaValuesTrans extends IBusiness {
	private ContentDAO dao=null;
	private String formula_id="";
	private String tab_name="";
	private String tab_id="0";
	private String formula_type="";//公式类别；1、2、3、4、5 录入 计算、导入、人数、和，
	private String tab_type="";//计划表类别；1 总额、2 名册、3 用工、4 其他
	
	public void execute() throws GeneralException {
		try {
			String sqlstr ="";
			HashMap hm = this.getFormHM();
			dao = new ContentDAO(this.getFrameconn());		
			
			formula_id = (String)hm.get("formula_id");			
			String flag = (String)hm.get("flag");
			flag = flag!=null&&flag.length()>0?flag:"0";

			BudgetFormulaBo Bo = new BudgetFormulaBo(this.frameconn, formula_id);
			formula_type = Bo.getFormula_type();
			tab_type = Bo.getTab_type();
			tab_id=Bo.getTab_id();
			tab_name=Bo.getTab_name();
			
			if ("getfldsetlist".equals(flag)) {// 返回指标列表
				String fieldsetid = "";
				fieldsetid = (String) hm.get("fieldsetid");
				hm.put("colitemlist", Bo.GetColItemList(fieldsetid,this.userView));
				hm.put("rowitemlist", Bo.GetRowItemList(fieldsetid));
			} else if ("getcodelist".equals(flag)) {// 返回代码列表
				String itemid = "";
				itemid = (String) hm.get("itemid");
				String tab_id = (String) hm.get("tab_id");	
				String codesetid =Bo.getCodeSetid();	
					
				if (tab_id!=null){
					try{
							Integer.parseInt(tab_id);
							String str = "select tab_type,codesetid,tab_name from gz_budget_tab  where tab_id="+ tab_id + "";			
							ArrayList dylist1 = dao.searchDynaList(str);
						    for (Iterator it = dylist1.iterator(); it.hasNext();) {
						    	DynaBean dynabean = (DynaBean) it.next();		 
						    	codesetid = dynabean.get("codesetid").toString();			 		 
						      }	
						hm.put("codeitemlist", Bo.GetCodeList(this.userView,itemid,codesetid));
					 }
						catch (Exception e) {
							hm.put("codeitemlist", Bo.GetCodeList(this.userView,itemid,codesetid));					
						}
					
				}
	

			}	else if ("updateformulatype".equals(flag)) {// 更新公式类别
				formula_type = (String)hm.get("formula_type");
				formula_type = formula_type!=null&&formula_type.length()>0?formula_type:"1";		

				sqlstr = "update gz_budget_formula set formulaType ="
						+ formula_type + " where formula_id=" + formula_id + "";
				try {
					dao.update(sqlstr);
					if ("2".equals(formula_type)){
						BudgetFormulaXmlBo xmlBo= new BudgetFormulaXmlBo(this.frameconn,this.formula_id);
						if ("1".equals(xmlBo.getRowcolflag())){
							xmlBo.setRowcolflag("1");
						}
					}
					hm.put("formula_id", this.formula_id);
				} catch (Exception sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				}
			} else if ("getformulaname".equals(flag)) {// 获取公式名称
				hm.put("formula_name", Bo.getFormula_name().trim());	

			} else if ("updateformulaname".equals(flag)) {// 更新公式名称
				String formula_name = (String)hm.get("formula_name");
				
				sqlstr = "update gz_budget_formula set formulaname ='"
						+ formula_name + "' where formula_id=" + formula_id + "";
				try {
					dao.update(sqlstr);			
				} catch (Exception sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				}
				hm.put("formula_name", formula_name);
				hm.put("formula_id", this.formula_id);
			} else if ("checkformula".equals(flag)) {// 检查公式内容
				String formulacontent = (String)hm.get("formulacontent");
				formulacontent=SafeCode.decode(formulacontent);
				formulacontent=PubFunc.keyWord_reback(formulacontent);
				
				String info="true";
				String strError="";
				if (!"".equals(formulacontent.trim())){
					BudgetFormulaListBo formulaListBo=new BudgetFormulaListBo(this.frameconn,this.userView,Integer.parseInt(this.formula_id));
					if (formulaListBo.verifyFormula(Integer.parseInt(this.formula_id),formulacontent)) {
						info ="true";					
					}
					else{
						info ="false";
						strError =formulaListBo.getLastErrorMsg();
					}
				}
				
				this.getFormHM().put("info",info);
				this.getFormHM().put("error",SafeCode.encode(strError));
			} else if ("checkcondformula".equals(flag)) {// 检查计算条件公式
				String formulacontent = (String)hm.get("formulacontent");
				formulacontent=SafeCode.decode(formulacontent);
				formulacontent=PubFunc.keyWord_reback(formulacontent);				
				String ctj = (String)hm.get("btj");
				boolean bTj=false;
				if ("true".equals(ctj)){
					bTj=true;
				}
				String info="true";
				String strError="";
				if (!"".equals(formulacontent.trim())){
					if (Bo.CheckFormula(this.userView, formulacontent, bTj)){
						info ="true";					
					}
					else{
						info ="false";
						strError =Bo.getStrError();
					}
					
				}

				this.getFormHM().put("info",info);
				this.getFormHM().put("error",SafeCode.encode(strError));
				
			} else if ("checkrowcol".equals(flag)) {// 检查行列范围
				String info="true";
				String strError="";
				BudgetFormulaListBo formulaListBo=new BudgetFormulaListBo(this.frameconn,this.userView,Integer.parseInt(this.formula_id));
				if (formulaListBo.verifyRowCol(Integer.parseInt(this.formula_id))) {
					info ="true";					
				}
				else{
					info ="false";
					strError =formulaListBo.getLastErrorMsg();
				}
				
				this.getFormHM().put("info",info);
				this.getFormHM().put("error",SafeCode.encode(strError));
				
				
			} else { // 返回子集类、及控制界面显示
				hm.put("formula_type", formula_type);
				hm.put("tab_type", tab_type);
				hm.put("tab_name", tab_name);
				if ("3".equals(formula_type)){ // 导入项
					if ("2".equals(tab_type)){ ////员工名册
						hm.put("setlist", Bo.GetFieldList());
					} else{						
						hm.put("setlist", Bo.GetFieldList_imp(this.userView));
					}
				} else if ("2".equals(formula_type)) {// 计算项
					hm.put("setlist", Bo.GetFieldList_calc(this.userView));
				}else
					hm.put("setlist", Bo.GetFieldList());
			} 
		} catch (GeneralException e) {
			e.printStackTrace();
			}
   }
	
	

	
}
	
