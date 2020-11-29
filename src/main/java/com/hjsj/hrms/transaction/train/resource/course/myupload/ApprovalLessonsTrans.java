package com.hjsj.hrms.transaction.train.resource.course.myupload;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ApprovalLessonsTrans extends IBusiness{

	public void execute() throws GeneralException {
		String course =	this.getFormHM().get("course").toString(); //得到要报批课程的ID
		
		String [] courses = course.split(",");
		
		for(int i = 0 ; i < courses.length ; i++){
			ApprovalLessons(PubFunc.decrypt(SafeCode.decode(courses[i])));
		}
	}
	
	/**
	 * 上传课程报批
	 * */
	
	public boolean ApprovalLessons(String courseId){
		String sql = " update r50 set r5022 = '02' where r5000 = '" + courseId + "'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
