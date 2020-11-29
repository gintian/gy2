package com.hjsj.hrms.actionform.performance.workPlan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:WorkplanstatusForm.java</p>
 * <p>Description:填报状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-07-09 11:11:11</p> 
 * @author JinChunhai
 * @version 6.0
 */

public class WorkplanstatusForm extends FrameForm
{

	private String tableHtml = "";
	private String codeid = "";
	private String unitName = "";
	private HashMap haveCycleMap = new HashMap();  // 统计周期
    private String cycle = "";     
    private ArrayList cycleTypeList = new ArrayList();
    private String year = "";     
    private ArrayList yearTypeList = new ArrayList();
    private String quarter = "";     
    private ArrayList quarterTypeList = new ArrayList();
    private String month = "";     
    private ArrayList monthTypeList = new ArrayList();
    private String type = "";     
    private ArrayList typeList = new ArrayList();
    private PaginationForm personlistform=new PaginationForm();//地址分页管理器
    private String report_flag = "0";//0为“未批”，1为“已报，已批”
    private String str_sql = "";
    private String str_whl = "";
    private String order_str = "";
    private String colums = "";
    private String status = "01";
    private String isSelectedAll = "0";//0是没有全选，1是已经全选了
    
    
	@Override
    public void inPutTransHM()
    {   	
    
    	this.getFormHM().put("tableHtml", this.getTableHtml());
    	this.getFormHM().put("codeid", this.getCodeid());
    	this.getFormHM().put("unitName", this.getUnitName());
    	this.getFormHM().put("haveCycleMap", this.getHaveCycleMap());
    	this.getFormHM().put("cycle", this.getCycle());
    	this.getFormHM().put("cycleTypeList", this.getCycleTypeList());
    	this.getFormHM().put("year", this.getYear());
    	this.getFormHM().put("yearTypeList", this.getYearTypeList());
    	this.getFormHM().put("quarter", this.getQuarter());
    	this.getFormHM().put("quarterTypeList", this.getQuarterTypeList());
    	this.getFormHM().put("month", this.getMonth());
    	this.getFormHM().put("monthTypeList", this.getMonthTypeList());
    	this.getFormHM().put("type", this.getType());
    	this.getFormHM().put("cycle", this.getCycle());
    	this.getFormHM().put("report_flag", this.getReport_flag());
    	this.getFormHM().put("pagerows", this.getPagerows()==0?"10":(this.getPagerows()+""));
    	this.getFormHM().put("status", this.getStatus());
    	this.getFormHM().put("str_whl", this.getStr_whl());
    	this.getFormHM().put("isSelectedAll", this.getIsSelectedAll());
    }
    
    
    @Override
    public void outPutFormHM()
    {
    
    	this.getPersonlistform().setList((ArrayList)this.getFormHM().get("personlist"));
    	this.setTableHtml((String)this.getFormHM().get("tableHtml"));
    	this.setCodeid((String)this.getFormHM().get("codeid"));
    	this.setUnitName((String)this.getFormHM().get("unitName"));
    	this.setHaveCycleMap((HashMap)this.getFormHM().get("haveCycleMap"));
    	this.setCycle((String)this.getFormHM().get("cycle"));
    	this.setCycleTypeList((ArrayList)this.getFormHM().get("cycleTypeList"));
    	this.setYear((String)this.getFormHM().get("year"));
    	this.setYearTypeList((ArrayList)this.getFormHM().get("yearTypeList"));
    	this.setQuarter((String)this.getFormHM().get("quarter"));
    	this.setQuarterTypeList((ArrayList)this.getFormHM().get("quarterTypeList"));
    	this.setMonth((String)this.getFormHM().get("month"));
    	this.setMonthTypeList((ArrayList)this.getFormHM().get("monthTypeList"));
    	this.setType((String)this.getFormHM().get("type"));
    	this.setTypeList((ArrayList)this.getFormHM().get("typeList"));
    	this.setReport_flag((String)this.getFormHM().get("report_flag"));
    	this.setStr_sql((String)this.getFormHM().get("str_sql"));
    	this.setStr_whl((String)this.getFormHM().get("str_whl"));
    	this.setOrder_str((String)this.getFormHM().get("order_str"));
    	this.setColums((String)this.getFormHM().get("colums"));
    	this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));//控制每页显示多少条数据
    	this.setStatus((String)this.getFormHM().get("status"));
    	this.setIsSelectedAll((String)this.getFormHM().get("isSelectedAll"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
    	if("/performance/workplan/workplanstatus_show".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
    		if(this.getPagination()!=null)
				this.getPagination().firstPage();
        }
    	return super.validate(arg0, arg1);
    }
	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public ArrayList getCycleTypeList() {
		return cycleTypeList;
	}

	public void setCycleTypeList(ArrayList cycleTypeList) {
		this.cycleTypeList = cycleTypeList;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getYearTypeList() {
		return yearTypeList;
	}

	public void setYearTypeList(ArrayList yearTypeList) {
		this.yearTypeList = yearTypeList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList typeList) {
		this.typeList = typeList;
	}

	public HashMap getHaveCycleMap() {
		return haveCycleMap;
	}

	public void setHaveCycleMap(HashMap haveCycleMap) {
		this.haveCycleMap = haveCycleMap;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public ArrayList getMonthTypeList() {
		return monthTypeList;
	}

	public void setMonthTypeList(ArrayList monthTypeList) {
		this.monthTypeList = monthTypeList;
	}

	public PaginationForm getPersonlistform() {
		return personlistform;
	}

	public void setPersonlistform(PaginationForm personlistform) {
		this.personlistform = personlistform;
	}

	public String getReport_flag() {
		return report_flag;
	}

	public void setReport_flag(String report_flag) {
		this.report_flag = report_flag;
	}

	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}

	public String getStr_whl() {
		return str_whl;
	}

	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}

	public String getOrder_str() {
		return order_str;
	}

	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}

	public String getColums() {
		return colums;
	}

	public void setColums(String colums) {
		this.colums = colums;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsSelectedAll() {
		return isSelectedAll;
	}

	public void setIsSelectedAll(String isSelectedAll) {
		this.isSelectedAll = isSelectedAll;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public ArrayList getQuarterTypeList() {
		return quarterTypeList;
	}

	public void setQuarterTypeList(ArrayList quarterTypeList) {
		this.quarterTypeList = quarterTypeList;
	}
    
}