package com.hjsj.hrms.actionform.train.resource;

import com.hrms.struts.action.FrameForm;

public class TrainLessonForm extends FrameForm {
	private String lessonContent ;
	private String r5100 ;


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("lessonContent", this.getLessonContent());
		this.getFormHM().put("r5100", this.getR5100());
	}

	@Override
    public void outPutFormHM() {
		this.setLessonContent((String)this.getFormHM().get("lessonContent"));
		this.setR5100((String)this.getFormHM().get("r5100"));
	}
	
	
	public String getR5100() {
		return r5100;
	}
	
	public void setR5100(String r5100) {
		this.r5100 = r5100;
	}
	
	public String getLessonContent() {
		return lessonContent;
	}
	
	public void setLessonContent(String lessonContent) {
		this.lessonContent = lessonContent;
	}
}
