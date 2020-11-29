package com.hjsj.hrms.transaction.kq.options.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
	  try{
		String optType=(String)this.getFormHM().get("optType");
		String kqFormulaId=(String)this.getFormHM().get("kqFormulaId");
		String kqFormulaName=(String)this.getFormHM().get("kqFormulaName");
		String kqAlert=(String)this.getFormHM().get("kqAlert");
		StringBuffer sql = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("1".equals(optType))
		{
			kqFormulaId = new IDGenerator(2,this.getFrameconn()).getId("hrpchkformula.chkid");
			sql.append("insert into hrpchkformula (chkid,name,information,flag,validflag) values (?,?,?,?,?)");
			ArrayList list = new ArrayList();
			list.add(kqFormulaId);
			list.add(kqFormulaName);
			list.add(kqAlert);
			list.add("5");
			list.add("1");
				dao.insert(sql.toString(), list);
		}
		else
		{
			sql.append(" update hrpchkformula set name=?,information=? where chkid="+kqFormulaId);
			ArrayList list = new ArrayList();
			list.add(kqFormulaName);
			list.add(kqAlert);
				dao.update(sql.toString(), list);
				// TODO Auto-generated catch block
		}
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw GeneralExceptionHandler.Handle(e);
	  }
	}
}
