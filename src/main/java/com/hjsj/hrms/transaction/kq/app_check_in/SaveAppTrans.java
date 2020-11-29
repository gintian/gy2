/*
 * Created on 2006-1-10
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 审批
 * @author wxh
 * 
 */
public class SaveAppTrans extends IBusiness {

    private String rest_overtime_time = "0";

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            this.rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();
            String sub_flag = (String) hm.get("sub_flag");
            String flag = (String) this.getFormHM().get("flag");
            String mess = (String) this.getFormHM().get("mess");
            String dbpre = (String) this.getFormHM().get("dbpre");
            String table = (String) this.getFormHM().get("table");
            String shenH = (String) this.getFormHM().get("mess2"); // Q1511
            String shenP = (String) this.getFormHM().get("mess1"); // Q1515
            ArrayList viewlist = (ArrayList) this.getFormHM().get("viewlist");
            /** 判断时间是否为空以及申请日期是否在考勤期间内 */
            String kq_year = "";
            String kq_duration = "";

            java.util.Date kq_start = null;
            java.util.Date kq_end = null;

            String start = "";
            String end = "";
            String ta = table.toLowerCase();
            String app_class_id = "";
            if (sub_flag != null && "02".equals(sub_flag))
                sub_flag = "02";
            else if (sub_flag != null && "08".equals(sub_flag))
                sub_flag = "08";
            else if (sub_flag != null && "03".equals(sub_flag))
                sub_flag = "03";
            else
                sub_flag = "02";

            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                /** 不分析是否在考勤期内,chenmengqing added at 20070214 */
                if (field.getItemid().equals(ta + "z1") && "D".equals(field.getItemtype())) {
                        kq_start = OperateDate.strToDate(field.getValue().toString(),	"yyyy-MM-dd HH:mm");
                        start = field.getValue().toString();
                    
                }
                if (field.getItemid().equals(ta + "z3") && "D".equals(field.getItemtype())) {
                	 kq_end = OperateDate.strToDate(field.getValue().toString(),	"yyyy-MM-dd HH:mm");
                	 end = field.getValue().toString();
                    
                }
                if ("q11".equalsIgnoreCase(ta) && "q1104".equalsIgnoreCase(field.getItemid()))
                    app_class_id = field.getValue().toString();
            }

            /** 判断开始日期是否在结束日期之前 */
            if (kq_start.after(kq_end))
            {
            	throw new GeneralException("申请日期错误,起始日期大于或等于结束日期！");
             //   throw new GeneralException("", ResourceFactory.getProperty("error.kq.wrongrequence"));
            } else {
            	kq_start = OperateDate.strToDate(kq_start.toString(),	"yyyy-MM-dd");
            	kq_end = OperateDate.strToDate(kq_end.toString(),	"yyyy-MM-dd");
			}

            if ("1".equals(flag)) {
                /**
                 * 新增加班申请
                 */
                ArrayList infolist = (ArrayList) this.getFormHM().get("infolist");
                String nbase = (String) this.getFormHM().get("dbpre");
                String a0100 = "";
                String b0110 = "";
                String e0122 = "";
                String a0101 = "";
                String e01a1 = "";
                ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
                boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();
                AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
                
                //this.cat.error("考勤模块新增申请记录开始：" + this.userView.getUserName());
                for (int i = 0; i < infolist.size(); i++) {
                    LazyDynaBean selectrec = (LazyDynaBean) infolist.get(i);
                    a0100 = (String) selectrec.get("a0100");
                    b0110 = (String) selectrec.get("b0110");
                    e0122 = (String) selectrec.get("e0122");
                    a0101 = (String) selectrec.get("a0101");
                    e01a1 = (String) selectrec.get("e01a1");

                    annualApply.checkAppInSealDuration(kq_start);

                    if (is_overtime_op) {
                        //检查公休日加班是否申请在公休日
                        if (KqAppInterface.isRestOvertime(mess)) {
                            if (!validateAppOper.is_Rest(kq_start, kq_end, a0100, nbase, app_class_id))
                                throw new GeneralException("", a0101 + "," + validateAppOper.getNo_Rest_mess(), "", "");
                        } else if (KqAppInterface.isFeastOvertime(mess)) { 
                            //检查节假日加班是否申请在节假日
                            if (!validateAppOper.is_Feast(kq_start, kq_end, b0110, app_class_id))
                                throw new GeneralException("", a0101 + "," + ResourceFactory.getProperty("error.kq.nfeast"), "", "");
                        }
                    }

                    annualApply.isRepeatedAllAppType(table, nbase, a0100, a0101, start, end, this.getFrameconn(), "", "");

                    boolean isCorrect = true;
                    /***** 不同的申请进行不同的交验，后保存 ****/
                    insertDAO(table, dbpre, a0100, b0110, e0122, a0101, e01a1, mess, viewlist, isCorrect, sub_flag, shenH, shenP);                    
                }
                //this.cat.error("考勤模块新增申请记录结束：" + this.userView.getUserName());
                if (kq_year != null && kq_duration != null) {
                    this.getFormHM().put("kq_year", kq_year);
                }
            } else if ("0".equals(flag)) {
                /***
                 * 根据审批标志，=1批准，=2驳回
                 */
                String sp = (String) this.getFormHM().get("sp");
                if (sp == null || "".equalsIgnoreCase(sp))
                    sp = "0";
                //this.cat.error("考勤模块更新申请记录开始：" + this.userView.getUserName());
                boolean isCorrect = update(table, mess, viewlist, sp, shenH, shenP);
                //this.cat.error("考勤模块更新申请记录结束：" + this.userView.getUserName());
                
                if (isCorrect && "1".equals(sp))
                    this.getFormHM().put("spFlag", "批准成功！");
                else if (!isCorrect && "1".equals(sp))
                    this.getFormHM().put("spFlag", "批准失败！");
                else if (isCorrect && "2".equals(sp))
                    this.getFormHM().put("spFlag", "驳回成功！");
                else if (!isCorrect && "2".equals(sp))
                    this.getFormHM().put("spFlag", "驳回失败！");
                else if (isCorrect && "3".equals(sp))
                    this.getFormHM().put("spFlag", "审核成功！");
                else if (!isCorrect && "3".equals(sp))
                    this.getFormHM().put("spFlag", "审核失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 员工->考勤员（或部门经理）签批或者考勤员直接录入
     * 
     * @param table
     * @param man
     * @param viewlist
     * @param sp
     *            =1 批准 =2驳回
     * @throws GeneralException
     */
    private boolean update(String table, String man, ArrayList viewlist, String sp, String shenH, String shenP)
            throws GeneralException {
        String insertid = "";
        String insertname = table + "01";
        boolean isCorrect = true;
        for (int i = 0; i < viewlist.size(); i++) {
            FieldItem field = (FieldItem) viewlist.get(i);
            if (field.getItemid().equalsIgnoreCase(insertname)) {
                insertid = field.getValue();
                break;
            }
        }

        if (insertid == null || insertid.length() <= 0)
            return true;

        String ta = table.toLowerCase();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());

        try {
            RecordVo vo = new RecordVo(table);
            vo.setString(ta + "01", insertid);
            vo = dao.findByPrimaryKey(vo);
            insertname = ta + "01";
            java.util.Date kq_start = null;
            java.util.Date kq_end = null;
            String start = "";
            String end = "";
            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                if (field.getItemid().equals(insertname) 
                        || "q1517".equalsIgnoreCase(field.getItemid())
                        || "q1519".equalsIgnoreCase(field.getItemid()))
                    continue;

                if ("N".equals(field.getItemtype())) {
                    if (field.getValue() != null && field.getValue().length() > 0) {
                        if (field.getItemid().equalsIgnoreCase(ta + "ld"))
                            continue;
                        
                        if (field.getItemid().equals(ta + "04")) {
                        	//xiexd 2014.9.23 转换字符
                            if (!"#".equalsIgnoreCase(PubFunc.keyWord_reback(field.getValue()).toString()))
                                vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                        } else
                            vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                    }
                } else if ("D".equals(field.getItemtype())
                        && (field.getItemid().equals(ta + "z1") || field.getItemid().equals(ta + "z3"))) {

                    java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                    if (field.getItemid().equals(ta + "z1")) {
                        start = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
                        kq_start = dd;
                    }
                    if (field.getItemid().equals(ta + "z3")) {
                        end = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
                        kq_end = dd;
                    }
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "05"))) {
                    java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "z7"))) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strDate = sdf.format(new java.util.Date());
                    vo.setDate(field.getItemid().toLowerCase(), DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm"));
                } else if ("D".equals(field.getItemtype())) {
                    String da = field.getValue();
                    da = da.replace(".", "-");
                    if (da != null && da.length() > 0) {
                        int len = field.getItemlength();
                        java.util.Date dat = new java.util.Date();
                        if (len == 4) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            dat = sdf.parse(da + "-01-01");
                        } else if (len == 7) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            dat = sdf.parse(da + "-01");
                        } else if (len == 10) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            dat = sdf.parse(da);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            dat = sdf.parse(da);
                        }
                        vo.setDate(field.getItemid().toLowerCase(), dat);
                    }
                } else {
                    if (field.getItemid().equals(ta + "03")) {
                        vo.setString(field.getItemid(), man);

                    } else if (field.getItemid().equals(ta + "09") && "3".equalsIgnoreCase(sp)) {
                        //if (field.getValue() == null || field.getValue().length() <= 0)
                            vo.setString(ta + "09", this.userView.getUserFullName());
                        //else
                        //    vo.setString(ta + "09", field.getValue());
                    } else if (field.getItemid().equals(ta + "13") && "1".equalsIgnoreCase(sp)) {
                        //if (field.getValue() == null || field.getValue().length() <= 0)
                            vo.setString(ta + "13", this.userView.getUserFullName());
                        //else
                        //    vo.setString(ta + "13", field.getValue());
                    } else if (field.getItemid().equals(ta + "13") && "2".equalsIgnoreCase(sp)) {
                        //if (field.getValue() == null || field.getValue().length() <= 0)
                            vo.setString(ta + "13", this.userView.getUserFullName());
                        //else
                        //    vo.setString(ta + "13", field.getValue());
                    } else if (field.getItemid().equals(ta + "11"))// 审核只能录入审核信息
                    {
                        // if(sp.equalsIgnoreCase("3")||sp.equalsIgnoreCase("2"))
                        // vo.setString(ta+"11",field.getValue());
                        if ("0".equals(field.getCodesetid())) {
                            // if(sp.equalsIgnoreCase("1")||sp.equalsIgnoreCase("2"))
                            // ora库有可能的有可能是3 审核
                            if ("1".equalsIgnoreCase(sp) || "2".equalsIgnoreCase(sp) || "3".equalsIgnoreCase(sp))
                                vo.setString(ta + "11", field.getValue());
                        } else {
                            vo.setString(field.getItemid(), shenH);
                        }
                    } else if (field.getItemid().equals(ta + "15")) {
                        // //审批只能录入审批信息，但是因为目前不知道驳回是审批人还是审核人，所以就放到审批人意见里面
                        if ("0".equals(field.getCodesetid())) {
                            // if(sp.equalsIgnoreCase("1")||sp.equalsIgnoreCase("2"))
                            // ora库有可能的有可能是3 审核
                            if ("1".equalsIgnoreCase(sp) || "2".equalsIgnoreCase(sp) || "3".equalsIgnoreCase(sp))
                                vo.setString(ta + "15", field.getValue());
                        } else {
                            vo.setString(field.getItemid(), shenP);
                        }
                    } else {
                        vo.setString(field.getItemid().toLowerCase(), field.getValue());
                    }
                }
            }

            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
            boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();
            if (!annualApply.isSessionSearl(kq_start, kq_end)) {
                isCorrect = false;
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");
            }

            if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end)) {
                isCorrect = false;
                throw new GeneralException(vo.getString("a0101") + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
            }

            if ("1".equalsIgnoreCase(sp)) {
                annualApply.checkAppInSealDuration(kq_start);

                if (is_overtime_op) {
                    if (KqAppInterface.isRestOvertime(man))
                        if (vo.getString(ta + "04") != null && !"".equals(vo.getString(ta + "04"))) {
                            java.util.Date s_Date = OperateDate.strToDate(vo.getString(ta + "z1"), "yyyy-MM-dd");
                            java.util.Date e_Date = OperateDate.strToDate(vo.getString(ta + "z3"), "yyyy-MM-dd");
                            //int num = RegisterDate.diffDate(s_Date, e_Date);
                            String op_date_to = "";
                            String class_id = "";
                            //szk公休日只判断一天
                            //for (int m = 0; m <= num; m++) {
                                op_date_to = OperateDate.dateToStr(OperateDate.addDay(s_Date, 0), "yyyy.MM.dd");
                                class_id =getClassId(vo.getString("nbase"), vo.getString("a0100"), op_date_to) ;
                                if (!("0".equals(class_id) || "".equals(class_id))) {
                                    isCorrect = false;
                                    throw new GeneralException(vo.getString("a0101") + "，" + "对不起，您申请的日期" + op_date_to + "不是公休日！");
                                }
                           // }
                            
                        } else {
                            if (!validateAppOper.is_Rest(kq_start, kq_end, vo.getString("a0100"), vo.getString("nbase"))) {
                                isCorrect = false;
                                throw new GeneralException(vo.getString("a0101") + "，" + validateAppOper.getNo_Rest_mess());
                            }
                        }

                    /** 判断是否是节假日 */
                    if (KqAppInterface.isFeastOvertime(man))
                        // if(!validateAppOper.is_Feast(kq_start,kq_end,vo.getString("b0110")))
                        if (!validateAppOper.is_Feast(kq_start, kq_end, vo.getString("b0110"), "" + vo.getString("q1104"))) {
                            isCorrect = false;
                            throw new GeneralException(vo.getString("a0101") + "，"
                                    + ResourceFactory.getProperty("error.kq.nfeast"));
                        }
                }

                annualApply.isRepeatedAllAppType(ta, vo.getString("nbase"), vo.getString("a0100"), vo.getString("a0101"), start, end,
                        this.getFrameconn(), insertid, "");
                
                vo.setString(ta + "z0", "01"); // 同意
                vo.setString(ta + "z5", "03"); // 已批
            }
            if ("2".equalsIgnoreCase(sp)) {
                if ("q15".equalsIgnoreCase(ta))// 对驳回已批假期管理的操作
                {
                    String old_z0 = vo.getString("q15z0");
                    String old_z5 = vo.getString("q15z5");
                    java.util.Date sp_D = vo.getDate("q15z7");
                    if (old_z0 != null && "01".equals(old_z0) && old_z5 != null && "03".equals(old_z5)) {
                        String sele = man; //.substring(0, 2); zxj 是否假期管理，只看前两位是不对的
                        if (KqParam.getInstance().isHoliday(this.frameconn, vo.getString("b0110"), sele)) {
                            HashMap kqItem_hash = annualApply.count_Leave(sele);
                            float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则
                            float leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, vo.getString("a0100"), vo
                                    .getString("nbase"), vo.getString("b0110"), kqItem_hash, holiday_rules);
                            if (leave_tiem > 0) {
                                // 驳回时删除销假信息
                                dao.delete("delete from q15 where q1519='" + vo.getString("q1501") + "' and q1517=1",
                                        new ArrayList());
                                isCorrect = annualApply.holsBackfill(start, end, vo.getString("a0100"), vo.getString("nbase"),
                                        sele, vo.getString("history"), leave_tiem);
                                if (!isCorrect) {
                                    throw new GeneralException(vo.getString("a0101") + "，驳回时管理假期类型计算失败！");
                                }

                                String sp_time = DateUtils.format(sp_D, "yyyy-MM-dd HH:mm:ss");
                                annualApply.bachReStatHols(sp_time, vo.getString("a0100"), vo.getString("b0110"), vo
                                        .getString("nbase"), sele, kqItem_hash);
                            }
                        }
                    }
                }
                vo.setString(ta + "z0", "02"); // 02 不同意
                vo.setString(ta + "z5", "07"); // 07 驳回
            } else if ("3".equalsIgnoreCase(sp)) {
                vo.setString(ta + "z0", "01"); // 02 不同意
                vo.setString(ta + "z5", "02"); // 07 驳回
            }
            if ("q11".equalsIgnoreCase(ta)) {
                annualApply.checkOvertimeRepeat(man, kq_start, kq_end, vo);
                
                if("1".equals(sp) || "3".equals(sp)){
                	String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
                	String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
                	if (para == null || para.length() <= 0)
                		para = "-1";
                	int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
                	if (overtimeLimit > 0) 
                	{
                		String IftoRest = "";
                		if(iftoRestField != null && iftoRestField.length() > 0)
                			IftoRest = vo.getString(iftoRestField);
                		
                		if(!"1".equals(IftoRest))//调休的加班不计算
                		{
                			// 加班最大限额控制
                			String errorMes = annualApply.checkOverTimelenMorethanLimit(vo, "3");
                			if (errorMes.length() > 0) {
                				throw new GeneralException(errorMes);
                			}
                		}
                		// 增加检查调休加班限额控制
    		            KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(frameconn, userView);
    		            String error = overTimeForLeaveBo.checkOvertimeForLeaveMaxHour(vo);
    		            if(!StringUtils.isEmpty(error)) {
    		            	throw new GeneralException(error);
    		            }
                		
                	}
                }
                //szk20131122结束时间大于申请时间时少判断一天
                Date start_T = DateUtils.getDate((DateUtils.format(kq_start, "HH:mm")), "HH:mm");
                Date end_T =DateUtils.getDate((DateUtils.format(kq_end, "HH:mm")), "HH:mm");
                KqUtilsClass kqUtilsClass = new KqUtilsClass();
                float time_f = kqUtilsClass.getPartMinute(start_T, end_T);
	        	  if (time_f < 0) {
	                    kq_end = DateUtils.addDays(kq_end, -1);
	                }
                annualApply.overTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            } else if ("q13".equalsIgnoreCase(ta)) {
                annualApply.awayTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            } else if ("q15".equalsIgnoreCase(ta)) {
                annualApply.leaveTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            }
            //this.cat.error("考勤模块更新申请记录：" + "操作标志：" + sp + ":" + ta + "01:" + vo.getString(ta + "01") + ":" 
//                    + DateUtils.FormatDate(kq_start, "yyyy-MM-dd HH:mm")
//                    + "--"
//                    + DateUtils.FormatDate(kq_end, "yyyy-MM-dd HH:mm")
//                    + "==" + this.userView.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }

    /**
     * 新增申请表单
     * 
     * @param table
     * @param dbpre
     * @param a0100
     * @param b0110
     * @param e0122
     * @param a0101
     * @param e01a1
     * @param man
     * @param viewlist
     * @throws GeneralException
     */
    private void insertDAO(String table, String dbpre, String a0100, String b0110, String e0122, String a0101, String e01a1,
            String man, ArrayList viewlist, boolean isCorrect, String sub_flag, String shenH, String shenP)
            throws GeneralException {
        // String insertid=""; //over_id,leave_id,away_id的值
        String insertname = "";
        String ta = table.toLowerCase();
        java.util.Date kq_start = null;
        java.util.Date kq_end = null;
        try {
            RecordVo vo = new RecordVo(table);
            insertname = ta + "01";

            if (sub_flag != null && "03".equals(sub_flag)) {
                vo.setString(ta + "z0", "01");
                vo.setString(ta + "z5", sub_flag);
            } else {
                vo.setString(ta + "z0", "03");
                vo.setString(ta + "z5", sub_flag);
            }

            vo.setString("nbase", dbpre);
            vo.setString("a0100", a0100);
            vo.setString("b0110", b0110);
            vo.setString("e0122", e0122);
            vo.setString("a0101", a0101);
            vo.setString("e01a1", e01a1);

            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                if (field.getItemid().equals(insertname) || "nbase".equals(field.getItemid())
                        || "a0100".equals(field.getItemid()) || "e01a1".equals(field.getItemid())
                        || "b0110".equals(field.getItemid()) || "e0122".equals(field.getItemid())
                        || "a0101".equals(field.getItemid()) || field.getItemid().equals(ta + "z5")
                        || field.getItemid().equals(ta + "z0") || "q1517".equalsIgnoreCase(field.getItemid())
                        || "q1519".equalsIgnoreCase(field.getItemid()))
                    continue;

                if ("N".equals(field.getItemtype())) {
                    if (field.getValue() != null && field.getValue().length() > 0) {
                        if (field.getItemid().equals(ta + "04")) {
                            if (!"#".equalsIgnoreCase(field.getValue()))
                                vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                        } else
                            vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                    }
                } else if ("D".equals(field.getItemtype())
                        && (field.getItemid().equals(ta + "z1") || field.getItemid().equals(ta + "z3")
                                || field.getItemid().equals(ta + "z7") || field.getItemid().equals(ta + "05"))) {
                    if (field.getItemid().equals(ta + "z1")) {
                        java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                        kq_start = dd;
                        vo.setDate(field.getItemid().toLowerCase(), dd);
                    } else if (field.getItemid().equals(ta + "z3")) {
                        java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                        kq_end = dd;
                        vo.setDate(field.getItemid().toLowerCase(), dd);
                    } else if (field.getItemid().equals(ta + "z7") && sub_flag != null && "03".equals(sub_flag)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String strDate = sdf.format(new java.util.Date());
                        vo.setDate(field.getItemid().toLowerCase(), DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm"));
                    } else if (field.getItemid().equals(ta + "05")) {
                        Calendar now = Calendar.getInstance();
                        java.util.Date dd = now.getTime();// 系统时间
                        vo.setDate(field.getItemid().toLowerCase(), dd);
                    }
                } else if (!"D".equals(field.getItemtype())) {
                    if (field.getItemid().equals(ta + "03")) {
                        vo.setString(field.getItemid(), man);

                    } else if (field.getItemid().equals(ta + "15")) {
                        vo.setString(field.getItemid(), shenP);
                    } else if (field.getItemid().equals(ta + "11")) {
                        vo.setString(field.getItemid(), shenH);
                    } else {
                        if (sub_flag != null && "03".equals(sub_flag)) {
                            if (field.getItemid().equalsIgnoreCase(ta + "09")) {
                                /*
                                 * //部门领导if(this.userView.getManagePrivCode().
                                 * equalsIgnoreCase("UM")) {
                                 * if(field.getValue()==
                                 * null||field.getValue().length()<=0) {
                                 * 
                                 * vo.setString(ta+"09",this.userView.
                                 * getUserFullName()); } }
                                 */

                            } else if (field.getItemid().equalsIgnoreCase(ta + "13")) {
                                /*
                                 * //单位领导if(this.userView.getManagePrivCode().
                                 * equalsIgnoreCase("UN")) {
                                 * if(field.getValue()==
                                 * null||field.getValue().length()<=0) {
                                 * vo.setString
                                 * (ta+"13",this.userView.getUserFullName()); }
                                 * }
                                 */
                            } else {
                                vo.setString(field.getItemid().toLowerCase(), field.getValue());
                            }
                        }
                        if (field.getItemid().equalsIgnoreCase(ta + "09") && "02".equalsIgnoreCase(sub_flag)) {
                            vo.setString(ta + "09", this.userView.getUserFullName());
                        } else if (field.getItemid().equalsIgnoreCase(ta + "13") && "03".equalsIgnoreCase(sub_flag)) {
                            vo.setString(ta + "13", this.userView.getUserFullName());
                        } else {
                            vo.setString(field.getItemid().toLowerCase(), field.getValue());
                        }

                    }
                }
            }
            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            /** 交验申请时间段是否已封存 ***/
            if (!annualApply.isSessionSearl(kq_start, kq_end)) {
                isCorrect = false;
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");
            }

            if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end)) {
                isCorrect = false;
                throw new GeneralException(vo.getString("a0101") + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
            }
            /***** 不同的申请进行不同的交验，后保存 ****/

            if ("q11".equalsIgnoreCase(ta)) {
                annualApply.checkOvertimeRepeat(man, kq_start, kq_end, vo);
                annualApply.overTimeApp("add", vo, man, kq_start, kq_end, isCorrect, "0");
            } else if ("q13".equalsIgnoreCase(ta)) {
                annualApply.awayTimeApp("add", vo, man, kq_start, kq_end, isCorrect, "0");
            } else if ("q15".equalsIgnoreCase(ta)) {
                annualApply.leaveTimeApp("add", vo, man, kq_start, kq_end, isCorrect, "1");
            }
            //this.cat.error("考勤模块新增申请记录：" + ta + ":" + dbpre + a0100 + a0101 + ":" 
//                        + DateUtils.FormatDate(kq_start, "yyyy-MM-dd HH:mm")
//                        + "--"
//                        + DateUtils.FormatDate(kq_end, "yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
      * 得到班次id
      * 
      * @param nbase
      * @param a0100
      * @param date
      * @return
      */
    private String getClassId(String nbase, String a0100, String date) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + Sql_switcher.isnull("class_id", "''") + " as class_id from kq_employ_shift");
        sql.append(" where nbase='" + nbase + "'");
        sql.append(" and a0100='" + a0100 + "'");
        sql.append(" and q03z0='" + date + "'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String class_id = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                class_id = rs.getString("class_id");
            }
        } catch (Exception e) {
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

}
