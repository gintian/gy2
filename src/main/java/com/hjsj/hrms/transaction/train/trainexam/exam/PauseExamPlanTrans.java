package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class PauseExamPlanTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
		ArrayList<String> planIds = new ArrayList<String>();
    	for(int i=0; i<selectedInfolist.size(); i++)
        {	
    	    LazyDynaBean rec = (LazyDynaBean)selectedInfolist.get(i);   
       	    String r5400 = rec.get("r5400").toString();
       	    planIds.add(r5400);

        }
    	
    	TrainExamPlanBo bo = new TrainExamPlanBo(this.getFrameconn(), this.userView);
    	boolean isOk = bo.pauseExamPlan(planIds);
    	if (!isOk)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.examplan.start.error"),"",""));
    	}
	}

}
