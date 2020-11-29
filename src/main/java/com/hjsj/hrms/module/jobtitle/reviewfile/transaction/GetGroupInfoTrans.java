package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 上会材料_获取学科组数据源
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetGroupInfoTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			
			String w0301 = (String)this.getFormHM().get("w0301");
			w0301 = PubFunc.decrypt(w0301);
			
			ArrayList subjectgroup = reviewFileBo.getProfessionGroupByW0301(w0301);// 获取人员信息 
			this.getFormHM().put("subjectgroup", subjectgroup);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
