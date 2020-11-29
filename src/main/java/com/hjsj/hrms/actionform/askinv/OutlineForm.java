/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.askinv;

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
public class OutlineForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        
        private String itemid="0";
        private String itemName="0";
        private String oldName = "";
        
        
       
	    /**
         * @return the oldName
         */
        public String getOldName() {
            return oldName;
        }

        /**
         * @param oldName the oldName to set
         */
        public void setOldName(String oldName) {
            this.oldName = oldName;
        }

        /**
	     * 建议对象
	     */
	    private RecordVo outlinevo=new RecordVo("investigate_point");
	    
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm outlineForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	        this.setOutlinevo((RecordVo)this.getFormHM().get("outlineTb"));
	        this.getOutlineForm().setList((ArrayList)this.getFormHM().get("outlinelist"));
	        this.setItemid((String)this.getFormHM().get("itemid"));
	        this.setItemName((String)this.getFormHM().get("itemName"));
	        this.setOldName((String)this.getFormHM().get("oldName"));
	      
	        
	        
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM()
	    {
	    	
		    this.getFormHM().put("selectedlist",(ArrayList)this.getOutlineForm().getSelectedList());
	        this.getFormHM().put("outlineov",this.getOutlinevo());
	        this.getFormHM().put("outlineTb",this.getOutlinevo());
	        this.getFormHM().put("flag",this.getFlag()); 
	        this.getFormHM().put("itemid",this.getItemid());
	        this.getFormHM().put("itemName",this.getItemName());
	        this.getFormHM().put("oldName", this.getOldName());
	        
	       
	        
	        
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	    public PaginationForm getOutlineForm() {
	        return outlineForm;
	    }
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	    public void setOutlineForm(PaginationForm outlineForm) {
	        this.outlineForm = outlineForm;
	    }
	    /**
	     * @return Returns the outlinevo.
	     */
	    public RecordVo getOutlinevo() {
	        return outlinevo;
	    }
	    /**
	     * @param proposevo The outlinevo to set.
	     */
	    public void setOutlinevo(RecordVo outlinevo) {
	        this.outlinevo = outlinevo;
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
	        if("/selfservice/infomanager/askinv/searchoutline".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	  this.setFlag("1");
	        	 
	        	  this.getOutlinevo().clearValues();
	        	  this.getOutlinevo().setString("status","1");
	        	  this.getOutlinevo().setString("describestatus","0");
	        }
	        
	        if("/selfservice/infomanager/askinv/addoutline".equals(arg0.getPath()) && arg1.getParameter("b_addquery")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  this.getOutlinevo().clearValues();
	        	  this.getOutlinevo().setString("status","1");
	        	  this.getOutlinevo().setString("describestatus","0");
	        }
	        if("/selfservice/infomanager/askinv/addoutline".equals(arg0.getPath()) && arg1.getParameter("b_saveadd")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	
	        }
	        
	        if("/selfservice/infomanager/askinv/searchoutline".equals(arg0.getPath()) && arg1.getParameter("b_addquery")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  this.getOutlinevo().clearValues();
	        	  this.getOutlinevo().setString("status","1");
	        	  this.getOutlinevo().setString("describestatus","0");
	        }
	        
	        if("/selfservice/infomanager/askinv/searchoutline".equals(arg0.getPath()) && arg1.getParameter("b_delete")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  this.getOutlinevo().clearValues();
	        	  this.getOutlinevo().setString("status","1");
	        	  this.getOutlinevo().setString("describestatus","0");
	        }
	        
	        /**
	         * 编辑
	         */
	        if("/selfservice/infomanager/askinv/addoutline".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	
	        	 this.setFlag("0");
	        }
	        /**
	         * 答复
	         */
	        if("/selfservice/infomanager/askinv/replyboard".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	
	        	
	        	this.setFlag("2");
	        }
	        /**
	         * 查阅答复
	         */
	        if("/selfservice/infomanager/askinv/viewboard".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	this.setFlag("2");
	        }         
	        return super.validate(arg0, arg1);
	    }
	  
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
	     * @return Returns the approve.
	     */
	   
	  
	    public  String getItemid()
	    {
	    	return this.itemid;
	    }
	    
	    public void setItemid(String itemid)
	    {
	    	this.itemid=itemid;
	    }
	    //To get and set content of topic 
	    public String getItemName()
	    {
	    	return this.itemName;
	    }
	    
	    public void setItemName(String itemName)
	    {
	    	this.itemName=itemName;
	    	
	    }

}
