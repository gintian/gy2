package com.hjsj.hrms.transaction.train.trainexam.question.type;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteQuesTypeTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
	   ArrayList selectedInfolist = (ArrayList)this.getFormHM().get("selectedinfolist");
	   ArrayList deleteList = new ArrayList();
		StringBuffer delStr = new StringBuffer();
		delStr.append("DELETE FROM tr_question_type");
		delStr.append(" WHERE type_id=?");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	for(int i=0;i<selectedInfolist.size();i++)
	        {
				
	    	    LazyDynaBean rec=(LazyDynaBean)selectedInfolist.get(i);   
	       	    String location_id=rec.get("type_id").toString();
	       	    ArrayList list=new ArrayList();
	       	    list.add(location_id);
	       	 deleteList.add(list);  	    
	       	          	              	
	        }
	    	dao.batchUpdate(delStr.toString(),deleteList);	 
	    }catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("delete.error"),"",""));
	    }
	}

}
