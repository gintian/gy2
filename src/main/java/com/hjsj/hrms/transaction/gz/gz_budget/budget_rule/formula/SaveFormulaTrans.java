package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap hm = this.getFormHM();
			String base = "no";
			String tab_id = (String) hm.get("tab_id");
			String formulaname = (String) hm.get("formulaname");
			String itemid = (String) hm.get("itemid");
			String itemid1 = (String) hm.get("itemid1");
			String mode = (String) hm.get("mode");
			String curformulaid = (String) hm.get("formulaid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			int formula_id = 0;
			int seq = 0;
			StringBuffer strsql = new StringBuffer();
	
			strsql.append("select max(formula_id) as maxid,max(seq) as maxseq from gz_budget_formula");
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) {
				int maxid = this.frowset.getInt("maxid");
				formula_id = maxid + 1;
				int maxseq = this.frowset.getInt("maxseq");
				seq = maxseq + 1;
			}		
			if ("insert".equals(mode)&&(!"0".equals(curformulaid))){//插入模式
				strsql.setLength(0);
				strsql.append("select seq from gz_budget_formula where formula_id =" + curformulaid);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) {
					seq = this.frowset.getInt("seq");
				}	
				strsql.setLength(0);
				strsql.append("update  gz_budget_formula set seq = seq+1 where seq >=" + String.valueOf(seq));
				dao.update(strsql.toString());
			}
			
			strsql.setLength(0);
			strsql.append("insert into  gz_budget_formula(formula_id,tab_id,formulaname,");
			strsql.append("formulatype,destflag,seq) values(");
			strsql.append(formula_id + ",");
			strsql.append(tab_id + ",'");
			strsql.append(formulaname + "',");
			strsql.append("1,1,");
			strsql.append(seq);
			strsql.append(")");
			dao.update(strsql.toString());
			
			
			base = "yes";
			hm.put("base", base);
			hm.put("formula_id", formula_id+"");
			hm.put("tab_id", tab_id+"");
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
}
