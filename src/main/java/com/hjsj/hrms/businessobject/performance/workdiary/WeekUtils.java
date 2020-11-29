package com.hjsj.hrms.businessobject.performance.workdiary;

import com.hrms.frame.dao.utility.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 *<p>Title:</p> 
 *<p>Description:时间处理</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class WeekUtils {
	/**
	 * 某年某月有几个星期
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  int
	 * */
	public int totalWeek(int yearnum,int monthnum){
		Date firstDate = numWeek(yearnum,monthnum,1,1);
		Date finalDate = finalWeek(yearnum,monthnum,7);

		return (DateUtils.dayDiff(firstDate,finalDate)+1)/7;
	}
	/**
	 * 某年某月的第几周星期几的日期(返回日期串格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @param week  当月的第几周
	 * @param  severalWeeks  星期几
	 * @return  date
	 * */
	public Date numWeek(int yearnum,int monthnum,int week,int severalWeeks){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.set(GregorianCalendar.DAY_OF_WEEK,GregorianCalendar.MONDAY); 
		cal.add(GregorianCalendar.DAY_OF_MONTH, 7*(week-1));
		cal.add(GregorianCalendar.DAY_OF_MONTH, severalWeeks-1);
		
		return cal.getTime();
	}
	/**
	 * 某年某月的第几周星期几的日期(返回字符串格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @param week  当月的第几周
	 * @param  severalWeeks 星期几
	 * @return  date
	 * */
	public String numWeekStr(int yearnum,int monthnum,int week,int severalWeeks){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.set(GregorianCalendar.DAY_OF_WEEK,GregorianCalendar.MONDAY); 
		cal.add(GregorianCalendar.DAY_OF_MONTH, 7*(week-1));
		cal.add(GregorianCalendar.DAY_OF_MONTH, severalWeeks-1);
		int day = cal.get(Calendar.DATE);
		monthnum=cal.get(Calendar.MONTH)+1;
		yearnum=cal.get(Calendar.YEAR);
		String monthnumstr=monthnum<10?"0"+monthnum:monthnum+"";
		String daystr = day<10?"0"+day:day+"";

		String datetime = yearnum+"-"+monthnumstr+"-"+daystr;
		return datetime;
	}
	/**
	 * 当前时间这周星期几的日期(返回日期串格式日期)
	 * @param  severalWeeks 第几周
	 * @return  date
	 * */
	public Date numWeek(int severalWeeks){
		int monthnum = Calendar.getInstance().get(Calendar.MONTH)+1;
		int yearnum = Calendar.getInstance().get(Calendar.YEAR);
		int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
		
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.set(GregorianCalendar.DAY_OF_WEEK,GregorianCalendar.MONDAY); 
		cal.add(GregorianCalendar.DAY_OF_MONTH, 7*(week-1));
		cal.add(GregorianCalendar.DAY_OF_MONTH, severalWeeks-1);
		
		return cal.getTime();
	}
	/**
	 * 当前时间这周星期几的日期(返回日期串格式日期)
	 * @param  severalWeeks 第几周
	 * @return  GregorianCalendar
	 * */
	public GregorianCalendar numWeekcal(int severalWeeks){
		int monthnum = Calendar.getInstance().get(Calendar.MONTH)+1;
		int yearnum = Calendar.getInstance().get(Calendar.YEAR);
		int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
		
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.set(GregorianCalendar.DAY_OF_WEEK,GregorianCalendar.MONDAY); 
		cal.add(GregorianCalendar.DAY_OF_MONTH, 7*(week-1));
		cal.add(GregorianCalendar.DAY_OF_MONTH, severalWeeks-1);
		
		return cal;
	}
	/**
	 * 当前时间这周星期几的日期(返回字符串格式日期)
	 * @param  severalWeeks 第几周
	 * @return  datetime
	 * */
	public String numWeekStr(int severalWeeks){
		int monthnum = Calendar.getInstance().get(Calendar.MONTH)+1;
		int yearnum = Calendar.getInstance().get(Calendar.YEAR);
		int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
		
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.set(GregorianCalendar.DAY_OF_WEEK,GregorianCalendar.MONDAY); 
		cal.add(GregorianCalendar.DAY_OF_MONTH, 7*(week-1));
		cal.add(GregorianCalendar.DAY_OF_MONTH, severalWeeks-1);
		int day = cal.get(Calendar.DATE);
		monthnum=cal.get(Calendar.MONTH)+1;
		yearnum=cal.get(Calendar.YEAR);
		String monthnumstr=monthnum<10?"0"+monthnum:monthnum+"";
		String daystr = day<10?"0"+day:day+"";

		String datetime = yearnum+"-"+monthnumstr+"-"+daystr;
		
		return datetime;
	}
	/**
	 * 这个月的最后一周星期几的日期
	 * @param yearnum 年
	 * @param monthnum 月
	 * @param week  当月的第几周
	 * @return  date
	 * */
	public Date finalWeek(int yearnum,int monthnum,int severalWeeks){
		Date affterDate=null; 

		if(monthnum>11){
			affterDate=numWeek(yearnum+1,1,1,1);
		}else{
			affterDate=numWeek(yearnum,monthnum+1,1,1);
		}
		GregorianCalendar cal = new GregorianCalendar(); 
			cal.setTime(affterDate);
			cal.add(GregorianCalendar.DAY_OF_MONTH,severalWeeks-8);   
		
		return cal.getTime();
	}
	/**
	 * 获取当月最后一天日期(返回日期格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  date
	 * */
	public Date lastMonth(int yearnum,int monthnum){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.add(Calendar.MONTH,1);   
		cal.add(Calendar.DAY_OF_MONTH,-1);   
		
		return cal.getTime();
	}
	//判断当前年月日 是否是周六周日
	public int isZhouMe(int yearnum , int monthnum , int daynum){
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(yearnum+"-"+monthnum+"-"+daynum); 
		} catch (Exception e) {
		}
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(date);
		 if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			return 1;
		 }else{
			 return 0;
		 }
	}
	/**
	 * 获取当月最后一天日期(返回日期格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  date
	 * */
	public GregorianCalendar lasGretMonth(int yearnum,int monthnum){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.add(Calendar.MONTH,1);   
		cal.add(Calendar.DAY_OF_MONTH,-1);   
		
		return cal;
	}
	/**
	 * 获取当月最后一天日期(返回字符串格式日期)
	 * @param yearnum 年
	 * @param monthnum 月
	 * @return  endtime
	 * */
	public String lastMonthStr(int yearnum,int monthnum){
		Date mDate=null; 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try{ 
			mDate=sdf.parse(yearnum+"-"+monthnum+"-"+"01"); 
		}catch(ParseException pe){
			
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.add(Calendar.MONTH,1);   
		cal.add(Calendar.DAY_OF_MONTH,-1);
		int day = cal.get(Calendar.DATE);
		monthnum=cal.get(Calendar.MONTH)+1;
		yearnum=cal.get(Calendar.YEAR);
		String monthnumstr=monthnum<10?"0"+monthnum:monthnum+"";
		String daystr = day<10?"0"+day:day+"";

		String endtime = yearnum+"-"+monthnumstr+"-"+daystr;
		
		return endtime;
	}
	public String firstDateOfMonth()
	{
		String str = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		return str.substring(0,8)+"01";
		
	}
	/**
	 * 获取当前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strDate(){
			int day = Calendar.getInstance().get(Calendar.DATE);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			String strday = day+"";
			String strmonth = month+"";
			
			if(day<10){
				strday = "0"+strday;
			}
			if(month<10){
				strmonth = "0"+strmonth;
			}	
			return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 获取当前时分秒 格式xxxx-xx-xx
	 * @return String hms 字符串
	 */
	public String strHMS(){
			int h = Calendar.getInstance().get(Calendar.HOUR);
			int m = Calendar.getInstance().get(Calendar.MINUTE);
			int s = Calendar.getInstance().get(Calendar.SECOND);
			
			String hs = h+"";
			String ms = m+"";
			String ss = s+"";
			
			if(h<10){
				hs = "0"+h;
			}
			if(m<10){
				ms = "0"+m;
			}
			if(s<10){
				ss = "0"+s;
			}
			
			return hs+":"+ms+":"+ss;
	}
	/**
	 * 获取距离当前日期几个月的字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strDate(int months){
			int day = Calendar.getInstance().get(Calendar.DATE);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1+months;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			if(month>12){
				month = month-12;
				year +=1;
			}
			if(month<1){
				month = month+12;
				year -=1;
			}
			
			String strday = day+"";
			String strmonth = month+"";
			
			if(day<10){
				strday = "0"+strday;
			}
			if(month<10){
				strmonth = "0"+strmonth;
			}	
			return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 获取某个日期所在的某个季度的第一天 
	 * @return data
	 */
	public Date getDataFistQuarter(Date indate) {
		 GregorianCalendar cal = new GregorianCalendar();
		 cal.setTime(indate);

	    //季，先求本季度的第一天，月减3得起始日，日减1得终止日
	    cal.set(GregorianCalendar.DATE, 1);
	    cal.set(GregorianCalendar.MONTH, cal.get(GregorianCalendar.MONTH) / 3 * 3);
	    return cal.getTime();
	}
	/**
	 * 获取某个日期所在的某个季度的最后一天
	 * @return data
	 */
	public Date getDataEndQuarter(Date indate) {
		 GregorianCalendar cal = new GregorianCalendar();
		 cal.setTime(indate);

	    //季，先求本季度的第一天，月减3得起始日，日减1得终止日
	    cal.set(GregorianCalendar.DATE, 1);
	    cal.set(GregorianCalendar.MONTH, cal.get(GregorianCalendar.MONTH) / 3 * 3);
	    cal.add(GregorianCalendar.MONTH, 3);
	    cal.add(GregorianCalendar.DATE, -1);
	    return cal.getTime();
	}
	/**
	 * 当前日期所处的季节
	 * @param indate
	 * @return quarter //季节
	 */
	public int getQuarter(Date indate){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(indate);
		int month = cal.get(GregorianCalendar.MONTH);
		double quarters = (month+1.0) / 3.0;
		int quarter = (int) Math.ceil(quarters);
		return quarter;
	}
	/**
	 * 将日期转换成字符串
	 * @param date 
	 */
	public String dateTostr(Date date){
		if(date==null) {
            return "";
        }
		int day = DateUtils.getDay(date);
		int month = DateUtils.getMonth(date);
		int year = DateUtils.getYear(date);
		
		String strday = day+"";
		String strmonth = month+"";
		
		if(day<10){
			strday = "0"+strday;
		}
		if(month<10){
			strmonth = "0"+strmonth;
		}
		
		return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 获取日期时分秒
	 * @param date 
	 */
	public String dateTohms(Date date){
		Calendar cal = DateUtils.getCalendar(date);
		
		int h = DateUtils.getHour(date);
		int m = DateUtils.getMinute(date);
		int s = cal.get(Calendar.SECOND);
		
		String hs = h+"";
		String ms = m+"";
		String ss = s+"";
		
		if(h<10){
			hs = "0"+h;
		}
		if(m<10){
			ms = "0"+m;
		}
		if(s<10){
			ss = "0"+s;
		}
		
		return hs+":"+ms+":"+ss;
	}
	/**
	 * 将字符串转换成日期
	 * @param Date date 日期型
	 */
	public Date strTodate(String strdate){
		Date date = null;
		try {
		      if (strdate != null && !"".equals(strdate)){
		    	  if(strdate.indexOf("-")!=-1) {
                      date = (new SimpleDateFormat("yyyy-MM-dd")).parse(strdate);
                  } else {
                      date = (new SimpleDateFormat("yyyy.MM.dd")).parse(strdate);
                  }
		      }
		}catch (ParseException e) {
		        e.printStackTrace(); 
		}
		return date;
	}
	/**
	 * 相差小时
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public static long getHourSpan(Date start_date,Date end_date){    	
		int sY=DateUtils.getYear(start_date);
		int sM=DateUtils.getMonth(start_date);
		int sD=DateUtils.getDay(start_date);
		int sH=DateUtils.getHour(start_date);
		int smm=DateUtils.getMinute(start_date);

		int eY=DateUtils.getYear(end_date);
		int eM=DateUtils.getMonth(end_date);
		int eD=DateUtils.getDay(end_date);
		int eH=DateUtils.getHour(end_date);
		int emm=DateUtils.getMinute(end_date);
		GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
		GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
		Date date1= d1.getTime();		         
		Date date2= d2.getTime();
		long l1=date1.getTime();
		long l2=date2.getTime();
		long part=(l2-l1)/(60*60*1000L);
		return part;
	}
	/**
	 * 相差小时
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public static long getYearSpan(Date start_date,Date end_date){    	
		int sY=DateUtils.getYear(start_date);
		int sM=DateUtils.getMonth(start_date);
		int sD=DateUtils.getDay(start_date);
		int sH=DateUtils.getHour(start_date);
		int smm=DateUtils.getMinute(start_date);

		int eY=DateUtils.getYear(end_date);
		int eM=DateUtils.getMonth(end_date);
		int eD=DateUtils.getDay(end_date);
		int eH=DateUtils.getHour(end_date);
		int emm=DateUtils.getMinute(end_date);
		GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
		GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
		Date date1= d1.getTime();		         
		Date date2= d2.getTime();
		long l1=date1.getTime();
		long l2=date2.getTime();
		long part=(l2-l1)/(24*365*60*60*1000L);
		return part;
	}
	/**
	 * 将日期补全
	 * @param date 
	 */
	public static String dateToComplete(String datestr){
		int day = 1;
		int month = 1;
		int year = 0;
		if(datestr!=null&&datestr.trim().length()>=4){
			String dataArr[] = null;
			if(datestr.indexOf("-")!=-1) {
                dataArr = datestr.split("-");
            } else {
                dataArr = datestr.split("\\.");
            }
			if(dataArr==null||dataArr.length<1) {
                return datestr;
            }
			try{
				if(dataArr.length>0) {
                    year = Integer.parseInt(dataArr[0]);
                }
				if(dataArr.length>1) {
                    month = Integer.parseInt(dataArr[1]);
                } else {
                    month = 1;
                }
				if(dataArr.length>2) {
                    day = Integer.parseInt(dataArr[2]);
                } else {
                    day = 1;
                }
			}catch(Exception e){
				return datestr;
			}
		}else{
			return datestr;
		}
		
		String strday = day+"";
		String strmonth = month+"";
		
		if(day<10){
			strday = "0"+strday;
		}
		if(month<10){
			strmonth = "0"+strmonth;
		}
		
		return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 比较两个日期大小
	 * @param date1
	 * @param date2
	 * @return 如果date1>date2 返回true 否则返回false;
	 */
	public static boolean compareTime(Date date1,Date date2){
		if(date1==null||date2==null) {
            return false;
        }
		if(date1.getTime()>date2.getTime()) {
            return true;
        } else {
            return false;
        }
	}
	/**
	 * 比较两个日期大小
	 * @param date1
	 * @param date2
	 * @return 如果date1>date2 返回2,date1=date2 返回1,date1<date2 返回0;
	 */
	public static int comPareTime(Date date1,Date date2){
		if(date1==null||date2==null) {
            return 1;
        }
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);

		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(date2);
		
		if(cal1.get(GregorianCalendar.YEAR)>cal2.get(GregorianCalendar.YEAR)){
			return 2;
		}else if(cal1.get(GregorianCalendar.YEAR)==cal2.get(GregorianCalendar.YEAR)){
			if(cal1.get(GregorianCalendar.MONTH)>cal2.get(GregorianCalendar.MONTH)){
				return 2;
			}else if(cal1.get(GregorianCalendar.MONTH)==cal2.get(GregorianCalendar.MONTH)){
				if(cal1.get(GregorianCalendar.DAY_OF_MONTH)>cal2.get(GregorianCalendar.DAY_OF_MONTH)){
					return 2;
				}else if(cal1.get(GregorianCalendar.DAY_OF_MONTH)==cal2.get(GregorianCalendar.DAY_OF_MONTH)){
					return 1;
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
	
}
