package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.set.TurnOvertime;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveStrutsTrans extends IBusiness {

    public void execute() throws GeneralException {
        boolean isCorrect = true;

        try {
            KqParam kqParam = KqParam.getInstance();

            HashMap hm = (HashMap) this.getFormHM();

            HashMap map = new HashMap();
            //考勤参数页签
            if (this.userView.hasTheFunction("270304")) {
                String cardn = (String) hm.get("kq_cardno");
                if (cardn == null || cardn.length() <= 0) {
                    cardn = "";
                }

                String kq_type = (String) hm.get("kq_type");
                if (kq_type == null || kq_type.length() <= 0) {
                    kq_type = "";
                }

                String g_no = (String) hm.get("kq_g_no");
                if (g_no == null || g_no.length() <= 0) {
                    g_no = "";
                }

                map.put("cardno", cardn);
                map.put("kq_type", kq_type);
                map.put("g_no", g_no);
                
                //标准工时
                String standard_hours = (String) hm.get("standard_hours");
                if (standard_hours == null || standard_hours.trim().length() <= 0) {
                	standard_hours = "8";
                }
                kqParam.setSTANDARD_HOURS(this.frameconn, standard_hours);

                
                //班组指标
                String bzin = (String) hm.get("kq_bzindex");
                if (bzin == null || bzin.length() <= 0) {
                    bzin = "";
                }
                kqParam.setShiftGroupItem(this.frameconn, bzin);

                //组织机构不带岗位
                String kq_orgview_post = (String) hm.get("kq_orgview_post");
                if (kq_orgview_post == null || kq_orgview_post.length() <= 0) {
                    kq_orgview_post = "0";
                }
                kqParam.setKqOrgViewPost(this.frameconn, kq_orgview_post);

                /**首钢考勤参数 考勤日期**/
                String kq_startdate_field = (String) hm.get("kq_startdate_field");
                if (kq_startdate_field == null || kq_startdate_field.length() <= 0)
                    kq_startdate_field = "";
                kqParam.setKqStartDateField(this.frameconn, kq_startdate_field);

                String kq_enddate_field = (String) hm.get("kq_enddate_field");
                if (kq_enddate_field == null || kq_enddate_field.length() <= 0)
                    kq_enddate_field = "";
                kqParam.setKqEndDateField(this.frameconn, kq_enddate_field);

                String dept_changedate_field = (String) hm.get("dept_changedate_field");
                if (dept_changedate_field == null || dept_changedate_field.length() <= 0)
                    dept_changedate_field = "";
                kqParam.setDeptChangeDateField(this.frameconn, dept_changedate_field);

                //调换班组，为首钢网上签到增加
                String bzinbz = (String) hm.get("kq_thbzindex");
                if (bzinbz == null || bzinbz.length() <= 0 || "#".equals(bzinbz)) {
                    bzinbz = "";
                }
                kqParam.setKqDepartment(this.frameconn, bzinbz);

                //归档审批方式
                String pigeonhole_type = (String) hm.get("pigeonhole_type");
                kqParam.setArchiveType(this.frameconn, pigeonhole_type);

                //是否允许修改日明细数据
                String up_dailyregister = (String) hm.get("up_dailyregister");
                kqParam.setUpdateDailyRegister(this.frameconn, up_dailyregister);

                //月考勤审核控制
                String checkControl_status = (String) hm.get("checkControl_status");
                String checkControl_content = (String) hm.get("checkControl_content");
                kqParam.setQ05_control(frameconn, checkControl_content, checkControl_status);
                
                //需要员工确认月汇总数据 0：不需要 1：需要(默认)
                String self_accept_month_data = (String) hm.get("self_accept_month_data");
                kqParam.setSelfAcceptMonthData(frameconn, self_accept_month_data);

                //登录时进行刷卡、请假和公出申请提醒
                String logon_kq_hint = (String) hm.get("logon_kq_hint");
                logon_kq_hint = logon_kq_hint == null || logon_kq_hint.length() <= 0 ? "0" : logon_kq_hint;
                kqParam.setLogon_kq_hint(this.getFrameconn(), logon_kq_hint);
            }

            // 考勤人员库
            if (this.userView.hasTheFunction("270305")) {
                ArrayList mes = (ArrayList) hm.get("messi");
                StringBuffer stbs = new StringBuffer();
                if (mes != null && mes.size() > 0) {
                    for (int n = 0; n < mes.size(); n++) {
                        stbs.append(mes.get(n).toString());
                        stbs.append(",");
                    }
                }

                map.put("nbase", stbs.toString());
            }

            String code = "";
            if (this.userView.hasTheFunction("270304") || this.userView.hasTheFunction("270305")) {
                if (this.userView.isSuper_admin()) {
                    code = "UN";
                } else {
                    ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                    code = "UN" + managePrivCode.getPrivOrgId();
                }

                KqParameter para = new KqParameter(this.userView, code, this.getFrameconn());
                para.setParams(map);
            }

            //人员基本信息参考指标页签
            if (this.userView.hasTheFunction("270306")) {
                ArrayList par_mes = (ArrayList) hm.get("par_mes");
                saveSetKqMain_set(par_mes);
            }

            //假期管理页签
            if (this.userView.hasTheFunction("270307")) {
                //假期类型
                ArrayList holi_mes = null;
                try { 
                    holi_mes = (ArrayList) hm.get("holi_mes");
                } catch (Exception e) {
                    holi_mes = new ArrayList();
                    holi_mes.add((String)hm.get("holi_mes"));
                }
                saveHoliday_type(holi_mes, kqParam);

                //假期扣减规则
                String holiday_minus_rule = SafeCode.keyWord_reback((String) hm.get("holiday_minus_rule"));// 考勤减扣规则
                kqParam.setHolidayMinusRule(this.frameconn, holiday_minus_rule);
            }

            //业务申请页签
            if (this.userView.hasTheFunction("270308")) {
                //节假日有排班算加班 0:不算加班；1：算加班
                String overtime_hol = (String) hm.get("overtime_hol");
                kqParam.setHolidayShiftIsOvertime(this.frameconn, overtime_hol);

                //加班时间不少于X分钟
                String min_overtime = (String) hm.get("min_overtime");
                kqParam.setMin_overtime(this.frameconn, min_overtime);

                //加班最迟登记天数
                String over_status = (String) hm.get("over_status");
                String over_rule = (String) hm.get("over_rule");
                kqParam.setOvertimeRule(this.frameconn, this.userView, over_rule, over_status);

                //考勤期间最大加班限额
                String durationOvertimeMaxLimit = (String) hm.get("DURATION_OVERTIME_MAX_LIMIT");
                kqParam.setDURATION_OVERTIME_MAX_LIMIT(frameconn, durationOvertimeMaxLimit);

                //请假提前规则
                String leave_status = (String) hm.get("leave_status");
                String leave_rule = (String) hm.get("leave_rule");
                kqParam.setLeavetimeRule(this.frameconn, this.userView, leave_rule, leave_status);

                //请假最迟规则
                String leave_rule_late_status = (String) hm.get("leave_rule_late_status");
                String leave_rule_late = (String) hm.get("leave_rule_late");
                kqParam.setLateLeavetimeRule(this.frameconn, this.userView, leave_rule_late, leave_rule_late_status);

                //是否判断申请加班类型与日期相符
                String opinion_overtime_type = (String) hm.get("opinion_overtime_type");
                if (opinion_overtime_type == null || opinion_overtime_type.length() <= 0)
                    opinion_overtime_type = "1";
                kqParam.setOpinionOvertimeType(this.frameconn, opinion_overtime_type);

                //节假日或公休日加班申请是否允许申请多次
                String restOvertimeTimes = (String) hm.get("rest_overtime");
                kqParam.setRestOvertimeTimes(this.frameconn, restOvertimeTimes);

                //已批申请登记数据是否可以删除 0:不删除；1：删除
                String approved_delete = (String) hm.get("approved_delete");
                kqParam.setApprovedDelete(this.frameconn, approved_delete);
                
                //公出期间允许请假加班
                String officeleave_enable_leave_overtime = (String)hm.get("officeleave_enable_leave_overtime");
                kqParam.setOFFICELEAVE_ENABLE_LEAVE_OVERTIME(this.frameconn, officeleave_enable_leave_overtime);
            }

            //刷卡页签
            if (this.userView.hasTheFunction("270309")) {
                //重复刷卡间隔
                String card_interval = (String) hm.get("card_interval");
                kqParam.setCard_interval(this.frameconn, card_interval);

                //进出刷卡分析离岗（刷卡匹配情况）
                String check_inout_match = (String) hm.get("check_inout_match");
                kqParam.setCheck_inout_match(this.frameconn, check_inout_match);

                //离岗最小时长限制（小于等于次数时不计离岗）
                String min_mid_leave_time = (String) hm.get("min_mid_leave_time");
                kqParam.setMinMidLeaveTime(this.frameconn, min_mid_leave_time);

                //中间休息时段有出入刷卡
                String restleave_type = (String) hm.get("restleave_type");
                if (restleave_type == null || restleave_type.length() <= 0)
                    restleave_type = "2";
                kqParam.setRestleave_calctime_type(this.frameconn, restleave_type);

                //弹性班规则
                String flextime_ruler = (String) hm.get("flextime_ruler");
                if (flextime_ruler == null || flextime_ruler.length() <= 0)
                    flextime_ruler = "0";
                kqParam.setFlextimeRuler(this.frameconn, flextime_ruler);

                //补刷卡原因代码类
                String card_causation = (String) this.getFormHM().get("card_causation");
                if (card_causation == null || card_causation.length() <= 0 || "#".equals(card_causation))
                    card_causation = "";
                kqParam.setCardCausation(this.frameconn, card_causation);

                //补刷卡次数
                String repair_card_num = (String) hm.get("repair_card_num");
                String repair_card_status = (String) hm.get("repair_card_status");
                kqParam.setRepairCardNum(this.frameconn, this.userView, repair_card_num, repair_card_status);

                //补刷卡审批关系
                String approve_relation = (String) this.getFormHM().get("approve_relation");//补刷卡审批关系
                if (approve_relation == null || approve_relation.length() <= 0 || "#".equals(approve_relation))
                    approve_relation = "";
                kqParam.setCardWfRelation(this.frameconn, approve_relation);

                //发卡系列参数
                String magcard_setid = (String) hm.get("magcard_setid");//存储卡号的子集
                kqParam.setMagcardSetId(this.frameconn, magcard_setid);

                String magcard_flag = (String) hm.get("magcard_flag");//从磁卡中读取卡号
                kqParam.setMagcardFlag(this.frameconn, magcard_flag);

                String magcard_cardid = (String) hm.get("magcard_cardid");
                kqParam.setMagcardCardId(this.frameconn, magcard_cardid);

                String magcard_com = (String) hm.get("magcard_com");
                kqParam.setMagcardCom(this.frameconn, magcard_com);

                //提前X分钟算早到
                String cardearly = (String) hm.get("cardearly");
                kqParam.setEarlyMinute(this.frameconn, cardearly);
            }

            //统计指标页签
            if (this.userView.hasTheFunction("270302")) {
                /**项目统计 **/
                ArrayList stat_q03 = (ArrayList) hm.get("stat_q03");
                saveSetKQ_Param(stat_q03);
            }

            //网上签到页签
            if (this.userView.hasTheFunction("27030a")) {
                //网上签到是否限制IP
                String net_sign_check_ip = (String) hm.get("net_sign_check_ip");
                if (net_sign_check_ip == null || net_sign_check_ip.length() <= 0)
                    net_sign_check_ip = "1";
                kqParam.setNetSignCheckIP(this.frameconn, net_sign_check_ip);

                //签到签退数据需审批 0：无需审批 1：需要审批
                String net_sign_approve = (String) hm.get("net_sign_approve");
                if (net_sign_approve == null || net_sign_approve.length() <= 0)
                    net_sign_approve = "0";
                kqParam.setNetSignApprove(this.frameconn, net_sign_approve);
            }

            //考勤簿页签
            if (this.userView.hasTheFunction("270303")) {
                ArrayList kqcard_q03 = (ArrayList) hm.get("kqcard_q03");
                saveKqBookItems(kqcard_q03);
            }

            //数据处理
            if (this.userView.hasTheFunction("27030b")) {
                //数据处理模式
                String data_processing = (String) hm.get("data_processing");
                kqParam.setData_processing(this.frameconn, data_processing);

                //启用精简处理方式 0：原处理方式 1：首钢特殊处理方式
                String quick_analyse_mode = (String) hm.get("quick_analyse_mode");
                kqParam.setQuickAnalyseMode(this.frameconn, quick_analyse_mode);
            }

            //加班扩展页签
            if (this.userView.hasTheFunction("27030d")) {
                String turn_enable = (String) this.getFormHM().get("turn_enable"); //启用标识 0 不启用 1 启用
                String turn_charge = (String) this.getFormHM().get("turn_charge"); //0 需要进出匹配 1 有刷卡即加班
                String turn_tlong = (String) this.getFormHM().get("turn_tlong"); //刷卡时长  0 默认时长 1 参考班次时长 2 实际刷卡时长
                String turn_time = (String) this.getFormHM().get("turn_time"); //刷卡时长
                String turn_classid = (String) this.getFormHM().get("turn_classid"); //被选班次
                //		  ArrayList turn_classlist = new ArrayList(); //班次列表
                String turn_appdoc = (String) this.getFormHM().get("turn_appdoc"); //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
                Map parameterSet = new HashMap();
                parameterSet.put("turn_enable", turn_enable);
                parameterSet.put("turn_charge", turn_charge);
                parameterSet.put("turn_tlong", turn_tlong);
                parameterSet.put("turn_time", turn_time);
                parameterSet.put("turn_classid", turn_classid);
                parameterSet.put("turn_appdoc", turn_appdoc);
                TurnOvertime tot = new TurnOvertime();
                if (!tot.saveSet(parameterSet)) {
                    throw new GeneralException("休息日转加班保存出错！加班时长若选择默认时长，请检查时长是否大于24；若选择参考班次时长，请检查是否选择参考班次。");
                }

                String overtimeToOff = (String) this.getFormHM().get("overtimeToOff");
                if (overtimeToOff == null || overtimeToOff.length() <= 0)
                    overtimeToOff = "";

                String vacationToOff = (String) this.getFormHM().get("vacationToOff");
                if (vacationToOff == null || vacationToOff.length() <= 0)
                    vacationToOff = "";
                
                String validityTime = (String) this.getFormHM().get("validityTime");
                if (validityTime == null || validityTime.length() <= 0)
                    validityTime = "";
                
                String overtimeForLeaveCycle = (String) this.getFormHM().get("overtimeForLeaveCycle");
                if (overtimeForLeaveCycle == null || overtimeForLeaveCycle.length() <= 0)
                    overtimeForLeaveCycle = "";
                
                String overtimeForLeaveMaxHour = (String) this.getFormHM().get("overtimeForLeaveMaxHour");
                if (overtimeForLeaveMaxHour == null || overtimeForLeaveMaxHour.length() <= 0)
                    overtimeForLeaveMaxHour = "";
                
                DbWizard dbWizard = new DbWizard(this.getFrameconn());
                if (!dbWizard.isExistTable("Q33", false) && (!"".equals(overtimeToOff) || !"".equals(vacationToOff))) {
                    throw new GeneralException("调休加班明细表不存在，请构库之后再设置调休加班参数！");
                }

                //调休加班
                kqParam.setOVERTIME_FOR_LEAVETIME(this.frameconn, overtimeToOff);

                //调休假类型
                kqParam.setLeaveTimeTypeUsedOverTime(this.frameconn, vacationToOff);

                //调休有效期限
                kqParam.setOVERTIME_FOR_LEAVETIME_LIMIT(this.frameconn, validityTime);
                
                //调休加班有效周期
                kqParam.setOVERTIME_FOR_LEAVETIME_CYCLE(this.frameconn, overtimeForLeaveCycle);
                
                //调休加班限额小时
                kqParam.setOVERTIME_FOR_LEAVETIME_MAX_HOUR(this.frameconn, overtimeForLeaveMaxHour);
            }

            //默认班次 暂时无用
            //String rest_kqclass = (String) hm.get("rest_kqclass");
            //kqParam.setDefault_rest_kqclass(this.frameconn, rest_kqclass);

            //申请比对页签
            if (this.userView.hasTheFunction("27030e")) {
                //业务申请是否与实际刷卡作比对
                String need_busicompare = (String) hm.get("need_busicompare");
                kqParam.setNeed_busicompare(this.frameconn, need_busicompare);

                //刷卡开始时间最早从申请起始时间前X分钟起
                String busi_cardbegin = (String) hm.get("busi_cardbegin");
                kqParam.setBusi_cardbegin(this.frameconn, busi_cardbegin);

                //刷卡结束时间最迟到申请结束时间后X分钟止
                String busi_cardend = (String) hm.get("busi_cardend");
                kqParam.setBusi_cardend(this.frameconn, busi_cardend);

                //申请时长小于刷卡时长超过X分钟计为异常
                String busifact_diff = (String) hm.get("busifact_diff");
                kqParam.setBusifact_diff(this.frameconn, busifact_diff);

                String busi_morethan_fact = (String) hm.get("busi_morethan_fact");
                //申请时长大于刷卡时长超过X分钟计为异常
                kqParam.setBusi_morethan_fact(this.frameconn, busi_morethan_fact);

                /*申请比对*/
                String leave_need_check = (String) hm.get("leave_need_check");
                kqParam.setLEAVE_NEED_CHECK(frameconn, leave_need_check);
                String leave_compare_rule = (String) hm.get("leave_compare_rule");
                String leave_updata_data = (String) hm.get("leave_updata_data");
                kqParam.setLEAVE_COMPARE_RULE(frameconn, leave_compare_rule, leave_updata_data);
                kqParam.setLEAVE_UPDATA_DATA(leave_updata_data);

                String overtime_need_check = (String) hm.get("overtime_need_check");
                kqParam.setOVERTIME_NEED_CHECK(frameconn, overtime_need_check);
                String overtime_compare_rule = (String) hm.get("overtime_compare_rule");
                String overtime_updata_data = (String) hm.get("overtime_updata_data");
                kqParam.setOVERTIME_COMPARE_RULE(frameconn, overtime_compare_rule, overtime_updata_data);
                kqParam.setOVERTIME_UPDATA_DATA(overtime_updata_data);

                String officeleave_need_check = (String) hm.get("officeleave_need_check");
                kqParam.setOFFICELEAVE_NEED_CHECK(frameconn, officeleave_need_check);
                String officeleave_compare_rule = (String) hm.get("officeleave_compare_rule");
                String officeleave_updata_data = (String) hm.get("officeleave_updata_data");
                kqParam.setOFFICELEAVE_COMPARE_RULE(frameconn, officeleave_compare_rule, officeleave_updata_data);
                kqParam.setOFFICELEAVE_UPDATA_DATA(officeleave_updata_data);
            }
            
            isCorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        if (isCorrect) {
            this.getFormHM().put("types", "ok");
        } else {
            this.getFormHM().put("types", "no");
        }
    }

    /**
     * 保存参考指标
     * @param par_mes
     */
    private void saveSetKqMain_set(ArrayList par_mes) throws GeneralException {
        StringBuffer content = new StringBuffer();
        if (par_mes == null || par_mes.size() <= 0)
            content.append("");
        else
            for (int i = 0; i < par_mes.size(); i++) {
                content.append(par_mes.get(i).toString());
                content.append(",");
            }
        KqParam.getInstance().setMainSetFields(this.frameconn, this.userView, content.toString(), "1");
    }

    /**
     * 项目统计 Q03
     * @param par_mes
     * @throws GeneralException
     */
    private void saveSetKQ_Param(ArrayList par_mes) throws GeneralException {
        StringBuffer content = new StringBuffer();
        if (par_mes == null || par_mes.size() <= 0)
            content.append("");
        else
            for (int i = 0; i < par_mes.size(); i++) {
                content.append(par_mes.get(i).toString());
                content.append(",");
            }
        KqParam.getInstance().setQ03StatFields(this.frameconn, content.toString().toUpperCase());
    }

    /**
     * 假期管理的假类别列表
     * @param code
     * @param holi_mes
     */
    private void saveHoliday_type(ArrayList holi_mes, KqParam kqParam) throws GeneralException {
        StringBuffer content = new StringBuffer();
        if (holi_mes == null || holi_mes.size() <= 0)
            content.append("");
        else
            for (int i = 0; i < holi_mes.size(); i++) {
                content.append(holi_mes.get(i).toString());
                content.append(",");
            }

        if (("," + content.toString() + ",").indexOf(",06,") == -1)
            throw new GeneralException(ResourceFactory.getProperty("kq.feast.manage.must.select"));

        kqParam.setHolidayTypes(this.frameconn, this.userView, content.toString());
    }

    /**
     * 首钢考勤薄
     * @param code
     * @param par_mes
     * @throws GeneralException
     */
    private void saveKqBookItems(ArrayList items) throws GeneralException {
        StringBuffer content = new StringBuffer();
        if (items == null || items.size() <= 0)
            content.append("");
        else {
            for (int i = 0; i < items.size(); i++) {
                content.append(items.get(i).toString());
                content.append(",");
            }
        }
        KqParam.getInstance().setKqBookItems(this.frameconn, content.toString().toUpperCase());
    }
}
