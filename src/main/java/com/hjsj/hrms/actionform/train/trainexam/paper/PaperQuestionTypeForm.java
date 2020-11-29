package com.hjsj.hrms.actionform.train.trainexam.paper;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PaperQuestionTypeForm extends FrameForm {

	private ArrayList itemlist=new ArrayList();
	private String strsql;
	private String strwhere;
	private String columns;
	private String order_by;
	private String r5300;
	private ArrayList questiontypes=new ArrayList();
	private String questiontype;
	private String start;//用于排序
	private String end;//用于排序
	
	/*添加试卷*/
	private String type_id;
	private ArrayList questions=new ArrayList();
	private String difficulty;//难度
	private String knowledge;// 知识点
	private String knowledgeviewvalue;// 知识点名称 保留值
	private String title;
	
	private ArrayList difficultyList=new ArrayList();
	private ArrayList knowledgeList = new ArrayList();
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("difficulty", this.getDifficulty());
		this.getFormHM().put("knowledge", this.getKnowledge());
		this.getFormHM().put("knowledgeviewvalue", this.getKnowledgeviewvalue());
		this.getFormHM().put("type_id", type_id);
		this.getFormHM().put("r5300", r5300);
	}

	@Override
    public void outPutFormHM() {

		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setQuestiontypes((ArrayList)this.getFormHM().get("questiontypes"));
		this.setStart((String)this.getFormHM().get("start"));
		this.setEnd((String)this.getFormHM().get("end"));
		
		this.setQuestions((ArrayList)this.getFormHM().get("questions"));
		this.setDifficulty((String)this.getFormHM().get("difficulty"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setDifficultyList((ArrayList)this.getFormHM().get("difficultyList"));
		this.setKnowledgeList((ArrayList)this.getFormHM().get("knowledgeList"));
		this.setKnowledgeviewvalue((String)this.getFormHM().get("knowledgeviewvalue"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
	try
	{
	    if ("/train/trainexam/paper/questiontype".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }else if ("/train/trainexam/paper/questiontype".equals(arg0.getPath()) && arg1.getParameter("b_add") != null)
	    {
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
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

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getR5300() {
		return r5300;
	}

	public void setR5300(String r5300) {
		this.r5300 = r5300;
	}

	public ArrayList getQuestiontypes() {
		return questiontypes;
	}

	public void setQuestiontypes(ArrayList questiontypes) {
		this.questiontypes = questiontypes;
	}

	public String getQuestiontype() {
		return questiontype;
	}

	public void setQuestiontype(String questiontype) {
		this.questiontype = questiontype;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public ArrayList getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList questions) {
		this.questions = questions;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(String knowledge) {
		this.knowledge = knowledge;
	}

	public String getKnowledgeviewvalue() {
		return knowledgeviewvalue;
	}

	public void setKnowledgeviewvalue(String knowledgeviewvalue) {
		this.knowledgeviewvalue = knowledgeviewvalue;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList getDifficultyList() {
		return difficultyList;
	}

	public void setDifficultyList(ArrayList difficultyList) {
		this.difficultyList = difficultyList;
	}

	public ArrayList getKnowledgeList() {
		return knowledgeList;
	}

	public void setKnowledgeList(ArrayList knowledgeList) {
		this.knowledgeList = knowledgeList;
	}

}
