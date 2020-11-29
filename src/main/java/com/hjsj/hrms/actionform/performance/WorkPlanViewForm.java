package com.hjsj.hrms.actionform.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

/**
 * <p>Title:WorkPlanViewForm.java</p>
 * <p>Description:工作纪实</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkPlanViewForm extends FrameForm
{
 
	//2016/1/27 wangjl 我的工作纪实查询条件
	private String searchterm;
	private String searchflag;
	public String getSearchterm() {
		return searchterm;
	}

	public void setSearchterm(String searchterm) {
		this.searchterm = searchterm;
	}
	
	public String getSearchflag() {
		return searchflag;
	}

	public void setSearchflag(String searchflag) {
		this.searchflag = searchflag;
	}

	//=1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结=7个人日报=8团队日报=9个人周报=10团队周报
	private String workType;
	private String state;//=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	private String nbase = "Usr";
	private String workNbase = ""; // 参数中设置的人员库
	private String startime = ""; // 计划或总结的开始时间
	private String a0100 = "";
	private String returnURL = "";   //返回路径
	private String target = "";
	private PaginationForm planListForm = new PaginationForm();
	private String year;	
	private ArrayList yearList = new ArrayList();
	
	private String month;
	private ArrayList monthList = new ArrayList();
	
	private String status;
	private ArrayList statusList = new ArrayList();
	
	private String a0101;	
	private String optPlan; // 对于计划和总结 =0查看，=1可填报，=2按钮置灰 =3审批
	private String userStatus;//0:用户    1：中间领导  2:终极领导   

	// 加过密的参数
	private String mdnbase = "";	
	private String mda0100 = "";
	private String mdopt = "";	
	private String mdp0100 = "";
	
	private ArrayList editContentList =new ArrayList();//新增计划列表（除计划条数外,其他所有可编辑指标）	
	private ArrayList baseInfoList = new ArrayList();//基本信息列表，因为是可变的，用list，两个指标一换行
	private ArrayList recordGradeList = new ArrayList();
	private String recordGradeName = "";
	
	private String planContent = ""; // 计划内容
	private String summaryStr = ""; // 总结内容
	private String planHtml; // 从后台生成html（已保存过的计划内容）
	private String p0100;//计划或总结编号
	private String log_type;//=1计划=2总结
	private ArrayList attachList = new ArrayList();//附件列表
	private String copyToStr;//抄送人员id
	private String codyToName;//抄送人员名字
	private FormFile formFile;//上传附件
	private LazyDynaBean leaderCommandsBean = new LazyDynaBean();
	private String p0113; //领导批示
	private String p0115; //纪实状态
	private String helpScript;//用到脚本中
	private String fileName;//附件名称
	/**
	 * 以下参数，用在新增计划或总结时，计算开始时间和结束时间
	 */
	private String day_num;
	private String week_num;
	private String quarter_num;
	private String month_num;//
	private String year_num;
	
	private String sp_level = ""; // 审批层级
	private String sp_relation = ""; // 审批关系
	private String record_grade = ""; // 纪实评分
	private String addORupdate = ""; // 新增或编辑日志的标志参数 
	private String dbType = "1";
	private String refer_id = ""; // 参考信息登记表
	private String refer_name = ""; // 参考信息登记表名称
	private String print_id = ""; // 打印信息登记表
	private String checkCycleStr = ""; // 表头显示的周期
	private String planP0100 = ""; // 总结显示同期计划的p0100
	private String planCycleStr = ""; // 总结显示同期计划
	private String dailyPlan_attachment = "True"; // 工作计划附件上传功能是否显示
	private String dailySumm_attachment = "True"; // 工作总结附件上传功能是否显示
	private String workLength = "0"; // 工作纪实录入的文字的最小字数
	private String pendingCode = ""; // 
	private String doneFlag = "0"; // 
	
	
	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("searchterm",this.getSearchterm());//2016/1/27 wangjl 我的工作纪实查询条件
		this.getFormHM().put("doneFlag",this.getDoneFlag());
		this.getFormHM().put("workLength",this.getWorkLength());
		this.getFormHM().put("dailyPlan_attachment",this.getDailyPlan_attachment());
		this.getFormHM().put("dailySumm_attachment",this.getDailySumm_attachment());
		this.getFormHM().put("planP0100",this.getPlanP0100());
		this.getFormHM().put("planCycleStr",this.getPlanCycleStr());
		this.getFormHM().put("mdnbase",this.getMdnbase());
		this.getFormHM().put("mda0100",this.getMda0100());
		this.getFormHM().put("mdopt",this.getMdopt());
		this.getFormHM().put("mdp0100",this.getMdp0100());
		this.getFormHM().put("startime",this.getStartime());
		this.getFormHM().put("checkCycleStr",this.getCheckCycleStr());
		this.getFormHM().put("refer_id",this.getRefer_id());
		this.getFormHM().put("refer_name",this.getRefer_name());
		this.getFormHM().put("print_id",this.getPrint_id());
		this.getFormHM().put("dbType",this.getDbType());
		this.getFormHM().put("summaryStr",this.getSummaryStr());
		this.getFormHM().put("addORupdate",this.getAddORupdate());
		this.getFormHM().put("sp_relation",this.getSp_relation());
		this.getFormHM().put("record_grade",this.getRecord_grade());
		this.getFormHM().put("sp_level",this.getSp_level());
		this.getFormHM().put("day_num",this.getDay_num());
		this.getFormHM().put("week_num",this.getWeek_num());
		this.getFormHM().put("quarter_num",this.getQuarter_num());
		this.getFormHM().put("year_num", this.getYear_num());
		this.getFormHM().put("fileName",this.getFileName());
		this.getFormHM().put("p0113",this.getP0113());
		this.getFormHM().put("p0115",this.getP0115());
		this.getFormHM().put("copyToStr", this.getCopyToStr());
		this.getFormHM().put("formFile",this.getFormFile());
	    this.getFormHM().put("log_type",this.getLog_type());
		this.getFormHM().put("workType",this.getWorkType());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("month", this.getMonth());
		this.getFormHM().put("a0101", this.getA0101());
		this.getFormHM().put("status", this.getStatus());
		this.getFormHM().put("selectedList",this.getPlanListForm().getSelectedList());
		this.getFormHM().put("planContent",this.getPlanContent());
		this.getFormHM().put("optPlan",this.getOptPlan());
		this.getFormHM().put("p0100", this.getP0100());
		this.getFormHM().put("editContentList",this.getEditContentList());
		this.getFormHM().put("leaderCommandsBean",this.getLeaderCommandsBean());
		this.getFormHM().put("month_num",this.getMonth_num());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("workNbase", this.getWorkNbase());
		this.getFormHM().put("userStatus", this.getUserStatus());
		this.getFormHM().put("recordGradeName", this.getRecordGradeName());
		
		//this.getFormHM().put("returnflag", ((HashMap)this.getFormHM().get("requestPamaHM")).get("returnflag"));
	}
	
	@Override
    public void outPutFormHM()
	{
		
		this.setDoneFlag((String)this.getFormHM().get("doneFlag"));
		this.setWorkLength((String)this.getFormHM().get("workLength"));
		this.setDailyPlan_attachment((String)this.getFormHM().get("dailyPlan_attachment"));
		this.setDailySumm_attachment((String)this.getFormHM().get("dailySumm_attachment"));
		this.setPlanP0100((String)this.getFormHM().get("planP0100"));
		this.setPlanCycleStr((String)this.getFormHM().get("planCycleStr"));
		this.setMdnbase((String)this.getFormHM().get("mdnbase"));
		this.setMda0100((String)this.getFormHM().get("mda0100"));
		this.setMdopt((String)this.getFormHM().get("mdopt"));
		this.setMdp0100((String)this.getFormHM().get("mdp0100"));
		this.setStartime((String)this.getFormHM().get("startime"));
		this.setCheckCycleStr((String)this.getFormHM().get("checkCycleStr"));
		this.setRefer_id((String)this.getFormHM().get("refer_id"));
		this.setRefer_name((String)this.getFormHM().get("refer_name"));
		this.setPrint_id((String)this.getFormHM().get("print_id"));
		this.setDbType((String)this.getFormHM().get("dbType"));
		this.setSummaryStr((String)this.getFormHM().get("summaryStr"));
		this.setAddORupdate((String)this.getFormHM().get("addORupdate"));
		this.setSp_relation((String)this.getFormHM().get("sp_relation"));
		this.setRecord_grade((String)this.getFormHM().get("record_grade"));
		this.setSp_level((String)this.getFormHM().get("sp_level"));
		this.setDay_num((String)this.getFormHM().get("day_num"));
		this.setWeek_num((String)this.getFormHM().get("week_num"));
		this.setQuarter_num((String)this.getFormHM().get("quarter_num"));
		this.setYear_num((String)this.getFormHM().get("year_num"));
		this.setMonth_num((String)this.getFormHM().get("month_num"));
		this.setHelpScript((String)this.getFormHM().get("helpScript"));
		this.setP0113((String)this.getFormHM().get("p0113"));
		this.setP0115((String)this.getFormHM().get("p0115"));
		this.setLeaderCommandsBean((LazyDynaBean)this.getFormHM().get("leaderCommandsBean"));
		this.setCodyToName((String)this.getFormHM().get("codyToName"));
		this.setCopyToStr((String)this.getFormHM().get("copyToStr"));
		this.setAttachList((ArrayList)this.getFormHM().get("attachList"));
		this.setLog_type((String)this.getFormHM().get("log_type"));
		this.setState((String)this.getFormHM().get("state"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setWorkType((String)this.getFormHM().get("workType"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.getPlanListForm().setList((ArrayList)this.getFormHM().get("planList"));
		this.setPlanHtml((String)this.getFormHM().get("planHtml"));
		this.setP0100((String)this.getFormHM().get("p0100"));
		this.setEditContentList((ArrayList)this.getFormHM().get("editContentList"));
		this.setBaseInfoList((ArrayList)this.getFormHM().get("baseInfoList"));
		this.setRecordGradeList((ArrayList)this.getFormHM().get("recordGradeList"));
		this.setOptPlan((String)this.getFormHM().get("optPlan"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setWorkNbase((String)this.getFormHM().get("workNbase"));
		this.setUserStatus((String)this.getFormHM().get("userStatus"));	
		this.setRecordGradeName((String)this.getFormHM().get("recordGradeName"));	
		this.setPendingCode((String)this.getFormHM().get("pendingCode"));	
	}
    public String getWorkType()
    {
    	return this.workType;
    }
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	public PaginationForm getPlanListForm() {
		return planListForm;
	}

	public void setPlanListForm(PaginationForm planListForm) {
		this.planListForm = planListForm;
	}
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public ArrayList getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	public String getOptPlan() {
		return optPlan;
	}

	public void setOptPlan(String optPlan) {
		this.optPlan = optPlan;
	}

	public ArrayList getEditContentList() {
		return editContentList;
	}

	public void setEditContentList(ArrayList editContentList) {
		this.editContentList = editContentList;
	}

	public ArrayList getBaseInfoList() {
		return baseInfoList;
	}

	public void setBaseInfoList(ArrayList baseInfoList) {
		this.baseInfoList = baseInfoList;
	}

	public String getPlanContent() {
		return planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	public String getPlanHtml() {
		return planHtml;
	}

	public void setPlanHtml(String planHtml) {
		this.planHtml = planHtml;
	}
	public String getP0100() {
		return p0100;
	}

	public void setP0100(String p0100) {
		this.p0100 = p0100;
	}
	public String getLog_type() {
		return log_type;
	}

	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}
	public ArrayList getAttachList() {
		return attachList;
	}

	public void setAttachList(ArrayList attachList) {
		this.attachList = attachList;
	}

	public String getCopyToStr() {
		return copyToStr;
	}

	public void setCopyToStr(String copyToStr) {
		this.copyToStr = copyToStr;
	}

	public String getCodyToName() {
		return codyToName;
	}

	public void setCodyToName(String codyToName) {
		this.codyToName = codyToName;
	}

	public FormFile getFormFile() {
		return formFile;
	}

	public void setFormFile(FormFile formFile) {
		this.formFile = formFile;
	}
	

	public LazyDynaBean getLeaderCommandsBean() {
		return leaderCommandsBean;
	}

	public void setLeaderCommandsBean(LazyDynaBean leaderCommandsBean) {
		this.leaderCommandsBean = leaderCommandsBean;
	}

	public String getP0113() {
		return p0113;
	}

	public void setP0113(String p0113) {
		this.p0113 = p0113;
	}
	public String getHelpScript() {
		return helpScript;
	}

	public void setHelpScript(String helpScript) {
		this.helpScript = helpScript;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMonth_num() {
		return month_num;
	}
	public void setMonth_num(String month_num) {
		this.month_num = month_num;
	}
	public String getDay_num() {
		return day_num;
	}
	public void setDay_num(String day_num) {
		this.day_num = day_num;
	}
	public String getWeek_num() {
		return week_num;
	}
	public void setWeek_num(String week_num) {
		this.week_num = week_num;
	}
	public String getQuarter_num() {
		return quarter_num;
	}
	public void setQuarter_num(String quarter_num) {
		this.quarter_num = quarter_num;
	}
	public String getYear_num() {
		return year_num;
	}
	public void setYear_num(String year_num) {
		this.year_num = year_num;
	}
	public String getSp_relation() {
		return sp_relation;
	}
	public void setSp_relation(String sp_relation) {
		this.sp_relation = sp_relation;
	}
    
	public String getRecord_grade() {
		return record_grade;
	}

	public void setRecord_grade(String record_grade) {
		this.record_grade = record_grade;
	}

	public String getSp_level() {
		return sp_level;
	}

	public void setSp_level(String sp_level) {
		this.sp_level = sp_level;
	}

	public String getAddORupdate() {
		return addORupdate;
	}

	public void setAddORupdate(String addORupdate) {
		this.addORupdate = addORupdate;
	}

	public String getSummaryStr() {
		return summaryStr;
	}

	public void setSummaryStr(String summaryStr) {
		this.summaryStr = summaryStr;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getRefer_id() {
		return refer_id;
	}

	public void setRefer_id(String refer_id) {
		this.refer_id = refer_id;
	}

	public String getPrint_id() {
		return print_id;
	}

	public void setPrint_id(String print_id) {
		this.print_id = print_id;
	}

	public String getCheckCycleStr() {
		return checkCycleStr;
	}

	public void setCheckCycleStr(String checkCycleStr) {
		this.checkCycleStr = checkCycleStr;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) { 
		this.returnURL =PubFunc.hireKeyWord_filter_reback(returnURL);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getRefer_name() {
		return refer_name;
	}

	public void setRefer_name(String refer_name) {
		this.refer_name = refer_name;
	}

	public String getWorkNbase() {
		return workNbase;
	}

	public void setWorkNbase(String workNbase) {
		this.workNbase = workNbase;
	}

	public String getP0115() {
		return p0115;
	}

	public void setP0115(String p0115) {
		this.p0115 = p0115;
	}

	public String getMdnbase() {
		return mdnbase;
	}

	public void setMdnbase(String mdnbase) {
		this.mdnbase = mdnbase;
	}

	public String getMda0100() {
		return mda0100;
	}

	public void setMda0100(String mda0100) {
		this.mda0100 = mda0100;
	}

	public String getMdopt() {
		return mdopt;
	}

	public void setMdopt(String mdopt) {
		this.mdopt = mdopt;
	}

	public String getMdp0100() {
		return mdp0100;
	}

	public void setMdp0100(String mdp0100) {
		this.mdp0100 = mdp0100;
	}

	public String getPlanP0100() {
		return planP0100;
	}

	public void setPlanP0100(String planP0100) {
		this.planP0100 = planP0100;
	}

	public String getPlanCycleStr() {
		return planCycleStr;
	}

	public void setPlanCycleStr(String planCycleStr) {
		this.planCycleStr = planCycleStr;
	}

	public String getDailyPlan_attachment() {
		return dailyPlan_attachment;
	}

	public void setDailyPlan_attachment(String dailyPlan_attachment) {
		this.dailyPlan_attachment = dailyPlan_attachment;
	}

	public String getDailySumm_attachment() {
		return dailySumm_attachment;
	}

	public void setDailySumm_attachment(String dailySumm_attachment) {
		this.dailySumm_attachment = dailySumm_attachment;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getWorkLength() {
		return workLength;
	}

	public void setWorkLength(String workLength) {
		this.workLength = workLength;
	}

	public ArrayList getRecordGradeList() {
		return recordGradeList;
	}

	public void setRecordGradeList(ArrayList recordGradeList) {
		this.recordGradeList = recordGradeList;
	}

	public String getRecordGradeName() {
		return recordGradeName;
	}

	public void setRecordGradeName(String recordGradeName) {
		this.recordGradeName = recordGradeName;
	}

	public String getPendingCode() {
		return pendingCode;
	}

	public void setPendingCode(String pendingCode) {
		this.pendingCode = pendingCode;
	}

	public String getDoneFlag() {
		return doneFlag;
	}

	public void setDoneFlag(String doneFlag) {
		this.doneFlag = doneFlag;
	}
	
}
