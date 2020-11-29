package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
import java.util.HashMap;

public class GetLeftDaysOfVacationTrans extends IBusiness {

    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        try {
            String peopleName = (String) this.getFormHM().get("peopleName");
            String a0100 = "";
            String nbase = "";
            String b0110 = "";
            if (peopleName != null && peopleName.length() > 3) {
                a0100 = peopleName.substring(3);
                nbase = peopleName.substring(0, 3);
                
                KqDBHelper kqDB = new KqDBHelper(this.frameconn);
                b0110 = kqDB.getEmpB0110(nbase, a0100);
            }

            String app_type = (String) this.getFormHM().get("app_type");//请假类型
            String app_way = (String) this.getFormHM().get("app_way");//按天/小时/区间申请
            String start_d = (String) this.getFormHM().get("start_d");
            String start_h = (String) this.getFormHM().get("start_h");
            String start_m = (String) this.getFormHM().get("start_m");
            String end_d = (String) this.getFormHM().get("end_d");

            String isshow = "";

            if (app_type != null && !"".equals(app_type.trim())) {
                if (KqParam.getInstance().isHoliday(this.frameconn, userView, app_type))
                    isshow = "1";
            }

            if ("".equals(peopleName) || peopleName == null) {
                isshow = "0";
            }

            float leftdays0fvacation = 0;
            if ("1".equals(isshow)) {
                Date startDate = null;
                Date endDate = null;
                GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.frameconn);
                AnnualApply annual = new AnnualApply(this.userView, this.frameconn);

                if ("0".equals(app_way)) //按天申请 
                {
                    String count = (String) this.getFormHM().get("date_count");
                    startDate = OperateDate.strToDate(start_d + " 00:00", "yyyy-MM-dd HH:mm");
                    endDate = ve.getEndTimeToQ15(startDate, count, app_way, app_type, nbase, peopleName.substring(3));
                } else if ("1".equals(app_way)) //按小时申请
                {
                    String count = (String) this.getFormHM().get("hr_count");
                    startDate = OperateDate.strToDate(start_d + " " + start_h + ":" + start_m, "yyyy-MM-dd HH:mm");
                    endDate = ve.getEndTimeToQ15(startDate, count, app_way, app_type, nbase, peopleName.substring(3));
                } else if ("2".equals(app_way)) //按区间申请
                {
                    startDate = OperateDate.strToDate(start_d, "yyyy-MM-dd HH:mm");
                    if ("".equals(end_d))
                        endDate = startDate;
                    else
                        endDate = OperateDate.strToDate(end_d, "yyyy-MM-dd HH:mm");
                }
                HashMap kqItem_hash = annual.count_Leave(app_type);
                float myTime = annual.getMy_Time(app_type, a0100, nbase, OperateDate.dateToStr(startDate, "yyyy.MM.dd HH:mm"),
                        OperateDate.dateToStr(endDate, "yyyy.MM.dd HH:mm"), b0110, kqItem_hash);
                leftdays0fvacation = (float)(Math.round(myTime*100))/100; //szk天数保留两位小数
            }
            this.getFormHM().put("leftdays0fvacation", String.valueOf(leftdays0fvacation));
            this.getFormHM().put("isshow", isshow);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
