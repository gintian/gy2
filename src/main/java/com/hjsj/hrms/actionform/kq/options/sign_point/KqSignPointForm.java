package com.hjsj.hrms.actionform.kq.options.sign_point;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class KqSignPointForm extends FrameForm{

	private String pid; //考勤点id
	private ArrayList signPoints; //考勤点集合
	private String arealevel; //可是区域范围
	private String nbaseStr; //考勤人员库，多个以逗号隔开 Ps: Usr,Oth,.....
	private String pointRadius;//考勤点半径，单位米
	private String selectedA0100; //页面下拉列表选中的人 格式： Usr`000000009

	private String uplevel; //部门层级
	private String cond_str;
	private String sql_str;
	private String cond_order;
	private String columns;

	
	/** 人员信息的字段列表 */
	private PaginationForm kqSignPointForm = new PaginationForm();
	/* 选中的字段值对列表 */
	private ArrayList selectfieldlist = new ArrayList();

	private String orglist;//考勤机构集合，以,隔开
	private String cflag;//操作标识
	
	public String getCflag() {
		return cflag;
	}



	public void setCflag(String cflag) {
		this.cflag = cflag;
	}



	public String getOrglist() {
		return orglist;
	}



	public void setOrglist(String orglist) {
		this.orglist = orglist;
	}



	@Override
    public void outPutFormHM() {
		HashMap paramMap = this.getFormHM();

		
		
		this.getKqSignPointForm().getPagination().gotoPage(1);
		this.setSelectfieldlist((ArrayList) this.getFormHM().get("selectfieldlist"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setCond_str((String) this.getFormHM().get("cond_str"));
		this.setSql_str((String) this.getFormHM().get("sql_str"));
		this.setCond_order((String) this.getFormHM().get("cond_order"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setPid((String) this.getFormHM().get("pid"));
		

		this.setPid((String)paramMap.get("pid"));
		this.setSignPoints((ArrayList)paramMap.get("signPoints"));
		this.setArealevel((String)paramMap.get("arealevel"));
		this.setPointRadius((String)paramMap.get("pointRadius"));
		this.setNbaseStr((String)paramMap.get("nbaseStr"));
		
		this.setOrglist((String) this.getFormHM().get(orglist));
		this.setCflag((String) this.getFormHM().get("cflag"));

	}



	@Override
    public void inPutTransHM() {

		if (this.getPagination() != null)
			this.getFormHM().put("selectfieldlist",(ArrayList) this.getPagination().getSelectedList());
		this.getFormHM().put("uplevel", this.getUplevel());
		this.getFormHM().put("cond_str", this.getCond_str());
		this.getFormHM().put("sql_str", this.getSql_str());
		this.getFormHM().put("cond_order", this.getCond_order());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("pid", this.getPid());

		this.getFormHM().put("pointRadius", this.getPointRadius());
		this.getFormHM().put("selectedA0100", this.getSelectedA0100());
		
		this.getFormHM().put("orglist", this.getOrglist());
		this.getFormHM().put("cflag", this.getCflag());

	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//查询人员返回第一页
		if ("/kq/options/sign_point/person_point".equals(arg0.getPath()) && arg1.getParameter("b_searchperson") != null) {
			 if (this.getPagination() != null) 
		            this.getPagination().firstPage();
        }
		return super.validate(arg0, arg1);
	}
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}


	public ArrayList getSelectfieldlist()
	{
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist)
	{
		this.selectfieldlist = selectfieldlist;
	}

	public String getUplevel()
	{
		return uplevel;
	}

	public void setUplevel(String uplevel)
	{
		this.uplevel = uplevel;
	}

	public String getCond_str()
	{
		return cond_str;
	}

	public void setCond_str(String condStr)
	{
		cond_str = condStr;
	}

	public String getSql_str()
	{
		return sql_str;
	}

	public void setSql_str(String sqlStr)
	{
		sql_str = sqlStr;
	}

	public String getCond_order()
	{
		return cond_order;
	}

	public void setCond_order(String condOrder)
	{
		cond_order = condOrder;
	}

	public String getColumns()
	{
		return columns;
	}

	public void setColumns(String columns)
	{
		this.columns = columns;
	}



	public PaginationForm getKqSignPointForm()
	{
		return kqSignPointForm;
	}

	public void setKqSignPointForm(PaginationForm kqSignPointForm)
	{
		this.kqSignPointForm = kqSignPointForm;
	}


	public ArrayList getSignPoints() {
		return signPoints;
	}

	public void setSignPoints(ArrayList signPoints) {
		this.signPoints = signPoints;
	}

	public String getArealevel() {
		return arealevel;
	}

	public void setArealevel(String arealevel) {
		this.arealevel = arealevel;
	}

	public String getPointRadius() {
		return pointRadius;
	}

	public void setPointRadius(String pointRadius) {
		this.pointRadius = pointRadius;
	}

	public String getNbaseStr() {
		return nbaseStr;
	}

	public void setNbaseStr(String nbaseStr) {
		this.nbaseStr = nbaseStr;
	}



	public String getSelectedA0100() {
		return selectedA0100;
	}



	public void setSelectedA0100(String selectedA0100) {
		this.selectedA0100 = selectedA0100;
	}


	
}
