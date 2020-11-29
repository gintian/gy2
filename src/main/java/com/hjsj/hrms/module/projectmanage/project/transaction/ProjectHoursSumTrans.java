package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ManProjectHoursBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ProjectHoursSumTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String projectIds = (String) this.getFormHM().get("projectIds"); 
            projectIds = this.decryptParam(projectIds);
            
            String milestoneIds = (String) this.getFormHM().get("milestoneIds");
            milestoneIds = this.decryptParam(milestoneIds);
            
            ManProjectHoursBo bo = new ManProjectHoursBo(this.frameconn,this.userView);
            
            //汇总前台所选里程碑
            if(milestoneIds != null && milestoneIds.length()>0)
                bo.sumData("p12",milestoneIds);
            
            //汇总前台所选项目数据（需同时汇总项目成员工时，里程碑工时，项目工时）
            if(projectIds != null && !"".equals(projectIds)) {
                bo.sumData("p13", "SELECT P1301 FROM P13 WHERE P1101 IN (" + projectIds + ")");
                bo.sumData("p12", "SELECT P1201 FROM P12 WHERE P1101 IN (" + projectIds + ")");
                bo.sumData("p11", projectIds);
            }
            
            this.getFormHM().put("msg", "success");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    private String decryptParam(String param) {
        String decrytStr = "";
        if(param == null || "".equals(param))
            return decrytStr;

        String[] ids = param.split(",");
        for (int i=0; i<ids.length; i++){
            String id = ids[i];
            if (id == null || "".equals(id.trim()))
                continue;
            
            decrytStr = decrytStr + PubFunc.decryption(id) + ",";
        }
        
        if(!"".equals(decrytStr))
            decrytStr = decrytStr.substring(0, decrytStr.length() - 1);
            
        return decrytStr;
    }
}
