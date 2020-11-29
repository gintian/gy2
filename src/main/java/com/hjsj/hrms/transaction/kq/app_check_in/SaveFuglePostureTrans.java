package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.kqself.CancelHols;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 代销假，批准
 * 
 * @author Owner
 * 
 */
public class SaveFuglePostureTrans extends IBusiness {

    public void execute() throws GeneralException {
        RecordVo vo = (RecordVo) this.getFormHM().get("cancelvo");
        String radio = (String) this.getFormHM().get("radio");
        if (vo == null)
            return;
        
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String smflag = (String) hm.get("smflag");
        if (smflag == null || smflag.length() <= 0)
            smflag = "01";
        
        String table = (String) hm.get("table");
        if(StringUtils.isNotEmpty(table))
        	table = table.toLowerCase();
        
        java.util.Date kq_start = null;
        java.util.Date kq_end = null;

        if ("2".equals(radio)) {
            vo.setString(table + "z1", (String) this.getFormHM().get("scope_start_time"));
            vo.setString(table + "z3", (String) this.getFormHM().get("scope_end_time"));
        }
        
        String start = vo.getString(table + "z1");
        String end = vo.getString(table + "z3");
        if (start == null || start.length() <= 0 || end == null || end.length() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "开始或结束时间不能为空！", "", ""));
        
        kq_start = DateUtils.getDate(start, "yyyy-MM-dd HH:mm");
        kq_end = DateUtils.getDate(end, "yyyy-MM-dd HH:mm");
        
        /** 判断开始日期是否在结束日期之前 */
        String q15z5 = vo.getString(table + "z5");
        if (q15z5 != null && "03".equals(q15z5))
            return;

        if (kq_start.after(kq_end))
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("error.kq.wrongrequence"), "", ""));

        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        annualApply.checkAppInSealDuration(kq_start);

        /**** 判断申请记录是否重复 ****/
        boolean isCorrect = true;
        if (annualApply.isRepeatedApp(vo.getString("nbase"), vo.getString("a0100"), start, end, table, this.getFrameconn(), vo.getString(table + "01"), vo.getString(table + "19"))) {
            isCorrect = false;
            if("q11".equalsIgnoreCase(table))
            	throw GeneralExceptionHandler.Handle(new GeneralException("", vo.getString("a0101") + ",在这个申请的时间段已经有加班申请！<br><br>" + annualApply.getAppLeavedMess(), "", ""));
            else if("q13".equalsIgnoreCase(table))
            	throw GeneralExceptionHandler.Handle(new GeneralException("", vo.getString("a0101") + ",在这个申请的时间段已经有公出申请！<br><br>" + annualApply.getAppLeavedMess(), "", ""));
            else if("q15".equalsIgnoreCase(table))
            	throw GeneralExceptionHandler.Handle(new GeneralException("", vo.getString("a0101") + ",在这个申请的时间段已经有休假申请！<br><br>" + annualApply.getAppLeavedMess(), "", ""));
            	
        }
        CancelHols cancelHols = new CancelHols(this.userView, this.getFrameconn());
        String sp = "";
        if ("07".equals(smflag)) {
            vo.setString(table + "z5", "07");
            sp = "";
        } else if ("03".equals(smflag)) {
            vo.setString(table + "z5", "03");
            sp = "5";
        } else if ("02".equals(smflag)) {
            vo.setString(table + "z5", "02");
            sp = "";
        } else {
            vo.setString(table + "z5", "01");
            sp = "";
        }
        vo.setString(table + "z0", "01");
        String sels = vo.getString(table + "03");

        cancelHols.cancelTimeApp(vo, sels, kq_start, kq_end, isCorrect, sp, "app", table);
    }

}
