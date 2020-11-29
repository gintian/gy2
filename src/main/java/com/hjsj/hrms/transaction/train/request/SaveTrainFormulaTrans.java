package com.hjsj.hrms.transaction.train.request;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveTrainFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
	  try{
		String optType=(String)this.getFormHM().get("optType");
		String trainFormulaId=(String)this.getFormHM().get("trainFormulaId");
		String trainFormulaName=(String)this.getFormHM().get("trainFormulaName");
		String trainAlert=(String)this.getFormHM().get("trainAlert");
		StringBuffer sql = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("1".equals(optType))
		{
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			trainFormulaId=idg.getId("hrpchkformula.chkid");
			
			sql.append("insert into hrpchkformula (chkid,name,information,flag,validflag) values (?,?,?,?,?)");
			ArrayList list = new ArrayList();
			list.add(trainFormulaId);
			list.add(trainFormulaName);
			list.add(trainAlert);
			list.add("7");
			list.add("1");
			
			dao.insert(sql.toString(), list);
		}
		else
		{
			sql.append(" update hrpchkformula set name=?,information=? where chkid="+trainFormulaId);
			ArrayList list = new ArrayList();
			list.add(trainFormulaName);
			list.add(trainAlert);
			
			dao.update(sql.toString(), list);
		}
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw GeneralExceptionHandler.Handle(e);
	  }
	}
}
