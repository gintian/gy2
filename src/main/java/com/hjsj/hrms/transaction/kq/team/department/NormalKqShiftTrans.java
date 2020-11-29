package com.hjsj.hrms.transaction.kq.team.department;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 继承部门班组排班
 * @author Owner
 * wangy
 */
public class NormalKqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException {
		String start_date = (String) this.getFormHM().get("start_date");
		String end_date = (String) this.getFormHM().get("end_date");
		String session_data=(String)this.getFormHM().get("session_data");
		if(session_data!=null&&session_data.length()>0&&session_data.length()<10)
		{
			ArrayList date_list =RegisterDate.getOneDurationDate(this.getFrameconn(),session_data);
			if (start_date == null || start_date.length() <= 0) 
			{
				start_date = date_list.get(0).toString().replaceAll("\\.","-");
			}
			if (end_date == null || end_date.length() <= 0) 
			{
				end_date = date_list.get(date_list.size() - 1).toString().replaceAll("\\", "-");
			}
		}
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
	}

}
