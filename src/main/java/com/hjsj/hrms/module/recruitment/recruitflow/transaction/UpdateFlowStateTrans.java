package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:保存招聘流程的工作职责描述。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-8 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class UpdateFlowStateTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("flag");
            String flowid = (String) this.getFormHM().get("flowid");
            if (flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);

            String sql = "update zp_flow_definition set valid=? where flow_id=?";
            ArrayList values = new ArrayList();
            values.add(flag);
            values.add(flowid);
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.update(sql, values);
            this.getFormHM().put("message", "success");
        } catch (Exception e) {
        	this.getFormHM().put("message", "failure");
            e.printStackTrace();
        }
    }

}
