package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class GetSelfLeftDaysOfVacationTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            DbWizard dbWizard = new DbWizard(this.getFrameconn());
            if (dbWizard.isExistTable("Q33", false)) {
                String start_d = ((String) this.getFormHM().get("start_d")) + " 00:00";
                Date start_date = OperateDate.strToDate(start_d, "yyyy-MM-dd HH:mm");
                
                String leaveUsedOvertime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
                HashMap kqItem_hash = annualApply.count_Leave(leaveUsedOvertime);
                String fielditemid = (String) kqItem_hash.get("fielditemid");
                int unit = 1;
                if (fielditemid != null && fielditemid.length() > 0) 
				{
					FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
					unit = fieldItem.getDecimalwidth();
				}
                
                GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.frameconn);
                int timesCount = ve.getTimesCount(start_date, this.userView.getDbname(), this.userView.getA0100(), this.frameconn);
                BigDecimal bg = new BigDecimal(timesCount/60.0);
                String len = bg.setScale(unit, BigDecimal.ROUND_HALF_UP).toString();//调休可用时长

                this.getFormHM().put("usableTime", len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
