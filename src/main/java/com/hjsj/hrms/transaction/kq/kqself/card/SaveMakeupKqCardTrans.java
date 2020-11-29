package com.hjsj.hrms.transaction.kq.kqself.card;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.RepairKqCard;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SaveMakeupKqCardTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hMap = (HashMap) this.getFormHM().get("requestPamaHM");
        String ip_adr = "";
        //01起草 02报批
        String z5 = "";
        //走审批关系时的直接领导
        String app_account = "";
        if(hMap == null){
        	ip_adr = (String) this.getFormHM().get("ip_adr");
        	z5 = (String) this.getFormHM().get("z5"); 
        	app_account = (String)this.getFormHM().get("app_account");
        }else{
        	ip_adr = (String) hMap.get("ip_adr");
        	z5 = (String) hMap.get("z5"); 
        	app_account = (String)hMap.get("account");
        }
        if ("null".equals(app_account) || "".equals(app_account))
            app_account = null;
        
        if(null != app_account)
            app_account = SafeCode.decode(app_account);
        
        String inout_flag = (String) this.getFormHM().get("inout_flag");
        inout_flag = inout_flag != null && inout_flag.length() > 0 ? inout_flag : "0";
        String oper_cause = (String) this.getFormHM().get("oper_cause");
        String nbase = this.userView.getDbname();
        String a0100 = this.userView.getA0100();
        
        String work_date = (String) this.getFormHM().get("makeup_date");
        String work_time = (String) this.getFormHM().get("makeup_time");
        
        if (work_date == null || work_date.length() <= 0 || work_time == null || work_time.length() <= 0) {
            this.getFormHM().put("mess", "时间日期不能为空，补签失败！");
            return;
        }
        
        work_date = work_date.replaceAll("-", "\\.");
        try {
            Date dd = DateUtils.getDate(work_date, "yyyy.MM.dd");
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "日期格式不正确！yyyy-MM-dd", "", ""));
        }
        
        try {
            Date dd = DateUtils.getDate(work_time, "HH:mm");
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "时间格式不正确！HH:mm", "", ""));
        }
        
        NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
        /** 开始：补刷时间不能大于服务器的当前时间 wangy **/
        String work_date_server = netSignIn.getWork_date().replaceAll("\\.", "-");
        String work_time_server = netSignIn.getWork_time();
        String work_date2 = work_date.replaceAll("\\.", "-");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2_server = Calendar.getInstance();
        try {
            c1.setTime(formatter.parse(work_date2));
            c2_server.setTime(formatter.parse(work_date_server));
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "日期时间类型不对！HH:mm", "", ""));
        }
        
        int result = c1.compareTo(c2_server);
        if (result > 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "补签日期不能大于当前日期！", "", ""));
        } else if (result == 0) {
            // 日期相等就要对比时间
            SimpleDateFormat form = new SimpleDateFormat("HH:mm");
            java.util.Calendar c1_1 = java.util.Calendar.getInstance();
            java.util.Calendar c2_2_server = java.util.Calendar.getInstance();
            try {
                c1_1.setTime(form.parse(work_time));
                c2_2_server.setTime(form.parse(work_time_server));
            } catch (Exception e) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", "时间类型不对！", "", ""));
            }
            
            int result_1 = c1_1.compareTo(c2_2_server);
            if (result_1 > 0)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "补签时间不能大于当前时间！", "", ""));
        }
        /** 结束 **/
        String cardno = netSignIn.getKqCard(nbase, a0100);
        if (cardno == null || cardno.length() <= 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "没有分配考勤卡号，不能刷卡！", "", ""));
        }
        
        RepairKqCard repairKqCard = new RepairKqCard(this.getFrameconn(), this.userView);
        int numLimit = repairKqCard.getRepairCardNumLimit();
        if (numLimit > 0) {
            ArrayList datelist = RegisterDate.getKq_duration(work_date, this.getFrameconn());
            if (datelist != null && datelist.size() > 0) {
                String start_date = datelist.get(0).toString();
                String end_date = datelist.get(datelist.size() - 1).toString();
                if (repairKqCard.isOverTopRepairdaynum(numLimit, nbase, a0100, start_date, end_date)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("", 
                            ResourceFactory.getProperty("kq.repair.over.num_hint_1") 
                            + numLimit 
                            + ResourceFactory.getProperty("kq.repair.over.num_hint_2"), 
                            "", ""));
                }
            }
        }
        
        if (!netSignIn.ifNetSign(nbase, a0100, work_date, work_time)) {
            throw GeneralExceptionHandler.Handle(new GeneralException("", "不可以在请假时间范围内，签到签退！", "", ""));
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new java.util.Date());
        Date oper_time = DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm:ss");
        if (!netSignIn.IsExists(nbase, a0100, work_date, work_time))
            throw GeneralExceptionHandler.Handle(new GeneralException("", "规定时间间隔内不能签多次！", "", ""));
        
        AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
        Date startDate = OperateDate.strToDate(work_date2 + " " + work_time, "yyyy-MM-dd HH:mm");
        Date endDate = OperateDate.strToDate(work_date2 + " " + work_time, "yyyy-MM-dd HH:mm");
		if(!annualApply.isSessionSearl(startDate,endDate))
    	    throw new GeneralException(ResourceFactory.getProperty("kq_card.repair.warn"));
		
        StringBuffer sql = new StringBuffer();
        sql.append("insert into kq_originality_data(a0100,nbase,card_no,work_date,work_time,a0101,b0110,e0122,e01a1,location");
        sql.append(",inout_flag,oper_cause,oper_user,oper_time,sp_flag,datafrom,oper_mach");
        if ("02".equals(z5))
            sql.append(",curr_user");
        sql.append(")");
        
        sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        if ("02".equals(z5))
            sql.append(",?");
        sql.append(")");
        
        ArrayList list = new ArrayList();
        list.add(a0100);
        list.add(nbase);
        list.add(cardno);
        list.add(work_date);
        list.add(work_time);
        list.add(userView.getUserFullName());
        list.add(userView.getUserOrgId());
        list.add(userView.getUserDeptId());
        list.add(userView.getUserPosId());
        list.add("补签");
        list.add(inout_flag);
        list.add(SafeCode.decode(oper_cause));
        list.add(this.userView.getUserFullName());
        list.add(DateUtils.getTimestamp(DateUtils.format(oper_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
        list.add(z5);
        list.add("1");
        list.add(ip_adr);
        if ("02".equals(z5)) {
            list.add(app_account);
        }
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
