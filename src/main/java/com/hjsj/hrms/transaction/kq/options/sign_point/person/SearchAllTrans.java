package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchAllTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sql = (String)this.getFormHM().get("sql");
		sql=sql!=null?sql:"";
		sql=SafeCode.decode(sql);

		ContentDAO dao  = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		sql=PubFunc.keyWord_reback(sql);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
					list.add(this.frowset.getString("a0100")+"`"+this.frowset.getString("a0101")+"`"+this.frowset.getString("b0110")+"`"+this.frowset.getString("e0122")+"`"+this.frowset.getString("e01a1")+"`"+this.frowset.getString("dbase")+"`"+this.frowset.getString("a0000"));

			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		this.getFormHM().put("listvalue",list);
	}

}
