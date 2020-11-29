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

public class ReExamStudentTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String planId = (String)hm.get("planid");
		planId = PubFunc.decrypt(SafeCode.decode(planId));
		
	    ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
	    ArrayList<ArrayList<String>> paramtList = new ArrayList<ArrayList<String>>();
		StringBuffer uptStr = new StringBuffer();
		StringBuffer delStr = new StringBuffer();
		
		uptStr.append("UPDATE R55 SET R5501=NULL,R5503=NULL,R5504=NULL,R5506=NULL,R5507=NULL,R5509=NULL,R5510=NULL,R5513=-1,R5515=-1,R5517=NULL");
		uptStr.append(" WHERE R5400=? AND UPPER(NBASE)=? AND A0100=?");
		
		delStr.append("DELETE FROM tr_exam_answer WHERE exam_type=2 AND exam_no=? AND UPPER(NBASE)=? AND A0100=?");
		
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	for(int i=0;i<selectedInfolist.size();i++)
	        {				
	    	    LazyDynaBean rec = (LazyDynaBean)selectedInfolist.get(i);   
	       	    String a0100 = rec.get("a0100").toString();
	       	    String nbae = rec.get("nbase").toString().toUpperCase();
	       	    
	       	    ArrayList<String> list = new ArrayList<String>();
	       	    list.add(planId);
	       	    list.add(nbae);
	       	    list.add(a0100);
	       	   
	       	    paramtList.add(list);  		       	          	              	
	        }
	    	
	    	dao.batchUpdate(uptStr.toString(),paramtList);	 
	    	dao.batchUpdate(delStr.toString(),paramtList);
	    	
	    	TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn, this.userView);
	    	bo.updatePendingTask(paramtList, 0);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.examstudent.redoexam.error"),"",""));
	    }
	}
}
