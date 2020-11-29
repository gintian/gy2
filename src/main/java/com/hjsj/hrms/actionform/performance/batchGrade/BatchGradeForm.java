package com.hjsj.hrms.actionform.performance.batchGrade;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:BatchGradeForm.java</p>
 * <p>Description:多人考评</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BatchGradeForm extends FrameForm 
{
	//	考核计划集合
    private ArrayList dblist=new ArrayList();
    private HashMap   planScoreflagMap=new HashMap();
    private String    model="1";                       // 0：绩效考核  1：民主评测
	private String    dbpre="0";
	private String    tableHtml="";
	private ArrayList objectList = new ArrayList();
	private String    objectSelfScoreHtml="";          //考核对象自我评价html	
	private String    objectLowerScoreHtml="";         // 下属对考核对象评价html	
	private String    searchname="";           
	private String    userIDs="";            //考核对象id串
	private String    fillCtrs="";           //打分控制串 0/1/0/1   0：考核主体可打分也可不打分 1：必填
	private String    userNames="";          //用户名串
	private String    template_id="";        //模版id
	private String    isKnowWhole="0";       // 1;有了结程度 2:总评选项  3：两者都有
	private String    gradeStatus="0";       //评分状态   1:正在编辑  2:已提交
	private String    scoreflag="2";         //=2混合，=1标度
	private String    wholeEval="";          //是否有总体评价
	private String    titleName="";			 //标题
	private String    pointDeformity="0";    //是否没有填指标上下限   1：没填
	private String    noGradeItem="";        //没有上下限的指标 名称
	private String    targetDeclare="";      //指标说明功能入口地址
	private String    individualPerformance="";  //个人绩效功能入口地址
	private String    span_ids="";
	private String    performanceType="0";            //考核形式   0：绩效考评  1：干部任免
	private String    plan_descript="";               //计划说明
	private String    plan_descript_content="";       //计划说明内容
	private String    pointContrl="";                 //指标打分控制  当取消不打分选项时，为1的指标恢复打分权限 为0的仍然置灰
	private String    degreeShowType="1";			  // //1-标准标度 2-指标标度
	
	private String    isEntiretySub="false";         //提交是否需要必填
	private String    isAutoCountTotalOrder="false";
	private String	  isShowTotalScore="false";		 //是否显示总分
	private String    isShowOrder="false";			 //是否显示排名
	private String    isPage="0";                    //是否分页显示  0：不显示  1：显示
	private String    current="1";			         //当前页数
	private String    showSumRow="False";            // 显示合计行
	
	private String    togetherCommit="False";        //多人打分统一提交, Ture, False, 默认为False
	private String    linkType="";                   //进入路径  liantong 表示从连通单独路径进入
	private String    evalOutLimitStdScore="False"; //评分时得分不受标准分限制True, False, 默认为 False;都加
	
	ArrayList pageList=new ArrayList();		
	private String    script_code="";               
	private String    modelEmail="false";      // 发送邮件标志参数  true:发邮件 false：不发邮件
	private ArrayList object_idList = new ArrayList(); // 需一键评分的考核对象
	private ArrayList gradeList = new ArrayList(); // 标准标度
	private String    grade_id = "";    
	private String    onlyFild = "";	// 考核对象唯一性指标
	private Hashtable paramTable=new Hashtable();
	private String    object_id="";
	
	private String dayWeekMonthFlag = "False";
	private String showDay = "";
	private String showWeek = "";
	private String showMonth = "";
	private String showHistoryScore="False";//显示历次得分
	private String timeInterval = "";
	private String plan_id="";
	ArrayList planNames = new ArrayList();
	ArrayList objectWholeScores = new ArrayList();
	public ArrayList getObjectWholeScores() {
		return objectWholeScores;
	}

	public void setObjectWholeScores(ArrayList objectWholeScores) {
		this.objectWholeScores = objectWholeScores;
	}

	public ArrayList getPlanNames() {
		return planNames;
	}

	public void setPlanNames(ArrayList planNames) {
		this.planNames = planNames;
	}

	public ArrayList getObjectNames() {
		return objectNames;
	}

	public void setObjectNames(ArrayList objectNames) {
		this.objectNames = objectNames;
	}

	public ArrayList getObjectAvg() {
		return objectAvg;
	}

	public void setObjectAvg(ArrayList objectAvg) {
		this.objectAvg = objectAvg;
	}

	public HashMap getHistoryMap() {
		return historyMap;
	}

	public void setHistoryMap(HashMap historyMap) {
		this.historyMap = historyMap;
	}

	ArrayList objectNames = new ArrayList();
	ArrayList objectAvg = new ArrayList();
	HashMap historyMap = new HashMap();
	ArrayList objectTotal = new ArrayList();
	
	
	
	private String mustFillWholeEval="";   //总体评价必填      2014.01.07   pjf
	
	private float topscore=0;
	public float getTopscore() {
		return topscore;
	}

	public void setTopscore(float topscore) {
		this.topscore = topscore;
	}

	public String getWholeEvalMode() {
		return wholeEvalMode;
	}

	public void setWholeEvalMode(String wholeEvalMode) {
		this.wholeEvalMode = wholeEvalMode;
	}

	private String wholeEvalMode = "0";
	 public ArrayList getObjectTotal() {
		return objectTotal;
	}

	public void setObjectTotal(ArrayList objectTotal) {
		this.objectTotal = objectTotal;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	// 开始时间
    private String startDate;
    public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	// 结束时间
    private String endDate; 
	public String getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getShowHistoryScore() {
		return showHistoryScore;
	}

	public void setShowHistoryScore(String showHistoryScore) {
		this.showHistoryScore = showHistoryScore;
	}

	private HashMap  dataMap=new HashMap();
	
	public HashMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	public String getShowDay() {
		return showDay;
	}

	public void setShowDay(String showDay) {
		this.showDay = showDay;
	}

	public String getShowWeek() {
		return showWeek;
	}

	public void setShowWeek(String showWeek) {
		this.showWeek = showWeek;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getDayWeekMonthFlag() {
		return dayWeekMonthFlag;
	}

	public void setDayWeekMonthFlag(String dayWeekMonthFlag) {
		this.dayWeekMonthFlag = dayWeekMonthFlag;
	}

	public BatchGradeForm() 
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
    public void outPutFormHM()
	{
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setOnlyFild((String)this.getFormHM().get("onlyFild"));
		this.setGrade_id((String)this.getFormHM().get("grade_id"));
		this.setGradeList((ArrayList)this.getFormHM().get("gradeList"));
		this.setObject_idList((ArrayList)this.getFormHM().get("object_idList"));
		this.setParamTable((Hashtable)this.getFormHM().get("paramTable"));
		this.setModelEmail((String)this.getFormHM().get("modelEmail"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setEvalOutLimitStdScore((String)this.getFormHM().get("evalOutLimitStdScore"));
		this.setPlan_descript_content((String)this.getFormHM().get("plan_descript_content"));
		this.setLinkType((String)this.getFormHM().get("linkType"));
		this.setTogetherCommit((String)this.getFormHM().get("togetherCommit"));
		this.setObjectSelfScoreHtml((String)this.getFormHM().get("objectSelfScoreHtml"));
		this.setShowSumRow((String)this.getFormHM().get("showSumRow"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setObjectList((ArrayList)this.getFormHM().get("objectList"));
		if(this.getFormHM().get("clear")!=null&& "1".equals((String)this.getFormHM().get("clear")))
		{
			
			this.setDbpre("0");
			this.setGradeStatus("0");
			this.getFormHM().remove("clear");
			
		}
		this.setScript_code((String)this.getFormHM().get("script_code"));
		
		this.setPerformanceType((String)this.getFormHM().get("performanceType"));
		this.setPlan_descript((String)this.getFormHM().get("plan_descript"));
		
		//得到考核计划的集合类
		this.setPointContrl((String)this.getFormHM().get("pointContrl"));
		this.setWholeEval((String)this.getFormHM().get("wholeEval"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setPlanScoreflagMap((HashMap)this.getFormHM().get("planScoreflagMap"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setUserIDs((String)this.getFormHM().get("userIDs"));
		this.setTemplate_id((String)this.getFormHM().get("template_id"));
		if(this.getFormHM().get("isKnowWhole")!=null)
			this.setIsKnowWhole((String)this.getFormHM().get("isKnowWhole"));
		
		this.setGradeStatus((String)this.getFormHM().get("gradeStatus"));
		this.setScoreflag((String)this.getFormHM().get("scoreflag"));
		this.setTitleName((String)this.getFormHM().get("titleName"));
		this.setPointDeformity((String)this.getFormHM().get("pointDeformity"));
		this.setNoGradeItem((String)this.getFormHM().get("noGradeItem"));
		this.setTargetDeclare((String)this.getFormHM().get("targetDeclare"));
		this.setIndividualPerformance((String)this.getFormHM().get("individualPerformance"));
		this.setSpan_ids((String)this.getFormHM().get("span_ids"));
		this.setFillCtrs((String)this.getFormHM().get("fillCtrs"));
		this.setUserNames((String)this.getFormHM().get("userNames"));
		this.setDegreeShowType((String)this.getFormHM().get("degreeShowType"));
		this.setIsPage((String)this.getFormHM().get("isPage"));
		this.setCurrent((String)this.getFormHM().get("current"));
		this.setPageList((ArrayList)this.getFormHM().get("pageList"));
		
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setIsAutoCountTotalOrder((String)this.getFormHM().get("isAutoCountTotalOrder"));
		this.setIsShowOrder((String)this.getFormHM().get("isShowOrder"));
		this.setIsShowTotalScore((String)this.getFormHM().get("isShowTotalScore"));
		this.setIsEntiretySub((String)this.getFormHM().get("isEntiretySub"));
		this.setObjectLowerScoreHtml((String)this.getFormHM().get("objectLowerScoreHtml"));
		this.setDayWeekMonthFlag((String)this.getFormHM().get("dayWeekMonthFlag"));
		this.setShowDay((String)this.getFormHM().get("showDay"));
		this.setShowWeek((String)this.getFormHM().get("showWeek"));
		this.setShowMonth((String)this.getFormHM().get("showMonth"));
		this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		this.setShowHistoryScore((String)this.getFormHM().get("showHistoryScore"));
		this.setTimeInterval((String)this.getFormHM().get("timeInterval"));
		this.setStartDate((String) this.getFormHM().get("startDate"));
		this.setEndDate((String) this.getFormHM().get("endDate"));
		this.setPlan_id((String) this.getFormHM().get("plan_id"));
		this.setPlanNames((ArrayList) this.getFormHM().get("planNames"));
		this.setObjectNames((ArrayList) this.getFormHM().get("objectNames"));
		this.setObjectAvg((ArrayList) this.getFormHM().get("objectAvg"));
		this.setHistoryMap((HashMap)this.getFormHM().get("historyMap"));
		this.setObjectTotal((ArrayList) this.getFormHM().get("objectTotal"));
		this.setWholeEvalMode((String)this.getFormHM().get("wholeEvalMode"));
		this.setTopscore(Float.parseFloat((String)this.getFormHM().get("topscore")));
		this.setObjectWholeScores((ArrayList) this.getFormHM().get("objectWholeScores"));
		this.setMustFillWholeEval((String)this.getFormHM().get("mustFillWholeEval"));
	}

	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("grade_id",this.getGrade_id());
		this.getFormHM().put("gradeList",this.getGradeList());
		this.getFormHM().put("object_idList",this.getObject_idList());
		this.getFormHM().put("modelEmail",this.getModelEmail());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("titleName",this.getTitleName());
		this.getFormHM().put("current",this.getCurrent());
		this.getFormHM().put("onlyFild",this.getOnlyFild());
		this.getFormHM().put("searchname",this.getSearchname());
		this.getFormHM().put("dayWeekMonthFlag",this.getDayWeekMonthFlag());
		this.getFormHM().put("showDay",this.getShowDay());
		this.getFormHM().put("showWeek",this.getShowWeek());
		this.getFormHM().put("showMonth",this.getShowMonth());
		this.getFormHM().put("showHistoryScore",this.getShowHistoryScore());
		this.getFormHM().put("timeInterval",this.getTimeInterval());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("wholeEvalMode", this.getWholeEvalMode());
		this.getFormHM().put("topscore", String.valueOf(this.getTopscore()));
		this.getFormHM().put("mustFillWholeEval", this.getMustFillWholeEval());
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getUserIDs() {
		return userIDs;
	}

	public void setUserIDs(String userIDs) {
		this.userIDs = userIDs;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getIsKnowWhole() {
		return isKnowWhole;
	}

	public void setIsKnowWhole(String isKnowWhole) {
		this.isKnowWhole = isKnowWhole;
	}	

	public String getGradeStatus() {
		return gradeStatus;
	}

	public void setGradeStatus(String gradeStatus) {
		this.gradeStatus = gradeStatus;
	}

	public String getScoreflag() {
		return scoreflag;
	}

	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}	

	public String getWholeEval() {
		return wholeEval;
	}

	public void setWholeEval(String wholeEval) {
		this.wholeEval = wholeEval;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getPointDeformity() {
		return pointDeformity;
	}

	public void setPointDeformity(String pointDeformity) {
		this.pointDeformity = pointDeformity;
	}

	public String getTargetDeclare() {
		return targetDeclare;
	}

	public void setTargetDeclare(String targetDeclare) {
		this.targetDeclare = targetDeclare;
	}

	public String getIndividualPerformance() {
		return individualPerformance;
	}

	public void setIndividualPerformance(String individualPerformance) {
		this.individualPerformance = individualPerformance;
	}

	public String getSpan_ids() {
		return span_ids;
	}

	public void setSpan_ids(String span_ids) {
		this.span_ids = span_ids;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public String getIsPage() {
		return isPage;
	}

	public void setIsPage(String isPage) {
		this.isPage = isPage;
	}

	public ArrayList getPageList() {
		return pageList;
	}

	public void setPageList(ArrayList pageList) {
		this.pageList = pageList;
	}

	public String getFillCtrs() {
		return fillCtrs;
	}

	public void setFillCtrs(String fillCtrs) {
		this.fillCtrs = fillCtrs;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public String getPointContrl() {
		return pointContrl;
	}

	public void setPointContrl(String pointContrl) {
		this.pointContrl = pointContrl;
	}

	public String getPlan_descript() {
		return plan_descript;
	}

	public void setPlan_descript(String plan_descript) {
		this.plan_descript = plan_descript;
	}

	public String getIsShowOrder() {
		return isShowOrder;
	}

	public void setIsShowOrder(String isShowOrder) {
		this.isShowOrder = isShowOrder;
	}

	public String getIsShowTotalScore() {
		return isShowTotalScore;
	}

	public void setIsShowTotalScore(String isShowTotalScore) {
		this.isShowTotalScore = isShowTotalScore;
	}

	public String getIsEntiretySub() {
		return isEntiretySub;
	}

	public void setIsEntiretySub(String isEntiretySub) {
		this.isEntiretySub = isEntiretySub;
	}

	public String getPerformanceType() {
		return performanceType;
	}

	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getIsAutoCountTotalOrder() {
		return isAutoCountTotalOrder;
	}

	public void setIsAutoCountTotalOrder(String isAutoCountTotalOrder) {
		this.isAutoCountTotalOrder = isAutoCountTotalOrder;
	}

	public String getScript_code() {
		return script_code;
	}

	public void setScript_code(String script_code) {
		this.script_code = script_code;
	}

	public String getNoGradeItem() {
		return noGradeItem;
	}

	public void setNoGradeItem(String noGradeItem) {
		this.noGradeItem = noGradeItem;
	}

	public String getDegreeShowType() {
		return degreeShowType;
	}

	public void setDegreeShowType(String degreeShowType) {
		this.degreeShowType = degreeShowType;
	}

	public String getObjectSelfScoreHtml() {
		return objectSelfScoreHtml;
	}

	public void setObjectSelfScoreHtml(String objectSelfScoreHtml) {
		this.objectSelfScoreHtml = objectSelfScoreHtml;
	}

	public String getShowSumRow() {
		return showSumRow;
	}

	public void setShowSumRow(String showSumRow) {
		this.showSumRow = showSumRow;
	}

	public String getTogetherCommit() {
		return togetherCommit;
	}

	public void setTogetherCommit(String togetherCommit) {
		this.togetherCommit = togetherCommit;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getPlan_descript_content() {
		return plan_descript_content;
	}

	public void setPlan_descript_content(String plan_descript_content) {
		this.plan_descript_content = plan_descript_content;
	}

	public String getEvalOutLimitStdScore() {
		return evalOutLimitStdScore;
	}

	public void setEvalOutLimitStdScore(String evalOutLimitStdScore) {
		this.evalOutLimitStdScore = evalOutLimitStdScore;
	}

	public HashMap getPlanScoreflagMap() {
		return planScoreflagMap;
	}

	public void setPlanScoreflagMap(HashMap planScoreflagMap) {
		this.planScoreflagMap = planScoreflagMap;
	}

	public String getModelEmail() {
		return modelEmail;
	}

	public void setModelEmail(String modelEmail) {
		this.modelEmail = modelEmail;
	}

	public Hashtable getParamTable() {
		return paramTable;
	}

	public void setParamTable(Hashtable paramTable) {
		this.paramTable = paramTable;
	}

	public ArrayList getObject_idList() {
		return object_idList;
	}

	public void setObject_idList(ArrayList object_idList) {
		this.object_idList = object_idList;
	}

	public ArrayList getGradeList() {
		return gradeList;
	}

	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}

	public String getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(String grade_id) {
		this.grade_id = grade_id;
	}

	public String getOnlyFild() {
		return onlyFild;
	}

	public void setOnlyFild(String onlyFild) {
		this.onlyFild = onlyFild;
	}

	public String getSearchname() {
		return searchname;
	}

	public void setSearchname(String searchname) {
		this.searchname = searchname;
	}

	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getObjectLowerScoreHtml() {
		return objectLowerScoreHtml;
	}

	public void setObjectLowerScoreHtml(String objectLowerScoreHtml) {
		this.objectLowerScoreHtml = objectLowerScoreHtml;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/selfservice/performance/batchGrade".equals(arg0.getPath()) && arg1.getParameter("b_historyScore") != null && "link".equals(arg1.getParameter("b_historyScore")))
		    {	
			    this.setTimeInterval("");
			    this.setStartDate("");
			    this.setEndDate("");
		    }
		    //34095 去掉报错的返回按钮 zhanghua
			if ("/selfservice/performance/batchGrade".equals(arg0.getPath()) && arg1.getParameter("b_Desc") != null){
				arg1.setAttribute("targetWindow", "0");
			}
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }

	/**
	 * @return the mustFillWholeEval
	 */
	public String getMustFillWholeEval() {
		return mustFillWholeEval;
	}

	/**
	 * @param mustFillWholeEval the mustFillWholeEval to set
	 */
	public void setMustFillWholeEval(String mustFillWholeEval) {
		this.mustFillWholeEval = mustFillWholeEval;
	}
}
