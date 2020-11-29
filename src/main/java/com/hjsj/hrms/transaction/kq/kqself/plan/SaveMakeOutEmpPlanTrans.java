package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hrms.frame.dao.ContentDAO;
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
 * 提交年假申请单,结束该休假计划
 *<p>
 * Title:
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Aug 14, 2007:6:58:38 PM
 * </p>
 * 
 * @author dengcan
 *@version 4.0
 */
public class SaveMakeOutEmpPlanTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            ArrayList onelist = (ArrayList) this.getFormHM().get("onelist");
            RecordVo vo_31 = new RecordVo("q31");
            for (int i = 0; i < onelist.size(); i++) {
                FieldItem field = (FieldItem) onelist.get(i);
                if ("N".equals(field.getItemtype()))
                    vo_31.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));
                if ("D".equals(field.getItemtype())) {
                    Date date = DateUtils.getDate(field.getValue().replaceAll("\\.", "-"), "yyyy-MM-dd HH:mm");
                    vo_31.setDate(field.getItemid().toLowerCase(), date);
                } else {
                    vo_31.setString(field.getItemid().toLowerCase(), field.getValue());
                }
            }

            vo_31.setString("q31z5", "06");
            Date kq_start = null;
            Date kq_end = null;
            RecordVo vo_15 = new RecordVo("q15");
            vo_15.setString("nbase", vo_31.getString("nbase"));
            vo_15.setString("a0100", vo_31.getString("a0100"));
            vo_15.setString("b0110", vo_31.getString("b0110"));
            vo_15.setString("e0122", vo_31.getString("e0122"));
            vo_15.setString("a0101", vo_31.getString("a0101"));
            vo_15.setString("e01a1", vo_31.getString("e01a1"));
            vo_15.setString("q15z5", "02");
            vo_15.setString("q15z0", "03");
            
            Calendar now = Calendar.getInstance();
            Date dd = now.getTime();// 系统时
            vo_15.setDate("q1505", dd);
            dd = vo_31.getDate("q31z1");
            
            kq_start = dd;
            String start = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
            vo_15.setDate("q15z1", dd);
            dd = vo_31.getDate("q31z3");
            
            kq_end = dd;
            String end = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
            vo_15.setDate("q15z3", dd);
            vo_15.setString("q1507", vo_31.getString("q3107"));
            vo_15.setString("q1503", "06");

            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            boolean isCorrect = !annualApply.isRepeatedAllAppType(userView.getDbname(), userView.getA0100(), userView.getUserFullName(), start, end, this.getFrameconn(), "", "");

            annualApply.leaveTimeApp("add", vo_15, "06", kq_start, kq_end, isCorrect, "0");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.updateValueObject(vo_31);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
