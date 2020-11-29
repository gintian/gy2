package com.hjsj.hrms.businessobject.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassObject;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  排班业务申请的判断 
 * <p>Title:ValidateAppOper.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 30, 2007 8:58:21 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ValidateAppOper {
    private Connection conn;
    private UserView   userView;

    public ValidateAppOper() {

    }

    public ValidateAppOper(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 公休日判断
     * @param da1
     * @param da2
     * @param a0100
     * @param nbase
     * @return
     * @throws GeneralException
     */
    private String no_Rest_mess; //公休日加班返回的信息

    public String getNo_Rest_mess() {
        return no_Rest_mess;
    }

    public void setNo_Rest_mess(String no_Rest_mess) {
        this.no_Rest_mess = no_Rest_mess;
    }

    private String rest_Peacetime_mess=""; //公休日加班申请平加班的报错信息

    public String getRest_Peacetime_mess() {
        return rest_Peacetime_mess;
    }

    public void setRest_Peacetime_mess(String rest_Peacetime_mess) {
        this.rest_Peacetime_mess = rest_Peacetime_mess;
    }

    public boolean is_Rest(Date da1, Date da2, String a0100, String nbase) throws GeneralException {
        boolean ret = true;
        if("00:00".equalsIgnoreCase(OperateDate.dateToStr(da2, "HH:mm"))){
        	da2 = OperateDate.addDay(da2, -1);
        }
        int num = RegisterDate.diffDate(da1, da2);
        StringBuffer sql = null;
        String op_date_to = "";
        String class_id = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            for (int m = 0; m <= num; m++) {
                op_date_to = getDateByAfter(da1, m);
                sql = new StringBuffer();
                sql.append("select class_id from kq_employ_shift");
                sql.append(" where a0100='" + a0100 + "' and nbase='" + nbase + "'");
                sql.append(" and q03z0='" + op_date_to + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    class_id = rs.getString("class_id");
                }
                if (class_id != null && !"0".equals(class_id) && class_id.length() > 0) {
                    ret = false;
                    break;
                }
            }
            ArrayList rest_list = IfRestDate.search_RestOfWeek(this.userView.getUserOrgId(), this.userView, this.conn);
            if ((class_id != null && !"0".equals(class_id) && class_id.length() > 0) && rest_list != null && rest_list.size() > 0) {
                String strRest = rest_list.get(0).toString();
                Date date = DateUtils.getDate(op_date_to, "yyyy.MM.dd");

                String EE = KqUtilsClass.getWeekName(date);

                if (strRest.indexOf(EE) != -1) {
                    KqClassObject kqClassObject = new KqClassObject(this.conn);
                    String class_name = kqClassObject.getClassFiledValue(class_id, "name");
                    this.setNo_Rest_mess("您所在的" + class_name + "，公休日" + op_date_to + "有排班班次，无法申请公休日加班！");
                } else {
                    this.setNo_Rest_mess("您申请的日期" + op_date_to + "不是公休日！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return ret;
    }

    /**
     * 判断申请班次是不是跨天，如果有跨天，则看是不是最后一天跨出休息日
     * @param da1
     * @param da2
     * @param a0100
     * @param nbase
     * @param app_class_id
     * @return
     * @throws GeneralException
     */
    public boolean is_Rest(Date da1, Date da2, String a0100, String nbase, String app_class_id) throws GeneralException {
        boolean ret = true;
        int num = RegisterDate.diffDate(da1, da2);
        StringBuffer sql = null;
        String op_date_to = "";
        String class_id = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn);
        boolean isspan = false;
        if (app_class_id != null && app_class_id.length() > 0) {
            isspan = kqUtilsClass.isSpanForKqClass(app_class_id);
        }
        int m = 0;
        try {
        	// linbz 查询最后一天的上班时间
        	ArrayList rest_list = IfRestDate.search_RestOfWeek(this.userView.getUserOrgId(), this.userView, this.conn);
        	KqClassObject kqClassObject = new KqClassObject(this.conn);
        	//szk公休日只判断该天 ??
            for (m = 0; m <= num; m++) {
                op_date_to = getDateByAfter(da1, m);
                sql = new StringBuffer();
                sql.append("select class_id from kq_employ_shift");
                sql.append(" where a0100='" + a0100 + "' and nbase='" + nbase + "'");
                sql.append(" and q03z0='" + op_date_to + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    class_id = rs.getString("class_id");
                }
                boolean classidBool = (class_id != null && !"0".equals(class_id) && class_id.length() > 0);
                // 最后一天如果有班次 校验班次开始时间
                if(classidBool && m == num && isspan) {
                    String startTimeStr = kqUtilsClass.getStartTimeclassById(class_id);
                    Date classStartTime = OperateDate.strToDate(startTimeStr, "HH:mm");
                    Date endTime = null;
                    endTime = OperateDate.getDateByFormat(da2, "HH:mm");
                    ret = false;
                    if (endTime.after(classStartTime)) {
                    	this.setNo_Rest_mess("您申请的结束日期" + DateUtils.format(da2, "yyyy.MM.dd") + "包含当天班次内时间，无法申请公休日加班！");
                    }
                }
                else if (classidBool && rest_list != null && rest_list.size() > 0) {
                	String strRest = rest_list.get(0).toString();
                	Date date = DateUtils.getDate(op_date_to, "yyyy.MM.dd");
                	String EE = KqUtilsClass.getWeekName(date);
                	ret = false;
                	if (strRest.indexOf(EE) != -1) {
                		String class_name = kqClassObject.getClassFiledValue(class_id, "name");
                		this.setNo_Rest_mess("您所在的" + class_name + "，公休日" + op_date_to + "有排班班次，无法申请公休日加班！");
                	} else {
                		this.setNo_Rest_mess("您申请的日期" + op_date_to + "不是公休日！");
                	}
                	break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return ret;
    }

    /**
     * 判断是否是节假日
     * @param da1
     * @param da2
     * @param code
     * @param app_class_id
     * @return
     * @throws GeneralException
     */
    public boolean is_Feast(Date da1, Date da2, String code, String app_class_id) throws GeneralException {
        boolean ret = false;
        try {
            if (code == null || code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
                code = managePrivCode.getPrivOrgId();
            }
            String b0110 = "UN" + code;
            ArrayList restList = IfRestDate.search_RestOfWeek(b0110, this.userView, this.conn);
            String rest_b0110 = restList.get(1).toString();
            int num = RegisterDate.diffDate(da1, da2);
//            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn);
//            boolean isspan = false;
//            if (app_class_id != null && app_class_id.length() > 0)
//                isspan = kqUtilsClass.isSpanForKqClass(app_class_id);
//            if (isspan) {
//                String startTimeStr = kqUtilsClass.getStartTimeclassById(app_class_id);
//                Date classStartTime = OperateDate.strToDate(startTimeStr, "HH:mm");
//                Date endTime = null;
//                endTime = OperateDate.getDateByFormat(da2, "HH:mm");
//                if (endTime.before(classStartTime)) {
//                    if (num > 1) {
//                        num--;
//                    }
//                }
//            }
            //szk节假日只判断申请的第一天是否符合
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(da1, m);
                String feast_name = IfRestDate.if_Feast(op_date_to, this.conn);
                if (StringUtils.isNotEmpty(feast_name)) {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                    if (StringUtils.isEmpty(turn_date)) {
                        ret = true;
                    } else {
                        /*//防止节假日和公休日为一天,公休日和工作日倒休
                        String strRest=restList.get(0).toString();
                        Date date=DateUtils.getDate(op_date_to,"yyyy.MM.dd");
                         SimpleDateFormat format = new SimpleDateFormat("EEEE-yyyy-MM-dd"); 
                         String EE=format.format(date);
                         String EE_c=EE;
                         EE=EE.substring(0,3);  
                         EE=KQRestOper.getRestStrTurnEtoC(EE);		    	     	
                         if(strRest.indexOf(EE)!=-1)
                         {
                        	 ret= true;
                         continue;
                         }else
                         {
                        	Category.getInstance("com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper").error(
                        		"strRest=="+strRest+";##EE:=="+EE+";##EE_c="+EE_c);
                        	 ret= false;
                        	 break; 
                         }*/
                    	return false;
                    }
                } else {
                    /*String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date_to,this.conn);       	    	    	  
                    if(week_date!=null&&week_date.length()>0)
                    {
                      ret= true;
                      continue;
                    }else
                    {
                      return false;
                    }*/
                	
                	
                	return false;
                }
//                if (m == num && isspan && !ret) 
//                	return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            throw GeneralExceptionHandler.Handle(se);
        }
        return ret;
    }

    /**
     * 判断一时间段是否有节假日
     * @param usr,tab, star,endtime 
     *        
     * @return boolean
     *        返回真 和假
     * @throws GeneralException 
     * */
    public boolean is_Feast(Date da1, Date da2, String code) throws GeneralException {
        boolean ret = false;
        try {
            if (code == null || code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
                code = managePrivCode.getPrivOrgId();
            }
            String b0110 = "UN" + code;
            ArrayList restList = IfRestDate.search_RestOfWeek(b0110, this.userView, this.conn);
            String rest_b0110 = restList.get(1).toString();
            if("00:00".equalsIgnoreCase(OperateDate.dateToStr(da2, "HH:mm"))){
            	da2 = OperateDate.addDay(da2, -1);
            }
            int num = RegisterDate.diffDate(da1, da2);
            for (int m = 0; m <= num; m++) {
                String op_date_to = getDateByAfter(da1, m);
                String feast_name = IfRestDate.if_Feast(op_date_to, this.conn);
                if (feast_name != null && feast_name.length() > 0) {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, op_date_to, this.conn);
                    if ((turn_date == null || turn_date.length() <= 0)) {
                        ret = true;
                        continue;
                    } else {
                        //防止节假日和公休日为一天,公休日和工作日倒休
                        String strRest = restList.get(0).toString();
                        Date date = DateUtils.getDate(op_date_to, "yyyy.MM.dd");

                        String EE = KqUtilsClass.getWeekName(date);

                        if (strRest.indexOf(EE) != -1) {
                            ret = true;
                            continue;
                        } else {
                            return false;
                        }
                    }
                } else {
                    /*String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date_to,this.conn);       	    	    	  
                    if(week_date!=null&&week_date.length()>0)
                    {
                      ret= true;
                      continue;
                    }else
                    {
                      return false;
                    }*/
                    return false;
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
            throw GeneralExceptionHandler.Handle(se);
        }
        return ret;
    }

    /**
     * 校验平时加班申请是否包含公休日 
     * @Title: if_Peacetime   
     * @Description: 校验平时加班申请是否包含公休日  
     * @param @param da1 加班申请起始时间
     * @param @param da2 加班申请结束时间
     * @param @param nbase 人员库
     * @param @param a0100 人员编号
     * @return boolean true: 不包含公休日，false: 包含公休日    
     * @throws
     */
    public boolean if_Peacetime(Date da1, Date da2, String nbase, String a0100) {
        boolean ret = true;
        String appDate = "";
        String class_id = "";
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
        	int num = RegisterDate.diffDate(da1, da2);
        	//szk平时加班判断第一天
            for (int m = 0; m <= num; m++) {
                appDate = getDateByAfter(da1, m);

                sql.setLength(0);
                sql.append("select class_id from kq_employ_shift");
                sql.append(" where a0100='" + a0100 + "' and nbase='" + nbase + "'");
                sql.append(" and q03z0='" + appDate + "'");
                
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    class_id = rs.getString("class_id");
                    if (class_id == null || "0".equals(class_id) || "".equals(class_id)) {
                        ret = false;
                        //ret=is_Peacetime_include_RestTime(da1,da2,op_date_to); rest_kqclass此参数隐藏了，不走这个
                        setRest_Peacetime_mess("申请平时加班，申请时间段不应包含休息班次！");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return ret;
    }

    /**
       * 取
       * @param dateString，
       *         某年某月某天
       * @param  afterNum
       *         天数  
       * @return string
       *          返回相加后得到新的某年某月某天
       * */
    public static String getDateByAfter(Date date, int afterNum) throws GeneralException {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);

        return new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
    }

    /**
     * 是否判断申请加班类型与申请日期相符
     * @return
     */
    public boolean is_OVERTIME_TYPE() {
        return "1".equals(KqParam.getInstance().getOpinionOvertimeType());
    }

    /**
     * 是否是公休日
     * @param da1
     * @param a0100
     * @param nbase
     * @return
     * @throws GeneralException
     */
    public boolean is_Rest(Date da1, String a0100, String nbase, String b0110) throws GeneralException {
        boolean ret = true;
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.conn);
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        String cur_date = DateUtils.format(da1, "yyyy.MM.dd");
        if (IfRestDate.if_Rest(cur_date, userView, rest_date))//判断公休日
        {
            String turn_date = IfRestDate.getTurn_Date(rest_b0110, cur_date, conn);
            if (turn_date != null && turn_date.length() > 0) {
                ret = false;
            }
        } else {
            String g_rest_date = IfRestDate.getWeek_Date(rest_b0110, cur_date, conn);//公休是否倒休		    	
            if (g_rest_date == null || g_rest_date.length() <= 0)//有倒休日期，上班
            {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * 判断给定天是不是节假日
     * @param da1
     * @param code
     * @return
     * @throws GeneralException
     */
    public boolean is_Feast(Date da1) throws GeneralException {
        boolean ret = false;
        try {
            String op_date_to = getDateByAfter(da1, 0);
            String feast_name = IfRestDate.if_Feast(op_date_to, this.conn);
            if (feast_name != null && feast_name.length() > 0) {
                ret = true;
            } else {
                return false;
            }
        } catch (Exception se) {
            se.printStackTrace();
            throw GeneralExceptionHandler.Handle(se);
        }
        return ret;
    }

    /**
     * 得到中间有多少个休息日
     * @param da1
     * @param da2
     * @param a0100
     * @param nbase
     * @return
     * @throws GeneralException
     */
    public int is_RestDays(Date da1, Date da2, String a0100, String nbase) throws GeneralException {
        int num = RegisterDate.diffDate(da1, da2);
        StringBuffer sql = null;
        String op_date_to = "";
        String class_id = "";
        int days = 0;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            for (int m = 0; m <= num; m++) {
                op_date_to = getDateByAfter(da1, m);
                sql = new StringBuffer();
                sql.append("select class_id from kq_employ_shift");
                sql.append(" where a0100='" + a0100 + "' and nbase='" + nbase + "'");
                sql.append(" and q03z0='" + op_date_to + "'");
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    class_id = rs.getString("class_id");
                }
                if (class_id == null || "0".equals(class_id) || class_id.length() <= 0) {
                    days++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return days;
    }
    
    /**
     * @Title: checkHoliday   
     * @Description: 检查假期管理假类是否超限（年假等）   
     * @param @param nbase 人员库
     * @param @param a0100 人员编号
     * @param @param a0101 姓名
     * @param @param b0110 单位编码
     * @param @param holidayId 假期类型编码
     * @param @param startDate 申请开始时间
     * @param @param endDate   申请结束时间
     * @param @param oper 申请操作方式（新增，修改）
     * @param @return 返回校验不通过信息，如校验通过，则信息为空
     * @param @throws GeneralException 
     * @return String    
     * @throws
     */
    private String checkHoliday(String nbase, String a0100, String a0101, String b0110, 
            String holidayId, Date startDateTime, Date endDateTime, String oper, String q1501) throws GeneralException {
        
        if (!KqParam.getInstance().isHoliday(this.conn, userView, holidayId)) {
            return "";
        }
        
        AnnualApply annual = new AnnualApply(this.userView, this.conn);
        float[] holiday_rules = annual.getHoliday_minus_rule();// 年假假期规则
        
        HashMap kqItem_hash = annual.count_Leave(holidayId);
        kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
        
        String start = OperateDate.dateToStr(startDateTime, "yyyy.MM.dd HH:mm:ss");
        String end = OperateDate.dateToStr(endDateTime, "yyyy.MM.dd HH:mm:ss");
        
        
        //剩余可休天数
        float usableDays = annual.getMy_Time(holidayId, a0100, nbase, start, end, b0110, kqItem_hash);
        if (usableDays <= 0) {
            return a0101 + "，该假期可休天数已用完，无法继续申请！";
        }
        usableDays = annual.roundNumByItemDecimalWidth(kqItem_hash, usableDays);
        
        //本次申请的天数
        float appDays = annual.getHistoryLeaveTime(startDateTime, endDateTime, a0100, nbase, b0110, kqItem_hash, holiday_rules);
        appDays = annual.roundNumByItemDecimalWidth(kqItem_hash, appDays);
        
        //取完整的假期扣减天数，主要目的是校验，不是为了取值
        annual.getLeaveManage(a0100, nbase, holidayId, start, end, appDays, "", b0110, kqItem_hash, holiday_rules);
        
        //已申请还未批准的天数
        float unApprovalDays = annual.othenSealTime(holidayId, startDateTime, endDateTime, a0100, nbase, b0110, q1501, kqItem_hash, oper, "", holiday_rules);
        unApprovalDays = annual.roundNumByItemDecimalWidth(kqItem_hash, unApprovalDays);
        
        
        //天数差=可休天数-本次申请天数-未批准天数
        float diffDays = usableDays - appDays - unApprovalDays;
        
        //天数差小于0，说明可休天数不够用
        if (diffDays < 0) {
            String msg = a0101 + "，可休天数不足，请调整申请时间！"
            		   + "本次申请天数为" + appDays + "天，";
            
            if (unApprovalDays > 0) {
                msg = msg + "已申请但尚未批准的天数为" + unApprovalDays + "天，";
            }
            
            msg = msg + "当前剩余可休天数为" + usableDays + "天！";
            
            return  msg;
        }
        
        return "";
    }
    
    /**
     * @Title: checkHoliday   
     * @Description: 检查新增加的假期管理假类申请是否超限（年假等）   
     * @param @param startDate 申请开始时间
     * @param @param endDate   申请结束时间
     * @param @param infoMap   申请人信息
     * @param @param holidayId 假期类型编码
     * @param @param nbase     申请人人员库
     * @param @return 返回校验不通过信息，如校验通过，则信息为空
     * @param @throws GeneralException 
     * @return String    
     * @throws
     */
    public String checkHoliday(Date startDate, Date endDate, Map infoMap, String holidayId, String nbase) 
        throws GeneralException {
        String a0100 = (String) infoMap.get("a0100");
        String a0101 = (String) infoMap.get("a0101");
        String b0110 = (String) infoMap.get("b0110");
        return checkHoliday(nbase, a0100, a0101, b0110, holidayId, startDate, endDate, "add", "");
    }
    
    /**
     * @Title: checkHoliday   
     * @Description: 检查新增加的假期管理假类申请是否超限（年假等）   
     * @param @param startDate 申请开始时间
     * @param @param endDate   申请结束时间
     * @param @param infoMap   申请人信息
     * @param @param holidayId 假期类型编码
     * @param @param nbase     申请人人员库
     * @param @return 返回校验不通过信息，如校验通过，则信息为空
     * @param @throws GeneralException 
     * @return String    
     * @throws
     */
    public String checkHoliday(Date startDate, Date endDate, RecordVo vo, String holidayId, String oper) 
        throws GeneralException {
        String nbase = (String) vo.getString("nbase");
        String a0100 = (String) vo.getString("a0100");
        String a0101 = (String) vo.getString("a0101");
        String b0110 = (String) vo.getString("b0110");
        String q1501 = (String) vo.getString("q1501");
        return checkHoliday(nbase, a0100, a0101, b0110, holidayId, startDate, endDate, oper, q1501);
    }
}
