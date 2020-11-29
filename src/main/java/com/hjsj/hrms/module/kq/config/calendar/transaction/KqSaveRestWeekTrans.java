package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * 保存公休日
 * 
 * @author xuanz
 *
 */
public class KqSaveRestWeekTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String weekDate = (String) this.getFormHM().get("week");
		JSONObject jsonObj = JSONObject.fromObject(weekDate);
		ArrayList nameList = new ArrayList();
		JSONObject returnJson = new JSONObject();
		String return_code = "success";
		String return_msg = "success";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer ssql = new StringBuffer();
		try {
			ssql.append("delete  from kq_restofweek  where b0110 ='UN'");
			dao.delete(ssql.toString(), nameList);
			ssql.delete(0, ssql.length());
			String week = (String) jsonObj.get("week");
			ssql.append("insert into kq_restofweek (b0110,rest_weeks)values(?,?)");
			nameList.add("UN");
			nameList.add(week);
			dao.insert(ssql.toString(), nameList);

		} catch (Exception exx) {
			return_code = "fail";
			return_msg = "2";
			throw GeneralExceptionHandler.Handle(exx);
		}
		returnJson.put("return_code", return_code);
		returnJson.put("return_msg", return_msg);
		this.formHM.put("returnStr", returnJson.toString());

	}
}
