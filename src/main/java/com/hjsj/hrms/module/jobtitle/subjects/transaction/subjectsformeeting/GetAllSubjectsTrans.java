package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 资格评审_学科组选择所有组
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class GetAllSubjectsTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			ArrayList<String> EspectList = new ArrayList<String>();// 需要排除组列表
			EspectList = (ArrayList<String>)this.getFormHM().get("espectlist");
			
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("subjects_picker_00001");
			String sql = catche.getTableSql();
			
			ArrayList<String> AllSubjectsList = new ArrayList<String>();//所有选中组
			SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
			AllSubjectsList = subjectsBo.getAllSubjects(sql, EspectList);
			
			this.getFormHM().put("allSubjectsList", AllSubjectsList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
