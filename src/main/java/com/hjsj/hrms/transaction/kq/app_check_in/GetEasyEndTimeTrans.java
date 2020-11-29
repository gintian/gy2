package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;

public class GetEasyEndTimeTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String start_date = (String) this.formHM.get("start_d"); // 开始日期
		List arrPer = (List) this.formHM.get("arrPer");// 用户信息列表
		GetValiateEndDate gv = new GetValiateEndDate(this.userView,
				this.frameconn);
		String nbase = "";
		String a0100 = "";
		if (!arrPer.isEmpty()) {
			String infoStr = (String) arrPer.get(0);
			nbase = infoStr.substring(0, 3);
			a0100 = infoStr.substring(3);
		} else {
			return;
		}
		java.util.Date startDate = OperateDate.strToDate(start_date,
				"yyyy-MM-dd");
		Map timeMap = gv.getTimeByDate(nbase, a0100, startDate);
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
