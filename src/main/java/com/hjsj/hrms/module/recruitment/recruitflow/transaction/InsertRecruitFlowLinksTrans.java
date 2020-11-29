package com.hjsj.hrms.module.recruitment.recruitflow.transaction;


import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.AddRecruitFlowBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:插入招聘流程环节
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-12 上午10:52:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class InsertRecruitFlowLinksTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String node_id = (String) this.getFormHM().get("node_id");
            String custom_name = (String) this.getFormHM().get("custom_name");
            String seq = (String) this.getFormHM().get("seq");
            String flowid = (String) this.getFormHM().get("flowid");
            if (flowid != null && flowid.length() > 0)
            	flowid = PubFunc.decrypt(flowid);
            int xh = Integer.parseInt(seq);
            String[] nodeids = node_id.split(",");
            String[] nodeNames = custom_name.split(","); 
            FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
            AddRecruitFlowBo addRecruitFlowBo = new AddRecruitFlowBo(this.frameconn, this.userView);
            /**
             * 更新插入节点之后的流程环节序号
             */
            insertLinkBo.upAfterSeq(nodeids, flowid, xh);
            /**
             * 向zp_flow_links插入数据(插入的流程环节)
             */
            if(StringUtils.isNotEmpty(node_id)&&StringUtils.isNotEmpty(custom_name)){
            	insertLinkBo.insertLinks(addRecruitFlowBo,nodeids, nodeNames, flowid, xh);
            }
            StringBuffer records = insertLinkBo.getLinkInfos(flowid,"≮");
            this.getFormHM().put("records", records.toString());
            this.getFormHM().put("seq", xh);
            this.getFormHM().put("msg", "success");
        } catch (Exception e) {
        	this.getFormHM().put("msg", "failure");
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }
}
