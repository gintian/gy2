package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取学科组信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetSubjectsTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			w0301 = PubFunc.decrypt(w0301);
			
			String categoriesid = this.getFormHM().get("categoriesid")==null?"":PubFunc.decrypt((String)this.getFormHM().get("categoriesid"));//会议编号
			
			String selectGroupId = this.getFormHM().get("selectGroupId")==null?"":PubFunc.decrypt((String)this.getFormHM().get("selectGroupId"));//投票页面选择的分组号
			
			SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
			
			ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
			subjectsList = subjectsBo.getSubjects(w0301,categoriesid,selectGroupId);//获取学科组
			
			this.getFormHM().put("subjectslist", subjectsList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
