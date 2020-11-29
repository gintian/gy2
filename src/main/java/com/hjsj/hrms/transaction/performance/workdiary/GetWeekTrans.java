package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetWeekTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String monthnum = (String)this.getFormHM().get("monthnum");
		monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:"1";
		
		String yearnum = (String)this.getFormHM().get("yearnum");
		yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"2007";
		
		WeekUtils WeekUtils= new WeekUtils();
		int num = WeekUtils.totalWeek(Integer.parseInt(yearnum),Integer.parseInt(monthnum));
		String week[] = {"第一周","第二周","第三周","第四周","第五周","第六周"};
		ArrayList list = new ArrayList();
		for(int i=0;i<num;i++){
			CommonData obj=new CommonData((i+1)+"",week[i]);
			list.add(obj);
		}
		this.getFormHM().put("weeklist",list);
	}

}
