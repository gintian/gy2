/*
 * Created on 2005-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.propose;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConsulantForm extends FrameForm {
	 
	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        
        String userAdmin="false";
	    /**
	     * 建议对象
	     */
	    private RecordVo consulantvo=new RecordVo("CONSULTATION");
	    /**
	     * 建议对象列表
	     */
	    private PaginationForm consulantForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	        this.setConsulantvo((RecordVo)this.getFormHM().get("cousulantTb"));
	        this.getConsulantForm().setList((ArrayList)this.getFormHM().get("consulantlist"));
	        this.setUserAdmin(Boolean.toString(this.userView.isSuper_admin()));
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
		    this.getFormHM().put("selectedlist",(ArrayList)this.getConsulantForm().getSelectedList());
	        this.getFormHM().put("cousulantov",this.getConsulantvo());
	        this.getFormHM().put("cousulantTb",this.getConsulantvo());
	        this.getFormHM().put("flag",this.getFlag());  
	        this.getFormHM().put("userAdmin",this.getUserAdmin());
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	    public PaginationForm getConsulantForm() {
	        return consulantForm;
	    }
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	    public void setConsulantForm(PaginationForm consulantForm) {
	        this.consulantForm = consulantForm;
	    }
	    /**
	     * @return Returns the consulantvo.
	     */
	    public RecordVo getConsulantvo() {
	        return consulantvo;
	    }
	    /**
	     * @param proposevo The consulantvo to set.
	     */
	    public void setConsulantvo(RecordVo consulantvo) {
	        this.consulantvo = consulantvo;
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
	        /**
	         * 新建
	         */
	        if("/selfservice/propose/searchconsulant".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	  this.setFlag("1");
	        	  this.getConsulantvo().clearValues();
	        }
	        /**
	         * 编辑
	         */
	        if("/selfservice/propose/addconsulant".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	 this.setFlag("0");
	        }
	        /**
	         * 答复
	         */
	        if("/selfservice/propose/replyconsulant".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	this.setFlag("2");
	        }
	        /**
	         * 查阅答复
	         */
	        if("/selfservice/propose/viewconsulant".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	this.setFlag("2");
	        }         
	        return super.validate(arg0, arg1);
	    }
	    /**
	     * @return Returns the annoymous.
	     */
	   
	    public String getFlag() {
	        return flag;
	    }
	    
	    /**
	     * @param flag The flag to set.
	     */
	   
	    public void setFlag(String flag) {
	        this.flag = flag;
	    }
	   
	  
	   

		/**
		 * @return 返回 userAdmin。
		 */
		public String getUserAdmin() {
			return userAdmin;
		}
		/**
		 * @param userAdmin 要设置的 userAdmin。
		 */
		public void setUserAdmin(String userAdmin) {
			this.userAdmin = userAdmin;
		}
}
