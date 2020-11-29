package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteExamPlanTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String forceDel = (String)hm.get("fd");
		
	    ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
	    ArrayList<ArrayList<String>> deleteList = new ArrayList<ArrayList<String>>();
		StringBuffer delStr = new StringBuffer();
		StringBuffer delStuStr = new StringBuffer();
		StringBuffer delStuAnswer = new StringBuffer();
		StringBuffer hintInfo = new StringBuffer();
		
		delStr.append("DELETE FROM R54 WHERE R5400=?");
		
		delStuStr.append("DELETE FROM R55 WHERE R5400=?");
		
		delStuAnswer.append("DELETE FROM tr_exam_answer WHERE EXAM_NO=? AND EXAM_TYPE=2");
		
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    boolean isOk = false;	    
	    try
	    {
	    	for(int i = 0; i < selectedInfolist.size(); i++)
	        {
				
	    	    LazyDynaBean rec=(LazyDynaBean)selectedInfolist.get(i);   
	       	    String r5400 = rec.get("r5400").toString();
	       	    String status = rec.get("r5411").toString();
	       	    String r5401 = rec.get("r5401").toString();
	       	    
	       	    if("0".equals(forceDel))
	       	    {
		       	    if("01".equalsIgnoreCase(status)|| "09".equalsIgnoreCase(status))
		       	    {
			       	    ArrayList<String> list = new ArrayList<String>();
			       	    list.add(r5400);
			       	    deleteList.add(list);
			       	}
		       	    else
		       	    {
		       	    	if("".equals(hintInfo.toString()))
		       	    	{
		       	    		hintInfo.append("以下计划未删除，原因：计划状态不是起草或暂停：");
		       	    		hintInfo.append("\n");
		       	    	}
		       	    		
		       	    	hintInfo.append(r5401);
		       	    	hintInfo.append("\n");
		       	    }
	       	    }
	       	    else
	       	    {
	       	    	ArrayList<String> list = new ArrayList<String>();
		       	    list.add(r5400);
	       	    	deleteList.add(list);
	       	    }
	       	          	              	
	        }	    	
	    	this.frameconn.setAutoCommit(false);
	    	//删除计划
	    	dao.batchUpdate(delStr.toString(), deleteList);
	    	//删除计划考试人员
	    	dao.batchUpdate(delStuStr.toString(), deleteList);
	    	//删除答案数据
	    	dao.batchUpdate(delStuAnswer.toString(), deleteList);
	    	//删除待办
	    	TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn, this.userView);
	    	bo.deletePendingTask(deleteList);
	    	isOk = true;
	    	
	    	this.frameconn.commit();
	    }
	    catch(Exception e)
	    {

            try
	    	{
	    	    this.frameconn.rollback();
	    	}
	        catch(Exception ec)
	        {
	        	ec.printStackTrace();
	        }
    	        
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("delete.error"),"",""));	    	
	    }
	    finally
        {
            try
            {
                this.frameconn.setAutoCommit(true);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
	    if(isOk&&(!"".equals(hintInfo.toString())))
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",hintInfo.toString(),"",""));
	    }
	}

}
