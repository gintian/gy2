package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.staff;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


public class StaffForm extends FrameForm{
	
	//员工日志部分
	private String strSelect = "";
	private String strWhere = "";
	private String strColumns = "";
	private String strOrder = "";
	private String staff_year = "";
	private String staff_month = "";
	private String staff_week_show = "";//为了在页面上显示用的
	private String staff_week = "";
	private String staff_day = "";//它的格式如：2013-1-31.不能仅仅存储31
	private String staff_day_show = "";
	private String staff_name = "";
	private ArrayList staff_namelist = new ArrayList();
	
	private ArrayList yearList = new ArrayList();
	private ArrayList monthList = new ArrayList();
	private ArrayList weekList = new ArrayList();
	private ArrayList dayList = new ArrayList();
	private String fromFlag= "";
	private String a0100 = "";
	private String nbase = "";
	
	@Override
    public void inPutTransHM()
	{
		
		
		//员工日志部分
		this.getFormHM().put(strSelect, this.getStrSelect());
		this.getFormHM().put(strWhere, this.getStrWhere());
		this.getFormHM().put(strColumns, this.getStrColumns());
		this.getFormHM().put(strOrder, this.getStrOrder());
		this.getFormHM().put("staff_year", this.getStaff_year());
		this.getFormHM().put("staff_month", this.getStaff_month());
		this.getFormHM().put("staff_week", this.getStaff_week());
		this.getFormHM().put("staff_week_show", this.getStaff_week_show());
		this.getFormHM().put("staff_day", this.getStaff_day());
		this.getFormHM().put("staff_day_show", this.getStaff_day_show());
		this.getFormHM().put("staff_name", this.getStaff_name());
		this.getFormHM().put("staff_namelist", this.getStaff_namelist());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"21":(this.getPagerows()+""));
		this.getFormHM().put("yearList",this.getYearList());
		this.getFormHM().put("monthList", this.getMonthList());
		this.getFormHM().put("weekList", this.getWeekList());
		this.getFormHM().put("dayList", this.getDayList());
		this.getFormHM().put("fromFlag", this.getFromFlag());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
	}
	@Override
    public void outPutFormHM()
	{	
		
		this.setStrSelect((String)this.getFormHM().get("strSelect"));
		this.setStrWhere((String)this.getFormHM().get("strWhere"));
		this.setStrColumns((String)this.getFormHM().get("strColumns"));
		this.setStrOrder((String)this.getFormHM().get("strOrder"));
		this.setStaff_year((String)this.getFormHM().get("staff_year"));
		this.setStaff_month((String)this.getFormHM().get("staff_month"));
		this.setStaff_week((String)this.getFormHM().get("staff_week"));
		this.setStaff_week_show((String)this.getFormHM().get("staff_week_show"));
		this.setStaff_day((String)this.getFormHM().get("staff_day"));
		this.setStaff_day_show((String)this.getFormHM().get("staff_day_show"));
		this.setStaff_name((String)this.getFormHM().get("staff_name"));
		this.setStaff_namelist((ArrayList)this.getFormHM().get("staff_namelist"));
		this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));//控制每页显示多少条数据
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setWeekList((ArrayList)this.getFormHM().get("weekList"));
		this.setDayList((ArrayList)this.getFormHM().get("dayList"));
		this.setFromFlag((String)this.getFormHM().get("fromFlag"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
	}

	public String getFromFlag() {
		return fromFlag;
	}
	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	@Override
    public void reset(ActionMapping parm1, HttpServletRequest parm2)
	{
	    super.reset(parm1, parm2);
	}
	public ArrayList getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}
	public ArrayList getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}
	public ArrayList getWeekList() {
		return weekList;
	}
	public void setWeekList(ArrayList weekList) {
		this.weekList = weekList;
	}
	public ArrayList getDayList() {
		return dayList;
	}
	public void setDayList(ArrayList dayList) {
		this.dayList = dayList;
	}
	public String getStrSelect() {
		return strSelect;
	}
	public void setStrSelect(String strSelect) {
		this.strSelect = strSelect;
	}
	public String getStrWhere() {
		return strWhere;
	}
	public void setStrWhere(String strWhere) {
		this.strWhere = strWhere;
	}
	public String getStrColumns() {
		return strColumns;
	}
	public void setStrColumns(String strColumns) {
		this.strColumns = strColumns;
	}
	public String getStrOrder() {
		return strOrder;
	}
	public void setStrOrder(String strOrder) {
		this.strOrder = strOrder;
	}
	public String getStaff_year() {
		return staff_year;
	}
	public void setStaff_year(String staff_year) {
		this.staff_year = staff_year;
	}
	public String getStaff_month() {
		return staff_month;
	}
	public void setStaff_month(String staff_month) {
		this.staff_month = staff_month;
	}
	public String getStaff_week() {
		return staff_week;
	}
	public void setStaff_week(String staff_week) {
		this.staff_week = staff_week;
	}
	public String getStaff_day() {
		return staff_day;
	}
	public void setStaff_day(String staff_day) {
		this.staff_day = staff_day;
	}
	public String getStaff_name() {
		return staff_name;
	}
	public void setStaff_name(String staff_name) {
		this.staff_name = staff_name;
	}
	public String getStaff_week_show() {
		return staff_week_show;
	}
	public void setStaff_week_show(String staff_week_show) {
		this.staff_week_show = staff_week_show;
	}
	
	public String getStaff_day_show() {
		return staff_day_show;
	}
	public void setStaff_day_show(String staff_day_show) {
		this.staff_day_show = staff_day_show;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public ArrayList getStaff_namelist() {
		return staff_namelist;
	}
	public void setStaff_namelist(ArrayList staff_namelist) {
		this.staff_namelist = staff_namelist;
	}
	
}
