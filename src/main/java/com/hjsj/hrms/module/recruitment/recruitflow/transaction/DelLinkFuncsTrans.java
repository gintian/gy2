package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.FlowLinksBo;
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
 * Description:删除招聘流程环节的可用操作。
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
public class DelLinkFuncsTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	String id = (String) this.getFormHM().get("id");
        	String linkid = (String) this.getFormHM().get("linkid");
        	String seq = (String) this.getFormHM().get("seq");
        	
        	RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
        	FlowLinksBo flowLinksBo = new FlowLinksBo(this.frameconn, this.userView);
    		recruitflowBo.delLinkFuncs(id,linkid,seq);
    		
    		StringBuffer jsonStr = flowLinksBo.getLinkTableFuns(linkid, ";");
    		
    		this.getFormHM().put("jsonStr", jsonStr.toString());
    		this.getFormHM().put("msg", "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("msg", "删除失败!");
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
