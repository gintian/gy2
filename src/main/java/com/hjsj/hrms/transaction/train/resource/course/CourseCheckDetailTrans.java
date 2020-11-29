package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class CourseCheckDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		String id = this.getFormHM().get("id").toString();
		id = PubFunc.decrypt(SafeCode.decode(id));
		// 判断课程下是否存在学员
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "select r5000 from tr_selected_lesson where r5000='" + id + "'";
		RowSet rs = null;
		String check = "no";
		try{
			rs = dao.search(sql);
			if(rs.next()){
				check = "yes";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("check", check);
	}

}
