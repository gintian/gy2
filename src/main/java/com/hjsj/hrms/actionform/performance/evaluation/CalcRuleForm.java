package com.hjsj.hrms.actionform.performance.evaluation;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:CalcRuleForm.java</p>
 * <p>Description:评估计算规则</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-18 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class CalcRuleForm extends FrameForm 
{
	
	private String code = ""; // 管理范围
	private String throwHighCount="0";   //去掉最大值数
	private String throwLowCount="0";    //去掉最小值数
	private String throwBaseNum="0";    //主体类别人数大于
	private String keepDecimal="0";     //计算结果保留小数位
	private String useWeight="";        //是否使用权重
	private String useKnow="";          //是否过滤了解程度
	private String[] knowText=null;         //保留的了解程度ID
	private String knowText_value="";
	private String appUseWeight="";     //评估中总体评价票数有权重
	private String unLeadSingleAvg="";  //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
	private String[] estBodyText=null;      //去最值的ID
	private String  estBodyText_value="";
	private ArrayList weightList=new ArrayList();  
	private ArrayList knowList=new ArrayList();  //了解程度列表
	private ArrayList bodySetList=new ArrayList();
	private String planid="";
	private String planStatus="";
	private String method="";                //计划类型：  360、目标
	private ArrayList gradeList=new ArrayList();
	private String checkInvalidGrade="";
	private String invalidGrade="";
	 
	private String nodeKnowDegree="false";   //是否有了解程度
	private String isScore="";
	private String isGrpAvg="";
	private String isorder="";
	private String isGrade="";	
	private String isXiShu="";
	private ArrayList bodyTypeList=new ArrayList();
	private String wholeEval = "";
	private String isShowValPrecision = "";//是否显示分值精度
	private String validateInfo="";//计算前的校验信息
	private String validateOper="";//计算前的校验操作 分为考核主体对考核对象的考评的校验和等级结果的检查校验
	private ArrayList rangelist=new ArrayList();// 可以定义分值范围的指标
	private String isvalidate="";				// 已定义分值指标 是否有效 false无效 true有效
	private ArrayList hasrangelist=new ArrayList();// 已定义分值范围的指标
	private String showRange="";                  //是否显示 定义分值指标范围 true/false 显示/不显示
	private String pointScoreFromKeyEvent="";    //指标评分优先取自关键事件
	private String isShowScoreFromKey="0";       //是否出现指标评分优先取自关键事件选项	
	private String showRule="cal";  // cal:计算  show:定义规则
	private String isShowHjsoft = "false"; // 打分按加扣分处理  且 权重模板 且 数据采集方式
	private String zeroByNull = "false";
	private String byModel = "";//绩效还是能力素质  郭峰
	private String zeroflag="1";
	
	public String getZeroflag() {
		return zeroflag;
	}

	public void setZeroflag(String zeroflag) {
		this.zeroflag = zeroflag;
	}

	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("isShowHjsoft", this.getIsShowHjsoft());
		this.getFormHM().put("hasrangelist", this.getHasrangelist());
		this.getFormHM().put("isvalidate", this.getIsvalidate());
		this.getFormHM().put("rangelist", this.getRangelist());
		this.getFormHM().put("throwBaseNum",this.getThrowBaseNum());
		this.getFormHM().put("throwHighCount",this.getThrowHighCount());
		this.getFormHM().put("throwLowCount",this.getThrowLowCount());
		this.getFormHM().put("keepDecimal",this.getKeepDecimal());
		this.getFormHM().put("useWeight",this.getUseWeight());
		this.getFormHM().put("useKnow",this.getUseKnow());
		this.getFormHM().put("knowText",this.getKnowText());
		this.getFormHM().put("appUseWeight",this.getAppUseWeight());
		this.getFormHM().put("unLeadSingleAvg",this.getUnLeadSingleAvg());
		this.getFormHM().put("estBodyText",this.getEstBodyText());
		this.getFormHM().put("weightList",this.getWeightList());
		this.getFormHM().put("planStatus", this.getPlanStatus());
		
		this.getFormHM().put("isScore", this.getIsScore());
		this.getFormHM().put("isGrpAvg", this.getIsGrpAvg());
		this.getFormHM().put("isorder", this.getIsorder());
		this.getFormHM().put("isGrade", this.getIsGrade());
		this.getFormHM().put("isXiShu", this.getIsXiShu());
		this.getFormHM().put("bodyTypeList", this.getBodyTypeList());
		this.getFormHM().put("wholeEval", this.getWholeEval());
		this.getFormHM().put("isShowValPrecision", this.getIsShowValPrecision());
		this.getFormHM().put("planid",this.getPlanid());
		this.getFormHM().put("validateInfo",this.getValidateInfo());
		this.getFormHM().put("validateOper", this.getValidateOper());
		this.getFormHM().put("showRange",this.getShowRange());
		this.getFormHM().put("zeroByNull", this.getZeroByNull());
		this.getFormHM().put("byModel", this.getByModel());
		this.getFormHM().put("zeroflag", this.getZeroflag());
	}

	@Override
    public void outPutFormHM()
	{
		
		this.setCode((String)this.getFormHM().get("code"));
		this.setIsShowHjsoft((String)this.getFormHM().get("isShowHjsoft"));
		this.setShowRule((String)this.getFormHM().get("showRule"));		
		this.setPointScoreFromKeyEvent((String)this.getFormHM().get("pointScoreFromKeyEvent"));
		this.setIsShowScoreFromKey((String)this.getFormHM().get("isShowScoreFromKey")); 
		this.setShowRange((String)this.getFormHM().get("showRange"));
		this.setHasrangelist((ArrayList)this.getFormHM().get("hasrangelist"));
		this.setIsvalidate((String)this.getFormHM().get("isvalidate"));
		this.setRangelist((ArrayList)this.getFormHM().get("rangelist"));
		this.setCheckInvalidGrade((String)this.getFormHM().get("checkInvalidGrade"));
		this.setInvalidGrade((String)this.getFormHM().get("invalidGrade"));
		this.setGradeList((ArrayList)this.getFormHM().get("gradeList"));
		this.setMethod((String)this.getFormHM().get("method"));
		
		this.setThrowBaseNum((String)this.getFormHM().get("throwBaseNum"));
		this.setValidateInfo((String)this.getFormHM().get("validateInfo"));
		this.setValidateOper((String)this.getFormHM().get("validateOper"));
		
		this.setIsShowValPrecision((String)this.getFormHM().get("isShowValPrecision"));
		this.setNodeKnowDegree((String)this.getFormHM().get("nodeKnowDegree"));
		this.setPlanid((String)this.getFormHM().get("planid"));
		this.setThrowHighCount((String)this.getFormHM().get("throwHighCount"));
		this.setThrowLowCount((String)this.getFormHM().get("throwLowCount"));
		this.setKeepDecimal((String)this.getFormHM().get("keepDecimal"));
		this.setUseWeight((String)this.getFormHM().get("useWeight"));
		this.setUseKnow((String)this.getFormHM().get("useKnow"));
		this.setKnowText_value((String)this.getFormHM().get("knowText_value"));
		this.setKnowText((String[])this.getFormHM().get("knowText"));
		this.setAppUseWeight((String)this.getFormHM().get("appUseWeight"));
		this.setUnLeadSingleAvg((String)this.getFormHM().get("unLeadSingleAvg"));
		this.setEstBodyText((String[])this.getFormHM().get("estBodyText"));
		this.setEstBodyText_value((String)this.getFormHM().get("estBodyText_value"));
		this.setWeightList((ArrayList)this.getFormHM().get("weightList"));
		this.setKnowList((ArrayList)this.getFormHM().get("knowList"));
		this.setBodySetList((ArrayList)this.getFormHM().get("bodySetList"));
		this.setPlanStatus((String)this.getFormHM().get("planStatus"));
		
		this.setIsScore((String)this.getFormHM().get("isScore"));
		this.setIsGrpAvg((String)this.getFormHM().get("isGrpAvg"));
		this.setIsorder((String)this.getFormHM().get("isorder"));
		this.setIsGrade((String)this.getFormHM().get("isGrade"));
		this.setIsXiShu((String)this.getFormHM().get("isXiShu"));
		this.setBodyTypeList((ArrayList)this.getFormHM().get("bodyTypeList"));
		this.setWholeEval((String)this.getFormHM().get("wholeEval"));
		this.setZeroByNull((String)this.getFormHM().get("zeroByNull"));
		this.setByModel((String)this.getFormHM().get("byModel"));
		this.setZeroflag((String)this.getFormHM().get("zeroflag"));
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
	{
		super.reset(arg0, arg1);
		this.setIsScore("0");
		this.setIsGrpAvg("0");
		this.setIsorder("0");
		this.setIsGrade("0");
		this.setIsXiShu("0");
		this.setZeroflag("0");
	}			 
	 
	public String getAppUseWeight() {
		return appUseWeight;
	}

	public void setAppUseWeight(String appUseWeight) {
		this.appUseWeight = appUseWeight;
	}

	public ArrayList getBodySetList() {
		return bodySetList;
	}

	public void setBodySetList(ArrayList bodySetList) {
		this.bodySetList = bodySetList;
	}

	public String[] getEstBodyText() {
		return estBodyText;
	}

	public void setEstBodyText(String[] estBodyText) {
		this.estBodyText = estBodyText;
	}

	public String getEstBodyText_value() {
		return estBodyText_value;
	}

	public void setEstBodyText_value(String estBodyText_value) {
		this.estBodyText_value = estBodyText_value;
	}

	public String getKeepDecimal() {
		return keepDecimal;
	}

	public void setKeepDecimal(String keepDecimal) {
		this.keepDecimal = keepDecimal;
	}

	public ArrayList getKnowList() {
		return knowList;
	}

	public void setKnowList(ArrayList knowList) {
		this.knowList = knowList;
	}

	public String[] getKnowText() {
		return knowText;
	}

	public void setKnowText(String[] knowText) {
		this.knowText = knowText;
	}

	public String getKnowText_value() {
		return knowText_value;
	}

	public void setKnowText_value(String knowText_value) {
		this.knowText_value = knowText_value;
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getThrowHighCount() {
		return throwHighCount;
	}

	public void setThrowHighCount(String throwHighCount) {
		this.throwHighCount = throwHighCount;
	}

	public String getThrowLowCount() {
		return throwLowCount;
	}

	public void setThrowLowCount(String throwLowCount) {
		this.throwLowCount = throwLowCount;
	}

	public String getUnLeadSingleAvg() {
		return unLeadSingleAvg;
	}

	public void setUnLeadSingleAvg(String unLeadSingleAvg) {
		this.unLeadSingleAvg = unLeadSingleAvg;
	}

	public String getUseKnow() {
		return useKnow;
	}

	public void setUseKnow(String useKnow) {
		this.useKnow = useKnow;
	}

	public String getUseWeight() {
		return useWeight;
	}

	public void setUseWeight(String useWeight) {
		this.useWeight = useWeight;
	}

	public ArrayList getWeightList() {
		return weightList;
	}

	public void setWeightList(ArrayList weightList) {
		this.weightList = weightList;
	}

	public String getNodeKnowDegree() {
		return nodeKnowDegree;
	}

	public void setNodeKnowDegree(String nodeKnowDegree) {
		this.nodeKnowDegree = nodeKnowDegree;
	}

	public String getPlanStatus()
	{
	
	    return planStatus;
	}

	public void setPlanStatus(String planStatus)
	{
	
	    this.planStatus = planStatus;
	}

	public ArrayList getBodyTypeList()
	{
		return bodyTypeList;
	}

	public void setBodyTypeList(ArrayList bodyTypeList)
	{
		this.bodyTypeList = bodyTypeList;
	}

	public String getIsGrade()
	{
		return isGrade;
	}

	public void setIsGrade(String isGrade)
	{
		this.isGrade = isGrade;
	}

	public String getIsGrpAvg()
	{
		return isGrpAvg;
	}

	public void setIsGrpAvg(String isGrpAvg)
	{
		this.isGrpAvg = isGrpAvg;
	}

	public String getIsorder()
	{
		return isorder;
	}

	public void setIsorder(String isorder)
	{
		this.isorder = isorder;
	}

	public String getIsScore()
	{
		return isScore;
	}

	public void setIsScore(String isScore)
	{
		this.isScore = isScore;
	}

	public String getIsXiShu()
	{
		return isXiShu;
	}

	public void setIsXiShu(String isXiShu)
	{
		this.isXiShu = isXiShu;
	}

	public String getWholeEval()
	{
		return wholeEval;
	}

	public void setWholeEval(String wholeEval)
	{
		this.wholeEval = wholeEval;
	}

	public String getIsShowValPrecision()
	{
		return isShowValPrecision;
	}

	public void setIsShowValPrecision(String isShowValPrecision)
	{
		this.isShowValPrecision = isShowValPrecision;
	}

	public String getValidateInfo()
	{
		return validateInfo;
	}

	public void setValidateInfo(String validateInfo)
	{
		this.validateInfo = validateInfo;
	}

	public String getValidateOper()
	{
		return validateOper;
	}

	public void setValidateOper(String validateOper)
	{
		this.validateOper = validateOper;
	}

	public String getThrowBaseNum()
	{
		return throwBaseNum;
	}

	public void setThrowBaseNum(String throwBaseNum)
	{
		this.throwBaseNum = throwBaseNum;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ArrayList getGradeList() {
		return gradeList;
	}

	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}

	public String getCheckInvalidGrade() {
		return checkInvalidGrade;
	}

	public void setCheckInvalidGrade(String checkInvalidGrade) {
		this.checkInvalidGrade = checkInvalidGrade;
	}

	public String getInvalidGrade() {
		return invalidGrade;
	}

	public void setInvalidGrade(String invalidGrade) {
		this.invalidGrade = invalidGrade;
	}

	public ArrayList getRangelist() {
		return rangelist;
	}

	public void setRangelist(ArrayList rangelist) {
		this.rangelist = rangelist;
	}

	public String getIsvalidate() {
		return isvalidate;
	}

	public void setIsvalidate(String isvalidate) {
		this.isvalidate = isvalidate;
	}

	public ArrayList getHasrangelist() {
		return hasrangelist;
	}

	public void setHasrangelist(ArrayList hasrangelist) {
		this.hasrangelist = hasrangelist;
	}

	public String getShowRange() {
		return showRange;
	}

	public void setShowRange(String showRange) {
		this.showRange = showRange;
	}

	public String getIsShowScoreFromKey() {
		return isShowScoreFromKey;
	}

	public void setIsShowScoreFromKey(String isShowScoreFromKey) {
		this.isShowScoreFromKey = isShowScoreFromKey;
	}

	public String getPointScoreFromKeyEvent() {
		return pointScoreFromKeyEvent;
	}

	public void setPointScoreFromKeyEvent(String pointScoreFromKeyEvent) {
		this.pointScoreFromKeyEvent = pointScoreFromKeyEvent;
	}

	public String getShowRule() {
		return showRule;
	}

	public void setShowRule(String showRule) {
		this.showRule = showRule;
	}

	public String getIsShowHjsoft() {
		return isShowHjsoft;
	}

	public void setIsShowHjsoft(String isShowHjsoft) {
		this.isShowHjsoft = isShowHjsoft;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getZeroByNull() {
		return zeroByNull;
	}

	public void setZeroByNull(String zeroByNull) {
		this.zeroByNull = zeroByNull;
	}

	public String getByModel() {
		return byModel;
	}

	public void setByModel(String byModel) {
		this.byModel = byModel;
	}

	
 
	
}
