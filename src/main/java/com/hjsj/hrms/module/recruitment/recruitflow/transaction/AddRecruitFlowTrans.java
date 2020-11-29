package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.AddRecruitFlowBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:新建招聘流程
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-7 上午10:52:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class AddRecruitFlowTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String flowid = (String) this.getFormHM().get("flowid");
            if(flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
            String name = (String) this.getFormHM().get("flowName");
            name = PubFunc.keyWord_filter(name);
            String description = (String) this.getFormHM().get("description");
            String node_id = (String) this.getFormHM().get("node_id");
            String custom_name = (String) this.getFormHM().get("custom_name");
            String b0110 = (String) this.getFormHM().get("codeinput_value");
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
            AddRecruitFlowBo addRecruitFlowBo = new AddRecruitFlowBo(this.frameconn, this.userView);
            if(b0110.indexOf("UN`")!=-1){
            	b0110 = b0110.replace("UN`", "");
            }
            if(b0110.indexOf("UM`")!=-1){
            	b0110 = b0110.replace("UM`","");
            }
            /**
             * 向zp_flow_definition中插入数据
             */
            addRecruitFlowBo.maintainDefinetion(flowid, name, description, b0110);
            /**
             * 向zp_flow_links插入数据
             */
            addRecruitFlowBo.maintainLinks(node_id, custom_name, flowid);
            this.getFormHM().put("message", "success");
        } catch (Exception e) {
        	this.getFormHM().put("message", "failure");
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }
   
}
