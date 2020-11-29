package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：PublishOrCloseRecruitBatchTrans
 * 类描述：发布、结束招聘批次
 * 创建人：sunming 
 * 创建时间：2015-10-30
 * 
 * @version
 */
public class PublishOrCloseRecruitBatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			/**选中行的id**/
			ArrayList ids = (ArrayList) this.getFormHM().get("selectid");
			/**type=1 发布批次  type=2结束批次**/
			String type = (String) this.getFormHM().get("type");
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			ArrayList list = new ArrayList();
			bo.publishOrCloseRecruitBatch(ids,type);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
