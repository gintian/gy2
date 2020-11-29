package com.hjsj.hrms.actionform.performance.kh_plan;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:ExamPlanForm.java</p>
 * <p>Description:考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ExamPlanForm extends FrameForm
{
	//指标id
	String tem_point_id = "";
    //  指标计算公式
	private String totalAppFormula = "";
	private ArrayList pointList = new ArrayList();
	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private String busitype = "0";
    // 权限控制
    private String priv = "1";
    // 设置计划审批模式（0 直批方式|1 审批方式）
    private String model = "1";
    // 时间范围
    private String timeInterval;
    // 开始时间
    private String startDate;
    // 结束时间
    private String endDate;
    // 审批状态
    private String spStatus;
    // 组织机构代码
    private String a_code;
    private String codeName;    
    private String statusName;    
    private String templateName;
    private String paramStr;    
    private String bigField;    
    // 复制基本信息
    private String copy_self;
    // 复制考核主体类别
    private String copy_khmainbodytype;
    // 复制考核对象
    private String copy_khobject;
    // 复制考核主体
    private String copy_khmainbody;
    // 复制考核主体的指标权限
    private String copy_khmainbody_pri;
    // 复制结果信息
    private String copyResultStr;    
    // 表对象
    private RecordVo examPlanVo = new RecordVo("per_plan");
    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    // list页面用
    private ArrayList setlist = new ArrayList();
    // 考核主体类别列表
    private ArrayList mainbodytypeList = new ArrayList();
    // 强制分布考核主体类别列表
    private ArrayList mainbodyGradetypeList = new ArrayList();
    
    // 描述性评议项
    private ArrayList extproList = new ArrayList();    
    private String addDescription = "";
    
	private ArrayList evaluateList = new ArrayList();//考核结果显示列表
    private String evaluate_str=""; 
    private String blind_point="0";    //评价盲点值

	private String dutyRuleid="False";//“按条件引入岗位职责指标”
	private String dutyRule="";//“按条件引入岗位职责指标”具体条件及因子表达式
	private String setid = "";//岗位职责子集
	private String setdesc = "";//岗位职责子集名称  简单条件组件需要用到
	
    private String scrollValue = "";//记录事件列表滚动条的位置
    
    private String batchScoreImportFormula = "" ;  //多人评分是否引入总分计算公式 pjf 2014.01.03
    
    
    public String getScrollValue() {
        return scrollValue;
    }

    public void setScrollValue(String scrollValue) {
        this.scrollValue = scrollValue;
    }

    private String scrollTopValue = ""; //记录事件列表竖直方向滚动条的位置
    
    /**
	 * @return the scrollTopValue
	 */
	public String getScrollTopValue() {
		return scrollTopValue;
	}

	/**
	 * @param scrollTopValue the scrollTopValue to set
	 */
	public void setScrollTopValue(String scrollTopValue) {
		this.scrollTopValue = scrollTopValue;
	}

	public ArrayList getMainbodyGradetypeList() {
		return mainbodyGradetypeList;
	}

	public void setMainbodyGradetypeList(ArrayList mainbodyGradetypeList) {
		this.mainbodyGradetypeList = mainbodyGradetypeList;
	}

	//标准标度的集合
    private ArrayList grade_template = new ArrayList();    
    // list页面用
    private PaginationForm mainbodytypelistform = new PaginationForm();
    /* 以下为考核计划参数设置中的参数 */
    // ///////////////////////////打分控制页参数//////////////////////////////
    // 数据采集录入方式(1-标度 2-混合 4-打分按加扣分处理)
    private String dataGatherMode = "2";
	private String addSubtractType="1";  //加扣分处理方式  1:加扣分  2:加分  3：扣分
    // 标度显示形式(1-标准标度内容 2-指标标度内容）
    private String degreeShowType="1";    
    // 分值转标度规则(1-就高 2-就低）
    private String scaleToDegreeRule="1";
    // 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
    private String sameAllScoreNumLess = "0"; 
    private String sameScoreNumLessValue = "0"; 
    // 是否限制 指标得分为A(优秀)的数目和总体评价最高等级数目（true|false）
    private String fineRestrict;
    // 指标得分为A(优秀)总体评价最高等级的个数(大于0小于1为百分比，大于1为绝对数)-1表示每个指标/总体评价分别设置，数据在FineMax节点
    private String fineMax;
    //部分指标分别设置的情况
    private ArrayList fine_partRestrict;    
    // 是否限制 指标得分为A(优秀)的数目和总体评价最低等级数目
    private String badlyRestrict;      
    // 指标得分为A(优秀)
    // 总体评价最低等级的个数(大于0小于1为百分比，大于1为绝对数)-1表示每个指标/总体评价分别设置，数据在FineMax节点
    private String badlyMax;     
    //部分指标分别设置的情况
    private ArrayList badly_partRestrict;
    // 考核对象指标结果全部相同时的选项 1: 可以保存, 2: 不能保存   3 指定不能保存的标度	
    private String sameResultsOption;
    // 结果全相同时不能保存的标度,(标度代码)‘A,D…’等. 上面指标为3时有效	
    private String noCanSaveDegrees="";  
    private ArrayList noCanSaveDegreesList=new ArrayList();
    // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2 记为
    private String blankScoreOption;
    private String mailTogoLink;
    private String radioDirection;
    // 指标未打分时 记为
    private String blankScoreUseDegree;    
    // 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
    private String scoreWay;    
    // 部门层级
    private String departmentLevel;   
    private ArrayList departmentLeveList = new ArrayList();   
    
    // ////////////////////BS控制参数页面参数//////////////////////////////
    private String scoreShowRelatePlan="False"; //多人评分显示引入计划得分
    // 显示考核指标说明[true|false]
    private String showIndicatorDesc = "False"; 
    //是否显示浏览按钮
    private String isBrowse;    
    //上传指标说明文件
    private FormFile file;    
    // 打分时显示统一打分的定量考核指标[true|false]
    private String showOneMark;
    // 显示个人总结[true|false]
    private String idioSummary;
    // 显示总分[true|false]
    private String showTotalScoreSort;
    // 提交后的计划是否需要显示[true|false]
    private String isShowSubmittedPlan;
    // 显示不打分原因[true|false]
    private String showNoMarking;
    // 提交需要必填[true|false]
    private String isEntireysub;
    // BS个人总结没填写，主体为其打分时不能提交。
    private String scoreBySumup;
    //模板附件 臧雪健，20140528添加，中信金属
    private File accessoriesAffix;
    // 提交后的分数是否显示[true|false]
	private String isShowSubmittedScores;
    // 直接上级可以查看下属打分
    private String selfScoreInDirectLeader;
    // 多人打分，每页人数
    private String scoreNumPerPage;
    // 显示排名[true|false]
    private String isShowOrder;
    // BS是否自动计算总分和排名[true|false]
    private String autoCalcTotalScoreAndOrder;
    // 绩效数据
    private String performanceDate="False";    
    // 绩效子集
    private String perSet;
    // 绩效子集显示方式 1-明细项，2-合计项 或 3-两者者显 0-都不选
    private String perSetShowMode;
    // 绩效子集统计方式 1-年、2-月、3-季度、4-半年、9-时间段 当没有选中显示绩效数据时候赋值：0
    private String perSetStatMode;
    // 用户可前台设置[true|false]
    private String statCustomMode;
    //显示绩效数据子集列表
    private ArrayList itemlist=new ArrayList();    
    //参数开始日期
    private String statStartDate;    
    //参数结束日期
    private String statEndDate;    
    //BS多人打分时是否等级控制
    private String mutiScoreGradeCtl="False";    
    //多人打分时同时显示自我评价
    private String mitiScoreMergeSelfEval="False";    
    //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
    private String checkGradeRange="0";    
    //显示个人目标
    private String  noteIdioGoal = "False";    
    //自我评价不显示打分 默认为 False
    private String  selfEvalNotScore = "False";    
    //显示考核指标内容
    private String  showIndicatorContent = "False";    
    //显示考核指标评分原则
    private String  showIndicatorRole = "False";    
    //显示考核指标标度说明
    private String  showIndicatorDegree = "False";      
    //关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
    private String relatingTargetCard = "1";  
    //显示已自评目标卡
    private String showYPTargetCard = "0";// 显示：1  不显示：0（默认） 在库中则存True，False。在form中用0 1比较方便。  郭峰
    //显示扣分原因(Ture, False(默认))
    private String showDeductionCause = "False";        
	//扣分原因是否必填 现改为启用评分说明
    private String mustFillCause = "False";       
    //多人打分时所有考核对象指标分数全相同时能否保存 默认为 True
    private String canSaveAllObjsScoreSame = "True";    
    //显示合计行
    private String showSumRow = "False";
    //显示基本信息[true|false]
    private String showBasicInfo = "False";
    //显示页面信息list
	private ArrayList messagelist=new ArrayList();
	//显示页面信息itemid
	private String basicInfoItem = "";
	private String lockMGradeColumn = "False";
    // ////////////////////////////////其它参数页面////////////////////////////////////////
    //按岗位素质模型测评[true|false]
    private String byModel="False";
    // 显示总体评价[true|false]
    private String wholeEval;    
    // 总体评价必填[true|false]
    private String mustFillWholeEval;    
    //显示描述性总体评价，默认为 True
    private String  descriptiveWholeEval = "True";
    // 显示了解程度[true|false]
    private String nodeKnowDegree;
    // 综合测评表显示测评说明[true|false]
    private String showAppraiseExplain;
    // 综合测评表中指标的评分显示为标度
    private String gatiShowDegree;
    // 考核形式（0：绩效考核 1：民主评测）
    private String performanceType;    
    // 考核结果反馈表设置
    private String showBackTables;
    private ArrayList cardList = new ArrayList();
    private String showBackTablesInfo;
    //总体评价的的等级分类列表
    private ArrayList perGradeSetList = new ArrayList();
    //选中的等级分类
    private String evalClass = "0";
    //////////////////////目标管理页面参数////////////////////////////////////////////////
    //积分修正[true|false]
    private String keyEventEnabled = "False";    
    //共性指标员工不能编辑, 默认为 False    能编辑
    private String publicPointCannotEdit = "False";    
    //目标卡制订支持几级审批
    private String targetMakeSeries = "1";
    //任务调整需新建任务项
    private String taskAdjustNeedNew = "False";  
    //每项任务可签批
    private String taskCanSign = "False";  
    //每项任务需回顾总结
    private String taskNeedReview = "False";      
    //目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
    private String  targetAppMode = "0";        
    //目标卡批准后允许再调整, (True, False, 默认为True)
    private String  targetAllowAdjustAfterApprove = "True";       
    //允许领导制定及调整目标卡 默认为False
    private String  allowLeadAdjustCard = "False";       
    //允许查看下级对考核对象评分 默认为False
    private String  allowSeeLowerGrade = "False"; 
    //模板id
    private String templateId = "";
    private String evalCanNewPoint = "False";//评估打分允许新增考核指标 (True, False默认为False)
    private String targetTraceEnabled  = "False";//目标卡跟踪显示和采集指标开关
    private String targetTraceItem = ""; //目标卡跟踪显示指标
    private String targetCollectItem = ""; //目标卡采集指标    
    private String targetCalcItem = ""; //目标卡计算指标
    private String noShowTargetAdjustHistory="False";  //打分时不显示任务调整历史
    private String allowLeaderTrace="False";   //允许领导制定及批准跟踪指标, True(默认) False
    private ArrayList targetCalcItemList = new ArrayList();//目标计算项目
    private ArrayList targetCollectItemList = new ArrayList();//目标采集项目
    private ArrayList targetTraceItemList = new ArrayList();//目标卡跟踪显示项目
    private String processNoVerifyAllScore = "True";//报批 批准进行总分校验 (True, False默认为True)
    private String verifyRule = "="; //报批 批准进行总分校验 校验规则 (=,<=)模板总分 默认为=
    private String evalOutLimitStdScore = "False";//评分时得分不受标准分限制 (True, False默认为False)
    private String targetDefineItem = ""; //目标卡指标   
    private ArrayList targetDefineItemList = new ArrayList();//目标卡显示项目
    private String isLimitPointValue = "False"; //限定项目下各任务的权重|分值和等于项目的权重|分值
    private String templateType="";//模板类型
    private String showLeaderEval= "False" ;//本人查看绩效面谈显示领导对其的评价
    private String targetItem = "";//所有有公式的计算指标
    private String allowAdjustEvalResult = "False";//允许调整评估结果 (True, False默认为False)
    private String adjustEvalRange = "0";//调整范围：0=指标，1=总分.默认为0
    private String adjustEvalDegreeType = "0";//调整使用标度0=指标标度，1=等级标度.默认为0
    private String adjustEvalDegreeNum = "0";//调整浮动等级：整数值
    private String calcMenScoreRefDept = "False";//个人考核评分=个人指标得分*部门指标得分的权重和（目标考核和360°）True, False, 默认为 False
    private String showGrpOrder = "True";//评分调整  显示排名：True, False, 默认为 True
    private String verifySameScore = "False";//打分分数相同不能提交：True, False, 默认为 False
    private String showEvalDirector = "False";//评估结果中显示"负责人"指标：True, False, 默认为 False	
    private String adjustEvalGradeStep ="";//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
    private String scoreFromItem = "False";//按项目权重逐级计算总分，True，False 默认False；
    private String readerType = "0"; // 机读类型:0光标阅读机(默认),1扫描仪
    private String bodysFromCard = "False"; //考核主体从机读卡读取(主体类别自动对应)
    private String menRefDeptTmpl = ""; //本次评分=个人指标得分*部门指标得分的权重和时，部门模板。为空表示与当前计划相同。
    private String objsFromCard = "False"; //考核对象是否从机读卡读取(考核实施中不需要选择考核对象) 
	private String showEmployeeRecord="False";  //显示员工日志 zhanghua 2018年5月15日 15:59:34 禁用员工日志
	private String taskSupportAttach="False"; //目标任务支持附件上传  True，False 默认False；
	
	private String spByBodySeq="False"; //按考核主体顺序号控制审批流程(True, False默认为False)
	private String gradeByBodySeq="False"; //按考核主体顺序号控制评分流程(True, False默认为False)
	private String allowSeeAllGrade="False"; //允许查看其它主体对考核对象评分(True, False默认为False)
	
	private String pointEvalType="0";  //360指标评分型式  0：下拉（默认）  1：单选
    
    private String template_id = ""; //部门模板编号。
    private String template_Name = ""; //部门模板名称
    private String status = ""; //部门模板编号。
    private String newTemplate_id = ""; //修改后部门模板编号
    private String oldTemplate_id =""; //修改前部门模板编号
    private String sectorTemplate ="0"; //部门模板状态
    
	private String qname = "";
	private String qmethod = "";
	private String qobject_type="";
	private String paramOper = "";//list: 列表页面编辑参数 detail:详细信息页面编辑参数
	private String bodyTypeIds = "";
	private String tempTemplateId="";//临时模板id 用于区分在detail页面编辑标度对象数部分指标设置时候上次设置的考核模板
	private String planSelect="";//新增或者编辑页面点击保存或者返回选中的计划
	
	private String calItemStr="";   //目标表计算指标串
	private String plan_visibility="";//共享标识
	private String copy_khmainbody_pri_title="";//另存页面复制主体权限等标题动态生成
	
	private String accordPVFlag="1";//打分控制参数 最高标度对象数设置不超过 按比例还是按数值标志 1：按数值 2：按比例
	private ArrayList targetMustFillItemList=new ArrayList();//目标卡必填指标
	private String targetMustFillItem="";	
	private String targetUsePrevious = "";  // 引入上期目标卡指标
	private ArrayList targetUsePreviousList = new ArrayList();
	
	// 评分说明必填高级设置
    private String upIsValid = "False";
    private String downIsValid = "False";
    private String upDegreeId = "";
    private String downDegreeId = "";    
    private String taskNameDesc="";//p0407的指标名称，在目标卡指标页面定义，存在xml参数中
    private String excludeDegree = ""; // 打分为该值时无需填写评分说明
    private List requiredField = new ArrayList(); // 必填指标 add by 刘蒙
    private String requiredFieldStr = ""; // 必填指标 add by 刘蒙
    
    // 预警提醒设置
    private String warnOpt1 = "False"; // 目标卡制定及审批参数  
    private String warnOpt2 = "False"; // 考核评分参数
    private String delayTime1 = "1"; // 目标卡制定及审批延期多少天预警
    private String delayTime2 = "1";  // 考核评分延期多少天预警   
    private String roleScope1 = ""; // 目标卡制定及审批预警对象编号（角色）
    private String roleScope1Desc = ""; // 目标卡制定及审批预警对象（角色）
    private String roleScope2 = ""; // 考核评分预警对象编号（角色）
    private String roleScope2Desc = ""; // 考核评分预警对象（角色）
    
    private String allowUploadFile = "True";//是否支持附件上传
    private String targetCompleteThenGoOn="false";//目标卡填写完整才允许提交（个性化任务、绩效报告）参数
    private String mutiScoreOnePageOnePoint="false";//单体打分
    
    private String showDay = "1";//日报
    private String showMonth = "3";//周报
    private String showWeek = "2";//月报
    private String showDayWeekMonth = "True";
    
    private String mainbodybodyid="";//强制分布中主体类别
    private String allmainbodybody = "";
    public String getAllmainbodybody() {
		return allmainbodybody;
	}

	public void setAllmainbodybody(String allmainbodybody) {
		this.allmainbodybody = allmainbodybody;
	}

	private String wholeEvalMode="0";//总体评价 0：录入等级 1：录入分值
    
    private String evalOutLimitScoreOrg="False";
    
    private String targetCollectItemMust = "";
    
    /**
     * @return the targetCollectItemMust
     */
    public String getTargetCollectItemMust() {
        return targetCollectItemMust;
    }

    /**
     * @param targetCollectItemMust the targetCollectItemMust to set
     */
    public void setTargetCollectItemMust(String targetCollectItemMust) {
        this.targetCollectItemMust = targetCollectItemMust;
    }

    /**
     * @return the evalOutLimitScoreOrg
     */
    public String getEvalOutLimitScoreOrg() {
        return evalOutLimitScoreOrg;
    }

    /**
     * @param evalOutLimitScoreOrg the evalOutLimitScoreOrg to set
     */
    public void setEvalOutLimitScoreOrg(String evalOutLimitScoreOrg) {
        this.evalOutLimitScoreOrg = evalOutLimitScoreOrg;
    }

    public String getWholeEvalMode() {
		return wholeEvalMode;
	}

	public void setWholeEvalMode(String wholeEvalMode) {
		this.wholeEvalMode = wholeEvalMode;
	}

	public String getMainbodybodyid() {
		return mainbodybodyid;
	}

	public void setMainbodybodyid(String mainbodybodyid) {
		this.mainbodybodyid = mainbodybodyid;
	}

	public String getShowDayWeekMonth() {
		return showDayWeekMonth;
	}

	public void setShowDayWeekMonth(String showDayWeekMonth) {
		this.showDayWeekMonth = showDayWeekMonth;
	}

	private String gradeSameNotSubmit="False";//等级不同分数相同不能提交
    private String showHistoryScore = "False";//显示历次得分表

	public String getShowHistoryScore() {
		return showHistoryScore;
	}

	public void setShowHistoryScore(String showHistoryScore) {
		this.showHistoryScore = showHistoryScore;
	}

	public String getGradeSameNotSubmit() {
		return gradeSameNotSubmit;
	}

	public void setGradeSameNotSubmit(String gradeSameNotSubmit) {
		this.gradeSameNotSubmit = gradeSameNotSubmit;
	}

	public String getShowDay() {
		return showDay;
	}

	public void setShowDay(String showDay) {
		this.showDay = showDay;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getShowWeek() {
		return showWeek;
	}

	public void setShowWeek(String showWeek) {
		this.showWeek = showWeek;
	}

	@Override
    public void inPutTransHM()
    { 
		
		this.getFormHM().put("addDescription", this.getAddDescription());
		this.getFormHM().put("extproList", this.getExtproList());
    	this.getFormHM().put("departmentLevel", this.getDepartmentLevel());
    	this.getFormHM().put("departmentLeveList", this.getDepartmentLeveList());
    	this.getFormHM().put("targetUsePrevious", this.getTargetUsePrevious());
    	this.getFormHM().put("targetUsePreviousList", this.getTargetUsePreviousList());
    	this.getFormHM().put("taskNameDesc",this.getTaskNameDesc());   	
    	this.getFormHM().put("performanceDate", this.getPerformanceDate());
    	this.getFormHM().put("busitype", this.getBusitype());
    	this.getFormHM().put("upIsValid", this.getUpIsValid());
    	this.getFormHM().put("downIsValid", this.getDownIsValid());
    	this.getFormHM().put("upDegreeId", this.getUpDegreeId());
    	this.getFormHM().put("downDegreeId", this.getDownDegreeId()); 
    	this.getFormHM().put("targetMustFillItem", this.getTargetMustFillItem());
    	this.getFormHM().put("targetMustFillItemList", this.getTargetMustFillItemList());
    	this.getFormHM().put("addSubtractType", this.getAddSubtractType());
    	this.getFormHM().put("pointEvalType", this.getPointEvalType());
    	this.getFormHM().put("taskSupportAttach", this.getTaskSupportAttach());
        this.getFormHM().put("excludeDegree", excludeDegree); // add by 刘蒙
        this.getFormHM().put("requiredFieldStr", requiredFieldStr); // add by 刘蒙
    	
    	this.getFormHM().put("spByBodySeq", this.getSpByBodySeq());
    	this.getFormHM().put("gradeByBodySeq", this.getGradeByBodySeq());
    	this.getFormHM().put("allowSeeAllGrade", this.getAllowSeeAllGrade());
    	
	    this.getFormHM().put("showEmployeeRecord", this.getShowEmployeeRecord());
	    this.getFormHM().put("oldTemplate_id", this.getOldTemplate_id());
	    this.getFormHM().put("sectorTemplate", this.getSectorTemplate());
	    this.getFormHM().put("status", this.getStatus());
	    this.getFormHM().put("newTemplate_id", this.getNewTemplate_id());
	    this.getFormHM().put("template_id", this.getTemplate_id());
	    this.getFormHM().put("template_Name", this.getTemplate_Name());
	    this.getFormHM().put("objsFromCard", this.getObjsFromCard());
	    this.getFormHM().put("menRefDeptTmpl", this.getMenRefDeptTmpl());
	    this.getFormHM().put("bodysFromCard", this.getBodysFromCard());
	    this.getFormHM().put("readerType", this.getReaderType());
	    this.getFormHM().put("scoreFromItem", this.getScoreFromItem());
	    this.getFormHM().put("adjustEvalGradeStep", this.getAdjustEvalGradeStep());
	    this.getFormHM().put("showEvalDirector", this.getShowEvalDirector());
	    this.getFormHM().put("verifySameScore", this.getVerifySameScore());
	    this.getFormHM().put("showGrpOrder", this.getShowGrpOrder());
	    this.getFormHM().put("allowAdjustEvalResult", this.getAllowAdjustEvalResult());
	    this.getFormHM().put("adjustEvalRange", this.getAdjustEvalRange());
	    this.getFormHM().put("adjustEvalDegreeType", this.getAdjustEvalDegreeType());
		this.getFormHM().put("adjustEvalDegreeNum", this.getAdjustEvalDegreeNum());
		this.getFormHM().put("calcMenScoreRefDept", this.getCalcMenScoreRefDept());
	    this.getFormHM().put("copy_khmainbody_pri_title", this.getCopy_khmainbody_pri_title());		
	    this.getFormHM().put("plan_visibility", this.getPlan_visibility());	
	    this.getFormHM().put("planSelect", this.getPlanSelect());	
	    this.getFormHM().put("tempTemplateId", this.getTempTemplateId());	
	    this.getFormHM().put("bodyTypeIds", this.getBodyTypeIds());	
	    this.getFormHM().put("paramOper", this.getParamOper());	
	    this.getFormHM().put("qname", this.getQname());	
	    this.getFormHM().put("qmethod", this.getQmethod());	
	    this.getFormHM().put("qobject_type", this.getQobject_type());	
	    this.getFormHM().put("templateType", this.getTemplateType());	
	    this.getFormHM().put("isLimitPointValue", this.getIsLimitPointValue());	
	    this.getFormHM().put("targetDefineItem", this.getTargetDefineItem());
	    this.getFormHM().put("targetDefineItemList", this.getTargetDefineItemList());
		this.getFormHM().put("timeInterval", this.getTimeInterval());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("spStatus", this.getSpStatus());
		this.getFormHM().put("startDate", this.getStartDate());
		this.getFormHM().put("endDate", this.getEndDate());
		this.getFormHM().put("model", this.getModel());
		this.getFormHM().put("khplanvo", this.getExamPlanVo());
		this.getFormHM().put("paramStr", this.getParamStr());
		this.getFormHM().put("bigField", this.getBigField());
		
		this.getFormHM().put("copy_self", this.getCopy_self());
		this.getFormHM().put("copy_khmainbodytype", this.getCopy_khmainbodytype());
		this.getFormHM().put("copy_khobject", this.getCopy_khobject());
		this.getFormHM().put("copy_khmainbody", this.getCopy_khmainbody());
		this.getFormHM().put("copy_khmainbody_pri", this.getCopy_khmainbody_pri());
		this.getFormHM().put("copyResultStr", this.getCopyResultStr());
	
	
		/** 打分控制页参数 */
		this.getFormHM().put("accordPVFlag", this.getAccordPVFlag());
		this.getFormHM().put("dataGatherMode", this.getDataGatherMode());
		this.getFormHM().put("degreeShowType", this.getDegreeShowType());
		this.getFormHM().put("scaleToDegreeRule", this.getScaleToDegreeRule());
		this.getFormHM().put("sameAllScoreNumLess", this.getSameAllScoreNumLess());
		this.getFormHM().put("sameScoreNumLessValue", this.getSameScoreNumLessValue());
		this.getFormHM().put("fineRestrict", this.getFineRestrict());
		this.getFormHM().put("fineMax", this.getFineMax());
		this.getFormHM().put("badlyRestrict", this.getBadlyRestrict());
		this.getFormHM().put("badlyMax", this.getBadlyMax());
		this.getFormHM().put("sameResultsOption", this.getSameResultsOption());
		this.getFormHM().put("noCanSaveDegrees", this.getNoCanSaveDegrees());
		this.getFormHM().put("noCanSaveDegreesList", this.getNoCanSaveDegreesList());
		this.getFormHM().put("blankScoreOption", this.getBlankScoreOption());
		this.getFormHM().put("mailTogoLink", this.getMailTogoLink());
		this.getFormHM().put("radioDirection", this.getRadioDirection());
		this.getFormHM().put("scoreWay", this.getScoreWay());
		this.getFormHM().put("Badly_partRestrict", this.getBadly_partRestrict());
		this.getFormHM().put("Fine_partRestrict", this.getFine_partRestrict());
		this.getFormHM().put("blankScoreUseDegree", this.getBlankScoreUseDegree());
		this.getFormHM().put("grade_template", this.getGrade_template());
		
		this.getFormHM().put("mainbodybodyid", this.getMainbodybodyid());
		this.getFormHM().put("allmainbodybody", this.getAllmainbodybody());
		this.getFormHM().put("mainbodyGradetypeList", this.getMainbodyGradetypeList());
		this.getFormHM().put("wholeEvalMode", this.getWholeEvalMode());
		
		
		/** BS控制参数页面参数 */
		this.getFormHM().put("scoreShowRelatePlan", this.getScoreShowRelatePlan());
		this.getFormHM().put("showIndicatorDesc", this.getShowIndicatorDesc());
		this.getFormHM().put("isBrowse", this.getIsBrowse());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("showOneMark",this.getShowOneMark());
		this.getFormHM().put("idioSummary", this.getIdioSummary());
		this.getFormHM().put("showTotalScoreSort", this.getShowTotalScoreSort());
		this.getFormHM().put("isShowSubmittedPlan", this.getIsShowSubmittedPlan());
		this.getFormHM().put("showNoMarking", this.getShowNoMarking());
		this.getFormHM().put("isEntireysub", this.getIsEntireysub());
		this.getFormHM().put("scoreBySumup", this.getScoreBySumup());
		this.getFormHM().put("accessoriesAffix", this.getAccessoriesAffix());	    //模板附件 臧雪健，20140528添加，中信金属
		this.getFormHM().put("isShowSubmittedScores", this.getIsShowSubmittedScores());
		this.getFormHM().put("selfScoreInDirectLeader", this.getSelfScoreInDirectLeader());
		this.getFormHM().put("scoreNumPerPage", this.getScoreNumPerPage());
		this.getFormHM().put("isShowOrder", this.getIsShowOrder());
		this.getFormHM().put("autoCalcTotalScoreAndOrder", this.getAutoCalcTotalScoreAndOrder());
		this.getFormHM().put("perSet", this.getPerSet());
		this.getFormHM().put("perSetShowMode", this.getPerSetShowMode());
		this.getFormHM().put("perSetStatMode", this.getPerSetStatMode());
		this.getFormHM().put("statCustomMode", this.getStatCustomMode());
		this.getFormHM().put("statStartDate", this.getStatStartDate());
		this.getFormHM().put("statEndDate", this.getStatEndDate());
		this.getFormHM().put("mutiScoreGradeCtl",this.getMutiScoreGradeCtl());
		this.getFormHM().put("checkGradeRange",this.getCheckGradeRange());
		this.getFormHM().put("mitiScoreMergeSelfEval",this.getMitiScoreMergeSelfEval());
		this.getFormHM().put("noteIdioGoal",this.getNoteIdioGoal());
		this.getFormHM().put("selfEvalNotScore",this.getSelfEvalNotScore());
		this.getFormHM().put("showIndicatorContent", this.getShowIndicatorContent());
		this.getFormHM().put("showIndicatorDegree", this.getShowIndicatorDegree());
		this.getFormHM().put("showIndicatorRole", this.getShowIndicatorRole());
		this.getFormHM().put("relatingTargetCard", this.getRelatingTargetCard());
		this.getFormHM().put("showYPTargetCard", this.getShowYPTargetCard());
		this.getFormHM().put("showDeductionCause", this.getShowDeductionCause());
		this.getFormHM().put("canSaveAllObjsScoreSame", this.getCanSaveAllObjsScoreSame());
		this.getFormHM().put("showSumRow", this.getShowSumRow());
		this.getFormHM().put("basicInfoItem", this.getBasicInfoItem());
		this.getFormHM().put("messagelist", this.getMessagelist());
		this.getFormHM().put("showBasicInfo", this.getShowBasicInfo());
		this.getFormHM().put("lockMGradeColumn", this.getLockMGradeColumn().toLowerCase());
		this.getFormHM().put("allowUploadFile", this.getAllowUploadFile());
		this.getFormHM().put("targetCompleteThenGoOn", this.getTargetCompleteThenGoOn());
		this.getFormHM().put("mutiScoreOnePageOnePoint", this.getMutiScoreOnePageOnePoint());
		this.getFormHM().put("showDay", this.getShowDay());
		this.getFormHM().put("showMonth", this.getShowMonth());
		this.getFormHM().put("showWeek", this.getShowWeek());
		this.getFormHM().put("showDayWeekMonth", this.getShowDayWeekMonth());
		this.getFormHM().put("gradeSameNotSubmit", this.getGradeSameNotSubmit());
		this.getFormHM().put("showHistoryScore", this.getShowHistoryScore());
		this.getFormHM().put("evalOutLimitScoreOrg", this.getEvalOutLimitScoreOrg());
		this.getFormHM().put("batchScoreImportFormula", this.getBatchScoreImportFormula());
		
		
		/** 其它参数页面 */
		this.getFormHM().put("warnOpt1", this.getWarnOpt1());
    	this.getFormHM().put("warnOpt2", this.getWarnOpt2());
    	this.getFormHM().put("delayTime1", this.getDelayTime1());
    	this.getFormHM().put("delayTime2", this.getDelayTime2());
    	this.getFormHM().put("roleScope1", this.getRoleScope1());
    	this.getFormHM().put("roleScope1Desc", this.getRoleScope1Desc());    	
    	this.getFormHM().put("roleScope2", this.getRoleScope2());
    	this.getFormHM().put("roleScope2Desc", this.getRoleScope2Desc()); 
		this.getFormHM().put("mustFillWholeEval", this.getMustFillWholeEval());
		this.getFormHM().put("byModel", this.getByModel());
		this.getFormHM().put("wholeEval", this.getWholeEval());
		this.getFormHM().put("evalClass", this.getEvalClass());
		this.getFormHM().put("perGradeSetList", this.getPerGradeSetList());
		this.getFormHM().put("nodeKnowDegree", this.getNodeKnowDegree());
		this.getFormHM().put("showAppraiseExplain", this.getShowAppraiseExplain());
		this.getFormHM().put("gatiShowDegree", this.getGatiShowDegree());
		this.getFormHM().put("performanceType", this.getPerformanceType());
		this.getFormHM().put("descriptiveWholeEval", this.getDescriptiveWholeEval());
		
		/** 目标管理页面 */
		this.getFormHM().put("keyEventEnabled", this.getKeyEventEnabled());	
		this.getFormHM().put("publicPointCannotEdit", this.getPublicPointCannotEdit());	
		this.getFormHM().put("targetMakeSeries",this.getTargetMakeSeries());
		this.getFormHM().put("taskAdjustNeedNew",this.getTaskAdjustNeedNew());
		this.getFormHM().put("taskCanSign",this.getTaskCanSign());
		this.getFormHM().put("taskNeedReview",this.getTaskNeedReview());
		this.getFormHM().put("targetAppMode",this.getTargetAppMode());
		this.getFormHM().put("TargetAllowAdjustAfterApprove", this.getTargetAllowAdjustAfterApprove());
		this.getFormHM().put("allowSeeLowerGrade", this.getAllowSeeLowerGrade());
		this.getFormHM().put("allowLeadAdjustCard", this.getAllowLeadAdjustCard());	
		this.getFormHM().put("evalCanNewPoint", this.getEvalCanNewPoint());
		this.getFormHM().put("targetTraceEnabled", this.getTargetTraceEnabled());
		this.getFormHM().put("targetTraceItem", this.getTargetTraceItem());
		this.getFormHM().put("targetCollectItem", this.getTargetCollectItem());
		this.getFormHM().put("targetCalcItem", this.getTargetCalcItem());
		this.getFormHM().put("targetCollectItemList", this.getTargetCollectItemList());
		this.getFormHM().put("targetCalcItemList", this.getTargetCalcItemList());
		this.getFormHM().put("targetTraceItemList", this.getTargetTraceItemList());   
		this.getFormHM().put("noShowTargetAdjustHistory", this.getNoShowTargetAdjustHistory());   
		this.getFormHM().put("allowLeaderTrace", this.getAllowLeaderTrace());  
		this.getFormHM().put("evalOutLimitStdScore", this.getEvalOutLimitStdScore());
		this.getFormHM().put("showLeaderEval", this.getShowLeaderEval());  
		this.getFormHM().put("processNoVerifyAllScore",this.getProcessNoVerifyAllScore());
		this.getFormHM().put("verifyRule",this.getVerifyRule());
		this.getFormHM().put("showBackTables",this.getShowBackTables());
		this.getFormHM().put("cardList",this.getCardList());
		this.getFormHM().put("showBackTablesInfo",this.getShowBackTablesInfo());
		this.getFormHM().put("mustFillCause",this.getMustFillCause());
		this.getFormHM().put("targetItem",this.getTargetItem());
		this.getFormHM().put("totalAppFormula", this.getTotalAppFormula());
		this.getFormHM().put("tem_point_id", this.getTem_point_id());
		this.getFormHM().put("targetCollectItemMust", this.getTargetCollectItemMust());
		this.getFormHM().put("scrollValue", this.getScrollValue());
		
        this.getFormHM().put("evaluate_str", this.getEvaluate_str());
        this.getFormHM().put("blind_point", this.getBlind_point());
        this.getFormHM().put("dutyRuleid", this.getDutyRuleid());
        this.getFormHM().put("dutyRule", this.getDutyRule());
        this.getFormHM().put("setid", this.getSetid());
        this.getFormHM().put("setdesc", this.getSetdesc());
    }

    @Override
    public void outPutFormHM()
    {
    	
    	this.setAddDescription((String)this.getFormHM().get("addDescription"));
    	this.setExtproList((ArrayList)this.getFormHM().get("extproList"));
    	this.setTem_point_id((String)this.getFormHM().get("tem_point_id"));
    	this.setTotalAppFormula((String)this.getFormHM().get("totalAppFormula"));
    	this.setTemplateId((String)this.getFormHM().get("templateId"));
    	this.setDepartmentLevel((String)this.getFormHM().get("departmentLevel"));
    	this.setPointList((ArrayList)this.getFormHM().get("pointList"));
    	this.setDepartmentLeveList((ArrayList)this.getFormHM().get("departmentLeveList"));
    	this.setTargetUsePrevious((String)this.getFormHM().get("targetUsePrevious"));
    	this.setTargetUsePreviousList((ArrayList)this.getFormHM().get("targetUsePreviousList"));
   	    this.setTaskNameDesc((String)this.getFormHM().get("taskNameDesc"));
    	this.setPerformanceDate((String)this.getFormHM().get("performanceDate"));
    	this.setBusitype((String)this.getFormHM().get("busitype"));
        this.setUpIsValid((String)this.getFormHM().get("upIsValid"));
        this.setDownIsValid((String)this.getFormHM().get("downIsValid"));
        this.setUpDegreeId((String)this.getFormHM().get("upDegreeId"));
    	this.setDownDegreeId((String)this.getFormHM().get("downDegreeId"));    	
    	this.setTargetMustFillItem((String)this.getFormHM().get("targetMustFillItem"));
    	this.setTargetMustFillItemList((ArrayList)this.getFormHM().get("targetMustFillItemList"));
    	this.setAddSubtractType((String)this.getFormHM().get("addSubtractType"));
    	this.setPointEvalType((String)this.getFormHM().get("pointEvalType"));
    	this.setTaskSupportAttach((String)this.getFormHM().get("taskSupportAttach"));
    	this.excludeDegree = (String)this.getFormHM().get("excludeDegree"); // add by 刘蒙
    	this.requiredFieldStr = (String)this.getFormHM().get("requiredFieldStr"); // add by 刘蒙
    	this.requiredField = (List)this.getFormHM().get("requiredField"); // add by 刘蒙
    	   	
    	this.setSpByBodySeq((String)this.getFormHM().get("spByBodySeq"));
    	this.setGradeByBodySeq((String)this.getFormHM().get("gradeByBodySeq"));
    	this.setAllowSeeAllGrade((String)this.getFormHM().get("allowSeeAllGrade"));
    	
	    this.setShowEmployeeRecord((String)this.getFormHM().get("showEmployeeRecord"));	
	    this.setOldTemplate_id((String)this.getFormHM().get("oldTemplate_id"));	
	    this.setSectorTemplate((String)this.getFormHM().get("sectorTemplate"));
	    this.setStatus((String)this.getFormHM().get("status"));
	    this.setNewTemplate_id((String)this.getFormHM().get("newTemplate_id"));
	    this.setTemplate_id((String)this.getFormHM().get("template_id"));
	    this.setTemplate_Name((String)this.getFormHM().get("template_Name"));
	    this.setObjsFromCard((String)this.getFormHM().get("objsFromCard"));
	    this.setMenRefDeptTmpl((String)this.getFormHM().get("menRefDeptTmpl"));
	    this.setBodysFromCard((String)this.getFormHM().get("bodysFromCard"));
	    this.setReaderType((String)this.getFormHM().get("readerType"));
	    this.setScoreFromItem((String)this.getFormHM().get("scoreFromItem"));
	    this.setAdjustEvalGradeStep((String)this.getFormHM().get("adjustEvalGradeStep"));
	    this.setVerifySameScore((String)this.getFormHM().get("verifySameScore"));
	    this.setShowEvalDirector((String)this.getFormHM().get("showEvalDirector"));
	    this.setShowGrpOrder((String)this.getFormHM().get("showGrpOrder"));
	    this.setAllowAdjustEvalResult((String)this.getFormHM().get("allowAdjustEvalResult"));
	    this.setAdjustEvalRange((String)this.getFormHM().get("adjustEvalRange"));
	    this.setAdjustEvalDegreeType((String)this.getFormHM().get("adjustEvalDegreeType"));
	    this.setAdjustEvalDegreeNum((String)this.getFormHM().get("adjustEvalDegreeNum"));
	    this.setCalcMenScoreRefDept((String)this.getFormHM().get("calcMenScoreRefDept"));
	    this.setCopy_khmainbody_pri_title((String)this.getFormHM().get("copy_khmainbody_pri_title"));
	    this.setPlan_visibility((String)this.getFormHM().get("plan_visibility"));
	    this.setCalItemStr((String)this.getFormHM().get("calItemStr"));
	    this.setPlanSelect((String)this.getFormHM().get("planSelect"));
	    this.setTempTemplateId((String)this.getFormHM().get("tempTemplateId"));
	    this.setBodyTypeIds((String)this.getFormHM().get("bodyTypeIds"));
	    this.setParamOper((String)this.getFormHM().get("paramOper"));
	    this.setQname((String)this.getFormHM().get("qname"));
	    this.setQobject_type((String)this.getFormHM().get("qobject_type"));
	    this.setQmethod((String)this.getFormHM().get("qmethod"));
	    this.setTemplateType((String)this.getFormHM().get("templateType"));
	    this.setIsLimitPointValue((String)this.getFormHM().get("isLimitPointValue"));
	    this.setTargetDefineItem((String)this.getFormHM().get("targetDefineItem"));
	    this.setTargetDefineItemList((ArrayList) this.getFormHM().get("targetDefineItemList"));
	    this.setReturnflag((String)this.getFormHM().get("returnflag"));  
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
	
		this.getMainbodytypelistform().setList((ArrayList) this.getFormHM().get("MainbodyTypeList"));
		this.setMainbodytypeList((ArrayList) this.getFormHM().get("MainbodyTypeList"));
	
		this.setEndDate((String) this.getFormHM().get("endDate"));
		this.setStartDate((String) this.getFormHM().get("startDate"));
		this.setSpStatus((String) this.getFormHM().get("spStatus"));
		this.setA_code((String) this.getFormHM().get("a_code"));
		this.setTimeInterval((String) this.getFormHM().get("timeInterval"));
		this.setModel((String) this.getFormHM().get("model"));
		this.setPriv((String) this.getFormHM().get("priv"));
		this.setExamPlanVo((RecordVo) this.getFormHM().get("khplanvo"));
		this.setParamStr((String)this.getFormHM().get("paramStr"));
		this.setBigField((String)this.getFormHM().get("bigField"));
		
		this.setCopy_khmainbody((String) this.getFormHM().get("copy_khmainbody"));
		this.setCopy_khmainbody_pri((String) this.getFormHM().get("copy_khmainbody_pri"));
		this.setCopy_khmainbodytype((String) this.getFormHM().get("copy_khmainbodytype"));
		this.setCopy_khobject((String) this.getFormHM().get("copy_khobject"));
		this.setCopy_self((String) this.getFormHM().get("copy_self"));
		this.setCopyResultStr((String) this.getFormHM().get("copyResultStr"));
		this.setCodeName((String) this.getFormHM().get("codeName"));
		this.setStatusName((String) this.getFormHM().get("statusName"));
		this.setTemplateName((String)this.getFormHM().get("templateName"));
		
		this.setMainbodybodyid((String)this.getFormHM().get("mainbodybodyid"));
		this.setAllmainbodybody((String)this.getFormHM().get("allmainbodybody"));
		this.setMainbodyGradetypeList((ArrayList)this.getFormHM().get("mainbodyGradetypeList"));
		this.setWholeEvalMode((String)this.getFormHM().get("wholeEvalMode"));
		
		
		/** 打分控制页参数 */
		this.setAccordPVFlag((String) this.getFormHM().get("accordPVFlag"));
		this.setDataGatherMode((String) this.getFormHM().get("dataGatherMode"));
		this.setDegreeShowType((String) this.getFormHM().get("degreeShowType"));
		this.setScaleToDegreeRule((String) this.getFormHM().get("scaleToDegreeRule"));
		this.setSameAllScoreNumLess((String) this.getFormHM().get("sameAllScoreNumLess"));
		this.setSameScoreNumLessValue((String) this.getFormHM().get("sameScoreNumLessValue"));
		this.setFineRestrict((String) this.getFormHM().get("fineRestrict"));
		this.setFineMax((String) this.getFormHM().get("fineMax"));
		this.setBadlyRestrict((String) this.getFormHM().get("badlyRestrict"));
		this.setBadlyMax((String) this.getFormHM().get("badlyMax"));
		this.setSameResultsOption((String) this.getFormHM().get("sameResultsOption"));
		this.setNoCanSaveDegrees((String) this.getFormHM().get("noCanSaveDegrees"));
		this.setNoCanSaveDegreesList((ArrayList)this.getFormHM().get("noCanSaveDegreesList"));
		this.setBlankScoreOption((String) this.getFormHM().get("blankScoreOption"));
		this.setMailTogoLink((String) this.getFormHM().get("mailTogoLink"));
		this.setRadioDirection((String) this.getFormHM().get("radioDirection"));
		this.setScoreWay((String) this.getFormHM().get("scoreWay"));
		this.setFine_partRestrict((ArrayList)this.getFormHM().get("Fine_partRestrict"));
		this.setBadly_partRestrict((ArrayList)this.getFormHM().get("Badly_partRestrict"));	
		this.setBlankScoreUseDegree((String) this.getFormHM().get("blankScoreUseDegree"));
		this.setGrade_template((ArrayList)this.getFormHM().get("grade_template"));
		
		/** BS控制参数页面参数 */
		this.setScoreShowRelatePlan((String)this.getFormHM().get("scoreShowRelatePlan"));
		this.setShowIndicatorDesc((String) this.getFormHM().get("showIndicatorDesc"));
		this.setFile((FormFile)this.getFormHM().get("file"));
		this.setIsBrowse((String)this.getFormHM().get("isBrowse"));
		this.setShowOneMark((String) this.getFormHM().get("showOneMark"));
		this.setIdioSummary((String) this.getFormHM().get("idioSummary"));	
		this.setShowTotalScoreSort((String) this.getFormHM().get("showTotalScoreSort"));
		this.setIsShowSubmittedPlan((String) this.getFormHM().get("isShowSubmittedPlan"));
		this.setShowNoMarking((String) this.getFormHM().get("showNoMarking"));
		this.setIsEntireysub((String) this.getFormHM().get("isEntireysub"));
		this.setScoreBySumup((String) this.getFormHM().get("scoreBySumup"));
		this.setIsShowSubmittedScores((String) this.getFormHM().get("isShowSubmittedScores"));
		this.setSelfScoreInDirectLeader((String) this.getFormHM().get("selfScoreInDirectLeader"));
		this.setScoreNumPerPage((String) this.getFormHM().get("scoreNumPerPage"));
		this.setIsShowOrder((String) this.getFormHM().get("isShowOrder"));
		this.setAutoCalcTotalScoreAndOrder((String) this.getFormHM().get("autoCalcTotalScoreAndOrder"));
		this.setPerSet((String) this.getFormHM().get("perSet"));
		this.setPerSetShowMode((String) this.getFormHM().get("perSetShowMode"));
		this.setPerSetStatMode((String) this.getFormHM().get("perSetStatMode"));
		this.setStatCustomMode((String) this.getFormHM().get("statCustomMode"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setStatStartDate((String) this.getFormHM().get("statStartDate"));
		this.setStatEndDate((String) this.getFormHM().get("statEndDate"));
		this.setMutiScoreGradeCtl((String) this.getFormHM().get("mutiScoreGradeCtl"));
		this.setMitiScoreMergeSelfEval((String) this.getFormHM().get("mitiScoreMergeSelfEval"));
		this.setCheckGradeRange((String) this.getFormHM().get("checkGradeRange"));
		this.setNoteIdioGoal((String) this.getFormHM().get("noteIdioGoal"));
		this.setSelfEvalNotScore((String) this.getFormHM().get("selfEvalNotScore"));
		this.setShowIndicatorContent((String) this.getFormHM().get("showIndicatorContent"));
		this.setShowIndicatorDegree((String) this.getFormHM().get("showIndicatorDegree"));
		this.setShowIndicatorRole((String) this.getFormHM().get("showIndicatorRole"));
		this.setRelatingTargetCard((String) this.getFormHM().get("relatingTargetCard"));
		this.setShowDeductionCause((String) this.getFormHM().get("showDeductionCause"));
		this.setMustFillCause((String) this.getFormHM().get("mustFillCause"));
		this.setCanSaveAllObjsScoreSame((String) this.getFormHM().get("canSaveAllObjsScoreSame"));
		this.setShowSumRow((String) this.getFormHM().get("showSumRow"));
		this.setBasicInfoItem((String)this.getFormHM().get("basicInfoItem"));
		this.setMessagelist((ArrayList)this.getFormHM().get("messagelist"));
		this.setShowBasicInfo((String)this.getFormHM().get("showBasicInfo"));
		this.setLockMGradeColumn(((String)this.getFormHM().get("lockMGradeColumn")).toLowerCase());
		this.setAllowUploadFile((String)this.getFormHM().get("allowUploadFile"));
		this.setTargetCompleteThenGoOn((String)this.getFormHM().get("targetCompleteThenGoOn"));
		this.setMutiScoreOnePageOnePoint((String)this.getFormHM().get("mutiScoreOnePageOnePoint"));
		this.setShowDay((String)this.getFormHM().get("showDay"));
		this.setShowWeek((String)this.getFormHM().get("showWeek"));
		this.setShowMonth((String)this.getFormHM().get("showMonth"));
		this.setShowDayWeekMonth((String)this.getFormHM().get("showDayWeekMonth"));
		this.setGradeSameNotSubmit((String)this.getFormHM().get("gradeSameNotSubmit"));
		this.setShowHistoryScore((String)this.getFormHM().get("showHistoryScore"));
		this.setBatchScoreImportFormula((String)this.getFormHM().get("batchScoreImportFormula"));
		
		/** 其它参数页面 */
		this.setWarnOpt1((String)this.getFormHM().get("warnOpt1"));
    	this.setWarnOpt2((String)this.getFormHM().get("warnOpt2"));
    	this.setDelayTime1((String)this.getFormHM().get("delayTime1"));
    	this.setDelayTime2((String)this.getFormHM().get("delayTime2"));
    	this.setRoleScope1((String)this.getFormHM().get("roleScope1"));
    	this.setRoleScope1Desc((String)this.getFormHM().get("roleScope1Desc"));   	
    	this.setRoleScope2((String)this.getFormHM().get("roleScope2"));
    	this.setRoleScope2Desc((String)this.getFormHM().get("roleScope2Desc"));
		this.setMustFillWholeEval((String) this.getFormHM().get("mustFillWholeEval"));
		this.setByModel((String) this.getFormHM().get("byModel"));
		this.setWholeEval((String) this.getFormHM().get("wholeEval"));
		this.setEvalClass((String) this.getFormHM().get("evalClass"));
		this.setPerGradeSetList((ArrayList) this.getFormHM().get("perGradeSetList"));
		this.setNodeKnowDegree((String) this.getFormHM().get("nodeKnowDegree"));
		this.setShowAppraiseExplain((String) this.getFormHM().get("showAppraiseExplain"));
		this.setGatiShowDegree((String) this.getFormHM().get("gatiShowDegree"));
		this.setPerformanceType((String) this.getFormHM().get("performanceType"));
		this.setDescriptiveWholeEval((String) this.getFormHM().get("descriptiveWholeEval"));
		this.setCardList((ArrayList)this.getFormHM().get("cardList"));
		
		
		/** 目标管理 */
		this.setKeyEventEnabled((String) this.getFormHM().get("keyEventEnabled"));
		this.setPublicPointCannotEdit((String) this.getFormHM().get("publicPointCannotEdit"));
		this.setTargetMakeSeries((String) this.getFormHM().get("targetMakeSeries"));
		this.setTaskAdjustNeedNew((String) this.getFormHM().get("taskAdjustNeedNew"));
		this.setTaskCanSign((String) this.getFormHM().get("taskCanSign"));
		this.setTaskNeedReview((String) this.getFormHM().get("taskNeedReview"));
		this.setTargetAppMode((String) this.getFormHM().get("targetAppMode"));
		this.setTargetAllowAdjustAfterApprove((String) this.getFormHM().get("TargetAllowAdjustAfterApprove"));
		this.setAllowLeadAdjustCard((String) this.getFormHM().get("allowLeadAdjustCard"));
		this.setAllowSeeLowerGrade((String) this.getFormHM().get("allowSeeLowerGrade"));	
		this.setEvalCanNewPoint((String) this.getFormHM().get("evalCanNewPoint"));
		this.setTargetTraceEnabled((String) this.getFormHM().get("targetTraceEnabled"));
		this.setTargetTraceItem((String) this.getFormHM().get("targetTraceItem"));
		this.setTargetCollectItem((String) this.getFormHM().get("targetCollectItem"));	   
		this.setTargetCalcItem((String) this.getFormHM().get("targetCalcItem"));		
		this.setTargetCollectItemList((ArrayList) this.getFormHM().get("targetCollectItemList"));
		this.setTargetCalcItemList((ArrayList) this.getFormHM().get("targetCalcItemList"));	
		this.setTargetTraceItemList((ArrayList) this.getFormHM().get("targetTraceItemList"));
		this.setNoShowTargetAdjustHistory((String) this.getFormHM().get("noShowTargetAdjustHistory"));
		this.setAllowLeaderTrace((String) this.getFormHM().get("allowLeaderTrace"));
		this.setEvalOutLimitStdScore((String) this.getFormHM().get("evalOutLimitStdScore"));
		this.setShowLeaderEval((String)this.getFormHM().get("showLeaderEval"));
		this.setProcessNoVerifyAllScore((String) this.getFormHM().get("processNoVerifyAllScore"));
		this.setVerifyRule((String) this.getFormHM().get("verifyRule"));
		this.setShowBackTables((String) this.getFormHM().get("showBackTables"));
		this.setShowBackTablesInfo((String) this.getFormHM().get("showBackTablesInfo"));
		this.setTargetItem((String)this.getFormHM().get("targetItem"));				
		this.setShowYPTargetCard((String)this.getFormHM().get("showYPTargetCard"));
		this.setEvalOutLimitScoreOrg((String)this.getFormHM().get("evalOutLimitScoreOrg"));
		this.setTargetCollectItemMust((String)this.getFormHM().get("targetCollectItemMust"));
		this.setScrollValue((String)this.getFormHM().get("scrollValue"));
		
    	this.setEvaluateList((ArrayList)this.getFormHM().get("evaluateList"));
        this.setEvaluate_str((String)this.getFormHM().get("evaluate_str"));
        this.setBlind_point((String)this.getFormHM().get("blind_point"));
        this.setDutyRuleid((String) this.getFormHM().get("dutyRuleid"));
        this.setDutyRule((String) this.getFormHM().get("dutyRule"));
        this.setSetid((String) this.getFormHM().get("setid"));
        this.setSetdesc((String) this.getFormHM().get("setdesc"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/kh_plan/examPlanList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && "link".equals(arg1.getParameter("b_query")))
		    {	
			    this.setA_code("");
			    this.setSpStatus("all");
			    this.setTimeInterval("all");
			    this.setStartDate("");
			    this.setEndDate("");
			    this.setQname("");
			    this.setQmethod("all");
			    this.setQobject_type("all");
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}
		    }
		    // 查询后调到首页 add by lium
		    if ("true".equals(arg1.getParameter("first")) && setlistform.getPagination() != null) {
		    	setlistform.getPagination().firstPage();
		    }
		    //返回时，不让其自动跳到第一页 2013.12.24 pjf
		   /* if (arg0.getPath().equals("/performance/kh_plan/examPlanList") && arg1.getParameter("b_query") != null && arg1.getParameter("b_query").equals("query"))
		    {				    
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}
		    }*/
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	this.setCopy_khmainbody("0");
		this.setCopy_khmainbody_pri("0");
		this.setCopy_khmainbodytype("0");
		this.setCopy_khobject("0");
		this.setCopy_self("0");
		this.setPlan_visibility("0");
		this.setPlan_visibility("0");
		
		if(
			("/performance/kh_plan/kh_params".equals(arg0.getPath()) && arg1.getParameter("b_defTargetItem") != null && "link".equals(arg1.getParameter("b_defTargetItem"))
					 && arg1.getParameter("oper") != null && "close".equals(arg1.getParameter("oper"))) ||
			("/performance/kh_plan/examPlanAdd".equals(arg0.getPath()) && arg1.getParameter("b_add") != null && "link".equals(arg1.getParameter("b_add"))))
			this.setAllowLeaderTrace("0");
		
    	if (("/performance/kh_plan/kh_params".equals(arg0.getPath()) && arg1.getParameter("paramOper") != null && "list".equals(arg1.getParameter("paramOper")))
    			||("/performance/kh_plan/examPlanList".equals(arg0.getPath()) && arg1.getParameter("b_saveparam") != null && "link".equals(arg1.getParameter("b_saveparam"))))
  	    {	
    		super.reset(arg0, arg1);
    		this.setMustFillWholeEval("0");
    		this.setTaskAdjustNeedNew("0");    		
    		this.setBadlyRestrict("0");
    		this.setFineRestrict("0");
    		this.setShowIndicatorDesc("0");
    		this.setShowTotalScoreSort("0");
    		this.setIsShowSubmittedPlan("0");
    		this.setShowNoMarking("0");
    		this.setIsEntireysub("0");
    		this.setScoreBySumup("0");
    		this.setIsShowSubmittedScores("0");
    		this.setSelfScoreInDirectLeader("0");
    		this.setScoreNumPerPage("0");
    		this.setIsShowOrder("0");
    		this.setAutoCalcTotalScoreAndOrder("0");
    		this.setWholeEval("0");
    		this.setNodeKnowDegree("0");
    		this.setShowAppraiseExplain("0");
    		this.setGatiShowDegree("0");
    		this.setPerformanceType("0");
    		this.setStatCustomMode("0");
    		this.setIdioSummary("0");
    		this.setShowOneMark("0");
    		this.setMutiScoreGradeCtl("0");
    		this.setKeyEventEnabled("0");
    		this.setMitiScoreMergeSelfEval("0");
    		this.setNoteIdioGoal("0");
    		this.setDescriptiveWholeEval("0");
    		this.setSelfEvalNotScore("0");
    		this.setPublicPointCannotEdit("0");
    		this.setTaskCanSign("0");
    		this.setTaskNeedReview("0");
    		this.setShowIndicatorContent("0");
    		this.setShowIndicatorDegree("0");
    		this.setShowIndicatorRole("0");
    	//	this.setRelatingTargetCard("0");
    		this.setShowDeductionCause("0");
    		this.setMustFillCause("0");
    		this.setTargetAllowAdjustAfterApprove("0");
    		this.setAllowLeadAdjustCard("0");
    		this.setAllowSeeLowerGrade("0");
    		this.setCanSaveAllObjsScoreSame("0");
    		this.setShowSumRow("0");
    		this.setEvalCanNewPoint("0");
    		this.setTargetTraceEnabled("0");
    		this.setNoShowTargetAdjustHistory("0");    
    		this.setEvalOutLimitStdScore("0");
    		this.setProcessNoVerifyAllScore("0");
    		this.setIsLimitPointValue("0");
    		this.setShowLeaderEval("0");
    		this.setAllowAdjustEvalResult("0");
    		this.setCalcMenScoreRefDept("0");
    		this.setShowGrpOrder("0");
    		this.setVerifySameScore("0");
    		this.setShowEvalDirector("0");
    		this.setScoreFromItem("0");
    		this.setBodysFromCard("0");
    		this.setObjsFromCard("0");
    		this.setShowEmployeeRecord("0");
    		this.setShowDay("0");
    		this.setShowWeek("0");
    		this.setShowMonth("0");
    		this.setTaskSupportAttach("0");   		
    		this.setUpIsValid("0");
    		this.setDownIsValid("0");
    		this.setPerformanceDate("0");
    		this.setScoreShowRelatePlan("0");
    		this.setWarnOpt1("0");
    		this.setWarnOpt2("0");
    		this.setSpByBodySeq("0");
    		this.setGradeByBodySeq("0");
    		this.setAllowSeeAllGrade("0");
    		this.setByModel("0");
    		this.setShowBasicInfo("0");
    		this.setLockMGradeColumn("0");
    		this.setAllowUploadFile("0");
    		this.setTargetCompleteThenGoOn("0");
    		this.setMutiScoreOnePageOnePoint("0");
    		this.setShowYPTargetCard("0");
    		this.setGradeSameNotSubmit("0");
    		this.setShowHistoryScore("0");
    		this.setMainbodybodyid("");
    		this.setAllmainbodybody("");
    		this.setWholeEvalMode("0");
    		this.setEvalOutLimitScoreOrg("0");
    		this.setScrollValue("0");
    		this.setBatchScoreImportFormula("0");
    		this.setDutyRuleid("0");
  	    }	
    }



	public String getTargetCompleteThenGoOn() {
		return targetCompleteThenGoOn;
	}

	public void setTargetCompleteThenGoOn(String targetCompleteThenGoOn) {
		this.targetCompleteThenGoOn = targetCompleteThenGoOn;
	}

	public ArrayList getTargetMustFillItemList() {
		return targetMustFillItemList;
	}

	public void setTargetMustFillItemList(ArrayList targetMustFillItemList) {
		this.targetMustFillItemList = targetMustFillItemList;
	}

	public String getTargetMustFillItem() {
		return targetMustFillItem;
	}

	public void setTargetMustFillItem(String targetMustFillItem) {
		this.targetMustFillItem = targetMustFillItem;
	}

	public String getAutoCalcTotalScoreAndOrder(){
		return autoCalcTotalScoreAndOrder;
    }

    public void setAutoCalcTotalScoreAndOrder(String autoCalcTotalScoreAndOrder)
    {

	this.autoCalcTotalScoreAndOrder = autoCalcTotalScoreAndOrder;
    }

    public String getBadlyMax()
    {

	return badlyMax;
    }

    public void setBadlyMax(String badlyMax)
    {

	this.badlyMax = badlyMax;
    }

    public String getBadlyRestrict()
    {

	return badlyRestrict;
    }

    public void setBadlyRestrict(String badlyRestrict)
    {

	this.badlyRestrict = badlyRestrict;
    }

    public String getBlankScoreOption()
    {

	return blankScoreOption;
    }

    public void setBlankScoreOption(String blankScoreOption)
    {

	this.blankScoreOption = blankScoreOption;
    }

    public String getDataGatherMode()
    {

	return dataGatherMode;
    }

    public void setDataGatherMode(String dataGatherMode)
    {

	this.dataGatherMode = dataGatherMode;
    }

    public String getFineMax()
    {

	return fineMax;
    }

    public void setFineMax(String fineMax)
    {

	this.fineMax = fineMax;
    }

    public String getFineRestrict()
    {

	return fineRestrict;
    }

    public void setFineRestrict(String fineRestrict)
    {

	this.fineRestrict = fineRestrict;
    }

    public String getGatiShowDegree()
    {

	return gatiShowDegree;
    }

    public void setGatiShowDegree(String gatiShowDegree)
    {

	this.gatiShowDegree = gatiShowDegree;
    }

    public String getIsEntireysub()
    {

	return isEntireysub;
    }

    public void setIsEntireysub(String isEntireysub)
    {

	this.isEntireysub = isEntireysub;
    }

    public String getIsShowOrder()
    {

	return isShowOrder;
    }

    public void setIsShowOrder(String isShowOrder)
    {

	this.isShowOrder = isShowOrder;
    }

    public String getIsShowSubmittedPlan()
    {

	return isShowSubmittedPlan;
    }

    public void setIsShowSubmittedPlan(String isShowSubmittedPlan)
    {

	this.isShowSubmittedPlan = isShowSubmittedPlan;
    }

    public String getIsShowSubmittedScores()
    {

	return isShowSubmittedScores;
    }

    public void setIsShowSubmittedScores(String isShowSubmittedScores)
    {

	this.isShowSubmittedScores = isShowSubmittedScores;
    }

    public String getNodeKnowDegree()
    {

	return nodeKnowDegree;
    }

    public void setNodeKnowDegree(String nodeKnowDegree)
    {

	this.nodeKnowDegree = nodeKnowDegree;
    }

    public String getPerformanceType()
    {

	return performanceType;
    }

    public void setPerformanceType(String performanceType)
    {

	this.performanceType = performanceType;
    }

    public String getSameResultsOption()
    {

	return sameResultsOption;
    }

    public void setSameResultsOption(String sameResultsOption)
    {

	this.sameResultsOption = sameResultsOption;
    }

    public String getScaleToDegreeRule()
    {

	return scaleToDegreeRule;
    }

    public void setScaleToDegreeRule(String scaleToDegreeRule)
    {

	this.scaleToDegreeRule = scaleToDegreeRule;
    }

    public String getScoreBySumup()
    {

	return scoreBySumup;
    }

    public String getScoreNumPerPage()
    {

	return scoreNumPerPage;
    }
    
    public File getAccessoriesAffix() 
    {
    	
	return accessoriesAffix;
	}

	public void setAccessoriesAffix(File accessoriesAffix) 
	{
		
	this.accessoriesAffix = accessoriesAffix;
	}

    public void setScoreNumPerPage(String scoreNumPerPage)
    {

	this.scoreNumPerPage = scoreNumPerPage;
    }

    public String getSelfScoreInDirectLeader()
    {

	return selfScoreInDirectLeader;
    }

    public void setSelfScoreInDirectLeader(String selfScoreInDirectLeader)
    {

	this.selfScoreInDirectLeader = selfScoreInDirectLeader;
    }

    public String getShowAppraiseExplain()
    {

	return showAppraiseExplain;
    }

    public void setShowAppraiseExplain(String showAppraiseExplain)
    {

	this.showAppraiseExplain = showAppraiseExplain;
    }

    public String getShowIndicatorDesc()
    {

	return showIndicatorDesc;
    }

    public void setShowIndicatorDesc(String showIndicatorDesc)
    {

	this.showIndicatorDesc = showIndicatorDesc;
    }

    public String getShowNoMarking()
    {

	return showNoMarking;
    }

    public void setShowNoMarking(String showNoMarking)
    {

	this.showNoMarking = showNoMarking;
    }

    public String getShowTotalScoreSort()
    {

	return showTotalScoreSort;
    }

    public void setShowTotalScoreSort(String showTotalScoreSort)
    {

	this.showTotalScoreSort = showTotalScoreSort;
    }

    public String getPerSet()
    {

	return perSet;
    }

    public void setPerSet(String perSet)
    {

	this.perSet = perSet;
    }

    public String getWholeEval()
    {

	return wholeEval;
    }

    public void setWholeEval(String wholeEval)
    {

	this.wholeEval = wholeEval;
    }

    public String getEndDate()
    {

	return endDate;
    }

    public String getCopyResultStr()
    {

	return copyResultStr;
    }

    public void setCopyResultStr(String copyResultStr)
    {

	this.copyResultStr = copyResultStr;
    }

    public void setEndDate(String endDate)
    {

	this.endDate = endDate;
    }

    public RecordVo getExamPlanVo()
    {

	return examPlanVo;
    }

    public void setExamPlanVo(RecordVo examPlanVo)
    {

	this.examPlanVo = examPlanVo;
    }

    public String getSpStatus()
    {

	return spStatus;
    }

    public void setSpStatus(String spStatus)
    {

	this.spStatus = spStatus;
    }

    public String getStartDate()
    {

	return startDate;
    }

    public void setStartDate(String startDate)
    {

	this.startDate = startDate;
    }

    public ArrayList getMainbodytypeList()
    {

	return mainbodytypeList;
    }

    public void setMainbodytypeList(ArrayList mainbodytypeList)
    {

	this.mainbodytypeList = mainbodytypeList;
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

    public String getTimeInterval()
    {

	return timeInterval;
    }

    public void setTimeInterval(String timeInterval)
    {

	this.timeInterval = timeInterval;
    }

    public PaginationForm getMainbodytypelistform()
    {

	return mainbodytypelistform;
    }

    public void setMainbodytypelistform(PaginationForm mainbodytypelistform)
    {

	this.mainbodytypelistform = mainbodytypelistform;
    }

    public String getModel()
    {

	return model;
    }

    public void setModel(String model)
    {

	this.model = model;
    }

    public String getA_code()
    {

	return a_code;
    }

    public void setA_code(String a_code)
    {

	this.a_code = a_code;
    }

    public String getPriv()
    {

	return priv;
    }

    public void setPriv(String priv)
    {

	this.priv = priv;
    }

    public String getCopy_khmainbody()
    {

	return copy_khmainbody;
    }

    public void setCopy_khmainbody(String copy_khmainbody)
    {

	this.copy_khmainbody = copy_khmainbody;
    }

    public String getCopy_khmainbody_pri()
    {

	return copy_khmainbody_pri;
    }

    public void setCopy_khmainbody_pri(String copy_khmainbody_pri)
    {

	this.copy_khmainbody_pri = copy_khmainbody_pri;
    }

    public String getCopy_khmainbodytype()
    {

	return copy_khmainbodytype;
    }

    public void setCopy_khmainbodytype(String copy_khmainbodytype)
    {

	this.copy_khmainbodytype = copy_khmainbodytype;
    }

    public String getCopy_khobject()
    {

	return copy_khobject;
    }

    public void setCopy_khobject(String copy_khobject)
    {

	this.copy_khobject = copy_khobject;
    }

    public String getCopy_self()
    {

	return copy_self;
    }

    public void setCopy_self(String copy_self)
    {

	this.copy_self = copy_self;
    }

    public String getCodeName()
    {

	return codeName;
    }

    public void setCodeName(String codeName)
    {

	this.codeName = codeName;
    }

    public String getScoreWay()
    {

	return scoreWay;
    }

    public void setScoreWay(String scoreWay)
    {

	this.scoreWay = scoreWay;
    }

    public String getPerSetShowMode()
    {

	return perSetShowMode;
    }

    public void setPerSetShowMode(String perSetShowMode)
    {

	this.perSetShowMode = perSetShowMode;
    }

    public String getPerSetStatMode()
    {

	return perSetStatMode;
    }

   
    public String getStatCustomMode()
    {

	return statCustomMode;
    }


    public String getIdioSummary()
    {
    
        return idioSummary;
    }

    public void setIdioSummary(String idioSummary)
    {
    
        this.idioSummary = idioSummary;
    }

    public String getShowOneMark()
    {
    
        return showOneMark;
    }

    public void setShowOneMark(String showOneMark)
    {
    
        this.showOneMark = showOneMark;
    }

    public void setPerSetStatMode(String perSetStatMode)
    {
    
        this.perSetStatMode = perSetStatMode;
    }

    public void setStatCustomMode(String statCustomMode)
    {
    
        this.statCustomMode = statCustomMode;
    }

    public void setScoreBySumup(String scoreBySumup)
    {
    
        this.scoreBySumup = scoreBySumup;
    }

    public ArrayList getItemlist()
    {
    
        return itemlist;
    }

    public void setItemlist(ArrayList itemlist)
    {
    
        this.itemlist = itemlist;
    }

    public String getStatusName()
    {
    
        return statusName;
    }

    public void setStatusName(String statusName)
    {
    
        this.statusName = statusName;
    }

    public String getStatEndDate()
    {
    
        return statEndDate;
    }

    public void setStatEndDate(String statEndDate)
    {
    
        this.statEndDate = statEndDate;
    }

    public String getStatStartDate()
    {
    
        return statStartDate;
    }

    public void setStatStartDate(String statStartDate)
    {
    
        this.statStartDate = statStartDate;
    }

    public ArrayList getBadly_partRestrict()
    {
    
        return badly_partRestrict;
    }

    public void setBadly_partRestrict(ArrayList badly_partRestrict)
    {
    
        this.badly_partRestrict = badly_partRestrict;
    }

    public ArrayList getFine_partRestrict()
    {
    
        return fine_partRestrict;
    }

    public void setFine_partRestrict(ArrayList fine_partRestrict)
    {
    
        this.fine_partRestrict = fine_partRestrict;
    }

    public String getIsBrowse()
    {
    
        return isBrowse;
    }

    public void setIsBrowse(String isBrowse)
    {
    
        this.isBrowse = isBrowse;
    }

    public FormFile getFile()
    {
    
        return file;
    }

    public void setFile(FormFile file)
    {
    
        this.file = file;
    }

    public String getTemplateName()
    {
    
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
    
        this.templateName = templateName;
    }

    public String getParamStr()
    {
    
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }

    public String getBigField()
    {
    
        return bigField;
    }

    public void setBigField(String bigField)
    {
    
        this.bigField = bigField;
    }

    public String getCheckGradeRange()
    {
    
        return checkGradeRange;
    }

    public void setCheckGradeRange(String checkGradeRange)
    {
    
        this.checkGradeRange = checkGradeRange;
    }

    public String getMutiScoreGradeCtl()
    {
    
        return mutiScoreGradeCtl;
    }

    public void setMutiScoreGradeCtl(String mutiScoreGradeCtl)
    {
    
        this.mutiScoreGradeCtl = mutiScoreGradeCtl;
    }

    public String getKeyEventEnabled()
    {
    
        return keyEventEnabled;
    }

    public void setKeyEventEnabled(String keyEventEnabled)
    {
    
        this.keyEventEnabled = keyEventEnabled;
    }

    public String getMitiScoreMergeSelfEval()
    {
    
        return mitiScoreMergeSelfEval;
    }

    public void setMitiScoreMergeSelfEval(String mitiScoreMergeSelfEval)
    {
    
        this.mitiScoreMergeSelfEval = mitiScoreMergeSelfEval;
    }

    public String getDegreeShowType()
    {
    
        return degreeShowType;
    }

    public void setDegreeShowType(String degreeShowType)
    {
    
        this.degreeShowType = degreeShowType;
    }

    public String getNoteIdioGoal()
    {
    
        return noteIdioGoal;
    }

    public void setNoteIdioGoal(String noteIdioGoal)
    {
    
        this.noteIdioGoal = noteIdioGoal;
    }

    public String getBlankScoreUseDegree()
    {
    
        return blankScoreUseDegree;
    }

    public void setBlankScoreUseDegree(String blankScoreUseDegree)
    {
    
        this.blankScoreUseDegree = blankScoreUseDegree;
    }

    public String getDescriptiveWholeEval()
    {
    
        return descriptiveWholeEval;
    }

    public void setDescriptiveWholeEval(String descriptiveWholeEval)
    {
    
        this.descriptiveWholeEval = descriptiveWholeEval;
    }

    public String getSelfEvalNotScore()
    {
    
        return selfEvalNotScore;
    }

    public void setSelfEvalNotScore(String selfEvalNotScore)
    {
    
        this.selfEvalNotScore = selfEvalNotScore;
    }

    public String getPublicPointCannotEdit()
    {
    
        return publicPointCannotEdit;
    }

    public void setPublicPointCannotEdit(String publicPointCannotEdit)
    {
    
        this.publicPointCannotEdit = publicPointCannotEdit;
    }

    public ArrayList getGrade_template()
    {
    
        return grade_template;
    }

    public void setGrade_template(ArrayList grade_template)
    {
    
        this.grade_template = grade_template;
    }

    public String getTargetMakeSeries()
    {
    
        return targetMakeSeries;
    }

    public void setTargetMakeSeries(String targetMakeSeries)
    {
    
        this.targetMakeSeries = targetMakeSeries;
    }

    public String getTaskAdjustNeedNew()
    {
    
        return taskAdjustNeedNew;
    }

    public void setTaskAdjustNeedNew(String taskAdjustNeedNew)
    {
    
        this.taskAdjustNeedNew = taskAdjustNeedNew;
    }

    public String getTaskCanSign()
    {
    
        return taskCanSign;
    }

    public void setTaskCanSign(String taskCanSign)
    {
    
        this.taskCanSign = taskCanSign;
    }

    public String getTaskNeedReview()
    {
    
        return taskNeedReview;
    }

    public void setTaskNeedReview(String taskNeedReview)
    {
    
        this.taskNeedReview = taskNeedReview;
    }

    public String getShowIndicatorContent()
    {
    
        return showIndicatorContent;
    }

    public void setShowIndicatorContent(String showIndicatorContent)
    {
    
        this.showIndicatorContent = showIndicatorContent;
    }

    public String getShowIndicatorDegree()
    {
    
        return showIndicatorDegree;
    }

    public void setShowIndicatorDegree(String showIndicatorDegree)
    {
    
        this.showIndicatorDegree = showIndicatorDegree;
    }

    public String getShowIndicatorRole()
    {
    
        return showIndicatorRole;
    }

    public void setShowIndicatorRole(String showIndicatorRole)
    {
    
        this.showIndicatorRole = showIndicatorRole;
    }

    public String getTargetAppMode()
    {
    
        return targetAppMode;
    }

    public void setTargetAppMode(String targetAppMode)
    {
    
        this.targetAppMode = targetAppMode;
    }

    public String getRelatingTargetCard()
    {
    
        return relatingTargetCard;
    }

    public void setRelatingTargetCard(String relatingTargetCard)
    {
    
        this.relatingTargetCard = relatingTargetCard;
    }

    public String getShowDeductionCause()
    {
    
        return showDeductionCause;
    }

    public void setShowDeductionCause(String showDeductionCause)
    {
    
        this.showDeductionCause = showDeductionCause;
    }

    public String getTargetAllowAdjustAfterApprove()
    {
    
        return targetAllowAdjustAfterApprove;
    }

    public void setTargetAllowAdjustAfterApprove(String targetAllowAdjustAfterApprove)
    {
    
        this.targetAllowAdjustAfterApprove = targetAllowAdjustAfterApprove;
    }

    public String getAllowLeadAdjustCard()
    {
    
        return allowLeadAdjustCard;
    }

    public void setAllowLeadAdjustCard(String allowLeadAdjustCard)
    {
    
        this.allowLeadAdjustCard = allowLeadAdjustCard;
    }

    public String getAllowSeeLowerGrade()
    {
    
        return allowSeeLowerGrade;
    }

    public void setAllowSeeLowerGrade(String allowSeeLowerGrade)
    {
    
        this.allowSeeLowerGrade = allowSeeLowerGrade;
    }

    public String getCanSaveAllObjsScoreSame()
    {
    
        return canSaveAllObjsScoreSame;
    }

    public void setCanSaveAllObjsScoreSame(String canSaveAllObjsScoreSame)
    {
    
        this.canSaveAllObjsScoreSame = canSaveAllObjsScoreSame;
    }

    public String getShowSumRow()
    {
    
        return showSumRow;
    }

    public void setShowSumRow(String showSumRow)
    {
    
        this.showSumRow = showSumRow;
    }

    public String getEvalCanNewPoint()
    {
    
        return evalCanNewPoint;
    }

    public void setEvalCanNewPoint(String evalCanNewPoint)
    {
    
        this.evalCanNewPoint = evalCanNewPoint;
    }

    public String getTargetCollectItem()
    {
    
        return targetCollectItem;
    }

    public void setTargetCollectItem(String targetCollectItem)
    {
    
        this.targetCollectItem = targetCollectItem;
    }

    public String getTargetTraceEnabled()
    {
    
        return targetTraceEnabled;
    }

    public void setTargetTraceEnabled(String targetTraceEnabled)
    {
    
        this.targetTraceEnabled = targetTraceEnabled;
    }

    public String getTargetTraceItem()
    {
    
        return targetTraceItem;
    }

    public void setTargetTraceItem(String targetTraceItem)
    {
    
        this.targetTraceItem = targetTraceItem;
    }

    public ArrayList getTargetCollectItemList()
    {
    
        return targetCollectItemList;
    }

    public void setTargetCollectItemList(ArrayList targetCollectItemList)
    {
    
        this.targetCollectItemList = targetCollectItemList;
    }

    public ArrayList getTargetTraceItemList()
    {
    
        return targetTraceItemList;
    }

    public void setTargetTraceItemList(ArrayList targetTraceItemList)
    {
    
        this.targetTraceItemList = targetTraceItemList;
    }

    public String getNoShowTargetAdjustHistory()
    {
    
        return noShowTargetAdjustHistory;
    }

    public void setNoShowTargetAdjustHistory(String noShowTargetAdjustHistory)
    {
    
        this.noShowTargetAdjustHistory = noShowTargetAdjustHistory;
    }

    public String getAllowLeaderTrace()
    {
    
        return allowLeaderTrace;
    }

    public void setAllowLeaderTrace(String allowLeaderTrace)
    {
    
        this.allowLeaderTrace = allowLeaderTrace;
    }

	public String getEvalOutLimitStdScore()
	{
		return evalOutLimitStdScore;
	}

	public void setEvalOutLimitStdScore(String evalOutLimitStdScore)
	{
		this.evalOutLimitStdScore = evalOutLimitStdScore;
	}

	public String getProcessNoVerifyAllScore()
	{
		return processNoVerifyAllScore;
	}

	public void setProcessNoVerifyAllScore(String processNoVerifyAllScore)
	{
		this.processNoVerifyAllScore = processNoVerifyAllScore;
	}

	public String getShowBackTables() {
		return showBackTables;
	}

	public void setShowBackTables(String showBackTables) {
		this.showBackTables = showBackTables;
	}

	public ArrayList getCardList() {
		return cardList;
	}

	public void setCardList(ArrayList cardList) {
		this.cardList = cardList;
	}

	public String getShowBackTablesInfo() {
		return showBackTablesInfo;
	}

	public void setShowBackTablesInfo(String showBackTablesInfo) {
		this.showBackTablesInfo = showBackTablesInfo;
	}

	public String getMustFillCause()
	{
		return mustFillCause;
	}

	public void setMustFillCause(String mustFillCause)
	{
		this.mustFillCause = mustFillCause;
	}

	public String getNoCanSaveDegrees()
	{
		return noCanSaveDegrees;
	}

	public void setNoCanSaveDegrees(String noCanSaveDegrees)
	{
		this.noCanSaveDegrees = noCanSaveDegrees;
	}

	public ArrayList getNoCanSaveDegreesList()
	{
		return noCanSaveDegreesList;
	}

	public void setNoCanSaveDegreesList(ArrayList noCanSaveDegreesList)
	{
		this.noCanSaveDegreesList = noCanSaveDegreesList;
	}

	public String getMustFillWholeEval()
	{
		return mustFillWholeEval;
	}

	public void setMustFillWholeEval(String mustFillWholeEval)
	{
		this.mustFillWholeEval = mustFillWholeEval;
	}

	public String getTargetDefineItem()
	{
		return targetDefineItem;
	}

	public void setTargetDefineItem(String targetDefineItem)
	{
		this.targetDefineItem = targetDefineItem;
	}

	public ArrayList getTargetDefineItemList()
	{
		return targetDefineItemList;
	}

	public void setTargetDefineItemList(ArrayList targetDefineItemList)
	{
		this.targetDefineItemList = targetDefineItemList;
	}

	public String getIsLimitPointValue()
	{
		return isLimitPointValue;
	}

	public void setIsLimitPointValue(String isLimitPointValue)
	{
		this.isLimitPointValue = isLimitPointValue;
	}

	public String getTemplateType()
	{
		return templateType;
	}

	public void setTemplateType(String templateType)
	{
		this.templateType = templateType;
	}

	public String getQmethod()
	{
		return qmethod;
	}

	public void setQmethod(String qmethod)
	{
		this.qmethod = qmethod;
	}

	public String getQname()
	{
		return qname;
	}

	public void setQname(String qname)
	{
		this.qname = qname;
	}

	public String getQobject_type()
	{
		return qobject_type;
	}

	public void setQobject_type(String qobject_type)
	{
		this.qobject_type = qobject_type;
	}

	public String getParamOper()
	{
		return paramOper;
	}

	public void setParamOper(String paramOper)
	{
		this.paramOper = paramOper;
	}

	public String getBodyTypeIds()
	{
		return bodyTypeIds;
	}

	public void setBodyTypeIds(String bodyTypeIds)
	{
		this.bodyTypeIds = bodyTypeIds;
	}

	public String getTempTemplateId()
	{
		return tempTemplateId;
	}

	public void setTempTemplateId(String tempTemplateId)
	{
		this.tempTemplateId = tempTemplateId;
	}

	public String getPlanSelect()
	{
		return planSelect;
	}

	public void setPlanSelect(String planSelect)
	{
		this.planSelect = planSelect;
	}

	public String getShowLeaderEval() {
		return showLeaderEval;
	}

	public void setShowLeaderEval(String showLeaderEval) {
		this.showLeaderEval = showLeaderEval;
	}

	public String getTargetCalcItem() {
		return targetCalcItem;
	}

	public void setTargetCalcItem(String targetCalcItem) {
		this.targetCalcItem = targetCalcItem;
	}

	public ArrayList getTargetCalcItemList() {
		return targetCalcItemList;
	}

	public void setTargetCalcItemList(ArrayList targetCalcItemList) {
		this.targetCalcItemList = targetCalcItemList;
	}

	public String getCalItemStr() {
		return calItemStr;
	}

	public void setCalItemStr(String calItemStr) {
		this.calItemStr = calItemStr;
	}

	public String getPlan_visibility()
	{
		return plan_visibility;
	}

	public void setPlan_visibility(String plan_visibility)
	{
		this.plan_visibility = plan_visibility;
	}

	public String getTargetItem() {
		return targetItem;
	}

	public void setTargetItem(String targetItem) {
		this.targetItem = targetItem;
	}

	public String getCopy_khmainbody_pri_title()
	{
		return copy_khmainbody_pri_title;
	}

	public void setCopy_khmainbody_pri_title(String copy_khmainbody_pri_title)
	{
		this.copy_khmainbody_pri_title = copy_khmainbody_pri_title;
	}

	public String getAccordPVFlag()
	{
		return accordPVFlag;
	}

	public void setAccordPVFlag(String accordPVFlag)
	{
		this.accordPVFlag = accordPVFlag;
	}

	public String getAllowAdjustEvalResult() {
		return allowAdjustEvalResult;
	}

	public void setAllowAdjustEvalResult(String allowAdjustEvalResult) {
		this.allowAdjustEvalResult = allowAdjustEvalResult;
	}

	public String getAdjustEvalRange() {
		return adjustEvalRange;
	}

	public void setAdjustEvalRange(String adjustEvalRange) {
		this.adjustEvalRange = adjustEvalRange;
	}

	public String getAdjustEvalDegreeType() {
		return adjustEvalDegreeType;
	}

	public void setAdjustEvalDegreeType(String adjustEvalDegreeType) {
		this.adjustEvalDegreeType = adjustEvalDegreeType;
	}

	public String getAdjustEvalDegreeNum() {
		return adjustEvalDegreeNum;
	}

	public void setAdjustEvalDegreeNum(String adjustEvalDegreeNum) {
		this.adjustEvalDegreeNum = adjustEvalDegreeNum;
	}

	public String getCalcMenScoreRefDept() {
		return calcMenScoreRefDept;
	}

	public void setCalcMenScoreRefDept(String calcMenScoreRefDept) {
		this.calcMenScoreRefDept = calcMenScoreRefDept;
	}

	public String getShowGrpOrder() {
		return showGrpOrder;
	}

	public void setShowGrpOrder(String showGrpOrder) {
		this.showGrpOrder = showGrpOrder;
	}

	public String getVerifySameScore() {
		return verifySameScore;
	}

	public void setVerifySameScore(String verifySameScore) {
		this.verifySameScore = verifySameScore;
	}

	public String getShowEvalDirector() {
		return showEvalDirector;
	}

	public void setShowEvalDirector(String showEvalDirector) {
		this.showEvalDirector = showEvalDirector;
	}

	public String getAdjustEvalGradeStep() {
		return adjustEvalGradeStep;
	}

	public void setAdjustEvalGradeStep(String adjustEvalGradeStep) {
		this.adjustEvalGradeStep = adjustEvalGradeStep;
	}

	public String getScoreFromItem() {
		return scoreFromItem;
	}

	public void setScoreFromItem(String scoreFromItem) {
		this.scoreFromItem = scoreFromItem;
	}

	public String getReaderType() {
		return readerType;
	}

	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}

	public String getBodysFromCard() {
		return bodysFromCard;
	}

	public void setBodysFromCard(String bodysFromCard) {
		this.bodysFromCard = bodysFromCard;
	}

	public String getMenRefDeptTmpl() {
		return menRefDeptTmpl;
	}

	public void setMenRefDeptTmpl(String menRefDeptTmpl) {
		this.menRefDeptTmpl = menRefDeptTmpl;
	}

	public String getObjsFromCard() {
		return objsFromCard;
	}

	public void setObjsFromCard(String objsFromCard) {
		this.objsFromCard = objsFromCard;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getTemplate_Name() {
		return template_Name;
	}

	public void setTemplate_Name(String template_Name) {
		this.template_Name = template_Name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNewTemplate_id() {
		return newTemplate_id;
	}

	public void setNewTemplate_id(String newTemplate_id) {
		this.newTemplate_id = newTemplate_id;
	}

	public String getSectorTemplate() {
		return sectorTemplate;
	}

	public void setSectorTemplate(String sectorTemplate) {
		this.sectorTemplate = sectorTemplate;
	}

	public String getOldTemplate_id() {
		return oldTemplate_id;
	}

	public void setOldTemplate_id(String oldTemplate_id) {
		this.oldTemplate_id = oldTemplate_id;
	}

	public String getShowEmployeeRecord() {
		return "False";
	}//zhanghua 2018年5月15日 15:59:34 禁用员工日志

	public void setShowEmployeeRecord(String showEmployeeRecord) {
		this.showEmployeeRecord = showEmployeeRecord;
	}

	public String getTaskSupportAttach() {
		return taskSupportAttach;
	}

	public void setTaskSupportAttach(String taskSupportAttach) {
		this.taskSupportAttach = taskSupportAttach;
	}

	public String getPointEvalType() {
		return pointEvalType;
	}

	public void setPointEvalType(String pointEvalType) {
		this.pointEvalType = pointEvalType;
	}

	public String getAddSubtractType() {
		return addSubtractType;
	}

	public void setAddSubtractType(String addSubtractType) {
		this.addSubtractType = addSubtractType;
	}

	public String getUpIsValid() {
		return upIsValid;
	}

	public void setUpIsValid(String upIsValid) {
		this.upIsValid = upIsValid;
	}

	public String getDownIsValid() {
		return downIsValid;
	}

	public void setDownIsValid(String downIsValid) {
		this.downIsValid = downIsValid;
	}

	public String getUpDegreeId() {
		return upDegreeId;
	}

	public void setUpDegreeId(String upDegreeId) {
		this.upDegreeId = upDegreeId;
	}

	public String getDownDegreeId() {
		return downDegreeId;
	}

	public void setDownDegreeId(String downDegreeId) {
		this.downDegreeId = downDegreeId;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getPerformanceDate() {
		return performanceDate;
	}

	public void setPerformanceDate(String performanceDate) {
		this.performanceDate = performanceDate;
	}

	public String getScoreShowRelatePlan() {
		return scoreShowRelatePlan;
	}

	public void setScoreShowRelatePlan(String scoreShowRelatePlan) {
		this.scoreShowRelatePlan = scoreShowRelatePlan;
	}

	public String getTaskNameDesc() {
		return taskNameDesc;
	}

	public void setTaskNameDesc(String taskNameDesc) {
		this.taskNameDesc = taskNameDesc;
	}

	public String getTargetUsePrevious() {
		return targetUsePrevious;
	}

	public void setTargetUsePrevious(String targetUsePrevious) {
		this.targetUsePrevious = targetUsePrevious;
	}

	public ArrayList getTargetUsePreviousList() {
		return targetUsePreviousList;
	}

	public void setTargetUsePreviousList(ArrayList targetUsePreviousList) {
		this.targetUsePreviousList = targetUsePreviousList;
	}

	public String getSameAllScoreNumLess() {
		return sameAllScoreNumLess;
	}

	public void setSameAllScoreNumLess(String sameAllScoreNumLess) {
		this.sameAllScoreNumLess = sameAllScoreNumLess;
	}

	public String getSameScoreNumLessValue() {
		return sameScoreNumLessValue;
	}

	public void setSameScoreNumLessValue(String sameScoreNumLessValue) {
		this.sameScoreNumLessValue = sameScoreNumLessValue;
	}

	public String getWarnOpt1() {
		return warnOpt1;
	}

	public void setWarnOpt1(String warnOpt1) {
		this.warnOpt1 = warnOpt1;
	}

	public String getWarnOpt2() {
		return warnOpt2;
	}

	public void setWarnOpt2(String warnOpt2) {
		this.warnOpt2 = warnOpt2;
	}

	public String getDelayTime1() {
		return delayTime1;
	}

	public void setDelayTime1(String delayTime1) {
		this.delayTime1 = delayTime1;
	}

	public String getDelayTime2() {
		return delayTime2;
	}

	public void setDelayTime2(String delayTime2) {
		this.delayTime2 = delayTime2;
	}

	public String getRoleScope1() {
		return roleScope1;
	}

	public void setRoleScope1(String roleScope1) {
		this.roleScope1 = roleScope1;
	}

	public String getRoleScope2() {
		return roleScope2;
	}

	public void setRoleScope2(String roleScope2) {
		this.roleScope2 = roleScope2;
	}

	public String getRoleScope1Desc() {
		return roleScope1Desc;
	}

	public void setRoleScope1Desc(String roleScope1Desc) {
		this.roleScope1Desc = roleScope1Desc;
	}

	public String getRoleScope2Desc() {
		return roleScope2Desc;
	}

	public void setRoleScope2Desc(String roleScope2Desc) {
		this.roleScope2Desc = roleScope2Desc;
	}

	public String getGradeByBodySeq() {
		return gradeByBodySeq;
	}

	public void setGradeByBodySeq(String gradeByBodySeq) {
		this.gradeByBodySeq = gradeByBodySeq;
	}

	public String getAllowSeeAllGrade() {
		return allowSeeAllGrade;
	}

	public void setAllowSeeAllGrade(String allowSeeAllGrade) {
		this.allowSeeAllGrade = allowSeeAllGrade;
	}

	public String getSpByBodySeq() {
		return spByBodySeq;
	}

	public void setSpByBodySeq(String spByBodySeq) {
		this.spByBodySeq = spByBodySeq;
	}

	public String getByModel() {
		return byModel;
	}

	public void setByModel(String byModel) {
		this.byModel = byModel;
	}

	public ArrayList getMessagelist() {
		return messagelist;
	}

	public void setMessagelist(ArrayList messagelist) {
		this.messagelist = messagelist;
	}

	public String getBasicInfoItem() {
		return basicInfoItem;
	}

	public void setBasicInfoItem(String basicInfoItem) {
		this.basicInfoItem = basicInfoItem;
	}

	public String getShowBasicInfo() {
		return showBasicInfo;
	}

	public void setShowBasicInfo(String showBasicInfo) {
		this.showBasicInfo = showBasicInfo;
	}

	public ArrayList getPerGradeSetList() {
		return perGradeSetList;
	}

	public void setPerGradeSetList(ArrayList perGradeSetList) {
		this.perGradeSetList = perGradeSetList;
	}

	public String getEvalClass() {
		return evalClass;
	}

	public void setEvalClass(String evalClass) {
		this.evalClass = evalClass;
	}

	public String getDepartmentLevel() {
		return departmentLevel;
	}

	public void setDepartmentLevel(String departmentLevel) {
		this.departmentLevel = departmentLevel;
	}

	public ArrayList getDepartmentLeveList() {
		return departmentLeveList;
	}

	public void setDepartmentLeveList(ArrayList departmentLeveList) {
		this.departmentLeveList = departmentLeveList;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public ArrayList getPointList() {
		return pointList;
	}

	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}

	public String getTem_point_id() {
		return tem_point_id;
	}

	public void setTem_point_id(String tem_point_id) {
		this.tem_point_id = tem_point_id;
	}

	public String getTotalAppFormula() {
		return totalAppFormula;
	}

	public void setTotalAppFormula(String totalAppFormula) {
		this.totalAppFormula = totalAppFormula;
	}

	public String getMailTogoLink() {
		return mailTogoLink;
	}

	public void setMailTogoLink(String mailTogoLink) {
		this.mailTogoLink = mailTogoLink;
	}

	public String getLockMGradeColumn() {
		return lockMGradeColumn;
	}

	public void setLockMGradeColumn(String lockMGradeColumn) {
		this.lockMGradeColumn = lockMGradeColumn;
	}

	public String getRadioDirection() {
		return radioDirection;
	}

	public void setRadioDirection(String radioDirection) {
		this.radioDirection = radioDirection;
	}
    
    public String getAllowUploadFile() {
		return allowUploadFile;
	}

	public void setAllowUploadFile(String allowUploadFile) {
		this.allowUploadFile = allowUploadFile;
	}

	public String getMutiScoreOnePageOnePoint() {
		return mutiScoreOnePageOnePoint;
	}

	public void setMutiScoreOnePageOnePoint(String mutiScoreOnePageOnePoint) {
		this.mutiScoreOnePageOnePoint = mutiScoreOnePageOnePoint;
	}

	public String getVerifyRule() {
		return verifyRule;
	}

	public void setVerifyRule(String verifyRule) {
		this.verifyRule = verifyRule;
	}

	public String getShowYPTargetCard() {
		return showYPTargetCard;
	}

	public void setShowYPTargetCard(String showYPTargetCard) {
		this.showYPTargetCard = showYPTargetCard;
	}

	/**
	 * @return the batchScoreImportFormula
	 */
	public String getBatchScoreImportFormula() {
		return batchScoreImportFormula;
	}

	/**
	 * @param batchScoreImportFormula the batchScoreImportFormula to set
	 */
	public void setBatchScoreImportFormula(String batchScoreImportFormula) {
		this.batchScoreImportFormula = batchScoreImportFormula;
	}

	public List getRequiredField() {
		return requiredField;
	}
	
	public void setRequiredField(List requiredField) {
		this.requiredField = requiredField;
	}

	public String getExcludeDegree() {
		return excludeDegree;
	}

	public void setExcludeDegree(String excludeDegree) {
		this.excludeDegree = excludeDegree;
	}

	public String getRequiredFieldStr() {
		return requiredFieldStr;
	}

	public void setRequiredFieldStr(String requiredFieldStr) {
		this.requiredFieldStr = requiredFieldStr;
	}

	public ArrayList getEvaluateList() {
		return evaluateList;
	}

	public void setEvaluateList(ArrayList evaluateList) {
		this.evaluateList = evaluateList;
	}

	public String getEvaluate_str() {
		return evaluate_str;
	}

	public void setEvaluate_str(String evaluateStr) {
		evaluate_str = evaluateStr;
	}

	public String getBlind_point() {
		return blind_point;
	}

	public void setBlind_point(String blindPoint) {
		blind_point = blindPoint;
	}

	public ArrayList getExtproList() {
		return extproList;
	}

	public void setExtproList(ArrayList extproList) {
		this.extproList = extproList;
	}

	public String getAddDescription() {
		return addDescription;
	}

	public void setAddDescription(String addDescription) {
		this.addDescription = addDescription;
	}

	public String getDutyRuleid() {
		return dutyRuleid;
	}

	public void setDutyRuleid(String dutyRuleid) {
		this.dutyRuleid = dutyRuleid;
	}

	public String getDutyRule() {
		return dutyRule;
	}

	public void setDutyRule(String dutyRule) {
		this.dutyRule = dutyRule;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getSetdesc() {
		return setdesc;
	}

	public void setSetdesc(String setdesc) {
		this.setdesc = setdesc;
	}
	
}
