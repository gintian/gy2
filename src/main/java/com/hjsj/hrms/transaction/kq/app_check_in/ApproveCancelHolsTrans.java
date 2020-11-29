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
 * 批准驳回
 * <p>
 * Title:ApproveCancelHolsTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Apr 24, 2007 5:48:02 PM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class ApproveCancelHolsTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            RecordVo vo = (RecordVo) this.getFormHM().get("cancelvo");
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
            String start = vo.getString(table + "z1");
            String end = vo.getString(table + "z3");
            if (start == null || start.length() <= 0 || end == null || end.length() <= 0)
                throw new GeneralException("开始或结束时间不能为空！");

            kq_start = DateUtils.getDate(start, "yyyy-MM-dd HH:mm");
            kq_end = DateUtils.getDate(end, "yyyy-MM-dd HH:mm");
            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            /** 判断开始日期是否在结束日期之前 */
            String q15z5 = vo.getString(table + "z5");
            if (q15z5 != null && "03".equals(q15z5))
                return;

            if (q15z5 != null && "07".equals(q15z5))
                return;

            if (kq_start.after(kq_end))
                throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

            annualApply.checkAppInSealDuration(kq_start);

            //请假单号
            String leaveAppId = vo.getString(table + "19");
            leaveAppId = leaveAppId == null ? "" : leaveAppId;
            //销假单号
            String cancelAppId = vo.getString(table + "01");
            cancelAppId = cancelAppId == null ? "" : cancelAppId;
            //判断申请记录是否重复
            annualApply.isRepeatedAllAppType(table, vo.getString("nbase"), vo.getString("a0100"), vo.getString("a0101"), 
                    start, end, this.getFrameconn(), leaveAppId, cancelAppId);

            boolean isCorrect = true;
            CancelHols cancelHols = new CancelHols(this.userView, this.getFrameconn());
            String sp = "";
            if ("07".equals(smflag)) {
                vo.setString(table + "z5", "07");
                sp = "";
            } else if ("03".equals(smflag)) {
                vo.setString(table + "z5", "03");
                sp = "5";
            } else {
                vo.setString(table + "z5", "02");
                sp = "";
            }
            vo.setString(table + "z0", "01");
            String sels = vo.getString(table + "03");

            cancelHols.cancelTimeApp(vo, sels, kq_start, kq_end, isCorrect, sp, "app", table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
