package com.hjsj.hrms.transaction.mobileapp.kq;

import com.hjsj.hrms.transaction.mobileapp.kq.checkin.CheckIn;
import com.hjsj.hrms.transaction.mobileapp.kq.checkin.LocusSearch;
import com.hjsj.hrms.transaction.mobileapp.kq.holiday.GetHolidayYear;
import com.hjsj.hrms.transaction.mobileapp.kq.holiday.HolidayDetail;
import com.hjsj.hrms.transaction.mobileapp.kq.holiday.MyHoliday;
import com.hjsj.hrms.transaction.mobileapp.kq.holiday.MyTransferHoliday;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
/**
 * 
 * <p>Title: MobileCheckInTrans </p>
 * <p>Description: 移动平台考勤交易类 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-22 下午03:27:33</p>
 * @author tiany
 * @version 1.0
 */
public class MobileCheckingAttendanceTrans extends IBusiness { 
	
	private static final long serialVersionUID = 1L;
	
	private final String CHECKIN = "0";//签到
    //private final String CHECKOUT = "1";//签退
    private final String LOCUS_SEARCH = "2";//轨迹查询
    private final String MYHOLIDAY = "3";//我的假期
    private final String MYTRANSFERHOLIDAY = "4";//我的调休
    private final String HOLIDAYDETAIL = "5";//休假明细
    private final String GETHOLIDAYYEARS = "6";//查询假期年份
    private final String GETTRANSFERHOLIDAYYEARS = "7";//查询假期年份
    private final String GETCHECKINFO = "8";//获取签到检查信息（签到前查询签到点和签到说明的库内长度,签到点和范围控制，手机绑定标示等)
    private final String GETCHECKOUTINFO = "9";//获取周边签到点
    
	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String transType = (String) hm.get("transType");
		hm.remove("message");
		hm.remove("succeed");
		String message = "";
		String succeed = "false";
		try {
			UserView userView = this.getUserView();
			Connection conn = this.getFrameconn();
			// 不同业务流程分支点
			if (transType != null) {
				if (CHECKIN.equals(transType)) {// 签到
					new CheckIn().execute(userView, hm, conn);
				} else if (LOCUS_SEARCH.equals(transType)) {// 轨迹查询
					new LocusSearch().execute(userView, hm, conn);
				} else if (MYHOLIDAY.equals(transType)) {// 我的假期
					new MyHoliday().execute(userView, hm, conn);
				} else if (MYTRANSFERHOLIDAY.equals(transType)) {// 我的调休
					new MyTransferHoliday().execute(userView, hm, conn);
				} else if (HOLIDAYDETAIL.equals(transType)) {// 休假明细
					new HolidayDetail().execute(userView, hm, conn);
				} else if (GETHOLIDAYYEARS.equals(transType) || GETTRANSFERHOLIDAYYEARS.equals(transType)) {// 查询假期年份或休假年份
					new GetHolidayYear().execute(userView, hm, conn, transType);
				} else if (GETCHECKINFO.equals(transType)) {// 获取签到检查信息（签到前查询签到点和签到说明的库内长度,签到点和范围控制，手机绑定标示等)
					new CheckIn().getCheckInfo(userView, hm, conn);
				} else if (GETCHECKOUTINFO.equals(transType)) {// 获取周边签到点
					new CheckIn().getCheckOutInfo(userView, hm, conn);
				} else {
					message = ResourceFactory.getProperty("mobileapp.kq.error.transTypeError");
					hm.put("succeed", succeed);
					hm.put("message", message);
				}
			} else {
				hm.put("succeed", succeed);
				hm.put("message", message);
				message = ResourceFactory.getProperty("mobileapp.kq.error.transTypeError");
			}

		} catch (Exception e) {
			succeed = "false";
			String errorMsg = e.toString();
			int index_i = errorMsg.indexOf("description:");
			message = errorMsg.substring(index_i + 12);
			e.printStackTrace();
			hm.put("message", message);
			this.cat.error(e.getMessage());
		}

	}    
       
}
