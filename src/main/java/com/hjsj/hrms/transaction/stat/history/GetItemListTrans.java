package com.hjsj.hrms.transaction.stat.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSetMetaData;

public class GetItemListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		String stype = (String) this.getFormHM().get("stype");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from hr_emp_hisdata where 1=2");
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			for (int i = 1; i <= size; i++) {
				String itemid = rsmd.getColumnName(i).toUpperCase();
			}
			if ("1".equals(stype) || "4".equals(stype)) {// 求和

			} else if ("2".equals(stype) || "3".equals(stype)) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
