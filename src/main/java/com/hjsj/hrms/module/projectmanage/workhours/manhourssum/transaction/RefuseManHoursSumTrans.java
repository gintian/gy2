package com.hjsj.hrms.module.projectmanage.workhours.manhourssum.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manhourssum.businessobject.ManHoursSumBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title: RefuseManHoursSumTrans </p>
 * <p>Description:拒绝申请 </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-28 下午1:39:49</p>
 * @author liuyang
 * @version 1.0
 */
public class RefuseManHoursSumTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String manSumIdStrs = (String) this.getFormHM().get("manSumIdStrs");
            String text = (String) this.getFormHM().get("text");
            ManHoursSumBo bo = new ManHoursSumBo(this.frameconn, this.userView);
        
            bo.refuseManHoursSumApply(manSumIdStrs,text);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
