package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchKqInitCodeTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		this.getFormHM().put("all_init","0");
		this.getFormHM().put("scope","1");
		this.getFormHM().put("mess","");
		Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		ArrayList list = KqUtilsClass.getMaxArchiveDuration();
		if (list.size() == 0) {
			this.getFormHM().put("Tstart",DateUtils.format(cur_d,"yyyy-MM-dd"));
			this.getFormHM().put("Tend",DateUtils.format(cur_d,"yyyy-MM-dd"));
		} else {
			String start = (String) list.get(0);
			String end = (String) list.get(1);
			this.getFormHM().put("Tstart",start);
			this.getFormHM().put("Tend",end);
		}
	}

}
