package com.hjsj.hrms.actionform.performance.kh_result;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class KhResultForm extends FrameForm{
	/**计划号*/
    private String planid;
    /**计划列表*/
    private PaginationForm planListForm = new PaginationForm();
    /**区别模块=0是本人=1是员工=3是团队*/
    private String model;
    /**结果统计图例列表*/
    private ArrayList drawList = new ArrayList();
    /**结果统计图标号*/
    private String drawId;
    /**按分值还是按得分率画图=0分值=1得分率*/
    private String drawtype;
    /**考核要素*/
    private ArrayList pointList  =new ArrayList();
    /**模块区分标志=0是绩效考核，=1是民主测评*/
    private String distinctionFlag;
    
    private String chartsets = "";
    private String scoreGradeStr = ""; //分值序列
    /**考核对象*/
    private String object_id;
    private String object_name;
    /**统计图形数据封装*/
    private HashMap figuresmap;
    /**统计图形参数*/
    private ChartParameter chartParameter = null ;
    /**统计图标题*/
    private String title;
    /**统计图标题对齐方式*/
    private String titleAlign;
    /*******************************************************************/
    /**考核对象基本信息*/
    private LazyDynaBean personalInformation = new LazyDynaBean();
    /**测评说明列表*/
    private ArrayList evaluationDescription = new ArrayList();
    /**考核模板项目列表（里面嵌套要素列表）*/
    private ArrayList itemList = new ArrayList();
    /**总体评价*/
    private ArrayList overallRating = new ArrayList();
    private ArrayList overallRatingDetail = new ArrayList();
    /**了解程度*/
    private ArrayList understandingOf = new ArrayList();
    private ArrayList understandingOfDetail = new ArrayList();
    /**项目总数*/
    private String itemTotal;
    /*********************************************************************/
    /**考核评语*/
    private String reviews;
    /***********************************************************************/
    /**统计图类型=5是饼图=11是直方图*/
    private String charttype;
    /**封装画统计图用到的数据*/
    private ArrayList sumRatingList = new ArrayList();
    private ArrayList overallRatingList= new ArrayList();
    /**得票情况列表*/
    private ArrayList voteList = new ArrayList();
   /**评语和意见列表*/
    private ArrayList reviewsAndViewsList = new ArrayList();
    /**总体评价统计图标题*/
    private String viewsTitle;
    /**分页用sql语句*/
    private String selectSql;
    private String whereSql;
    private String columns;
    private String orderSql;
    /**单位或部门代码过滤*/
    private String code;
    /**权限内的人员库*/
    private ArrayList dbList = new ArrayList();
    /**应用库前缀*/
    private String nbase;
    CardTagParamView cardparam=new CardTagParamView();
    private String tabid;
    
    private String  cardHtml="";
    /**是否显示总体评价*/
    private String isShowOverallRating;
    /**是否显示测评说明,同时控制选票统计*/
    private String isShowEvaluationDescription;
    /**指标评分是否显示为标度*/
    private String isShowDegree;
    /**面谈记录*/
    private ArrayList interviewList = new ArrayList();
    /**谈话记录内容*/
    private String interview;
    private String body;
    private String descriptiveWholeEval;
    private String wholeEval;
    /**表格的选票统计中是否显示了解程度*/
    private String isShowKnowDegree;
    /**表格的选票统计中是否显示总体评价*/
    private String isShowWholeEval;
    private String isShowVoteTd;
    private String isShow3D;
    private String isShowScore;
    private String chart_type;
    private ArrayList dataList = new ArrayList();
    private String isCard="0";
    private String tabIDs;
    private ArrayList tabList = new ArrayList();
    private String oper;
    
    
    /** 绩效面谈调用模版 */
    private String opt="0";  //0:只读  1：可编辑
    private String templet_id="";  //模版id
    private String flag="1";  //1:传统绩效面谈 2：调用模版面谈(操作人：考核主体)  3：调用模版面谈(操作人：考核对象)
    private String ins_id="";
    private String task_id="";
    private ArrayList recordsList=new ArrayList();
    
    private String modelType;
    private String performanceYear;
    private ArrayList performanceYearList = new ArrayList();
    private String objecType="2";
    private ArrayList tableList = new ArrayList();
    private String isLT;
    private String configButton;
    private String selectTabId;
    private String isCloseButton;//是否显示关闭按钮
    private String returnvalue="";
    private String from_flag;
    private String alertMessage;
    private String firstLink;
    private String label_enabled="";
    
    //同级人员得分分布图
    private String graphType = "1";
    private ArrayList graphTypeList = new ArrayList();
    private String department= "0";
    private ArrayList departmentList = new ArrayList();
    // 绩效、能力素质区分0：绩效 1：能力素质
    private String busitype = "";
    //评价盲点
    private String blindHtml;
    
	public String getLabel_enabled() {
		return label_enabled;
	}
	public void setLabel_enabled(String label_enabled) {
		this.label_enabled = label_enabled;
	}
	public String getFirstLink() {
		return firstLink;
	}
	public void setFirstLink(String firstLink) {
		this.firstLink = firstLink;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1){
    	super.reset(arg0, arg1);
    	this.setDepartment("0");
    }
	@Override
    public void inPutTransHM() {
			
		this.getFormHM().put("sumRatingList", this.getSumRatingList());
		this.getFormHM().put("chartsets", this.getChartsets());
		this.getFormHM().put("scoreGradeStr", this.getScoreGradeStr());
		this.getFormHM().put("from_flag", this.getFrom_flag());
		this.getFormHM().put("isCloseButton", this.getIsCloseButton());
		this.getFormHM().put("selectTabId", this.getSelectTabId());
		this.getFormHM().put("configButton", this.getConfigButton());
		this.getFormHM().put("isLT",this.getIsLT());
		this.getFormHM().put("tableList", this.getTableList());
		this.getFormHM().put("modelType", this.getModelType());
		this.getFormHM().put("performanceYear", this.getPerformanceYear());
		this.getFormHM().put("performanceYearList", this.getPerformanceYearList());
	    this.getFormHM().put("objecType", this.getObjecType());
		this.getFormHM().put("oper", this.getOper());
		this.getFormHM().put("tabList", this.getTabList());
		this.getFormHM().put("dataList", this.getDataList());
		this.getFormHM().put("chart_type",this.getChart_type());
		this.getFormHM().put("isShow3D", this.getIsShow3D());
		this.getFormHM().put("isShowScore", this.getIsShowScore());
		this.getFormHM().put("isShowVoteTd", this.getIsShowVoteTd());
		this.getFormHM().put("isShowKnowDegree", this.getIsShowKnowDegree());
		this.getFormHM().put("isShowWholeEval", this.getIsShowWholeEval());
		this.getFormHM().put("wholeEval",this.getWholeEval());
		this.getFormHM().put("descriptiveWholeEval", this.getDescriptiveWholeEval());
		this.getFormHM().put("body",this.getBody());
		this.getFormHM().put("isShowOverallRating", this.getIsShowOverallRating());
		this.getFormHM().put("isShowEvaluationDescription", this.getIsShowEvaluationDescription());
		this.getFormHM().put("isShowDegree", this.getIsShowDegree());
		this.getFormHM().put("selectedList",this.getPlanListForm().getSelectedList());
		this.getFormHM().put("distinctionFlag", this.getDistinctionFlag());
		this.getFormHM().put("planid", this.getPlanid());
		this.getFormHM().put("drawId",this.getDrawId());
		this.getFormHM().put("drawtype", this.getDrawtype());
		this.getFormHM().put("model", this.getModel());
		this.getFormHM().put("object_id",this.getObject_id());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("reviews", this.getReviews());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("isCard",this.getIsCard());
		this.getFormHM().put("tabIDs",this.getTabIDs());
		this.getFormHM().put("graphType", this.getGraphType());
		this.getFormHM().put("graphTypeList", this.getGraphTypeList());
		this.getFormHM().put("department",this.getDepartment());
		this.getFormHM().put("departmentList", this.getDepartmentList());
		this.getFormHM().put("blindHtml", this.getBlindHtml());

	}
	@Override
    public void outPutFormHM() {
		
		this.setSumRatingList((ArrayList)this.getFormHM().get("sumRatingList"));
		this.setScoreGradeStr((String)this.getFormHM().get("scoreGradeStr"));
		this.setChartsets((String)this.getFormHM().get("chartsets"));
		this.setLabel_enabled((String)this.getFormHM().get("label_enabled"));
		this.setFirstLink((String)this.getFormHM().get("firstLink"));
		this.setAlertMessage((String)this.getFormHM().get("alertMessage"));
		this.setFrom_flag((String)this.getFormHM().get("from_flag"));
		this.setIsCloseButton((String)this.getFormHM().get("isCloseButton"));
		this.setSelectTabId((String)this.getFormHM().get("selectTabId"));
		this.setConfigButton((String)this.getFormHM().get("configButton"));
		this.setIsLT((String)this.getFormHM().get("isLT"));
		this.setTableList((ArrayList)this.getFormHM().get("tableList"));
		this.setModelType((String)this.getFormHM().get("modelType"));
		this.setPerformanceYear((String)this.getFormHM().get("performanceYear"));
		this.setPerformanceYearList((ArrayList)this.getFormHM().get("performanceYearList"));
		this.setIns_id((String)this.getFormHM().get("ins_id"));
		this.setTask_id((String)this.getFormHM().get("task_id"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setTemplet_id((String)this.getFormHM().get("templet_id"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setRecordsList((ArrayList)this.getFormHM().get("recordsList"));
		
		
		
	    this.setObjecType((String)this.getFormHM().get("objecType"));
		this.setOper((String)this.getFormHM().get("oper"));
		this.setTabList((ArrayList)this.getFormHM().get("tabList"));
		this.setDataList((ArrayList)this.getFormHM().get("dataList"));
		this.setChart_type((String)this.getFormHM().get("chart_type"));
		this.setIsShow3D((String)this.getFormHM().get("isShow3D"));
		this.setIsShowScore((String)this.getFormHM().get("isShowScore"));
		this.setIsShowVoteTd((String)this.getFormHM().get("isShowVoteTd"));
		this.setIsShowWholeEval((String)this.getFormHM().get("isShowWholeEval"));
		this.setIsShowKnowDegree((String)this.getFormHM().get("isShowKnowDegree"));
		this.setDescriptiveWholeEval((String)this.getFormHM().get("descriptiveWholeEval"));
		this.setWholeEval((String)this.getFormHM().get("wholeEval"));
		this.setBody((String)this.getFormHM().get("body"));
		this.setInterview((String)this.getFormHM().get("interview"));
		this.setInterviewList((ArrayList)this.getFormHM().get("interviewList"));
		this.setIsShowOverallRating((String)this.getFormHM().get("isShowOverallRating"));
		this.setIsShowEvaluationDescription((String)this.getFormHM().get("isShowEvaluationDescription"));
		this.setIsShowDegree((String)this.getFormHM().get("isShowDegree"));
		this.setOverallRatingDetail((ArrayList)this.getFormHM().get("overallRatingDetail"));
		this.setUnderstandingOfDetail((ArrayList)this.getFormHM().get("understandingOfDetail"));
		this.setTitleAlign((String)this.getFormHM().get("titleAlign"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setDbList((ArrayList)this.getFormHM().get("dbList"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSelectSql((String)this.getFormHM().get("selectSql"));
		this.setWhereSql((String)this.getFormHM().get("whereSql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.setCharttype((String)this.getFormHM().get("charttype"));
		this.setViewsTitle((String)this.getFormHM().get("viewsTitle"));
		this.setOverallRatingList((ArrayList)this.getFormHM().get("overallRatingList"));
		this.setVoteList((ArrayList)this.getFormHM().get("voteList"));
		this.setReviewsAndViewsList((ArrayList)this.getFormHM().get("reviewsAndViewsList"));
		this.setReviews((String)this.getFormHM().get("reviews"));
		this.setItemTotal((String)this.getFormHM().get("itemTotal"));
		this.setPersonalInformation((LazyDynaBean)this.getFormHM().get("personalInformation"));
		this.setEvaluationDescription((ArrayList)this.getFormHM().get("evaluationDescription"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setOverallRating((ArrayList)this.getFormHM().get("overallRating"));
		this.setUnderstandingOf((ArrayList)this.getFormHM().get("understandingOf"));
		this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
	    this.setObject_id((String)this.getFormHM().get("object_id"));
	    this.setObject_name((String)this.getFormHM().get("object_name"));
		this.getPlanListForm().setList((ArrayList)this.getFormHM().get("planList"));
		this.setPlanid((String)this.getFormHM().get("planid"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setDistinctionFlag((String)this.getFormHM().get("distinctionFlag"));
		this.setDrawId((String)this.getFormHM().get("drawId"));
		this.setDrawtype((String)this.getFormHM().get("drawtype"));
		/**高法修改，只要一种图*/
		/*ArrayList list =(ArrayList)this.getFormHM().get("drawList");
		ArrayList dList = new ArrayList();
		if(list!=null)
	    	dList.add((CommonData)list.get(0));*/
		this.setDrawList((ArrayList)this.getFormHM().get("drawList"));
		this.setPointList((ArrayList)this.getFormHM().get("pointList"));
		this.setFiguresmap((HashMap)this.getFormHM().get("figuresmap"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setCardHtml((String)this.getFormHM().get("cardHtml"));
		this.setIsCard((String)this.getFormHM().get("isCard"));
		this.setTabIDs((String)this.getFormHM().get("tabIDs"));
		this.setGraphType((String)this.getFormHM().get("graphType"));
		this.setGraphTypeList((ArrayList)this.getFormHM().get("graphTypeList"));
		this.setDepartment((String)this.getFormHM().get("department"));
		this.setDepartmentList((ArrayList)this.getFormHM().get("departmentList"));
		this.setBlindHtml((String)this.getFormHM().get("blindHtml"));
		this.setBusitype((String)this.getFormHM().get("busitype"));

	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/kh_result/kh_result_personlist".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
            /**定位到首页,*/
           /* if(this.getPagination()!=null)   返回时，不让其定位在首页  2013.12.24 pjf
            	this.getPagination().firstPage();  */
            
        }
		if("/performance/kh_result/kh_result_orgtree".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
			 if(arg1.getParameter("returnvalue")==null)
		        {
		        	   this.getFormHM().put("returnvalue", "");
		        	   this.setReturnvalue("");
		        }else
		        {
		        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
		        }
            
        }
		if("/performance/kh_result/kh_plan_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
			 if(arg1.getParameter("returnvalue")==null)
		        {
		        	   this.getFormHM().put("returnvalue", "");
		        	   this.setReturnvalue("");
		        }else
		        {
		        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
		        }
			 /**定位到首页,*/
	            /*if(this.getPagination()!=null)    返回时，不让其定位在首页   2013.12.24 pjf
	            	this.getPagination().firstPage(); */    
	            
            if(this.getPlanListForm()!=null)
            	this.getPlanListForm().getPagination().firstPage();
        }
		return super.validate(arg0, arg1);
	}
	public String getDrawId() {
		return drawId;
	}
	public void setDrawId(String drawId) {
		this.drawId = drawId;
	}
	public ArrayList getDrawList() {
		return drawList;
	}
	public void setDrawList(ArrayList drawList) {
		this.drawList = drawList;
	}
	public String getDrawtype() {
		return drawtype;
	}
	public void setDrawtype(String drawtype) {
		this.drawtype = drawtype;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPlanid() {
		return planid;
	}
	public void setPlanid(String planid) {
		this.planid = planid;
	}
	public String getDistinctionFlag() {
		return distinctionFlag;
	}
	public void setDistinctionFlag(String distinctionFlag) {
		this.distinctionFlag = distinctionFlag;
	}
	public ArrayList getPointList() {
		return pointList;
	}
	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}
	public PaginationForm getPlanListForm() {
		return planListForm;
	}
	public void setPlanListForm(PaginationForm planListForm) {
		this.planListForm = planListForm;
	}
	public String getObject_id() {
		return object_id;
	}
	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}
	public HashMap getFiguresmap() {
		return figuresmap;
	}
	public void setFiguresmap(HashMap figuresmap) {
		this.figuresmap = figuresmap;
	}
	public ChartParameter getChartParameter() {
		return chartParameter;
	}
	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList getEvaluationDescription() {
		return evaluationDescription;
	}
	public void setEvaluationDescription(ArrayList evaluationDescription) {
		this.evaluationDescription = evaluationDescription;
	}
	public ArrayList getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}
	public ArrayList getOverallRating() {
		return overallRating;
	}
	public void setOverallRating(ArrayList overallRating) {
		this.overallRating = overallRating;
	}
	public LazyDynaBean getPersonalInformation() {
		return personalInformation;
	}
	public void setPersonalInformation(LazyDynaBean personalInformation) {
		this.personalInformation = personalInformation;
	}
	public ArrayList getUnderstandingOf() {
		return understandingOf;
	}
	public void setUnderstandingOf(ArrayList understandingOf) {
		this.understandingOf = understandingOf;
	}
	public String getItemTotal() {
		return itemTotal;
	}
	public void setItemTotal(String itemTotal) {
		this.itemTotal = itemTotal;
	}
	public String getReviews() {
		return reviews;
	}
	public void setReviews(String reviews) {
		this.reviews = reviews;
	}
	public String getCharttype() {
		return charttype;
	}
	public void setCharttype(String charttype) {
		this.charttype = charttype;
	}
	public ArrayList getOverallRatingList() {
		return overallRatingList;
	}
	public void setOverallRatingList(ArrayList overallRatingList) {
		this.overallRatingList = overallRatingList;
	}
	public ArrayList getReviewsAndViewsList() {
		return reviewsAndViewsList;
	}
	public void setReviewsAndViewsList(ArrayList reviewsAndViewsList) {
		this.reviewsAndViewsList = reviewsAndViewsList;
	}
	public ArrayList getVoteList() {
		return voteList;
	}
	public void setVoteList(ArrayList vteList) {
		this.voteList = vteList;
	}
	public String getViewsTitle() {
		return viewsTitle;
	}
	public void setViewsTitle(String viewsTitle) {
		this.viewsTitle = viewsTitle;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getOrderSql() {
		return orderSql;
	}
	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}
	public String getSelectSql() {
		return selectSql;
	}
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}
	public String getWhereSql() {
		return whereSql;
	}
	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList getDbList() {
		return dbList;
	}
	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getTitleAlign() {
		return titleAlign;
	}
	public void setTitleAlign(String titleAlign) {
		this.titleAlign = titleAlign;
	}
	public ArrayList getOverallRatingDetail() {
		return overallRatingDetail;
	}
	public void setOverallRatingDetail(ArrayList overallRatingDetail) {
		this.overallRatingDetail = overallRatingDetail;
	}
	public ArrayList getUnderstandingOfDetail() {
		return understandingOfDetail;
	}
	public void setUnderstandingOfDetail(ArrayList understandingOfDetail) {
		this.understandingOfDetail = understandingOfDetail;
	}
	public String getCardHtml() {
		return cardHtml;
	}
	public void setCardHtml(String cardHtml) {
		this.cardHtml = cardHtml;
	}
	public String getIsShowOverallRating() {
		return isShowOverallRating;
	}
	public void setIsShowOverallRating(String isShowOverallRating) {
		this.isShowOverallRating = isShowOverallRating;
	}
	public String getIsShowEvaluationDescription() {
		return isShowEvaluationDescription;
	}
	public void setIsShowEvaluationDescription(String isShowEvaluationDescription) {
		this.isShowEvaluationDescription = isShowEvaluationDescription;
	}
	public String getIsShowDegree() {
		return isShowDegree;
	}
	public void setIsShowDegree(String isShowDegree) {
		this.isShowDegree = isShowDegree;
	}
	public ArrayList getInterviewList() {
		return interviewList;
	}
	public void setInterviewList(ArrayList interviewList) {
		this.interviewList = interviewList;
	}
	public String getInterview() {
		return interview;
	}
	public void setInterview(String interview) {
		this.interview = interview;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getDescriptiveWholeEval() {
		return descriptiveWholeEval;
	}
	public void setDescriptiveWholeEval(String descriptiveWholeEval) {
		this.descriptiveWholeEval = descriptiveWholeEval;
	}
	public String getWholeEval() {
		return wholeEval;
	}
	public void setWholeEval(String wholeEval) {
		this.wholeEval = wholeEval;
	}
	public String getIsShowKnowDegree() {
		return isShowKnowDegree;
	}
	public void setIsShowKnowDegree(String isShowKnowDegree) {
		this.isShowKnowDegree = isShowKnowDegree;
	}
	public String getIsShowWholeEval() {
		return isShowWholeEval;
	}
	public void setIsShowWholeEval(String isShowWholeEval) {
		this.isShowWholeEval = isShowWholeEval;
	}
	public String getIsShowVoteTd() {
		return isShowVoteTd;
	}
	public void setIsShowVoteTd(String isShowVoteTd) {
		this.isShowVoteTd = isShowVoteTd;
	}
	public String getIsShow3D() {
		return isShow3D;
	}
	public void setIsShow3D(String isShow3D) {
		this.isShow3D = isShow3D;
	}
	public String getIsShowScore() {
		return isShowScore;
	}
	public void setIsShowScore(String isShowScore) {
		this.isShowScore = isShowScore;
	}
	public String getChart_type() {
		return chart_type;
	}
	public void setChart_type(String chart_type) {
		this.chart_type = chart_type;
	}
	public ArrayList getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}
	public String getIsCard() {
		return isCard;
	}
	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}
	public String getTabIDs() {
		return tabIDs;
	}
	public void setTabIDs(String tabIDs) {
		this.tabIDs = tabIDs;
	}
	public ArrayList getTabList() {
		return tabList;
	}
	public void setTabList(ArrayList tabList) {
		this.tabList = tabList;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public ArrayList getRecordsList() {
		return recordsList;
	}
	public void setRecordsList(ArrayList recordsList) {
		this.recordsList = recordsList;
	}
	public String getTemplet_id() {
		return templet_id;
	}
	public void setTemplet_id(String templet_id) {
		this.templet_id = templet_id;
	}
	public String getIns_id() {
		return ins_id;
	}
	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}
	public String getObjecType()
	{
	
	    return objecType;
	}
	public void setObjecType(String objecType)
	{
	
	    this.objecType = objecType;
	}
	public String getPerformanceYear() {
		return performanceYear;
	}
	public void setPerformanceYear(String performanceYear) {
		this.performanceYear = performanceYear;
	}
	public ArrayList getPerformanceYearList() {
		return performanceYearList;
	}
	public void setPerformanceYearList(ArrayList performanceYearList) {
		this.performanceYearList = performanceYearList;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public ArrayList getTableList() {
		return tableList;
	}
	public void setTableList(ArrayList tableList) {
		this.tableList = tableList;
	}
	public String getIsLT() {
		return isLT;
	}
	public void setIsLT(String isLT) {
		this.isLT = isLT;
	}
	public String getConfigButton() {
		return configButton;
	}
	public void setConfigButton(String configButton) {
		this.configButton = configButton;
	}
	public String getSelectTabId() {
		return selectTabId;
	}
	public void setSelectTabId(String selectTabId) {
		this.selectTabId = selectTabId;
	}
	public String getIsCloseButton() {
		return isCloseButton;
	}
	public void setIsCloseButton(String isCloseButton) {
		this.isCloseButton = isCloseButton;
	}
	public String getFrom_flag() {
		return from_flag;
	}
	public void setFrom_flag(String from_flag) {
		this.from_flag = from_flag;
	}
	public String getAlertMessage() {
		return alertMessage;
	}
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getObject_name() {
		return object_name;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getGraphType() {
		return graphType;
	}
	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}
	public ArrayList getGraphTypeList() {
		return graphTypeList;
	}
	public void setGraphTypeList(ArrayList graphTypeList) {
		this.graphTypeList = graphTypeList;
	}
	public String getBlindHtml() {
		return blindHtml;
	}
	public void setBlindHtml(String blindHtml) {
		this.blindHtml = blindHtml;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public ArrayList getDepartmentList() {
		return departmentList;
	}
	public void setDepartmentList(ArrayList departmentList) {
		this.departmentList = departmentList;
	}
	public String getChartsets() {
		return chartsets;
	}
	public void setChartsets(String chartsets) {
		this.chartsets = chartsets;
	}
	public String getScoreGradeStr() {
		return scoreGradeStr;
	}
	public void setScoreGradeStr(String scoreGradeStr) {
		this.scoreGradeStr = scoreGradeStr;
	}
	public ArrayList getSumRatingList() {
		return sumRatingList;
	}
	public void setSumRatingList(ArrayList sumRatingList) {
		this.sumRatingList = sumRatingList;
	}
	public String getBusitype() {
		return busitype;
	}
	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
}
