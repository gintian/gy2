package com.hjsj.hrms.actionform.pos;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TaskBookForm extends FrameForm {

	private String loadtype;
	private String a_code;
	private PaginationForm rolelistForm=new PaginationForm();
	private String filetitle;
	private FormFile mediafile;
	private String a0100;
	private String usertable;
	private String usernumber;
	private String i9999;
	private String uplevel;
	private String dbname;
	private String username;
	private ArrayList dbnamelist = new ArrayList();
	private String taskyear;
	private ArrayList yearlist = new ArrayList();
	private String sqlstr="";
	private String column="";
	private String order_by="";
	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setLoadtype((String)this.getFormHM().get("loadtype"));
		this.getRolelistForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setFiletitle((String)this.getFormHM().get("filetitle"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setUsertable((String)this.getFormHM().get("usertable"));
		this.setUsernumber((String)this.getFormHM().get("usernumber"));
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setDbnamelist((ArrayList)this.getFormHM().get("dbnamelist"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setTaskyear((String)this.getFormHM().get("taskyear"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("file",this.getMediafile());
		this.getFormHM().put("dbname",this.getDbname());
		this.getFormHM().put("username",this.getUsername());
		this.getFormHM().put("taskyear",this.getTaskyear());
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
   {
        if("/pos/roleinfo/taskbooklist".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
        {
            if(this.rolelistForm.getPagination()!=null)
              this.rolelistForm.getPagination().firstPage();
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

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public ArrayList getDbnamelist() {
		return dbnamelist;
	}

	public void setDbnamelist(ArrayList dbnamelist) {
		this.dbnamelist = dbnamelist;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTaskyear() {
		return taskyear;
	}

	public void setTaskyear(String taskyear) {
		this.taskyear = taskyear;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

}
