package com.hjsj.hrms.actionform.gz.gz_accounting.piecerate;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class PieceRateDetailForm extends FrameForm {
	
	private String canEdit = "false";
	private String s0100 = "";
	private String busiid = "";
	private String tableName;
	private ArrayList fieldlist = new ArrayList();// 字段列表 通用
	private String sql;
	private String infoStr;
	private String str_sql;
	private String dbname;
	private String topOrgDesc;
	
	
	private String objName;
	String right_fields="";
	String objsStr="";

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tableName", this.getTableName());        
		this.getFormHM().put("str_sql", this.getStr_sql());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"10":(this.getPagerows()+""));
	}

	@Override
    public void outPutFormHM() {
		this.setInfoStr((String) this.getFormHM().get("infoStr"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setS0100((String) this.getFormHM().get("s0100"));
		this.setBusiid((String) this.getFormHM().get("busiid"));
		this.setTableName((String) this.getFormHM().get("tableName"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setCanEdit((String) this.getFormHM().get("canEdit"));
		if (this.getFormHM().get("pagerows") != null)
	    	this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));

		this.setTopOrgDesc((String) this.getFormHM().get("topOrgDesc"));	
		this.setDbname((String) this.getFormHM().get("dbname"));	
	}

	public String getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(String canEdit) {
		this.canEdit = canEdit;
	}

	public String getS0100() {
		return s0100;
	}

	public void setS0100(String s0100) {
		this.s0100 = s0100;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getInfoStr() {
		return infoStr;
	}

	public void setInfoStr(String infoStr) {
		this.infoStr = infoStr;
	}

	public String getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String right_fields) {
		this.right_fields = right_fields;
	}

	public String getObjsStr() {
		return objsStr;
	}

	public void setObjsStr(String objsStr) {
		this.objsStr = objsStr;
	}

	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}

	public String getBusiid() {
		return busiid;
	}

	public void setBusiid(String busiid) {
		this.busiid = busiid;
	}

    public String getTopOrgDesc() {
        return topOrgDesc;
    }

    public void setTopOrgDesc(String topOrgDesc) {
        this.topOrgDesc = topOrgDesc;
    }

}
