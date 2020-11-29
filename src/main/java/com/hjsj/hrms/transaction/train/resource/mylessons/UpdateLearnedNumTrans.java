package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class UpdateLearnedNumTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5000 = (String)this.getFormHM().get("r5000");
		if(r5000==null||r5000.length()<1)
			return;
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		String sql = "update tr_selected_lesson set learnednum=" + Sql_switcher.isnull("learnednum", "0") + "+1";
		sql += " where a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"' and r5000="+r5000;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
