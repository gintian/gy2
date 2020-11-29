package com.hjsj.hrms.module.kq.application;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 调休加班类
 * <p>Title: OverTimeForLeaveBo </p>
 * <p>Description: 处理调休加班、调休加相关业务</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2017年9月26日 下午6:48:10</p>
 * @author zhaoxj
 * @version 1.0
 */
public class KqOverTimeForLeaveBo {
    private Connection conn;
    private UserView userView;
    private Category category;
    
    private KqOverTimeForLeaveBo() {
        //私有化，避免不传数据库连接和用户对象而误调用
    }

    public KqOverTimeForLeaveBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.category = Category.getInstance(KqOverTimeForLeaveBo.class);
    }
    
    /**
     *  检查调休假功能是否启用
     * （调休假周期、类型等参数设置了即为启用，否则为未启用）
     * @return
     */
    public boolean validOverTimeForLeaveFunc() {
        boolean valid = false;
        
        KqParam kqParam = KqParam.getInstance();
        //周期类型
        String cycle = kqParam.getOVERTIME_FOR_LEAVETIME_CYCLE();
        cycle = cycle == null ? "" : cycle;
        
        //周期天数（月数）
        String limit = kqParam.getOVERTIME_FOR_LEAVETIME_LIMIT();      
        limit = limit == null ? "" : limit;
        
        //调休假类型
        String leaveType = kqParam.getLEAVETIME_TYPE_USED_OVERTIME();
        leaveType = leaveType == null ? "" : leaveType;
        
        //有调休加班表Q33，并且设置了调休假类型，并且设置了调休周期
        DbWizard dbWizard = new DbWizard(this.conn);
        if (dbWizard.isExistTable("Q33", false) && !"".equals(leaveType) && !"".equals(cycle)) {
            if(("0".equals(cycle) || "1".equals(cycle)) && "".equals(limit)) {
                valid = false;
            } else {
                valid = true;
            }
        }
        
        return valid;
    }
  
    /**
     * 得到当前调休有效范围
     * @Title: getEffectivePeriod   
     * @Description: 取当天所在有效范围
     * @return from：开始日期，to：结束日期
     */
    public HashMap<String, String> getEffectivePeriod() {
        return  getEffectivePeriod(new Date());
    }
    
    /**
     * 得到某日期所处的调休有效范围
     * @Title: getEffectivePeriod   
     * @Description: 得到某日期所处的调休有效范围  
     * @param aDate
     * @return from：开始日期，to：结束日期
     */
    public HashMap<String, String> getEffectivePeriod(Date aDate) {
        Date now = new Date();
        if(aDate == null)
            aDate = now;
        
        String strYear = DateUtils.format(aDate, "yyyy");
        String strMD = DateUtils.format(aDate, "MM-dd");
        
        HashMap<String, String> period = new HashMap<String, String>();
        
        String otForLeaveCycle = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_CYCLE();
        if(StringUtils.isEmpty(otForLeaveCycle))
            return period;
        
        String otForLeaveLimit = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
        
        if("0".equalsIgnoreCase(otForLeaveCycle)) {//按天
            //按天的只能取当天
            int days = 0; 
            if(!StringUtils.isEmpty(otForLeaveLimit))
                days = Integer.parseInt(otForLeaveLimit);
            
            String fromDate = DateUtils.format(DateUtils.addDays(now, 0-days),"yyyy-MM-dd");
            String toDate = DateUtils.format(now, "yyyy-MM-dd");
            period.put("from", fromDate);
            period.put("to",   toDate);
        } else if("1".equalsIgnoreCase(otForLeaveCycle)) {//按年
            period.put("from", strYear + "-01-01");
            period.put("to",   strYear + "-12-31");
        } else if("2".equalsIgnoreCase(otForLeaveCycle)) {//按半年
            if(strMD.compareTo("07-01") < 0) { //上半年
                period.put("from", strYear + "-01-01");
                period.put("to",   strYear + "-06-30");
            } else { //下半年
                period.put("from", strYear + "-07-01");
                period.put("to",   strYear + "-12-31");
            }
        } else if("3".equalsIgnoreCase(otForLeaveCycle)) {//按季度
            if(strMD.compareTo("04-01") < 0) { //第一季度
                period.put("from", strYear + "-01-01");
                period.put("to",   strYear + "-03-31");
            } else if(strMD.compareTo("07-01") < 0){//第二季度
                period.put("from", strYear + "-04-01");
                period.put("to",   strYear + "-06-30");
            } else if(strMD.compareTo("10-01") < 0) {//第三季度
                period.put("from", strYear + "-07-01");
                period.put("to",   strYear + "-09-30");
            } else {//第四季度
                period.put("from", strYear + "-10-01");
                period.put("to",   strYear + "-12-31");
            }
        } else if("4".equalsIgnoreCase(otForLeaveCycle)) {//按月
            int monthNum = 1; 
            if(!StringUtils.isEmpty(otForLeaveLimit))
                monthNum = Integer.parseInt(otForLeaveLimit);
            
            Date fromDate = DateUtils.addMonths(aDate, 1 - monthNum);
            String strFromDate = DateUtils.format(fromDate, "yyyy-MM") + "-01";
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(aDate);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            String strToDate = DateUtils.format(calendar.getTime(), "yyyy-MM-dd");
            
            period.put("from", strFromDate);
            period.put("to",   strToDate);
        }
        
        return  period;
    }
    
    /**
     * 取得某天所在期间内可用（未使用）的调休加班时长分钟数
     * @Title: getUseableTimeLen   
     * @Description:    
     * @param nbase
     * @param a0100
     * @param aDate
     * @return
     */
    public int getUseableTimeMinute(String nbase, String a0100, Date aDate) {
        int timeLen = 0;
        
        HashMap<String, String> period = this.getEffectivePeriod(aDate);
        String from = (String)period.get("from");
        String to = (String)period.get("to");
        
        if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) 
            return timeLen;
        
        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT SUM(Q3309) AS timelen FROM Q33");
        sql.append("SELECT Q3309,Q3303 FROM Q33");
        sql.append(" WHERE nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND q3303>=?");
        sql.append(" AND q3303<=?");
        
        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(a0100);
        params.add(from.replaceAll("-", "."));
        params.add(to.replaceAll("-", "."));
        // 获取q33中包含的日期  
        ArrayList Q3303list = new ArrayList();
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), params);
            while(rs.next()) {
            	timeLen += rs.getInt("Q3309");
            	
            	Q3303list.add(rs.getString("Q3303"));
            }
            // 获取调休加班 已批的 并不包含在q33里
	    	int timeCount = getUseableTimeMinuteNOQ33(Q3303list, nbase, a0100, from, to);
	    	timeLen += timeCount;
	    	
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }        
        
        return timeLen;
    }
    /**
     * 查询调休加班 已批的 并不包含在q33里
     * @param Q3303list		q33中范围内已有的日期集合
     * @param nbase
     * @param a0100
     * @param fromdate		2018-08-08
     * @param todate
     * @return
     */
    public int getUseableTimeMinuteNOQ33(ArrayList Q3303list, String nbase, String a0100
    		, String fromdate, String todate) {
        
    	int timeLen = 0;
        RowSet rs = null;
        try {
        	ContentDAO dao = new ContentDAO(this.conn);
            String fromDateSql = Sql_switcher.dateValue(fromdate+" 00:00:00");
    		String toDateSql = Sql_switcher.dateValue(todate+" 23:59:59");
    		
    		// 获取所有调休假类型SQL串
	    	StringBuffer overtimeTypes = new StringBuffer("");
	    	ArrayList<String> overtimeTypeList = this.getOverTimeForLeaveTypes();
	        for(int i=0; i<overtimeTypeList.size(); i++) {
	            String anOverTimeType = (String)overtimeTypeList.get(i);
	            if(StringUtils.isNotEmpty(anOverTimeType))
	            	overtimeTypes.append("'").append(anOverTimeType).append("'");
	            
	            if(i < overtimeTypeList.size()-1)
	            	overtimeTypes.append(",");
	        }
	        if(StringUtils.isEmpty(overtimeTypes.toString()))
	            return timeLen;
	        
	        AnnualApply annualApply = new AnnualApply(userView, conn);
	        // 获取是否调休的指标标识 =1是调休
	        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
	        // 休息扣除数分钟
	        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
	        
	        ArrayList list = new ArrayList();
	        list.add(nbase);
	        list.add(a0100);
	        // 已销假的单号集合串  ,123,456,789,
	        StringBuffer Q1119Str = new StringBuffer(",");
	        StringBuffer sql = new StringBuffer("");
	        
	        sql.append("SELECT Q1119 FROM Q11");
	        sql.append(" WHERE nbase=?");
	        sql.append(" AND A0100=?");
	        sql.append(" AND Q11Z5='03'");
	        sql.append(" AND Q1103 IN (").append(overtimeTypes.toString()).append(")");
	        // 校验非销假单条件
	        sql.append(" AND (Q1119<>'' or Q1119 is not null)");
	        sql.append(" AND (");
	        sql.append(" (").append("Q11Z1").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z1").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z3").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z1").append("<").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append(">").append(toDateSql).append(")");
	        sql.append(")");
	        rs = dao.search(sql.toString(), list);
	    	while(rs.next()) {
	    		Q1119Str.append(rs.getString("Q1119")+",");
	    	}
	    	
	        sql.setLength(0);
	        sql.append("SELECT * FROM Q11");
	        sql.append(" WHERE nbase=?");
	        sql.append(" AND A0100=?");
	        sql.append(" AND Q11Z5='03'");
	        sql.append(" AND Q1103 IN (").append(overtimeTypes.toString()).append(")");
	        // 校验非销假单条件
	        sql.append(" AND (Q1119='' or Q1119 is null)");
	        sql.append(" AND (");
	        sql.append(" (").append("Q11Z1").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z1").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z3").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z1").append("<").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append(">").append(toDateSql).append(")");
	        sql.append(")");
	        if(StringUtils.isNotEmpty(overForOff))
	        	sql.append(" AND ").append(overForOff).append("='1'");
	        
	        float timeCount = 0;
	        rs = dao.search(sql.toString(), list);
	    	while(rs.next()) {
	    		// 若存在已销假单据 直接跳出
	    		if(Q1119Str.toString().contains(","+rs.getString("q1101")+","))
	    			continue;
	    		boolean bool = true;
	    		Date qFromDate = rs.getTimestamp("q11z1");
	        	Date qToDate = rs.getTimestamp("q11z3");
	        	
	        	for(int i=0;i<Q3303list.size();i++) {
	        		Date q3303Date1 = DateUtils.getDate((String)Q3303list.get(i)+" 00:00", "yyyy.MM.dd HH:mm");
	        		Date q3303Date2 = DateUtils.getDate((String)Q3303list.get(i)+" 23:59", "yyyy.MM.dd HH:mm");
	        		
        			if((q3303Date1.after(qFromDate) && q3303Date2.before(qToDate))
        					|| (!q3303Date1.after(qFromDate) && !q3303Date2.before(qFromDate))
        					|| (!q3303Date1.after(qToDate) && !q3303Date2.before(qToDate))) {	
        				bool = false; 
        				break;
	        		}else 
	        			continue;
	        	}
	        	if(!bool)
	        		continue;
	        	
	        	String q1103 = rs.getString("q1103");
	        	String q1104 = rs.getString("q1104");
	        	// 计算加班申请时长 小时数
	        	timeCount = timeCount + annualApply.getOvertimeLen(q1103 ,q1104 ,nbase, a0100, 
	        			qFromDate, qToDate);
	        	// 扣除休息数 分钟
	        	if(StringUtils.isNotEmpty(dert)) {
	        		float dertCount = rs.getFloat(dert);
	        		timeCount = timeCount - dertCount/60;
	        	}
	    	}
	    	timeLen = (int)(timeCount*60);
	    	
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }        
        
        return timeLen;
    }
    
    /**
     * 返回调休加班类型列表
     * @Title: getOverTimeForLeaveTypes   
     * @Description:    
     * @return 
     */
    public ArrayList<String> getOverTimeForLeaveTypes() {
        String content = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
        if(content == null || "".equals(content))
            return new ArrayList();
        
        String[] array = content.split(",");
        return new ArrayList<String>(Arrays.asList(array));
    }
    
    /**
     * 获取调休假指标对象
     * @return FieldItem
     */
    private FieldItem getLeaveItemUsedOvertime() {
        FieldItem item = null;
        String leaveType = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT fielditemid FROM kq_item");
        sql.append(" WHERE item_id=?");
        ArrayList<String> params = new ArrayList<String>();
        params.add(leaveType);
        
        //从考勤规则表中获取"调休假”对应的日明细指标
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String fieldItemId = "";
        try {
            rs = dao.search(sql.toString(), params);
            if (rs.next()) {
                fieldItemId = rs.getString("fielditemid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (StringUtils.isEmpty(fieldItemId)) {
            return item;
        }
        
        //从字典中获取日明细中调休假指标，如果不存在或未构库，则返回null
        item = DataDictionary.getFieldItem(fieldItemId, "q03");
        if (item == null || !"1".equals(item.getUseflag())) {
            return null;
        }
        
        return item;
    }
    
    /**
     * 检查未调休加班是否超出限额
     * @Title: checkOvertimeForLeaveMaxHour   
     * @Description:    
     * @param overtime 加班申请
     * @return 超限提示信息；不超限，则为空。
     */
    public String checkOvertimeForLeaveMaxHour(LazyDynaBean overtime) {
        StringBuffer msg = new StringBuffer();
        try {
	        if(overtime == null) {
	            this.category.info("未调休加班是否超出限额时没有传入加班信息");
	            return "";
	        }
	        
	        String overtimeType = (String)overtime.get("q1103");
	        //1、检查是否为调休加班 
	        boolean isOvertimeForLeave = isOvertimeForLeave(overtime);
	        //不是调休加班
	        if(!isOvertimeForLeave)
	            return "";
	        
	        //未调休限额小时数
	        int maxLimit = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_MAX_HOUR());
	        //不限制额
	        if(maxLimit <= 0)
	            return "";
	        
	        String nbase = (String)overtime.get("nbase");
	        String a0100 = (String)overtime.get("a0100");
	        Date fromDate = (Date)overtime.get("q11z1");
	        Date toDate = (Date)overtime.get("q11z3");
	        
	        //调休假类型
	        FieldItem leaveItem = getLeaveItemUsedOvertime();
	        int leaveItemDecimalWidth = 2;
	        if (leaveItem != null)
	            leaveItemDecimalWidth = leaveItem.getDecimalwidth();
	        
	        //2、检查当前未调休加班额
	        int useableTimeLen = this.getUseableTimeMinute(nbase, a0100, fromDate);
	        if(useableTimeLen >= maxLimit*60) {
	            msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append((String)overtime.get("a0101"));
	            msg.append("当前未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，已无法再申请调休加班。");
	            return msg.toString();
	        }
	        
	        // 3、检查当前待批调休加班额
	        String q1101 = (String)overtime.get("q1101");
	        int pendingTimeLen = this.getPendingOvertimeForLeaveMinute(q1101, nbase, a0100, overtimeType, fromDate);
	        if((useableTimeLen+pendingTimeLen) >= maxLimit*60) {
	        	    msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append((String)overtime.get("a0101"));
	            msg.append("当前待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，<br/>");
	            msg.append("合计 ").append(PubFunc.round((pendingTimeLen+useableTimeLen)/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("已超出限额要求！<br/>");
	            msg.append("请调整本次申请。");
	            return msg.toString();
	        }
	        // 4、检查该时间段内调休假的销假申请单已报批状态的时长
	        int pendingLeaveTimeLen = this.getPendingLeavetimeForLeaveMinute("", nbase, a0100, fromDate);
	        if((useableTimeLen+pendingTimeLen+pendingLeaveTimeLen) >= maxLimit*60) {
        	    msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append((String)overtime.get("a0101"));
	            msg.append("当前待批调休假的销假申请 ").append(PubFunc.round(pendingLeaveTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，<br/>");
	            msg.append("合计 ").append(PubFunc.round((pendingLeaveTimeLen+pendingTimeLen+useableTimeLen)/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("已超出限额要求！<br/>");
	            msg.append("请调整本次申请。");
	            return msg.toString();
	        }
	        // 5、计算当前加班时长
	        String q1104 = (String)overtime.get("q1104");
	        double nowOverTimeLen = 0;
        	    AnnualApply annualApply = new AnnualApply(userView, conn);

            	// 计算加班申请时长 小时数
            	float timeCount = annualApply.getOvertimeLen(overtimeType ,q1104 ,nbase, a0100, fromDate, toDate);
            	nowOverTimeLen = timeCount*60;
            	
            	// 休息扣除数分钟
	        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
	        if(StringUtils.isNotEmpty(dert)) {
		        Object dertCount = (Object)overtime.get(dert);
		        double derts = Double.parseDouble(dertCount.toString());
		        nowOverTimeLen = nowOverTimeLen - derts;
	        }
	        
	        if((nowOverTimeLen+useableTimeLen+pendingTimeLen+pendingLeaveTimeLen) > maxLimit*60) {
	            msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append((String)overtime.get("a0101"));
	            msg.append("当前申请加班 ").append(PubFunc.round(nowOverTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("待批调休假的销假申请 ").append(PubFunc.round(pendingLeaveTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，<br/>");
	            msg.append("合计 ").append(PubFunc.round((nowOverTimeLen+pendingLeaveTimeLen+pendingTimeLen+useableTimeLen)/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("已超出限额要求！<br/>");
	            msg.append("请调整本次申请。");
	            return msg.toString();
	        }
	        
	    } catch (Exception e) {
	   	 	e.printStackTrace();
	   	 	msg.append(e.toString());
	   } 
        return msg.toString();
    }
    
    public String checkOvertimeForLeaveMaxHour(RecordVo overtime) {
        LazyDynaBean overtimeBean = new LazyDynaBean();
        overtimeBean.set("q1101", overtime.getString("q1101"));
        overtimeBean.set("nbase", overtime.getString("nbase"));
        overtimeBean.set("a0100", overtime.getString("a0100"));
        overtimeBean.set("a0101", overtime.getString("a0101"));
        overtimeBean.set("q1103", overtime.getString("q1103"));
        overtimeBean.set("q11z1", overtime.getDate("q11z1"));
        overtimeBean.set("q11z3", overtime.getDate("q11z3"));
        // 班次
        overtimeBean.set("q1104", overtime.getString("q1104"));
        // 是否调休
        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
        if(StringUtils.isNotEmpty(overForOff))
        	overtimeBean.set(overForOff, overtime.getString(overForOff)==null?"2":overtime.getString(overForOff));
        // 休息扣除数
        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
        if(StringUtils.isNotEmpty(dert)) 
        	overtimeBean.set(dert, overtime.getObject(dert)==null?"0":overtime.getObject(dert));
        
        return checkOvertimeForLeaveMaxHour(overtimeBean);
    }

    /**
     * 判断是否为调休加班
     * @Title: isOvertimeForLeave   
     * @Description: 满足：1、是调休加班之一，2、无“是否调休”或有“是否调休”指标时值为“是”1的   
     * @param overtime 加班数据
     * @return
     */
    public boolean isOvertimeForLeave(LazyDynaBean overtime) {
        String overtimeType = (String)overtime.get("q1103");
        
        boolean isOvertimeForLeave = false;
        ArrayList<String> overtimeTypes = this.getOverTimeForLeaveTypes();
        for(int i=0; i<overtimeTypes.size(); i++) {
            String anOverTimeTypes = (String)overtimeTypes.get(i);
            if(anOverTimeTypes.equals(overtimeType)) {
                isOvertimeForLeave = true;
                break;
            }
        }
        //不是调休加班之一，退出
        if(!isOvertimeForLeave)
          return false;
        
        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
        if (overForOff != null && overForOff.length() > 0) {
            isOvertimeForLeave = "1".equalsIgnoreCase((String)overtime.get(overForOff));
            //是调休加班之一，但是不调休，退出
            if(!isOvertimeForLeave)
              return false;
        }
        
        return isOvertimeForLeave;
    }
    /**
     * 获取该时间段内调休假的销假申请单已报批状态的时长
     * @param nbase
     * @param a0100
     * @param fromDate
     * @return
     */
    public int getPendingLeavetimeForLeaveMinute(String q1501, String nbase, String a0100, Date fromDate) {
    	int timeLen = 0;
    	RowSet rs = null;
    	try {
    		HashMap<String, String> period = this.getEffectivePeriod(fromDate);
    		String from = (String)period.get("from");
    		String to = (String)period.get("to");
    		
    		if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) 
    			return timeLen;
    		
    		String fromDateSql = Sql_switcher.dateValue(from+" 00:00:00");
    		String toDateSql = Sql_switcher.dateValue(to+" 23:59:59");
    		ContentDAO dao = new ContentDAO(this.conn);
    		AnnualApply annualApply = new AnnualApply(userView, conn);
    		StringBuffer sql = new StringBuffer("");
    		// 查询在途中的 调休假 的 销假申请(已报批)
    		sql.append("SELECT * FROM Q15");
	        sql.append(" WHERE nbase=?");
	        sql.append(" AND A0100=?");
	        sql.append(" AND Q1503=?");
	        sql.append(" AND Q15Z5='02'");
	        if(StringUtils.isNotBlank(q1501) && !"pendingLeaveApp".equals(q1501))
	        	sql.append(" AND Q1501<>?");
	        // 校验 调休假销假单条件
	        if(!"pendingLeaveApp".equals(q1501))
	        	sql.append(" AND Q1517=1 AND (Q1519<>'' or Q1519 is not null)");
	        sql.append(" AND (");
	        sql.append(" (").append("Q15Z1").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q15Z1").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q15Z3").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q15Z3").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q15Z1").append("<").append(fromDateSql);
	        sql.append(" AND ").append("Q15Z3").append(">").append(toDateSql).append(")");
	        sql.append(")");
	        // 获取调休假类型
	        String leaveTypeId = KqParam.getInstance().getLeaveTimeTypeUsedOverTime();
	        ArrayList list = new ArrayList();
	        list.add(nbase);
	        list.add(a0100);
	        list.add(leaveTypeId);
	        if(StringUtils.isNotBlank(q1501) && !"pendingLeaveApp".equals(q1501))
	        	list.add(q1501);
	        
	        //考勤规则应取该假类自己的规则
            HashMap kqItem_hash = annualApply.count_Leave(leaveTypeId);
            kqItem_hash.remove("item_unit");
            kqItem_hash.put("item_unit", KqConstant.Unit.HOUR);
            
	        float timeCount = 0;
	        rs = dao.search(sql.toString(), list);
	    	while(rs.next()) {
	    		Date qFromDate = rs.getTimestamp("q15z1");
	        	Date qToDate = rs.getTimestamp("q15z3");
	        	String b0110 = rs.getString("b0110");
	        	
	        	// 计算调休假 销假申请时长 小时数
	        	timeCount = timeCount + annualApply.calcLeaveAppTimeLen(nbase, a0100, b0110, 
	        			qFromDate, qToDate, kqItem_hash, null, Integer.MAX_VALUE);
	    	}
	    	timeLen = (int)(timeCount*60);
	    	
    	} catch (Exception e) {
	    	 e.printStackTrace();
	    } finally {
	        PubFunc.closeDbObj(rs);
	    }
    	return timeLen;
    }
    /**
     * 获取在途的调休加班时长
     * @param q1101
     * @param nbase
     * @param a0100
     * @param overtimeType
     * @param fromDate
     * @return
     */
    public int getPendingOvertimeForLeaveMinute(String q1101, String nbase, String a0100, String overtimeType, Date fromDate) {
    	int timeLen = 0;
    	RowSet rs = null;
    	try {
    		HashMap<String, String> period = this.getEffectivePeriod(fromDate);
    		String from = (String)period.get("from");
    		String to = (String)period.get("to");
    		
    		if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) 
    			return timeLen;
    		
    		String fromDateSql = Sql_switcher.dateValue(from+" 00:00:00");
    		String toDateSql = Sql_switcher.dateValue(to+" 23:59:59");
    		
    		// 查询调休加班在途的
	    	// 获取所有调休假类型SQL串
	    	StringBuffer overtimeTypes = new StringBuffer("");
	    	ArrayList<String> overtimeTypeList = this.getOverTimeForLeaveTypes();
	        for(int i=0; i<overtimeTypeList.size(); i++) {
	            String anOverTimeType = (String)overtimeTypeList.get(i);
	            if(StringUtils.isNotEmpty(anOverTimeType))
	            	overtimeTypes.append("'").append(anOverTimeType).append("'");
	            
	            if(i < overtimeTypeList.size()-1)
	            	overtimeTypes.append(",");
	        }
	        if(StringUtils.isEmpty(overtimeTypes.toString()))
	            return timeLen;
	        
	        ContentDAO dao = new ContentDAO(this.conn);
	        AnnualApply annualApply = new AnnualApply(userView, conn);
	        StringBuffer sql = new StringBuffer("");
	        // 获取是否调休的指标标识 =1是调休
	        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
	        // 休息扣除数分钟
	        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
	        sql.setLength(0);
	        sql.append("SELECT * FROM Q11");
	        sql.append(" WHERE nbase=?");
	        sql.append(" AND A0100=?");
	        // 33836 linbz 模板审批时会把报批后的该条单据作为待批单据处理，故加校验条件不等于该单号的待批数据
	        if(StringUtils.isNotEmpty(q1101))
	        	sql.append(" AND Q1101<>?");
	        sql.append(" AND Q11Z5='02'");
	        sql.append(" AND Q1103 IN (").append(overtimeTypes.toString()).append(")");
	        // 校验非销假单条件
	        sql.append(" AND (Q1119='' or Q1119 is null)");
	        sql.append(" AND (");
	        sql.append(" (").append("Q11Z1").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z1").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z3").append(">=").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append("<=").append(toDateSql).append(")");
	        sql.append(" OR (").append("Q11Z1").append("<").append(fromDateSql);
	        sql.append(" AND ").append("Q11Z3").append(">").append(toDateSql).append(")");
	        sql.append(")");
	        if(StringUtils.isNotEmpty(overForOff))
	        	sql.append(" AND ").append(overForOff).append("='1'");
	        
	        ArrayList list = new ArrayList();
	        list.add(nbase);
	        list.add(a0100);
	        if(StringUtils.isNotEmpty(q1101))
	        	list.add(q1101);
	        
	        float timeCount = 0;
	        rs = dao.search(sql.toString(), list);
	    	while(rs.next()) {
	    		Date qFromDate = rs.getTimestamp("q11z1");
	        	Date qToDate = rs.getTimestamp("q11z3");
	        	String q1103 = rs.getString("q1103");
	        	String q1104 = rs.getString("q1104");
	        	
	        	// 计算加班申请时长 小时数
	        	timeCount = timeCount + annualApply.getOvertimeLen(q1103 ,q1104 ,nbase, a0100, 
	        			qFromDate, qToDate);
	        	// 扣除休息数 分钟
	        	if(StringUtils.isNotEmpty(dert)) {
	        		float dertCount = rs.getFloat(dert);
	        		timeCount = timeCount - dertCount/60;
	        	}
	    	}
	    	timeLen = (int)(timeCount*60);
	    	
    	} catch (Exception e) {
	    	 e.printStackTrace();
	    } finally {
	        PubFunc.closeDbObj(rs);
	    }
    	return timeLen;
    }
    /**
     * 得到有效的考勤区间内查看调休的有效范围
     * @Title: getEffectivePeriod   
     * @param aDate
     * @return 
     */
    public String getLeaveCycleList() {
        StringBuffer dateIntervalJson = new StringBuffer("[");
        RowSet rowSet = null;
        try{
            String otForLeaveCycle = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_CYCLE();
            if("0".equalsIgnoreCase(otForLeaveCycle) || "4".equalsIgnoreCase(otForLeaveCycle))
                return "";
            
            StringBuffer strsql = new StringBuffer();
            strsql.append("SELECT DISTINCT kq_year FROM kq_duration order by kq_year desc");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(strsql.toString());
            int i = 0;
            while (rowSet.next()) {
                String holidYear = rowSet.getString("kq_year");
                if("1".equalsIgnoreCase(otForLeaveCycle)) {//按年
                    dateIntervalJson.append("{id:'" + holidYear + "',name:'" + holidYear + "'},");
                } else if("2".equalsIgnoreCase(otForLeaveCycle)) {//按半年
                    dateIntervalJson.append("{id:'" + holidYear + "-01-01',name:'" + holidYear + "上半年'},");
                    dateIntervalJson.append("{id:'" + holidYear + "-07-01',name:'" + holidYear + "下半年'},");
                } else if("3".equalsIgnoreCase(otForLeaveCycle)) {//按季度
                    dateIntervalJson.append("{id:'" + holidYear + "-01-01',name:'" + holidYear + "第一季度'},");
                    dateIntervalJson.append("{id:'" + holidYear + "-04-01',name:'" + holidYear + "第二季度'},");
                    dateIntervalJson.append("{id:'" + holidYear + "-07-01',name:'" + holidYear + "第三季度'},");
                    dateIntervalJson.append("{id:'" + holidYear + "-10-01',name:'" + holidYear + "第四季度'},");
                } 
            }
            
            if(dateIntervalJson.toString().endsWith(","))
                dateIntervalJson.setLength(dateIntervalJson.length() - 1);
            
            dateIntervalJson.append("]");
            
                
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return  dateIntervalJson.toString();
    }

    /**
     * 得到有效的考勤区间内查看调休的有效范围
     * @Title: getEffectivePeriod   
     * @param aDate
     * @return 
     */
    public String getShowLeaveCycle() {
        String showLeaveCycle = "";
        RowSet rowSet = null;
        try{
            String otForLeaveCycle = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_CYCLE();
            
            StringBuffer strsql = new StringBuffer();
            strsql.append("SELECT distinct kq_year, kq_duration  FROM kq_duration ");
            strsql.append("where finished = '0'");
            strsql.append("order by kq_year, kq_duration");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(strsql.toString());
            if(rowSet.next())
                showLeaveCycle = rowSet.getString("kq_year");
            // 34513 调休假显示默认有效期错误
            int holidMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
            if("1".equalsIgnoreCase(otForLeaveCycle)) {//按年
                
            } else if("2".equalsIgnoreCase(otForLeaveCycle)) {//按半年
                if(holidMonth < 7)
                    showLeaveCycle += "-01-01";
                else
                    showLeaveCycle += "-07-01";
                    
            } else if("3".equalsIgnoreCase(otForLeaveCycle)) {//按季度
                if(holidMonth < 4)
                    showLeaveCycle += "-01-01";
                else if(holidMonth < 7)
                    showLeaveCycle += "-04-01";
                else if(holidMonth < 10)
                    showLeaveCycle += "-07-01";
                else
                    showLeaveCycle += "-10-01";
            } else if("4".equalsIgnoreCase(otForLeaveCycle)) {//按季度
                if(holidMonth < 10)
                    showLeaveCycle += "-0" + holidMonth + "-01";
                else
                    showLeaveCycle += "-" + holidMonth + "-01";
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return  showLeaveCycle;
    }
    
    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }
    /**
     * 校验销假  销调休假是否符合规则
     * @Title: checkQXJOvertimeForLeaveAllHour   
     * @Description:    
     * @param qxjVo		该销假单数据 
     * @return 超限提示信息；不超限，则为空。
     */
    public String checkQXJOvertimeForLeaveAllHour(RecordVo qxjVo) {
        StringBuffer msg = new StringBuffer();
        try {
	        if(null == qxjVo) {
	            this.category.info("未调休加班是否超出限额时没有传入加班信息");
	            return "";
	        }
	        //1、检查所销假单是否为调休假 // 上一步已有调休假类型校验
	        // 未调休限额小时数
	        int maxLimit = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_MAX_HOUR());
	        //不限制额
	        if(maxLimit <= 0)
	            return "";
	        String nbase = qxjVo.getString("nbase");
	        String a0100 = qxjVo.getString("a0100");
	        // 现在销假 应按当前时间校验
	        Date nowDate = new Date();
	        
	        //2、检查当前未调休加班额
	        int useableTimeLen = this.getUseableTimeMinute(nbase, a0100, nowDate);
	        if(useableTimeLen >= maxLimit*60) {
	            msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append(qxjVo.getString("a0101"));
	            msg.append("当前未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", 2)).append("小时，已无法申请该销假单。");
	            return msg.toString();
	        }
	        
	        //3、检查当前待批调休加班额
	        int pendingTimeLen = this.getPendingOvertimeForLeaveMinute("", nbase, a0100, "", nowDate);
	        if((useableTimeLen+pendingTimeLen) >= maxLimit*60) {
	        	msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
        	    msg.append(qxjVo.getString("a0101"));
	            msg.append("当前待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("合计 ").append(PubFunc.round(pendingTimeLen/60.0 + useableTimeLen/60.0 + "", 2)).append("小时，<br/>");
	            msg.append("已超出限额要求！");
	            msg.append("已无法申请该销假单。");
	            return msg.toString();
	        }
	        // 销假如果是请假单的销假则需去除 该申请单本身的待批
	        String q1501 = qxjVo.getObject("q1501")==null?"":qxjVo.getString("q1501");
	        // 4、检查该时间段内调休假的销假申请单已报批状态的时长
	        int pendingLeaveTimeLen = this.getPendingLeavetimeForLeaveMinute(q1501, nbase, a0100, nowDate);
	        if((useableTimeLen+pendingTimeLen+pendingLeaveTimeLen) >= maxLimit*60) {
	        	msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
        	    msg.append(qxjVo.getString("a0101"));
        	    msg.append("当前待批调休假的销假申请 ").append(PubFunc.round(pendingLeaveTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("合计 ").append(PubFunc.round((pendingLeaveTimeLen+pendingTimeLen+useableTimeLen)/60.0 + "", 2)).append("小时，<br/>");
	            msg.append("已超出限额要求！");
	            msg.append("已无法申请该销假单。");
	            return msg.toString();
	        }
	        // 4、计算当前加班时长
	        Date fromDate = null;
	        Date toDate = null;
	        Object obj1 = qxjVo.getObject("q15z1");
	        Object obj3 = qxjVo.getObject("q15z3");
	        if (obj1 instanceof Date) 
	        {
	            fromDate = (Date) obj1;
	            toDate = (Date) obj3;
	        } else if (obj1 instanceof String) 
	        {
	            fromDate =  OperateDate.strToDate((String) obj1, "yyyy-MM-dd HH:mm");
	            toDate =  OperateDate.strToDate((String) obj3, "yyyy-MM-dd HH:mm");
	        }
	        
	        double nowOverTimeLen = 0;
    	    AnnualApply annualApply = new AnnualApply(userView, conn);
    	    // 考勤规则应取改假类自己的规则
            HashMap kqItemHash = annualApply.count_Leave(qxjVo.getString("q1503"));
            kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
            // 假期时长扣减规则参数
            float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
            if (KqParam.getInstance().isHoliday(this.conn, qxjVo.getString("b0110"), qxjVo.getString("q1503")))
                holidayRules = annualApply.getHoliday_minus_rule();
            float timeLen = annualApply.calcLeaveAppTimeLen(nbase, a0100, "", fromDate, toDate, kqItemHash, holidayRules, Integer.MAX_VALUE);
        	nowOverTimeLen = timeLen*60;
            	
        	// 休息扣除数分钟
//	        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
//	        if(StringUtils.isNotEmpty(dert)) {
//		        Object dertCount = (Object)overtime.get(dert);
//		        double derts = Double.parseDouble(dertCount.toString());
//		        nowOverTimeLen = nowOverTimeLen - derts;
//	        }
	        
	        if((nowOverTimeLen+useableTimeLen+pendingTimeLen) > maxLimit*60) {
	            msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	            msg.append(qxjVo.getString("a0101"));
	            msg.append("当前销假申请单 ").append(PubFunc.round(nowOverTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("待批调休假的销假申请 ").append(PubFunc.round(pendingLeaveTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", 2)).append("小时，");
	            msg.append("合计 ").append(PubFunc.round((nowOverTimeLen+pendingLeaveTimeLen+pendingTimeLen+useableTimeLen)/60.0 + "", 2)).append("小时，<br/>");
	            msg.append("已超出限额要求！");
	            msg.append("已无法申请该销假单。");
	            return msg.toString();
	        }
	        
	    } catch (Exception e) {
	   	 	e.printStackTrace();
	   	 	msg.append(e.toString());
	   } 
        return msg.toString();
    }
    /**
     * 校验销假 销调休加班
     * @param qxjVo
     * @return
     */
    public String checkQXJOvertimeForPendingLeaveHour(RecordVo qxjVo) {
        StringBuffer msg = new StringBuffer();
        try {
	        if(qxjVo == null) {
	            this.category.info("未调休加班是否超出限额时没有传入加班信息");
	            return "";
	        }
	        String overtimeType = qxjVo.getString("q1103");
	        //未调休限额小时数
	        int maxLimit = Integer.parseInt(KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_MAX_HOUR());
	        //不限制额
	        if(maxLimit <= 0)
	            return "";
	        String a0101 = qxjVo.getString("a0101");
	        String nbase = qxjVo.getString("nbase");
	        String a0100 = qxjVo.getString("a0100");
	        Date fromDate = qxjVo.getDate("q11z1");
	        Date toDate = qxjVo.getDate("q11z3");
	        //调休假类型
	        FieldItem leaveItem = getLeaveItemUsedOvertime();
	        int leaveItemDecimalWidth = 2;
	        if (leaveItem != null)
	            leaveItemDecimalWidth = leaveItem.getDecimalwidth();
	        //2、检查当前未调休加班额
	        int useableTimeLen = this.getUseableTimeMinute(nbase, a0100, fromDate);
	        // 3、检查当前待批调休加班额
//	        String q1101 = qxjVo.getString("q1101");
//	        int pendingTimeLen = this.getPendingOvertimeForLeaveMinute(q1101, nbase, a0100, overtimeType, fromDate);
	        // 4、检查该时间段内调休假的 销假 申请单已报批状态的时长
//	        int pendingLeaveTimeLen = this.getPendingLeavetimeForLeaveMinute("", nbase, a0100, fromDate);
	        // 5、检查该时间段内调休假的申请单已报批状态的时长
	        int pendingLeaveAppTimeLen = this.getPendingLeavetimeForLeaveMinute("pendingLeaveApp", nbase, a0100, fromDate);
	        // 6、计算当前加班时长
//	        String q1104 = qxjVo.getString("q1104");
	        double nowOverTimeLen = 0;
        	AnnualApply annualApply = new AnnualApply(userView, conn);
        	// 计算加班申请时长 小时数
        	float timeCount = annualApply.getOvertimeLen(overtimeType ,"" ,nbase, a0100, fromDate, toDate);
        	nowOverTimeLen = timeCount*60;
        	// 休息扣除数分钟
	        String dert = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
	        if(StringUtils.isNotEmpty(dert)) {
		        Object dertCount = qxjVo.getObject(dert)==null?"0":qxjVo.getObject(dert);
		        double derts = Double.parseDouble(dertCount.toString());
		        nowOverTimeLen = nowOverTimeLen - derts;
	        }
//	        int sumLen = maxLimit*60-(useableTimeLen+pendingTimeLen+pendingLeaveTimeLen+pendingLeaveAppTimeLen);
	        int sumLen = useableTimeLen - pendingLeaveAppTimeLen;
	        // 45076 若出现负数则按0处理
	        if(sumLen < nowOverTimeLen) {
	        	msg.append("未调休加班限额").append(maxLimit).append("小时。<br/>");
	        	msg.append(a0101);
	        	msg.append("当前撤销调休加班 ").append(PubFunc.round(nowOverTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	        	msg.append("待批调休假的申请 ").append(PubFunc.round(pendingLeaveAppTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
//	        	msg.append("待批调休假的销假申请 ").append(PubFunc.round(pendingLeaveTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
//	            msg.append("待批调休加班 ").append(PubFunc.round(pendingTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，");
	            msg.append("未调休加班共").append(PubFunc.round(useableTimeLen/60.0 + "", leaveItemDecimalWidth)).append("小时，<br/>");
	            msg.append("故本次可撤销调休加班时长最多为").append(PubFunc.round((sumLen<0 ? 0 : sumLen)/60.0 + "", leaveItemDecimalWidth)).append("小时，<br/>");
	            msg.append("请调整本次申请。");
	        }
	    } catch (Exception e) {
	   	 	e.printStackTrace();
	   	 	msg.append(e.toString());
	    } 
        return msg.toString();
    }
    
    public static void main(String[] args) {
        KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo();
        HashMap<String, String> period = kqOverTimeForLeaveBo.getEffectivePeriod();
        if(period != null && !period.isEmpty())
            System.out.println(period.get("from").toString() + "~" + period.get("to").toString());
        else
            System.out.println("没取到有效期");
    }
}
