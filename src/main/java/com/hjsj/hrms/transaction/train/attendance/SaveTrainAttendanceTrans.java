package com.hjsj.hrms.transaction.train.attendance;
/**
 * liweichao
 */

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveTrainAttendanceTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO 培训考勤参数设置save
		String card_no=(String)this.getFormHM().get("card_no");
		String leave_early=(String)this.getFormHM().get("leave_early");
		String late_for=(String)this.getFormHM().get("late_for");
		
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		constantbo.setTextValue("/param/attendance", null);
		constantbo.setTextValue("/param/attendance/card_no", card_no);
		constantbo.setTextValue("/param/attendance/leave_early", leave_early);
		constantbo.setTextValue("/param/attendance/late_for", late_for);
		constantbo.saveStrValue();
	}

}
