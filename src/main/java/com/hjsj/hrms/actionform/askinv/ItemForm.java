/*
 * Created on 2005-5-24
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
public class ItemForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        private String approve="1";
        private String id="0";
        private String content="0";
        private String fillflag="";
        private String selects;
        private String minvalue;
        private String maxvalue;
       
		
	    /**
	     * 建议对象
	     */
	    private RecordVo itemvo=new RecordVo("investigate_item");
	    
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm itemForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	    	this.setSelects((String)this.getFormHM().get("selects"));
	    	this.setMinvalue((String)this.getFormHM().get("minvalue"));
	    	this.setMaxvalue((String)this.getFormHM().get("maxvalue"));
	        this.setItemvo((RecordVo)this.getFormHM().get("itemTb"));
	        this.getItemForm().setList((ArrayList)this.getFormHM().get("itemlist"));
	        this.setId((String)this.getFormHM().get("id"));
	        this.setContent((String)this.getFormHM().get("content"));
	        this.setFillflag((String)this.getFormHM().get("fillflag"));
	        
	        
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM()
	    {
	    	this.getFormHM().put("selects", this.getSelects());
	    	this.getFormHM().put("minvalue",this.getMinvalue());
	    	this.getFormHM().put("maxvalue", this.getMaxvalue());
		    this.getFormHM().put("selectedlist",(ArrayList)this.getItemForm().getSelectedList());
	        this.getFormHM().put("itemov",this.getItemvo());
	        this.getFormHM().put("itemTb",this.getItemvo());
	        this.getFormHM().put("flag",this.getFlag()); 
	        this.getFormHM().put("id",this.getId());
	        this.getFormHM().put("content",this.getContent());
            this.getFormHM().put("fillflag",this.getFillflag());
	        
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	    public PaginationForm getItemForm() {
	        return itemForm;
	    }
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	    public void setItemForm(PaginationForm itemForm) {
	        this.itemForm = itemForm;
	    }
	    /**
	     * @return Returns the itemvo.
	     */
	    public RecordVo getItemvo() {
	        return itemvo;
	    }
	    /**
	     * @param proposevo The itemvo to set.
	     */
	    public void setItemvo(RecordVo itemvo) {
	        this.itemvo = itemvo;
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
	   /* public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
			if(arg0.getPath().equals("/gz/templateset/moneystyle/initMoneyStyleDetail")&&arg1.getParameter("b_init")!=null)
			{
	            *//**定位到首页,*//*
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();              
	        }
			if(arg1.getParameter("b_init")!=null&&arg1.getParameter("b_init").equals("init"))
				if(this.getMoneyListForm()!=null)
					this.getMoneyListForm().getPagination().firstPage();
			return super.validate(arg0, arg1);
		}
		*/
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	        /**
	         * 新建
	         */
	    	///selfservice/infomanager/askinv/additem.do?b_addquery=link
	    	if("/selfservice/infomanager/askinv/additem".equals(arg0.getPath())&&arg1.getParameter("b_addquery")!=null)
	    	{
	    		 if(this.getPagination()!=null)
		            	this.getPagination().firstPage();    
	    		 if(this.getItemForm()!=null)
						this.getItemForm().getPagination().firstPage();
	    	}
	        if("/selfservice/infomanager/askinv/searchitem".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	  this.setFlag("1");
	        	 
	        	  this.getItemvo().clearValues();
	        	  this.getItemvo().setString("status","0");
	        }
	        //more code for modifing
	        if("/selfservice/infomanager/askinv/additem".equals(arg0.getPath()) && arg1.getParameter("b_addquery")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  this.getItemvo().clearValues();
	        	  this.getItemvo().setString("status","0");
	        }
	        //end
	        if("/selfservice/infomanager/askinv/additem".equals(arg0.getPath()) && arg1.getParameter("b_saveadd")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  
	        }
	        //返回
	        if("/selfservice/infomanager/askinv/searchitem".equals(arg0.getPath()) && arg1.getParameter("b_addquery")!=null)
	        {
	        	 this.setFlag("1");
	        	 
	        	  this.getItemvo().clearValues();
	        	  this.getItemvo().setString("status","0");
	        }
	        
	       
	        /**
	         * 编辑
	         */
	        if("/selfservice/infomanager/askinv/additem".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
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
	   
	   public String getApprove()
	    {
	    	return this.approve;
	    }
	    
	   /*
	    * @param approve to set
	    */
	  
	    public void setApprove(String approve)
	    {
	    	this.approve=approve;
	    }
	    
	    public  String getId()
	    {
	    	return this.id;
	    }
	    
	    public void setId(String id)
	    {
	    	this.id=id;
	    }
	    //To get and set content of topic 
	    public String getContent()
	    {
	    	return this.content;
	    }
	    
	    public void setContent(String content)
	    {
	    	this.content=content;
	    	
	    }

		public String getFillflag() {
			return fillflag;
		}

		public void setFillflag(String fillflag) {
			this.fillflag = fillflag;
		}

		public String getSelects() {
			return selects;
		}

		public void setSelects(String selects) {
			this.selects = selects;
		}

		public String getMinvalue() {
			return minvalue;
		}

		public void setMinvalue(String minvalue) {
			this.minvalue = minvalue;
		}

		public String getMaxvalue() {
			return maxvalue;
		}

		public void setMaxvalue(String maxvalue) {
			this.maxvalue = maxvalue;
		}
	    
	 

}
