package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.monthwork;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * MonthWorkForm.java
 * Description: 国网月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 8, 2013 2:59:20 PM Jianghe created
 */
public class MonthWorkForm extends FrameForm{
	private String currentTime="";
	private String currentYear="";
	private String currentMonth="";
	private String currentDay="";
	private String tableHtml="";
	private String p0100="";
	private String record_num="";
	private String init="";//判断入口，init 菜单 ,1 月，2 从年日志进
	private String returnUrl="";
	private String hp_start;
	private String hp_end;
	private ArrayList gridList = new ArrayList();
	private String currDateStr;
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("currentYear",this.getCurrentYear());
		this.getFormHM().put("currentMonth",this.getCurrentMonth());
		this.getFormHM().put("currentDay",this.getCurrentDay());
		this.getFormHM().put("record_num",this.getRecord_num());
		this.getFormHM().put("p0100",this.getP0100());
		this.getFormHM().put("hp_start", this.getHp_start());
		this.getFormHM().put("hp_end", this.getHp_end());
		this.getFormHM().put("currDateStr", this.getCurrDateStr());
		this.getFormHM().put("returnUrl", this.getReturnUrl());
	}
	@Override
    public void outPutFormHM()
	{	
		this.setCurrentTime((String)this.getFormHM().get("currentTime"));
		this.setCurrentYear((String)this.getFormHM().get("currentYear"));
		this.setCurrentMonth((String)this.getFormHM().get("currentMonth"));
		this.setCurrentDay((String)this.getFormHM().get("currentDay"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setP0100((String)this.getFormHM().get("p0100"));
		this.setRecord_num((String)this.getFormHM().get("record_num"));
		this.setInit((String)this.getFormHM().get("init"));
		this.setReturnUrl((String)this.getFormHM().get("returnUrl"));
		this.setCurrDateStr((String)this.getFormHM().get("currDateStr"));
		this.setHp_start((String)this.getFormHM().get("hp_start"));
		this.setHp_end((String)this.getFormHM().get("hp_end"));
		this.setGridList((ArrayList)this.getFormHM().get("gridList"));
	}
	public String getCurrDateStr() {
		return currDateStr;
	}
	public void setCurrDateStr(String currDateStr) {
		this.currDateStr = currDateStr;
	}
	public String getHp_start() {
		return hp_start;
	}
	public void setHp_start(String hp_start) {
		this.hp_start = hp_start;
	}
	public String getHp_end() {
		return hp_end;
	}
	public void setHp_end(String hp_end) {
		this.hp_end = hp_end;
	}
	public ArrayList getGridList() {
		return gridList;
	}
	public void setGridList(ArrayList gridList) {
		this.gridList = gridList;
	}


	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public String getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}
	public String getCurrentMonth() {
		return currentMonth;
	}
	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}
	public String getTableHtml() {
		return tableHtml;
	}
	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}
	public String getCurrentDay() {
		return currentDay;
	}
	public void setCurrentDay(String currentDay) {
		this.currentDay = currentDay;
	}
	public String getP0100() {
		return p0100;
	}
	public void setP0100(String p0100) {
		this.p0100 = p0100;
	}
	public String getRecord_num() {
		return record_num;
	}
	public void setRecord_num(String record_num) {
		this.record_num = record_num;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
}
