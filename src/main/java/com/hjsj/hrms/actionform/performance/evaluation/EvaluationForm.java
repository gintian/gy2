package com.hjsj.hrms.actionform.performance.evaluation;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * <p>Title:EvaluationForm.java</p>
 * <p>Description:绩效评估</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-26 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class EvaluationForm extends FrameForm
{
	
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private ArrayList planList = new ArrayList(); // 绩效计划
    private String method="1"; //考核方法
    private String planid = "";
    private ArrayList computeFashionList = new ArrayList(); // 计算方式
    private String computeFashion = "";//1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计
    private String planStatus = ""; // 计划状态
    private String code = ""; // 管理范围
    private ArrayList bodyList = new ArrayList(); // 主体类别
    private String bodyid = "";
    private String templateid = ""; // 计划对应的考核模板
    private String object_type = ""; // 1:部门 2:人员 3:单位 4.部门
    private String evaluationTableHtml = ""; // 评估表html
    private String plan_scope = "all"; // 计划范围 all:所有 start:已启动 evaluation:已评估    
    private String summarize="";   //评语或总结
    private ArrayList summaryFileIdsList=new ArrayList();   //个人总结附件id列表
    private String objectid="";
    private ArrayList objectList=new ArrayList();    
    private String  isHandScore="0";  //计划是否有手工录分
    private String  handScore="0";  //启动方式  0:启动（打分） 1：启动（录入结果）    
    // finished：结束
    private String pointResult = "1"; // 指标结果值 1:分数 2:平均分比值 3:总分比值 4:单项比值
    private String briefing = ""; // 简报内容
    private String briefingName = ""; // 简报文件名
    // 指标结果菜单显示
    private String pointResultValue = "false";
    private String pointResult_score = "false";
    private String pointResult_avg = "false";
    private String pointResult_total = "false";
    private String pointResult_single = "false";

    // 计划菜单显示
    private String plan_all = "false";
    private String plan_start = "false";
    private String plan_evaluation = "false";
    private String plan_finished = "false";
    private String correctScore = ""; // 修正分值
    private String correctCause = ""; // 修正原因
    private String object_id = "";
    
    //统一打分
    private ArrayList rateList = new ArrayList();  //统一打分项目列表
    private PaginationForm rateListForm = new PaginationForm();    
    private ArrayList pointList = new ArrayList();  //连表查询出考核指标要素
    private String sql;    //生成所需要的SQL;
    private String per_result;   //数据库表名;
    private ArrayList updateList = new ArrayList();  //更新的list

    // 对象字符串,提供手工查询和条件选择的接口
    private String objStr="";
    private String objStr_temp="";
    private String order_str="";
    private ArrayList currentObjList = new ArrayList(); //当前考核对象列表    
    private String remark;    
    private ArrayList remarkTemplates = new ArrayList();    
    private String  expr="";  //绩效系数公式
    private String customizeGrade="";//自定义等级公式
    private ArrayList customizeGradeList = new ArrayList();
    private String grpMenu1=""; //第一组分组指标  排名指标1，格式：字段名:层级 如："E0122;1" 代码指标通过字段名找。
    private String grpMenu2=""; //第二组分组指标  排名指标1，格式：字段名:层级 如："E0122;1" 代码指标通过字段名找。
    private String grpMenu1Name=""; //第一组排名指标名称
    private String grpMenu1Num="";  //第一组排名指标层级
    private String grpMenu2Name=""; //第二组排名指标名称
    private String grpMenu2Num=""; //第二组排名指标层级
    private String childrenTemp=""; //引入子集组合成的字符串
    private String[]  salarySetIDs=null;
    private String gjsjformula="";  //先根据等级分类规则生成等级
    private String onlyFild="";  //系统唯一性指标
    private String importPlanIds="";  //引入的考核计划的"评估结果中显示"负责人"指标"参数选中的计划ID
    private String mergeModePrams=""; //汇总参数
    private ArrayList yScoreNGradeList = new ArrayList();  //列出分数相同但考核等级不同的考核对象
    private String yScoreNGrade="";   //判断结果表中是否有分数相同但考核等级不同的考核对象    
    private String voteScoreDecimal ="";//总分精度    
    private String voteDecimal ="";//权重精度    
    private ArrayList planbodylist = new ArrayList();    
    private String isAlert = "";    
    private String isShowComputFashion="1";//是否显示计算方式的下拉菜单    
    private String gradeFormula = "0";//计算公式 等级    
    private String  procedureName = "";    
    private String  evalRemark = ""; //备注
    
    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    // list页面用
    private ArrayList setlist = new ArrayList();     
    
    private String dispUnitScore = "0";//显示统一打分的菜单
    private String showBackTables;
    private String isDispAll="true";
    private String startEditScore = "0";
    private String khObjWhere = "";//这个给绩效面谈传递参数 不包括手工选择和条件查询对考核对象的限制
    private String khObjWhere2 = "";//这个给绩效评估模块内部用 包括所有对考核对象的限制 （登录用户操作单位管理范围之类限制,组织机构树选中某结点的限制，手工选择和条件查询对考核对象的限制）
    
	private ArrayList relatelist;
	private ArrayList choicelist;
	private String formula;//总分计算公式
	private ArrayList exprrelatelist;
	private String expression;
	private String flag;
    
	private String validateInfo="";//计算前的校验信息
	private String validateOper="";//计算前的校验操作 分为考核主体对考核对象的考评的校验和等级结果的检查校验
	
	private String objName="";
	
	private String interViewType="1";
	private String jxReportInfo="";//绩效报告提示
	private Hashtable planParamSet = new Hashtable();//取计划参数
	private FormFile file=null;
	private ArrayList implist=new ArrayList();//批量导入数据
	private LazyDynaBean contBean=new LazyDynaBean();
	private String canimport="";
	//票数及占比反馈表 
	private String showaband="";// 显示弃权
	private String showbenbu="";// 显示本部平均
	private String showmethod="";// 横向显示
	private String showobjectpos="";//考核对象显示 =1首条，=2上一条，=3下一条，=4末条。
	private String object_name="";//考核对象名
	private String a0100="";	 //考核对象id
	private String upa0100=""; 	// 上一个
	private String nexta0100=""; //下一个
	
	//备注类型的字段名字和字段值
	private String remarkFieldName = "";
	private String remarkFieldValue = "";
	
	//查看对象得分明细
	private String tableHtml = "";
	private String recheckObjectid = "";//考核对象
	private String showWays ="";
	private String plan_name = "";
	private String objectName = "";
	//显示卡片
	private String cardHtml = "";
	private String scoreExplain = "";//评分说明 0 没有 1 有
	private String scoreExplainFlag = "0";//用户在卡片中是否勾选"评分说明" 0 不勾选 1 勾选
	private String cardObject_id = "";//考核对象
	private ArrayList object_list = new ArrayList();
	//总体评价
	private String totalevaluateObject = "";//考核对象
	private ArrayList evaluate_object_list = new ArrayList();//考核对象列表
	private String evaluateHtml = "";//html的代码
	private String byModel = "";//按岗位素质模型测评
	
	private String obtype;
	
	// 1: 得分统计  2:主体票数统计  3:指标票数分统计  4:主体得分统计
	private Map computeFashionSQLMap = new HashMap(); // 计算方式和排序方式 add by 刘蒙
	
	
	private String deviationScore="0";//是否纠偏总分   0否 1是
	private String scoreDeviationFormula="";//总分纠偏公式
	private String totalScoreFormulaType="";//总分公式01  总分纠偏公式02
	private String showDetails="false";//是否显示对象详情
	private String proAppraise="false";
	private String Plan_type="1";
	private String feedback = "";//是否显示结果反馈 1：显示
    public String getShowDetails() {
		return showDetails;
	}

	public void setShowDetails(String showDetails) {
		this.showDetails = showDetails;
	}

	public String getDeviationScore() {
		return deviationScore;
	}

	public void setDeviationScore(String deviationScore) {
		this.deviationScore = deviationScore;
	}

	public String getScoreDeviationFormula() {
		return scoreDeviationFormula;
	}

	public void setScoreDeviationFormula(String scoreDeviationFormula) {
		this.scoreDeviationFormula = scoreDeviationFormula;
	}

	public String getObtype() {
		return obtype;
	}

	public void setObtype(String obtype) {
		this.obtype = obtype;
	}

	public String getProAppraise() {
		return proAppraise;
	}

	public void setProAppraise(String proAppraise) {
		this.proAppraise = proAppraise;
	}

	public void set_PointResult()
    {

		if ("1".equals(computeFashion)) // 1：得分统计 2:主体票数统计 3: 指标票数分统计
		    pointResultValue = "true";
		else
		    pointResultValue = "false";
	
		if ("1".equals(pointResult) && "1".equals(computeFashion))
		    pointResult_score = "true";
		else
		    pointResult_score = "false";
	
		if ("2".equals(pointResult) && "1".equals(computeFashion))
		    pointResult_avg = "true";
		else
		    pointResult_avg = "false";
	
		if ("3".equals(pointResult) && "1".equals(computeFashion))
		    pointResult_total = "true";
		else
		    pointResult_total = "false";
	
		if ("4".equals(pointResult) && "1".equals(computeFashion))
		    pointResult_single = "true";
		else
		    pointResult_single = "false";
    }

    public void setPlan()
    {

		if ("all".equals(plan_scope))
		    plan_all = "true";
		else
		    plan_all = "false";
	
		if ("start".equals(plan_scope))
		    plan_start = "true";
		else
		    plan_start = "false";
	
		if ("evaluation".equals(plan_scope))
		    plan_evaluation = "true";
		else
		    plan_evaluation = "false";
	
		if ("finished".equals(plan_scope))
		    plan_finished = "true";
		else
		    plan_finished = "false";
    }

    @Override
    public void inPutTransHM()
    {
    	this.getFormHM().put("nexta0100", this.getNexta0100());
    	this.getFormHM().put("upa0100", this.getUpa0100());
    	this.getFormHM().put("showaband", this.getShowaband());
    	this.getFormHM().put("showbenbu", this.getShowbenbu());
    	this.getFormHM().put("showmethod", this.getShowmethod());
    	this.getFormHM().put("showobjectpos", this.getShowobjectpos());
    	this.getFormHM().put("busitype", this.getBusitype());
    	this.getFormHM().put("contBean", this.getContBean());
    	this.getFormHM().put("file", this.getFile());
    	this.getFormHM().put("handScore",this.getHandScore());
    	this.getFormHM().put("yScoreNGrade",this.getYScoreNGrade());
    	this.getFormHM().put("yScoreNGradeList",this.getYScoreNGradeList());
    	this.getFormHM().put("mergeModePrams",this.getMergeModePrams());
    	this.getFormHM().put("importPlanIds",this.getImportPlanIds());
    	this.getFormHM().put("onlyFild", this.getOnlyFild());
    	this.getFormHM().put("gjsjformula", this.getGjsjformula());
    	this.getFormHM().put("salarySetIDs",this.getSalarySetIDs());
    	this.getFormHM().put("childrenTemp", this.getChildrenTemp());
    	this.getFormHM().put("grpMenu1Name", this.getGrpMenu1Name());
        this.getFormHM().put("grpMenu1Num", this.getGrpMenu1Num());
        this.getFormHM().put("grpMenu2Name", this.getGrpMenu2Name());
        this.getFormHM().put("grpMenu2Num", this.getGrpMenu2Num());       
    	this.getFormHM().put("grpMenu1", this.getGrpMenu1());
        this.getFormHM().put("grpMenu2", this.getGrpMenu2());
    	this.getFormHM().put("planParamSet", this.getPlanParamSet());
        this.getFormHM().put("jxReportInfo", this.getJxReportInfo());
    	this.getFormHM().put("interViewType",this.getInterViewType());
		this.getFormHM().put("validateInfo",this.getValidateInfo());
		this.getFormHM().put("validateOper", this.getValidateOper());
		this.getFormHM().put("formula",this.getFormula());
		this.getFormHM().put("flag",this.getFlag());
    	this.getFormHM().put("khObjWhere",this.getKhObjWhere());
    	this.getFormHM().put("khObjWhere2",this.getKhObjWhere2());
    	this.getFormHM().put("startEditScore", this.getStartEditScore());	
	    this.getFormHM().put("isDispAll",this.getIsDispAll());
	    this.getFormHM().put("evalRemark",this.getEvalRemark());
	    this.getFormHM().put("gradeFormula",this.getGradeFormula());
	    this.getFormHM().put("procedureName",this.getProcedureName());
	    this.getFormHM().put("summarize", this.getSummarize());	
	    this.getFormHM().put("objectid",this.getObjectid());
		this.getFormHM().put("planid", this.getPlanid());
		this.getFormHM().put("computeFashion", this.getComputeFashion());
		this.getFormHM().put("plan_scope", this.getPlan_scope());
		this.getFormHM().put("pointResult", this.getPointResult());
		this.getFormHM().put("bodyid", this.getBodyid());
		this.getFormHM().put("showBackTables",this.getShowBackTables());
		set_PointResult();
		setPlan();
		this.getFormHM().put("objStr", this.getObjStr());
		this.getFormHM().put("objStr_temp", this.getObjStr_temp());
		this.getFormHM().put("correctScore", this.getCorrectScore());
		this.getFormHM().put("correctCause", this.getCorrectCause());	
		this.getFormHM().put("remark", this.getRemark());
		this.getFormHM().put("pointList", this.getPointList());
		this.getFormHM().put("order_str",this.getOrder_str());
		
		this.getFormHM().put("expr",this.getExpr());
		this.getFormHM().put("voteScoreDecimal",this.getVoteScoreDecimal());
		this.getFormHM().put("voteDecimal",this.getVoteDecimal());
		this.getFormHM().put("planbodylist",this.getPlanbodylist());
		this.getFormHM().put("isAlert",this.getIsAlert());
		this.getFormHM().put("isShowComputFashion",this.getIsShowComputFashion());
		this.getFormHM().put("dispUnitScore",this.getDispUnitScore());
		this.getFormHM().put("objName",this.getObjName());
		this.getFormHM().put("customizeGrade",this.getCustomizeGrade());
		this.getFormHM().put("customizeGradeList",this.getCustomizeGradeList());
		this.getFormHM().put("implist",this.getImplist());
		this.getFormHM().put("remarkFieldValue", this.getRemarkFieldValue());
		this.getFormHM().put("remarkFieldName", this.getRemarkFieldName());
		this.getFormHM().put("tableHtml", this.getTableHtml());
		this.getFormHM().put("recheckObjectid", this.getRecheckObjectid());
		this.getFormHM().put("showWays", this.getShowWays());
		this.getFormHM().put("plan_name", this.getPlan_name());
		this.getFormHM().put("objectName", this.getObjectName());
		this.getFormHM().put("cardHtml", this.getCardHtml());
		this.getFormHM().put("scoreExplain", this.getScoreExplain());
		this.getFormHM().put("scoreExplainFlag", this.getScoreExplainFlag());
		this.getFormHM().put("cardObject_id", this.getCardObject_id());
		this.getFormHM().put("object_list", this.getObject_list());
		this.getFormHM().put("totalevaluateObject", this.getTotalevaluateObject());
		this.getFormHM().put("evaluate_object_list", this.getEvaluate_object_list());
		this.getFormHM().put("evaluateHtml", this.getEvaluateHtml());
		this.getFormHM().put("obtype", this.getObtype());
		this.getFormHM().put("byModel", this.getByModel());
		this.getFormHM().put("deviationScore", this.getDeviationScore());
		this.getFormHM().put("scoreDeviationFormula", this.getScoreDeviationFormula());
		this.getFormHM().put("totalScoreFormulaType", this.getTotalScoreFormulaType());
		this.getFormHM().put("showDetails", this.getShowDetails());
		this.getFormHM().put("computeFashionSQLMap", this.computeFashionSQLMap);
		this.getFormHM().put("feedback", this.getFeedback());
    }

    @Override
    public void outPutFormHM()
    {
    	this.setUpa0100((String)this.getFormHM().get("upa0100"));
    	this.setNexta0100((String)this.getFormHM().get("nexta0100"));
    	this.setA0100((String)this.getFormHM().get("a0100"));
    	this.setObject_name((String)this.getFormHM().get("object_name"));
    	this.setBusitype((String)this.getFormHM().get("busitype"));
    	this.setContBean((LazyDynaBean)this.getFormHM().get("contBean"));
    	this.setFile((FormFile)this.getFormHM().get("file"));
    	this.setHandScore((String)this.getFormHM().get("handScore"));
    	this.setYScoreNGrade((String)this.getFormHM().get("yScoreNGrade"));
    	this.setYScoreNGradeList((ArrayList)this.getFormHM().get("yScoreNGradeList"));
    	this.setMergeModePrams((String)this.getFormHM().get("mergeModePrams"));
    	this.setImportPlanIds((String)this.getFormHM().get("importPlanIds"));
    	this.setOnlyFild((String)this.getFormHM().get("onlyFild"));
    	this.setGjsjformula((String)this.getFormHM().get("gjsjformula"));
    	this.setSalarySetIDs((String[])this.getFormHM().get("salarySetIDs"));
    	this.setChildrenTemp((String)this.getFormHM().get("childrenTemp"));
    	this.setGrpMenu1Name((String)this.getFormHM().get("grpMenu1Name"));
    	this.setGrpMenu1Num((String)this.getFormHM().get("grpMenu1Num"));
    	this.setGrpMenu2Name((String)this.getFormHM().get("grpMenu2Name"));
    	this.setGrpMenu2Num((String)this.getFormHM().get("grpMenu2Num"));   	
    	this.setGrpMenu1((String)this.getFormHM().get("grpMenu1"));
    	this.setGrpMenu2((String)this.getFormHM().get("grpMenu2"));
    	this.setCustomizeGrade((String)this.getFormHM().get("customizeGrade"));
    	this.setPlanParamSet((Hashtable)this.getFormHM().get("planParamSet"));
        this.setJxReportInfo((String)this.getFormHM().get("jxReportInfo"));
        this.setCustomizeGradeList((ArrayList)this.getFormHM().get("customizeGradeList")); 
    	this.setReturnflag((String)this.getFormHM().get("returnflag")); 
    	this.setInterViewType((String)this.getFormHM().get("interViewType"));
    	this.setObjName((String)this.getFormHM().get("objName"));
		this.setValidateInfo((String)this.getFormHM().get("validateInfo"));
		this.setValidateOper((String)this.getFormHM().get("validateOper"));
    	this.setRelatelist((ArrayList)this.getFormHM().get("relatelist"));
		this.setChoicelist((ArrayList)this.getFormHM().get("choicelist"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setExprrelatelist((ArrayList)this.getFormHM().get("exprrelatelist"));
    	this.setKhObjWhere((String)this.getFormHM().get("khObjWhere"));
    	this.setKhObjWhere2((String)this.getFormHM().get("khObjWhere2"));
    	this.setStartEditScore((String)this.getFormHM().get("startEditScore"));
    	this.setIsDispAll((String) this.getFormHM().get("isDispAll"));
    	this.setShowBackTables((String) this.getFormHM().get("showBackTables"));
    	this.setDispUnitScore((String)this.getFormHM().get("dispUnitScore"));
    	this.setEvalRemark((String)this.getFormHM().get("evalRemark"));
	    this.setGradeFormula((String)this.getFormHM().get("gradeFormula"));
	    this.setProcedureName((String)this.getFormHM().get("procedureName"));
		this.setIsShowComputFashion((String)this.getFormHM().get("isShowComputFashion"));
		this.setPlanbodylist((ArrayList)this.getFormHM().get("planbodylist"));
	    this.setVoteDecimal((String)this.getFormHM().get("voteDecimal"));
	    this.setVoteScoreDecimal((String)this.getFormHM().get("voteScoreDecimal"));
	    this.setExpr((String)this.getFormHM().get("expr"));
	    	
	    this.setIsHandScore((String)this.getFormHM().get("isHandScore"));
	    this.setSummarize((String)this.getFormHM().get("summarize"));
	    this.setSummaryFileIdsList((ArrayList)this.getFormHM().get("summaryFileIdsList"));
	    
	    this.setObjectid((String)this.getFormHM().get("objectid"));
	    this.setObjectList((ArrayList)this.getFormHM().get("objectList"));
	    this.setMethod((String)this.getFormHM().get("method"));	
	    	
	    this.setOrder_str((String)this.getFormHM().get("order_str"));
		this.setCode((String) this.getFormHM().get("code"));
		this.setPlan_scope((String) this.getFormHM().get("plan_scope"));
		this.setPointResult((String) this.getFormHM().get("pointResult"));
		this.setPlanStatus((String) this.getFormHM().get("planStatus"));
		this.setBodyid((String) this.getFormHM().get("bodyid"));
		this.setBodyList((ArrayList) this.getFormHM().get("bodylist"));
		this.setPlanid((String) this.getFormHM().get("planid"));
		this.setPlanList((ArrayList) this.getFormHM().get("planList"));
		this.setComputeFashionList((ArrayList) this.getFormHM().get("computeFashionList"));
		this.setComputeFashion((String) this.getFormHM().get("computeFashion"));
		this.setTemplateid((String) this.getFormHM().get("templateid"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setEvaluationTableHtml((String) this.getFormHM().get("evaluationTableHtml"));
		this.setBriefing((String) this.getFormHM().get("briefing")); // 简报输出
		this.setBriefingName((String) this.getFormHM().get("briefingName"));
		this.setRateList((ArrayList) this.getFormHM().get("rateList"));
		this.setUpdateList((ArrayList) this.getFormHM().get("updateList"));
		this.setPointList((ArrayList) this.getFormHM().get("pointList"));
		this.setSql((String)this.getFormHM().get("sql")); 
		this.setPer_result((String)this.getFormHM().get("per_result"));
		set_PointResult();
		setPlan();
		this.setObjStr((String) this.getFormHM().get("objStr"));
		this.setObjStr_temp((String) this.getFormHM().get("objStr_temp"));
		this.setCorrectScore((String) this.getFormHM().get("correctScore"));
		this.setCorrectCause((String) this.getFormHM().get("correctCause"));
		this.setObject_id((String) this.getFormHM().get("object_id"));
		this.setCurrentObjList((ArrayList)this.getFormHM().get("CurrentObjList"));
		this.setRemark((String)this.getFormHM().get("remark"));
		this.setRemarkTemplates((ArrayList)this.getFormHM().get("RemarkTemplates"));
		this.setIsAlert((String)this.getFormHM().get("isAlert"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.getRateListForm().setList((ArrayList) this.getFormHM().get("rateList"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setImplist((ArrayList)this.getFormHM().get("implist"));
		this.setCanimport((String)this.getFormHM().get("canimport"));
		this.setShowaband((String)this.getFormHM().get("showaband"));
		this.setShowbenbu((String)this.getFormHM().get("showbenbu"));
		this.setShowmethod((String)this.getFormHM().get("showmethod"));
		this.setShowobjectpos((String)this.getFormHM().get("showobjectpos"));
		this.setRemarkFieldName((String)this.getFormHM().get("remarkFieldName"));
		this.setRemarkFieldValue((String)this.getFormHM().get("remarkFieldValue"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setRecheckObjectid((String)this.getFormHM().get("recheckObjectid"));
		this.setShowWays((String)this.getFormHM().get("showWays"));
		this.setPlan_name((String)this.getFormHM().get("plan_name"));
		this.setObjectName((String)this.getFormHM().get("objectName"));
		this.setCardHtml((String)this.getFormHM().get("cardHtml"));
		this.setScoreExplain((String)this.getFormHM().get("scoreExplain"));
		this.setScoreExplainFlag((String)this.getFormHM().get("scoreExplainFlag"));
		this.setCardObject_id((String)this.getFormHM().get("cardObject_id"));
		this.setObject_list((ArrayList)this.getFormHM().get("object_list"));
		this.setTotalevaluateObject((String)this.getFormHM().get("totalevaluateObject"));
		this.setEvaluate_object_list((ArrayList)this.getFormHM().get("evaluate_object_list"));
		this.setEvaluateHtml((String)this.getFormHM().get("evaluateHtml"));
		this.setObtype((String)this.getFormHM().get("obtype"));
		this.setByModel((String)this.getFormHM().get("byModel"));    
		this.setDeviationScore((String)this.getFormHM().get("deviationScore"));
		this.setScoreDeviationFormula((String)this.getFormHM().get("scoreDeviationFormula"));
		this.setTotalScoreFormulaType((String)this.getFormHM().get("totalScoreFormulaType"));
		this.setShowDetails((String)this.getFormHM().get("showDetails"));
		this.computeFashionSQLMap = (HashMap) this.getFormHM().get("computeFashionSQLMap");
		this.setProAppraise((String) this.getFormHM().get("proAppraise"));
		this.setPlan_type((String) this.getFormHM().get("Plan_type"));
		this.setFeedback((String) this.getFormHM().get("feedback"));
    }
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1){
    	super.reset(arg0, arg1);
    	this.setScoreExplainFlag("0");
    }
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/evaluation/performanceEvaluation".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {		
			if (this.setlistform.getPagination() != null){
			    this.setlistform.getPagination().firstPage();
			}
		    }
		    if ("/performance/evaluation/performanceEvaluation".equals(arg0.getPath()) && arg1.getParameter("b_rate") != null)
		    {		
			if (this.setlistform.getPagination() != null){
			    this.setlistform.getPagination().firstPage();
			}
		    }
		    if ("/performance/evaluation/performanceEvaluation".equals(arg0.getPath()) && arg1.getParameter("b_saveFormula") != null)
		    {
				arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
    
    public ArrayList getImplist() {
		return implist;
	}

	public String getShowaband() {
		return showaband;
	}

	public void setShowaband(String showaband) {
		this.showaband = showaband;
	}

	public String getShowbenbu() {
		return showbenbu;
	}

	public void setShowbenbu(String showbenbu) {
		this.showbenbu = showbenbu;
	}

	public String getShowmethod() {
		return showmethod;
	}

	public void setShowmethod(String showmethod) {
		this.showmethod = showmethod;
	}

	public String getShowobjectpos() {
		return showobjectpos;
	}

	public void setShowobjectpos(String showobjectpos) {
		this.showobjectpos = showobjectpos;
	}

	public void setImplist(ArrayList implist) {
		this.implist = implist;
	}

	public String getCanimport() {
		return canimport;
	}

	public void setCanimport(String canimport) {
		this.canimport = canimport;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getComputeFashion()
    {

	return computeFashion;
    }

    public void setComputeFashion(String computeFashion)
    {

	this.computeFashion = computeFashion;
    }

    public ArrayList getComputeFashionList()
    {

	return computeFashionList;
    }

    public void setComputeFashionList(ArrayList computeFashionList)
    {

	this.computeFashionList = computeFashionList;
    }

    public String getPlanid()
    {

	return planid;
    }

    public void setPlanid(String planid)
    {

	this.planid = planid;
    }

    public ArrayList getPlanList()
    {

	return planList;
    }

    public void setPlanList(ArrayList planList)
    {

	this.planList = planList;
    }

    public String getTemplateid()
    {

	return templateid;
    }

    public void setTemplateid(String templateid)
    {

	this.templateid = templateid;
    }

    public String getObject_type()
    {

	return object_type;
    }

    public void setObject_type(String object_type)
    {

	this.object_type = object_type;
    }

    public String getEvaluationTableHtml()
    {

	return evaluationTableHtml;
    }

    public void setEvaluationTableHtml(String evaluationTableHtml)
    {

	this.evaluationTableHtml = evaluationTableHtml;
    }

    public String getPlan_all()
    {

	return plan_all;
    }

    public void setPlan_all(String plan_all)
    {

	this.plan_all = plan_all;
    }

    public String getPlan_evaluation()
    {

	return plan_evaluation;
    }

    public void setPlan_evaluation(String plan_evaluation)
    {

	this.plan_evaluation = plan_evaluation;
    }

    public String getPlan_finished()
    {

	return plan_finished;
    }

    public void setPlan_finished(String plan_finished)
    {

	this.plan_finished = plan_finished;
    }

    public String getPlan_start()
    {

	return plan_start;
    }

    public void setPlan_start(String plan_start)
    {

	this.plan_start = plan_start;
    }

    public String getPointResult_avg()
    {

	return pointResult_avg;
    }

    public void setPointResult_avg(String pointResult_avg)
    {

	this.pointResult_avg = pointResult_avg;
    }

    public String getPointResult_score()
    {

	return pointResult_score;
    }

    public void setPointResult_score(String pointResult_score)
    {

	this.pointResult_score = pointResult_score;
    }

    public String getPointResult_single()
    {

	return pointResult_single;
    }

    public void setPointResult_single(String pointResult_single)
    {

	this.pointResult_single = pointResult_single;
    }

    public String getPointResult_total()
    {

	return pointResult_total;
    }

    public void setPointResult_total(String pointResult_total)
    {

	this.pointResult_total = pointResult_total;
    }

    public String getPointResultValue()
    {

	return pointResultValue;
    }

    public void setPointResultValue(String pointResultValue)
    {

	this.pointResultValue = pointResultValue;
    }

    public String getPlan_scope()
    {

	return plan_scope;
    }

    public void setPlan_scope(String plan_scope)
    {

	this.plan_scope = plan_scope;
    }

    public String getPointResult()
    {

	return pointResult;
    }

    public void setPointResult(String pointResult)
    {

	this.pointResult = pointResult;
    }

    public String getBodyid()
    {

	return bodyid;
    }

    public void setBodyid(String bodyid)
    {

	this.bodyid = bodyid;
    }

    public ArrayList getBodyList()
    {

	return bodyList;
    }

    public void setBodyList(ArrayList bodyList)
    {

	this.bodyList = bodyList;
    }

    public String getPlanStatus()
    {

	return planStatus;
    }

    public void setPlanStatus(String planStatus)
    {

	this.planStatus = planStatus;
    }

    public String getCorrectCause()
    {

	return correctCause;
    }

    public void setCorrectCause(String correctCause)
    {

	this.correctCause = correctCause;
    }

    public String getCorrectScore()
    {

	return correctScore;
    }

    public void setCorrectScore(String correctScore)
    {

	this.correctScore = correctScore;
    }

    public String getObject_id()
    {

	return object_id;
    }

    public void setObject_id(String object_id)
    {

	this.object_id = object_id;
    }

    public String getBriefing()
    {

	return briefing;
    }

    public void setBriefing(String briefing)
    {

	this.briefing = briefing;
    }

    public String getBriefingName()
    {

	return briefingName;
    }

    public void setBriefingName(String briefingName)
    {

	this.briefingName = briefingName;
    }

    public String getCode()
    {

	return code;
    }

    public void setCode(String code)
    {

	this.code = code;
    }

    public String getObjStr()
    {

	return objStr;
    }

    public void setObjStr(String objStr)
    {

	this.objStr = objStr;
    }

    public ArrayList getCurrentObjList()
    {
    
        return currentObjList;
    }

    public void setCurrentObjList(ArrayList currentObjList)
    {
    
        this.currentObjList = currentObjList;
    }

    public String getRemark()
    {
    
        return remark;
    }

    public void setRemark(String remark)
    {
    
        this.remark = remark;
    }

    public ArrayList getRemarkTemplates()
    {
    
        return remarkTemplates;
    }

    public void setRemarkTemplates(ArrayList remarkTemplates)
    {
    
        this.remarkTemplates = remarkTemplates;
    }

    public ArrayList getRateList() {
		return rateList;
	}
	public void setRateList(ArrayList rateList) {
		this.rateList = rateList;
	}
	
	public ArrayList getPointList() {
		return pointList;
	}
	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getPer_result() {
		return per_result;
	}

	public void setPer_result(String per_result) {
		this.per_result = per_result;
	}
	
	public ArrayList getUpdateList() {
		return updateList;
	}
	public void setUpdateList(ArrayList updateList) {
		this.updateList = updateList;
	}

	public String getOrder_str() {
		return order_str;
	}

	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	public ArrayList getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList objectList) {
		this.objectList = objectList;
	}

	public String getSummarize() {
		return summarize;
	}

	public void setSummarize(String summarize) {
		this.summarize = summarize;
	}

	public String getIsHandScore() {
		return isHandScore;
	}

	public void setIsHandScore(String isHandScore) {
		this.isHandScore = isHandScore;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public ArrayList getSummaryFileIdsList() {
		return summaryFileIdsList;
	}

	public void setSummaryFileIdsList(ArrayList summaryFileIdsList) {
		this.summaryFileIdsList = summaryFileIdsList;
	}

	public String getVoteDecimal()
	{
	
	    return voteDecimal;
	}

	public void setVoteDecimal(String voteDecimal)
	{
	
	    this.voteDecimal = voteDecimal;
	}

	public String getVoteScoreDecimal()
	{
	
	    return voteScoreDecimal;
	}

	public void setVoteScoreDecimal(String voteScoreDecimal)
	{
	
	    this.voteScoreDecimal = voteScoreDecimal;
	}

	public ArrayList getPlanbodylist()
	{
	
	    return planbodylist;
	}

	public void setPlanbodylist(ArrayList planbodylist)
	{
	
	    this.planbodylist = planbodylist;
	}

	public String getIsAlert()
	{
	
	    return isAlert;
	}

	public void setIsAlert(String isAlert)
	{
	
	    this.isAlert = isAlert;
	}

	public String getIsShowComputFashion()
	{
	
	    return isShowComputFashion;
	}

	public void setIsShowComputFashion(String isShowComputFashion)
	{
	
	    this.isShowComputFashion = isShowComputFashion;
	}

	public String getGradeFormula()
	{
		return gradeFormula;
	}

	public void setGradeFormula(String gradeFormula)
	{
		this.gradeFormula = gradeFormula;
	}

	public String getProcedureName()
	{
		return procedureName;
	}

	public void setProcedureName(String procedureName)
	{
		this.procedureName = procedureName;
	}

	public String getEvalRemark()
	{
		return evalRemark;
	}

	public void setEvalRemark(String evalRemark)
	{
		this.evalRemark = evalRemark;
	}

	public ArrayList getSetlist()
	{
		return setlist;
	}

	public void setSetlist(ArrayList setlist)
	{
		this.setlist = setlist;
	}

	public PaginationForm getSetlistform()
	{
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform)
	{
		this.setlistform = setlistform;
	}

	public String getDispUnitScore() {
		return dispUnitScore;
	}

	public void setDispUnitScore(String dispUnitScore) {
		this.dispUnitScore = dispUnitScore;
	}

	public String getShowBackTables() {
		return showBackTables;
	}

	public void setShowBackTables(String showBackTables) {
		this.showBackTables = showBackTables;
	}

	public String getIsDispAll()
	{
		return isDispAll;
	}

	public void setIsDispAll(String isDispAll)
	{
		this.isDispAll = isDispAll;
	}

	public String getStartEditScore()
	{
		return startEditScore;
	}

	public void setStartEditScore(String startEditScore)
	{
		this.startEditScore = startEditScore;
	}

	public String getKhObjWhere()
	{
		return khObjWhere;
	}

	public void setKhObjWhere(String khObjWhere)
	{
		this.khObjWhere = khObjWhere;
	}

	public String getKhObjWhere2()
	{
		return khObjWhere2;
	}

	public void setKhObjWhere2(String khObjWhere2)
	{
		this.khObjWhere2 = khObjWhere2;
	}

	public ArrayList getChoicelist()
	{
		return choicelist;
	}

	public void setChoicelist(ArrayList choicelist)
	{
		this.choicelist = choicelist;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public ArrayList getExprrelatelist()
	{
		return exprrelatelist;
	}

	public void setExprrelatelist(ArrayList exprrelatelist)
	{
		this.exprrelatelist = exprrelatelist;
	}

	public String getFlag()
	{
		return flag;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}

	public String getFormula()
	{
		return formula;
	}

	public void setFormula(String formula)
	{
		this.formula = formula;
	}

	public ArrayList getRelatelist()
	{
		return relatelist;
	}

	public void setRelatelist(ArrayList relatelist)
	{
		this.relatelist = relatelist;
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

	public String getObjName()
	{
		return objName;
	}

	public void setObjName(String objName)
	{
		this.objName = objName;
	}

	public String getInterViewType()
	{
		return interViewType;
	}

	public void setInterViewType(String interViewType)
	{
		this.interViewType = interViewType;
	} 
	
    public String getJxReportInfo()
	{
		return jxReportInfo;
	}

	public void setJxReportInfo(String jxReportInfo)
	{
		this.jxReportInfo = jxReportInfo;
	}

	public PaginationForm getRateListForm()
	{
		return rateListForm;
	}

	public void setRateListForm(PaginationForm rateListForm)
	{
		this.rateListForm = rateListForm;
	}

	public Hashtable getPlanParamSet() {
		return planParamSet;
	}

	public void setPlanParamSet(Hashtable planParamSet) {
		this.planParamSet = planParamSet;
	}

	public String getCustomizeGrade() {
		return customizeGrade;
	}

	public void setCustomizeGrade(String customizeGrade) {
		this.customizeGrade = customizeGrade;
	}

	public ArrayList getCustomizeGradeList() {
		return customizeGradeList;
	}

	public void setCustomizeGradeList(ArrayList customizeGradeList) {
		this.customizeGradeList = customizeGradeList;
	}

	public String getGrpMenu1() {
		return grpMenu1;
	}

	public void setGrpMenu1(String grpMenu1) {
		this.grpMenu1 = grpMenu1;
	}

	public String getGrpMenu2() {
		return grpMenu2;
	}

	public void setGrpMenu2(String grpMenu2) {
		this.grpMenu2 = grpMenu2;
	}

	public String getGrpMenu1Name() {
		return grpMenu1Name;
	}

	public void setGrpMenu1Name(String grpMenu1Name) {
		this.grpMenu1Name = grpMenu1Name;
	}

	public String getGrpMenu1Num() {
		return grpMenu1Num;
	}

	public void setGrpMenu1Num(String grpMenu1Num) {
		this.grpMenu1Num = grpMenu1Num;
	}

	public String getGrpMenu2Name() {
		return grpMenu2Name;
	}

	public void setGrpMenu2Name(String grpMenu2Name) {
		this.grpMenu2Name = grpMenu2Name;
	}

	public String getGrpMenu2Num() {
		return grpMenu2Num;
	}

	public void setGrpMenu2Num(String grpMenu2Num) {
		this.grpMenu2Num = grpMenu2Num;
	}

	public String getChildrenTemp() {
		return childrenTemp;
	}

	public void setChildrenTemp(String childrenTemp) {
		this.childrenTemp = childrenTemp;
	}

	public String[] getSalarySetIDs() {
		return salarySetIDs;
	}

	public void setSalarySetIDs(String[] salarySetIDs) {
		this.salarySetIDs = salarySetIDs;
	}

	public String getGjsjformula() {
		return gjsjformula;
	}

	public void setGjsjformula(String gjsjformula) {
		this.gjsjformula = gjsjformula;
	}

	public String getOnlyFild() {
		return onlyFild;
	}

	public void setOnlyFild(String onlyFild) {
		this.onlyFild = onlyFild;
	}

	public String getImportPlanIds() {
		return importPlanIds;
	}

	public void setImportPlanIds(String importPlanIds) {
		this.importPlanIds = importPlanIds;
	}

	public String getMergeModePrams() {
		return mergeModePrams;
	}

	public void setMergeModePrams(String mergeModePrams) {
		this.mergeModePrams = mergeModePrams;
	}

	public ArrayList getYScoreNGradeList() {
		return yScoreNGradeList;
	}

	public void setYScoreNGradeList(ArrayList scoreNGradeList) {
		yScoreNGradeList = scoreNGradeList;
	}

	public String getYScoreNGrade() {
		return yScoreNGrade;
	}

	public void setYScoreNGrade(String scoreNGrade) {
		yScoreNGrade = scoreNGrade;
	}

	public String getHandScore() {
		return handScore;
	}

	public void setHandScore(String handScore) {
		this.handScore = handScore;
	}

	public LazyDynaBean getContBean() {
		return contBean;
	}

	public void setContBean(LazyDynaBean contBean) {
		this.contBean = contBean;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getUpa0100() {
		return upa0100;
	}

	public void setUpa0100(String upa0100) {
		this.upa0100 = upa0100;
	}

	public String getNexta0100() {
		return nexta0100;
	}

	public void setNexta0100(String nexta0100) {
		this.nexta0100 = nexta0100;
	}

	public String getRemarkFieldName() {
		return remarkFieldName;
	}

	public void setRemarkFieldName(String remarkFieldName) {
		this.remarkFieldName = remarkFieldName;
	}

	public String getRemarkFieldValue() {
		return remarkFieldValue;
	}

	public void setRemarkFieldValue(String remarkFieldValue) {
		this.remarkFieldValue = remarkFieldValue;
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getRecheckObjectid() {
		return recheckObjectid;
	}

	public void setRecheckObjectid(String recheckObjectid) {
		this.recheckObjectid = recheckObjectid;
	}

	public String getShowWays() {
		return showWays;
	}

	public void setShowWays(String showWays) {
		this.showWays = showWays;
	}

	public String getPlan_name() {
		return plan_name;
	}

	public void setPlan_name(String plan_name) {
		this.plan_name = plan_name;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getCardHtml() {
		return cardHtml;
	}

	public void setCardHtml(String cardHtml) {
		this.cardHtml = cardHtml;
	}

	public String getScoreExplain() {
		return scoreExplain;
	}

	public void setScoreExplain(String scoreExplain) {
		this.scoreExplain = scoreExplain;
	}

	public String getScoreExplainFlag() {
		return scoreExplainFlag;
	}

	public void setScoreExplainFlag(String scoreExplainFlag) {
		this.scoreExplainFlag = scoreExplainFlag;
	}

	public String getCardObject_id() {
		return cardObject_id;
	}

	public void setCardObject_id(String cardObject_id) {
		this.cardObject_id = cardObject_id;
	}

	public ArrayList getObject_list() {
		return object_list;
	}

	public void setObject_list(ArrayList object_list) {
		this.object_list = object_list;
	}

	public String getTotalevaluateObject() {
		return totalevaluateObject;
	}

	public void setTotalevaluateObject(String totalevaluateObject) {
		this.totalevaluateObject = totalevaluateObject;
	}

	public ArrayList getEvaluate_object_list() {
		return evaluate_object_list;
	}

	public void setEvaluate_object_list(ArrayList evaluate_object_list) {
		this.evaluate_object_list = evaluate_object_list;
	}

	public String getEvaluateHtml() {
		return evaluateHtml;
	}

	public void setEvaluateHtml(String evaluateHtml) {
		this.evaluateHtml = evaluateHtml;
	}

	public String getByModel() {
		return byModel;
	}

	public void setByModel(String byModel) {
		this.byModel = byModel;
	}

	public String getTotalScoreFormulaType() {
		return totalScoreFormulaType;
	}

	public void setTotalScoreFormulaType(String totalScoreFormulaType) {
		this.totalScoreFormulaType = totalScoreFormulaType;
	}

	public Map getComputeFashionSQLMap() {
		return computeFashionSQLMap;
	}
	
	public void setComputeFashionSQLMap(Map computeFashionSQLMap) {
		this.computeFashionSQLMap = computeFashionSQLMap;
	}

	public String getObjStr_temp() {
		return objStr_temp;
	}

	public void setObjStr_temp(String objStr_temp) {
		this.objStr_temp = objStr_temp;
	}

	public String getPlan_type() {
		return Plan_type;
	}

	public void setPlan_type(String plan_type) {
		Plan_type = plan_type;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
}
