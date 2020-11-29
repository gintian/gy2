package com.hjsj.hrms.transaction.general.salarychange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SortFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		String tabid = (String)reqhm.get("tabid");
		reqhm.remove("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		ArrayList sortlist = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select id,chz from gzAdj_formula where tabid=");
		buf.append(tabid);
		buf.append(" order by nsort");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next()){
				CommonData dataobj = new CommonData(this.frowset.getString("id"),this.frowset.getString("chz"));
				sortlist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("sortlist",sortlist);
		this.getFormHM().put("tableid",tabid);
	}

}
