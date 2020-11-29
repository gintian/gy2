package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:删除招聘流程环节的工作职责描述。
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
public class DelFlowLinkTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	ArrayList ids = (ArrayList) this.getFormHM().get("ids");
        	ArrayList seqs = (ArrayList) this.getFormHM().get("seqs");
        	ArrayList custom_names = (ArrayList) this.getFormHM().get("custom_names");
        	String flowid = (String) this.getFormHM().get("flowid");
            if(flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
        	
        	RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
    		recruitflowBo.delFlowLink(ids);
    		recruitflowBo.upSeq(flowid);
    		/**
    		 * 生成前台表格数据字符串
    		 */
    		FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
    		StringBuffer records = insertLinkBo.getLinkInfos(flowid,"≮");
            this.getFormHM().put("records", records.toString());
    		
        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("msg", "删除失败!");
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
