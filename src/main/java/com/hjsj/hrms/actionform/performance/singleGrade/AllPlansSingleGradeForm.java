package com.hjsj.hrms.actionform.performance.singleGrade;

import com.hrms.struts.action.FrameForm;

public class AllPlansSingleGradeForm extends FrameForm{
	
	String isHasSaveButton = "0";//是否有保存、提交按钮  如果所有计划都是已评状态，那么就没有这两个按钮
	String gradeHtml = "";//在后台生成的表格
	String jsonStr = "";//json串，存储着所有的数据，用于在前台循环

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isHasSaveButton", this.getIsHasSaveButton());
		this.getFormHM().put("gradeHtml", this.getGradeHtml());
		this.getFormHM().put("jsonStr", this.getJsonStr());

	}

	@Override
    public void outPutFormHM() {
		
		this.setIsHasSaveButton((String)this.getFormHM().get("isHasSaveButton"));
		this.setGradeHtml((String)this.getFormHM().get("gradeHtml"));
		this.setJsonStr((String)this.getFormHM().get("jsonStr"));
	}

	public String getIsHasSaveButton() {
		return isHasSaveButton;
	}

	public void setIsHasSaveButton(String isHasSaveButton) {
		this.isHasSaveButton = isHasSaveButton;
	}

	public String getGradeHtml() {
		return gradeHtml;
	}

	public void setGradeHtml(String gradeHtml) {
		this.gradeHtml = gradeHtml;
	}


	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}
	

}
