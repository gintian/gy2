package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

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
public class DelFlowTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String flowid = (String) this.getFormHM().get("flowid");
            if (flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
    		recruitflowBo.delFlow(flowid);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
