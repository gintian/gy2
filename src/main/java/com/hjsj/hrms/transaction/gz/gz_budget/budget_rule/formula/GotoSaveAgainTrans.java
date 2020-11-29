package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

public class GotoSaveAgainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String formula_id = (String) this.getFormHM().get("formula_id");
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			sql
					.append("select tab_id,formulaname from gz_budget_formula where formula_id = "
							+ formula_id + "");
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String tab_id = this.frowset.getString("tab_id");
				String formulaname = this.frowset.getString("formulaname");
				StringBuffer sqll = new StringBuffer();
				sqll
						.append("select tab_name from gz_budget_tab where tab_id = "
								+ tab_id + "");
				RowSet rs = dao.search(sqll.toString());
				while (rs.next()) {
					String tab_name = rs.getString("tab_name");
					bean.set("tab_name", tab_name);
				}
				bean.set("formulaname", formulaname);
				bean.set("formula_id", formula_id);
				list.add(bean);
			}
			this.getFormHM().put("list", list);
			this.getFormHM().put("formula_id", formula_id);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
