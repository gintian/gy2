package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:保存培训考试计划参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-12-09 13:00:00
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class SaveExamPlanParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	String flag = "ok";
    	
		String r5400 = (String) this.getFormHM().get("r5400");
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		String email = (String)this.getFormHM().get("email");
		String sms = (String)this.getFormHM().get("sms");
		String tmpId = (String)this.getFormHM().get("tmp");
		String SueId = (String)this.getFormHM().get("tmp1");
		String autoCompute = (String)this.getFormHM().get("autoCompute");
		String autoRelease = (String)this.getFormHM().get("autoRelease");
		String weixin = (String) this.getFormHM().get("weixin");
		String dingTalk = (String) this.getFormHM().get("dingTalk");
		String pendingTask = (String) this.getFormHM().get("pendingTask");
		
		String enabled = (String)this.getFormHM().get("enabled");
		if(enabled == null || enabled.length()<1)
		    enabled = "false";
		
		String times = (String)this.getFormHM().get("times");
		if(times == null || times.length()<1)
		    times = "0";
		
		TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn);
		if("false".equalsIgnoreCase(email))
			bo.setEmailEnable(Boolean.FALSE);
		else
			bo.setEmailEnable(Boolean.TRUE);
		
		if("false".equalsIgnoreCase(sms))
			bo.setSmsEnable(Boolean.FALSE);
		else
			bo.setSmsEnable(Boolean.TRUE);
		
		if("false".equalsIgnoreCase(weixin))
			bo.setWeixinEnable(Boolean.FALSE);
		else
			bo.setWeixinEnable(Boolean.TRUE);
		
		if("false".equalsIgnoreCase(dingTalk))
		    bo.setDingTalk(Boolean.FALSE);
		else
		    bo.setDingTalk(Boolean.TRUE);
		
		if("false".equalsIgnoreCase(pendingTask))
		    bo.setPendingTaskEnable(Boolean.FALSE);
		else
		    bo.setPendingTaskEnable(Boolean.TRUE);
		
		bo.setMessageTmpId(tmpId);
		
		bo.setMessageSueId(SueId);
		bo.setAutoCompute("false".equalsIgnoreCase(autoCompute)?Boolean.FALSE:Boolean.TRUE);
		bo.setAutoRelease("true".equalsIgnoreCase(autoRelease)?Boolean.TRUE:Boolean.FALSE);
		
        bo.setEnabled("false".equalsIgnoreCase(enabled)?Boolean.FALSE:Boolean.TRUE);
		bo.setTimes(times);
		bo.saveMessageParam(r5400);
		
		this.getFormHM().put("flag", flag);
    }

}
