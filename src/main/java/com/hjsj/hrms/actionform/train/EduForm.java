package com.hjsj.hrms.actionform.train;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:培训课程</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-14:17:30:32</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class EduForm extends FrameForm {

	
		/**
		 * 分页Form
		 */
	
		private PaginationForm eduForm=new PaginationForm(); 
	          
	    /**
	     * 声明培训表对象
	     */
	    private RecordVo eduvo=new RecordVo("R31");
	    
	    
	    private ArrayList dynamicCol=new ArrayList();	//动态列名ArrayList
	    
		/**
		 * @return 返回 dynamicCol。
		 */
		public ArrayList getDynamicCol() {
			return dynamicCol;
		}
		/**
		 * @param dynamicCol 要设置的 dynamicCol。
		 */
		public void setDynamicCol(ArrayList dynamicCol) {
			this.dynamicCol = dynamicCol;
		}
	       
	  
	    @Override
        public void outPutFormHM() {
	    	
	        this.getEduForm().setList((ArrayList)this.getFormHM().get("edulist"));
	        this.setDynamicCol((ArrayList)this.getFormHM().get("dynamicCol"));
	            	        
	    }

	    /* 
	     * 
	     */
	    @Override
        public void inPutTransHM() {
		  
	    	this.getFormHM().put("dynamicCol",this.getDynamicCol());
	    }

	    /**
	     * @return Returns the eduForm.
	     */
	    public PaginationForm getEduForm() {
	        return eduForm;
	    }
	    /**
	     * @param eduForm The eduForm to set.
	     */
	    public void setEduForm(PaginationForm eduForm) {
	        this.eduForm = eduForm;
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
