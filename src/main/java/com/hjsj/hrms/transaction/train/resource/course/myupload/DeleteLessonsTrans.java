package com.hjsj.hrms.transaction.train.resource.course.myupload;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteLessonsTrans extends IBusiness{

	public void execute() throws GeneralException {
		String course =	this.getFormHM().get("course").toString(); //得到要删除课程的ID
		
		String [] courses = course.split(",");
		
		for(int i = 0 ; i < courses.length ; i++){
			DeleteCourse(PubFunc.decrypt(SafeCode.decode(courses[i])));
		}
	}
	
	/**
	 * 删除课程
	 * */
	public void DeleteCourse(String courseId){
		String sql = "delete from r50 where r5000 = '"+courseId+"'";
		String sql1 = "delete from r51 where r5000 = '"+courseId+"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.delete(sql, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				dao.delete(sql1, new ArrayList());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
