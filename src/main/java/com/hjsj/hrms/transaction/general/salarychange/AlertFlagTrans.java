package com.hjsj.hrms.transaction.general.salarychange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class AlertFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update gzAdj_formula set flag=");
		sqlstr.append(flag);
		sqlstr.append(" where tabid=");
		sqlstr.append(tableid);
		sqlstr.append(" and id=");
		sqlstr.append(id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sqlstr.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
