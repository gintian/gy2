package com.hjsj.hrms.module.kq.interfaces;

import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.util.KqItem;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 新考勤申请时长计算类
 * @author jimjim
 *
 */
public class KqAppCaculator {
    /* 排班情况分类*/
    private enum ShiftType {
        //不排班 固定班制 排班制
        NO,  FIXED, SHIFT
    }
    
    // 工作日类型
    private enum DateType {
        //工作日
        WORK,
        //公休日
        REST,
        //节假日
        FEAST
    }

    private Category logger = Category.getInstance(this.getClass());
    private Connection conn;
    
    // 班组信息
    private String groupId = "";
    private String shiftData = "";
    private String restType = "";
    
    // 日期类型列表
    private HashMap<String, DateType> datesType = new HashMap<String, DateType>();
    // 排班
    private HashMap<String, ArrayList<Integer>> shiftsHm = new HashMap<String, ArrayList<Integer>>();
    // 班次
    private HashMap<Integer, HashMap<String, String>> classesHm = new HashMap<Integer, HashMap<String,String>>();
    // 默认的休息班次（供使用排班但有没有排班时使用）
    ArrayList<Integer> defaultClassList = new ArrayList<Integer>();
    
    private KqAppCaculator() {
        
    }
     
    public KqAppCaculator (Connection conn) {
        this.conn = conn;
    }
    
    /**
     * 计算申请时长
     * @param appInfo 申请信息：type,nbase,a0100,starttime,endtime
     * @return 时长
     */
    public double calcAppTimeLen(LazyDynaBean appInfo) {
        double timeLen = 0;
        
        String appTypeId = (String) appInfo.get("type");
        String nbase = (String) appInfo.get("nbase");
        String a0100 = (String) appInfo.get("a0100");
        
        if(!(appInfo.get("starttime") instanceof Date) || !(appInfo.get("endtime") instanceof Date)){
            return timeLen;
        }
        
        Date startTime = (Date) appInfo.get("starttime");
        Date endTime = (Date) appInfo.get("endtime");
        
        // 自定义考勤申请计算规则类
        String appCaculateClass = SystemConfig.getPropertyValue("kq_app_caculate_class");
        if (StringUtils.isNotBlank(appCaculateClass)) {
            try {
                IKqAppCaculate customeCaculate = (IKqAppCaculate)Class.forName(appCaculateClass).newInstance();
                timeLen = customeCaculate.calcAppTimeLen(nbase, a0100, appTypeId, startTime, endTime);
            } catch(ClassNotFoundException e) {
                logger.error("考勤申请计算异常：没有找到system中自定义的计算类" + appCaculateClass);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("考勤申请计算异常：执行system中自定义的计算类" + appCaculateClass + "出错！");
            }
        } else { 
            // 以下位产品标准计算规则
            this.loadDatesType(startTime, endTime);
            
            if (appTypeId.startsWith("0") || appTypeId.startsWith("3")) {
                // 计算请假、公出时长
                timeLen = calcLeaveTimeLen(nbase, a0100, appTypeId, startTime, endTime);
            } else {
                // 计算加班时长
                timeLen = calcOvertimeTimeLen(nbase, a0100, appTypeId, startTime, endTime);
            }
        }
        
        return timeLen;
    }
    
    /**
     * 计算请假、公出时长
     * @param nbase 申请人人员库前缀
     * @param a0100 申请人id
     * @param appTypeId 申请类型
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @return 时长（单位：考勤规则指定的单位）
     */
    private double calcLeaveTimeLen(String nbase, String a0100, String appTypeId, Date startTime, Date endTime) {
        double timeLen = 0;
        
        KqItem kqItem = new KqItem(this.conn);
        try {
            HashMap<String, String> kqItemHm = kqItem.getKqItem(appTypeId); 
            
            ShiftType st = getShiftType(nbase, a0100, startTime, endTime);
            if (st == ShiftType.NO) {
                // 不使用排班
                //timeLen = calcTimeLenOnNoShift(startTime, endTime, kqItemHm);
                // 不适用排班的，模拟固定排班的方式
                loadFixedShifts(startTime, endTime, st);
            } else {
                if (st == ShiftType.FIXED) {
                    // 加载固定排班
                    loadFixedShifts(startTime, endTime, st);
                } else {
                    // 加载排班
                    loadShifts(startTime, endTime, nbase, a0100);
                }
                
            }
            timeLen = calcTimeLenByShift(startTime, endTime, kqItemHm);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        
        return timeLen;
    }
    
    private double calcTimeLenByShift(Date startTime, Date endTime, HashMap<String, String> kqItemHm) {
        double timeLen = 0;
        
        AnnualApply annualApply = new AnnualApply();
        
        String kqItemId = kqItemHm.get("item_id");
        String targetUnit = kqItemHm.get("item_unit");
        String hasRest = kqItemHm.get("has_rest");
        String hasFeast = kqItemHm.get("has_feast");
        
        int dayNum = RegisterDate.diffDate(startTime, endTime) + 1;
        float timeLenMinute = 0;
        float baseMinute = 480;
        Date curDateStart = null;
        Date curDateEnd = null;
        ArrayList<Integer> classList = null;
        boolean hasShift = false;
        for (int i = 0; i < dayNum; i++) {
            curDateStart = DateUtils.addDays(startTime, i);
            String curDateStr = DateUtils.format(curDateStart, "yyyy-MM-dd");
            
            DateType curDateType = this.datesType.get(curDateStr);
            
            classList = this.shiftsHm.get(curDateStr);
            
            // 判断当天是否有排班
            hasShift = false;
            if (classList != null) {
                for (int j = 0; j < classList.size(); j++) {
                    if (classList.get(j) > 0) {
                        hasShift = true;
                        break;
                    }
                }
            } else {
                classList = this.defaultClassList;
            }
            
            // 请假、公出需要判断是否包含公休日、节假日
            if (kqItemId.startsWith("0") || kqItemId.startsWith("3")) {
                // 不包含公休日(没排班或排了休息班算公休）
                if ("0".equals(hasRest) && !hasShift) {
                    continue;
                } 
                
                // 不包含节假日
                if ("0".equals(hasRest) && DateType.FEAST == curDateType) {
                    continue;
                }
            }
            
//            if (i != 0) {
//                curDateStart = DateUtils.getDate(curDateStr, "yyyy-MM-dd");
//            }
//            
//            if (i < dayNum - 1) {
//                curDateEnd = DateUtils.getDate(curDateStr + " 23:59", "yyyy-MM-dd HH:mm");
//            } else {
//                curDateEnd = endTime;
//            }
            
            //timeLenMinute = KQRestOper.getPartMinute(curDateStart, curDateStart);
            
            HashMap timeHm = null;
            float curDateAppTimeLen = 0;
            float curDateClassTimeLenSum = 0;
            for (int j = 0; j < classList.size(); j++) {
                if (classList.get(j) >= 0) {
                    // 一天有多个班次，只要有非休息班次，那么休息班次本身不需要计算
                    if (hasShift && classList.get(j) == 0) {
                        continue;
                    }
                    
                    HashMap<String, String> classmMap = this.classesHm.get(classList.get(j));
                    if (classmMap == null) {
                        continue;
                    }
                    
                    HashMap timeLenHm = annualApply.getCurDateTime(classmMap, curDateStart, startTime, endTime);
                    
                    Float fTimeLen = (Float) timeLenHm.get("timeLen");
                    curDateAppTimeLen += fTimeLen.floatValue();
                    //Float fTimeSum = (Float) timeLenHm.get("time_sum");
                    curDateClassTimeLenSum += Float.parseFloat(classmMap.get("work_hours"));
                }
            }            
            
            if (curDateAppTimeLen > 0.01) {
                double timeLenDay = annualApply.tranMinuteValueByUnit(curDateAppTimeLen, targetUnit, curDateClassTimeLenSum);
                if (KqConstant.Unit.DAY.equals(targetUnit)) {
                    timeLenDay = (float) ((timeLenDay <= 0.5) ? 0.5 : 1);
                }
                timeLen += timeLenDay;
            }
        }
        
        return timeLen;
    }

    private void loadShifts(Date startTime, Date endTime, String nbase, String a0100) {
        this.shiftsHm.clear();
        //
        ContentDAO dao = new ContentDAO(this.conn);
        
        String st = DateUtils.format(startTime, "yyyy.MM.dd");
        String ed = DateUtils.format(endTime, "yyyy.MM.dd");
        
        StringBuffer sql = new StringBuffer();
        sql.append("select q03z0,").append(Sql_switcher.isnull("Class_id_1", "0")).append(" class_id_1");
        sql.append(",").append(Sql_switcher.isnull("Class_id_2", "0")).append(" class_id_2");
        sql.append(",").append(Sql_switcher.isnull("Class_id_3", "0")).append(" class_id_3");
        sql.append(" from kq_employ_shift_v2");
        sql.append(" where guidkey=(select guidkey from ").append(nbase).append("A01").append(" where a0100=?)");
        sql.append(" and q03z0>=").append(Sql_switcher.dateValue(st));
        sql.append(" and q03z0<=").append(Sql_switcher.dateValue(ed));
        
        ArrayList<String> sqlParams = new ArrayList<String>();
        sqlParams.add(a0100);
        
        String q03z0 = "";
        
        Integer classId1 = -1;
        Integer classId2 = -1;
        Integer classId3 = -1;
        StringBuffer useClasses = new StringBuffer("0");
        
        // 从排班表中取排班记录
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), sqlParams);
            while (rs.next()) {
               // if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    q03z0 = DateUtils.FormatDate(rs.getDate("q03z0"), "yyyy-MM-dd");
                //} else {
                //    q03z0 = rs.getString("q03z0");
                //}
                
                classId1 = rs.getInt("class_id_1");
                classId2 = rs.getInt("class_id_2");
                classId3 = rs.getInt("class_id_3");
                
                ArrayList<Integer> classList = new ArrayList<Integer>();
                classList.add(classId1);
                classList.add(classId2);
                classList.add(classId3);
                
                this.shiftsHm.put(q03z0, classList);
                
                if (!classInUseClasses(classId1+"", useClasses.toString())) {
                    useClasses.append(",").append(classId1); 
                }
                
                if (!classInUseClasses(classId2+"", useClasses.toString())) {
                    useClasses.append(",").append(classId2); 
                }
                
                if (!classInUseClasses(classId3+"", useClasses.toString())) {
                    useClasses.append(",").append(classId3); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        loadClasses(useClasses.toString());
    }
    
    private boolean classInUseClasses(String classId, String useClasses) {
        return ("," + useClasses.toString() + ",").contains("," + classId + ",");
    }

    /**加载计算用到的班次信息
     * @param useClasses 班次id串，格式：1,2,3
     */
    private void loadClasses(String useClasses) {
        // 从班次表中获取本次需要的班次信息
        initClassInfo();
        
        ContentDAO dao = new ContentDAO(this.conn);
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * from kq_class");
        sql.append(" where class_id in (").append(useClasses).append(")");
        
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                HashMap<String, String> aClassMap = new HashMap<String, String>();
                aClassMap.put("class_id", rs.getInt("class_id")+"");
                aClassMap.put("name", rs.getString("name"));
                aClassMap.put("onduty_1", rs.getString("onduty_1"));
                aClassMap.put("offduty_1", rs.getString("offduty_1"));
                aClassMap.put("onduty_2", rs.getString("onduty_2"));
                aClassMap.put("offduty_2", rs.getString("offduty_2"));
                aClassMap.put("onduty_3", rs.getString("onduty_3"));
                aClassMap.put("offduty_3", rs.getString("offduty_3"));
                aClassMap.put("work_hours", rs.getFloat("work_hours")+"");
                
                this.classesHm.put(rs.getInt("class_id"), aClassMap);                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }
    
    private void initClassInfo() {
        this.classesHm.clear();
        HashMap<String, String> aClassMap = new HashMap<String, String>();
        aClassMap.put("class_id", "0");
        aClassMap.put("name", "休息");
        aClassMap.put("onduty_1", "");
        aClassMap.put("offduty_1", "");
        aClassMap.put("onduty_2", "");
        aClassMap.put("offduty_2", "");
        aClassMap.put("onduty_3", "");
        aClassMap.put("offduty_3", "");
        aClassMap.put("work_hours", "");
        
        this.classesHm.put(0, aClassMap);
        
        this.defaultClassList.clear();
        this.defaultClassList.add(0);
    }

    private void loadFixedShifts(Date startTime, Date endTime, ShiftType st) {
        this.shiftsHm.clear();
        
        String[] shifts = this.shiftData.split(";");
        
        StringBuffer useClasses = new StringBuffer("0");
        
        int dayNum = RegisterDate.diffDate(startTime, endTime) + 1;
        float timeLenMinute = 0;
        float baseMinute = 480;
        Date curDateStart = null;
        Date curDateEnd = null;
        for (int i = 0; i < dayNum; i++) {
            curDateStart = DateUtils.addDays(startTime, i);
            String curDateStr = DateUtils.format(curDateStart, "yyyy-MM-dd");
            
            DateType curDateType = this.datesType.get(curDateStr);
            
            String curDateClasse = "";
            // 非班组人员（无排班）
            if (ShiftType.NO == st) {
               // 公休日、节假日按休息处理
                if (DateType.FEAST == curDateType || DateType.REST == curDateType) {
                    continue;
                }
                
                curDateClasse = this.shiftData;
            } else {
                // 固定班制
                //法定节假日及公休日自动排休），公休日、节假日按休息处理
                if (ShiftType.FIXED == st && "1".equalsIgnoreCase(this.restType)) {
                    if (DateType.FEAST == curDateType || DateType.REST == curDateType) {
                        continue;
                    }
                }
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(curDateStart);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                
                if (dayOfWeek == 1) {
                    dayOfWeek = 6;
                } else {
                    dayOfWeek = dayOfWeek - 2;
                }
                
                if (shifts.length > dayOfWeek) {
                    curDateClasse = shifts[dayOfWeek];
                }
            }
            
            if (StringUtils.isBlank(curDateClasse)) {
                continue;
            }
            
            String[] classes = curDateClasse.split(",");
            
            ArrayList<Integer> classList = new ArrayList<Integer>();
            Integer classId1 = -1;
            Integer classId2 = -1;
            Integer classId3 = -1;
            
            if (classes.length > 0) {
                classId1 = Integer.parseInt(classes[0]);
            }
            if (classes.length > 1) {
                classId2 = Integer.parseInt(classes[1]);
            }
            if (classes.length > 2) {
                classId3 = Integer.parseInt(classes[2]);
            }
            
            classList.clear();
            classList.add(classId1);
            classList.add(classId2);
            classList.add(classId3);
            
            this.shiftsHm.put(curDateStr, classList);
            
            if (!classInUseClasses(classId1+"", useClasses.toString())) {
                useClasses.append(",").append(classId1); 
            }
            
            if (!classInUseClasses(classId2+"", useClasses.toString())) {
                useClasses.append(",").append(classId2); 
            }
            
            if (!classInUseClasses(classId3+"", useClasses.toString())) {
                useClasses.append(",").append(classId3); 
            }
        }
        
        loadClasses(useClasses.toString());
    }

    /**
     * 计算加班时长
     * @param nbase 申请人人员库前缀
     * @param a0100 申请人id
     * @param appTypeId 申请类型
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @return 时长（单位：考勤规则指定的单位）
     */
    private double calcOvertimeTimeLen(String nbase, String a0100, String appTypeId, Date startTime, Date endTime) {
        double timeLen = 0;
        
        KqItem kqItem = new KqItem(this.conn);
        try {
            HashMap<String, String> kqItemHm = kqItem.getKqItem(appTypeId); 
            // 加班暂时不考虑排班
            timeLen = calcTimeLenOnNoShift(startTime, endTime, kqItemHm);
            
//            ShiftType st = getShiftType(nbase, a0100, startTime, endTime);
//            if (st == ShiftType.NO) {
//                // 不使用排班
//                timeLen = calcTimeLenOnNoShift(startTime, endTime, kqItemHm);
//            } else {
//                if (st == ShiftType.FIXED) {
//                    // 加载固定排班
//                    loadFixedShifts(startTime, endTime);
//                } else {
//                    // 加载排班
//                    loadShifts(startTime, endTime, nbase, a0100);
//                }
//                
//                timeLen = calcTimeLenByShift(startTime, endTime, kqItemHm);
//            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        
        return timeLen;
    }
    
    /**
     * 不使用排班功能场景下计算申请时长
     * @param startTime 申请开始时间
     * @param endTime 申请结束时间
     * @param kqItemHm 申请类型对应的考勤规则
     * @return
     */
    private double calcTimeLenOnNoShift(Date startTime, Date endTime, HashMap<String, String> kqItemHm) {
        double timeLen = 0;
        
        AnnualApply annualApply = new AnnualApply();
        
        String kqItemId = kqItemHm.get("item_id");
        String targetUnit = kqItemHm.get("item_unit");
        String hasRest = kqItemHm.get("has_rest");
        String hasFeast = kqItemHm.get("has_feast");
        
        int dayNum = RegisterDate.diffDate(startTime, endTime) + 1;
        float timeLenMinute = 0;
        float baseMinute = 480;
        Date curDateStart = null;
        Date curDateEnd = null;
        
        for (int i = 0; i < dayNum; i++) {
            curDateStart = DateUtils.addDays(startTime, i);
            String curDateStr = DateUtils.format(curDateStart, "yyyy-MM-dd");
            
            DateType curDateType = this.datesType.get(curDateStr);
            
            // 请假、公出需要判断是否包含公休日、节假日
            if (kqItemId.startsWith("0") || kqItemId.startsWith("3")) {
                // 不包含公休日
                if ("0".equals(hasRest) && DateType.REST == curDateType) {
                    continue;
                } 
                
                // 不包含节假日
                if ("0".equals(hasRest) && DateType.FEAST == curDateType) {
                    continue;
                }
            }
            
            if (i != 0) {
                curDateStart = DateUtils.getDate(curDateStr, "yyyy-MM-dd");
            }
            
            if (i < dayNum - 1) {
                curDateEnd = DateUtils.getDate(curDateStr + " 23:59", "yyyy-MM-dd HH:mm");
            } else {
                curDateEnd = endTime;
            }
            
            timeLenMinute = KQRestOper.getPartMinute(curDateStart, curDateEnd);
            
            if (timeLenMinute > 0) {
                baseMinute = timeLenMinute > 480 ? timeLenMinute : 480;
                if (KqConstant.Unit.DAY.equals(targetUnit)) {
                    timeLen += timeLenMinute<=240 ? 0.5 : 1;
                } else {                
                    timeLen += annualApply.tranMinuteValueByUnit(timeLenMinute, targetUnit, baseMinute);
                }
            }
        }
        
        return timeLen;
    }
    
    private ShiftType getShiftType(String nbase, String a0100, Date startTime, Date endTime) {
        ShiftType st = ShiftType.NO;
        
        StringBuffer sql = new StringBuffer();
        sql.append("select A.Group_id,").append(Sql_switcher.isnull("B.Shift_type","1")).append(" shift_type,B.Shift_data,B.rest_type");
        sql.append(" from kq_group_emp_v2 A left join kq_shift_group B");
        sql.append(" on A.group_id=B.group_id");
        sql.append(" WHERE A.guidkey=(select guidkey from ").append(nbase).append("A01");
        sql.append(" where a0100=?)");
        
        ArrayList<String> sqlParams = new ArrayList<String>();
        sqlParams.add(a0100);
        
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString(), sqlParams);
            if (rs.next()) {
                this.groupId = rs.getString("group_id");
                this.shiftData = rs.getString("shift_data");
                this.restType = rs.getString("rest_type");
                // 班组排班类型
                st = rs.getInt("shift_type")==0 ? ShiftType.FIXED : ShiftType.SHIFT;
            } else {
                // 没班组的，也就是不排班的人员，模拟固定排班的方式
                String defaultClassIds = SystemConfig.getPropertyValue("kq_default_class");
                if (StringUtils.isBlank(defaultClassIds)) {
                    defaultClassIds = "0";
                }
                
                // 仅保存当前排班
                this.shiftData = defaultClassIds;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return st;
    }
    
    private void loadDatesType(Date startDate, Date endDate) {
        try {
            ValidateAppOper validateAppOper = new ValidateAppOper(null, this.conn); 

            int num = RegisterDate.diffDate(startDate, endDate) + 1;
            Date curDate = null;
            String curDateStr = "";
            for (int i = 0; i < num; i++) {
                curDate = DateUtils.addDays(startDate, i);
                curDateStr = DateUtils.format(curDate, "yyyy-MM-dd");
                
                if (validateAppOper.is_Feast(curDate)) {
                    datesType.put(curDateStr, DateType.FEAST);
                } else if (validateAppOper.is_Rest(curDate, "", "", "UN")) {
                    datesType.put(curDateStr, DateType.REST);
                } else {
                    datesType.put(curDateStr, DateType.WORK);
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {

    }

}
