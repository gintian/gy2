package com.hjsj.hrms.actionform.performance.nworkplan;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class MonthWorkplanForm extends FrameForm{
	// 年，季，月，周 标识 4,3,2,1
	private String state = "";
	// 审批关系
	private String sp_relation = ""; 
	//本月工作总结
	//当月总结显示的时间段
	private String currentTime="";
	//当月总结所对应的 年&月
	private String currentYear="";
	private String currentMonth="";
	//总结指标  title,content  从工作纪实设置中获取
	private String summarizeFields="";
	//页面显示的当月共有多少条总结数据
	private String summarizeDataSize="";
	//页面显示的当月总结数据    bean
	private ArrayList summarizeDataList=new ArrayList();
	//总结所用指标     bean:itemid,itemtype,itemdesc,codesetid,decimalwidth,          value,viewvalue//用于新增和修改
	private ArrayList zongjieFieldsList=new ArrayList();
	
	//下月工作计划
	private String nextTime="";
	private String nextYear="";
	private String nextMonth="";
	private String planFields="";
	private String planDataSize="";
	private ArrayList planDataList=new ArrayList();
	//计划所用指标     bean:itemid,itemtype,itemdesc,codesetid,decimalwidth,          value,viewvalue,fillable//用于新增和修改
	private ArrayList jihuaFieldsList=new ArrayList();
	//状态 01 起草,02 已报批,03 已批,07 驳回,"" 未填
	private String p0115="";
	//判断入口，init 菜单 ,4 从团队工作纪实进
	private String init="";
	//当前时间对应的p0100
	private String p0100="";
	private String record_num="";
	//1 新增  2 修改
	private String type="";
	//1 计划  2 总结
	private String log_type="";
	//1 保存  2 保存&继续
	private String saveflag="";
	//高亮的记录
	private String hyperlinkRecord = "";
	private String hyperlinkP0100 = "";
	//0 从人员tab进入   1 从部门tab进入
	String personPage = "";
	//当前人是否是处长  0 职员 1 处长
	String isChuZhang="";
	@Override
    public void inPutTransHM()
    { 
		this.getFormHM().put("currentYear", this.getCurrentYear());
		this.getFormHM().put("currentMonth", this.getCurrentMonth());
		this.getFormHM().put("nextYear", this.getNextYear());
		this.getFormHM().put("nextMonth", this.getNextMonth());
		this.getFormHM().put("p0100", this.getP0100());
		this.getFormHM().put("record_num", this.getRecord_num());
		this.getFormHM().put("hyperlinkRecord", this.getHyperlinkRecord());
		this.getFormHM().put("hyperlinkP0100", this.getHyperlinkP0100());
		this.getFormHM().put("zongjieFieldsList", this.getZongjieFieldsList());
		this.getFormHM().put("jihuaFieldsList", this.getJihuaFieldsList());
    }
    @Override
    public void outPutFormHM()
    {
    	this.setPersonPage((String)this.getFormHM().get("personPage"));
    	this.setIsChuZhang((String)this.getFormHM().get("isChuZhang"));
    	this.setState((String)this.getFormHM().get("state"));
    	this.setSp_relation((String)this.getFormHM().get("sp_relation"));
    	this.setSaveflag((String)this.getFormHM().get("saveflag"));
    	this.setType((String)this.getFormHM().get("type"));
    	this.setLog_type((String)this.getFormHM().get("log_type"));
    	this.setCurrentTime((String)this.getFormHM().get("currentTime"));
    	this.setCurrentYear((String)this.getFormHM().get("currentYear"));
    	this.setCurrentMonth((String)this.getFormHM().get("currentMonth"));
    	this.setSummarizeFields((String)this.getFormHM().get("summarizeFields"));
    	this.setNextTime((String)this.getFormHM().get("nextTime"));
    	this.setNextYear((String)this.getFormHM().get("nextYear"));
    	this.setNextMonth((String)this.getFormHM().get("nextMonth"));
    	this.setPlanFields((String)this.getFormHM().get("planFields"));
    	this.setP0115((String)this.getFormHM().get("p0115"));
    	this.setInit((String)this.getFormHM().get("init"));
    	this.setP0100((String)this.getFormHM().get("p0100"));
    	this.setRecord_num((String)this.getFormHM().get("record_num"));
    	this.setHyperlinkRecord((String)this.getFormHM().get("hyperlinkRecord"));
    	this.setHyperlinkP0100((String)this.getFormHM().get("hyperlinkP0100"));
    	this.setSummarizeDataSize((String)this.getFormHM().get("summarizeDataSize"));
    	this.setPlanDataSize((String)this.getFormHM().get("planDataSize"));
    	this.setPlanDataList((ArrayList)this.getFormHM().get("planDataList"));
		this.setSummarizeDataList((ArrayList)this.getFormHM().get("summarizeDataList"));
		this.setZongjieFieldsList((ArrayList)this.getFormHM().get("zongjieFieldsList"));
		this.setJihuaFieldsList((ArrayList)this.getFormHM().get("jihuaFieldsList"));
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
	
	public String getSummarizeFields() {
		return summarizeFields;
	}
	public void setSummarizeFields(String summarizeFields) {
		this.summarizeFields = summarizeFields;
	}
	public String getNextTime() {
		return nextTime;
	}
	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}
	public String getNextYear() {
		return nextYear;
	}
	public void setNextYear(String nextYear) {
		this.nextYear = nextYear;
	}
	
	public String getCurrentMonth() {
		return currentMonth;
	}
	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}
	public String getNextMonth() {
		return nextMonth;
	}
	public void setNextMonth(String nextMonth) {
		this.nextMonth = nextMonth;
	}
	public String getPlanFields() {
		return planFields;
	}
	public void setPlanFields(String planFields) {
		this.planFields = planFields;
	}
	
	public String getP0115() {
		return p0115;
	}
	public void setP0115(String p0115) {
		this.p0115 = p0115;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
	public ArrayList getSummarizeDataList() {
		return summarizeDataList;
	}
	public void setSummarizeDataList(ArrayList summarizeDataList) {
		this.summarizeDataList = summarizeDataList;
	}
	public ArrayList getPlanDataList() {
		return planDataList;
	}
	public void setPlanDataList(ArrayList planDataList) {
		this.planDataList = planDataList;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLog_type() {
		return log_type;
	}
	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}
	public String getSummarizeDataSize() {
		return summarizeDataSize;
	}
	public void setSummarizeDataSize(String summarizeDataSize) {
		this.summarizeDataSize = summarizeDataSize;
	}
	public String getPlanDataSize() {
		return planDataSize;
	}
	public void setPlanDataSize(String planDataSize) {
		this.planDataSize = planDataSize;
	}
	public ArrayList getZongjieFieldsList() {
		return zongjieFieldsList;
	}
	public void setZongjieFieldsList(ArrayList zongjieFieldsList) {
		this.zongjieFieldsList = zongjieFieldsList;
	}
	public ArrayList getJihuaFieldsList() {
		return jihuaFieldsList;
	}
	public void setJihuaFieldsList(ArrayList jihuaFieldsList) {
		this.jihuaFieldsList = jihuaFieldsList;
	}
	public String getSaveflag() {
		return saveflag;
	}
	public void setSaveflag(String saveflag) {
		this.saveflag = saveflag;
	}
	public String getSp_relation() {
		return sp_relation;
	}
	public void setSp_relation(String sp_relation) {
		this.sp_relation = sp_relation;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getHyperlinkRecord() {
		return hyperlinkRecord;
	}
	public void setHyperlinkRecord(String hyperlinkRecord) {
		this.hyperlinkRecord = hyperlinkRecord;
	}
	public String getHyperlinkP0100() {
		return hyperlinkP0100;
	}
	public void setHyperlinkP0100(String hyperlinkP0100) {
		this.hyperlinkP0100 = hyperlinkP0100;
	}
	public String getPersonPage() {
		return personPage;
	}
	public void setPersonPage(String personPage) {
		this.personPage = personPage;
	}
	public String getIsChuZhang() {
		return isChuZhang;
	}
	public void setIsChuZhang(String isChuZhang) {
		this.isChuZhang = isChuZhang;
	}
}
