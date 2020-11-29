package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取学科组组内人员信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetSubjectsPersonTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类

			String group_id = (String)this.getFormHM().get("group_id");//学科组编号
			group_id = PubFunc.decrypt(group_id);
			
			ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();
			personList = subjectsBo.getSubjectsPerson(group_id);//获取学科组组内人员信息
			
			this.getFormHM().put("personList", personList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
