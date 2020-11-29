/*
 * 创建日期 2005-6-29
 *
 */
package com.hjsj.hrms.actionform.performance;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 * 
 * 查询统计Form
 *  
 */
public class StatisticForm extends FrameForm {
	private String showAppraiseExplain="true";  //是否显示评测说明
	
	private String appraise="";   //考核评语
	private String model="";      //  0：绩效考核  1：民主评测
	private String grade_id = ""; //等级

	private String planNum = ""; //活动计划号
	private ArrayList planList=new ArrayList();

	private String totalGrade = "0"; //综合评分

	private String totalCount = "0"; //测评总数量

	private ArrayList examinelist = new ArrayList(); //测试级别及数量ArrayList

	private ArrayList itemwhilelist = new ArrayList(); //项目ArrayList 第一个循环

	private String itemTotalCount = "0"; //输出页面项目总数

	private String resultdesc = ""; //测评等级

	private ArrayList elevellist = new ArrayList(); //总体评价ArrayList对象

	private String flag = "0"; //所有项目为空的判断

	private String message = ""; //提示消息

	private String objectId = "0"; //考核对象Id

	private String drawingFlag = "0"; //图形与表格显示标记

	private String companyId = ""; //对象单位代码

	private String companyName = ""; //对象单位名称
	
	private String GATIShowDegree=""; //BS 综合测评表中指标的评分显示为标度
	
	private String WholeEval="";       //总体评价
	private String NodeKnowDegree="";  //了解程度
	private String isShowStatistic="0"; //是否显示选票统计   1：显示   0：不显示

	private HashMap statisticDrawHm=new HashMap();		//图形统计HashMap
	
	private ArrayList pointNotelst=new ArrayList();		//要素名称与id对应说明ArrayList
	private ArrayList knowlist=new ArrayList();	//了解程度
	private String selfStr="select plan_id,name from per_plan where status=? and 1>2";
	private String objectIdStr="select plan_id,name from per_plan where status=?  and 1>2";
	/**考评结果*/
    private DynaBean result_bean=new LazyDynaBean();  //chenmengqing added 20050829

    private ChartParameter chartParameter = null ; //图形参数
    private String title;
    private String showtype="0";
	/**
	 * @return 返回 objectIdStr。
	 */
    /****对应一个计划的多个登记表****/
    private ArrayList enrol_list=new ArrayList();
    private ArrayList page_list=new ArrayList();
    private String tabid;
    private String infokind="5";//表示计划登记表
    private String nid;
    private String property;
    private String enrol_flag;
    CardTagParamView cardparam=new CardTagParamView();
	public String getEnrol_flag() {
		return enrol_flag;
	}
	public void setEnrol_flag(String enrol_flag) {
		this.enrol_flag = enrol_flag;
	}
	public String getInfokind() {
		return infokind;
	}
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getObjectIdStr() {
		return objectIdStr;
	}
	/**
	 * @param objectIdStr 要设置的 objectIdStr。
	 */
	public void setObjectIdStr(String objectIdStr) {
		this.objectIdStr = objectIdStr;
	}
	/**
	 * @return 返回 selfStr。
	 */
	public String getSelfStr() {
		return selfStr;
	}
	/**
	 * @param selfStr 要设置的 selfStr。
	 */
	public void setSelfStr(String selfStr) {
		this.selfStr = selfStr;
	}
	/**
	 * @return 返回 knowlist。
	 */
	public ArrayList getKnowlist() {
		return knowlist;
	}
	/**
	 * @param knowlist 要设置的 knowlist。
	 */
	public void setKnowlist(ArrayList knowlist) {
		this.knowlist = knowlist;
	}
	public void setPointNotelst(ArrayList pointNotelst)
	{
		this.pointNotelst=pointNotelst;
	}
	
	public ArrayList getPointNotelst()
	{
		return this.pointNotelst;
	}
	
	public void setStatisticDrawHm(HashMap statisticDrawHm)
	{
		this.statisticDrawHm=statisticDrawHm;
	}
	
	public HashMap getStatisticDrawHm()
	{
		return this.statisticDrawHm;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * 图形与表格显示标记
	 * 
	 * @return
	 */
	public String getDrawingFlag() {
		return drawingFlag;
	}

	public void setDrawingFlag(String drawingFlag) {
		this.drawingFlag = drawingFlag;
	}

	/**
	 * 用户对象Id
	 * 
	 * @return
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectId() {
		return this.objectId;
	}

	/**
	 * 提示消息属性
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	/**
	 * 清除属性
	 *  
	 */
	public void messageClear() {
		this.getFormHM().put("message", "");
	}

	/**
	 * 所有的测试等级分类
	 * 
	 * @return
	 */

	public ArrayList getElevellist() {
		return this.elevellist;
	}

	public void setElevellist(ArrayList elevellist) {
		this.elevellist = elevellist;
	}

	/**
	 * 所有项目为空的判断
	 * 
	 * @return
	 */
	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * 测评等级属性
	 * 
	 * @return
	 */
	public String getResultdesc() {
		return resultdesc;
	}

	public void setResultdesc(String resultdesc) {
		this.resultdesc = resultdesc;
	}

	/**
	 * 输出页面项目总数属性
	 * 
	 * @return
	 */
	public String getItemTotalCount() {
		return itemTotalCount;
	}

	public void setItemTotalCount(String itemTotalCount) {
		this.itemTotalCount = itemTotalCount;
	}

	/**
	 * 项目ArrayList属性
	 * 
	 * @return
	 */
	public ArrayList getItemwhilelist() {
		return itemwhilelist;
	}

	public void setItemwhilelist(ArrayList itemwhilelist) {
		this.itemwhilelist = itemwhilelist;
	}

	/**
	 * 测评总数量属性
	 * 
	 * @return
	 */
	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 测试级别及数量ArrayList属性
	 * 
	 * @return
	 */
	public ArrayList getExaminelist() {
		return examinelist;
	}

	public void setExaminelist(ArrayList examinelist) {
		this.examinelist = examinelist;
	}

	public String getGrade_id() {
		return this.grade_id;
	}

	public void setGrade_id(String grade_id) {
		this.grade_id = grade_id;
	}

	public String getPlanNum() {
		return this.planNum;
	}

	public void setPlanNum(String planNum) {
		this.planNum = planNum;
	}

	public String getTotalGrade() {
		return totalGrade;
	}

	public void setTotalGrade(String totalGrade) {
		this.totalGrade = totalGrade;
	}

	@Override
    public void outPutFormHM() {
		this.setTitle((String)this.getFormHM().get("title"));
		this.setShowAppraiseExplain((String)this.getFormHM().get("showAppraiseExplain"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setShowtype((String)this.getFormHM().get("showtype"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setGATIShowDegree((String)this.getFormHM().get("GATIShowDegree"));
		this.setPlanNum(this.getFormHM().get("planNum").toString());
		this.setTotalGrade(this.getFormHM().get("totalGrade").toString());
		this.setGrade_id(this.getFormHM().get("grade_id").toString());
		this.setExaminelist((ArrayList) this.getFormHM().get("examinelist"));
		this.setTotalCount(this.getFormHM().get("totalCount").toString());
		this.setItemwhilelist((ArrayList) this.getFormHM().get("itemwhilelist"));
		this.setItemTotalCount(this.getFormHM().get("itemTotalCount").toString());
		this.setResultdesc(this.getFormHM().get("resultdesc").toString());
		this.setFlag(this.getFormHM().get("flag").toString());
		this.setElevellist((ArrayList) this.getFormHM().get("elevellist"));
		this.setMessage(this.getFormHM().get("message").toString());
		this.setObjectId(this.getFormHM().get("objectId").toString());
		this.setDrawingFlag(this.getFormHM().get("drawingFlag").toString());
		this.setCompanyId(this.getFormHM().get("companyId").toString());
		this.setCompanyName(this.getFormHM().get("companyName").toString());
		this.setStatisticDrawHm((HashMap)this.getFormHM().get("statisticDrawHm"));
		this.setPointNotelst((ArrayList)this.getFormHM().get("pointNotelst"));
		this.setKnowlist((ArrayList)this.getFormHM().get("knowlist"));
		this.setSelfStr(this.getFormHM().get("selfStr").toString());
		this.setObjectIdStr(this.getFormHM().get("objectIdStr").toString());
		//chenmengqing added 20050829
		this.setResult_bean((DynaBean)this.getFormHM().get("result_bean"));
		this.setAppraise((String)this.getFormHM().get("appraise"));
		//dengcan  add 20070320
		this.setNodeKnowDegree((String)this.getFormHM().get("nodeKnowDegree"));  //了解程度
		this.setWholeEval((String)this.getFormHM().get("wholeEval"));            //总体评价
		this.setIsShowStatistic((String)this.getFormHM().get("isShowStatistic"));
		
		this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
	    this.setEnrol_list((ArrayList)this.getFormHM().get("enrol_list"));
	    this.setNid((String)this.getFormHM().get("nid"));
	    this.setInfokind((String)this.getFormHM().get("infokind"));
	    this.setTabid((String)this.getFormHM().get("tabid"));
	    this.setProperty((String)this.getFormHM().get("property"));	  
	    this.setEnrol_flag((String)this.getFormHM().get("enrol_flag"));
	    this.setPage_list((ArrayList)this.getFormHM().get("page_list"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("planNum", this.getPlanNum());
		this.getFormHM().put("totalGrade", this.getTotalGrade());
		this.getFormHM().put("grade_id", this.getGrade_id());
		this.getFormHM().put("examinelist", this.getExaminelist());
		this.getFormHM().put("totalCount", this.getTotalCount());
		this.getFormHM().put("itemwhilelist", this.getItemwhilelist());
		this.getFormHM().put("itemTotalCount", this.getItemTotalCount());
		this.getFormHM().put("resultdesc", this.getResultdesc());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("elevellist", this.getElevellist());
		this.getFormHM().put("message", this.getMessage());
		this.getFormHM().put("objectId", this.getObjectId());
		this.getFormHM().put("drawingFlag", this.getDrawingFlag());
		this.getFormHM().put("companyId", this.getCompanyId());
		this.getFormHM().put("companyName", this.getCompanyName());
		this.getFormHM().put("statisticDrawHm",this.getStatisticDrawHm());
		this.getFormHM().put("pointNotelst",this.getPointNotelst());
		this.getFormHM().put("knowlist",this.getKnowlist());
		this.getFormHM().put("selfStr",this.getSelfStr());
		this.getFormHM().put("objectIdStr",this.getObjectIdStr());
		
		
	}
	public void clearDrawingFlag()
	{
		this.getFormHM().put("drawingFlag","0");
	}
	public DynaBean getResult_bean() {
		return result_bean;
	}
	public void setResult_bean(DynaBean result_bean) {
		this.result_bean = result_bean;
	}
	public String getAppraise() {
		return appraise;
	}
	public void setAppraise(String appraise) {
		this.appraise = appraise;
	}
	public ChartParameter getChartParameter() {
		return chartParameter;
	}
	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}
	public ArrayList getEnrol_list() {
		return enrol_list;
	}
	public void setEnrol_list(ArrayList enrol_list) {
		this.enrol_list = enrol_list;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	public ArrayList getPage_list() {
		return page_list;
	}
	public void setPage_list(ArrayList page_list) {
		this.page_list = page_list;
	}
	public String getGATIShowDegree() {
		return GATIShowDegree;
	}
	public void setGATIShowDegree(String showDegree) {
		GATIShowDegree = showDegree;
	}
	public String getNodeKnowDegree() {
		return NodeKnowDegree;
	}
	public void setNodeKnowDegree(String nodeKnowDegree) {
		NodeKnowDegree = nodeKnowDegree;
	}
	public String getWholeEval() {
		return WholeEval;
	}
	public void setWholeEval(String wholeEval) {
		WholeEval = wholeEval;
	}
	public String getIsShowStatistic() {
		return isShowStatistic;
	}
	public void setIsShowStatistic(String isShowStatistic) {
		this.isShowStatistic = isShowStatistic;
	}
	public ArrayList getPlanList() {
		return planList;
	}
	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getShowAppraiseExplain() {
		return showAppraiseExplain;
	}
	public void setShowAppraiseExplain(String showAppraiseExplain) {
		this.showAppraiseExplain = showAppraiseExplain;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShowtype() {
		return showtype;
	}
	public void setShowtype(String showtype) {
		this.showtype = showtype;
	}

}