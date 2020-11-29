package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Map;

public class GetEndTimeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String start_date = (String) this.formHM.get("start_date");
		String infoStr = (String) this.formHM.get("infoStr");
		String nbase = (String) this.formHM.get("dbpre");
		GetValiateEndDate gv = new GetValiateEndDate(this.userView,
				this.frameconn);
		ArrayList list = gv.userInfo(infoStr);
		if(list.isEmpty()) return;
		Map infoMap = (Map) list.get(0);
		java.util.Date startDate = OperateDate.strToDate(start_date,
				"yyyy-MM-dd");
		Map timeMap = gv.getTimeByDate(nbase, (String) infoMap.get("a0100"),
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
