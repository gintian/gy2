package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class EditSave2ReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		RecordVo vo = (RecordVo) this.getFormHM().get("reportcyclevo");
		if (vo == null)
			return;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	      	
	}
	

