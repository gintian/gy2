package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 考勤对时间处理
 * kq_feast
 *      节假日
 * kq_restofweek
 *      公休日
 * */
//u
public class KQRestOper {
    public KQRestOper() {
    }

    /**
     * 判断公休日，
     * @param restlist 
     *        公休日的参数
     * @return list
     *          返回星期几（星期六，星期天）
     * */
    public static String getRestStr(String rest) {
        StringBuffer strRest = new StringBuffer();

        if (rest.indexOf("1") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[1] + ",");
        }
        if (rest.indexOf("2") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[2] + ",");
        }
        if (rest.indexOf("3") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[3] + ",");
        }
        if (rest.indexOf("4") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[4] + ",");
        }
        if (rest.indexOf("5") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[5] + ",");
        }
        if (rest.indexOf("6") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[6] + ",");
        }
        if (rest.indexOf("7") != -1) {
            strRest.append(KqUtilsClass.WEEKNAMES[0] + ",");
        }

        return strRest.toString();
    }

    public static String getRestStrTurn(String rest) {
        StringBuffer strRest = new StringBuffer();
        if (rest != null && rest.length() > 0) {
            rest = rest.toLowerCase();
        }
        try {
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[1]) != -1) {
                strRest.append("1,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[2]) != -1) {
                strRest.append("2,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[3]) != -1) {
                strRest.append("3,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[4]) != -1) {
                strRest.append("4,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[5]) != -1) {
                strRest.append("5,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[6]) != -1) {
                strRest.append("6,");
            }
            if (rest.indexOf(KqUtilsClass.WEEKNAMES[0]) != -1) {
                strRest.append("7,");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strRest.toString();
    }

    /**
     *  判断是否是公休日
     * @param reststr
     *         公休日
     * @param  cur_date 
     *         当前日期
     * @return  返回boolean 
     *         true:当前日期是公休日  
     *         false:不是公休日         
     * */
    public static boolean if_RestWeek(String reststr, String cur_date) {

        boolean isCorrect = false;

        Date date = DateUtils.getDate(cur_date, "yyyy.MM.dd");
        String EE = KqUtilsClass.getWeekName(date);

        if (reststr.indexOf(EE) != -1) {
            isCorrect = true;
        }

        return isCorrect;
    }

    /**
     * 得到考勤业务编号的sql
     * */
    public static String kq_CodeItemSQL() {
        StringBuffer sql = new StringBuffer();
        sql.append("select codeitemid from codeitem where ");
        sql.append(" codesetid=? and parentid=? ");
        sql.append(" and codeitemid<>?");
        return sql.toString();
    }

    //开始时间 小于等于考勤期间结束；结束时间大于等于考勤期间开始
    //加班的
    public static String kq_Q11_dateSQL(String start_date, String end_date) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append(" and Q11Z3>=" + Sql_switcher.dateValue(start_date));
        selectSQL.append(" and Q11Z1<=" + Sql_switcher.dateValue(end_date) + " ");
        return selectSQL.toString();
    }

    //	请假的
    public static String kq_Q15_dateSQL(String start_date, String end_date) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append(" and Q15Z3>=" + Sql_switcher.dateValue(start_date));
        selectSQL.append(" and Q15Z1<=" + Sql_switcher.dateValue(end_date) + " ");
        return selectSQL.toString();
    }

    //	公出的
    public static String kq_Q13_dateSQL(String start_date, String end_date) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append(" and Q13Z3>=" + Sql_switcher.dateValue(start_date));
        selectSQL.append(" and Q13Z1<=" + Sql_switcher.dateValue(end_date) + " ");
        return selectSQL.toString();
    }

    public static String kq_Q17_dateSQL(String cur_date) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append(" and Q17Z1<=" + Sql_switcher.dateValue(cur_date));
        selectSQL.append(" and Q17Z3>=" + Sql_switcher.dateValue(cur_date) + " ");
        return selectSQL.toString();
    }

    //通过考勤项目里给定的计量单位,计算
    public static float getUnit(String item_unit, float work_time, float rest_time_diff) {
        float unit = 0;
        if ("01".equals(item_unit))//小时
        {
            unit = rest_time_diff;
        } else if ("02".equals(item_unit))//天
        {
            unit = rest_time_diff;
            if (unit >= work_time) {
                unit = 1;
            } else {
                unit = unit / work_time;
            }
        } else if ("04".equals(item_unit))//次
        {
            unit = 1;
        } else if ("03".equals(item_unit))//分钟
        {

            unit = rest_time_diff * 60;

        }

        return unit;
    }

    public static String getEE(String cur_date) {

        Date date = DateUtils.getDate(cur_date, "yyyy.mm.dd");
        String EE = KqUtilsClass.getWeekName(date);

        return EE;
    }

    public static String getLXtype(ArrayList columnlist, String fielditemid) {
        String lxtype = "";
        for (int r = 0; r < columnlist.size(); r++) {
            FieldItem fielditem = (FieldItem) columnlist.get(r);
            if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                if (fielditem.getDecimalwidth() > 0) {
                    lxtype = "float";
                    break;
                } else {
                    lxtype = "int";
                    break;
                }
            }
        }
        return lxtype;
    }

    public static float con_Unit(String item_unit, float l_unit, float work_time) {
        float unit = 0;
        if ("01".equals(item_unit))//小时
        {
            unit = l_unit;
        } else if ("02".equals(item_unit))//天
        {
            if (l_unit >= work_time) {
                unit = 1;
            } else {
                unit = l_unit / work_time;
            }
        } else if ("04".equals(item_unit))//次
        {
            unit = 1;
        } else if ("03".equals(item_unit))//分钟
        {
            unit = l_unit * 60;

        }
        return unit;
    }

    /**********适用于加班********/
    /**
     * @param op_date,当前天
     * @param kq_Strart_time 开始时间
     * @param kq_end_time 结束时间
     * @param tiemlist 正常时间段
     * @param flag 标记,0.开始时间,1结束时间
     * @throws GeneralException 
     */
    public static float getTime_StartDiff(String op_date, Date kq_Strart_time, Date kq_end_time, ArrayList tiemlist, String flag) throws GeneralException {

        float time = 0;
        for (int r = 0; r < tiemlist.size(); r++) {

            String[] time_slice = getTimeSlice(tiemlist.get(r).toString());
            String cur_sb_Time_str = time_slice[0];
            String cur_xb_Time_str = time_slice[1];

            Date cur_sb_Time = DateUtils.getDate(op_date + " " + cur_sb_Time_str, "yyyy-MM-dd HH:mm");
            Date cur_xb_Time = DateUtils.getDate(op_date + " " + cur_xb_Time_str, "yyyy-MM-dd HH:mm");
            float sb = toHourFormMinute(kq_Strart_time, cur_sb_Time);
            float xb = toHourFormMinute(kq_Strart_time, cur_xb_Time);
            float end_sb = toHourFormMinute(kq_end_time, cur_sb_Time);
            float end_xb = toHourFormMinute(kq_end_time, cur_xb_Time);
            if (sb <= 0 && xb >= 0 && end_sb <= 0 && end_xb >= 0)//开始时间和结束时间在一个时间段内
            {
                time = toHourFormMinute(kq_Strart_time, kq_end_time);
                break;
            }
            if (r == 0) {
                if (sb >= 0 && xb >= 0 && end_sb <= 0)//开始时间在上班时间前的情况
                {
                    time = time + toHourFormMinute(kq_Strart_time, cur_sb_Time);
                } else if (sb >= 0 && xb >= 0 && end_sb >= 0 && end_xb >= 0) {
                    //开始时间和结束时间都在上班时间前
                    if ("1".equals(flag)) {
                        Date zero = DateUtils.getDate(op_date + " " + "00:00", "yyyy-MM-dd HH:mm");
                        time = time + toHourFormMinute(zero, kq_end_time);
                    } else {
                        time = time + toHourFormMinute(kq_Strart_time, kq_end_time);
                    }

                    break;
                }
            }
            if (sb <= 0 && xb >= 0 && end_sb <= 0 && end_xb <= 0)//只有开始时间段在区间内
            {
                if ("0".equals(flag)) {
                    Date zero = DateUtils.getDate(op_date + " " + "24:00", "yyyy-MM-dd HH:mm");
                    time = toHourFormMinute(kq_Strart_time, zero);
                } else {
                    time = time + toHourFormMinute(kq_Strart_time, cur_xb_Time);
                    if (r == tiemlist.size() - 1) {
                        time = time + toHourFormMinute(cur_xb_Time, kq_end_time);
                        break;
                    }
                }

                continue;
            }
            if (sb >= 0 && xb >= 0 && end_sb <= 0 && end_xb <= 0)//都不在时间段内,在两头
            {
                time = time + toHourFormMinute(cur_sb_Time, cur_xb_Time);
                if (r == tiemlist.size() - 1) {
                    time = time + toHourFormMinute(cur_xb_Time, kq_end_time);
                    break;
                }
                continue;
            }

            if (sb >= 0 && xb >= 0 && end_sb <= 0 && end_xb >= 0)//只有结束时间在时间段内
            {
                time = time + toHourFormMinute(cur_sb_Time, kq_end_time);
                break;
            }
            if (r == tiemlist.size() - 1) {
                //开始时间和结束时间都在下班时间后
                if (sb <= 0 && xb <= 0 && end_sb <= 0 && end_xb <= 0) {
                    if ("0".equals(flag)) {
                        Date zero = DateUtils.getDate(op_date + " " + "24:00", "yyyy-MM-dd HH:mm");
                        time = toHourFormMinute(kq_Strart_time, zero);
                    } else {
                        time = toHourFormMinute(kq_Strart_time, kq_end_time);
                    }

                    break;
                }
            }
        }
        if (time < 0) {
            time = time * -1;
        }
        return time;
    }

    /********适用于请假
     * @throws GeneralException ********/

    public static float getTime_StartDiffQ15(String op_date, Date kq_Strart_time, Date kq_end_time, ArrayList tiemlist) throws GeneralException {
        //.out.println(kq_Strart_time+"----f--->"+kq_end_time+op_date);
        float time = 0;
        for (int r = 0; r < tiemlist.size(); r++) {

            String[] time_slice = getTimeSlice(tiemlist.get(r).toString());
            String cur_sb_Time_str = time_slice[0];
            String cur_xb_Time_str = time_slice[1];

            Date cur_sb_Time = DateUtils.getDate(op_date + " " + cur_sb_Time_str, "yyyy-MM-dd HH:mm");
            Date cur_xb_Time = DateUtils.getDate(op_date + " " + cur_xb_Time_str, "yyyy-MM-dd HH:mm");
            float sb = toHourFormMinute(kq_Strart_time, cur_sb_Time);
            float xb = toHourFormMinute(kq_Strart_time, cur_xb_Time);
            float end_sb = toHourFormMinute(kq_end_time, cur_sb_Time);
            float end_xb = toHourFormMinute(kq_end_time, cur_xb_Time);
            if (sb <= 0 && xb >= 0 && end_sb <= 0 && end_xb >= 0)//开始时间和结束时间在一个时间段内
            {
                time = time + toHourFormMinute(kq_Strart_time, kq_end_time);
                break;
            }
            if (sb >= 0 && xb >= 0 && end_sb <= 0 && end_xb >= 0)//结束时间在区间内
            {
                time = time + toHourFormMinute(cur_sb_Time, kq_end_time);
                break;
            }
            if (sb >= 0 && xb >= 0 && end_sb <= 0 && end_xb <= 0) {
                time = time + toHourFormMinute(cur_sb_Time, cur_xb_Time);//开始时间和结束时间在时间段的两头            
                continue;
            }
            if (sb <= 0 && xb >= 0 && end_sb <= 0 && end_xb <= 0)//只有开始时间在时间段内
            {
                time = time + toHourFormMinute(kq_Strart_time, cur_xb_Time);
                continue;
            }
            if (sb >= 0 && xb >= 0 && end_sb >= 0 && end_xb >= 0) {
                continue;
            }
        }

        return time;
    }

    public static float getTime_OverTimeDiff(String op_date, Date kq_Strart_time, Date kq_end_time) throws GeneralException {

        float time = toHourFormMinute(kq_Strart_time, kq_end_time);
        return time;
    }

    public static String getOrgTerm(String code, String kind) {
        String termsql = "";
        if ("1".equals(kind)) {
            termsql = " and e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            termsql = " and e01a1 like '" + code + "%'";
        } else {
            termsql = " and b0110 like '" + code + "%'";
        }
        return termsql;
    }

    public static String getOrgTerm(String kind) {
        String termsql = "";
        if ("1".equals(kind)) {
            termsql = " and e0122 like ? ";
        } else if ("0".equals(kind)) {
            termsql = " and e01a1 like ? ";
        } else {
            termsql = " and b0110 like ? ";
        }
        return termsql;
    }

    public static String selcet_kq_emp(String userbase, String date, String code, String kind, String whereIN) {
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select distinct a0100 from Q03");
        sqlstr.append(" where nbase='" + userbase + "'");
        sqlstr.append(" and Q03Z0 like '");
        sqlstr.append(date);
        sqlstr.append("%'");
        if ("1".equals(kind)) {
            sqlstr.append(" and e0122 like '");
        } else if ("0".equals(kind)) {
            sqlstr.append(" and e01a1 like '");
        } else {
            sqlstr.append(" and b0110 like '");
        }
        sqlstr.append(code);
        sqlstr.append("%'");
        sqlstr.append(" and a0100 in(select a0100 " + whereIN + ")");
        return sqlstr.toString();
    }

    /**
     * 得到一天工作时间
     * @param sb_time
     *              上班时间
     * @param xb_time 
     *              下班时间
     * @return 
     *          返回一天多少个小时
     * @throws GeneralException 
     */
    public static float getWork_Time(ArrayList timelist) throws GeneralException {
        float work_tiem = 0;
        for (int i = 0; i < timelist.size(); i++) {
            String whours = timelist.get(i).toString();
            String[] one_time = whours.split("~");
            String sb_time = one_time[0];
            String xb_time = one_time[1];
            Date sb_T = DateUtils.getDate(sb_time, "HH:mm");
            Date xb_T = DateUtils.getDate(xb_time, "HH:mm");
            work_tiem = work_tiem + toHourFormMinute(sb_T, xb_T);
        }
        return work_tiem;
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     * @throws GeneralException 
     */
    public static float round(String v, int scale) throws GeneralException {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        try
		{
        	 BigDecimal b = new BigDecimal(v);
             BigDecimal one = new BigDecimal("1");
             return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
		}
		catch (Exception e)
		{     //szk抛格式异常
			  throw new GeneralException("数据格式异常,请输入正确的数字！");
		}
       
    }

    public static float toHourFormMinute(Date start_date, Date end_date) throws GeneralException {
        long unit = getPartMinute(start_date, end_date);
        float fl = unit;
        fl = fl / 60;
        fl = round(fl + "", 2);
        return fl;
    }

    public static long getPartMinute(Date start_date, Date end_date) {
        int sY = DateUtils.getYear(start_date);
        int sM = DateUtils.getMonth(start_date);
        int sD = DateUtils.getDay(start_date);
        int sH = DateUtils.getHour(start_date);
        int smm = DateUtils.getMinute(start_date);

        int eY = DateUtils.getYear(end_date);
        int eM = DateUtils.getMonth(end_date);
        int eD = DateUtils.getDay(end_date);
        int eH = DateUtils.getHour(end_date);
        int emm = DateUtils.getMinute(end_date);
        GregorianCalendar d1 = new GregorianCalendar(sY, sM - 1, sD, sH, smm, 00);
        GregorianCalendar d2 = new GregorianCalendar(eY, eM - 1, eD, eH, emm, 00);
        Date date1 = d1.getTime();
        Date date2 = d2.getTime();
        long l1 = date1.getTime();
        long l2 = date2.getTime();
        long part = (l2 - l1) / (60 * 1000L);
        return part;
    }

    public static ArrayList getOneWorkTiem(String kq_whours) {

        boolean isCorrect = true;
        int w = 0;
        int s = 0;
        ArrayList timelist = new ArrayList();
        while (isCorrect) {
            if (s == 0) {
                w = kq_whours.indexOf("|");
            } else {

                w = kq_whours.indexOf("|", s);
            }
            if (w == -1) {
                isCorrect = false;
            } else {
                timelist.add(kq_whours.substring(s, w));
                s = w + 1;
            }
        }
        if (kq_whours.substring(s) != null && kq_whours.substring(s).length() > 0) {
            timelist.add(kq_whours.substring(s));
        }
        return timelist;
    }

    public static String[] getTimeSlice(String timeslice) {
        String[] times = timeslice.split("~");
        return times;
    }

    /**
     * 通过开始结束时间得到一个包含CommonData的list
     * @param statr_date
     * @param end_date
     * @return
     */
    public ArrayList getDateList(String statr_date, String end_date) {
        ArrayList datelist = new ArrayList();
        statr_date = statr_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        Date d1 = DateUtils.getDate(statr_date, "yyyy.MM.dd");
        Date d2 = DateUtils.getDate(end_date, "yyyy.MM.dd");
        int spacedate = DateUtils.dayDiff(d1, d2);
        CommonData vo = null;
        String kq_day = "";
        for (int i = 0; i <= spacedate; i++) {
            kq_day = DateUtils.format(d1, "yyyy.MM.dd");
            d1 = DateUtils.addDays(d1, 1);
            vo = new CommonData();
            vo.setDataName(kq_day);
            vo.setDataValue(kq_day);
            datelist.add(vo);
        }
        return datelist;
    }
}
