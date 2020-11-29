package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：DeleteRecruitBatchTrans 
 * 类描述：删除招聘批次
 * 创建人：sunming 
 * 创建时间：2015-10-28
 * 
 * @version
 */
public class DeleteRecruitBatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			/**选中行的id**/
			ArrayList ids = (ArrayList) this.getFormHM().get("selectid");
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			ArrayList list = new ArrayList();
			list = bo.deleteRecruitBatch(ids);
			this.getFormHM().put("list", list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
