package com.hjsj.hrms.actionform.performance.workPlanTeam;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class WorkPlanTeamForm extends FrameForm{
    //2年 4季 6 月 8日 10周
	//=1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结=7个人日报=8团队日报=9个人周报=10团队周报
	private String workType;
	private String state;//=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	private String a_code;
	private String year="";
	private String month="";
	private String status="";//状态
	private String log_type = "";//日志类型
	private String name = "";
	
	private String str_sql="";
	private String str_whl="";
	private String order_str="";
	private String columns = "";
	private String    isSelectedAll="0";               //是否全选 1.全选
	private HashMap spMap = new HashMap();
	private LinkedHashMap weekMap = new LinkedHashMap();
	
	private ArrayList yearList = new ArrayList();
	private ArrayList monthList = new ArrayList();
	private ArrayList statusList = new ArrayList();
	private ArrayList logtypeList = new ArrayList();
	private ArrayList seasonList = new ArrayList();
	private ArrayList weekList = new ArrayList();
	private ArrayList dayList = new ArrayList();
	private ArrayList noFillList = new ArrayList();
	private PaginationForm setlistform = new PaginationForm();
	
	private String season = "";
	private String week = "";
	private String day = "";
	
	private String startime = ""; // 计划或总结的开始时间
	private String dbType = "1";	
	private String print_id = ""; // 打印信息登记表
	
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("startime",this.getStartime());
		this.getFormHM().put("dbType",this.getDbType());
		this.getFormHM().put("print_id",this.getPrint_id());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("workType",this.getWorkType());
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("month",this.getMonth());
		this.getFormHM().put("season",this.getSeason());
		this.getFormHM().put("week",this.getWeek());
		this.getFormHM().put("day",this.getDay());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("log_type",this.getLog_type());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("isSelectedAll",this.getIsSelectedAll());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"20":(this.getPagerows()+""));
		
	}
	@Override
    public void outPutFormHM()
	{	
		
		this.setStartime((String)this.getFormHM().get("startime"));
		this.setDbType((String)this.getFormHM().get("dbType"));
		this.setPrint_id((String)this.getFormHM().get("print_id"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setState((String)this.getFormHM().get("state"));
		this.setWorkType((String)this.getFormHM().get("workType"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setLog_type((String)this.getFormHM().get("log_type"));
		this.setSeason((String)this.getFormHM().get("season"));
		this.setWeek((String)this.getFormHM().get("week"));
		this.setDay((String)this.getFormHM().get("day"));
		this.setName((String)this.getFormHM().get("name"));
		this.setStr_sql((String)this.getFormHM().get("str_sql"));
		this.setStr_whl((String)this.getFormHM().get("str_whl"));
		this.setOrder_str((String)this.getFormHM().get("order_str"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setIsSelectedAll((String)this.getFormHM().get("isSelectedAll"));
		this.setSpMap((HashMap)this.getFormHM().get("spMap"));
		this.setWeekMap((LinkedHashMap)this.getFormHM().get("weekMap"));
		this.setPagerows(Integer.parseInt((String)this.getFormHM().get("pagerows")));	
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.setLogtypeList((ArrayList)this.getFormHM().get("logtypeList"));
		this.setSeasonList((ArrayList)this.getFormHM().get("seasonList"));
		this.setWeekList((ArrayList)this.getFormHM().get("weekList"));
		this.setDayList((ArrayList)this.getFormHM().get("dayList"));
		this.setNoFillList((ArrayList)this.getFormHM().get("noFillList"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("noFillList"));

	}
    public String getWorkType()
    {
    	return this.workType;
    }
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public HashMap getSpMap() {
		return spMap;
	}
	public void setSpMap(HashMap spMap) {
		this.spMap = spMap;
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
	public ArrayList getStatusList() {
		return statusList;
	}
	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}
	public String getIsSelectedAll() {
		return isSelectedAll;
	}
	public void setIsSelectedAll(String isSelectedAll) {
		this.isSelectedAll = isSelectedAll;
	}
	public ArrayList getSeasonList() {
		return seasonList;
	}
	public void setSeasonList(ArrayList seasonList) {
		this.seasonList = seasonList;
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
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public ArrayList getNoFillList() {
		return noFillList;
	}
	public void setNoFillList(ArrayList noFillList) {
		this.noFillList = noFillList;
	}
	public PaginationForm getSetlistform() {
		return setlistform;
	}
	public void setSetlistform(PaginationForm setlistform) {
		this.setlistform = setlistform;
	}
	public LinkedHashMap getWeekMap() {
		return weekMap;
	}
	public void setWeekMap(LinkedHashMap weekMap) {
		this.weekMap = weekMap;
	}
	public String getLog_type() {
		return log_type;
	}
	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}
	public ArrayList getLogtypeList() {
		return logtypeList;
	}
	public void setLogtypeList(ArrayList logtypeList) {
		this.logtypeList = logtypeList;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
//		if(arg1.getParameter("flag")!=null&&arg1.getParameter("flag").equals("1"))
//		{
//			if(this.getPagination()!=null)
//				this.getPagination().firstPage();
//			    this.getSetlistform().getPagination().firstPage();
//		}
		if(this.getPagination()!=null){
			this.getPagination().firstPage();
		    this.getSetlistform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	public String getStartime() {
		return startime;
	}
	public void setStartime(String startime) {
		this.startime = startime;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getPrint_id() {
		return print_id;
	}
	public void setPrint_id(String print_id) {
		this.print_id = print_id;
	}
}
