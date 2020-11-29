package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 新增、删除学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class ChangeSubjectsTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String msg = "";
			SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String)this.getFormHM().get("type");//”1”/”3”(新增/删除)
			
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			w0301 = PubFunc.decrypt(w0301);
			String group_id = (String)this.getFormHM().get("group_id");//学科组编号
			group_id = PubFunc.decrypt(group_id);
			String categoriesid = (String)this.getFormHM().get("categoriesid");//学科组编号
			categoriesid = PubFunc.decrypt(categoriesid);
			
			if("1".equals(type)) {//新增
				msg = subjectsBo.createSubjects(w0301, group_id, categoriesid);
				
			} else if("3".equals(type)) {//删除
				subjectsBo.deleteSubjects(w0301, group_id,categoriesid);
			}
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
