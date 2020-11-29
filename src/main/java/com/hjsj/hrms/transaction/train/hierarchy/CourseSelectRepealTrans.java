package com.hjsj.hrms.transaction.train.hierarchy;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CourseSelectRepealTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5000 = (String) this.getFormHM().get("r5000");
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		String id = (String) this.getFormHM().get("id");
		id = PubFunc.decrypt(SafeCode.decode(id));
		try{
			if(r5000!=null&&r5000.length()>0)
				courseSelect(r5000);//选课
			if(id!=null&&id.length()>0)
				courseRepeal(id);//撤课
		}catch (Exception e) {
			e.fillInStackTrace();
		}
	}
	
	private void courseSelect(String r5000) throws Exception{
		TrainCourseBo bo = new TrainCourseBo(this.getFrameconn());
		bo.pushCourse(r5000, userView.getDbname(), userView.getA0100(), userView.getUserOrgId(), userView.getUserDeptId(), userView.getUserPosId(), userView.getUserFullName(), "1","");
	}
	
	private void courseRepeal(String id) throws Exception{
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nbase = this.userView.getDbname();
		String aid = this.userView.getA0100();
		/**删除相应课件信息*/
		//陈旭光修改：撤销课程时，同时删除对应的学习记录
		//删除自测分数记录
		dao.update("delete from tr_selfexam_paper where nbase='"+nbase+"' and a0100='"+aid+"' and r5300 = (select r5300 from tr_lesson_paper where r5000 = (select r5000 from tr_selected_lesson where id="+id+"))");
		//删除自测考试答案记录
		dao.update("delete from tr_exam_answer where nbase='"+nbase+"' and a0100='"+aid+"' and exam_type = 1 and r5300 =(select r5300 from tr_lesson_paper where r5000 = (select r5000 from tr_selected_lesson where id="+id+"))");
		//删除对应的scorm课件记录
		dao.update("delete from tr_selected_course_scorm  where nbase='"+nbase+"' and a0100='"+aid+"' and r5100 in (select r5100 from r51 where r5000 = (select r5000 from tr_selected_lesson where id="+id+"))");
		//删除对应的课件记录
		dao.update("delete from tr_selected_course  where nbase='"+nbase+"' and a0100='"+aid+"' and r5100 in (select r5100 from r51 where r5000 = (select r5000 from tr_selected_lesson where id="+id+"))");
		dao.delete("delete from tr_selected_lesson where id="+id, new ArrayList());
		
	}
}