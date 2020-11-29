package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:结束培训考试计划（收卷）
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-12-12 11:10:00
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class FinishExamPlanTrans extends IBusiness
{

    public void execute() throws GeneralException
    {    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5400 = hm.get("r5400").toString();
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn);		
		bo.finishExamPlan(r5400);		
    }

}
