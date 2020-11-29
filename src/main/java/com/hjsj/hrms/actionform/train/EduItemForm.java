package com.hjsj.hrms.actionform.train;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:培训项目Form</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-16:17:07:32</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class EduItemForm extends FrameForm {

	
	/**
	 * 培训活动表id
	 */
    String eduid="0";
       
    /**
     * 声明窗体对象
     */
    
    private PaginationForm eduItemForm=new PaginationForm();     
  
    @Override
    public void outPutFormHM() {
        this.getEduItemForm().setList((ArrayList)this.getFormHM().get("eduItemlist"));
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
     * @return Returns the eduItemForm.
     */
    public PaginationForm getEduItemForm() {
        return eduItemForm;
    }
    /**
     * @param eduItemForm The eduItemForm to set.
     */
    public void setEduItemForm(PaginationForm eduItemForm) {
        this.eduItemForm = eduItemForm;
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
