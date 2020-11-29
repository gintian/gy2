package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
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
			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类

			String b0110 = this.userView.getUnitIdByBusi("9");//取得所属单位
			String isHistory = (String)this.getFormHM().get("ishistory");//是否显示历史 1：是 0：否
			String year = (String)this.getFormHM().get("year");//按年查询历史记录
			year = year==null?"":year;
			
			ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
			subjectsList = subjectsBo.getSubjects(b0110, isHistory, year);//获取学科组
			ArrayList yearList = subjectsBo.getSubjectsHistoryYear(b0110, isHistory);
			
			this.getFormHM().put("subjectslist", subjectsList);
			this.getFormHM().put("yearList", yearList);
			
			this.getFormHM().put("addVersion", this.userView.hasTheFunction("380020301"));//新建学科组权限
			this.getFormHM().put("editVersion", this.userView.hasTheFunction("380020302"));//编辑学科组权限
			this.getFormHM().put("deleteVersion", this.userView.hasTheFunction("380020303"));//删除学科组权限
			this.getFormHM().put("addPersonVersion", this.userView.hasTheFunction("380020304"));//新增成员权限
			this.getFormHM().put("deletePersonVersion", this.userView.hasTheFunction("380020305"));//删除成员权限
			this.getFormHM().put("showHistoryVersion", this.userView.hasTheFunction("380020306"));//显示历史权限
			this.getFormHM().put("randomSelectionVersion", this.userView.hasTheFunction("380020307"));//专家抽取权限
			this.getFormHM().put("setLeaderVersion", this.userView.hasTheFunction("380020308"));//显示历史权限
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
