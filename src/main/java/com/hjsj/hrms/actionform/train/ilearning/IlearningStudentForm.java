package com.hjsj.hrms.actionform.train.ilearning;

import com.hrms.struts.action.FrameForm;

public class IlearningStudentForm extends FrameForm {
	private static final long serialVersionUID = 1L;
	
	//正学课程数量
	private String learningCourseCount;
	//已学课程数量
	private String learnedCourseCount;
	//陈旭光修改：将正学、已学课程细分为正学必修、正学选修、已学必修、已学选修
	//正学必修课程数量
	private String learningReqCourseCount; 
	//正学选修课程数量
	private String learningOptCourseCount;
	//已学选修课程数量
	private String learnedOptCourseCount;
	//已学必修课程数量
	private String learnedReqCourseCount;
	//学习所得积分
	private String learningPoint;

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("learningCourseCount", this.getLearningCourseCount());
		this.getFormHM().put("learnedCourseCount", this.getLearnedCourseCount());
		this.getFormHM().put("learningReqCourseCount", this.getLearningReqCourseCount());
		this.getFormHM().put("learningOptCourseCount", this.getLearningOptCourseCount());
		this.getFormHM().put("learnedOptCourseCount", this.getLearningOptCourseCount());
		this.getFormHM().put("learnedReqCourseCount", this.getLearnedReqCourseCount());
		this.getFormHM().put("learningPoint", this.getLearningPoint());
	}

	@Override
    public void outPutFormHM() {
		this.setLearningCourseCount((String)this.getFormHM().get("learningCourseCount"));
		this.setLearnedCourseCount((String)this.getFormHM().get("learnedCourseCount"));
		this.setLearningReqCourseCount((String)this.getFormHM().get("learningReqCourseCount"));
		this.setLearningOptCourseCount((String)this.getFormHM().get("learningOptCourseCount"));
		this.setLearnedReqCourseCount((String)this.getFormHM().get("learnedReqCourseCount"));
		this.setLearnedOptCourseCount((String)this.getFormHM().get("learnedOptCourseCount"));
		this.setLearningPoint((String)this.getFormHM().get("learningPoint"));
	}

	
	public String getLearningCourseCount() {
		return learningCourseCount;
	}

	public void setLearningCourseCount(String learningCourseCount) {
		this.learningCourseCount = learningCourseCount;
	}

	public String getLearnedCourseCount() {
		return learnedCourseCount;
	}

	public void setLearnedCourseCount(String learnedCourseCount) {
		this.learnedCourseCount = learnedCourseCount;
	}

	public String getLearningReqCourseCount() {
		return learningReqCourseCount;
	}

	public void setLearningReqCourseCount(String learningReqCourseCount) {
		this.learningReqCourseCount = learningReqCourseCount;
	}

	public String getLearningOptCourseCount() {
		return learningOptCourseCount;
	}

	public void setLearningOptCourseCount(String learningOptCourseCount) {
		this.learningOptCourseCount = learningOptCourseCount;
	}

	public String getLearnedOptCourseCount() {
		return learnedOptCourseCount;
	}

	public void setLearnedOptCourseCount(String learnedOptCourseCount) {
		this.learnedOptCourseCount = learnedOptCourseCount;
	}

	public String getLearnedReqCourseCount() {
		return learnedReqCourseCount;
	}

	public void setLearnedReqCourseCount(String learnedReqCourseCount) {
		this.learnedReqCourseCount = learnedReqCourseCount;
	}

	public void setLearningPoint(String learningPoint) {
		this.learningPoint = learningPoint;
	}

	public String getLearningPoint() {
		return learningPoint;
	}

}
