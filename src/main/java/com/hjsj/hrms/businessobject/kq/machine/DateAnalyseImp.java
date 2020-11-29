package com.hjsj.hrms.businessobject.kq.machine;

public interface DateAnalyseImp {

	  //  考勤规则大类代码
	  public static String kqItem_Leave    = "0";       // 请假
	  public static String kqItem_Overtime = "1";       // 加班
	  public static String kqItem_Work     = "2";       // 出勤
	  public static String kqItem_Away     = "3";       // 公出

	  public static String kqItem_LAffair   = "01";     // 事假
	  public static String kqItem_LSick     = "02";     // 病假
	  public static String kqItem_LAnnual   = "06";     // 年假
	  public static String kqItem_ONDUTY    = "27";     // 应出勤
	  public static String kqItem_FACTDUTY  = "28";     // 实出勤

	  public static String kqItem_ORest    = "10";      // 公休日加班
	  public static String kqItem_OFeast   = "11";      // 节假日加班
	  public static String kqItem_OGeneral = "12";      // 平时加班

	  public static String kqItem_WLate    = "21";      //迟到
	  public static String kqItem_WEarly   = "23";      //早退
	  public static String kqItem_WAbsent  = "25";      //旷工
	  public static String kqItem_Night    = "26";      //夜班
	  public static String KqItem_LEAVETIME= "29";      //离岗时间
	  public static String kqType_hand="01";            //手工考勤
	  public static String KqType_Machine="02";         //机器考勤
	  public static String kqType_Nokq="03";            //不参加考勤
	  public static String kqType_Leavekq="04";            //不参加考勤
	  
	  public static String unit_HOUR=  "01";
	  public static String unit_DAY    ="02";
	  public static String unit_ONCE  ="04";
	  public static String unit_MINUTE  ="03";
	  public static String dkHoliday  ="3";
	  public static String[] KqItem_Overtime={"10","11","12"};
}
