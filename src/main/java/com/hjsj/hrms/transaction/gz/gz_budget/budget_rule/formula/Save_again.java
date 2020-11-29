package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class Save_again extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			HashMap hm = this.getFormHM();
			String base = "no";
			String formulaname = (String) hm.get("formulaname");
			String formula_id = (String) hm.get("formula_id");
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			StringBuffer sqll = new StringBuffer();
			StringBuffer sql2 = new StringBuffer();
			StringBuffer sql3 = new StringBuffer();
			StringBuffer sql4 = new StringBuffer();
			int c = 0;
			int cc = 0;
			String tab_id = "";
			String formuladcrp = "";
			String formulatype = "";
			String destflag = "";
			String extattr = "";
			sql.append("select max(formula_id) as a from gz_budget_formula");
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				int b = this.frowset.getInt("a");
				c = b + 1;
			}
			sqll.append("select max(seq) as aa from gz_budget_formula");
			this.frowset = dao.search(sqll.toString());
			if (this.frowset.next()) {
				int bb = this.frowset.getInt("aa");
				cc = bb + 1;
			}
			sql2.append("select tab_id,formuladcrp,formulatype,destflag,extattr from gz_budget_formula where formula_id = "+formula_id+"");
			this.frowset = dao.search(sql2.toString());
			if (this.frowset.next()) {
				formuladcrp = this.frowset.getString("formuladcrp");
				formulatype = this.frowset.getString("formulatype");
				destflag = this.frowset.getString("destflag");
				extattr = this.frowset.getString("extattr");
				tab_id = this.frowset.getString("tab_id");
			}
			sql3.append("insert into  gz_budget_formula(formula_id,tab_id,formulaname,");
			sql3.append("formuladcrp,formulatype,destflag,seq,extattr) values(");
			sql3.append(c + ",");
			sql3.append(tab_id + ",'");
			sql3.append(formulaname + "',");
			sql3.append("'" + formuladcrp + "',");
			sql3.append("'" + formulatype + "',");
			sql3.append("'" + destflag + "',");
			sql3.append("'" + cc + "',");
			sql3.append("'" + extattr + "')");
			dao.update(sql3.toString());
			base = "yes";
			hm.put("base", base);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
