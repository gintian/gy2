package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
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
 * Description:判断招聘流程是否有招聘过程数据
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
public class IsFlowUsedTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String flowid = (String) this.getFormHM().get("flowid");
            String delLinks = (String) this.getFormHM().get("delLinks");
            if (flowid != null && flowid.length() > 0)
                flowid = PubFunc.decrypt(flowid);
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
        	String message ="";
        	if(StringUtils.isNotEmpty(flowid))//判断流程是否有招聘数据
        		message = recruitflowBo.isUsedInProcess(flowid);
        	else if(StringUtils.isNotEmpty(delLinks)){//判断环节是否有招聘数据
        		for (int i = 0; i < delLinks.split(",").length; i++) {
        			message = recruitflowBo.isLinkUsed(delLinks.split(",")[i]);
        			if(StringUtils.isNotEmpty(message))
        				break;
				}
        	}
        	this.getFormHM().put("msg", message);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
