package com.hjsj.hrms.actionform.hire.employSummarise;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class EmploySummariseForm extends FrameForm {
	private PaginationForm engagePlanListform=new PaginationForm();
	private ArrayList columnsList=new ArrayList();
	private String extendWhereSql="";
	private String orderSql="";
	private String dbname="";
	private String username="";
	private String viewType="1";   //1:用工需求  2：招聘计划
	
	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setViewType((String)this.getFormHM().get("viewType"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setDbname((String)this.getFormHM().get("dbName"));
		this.setColumnsList((ArrayList)this.getFormHM().get("columnsList"));
		this.setExtendWhereSql((String)this.getFormHM().get("extendWhereSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.getEngagePlanListform().setList((ArrayList)this.getFormHM().get("planList"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("viewType",viewType);
		this.getFormHM().put("extendWhereSql",extendWhereSql);
		this.getFormHM().put("orderSql",orderSql);
		this.getFormHM().put("selectedlist",
				this.getEngagePlanListform().getSelectedList());

	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("flag")!=null&& "1".equals(arg1.getParameter("flag")))
		{
			if(this.getEngagePlanListform().getPagination()!=null)
				this.getEngagePlanListform().getPagination().firstPage();
		}
		
		return super.validate(arg0, arg1);
	}
	
	public ArrayList getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList columnsList) {
		this.columnsList = columnsList;
	}

	public PaginationForm getEngagePlanListform() {
		return engagePlanListform;
	}

	public void setEngagePlanListform(PaginationForm engagePlanListform) {
		this.engagePlanListform = engagePlanListform;
	}

	public String getExtendWhereSql() {
		return extendWhereSql;
	}

	public void setExtendWhereSql(String extendWhereSql) {
		this.extendWhereSql = extendWhereSql;
	}

	public String getOrderSql() {
		return orderSql;
	}

	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

}
