package com.hjsj.hrms.actionform.kq.options.manager;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class CancellationCardForm extends FrameForm {
    private String sql;
    private String where;
    private String orderby;
    private String column;
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	@Override
    public void outPutFormHM()
    {   
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setColumn((String)this.getFormHM().get("column"));
	}
	@Override
    public void inPutTransHM()
	{
		 if(this.getPagination()!=null)			
			 this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/kq/options/manager/cancellcard".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
		{
		   if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		     this.getFormHM().clear();
		}
		return super.validate(arg0, arg1);
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
}
