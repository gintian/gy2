package com.hjsj.hrms.transaction.train.ilearning;

import com.hjsj.hrms.businessobject.train.point.TrainPointBo;
import com.hjsj.hrms.businessobject.train.resource.MyLessonBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchStudentStatuTrans extends IBusiness {
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		MyLessonBo myLessonBo = new MyLessonBo(this.frameconn, this.userView);
		int learningCount = myLessonBo.getUserLearningCourseCount();
		int learnedCount = myLessonBo.getUserLearnedCourseCount();
		//陈旭光修改：将正学、已学课程细分为正学必修、正学选修、已学必修、已学选修
		int learningReqCount = myLessonBo.getUserLearningReqCourseCount();
		int learningOptCount = myLessonBo.getUserLearningOptCourseCount();
		int learnedReqCount = myLessonBo.getUserLearnedReqCourseCount();
		int learnedOptCount = myLessonBo.getUserLearnedOptCourseCount();
		this.getFormHM().put("learningCourseCount", String.valueOf(learningCount));
		this.getFormHM().put("learnedCourseCount", String.valueOf(learnedCount));
		this.getFormHM().put("learningReqCourseCount", String.valueOf(learningReqCount));
		this.getFormHM().put("learningOptCourseCount", String.valueOf(learningOptCount));
		this.getFormHM().put("learnedReqCourseCount", String.valueOf(learnedReqCount));
		this.getFormHM().put("learnedOptCourseCount", String.valueOf(learnedOptCount));
		
		TrainPointBo trainPointBo = new TrainPointBo(this.frameconn, this.userView);
		int learningPoint = trainPointBo.getCurUserUsablePoint();
		this.getFormHM().put("learningPoint", String.valueOf(learningPoint));
		

	}

}
