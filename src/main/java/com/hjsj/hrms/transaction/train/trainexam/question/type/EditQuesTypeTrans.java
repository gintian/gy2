package com.hjsj.hrms.transaction.train.trainexam.question.type;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class EditQuesTypeTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		
	     String e_flag=(String)this.getFormHM().get("e_flag");

	     String type_id=(String)this.getFormHM().get("type_id");
	     RecordVo vo=new RecordVo("tr_question_type");
	     ContentDAO dao=new ContentDAO(this.getFrameconn());	     
	     try
	     {
	    	 if(e_flag==null||e_flag.length()<=0)
		     {
		    	 e_flag="add";
		     }
		     if("up".equalsIgnoreCase(e_flag))
		     {
		    	 vo.setString("type_id",type_id);
		   	     vo=dao.findByPrimaryKey(vo);		   	   
		     }
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.quesType.error"),"",""));
	     }
	     this.getFormHM().put("quesType",vo);
	     this.getFormHM().put("type_id",type_id);
	     this.getFormHM().put("e_flag",e_flag);
	}

}
