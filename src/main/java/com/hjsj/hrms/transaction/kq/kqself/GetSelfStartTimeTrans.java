package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

public class GetSelfStartTimeTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String start_d = (String) this.formHM.get("start_d");
		GetValiateEndDate gv = new GetValiateEndDate(this.userView,
				this.frameconn);
		java.util.Date startDate = OperateDate.strToDate(start_d,
				"yyyy-MM-dd");
		Map timeMap = gv.getTimeByDate(this.userView.getDbname(),this.userView.getA0100(),
				startDate);
		if (timeMap == null || timeMap.isEmpty()) {
			this.formHM.put("start_time_h", OperateDate.dateToStr(
					new java.util.Date(), "HH"));
			this.formHM.put("start_time_m", OperateDate.dateToStr(
					new java.util.Date(), "mm"));
		} else {
			this.formHM.put("startTime", timeMap.get("startTime"));
			String time[] = ((String) timeMap.get("startTime")).split(":");
			this.formHM.put("start_time_h", time[0]);
			this.formHM.put("start_time_m", time[1]);
		}
		
	}

}
