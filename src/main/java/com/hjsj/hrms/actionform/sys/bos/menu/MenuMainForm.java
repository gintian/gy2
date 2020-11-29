package com.hjsj.hrms.actionform.sys.bos.menu;

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
public class MenuMainForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        private String parentid;
        private String menuid;
        private String codeitemdesc;
        private String codeitemid;
        
        private String codeitemurl;
        private String codeitemicon; 
        private String codeitemfunc_id;
        private String codeitemtarget;
        private String menuhide;
        
        private String precodeitemid;
        private Document menu_dom;
        private String menuflag;
        private FormFile file;
        
        private String addmenu_id;
        private String addmenu_name;
        private String addcodeitemurl;
        private String addcodeitemicon; 
        private String addcodeitemfunc_id;
        private String addcodeitemtarget;
        private String addmenuhide;
        private String editmenu_id;
        private String editmenu_name;
        private String editcodeitemurl;
        private String editcodeitemicon; 
        private String editcodeitemfunc_id;
        private String editcodeitemtarget;
        private String editmenuhide;
        private ArrayList menuhidelist = new ArrayList(); 
        private ArrayList sortlist = new ArrayList(); //节点list
        private String[] sort_fields; 
        private String sorting ;
       //二次验证  changxy 20160621
        private String validate;
        private ArrayList validateList;
  
	    /**
	     * 建议对象
	     */
        
       
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm menuMainForm=new PaginationForm();     
	  
	    @Override
        public void outPutFormHM() {
	       
	        this.getMenuMainForm().setList((ArrayList)this.getFormHM().get("menuMainlist"));
	        this.setParentid((String)this.getFormHM().get("parentid"));
	        this.setMenuid((String)this.getFormHM().get("menuid"));
	        this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	        this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	        
	        this.setCodeitemurl((String)this.getFormHM().get("codeitemurl"));
	        this.setCodeitemicon((String)this.getFormHM().get("codeitemicon"));
	        this.setCodeitemfunc_id((String)this.getFormHM().get("codeitemfunc_id"));
	        this.setCodeitemtarget((String)this.getFormHM().get("codeitemtarget"));
	        
	        this.setPrecodeitemid((String)this.getFormHM().get("precodeitemid"));
	        this.setMenu_dom((Document)this.getFormHM().get("menu_dom"));
	        this.setMenuflag((String)this.getFormHM().get("menuflag"));
	        this.setMenuhide((String)this.getFormHM().get("menuhide"));
	        this.setAddmenu_id((String)this.getFormHM().get("addmenu_id"));
	        this.setAddmenu_name((String)this.getFormHM().get("addmenu_name"));
	        this.setAddcodeitemurl((String)this.getFormHM().get("addcodeitemurl"));
	        this.setAddcodeitemicon((String)this.getFormHM().get("addcodeitemicon"));
	        this.setAddcodeitemfunc_id((String)this.getFormHM().get("addcodeitemfunc_id"));
	        this.setAddcodeitemtarget((String)this.getFormHM().get("addcodeitemtarget"));
	        this.setAddmenuhide((String)this.getFormHM().get("addmenuhide"));
	        this.setEditmenu_id((String)this.getFormHM().get("editmenu_id"));
	        this.setEditmenu_name((String)this.getFormHM().get("editmenu_name"));
	        this.setEditcodeitemurl((String)this.getFormHM().get("editcodeitemurl"));
	        this.setEditcodeitemicon((String)this.getFormHM().get("editcodeitemicon"));
	        this.setEditcodeitemfunc_id((String)this.getFormHM().get("editcodeitemfunc_id"));
	        this.setEditcodeitemtarget((String)this.getFormHM().get("editcodeitemtarget"));
	        this.setEditmenuhide((String)this.getFormHM().get("editmenuhide"));
	        this.setMenuhidelist((ArrayList)this.getFormHM().get("menuhidelist"));
	        this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
	        this.setSorting((String)this.getFormHM().get("sorting"));
	        this.setValidate((String)this.getFormHM().get("validate"));
	        this.setValidateList((ArrayList)this.getFormHM().get("validateList"));    
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
	        if (this.getMenuMainForm()!=null)
	            this.getFormHM().put("selectedlist",(ArrayList)this.getMenuMainForm().getSelectedList());
			this.getFormHM().put("file", file);
		    //this.getFormHM().put("parentid",this.parentid);
		   // this.getFormHM().put("menuid",this.menuid);
	      //  this.getFormHM().put("flag",this.getFlag());   
			this.getFormHM().put("addmenu_id", addmenu_id);
			this.getFormHM().put("addmenu_name", addmenu_name);
			this.getFormHM().put("addcodeitemurl", addcodeitemurl);
			this.getFormHM().put("addcodeitemicon", addcodeitemicon);
			this.getFormHM().put("addcodeitemfunc_id", addcodeitemfunc_id);
			this.getFormHM().put("addcodeitemtarget", addcodeitemtarget);
			this.getFormHM().put("addmenuhide", addmenuhide);
			this.getFormHM().put("editmenu_id", editmenu_id);
			this.getFormHM().put("editmenu_name", editmenu_name);
			this.getFormHM().put("editcodeitemurl", editcodeitemurl);
			this.getFormHM().put("editcodeitemicon", editcodeitemicon);
			this.getFormHM().put("editcodeitemfunc_id", editcodeitemfunc_id);
			this.getFormHM().put("editcodeitemtarget", editcodeitemtarget);
			this.getFormHM().put("editmenuhide", editmenuhide);
			this.getFormHM().put("sort_fields",this.getSort_fields());
			this.getFormHM().put("validate",validate);
			this.getFormHM().put("validateList", validateList);
		
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

		public PaginationForm getMenuMainForm() {
			return menuMainForm;
		}

		public String getAddmenuhide() {
			return addmenuhide;
		}

		public void setAddmenuhide(String addmenuhide) {
			this.addmenuhide = addmenuhide;
		}

		public String getEditmenuhide() {
			return editmenuhide;
		}

		public void setEditmenuhide(String editmenuhide) {
			this.editmenuhide = editmenuhide;
		}

		public ArrayList getMenuhidelist() {
			return menuhidelist;
		}

		public void setMenuhidelist(ArrayList menuhidelist) {
			this.menuhidelist = menuhidelist;
		}

		public void setMenuMainForm(PaginationForm menuMainForm) {
			this.menuMainForm = menuMainForm;
		}

		public String getParentid() {
			return parentid;
		}

		public void setParentid(String parentid) {
			this.parentid = parentid;
		}

		public String getMenuid() {
			return menuid;
		}

		public void setMenuid(String menuid) {
			this.menuid = menuid;
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

		public Document getMenu_dom() {
			return menu_dom;
		}

		public void setMenu_dom(Document menu_dom) {
			this.menu_dom = menu_dom;
		}

		public String getMenuflag() {
			return menuflag;
		}

		public void setMenuflag(String menuflag) {
			this.menuflag = menuflag;
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

		public String getAddmenu_id() {
			return addmenu_id;
		}

		public void setAddmenu_id(String addmenu_id) {
			this.addmenu_id = addmenu_id;
		}

		public String getAddmenu_name() {
			return addmenu_name;
		}

		public void setAddmenu_name(String addmenu_name) {
			this.addmenu_name = addmenu_name;
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

		public String getEditmenu_id() {
			return editmenu_id;
		}

		public void setEditmenu_id(String editmenu_id) {
			this.editmenu_id = editmenu_id;
		}

		public String getEditmenu_name() {
			return editmenu_name;
		}

		public void setEditmenu_name(String editmenu_name) {
			this.editmenu_name = editmenu_name;
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

		public String getMenuhide() {
			return menuhide;
		}

		public void setMenuhide(String menuhide) {
			this.menuhide = menuhide;
		}

		public String getValidate() {
			return validate;
		}

		public void setValidate(String validate) {
			this.validate = validate;
		}

		public ArrayList getValidateList() {
			return validateList;
		}

		public void setValidateList(ArrayList validateList) {
			this.validateList = validateList;
		}
}
