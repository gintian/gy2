package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 新增、删除 学科组组内人员
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class ChangeSubjectsPersonTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String msg = "";
			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String)this.getFormHM().get("type");//”1”/”2”(新增/删除)
			String group_id = (String)this.getFormHM().get("group_id");//学科组编号
			group_id = PubFunc.decrypt(group_id);
			ArrayList<String> personidList = new ArrayList<String>();
			personidList = (ArrayList<String>)this.getFormHM().get("personidList");//人员编号list
			
			if("1".equals(type)) {//新增
				msg = subjectsBo.createSubjectsPerson(group_id, personidList);
			}else if("2".equals(type)) {//删除
				msg = subjectsBo.deleteSubjectsPerson(group_id, personidList);
			}else if("3".equals(type)){//修改专家角色
				String role =  (String)this.getFormHM().get("role");
				msg = subjectsBo.updateSubjectsPerson(group_id, personidList,role);
			}
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
