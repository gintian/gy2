package com.hjsj.hrms.actionform.gz.gz_analyse;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class HistoryDataForm extends FrameForm {

	/**薪资和保险福利标志,默认为工资业务
     *保险福利为1 
     */
    private String gz_module="0";
    private String salaryid;
    /**归档类型=0全部归档，=1按时间范围归档*/
    private String type="0";
    /**起始时间*/
    private String startDate;
    /**结束时间*/
    private String endDate;
    private PaginationForm salarySetListform=new PaginationForm();
    private String[] salaryids;
    private String a_code="";
    /**项目过滤号和条件过滤号*/
    private String itemid;
    private String condid;
	private String cond_id_str;
    /**项目过滤列表*/
    private ArrayList itemlist=new ArrayList();
    /**条件过滤列表*/
    private ArrayList condlist=new ArrayList();
    /**处理的业务日期*/
    private String bosdate;
    /**处理的业务次数*/
    private String count;
    /**当前薪资类别处理过的业务日期列表*/
    private ArrayList datelist=new ArrayList();
    /**处理的业务日期对应的发放次数列表*/
    private ArrayList countlist=new ArrayList();    
    /**数据过滤语句*/
    private String sql;
    /**薪资项目列表*/
    private ArrayList fieldlist=new ArrayList();
    private String fieldStr="";
    private String isOnlySet="0";
	/**
	 * 人员过滤
	 */
	private String empfiltersql="";
	/**
	 * 项目过滤
	 */
	private String proright_str="";



 
    
    /**是否按操作单位来控制权限=0不=1按*/
    private String viewUnit;
    
    
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("cond_id_str",this.getCond_id_str());
		this.getFormHM().put("isOnlySet", this.getIsOnlySet());
		this.getFormHM().put("salaryid", this.getSalaryid());
		this.getFormHM().put("gz_module", this.getGz_module());
		this.getFormHM().put("salaryids", this.getSalaryids());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());		
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("condid", this.getCondid());
		this.getFormHM().put("itemlist", this.getItemlist());
		this.getFormHM().put("condlist", this.getCondlist());		
		this.getFormHM().put("bosdate", this.getBosdate());
		this.getFormHM().put("count", this.getCount());
		this.getFormHM().put("datelist", this.getDatelist());
		this.getFormHM().put("countlist", this.getCountlist());
		this.getFormHM().put("sql", this.getSql());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("fieldStr", this.getFieldStr());
		this.getFormHM().put("empfiltersql",this.getEmpfiltersql());
		this.getFormHM().put("proright_str",this.getProright_str());
	}

	@Override
    public void outPutFormHM() {
		this.setViewUnit((String)this.getFormHM().get("viewUnit"));
		this.setCond_id_str((String)this.getFormHM().get("cond_id_str"));
		this.setProright_str((String)this.getFormHM().get("proright_str"));
		this.setEmpfiltersql((String)this.getFormHM().get("empfiltersql"));
		this.setIsOnlySet((String)this.getFormHM().get("isOnlySet"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.getSalarySetListform().setList((ArrayList)this.getFormHM().get("salarySetList"));
		this.setSalaryids((String[])this.getFormHM().get("salaryids"));
		this.setType((String)this.getFormHM().get("type"));
		this.setStartDate((String)this.getFormHM().get("startDate"));
		this.setEndDate((String)this.getFormHM().get("endDate"));		
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));		
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setCountlist((ArrayList)this.getFormHM().get("countlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldStr((String)this.getFormHM().get("fieldStr"));
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public PaginationForm getSalarySetListform() {
		return salarySetListform;
	}

	public void setSalarySetListform(PaginationForm salarySetListform) {
		this.salarySetListform = salarySetListform;
	}

	public String[] getSalaryids()
	{
	
	    return salaryids;
	}

	public void setSalaryids(String[] salaryids)
	{
	
	    this.salaryids = salaryids;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getA_code()
	{
	
	    return a_code;
	}

	public void setA_code(String a_code)
	{
	
	    this.a_code = a_code;
	}

	public String getBosdate()
	{
	
	    return bosdate;
	}

	public void setBosdate(String bosdate)
	{
	
	    this.bosdate = bosdate;
	}

	public String getCondid()
	{
	
	    return condid;
	}

	public void setCondid(String condid)
	{
	
	    this.condid = condid;
	}

	public ArrayList getCondlist()
	{
	
	    return condlist;
	}

	public void setCondlist(ArrayList condlist)
	{
	
	    this.condlist = condlist;
	}

	public String getCount()
	{
	
	    return count;
	}

	public void setCount(String count)
	{
	
	    this.count = count;
	}

	public ArrayList getCountlist()
	{
	
	    return countlist;
	}

	public void setCountlist(ArrayList countlist)
	{
	
	    this.countlist = countlist;
	}

	public ArrayList getDatelist()
	{
	
	    return datelist;
	}

	public void setDatelist(ArrayList datelist)
	{
	
	    this.datelist = datelist;
	}

	public ArrayList getFieldlist()
	{
	
	    return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist)
	{
	
	    this.fieldlist = fieldlist;
	}

	public String getItemid()
	{
	
	    return itemid;
	}

	public void setItemid(String itemid)
	{
	
	    this.itemid = itemid;
	}

	public ArrayList getItemlist()
	{
	
	    return itemlist;
	}

	public void setItemlist(ArrayList itemlist)
	{
	
	    this.itemlist = itemlist;
	}

	public String getSql()
	{
	
	    return sql;
	}

	public void setSql(String sql)
	{
	
	    this.sql = sql;
	}

	public String getFieldStr()
	{
	
	    return fieldStr;
	}

	public void setFieldStr(String fieldStr)
	{
	
	    this.fieldStr = fieldStr;
	}

	public String getIsOnlySet() {
		return isOnlySet;
	}

	public void setIsOnlySet(String isOnlySet) {
		this.isOnlySet = isOnlySet;
	}

	public String getViewUnit() {
		return viewUnit;
	}

	public void setViewUnit(String viewUnit) {
		this.viewUnit = viewUnit;
	}

	public String getEmpfiltersql() {
		return empfiltersql;
	}

	public void setEmpfiltersql(String empfiltersql) {
		this.empfiltersql = empfiltersql;
	}


	public String getProright_str() {
		return proright_str;
	}

	public void setProright_str(String proright_str) {
		this.proright_str = proright_str;
	}

	public String getCond_id_str() {
		return cond_id_str;
	}

	public void setCond_id_str(String cond_id_str) {
		this.cond_id_str = cond_id_str;
	}

}
