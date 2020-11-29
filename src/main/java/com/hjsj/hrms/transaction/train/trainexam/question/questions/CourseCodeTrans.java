package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CourseCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 获得连接传递的参数
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		String setid = (String) map.get("setid");
		map.remove("setid");
		if (setid == null || setid.length() <= 0) {
			setid = "";
		}
		
		this.getFormHM().put("trainsetid", setid);
		this.getFormHM().put("knowledge", "");
		this.getFormHM().put("knowledgeviewvalue", "");
		this.getFormHM().put("questionType", "");
		this.getFormHM().put("difficulty", "");
	}

}
