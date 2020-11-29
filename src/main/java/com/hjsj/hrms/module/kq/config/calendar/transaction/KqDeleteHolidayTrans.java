package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.ArrayList;

public class KqDeleteHolidayTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		JSONObject returnJson = new JSONObject();
		String return_code = "success";
		String return_msg = "success";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String holiday = (String) this.getFormHM().get("holiday");
			JSONObject jsonObj = JSONObject.fromObject(holiday);
			String id = (String) jsonObj.get("id");
			ArrayList list = new ArrayList();
			String sql = "delete from kq_feast where feast_id =?";
			list.add(id);
			dao.delete(sql, list);
		} catch (Exception ee) {
			returnJson.put("return_code", "fail");
			returnJson.put("return_msg", "2");
			ee.printStackTrace();
		} finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
			this.formHM.put("returnStr", returnJson.toString());
		}
	}

}
