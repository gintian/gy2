package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 修改学科组是否显示历史
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class ModifySubjectsHistoryTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String msg = "";
			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类
			
			String group_id = (String)this.getFormHM().get("group_id");//学科组编号
			group_id = PubFunc.decrypt(group_id);
			String isHistory = (String)this.getFormHM().get("ishistory");//是否显示历史 0：不显示 1：显示
			
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("key", "history");
			map.put("value", isHistory);
			list.add(map);
			subjectsBo.modifySubjects(group_id, list);
				
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
