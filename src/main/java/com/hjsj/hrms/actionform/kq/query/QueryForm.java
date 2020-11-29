package com.hjsj.hrms.actionform.kq.query;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class QueryForm extends FrameForm  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList fieldlist=new ArrayList(); //查询字段列表
	private ArrayList class_list = new ArrayList(); //班次列表
	private String item_field; //选择查询字段
	private String classid=""; //选中班次  无意义
	private String orgparentcode;
	
	private String query;

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("item_field", this.getItem_field());
		this.getFormHM().put("classid", this.getClassid());
		this.getFormHM().put("class_list", this.getClass_list());
	    this.getFormHM().put("orgparentcode", this.getOrgparentcode());
	}

	@Override
    public void outPutFormHM() {
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setItem_field((String)this.getFormHM().get("item_field"));
		this.setClass_list((ArrayList)this.getFormHM().get("class_list"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
	}
	
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getItem_field() {
		return item_field;
	}

	public void setItem_field(String item_field) {
		this.item_field = item_field;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList getClass_list() {
		return class_list;
	}

	public void setClass_list(ArrayList class_list) {
		this.class_list = class_list;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}


}
