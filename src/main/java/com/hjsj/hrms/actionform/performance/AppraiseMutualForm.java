/*
 * 创建日期 2005-6-25
 *
 */
package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author luangaojiong
 *	互评Form
 */
public class AppraiseMutualForm extends FrameForm {
	private String performanceType="0";			//考核形式  0：绩效考核  1：民主评测
	private String   isFile="0";         // 是否有附件  0：没有   1：有
	String insertUpdateFlag="0";//自评提交插入与更新标识
	String planNum = "0"; //计划id号
	private String planStatus="0";  //计划状态
	String itemid = "0";	//项目号

	String itemName = "0";	//项目名称
	private String pointName="";		//要素表名称
	private String pointKind="";		//要素类型
	private String layer="0";			//项目所在层
	private String nowMaxLay="0";		//项目的最大层	
	private String maxLay="0";			//所有项目最深层
	private int	leafPointElementNum=0;	//项目的要素数量
	
	private ArrayList itemwhilelst = new ArrayList(); //项目ArrayList列表

	private ArrayList pointlst = new ArrayList(); //要素表ArrayList
	private ArrayList pointreturnlist=new ArrayList();		//返回上一次操作
	private String pointId = "0"; //要素表id号

	private String score = "0";   //最大分值
	private String outHtml="";	   //输出到页面
	private String message="";	   //提示消息
	private String objectId="0";  //考核对象id
		
	private String strSQL="SELECT per_mainbody.object_id, per_object.A0101 FROM per_mainbody , per_object where per_mainbody.object_id = per_object.object_id and 1>2";		//SQL语句
	
	private PaginationForm appraiseMutualForm = new PaginationForm();
	private String wholeEven="";
	private String knowDegree="";
	private String strSQL2="select plan_id,name from per_plan where status=? and plan_id in  (select plan_id from per_mainbody where  1>2)";

	/**个人总结*/
	private String summary;
	private ArrayList summaryFileIdsList=new ArrayList();   //个人总结附件id列表
	private String summaryState="0";   //报告提交状态
	/**个人目标*/
	private String goalContext="";   //目标内容
	private String isGoalFile="0";   //是否有上传的附件
	private ArrayList goalFileIdsList=new ArrayList();   //个人目标附件id列表
	private String goalState="0";   //目标提交状态
	
	private String rejectCause="";   //驳回原因
	private String rejectCauseDesc=""; //
	private String isUnderLeader="0";  //是否是直接领导  0：不是  1：是
	
	/**是否显示个人汇总标志*/
	private String summaryflag="False";	
	/**打分状态*/
	private String status="0";
	
	 private String   fileName="";            //报告附件名称
	 private String   goalfileName="";        //目标附件名称
	 private FormFile file;				 //上传附件
	 private FormFile goalfile;				 //上传目标附件
	 private String isSelf="false";
	 
	 private String allowUploadFile="";
	
	private String isnull="";		//判断模板文件是否为空
		 
	 public String getIsnull() {
		return isnull;
	}
	public void setIsnull(String isnull) {
		this.isnull = isnull;
	}

	public String getAllowUploadFile() {
		return allowUploadFile;
	}
	public void setAllowUploadFile(String allowUploadFile) {
		this.allowUploadFile = allowUploadFile;
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
	public String getSummaryflag() {
		return summaryflag;
	}
	public void setSummaryflag(String summaryflag) {
		this.summaryflag = summaryflag;
	}
	/**
	 * @return 返回 insertUpdateFlag。
	 */
	public String getInsertUpdateFlag() {
		return insertUpdateFlag;
	}
	/**
	 * @param insertUpdateFlag 要设置的 insertUpdateFlag。
	 */
	public void setInsertUpdateFlag(String insertUpdateFlag) {
		this.insertUpdateFlag = insertUpdateFlag;
	}
	/**
	 * @return 返回 strSQL2。
	 */
	public String getStrSQL2() {
		return strSQL2;
	}
	/**
	 * @param strSQL2 要设置的 strSQL2。
	 */
	public void setStrSQL2(String strSQL2) {
		this.strSQL2 = strSQL2;
	}
	/**
	 * @return 返回 knowDegree。
	 */
	public String getKnowDegree() {
		return knowDegree;
	}
	/**
	 * @param knowDegree 要设置的 knowDegree。
	 */
	public void setKnowDegree(String knowDegree) {
		this.knowDegree = knowDegree;
	}
	/**
	 * @return 返回 wholeEven。
	 */
	public String getWholeEven() {
		return wholeEven;
	}
	/**
	 * @param wholeEven 要设置的 wholeEven。
	 */
	public void setWholeEven(String wholeEven) {
		this.wholeEven = wholeEven;
	}
	/**
	 * 返回上一次操作属性
	 * @return
	 */

	public ArrayList getPointreturnlist()
	{
		return this.pointreturnlist;
	}
	
	public void setPointreturnlist(ArrayList pointreturnlist)
	{
		this.pointreturnlist=pointreturnlist;
	}
	public PaginationForm getAppraiseMutualForm() {
		return appraiseMutualForm;
	}
	
	/**
	 * 项目要素数量属性
	 * @param leafPointElementNum
	 */
	public void setLeafPointElementNum(int leafPointElementNum)
	{
		this.leafPointElementNum=leafPointElementNum;
	}
	
	public int getLeafPointElementNum()
	{
		return this.leafPointElementNum;
	}
	
	public void setStrSQL(String strSQL)
	{
		this.strSQL=strSQL;
	}
	public String getStrSQL()
	{
		return this.strSQL;
	}
	
	/**
	 * 考核对象id属性
	 * @param objectId
	 */
	
	public void setObjectId(String objectId)
	{
		this.objectId=objectId;
	}
	
	public String getObjectId()
	{
		return this.objectId;
	}
	
	

	public void setAppraiseMutualForm(PaginationForm appraiseMutualForm) {
		this.appraiseMutualForm = appraiseMutualForm;
	}
	/**
	 * 当前项目最大层属性
	 * @param nowMaxLay
	 */
	public void setNowMaxLay(String nowMaxLay)
	{
		this.nowMaxLay=nowMaxLay;
	}
	
	public String getNowMaxlay()
	{
		return this.nowMaxLay;
	}
	
	/**
	 * 所有项目最大层属性
	 * @param maxLay
	 */
	public void setMaxLay(String maxLay)
	{
		this.maxLay=maxLay;
	}
	
	public String getMaxLay()
	{
		return this.maxLay;
	}
	/**
	 * 输出页面String属性
	 * @param outHtml
	 */
	
	public void setOutHtml(String outHtml)
	{
		this.outHtml=outHtml;
	}
	
	public String getOutHtml()
	{
		return this.outHtml;
	}
	/**
	 * 项目所在层属性
	 * @param layer
	 */
	public void setLayer(String layer)
	{
		this.layer=layer;
	}
	
	public String getLayer()
	{
		return this.layer;
	}
	
	/**
	 * 要素类型属性
	 * @param pointKind
	 */
	
	public void setPointKind(String pointKind)
	{
		this.pointKind=pointKind;
	}
	
	public String getPointKind()
	{
		return this.pointKind;
	}
	
	/**
	 * 
	 * 项目表itemid属性
	 */
	public String getItemid() {
		return this.itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	
	/**
	 * 
	 * 要素表名称属性
	 */
	public void setPointName(String pointName)
	{
		this.pointName=pointName;
	}
	
	public String getPointName()
	{
		return this.pointName;
	}
	
	/**
	 * 
	 * 要素表pointid属性
	 */

	public String getPointId() {
		return this.pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}

	/**
	 * 
	 * 要素表分值属性
	 */
	public String getScore() {
		return this.score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	/**
	 * 
	 * 项目名称属性
	 */
	public String getItemName() {
		return this.itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * 
	 * 要素ArrayList属性
	 */
	public ArrayList getPointlst() {
		return this.pointlst;
	}

	public void setPointlst(ArrayList pointlst) {
		this.pointlst = pointlst;
	}

	/**
	 * 
	 * 计划号码属性
	 */

	public String getPlanNum() {
		return this.planNum;
	}

	public void setPlanNum(String planNum) {
		this.planNum = planNum;
	}

	@Override
    public void outPutFormHM() {
		this.setIsSelf((String)this.getFormHM().get("isSelf"));
		this.setPlanStatus((String)this.getFormHM().get("planStatus"));
		this.setIsFile((String)this.getFormHM().get("isFile"));
		this.setPerformanceType((String)this.getFormHM().get("performanceType"));
		this.setPointreturnlist((ArrayList)this.getFormHM().get("pointreturnlist"));
		this.setPlanNum(this.getFormHM().get("planNum").toString());
		this.setOutHtml(this.getFormHM().get("outHtml").toString());
		this.setItemwhilelst((ArrayList) this.getFormHM().get("itemwhilelst"));
		this.setMessage(this.getFormHM().get("message").toString());
		this.setObjectId(this.getFormHM().get("objectId").toString());
		this.setStrSQL(this.getFormHM().get("strSQL").toString());
		this.setWholeEven(this.getFormHM().get("wholeEven").toString());
		this.setKnowDegree(this.getFormHM().get("knowDegree").toString());
		this.setStrSQL2(this.getFormHM().get(" strSQL2").toString());
		this.setInsertUpdateFlag(this.getFormHM().get("insertUpdateFlag").toString());
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setSummaryflag((String)this.getFormHM().get("SummaryFlag"));
		this.setStatus((String)this.getFormHM().get("status"));		
		this.setSummaryFileIdsList((ArrayList)this.getFormHM().get("summaryFileIdsList"));
		this.setGoalFileIdsList((ArrayList)this.getFormHM().get("goalFileIdsList"));
		this.setGoalContext((String)this.getFormHM().get("goalContext"));
		this.setIsGoalFile((String)this.getFormHM().get("isGoalFile"));
		this.setSummaryState((String)this.getFormHM().get("summaryState"));
		this.setGoalState((String)this.getFormHM().get("goalState"));
		
		this.setRejectCauseDesc((String)this.getFormHM().get("rejectCauseDesc"));
		this.setIsUnderLeader((String)this.getFormHM().get("isUnderLeader"));
		this.setAllowUploadFile((String)this.getFormHM().get("allowUploadFile"));
		
		this.setIsnull((String)this.getFormHM().get("isnull"));
	}

	/**
	 * 
	 * 项目表ArrayList属性
	 */

	public void setItemwhilelst(ArrayList itemwhilelst) {
		this.itemwhilelst = itemwhilelst;
	}

	public ArrayList getItemwhilelst() {
		return this.itemwhilelst;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("rejectCause",this.getRejectCause());
		
		this.getFormHM().put("pointreturnlist",this.getPointreturnlist());
		this.getFormHM().put("planNum", this.getPlanNum());
		this.getFormHM().put("outHtml",this.getOutHtml());
		this.getFormHM().put("message",this.getMessage());
		this.getFormHM().put("objectId",this.getObjectId());
		this.getFormHM().put("strSQL",this.getStrSQL());
		this.getFormHM().put("wholeEven",this.getWholeEven());
		this.getFormHM().put("knowDegree",this.getKnowDegree());
		this.getFormHM().put(" strSQL2",this.getStrSQL2());
		this.getFormHM().put("insertUpdateFlag",this.getInsertUpdateFlag());
		this.getFormHM().put("goalfileName",this.getGoalfileName());
		this.getFormHM().put("fileName", this.getFileName());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("goalfile",this.getGoalfile());
		
		this.getFormHM().put("summary",this.getSummary());
		this.getFormHM().put("goalContext",this.getGoalContext());
		this.getFormHM().put("allowUploadFile",this.getAllowUploadFile());
		
		this.getFormHM().put("isnull",this.getIsnull());
	}
	public void setMessage(String message)
	{
		this.message=message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void messageClear()
	{
		this.getFormHM().put("message","");
	}


	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
		return super.validate(arg0, arg1);
	}
	public String getPerformanceType() {
		return performanceType;
	}
	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}
	public String getIsFile() {
		return isFile;
	}
	public void setIsFile(String isFile) {
		this.isFile = isFile;
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
	public String getNowMaxLay() {
		return nowMaxLay;
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
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public FormFile getGoalfile() {
		return goalfile;
	}
	public void setGoalfile(FormFile goalfile) {
		this.goalfile = goalfile;
	}
	public String getIsSelf() {
		return isSelf;
	}
	public void setIsSelf(String isSelf) {
		this.isSelf = isSelf;
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

}
