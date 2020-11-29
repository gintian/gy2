package com.hjsj.hrms.actionform.pos;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PosRoleInfoForm extends FrameForm {

	private String loadtype;
	private String a_code;
	private PaginationForm rolelistForm=new PaginationForm();
	private String filetitle;
	private FormFile mediafile;
	private String codesetid;
	private String usertable;
	private String usernumber;
	private String i9999;
	private String uplevel;
	private String returnvalue="";
	/**哪个模块，L为廉政风险防范模块，Z为全员职位说明书*/
	private String modular;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setLoadtype((String)this.getFormHM().get("loadtype"));
		this.getRolelistForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setFiletitle((String)this.getFormHM().get("filetitle"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setUsertable((String)this.getFormHM().get("usertable"));
		this.setUsernumber((String)this.getFormHM().get("usernumber"));
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setModular((String) this.getFormHM().get("modular"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("file",this.getMediafile());
		this.getFormHM().put("modular", this.getModular());
		
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
   {
        if("/pos/roleinfo/pos_dept_post".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
        {
            if(this.rolelistForm.getPagination()!=null)
              this.rolelistForm.getPagination().firstPage();
           
        }	
        if("/pos/roleinfo/pos_roleinfo_tree".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
        {
           
            if(arg1.getParameter("returnvalue")==null)
	           {
	        	   this.getFormHM().put("returnvalue", "");
	        	   this.setReturnvalue("");
	           }else
	           {
	        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
	           }
        }
      
     return super.validate(arg0, arg1);
    }
	
	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public PaginationForm getRolelistForm() {
		return rolelistForm;
	}

	public void setRolelistForm(PaginationForm rolelistForm) {
		this.rolelistForm = rolelistForm;
	}

	public FormFile getMediafile() {
		return mediafile;
	}

	public void setMediafile(FormFile mediafile) {
		this.mediafile = mediafile;
	}

	public String getFiletitle() {
		return filetitle;
	}

	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getI9999() {
		return i9999;
	}

	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}

	public String getUsernumber() {
		return usernumber;
	}

	public void setUsernumber(String usernumber) {
		this.usernumber = usernumber;
	}

	public String getUsertable() {
		return usertable;
	}

	public void setUsertable(String usertable) {
		this.usertable = usertable;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public String getModular() {
		return modular;
	}

	public void setModular(String modular) {
		this.modular = modular;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

}
