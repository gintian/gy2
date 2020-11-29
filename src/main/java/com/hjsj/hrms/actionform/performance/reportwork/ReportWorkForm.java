package com.hjsj.hrms.actionform.performance.reportwork;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
/**
 * <p>Title:ReportWorkForm</p>
 * @author lizhenwei
 * 
 */

public class ReportWorkForm extends FrameForm{
	
	
	/**
	 * 发布的计划列表
	 */
	ArrayList plainList = new ArrayList();
	/**
	 * 人员列表
	 */
	ArrayList personList = new ArrayList();
	/**
	 * 分页显示
	 */
	private PaginationForm reportWorkForm=new PaginationForm();
	/**
	 * 考核计划id
	 */
    private String plain_id;
    /**
     * 报告内容
     */
    private String content;
    
    private ArrayList summaryFileIdsList=new ArrayList();
    
    private String summaryState="0";
    /**
     * 按姓名查询
     */
    private String name;
    /**
     * 实时查询数据库分页用
     */
    private String sql_str;
    private String where_str;
    private String columns;
    private String isnull;
    private String id;
    private String isFile;
    private String order_sql;
	@Override
    public void outPutFormHM() {
		this.setPersonList((ArrayList)this.getFormHM().get("personList"));
		this.setPlainList((ArrayList)this.getFormHM().get("plainList"));
		//this.getReportWorkForm().setList((ArrayList)this.getFormHM().get("orgList"));
	    this.setContent((String)this.getFormHM().get("content"));
	    this.setPlain_id((String)this.getFormHM().get("plain_id"));
	    this.setColumns((String)this.getFormHM().get("columns"));
	    this.setWhere_str((String)this.getFormHM().get("where_str"));
	    this.setSql_str((String)this.getFormHM().get("sql_str"));
	    this.setIsnull((String)this.getFormHM().get("isnull"));
	    this.setName((String)this.getFormHM().get("name"));
	    this.setId((String)this.getFormHM().get("id"));
	    this.setIsFile((String)this.getFormHM().get("isFile"));
	    this.setOrder_sql((String)this.getFormHM().get("order_sql"));
	    this.setSummaryFileIdsList((ArrayList)this.getFormHM().get("summaryFileIdsList"));
	    this.setSummaryState((String)this.getFormHM().get("summaryState"));
	}

	@Override
    public void inPutTransHM() {
		//this.getFormHM().put("selectedList",this.getReportWorkForm().getSelectedList());
		this.getFormHM().put("plain_id",this.getPlain_id());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("id",this.getId());
	}

	public ArrayList getPersonList() {
		return personList;
	}

	public void setPersonList(ArrayList personList) {
		this.personList = personList;
	}

	public ArrayList getPlainList() {
		return plainList;
	}

	public void setPlainList(ArrayList plainList) {
		this.plainList = plainList;
	}

	public PaginationForm getReportWorkForm() {
		return reportWorkForm;
	}

	public void setReportWorkForm(PaginationForm reportWorkForm) {
		this.reportWorkForm = reportWorkForm;
	}

	public String getPlain_id() {
		return plain_id;
	}

	public void setPlain_id(String plain_id) {
		this.plain_id = plain_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getWhere_str() {
		return where_str;
	}

	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getIsnull() {
		return isnull;
	}

	public void setIsnull(String isnull) {
		this.isnull = isnull;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsFile() {
		return isFile;
	}

	public void setIsFile(String isFile) {
		this.isFile = isFile;
	}

	public String getOrder_sql() {
		return order_sql;
	}

	public void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}

	public ArrayList getSummaryFileIdsList() {
		return summaryFileIdsList;
	}

	public void setSummaryFileIdsList(ArrayList summaryFileIdsList) {
		this.summaryFileIdsList = summaryFileIdsList;
	}

	public String getSummaryState() {
		return summaryState;
	}

	public void setSummaryState(String summaryState) {
		this.summaryState = summaryState;
	}

}
