package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;

/**
 *Title:ExportDataTrans
 *Description:考场设置导出
 *Company:HJHJ
 *Create time:2015-11-3 
 *@author lis
 */
public class ExportDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			Connection conn = this.getFrameconn();
			
			ExamHallBo bo = new ExamHallBo(conn, this.userView);
			
			String fileName="";
			//导出excel
			fileName=bo.exportFile(); 
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
