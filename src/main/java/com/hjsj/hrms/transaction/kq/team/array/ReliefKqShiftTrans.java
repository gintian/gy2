package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ReliefKqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
		String a_code= (String)hm.get("a_code");
		String nbase= (String)hm.get("nbase");
		if(a_code==null||a_code.length()<=0)
			return;		
		/*String start_date=PubFunc.getStringDate("yyyy-MM-dd");
		Date d_start=DateUtils.getDate(start_date,"yyyy-MM-dd");
		Date d_end=DateUtils.addDays(d_start,30);*/
		String session_data=(String)this.getFormHM().get("session_data");
		ArrayList date_list =RegisterDate.getOneDurationDate(this.getFrameconn(),session_data);
		String start_date=date_list.get(0).toString().replaceAll("\\.","-");
		String end_date=date_list.get(date_list.size()-1).toString().replaceAll("\\.","-");
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		this.getFormHM().put("rest_postpone","");
		this.getFormHM().put("feast_postpone","");		
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("nbase",nbase);
	}


}
