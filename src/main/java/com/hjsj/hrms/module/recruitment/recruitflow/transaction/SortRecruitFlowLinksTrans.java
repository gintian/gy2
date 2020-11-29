package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:调整招聘流程环节顺序
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
public class SortRecruitFlowLinksTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String position = (String) this.getFormHM().get("position");
            ArrayList dragIds = (ArrayList) this.getFormHM().get("dragIds");
            ArrayList dragUpSeqs = (ArrayList) this.getFormHM().get("dragUpSeqs");
            ArrayList leftIds = (ArrayList) this.getFormHM().get("leftIds");
            ArrayList updateSeqs = (ArrayList) this.getFormHM().get("updateSeqs");
            String flowid = (String) this.getFormHM().get("flowid");
            if(flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
            
            RecruitflowBo rfb = new RecruitflowBo(this.frameconn, this.userView);
            rfb.sortSeq(leftIds, dragIds, updateSeqs,dragUpSeqs,position);
            /**
    		 * 生成前台表格数据字符串
    		 */
    		FlowLinksBo insertLinkBo = new FlowLinksBo(this.frameconn,this.userView);
    		StringBuffer records = insertLinkBo.getLinkInfos(flowid,"≮");
            this.getFormHM().put("records", PubFunc.keyWord_reback(records.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }
    
}
