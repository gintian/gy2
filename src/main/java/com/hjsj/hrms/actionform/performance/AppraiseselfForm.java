package com.hjsj.hrms.actionform.performance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 
 * create time:2005-6-20:13:18:24
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class AppraiseselfForm extends FrameForm {

	String insertUpdateFlag="0";//自评提交插入与更新标识
	
	String planNum = "0"; //计划id号

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

	private String pointId = "0"; //要素表id号

	private String score = "0";   //最大分值
	private String outHtml="";	   //输出到页面
	private String message="";	   //提示消息
	private ArrayList pointreturnlist=new ArrayList();		//返回上一次操作
	private PaginationForm appraiseselfForm = new PaginationForm();
	private String wholeEven="";
	private String knowDegree="";
	private String strSQL="select plan_id,name from per_plan where status=? and plan_id in  (select plan_id from per_mainbody where object_id=mainbody_id and 1>2)";
	/**个人总结*/
	private String summary;
	/**是否显示个人汇总标志*/
	private String summaryflag="False";
	/**打分状态*/
	private String status="0";
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public PaginationForm getAppraiseselfForm() {
		return appraiseselfForm;
	}
	public ArrayList getPointreturnlist()
	{
		return this.pointreturnlist;
	}
	
	public void setPointreturnlist(ArrayList pointreturnlist)
	{
		this.pointreturnlist=pointreturnlist;
	}
	public void setLeafPointElementNum(int leafPointElementNum)
	{
		this.leafPointElementNum=leafPointElementNum;
	}
	
	public int getLeafPointElementNum()
	{
		return this.leafPointElementNum;
	}

	public void setAppraiseselfForm(PaginationForm appraiseselfForm) {
		this.appraiseselfForm = appraiseselfForm;
	}
	public void setNowMaxLay(String nowMaxLay)
	{
		this.nowMaxLay=nowMaxLay;
	}
	
	public String getNowMaxlay()
	{
		return this.nowMaxLay;
	}
	
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
		this.setPointreturnlist((ArrayList)this.getFormHM().get("pointreturnlist"));
		this.setPlanNum(this.getFormHM().get("planNum").toString());
		this.setItemwhilelst((ArrayList) this.getFormHM().get("itemwhilelst"));
		this.setOutHtml(this.getFormHM().get("outHtml").toString());
		this.setMessage(this.getFormHM().get("message").toString());
		this.setWholeEven(this.getFormHM().get("wholeEven").toString());
		this.setKnowDegree(this.getFormHM().get("knowDegree").toString());
		this.setStrSQL(this.getFormHM().get("strSQL").toString());
		this.setInsertUpdateFlag(this.getFormHM().get("insertUpdateFlag").toString());
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setSummaryflag((String)this.getFormHM().get("SummaryFlag"));
		this.setStatus((String)this.getFormHM().get("status"));
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
		this.getFormHM().put("pointreturnlist",this.getPointreturnlist());
		this.getFormHM().put("planNum", this.getPlanNum());
		this.getFormHM().put("outHtml",this.getOutHtml());
		this.getFormHM().put("message",this.getMessage());
		this.getFormHM().put("wholeEven",this.getWholeEven());
		this.getFormHM().put("knowDegree",this.getKnowDegree());
		this.getFormHM().put("strSQL",this.getStrSQL());
		this.getFormHM().put("insertUpdateFlag",this.getInsertUpdateFlag());
		this.getFormHM().put("summary",this.getSummary());
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
	public void clearPlan()
	{
		this.getFormHM().put("planNum","0");
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		 if("/selfservice/performance/appraiseself".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null && arg1.getParameter("planId")==null)
	        {
		 	  this.getFormHM().put("planNum","0");
		 	  this.setPlanNum("0");
	        }
		return super.validate(arg0, arg1);
	}
	/**
	 * @return 返回 strSQL。
	 */
	public String getStrSQL() {
		return strSQL;
	}
	/**
	 * @param strSQL 要设置的 strSQL。
	 */
	public void setStrSQL(String strSQL) {
		this.strSQL = strSQL;
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
}