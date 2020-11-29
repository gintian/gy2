package com.hjsj.hrms.businessobject.sys.options;

import java.util.TreeMap;

public class UserPopedom {

	//描述信息
	private String displayMessage; //表格描述信息
	
	// 用户权限信息
	private String orgOrUserGroup ; // 单位
	private String dept; // 部门
	private String job ; // 职位
	private String a0100; //个人
	
	private String dbPres;		 //应用库
	private String managerSpace; //管理范围
	private String formula;	//记录授权高级条件
	private String partymanager;
	private String menbermanager;
	
	private String setOrItemReadPriv; //子集或指标读权限
	private String setOrItemWritePriv; //子集或指标写权限
	
	
	private String selfFunctionPriv; //自助功能权限
	private String operFunctionPriv; //业务功能权限
	private TreeMap functionPriv;
	
	//资源权限
	private String cardResourcePriv;  //登记表权限
	private String reportResourcePriv;//统计表权限
	private String lexprResourcePriv; //常用查询权限
	private String staticsResourcePriv; //常用统计权限
	private String musterResourcePriv; //常用花名册权限
	private String highMusterResourcePriv; //高级花名册权限
	private String lawruleResourcePriv;  //规章制度权限
	private String rsbdResourcePriv;//人事异动权限
	private String xzbdResourcePriv;//薪资变动
	private String wjdcResourcePriv;//问卷调查
	private String pxbResourcePriv;//培训班
	private String gglResourcePriv;//公告栏
	private String xzlbResourcePriv;//薪资类别
	private String gzfxtResourcePriv;//工资分析图表
	private String daflResourcePriv;//档案分类
	private String kqjResourcePriv;//考勤机
	private String bxbdResourcePriv;//保险变动
	private String orgbdResourcePriv;
	private String posbdResourcePriv;
	private String bxlbResourcePriv;//保险类别
	private String wdflResourcePriv;//文档分类
	private String zsflResourcePriv;//知识分类
	private String khzbResourcePriv;//考核指标
	private String khmbResourcePriv;//考核模板
	private String jbbcResourcePriv;//基本班次
	private String kqbzResourcePriv;//考勤班组
	
	

	public String getJbbcResourcePriv() {
		return jbbcResourcePriv;
	}
	public void setJbbcResourcePriv(String jbbcResourcePriv) {
		this.jbbcResourcePriv = jbbcResourcePriv;
	}
	public String getKqbzResourcePriv() {
		return kqbzResourcePriv;
	}
	public void setKqbzResourcePriv(String kqbzResourcePriv) {
		this.kqbzResourcePriv = kqbzResourcePriv;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getOrgOrUserGroup() {
		return orgOrUserGroup;
	}
	public void setOrgOrUserGroup(String orgOrUserGroup) {
		this.orgOrUserGroup = orgOrUserGroup;
	}
	public String getDbPres() {
		return dbPres;
	}
	public void setDbPres(String dbPres) {
		this.dbPres = dbPres;
	}
	public String getManagerSpace() {
		return managerSpace;
	}
	public void setManagerSpace(String managerSpace) {
		this.managerSpace = managerSpace;
	}
	public String getOperFunctionPriv() {
		return operFunctionPriv;
	}
	public void setOperFunctionPriv(String operFunctionPriv) {
		this.operFunctionPriv = operFunctionPriv;
	}
	public String getSelfFunctionPriv() {
		return selfFunctionPriv;
	}
	public void setSelfFunctionPriv(String selfFunctionPriv) {
		this.selfFunctionPriv = selfFunctionPriv;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getCardResourcePriv() {
		return cardResourcePriv;
	}
	public void setCardResourcePriv(String cardResourcePriv) {
		this.cardResourcePriv = cardResourcePriv;
	}
	public String getHighMusterResourcePriv() {
		return highMusterResourcePriv;
	}
	public void setHighMusterResourcePriv(String highMusterResourcePriv) {
		this.highMusterResourcePriv = highMusterResourcePriv;
	}
	public String getLexprResourcePriv() {
		return lexprResourcePriv;
	}
	public void setLexprResourcePriv(String lexprResourcePriv) {
		this.lexprResourcePriv = lexprResourcePriv;
	}
	public String getMusterResourcePriv() {
		return musterResourcePriv;
	}
	public void setMusterResourcePriv(String musterResourcePriv) {
		this.musterResourcePriv = musterResourcePriv;
	}
	public String getReportResourcePriv() {
		return reportResourcePriv;
	}
	public void setReportResourcePriv(String reportResourcePriv) {
		this.reportResourcePriv = reportResourcePriv;
	}
	public String getStaticsResourcePriv() {
		return staticsResourcePriv;
	}
	public void setStaticsResourcePriv(String staticsResourcePriv) {
		this.staticsResourcePriv = staticsResourcePriv;
	}
	public String getSetOrItemReadPriv() {
		return setOrItemReadPriv;
	}
	public void setSetOrItemReadPriv(String setOrItemReadPriv) {
		this.setOrItemReadPriv = setOrItemReadPriv;
	}
	public String getSetOrItemWritePriv() {
		return setOrItemWritePriv;
	}
	public void setSetOrItemWritePriv(String setOrItemWritePriv) {
		this.setOrItemWritePriv = setOrItemWritePriv;
	}
	public String getDisplayMessage() {
		return displayMessage;
	}
	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}
	public String getLawruleResourcePriv() {
		return lawruleResourcePriv;
	}
	public void setLawruleResourcePriv(String lawruleResourcePriv) {
		this.lawruleResourcePriv = lawruleResourcePriv;
	}
	public String getRsbdResourcePriv() {
		return rsbdResourcePriv;
	}
	public void setRsbdResourcePriv(String rsbdResourcePriv) {
		this.rsbdResourcePriv = rsbdResourcePriv;
	}
	public String getXzbdResourcePriv() {
		return xzbdResourcePriv;
	}
	public void setXzbdResourcePriv(String xzbdResourcePriv) {
		this.xzbdResourcePriv = xzbdResourcePriv;
	}
	public String getWjdcResourcePriv() {
		return wjdcResourcePriv;
	}
	public void setWjdcResourcePriv(String wjdcResourcePriv) {
		this.wjdcResourcePriv = wjdcResourcePriv;
	}
	public String getPxbResourcePriv() {
		return pxbResourcePriv;
	}
	public void setPxbResourcePriv(String pxbResourcePriv) {
		this.pxbResourcePriv = pxbResourcePriv;
	}
	public String getGglResourcePriv() {
		return gglResourcePriv;
	}
	public void setGglResourcePriv(String gglResourcePriv) {
		this.gglResourcePriv = gglResourcePriv;
	}
	public String getXzlbResourcePriv() {
		return xzlbResourcePriv;
	}
	public void setXzlbResourcePriv(String xzlbResourcePriv) {
		this.xzlbResourcePriv = xzlbResourcePriv;
	}
	public String getGzfxtResourcePriv() {
		return gzfxtResourcePriv;
	}
	public void setGzfxtResourcePriv(String gzfxtResourcePriv) {
		this.gzfxtResourcePriv = gzfxtResourcePriv;
	}
	public String getDaflResourcePriv() {
		return daflResourcePriv;
	}
	public void setDaflResourcePriv(String daflResourcePriv) {
		this.daflResourcePriv = daflResourcePriv;
	}
	public String getKqjResourcePriv() {
		return kqjResourcePriv;
	}
	public void setKqjResourcePriv(String kqjResourcePriv) {
		this.kqjResourcePriv = kqjResourcePriv;
	}
	public String getBxbdResourcePriv() {
		return bxbdResourcePriv;
	}
	public void setBxbdResourcePriv(String bxbdResourcePriv) {
		this.bxbdResourcePriv = bxbdResourcePriv;
	}
	public String getBxlbResourcePriv() {
		return bxlbResourcePriv;
	}
	public void setBxlbResourcePriv(String bxlbResourcePriv) {
		this.bxlbResourcePriv = bxlbResourcePriv;
	}
	public String getWdflResourcePriv() {
		return wdflResourcePriv;
	}
	public void setWdflResourcePriv(String wdflResourcePriv) {
		this.wdflResourcePriv = wdflResourcePriv;
	}
	public String getZsflResourcePriv() {
		return zsflResourcePriv;
	}
	public void setZsflResourcePriv(String zsflResourcePriv) {
		this.zsflResourcePriv = zsflResourcePriv;
	}
	public String getKhzbResourcePriv() {
		return khzbResourcePriv;
	}
	public void setKhzbResourcePriv(String khzbResourcePriv) {
		this.khzbResourcePriv = khzbResourcePriv;
	}
	public String getKhmbResourcePriv() {
		return khmbResourcePriv;
	}
	public void setKhmbResourcePriv(String khmbResourcePriv) {
		this.khmbResourcePriv = khmbResourcePriv;
	}
	public String getOrgbdResourcePriv() {
		return orgbdResourcePriv;
	}
	public void setOrgbdResourcePriv(String orgbdResourcePriv) {
		this.orgbdResourcePriv = orgbdResourcePriv;
	}
	public String getPosbdResourcePriv() {
		return posbdResourcePriv;
	}
	public void setPosbdResourcePriv(String posbdResourcePriv) {
		this.posbdResourcePriv = posbdResourcePriv;
	}
	public String getPartymanager() {
		return partymanager;
	}
	public void setPartymanager(String partymanager) {
		this.partymanager = partymanager;
	}
	public String getMenbermanager() {
		return menbermanager;
	}
	public void setMenbermanager(String menbermanager) {
		this.menbermanager = menbermanager;
	}
	public TreeMap getFunctionPriv() {
		return functionPriv;
	}
	public void setFunctionPriv(TreeMap functionPriv) {
		this.functionPriv = functionPriv;
	}
	
	
	
}
