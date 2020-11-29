package com.hjsj.hrms.module.projectmanage.workhours.manhourssum.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manhourssum.businessobject.ManHoursSumBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

public class AccedeManHoursSumTrans extends IBusiness {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void execute() throws GeneralException {
        try {
            
            ManHoursSumBo bo = new ManHoursSumBo(this.frameconn, this.userView);
            String manSumIdStrs = (String) this.getFormHM().get("manSumIdStrs");
            String type = (String) this.getFormHM().get("type");
            String text = (String) this.getFormHM().get("text");
            if("refuse".equals(type)){
                bo.refuseManHoursSumApply(manSumIdStrs,text);
                bo.sendMsgToAll(manSumIdStrs,text,type);
            }
            else if("dele".equals(type)){
                String manDetailStrs = (String) this.getFormHM().get("manDetailStrs");
                String landMarkStrs = (String) this.getFormHM().get("landMarkStrs");
                String projectId = (String) this.getFormHM().get("projectId");
                if(StringUtils.isNotEmpty(projectId))
                    projectId = PubFunc.decrypt(projectId);
                bo.deleManHoursSumApply(manSumIdStrs, manDetailStrs, projectId,landMarkStrs);
            }
            else {
                String manDetailStrs = (String) this.getFormHM().get("manDetailStrs");
                String landMarkStrs = (String) this.getFormHM().get("landMarkStrs");
                String projectId = (String) this.getFormHM().get("projectId");
                if(StringUtils.isNotEmpty(projectId))
                    projectId = PubFunc.decrypt(projectId);
                bo.accedeManHoursSumApply(manSumIdStrs, manDetailStrs, projectId,landMarkStrs,text);
                bo.sendMsgToAll(manSumIdStrs,text,type);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
}
