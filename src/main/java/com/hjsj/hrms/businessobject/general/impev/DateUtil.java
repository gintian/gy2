package com.hjsj.hrms.businessobject.general.impev;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String format(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
		
	}

	public static String getLastDayOfMonth() {
		Calendar cDay1 = Calendar.getInstance();
		cDay1.setTime(new Date());
		final int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		Date lastDate = cDay1.getTime();
		lastDate.setDate(lastDay);
		return format(lastDate);
	}
	
	public static String getFirstDayOfMonth() {
		Calendar cDay1 = Calendar.getInstance();
		cDay1.setTime(new Date());
		final int lastDay = cDay1.getActualMinimum(Calendar.DAY_OF_MONTH);
		Date lastDate = cDay1.getTime();
		lastDate.setDate(lastDay);
		return format(lastDate);
	}
}
