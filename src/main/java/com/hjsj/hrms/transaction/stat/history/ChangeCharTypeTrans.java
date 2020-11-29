package com.hjsj.hrms.transaction.stat.history;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ChangeCharTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String backdate = (String)this.getFormHM().get("backdate");
		String chart_type= (String)this.getFormHM().get("chart_type");
		String backdates = (String)this.getFormHM().get("backdates");
		if(("5".equals(chart_type)||"20".equals(chart_type))&&backdates.length()>10){
	    	this.getFormHM().put("backdates", backdate);
		}
	}

}
