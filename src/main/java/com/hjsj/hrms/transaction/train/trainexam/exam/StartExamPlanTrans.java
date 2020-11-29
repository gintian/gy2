package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class StartExamPlanTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String sendMsg = (String)hm.get("sendmsg");
		
		TrainExamPlanBo bo = new TrainExamPlanBo(this.getFrameconn(),this.getUserView());
		StringBuffer hintInfo = new StringBuffer();		
		
		ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
		ArrayList planIds = new ArrayList();
    	for(int i=0; i<selectedInfolist.size(); i++)
        {	
    	    LazyDynaBean rec = (LazyDynaBean)selectedInfolist.get(i);   
       	    String r5400 = rec.get("r5400").toString();
       	    String r5401 = rec.get("r5401").toString();
       	    
       	    if(bo.existStudentInExamPlan(r5400))
       	    {
       	    	planIds.add(r5400);
       	    }
       	    else
       	    {
       	    	if("".equals(hintInfo.toString()))
       	    	{
       	    		hintInfo.append("以下计划没有安排考试人员，不能启动：");
       	    		hintInfo.append("\n");
       	    	}
       	    		
       	    	hintInfo.append(r5401);
       	    	if(i < selectedInfolist.size() -1){
       	    		hintInfo.append("，");
       	    	}
       	    	hintInfo.append("\n");
       	    }

        }
    	
    	boolean isOk = true;
    	if(planIds.size() > 0)
    	{
    		try {
    			isOk = bo.startExamPlan(planIds, sendMsg);
    		} catch (Exception e) {
    			throw GeneralExceptionHandler.Handle(e);
    		}
    	}

    	if (!isOk)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.examplan.start.error"),"",""));
    	}
    	else if(!"".equals(hintInfo.toString()))
    	{    		
    		throw GeneralExceptionHandler.Handle(new GeneralException("",hintInfo.toString(),"",""));
    	}
	}
}
