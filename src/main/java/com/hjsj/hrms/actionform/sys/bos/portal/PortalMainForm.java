package com.hjsj.hrms.actionform.sys.bos.portal;

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
public class PortalMainForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        private String parentid;
        private String portalid;
        private String codeitemdesc;
        private String codeitemid;
        
        private String codeitemurl;
        private String codeitemicon; 
        private String codeitemfunc_id;
        private String codeitemtarget;
        
        private String precodeitemid;
        private Document portal_dom;
        private String portalflag;
        private FormFile file;
        
        private String addportal_id;
        private String addportal_name;
        private String addcodeitemurl;
        private String addcodeitemicon; 
        private String addcodeitemfunc_id;
        private String addcodeitemtarget;
        private String editportal_id;
        private String editportal_name;
        private String editcodeitemurl;
        private String editcodeitemicon; 
        private String editcodeitemfunc_id;
        private String editcodeitemtarget;
        private String colnum;
        private ArrayList numlist;
        private String colwidth;
        private String opt;
        private String colwidths;
        private String columnsuper;
        private String height;
        private String hide;
        private String priv;
        private String operation;
        private ArrayList hidelist;
        private ArrayList privlist;
        private String roles;
        
       
	    /**
	     * 建议对象列表
	     */
        private PaginationForm roleListForm=new PaginationForm();
	    private PaginationForm portalMainForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	       
	        this.getPortalMainForm().setList((ArrayList)this.getFormHM().get("portalMainlist"));
	        this.setParentid((String)this.getFormHM().get("parentid"));
	        this.setPortalid((String)this.getFormHM().get("portalid"));
	        this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	        this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	        
	        this.setCodeitemurl((String)this.getFormHM().get("codeitemurl"));
	        this.setCodeitemicon((String)this.getFormHM().get("codeitemicon"));
	        this.setCodeitemfunc_id((String)this.getFormHM().get("codeitemfunc_id"));
	        this.setCodeitemtarget((String)this.getFormHM().get("codeitemtarget"));
	        
	        this.setPrecodeitemid((String)this.getFormHM().get("precodeitemid"));
	        this.setPortal_dom((Document)this.getFormHM().get("portal_dom"));
	        this.setPortalflag((String)this.getFormHM().get("portalflag"));
	        
	        this.setAddportal_id((String)this.getFormHM().get("addportal_id"));
	        this.setAddportal_name((String)this.getFormHM().get("addportal_name"));
	        this.setAddcodeitemurl((String)this.getFormHM().get("addcodeitemurl"));
	        this.setAddcodeitemicon((String)this.getFormHM().get("addcodeitemicon"));
	        this.setAddcodeitemfunc_id((String)this.getFormHM().get("addcodeitemfunc_id"));
	        this.setAddcodeitemtarget((String)this.getFormHM().get("addcodeitemtarget"));
	        this.setEditportal_id((String)this.getFormHM().get("editportal_id"));
	        this.setEditportal_name((String)this.getFormHM().get("editportal_name"));
	        this.setEditcodeitemurl((String)this.getFormHM().get("editcodeitemurl"));
	        this.setEditcodeitemicon((String)this.getFormHM().get("editcodeitemicon"));
	        this.setEditcodeitemfunc_id((String)this.getFormHM().get("editcodeitemfunc_id"));
	        this.setEditcodeitemtarget((String)this.getFormHM().get("editcodeitemtarget"));
	        this.setNumlist((ArrayList)this.getFormHM().get("numlist"));
	        this.setColnum((String)this.getFormHM().get("colnum"));
	        this.setColwidth((String)this.getFormHM().get("colwidth"));
	        this.setOpt((String)this.getFormHM().get("opt"));
	        this.setColwidths((String)this.getFormHM().get("colwidths"));
	        this.setColumnsuper((String)this.getFormHM().get("columnsuper"));
	        this.setHeight((String)this.getFormHM().get("height"));
	        this.setHide((String)this.getFormHM().get("hide"));
	        this.setPriv((String)this.getFormHM().get("priv"));
	        this.setOperation((String)this.getFormHM().get("operation"));
	        this.setHidelist((ArrayList)this.getFormHM().get("hidelist"));
	        this.setPrivlist((ArrayList)this.getFormHM().get("privlist"));
	        this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
	        this.setRoles((String)this.getFormHM().get("roles"));
	    }

	    public String getColumnsuper() {
			return columnsuper;
		}

		public void setColumnsuper(String columnsuper) {
			this.columnsuper = columnsuper;
		}

		/* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
		    this.getFormHM().put("selectedlist",(ArrayList)this.getPortalMainForm().getSelectedList());
		    this.getFormHM().put("selectedroleList",(ArrayList)this.getRoleListForm().getSelectedList());
			this.getFormHM().put("file", file);
		    //this.getFormHM().put("parentid",this.parentid);
		   // this.getFormHM().put("portalid",this.portalid);
	      //  this.getFormHM().put("flag",this.getFlag());   
			this.getFormHM().put("addportal_id", addportal_id);
			this.getFormHM().put("addportal_name", addportal_name);
			this.getFormHM().put("addcodeitemurl", addcodeitemurl);
			this.getFormHM().put("addcodeitemicon", addcodeitemicon);
			this.getFormHM().put("addcodeitemfunc_id", addcodeitemfunc_id);
			this.getFormHM().put("addcodeitemtarget", addcodeitemtarget);
			this.getFormHM().put("editportal_id", editportal_id);
			this.getFormHM().put("editportal_name", editportal_name);
			this.getFormHM().put("editcodeitemurl", editcodeitemurl);
			this.getFormHM().put("editcodeitemicon", editcodeitemicon);
			this.getFormHM().put("editcodeitemfunc_id", editcodeitemfunc_id);
			this.getFormHM().put("editcodeitemtarget", editcodeitemtarget);
			this.getFormHM().put("selectedrolelist",this.getRoleListForm().getSelectedList());
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

		public PaginationForm getPortalMainForm() {
			return portalMainForm;
		}

		public void setPortalMainForm(PaginationForm portalMainForm) {
			this.portalMainForm = portalMainForm;
		}

		public String getParentid() {
			return parentid;
		}

		public void setParentid(String parentid) {
			this.parentid = parentid;
		}

		public String getPortalid() {
			return portalid;
		}

		public void setPortalid(String portalid) {
			this.portalid = portalid;
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

		public Document getPortal_dom() {
			return portal_dom;
		}

		public void setPortal_dom(Document portal_dom) {
			this.portal_dom = portal_dom;
		}

		public String getPortalflag() {
			return portalflag;
		}

		public void setPortalflag(String portalflag) {
			this.portalflag = portalflag;
		}

		public FormFile getFile() {
			return file;
		}

		public void setFile(FormFile file) {
			this.file = file;
		}

		public String getCodeitemurl() {
			return codeitemurl;
		}

		public void setCodeitemurl(String codeitemurl) {
			this.codeitemurl = codeitemurl;
		}

		public String getCodeitemicon() {
			return codeitemicon;
		}

		public void setCodeitemicon(String codeitemicon) {
			this.codeitemicon = codeitemicon;
		}

		public String getCodeitemfunc_id() {
			return codeitemfunc_id;
		}

		public void setCodeitemfunc_id(String codeitemfunc_id) {
			this.codeitemfunc_id = codeitemfunc_id;
		}

		public String getCodeitemtarget() {
			return codeitemtarget;
		}

		public void setCodeitemtarget(String codeitemtarget) {
			this.codeitemtarget = codeitemtarget;
		}

		public String getAddportal_id() {
			return addportal_id;
		}

		public void setAddportal_id(String addportal_id) {
			this.addportal_id = addportal_id;
		}

		public String getAddportal_name() {
			return addportal_name;
		}

		public void setAddportal_name(String addportal_name) {
			this.addportal_name = addportal_name;
		}

		public String getAddcodeitemurl() {
			return addcodeitemurl;
		}

		public void setAddcodeitemurl(String addcodeitemurl) {
			this.addcodeitemurl = addcodeitemurl;
		}

		public String getAddcodeitemicon() {
			return addcodeitemicon;
		}

		public void setAddcodeitemicon(String addcodeitemicon) {
			this.addcodeitemicon = addcodeitemicon;
		}

		public String getAddcodeitemfunc_id() {
			return addcodeitemfunc_id;
		}

		public void setAddcodeitemfunc_id(String addcodeitemfunc_id) {
			this.addcodeitemfunc_id = addcodeitemfunc_id;
		}

		public String getAddcodeitemtarget() {
			return addcodeitemtarget;
		}

		public void setAddcodeitemtarget(String addcodeitemtarget) {
			this.addcodeitemtarget = addcodeitemtarget;
		}

		public String getEditportal_id() {
			return editportal_id;
		}

		public void setEditportal_id(String editportal_id) {
			this.editportal_id = editportal_id;
		}

		public String getEditportal_name() {
			return editportal_name;
		}

		public void setEditportal_name(String editportal_name) {
			this.editportal_name = editportal_name;
		}

		public String getEditcodeitemurl() {
			return editcodeitemurl;
		}

		public void setEditcodeitemurl(String editcodeitemurl) {
			this.editcodeitemurl = editcodeitemurl;
		}

		public String getEditcodeitemicon() {
			return editcodeitemicon;
		}

		public void setEditcodeitemicon(String editcodeitemicon) {
			this.editcodeitemicon = editcodeitemicon;
		}

		public String getEditcodeitemfunc_id() {
			return editcodeitemfunc_id;
		}

		public void setEditcodeitemfunc_id(String editcodeitemfunc_id) {
			this.editcodeitemfunc_id = editcodeitemfunc_id;
		}

		public String getEditcodeitemtarget() {
			return editcodeitemtarget;
		}

		public void setEditcodeitemtarget(String editcodeitemtarget) {
			this.editcodeitemtarget = editcodeitemtarget;
		}

		public String getColnum() {
			return colnum;
		}

		public void setColnum(String colnum) {
			this.colnum = colnum;
		}

		public ArrayList getNumlist() {
			return numlist;
		}

		public void setNumlist(ArrayList numlist) {
			this.numlist = numlist;
		}

		public String getColwidth() {
			return colwidth;
		}

		public void setColwidth(String colwidth) {
			this.colwidth = colwidth;
		}

		public String getOpt() {
			return opt;
		}

		public void setOpt(String opt) {
			this.opt = opt;
		}

		public String getColwidths() {
			return colwidths;
		}

		public void setColwidths(String colwidths) {
			this.colwidths = colwidths;
		}

		public String getHeight() {
			return height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

		public String getHide() {
			return hide;
		}

		public void setHide(String hide) {
			this.hide = hide;
		}

		public String getPriv() {
			return priv;
		}

		public void setPriv(String priv) {
			this.priv = priv;
		}

		public String getOperation() {
			return operation;
		}

		public void setOperation(String operation) {
			this.operation = operation;
		}

		public ArrayList getHidelist() {
			return hidelist;
		}

		public void setHidelist(ArrayList hidelist) {
			this.hidelist = hidelist;
		}

		public ArrayList getPrivlist() {
			return privlist;
		}

		public void setPrivlist(ArrayList privlist) {
			this.privlist = privlist;
		}

		public PaginationForm getRoleListForm() {
			return roleListForm;
		}

		public void setRoleListForm(PaginationForm roleListForm) {
			this.roleListForm = roleListForm;
		}

		public String getRoles() {
			return roles;
		}

		public void setRoles(String roles) {
			this.roles = roles;
		}
	    
	    /**
	     * @return Returns the approve.
	     */
	   
	  

}
