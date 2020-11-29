package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class LearnCourseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		// 是否是我的课程，me为我的课程，其他值为模块
		String opt = (String) hm.get("opt");
		
		// 课程id
		String lessonId = (String) hm.get("lesson");
		
	}
	
}
