package com.hjsj.hrms.module.projectmanage.workhours.manhoursdetail.transaction;

import com.hjsj.hrms.module.projectmanage.workhours.manhoursdetail.businessobject.ManHoursDetailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 
 * <p>Title: ManHoursDetailUpdateTrans </p>
 * <p>Description: 项目人员信息</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-28 下午1:15:16</p>
 * @author liuyang
 * @version 1.0
 */
public class ManHoursDetailUpdateTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        
        try {
            // 项目Id
            String projectId = (String) this.getFormHM().get("P1101");
            if(StringUtils.isNotEmpty(projectId)){
                projectId = PubFunc.decrypt(projectId);
            }
            ArrayList dataList = (ArrayList) this.getFormHM().get("dateList");

            ManHoursDetailBo bo = new ManHoursDetailBo(this.frameconn, this.userView);
            // 修改
            String tip = bo.updateManHoursDetail(projectId, dataList);

            this.getFormHM().put("tip", tip);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
