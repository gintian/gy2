package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:保存培训考试计划
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-19 13:00:00
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class SetExamPlanParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5400 = (String) hm.get("r5400");
		this.getFormHM().put("r5400", r5400);
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		
		TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn);
		ArrayList msgTmpLst = planBo.getMessageTmpList(r5400);		
		this.getFormHM().put("messageTmpList", msgTmpLst);
		planBo.loadMessageParam(r5400);
		this.getFormHM().put("messageTmp", planBo.getMessageTmpId());
		this.getFormHM().put("emailEnable", planBo.getEmailEnable());
		this.getFormHM().put("smsEnable", planBo.getSmsEnable());
		this.getFormHM().put("weixinEnable", planBo.getWeixinEnable());
		this.getFormHM().put("messageSue", planBo.getMessageSueId());
		this.getFormHM().put("autoCompute", planBo.getAutoCompute());
		this.getFormHM().put("autoRelease", planBo.getAutoRelease());
		this.getFormHM().put("enabled", planBo.getEnabled());
		this.getFormHM().put("times", planBo.getTimes());
		this.getFormHM().put("dingTalk", planBo.getDingTalk());
		this.getFormHM().put("pendingTask", planBo.getPendingTaskEnable());
	    
    }
}
