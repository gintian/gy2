package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:通过日期得到考期日历
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-6-17:13:53:21
 * </p>
 * 
 * @author kf-1
 * @version 1.0
 * 
 */
public class ShowWorkDataTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		String workdate = (String) hm.get("workdate");
		String b0110 = (String) hm.get("b0110");
		if (b0110.indexOf("UN") == -1) {
			b0110 = "UN" + b0110;
		}
		ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
		String rest_date = restList.get(0).toString();
		String rest_b0110 = restList.get(1).toString();
		Date date = DateUtils.getDate(workdate, "yyyy.MM.dd");
		String weekName = KqUtilsClass.getWeekName(date);
		String kqDateZs = SystemConfig.getPropertyValue("kqDaysHide");// 考勤期间下拉列表框按默认方式显示工作日标识
		String kqDateYs = SystemConfig.getPropertyValue("kqHolidaysColor");// 是否改变颜色
		try {
			String rest = IfRestDate.is_RestDate(workdate, this.userView,
					rest_date, rest_b0110, this.getFrameconn());

//			if (rest.indexOf("工作日") != -1) {
//				mm_date = mm_date + " " + rest;
//			} else {
//				// mm_date="<font color='#FF0000'>"+mm_date+" "+rest+"</font>";
//				// 改为绿色与排班统一
//				mm_date = "<font color='#00ff00'>" + mm_date + " " + rest
//						+ "</font>";
//			}
			String style = "";
			String rest_state=ResourceFactory.getProperty("kq.date.work");
			if("1".equals(kqDateYs)){
    			if (weekName.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday"))
						|| weekName.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.Saturday"))) {
					style = "COLOR='#009966'";
				} else {
					style = "COLOR='#000000'";
				}
    		}else{
    			if (rest.equals(rest_state)){
    				style = "COLOR='#000000'";
				} else {
					style = "COLOR='#009966'";
				}
    		}
			
			String strDate = workdate + " " + weekName;
    		if("1".equals(kqDateZs)){
    		    strDate = "<font " + style + ">" + strDate + " </font>";
    		}else{
    		    strDate = "<font " + style + ">" + strDate + " " + rest + "</font>";
    		}
			this.getFormHM().put("onedate", strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
