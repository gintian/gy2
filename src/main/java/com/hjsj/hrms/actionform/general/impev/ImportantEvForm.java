package com.hjsj.hrms.actionform.general.impev;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ImportantEvForm extends FrameForm {
	private ArrayList fieldlist=new ArrayList();/**字段列表*/
	private ArrayList resultList=new ArrayList();/**结果列表*/
	private PaginationForm paginationForm=new PaginationForm();
	private String num_per_page="20";/*每页记录数*/
	private String sqlstr="";
	private String wherestr="";
	private String cloums="";
	private String orderby="";
	private String checkflag="";
	private String fromdate="";
    private String todate="";
    private String a_code="";
    private String content;
    private String p0600;
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("fromdate", this.getFromdate());
		this.setFromdate("");
    	this.getFormHM().put("todate", this.getTodate());
    	this.setTodate("");
    	this.getFormHM().put("checkflag", this.getCheckflag());
    	this.getFormHM().put("content", this.getContent());
    	this.setContent("");
    	this.getFormHM().put("selectedlist",this.getPaginationForm().getSelectedList());
    	this.getFormHM().put("num_per_page",this.getNum_per_page());

	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setResultList((ArrayList)this.getFormHM().get("resultList"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setCloums((String)this.getFormHM().get("cloums"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setFromdate((String)this.getFormHM().get("fromdate"));
		this.setTodate((String)this.getFormHM().get("todate"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setP0600((String)this.getFormHM().get("p0600"));
		this.getPaginationForm().setList((ArrayList)this.getFormHM().get("resultList"));
		this.setNum_per_page((String)this.getFormHM().get("num_per_page"));

	}
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	   {
		   if("/general/impev/importantev".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	        {
			   if(this.num_per_page!=null&&this.num_per_page.length()>0)
		    	 this.pagerows=Integer.parseInt(num_per_page);
	        }
		   return super.validate(arg0, arg1);
	   }	   
	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getCloums() {
		return cloums;
	}

	public void setCloums(String cloums) {
		this.cloums = cloums;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getFromdate() {
		return fromdate;
	}

	public void setFromdate(String fromdate) {
		this.fromdate = fromdate;
	}

	public String getTodate() {
		return todate;
	}

	public void setTodate(String todate) {
		this.todate = todate;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getP0600() {
		return p0600;
	}

	public void setP0600(String p0600) {
		this.p0600 = p0600;
	}

	public ArrayList getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList resultList) {
		this.resultList = resultList;
	}

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}

	public String getNum_per_page() {
		return num_per_page;
	}

	public void setNum_per_page(String num_per_page) {
		this.num_per_page = num_per_page;
	}

}
