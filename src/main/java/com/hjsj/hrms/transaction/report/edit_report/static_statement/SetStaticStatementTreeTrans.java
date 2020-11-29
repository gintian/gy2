package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
public class SetStaticStatementTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		this.getFormHM().put("scopeid", hm.get("scopeid"));
		
	}
	LazyDynaBean bean=new LazyDynaBean();
}
