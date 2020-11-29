package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:修改招聘流程环节的工作职责描述。
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
public class UpdateFlowLinkTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	ArrayList ids = (ArrayList) this.getFormHM().get("ids");
            ArrayList seqs = (ArrayList) this.getFormHM().get("seqs");
            ArrayList custom_names = (ArrayList) this.getFormHM().get("custom_names");
            ArrayList remarks = (ArrayList) this.getFormHM().get("remarks");
            ArrayList org_flags = (ArrayList) this.getFormHM().get("org_flags");
            ArrayList valids = (ArrayList) this.getFormHM().get("valids");
            String flowid = (String) this.getFormHM().get("flowid");
            if(flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
            
            FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
            insertLinkBo.updateLinks(ids, seqs, custom_names, remarks, org_flags, valids, flowid);
            this.getFormHM().put("message", "success");
            
        } catch (Exception e) {
        	this.getFormHM().put("message", "failure");
            e.printStackTrace();
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
