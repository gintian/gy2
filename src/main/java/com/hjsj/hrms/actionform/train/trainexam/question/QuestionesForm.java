package com.hjsj.hrms.actionform.train.trainexam.question;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class QuestionesForm extends FrameForm{
	
	// 知识点
	private String knowledge;
	
	// 知识点名称 保留值
	private String knowledgeviewvalue;
	
	// 题型
	private String questionType;
	
	// 题型列表
	private ArrayList questionTypeList;
	
	// 难度
	private String difficulty;
	
	// 难度列表
	private ArrayList difficultyList;
	
	// sql语句
	private String strsql;
	
	// where 条件
	private String strwhere;
	
	// sql语句中的列
	private String columns;
	
	// sql中的排序
	private String order;
	
	// 
	private String code;
	
	// 新增编辑中的知识点
	private String addKnowledge;
	
	// 知识点名称
	private String addKnowledgeNames;
	
	// 试题分类
	private String questionClass;
	
	// 新增编辑中的试题类型
	private String addQuestionType;
	
	// 新增编辑中的试题难度
	private String addDifficulty;
	
	// 答题时间
	private String answerTime;
	
	// 分数
	private String fraction;
	
	// 所属单位
	private String questionOrg;
	
	// 试题题目
	private String questionHead;
	
	// 试题选项
	private String selection;
	
	// 试题答案
	private String questionAnswer;
	
	// 公开,1为公开，2为不公开
	private String isPublic;
	
	// 试题id
	private String questionId;
	
	// 试题解析
	private String  questionAnalysis;
	
	// 是否为主观题,1为主观题，0为客观题
	private String isObjective;
	
	// 部门的父节点
	private String orgparentcode;
	
	// 试题名称
	private String addQuestionName;
	
	// 指标集合
	private ArrayList fieldList;
	// 浏览
	private String liulan;
	
	private String trainsetid;

	public String getTrainsetid() {
		return trainsetid;
	}

	public void setTrainsetid(String trainsetid) {
		this.trainsetid = trainsetid;
	}

	public String getAddQuestionName() {
		return addQuestionName;
	}

	public void setAddQuestionName(String addQuestionName) {
		this.addQuestionName = addQuestionName;
	}

	public String getIsObjective() {
		return isObjective;
	}

	public void setIsObjective(String isObjective) {
		this.isObjective = isObjective;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("knowledge", this.getKnowledge());
		this.getFormHM().put("questionType", this.getQuestionType());
		this.getFormHM().put("difficulty", this.getDifficulty());
		this.getFormHM().put("addKnowledge", this.getAddKnowledge());
		this.getFormHM().put("addDifficulty",this.getAddDifficulty());
		this.getFormHM().put("addQuestionType", this.getAddQuestionType());
		this.getFormHM().put("answerTime", this.getAnswerTime());
		this.getFormHM().put("questionClass", this.getQuestionClass());
		this.getFormHM().put("fraction", this.getFraction());
		this.getFormHM().put("questionOrg", this.getQuestionOrg());
		this.getFormHM().put("questionHead", this.getQuestionHead());
		this.getFormHM().put("selection", this.getSelection());
		this.getFormHM().put("questionAnswer", this.getQuestionAnswer());
		this.getFormHM().put("questionAnalysis", this.getQuestionAnalysis());
		this.getFormHM().put("isPublic", this.getIsPublic());
		this.getFormHM().put("questionId", this.getQuestionId());
		this.getFormHM().put("addQuestionName", this.getAddQuestionName());
		this.getFormHM().put("knowledgeviewvalue", this.getKnowledgeviewvalue());
	}

	@Override
    public void outPutFormHM() {
		this.setKnowledge((String) this.getFormHM().get("knowledge"));
		this.setQuestionType((String) this.getFormHM().get("questionType"));
		this.setQuestionTypeList((ArrayList) this.getFormHM().get("questionTypeList"));
		this.setDifficulty((String) this.getFormHM().get("difficulty"));
		this.setDifficultyList((ArrayList) this.getFormHM().get("difficultyList"));
		this.setStrsql((String) this.getFormHM().get("strsql"));
		this.setStrwhere((String) this.getFormHM().get("strwhere"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setOrder((String) this.getFormHM().get("order"));
		this.setAddKnowledge((String) this.getFormHM().get("addKnowledge"));
		this.setAddKnowledgeNames((String) this.getFormHM().get("addKnowledgeNames"));
		this.setIsPublic((String) this.getFormHM().get("isPublic"));
		this.setAddDifficulty((String) this.getFormHM().get("addDifficulty"));
		this.setAddQuestionType((String) this.getFormHM().get("addQuestionType"));
		this.setAnswerTime((String) this.getFormHM().get("answerTime"));
		this.setQuestionClass((String) this.getFormHM().get("questionClass"));
		this.setFraction((String) this.getFormHM().get("fraction"));
		this.setQuestionOrg((String) this.getFormHM().get("questionOrg"));
		this.setQuestionHead((String) this.getFormHM().get("questionHead"));
		this.setSelection((String) this.getFormHM().get("selection"));
		this.setQuestionAnswer((String) this.getFormHM().get("questionAnswer"));
		this.setQuestionAnalysis((String) this.getFormHM().get("questionAnalysis"));
		this.setCode((String) this.getFormHM().get("aCode"));
		this.setQuestionId((String) this.getFormHM().get("questionId"));
		this.setIsObjective((String) this.getFormHM().get("isObjective"));
		this.setOrgparentcode((String) this.getFormHM().get("orgparentcode"));
		
		this.setAddQuestionName((String) this.getFormHM().get("addQuestionName"));
		this.setFieldList((ArrayList) this.getFormHM().get("fieldList"));
		this.setLiulan((String) this.getFormHM().get("liulan"));
		this.setTrainsetid((String)this.getFormHM().get("trainsetid"));
		this.setKnowledgeviewvalue((String)this.getFormHM().get("knowledgeviewvalue"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		 if("/train/trainexam/question/questiones/questiones".equals(arg0.getPath()) && arg1.getParameter("b_query") != null){
		        if(this.getPagination() != null && "link".equalsIgnoreCase(arg1.getParameter("b_query"))) { 
		        	this.getPagination().firstPage();
		        }
		 }
		return super.validate(arg0, arg1);
	}
	
	public String getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(String knowledge) {
		this.knowledge = knowledge;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public ArrayList getQuestionTypeList() {
		return questionTypeList;
	}

	public void setQuestionTypeList(ArrayList questionTypeList) {
		this.questionTypeList = questionTypeList;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public ArrayList getDifficultyList() {
		return difficultyList;
	}

	public void setDifficultyList(ArrayList difficultyList) {
		this.difficultyList = difficultyList;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getAddKnowledge() {
		return addKnowledge;
	}

	public void setAddKnowledge(String addKnowledge) {
		this.addKnowledge = addKnowledge;
	}

	public String getQuestionClass() {
		return questionClass;
	}

	public void setQuestionClass(String questionClass) {
		this.questionClass = questionClass;
	}

	public String getAddQuestionType() {
		return addQuestionType;
	}

	public void setAddQuestionType(String addQuestionType) {
		this.addQuestionType = addQuestionType;
	}

	public String getAddDifficulty() {
		return addDifficulty;
	}

	public void setAddDifficulty(String addDifficulty) {
		this.addDifficulty = addDifficulty;
	}

	public String getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}

	public String getFraction() {
		return fraction;
	}

	public void setFraction(String fraction) {
		this.fraction = fraction;
	}

	public String getQuestionOrg() {
		return questionOrg;
	}

	public void setQuestionOrg(String questionOrg) {
		this.questionOrg = questionOrg;
	}

	public String getQuestionHead() {
		return questionHead;
	}

	public void setQuestionHead(String questionHead) {
		this.questionHead = questionHead;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	public String getQuestionAnalysis() {
		return questionAnalysis;
	}

	public void setQuestionAnalysis(String questionAnalysis) {
		this.questionAnalysis = questionAnalysis;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getAddKnowledgeNames() {
		return addKnowledgeNames;
	}

	public void setAddKnowledgeNames(String addKnowledgeNames) {
		this.addKnowledgeNames = addKnowledgeNames;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getLiulan() {
		return liulan;
	}

	public void setLiulan(String liulan) {
		this.liulan = liulan;
	}

	public String getKnowledgeviewvalue() {
		return knowledgeviewvalue;
	}

	public void setKnowledgeviewvalue(String knowledgeviewvalue) {
		this.knowledgeviewvalue = knowledgeviewvalue;
	}
}
