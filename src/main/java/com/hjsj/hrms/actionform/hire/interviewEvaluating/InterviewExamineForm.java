package com.hjsj.hrms.actionform.hire.interviewEvaluating;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class InterviewExamineForm extends FrameForm {
	private PaginationForm interviewExamineListform=new PaginationForm();
	private String linkDesc="";
	private String username="";
    private String codeid="";
	private String extendSql;
	private String orderSql;
	private String dbName="";		//库前缀
	private ArrayList tableColumnsList = new ArrayList();
	private String z0101="";
	
	
	/** **考核打分*** */
	private String z0127="";
	
	private String object_id = "0";
	private String gradeHtml = " ";
	private String isNull = "0";
	private String scoreflag = "1"; // =2混合，=1标度
	private String dataArea = ""; // 各指标的数值范围
	private String mainBodyId = "0"; // 考核主体id
	private String templateId = "0"; // 考核模版id
	
	private String isSelfGrade="block";  //block:替人打分  none：自己打分
	private ArrayList gradeUserList=new ArrayList();  //考官列表
	/**初试复试采用不同的测评表添加**/
	private String hireState="";//招聘人员的测试状态  初试复试


	private String status = "0"; // 权重分值表识 0：分值 1：权重
	private String titleName = "";
	private String    lay="0";           //表头层数
	
	private String examineNeedRecord="";	
	private String examineNeedRecordSet="";
	private String a0100="";
	private ArrayList fieldset=new ArrayList();  
	private ArrayList fieldName=new ArrayList();
	private String commentUserFild="";
	private String commentdateFild="";
	private String sumrise;
	@Override
    public void outPutFormHM() {
		this.setHireState((String) this.getFormHM().get("hireState"));
		this.setSumrise((String)this.getFormHM().get("sumrise"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	    	this.setCommentdateFild((String)this.getFormHM().get("commentdateFild"));
	    	this.setCommentUserFild((String)this.getFormHM().get("commentUserFild"));
	    	this.setFieldName((ArrayList)this.getFormHM().get("fieldName"));
	    	this.setExamineNeedRecord((String)this.getFormHM().get("examineNeedRecord"));
	    	this.setExamineNeedRecordSet((String)this.getFormHM().get("examineNeedRecordSet"));
		this.setZ0101((String)this.getFormHM().get("z0101"));
		this.setGradeUserList((ArrayList)this.getFormHM().get("gradeUserList"));
		this.setIsSelfGrade((String)this.getFormHM().get("isSelfGrade"));
		
		this.setLinkDesc((String)this.getFormHM().get("linkDesc"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setExtendSql((String)this.getFormHM().get("extendSql"));
		this.setOrderSql((String)this.getFormHM().get("orderSql"));
		this.setDbName((String)this.getFormHM().get("dbName"));		
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setTableColumnsList((ArrayList)this.getFormHM().get("tableColumnsList"));
		this.getInterviewExamineListform().setList((ArrayList)this.getFormHM().get("interviewExamineList"));
		/** **考核打分*** */
		this.setZ0127((String)this.getFormHM().get("z0127"));
		
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setGradeHtml((String)this.getFormHM().get("gradeHtml"));
		this.setIsNull((String)this.getFormHM().get("isNull"));
		this.setScoreflag((String)this.getFormHM().get("scoreflag"));
		this.setDataArea((String)this.getFormHM().get("dataArea"));
		this.setMainBodyId((String)this.getFormHM().get("mainBodyId"));
		this.setTemplateId((String)this.getFormHM().get("templateId"));		
		this.setStatus((String)this.getFormHM().get("status"));
		this.setTitleName((String)this.getFormHM().get("titleName"));
		this.setLay((String)this.getFormHM().get("lay"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setFieldset((ArrayList)this.getFormHM().get("fieldset"));
	}
	
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("hireState", this.getHireState());
		this.getFormHM().put("sumrise", this.getSumrise());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("extendSql",this.getExtendSql());
		this.getFormHM().put("orderSql",this.getOrderSql());
//		 选中的集合
		this.getFormHM().put("selectedlist",this.getInterviewExamineListform().getSelectedList());
		this.getFormHM().put("examineNeedRecord", this.getExamineNeedRecord());
		this.getFormHM().put("examineNeedRecordSet", this.getExamineNeedRecordSet());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("fieldset", this.getFieldset());
		this.getFormHM().put("fieldName", this.getFieldName());
		this.getFormHM().put("commentUserFild", this.getCommentUserFild());
		this.getFormHM().put("commentdateFild", this.getCommentdateFild());
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/interviewEvaluating/interviewExamine".equals(arg0.getPath())&&(arg1.getParameter("br_query")!=null))
		{
            /**定位到首页,*/
			this.getInterviewExamineListform().getPagination().firstPage();                
        }

		return super.validate(arg0, arg1);
	}

	public String getCodeid() {
		return codeid;
	}


	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}


	public String getDbName() {
		return dbName;
	}


	public void setDbName(String dbName) {
		this.dbName = dbName;
	}


	public String getExtendSql() {
		return extendSql;
	}


	public void setExtendSql(String extendSql) {
		this.extendSql = extendSql;
	}




	public String getOrderSql() {
		return orderSql;
	}


	public void setOrderSql(String orderSql) {
		this.orderSql = orderSql;
	}


	
	public ArrayList getTableColumnsList() {
		return tableColumnsList;
	}


	public void setTableColumnsList(ArrayList tableColumnsList) {
		this.tableColumnsList = tableColumnsList;
	}

	public PaginationForm getInterviewExamineListform() {
		return interviewExamineListform;
	}


	public void setInterviewExamineListform(PaginationForm interviewExamineListform) {
		this.interviewExamineListform = interviewExamineListform;
	}


	public String getDataArea() {
		return dataArea;
	}


	public void setDataArea(String dataArea) {
		this.dataArea = dataArea;
	}


	public String getGradeHtml() {
		return gradeHtml;
	}


	public void setGradeHtml(String gradeHtml) {
		this.gradeHtml = gradeHtml;
	}


	public String getIsNull() {
		return isNull;
	}


	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}


	public String getMainBodyId() {
		return mainBodyId;
	}


	public void setMainBodyId(String mainBodyId) {
		this.mainBodyId = mainBodyId;
	}

	public String getObject_id() {
		return object_id;
	}


	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}


	public String getScoreflag() {
		return scoreflag;
	}


	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getTemplateId() {
		return templateId;
	}


	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}


	public String getTitleName() {
		return titleName;
	}


	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}


	public String getLay() {
		return lay;
	}


	public void setLay(String lay) {
		this.lay = lay;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getLinkDesc() {
		return linkDesc;
	}


	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}


	public String getZ0127() {
		return z0127;
	}


	public void setZ0127(String z0127) {
		this.z0127 = z0127;
	}


	public ArrayList getGradeUserList() {
		return gradeUserList;
	}


	public void setGradeUserList(ArrayList gradeUserList) {
		this.gradeUserList = gradeUserList;
	}


	public String getIsSelfGrade() {
		return isSelfGrade;
	}


	public void setIsSelfGrade(String isSelfGrade) {
		this.isSelfGrade = isSelfGrade;
	}


	public String getZ0101() {
		return z0101;
	}


	public void setZ0101(String z0101) {
		this.z0101 = z0101;
	}


	public String getExamineNeedRecord()
	{
	
	    return examineNeedRecord;
	}


	public void setExamineNeedRecord(String examineNeedRecord)
	{
	
	    this.examineNeedRecord = examineNeedRecord;
	}


	public String getExamineNeedRecordSet()
	{
	
	    return examineNeedRecordSet;
	}


	public void setExamineNeedRecordSet(String examineNeedRecordSet)
	{
	
	    this.examineNeedRecordSet = examineNeedRecordSet;
	}


	public String getA0100()
	{
	
	    return a0100;
	}


	public void setA0100(String a0100)
	{
	
	    this.a0100 = a0100;
	}


	public ArrayList getFieldset()
	{
	
	    return fieldset;
	}


	public void setFieldset(ArrayList fieldset)
	{
	
	    this.fieldset = fieldset;
	}


	public ArrayList getFieldName()
	{
	
	    return fieldName;
	}


	public void setFieldName(ArrayList fieldName)
	{
	
	    this.fieldName = fieldName;
	}


	public String getCommentUserFild()
	{
	
	    return commentUserFild;
	}


	public void setCommentUserFild(String commentUserFild)
	{
	
	    this.commentUserFild = commentUserFild;
	}


	public String getCommentdateFild()
	{
	
	    return commentdateFild;
	}


	public void setCommentdateFild(String commentdateFild)
	{
	
	    this.commentdateFild = commentdateFild;
	}
	public String getSumrise() {
		return sumrise;
	}


	public void setSumrise(String sumrise) {
		this.sumrise = sumrise;
	}
	public String getHireState() {
		return hireState;
	}


	public void setHireState(String hireState) {
		this.hireState = hireState;
	}

}
