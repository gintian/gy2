package com.hjsj.hrms.actionform.competencymodal;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class PostModalForm extends FrameForm {
	private PaginationForm postModalListForm=new PaginationForm();
	private String treeItem;
	private String historyDate;
	/**是否有历史时点功能=1有=0没有*/
	private String ishistory;
	private String postSeqName;
	private String pointSet;
	private String pointCode;
	private String pointName;
	private String score;
	private String grade;
	private ArrayList gradeList = new ArrayList();
	/**模型类别=1职务 =2岗位序列  =3岗位*/
	private String object_type;
    /**根节点汉字*/
	private String rootDesc;
	/**关联代码*/
	private String codesetid;
	/**当前选择代码类*/
	private String codeitemid;
	private ArrayList editPostModalList = new ArrayList();
	
	
	private String resultHtml="";
	private HashMap dataMap=new HashMap();
	/**是否可维护=1可以=0不可以（在历史时点下不可以维护，只能查看）*/
	private String isoper="";

	@Override
    public void inPutTransHM() {
		this.getFormHM().put(isoper, this.getIsoper());
		this.getFormHM().put("historyDate", this.getHistoryDate());
		this.getFormHM().put("ishistory", this.getIshistory());
		this.getFormHM().put("editPostModalList", this.getEditPostModalList());
		this.getFormHM().put("selectedList",this.getPostModalListForm().getSelectedList());
		this.getFormHM().put("object_type", this.getObject_type());
		this.getFormHM().put("codesetid",this.getCodesetid());
		this.getFormHM().put("codeitemid",this.getCodeitemid());
		this.getFormHM().put("pointCode", this.getPointCode());
	}
	@Override
    public void outPutFormHM() {
		this.setIsoper((String)this.getFormHM().get("isoper"));
		this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		this.setResultHtml((String)this.getFormHM().get("resultHtml"));
		
		this.setHistoryDate((String)this.getFormHM().get("historyDate"));
		this.setIshistory((String)this.getFormHM().get("ishistory"));
		this.setEditPostModalList((ArrayList)this.getFormHM().get("editPostModalList"));
		this.setRootDesc((String)this.getFormHM().get("rootDesc"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.getPostModalListForm().setList((ArrayList)this.getFormHM().get("postModalList"));
		this.setGradeList((ArrayList)this.getFormHM().get("gradeList"));
		this.setTreeItem((String)this.getFormHM().get("treeItem"));
		this.setObject_type((String)this.getFormHM().get("object_type"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setPointCode((String)this.getFormHM().get("pointCode"));
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	    {
			try
			{
			    if ("/competencymodal/postseq_commodal/post_modal_list".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && ("link".equals(arg1.getParameter("b_query"))))
			    {		
					if (this.postModalListForm.getPagination() != null)
					{
					    this.postModalListForm.getPagination().firstPage();
					}								
			    }		 
			  
			} catch (Exception e)
			{
			    e.printStackTrace();
			}
			return super.validate(arg0, arg1);
	    }
	public PaginationForm getPostModalListForm() {
		return postModalListForm;
	}
	public void setPostModalListForm(PaginationForm postModalListForm) {
		this.postModalListForm = postModalListForm;
	}
	public String getTreeItem() {
		return treeItem;
	}
	public void setTreeItem(String treeItem) {
		this.treeItem = treeItem;
	}
	public String getHistoryDate() {
		return historyDate;
	}
	public void setHistoryDate(String historyDate) {
		this.historyDate = historyDate;
	}
	public String getPostSeqName() {
		return postSeqName;
	}
	public void setPostSeqName(String postSeqName) {
		this.postSeqName = postSeqName;
	}
	public String getPointSet() {
		return pointSet;
	}
	public void setPointSet(String pointSet) {
		this.pointSet = pointSet;
	}
	public String getPointCode() {
		return pointCode;
	}
	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}
	public String getPointName() {
		return pointName;
	}
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public ArrayList getGradeList() {
		return gradeList;
	}
	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}
	public String getObject_type() {
		return object_type;
	}
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	public String getRootDesc() {
		return rootDesc;
	}
	public void setRootDesc(String rootDesc) {
		this.rootDesc = rootDesc;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getCodeitemid() {
		return codeitemid;
	}
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public ArrayList getEditPostModalList() {
		return editPostModalList;
	}
	public void setEditPostModalList(ArrayList editPostModalList) {
		this.editPostModalList = editPostModalList;
	}
	public String getIshistory() {
		return ishistory;
	}
	public void setIshistory(String ishistory) {
		this.ishistory = ishistory;
	}
	public String getResultHtml() {
		return resultHtml;
	}
	public void setResultHtml(String resultHtml) {
		this.resultHtml = resultHtml;
	}
	public HashMap getDataMap() {
		return dataMap;
	}
	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}
	
	public String getIsoper() {
		return isoper;
	}
	public void setIsoper(String isoper) {
		this.isoper = isoper;
	}
}
