package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteExamStudentTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String planId = (String)hm.get("planid");
		planId =PubFunc.decrypt(SafeCode.decode(planId));
		
	    ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
	    ArrayList<ArrayList<String>> deleteList = new ArrayList<ArrayList<String>>();
		
	    StringBuffer delStr = new StringBuffer();
		delStr.append("DELETE FROM R55");
		delStr.append(" WHERE R5400=? AND NBASE=? AND A0100=?");
		
		StringBuffer delAnswer = new StringBuffer();
		delAnswer.append("DELETE FROM tr_exam_answer");
		delAnswer.append(" WHERE EXAM_NO=? AND EXAM_TYPE=2");
		delAnswer.append(" AND NBASE=? AND A0100=?");
		
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	for(int i=0;i<selectedInfolist.size();i++)
	        {				
	    	    LazyDynaBean rec = (LazyDynaBean)selectedInfolist.get(i);   
	       	    String a0100 = rec.get("a0100").toString();
	       	    String nbae = rec.get("nbase").toString();
	       	    
	       	    ArrayList<String> list = new ArrayList<String>();
	       	    list.add(planId);
	       	    list.add(nbae);
	       	    list.add(a0100);
	       	   
	       	    deleteList.add(list);  	    
	       	          	              	
	        }
	    	dao.batchUpdate(delStr.toString(),deleteList);	 
	    	dao.batchUpdate(delAnswer.toString(),deleteList);	
	    	TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn, this.userView);
	    	bo.deleteStudentsPendingTask(deleteList);
	    }
	    catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("delete.error"),"",""));
	    }
	}

}
