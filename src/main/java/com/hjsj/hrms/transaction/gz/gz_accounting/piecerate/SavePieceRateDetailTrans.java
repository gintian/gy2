package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePieceRateDetailTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
		    HashMap hm=this.getFormHM();

			String tableName=(String)hm.get("detail_table");
			ArrayList list=(ArrayList)hm.get("detail_record");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
