package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class UpdateKqSelfTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String table = (String) this.getFormHM().get("table");
            String sels = (String) this.getFormHM().get("sels");
            ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");
            String sp_flag = (String) this.getFormHM().get("sp_flag");
            if (sels == null || sels.length() <= 0) {
                return;
            }

            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            String org_id = managePrivCode.getPrivOrgId();
            KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, org_id, this.getFrameconn());

            String kq_type = kq_paramter.getKq_type();
            boolean is_type = true;
            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            if (kq_type != null && kq_type.length() > 0) {
                is_type = annualApply.getKqType(kq_type, this.getFrameconn(), this.userView);
            }

            if (!is_type)
                throw new GeneralException(this.userView.getUserFullName() + "的" + ResourceFactory.getProperty("kq.type.no.join"));

            java.util.Date kq_start = null;
            java.util.Date kq_end = null;

            String insertname = table + "01";
            String ta = table.toLowerCase();

            RecordVo vo = new RecordVo(table);
            String insertid = "";
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (field.getItemid().equalsIgnoreCase(insertname)) {
                    insertid = field.getValue().trim();
                    break;
                }

            }
            
            vo.setString(ta + "01", insertid);
            vo.setString("nbase", userView.getDbname());
            vo.setString("a0100", userView.getA0100());
            vo.setString("b0110", userView.getUserOrgId());
            vo.setString("e0122", userView.getUserDeptId());
            vo.setString("a0101", userView.getUserFullName());
            vo.setString("e01a1", userView.getUserPosId());
            insertname = ta + "01";
            String start = "";
            String end = "";
            vo.setString(ta + "z0", "03");
            /** 报批标志,区分保存、报批两种状态 */
            if (sp_flag != null && "02".equalsIgnoreCase(sp_flag))
                vo.setString(ta + "z5", "02");
            else if (sp_flag != null && "08".equalsIgnoreCase(sp_flag))
                vo.setString(ta + "z5", "08");
            else
                vo.setString(ta + "z5", "01");

            // vo.setString(ta+"z5","02");
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                //xiexd 2014.09.30 全角替换
                String field_getValue =  PubFunc.keyWord_reback(field.getValue());
                if (field.getItemid().equals(insertname) || field.getItemid().equals(ta + "z0") || "nbase".equals(field.getItemid()) || "a0100".equals(field.getItemid()) || "b0110".equals(field.getItemid()) || "e0122".equals(field.getItemid()) || "e01a1".equals(field.getItemid()) || "a0101".equals(field.getItemid()) || field.getItemid().equals(ta + "z5"))
                    continue;

                if ("q1517".equalsIgnoreCase(field.getItemid()) || "q1519".equalsIgnoreCase(field.getItemid()))
                    continue;

                if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "z1") || field.getItemid().equals(ta + "z3"))) {
                    java.util.Date dd = DateUtils.getDate(field_getValue, "yyyy-MM-dd HH:mm");
                    if (field.getItemid().equals(ta + "z1")) {
                        start = field_getValue;
                        kq_start = dd;
                    }

                    if (field.getItemid().equals(ta + "z3")) {
                        end = field_getValue;
                        kq_end = dd;
                    }

                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "05"))) {
                    java.util.Date dd = DateUtils.getDate(field_getValue, "yyyy-MM-dd HH:mm");
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("N".equals(field.getItemtype())) {
                    if (field_getValue != null && field_getValue.length() > 0) {
                        if (field.getItemid().equals(ta + "04")) {
                            if (!"#".equals(field_getValue))
                                vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field_getValue));
                        } else {
                            vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field_getValue));
                        }
                    }
                } else if (field.getItemid().equals(ta + "03")) {
                    vo.setString(field.getItemid(), sels);
                } else if (!"D".equals(field.getItemtype())) {
                    vo.setString(field.getItemid().toLowerCase(), field_getValue);
                }
            }
            
            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
            /** 判断开始日期是否在结束日期之前 */
            if (!annualApply.isSessionSearl(kq_start, kq_end))
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");

            if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end))
                throw new GeneralException("申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");

            if (kq_start.after(kq_end))
                throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

            annualApply.checkAppInSealDuration(kq_start);

            if (validateAppOper.is_OVERTIME_TYPE())// 是否判断申请加班类型与申请日期相符
            {
                /* *//** 判断是否是公休日 */
                /*
                 * if(KqAppInterface.isRestOvertime(sels))
                 * if(!validateAppOper.is_Rest(kq_start
                 * ,kq_end,userView.getA0100(),this.userView.getDbname())) throw
                 * GeneralExceptionHandler.Handle(new GeneralException(
                 * "",this.userView.getUserFullName()+","+validateAppOper
                 * .getNo_Rest_mess(),"",""));
                 *//** 判断是否是节假日 */
                /*
                 * if(KqAppInterface.isFeastOvertime(sels))
                 * if(!validateAppOper.is_Feast(kq_start
                 * ,kq_end,userView.getUserOrgId())) throw
                 * GeneralExceptionHandler.Handle(new
                 * GeneralException("",ResourceFactory
                 * .getProperty("error.kq.nfeast"),"",""));
                 */
            }

            annualApply.isRepeatedAllAppType(ta, userView.getDbname(), 
                    userView.getA0100(), userView.getUserFullName(), 
                    start, end, this.getFrameconn(), insertid, "");
            boolean ss = true;

            /***** 不同的申请进行不同的交验，后保存 ****/

            if ("q11".equalsIgnoreCase(ta)) {
                annualApply.checkOvertimeRepeat(sels, kq_start, kq_end, vo);
                if ("02".equals(sp_flag) || "08".equals(sp_flag)) {
                    float apptimeLen = 0;
                    String field = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
                    String fieldValue = "1";
                    if (field != null && field.length() > 0) {
                        fieldValue = (String) vo.getString(field);
                        if (fieldValue == null || "".equals(fieldValue)) {
                            throw new GeneralException("请确认加班申请单是否调休！");
                        }
                    }

                    if (!"1".equals(fieldValue))
                        apptimeLen = annualApply.getOneOverTimelen(vo);//计算加班时长

                    KqParam kqParam = KqParam.getInstance();
                    String para = kqParam.getDURATION_OVERTIME_MAX_LIMIT();
                    if (para == null || para.length() <= 0)
                        para = "-1";
                    int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
                    if (overtimeLimit > 0) {
                        float overtimeLen = 0;
                        //计算考勤期间内的加班时长
                        overtimeLen = annualApply.getKqdurationOverTimelen(vo.getString("nbase"), vo.getString("a0100"), "1");

                        if ((overtimeLen + apptimeLen > overtimeLimit) && apptimeLen > 0) {
                        	throw new GeneralException("所选申请单时长为" + PubFunc.round("" + apptimeLen, 2) + "小时，本期内已申请的加班时长为"
                                    + PubFunc.round("" + overtimeLen, 2) + "小时，合计已超出加班限额规定的" + PubFunc.round("" + overtimeLimit, 2)
                                    + "小时。");
                        }
                    }
                }
                annualApply.overTimeApp("up", vo, sels, kq_start, kq_end, ss, "0");
            } else if ("q13".equalsIgnoreCase(ta)) {
                annualApply.awayTimeApp("up", vo, sels, kq_start, kq_end, ss, "0");
            } else if ("q15".equalsIgnoreCase(ta)) {
                annualApply.leaveTimeApp("up", vo, sels, kq_start, kq_end, ss, "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
