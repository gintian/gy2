package com.hjsj.hrms.actionform.performance.singleGrade;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Hashtable;

public class SingleGradeForm extends FrameForm {
//	考核计划集合
	private String     performanceType="0";			   //考核形式  0：绩效考核  1：民主评测	
	private String    model="1";                       // 0：绩效考核  1：民主评测
	private String 	  optObject="1";				   // 1：领导班子  2：班子成员
	private String    fromModel="menu";                // frontPanel 来自首页快捷评分面板进入  ,menu
	
	private String     degreeShowType="";             //1-标准标度 2-指标标度 3-采集标准标度，显示指标标度
	
    private ArrayList dblist=new ArrayList();
    private String    dbpre="0";
    private ArrayList objectList=new ArrayList();
    private String    appitem_id = "";
    private ArrayList appContantList = new ArrayList();
    private String    object_id="0";
    private String    gradeHtml=" ";
	private String    personalComment=" ";
	private String    goalComment=" ";
	private String    isNull="0";		
	private String    noGradeItem="";
	private String    scoreflag="2";     //=2混合，=1标度
	private String    dataArea="";       //各指标的数值范围
	private String    mainBodyId="0";    //考核主体id
	private String    templateId="0";    //考核模版id
	private String    nodeKnowDegree=""; //了解程度
	private String    wholeEval="";		 //总体评价
	private String    totalAppFormula="";		 // 总体评价的计算公式，默认为空
	private String    scoreBySumup="";   //BS个人总结没填写，主体为其打分时不能提交
	private String    limitation="-1";   //=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)
	private String    gradeClass="";  
	private String    status="0";        //权重分值表识 0：分值 1：权重
	private String    titleName="";
	private String    notMark="";		 //不打分功能入口地址	
	private String    targetDeclare="";  //指标说明功能入口地址
	private String    individualPerformance="";  //个人绩效功能入口地址
	private String    operate="";        //功能模块入口  1：自我评价 2：考评打分 3:多人考评
    private String    lay="0";           //表头层数
    private String    fillCtrl="0";      //打分控制  1：必打分  0：不一定
    private String    isShowTotalScore="";  //是否显示总分
    private FormFile file;				 //上传附件
    private FormFile goalfile;				 //上传目标附件
    private String   fileName="";            //报告附件名称
    private String   goalfileName="";        //目标附件名称
    private String   isFile="0";         // 是否有附件  0：没有   1：有
    private String    isEntireysub="false";
	/**个人总结*/
	private String summary="";
	private String isSummary="false";  //是否显示个人总结
	private ArrayList summaryFileIdsList=new ArrayList();   //个人总结附件id列表
	private String summaryState="0"; //绩效报告状态
	private String s_rejectCause="";
	/**个人目标*/
	private String goalContext="";   //目标内容
	private String isGoalFile="0";   //是否有上传的附件
	private String noteIdioGoal="false";  //是否显示个人目标
	private ArrayList goalFileIdsList=new ArrayList();
	private String goalState="0";    //目标状态 
	private String g_rejectCause="";
	
	private String employRecordUrl="";  //员工日志链接
	
	private String isSelfMark="1";       //是否可以自我打分  1：可以  0：不可以 （自我评价模块）
	
	private String   statCustomMode="True"; //显示绩效子集统计自定义 True False
	private String   statMethod="1";    //1:按年统计 2：按月统计 3：按季度统计  4：按半年统计 9:时间段
	private String   statStartDate="";  //起始时间
	private String   statEndDate="";    //终止时间
	private ArrayList years=new ArrayList();
	private ArrayList months=new ArrayList();
	private ArrayList counts=new ArrayList();
	private ArrayList quarters=new ArrayList();  //季度
	private ArrayList halfYears=new ArrayList(); //上半年-下半年
	
	private String   pointContrl="";
	
	private String   year="";
	private String   month="";
	private String   count="";
	private String   quarter="";
	private String   halfYear="";
	
	private String   perSetShowMode="";  //绩效子集显示方式  1-明细项，2-合计项 或 3-两者者显
	private String   perCompare="0";     //个人绩效比对 0：不比对  1：比对
	private String   changFlag="0";      //按月变化标志 0:不变化  1：按月变化 2：按年变化
	 /**当前页*/
    private int current=1;	
    private PaginationForm performanceListform=new PaginationForm();
    private String[] reportTitles=null;
    private ArrayList itemidList=new ArrayList();
    private String size;
    
    private String scoreCause="";  //赋分原因
    private String objectStatus=""; //考核对象状态
    
    private String relatingTargetCard="1"; //是否显示关联目标卡（针对自我打分模块）1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
    private String evalOutLimitStdScore="False"; //评分时得分不受标准分限制True, False, 默认为 False;都加
	
    private String pointIDs="";
    private String PointEvalType="0";  //360指标评分型式  1：下拉（默认）   2：单选
    
    private Hashtable paramTable=new Hashtable();
    private String searchname = "";
    private String allowUploadFile = "";//是否显示附件
    
    private String showHistoryScore="False";
    
    private String wholeEvalScore ="";
    private String wholeEvalMode = "0";
    
    private String plan_descript_content = ""; //2013.11.09 pjf
    
    
    private String mustFillWholeEval="";   //总体评价必填      2014.01.07   pjf
    
	private String isnullAffix="";		//判断模板文件是否为空
	
	private String filenametemplet="";	//模板文件名

	private String errorMsg = ""; // 出错的信息，防止抛出异常后"返回"按钮跳转至过期页面的问题 lium
	
	public String getErrorMsg() {
		return this.errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getFilenametemplet() {
		return filenametemplet;
	}

	public void setFilenametemplet(String filenametemplet) {
		this.filenametemplet = filenametemplet;
	}

	public String getIsnullAffix() {
		return isnullAffix;
	}

	public void setIsnullAffix(String isnullAffix) {
		this.isnullAffix = isnullAffix;
	}

	/**
	 * @return the plan_descript_content
	 */
	public String getPlan_descript_content() {
		return plan_descript_content;
	}

	/**
	 * @param plan_descript_content the plan_descript_content to set
	 */
	public void setPlan_descript_content(String plan_descript_content) {
		this.plan_descript_content = plan_descript_content;
	}

	public String getWholeEvalMode() {
		return wholeEvalMode;
	}

	public void setWholeEvalMode(String wholeEvalMode) {
		this.wholeEvalMode = wholeEvalMode;
	}

	public float getTopscore() {
		return topscore;
	}

	public void setTopscore(float topscore) {
		this.topscore = topscore;
	}

	private float topscore=0;

	
	
	public String getWholeEvalScore() {
		return wholeEvalScore;
	}

	public void setWholeEvalScore(String wholeEvalScore) {
		this.wholeEvalScore = wholeEvalScore;
	}
    
	public String getShowHistoryScore() {
		return showHistoryScore;
	}

	public void setShowHistoryScore(String showHistoryScore) {
		this.showHistoryScore = showHistoryScore;
	}

	@Override
    public void outPutFormHM() {
				
		this.setAppContantList((ArrayList)this.getFormHM().get("appContantList"));
		this.setAppitem_id((String)this.getFormHM().get("appitem_id"));
		this.setParamTable((Hashtable)this.getFormHM().get("paramTable"));
		this.setFromModel((String)this.getFormHM().get("fromModel"));
		this.setDegreeShowType((String)this.getFormHM().get("DegreeShowType"));
		this.setPointEvalType((String)this.getFormHM().get("PointEvalType"));
		this.setPointIDs((String)this.getFormHM().get("pointIDs"));
		
		this.setEmployRecordUrl((String)this.getFormHM().get("employRecordUrl"));
		this.setEvalOutLimitStdScore((String)this.getFormHM().get("evalOutLimitStdScore"));
		this.setRelatingTargetCard((String)this.getFormHM().get("relatingTargetCard"));
		this.setScoreCause((String)this.getFormHM().get("scoreCause"));
		this.setObjectStatus((String)this.getFormHM().get("objectStatus"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		
		this.setS_rejectCause((String)this.getFormHM().get("s_rejectCause"));
		this.setG_rejectCause((String)this.getFormHM().get("g_rejectCause"));
		
		this.setNoteIdioGoal((String)this.getFormHM().get("noteIdioGoal"));
		this.setIsSummary((String)this.getFormHM().get("isSummary"));
		this.setGoalContext((String)this.getFormHM().get("goalContext"));
		this.setGoalFileIdsList((ArrayList)this.getFormHM().get("goalFileIdsList"));
		this.setGoalState((String)this.getFormHM().get("goalState"));
		this.setIsGoalFile((String)this.getFormHM().get("isGoalFile"));
		
		this.setSummaryState((String)this.getFormHM().get("summaryState"));
		this.setSummaryFileIdsList((ArrayList)this.getFormHM().get("summaryFileIdsList"));
		
		this.setPerSetShowMode((String)this.getFormHM().get("perSetShowMode"));
		this.setIsEntireysub((String)this.getFormHM().get("isEntireysub"));
		this.setOptObject((String)this.getFormHM().get("optObject"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setIsFile((String)this.getFormHM().get("isFile"));
		this.setSize((String)this.getFormHM().get("size"));
		this.setPerformanceType((String)this.getFormHM().get("performanceType"));
//		得到考核计划的集合类		
		this.setPointContrl((String)this.getFormHM().get("pointContrl"));
		this.setObjectList((ArrayList)this.getFormHM().get("objectList"));
		this.setGradeHtml((String)this.getFormHM().get("gradeHtml"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setPersonalComment((String)this.getFormHM().get("personalComment"));
		this.setGoalComment((String)this.getFormHM().get("goalComment"));
		this.setIsNull((String)this.getFormHM().get("isNull"));
		this.setNoGradeItem((String)this.getFormHM().get("noGradeItem"));
		this.setScoreflag((String)this.getFormHM().get("scoreflag"));
		this.setDataArea((String)this.getFormHM().get("dataArea"));
		this.setMainBodyId((String)this.getFormHM().get("mainBodyId"));
		this.setTemplateId((String)this.getFormHM().get("templateId"));
		this.setNodeKnowDegree((String)this.getFormHM().get("nodeKnowDegree"));
		this.setWholeEval((String)this.getFormHM().get("wholeEval"));
		this.setTotalAppFormula((String)this.getFormHM().get("totalAppFormula"));
		this.setLimitation((String)this.getFormHM().get("limitation"));
		this.setGradeClass((String)this.getFormHM().get("gradeClass"));
		this.setStatus((String)this.getFormHM().get("status"));
		if(this.getFormHM().get("object_id")!=null)
			this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setNotMark((String)this.getFormHM().get("notMark"));
		this.setIsSelfMark((String)this.getFormHM().get("isSelfMark"));
		this.setTargetDeclare((String)this.getFormHM().get("targetDeclare"));
		this.setIndividualPerformance((String)this.getFormHM().get("individualPerformance"));
		this.setScoreBySumup((String)this.getFormHM().get("scoreBySumup"));
		this.setIsShowTotalScore((String)this.getFormHM().get("isShowTotalScore"));
		
		this.setFillCtrl((String)this.getFormHM().get("fillctrl"));
		this.setLay((String)this.getFormHM().get("lay"));
		
		this.setYears((ArrayList)this.getFormHM().get("years"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonths((ArrayList)this.getFormHM().get("months"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setCounts((ArrayList)this.getFormHM().get("counts"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setHalfYears((ArrayList)this.getFormHM().get("halfYears"));
		this.setHalfYear((String)this.getFormHM().get("halfYear"));
		this.setQuarters((ArrayList)this.getFormHM().get("quarters"));
		this.setQuarter((String)this.getFormHM().get("quarter"));
		this.setStatCustomMode((String)this.getFormHM().get("statCustomMode"));
		
		
		
		this.setPerCompare((String)this.getFormHM().get("perCompare"));
		this.setChangFlag((String)this.getFormHM().get("changFlag"));
		this.getPerformanceListform().setList((ArrayList)this.getFormHM().get("performanceListform"));
		this.setReportTitles((String[])this.getFormHM().get("reportTitles"));
		this.setItemidList((ArrayList)this.getFormHM().get("itemidList"));
		this.setOperate((String)this.getFormHM().get("operate"));
		this.setAllowUploadFile((String)this.getFormHM().get("allowUploadFile"));
		
		this.setShowHistoryScore((String)this.getFormHM().get("showHistoryScore"));
		
		this.setWholeEvalScore((String)this.getFormHM().get("wholeEvalScore"));
		this.setWholeEvalMode((String)this.getFormHM().get("wholeEvalMode"));
		this.setTopscore(Float.parseFloat((String)this.getFormHM().get("topscore")));
		this.setPlan_descript_content((String)this.getFormHM().get("plan_descript_content"));
		this.setMustFillWholeEval((String)this.getFormHM().get("mustFillWholeEval"));
		
		this.setIsnullAffix((String)this.getFormHM().get("isnullAffix"));
		
		this.setFilenametemplet((String)this.getFormHM().get("filenametemplet"));
		
		String _msg = (String) getFormHM().get("errorMsg");
		_msg = _msg == null || "".equals(_msg.trim()) ? "" : _msg;
		this.errorMsg = _msg;
		getFormHM().remove("errorMsg");
	}

	@Override
    public void inPutTransHM() {
				
		this.getFormHM().put("appContantList",this.getAppContantList());
		this.getFormHM().put("appitem_id",this.getAppitem_id());
		this.getFormHM().put("scoreCause",this.getScoreCause());
		this.getFormHM().put("goalContext",this.getGoalContext());
		this.getFormHM().put("searchname",this.getSearchname());
		
		this.getFormHM().put("goalfileName",this.getGoalfileName());
		this.getFormHM().put("fileName", this.getFileName());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("object_id",this.getObject_id());
		this.getFormHM().put("summary",this.getSummary());
		this.getFormHM().put("titleName",this.getTitleName());
		
		this.getFormHM().put("statMethod",this.getStatMethod());
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("month",this.getMonth());
		this.getFormHM().put("count",this.getCount());
		this.getFormHM().put("quarter",this.getQuarter());
		this.getFormHM().put("halfYear",this.getHalfYear());
		this.getFormHM().put("perCompare",this.getPerCompare());
		
		this.getFormHM().put("statStartDate",this.getStatStartDate());
		this.getFormHM().put("statEndDate",this.getStatEndDate());
		
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("goalfile",this.getGoalfile());
		this.getFormHM().put("allowUploadFile",this.getAllowUploadFile());
		
		this.getFormHM().put("showHistoryScore", this.getShowHistoryScore());
		this.getFormHM().put("wholeEvalScore", this.getWholeEvalScore());
		this.getFormHM().put("wholeEvalMode", this.getWholeEvalMode());
		this.getFormHM().put("topscore", String.valueOf(this.getTopscore()));
		this.getFormHM().put("plan_descript_content", this.getPlan_descript_content());
		this.getFormHM().put("mustFillWholeEval", this.getMustFillWholeEval());
		 
	}
	
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/selfservice/performance/singleGrade".equals(arg0.getPath())&&arg1.getParameter("b_individual")!=null){
            /**定位到首页,*/
            if(this.getPerformanceListform()!=null)
            	this.getPerformanceListform().getPagination().firstPage();  
        } 
		return super.validate(arg0, arg1);
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

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public String getGradeHtml() {
		return gradeHtml;
	}

	public void setGradeHtml(String gradeHtml) {
		this.gradeHtml = gradeHtml;
	}

	public String getPersonalComment() {
		return personalComment;
	}

	public void setPersonalComment(String personalComment) {
		this.personalComment = personalComment;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getScoreflag() {
		return scoreflag;
	}

	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}

	public String getDataArea() {
		return dataArea;
	}

	public void setDataArea(String dataArea) {
		this.dataArea = dataArea;
	}

	public String getMainBodyId() {
		return mainBodyId;
	}

	public void setMainBodyId(String mainBodyId) {
		this.mainBodyId = mainBodyId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getNodeKnowDegree() {
		return nodeKnowDegree;
	}

	public void setNodeKnowDegree(String nodeKnowDegree) {
		this.nodeKnowDegree = nodeKnowDegree;
	}

	public String getWholeEval() {
		return wholeEval;
	}

	public void setWholeEval(String wholeEval) {
		this.wholeEval = wholeEval;
	}

	public String getLimitation() {
		return limitation;
	}

	public void setLimitation(String limitation) {
		this.limitation = limitation;
	}

	public String getGradeClass() {
		return gradeClass;
	}

	public void setGradeClass(String gradeClass) {
		this.gradeClass = gradeClass;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getNotMark() {
		return notMark;
	}

	public void setNotMark(String notMark) {
		this.notMark = notMark;
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

	public String getChangFlag() {
		return changFlag;
	}

	public void setChangFlag(String changFlag) {
		this.changFlag = changFlag;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getPerCompare() {
		return perCompare;
	}

	public void setPerCompare(String perCompare) {
		this.perCompare = perCompare;
	}

	public PaginationForm getPerformanceListform() {
		return performanceListform;
	}

	public void setPerformanceListform(PaginationForm performanceListform) {
		this.performanceListform = performanceListform;
	}

	public String[] getReportTitles() {
		return reportTitles;
	}

	public void setReportTitles(String[] reportTitles) {
		this.reportTitles = reportTitles;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getCounts() {
		return counts;
	}

	public void setCounts(ArrayList counts) {
		this.counts = counts;
	}

	public ArrayList getMonths() {
		return months;
	}

	public void setMonths(ArrayList months) {
		this.months = months;
	}

	public ArrayList getYears() {
		return years;
	}

	public void setYears(ArrayList years) {
		this.years = years;
	}

	public ArrayList getItemidList() {
		return itemidList;
	}

	public void setItemidList(ArrayList itemidList) {
		this.itemidList = itemidList;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getLay() {
		return lay;
	}

	public void setLay(String lay) {
		this.lay = lay;
	}

	public String getHalfYear() {
		return halfYear;
	}

	public void setHalfYear(String halfYear) {
		this.halfYear = halfYear;
	}

	public ArrayList getHalfYears() {
		return halfYears;
	}

	public void setHalfYears(ArrayList halfYears) {
		this.halfYears = halfYears;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public ArrayList getQuarters() {
		return quarters;
	}

	public void setQuarters(ArrayList quarters) {
		this.quarters = quarters;
	}

	public String getStatMethod() {
		return statMethod;
	}

	public void setStatMethod(String statMethod) {
		this.statMethod = statMethod;
	}

	public String getIsSelfMark() {
		return isSelfMark;
	}

	public void setIsSelfMark(String isSelfMark) {
		this.isSelfMark = isSelfMark;
	}

	public String getStatCustomMode() {
		return statCustomMode;
	}

	public void setStatCustomMode(String statCustomMode) {
		this.statCustomMode = statCustomMode;
	}

	public String getStatEndDate() {
		return statEndDate;
	}

	public void setStatEndDate(String statEndDate) {
		this.statEndDate = statEndDate;
	}

	public String getStatStartDate() {
		return statStartDate;
	}

	public void setStatStartDate(String statStartDate) {
		this.statStartDate = statStartDate;
	}

	public String getFillCtrl() {
		return fillCtrl;
	}

	public void setFillCtrl(String fillCtrl) {
		this.fillCtrl = fillCtrl;
	}

	public String getScoreBySumup() {
		return scoreBySumup;
	}

	public void setScoreBySumup(String scoreBySumup) {
		this.scoreBySumup = scoreBySumup;
	}

	public String getPointContrl() {
		return pointContrl;
	}

	public void setPointContrl(String pointContrl) {
		this.pointContrl = pointContrl;
	}

	public String getPerformanceType() {
		return performanceType;
	}

	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getIsFile() {
		return isFile;
	}

	public void setIsFile(String isFile) {
		this.isFile = isFile;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getOptObject() {
		return optObject;
	}

	public void setOptObject(String optObject) {
		this.optObject = optObject;
	}

	public String getIsShowTotalScore() {
		return isShowTotalScore;
	}

	public void setIsShowTotalScore(String isShowTotalScore) {
		this.isShowTotalScore = isShowTotalScore;
	}

	public String getPerSetShowMode() {
		return perSetShowMode;
	}

	public void setPerSetShowMode(String perSetShowMode) {
		this.perSetShowMode = perSetShowMode;
	}

	public String getIsEntireysub() {
		return isEntireysub;
	}

	public void setIsEntireysub(String isEntireysub) {
		this.isEntireysub = isEntireysub;
	}

	public String getNoGradeItem() {
		return noGradeItem;
	}

	public void setNoGradeItem(String noGradeItem) {
		this.noGradeItem = noGradeItem;
	}

	public String getGoalContext() {
		return goalContext;
	}

	public void setGoalContext(String goalContext) {
		this.goalContext = goalContext;
	}

	public String getIsGoalFile() {
		return isGoalFile;
	}

	public void setIsGoalFile(String isGoalFile) {
		this.isGoalFile = isGoalFile;
	}

	public String getNoteIdioGoal() {
		return noteIdioGoal;
	}

	public void setNoteIdioGoal(String noteIdioGoal) {
		this.noteIdioGoal = noteIdioGoal;
	}

	public String getIsSummary() {
		return isSummary;
	}

	public void setIsSummary(String isSummary) {
		this.isSummary = isSummary;
	}

	public FormFile getGoalfile() {
		return goalfile;
	}

	public void setGoalfile(FormFile goalfile) {
		this.goalfile = goalfile;
	}

	public String getGoalComment() {
		return goalComment;
	}

	public void setGoalComment(String goalComment) {
		this.goalComment = goalComment;
	}

	public ArrayList getSummaryFileIdsList() {
		return summaryFileIdsList;
	}

	public void setSummaryFileIdsList(ArrayList summaryFileIdsList) {
		this.summaryFileIdsList = summaryFileIdsList;
	}



	public ArrayList getGoalFileIdsList() {
		return goalFileIdsList;
	}

	public void setGoalFileIdsList(ArrayList goalFileIdsList) {
		this.goalFileIdsList = goalFileIdsList;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getGoalfileName() {
		return goalfileName;
	}

	public void setGoalfileName(String goalfileName) {
		this.goalfileName = goalfileName;
	}

	public String getGoalState() {
		return goalState;
	}

	public void setGoalState(String goalState) {
		this.goalState = goalState;
	}

	public String getSummaryState() {
		return summaryState;
	}

	public void setSummaryState(String summaryState) {
		this.summaryState = summaryState;
	}

	public String getG_rejectCause() {
		return g_rejectCause;
	}

	public void setG_rejectCause(String cause) {
		g_rejectCause = cause;
	}

	public String getS_rejectCause() {
		return s_rejectCause;
	}

	public void setS_rejectCause(String cause) {
		s_rejectCause = cause;
	}

	public String getObjectStatus() {
		return objectStatus;
	}

	public void setObjectStatus(String objectStatus) {
		this.objectStatus = objectStatus;
	}

	public String getScoreCause() {
		return scoreCause;
	}

	public void setScoreCause(String scoreCause) {
		this.scoreCause = scoreCause;
	}

	public String getRelatingTargetCard() {
		return relatingTargetCard;
	}

	public void setRelatingTargetCard(String relatingTargetCard) {
		this.relatingTargetCard = relatingTargetCard;
	}

	public String getEvalOutLimitStdScore() {
		return evalOutLimitStdScore;
	}

	public void setEvalOutLimitStdScore(String evalOutLimitStdScore) {
		this.evalOutLimitStdScore = evalOutLimitStdScore;
	}

	public String getEmployRecordUrl() {
		return employRecordUrl;
	}

	public void setEmployRecordUrl(String employRecordUrl) {
		this.employRecordUrl = employRecordUrl;
	}

	public String getPointIDs() {
		return pointIDs;
	}

	public void setPointIDs(String pointIDs) {
		this.pointIDs = pointIDs;
	}

	public String getPointEvalType() {
		return PointEvalType;
	}

	public void setPointEvalType(String pointEvalType) {
		PointEvalType = pointEvalType;
	}

	public String getDegreeShowType() {
		return degreeShowType;
	}

	public void setDegreeShowType(String degreeShowType) {
		this.degreeShowType = degreeShowType;
	}

	public String getFromModel() {
		return fromModel;
	}

	public void setFromModel(String fromModel) {
		this.fromModel = fromModel;
	}

	public Hashtable getParamTable() {
		return paramTable;
	}

	public void setParamTable(Hashtable paramTable) {
		this.paramTable = paramTable;
	}

	public String getSearchname() {
		return searchname;
	}

	public void setSearchname(String searchname) {
		this.searchname = searchname;
	}

	public String getTotalAppFormula() {
		return totalAppFormula;
	}

	public void setTotalAppFormula(String totalAppFormula) {
		this.totalAppFormula = totalAppFormula;
	}

	public String getAllowUploadFile() {
		return allowUploadFile;
	}

	public void setAllowUploadFile(String allowUploadFile) {
		this.allowUploadFile = allowUploadFile;
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

	public String getAppitem_id() {
		return appitem_id;
	}

	public void setAppitem_id(String appitem_id) {
		this.appitem_id = appitem_id;
	}

	public ArrayList getAppContantList() {
		return appContantList;
	}

	public void setAppContantList(ArrayList appContantList) {
		this.appContantList = appContantList;
	}

}
