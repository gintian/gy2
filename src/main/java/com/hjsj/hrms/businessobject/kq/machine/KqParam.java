package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class KqParam {

    private static boolean PARAM_LOADED = false;        //静态变量参数是否已加载过

    private static String  kq_startdate_field;          //人员开始考勤日期指标
    private static String  kq_enddate_field;            //人员停止考勤日期指标
    private static String  dept_changedate_field;       //人员部门变动日期指标
    private static String  KQ_DEPARTMENT;               //考勤部门	
    private static String  KQBOOK_ITEMS;                //考勤簿月汇总指标
    private static String  Q03_STAT_FIELDS;             //考勤统计指标
    private static String  SHIFT_GROUP_ITEM;            //主集班组指标
    private static int     card_interval;                //重复刷卡间隔X分钟
    private static int     min_overtime;                 //加班时间不少于X分钟
    private static String  OPINION_OVERTIME_TYPE;       //是否判断申请加班类型与申请日期相符
    private static String  REST_OVERTIME_TIME;          //节假日或公休日加班申请是否允许申请多次
    private static String  need_busicompare;             //业务申请是否与实际刷卡作比对
    private static String  busi_cardbegin;               //刷卡开始时间最早从申请起始时间前X分钟起
    private static String  busi_cardend;                 //刷卡结束时间最迟到申请结束时间后X分钟止：
    private static String  restleave_calctime_type;      //中间休息时段有出入刷卡,则离岗时长应
    private static String  MIN_MID_LEAVE_TIME;          //单次进出小于等于X分钟不计离岗时间
    private static String  FLEXTIME_RULER;              //弹性班规则
    private static String  busifact_diff;                //6、申请时长小于刷卡时长超过X分钟计为异常 BUSIFACT_DIFF
    private static String  busi_morethan_fact;           //7、申请时长大于刷卡时长超过X分钟计为异常BUSI_MORETHAN_FACT
    private static String  default_rest_kqclass;
    private static String  check_inout_match;
    private static String  holidayShiftIsOvertime;      //节假日排班算加班
    private static String  leaveTimeTypeUsedOverTime;   //倒休假类型
    private static String  holidayTypes;                //假期管理假类
    private static String  HOLIDAY_MINUS_RULE;          //假期扣减规则
    private static String  q05_control;
    private static String  q05_control_status;
    private static String  NET_SIGN_CHECK_IP;           //网上签到是否绑定ip
    private static String  NET_SIGN_APPROVE;            //网上签到是否需要审批   

    private static String  LEAVE_NEED_CHECK;
    private static String  LEAVE_COMPARE_RULE;
    private static String  LEAVE_UPDATA_DATA;
    private static String  OVERTIME_NEED_CHECK;
    private static String  OVERTIME_COMPARE_RULE;
    private static String  OVERTIME_UPDATA_DATA;
    private static String  OFFICELEAVE_NEED_CHECK;
    private static String  OFFICELEAVE_COMPARE_RULE;
    private static String  OFFICELEAVE_UPDATA_DATA;
    private static String  OFFICELEAVE_ENABLE_LEAVE_OVERTIME; //公出期间允许请假、加班

    private static String  logon_kq_hint;
    private static String  STANDARD_HOURS;				//标准工时 默认8小时
    private static String  OVERTIME_FOR_LEAVETIME;      //可以转调休的加班
    private static String  LEAVETIME_TYPE_USED_OVERTIME; //调休假类型
    private static String  OVERTIME_FOR_LEAVETIME_LIMIT; //加班转调休有效期限（按天、月） 
    private static String  OVERTIME_FOR_LEAVETIME_CYCLE; //加班转调休有效周期（0：天 1：年 2：半年 3：季度 4：月）
    private static String  OVERTIME_FOR_LEAVETIME_MAX_HOUR; //未调休加班最大限额小时（<=0不限制）

    private static String  data_processing;             //数据处理方式（分用户、集中））
    private static String  QUICK_ANALYSE_MODE;          //数据处理模式（精简

    private static String  DURATION_OVERTIME_MAX_LIMIT; //考勤期间内加班最大限额

    private static String  kq_orgview_post;             //组织机构树岗位节点是否隐藏
    private static String  CARD_CAUSATION;              //刷卡原因代码
    private static String  CARD_WF_RELATION;            //补刷卡审批关系
    private static String  EARLY_MINUTE;                //提前X分钟算作早到
    private static String  PIGEONHOLE_TYPE;             //归档审批方式
    private static String  UP_DAILYREFISTER;            //修改日明细登记数据 0:允许 1：不允许
    private static String  APPROVED_DELETE;             // 已批申请登记数据是否可以删除;0:不删除；1：删除
    
    private static String MAGCARD_SETID; //存储卡号的子集
    private static String MAGCARD_FLAG; //从磁卡中读取卡号
    private static String MAGCARD_CARDID; //工作证登记表
    private static String MAGCARD_COM;//读卡器com口
    
    private static String REST_TO_OVERTIME; //是否启用休息日转加班
    private static String REST_TO_OVERTIME_CARD; //0 需要进出匹配 1 有刷卡即加班
    private static String REST_TO_OVERTIME_TIMELEN; //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
    private static String REST_TO_OVERTIME_APPLY; //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认

    private static String SELF_ACCEPT_MONTH_DATA; //需要员工确认月汇总数据 0：不需要 1：需要
    
    private static class KqParamInstance {
        private static KqParam instance = new KqParam();
    }

    public static KqParam getInstance() {
        return KqParamInstance.instance;
    }

    private KqParam() {
        init();
    }

    //重新加载全部参数
    public void reloadAllParam() {
        PARAM_LOADED = false;
        init();
    }

    public void setKqDepartment(Connection conn, String kqDepartment) {
        kqDepartment = tranDefaultSign(kqDepartment);
        if (setContent(conn, "UN", "EXCHANGE_GROUP_ITEM", kqDepartment, "考勤部门")) {
            KqParam.KQ_DEPARTMENT = kqDepartment;
        }
    }

    public String getKqDepartment() {
        return KqParam.KQ_DEPARTMENT;
    }

    public String getData_processing() {
        return KqParam.data_processing;
    }

    public void setData_processing(Connection conn, String data_processing) {
        if (setContent(conn, "UN", "DATA_PROCESSING", data_processing, "数据处理模式")) {
            KqParam.data_processing = data_processing;
        }
    }

    public String getLogon_kq_hint() {
        return KqParam.logon_kq_hint;
    }

    public void setLogon_kq_hint(Connection conn, String logonKqHint) {
        if (setContent(conn, "UN", "LOGON_KQ_HINT", logonKqHint, "登录时是否需要考勤打卡提醒")) {
            KqParam.logon_kq_hint = logonKqHint;
        }
    }

    public String getQ05_control() {
        return KqParam.q05_control;
    }

    public void setQ05_control(Connection conn, String q05Control, String status) {
        if (setContent(conn, "UN", "Q05_CONTROL", q05Control, "员工月汇总审核", status)) {
            KqParam.q05_control = q05Control;
            KqParam.q05_control_status = status;
        }
    }

    public String getQ05ControlStatus() {
        return KqParam.q05_control_status;
    }

    public String getHolidayShiftIsOvertime() {
        return KqParam.holidayShiftIsOvertime;
    }

    public void setHolidayShiftIsOvertime(Connection conn, String holidayShiftIsOvertime) {
        if (setContent(conn, "UN", "HOLIDAYSHIFT_OVERTIME", holidayShiftIsOvertime, "节假日有排班算加班")) {
            KqParam.holidayShiftIsOvertime = holidayShiftIsOvertime;
        }
    }

    public String getCheck_inout_match() {
        return check_inout_match;
    }

    public void setCheck_inout_match(Connection conn, String check_inout_match) {
        if (setContent(conn, "UN", "CHECK_INOUT_MATCH", check_inout_match, "刷卡匹配情况")) {
            KqParam.check_inout_match = check_inout_match;
        }
    }

    public String getDefault_rest_kqclass() {
        return KqParam.default_rest_kqclass;
    }

    public void setDefault_rest_kqclass(Connection conn, String default_rest_kqclass) {
        if (default_rest_kqclass == null || default_rest_kqclass.length() <= 0 || "#".equals(default_rest_kqclass)) {
            default_rest_kqclass = "0";
        }

        if (setContent(conn, "UN", "DEFAULT_REST_KQCLASS", default_rest_kqclass, "默认班次")) {
            KqParam.default_rest_kqclass = default_rest_kqclass;
        }
    }

    public int getBusi_cardbegin() {
        int b_f = 0;
        b_f = Integer.parseInt(KqParam.busi_cardbegin);
        return b_f;
    }

    public void setBusi_cardbegin(Connection conn, String busi_cardbegin) {
        if (busi_cardbegin == null || busi_cardbegin.length() <= 0) {
            busi_cardbegin = "0";
        }

        if (setContent(conn, "UN", "BUSI_CARDBEGIN", busi_cardbegin, "刷卡开始时间最早从申请起始时间前X分钟起")) {
            KqParam.busi_cardbegin = busi_cardbegin;
        }
    }

    public int getBusi_cardend() {
        int b_f = 0;
        b_f = Integer.parseInt(KqParam.busi_cardend);
        return b_f;
    }

    public void setBusi_cardend(Connection conn, String busi_cardend) {
        if (busi_cardend == null || busi_cardend.length() <= 0) {
            busi_cardend = "0";
        }

        if (setContent(conn, "UN", "BUSI_CARDEND", busi_cardend, "刷卡结束时间最迟到申请结束时间后X分钟止")) {
            KqParam.busi_cardend = busi_cardend;
        }
    }

    public int getCard_interval() {
        return KqParam.card_interval;
    }

    public void setCard_interval(Connection conn, String card_interval) {
        if (card_interval == null || card_interval.length() <= 0) {
            card_interval = "0";
        }

        if (setContent(conn, "UN", "CARD_INTERVAL", card_interval, "重复刷卡间隔")) {
            KqParam.card_interval = Integer.parseInt(card_interval);
        }
    }

    public int getMin_overtime() {

        return KqParam.min_overtime;
    }

    public void setMin_overtime(Connection conn, String min_overtime) {
        if (min_overtime == null || min_overtime.length() <= 0) {
            min_overtime = "0";
        }

        if (setContent(conn, "UN", "MIN_OVERTIME", min_overtime, "加班时间不少于X分钟")) {
            KqParam.min_overtime = Integer.parseInt(min_overtime);
        }
    }

    public String getNeed_busicompare() {
        return KqParam.need_busicompare;
    }

    public void setNeed_busicompare(Connection conn, String need_busicompare) {
        if (need_busicompare == null || need_busicompare.length() <= 0) {
            need_busicompare = "";
        }

        if (setContent(conn, "UN", "NEED_BUSICOMPARE", need_busicompare, "业务申请是否与实际刷卡作比对")) {
            KqParam.need_busicompare = need_busicompare;
        }
    }

    public String getRestleave_calctime_type() {
        return KqParam.restleave_calctime_type;
    }

    public void setRestleave_calctime_type(Connection conn, String restleave_calctime_type) {
        if (restleave_calctime_type == null || restleave_calctime_type.length() <= 0) {
            restleave_calctime_type = "2";
        }

        if (this.setContent(conn, "UN", "RESTLEAVE_CALCTIME_TYPE", restleave_calctime_type, "中间休息时段有出入刷卡")) {
            KqParam.restleave_calctime_type = restleave_calctime_type;
        }
    }

    /*
     * 获取调休假类型
     */
    public String getLeaveTimeTypeUsedOverTime() {
        return KqParam.leaveTimeTypeUsedOverTime;
    }

    public void setLeaveTimeTypeUsedOverTime(Connection conn, String leaveTimeTypeUsedOverTime) {
        if (setContent(conn, "UN", "LEAVETIME_TYPE_USED_OVERTIME", leaveTimeTypeUsedOverTime, "倒休假类型")) {
            KqParam.leaveTimeTypeUsedOverTime = leaveTimeTypeUsedOverTime;
            LEAVETIME_TYPE_USED_OVERTIME = leaveTimeTypeUsedOverTime;
        }
    }

    /*
     * 获取假期管理假类
     */
    public String getHolidayTypes(Connection conn, UserView userView) {
        String name = "Holiday_type";
        KqParam.holidayTypes = getContentWithGroup(conn, userView, name, "06,");

        return KqParam.holidayTypes;
    }
    
    public String getHolidayTypes(Connection conn, String b0110) {
        String name = "Holiday_type";
        if (!b0110.startsWith("UN")) {
            b0110 = "UN" + b0110;
        }
        return getContentWithGroup(conn, b0110, name, "06,");
    }

    public void setHolidayTypes(Connection conn, UserView userView, String holidayTypes) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        if (setContent(conn, orgId, "Holiday_type", holidayTypes, "假期管理类别")) {
            KqParam.holidayTypes = holidayTypes;
        }
    }

    private String getUserManagePrivOrgIdWithPre(Connection conn, UserView userView) {
        String orgId;
        if (userView.isSuper_admin()) {
            orgId = "UN";
        } else {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
            orgId = "UN" + managePrivCode.getPrivOrgId();
        }

        return orgId;
    }

    /*
     * 获取网上签到绑定ip参数
     */
    public String getNetSignApprove() {
        return KqParam.NET_SIGN_APPROVE;
    }

    /*
     * 设置网上签到绑定ip参数
     */
    public void setNetSignApprove(Connection conn, String netSignApprove) {
        if (setContent(conn, "UN", "NET_SIGN_APPROVE", netSignApprove, "签到签退数据需审批")) {
            KqParam.NET_SIGN_APPROVE = netSignApprove;
        }
    }

    /*
     * 获取网上签到是否需要审批参数
     */
    public String getNetSignCheckIP() {
        return KqParam.NET_SIGN_CHECK_IP;
    }

    /*
     * 设置网上签到是否需要审批参数
     */
    public void setNetSignCheckIP(Connection conn, String netSignCheckIP) {
        if (setContent(conn, "UN", "NET_SIGN_CHECK_IP", netSignCheckIP, "网上签到是否限制IP")) {
            KqParam.NET_SIGN_CHECK_IP = netSignCheckIP;
        }
    }

    public int getBusi_morethan_fact() {
        return Integer.parseInt(KqParam.busi_morethan_fact);
    }

    public void setBusi_morethan_fact(Connection conn, String busi_morethan_fact) {
        if (busi_morethan_fact == null || busi_morethan_fact.length() <= 0) {
            busi_morethan_fact = "0";
        }

        if (setContent(conn, "UN", "BUSI_MORETHAN_FACT", busi_morethan_fact, "申请时长大于刷卡时长超过X分钟计为异常")) {
            KqParam.busi_morethan_fact = busi_morethan_fact;
        }
    }

    public int getBusifact_diff() {
        return Integer.parseInt(KqParam.busifact_diff);
    }

    public void setBusifact_diff(Connection conn, String busifact_diff) {
        if (busifact_diff == null || busifact_diff.length() <= 0) {
            busifact_diff = "0";
        }

        if (setContent(conn, "UN", "BUSIFACT_DIFF", busifact_diff, "申请时长小于刷卡时长超过X分钟计为异常")) {
            KqParam.busifact_diff = busifact_diff;
        }
    }

    /*请假*/
    public String getLEAVE_NEED_CHECK() {
        return LEAVE_NEED_CHECK;
    }

    public void setLEAVE_NEED_CHECK(Connection conn, String leave_need_check) {
        if (setContent(conn, "UN", "LEAVE_NEED_CHECK", leave_need_check, "是否需要比对请假申请")) {
            KqParam.LEAVE_NEED_CHECK = leave_need_check;
        }
    }

    public String getLEAVE_COMPARE_RULE() {
        return LEAVE_COMPARE_RULE;
    }

    public void setLEAVE_COMPARE_RULE(Connection conn, String leave_compare_rule, String leave_updata_data) {
        if (setContent(conn, "UN", "LEAVE_COMPARE_RULE", leave_compare_rule, "请假申请比对自动处理规则", leave_updata_data)) {
            KqParam.LEAVE_COMPARE_RULE = leave_compare_rule;
        }
    }

    /*公出*/
    public String getOFFICELEAVE_NEED_CHECK() {
        return OFFICELEAVE_NEED_CHECK;
    }

    public void setOFFICELEAVE_NEED_CHECK(Connection conn, String officeleave_need_check) {
        if (setContent(conn, "UN", "OFFICELEAVE_NEED_CHECK", officeleave_need_check, "是否需要比对公出申请")) {
            KqParam.OFFICELEAVE_NEED_CHECK = officeleave_need_check;
        }
    }

    public String getOFFICELEAVE_COMPARE_RULE() {
        return OFFICELEAVE_COMPARE_RULE;
    }

    public void setOFFICELEAVE_COMPARE_RULE(Connection conn, String officeleave_compare_rule, String officeleave_updata_data) {
        if (setContent(conn, "UN", "OFFICELEAVE_COMPARE_RULE", officeleave_compare_rule, "公出申请比对自动处理规则", officeleave_updata_data)) {
            KqParam.OFFICELEAVE_COMPARE_RULE = officeleave_compare_rule;
        }
    }

    /*加班*/
    public String getOVERTIME_NEED_CHECK() {
        return OVERTIME_NEED_CHECK;
    }

    public void setOVERTIME_NEED_CHECK(Connection conn, String overtime_need_check) {
        if (setContent(conn, "UN", "OVERTIME_NEED_CHECK", overtime_need_check, "是否需要比对加班申请")) {
            KqParam.OVERTIME_NEED_CHECK = overtime_need_check;
        }
    }

    public String getOVERTIME_COMPARE_RULE() {
        return OVERTIME_COMPARE_RULE;
    }

    public void setOVERTIME_COMPARE_RULE(Connection conn, String overtime_compare_rule, String overtime_updata_data) {
        if (setContent(conn, "UN", "OVERTIME_COMPARE_RULE", overtime_compare_rule, "加班申请比对自动处理规则", overtime_updata_data)) {
            KqParam.OVERTIME_COMPARE_RULE = overtime_compare_rule;
        }
    }

    public String getLEAVE_UPDATA_DATA() {
        return LEAVE_UPDATA_DATA;
    }

    public void setLEAVE_UPDATA_DATA(String lEAVEUPDATADATA) {
        LEAVE_UPDATA_DATA = lEAVEUPDATADATA;
    }

    public String getOVERTIME_UPDATA_DATA() {
        return OVERTIME_UPDATA_DATA;
    }

    public void setOVERTIME_UPDATA_DATA(String oVERTIMEUPDATADATA) {
        OVERTIME_UPDATA_DATA = oVERTIMEUPDATADATA;
    }

    public String getOFFICELEAVE_UPDATA_DATA() {
        return OFFICELEAVE_UPDATA_DATA;
    }

    public void setOFFICELEAVE_UPDATA_DATA(String oFFICELEAVEUPDATADATA) {
        OFFICELEAVE_UPDATA_DATA = oFFICELEAVEUPDATADATA;
    }

    public void setOVERTIME_FOR_LEAVETIME(Connection conn, String overtimeForLeavetime) {
        if (setContent(conn, "UN", "OVERTIME_FOR_LEAVETIME", overtimeForLeavetime, "调休加班类型")) {
            OVERTIME_FOR_LEAVETIME = overtimeForLeavetime;
        }
    }

    public String getOVERTIME_FOR_LEAVETIME() {
        return OVERTIME_FOR_LEAVETIME;
    }

    public String getLEAVETIME_TYPE_USED_OVERTIME() {
        return LEAVETIME_TYPE_USED_OVERTIME;
    }

    public void setOVERTIME_FOR_LEAVETIME_LIMIT(Connection conn, String overtimeForLeavetimeLimit) {
        if (setContent(conn, "UN", "OVERTIME_FOR_LEAVETIME_LIMIT", overtimeForLeavetimeLimit, "加班转调休有效期限")) {
            OVERTIME_FOR_LEAVETIME_LIMIT = overtimeForLeavetimeLimit;
        }
    }

    public String getOVERTIME_FOR_LEAVETIME_LIMIT() {
        return OVERTIME_FOR_LEAVETIME_LIMIT;
    }
    
    public void setOVERTIME_FOR_LEAVETIME_CYCLE(Connection conn, String overtimeForLeaveCycle) {
        if (setContent(conn, "UN", "OVERTIME_FOR_LEAVETIME_CYCLE", overtimeForLeaveCycle, "加班转调休有效周期")) {
            OVERTIME_FOR_LEAVETIME_CYCLE = overtimeForLeaveCycle;
        }
    }

    public String getOVERTIME_FOR_LEAVETIME_CYCLE() {
        return OVERTIME_FOR_LEAVETIME_CYCLE;
    }
    
    public void setOVERTIME_FOR_LEAVETIME_MAX_HOUR(Connection conn, String overtimeForLeavetimeLimit) {
        if (setContent(conn, "UN", "OVERTIME_FOR_LEAVETIME_MAX_HOUR", overtimeForLeavetimeLimit, "未调休加班限额小时数")) {
            OVERTIME_FOR_LEAVETIME_MAX_HOUR = overtimeForLeavetimeLimit;
        }
    }

    public String getOVERTIME_FOR_LEAVETIME_MAX_HOUR() {
        return OVERTIME_FOR_LEAVETIME_MAX_HOUR;
    }

    public void setDURATION_OVERTIME_MAX_LIMIT(Connection conn, String durationOvertimeMaxLimit) {
        if (setContent(conn, "UN", "DURATION_OVERTIME_MAX_LIMIT", durationOvertimeMaxLimit, "考勤期间最大加班限额")) {
            DURATION_OVERTIME_MAX_LIMIT = durationOvertimeMaxLimit;
        }
    }

    public String getDURATION_OVERTIME_MAX_LIMIT() {
        return DURATION_OVERTIME_MAX_LIMIT;
    }

    public void setOpinionOvertimeType(Connection conn, String oPinionOvertimeType) {
        if (setContent(conn, "UN", "OPINION_OVERTIME_TYPE", oPinionOvertimeType, "是否判断申请加班类型与日期相符")) {
            OPINION_OVERTIME_TYPE = oPinionOvertimeType;
        }
    }

    public String getOpinionOvertimeType() {
        return OPINION_OVERTIME_TYPE;
    }

    public void setRestOvertimeTimes(Connection conn, String restOvertimeTimes) {
        if (setContent(conn, "UN", "REST_OVERTIME_TIME", restOvertimeTimes, "节假日或公休日加班申请是否允许申请多次")) {
            REST_OVERTIME_TIME = restOvertimeTimes;
        }
    }

    public String getRestOvertimeTimes() {
        return REST_OVERTIME_TIME;
    }

    public void setShiftGroupItem(Connection conn, String shiftGroupItem) {
        shiftGroupItem = tranDefaultSign(shiftGroupItem);
        if (setContent(conn, "UN", "SHIFT_GROUP_ITEM", shiftGroupItem, "班组指标")) {
            SHIFT_GROUP_ITEM = shiftGroupItem;
        }
    }

    public String getShiftGroupItem() {
        return SHIFT_GROUP_ITEM;
    }

    public void setKqOrgViewPost(Connection conn, String kqOrgViewPost) {
        if (setContent(conn, "UN", "kq_orgview_post", kqOrgViewPost, "组织机构树隐藏岗位节点")) {
            KqParam.kq_orgview_post = kqOrgViewPost;
        }
    }

    public String getKqOrgViewPost() {
        return kq_orgview_post;
    }

    public void setQuickAnalyseMode(Connection conn, String quickAnalyseMode) {
        if (setContent(conn, "UN", "QUICK_ANALYSE_MODE", quickAnalyseMode, "数据处理方式")) {
            QUICK_ANALYSE_MODE = quickAnalyseMode;
        }
    }

    public String getQuickAnalyseMode() {
        return QUICK_ANALYSE_MODE;
    }

    public void setKqStartDateField(Connection conn, String kqStartDateField) {
        kqStartDateField = tranDefaultSign(kqStartDateField);
        if (setContent(conn, "UN", "kq_startdate_field", kqStartDateField, "考勤开始日期")) {
            KqParam.kq_startdate_field = kqStartDateField;
        }
    }

    public String getKqStartDateField() {
        return kq_startdate_field;
    }

    public void setSTANDARD_HOURS(Connection conn, String standard_hours) {
        if (setContent(conn, "UN", "STANDARD_HOURS", standard_hours, "标准工时（小时）")) {
            KqParam.STANDARD_HOURS = standard_hours;
        }
    }

    public String getSTANDARD_HOURS() {
        return STANDARD_HOURS;
    }
    public void setKqEndDateField(Connection conn, String kqEndDateField) {
        kqEndDateField = tranDefaultSign(kqEndDateField);
        if (setContent(conn, "UN", "kq_enddate_field", kqEndDateField, "考勤结束日期")) {
            KqParam.kq_enddate_field = kqEndDateField;
        }
    }

    public String getKqEndDateField() {
        return kq_enddate_field;
    }

    public void setDeptChangeDateField(Connection conn, String deptChangeDateField) {
        deptChangeDateField = tranDefaultSign(deptChangeDateField);
        if (setContent(conn, "UN", "dept_changedate_field", deptChangeDateField, "部门变动日期")) {
            KqParam.dept_changedate_field = deptChangeDateField;
        }
    }

    public String getDeptChangeDateField() {
        return dept_changedate_field;
    }

    public void setKqBookItems(Connection conn, String kqBookItems) {
        if (setContent(conn, "UN", "KQBOOK_ITEMS", kqBookItems, "考勤簿指标")) {
            KQBOOK_ITEMS = kqBookItems;
        }
    }

    public String getKqBookItems() {
        return KQBOOK_ITEMS;
    }

    public void setQ03StatFields(Connection conn, String q03StatFields) {
        if (setContent(conn, "UN", "KQ_PARAM", q03StatFields, "考勤项目基本指标")) {
            Q03_STAT_FIELDS = q03StatFields;
        }
    }

    public String getQ03StatFields() {
        return Q03_STAT_FIELDS;
    }

    public void setFlextimeRuler(Connection conn, String flextimeRuler) {
        if (setContent(conn, "UN", "FLEXTIME_RULER", flextimeRuler, "弹性班规则")) {
            FLEXTIME_RULER = flextimeRuler;
        }
    }

    public String getFlextimeRuler() {
        return FLEXTIME_RULER;
    }

    public void setHolidayMinusRule(Connection conn, String holidayMinusRule) {
        if (setContent(conn, "UN", "HOLIDAY_MINUS_RULE", holidayMinusRule, "假期扣减规则")) {
            HOLIDAY_MINUS_RULE = holidayMinusRule;
        }
    }

    public String getHolidayMinusRule() {
        return HOLIDAY_MINUS_RULE;
    }

    public void setMinMidLeaveTime(Connection conn, String minMidLeaveTime) {
        if (setContent(conn, "UN", "MIN_MID_LEAVE_TIME", minMidLeaveTime, "离岗最小时长限制")) {
            MIN_MID_LEAVE_TIME = minMidLeaveTime;
        }
    }

    public String getMinMidLeaveTime() {
        return MIN_MID_LEAVE_TIME;
    }

    public void setCardCausation(Connection conn, String cardCausation) {
        cardCausation = tranDefaultSign(cardCausation);
        if (setContent(conn, "UN", "CARD_CAUSATION", cardCausation, "补刷卡原因代码项")) {
            CARD_CAUSATION = cardCausation;
        }
    }

    public String getCardCausation() {
        return CARD_CAUSATION;
    }

    public void setCardWfRelation(Connection conn, String cardWfRelation) {
        cardWfRelation = tranDefaultSign(cardWfRelation);
        if (setContent(conn, "UN", "CARD_WF_RELATION", cardWfRelation, "刷卡审批关系")) {
            CARD_WF_RELATION = cardWfRelation;
        }
    }

    public String getCardWfRelation() {
        return CARD_WF_RELATION;
    }

    public void setEarlyMinute(Connection conn, String earlyMinute) {
        if (setContent(conn, "UN", "EARLY_MINUTE", earlyMinute, "提前X分钟算作早到")) {
            EARLY_MINUTE = earlyMinute;
        }
    }

    public String getEarlyMinute() {
        return EARLY_MINUTE;
    }

    public void setArchiveType(Connection conn, String archiveType) {
        if (setContent(conn, "UN", "PIGEONHOLE_TYPE", archiveType, "归档审批方式")) {
            PIGEONHOLE_TYPE = archiveType;
        }
    }

    public String getArchiveType() {
        return PIGEONHOLE_TYPE;
    }

    public void setUpdateDailyRegister(Connection conn, String upDailyRegister) {
        if (setContent(conn, "UN", "UP_DAILYREFISTER", upDailyRegister, "修改日明细数据")) {
            UP_DAILYREFISTER = upDailyRegister;
        }
    }

    public String getUpdateDailyRegister() {
        return UP_DAILYREFISTER;
    }

    public void setApprovedDelete(Connection conn, String approvedDelete) {
        if (setContent(conn, "UN", "APPROVED_DELETE", approvedDelete, "已批申请登记数据是否可以删除")) {
            APPROVED_DELETE = approvedDelete;
        }
    }

    public String getApprovedDelete() {
        return APPROVED_DELETE;
    }

    public void setMagcardSetId(Connection conn, String magcardSetId) {
        magcardSetId = tranDefaultSign(magcardSetId);
        if (setContent(conn, "UN", "MAGCARD_SETID", magcardSetId, "存储卡号的子集")) {
            MAGCARD_SETID = magcardSetId;
        }
    }

    public String getMagcardSetId() {
        return MAGCARD_SETID;
    }

    public void setMagcardFlag(Connection conn, String magcardFlag) {
        if (setContent(conn, "UN", "MAGCARD_FLAG", magcardFlag, "从磁卡中读取卡号")) {
            MAGCARD_FLAG = magcardFlag;
        }
    }

    public String getMagcardFlag() {
        return MAGCARD_FLAG;
    }

    public void setMagcardCardId(Connection conn, String magcardCardId) {
        magcardCardId = tranDefaultSign(magcardCardId);
        if (setContent(conn, "UN", "MAGCARD_CARDID", magcardCardId, "工作证登记表")) {
            MAGCARD_CARDID = magcardCardId;
        }
    }

    public String getMagcardCardId() {
        return MAGCARD_CARDID;
    }

    public void setMagcardCom(Connection conn, String magcardCom) {
        if (setContent(conn, "UN", "MAGCARD_COM", magcardCom, "读卡器com口")) {
            MAGCARD_COM = magcardCom;
        }
    }

    public String getMagcardCom() {
        return MAGCARD_COM;
    }
    
    public String getRepairCardNum(Connection conn, UserView userView) {
        return getContentWithGroup(conn, userView, "REPAIR_CARD_NUM", "");        
    }
    
    public String getRepairCardNum(Connection conn, String b0110) {
        return getContentWithGroup(conn, b0110, "REPAIR_CARD_NUM", "");        
    }

    public void setRepairCardNum(Connection conn, UserView userView, String repairCardNum, String status) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        setContent(conn, orgId, "REPAIR_CARD_NUM", repairCardNum, "补刷卡次数限制", status);
    }
    
    public String getRepairCardNumStatus(Connection conn, UserView userView) {
        return getStatusWithGroup(conn, userView, "REPAIR_CARD_NUM", "0");
    }

    
    public String getLateLeavetimeRule(Connection conn, UserView userView) {
        return getContentWithGroup(conn, userView, "Late_Leavetime_rule", "");        
    }
    
    public String getLateLeavetimeRule(Connection conn, String b0110) {
        return getContentWithGroup(conn, b0110, "Late_Leavetime_rule", "");        
    }
    
    public void setLateLeavetimeRule(Connection conn, UserView userView, String lateLeavetimeRule, String status) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        setContent(conn, orgId, "Late_Leavetime_rule", lateLeavetimeRule, "请假最迟规则", status);
    }
    
    public String getLateLeavetimeRuleStatus(Connection conn, UserView userView) {
        return getStatusWithGroup(conn, userView, "Late_Leavetime_rule", "0");
    }
    
    public String getLeavetimeRule(Connection conn, UserView userView) {
        return getContentWithGroup(conn, userView, "Leavetime_rule", "");        
    }
    
    
    
    public String getLeavetimeRule(Connection conn, String b0110) {
        return getContentWithGroup(conn, b0110, "Leavetime_rule", "");        
    }
    
    public void setLeavetimeRule(Connection conn, UserView userView, String leavetimeRule, String status) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        setContent(conn, orgId, "Leavetime_rule", leavetimeRule, "请假提前规则", status);
    }
    
    public String getLeavetimeRuleStatus(Connection conn, UserView userView) {
        return getStatusWithGroup(conn, userView, "Leavetime_rule", "0");
    }
    
    public String getOvertimeRule(Connection conn, UserView userView) {
        String overtimeRule = getContentWithGroup(conn, userView, "Overtime_rule", "");        
        return tranOvertimeRuleValue(overtimeRule);
    }
    
    public String getOvertimeRule(Connection conn, String b0110) {
        String overtimeRule = getContentWithGroup(conn, b0110, "Overtime_rule", "");
        return tranOvertimeRuleValue(overtimeRule);
    }
    
    private String tranOvertimeRuleValue(String overtimeRule) {
        if (overtimeRule == null || overtimeRule.length() <= 0) {
            return "";
        }
        
        try {
            return Integer.parseInt(overtimeRule) + "";
        } catch (Exception e) {
            return "";
        }
    }
    
    public void setOvertimeRule(Connection conn, UserView userView, String overtimeRule, String status) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        setContent(conn, orgId, "Overtime_rule", overtimeRule, "加班登记最迟规则", status);
    }
    
    public String getOvertimeRuleStatus(Connection conn, UserView userView) {
        return getStatusWithGroup(conn, userView, "Overtime_rule", "0");
    }
    
    public String getMainSetFields(Connection conn, UserView userView) {
        return getContentWithGroup(conn, userView, "MAIN_SET", "");        
    }
    
    public void setMainSetFields(Connection conn, UserView userView, String mainSetFields, String status) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        setContent(conn, orgId, "MAIN_SET", mainSetFields, "人员基本信息指标列表", status);
    }
    
    public void setRestToOvertime(Connection conn, String restToOvertime) {
        if (setContent(conn, "UN", "REST_TO_OVERTIME", restToOvertime, "是否启用休息日转加班", restToOvertime)) {
            REST_TO_OVERTIME = restToOvertime;
        }
    }

    public String getRestToOvertime() {
        return REST_TO_OVERTIME;
    }

    public void setRestToOvertimeCard(Connection conn, String restToOvertimeCard) {
        if (setContent(conn, "UN", "REST_TO_OVERTIME_CARD", restToOvertimeCard, "休息日转加班刷卡要求")) {
            REST_TO_OVERTIME_CARD = restToOvertimeCard;
        }
    }

    public String getRestToOvertimeCard() {
        return REST_TO_OVERTIME_CARD;
    }

    public void setRestToOvertimeTimelen(Connection conn, String restToOvertimeTimelen) {
        if (setContent(conn, "UN", "REST_TO_OVERTIME_TIMELEN", restToOvertimeTimelen, "休息日转加班时长规则")) {
            REST_TO_OVERTIME_TIMELEN = restToOvertimeTimelen;
        }
    }

    public String getRestToOvertimeTimelen() {
        return REST_TO_OVERTIME_TIMELEN;
    }

    public void setRestToOvertimeApply(Connection conn, String restToOvertimeApply) {
        if (setContent(conn, "UN", "REST_TO_OVERTIME_APPLY", restToOvertimeApply, "休息日转加班申请单规则")) {
            REST_TO_OVERTIME_APPLY = restToOvertimeApply;
        }
    }

    public String getRestToOvertimeApply() {
        return REST_TO_OVERTIME_APPLY;
    }

    private void init() {
        Connection conn = null;
        try {
            if (KqParam.PARAM_LOADED) {
                return;
            }

            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            //中间休息时段有出入刷卡,则离岗时长应
            String name = "RESTLEAVE_CALCTIME_TYPE";
            String content = getContent(dao, name, "UN", "2");
            restleave_calctime_type = content;
            
            //考勤开始日期
            name = "kq_startdate_field";
            content = getContent(dao, name, "UN", "");
            kq_startdate_field = content;

            //考勤结束日期
            name = "kq_enddate_field";
            content = getContent(dao, name, "UN", "");
            kq_enddate_field = content;
            //标准工时
            name = "STANDARD_HOURS";
            content = getContent(dao, name, "UN", "8");
            STANDARD_HOURS = content;

            //考勤部门变动日期指标
            name = "dept_changedate_field";
            content = getContent(dao, name, "UN", "");
            dept_changedate_field = content;

            //考勤簿指标
            name = "KQBOOK_ITEMS";
            content = getContent(dao, name, "UN", "");
            KQBOOK_ITEMS = content;

            //考勤汇总指标 KQ_PARAM此参数名起的奇差无比，忍痛再次继续使用
            name = "KQ_PARAM";
            content = getContent(dao, name, "UN", "");
            Q03_STAT_FIELDS = content;

            //考勤部门
            name = "EXCHANGE_GROUP_ITEM";
            content = getContent(dao, name, "UN", "");
            KQ_DEPARTMENT = content;

            //班组指标
            name = "SHIFT_GROUP_ITEM";
            content = getContent(dao, name, "UN", "");
            SHIFT_GROUP_ITEM = content;

            //1、重复刷卡间隔X分钟： CARD_INTERVAL
            name = "CARD_INTERVAL";
            content = getContent(dao, name, "UN", "0");
            card_interval = Integer.parseInt(content);

            //2、加班时间不少于X分钟：MIN_OVERTIME
            name = "MIN_OVERTIME";
            content = getContent(dao, name, "UN", "0");
            min_overtime = Integer.parseInt(content);

            // 是否判断加班申请类型与日期相符
            name = "OPINION_OVERTIME_TYPE";
            content = getContent(dao, name, "UN", "1");
            OPINION_OVERTIME_TYPE = content;

            // 节假日或公休日加班申请是否允许申请多次
            name = "REST_OVERTIME_TIME";
            content = getContent(dao, name, "UN", "1");
            REST_OVERTIME_TIME = content;

            //3、业务申请是否与实际刷卡作比对：NEED_BUSICOMPARE 
            name = "NEED_BUSICOMPARE";
            content = getContent(dao, name, "UN", "0");
            need_busicompare = content;

            //4、刷卡开始时间最早从申请起始时间前X分钟起：BUSI_CARDBEGIN
            name = "BUSI_CARDBEGIN";
            content = getContent(dao, name, "UN", "0");
            busi_cardbegin = content;

            //5、刷卡结束时间最迟到申请结束时间后X分钟止：BUSI_CARDEND
            name = "BUSI_CARDEND";//5、刷卡结束时间最迟到申请结束时间后X分钟止：BUSI_CARDEND
            content = getContent(dao, name, "UN", "0");
            busi_cardend = content;

            name = "BUSIFACT_DIFF";
            content = getContent(dao, name, "UN", "0");
            busifact_diff = content;

            name = "BUSI_MORETHAN_FACT";
            content = getContent(dao, name, "UN", "0");
            busi_morethan_fact = content;

            name = "DEFAULT_REST_KQCLASS";
            content = getContent(dao, name, "UN", "0");
            default_rest_kqclass = content;

            name = "CHECK_INOUT_MATCH";
            content = getContent(dao, name, "UN", "0");
            check_inout_match = content;

            name = "HOLIDAYSHIFT_OVERTIME";
            content = getContent(dao, name, "UN", "0");
            holidayShiftIsOvertime = content;

            name = "Q05_CONTROL";
            content = getContent(dao, name, "UN", "0");
            q05_control = content;
            content = getStatus(dao, name, "UN", "0");
            q05_control_status = content;

            name = "NET_SIGN_CHECK_IP";
            content = getContent(dao, name, "UN", "1");
            NET_SIGN_CHECK_IP = content;

            name = "NET_SIGN_APPROVE";
            content = getContent(dao, name, "UN", "0");
            NET_SIGN_APPROVE = content;

//            name = "Holiday_type";
//            content = getContent(dao, name, "UN", "");
//            holidayTypes = content;

            name = "LEAVE_NEED_CHECK";
            content = getContent(dao, name, "UN", "0");
            LEAVE_NEED_CHECK = content;

            name = "LEAVE_COMPARE_RULE";
            content = getContent(dao, name, "UN", "0");
            LEAVE_COMPARE_RULE = content;
            content = getStatus(dao, name, "UN", "1");
            LEAVE_UPDATA_DATA = content;

            name = "OVERTIME_NEED_CHECK";
            content = getContent(dao, name, "UN", "0");
            OVERTIME_NEED_CHECK = content;

            name = "OVERTIME_COMPARE_RULE";
            content = getContent(dao, name, "UN", "0");
            OVERTIME_COMPARE_RULE = content;
            content = getStatus(dao, name, "UN", "1");
            OVERTIME_UPDATA_DATA = content;

            name = "OFFICELEAVE_NEED_CHECK";
            content = getContent(dao, name, "UN", "0");
            OFFICELEAVE_NEED_CHECK = content;

            name = "OFFICELEAVE_COMPARE_RULE";
            content = getContent(dao, name, "UN", "0");
            OFFICELEAVE_COMPARE_RULE = content;
            content = getStatus(dao, name, "UN", "1");
            OFFICELEAVE_UPDATA_DATA = content;

            name = "LOGON_KQ_HINT";
            content = getContent(dao, name, "UN", "0");
            logon_kq_hint = content;

            //调休加班5个参数
            name = "OVERTIME_FOR_LEAVETIME";
            content = getContent(dao, name, "UN", "");
            OVERTIME_FOR_LEAVETIME = content;

            name = "LEAVETIME_TYPE_USED_OVERTIME";
            content = getContent(dao, name, "UN", "");
            LEAVETIME_TYPE_USED_OVERTIME = content;
            leaveTimeTypeUsedOverTime = content;

            name = "OVERTIME_FOR_LEAVETIME_LIMIT";
            content = getContent(dao, name, "UN", "0");
            OVERTIME_FOR_LEAVETIME_LIMIT = content;
            
            name = "OVERTIME_FOR_LEAVETIME_CYCLE";
            content = getContent(dao, name, "UN", "0");
            OVERTIME_FOR_LEAVETIME_CYCLE = content;
            
            name = "OVERTIME_FOR_LEAVETIME_MAX_HOUR";
            content = getContent(dao, name, "UN", "0");
            OVERTIME_FOR_LEAVETIME_MAX_HOUR = content;

            name = "DATA_PROCESSING";
            content = getContent(dao, name, "UN", "0");
            data_processing = content;

            name = "QUICK_ANALYSE_MODE";
            content = getContent(dao, name, "UN", "0");
            QUICK_ANALYSE_MODE = content;

            name = "DURATION_OVERTIME_MAX_LIMIT";
            content = getContent(dao, name, "UN", "");
            DURATION_OVERTIME_MAX_LIMIT = content;

            name = "kq_orgview_post";
            content = getContent(dao, name, "UN", "0");
            kq_orgview_post = content;

            //弹性班规则，默认完全弹性
            name = "FLEXTIME_RULER";
            content = getContent(dao, name, "UN", "0");
            FLEXTIME_RULER = content;

            //假期扣减规则
            name = "HOLIDAY_MINUS_RULE";
            content = getContent(dao, name, "UN", "");
            HOLIDAY_MINUS_RULE = content;

            //单次进出小于等于X分钟不计离岗时间
            name = "MIN_MID_LEAVE_TIME";
            content = getContent(dao, name, "UN", "");
            MIN_MID_LEAVE_TIME = content;

            //补刷卡原因代码项
            name = "CARD_CAUSATION";
            CARD_CAUSATION = getContent(dao, name, "UN", "");

            //补刷卡审批关系
            name = "CARD_WF_RELATION";
            CARD_WF_RELATION = getContent(dao, name, "UN", "0");

            //提前X分钟算作早到
            name = "EARLY_MINUTE";
            EARLY_MINUTE = getContent(dao, name, "UN", "");

            //归档审批方式
            name = "PIGEONHOLE_TYPE";
            PIGEONHOLE_TYPE = getContent(dao, name, "UN", "");

            //修改日明细数据
            name = "UP_DAILYREFISTER";
            UP_DAILYREFISTER = getContent(dao, name, "UN", "0");

            //已批申请登记数据是否可以删除
            name = "APPROVED_DELETE";
            APPROVED_DELETE = getContent(dao, name, "UN", "1");
            
            name = "MAGCARD_SETID";
            MAGCARD_SETID = getContent(dao, name, "UN", "");
            
            name = "MAGCARD_FLAG";
            MAGCARD_FLAG = getContent(dao, name, "UN", "");
            
            name = "MAGCARD_CARDID";
            MAGCARD_CARDID = getContent(dao, name, "UN", "");
            
            name = "MAGCARD_COM";
            MAGCARD_COM = getContent(dao, name, "UN", "");
            
            //休息日转加班相关参数
            name = "REST_TO_OVERTIME";
            REST_TO_OVERTIME = getStatus(dao, name, "UN", "0");
            name = "REST_TO_OVERTIME_CARD";
            REST_TO_OVERTIME_CARD = getContent(dao, name, "UN", "");
            name = "REST_TO_OVERTIME_TIMELEN";
            REST_TO_OVERTIME_TIMELEN = getContent(dao, name, "UN", "");
            name = "REST_TO_OVERTIME_APPLY";
            REST_TO_OVERTIME_APPLY = getContent(dao, name, "UN", "");

            //员工自助确认月汇总数据
            name = "SELF_ACCEPT_MONTH_DATA";
            SELF_ACCEPT_MONTH_DATA = getContent(dao, name, "UN", "1"); 
            
            name = "OFFICELEAVE_ENABLE_LEAVE_OVERTIME";
            OFFICELEAVE_ENABLE_LEAVE_OVERTIME = getContent(dao, name, "UN", "1");
            
            KqParam.PARAM_LOADED = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(conn);
        }
    }

    /**
     * @Title: getContentWithGroup   
     * @Description: 取操作用户管理范围考勤参数content部分，支持集团化   
     * @param @param conn
     * @param @param userView
     * @param @param name
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getContentWithGroup(Connection conn, UserView userView, String name, String defaultValue) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        return getContentWithGroup(conn, orgId, name, defaultValue); 
    }
    
    /**
     * @Title: getContentWithGroup   
     * @Description: 取某单位的参数content部分，支持集团化   
     * @param @param conn
     * @param @param b0110
     * @param @param name
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getContentWithGroup(Connection conn, String b0110, String name, String defaultValue) {
        ContentDAO dao = new ContentDAO(conn);
        return getContent(dao, name, b0110, defaultValue);
    }
    
    /**
     * @Title: getStatusWithGroup   
     * @Description: 取考勤参数status部分，支持集团化   
     * @param @param conn
     * @param @param userView
     * @param @param name
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getStatusWithGroup(Connection conn, UserView userView, String name, String defaultValue) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);

        ContentDAO dao = new ContentDAO(conn);
        return getStatus(dao, name, orgId, defaultValue);
    }
    
    /**
     * @Title: getContent   
     * @Description: 取考勤参数content部分，如本单位没有设置，将查找上级单位，直至UN  
     * @param @param dao
     * @param @param name
     * @param @param b0110
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    public String getContent(ContentDAO dao, String name, String b0110, String defaultValue) {
        return getParamInfo(dao, name, b0110, defaultValue, "content", "");
    }
    /**
     * 获取kq_parameter表中某个指标的内容
     * 注：假期管理获取计算公式内容时添加年份参数，若年份为空则按原来的逻辑查询
     */
    public String getContent(ContentDAO dao, String name, String b0110, String defaultValue, String holidayYear) {
        return getParamInfo(dao, name, b0110, defaultValue, "content", holidayYear);
    }
    
    /**
     * @Title: getStatus   
     * @Description: 取考勤参数status部分，如本单位没有设置，将查找上级单位，直至UN   
     * @param @param dao
     * @param @param name
     * @param @param b0110
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getStatus(ContentDAO dao, String name, String b0110, String defaultValue) {
        return getParamInfo(dao, name, b0110, defaultValue, "status", "");
    }
    /**
     * 获取kq_parameter表中某个指标的内容
     * 注：假期管理获取计算公式内容时添加年份参数，若年份为空则按原来的逻辑查询
     */
    private String getParamInfo(ContentDAO dao, String name, String b0110, String defaultValue,
            String paramField, String holidayYear) {
        String value = defaultValue;
        
        RowSet rs = null;
        try {
            rs = queryParam(dao, name, b0110, holidayYear);
            while (!foundParam(rs, b0110, paramField)) {
                KqUtilsClass.closeDBResource(rs);
                
                b0110 = getParentB0110(dao, b0110);
                rs = queryParam(dao, name, b0110, holidayYear);
            }
            
            if (rs.first()) {
                value = rs.getString(paramField);
                
                if (value == null || "".equals(value.trim()) || "#".equals(value.trim())) {
                    value = defaultValue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return value;
    }

    private RowSet queryParam(ContentDAO dao, String paramName, String b0110, String holidayYear) {
        StringBuffer sql = new StringBuffer();
        ArrayList<String> valueList = new ArrayList<String>();
        sql.append("select content,status from kq_parameter");
        if(StringUtils.isEmpty(holidayYear)) {
            sql.append(" where upper(name)=?");
            sql.append(" and upper(b0110)=?");
            
            valueList.add(paramName.toUpperCase());
            valueList.add(b0110.toUpperCase());
        } else {
            sql.append(" where upper(b0110)=?");
            sql.append(" and upper(name) like ?");
            sql.append(" and upper(name)<=?");
            sql.append(" order by name desc");
            
            valueList.add(b0110.toUpperCase());
            valueList.add(paramName.toUpperCase() + "%");
            valueList.add(paramName.toUpperCase() + "_" + holidayYear);
        }
        
        try {
            return dao.search(sql.toString(), valueList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getParentB0110(ContentDAO dao, String b0110) {
        RowSet rs = null;
        String parentId = "UN";
        try {
            if (b0110.startsWith("UN")) {
                b0110 = b0110.substring(2);
            }

            String orgSql = "SELECT parentid from organization where codeitemid='" + b0110 + "'";
            rs = dao.search(orgSql);
            if (rs.next()) {
                if (!b0110.equalsIgnoreCase(rs.getString("parentid"))) {
                    parentId = "UN" + rs.getString("parentid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return parentId;
    }

    /**
     * @Title: foundParam   
     * @Description: 判断是否取到了参数（已上溯到根节点或当前节点有参数）  
     * @param @param rs
     * @param @param b0110
     * @param @return 
     * @return boolean    
     * @throws
     */
    private boolean foundParam(RowSet rs, String b0110, String paramField) {
        boolean found = false;
        try {
            found = "UN".equals(b0110.toUpperCase()) //到根节点
                    || (rs.next() && (null != rs.getString(paramField) && !"".equals(rs.getString(paramField).trim())));//没取到参数
        } catch (Exception e) {

        }

        return found;
    }

    public boolean setContent(Connection conn, String code, String name, String content, String description) {
        return setContent(conn, code, name, content, description, "1");
    }

    private boolean setContent(Connection conn, String code, String name, String content, String description, String status) {
        boolean isOK = false;

        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select content,status from kq_parameter ");
        sql.append(" where UPPER(name)='" + name.toUpperCase() + "' and b0110='");
        sql.append(code);
        sql.append("'");
        ContentDAO dao = new ContentDAO(conn);
        try {
            content = PubFunc.keyWord_reback(content);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                StringBuffer up = new StringBuffer();
                up.append("update kq_parameter set ");
                up.append("content='" + content + "'");
                up.append(",status='" + status + "'");
                up.append(" where UPPER(name)='" + name.toUpperCase() + "'");
                up.append(" and B0110='" + code + "'");
                dao.update(up.toString());
            } else {
                StringBuffer insert = new StringBuffer();
                insert.append("insert into kq_parameter (name,B0110,content,description,status)");
                insert.append(" values (?,?,?,?,?)");
                ArrayList list = new ArrayList();
                list.add(name);
                list.add(code);
                list.add(content);
                list.add(description);
                list.add(status);
                dao.insert(insert.toString(), list);
            }

            isOK = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return isOK;
    }

    /**
     * @Title: tranDefaultSign   
     * @Description: 转换默认的#号值为"",否则在使用参数的地方还需要再次进行判断。   
     * @param @param paramValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String tranDefaultSign(String paramValue) {
        return (paramValue == null || "#".equals(paramValue.trim())) ? "" : paramValue.trim();
    }
    
    /**
     * @Title: isHoliday   
     * @Description: 某请假类型是否为假期之一（根据用户管理范围取假期类型设置）   
     * @param @param conn
     * @param @param userView
     * @param @param leaveTypeId
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean isHoliday(Connection conn, UserView userView, String leaveTypeId) {
        String holiday_type = getHolidayTypes(conn, userView);
        return leaveTypeIdInHolidayTypes(holiday_type, leaveTypeId, true);
    }
    
    /**
     * @Title: isHoliday   
     * @Description: 某请假类型是否为假期之一（直接取某单位假期类型设置）   
     * @param @param conn
     * @param @param b0110
     * @param @param leaveTypeId
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean isHoliday(Connection conn, String b0110, String leaveTypeId) {
        String holiday_type = getHolidayTypes(conn, b0110);
        return leaveTypeIdInHolidayTypes(holiday_type, leaveTypeId, true);
    }
    
    /*
     * 检查某请假类型是否在假期类型设置串中
     * 前后加","的原因，确保不会发生"01"匹配进“06,06001”的情况
     */
    public boolean leaveTypeIdInHolidayTypes(String holidayTypes, String leaveTypeId, boolean needSwitch) {
        //需要根据system.properties参数进行年假映射转换
        if (needSwitch) {
            leaveTypeId = KqAppInterface.switchTypeIdFromHolidayMap(leaveTypeId);
        }
        return ("," + holidayTypes.toUpperCase() + ",").contains("," + leaveTypeId.toUpperCase() + ",");
    }
    /**
     * 员工自助确认月汇总数据
     * @Title: setSelfAcceptMonthData   
     * @Description:    
     * @param conn
     * @param selfAcceptMonthData
     */
    public void setSelfAcceptMonthData(Connection conn, String selfAcceptMonthData) {
        if (setContent(conn, "UN", "SELF_ACCEPT_MONTH_DATA", selfAcceptMonthData, "员工自助确认月汇总数据")) {
            SELF_ACCEPT_MONTH_DATA = selfAcceptMonthData;
        }
    }

    /**
     * 是否需要员工自助确认月汇总数据
     * @Title: getSelfAcceptMonthData   
     * @Description:    
     * @return 0不需要 1需要
     */
    public String getSelfAcceptMonthData() {
        return SELF_ACCEPT_MONTH_DATA;
    }

    public String getOFFICELEAVE_ENABLE_LEAVE_OVERTIME() {
        return OFFICELEAVE_ENABLE_LEAVE_OVERTIME;
    }

    public void setOFFICELEAVE_ENABLE_LEAVE_OVERTIME(Connection conn, String officeleaveEnableLeaveOvertime) {
        if (setContent(conn, "UN", "OFFICELEAVE_ENABLE_LEAVE_OVERTIME", officeleaveEnableLeaveOvertime, "公出期间允许请假、加班")) {
            OFFICELEAVE_ENABLE_LEAVE_OVERTIME = officeleaveEnableLeaveOvertime;
        }
    }
	
}
