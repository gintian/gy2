package com.hjsj.hrms.actionform.sys.codemaintence;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class CodeSetForm extends FrameForm {

	private String sql;
	private String where;
	private String column;
	private String orderby;
	private PaginationForm pageListForm = new PaginationForm();
	
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
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

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSql((String) hm.get("sqlstr"));
		this.setWhere((String)hm.get("where"));
		this.setColumn((String)hm.get("column"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/system/codemaintence/serch_codeset".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		/* jingq add 2014.4.22 未关联指标代码类功能代码列表界面页码重新进入初始为首页 */
		if("/system/codemaintence/serch_codeset".equals(arg0.getPath())&&arg1.getParameter("b_norelation")!=null){
		    if(this.getPagination()!=null){
		        this.getPagination().firstPage();
		    }
		}
		return super.validate(arg0, arg1);
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}
}
