package com.hjsj.hrms.transaction.kq.kqself.details;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class AffirmKqDetailsTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		if (hm.get("date") != null) {
			this.getFormHM().put("date", hm.get("date"));
			hm.remove("date");
		}
		String date = (String)this.getFormHM().get("date");
		StringBuffer sb = new StringBuffer();
		sb.append("update Q05 set accepted = '1' ");
		sb.append("where a0100 = '" + this.userView.getA0100() + "' ");
		sb.append("and nbase = '" + this.userView.getDbname() + "' ");
		sb.append("and q03z0 = '" + date + "'");
		ContentDAO dao = new ContentDAO(frameconn);
		try {
			dao.update(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
