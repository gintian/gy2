package com.hjsj.hrms.actionform.train.resource;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class FacilityInfoForm extends FrameForm {
    
	private String type;
	private String columns;
	private String strsql;
	private String strwhere;
	private String order_by;
	private ArrayList itemList=new ArrayList();
	private String uplevel;
	
	private String startdate;
	private String enddate;
	
	private String fieldId;
	private String fieldName;
	
	private String r5900;
	private String state;
	private String number;
	private String strdate;
	
	
	private String year;
	private String month;
	private String dateOfMonth;
	
	@Override
    public void outPutFormHM() {
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setStartdate((String)this.getFormHM().get("startdate"));
		this.setEnddate((String)this.getFormHM().get("enddate"));
		
		this.setFieldId((String)this.getFormHM().get("fieldId"));
		this.setFieldName((String)this.getFormHM().get("fieldName"));
		this.setStrdate((String)this.getFormHM().get("strdate"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setDateOfMonth((String)this.getFormHM().get("dateOfMonth"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("fieldId", this.getFieldId());
		this.getFormHM().put("fieldName", this.getFieldName());
		this.getFormHM().put("startdate", this.getStartdate());
		this.getFormHM().put("enddate", this.getEnddate());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("month", this.getMonth());
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/train/resource/facility/facilityinfo".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			if(this.getPagination()!=null)
            	this.getPagination().firstPage();   
        }
		
		if("/train/resource/trainroom/selftrainroom".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			if(this.getPagination()!=null&& "link".equalsIgnoreCase(arg1.getParameter("b_query")))
            	this.getPagination().firstPage();   
        }
		
		if("/train/resource/trainroom/trainroom".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            if(this.getPagination()!=null&& "link".equalsIgnoreCase(arg1.getParameter("b_query")))
                this.getPagination().firstPage();   
        }
		
		return super.validate(arg0, arg1);
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStrdate() {
		return strdate;
	}

	public void setStrdate(String strdate) {
		this.strdate = strdate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public String getR5900() {
		return r5900;
	}

	public void setR5900(String r5900) {
		this.r5900 = r5900;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDateOfMonth() {
		return dateOfMonth;
	}

	public void setDateOfMonth(String dateOfMonth) {
		this.dateOfMonth = dateOfMonth;
	}
}
