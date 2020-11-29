package com.hjsj.hrms.actionform.performance.objectiveManage;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Hashtable;

public class CopyObjectCardForm extends FrameForm {
	private String opt="1";    // 0:查看  1：操作  2.打分  
	private String model="";   // 1:团对  2:我的目标   3:目标制订  4.目标评估
	private String body_id="";  //考核主体类别
	private String returnURL="";   //返回路径
	private String target="";
	
	private String processing_state_all="0";
	private String itemKind="2";  //编辑的目标的项目属性  1:共性  2：个性
	private String planid="";
	private String planStatus="0";  //计划状态
	private String plan_objectType="2";  // 1:部门  2：人员
	private String status="0";           //　０:分值模版  1:权重模版
	private String editOpt="";   //是否可以进行保存和提交操作  1:可以  0：不可以
	private String object_id="";
	private ArrayList p04FieldList=new ArrayList();
	private ArrayList leafItemList=new ArrayList();  //个性项目列表
	private String cardHtml="";
	private ArrayList taskDescribeList=new ArrayList();
	private ArrayList adjustBeforePointList=new ArrayList();
	
	private String importPoint_value="";     //引入绩效指标值
	private String perPointNoGrade="0";      //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
	private String    noGradeItem="";
	private String isEntireysub="False";     //提交是否必填
	private String objectSpFlag="01";        //快哦啊和对象审批状态
	private String targetDeclare="";         //指标说明脚本
	private String mainbodyScoreStatus="0";   //考核主体打分状态 0:未打分  1：正在编辑  2：已提交
	
	private String desc="";                 
	private String personalComment="";       //个人总结代码
	private String personalComment2="";
	private String summary="";               //个人总结内容
	private String isFile="0";              //个人总结是否有附件
	
	
	private ArrayList summary_planList=new ArrayList();  //个人总结计划列表
	private String    summary_planID="";                 //计划id
	
	private ArrayList summaryFileIdsList=new ArrayList();   //个人总结附件id列表
	private String summaryState="0"; //绩效报告状态
	private String fileName="";            //报告附件名称
	private String rejectCause="";
	private String rejectCauseDesc="";
	private String isUnderLeader="0";  //是否是直接领导
	private String un_functionary="";  //团队负责人
	
	private FormFile file;				 //上传附件
	
	private String currappuser="";
	private String a_p0400="";    //插入当前位置的指标
	private String adjustDesc="";  //变更说明
	private String before_value="";
	private String after_value="";
	private String adjustDate="";  //调整日期
	private String pointContent=""; //任务内容
	
	private String myView="";    //我的回顾
	private String otherView=""; //其他回顾
	private String  isApprove="0";        //是否出现批准按钮
	private String isAdjustPoint="False"; 
	private Hashtable planParam=null;
	private ArrayList planList=new ArrayList();
	
	private String url_p="";
	
	private String allowLeadAdjustCard="False"; //允许领导制定及调整目标卡
	
	private String pendingCode="";   //普天 待办信息在应用系统的唯一标识代号
	
	private String creatCard_mail="false";    //目标卡制作 发送email
	private String evaluateCard_mail="false"; //目标卡评估 发送email
	private String grade_template_id_str="";  //标准标度的 id 值
	
	private String tabIDs="";
	private ArrayList tabList=new ArrayList();
	private String isCard="0";
	private String itemtype;//=0普通指标=1加扣分指标
	private String importPositionField;//页面是否显示引入职责指标=false不显示=true显示
	private PaginationForm positionFieldListForm = new PaginationForm();//岗位职责子集指标列表
	private ArrayList headList = new ArrayList();//岗位职责子集指标列表表头
	private String itemid;//选择导入职责子集指标项目ID
	private String positionID;//考核对象岗位
	private String tableWidth;//表格总宽度
	private String a_code;//UN,UM,应用库前缀
	private String targetTraceEnabled;
	private String targetCollectItem;
	private String isHaveRecord;//职务子集是否有符合条件的指标
	private String alertMessage;
	private String entranceType;//进入入口：=0为正常的从目标管理进入，=1为从首页目标设定我的目标，=2为首页目标设定团队，=3为首页完成情况（我的目标），=4为首页员工目标=5首页目标评分进入
	private ArrayList columnList = new ArrayList();
	private ArrayList mainbodylist = new ArrayList();
	private String  AllowLeaderTrace="false"; //允许领导制定及批准跟踪指标, True(默认) False
	private String  isAllowAppealTrancePoint="false"; //是否允许报批跟踪指标
	private String  isAllowApproveTrancePoint="false";  //是否允许批准跟踪指标。
	private RecordVo per_objectVo=null;
	private ArrayList editableTaskList = new ArrayList();
	private String p0400;
	private String clientName;//客户标识，主要判断联通
	private String appealObjectStr;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("clientName", this.getClientName());
		this.getFormHM().put("p0400",this.getP0400());
		this.getFormHM().put("editableTaskList", this.getEditableTaskList());
		this.getFormHM().put("entranceType", this.getEntranceType());
		this.getFormHM().put("isHaveRecord", this.getIsHaveRecord());
		this.getFormHM().put("alertMessage", this.getAlertMessage());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("tableWidth", this.getTableWidth());
		this.getFormHM().put("positionID", this.getPositionID());
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("headList", this.getHeadList());
		this.getFormHM().put("selectedList",this.positionFieldListForm.getSelectedList());
		this.getFormHM().put("importPositionField", this.getImportPositionField());
		this.getFormHM().put("itemtype", this.getItemtype());
		this.getFormHM().put("adjustBeforePointList",this.getAdjustBeforePointList());
		this.getFormHM().put("fileName",this.getFileName());
		this.getFormHM().put("rejectCause",rejectCause);
		
		this.getFormHM().put("p04FieldList",this.getP04FieldList());
		this.getFormHM().put("importPoint_value", this.getImportPoint_value());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("summary",this.getSummary());
	
		this.getFormHM().put("adjustDesc", this.getAdjustDesc());
		this.getFormHM().put("before_value",this.getBefore_value());
		this.getFormHM().put("after_value",this.getAfter_value());
		this.getFormHM().put("adjustDate", adjustDate);
		this.getFormHM().put("pointContent", pointContent);
		
		this.getFormHM().put("myView",this.getMyView());
		this.getFormHM().put("planid",this.getPlanid());
		this.getFormHM().put("appealObjectStr",this.getAppealObjectStr());
	}

	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	    this.setSummary_planID((String)this.getFormHM().get("summary_planID"));
		this.setSummary_planList((ArrayList)this.getFormHM().get("summary_planList"));
		
		this.setAppealObjectStr((String)this.getFormHM().get("appealObjectStr"));
		this.setPer_objectVo((RecordVo)this.getFormHM().get("per_objectVo"));
		this.setClientName((String)this.getFormHM().get("clientName"));
		this.setP0400((String)this.getFormHM().get("p0400"));
		this.setEditableTaskList((ArrayList)this.getFormHM().get("editableTaskList"));
		this.setIsAllowAppealTrancePoint((String)this.getFormHM().get("isAllowAppealTrancePoint"));
		this.setIsAllowApproveTrancePoint((String)this.getFormHM().get("isAllowApproveTrancePoint"));
		this.setAllowLeaderTrace((String)this.getFormHM().get("AllowLeaderTrace"));
		this.setColumnList((ArrayList)this.getFormHM().get("columnList"));
		this.setMainbodylist((ArrayList)this.getFormHM().get("mainbodylist"));
		this.setEntranceType((String)this.getFormHM().get("entranceType"));
		this.setIsHaveRecord((String)this.getFormHM().get("isHaveRecord"));
		this.setAlertMessage((String)this.getFormHM().get("alertMessage"));
		this.setTargetCollectItem((String)this.getFormHM().get("targetCollectItem"));
		this.setTargetTraceEnabled((String)this.getFormHM().get("targetTraceEnabled"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setTableWidth((String)this.getFormHM().get("tableWidth"));
		this.setPositionID((String)this.getFormHM().get("positionID"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setHeadList((ArrayList)this.getFormHM().get("headList"));
		this.getPositionFieldListForm().setList((ArrayList)this.getFormHM().get("positionField"));
		this.setImportPositionField((String)this.getFormHM().get("importPositionField"));
		this.setItemtype((String)this.getFormHM().get("itemtype"));
		this.setTabIDs((String)this.getFormHM().get("tabIDs"));
		this.setTabList((ArrayList)this.getFormHM().get("tabList"));
		this.setIsCard((String)this.getFormHM().get("isCard"));
		
		this.setProcessing_state_all((String)this.getFormHM().get("processing_state_all"));
		this.setGrade_template_id_str((String)this.getFormHM().get("grade_template_id_str"));
		this.setCreatCard_mail((String)this.getFormHM().get("creatCard_mail"));
		this.setEvaluateCard_mail((String)this.getFormHM().get("evaluateCard_mail"));
		this.setPendingCode((String)this.getFormHM().get("pendingCode"));
		
		this.setAdjustBeforePointList((ArrayList)this.getFormHM().get("adjustBeforePointList"));
		this.setUn_functionary((String)this.getFormHM().get("un_functionary"));
		this.setMainbodyScoreStatus((String)this.getFormHM().get("mainbodyScoreStatus"));
		this.setPlanParam((Hashtable)this.getFormHM().get("planParam"));
		this.setCurrappuser((String)this.getFormHM().get("currappuser"));
		this.setMyView((String)this.getFormHM().get("myView"));
		this.setOtherView((String)this.getFormHM().get("otherView"));
		this.setIsApprove((String)this.getFormHM().get("isApprove"));
		this.setIsAdjustPoint((String)this.getFormHM().get("isAdjustPoint"));
		
		this.setPlanStatus((String)this.getFormHM().get("planStatus"));
		this.setIsUnderLeader((String)this.getFormHM().get("isUnderLeader"));
		this.setRejectCauseDesc((String)this.getFormHM().get("rejectCauseDesc"));
		this.setSummaryFileIdsList((ArrayList)this.getFormHM().get("summaryFileIdsList"));
		this.setSummaryState((String)this.getFormHM().get("summaryState"));
		
		this.setAdjustDesc((String)this.getFormHM().get("adjustDesc"));
		this.setBefore_value((String)this.getFormHM().get("before_value"));
		this.setAfter_value((String)this.getFormHM().get("after_value"));
		this.setAdjustDate((String)this.getFormHM().get("adjustDate"));
		this.setPointContent((String)this.getFormHM().get("pointContent"));
		
		
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setIsFile((String)this.getFormHM().get("isFile"));
		
		
		this.setDesc((String)this.getFormHM().get("desc"));
		this.setItemKind((String)this.getFormHM().get("itemKind"));
		this.setA_p0400((String)this.getFormHM().get("a_p0400"));
		this.setEditOpt((String)this.getFormHM().get("editOpt"));
		this.setBody_id((String)this.getFormHM().get("body_id"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setPlanid((String)this.getFormHM().get("planid"));
		this.setPlan_objectType((String)this.getFormHM().get("plan_objectType"));
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setP04FieldList((ArrayList)this.getFormHM().get("p04FieldList"));
		this.setCardHtml((String)this.getFormHM().get("cardHtml"));
		this.setLeafItemList((ArrayList)this.getFormHM().get("leafItemList"));
		this.setTaskDescribeList((ArrayList)this.getFormHM().get("taskDescribeList"));
		this.setPerPointNoGrade((String)this.getFormHM().get("perPointNoGrade"));
		this.setNoGradeItem((String)this.getFormHM().get("noGradeItem"));
		
		this.setIsEntireysub((String)this.getFormHM().get("isEntireysub"));
		this.setObjectSpFlag((String)this.getFormHM().get("objectSpFlag"));
		this.setPersonalComment((String)this.getFormHM().get("personalComment"));
		this.setPersonalComment2((String)this.getFormHM().get("personalComment2"));
		this.setTargetDeclare((String)this.getFormHM().get("targetDeclare"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		
		this.setAllowLeadAdjustCard((String)this.getFormHM().get("allowLeadAdjustCard"));
		this.setPlanid((String)this.getFormHM().get("planid"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/objectiveManage/import_position_field_list".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/performance/objectiveManage/import_position_field_list".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
			if(this.getPositionFieldListForm()!=null)
				this.getPositionFieldListForm().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	public String getCardHtml() {
		return cardHtml;
	}

	public void setCardHtml(String cardHtml) {
		this.cardHtml = cardHtml;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getPlan_objectType() {
		return plan_objectType;
	}

	public void setPlan_objectType(String plan_objectType) {
		this.plan_objectType = plan_objectType;
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public ArrayList getP04FieldList() {
		return p04FieldList;
	}

	public void setP04FieldList(ArrayList fieldList) {
		p04FieldList = fieldList;
	}

	public ArrayList getLeafItemList() {
		return leafItemList;
	}

	public void setLeafItemList(ArrayList leafItemList) {
		this.leafItemList = leafItemList;
	}

	public ArrayList getTaskDescribeList() {
		return taskDescribeList;
	}

	public void setTaskDescribeList(ArrayList taskDescribeList) {
		this.taskDescribeList = taskDescribeList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImportPoint_value() {
		return importPoint_value;
	}

	public void setImportPoint_value(String importPoint_value) {
		this.importPoint_value = importPoint_value;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPerPointNoGrade() {
		return perPointNoGrade;
	}

	public void setPerPointNoGrade(String perPointNoGrade) {
		this.perPointNoGrade = perPointNoGrade;
	}

	public String getIsEntireysub() {
		return isEntireysub;
	}

	public void setIsEntireysub(String isEntireysub) {
		this.isEntireysub = isEntireysub;
	}

	public String getBody_id() {
		return body_id;
	}

	public void setBody_id(String body_id) {
		this.body_id = body_id;
	}

	public String getEditOpt() {
		return editOpt;
	}

	public void setEditOpt(String editOpt) {
		this.editOpt = editOpt;
	}

	public String getA_p0400() {
		return a_p0400;
	}

	public void setA_p0400(String a_p0400) {
		this.a_p0400 = a_p0400;
	}

	public String getItemKind() {
		return itemKind;
	}

	public void setItemKind(String itemKind) {
		this.itemKind = itemKind;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getObjectSpFlag() {
		return objectSpFlag;
	}

	public void setObjectSpFlag(String objectSpFlag) {
		this.objectSpFlag = objectSpFlag;
	}

	public String getPersonalComment() {
		return personalComment;
	}

	public void setPersonalComment(String personalComment) {
		this.personalComment = personalComment;
	}

	public String getIsFile() {
		return isFile;
	}

	public void setIsFile(String isFile) {
		this.isFile = isFile;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getTargetDeclare() {
		return targetDeclare;
	}

	public void setTargetDeclare(String targetDeclare) {
		this.targetDeclare = targetDeclare;
	}

	public String getPersonalComment2() {
		return personalComment2;
	}

	public void setPersonalComment2(String personalComment2) {
		this.personalComment2 = personalComment2;
	}

	public String getNoGradeItem() {
		return noGradeItem;
	}

	public void setNoGradeItem(String noGradeItem) {
		this.noGradeItem = noGradeItem;
	}

	public String getAdjustDesc() {
		return adjustDesc;
	}

	public void setAdjustDesc(String adjustDesc) {
		this.adjustDesc = adjustDesc;
	}

	public String getAfter_value() {
		return after_value;
	}

	public void setAfter_value(String after_value) {
		this.after_value = after_value;
	}

	public String getBefore_value() {
		return before_value;
	}

	public void setBefore_value(String before_value) {
		this.before_value = before_value;
	}


	public ArrayList getSummaryFileIdsList() {
		return summaryFileIdsList;
	}

	public void setSummaryFileIdsList(ArrayList summaryFileIdsList) {
		this.summaryFileIdsList = summaryFileIdsList;
	}

	public String getSummaryState() {
		return summaryState;
	}

	public void setSummaryState(String summaryState) {
		this.summaryState = summaryState;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}

	public String getRejectCauseDesc() {
		return rejectCauseDesc;
	}

	public void setRejectCauseDesc(String rejectCauseDesc) {
		this.rejectCauseDesc = rejectCauseDesc;
	}

	public String getIsUnderLeader() {
		return isUnderLeader;
	}

	public void setIsUnderLeader(String isUnderLeader) {
		this.isUnderLeader = isUnderLeader;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public String getAdjustDate() {
		return adjustDate;
	}

	public void setAdjustDate(String adjustDate) {
		this.adjustDate = adjustDate;
	}

	public String getPointContent() {
		return pointContent;
	}

	public void setPointContent(String pointContent) {
		this.pointContent = pointContent;
	}

	public String getMyView() {
		return myView;
	}

	public void setMyView(String myView) {
		this.myView = myView;
	}

	public String getOtherView() {
		return otherView;
	}

	public void setOtherView(String otherView) {
		this.otherView = otherView;
	}

	public String getCurrappuser() {
		return currappuser;
	}

	public void setCurrappuser(String currappuser) {
		this.currappuser = currappuser;
	}

	public String getIsApprove() {
		return isApprove;
	}

	public void setIsApprove(String isApprove) {
		this.isApprove = isApprove;
	}

	public Hashtable getPlanParam() {
		return planParam;
	}

	public void setPlanParam(Hashtable planParam) {
		this.planParam = planParam;
	}

	public String getIsAdjustPoint() {
		return isAdjustPoint;
	}

	public void setIsAdjustPoint(String isAdjustPoint) {
		this.isAdjustPoint = isAdjustPoint;
	}

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}

	public String getMainbodyScoreStatus() {
		return mainbodyScoreStatus;
	}

	public void setMainbodyScoreStatus(String mainbodyScoreStatus) {
		this.mainbodyScoreStatus = mainbodyScoreStatus;
	}


	public String getUrl_p() {
		return url_p;
	}


	public void setUrl_p(String url_p) {
		this.url_p = url_p;
	}

	

	public String getUn_functionary() {
		return un_functionary;
	}

	public void setUn_functionary(String un_functionary) {
		this.un_functionary = un_functionary;
	}

	public ArrayList getAdjustBeforePointList() {
		return adjustBeforePointList;
	}

	public void setAdjustBeforePointList(ArrayList adjustBeforePointList) {
		this.adjustBeforePointList = adjustBeforePointList;
	}

	public String getAllowLeadAdjustCard() {
		return allowLeadAdjustCard;
	}

	public void setAllowLeadAdjustCard(String allowLeadAdjustCard) {
		this.allowLeadAdjustCard = allowLeadAdjustCard;
	}

	public String getPendingCode() {
		return pendingCode;
	}

	public void setPendingCode(String pendingCode) {
		this.pendingCode = pendingCode;
	}

	public String getCreatCard_mail() {
		return creatCard_mail;
	}

	public void setCreatCard_mail(String creatCard_mail) {
		this.creatCard_mail = creatCard_mail;
	}

	public String getEvaluateCard_mail() {
		return evaluateCard_mail;
	}

	public void setEvaluateCard_mail(String evaluateCard_mail) {
		this.evaluateCard_mail = evaluateCard_mail;
	}

	public String getGrade_template_id_str() {
		return grade_template_id_str;
	}

	public void setGrade_template_id_str(String grade_template_id_str) {
		this.grade_template_id_str = grade_template_id_str;
	}

	public String getProcessing_state_all() {
		return processing_state_all;
	}

	public void setProcessing_state_all(String processing_state_all) {
		this.processing_state_all = processing_state_all;
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

	public String getIsCard() {
		return isCard;
	}

	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getImportPositionField() {
		return importPositionField;
	}

	public void setImportPositionField(String importPositionField) {
		this.importPositionField = importPositionField;
	}

	public PaginationForm getPositionFieldListForm() {
		return positionFieldListForm;
	}

	public void setPositionFieldListForm(PaginationForm positionFieldListForm) {
		this.positionFieldListForm = positionFieldListForm;
	}

	public ArrayList getHeadList() {
		return headList;
	}

	public void setHeadList(ArrayList headList) {
		this.headList = headList;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getPositionID() {
		return positionID;
	}

	public void setPositionID(String positionID) {
		this.positionID = positionID;
	}

	public String getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getTargetTraceEnabled() {
		return targetTraceEnabled;
	}

	public void setTargetTraceEnabled(String targetTraceEnabled) {
		this.targetTraceEnabled = targetTraceEnabled;
	}

	public String getTargetCollectItem() {
		return targetCollectItem;
	}

	public void setTargetCollectItem(String targetCollectItem) {
		this.targetCollectItem = targetCollectItem;
	}

	public String getIsHaveRecord() {
		return isHaveRecord;
	}

	public void setIsHaveRecord(String isHaveRecord) {
		this.isHaveRecord = isHaveRecord;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getEntranceType() {
		return entranceType;
	}

	public void setEntranceType(String entranceType) {
		this.entranceType = entranceType;
	}

	public ArrayList getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}

	public ArrayList getMainbodylist() {
		return mainbodylist;
	}

	public void setMainbodylist(ArrayList mainbodylist) {
		this.mainbodylist = mainbodylist;
	}

	public String getAllowLeaderTrace() {
		return AllowLeaderTrace;
	}

	public void setAllowLeaderTrace(String allowLeaderTrace) {
		AllowLeaderTrace = allowLeaderTrace;
	}

	public String getIsAllowAppealTrancePoint() {
		return isAllowAppealTrancePoint;
	}

	public void setIsAllowAppealTrancePoint(String isAllowAppealTrancePoint) {
		this.isAllowAppealTrancePoint = isAllowAppealTrancePoint;
	}

	public String getIsAllowApproveTrancePoint() {
		return isAllowApproveTrancePoint;
	}

	public void setIsAllowApproveTrancePoint(String isAllowApproveTrancePoint) {
		this.isAllowApproveTrancePoint = isAllowApproveTrancePoint;
	}

	public RecordVo getPer_objectVo() {
		return per_objectVo;
	}

	public void setPer_objectVo(RecordVo per_objectVo) {
		this.per_objectVo = per_objectVo;
	}

	public String getSummary_planID() {
		return summary_planID;
	}

	public void setSummary_planID(String summary_planID) {
		this.summary_planID = summary_planID;
	}

	public ArrayList getSummary_planList() {
		return summary_planList;
	}

	public void setSummary_planList(ArrayList summary_planList) {
		this.summary_planList = summary_planList;
	}
	

	public ArrayList getEditableTaskList() {
		return editableTaskList;
	}

	public void setEditableTaskList(ArrayList editableTaskList) {
		this.editableTaskList = editableTaskList;
	}

	public String getP0400() {
		return p0400;
	}

	public void setP0400(String p0400) {
		this.p0400 = p0400;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAppealObjectStr() {
		return appealObjectStr;
	}

	public void setAppealObjectStr(String appealObjectStr) {
		this.appealObjectStr = appealObjectStr;
	}



}

