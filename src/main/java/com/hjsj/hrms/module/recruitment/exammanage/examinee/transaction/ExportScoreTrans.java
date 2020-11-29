package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;


import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ScoreExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 导出考试成绩
 * @author Administrator
 *
 */
public class ExportScoreTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		try {
			String batch_id = (String) this.getFormHM().get("batch_id");
			String model = (String) this.getFormHM().get("model");//是否生成模板
			// 生成excel,加密文件
			ScoreExcelBo achieveExcel = new ScoreExcelBo(this.getFrameconn(),model);
			String excelFile = achieveExcel.createExcel(batch_id, this.userView);
			excelFile = PubFunc.encrypt(excelFile);
			this.getFormHM().clear();
			this.getFormHM().put("fileName", excelFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	
}
