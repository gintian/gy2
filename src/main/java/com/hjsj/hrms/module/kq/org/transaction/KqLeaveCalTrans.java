package com.hjsj.hrms.module.kq.org.transaction;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.kq.org.businessobject.KqLeaveCalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class KqLeaveCalTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String relationId = KqParam.getInstance().getCardWfRelation(); //审批关系id
            if ("#".equals(relationId) || StringUtils.isEmpty(relationId)) {
                relationId = "0";
            }

            String spid = relationId;
            String month = (String) this.getFormHM().get("month");
            String scope = (String) this.getFormHM().get("scope");
            String leaveTypes = (String) this.getFormHM().get("leaveTypes");
            KqLeaveCalBo kcBo = new KqLeaveCalBo(this.frameconn, this.userView);
            //请假种类，和定义的颜色
            ArrayList<HashMap> leaveType = kcBo.kqLeaveType(leaveTypes);
            //销假与请假进行时间段合并之后的数据
            ArrayList<HashMap> leaveInfo = kcBo.kqLeaveInfo(spid, leaveTypes, scope, month);
            this.getFormHM().put("leaveType", leaveType);
            this.getFormHM().put("leaveInfo", leaveInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

