package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ManProjectHoursBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 删除项目或里程碑
 * <p>Title: DeleteProjectOrMilestoneTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-1-6 下午06:15:04</p>
 * @author zhaoxj
 * @version 1.0
 */
public class DeleteProjectOrMilestoneTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String projectIds = (String)this.getFormHM().get("projectIds");
            projectIds = this.decryptParam(projectIds);
            
            String milestoneIds = (String)this.getFormHM().get("milestoneIds");
            milestoneIds = this.decryptParam(milestoneIds);
            
            ManProjectHoursBo bo = new ManProjectHoursBo(this.getFrameconn(), this.userView);
            bo.deleteProject(projectIds);
            bo.deleteMilestone(milestoneIds);
            
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
        
        return decrytStr;
    }
}
