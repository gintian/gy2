package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveSortPerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList sqls = new ArrayList();
		String sort = (String) hm.get("sorting");
		sort = sort != null && sort.trim().length() > 0 ? sort : "";

		String[] fitem = sort.split(",");
		;

		int length = fitem.length;
		for (int i = 0; i < length; i++) {
			String sqlstr = "update per_know set seq=" + (i)
					+ " where know_id=" + fitem[i];	
		
			sqls.add(sqlstr);
		}
		try {
			dao.batchUpdate(sqls);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hm.put("info", "ok");
	}

}
