package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SaveKnowLedgeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String type_id = (String)this.getFormHM().get("type_id");
		String column = (String)this.getFormHM().get("column");
		String value = (String)this.getFormHM().get("value");
		value=value==null||value.length()<1?"0":value;
		//System.out.println(r5300+"--"+type_id+"--"+column+"--"+value);
		String sql = "update tr_exam_question_type set ";
		if("know_ids".equals(column))
			sql+=column+"='"+value+"'";
		else
			sql+=column+"="+value;
		sql+=" where r5300="+r5300+" and type_id="+type_id;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
