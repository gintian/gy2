package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.yearwork;

import com.hrms.struts.action.FrameForm;

/**
 * YearWorkForm.java
 * Description: 国网年报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 8, 2013 2:59:32 PM Jianghe created
 */
public class YearWorkForm extends FrameForm{
	private String currentTime="";
	private String currentYear="";
	private String tableHtml="";
	private String returnUrl="";
	private String init="";//判断入口，init 菜单 ,1 切换月份，4 从员工日志进
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("currentYear",this.getCurrentYear());
		this.getFormHM().put("returnUrl", this.getReturnUrl());
	}
	@Override
    public void outPutFormHM()
	{	
		this.setCurrentTime((String)this.getFormHM().get("currentTime"));
		this.setCurrentYear((String)this.getFormHM().get("currentYear"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setReturnUrl((String)this.getFormHM().get("returnUrl"));
		this.setInit((String)this.getFormHM().get("init"));
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
	public String getTableHtml() {
		return tableHtml;
	}
	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
}
