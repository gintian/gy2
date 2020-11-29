package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 获取编辑评委会信息
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetEditInfoTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			//判断是评委会的还是学科组的
			String pageType = (String)this.getFormHM().get("pageType");
			String type = (String)this.getFormHM().get("type");//1:新建 2：编辑
			String id = (String)this.getFormHM().get("id");//评委会、学科组编号
			id = PubFunc.decrypt(id);
			HashMap<String, String> map = new HashMap<String, String>();
			if("committee".equals(pageType)){
				CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
				map = committeeBo.getCommitteeInfo(type, id);
			}else if("subjects".equals(pageType)){
				SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);
				map = subjectsBo.getSubjectsInfo(type,id);
			}
			this.getFormHM().put("infos", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
