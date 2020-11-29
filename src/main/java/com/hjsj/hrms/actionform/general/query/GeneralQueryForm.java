package com.hjsj.hrms.actionform.general.query;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 * <p>Title:GeneralQueryForm.java</p>
 * <p>Description:通用查询form</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 14, 2006 2:06:09 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class GeneralQueryForm extends FrameForm {
	private ArrayList fieldList=new ArrayList();  
	private String    tableName="";
	private String right_fields[];  
	private ArrayList selectedFieldList=new ArrayList();

	
	@Override
    public void outPutFormHM() {
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));

	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("tableName",this.getTableName());

	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}


	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}

	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

}
