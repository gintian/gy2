package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 统计所有请假天数
 * @author Owner
 *
 */
public class AppStatisticsTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String a0100 = (String) hm.get("a0100");
            a0100 = PubFunc.decrypt(a0100);
            String nbase = (String) hm.get("nbase");
            nbase = PubFunc.decrypt(nbase);
            String b0110 = (String) hm.get("b0110");
            b0110 = PubFunc.decrypt(b0110);
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            String select_time_type = (String) this.getFormHM().get("select_time_type");
            if (select_time_type == null || select_time_type.length() <= 0)
                select_time_type = "0";
            SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
            ArrayList list = searchAllApp.getShowType("q15", this.getFrameconn());
            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            RowSet rs = null;
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            ArrayList applist = new ArrayList();
            String isLeave = "false";
            String holiday_type = KqParam.getInstance().getHolidayTypes(this.frameconn, b0110);
            

            for (int i = 0; i < list.size(); i++) {
                try {
                    CommonData da = (CommonData) list.get(i);
                    String kq_item = da.getDataValue();
                    if (kq_item != null && "all".equals(kq_item))
                        continue;
                    
                    HashMap kqItem_hash = annualApply.count_Leave(kq_item);
                    //因为这里要统计天数，所以各种请假类型计算都切换到天数
                    kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
                    float[] holiday_rules =null;//年假假期规则
                    if (KqParam.getInstance().leaveTypeIdInHolidayTypes(holiday_type, kq_item, true)) {
                    	holiday_rules = annualApply.getHoliday_minus_rule();//年假假期规则
                    }
                    
                    StringBuffer sql = new StringBuffer();
                    sql.append("select q1501,q15z1,q15z3,b0110,history from q15 where ");
                    sql.append(" a0100='" + a0100 + "' and nbase='" + nbase + "'");
                    sql.append(" and " + Sql_switcher.isnull("q1517", "0") + "=0");
                    String cond0 = searchAllApp.getWhere2("q15", start_date, end_date, kq_item, "03", "", select_time_type);
                    sql.append(" and ");
                    sql.append(cond0);
                    sql.append(" and q15z0='01'");
                    sql.append(" and " + Sql_switcher.isnull("q1517", "0") + "=0");

                    rs = dao.search(sql.toString());
                    float leaveLen = 0;
                    String history = "";
                    String start = "";
                    String end = "";
                    while (rs.next()) {
                        Date kq_start = rs.getTimestamp("q15z1");
                        Date kq_end = rs.getTimestamp("q15z3");
                        Date src_z1 = rs.getTimestamp("q15z1");
                        start = DateUtils.format(kq_start, "yyyy.MM.dd HH:mm:ss");
                        end = DateUtils.format(kq_end, "yyyy.MM.dd HH:mm:ss");
                        String edD = DateUtils.format(kq_end, "yyyy.MM.dd");
                        String q1501 = rs.getString("q1501");
                        b0110 = rs.getString("b0110");
                        history = rs.getString("history");
                        float leave_tiem = 0;
                        if (KqParam.getInstance().leaveTypeIdInHolidayTypes(holiday_type, kq_item, true)) {
                            leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, a0100, nbase, b0110, kqItem_hash,
                                    holiday_rules);
                        } else {
                            leave_tiem = annualApply.calcLeaveAppTimeLen(nbase, a0100, "", kq_start, kq_end, kqItem_hash, holiday_rules, Integer.MAX_VALUE);
                        }
                        
                        leaveLen = leave_tiem + leaveLen;
                        
                        StringBuffer buf = new StringBuffer();
                        buf.append("select q1501,q15z1,q15z3,b0110 from q15 where ");
                        buf.append(" q1517='1' and q1519='" + q1501 + "' and q15z0='01' and q15z5='03'");
                        
                        RowSet xjrs = null;
                        try {
                            xjrs = dao.search(buf.toString());
                            float xjtime = 0;
                            while (xjrs.next()) {
                                kq_start = xjrs.getTimestamp("q15z1");
                                kq_end = xjrs.getTimestamp("q15z3");
                                if (KqParam.getInstance().leaveTypeIdInHolidayTypes(holiday_type, kq_item, true)) {
                                    if (holiday_rules != null ) {
                                        if (edD.equalsIgnoreCase(DateUtils.format(kq_end, "yyyy.MM.dd")))//先看销假的结束天和请假的结束天是否一致
                                        {
                                            float his_leave_tiem = annualApply.getHistoryLeaveTime(src_z1, kq_start, a0100,
                                                    nbase, b0110, kqItem_hash, holiday_rules);
                                            String history_hols_ed = annualApply.getLeaveManage(a0100, nbase, kq_item, start,
                                                    DateUtils.format(kq_start, "yyyy.MM.dd HH:mm"), his_leave_tiem, "1", b0110,
                                                    kqItem_hash, holiday_rules);
                                            if (history != null && history_hols_ed != null
                                                    && history_hols_ed.equalsIgnoreCase(history)&&kq_start.equals(end_date))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
                                            {
                                                leave_tiem = 0;
                                            } else {
                                                leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, a0100, nbase,
                                                        b0110, kqItem_hash, holiday_rules);
                                              //linbz 20161012 问题23167  leave_tiem 在上面的方法里已包含考勤规则，不需要再根据考勤规则去除天数
//                                                float restday = getRestDay(start, end, a0100, kqItem_hash);
//                                                leave_tiem = leave_tiem - restday;
                                            }
                                        } else {
                                            leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, a0100, nbase, b0110,
                                                    kqItem_hash, holiday_rules);
//                                            float restday = getRestDay(start, end, a0100, kqItem_hash);
//                                            leave_tiem = leave_tiem - restday;
                                        }

                                    } else {
                                        leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, a0100, nbase, b0110,
                                                kqItem_hash, holiday_rules);
//                                        float restday = getRestDay(start, end, a0100, kqItem_hash);
//                                        leave_tiem = leave_tiem - restday;
                                    }
                                } else {
                                    leave_tiem = annualApply.getLeaveTime(kq_start, kq_end, a0100, nbase, b0110, kqItem_hash);//无假期规则，直接计算时间
                                }
                                xjtime = xjtime + leave_tiem;
                            }
                            leaveLen = leaveLen - xjtime;

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            KqUtilsClass.closeDBResource(xjrs);
                        }
                    }
                    
                    if (leaveLen > 0) {
                        String valuef = Float.toString(annualApply.roundNumByItemDecimalWidth(kqItem_hash, leaveLen));
                        da.setDataValue(valuef);
                        applist.add(da);
                        isLeave = "true";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    KqUtilsClass.closeDBResource(rs);
                }

            }

            this.getFormHM().put("leaveTimeList", applist);
            this.getFormHM().put("isLeave", isLeave);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String getClassid(String a0100, Date date) {//获取某一天的班次id
        ContentDAO cd = new ContentDAO(this.getFrameconn());
        StringBuffer sb = new StringBuffer();
        String class_id = null;
        String datestr = OperateDate.dateToStr(date, "yyyy-MM-dd HH:mm");
        sb.append("select class_id from kq_employ_shift where A0100 = '" + a0100 + "' and Q03Z0 = '"
                + datestr.substring(0, 10).replace('-', '.') + "'");
        RowSet rs = null;
        try {
            rs = cd.search(sb.toString());
            while (rs.next()) {
                int id = rs.getInt(1);
                class_id = Integer.toString(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return class_id;
    }

    private float getRestDay(String start_date, String end_date, String a0100, HashMap kqItem_hash) {
        int days = 0;
        ArrayList dateList = new ArrayList();
        Date start = OperateDate.strToDate(start_date.substring(0, 16).replace(".", "-"), "yyyy-MM-dd HH:mm");
        Date end = OperateDate.strToDate(end_date.substring(0, 16).replace(".", "-"), "yyyy-MM-dd HH:mm");
        dateList = OperateDate.getDayByDate(end, start);
        String class_id;
        GetValiateEndDate ve = new GetValiateEndDate(userView, this.getFrameconn());
        String has_feast = (String) kqItem_hash.get("has_feast");
        has_feast = has_feast != null && has_feast.length() > 0 ? has_feast : "0";
        String has_rest = (String) kqItem_hash.get("has_rest");
        has_rest = has_rest != null && has_rest.length() > 0 ? has_rest : "0";
        ArrayList restList = IfRestDate.search_RestOfWeek(null, userView, this.getFrameconn());
        for (int j = 0; j < dateList.size(); j++) {
            Date date = (Date) dateList.get(j);
            class_id = getClassid(a0100, date);
            if (ve.isFeast(date)) {
                if ("0".equals(kqItem_hash.get("has_feast"))) {
                    if ("0".equals(class_id)|| "".equals(class_id))
                        days++;
                }
            } else if ((restList != null && restList.size() > 0) &&( "0".equals(class_id) || class_id == "") ) //不是节假日则为公休日 
            {
                if ("0".equals(kqItem_hash.get("has_rest"))) {
                        days++;
                }
            }
        }
        float day = (float) days;
        BigDecimal b = new BigDecimal(days);
        day = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return day;
    }

}
