package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.daywork;

import com.hrms.struts.action.FrameForm;

public class CalendarDayForm extends FrameForm{

	private String calendarDayHtml = "";//输出日历
	private String workRecordHtml = "";//输出工作记录列表
	private String recordShowHtml = "";//输出具体的工作记录
	private String commentTrace = "";//领导痕迹
	private String jsonstr = "";//输出时间段事件
	private String leader = "";//领导批示
	
	private String scrollValue = "";//记录事件列表滚动条的位置
	private String wholeScroll = "";
	private String axle = "";//时间轴的滚动条位置
	private String p01_key = "";//初始进入页面时时间是否需要变蓝。配合recordNum一起使用
	private String recordNum = "";//初始进入页面时时间是否需要变蓝
	private String frompage = "";
	private String fromyear = "";//从周、月、年报中传递过来的年
	private String frommonth = "";
	private String fromday = "";
	private String isowner = "";//js方法showDetail的一个参数
	
	//员工日志部分
	private String a0100 = "";//人员编号
	private String nbase = "";//人员库
	private String staff_url = "";//返回到员工日志的url
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("calendarDayHtml", this.getCalendarDayHtml());
		this.getFormHM().put("workRecordHtml", this.getWorkRecordHtml());
		this.getFormHM().put("recordShowHtml", this.getRecordShowHtml());
		this.getFormHM().put("commentTrace", this.getCommentTrace());
		this.getFormHM().put("jsonstr", this.getJsonstr());
		this.getFormHM().put("leader", this.getLeader());
		
		this.getFormHM().put("scrollValue", this.getScrollValue());
		this.getFormHM().put("wholeScroll", this.getWholeScroll());
		this.getFormHM().put("axle", this.getAxle());
		this.getFormHM().put("p01_key", this.getP01_key());
		this.getFormHM().put("recordNum", this.getRecordNum());
		this.getFormHM().put("frompage", this.getFrompage());
		this.getFormHM().put("fromyear", this.getFromyear());
		this.getFormHM().put("frommonth", this.getFrommonth());
		this.getFormHM().put("fromday", this.getFromday());
		this.getFormHM().put("isowner", this.getIsowner());
		
		//员工日志部分
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("staff_url", this.getStaff_url());
	}

	@Override
    public void outPutFormHM() {
		this.setCalendarDayHtml((String)this.getFormHM().get("calendarDayHtml"));
		this.setWorkRecordHtml((String)this.getFormHM().get("workRecordHtml"));
		this.setRecordShowHtml((String)this.getFormHM().get("recordShowHtml"));
		this.setCommentTrace((String)this.getFormHM().get("commentTrace"));
		this.setJsonstr((String)this.getFormHM().get("jsonstr"));
		this.setLeader((String)this.getFormHM().get("leader"));
		
		this.setScrollValue((String)this.getFormHM().get("scrollValue"));
		this.setWholeScroll((String)this.getFormHM().get("wholeScroll"));
		this.setAxle((String)this.getFormHM().get("axle"));
		this.setP01_key((String)this.getFormHM().get("p01_key"));
		this.setRecordNum((String)this.getFormHM().get("recordNum"));
		this.setFrompage((String)this.getFormHM().get("frompage"));
		this.setFromyear((String)this.getFormHM().get("fromyear"));
		this.setFrommonth((String)this.getFormHM().get("frommonth"));
		this.setFromday((String)this.getFormHM().get("fromday"));
		this.setIsowner((String)this.getFormHM().get("isowner"));
		
		//员工日志部分
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setStaff_url((String)this.getFormHM().get("staff_url"));
	}

	public String getCalendarDayHtml() {
		return calendarDayHtml;
	}

	public void setCalendarDayHtml(String calendarDayHtml) {
		this.calendarDayHtml = calendarDayHtml;
	}

	public String getWorkRecordHtml() {
		return workRecordHtml;
	}

	public void setWorkRecordHtml(String workRecordHtml) {
		this.workRecordHtml = workRecordHtml;
	}

	public String getRecordShowHtml() {
		return recordShowHtml;
	}

	public void setRecordShowHtml(String recordShowHtml) {
		this.recordShowHtml = recordShowHtml;
	}

	public String getScrollValue() {
		return scrollValue;
	}

	public void setScrollValue(String scrollValue) {
		this.scrollValue = scrollValue;
	}

	public String getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(String recordNum) {
		this.recordNum = recordNum;
	}

	public String getFrompage() {
		return frompage;
	}

	public void setFrompage(String frompage) {
		this.frompage = frompage;
	}

	public String getFromyear() {
		return fromyear;
	}

	public void setFromyear(String fromyear) {
		this.fromyear = fromyear;
	}

	public String getFrommonth() {
		return frommonth;
	}

	public void setFrommonth(String frommonth) {
		this.frommonth = frommonth;
	}

	public String getFromday() {
		return fromday;
	}

	public void setFromday(String fromday) {
		this.fromday = fromday;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getStaff_url() {
		return staff_url;
	}

	public void setStaff_url(String staff_url) {
		this.staff_url = staff_url;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getP01_key() {
		return p01_key;
	}

	public void setP01_key(String p01_key) {
		this.p01_key = p01_key;
	}

	public String getJsonstr() {
		return jsonstr;
	}

	public void setJsonstr(String jsonstr) {
		this.jsonstr = jsonstr;
	}

	public String getIsowner() {
		return isowner;
	}

	public void setIsowner(String isowner) {
		this.isowner = isowner;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getAxle() {
		return axle;
	}

	public void setAxle(String axle) {
		this.axle = axle;
	}

	public String getWholeScroll() {
		return wholeScroll;
	}

	public void setWholeScroll(String wholeScroll) {
		this.wholeScroll = wholeScroll;
	}

	public String getCommentTrace() {
		return commentTrace;
	}

	public void setCommentTrace(String commentTrace) {
		this.commentTrace = commentTrace;
	}

}
