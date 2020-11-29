package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

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
			SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String)this.getFormHM().get("type");//”1”/”2”/"3"/"4"(新增/删除/修改/获取)

			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			w0301 = PubFunc.decrypt(w0301);
			String group_id = (String)this.getFormHM().get("group_id");//学科组编号
			group_id = PubFunc.decrypt(group_id);
			
			
			if("1".equals(type)) {//新增
				String categories_id = (String)this.getFormHM().get("categoriesid");//会议编号
				categories_id = PubFunc.decrypt(categories_id);
				ArrayList<String> personidList = new ArrayList<String>();
				personidList = (ArrayList<String>)this.getFormHM().get("personidList");//人员编号list
				msg = subjectsBo.createSubjectsPerson(w0301, group_id, personidList, categories_id);
				
			} else if("2".equals(type)) {//删除
				ArrayList<String> personidList = new ArrayList<String>();
				personidList = (ArrayList<String>)this.getFormHM().get("personidList");//人员编号list
				String categoriesid = (String)this.getFormHM().get("categoriesid");//学科组编号
				categoriesid = PubFunc.decrypt(categoriesid);
				subjectsBo.deleteSubjectsPerson(w0301, group_id, personidList, categoriesid);
			} else if("3".equals(type)) {//修改
				String w0101 = (String)this.getFormHM().get("w0101");//专家编号
				w0101 = PubFunc.decrypt(w0101);
				String username = (String)this.getFormHM().get("username");
				String password = (String)this.getFormHM().get("password");
				String role = String.valueOf(this.getFormHM().get("role"));
				msg = subjectsBo.changeSubjectsPerson(w0301, group_id, w0101, username, password, role);
			} else if("4".equals(type)) {//获取
				String w0101 = (String)this.getFormHM().get("w0101");//专家编号
				w0101 = PubFunc.decrypt(w0101);
				
				HashMap<String, String> map = new HashMap<String, String>();
				map = subjectsBo.getSubjectsPersonInfo(w0301, group_id, w0101);
				this.getFormHM().put("personInfo", map);
			}
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
