package com.hjsj.hrms.actionform.sys.bos.func;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FunctionMainForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        private String parentid;
        private String functionid;
        private String codeitemdesc;
        private String codeitemid;
        private String precodeitemid;
        private Document function_dom;
        private String funcflag;
        private FormFile file;
        private String addfunction_id;
        private String addfunction_name;
        private String editfunction_id;
        private String editfunction_name;
        private ArrayList sortlist = new ArrayList(); //节点list
        private String[] sort_fields; 
        private String sorting ;
	    /**
	     * 建议对象
	     */
        
       
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm functionMainForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	       
	        this.getFunctionMainForm().setList((ArrayList)this.getFormHM().get("functionMainlist"));
	        this.setParentid((String)this.getFormHM().get("parentid"));
	        this.setFunctionid((String)this.getFormHM().get("functionid"));
	        this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	        this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	        this.setPrecodeitemid((String)this.getFormHM().get("precodeitemid"));
	        this.setFunction_dom((Document)this.getFormHM().get("function_dom"));
	        this.setFuncflag((String)this.getFormHM().get("funcflag"));
	        
	        this.setAddfunction_id((String)this.getFormHM().get("addfunction_id"));
	        this.setAddfunction_name((String)this.getFormHM().get("addfunction_name"));
	        this.setEditfunction_id((String)this.getFormHM().get("editfunction_id"));
	        this.setEditfunction_name((String)this.getFormHM().get("editfunction_name"));
	        this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
	        this.setSorting((String)this.getFormHM().get("sorting"));
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
		    this.getFormHM().put("selectedlist",(ArrayList)this.getFunctionMainForm().getSelectedList());
			this.getFormHM().put("file", file);
			this.getFormHM().put("addfunction_id", addfunction_id);
			this.getFormHM().put("addfunction_name", addfunction_name);
			this.getFormHM().put("editfunction_id", editfunction_id);
			this.getFormHM().put("editfunction_name", editfunction_name);
			this.getFormHM().put("sort_fields",this.getSort_fields());
		    //this.getFormHM().put("parentid",this.parentid);
		   // this.getFormHM().put("functionid",this.functionid);
	      //  this.getFormHM().put("flag",this.getFlag());      
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	 
	    /**
	     * @param proposevo The downFilevo to set.
	     */
	  
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
	    	
	    	String fileflag=arg1.getParameter("fileflag");
	    	if(fileflag==null)
	    	{
	    		
	    	}
	    	else
	    	{
	    		this.setFlag(fileflag);
	    		
	    	}
	       
	    	/* 此form中添加锁版本 guodd 2018-09-07*/
	    	EncryptLockClient lock = (EncryptLockClient)arg1.getSession().getServletContext().getAttribute("lock");
	    	this.getFormHM().put("lockVersion", lock.getVersion()+"");
	    	
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

		public PaginationForm getFunctionMainForm() {
			return functionMainForm;
		}

		public void setFunctionMainForm(PaginationForm functionMainForm) {
			this.functionMainForm = functionMainForm;
		}

		public String getParentid() {
			return parentid;
		}

		public void setParentid(String parentid) {
			this.parentid = parentid;
		}

		public String getFunctionid() {
			return functionid;
		}

		public void setFunctionid(String functionid) {
			this.functionid = functionid;
		}



		public String getCodeitemdesc() {
			return codeitemdesc;
		}

		public void setCodeitemdesc(String codeitemdesc) {
			this.codeitemdesc = codeitemdesc;
		}

		public String getCodeitemid() {
			return codeitemid;
		}

		public void setCodeitemid(String codeitemid) {
			this.codeitemid = codeitemid;
		}

		public String getPrecodeitemid() {
			return precodeitemid;
		}

		public void setPrecodeitemid(String precodeitemid) {
			this.precodeitemid = precodeitemid;
		}

		public Document getFunction_dom() {
			return function_dom;
		}

		public void setFunction_dom(Document function_dom) {
			this.function_dom = function_dom;
		}

		public String getFuncflag() {
			return funcflag;
		}

		public void setFuncflag(String funcflag) {
			this.funcflag = funcflag;
		}

		public FormFile getFile() {
			return file;
		}

		public void setFile(FormFile file) {
			this.file = file;
		}

		public String getAddfunction_id() {
			return addfunction_id;
		}

		public void setAddfunction_id(String addfunction_id) {
			this.addfunction_id = addfunction_id;
		}

		public String getAddfunction_name() {
			return addfunction_name;
		}

		public void setAddfunction_name(String addfunction_name) {
			this.addfunction_name = addfunction_name;
		}

		public String getEditfunction_id() {
			return editfunction_id;
		}

		public void setEditfunction_id(String editfunction_id) {
			this.editfunction_id = editfunction_id;
		}

		public String getEditfunction_name() {
			return editfunction_name;
		}

		public void setEditfunction_name(String editfunction_name) {
			this.editfunction_name = editfunction_name;
		}

		public ArrayList getSortlist() {
			return sortlist;
		}

		public void setSortlist(ArrayList sortlist) {
			this.sortlist = sortlist;
		}

		public String[] getSort_fields() {
			return sort_fields;
		}

		public void setSort_fields(String[] sort_fields) {
			this.sort_fields = sort_fields;
		}

		public String getSorting() {
			return sorting;
		}

		public void setSorting(String sorting) {
			this.sorting = sorting;
		}
	    
	    /**
	     * @return Returns the approve.
	     */
	   
	  

}
