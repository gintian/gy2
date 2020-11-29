package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SelectAllTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sql = (String)this.getFormHM().get("sql");
		sql=sql!=null?sql:"";
		sql=SafeCode.decode(sql);

		ContentDAO dao  = new ContentDAO(this.getFrameconn());
		String items = "";
		sql=PubFunc.keyWord_reback(sql);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				items+=this.frowset.getString("dbpre")+this.frowset.getString("A0100")+",";
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("listvalue",items);
	}
}
