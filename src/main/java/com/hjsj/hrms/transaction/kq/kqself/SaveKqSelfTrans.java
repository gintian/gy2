/*
 * Created on 2006-3-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wxh
 *
 */
public class SaveKqSelfTrans extends IBusiness {

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

            if (!is_type) {
                throw new GeneralException(this.userView.getUserFullName() + "的" + ResourceFactory.getProperty("kq.type.no.join"));
            }

            java.util.Date kq_start = null;
            java.util.Date kq_end = null;
            String return_id = "";

            String insertname = "";
            String ta = table.toLowerCase();

            RecordVo vo = new RecordVo(table);
            insertname = ta + "01";
            vo.setString("nbase", userView.getDbname());
            vo.setString("a0100", userView.getA0100());
            vo.setString("b0110", userView.getUserOrgId());
            vo.setString("e0122", userView.getUserDeptId());
            vo.setString("a0101", userView.getUserFullName());
            vo.setString("e01a1", userView.getUserPosId());

            vo.setString(ta + "z0", "03");
            /**报批标志,区分保存、报批两种状态*/
            if (sp_flag != null && "02".equalsIgnoreCase(sp_flag))
                vo.setString(ta + "z5", "02");
            else if (sp_flag != null && "08".equalsIgnoreCase(sp_flag))
                vo.setString(ta + "z5", "08");
            else
                vo.setString(ta + "z5", "01");
            
            String start = "";
            String end = "";
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (field.getItemid().equalsIgnoreCase(insertname) || field.getItemid().equalsIgnoreCase(ta + "z0")
                        || "nbase".equals(field.getItemid()) || "a0100".equals(field.getItemid())
                        || "b0110".equals(field.getItemid()) || "e0122".equals(field.getItemid())
                        || "e01a1".equals(field.getItemid()) || "a0101".equals(field.getItemid())
                        || field.getItemid().equals(ta + "z5") || "q1517".equalsIgnoreCase(field.getItemid())
                        || "q1519".equalsIgnoreCase(field.getItemid()))
                    continue;
                
                if ("D".equals(field.getItemtype())
                        && (field.getItemid().equals(ta + "z1") || field.getItemid().equals(ta + "z3"))) {
                    java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                    if (field.getItemid().equals(ta + "z1")) {
                        start = field.getValue();
                        kq_start = dd;
                    }
                    if (field.getItemid().equals(ta + "z3")) {
                        end = field.getValue();
                        kq_end = dd;
                    }

                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "05"))) {
                    Calendar now = Calendar.getInstance();
                    Date dd = now.getTime();//系统时间
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("N".equals(field.getItemtype())) {
                    if (field.getValue() != null && field.getValue().length() > 0) {
                        if (field.getItemid().equals(ta + "04")) {
                            if (!"#".equals(field.getValue()))
                                vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                        } else {
                            vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                        }
                    }
                } else if (field.getItemid().equals(ta + "03")) {
                    vo.setString(field.getItemid(), sels);
                } else if (!"D".equals(field.getItemtype())) {
                    vo.setString(field.getItemid().toLowerCase(), field.getValue());
                }
            }
            
            /**判断开始日期是否在结束日期之前  */
            if (!annualApply.isSessionSearl(kq_start, kq_end))
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");

            if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end))
                throw new GeneralException("申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");

            ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
            if (kq_start.after(kq_end))
                throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

            annualApply.checkAppInSealDuration(kq_start);

            if (validateAppOper.is_OVERTIME_TYPE())//是否判断申请加班类型与申请日期相符
            {
                /* if(KqAppInterface.isRestOvertime(sels))  
                	  if(!validateAppOper.is_Rest(kq_start,kq_end,vo.getString("a0100"),vo.getString("nbase")))    		  
                		  throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("a0101")+","+validateAppOper.getNo_Rest_mess(),"",""));
                  *//**判断是否是节假日  */
                /*	
                if(KqAppInterface.isFeastOvertime(sels))  
                if(!validateAppOper.is_Feast(kq_start,kq_end,vo.getString("b0110")))    
                throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("a0101")+","+ResourceFactory.getProperty("error.kq.nfeast"),"",""));	*/
            }
            /****判断申请记录是否重复****/
            boolean isCorrect = true;
            if (!"q13".equalsIgnoreCase(ta) && annualApply.isRepeatedApp(userView.getDbname(), userView.getA0100(), start, end, "q11", 
                    this.getFrameconn(), "", "")) {
                isCorrect = false;
                if (annualApply.getAppLeavedMess() == null) {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有加班申请！<br><br>");
                } else {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有加班申请！<br><br>" + annualApply.getAppLeavedMess());
                }
            } else if ("q13".equalsIgnoreCase(ta) && annualApply.isRepeatedApp(userView.getDbname(), userView.getA0100(), start, end, "q13", 
                    this.getFrameconn(), "", "")) {
                isCorrect = false;
                if (annualApply.getAppLeavedMess() == null) {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有公出申请！<br><br>");
                } else {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有公出申请！<br><br>" + annualApply.getAppLeavedMess());
                }
            } else if (!"q13".equalsIgnoreCase(ta) && annualApply.isRepeatedApp(userView.getDbname(), userView.getA0100(), start, end, "q15", 
                    this.getFrameconn(), "", "")) {
                isCorrect = false;
                if (annualApply.getAppLeavedMess() == null) {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有休假申请！<br><br>");
                } else {
                    throw new GeneralException(userView.getUserFullName()
                            + "，在这个申请的时间段已经有休假申请！<br><br>" + annualApply.getAppLeavedMess());
                }
            }
            /*****不同的申请进行不同的交验，后保存****/

            if ("q11".equalsIgnoreCase(ta)) {
                annualApply.checkOvertimeRepeat(sels, kq_start, kq_end, vo);
                return_id = annualApply.overTimeApp("add", vo, sels, kq_start, kq_end, isCorrect, "0");
            } else if ("q13".equalsIgnoreCase(ta)) {
                return_id = annualApply.awayTimeApp("add", vo, sels, kq_start, kq_end, isCorrect, "0");
            } else if ("q15".equalsIgnoreCase(ta)) {
                return_id = annualApply.leaveTimeApp("add", vo, sels, kq_start, kq_end, isCorrect, "0");
            }

            this.getFormHM().put("id", return_id);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
