package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class EmployActualizeForm extends FrameForm {
	
	private String linkDesc="";
    private String select_str="";
    private String from_str="";
    private String username="";
    
    private ArrayList fieldSetList=new ArrayList();
    private ArrayList selectedFieldList=new ArrayList();
    private String  rightFields[]=null;
    private String dbpre="";
    private String sql="";
    private String column_str="";
    private ArrayList positionList=new ArrayList();
    private String z0301="";
    
	private ArrayList tableHeadNameList=new ArrayList();				  //表头列名；
	private ArrayList tableColumnsList=new ArrayList();		
	private String columnSize="";
	private String columns="";
	private String dbName="";		//库前缀
	private String summary="";      //评语
	private String a0100="";
	private String codeid="";
	private String extendSql;
	private String orderSql;
	
    private String hireStateStr="";
	
	@Override
    public void outPutFormHM() {
		this.setHireStateStr((String)this.getFormHM().get("hireStateStr"));
		
		this.setLinkDesc((String)this.getFormHM().get("linkDesc"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setSelect_str((String)this.getFormHM().get("select_str"));
		this.setFrom_str((String)this.getFormHM().get("from_str"));
		this.setTableHeadNameList((ArrayList)this.getFormHM().get("tableHeadNameList"));
		this.setTableColumnsList((ArrayList)this.getFormHM().get("tableColumnsList"));
		this.setColumns((String)this.getFormHM().get("columns"));
		if(this.getFormHM().get("tableHeadNameList")!=null)
		this.setColumnSize(String.valueOf(((ArrayList)this.getFormHM().get("tableHeadNameList")).size()+1));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setExtendSql((String)this.getFormHM().get("extendSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.setDbName((String)this.getFormHM().get("dbName"));
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
		this.setPositionList((ArrayList)this.getFormHM().get("positionList"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("extendSql",this.getExtendSql());
		this.getFormHM().put("orderSql",this.getOrderSql());	
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("summary",this.getSummary());
		this.getFormHM().put("rightFields",this.getRightFields());
		this.getFormHM().put("sql",this.getSql());
		this.getFormHM().put("column_str",this.getColumn_str());
		this.getFormHM().put("z0301",this.getZ0301());
	}



	public ArrayList getTableHeadNameList() {
		return tableHeadNameList;
	}

	public void setTableHeadNameList(ArrayList tableHeadNameList) {
		this.tableHeadNameList = tableHeadNameList;
	}

	public ArrayList getTableColumnsList() {
		return tableColumnsList;
	}

	public void setTableColumnsList(ArrayList tableColumnsList) {
		this.tableColumnsList = tableColumnsList;
	}

	public String getFrom_str() {
		return from_str;
	}

	public void setFrom_str(String from_str) {
		this.from_str = from_str;
	}

	public String getSelect_str() {
		return select_str;
	}

	public void setSelect_str(String select_str) {
		this.select_str = select_str;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(String columnSize) {
		this.columnSize = columnSize;
	}

	public String getExtendSql() {
		return extendSql;
	}

	public void setExtendSql(String extendSql) {
		this.extendSql = extendSql;
	}

	public String getOrderSql() {
		return orderSql;
	}

	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String[] getRightFields() {
		return rightFields;
	}

	public void setRightFields(String[] rightFields) {
		this.rightFields = rightFields;
	}

	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}

	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getColumn_str() {
		return column_str;
	}

	public void setColumn_str(String column_str) {
		this.column_str = column_str;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList getPositionList() {
		return positionList;
	}

	public void setPositionList(ArrayList positionList) {
		this.positionList = positionList;
	}

	public String getZ0301() {
		return z0301;
	}

	public void setZ0301(String z0301) {
		this.z0301 = z0301;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLinkDesc() {
		return linkDesc;
	}

	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}

	public String getHireStateStr() {
		return hireStateStr;
	}

	public void setHireStateStr(String hireStateStr) {
		this.hireStateStr = hireStateStr;
	}

}
