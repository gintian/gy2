package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetClassToCycleTran extends IBusiness{

	public void execute() throws GeneralException
	{
		String cycle_id=(String)this.getFormHM().get("cycle_id");
		if(cycle_id==null||cycle_id.length()<=0)
			return;
		KqClassArray kqClassArray = new KqClassArray(this.getFrameconn(),userView);
		HashMap map=kqClassArray.getClassFromId(cycle_id);
		ArrayList shift_class_list=(ArrayList)map.get("id_list");
		ArrayList day_list=(ArrayList)map.get("days_list");
		this.getFormHM().put("shift_class_list",shift_class_list);
		this.getFormHM().put("day_list",day_list);
	}

}
