package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * <p>Title:InitDataForm.java</p>
 * <p>Description:绩效/能力素质数据初始化</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-26 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class InitDataForm extends FrameForm 
{
	
	private ArrayList tableList=new ArrayList();
	private String isAllData="0";   //是否包括所有绩效数据
	private String timeScope="0";   //0：全部  1：时间范围
	private String startDate="";
	private String endDate="";
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)	
	
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("tableList",this.getTableList());
		this.getFormHM().put("timeScope",this.getTimeScope());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate",this.getEndDate());
		this.getFormHM().put("busitype", this.getBusitype());
	}
	@Override
    public void outPutFormHM()
	{
		this.setTableList((ArrayList)this.getFormHM().get("tableList"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
	}

	
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIsAllData() {
		return isAllData;
	}

	public void setIsAllData(String isAllData) {
		this.isAllData = isAllData;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getTimeScope() {
		return timeScope;
	}

	public void setTimeScope(String timeScope) {
		this.timeScope = timeScope;
	}

	public ArrayList getTableList() {
		return tableList;
	}

	public void setTableList(ArrayList tableList) {
		this.tableList = tableList;
	}
	
	public String getBusitype() {
		return busitype;
	}
	
	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

}
