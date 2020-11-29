package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewConsoleBo;
import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetBackMapTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String w0301 = (String)this.getFormHM().get("w0301");//会议编号
		w0301 = PubFunc.decrypt(w0301);
		
		String categoriesid = PubFunc.decrypt((String) this.getFormHM().get("categoriesid"));//申报人员分类ID
		
		String selectGroupId = PubFunc.decrypt((String) this.getFormHM().get("selectGroupId"));//会议编号
		SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
		
		int groupCount = subjectsBo.getGroupCountMap(w0301,categoriesid,selectGroupId);//获取组名和组人数的map集合
		ReviewConsoleBo bo = new ReviewConsoleBo(this.getFrameconn(), this.getUserView());
		HashMap<String, String> map = bo.getGroupMap(w0301, "2");
		
		this.getFormHM().put("groupMap", map);
		this.getFormHM().put("groupCount", groupCount);
	}

}
