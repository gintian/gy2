package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.set.TurnOvertime;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectParameterTrans extends IBusiness {

    private KqDBHelper kqDB;
    public void execute() throws GeneralException {

        try {
            // 给考勤方式中加入暂停考勤
            kqDB = new KqDBHelper(this.getFrameconn());
            kqDB.addKqTypeStopCodeItem();

            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String mess = (String) hm.get("mess");
            StringBuffer stb = new StringBuffer();
            
            ArrayList slist = new ArrayList();
            ArrayList selectedlist = new ArrayList();
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            //考勤方式候选指标列表
            stb.delete(0, stb.length());
            stb.append("SELECT itemid,itemdesc FROM fielditem  WHERE fieldsetid ='A01' AND codesetid='29' AND useflag='1'");
            ArrayList flist = kqDB.getCommonDataListFromDB(stb.toString(), "itemdesc", "itemid");

            //候选字符型指标列表
            stb.delete(0, stb.length());
            stb.append("SELECT itemid,itemdesc FROM fielditem  WHERE fieldsetid ='A01' AND itemtype='A' AND itemid<>'A0101' AND useflag='1' AND (codesetid='0' or codesetid IS NULL)");
            ArrayList listss = kqDB.getCommonDataListFromDB(stb.toString(), "itemdesc", "itemid");

            // 增加班组指标
            stb.delete(0, stb.length());
            stb.append("SELECT itemid,itemdesc FROM fielditem  WHERE fieldsetid ='A01' AND itemtype='A' AND useflag='1'");
            ArrayList bzlist = kqDB.getCommondDataListFromDBWithEmptyItem(stb.toString(), "itemdesc", "itemid");
            
            // 调换班组，为首钢网上考勤增加;
            stb.delete(0, stb.length());
            // 添加职位条件 ---wangzhongjun
            stb.append("SELECT itemid,itemdesc FROM fielditem");
            stb.append(" WHERE fieldsetid ='A01'");
            stb.append(" AND itemid<>'E0122' and itemid<>'E01A1'");
            stb.append(" AND itemtype='A' AND useflag='1'");
            stb.append(" AND (Codesetid='UM' or Codesetid='@K')");
            ArrayList thlist = kqDB.getCommondDataListFromDBWithEmptyItem(stb.toString(), "itemdesc", "itemid");

            String code = "";
            if (this.userView.isSuper_admin()) {
                code = "UN";
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                code = managePrivCode.getPrivOrgId();
            }
            
            KqParameter para = new KqParameter(this.userView, code, this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String two = (String) hashmap.get("nbase");
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String kq_g_no = (String) hashmap.get("g_no");
            
            KqParam kqParam = KqParam.getInstance();
            
            String bzindex = kqParam.getShiftGroupItem();
            
            String kq_orgview_post = kqParam.getKqOrgViewPost();
            
            String thbzindex = kqParam.getKqDepartment(); // 首钢增加调换班组;为网上签到；
            
            stb.delete(0, stb.length());
            stb.append("select * from dbname order by dbid"); // 增加了人员库排序
            this.frowset = dao.search(stb.toString());
            // ArrayList dbaselist=userView.getPrivDbList();
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre").toLowerCase();
                if ((two.toLowerCase()).indexOf(dbpre) != -1)
                    selectedlist.add("1");
                else
                    selectedlist.add("0");
                
                CommonData vo = new CommonData(this.frowset.getString("pre"), this.frowset.getString("dbname"));
                slist.add(vo);
            }
            
            // 化学工程 补刷卡审批关系
            stb.delete(0, stb.length());
            stb.append("select cname,relation_id from t_wf_relation where validflag=1 and actor_type=1");
            ArrayList relationlist = kqDB.getCommondDataListFromDBWithEmptyItem(stb.toString(), "cname", "relation_id");

            this.getFormHM().put("kq_cardno", kq_cardno);
            this.getFormHM().put("kq_g_no", kq_g_no);
            this.getFormHM().put("kq_type", kq_type);
            this.getFormHM().put("kq_bzindex", bzindex);
            this.getFormHM().put("kq_thbzindex", thbzindex);
            this.getFormHM().put("kq_orgview_post", kq_orgview_post);
            this.getFormHM().put("standard_hours", kqParam.getSTANDARD_HOURS()); //标准工时 
            this.getFormHM().put("kq_startdate_field", kqParam.getKqStartDateField()); // 为首钢添加 考勤开始日期
            this.getFormHM().put("kq_enddate_field", kqParam.getKqEndDateField()); // 为首钢添加 考勤结束日期
            this.getFormHM().put("dept_changedate_field", kqParam.getDeptChangeDateField()); // 为首钢添加 部门变动日期

            getKqMainSetParam();
            /*** 假期管理 **/
            /** 统计项目 **/
            getStatQ03();
            /** 首钢考勤薄 **/
            getKqBookItems();
            /*** 首钢考勤日期参数 */
            this.getFormHM().put("kq_datelist", getDateList());
            getHoliday_type();
            Overtime_rule();// 加班登记最迟规则
            getLeavetime_rule();// 请假登记提前规则
            getLeavetime_Laterule();// 请假登记最迟规则
            getRepair_card_num();// 补刷卡次数
            
            String holiday_minus_rule = kqParam.getHolidayMinusRule();// 考勤减扣规则
            String holid = "";
            if (holiday_minus_rule != null) {
                String[] str = holiday_minus_rule.split(";");
                for (int i = 0; i < str.length; i++) {
                    holid += "," + str[i];
                }
                holid = holid.substring(1);
            }
            this.getFormHM().put("holiday_minus_rule", holid);
            
            // 中间休息时段有出入刷卡,则离岗时长应
            String restleave_type = kqParam.getRestleave_calctime_type();
            restleave_type = restleave_type != null && restleave_type.length() > 0 ? restleave_type : "2";
            this.getFormHM().put("restleave_type", restleave_type);
            
            // 弹性班规则
            String flextime_ruler = kqParam.getFlextimeRuler();
            flextime_ruler = flextime_ruler != null && flextime_ruler.length() > 0 ? flextime_ruler : "0";
            this.getFormHM().put("flextime_ruler", flextime_ruler);
            
            String net_sign_check_ip = kqParam.getNetSignCheckIP();// 签到是否限制IP            
            // 0：不绑定
            // 1：绑定（默认）2：有IP绑定，无IP不绑定
            net_sign_check_ip = net_sign_check_ip != null && net_sign_check_ip.length() > 0 ? net_sign_check_ip : "1";
            this.getFormHM().put("net_sign_check_ip", net_sign_check_ip);
            
            String net_sign_approve = kqParam.getNetSignApprove();// 签到签退数据需审批
            // 0：无需审批
            // 1：需要审批
            net_sign_approve = net_sign_approve != null && net_sign_approve.length() > 0 ? net_sign_approve : "0";
            this.getFormHM().put("net_sign_approve", net_sign_approve);

            String logon_kq_hint = kqParam.getLogon_kq_hint();// 登陆考勤提醒
            logon_kq_hint = logon_kq_hint != null && logon_kq_hint.length() > 0 ? logon_kq_hint : "0";
            this.getFormHM().put("logon_kq_hint", logon_kq_hint);

            String rest_kqclass = kqParam.getDefault_rest_kqclass();// 班次
            this.getFormHM().put("rest_kqclass", rest_kqclass);
            
            String opinion_overtime_type = kqParam.getOpinionOvertimeType();
            this.getFormHM().put("opinion_overtime_type", opinion_overtime_type);// 是否判断申请加班类型与申请日期相符
           
            getClassList();// 得到班次列
            
            // 单次进出小于等于X分钟不计离岗时间
            String min_mid_leave_time = kqParam.getMinMidLeaveTime();
            this.getFormHM().put("min_mid_leave_time", min_mid_leave_time);
            
            String card_interval = kqParam.getCard_interval() + "";// 重复刷卡间隔X分钟
            if ("0".equals(card_interval))
                card_interval = "";
            this.getFormHM().put("card_interval", card_interval);
            
            String min_overtime = kqParam.getMin_overtime() + "";
            if ("0".equals(min_overtime))
                min_overtime = "";
            // 加班时间不少于X分钟
            this.getFormHM().put("min_overtime", min_overtime);
            
            String need_busicompare = kqParam.getNeed_busicompare();// 业务申请是否与实际刷卡作比对
            this.getFormHM().put("need_busicompare", need_busicompare);
            
            String busi_cardbegin = kqParam.getBusi_cardbegin() + "";// 刷卡开始时间最早从申请起始时间前X分钟起
            this.getFormHM().put("busi_cardbegin", busi_cardbegin);
            
            String busi_cardend = kqParam.getBusi_cardend() + "";// 刷卡结束时间最迟到申请结束时间后X分钟止：
            this.getFormHM().put("busi_cardend", busi_cardend);
            
            String busifact_diff = kqParam.getBusifact_diff() + "";// 6、申请时长小于刷卡时长超过X分钟计为异常
            // BUSIFACT_DIFF
            this.getFormHM().put("busifact_diff", busifact_diff);
            
            String busi_morethan_fact = kqParam.getBusi_morethan_fact() + "";// 7、申请时长大于刷卡时长超过X分钟计为异常BUSI_MORETHAN_FACT
            this.getFormHM().put("busi_morethan_fact", busi_morethan_fact);
            
            String rest_overtime_time = kqParam.getRestOvertimeTimes();
            this.getFormHM().put("rest_overtime_time", rest_overtime_time);// //节假日或公休日加班申请是否允许申请多次
            
            String officeleave_enable_leave_overtime = kqParam.getOFFICELEAVE_ENABLE_LEAVE_OVERTIME();
            this.getFormHM().put("officeleave_enable_leave_overtime", officeleave_enable_leave_overtime);// 公出期间允许请假、加班
        
            String card_causation = kqParam.getCardCausation();
            this.getFormHM().put("card_causation", card_causation);// //补刷卡原因代码项
            
            String approve_relation = kqParam.getCardWfRelation();
            this.getFormHM().put("approve_relation", approve_relation);
            
            String pigeonhole_type = kqParam.getArchiveType();
            pigeonhole_type = pigeonhole_type != null && pigeonhole_type.length() > 0 ? pigeonhole_type : "0";
            this.getFormHM().put("pigeonhole_type", pigeonhole_type);// 归档方式是否审批
            
            String up_dailyregister = kqParam.getUpdateDailyRegister();
            up_dailyregister = up_dailyregister != null && up_dailyregister.length() > 0 ? up_dailyregister : "0";
            this.getFormHM().put("up_dailyregister", up_dailyregister);
            
            String magcard_setid = kqParam.getMagcardSetId();
            this.getFormHM().put("magcard_setid", magcard_setid);
            
            String magcard_flag = kqParam.getMagcardFlag();
            this.getFormHM().put("magcard_flag", magcard_flag);
            
            String magcard_cardid = kqParam.getMagcardCardId();
            this.getFormHM().put("magcard_cardid", magcard_cardid);
            
            String magcard_com = kqParam.getMagcardCom();// com口
            this.getFormHM().put("magcard_com", magcard_com);
            
            String cardearly = kqParam.getEarlyMinute();// 提前早到多少分钟算作早到
            this.getFormHM().put("cardearly", cardearly);
            
            String check_inout_match = kqParam.getCheck_inout_match();// 刷卡匹配情况
            check_inout_match = check_inout_match != null && check_inout_match.length() > 0 ? check_inout_match : "0";
            
            this.getFormHM().put("check_inout_match", check_inout_match);
            
            String data_processing = kqParam.getData_processing(); // 数据处理模式
            // 0:分用户处理
            // 1:集中处理
            data_processing = data_processing != null && data_processing.length() > 0 ? data_processing : "0";
            this.getFormHM().put("data_processing", data_processing);
            
            String quick_analyse_mode = kqParam.getQuickAnalyseMode();// 数据处理精简方式
            // 0：原处理方式
            // 1：首钢特殊处理方式
            quick_analyse_mode = quick_analyse_mode != null && quick_analyse_mode.length() > 0 ? quick_analyse_mode : "0";
            this.getFormHM().put("quick_analyse_mode", quick_analyse_mode);
            
            String overtime_hol = kqParam.getHolidayShiftIsOvertime();// 节假日有排班算作加班;0:不算加班；1：算加班            
            overtime_hol = overtime_hol != null && overtime_hol.length() > 0 ? overtime_hol : "0";
            this.getFormHM().put("overtime_hol", overtime_hol);
            
            // 已批申请登记数据是否可以删除;0:不删除；1：删除
            String approved_delete = kqParam.getApprovedDelete();
            approved_delete = approved_delete != null && approved_delete.length() > 0 ? approved_delete : "1";
            this.getFormHM().put("approved_delete", approved_delete);
            
            String checkControl_status = kqParam.getQ05ControlStatus();
            checkControl_status = checkControl_status != null && checkControl_status.length() > 0 ? checkControl_status : "0";
            this.getFormHM().put("checkControl_status", checkControl_status);
            
            String self_accept_month_data = kqParam.getSelfAcceptMonthData();
            self_accept_month_data = self_accept_month_data != null && self_accept_month_data.length() > 0 ? self_accept_month_data : "1";
            this.getFormHM().put("self_accept_month_data", self_accept_month_data);
            
            String checkControl_content = kqParam.getQ05_control();
            checkControl_content = checkControl_content != null && checkControl_content.length() > 0 ? checkControl_content : "1";
            this.getFormHM().put("checkControl_content", checkControl_content);
        
            if (mess == null)
                this.getFormHM().put("sige", "1");
            else
                this.getFormHM().put("sige", mess);
            
            this.getFormHM().put("nlist", listss);
            this.getFormHM().put("tlist", flist);
            this.getFormHM().put("selist", selectedlist);
            this.getFormHM().put("slist", slist);
            this.getFormHM().put("setList", getPerSetList());
            this.getFormHM().put("cardlist", searchcardlist());
            this.getFormHM().put("relationlist", relationlist);
    
            this.getFormHM().put("templateList", templateTable());// 申请模板
            this.getFormHM().put("bzlist", bzlist); // 班组
            this.getFormHM().put("thlist", thlist); // 首钢调换班组,为网上签到新增
            
            ArrayList sync_list = new ArrayList();
            CommonData da = new CommonData();
            da.setDataName("SQLSERVER");
            da.setDataValue("SQLSERVER");
            sync_list.add(da);
            da = new CommonData();
            da.setDataName("ORACLE");
            da.setDataValue("ORACLE");
            sync_list.add(da);
            this.getFormHM().put("sync_list", sync_list);
    
            /**
             * 休息日刷卡转加班
             */
            TurnOvertime tot = new TurnOvertime();
            String turn_enable = tot.getEnable();
            // 启用标识 0 不启用 1 启用
            String turn_charge = tot.getCharge(); // 0 需要进出匹配 1 有刷卡即加班
            String turn_tlong = tot.getTlong(); // 刷卡时长 2 默认时长 1 参考班次时长 0 实际刷卡时长
            String turn_time = tot.getTime(); // 刷卡时长
            String turn_classid = tot.getClassid(); // 被选班次
            String turn_appdoc = tot.getAppdoc();
            this.getFormHM().put("turn_enable", turn_enable);
            this.getFormHM().put("turn_charge", turn_charge);
            this.getFormHM().put("turn_tlong", turn_tlong);
            this.getFormHM().put("turn_time", turn_time);
            this.getFormHM().put("turn_classid", turn_classid);
            this.getFormHM().put("turn_appdoc", turn_appdoc);
            this.getFormHM().put("turn_classlist", this.getFormHM().get("class_list"));
            
            /**
             * 调休加班
             */
            String overtimeToOff = kqParam.getOVERTIME_FOR_LEAVETIME();// 可用来调休的加班
            this.getFormHM().put("overtimeToOff", overtimeToOff);
            getVacationToOff(overtimeToOff);
    
            SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);// 调休假列表
            ArrayList appTypeList = searchAllApp.getTableList("Q15", this.getFrameconn());
            for (int i = 0; i < appTypeList.size(); i++) {
                CommonData cd = (CommonData) appTypeList.get(i);
                if ("06".equals(cd.getDataValue())) {
                    appTypeList.remove(i);
                }
            }
            this.getFormHM().put("vacationTypeList", appTypeList);
            String vacationToOff = kqParam.getLEAVETIME_TYPE_USED_OVERTIME();
            this.getFormHM().put("vacationToOff", vacationToOff);
    
            String validityTime = kqParam.getOVERTIME_FOR_LEAVETIME_LIMIT();// 调休有效期限
            validityTime = validityTime != null && validityTime.length() > 0 ? validityTime : "180";
            this.getFormHM().put("validityTime", validityTime);
            
            String overtimeForLeaveCycle = kqParam.getOVERTIME_FOR_LEAVETIME_CYCLE();// 调休有效周期
            this.getFormHM().put("overtimeForLeaveCycle", overtimeForLeaveCycle);
            
            ArrayList<CommonData> overtimeForLeaveCycleList = new ArrayList<CommonData>();
            CommonData cd = new CommonData();
            cd.setDataName("按年");
            cd.setDataValue("1");
            overtimeForLeaveCycleList.add(cd);
            cd = new CommonData();
            cd.setDataName("按半年");
            cd.setDataValue("2");
            overtimeForLeaveCycleList.add(cd);
            cd = new CommonData();
            cd.setDataName("按季度");
            cd.setDataValue("3");
            overtimeForLeaveCycleList.add(cd);
            cd = new CommonData();
            cd.setDataName("按月");
            cd.setDataValue("4");
            overtimeForLeaveCycleList.add(cd);
            cd = new CommonData();
            cd.setDataName("按天");
            cd.setDataValue("0");
            overtimeForLeaveCycleList.add(cd);
            this.getFormHM().put("overtimeForLeaveCycleList", overtimeForLeaveCycleList);
            
            String overtimeForLeaveMaxHour = kqParam.getOVERTIME_FOR_LEAVETIME_MAX_HOUR();// 调休有效限额小时
            overtimeForLeaveMaxHour = overtimeForLeaveMaxHour != null && overtimeForLeaveMaxHour.length() > 0 ? overtimeForLeaveMaxHour : "0";
            this.getFormHM().put("overtimeForLeaveMaxHour", overtimeForLeaveMaxHour);
    
            /* 申请比对 */
            String leave_need_check = kqParam.getLEAVE_NEED_CHECK();
            this.getFormHM().put("leave_need_check", leave_need_check);
            
            String leave_compare_rule = kqParam.getLEAVE_COMPARE_RULE();
            this.getFormHM().put("leave_compare_rule", leave_compare_rule);
            
            String leave_updata_data = kqParam.getLEAVE_UPDATA_DATA();
            this.getFormHM().put("leave_updata_data", leave_updata_data);
    
            String overtime_need_check = kqParam.getOVERTIME_NEED_CHECK();
            overtime_need_check = overtime_need_check != null && overtime_need_check.length() > 0 ? overtime_need_check : "0";
            this.getFormHM().put("overtime_need_check", overtime_need_check);
            
            String overtime_compare_rule = kqParam.getOVERTIME_COMPARE_RULE();
            this.getFormHM().put("overtime_compare_rule", overtime_compare_rule);
            
            String overtime_updata_data = kqParam.getOVERTIME_UPDATA_DATA();
            this.getFormHM().put("overtime_updata_data", overtime_updata_data);
    
            String officeleave_need_check = kqParam.getOFFICELEAVE_NEED_CHECK();
            officeleave_need_check = officeleave_need_check != null && officeleave_need_check.length() > 0 ? officeleave_need_check : "0";
            this.getFormHM().put("officeleave_need_check", officeleave_need_check);
            
            String officeleave_compare_rule = kqParam.getOFFICELEAVE_COMPARE_RULE();
            this.getFormHM().put("officeleave_compare_rule", officeleave_compare_rule);
            
            String officeleave_updata_data = kqParam.getOFFICELEAVE_UPDATA_DATA();
            this.getFormHM().put("officeleave_updata_data", officeleave_updata_data);
            
            //考勤期间加班最大限额
            String durationOvertimeMaxLimit = kqParam.getDURATION_OVERTIME_MAX_LIMIT();
            this.getFormHM().put("DURATION_OVERTIME_MAX_LIMIT", durationOvertimeMaxLimit);
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
    }

    /**
     * 考勤参数
     * 
     */
    private void getKqMainSetParam() throws GeneralException {
        ArrayList par_select_list = new ArrayList();
        ArrayList list = new ArrayList();
        try {
            String content = KqParam.getInstance().getMainSetFields(this.frameconn, this.userView);
            ArrayList fieldlist = this.userView.getPrivFieldList("A01", Constant.USED_FIELD_SET);
            CommonData vo = null;
            
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldlist.get(i);
                if (!"a0100".equals(fielditem.getItemid()) && !"a0101".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid()) && !"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid())) {
                    // 结构参数，参考指标，不应该将备注型指标列出来
                    if (!"M".equalsIgnoreCase(fielditem.getItemtype())) {
                        vo = new CommonData();
                        vo.setDataName(fielditem.getItemdesc());
                        vo.setDataValue(fielditem.getItemid());
                        list.add(vo);
                        if (content.toLowerCase().indexOf(fielditem.getItemid().toLowerCase()) != -1) {
                            par_select_list.add("1");
                        } else {
                            par_select_list.add("0");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.getFormHM().put("par_list", list);
        this.getFormHM().put("par_select_list", par_select_list);
    }

    /**
     * 统计项目
     */
    private void getStatQ03() throws GeneralException {
        ArrayList list = new ArrayList();
        CommonData vo = null;
        ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        String content = KqParam.getInstance().getQ03StatFields();
        ArrayList par_select_list = new ArrayList();

        try {
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldlist.get(i);
                if (!"nbase".equals(fielditem.getItemid()) && !"a0100".equals(fielditem.getItemid()) 
                        && !"q03z0".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid()) 
                        && !"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid()) 
                        && !"a0101".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) 
                        && !"q03z0".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()) 
                        && "N".equalsIgnoreCase(fielditem.getItemtype()) && !"i9999".equalsIgnoreCase(fielditem.getItemid())) {
                    vo = new CommonData();
                    vo.setDataName(fielditem.getItemdesc());
                    vo.setDataValue(fielditem.getItemid());
                    list.add(vo);

                    if (content.toLowerCase().indexOf(fielditem.getItemid().toLowerCase()) != -1) {
                        par_select_list.add("1");
                    } else {
                        par_select_list.add("0");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getFormHM().put("statq03_list", list);
        this.getFormHM().put("par_statq03_list", par_select_list);
    }

    /**
     * 首钢考勤薄月汇总指标
     */
    private void getKqBookItems(){
        ArrayList list = new ArrayList();
        CommonData vo = null;
        
        ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        String content = KqParam.getInstance().getKqBookItems();
        ArrayList par_select_list = new ArrayList();
        
        try {
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldlist.get(i);
                // 原来只展现为N类型的数据，现在改为都展现将操作用户与操作时间排出
                if (!"nbase".equals(fielditem.getItemid()) && !"a0100".equals(fielditem.getItemid())
                        && !"q03z0".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid()) 
                        && !"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid()) 
                        && !"a0101".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) 
                        && !"q03z0".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid())
                        && !"modusername".equals(fielditem.getItemid()) && !"modtime".equals(fielditem.getItemid()) 
                        && !"i9999".equalsIgnoreCase(fielditem.getItemid())) {
                    vo = new CommonData();
                    vo.setDataName(fielditem.getItemdesc());
                    vo.setDataValue(fielditem.getItemid());
                    list.add(vo);

                    if (content.toLowerCase().indexOf(fielditem.getItemid().toLowerCase()) != -1) {
                        par_select_list.add("1");
                    } else {
                        par_select_list.add("0");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getFormHM().put("timecard03_list", list);
        this.getFormHM().put("kq_timecard03_list", par_select_list);
    }

    /**
     * 假期管理
     * 
     * @param code
     */
    private void getHoliday_type() {
        ArrayList list = new ArrayList();
        ArrayList holi_select_list = new ArrayList();
        try {
            CommonData vo = null;
            
            String content = KqParam.getInstance().getHolidayTypes(this.frameconn, userView);
            
            StringBuffer sql = new StringBuffer();
            String codeitemid = "";
            sql.append("select codeitemid,codeitemdesc from codeitem");
            sql.append(" where codesetid='27' and parentid like '0%' and codeitemid<>parentid");
            // 33177 linbz 假期管理的假别排序问题
            sql.append(" order by a0000,codeitemid ");

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                vo = new CommonData();
                codeitemid = this.frowset.getString("codeitemid");
                if (codeitemid == null || codeitemid.length() <= 0) {
                    codeitemid = "";
                    continue;
                }

                vo.setDataName(this.frowset.getString("codeitemdesc"));
                vo.setDataValue(codeitemid);
                list.add(vo);

                if (KqParam.getInstance().leaveTypeIdInHolidayTypes(content, codeitemid, false)) {
                    holi_select_list.add("1");
                } else {
                    holi_select_list.add("0");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getFormHM().put("holi_select_list", holi_select_list);
        this.getFormHM().put("holi_list", list);
    }

    /**
     * 加班登记最迟规则
     * 
     * @param code
     */
    private void Overtime_rule() {
        String content = KqParam.getInstance().getOvertimeRule(this.frameconn, this.userView);
        String status = KqParam.getInstance().getOvertimeRuleStatus(this.frameconn, this.userView);
        this.getFormHM().put("over_rule", content);
        this.getFormHM().put("over_status", status);
    }

    /**
     * 请假登记提前规则
     * 
     */
    private void getLeavetime_rule() {
        String content = KqParam.getInstance().getLeavetimeRule(this.frameconn, this.userView);
        String status = KqParam.getInstance().getLeavetimeRuleStatus(this.frameconn, this.userView);
        this.getFormHM().put("leave_rule", content);
        this.getFormHM().put("leave_status", status);
    }

    /**
     * 请假登记最迟规则 Late_Leavetime_rule
     */
    private void getLeavetime_Laterule() {
        String content = KqParam.getInstance().getLateLeavetimeRule(this.frameconn, this.userView);
        String status = KqParam.getInstance().getLateLeavetimeRuleStatus(this.frameconn, this.userView);
        this.getFormHM().put("leave_rule_late", content);
        this.getFormHM().put("leave_rule_late_status", status);
    }

    /**
     * 补刷卡次数 Late_Leavetime_rule
     */
    private void getRepair_card_num() {
        String content = KqParam.getInstance().getRepairCardNum(this.frameconn, this.userView);
        String status = KqParam.getInstance().getRepairCardNumStatus(this.frameconn, this.userView);
        this.getFormHM().put("repair_card_num", content);
        this.getFormHM().put("repair_card_status", status);
    }

    private void getClassList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_class where class_id<>'0' order by displayorder");
        
        ArrayList list = null;
        try {
            list = kqDB.getCommondDataListFromDBWithEmptyItem(sql.toString(), "name", "class_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getFormHM().put("class_list", list);
    }

    private ArrayList getPerSetList() {
        ArrayList list = null;
        
        String sql = "select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag ='1' ORDER BY displayorder";
        try {
            list = kqDB.getCommondDataListFromDBWithEmptyItem(sql, "customdesc", "fieldsetid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    private ArrayList searchcardlist() throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer buf = new StringBuffer();
        ArrayList emplist = new ArrayList();
        RowSet rset = null;
        try {
            CommonData aCommondData = new CommonData();
            aCommondData.setDataName("请选择...");
            aCommondData.setDataValue("#");
            emplist.add(0, aCommondData);
            
            buf.append("select tabid,name,flaga from rname order by tabid");
            rset = dao.search(buf.toString());
            while (rset.next()) {
                String tabid = rset.getString("tabid");
                /** A 人员，B单位,C职位 */
                String flaga = rset.getString("flaga");
                if (this.userView.isHaveResource(IResourceConstant.CARD, tabid)) {
                    CommonData data = new CommonData();
                    data.setDataValue(tabid);
                    data.setDataName(rset.getString("name"));
                    if ("A".equalsIgnoreCase(flaga)) {
                        emplist.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
        return emplist;
    }

    /**
     * 自助申请word打印模板参数
     * 
     * @return
     */
    private ArrayList templateTable() {
        StringBuffer sql = new StringBuffer();
        sql.append("select description,name from kq_parameter");
        sql.append(" where b0110='UN' ");
        sql.append(" and (name='Kq_template_Q11' or name='Kq_template_Q13' or name='Kq_template_Q15')");// 加班，公出，请假

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList templateList = new ArrayList();
        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo("kq_parameter");
                vo.setString("name", this.frowset.getString("name"));
                vo.setString("description", this.frowset.getString("description"));
                templateList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return templateList;
    }

    /**
     * 考勤日期参数
     * 
     * @author: LiWeichao
     * @return 人员信息集指标
     */
    private ArrayList getDateList() throws GeneralException {
        // 郑文龙-----------------日期类型代码字段排序
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT displayorder,displayid,itemid,itemdesc,itemtype");
        sql.append(" FROM fielditem A left join fieldset B");
        sql.append(" ON A.fieldsetid=B.fieldsetid");
        sql.append(" WHERE A.itemtype='D'");
        sql.append(" AND A.useflag='1'");
        sql.append(" AND A.itemdesc<>'年月标识'");
        sql.append(" AND B.fieldsetid like 'A%'");
        sql.append(" ORDER BY displayorder,displayid");

        ArrayList list = null;
        try {
            list = kqDB.getCommondDataListFromDBWithEmptyItem(sql.toString(), "itemdesc", "itemid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        // 郑文龙-----------------日期类型代码字段排序
    }

    /**
     * 可以用来调休的加班
     * 
     */
    private void getVacationToOff(String overTimeForLeaves) {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        ArrayList overtimeSelList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        
        try {
            sql.append("SELECT item_id, item_name FROM kq_item  where item_id LIKE '1%' ORDER BY item_id");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                if (rs.getString("item_id").length() > 1) {
                    CommonData datavo = new CommonData(rs.getString("item_id"), rs.getString("item_name"));
                    list.add(datavo);
                }
                
                if (overTimeForLeaves != null && (("," + overTimeForLeaves + ",").contains("," + rs.getString("item_id") + ","))) {
                    overtimeSelList.add("1");
                } else {
                    overtimeSelList.add("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        this.getFormHM().put("vacation_select_list", overtimeSelList);
        this.getFormHM().put("vacation_list", list);
    }
}
