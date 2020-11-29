package com.hjsj.hrms.actionform.general.muster.hmuster;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class StipendHmusterForm extends FrameForm {
	String html="";
	String a0100="";
	String dbpre="";
	String isTimeIdentifine="0";  // 0:无年月标识  1：有
	String operate="";    // 0:无条件  1：按年查询   2：按月查询  3 按季度查询  4 按时间段查询
	String year="";
	String month="";
	String startDate="";
	String endDate="";
	String quarter="";
	ArrayList operateList=getoperateList();
	ArrayList yearList=new ArrayList();
	ArrayList monthList=getMonthList();
	ArrayList quarterList=getQurterList();
	ArrayList hmusterList=new ArrayList();
	String musterID="";
	String musterName="";
	String paperRows="21";
	private String groupCount;
	private String flag;
	private String musterFlag;
	
	public String getMusterFlag() {
		return musterFlag;
	}

	public void setMusterFlag(String musterFlag) {
		this.musterFlag = musterFlag;
	}

	@Override
    public void outPutFormHM() {
		this.setMusterFlag((String)this.getFormHM().get("musterFlag"));
		this.setGroupCount((String)this.getFormHM().get("groupCount"));
		this.setIsTimeIdentifine((String)this.getFormHM().get("isTimeIdentifine"));
		this.setOperate((String)this.getFormHM().get("operate"));
		this.setHtml((String)this.getFormHM().get("html"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setHmusterList((ArrayList)this.getFormHM().get("hmusterList"));
		this.setMusterID((String)this.getFormHM().get("musterID"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMusterName((String)this.getFormHM().get("musterName"));
		this.setPaperRows((String)this.getFormHM().get("paperRows"));
	}

	@Override
    public void inPutTransHM() {
		if(this.getIsTimeIdentifine()!=null&& "1".equals(this.getIsTimeIdentifine()))
		{
			this.getFormHM().put("operate",this.getOperate());
			this.getFormHM().put("year",this.getYear());
			this.getFormHM().put("month",this.getMonth());
			this.getFormHM().put("quarter",this.getQuarter());
			this.getFormHM().put("startDate",this.getStartDate());
			this.getFormHM().put("endDate",this.getEndDate());
			this.getFormHM().put("groupCount", this.getGroupCount());
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("musterFlag", this.getMusterFlag());
	}

	public ArrayList getoperateList()
	{
		ArrayList list=new ArrayList();
		CommonData data0=new CommonData("1","年");
		list.add(data0);
		CommonData data1=new CommonData("2","月份");
		list.add(data1);
		CommonData data2=new CommonData("3","季度");
		list.add(data2);
		CommonData data3=new CommonData("4","时间范围");
		list.add(data3);
		return list;
	}
	
	public ArrayList getQurterList()
	{
		ArrayList list=new ArrayList();
		for(int i=1;i<=4;i++)
		{
			CommonData data1=new CommonData(String.valueOf(i),String.valueOf(i));
			list.add(data1);
		}
		return list;
	}
	
	public ArrayList getMonthList()
	{
		ArrayList list=new ArrayList();
		for(int i=1;i<=12;i++)
		{
			CommonData data1=new CommonData(String.valueOf(i),String.valueOf(i));
			list.add(data1);
		}
		
		return list;
	}
	
	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ArrayList getHmusterList() {
		return hmusterList;
	}

	public void setHmusterList(ArrayList hmusterList) {
		this.hmusterList = hmusterList;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getIsTimeIdentifine() {
		return isTimeIdentifine;
	}

	public void setIsTimeIdentifine(String isTimeIdentifine) {
		this.isTimeIdentifine = isTimeIdentifine;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMusterID() {
		return musterID;
	}

	public void setMusterID(String musterID) {
		this.musterID = musterID;
	}

	public String getMusterName() {
		return musterName;
	}

	public void setMusterName(String musterName) {
		this.musterName = musterName;
	}

	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}

	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}

	public ArrayList getQuarterList() {
		return quarterList;
	}

	public void setQuarterList(ArrayList quarterList) {
		this.quarterList = quarterList;
	}

	public ArrayList getOperateList() {
		return operateList;
	}

	public void setOperateList(ArrayList operateList) {
		this.operateList = operateList;
	}

	public String getPaperRows() {
		return paperRows;
	}

	public void setPaperRows(String paperRows) {
		this.paperRows = paperRows;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
