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
public class MoveRecruitFlowLinksTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String seq = (String) this.getFormHM().get("seq");
            String flag = (String) this.getFormHM().get("flag");
            String linkid = (String) this.getFormHM().get("linkid");
            String othLinkId = (String) this.getFormHM().get("othLinkId");
            
            RecruitflowBo rfb = new RecruitflowBo(this.frameconn, this.userView);
            rfb.moveSeq(seq, linkid, flag, othLinkId);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }
    
}
