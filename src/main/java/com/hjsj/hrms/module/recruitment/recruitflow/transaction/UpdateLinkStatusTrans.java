package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:修改招聘流程环节状态。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-9 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class UpdateLinkStatusTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	ArrayList ids = (ArrayList) this.getFormHM().get("ids");
            ArrayList seqs = (ArrayList) this.getFormHM().get("seqs");
            ArrayList custom_names = (ArrayList) this.getFormHM().get("custom_names");
            ArrayList valids = (ArrayList) this.getFormHM().get("valids");
            String linkid = (String) this.getFormHM().get("linkid");
            ArrayList resume_modifys = (ArrayList) this.getFormHM().get("resume_modify");
            FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
            insertLinkBo.updateLinkFucs("zp_flow_status",ids, seqs, custom_names, valids,resume_modifys,null,linkid);
            this.getFormHM().put("message", "success");
            
        } catch (Exception e) {
        	this.getFormHM().put("message", "failure");
            e.printStackTrace();
        }
    }

}
