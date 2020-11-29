package com.hjsj.hrms.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OperateDate {

	private final static long DAY = 24L * 60L * 60L * 1000L;

	private final static long HOUR = 60L * 60L * 1000L;

	/**
	 * 加减天数
	 * 
	 * @param date
	 *            时间
	 * @param dayCount
	 *            天数
	 * @return java.util.Date 日期
	 */
	public static java.util.Date addDay(java.util.Date date, int dayCount) {
		long longDate = date.getTime() + dayCount * DAY;
		return new java.util.Date(longDate);
	}

	/**
	 * 加减小时
	 * 
	 * @param date
	 *            时间
	 * @param hourCount
	 *            小时
	 * @return java.util.Date 日期
	 */
	public static java.util.Date addHour(java.util.Date date, float hourCount) {
		
		Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.add(Calendar.SECOND, (int) (hourCount*60*60));
		return c.getTime();
	}

	/**
	 * 根据开始时间和结束时间计算两个时间相差多少小时
	 * 
	 * @param startTime
	 * @param endTime
	 * @return float 小时
	 */
	// private static long getHourLong(java.util.Date startTime,
	// java.util.Date endTime) {
	// if (startTime.getTime() > endTime.getTime()) {
	// endTime = addDay(endTime, 1);
	// return (endTime.getTime() - startTime.getTime());
	// } else {
	// return (endTime.getTime() - startTime.getTime());
	// }
	// }
	/**
	 * 根据日期参数date 和 格式参数 strFormat 得到一个日期样子的字符串
	 * 
	 * @param java.util.Date
	 *            strDate 时间字符串
	 * @param String
	 *            strFormat 时间格式样式
	 * @return String 日期字符串
	 */
	public static String dateToStr(java.util.Date date, String strFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
		String strDate = formatter.format(date);
		return strDate;
	}

	/**
	 * 根据日期样式的字符串参数date 和 日期格式参数 strFormat 得到一个日期
	 * 
	 * @param strDate
	 *            时间字符串
	 * @param strFormat
	 *            时间格式样式
	 * @return java.util.Date 日期型
	 */
	public static java.util.Date strToDate(String strDate, String strFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = formatter.parse(strDate, pos);
		return date;
	}

	/**
	 * 得到两个日期之间的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return 得到一个新的日期
	 */
	public static int getDayCountByDate(java.util.Date date1,
			java.util.Date date2) {
		java.util.Date dateS = getDateByFormat(date1, "yyyy-MM-dd");
		java.util.Date dateE = getDateByFormat(date2, "yyyy-MM-dd");
		long dayCount = (dateS.getTime() - dateE.getTime()) / DAY;
		return (int) dayCount;
	}

	/**
	 * 根据两个日期 求得 一个 按天数递增的 日期序列List
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static java.util.ArrayList getDayByDate(java.util.Date date1,
			java.util.Date date2) {
		java.util.ArrayList list = new java.util.ArrayList();
		int d_count = getDayCountByDate(date1, date2);
		java.util.Date addDate = date1;
		if (date1.getTime() > date2.getTime()) {
			addDate = date2;
		}
		for (int i = 0; i <= Math.abs(d_count); i++) {
			list.add(addDay(addDate, i));
		}
		return list;
	}

	/**
	 * 通过日期的到这个日期的 星期数
	 * 
	 * @param date
	 *            日期
	 * @return int
	 */
	public static int getWeek(java.util.Date date) {
		int week[] = { 7, 1, 2, 3, 4, 5, 6 };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return week[calendar.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 按照给定的模型返回日期格式
	 * 
	 * @param date
	 * @param strFormat
	 * @return
	 */
	public static java.util.Date getDateByFormat(java.util.Date date,
			String strFormat) {
		return strToDate(dateToStr(date, strFormat), strFormat);
	}

	/**
	 * 比较日期（yyyy-MM-dd部分）是否相等
	 * 
	 * @param date1
	 * @param date2
	 * @return 相当 返回 true
	 */
	public static boolean equalToDate(java.util.Date date1, java.util.Date date2) {
		date1 = getDateByFormat(date1, "yyyy-MM-dd");
		date2 = getDateByFormat(date2, "yyyy-MM-dd");
		long num = Math.abs((date1.getTime() - date2.getTime()) / DAY);
		if (num < 1) {
			return true;
		} else {
			return false;
		}
	}
}
