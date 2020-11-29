package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 考勤申请登记
 *<p>Title:SaveAppRegisterTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 17, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SaveAppRegisterTrans extends IBusiness {

    private String rest_overtime_time = "0";
    private String dert_itemid        = "";
    private String dert_value         = "0";

    public void execute() throws GeneralException {
        try {
            String app_fashion = (String) this.getFormHM().get("app_fashion");
            String table = (String) this.getFormHM().get("table");
            String app_type = (String) this.getFormHM().get("app_type");
            String app_reason = (String) this.getFormHM().get("app_reason");
            this.getFormHM().remove("app_reason");//防止事由含有回车符，去除

            String appReaCode = "";
            String appReaField = "";
            String appReaCodesetid = (String) this.getFormHM().get("appReaCodesetid");
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) {
                appReaCode = (String) this.getFormHM().get("appReaCode");
                appReaField = kqUtilsClass.getAppReaField(new ContentDAO(frameconn)).toLowerCase();
            }

            String sub_flag = (String) this.getFormHM().get("sub_flag");
            if (sub_flag != null && "02".equals(sub_flag))
                sub_flag = "02";
            else if (sub_flag != null && "08".equals(sub_flag))
                sub_flag = "08";
            else if (sub_flag != null && "03".equals(sub_flag))
                sub_flag = "03";
            else
                sub_flag = "02";

            if (app_fashion == null || app_fashion.length() <= 0) {
                this.getFormHM().put("reflag", "lost");
                return;
            }

            if (table != null && "q11".equalsIgnoreCase(table)) {//是否有扣除休息时间
                this.dert_itemid = (String) this.getFormHM().get("dert_itemid");
                this.dert_value = (String) this.getFormHM().get("dert_value");
                this.dert_value = this.dert_value != null && this.dert_value.length() > 0 ? this.dert_value : "0";
            }

            this.rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();
            
            ArrayList arrayPer = (ArrayList) this.getFormHM().get("arrPer");
            if (arrayPer == null || arrayPer.size() <= 0) {
                throw new GeneralException("请确定是否已选定人员！");
            }
            
            ArrayList emplist = empMessList(arrayPer);
            boolean isCorrect = false;
            if ("0".equals(app_fashion)) {
                isCorrect = easyApp(table, emplist, app_type, app_reason, sub_flag);
            } else if ("1".equals(app_fashion)) {
                isCorrect = intricacyApp(table, emplist, app_type, app_reason, sub_flag, appReaField, appReaCode, appReaCodesetid);
            } else if ("2".equals(app_fashion)) {
                isCorrect = jumpApp(table, emplist, app_type, app_reason, sub_flag, appReaField, appReaCode, appReaCodesetid);

            } else {
                this.getFormHM().put("reflag", "lost");
                return;
            }

            if (isCorrect) {
                this.getFormHM().put("reflag", "ok");
            } else {
                this.getFormHM().put("reflag", "lost");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 简单申请
     * @param table申请表
     * @param infolist人员信息
     * @param app_type申请类型
     * @param app_reason申请事由
     * @return
     * @throws GeneralException
     */
    private boolean easyApp(String table, ArrayList infolist, String app_type, String app_reason, String sub_flag)
            throws GeneralException {
        boolean isCorrect = true;
        String start_d = (String) this.getFormHM().get("start_d");
        String end_d = (String) this.getFormHM().get("end_d");
        java.util.Date kq_start = DateUtils.getDate(start_d, "yyyy-MM-dd");
        java.util.Date kq_end = DateUtils.getDate(end_d, "yyyy-MM-dd");
        /**判断开始日期是否在结束日期之前  */
        if (kq_start.after(kq_end))
            throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

        ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
        boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        Calendar now = Calendar.getInstance();
        java.util.Date cur_d = now.getTime();//系统时间
        String a0100 = "";
        String b0110 = "";
        String e0122 = "";
        String a0101 = "";
        String e01a1 = "";
        String nbase = "";
        for (int i = 0; i < infolist.size(); i++) {
            LazyDynaBean selectrec = (LazyDynaBean) infolist.get(i);
            nbase = (String) selectrec.get("nbase");
            a0100 = (String) selectrec.get("a0100");
            b0110 = (String) selectrec.get("b0110");
            e0122 = (String) selectrec.get("e0122");
            a0101 = (String) selectrec.get("a0101");
            e01a1 = (String) selectrec.get("e01a1");

            annualApply.checkAppInSealDuration(kq_start);

            if (is_overtime_op) {
                /* if(KqAppInterface.isRestOvertime(app_type))  
                  if(!validateAppOper.is_Rest(kq_start,kq_end,a0100,nbase))    		  
                	  throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+validateAppOper.getNo_Rest_mess(),"",""));
                
                 *//**判断是否是节假日  */
                /*	
                if(KqAppInterface.isFeastOvertime(app_type))  
                if(!validateAppOper.is_Feast(kq_start,kq_end,b0110))    
                throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+ResourceFactory.getProperty("error.kq.nfeast"),"",""));*/
            }

            annualApply.isRepeatedAllAppType(table, nbase, a0100, a0101, start_d, end_d, this.getFrameconn(), "", "");
            isCorrect = true;

            /*****不同的申请进行不同的交验，后保存****/
            RecordVo vo = new RecordVo(table);
            String ta = table.toLowerCase();
            if (sub_flag != null && "03".equals(sub_flag)) {
                vo.setString(ta + "z0", "01");
                vo.setString(ta + "z5", sub_flag);
            } else {
                vo.setString(ta + "z0", "03");
                vo.setString(ta + "z5", sub_flag);
            }
            vo.setString("nbase", nbase);
            vo.setString("a0100", a0100);
            vo.setString("b0110", b0110);
            vo.setString("e0122", e0122);
            vo.setString("a0101", a0101);
            vo.setString("e01a1", e01a1);
            kq_start = DateUtils.getDate(start_d, "yyyy-MM-dd HH:mm");
            kq_end = DateUtils.getDate(end_d, "yyyy-MM-dd HH:mm");
            vo.setDate(ta + "z1", kq_start);
            vo.setDate(ta + "z3", kq_end);
            vo.setDate(ta + "05", cur_d);
            vo.setString(ta + "03", app_type);
            vo.setString(ta + "07", app_reason);
            if ("q11".equalsIgnoreCase(table) && this.dert_itemid != null && this.dert_itemid.length() > 0)
                vo.setString(this.dert_itemid, this.dert_value);//是否有扣除休息时间
            if (sub_flag != null && "03".equals(sub_flag)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String strDate = sdf.format(new java.util.Date());
                vo.setDate(ta + "z7", DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm"));
            }
            
            if ("02".equalsIgnoreCase(sub_flag))//审核人
            {
                vo.setString(ta + "09", this.userView.getUserFullName());
            }
            
            if ("03".equalsIgnoreCase(sub_flag))//审批人
            {
                vo.setString(ta + "13", this.userView.getUserFullName());
            }
            
            if (!annualApply.isSessionSearl(kq_start, kq_end))
                throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");

            if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end))
                throw new GeneralException(vo.getString("a0101") + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");

            if ("q11".equalsIgnoreCase(ta)) {
                //不同的加班申请进行不同的校验，校验不通过的会抛异常
                annualApply.checkOvertimeRepeat(app_type, kq_start, kq_end, vo);
                annualApply.overTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
            } else if ("q13".equalsIgnoreCase(ta)) {
                annualApply.awayTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
            } else if ("q15".equalsIgnoreCase(ta)) {
                annualApply.leaveTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "1");
            }
        }
        return isCorrect;
    }

    /**
     * 复杂申请
     * @param table 申请表
     * @param emplist  人员信息
     * @param app_type  申请类型
     * @param app_reason 申请事由
     * @return
     */
    private boolean intricacyApp(String table, ArrayList infolist, String app_type, String app_reason, String sub_flag,
            String appReaField, String appReaCode, String appReaCodesetid) throws GeneralException {
        boolean isCorrect = true;
        String start_str_d = (String) this.getFormHM().get("start_d");
        String end_str_d = (String) this.getFormHM().get("end_d");
        String start_t = (String) this.getFormHM().get("start_t");
        String end_t = (String) this.getFormHM().get("end_t");
        String intricacy_app_fashion = (String) this.getFormHM().get("fashion_type");
        String class_id = (String) this.getFormHM().get("class_id");
        if (class_id == null || class_id.length() <= 0 || "#".equals(class_id))
            class_id = null;
        ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.getFrameconn());
        //boolean is_overtime_op=validateAppOper.is_OVERTIME_TYPE();
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        Calendar now = Calendar.getInstance();
        java.util.Date cur_d = now.getTime();//系统时间
        Date start_D = DateUtils.getDate(start_str_d, "yyyy-MM-dd");
        Date end_D = DateUtils.getDate(end_str_d, "yyyy-MM-dd");
        int diff = DateUtils.dayDiff(start_D, end_D);
        Date cur_D = null;
        String a0100 = "";
        String b0110 = "";
        String e0122 = "";
        String a0101 = "";
        String e01a1 = "";
        String nbase = "";
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        boolean isspan = kqUtilsClass.isSpanForKqClass(class_id);
        if (isspan) {
            isspan = kqUtilsClass.isSpanForTimeStr(start_t, end_t);
        }
        Date start_T = DateUtils.getDate(start_t, "HH:mm");
        Date end_T = DateUtils.getDate(end_t, "HH:mm");
        float time_f = kqUtilsClass.getPartMinute(start_T, end_T);
        String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
		if (para == null || para.length() <= 0)
		 	para = "-1";
		int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
		String iftoRest = (String) this.getFormHM().get("IftoRest");
     	String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
		
        for (int i = 0; i < infolist.size(); i++) {
            LazyDynaBean selectrec = (LazyDynaBean) infolist.get(i);
            nbase = (String) selectrec.get("nbase");
            a0100 = (String) selectrec.get("a0100");
            b0110 = (String) selectrec.get("b0110");
            e0122 = (String) selectrec.get("e0122");
            a0101 = (String) selectrec.get("a0101");
            e01a1 = (String) selectrec.get("e01a1");
            float apptimeLen = 0;
            RecordVo vo = null;
            for (int r = 0; r <= diff; r++) {
                cur_D = DateUtils.addDays(start_D, r);
                String curD_str = DateUtils.format(cur_D, "yyyy-MM-dd");
                if ("1".equals(intricacy_app_fashion))//每公休日申请一次
                {
                    if (!validateAppOper.is_Rest(cur_D, a0100, nbase, b0110))
                        continue;
                }
                String start_d = curD_str + " " + start_t;
                String end_d = curD_str + " " + end_t;
                Date kq_start = DateUtils.getDate(start_d, "yyyy-MM-dd HH:mm");
                Date kq_end = DateUtils.getDate(end_d, "yyyy-MM-dd HH:mm");
                if (isspan) {
                    kq_end = DateUtils.addDays(kq_end, 1);
                    end_d = DateUtils.format(kq_end, "yyyy-MM-dd HH:mm");
                }

                annualApply.checkAppInSealDuration(kq_start);

                /*if(is_overtime_op)
                {
                   if(KqAppInterface.isRestOvertime(app_type))  
                	  if(!validateAppOper.is_Rest(kq_start,kq_end,a0100,nbase))    		  
                		  throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+validateAppOper.getNo_Rest_mess(),"",""));
                
                   *//**判断是否是节假日  */
                /*	
                if(KqAppInterface.isFeastOvertime(app_type))  
                if(!validateAppOper.is_Feast(kq_start,kq_end,b0110))    
                throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+ResourceFactory.getProperty("error.kq.nfeast"),"",""));	
                }*/
                annualApply.isRepeatedAllAppType(table, nbase, a0100, a0101, start_d, end_d, this.getFrameconn(), "", "");
                isCorrect = true;
                /*****不同的申请进行不同的交验，后保存****/
                vo = new RecordVo(table);
                String ta = table.toLowerCase();
                if (sub_flag != null && "03".equals(sub_flag)) {
                    vo.setString(ta + "z0", "01");
                    vo.setString(ta + "z5", sub_flag);
                } else {
                    vo.setString(ta + "z0", "03");
                    vo.setString(ta + "z5", sub_flag);
                }
                vo.setString("nbase", nbase);
                vo.setString("a0100", a0100);
                vo.setString("b0110", b0110);
                vo.setString("e0122", e0122);
                vo.setString("a0101", a0101);
                vo.setString("e01a1", e01a1);
                /*kq_start=DateUtils.getDate(start_d,"yyyy-MM-dd HH:mm");
                kq_end=DateUtils.getDate(end_d,"yyyy-MM-dd HH:mm");*/
                vo.setDate(ta + "z1", kq_start);
                vo.setDate(ta + "z3", kq_end);
                vo.setDate(ta + "05", cur_d);
                vo.setString(ta + "03", app_type);
                vo.setString(ta + "07", app_reason);
                if ("".equals(class_id)) {
                    class_id = "null";
                }
                vo.setString(ta + "04", class_id);
                if ("q11".equalsIgnoreCase(table) && this.dert_itemid != null && this.dert_itemid.length() > 0)
                    vo.setString(this.dert_itemid, this.dert_value);//是否有扣除休息时间
                if (sub_flag != null && "03".equals(sub_flag)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strDate = sdf.format(new java.util.Date());
                    vo.setDate(ta + "z7", DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm"));
                    /*if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"))
                    {
                    vo.setString(ta+"09",this.userView.getUserFullName());
                    }
                    if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"))
                    {
                    vo.setString(ta+"13",this.userView.getUserFullName());
                    }*/
                }
                if ("02".equalsIgnoreCase(sub_flag))//审核人
                {
                    vo.setString(ta + "09", this.userView.getUserFullName());
                }
                if ("03".equalsIgnoreCase(sub_flag))//审批人
                {
                    vo.setString(ta + "13", this.userView.getUserFullName());
                }

                if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) {
                    vo.setString(appReaField, appReaCode);
                }

                if (!annualApply.isSessionSearl(kq_start, kq_end))
                    throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");

                if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end))
                    throw new GeneralException("", vo.getString("a0101") + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
                
                if ("q11".equalsIgnoreCase(ta)) {
                    //不同的加班申请进行不同的校验，校验不通过的会抛异常
                    annualApply.checkOvertimeRepeat(app_type, kq_start, kq_end, vo);
                   
		        	if (iftoRestField != null && iftoRestField.length() > 0) 
					{
		        		vo.setString(iftoRestField, iftoRest);
		        		String IftoRest = "";
	    				IftoRest = vo.getString(iftoRestField);
	        			if ("1".equals(iftoRest)) 
		        		{
		        			String error = annualApply.CheckAppTypeIsToLeave(vo.getString("q1103"));
		        			if (error.length() > 0) 
							{
								throw new GeneralException(error);
							}
		        		}
					}
		        	if(!"1".equals(iftoRest))//调休的加班不计算
        			{
        				apptimeLen = apptimeLen + annualApply.getOneOverTimelen(vo);
        			}
		        	//szk20131119结束时间大于申请时间时少判断一天
		        	  if (time_f < 0) {
		                    kq_end = DateUtils.addDays(kq_end, -1);
		                }
		                end_d = DateUtils.format(kq_end, "yyyy-MM-dd HH:mm");        	
                    annualApply.overTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
                } else if ("q13".equalsIgnoreCase(ta)) {
                    annualApply.awayTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
                } else if ("q15".equalsIgnoreCase(ta)) {
                    annualApply.leaveTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "1");
                }
            }//时间循环结束
            
			if (overtimeLimit > 0) 
			{
				float overtimeLen = annualApply.getKqdurationOverTimelen(vo.getString("nbase"), vo.getString("a0100"), "3");
				if("03".equals(sub_flag))
					overtimeLen = overtimeLen - apptimeLen;
				if ((overtimeLen + apptimeLen > overtimeLimit) && apptimeLen > 0) {
						throw new GeneralException(vo.getString("a0101") + "申请加班时长为" + PubFunc.round(""+apptimeLen,2) + "小时，本期内已申请的加班时长为" 
								+ PubFunc.round(""+overtimeLen,2) + "小时，合计已超出加班限额规定的" + PubFunc.round(""+overtimeLimit,2) + "小时。");
					
				}
			}
				
        }//人员for结束
        return isCorrect;
    }

    /**
     * 得到选定人的基本信息
     * @param arrayPer
     * @return
     */
    private ArrayList empMessList(ArrayList arrayPer) {
        ArrayList list = new ArrayList();
        String perMess = "";
        String sql = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            for (int i = 0; i < arrayPer.size(); i++) {
                perMess = arrayPer.get(i).toString();
                String nbase = perMess.substring(0, 3);
                String a0100 = perMess.substring(3);
                sql = "select a0101,b0110,e0122,e01a1 from " + nbase + "A01 where a0100='" + a0100 + "'";
                this.frowset = dao.search(sql);
                LazyDynaBean rec = new LazyDynaBean();
                if (this.frowset.next()) {
                    rec.set("a0100", a0100);
                    rec.set("nbase", nbase);
                    rec.set("b0110", PubFunc.nullToStr(this.frowset.getString("b0110")));
                    rec.set("e0122", PubFunc.nullToStr(this.frowset.getString("e0122")));
                    rec.set("e01a1", PubFunc.nullToStr(this.frowset.getString("e01a1")));
                    rec.set("a0101", PubFunc.nullToStr(this.frowset.getString("a0101")));
                    list.add(rec);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 跳天申请
     * @param table
     * @param infolist
     * @param app_type
     * @param app_reason
     * @param sub_flag
     * @return
     * @throws GeneralException
     */
    private boolean jumpApp(String table, ArrayList infolist, String app_type, String app_reason, String sub_flag,
            String appReaField, String appReaCode, String appReaCodesetid) throws GeneralException {
        boolean isCorrect = true;
        String start_t = (String) this.getFormHM().get("start_t");
        String end_t = (String) this.getFormHM().get("end_t");
        String class_id = (String) this.getFormHM().get("class_id");
        if (class_id == null || class_id.length() <= 0 || "#".equals(class_id))
            class_id = null;
        //ValidateAppOper validateAppOper=new ValidateAppOper(this.userView,this.getFrameconn());
        //boolean is_overtime_op=validateAppOper.is_OVERTIME_TYPE();
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        Calendar now = Calendar.getInstance();
        java.util.Date cur_d = now.getTime();//系统时间
        ArrayList app_dates = (ArrayList) this.getFormHM().get("app_dates");

        if (app_dates == null || app_dates.size() <= 0)
            throw new GeneralException("请确定是否已选定申请日期！");

        String a0100 = "";
        String b0110 = "";
        String e0122 = "";
        String a0101 = "";
        String e01a1 = "";
        String nbase = "";
        Date start_T = DateUtils.getDate(start_t, "HH:mm");
        Date end_T = DateUtils.getDate(end_t, "HH:mm");
        KqUtilsClass kqUtilsClass = new KqUtilsClass();
        float time_f = kqUtilsClass.getPartMinute(start_T, end_T);
        
        String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
		if (para == null || para.length() <= 0)
		 	para = "-1";
		int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
		String iftoRest = (String) this.getFormHM().get("IftoRest");
    	String iftoRestField = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
		
        for (int i = 0; i < infolist.size(); i++) {
            LazyDynaBean selectrec = (LazyDynaBean) infolist.get(i);
            nbase = (String) selectrec.get("nbase");
            a0100 = (String) selectrec.get("a0100");
            b0110 = (String) selectrec.get("b0110");
            e0122 = (String) selectrec.get("e0122");
            a0101 = (String) selectrec.get("a0101");
            e01a1 = (String) selectrec.get("e01a1");
            float apptimeLen = 0;
            RecordVo vo = null;
            for (int r = 0; r < app_dates.size(); r++) {
                String app_date = app_dates.get(r).toString();
                String curD_str = app_date.replaceAll("\\.", "-");
                String start_d = curD_str + " " + start_t;
                String end_d = curD_str + " " + end_t;
                Date kq_start = DateUtils.getDate(start_d, "yyyy-MM-dd HH:mm");
                Date kq_end = DateUtils.getDate(end_d, "yyyy-MM-dd HH:mm");
                if (time_f < 0) {
                    kq_end = DateUtils.addDays(kq_end, 1);
                }
                end_d = DateUtils.format(kq_end, "yyyy-MM-dd HH:mm");
                annualApply.checkAppInSealDuration(kq_start);
                /*if(is_overtime_op)
                {
                   if(KqAppInterface.isRestOvertime(app_type))  
                	  if(!validateAppOper.is_Rest(kq_start,kq_end,a0100,nbase))    		  
                		  throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+validateAppOper.getNo_Rest_mess(),"",""));
                
                   *//**判断是否是节假日  */
                /*	
                if(KqAppInterface.isFeastOvertime(app_type))  
                if(!validateAppOper.is_Feast(kq_start,kq_end,b0110))    
                throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+","+ResourceFactory.getProperty("error.kq.nfeast"),"",""));	
                }*/
                isCorrect = !annualApply.isRepeatedAllAppType(table, nbase, a0100, a0101, start_d, end_d, this.getFrameconn(), "", "");
                /*****不同的申请进行不同的交验，后保存****/
                vo = new RecordVo(table);
                String ta = table.toLowerCase();
                if (sub_flag != null && "03".equals(sub_flag)) {
                    vo.setString(ta + "z0", "01");
                    vo.setString(ta + "z5", sub_flag);
                } else {
                    vo.setString(ta + "z0", "03");
                    vo.setString(ta + "z5", sub_flag);
                }
                vo.setString("nbase", nbase);
                vo.setString("a0100", a0100);
                vo.setString("b0110", b0110);
                vo.setString("e0122", e0122);
                vo.setString("a0101", a0101);
                vo.setString("e01a1", e01a1);
                /*kq_start=DateUtils.getDate(start_d,"yyyy-MM-dd HH:mm");
                kq_end=DateUtils.getDate(end_d,"yyyy-MM-dd HH:mm");*/
                vo.setDate(ta + "z1", kq_start);
                vo.setDate(ta + "z3", kq_end);
                vo.setDate(ta + "05", cur_d);
                vo.setString(ta + "03", app_type);
                vo.setString(ta + "07", app_reason);
                vo.setString(ta + "04", class_id);
                if ("q11".equalsIgnoreCase(table) && this.dert_itemid != null && this.dert_itemid.length() > 0)
                    vo.setString(this.dert_itemid, this.dert_value);//是否有扣除休息时间
                if (sub_flag != null && "03".equals(sub_flag)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strDate = sdf.format(new java.util.Date());
                    vo.setDate(ta + "z7", DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm"));
                    /*if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"))
                    {
                    vo.setString(ta+"09",this.userView.getUserFullName());
                    }
                    if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"))
                    {
                    vo.setString(ta+"13",this.userView.getUserFullName());
                    }*/
                }
                if ("02".equalsIgnoreCase(sub_flag))//审核人
                {
                    vo.setString(ta + "09", this.userView.getUserFullName());
                }
                if ("03".equalsIgnoreCase(sub_flag))//审批人
                {
                    vo.setString(ta + "13", this.userView.getUserFullName());
                }

                if ("Q11".equalsIgnoreCase(table) && appReaCodesetid != null && appReaCodesetid.length() > 0) {
                    vo.setString(appReaField, appReaCode);
                }

                if (!annualApply.isSessionSearl(kq_start, kq_end))
                    throw new GeneralException("该考勤期间已封存或不存在，不能做该申请操作！");

                if (!annualApply.getKqDataState(vo.getString("nbase"), vo.getString("a0100"), kq_start, kq_end))
                    throw new GeneralException(vo.getString("a0101") + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");

                if ("q11".equalsIgnoreCase(ta)) {
                    //不同的加班申请进行不同的校验，校验不通过的会抛异常
                    annualApply.checkOvertimeRepeat(app_type, kq_start, kq_end, vo);
                    
		        	if (iftoRestField != null && iftoRestField.length() > 0) 
					{
		        		vo.setString(iftoRestField, iftoRest);
	        		 	String IftoRest = "";
	        		 	IftoRest = vo.getString(iftoRestField);
	        		 	String error = "";
						if ("1".equals(iftoRest)) 
		        		{
		        			error = annualApply.CheckAppTypeIsToLeave(vo.getString("q1103"));
		        			if (error.length() > 0) 
							{
								throw new GeneralException(error);
							}
		        		}
					}
		        	if(!"1".equals(iftoRest))//调休的加班不计算
        			{
        				apptimeLen = annualApply.getOneOverTimelen(vo) * app_dates.size();
        			}	
		        	//szk20131119结束时间大于申请时间时少判断一天
		        	  if (time_f < 0) {
		                    kq_end = DateUtils.addDays(kq_end, -1);
		                }
		                end_d = DateUtils.format(kq_end, "yyyy-MM-dd HH:mm");
                    annualApply.overTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
                } else if ("q13".equalsIgnoreCase(ta)) {
                    annualApply.awayTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "0");
                } else if ("q15".equalsIgnoreCase(ta)) {
                    annualApply.leaveTimeApp("add", vo, app_type, kq_start, kq_end, isCorrect, "1");
                }
            }//时间循环结束
            
            if (overtimeLimit > 0) 
			{
            	float overtimeLen = annualApply.getKqdurationOverTimelen(vo.getString("nbase"), vo.getString("a0100"), "3");
            	if("03".equals(sub_flag))
            		overtimeLen = overtimeLen - apptimeLen;
            	if ((overtimeLen + apptimeLen > overtimeLimit) && apptimeLen > 0) {
						throw new GeneralException(vo.getString("a0101") + "申请加班时长为" + PubFunc.round(""+apptimeLen,2) + "小时，本期内已申请的加班时长为" 
								+ PubFunc.round(""+overtimeLen,2) + "小时，合计已超出加班限额规定的" + PubFunc.round(""+overtimeLimit,2) + "小时。");
            	}
			}
			
        }//人员for结束
        return isCorrect;
    }
}
