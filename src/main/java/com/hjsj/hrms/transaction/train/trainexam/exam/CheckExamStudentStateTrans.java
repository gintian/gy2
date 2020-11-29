package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamStudentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:检查培训学员状态（收卷时有未考需提示）
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
public class CheckExamStudentStateTrans extends IBusiness
{

    public void execute() throws GeneralException
    {    
    	String flag = "ok";    	
    	int stuCnt = 0; 
    		
		String r5400s = (String)this.getFormHM().get("r5400");
		
		//需要检查的状态类别（"exam":考试状态 "paper":试卷状态）
		String stateType = (String)this.getFormHM().get("statetype");
		
		String[] planIds = r5400s.split(",");
		
		TrainExamPlanBo plan = new TrainExamPlanBo(this.frameconn);
		TrainExamStudentBo bo = new TrainExamStudentBo(this.frameconn);
		
		String r5400;
		String planNames = "";
		for(int i=0; i<planIds.length; i++)
		{
			r5400 = PubFunc.decrypt(SafeCode.decode(planIds[i].trim()));
			if("exam".equals(stateType))
			    stuCnt = bo.getUnFinishExamStudentCount(r5400);
			else
			{				
			    stuCnt = bo.getUnCheckExamStudentCount(r5400);
			    if(stuCnt > 0)
			    	planNames = planNames + plan.getPlanName(r5400) + "\\n";
			}
			
			if(stuCnt > 0)
				flag = "error";			
		}		
		
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("id", r5400s);
		this.getFormHM().put("plannames", planNames);
    }

}
