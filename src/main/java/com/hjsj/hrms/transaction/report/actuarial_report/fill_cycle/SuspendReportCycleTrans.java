package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class SuspendReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("report_id");
		hm.remove("id");
		RecordVo vo = new RecordVo("tt_cycle");
		vo.setInt("id", Integer.parseInt(id));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			vo = dao.findByPrimaryKey(vo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			vo.setString("status", "09");
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	




}
