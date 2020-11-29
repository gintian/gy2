package com.hjsj.hrms.actionform.train;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:培训学员Form</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-17:14:23:02</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class StudentForm extends FrameForm {

	/**
	 * 活动表id
	 */
    String eduid="0";
       
    /**
     * 声明窗体对象
     */
    
    private PaginationForm studentForm=new PaginationForm();     
  
    @Override
    public void outPutFormHM() {
        this.getStudentForm().setList((ArrayList)this.getFormHM().get("studentlist"));
        this.setEduid(this.getFormHM().get("eduid").toString());    	        
    }

    
    @Override
    public void inPutTransHM() {
    	
    	this.getFormHM().put("eduid",this.getEduid());
	  
    }
    /**
     * 活动表id属性
     * @param eduid
     */
    public void setEduid(String eduid)
    {
    	this.eduid=eduid;
    }
    
    public String getEduid()
    {
    	return this.eduid;
    }
    /**
     * @return Returns the studentForm.
     */
    public PaginationForm getStudentForm() {
        return studentForm;
    }
    /**
     * @param studentForm The studentForm to set.
     */
    public void setStudentForm(PaginationForm studentForm) {
        this.studentForm = studentForm;
    }
    
    /* 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);
    }
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
              
        return super.validate(arg0, arg1);
    }
  
  	   

}
