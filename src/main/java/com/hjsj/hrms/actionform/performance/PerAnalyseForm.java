package com.hjsj.hrms.actionform.performance;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:PerAnalyseForm.java</p>
 * <p>Description:绩效分析</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author Administrator
 * @version 1.0
 */

public class PerAnalyseForm extends FrameForm 
{
		
    private String busitype = "0"; // 业务分类字段 =0(绩效考核); =1(能力素质)
	private HashMap dataMap=new HashMap();
	private ArrayList dataList=new ArrayList();
	private ArrayList pointToNameList=new ArrayList();
	private ChartParameter chartParam=new ChartParameter();
	private String chartParameterStr="";
	private String chart_type="";   //图形样式
	
	private ArrayList stencilList=new ArrayList();  //考核模板列表
	private String stencilId="";                 //考核模板id
	private String period="";                    //考核周期
	private ArrayList periodList=new ArrayList();   //考核周期列表
	private ArrayList perPlanList=new ArrayList();  //考核计划列表
	private String planIds="";                   //已选的考核计划
	private String plan_ids="";                  //符合条件的考核计划id s
	private String objectNum="0";                //考核对象数量
	private ArrayList pointList=new ArrayList();    //指标列表
	private String pointID="";
	private ArrayList abscissaNameList=new ArrayList();  //横坐标名称列表
	private ArrayList reverseDataList=new ArrayList();   //反查数据
	private String object_type="2";                   //考核对象类型  1：部门  2：人员 
	private String opt="1";                      //1：单人对比分析   2：多人对比分析
	
	private ArrayList perDegreeList=new ArrayList();   //考核等级列表
	private ArrayList wholeEvalDataList=new ArrayList(); //总体评价数据
	private String objectName="";
	private String remark="";                         //评语
	
	private String objectType="0";  //主体分类对比分析/考核对象方式  0：单考核对象  1：多考核对象
	
	private String statHtml="";
	private String chartHeight ="400";
	private String chartWidth="400";
	
	private String isShowScore="1";  //是否显示分值  1：显示 0：不显示
	private String isShow3D="0";     //是否显示立体图
	private String isShowRadar="0";     //是否显示雷达图
	private String scoreGradeStr=""; //分值序列
	
	private String isShowPercentVal="2";//是否按百分比显示分值  0不按百分比，1按百分比，2按等级
	private ArrayList planList = new ArrayList();
	private String statTitle="";
	   // list页面用
    private PaginationForm setlistform = new PaginationForm();
    private String objSelected = "";
    private String fromModule = "";
    private String pointName = "";
    private String perVoteStatInfo = "";
    private String codeitemid = "";
    private String mcontrastids = "";//多人对比分析 上个计划选中的对象
    
    private String gradeResultHtml = ""; // 人岗匹配界面
    private String object_id = ""; // 考核对象id
    private String objE01A1 = ""; // 考核对象岗位编码
    private String byModel="";   //是否按岗位素质模型  1按
    
    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 start */
    // 是否显示历史
    private String lastplan;
    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 end */

    private String isfromKhResult="";   //1：自助-绩效考评-考评反馈-本人能力素质考核结果 0:其他
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/perAnalyse".equals(arg0.getPath()) && arg1.getParameter("b_reverse") != null)
		    {		
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}	
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
    
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("objE01A1", this.getObjE01A1());
		this.getFormHM().put("object_id", this.getObject_id());
		this.getFormHM().put("gradeResultHtml", this.getGradeResultHtml());
		this.getFormHM().put("busitype", this.getBusitype());
		this.getFormHM().put("mcontrastids", this.getMcontrastids());
		this.getFormHM().put("codeitemid", this.getCodeitemid());
		this.getFormHM().put("perVoteStatInfo", this.getPerVoteStatInfo());
		this.getFormHM().put("pointName", this.getPointName());
		this.getFormHM().put("fromModule", this.getFromModule());
		this.getFormHM().put("objSelected", this.getObjSelected());
		this.getFormHM().put("statTitle", this.getStatTitle());
		this.getFormHM().put("stencilId", this.getStencilId());
		this.getFormHM().put("planIds",this.getPlanIds());
		this.getFormHM().put("pointID",this.getPointID());
		this.getFormHM().put("period",this.getPeriod());
		this.getFormHM().put("objectType",this.getObjectType());

		this.getFormHM().put("isShowScore",this.getIsShowScore());
		this.getFormHM().put("isShow3D",this.getIsShow3D());
		this.getFormHM().put("isShowRadar",this.getIsShowRadar());
		this.getFormHM().put("chart_type",this.getChart_type());
		this.getFormHM().put("chartParameterStr",this.getChartParameterStr());
		this.getFormHM().put("planList",this.getPlanList());
		this.getFormHM().put("isShowPercentVal",this.getIsShowPercentVal());
		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 start */
		this.getFormHM().put("lastplan",this.getLastplan());
		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 end */
		this.getFormHM().put("isfromKhResult",this.getIsfromKhResult());//1：自助-绩效考评-考评反馈-本人能力素质考核结果 0:其他
	}

	@Override
    public void outPutFormHM()
	{
		
		this.setObjE01A1((String)this.getFormHM().get("objE01A1"));
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setGradeResultHtml((String)this.getFormHM().get("gradeResultHtml"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
		this.setMcontrastids((String)this.getFormHM().get("mcontrastids"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setPerVoteStatInfo((String)this.getFormHM().get("perVoteStatInfo"));
		this.setPointID((String)this.getFormHM().get("pointID"));
		this.setPointName((String)this.getFormHM().get("pointName"));
		this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.setFromModule((String)this.getFormHM().get("fromModule"));
		this.setObjSelected((String)this.getFormHM().get("objSelected"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("reverseDataList"));
		this.setStatTitle((String)this.getFormHM().get("statTitle"));
		this.setIsShowPercentVal((String)this.getFormHM().get("isShowPercentVal"));
		this.setScoreGradeStr((String)this.getFormHM().get("scoreGradeStr"));
		this.setChartParameterStr((String)this.getFormHM().get("chartParameterStr"));
		this.setChart_type((String)this.getFormHM().get("chart_type"));
		this.setIsShowScore((String)this.getFormHM().get("isShowScore"));
		this.setIsShow3D((String)this.getFormHM().get("isShow3D"));
		this.setIsShowRadar((String)this.getFormHM().get("isShowRadar"));
		
		if(this.getFormHM().get("dataMap")!=null)
			this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		this.setDataList((ArrayList)this.getFormHM().get("dataList"));
		this.setPointToNameList((ArrayList)this.getFormHM().get("pointToNameList"));
		if(this.getFormHM().get("chartParam")!=null)
			this.setChartParam((ChartParameter)this.getFormHM().get("chartParam"));
		this.setPeriod((String)this.getFormHM().get("period"));
		this.setStencilId((String)this.getFormHM().get("stencilId"));
		this.setStencilList((ArrayList)this.getFormHM().get("stencilList"));
		this.setPeriodList((ArrayList)this.getFormHM().get("periodList"));
		this.setPerPlanList((ArrayList)this.getFormHM().get("perPlanList"));
		this.setPointList((ArrayList)this.getFormHM().get("pointList"));
		this.setAbscissaNameList((ArrayList)this.getFormHM().get("abscissaNameList"));
		this.setPlan_ids((String)this.getFormHM().get("plan_ids"));
		this.setPlanIds((String)this.getFormHM().get("planIds"));
		
		this.setChartHeight((String)this.getFormHM().get("chartHeight"));
		this.setChartWidth((String)this.getFormHM().get("chartWidth"));
		this.setObjectNum((String)this.getFormHM().get("objectNum"));
		this.setStatHtml((String)this.getFormHM().get("statHtml"));
		this.setReverseDataList((ArrayList)this.getFormHM().get("reverseDataList"));
		this.setObject_type((String)this.getFormHM().get("object_type"));
		this.setOpt((String)this.getFormHM().get("opt"));
		
		this.setPerDegreeList((ArrayList)this.getFormHM().get("perDegreeList"));
		this.setWholeEvalDataList((ArrayList)this.getFormHM().get("wholeEvalDataList"));
		this.setRemark((String)this.getFormHM().get("remark"));
		this.setObjectName((String)this.getFormHM().get("objectName"));
		
		this.setObjectType((String)this.getFormHM().get("objectType"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setByModel((String)this.getFormHM().get("byModel"));
		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 start */
		this.setLastplan((String)this.getFormHM().get("lastplan"));
		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 2014-8-20 end */
		this.setIsfromKhResult((String)this.getFormHM().get("isfromKhResult"));//1：自助-绩效考评-考评反馈-本人能力素质考核结果 0:其他
	}

	
	public ArrayList getAbscissaNameList() {
		return abscissaNameList;
	}

	public void setAbscissaNameList(ArrayList abscissaNameList) {
		this.abscissaNameList = abscissaNameList;
	}

	public ChartParameter getChartParam() {
		return chartParam;
	}

	public void setChartParam(ChartParameter chartParam) {
		this.chartParam = chartParam;
	}

	public HashMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	public ArrayList getPerPlanList() {
		return perPlanList;
	}

	public void setPerPlanList(ArrayList perPlanList) {
		this.perPlanList = perPlanList;
	}

	public String getPlanIds() {
		return planIds;
	}

	public void setPlanIds(String planIds) {
		this.planIds = planIds;
	}

	public String getPointID() {
		return pointID;
	}

	public void setPointID(String pointID) {
		this.pointID = pointID;
	}

	public ArrayList getPointList() {
		return pointList;
	}

	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}

	public String getStencilId() {
		return stencilId;
	}

	public void setStencilId(String stencilId) {
		this.stencilId = stencilId;
	}

	public ArrayList getStencilList() {
		return stencilList;
	}

	public void setStencilList(ArrayList stencilList) {
		this.stencilList = stencilList;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public ArrayList getPeriodList() {
		return periodList;
	}

	public void setPeriodList(ArrayList periodList) {
		this.periodList = periodList;
	}

	public String getPlan_ids() {
		return plan_ids;
	}

	public void setPlan_ids(String plan_ids) {
		this.plan_ids = plan_ids;
	}

	public String getChart_type() {
		return chart_type;
	}

	public void setChart_type(String chart_type) {
		this.chart_type = chart_type;
	}

	public String getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}

	public String getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}

	public ArrayList getPointToNameList() {
		return pointToNameList;
	}

	public void setPointToNameList(ArrayList pointToNameList) {
		this.pointToNameList = pointToNameList;
	}

	public String getObjectNum() {
		return objectNum;
	}

	public void setObjectNum(String objectNum) {
		this.objectNum = objectNum;
	}

	public String getStatHtml() {
		return statHtml;
	}

	public void setStatHtml(String statHtml) {
		this.statHtml = statHtml;
	}

	public ArrayList getReverseDataList() {
		return reverseDataList;
	}

	public void setReverseDataList(ArrayList reverseDataList) {
		this.reverseDataList = reverseDataList;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public ArrayList getPerDegreeList() {
		return perDegreeList;
	}

	public void setPerDegreeList(ArrayList perDegreeList) {
		this.perDegreeList = perDegreeList;
	}

	public ArrayList getWholeEvalDataList() {
		return wholeEvalDataList;
	}

	public void setWholeEvalDataList(ArrayList wholeEvalDataList) {
		this.wholeEvalDataList = wholeEvalDataList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
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

	public String getChartParameterStr() {
		return chartParameterStr;
	}

	public void setChartParameterStr(String chartParameterStr) {
		this.chartParameterStr = chartParameterStr;
	}

	public String getScoreGradeStr() {
		return scoreGradeStr;
	}

	public void setScoreGradeStr(String scoreGradeStr) {
		this.scoreGradeStr = scoreGradeStr;
	}

	public ArrayList getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	public ArrayList getPlanList()
	{
	
	    return planList;
	}

	public void setPlanList(ArrayList planList)
	{
	
	    this.planList = planList;
	}

	public String getIsShowPercentVal()
	{
		return isShowPercentVal;
	}

	public void setIsShowPercentVal(String isShowPercentVal)
	{
		this.isShowPercentVal = isShowPercentVal;
	}

	public String getStatTitle()
	{
		return statTitle;
	}

	public void setStatTitle(String statTitle)
	{
		this.statTitle = statTitle;
	}

	public PaginationForm getSetlistform()
	{
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform)
	{
		this.setlistform = setlistform;
	}
	public String getObjSelected()
	{
		return objSelected;
	}
	public void setObjSelected(String objSelected)
	{
		this.objSelected = objSelected;
	}
	public String getFromModule()
	{
		return fromModule;
	}
	public void setFromModule(String fromModule)
	{
		this.fromModule = fromModule;
	}
	public String getPointName()
	{
		return pointName;
	}
	public void setPointName(String pointName)
	{
		this.pointName = pointName;
	}
	public String getPerVoteStatInfo()
	{
		return perVoteStatInfo;
	}
	public void setPerVoteStatInfo(String perVoteStatInfo)
	{
		this.perVoteStatInfo = perVoteStatInfo;
	}
	public String getCodeitemid()
	{
		return codeitemid;
	}
	public void setCodeitemid(String codeitemid)
	{
		this.codeitemid = codeitemid;
	}
	public String getMcontrastids()
	{
		return mcontrastids;
	}
	public void setMcontrastids(String mcontrastids)
	{
		this.mcontrastids = mcontrastids;
	}
	public String getIsShowRadar() {
		return isShowRadar;
	}
	public void setIsShowRadar(String isShowRadar) {
		this.isShowRadar = isShowRadar;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getGradeResultHtml() {
		return gradeResultHtml;
	}

	public void setGradeResultHtml(String gradeResultHtml) {
		this.gradeResultHtml = gradeResultHtml;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getObjE01A1() {
		return objE01A1;
	}

	public void setObjE01A1(String objE01A1) {
		this.objE01A1 = objE01A1;
	}

	public String getByModel() {
		return byModel;
	}

	public void setByModel(String byModel) {
		this.byModel = byModel;
	}

	public String getLastplan() {
		return lastplan;
	}

	public void setLastplan(String lastplan) {
		this.lastplan = lastplan;
	}
	
	public String getIsfromKhResult() {
		return isfromKhResult;
	}
	
	public void setIsfromKhResult(String isfromKhResult) {
		this.isfromKhResult = isfromKhResult;
	}
	
	
}
