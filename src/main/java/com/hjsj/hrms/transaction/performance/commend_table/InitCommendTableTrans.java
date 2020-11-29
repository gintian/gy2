package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitCommendTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			
			CommendTableBo bo = new CommendTableBo(this.getFrameconn(),this.userView);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String tableType=bo.getTableType();
			HashMap map = bo.getCommendList();
			ArrayList commendList = (ArrayList)map.get("list");
			String flag = (String)map.get("flag");
			this.getFormHM().put("commendList", commendList);
			this.getFormHM().put("tableType", tableType);
			this.getFormHM().put("flag",flag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
