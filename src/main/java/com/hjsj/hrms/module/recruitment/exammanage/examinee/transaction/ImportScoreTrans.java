package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ScoreExcelBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 导入考试成绩
 * @author Administrator
 *
 */
public class ImportScoreTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			String batchId=(String) this.getFormHM().get("batchId");
			String fileId=(String) this.getFormHM().get("fileId");
			
			ScoreExcelBo score = new ScoreExcelBo(this.getFrameconn());
			//调用导入方法，拿到返回信息
			this.userView.getHm().put("error_message",score.getImportScore(fileId));
			this.userView.getHm().put("batchId", batchId);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
