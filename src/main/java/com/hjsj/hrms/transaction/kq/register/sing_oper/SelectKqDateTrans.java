package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SelectKqDateTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		HashMap hm=(HashMap)this.getFormHM();
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		String date=((String)hm.get("date")).replaceAll("-", ".");
		String nbase=(String)hm.get("nbase");
		String a0100=(String)hm.get("a0100");
		String start_date="";
		String end_date="";
		String r_date="";
		String flag="";
		if(datelist!=null&&datelist.size()>0)
		{
			CommonData vo_date=(CommonData)datelist.get(0);
	    	start_date=vo_date.getDataValue();
	    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
	   	    end_date=vo_date.getDataValue();
	   	    
		}else
		{
			datelist=RegisterDate.getKqDayList(this.getFrameconn());
			start_date=datelist.get(0).toString();
			end_date=datelist.get(datelist.size()-1).toString();
		}
		Date start_d=DateUtils.getDate(start_date,"yyyy.MM.dd"); 
		Date end_d=DateUtils.getDate(end_date,"yyyy.MM.dd"); 
		Date cur_d=DateUtils.getDate(date,"yyyy.MM.dd"); 
		int diff2=DateUtils.dayDiff(cur_d,end_d);
		int diff1=DateUtils.dayDiff(start_d,cur_d);
		if(diff1<0)
		{
			r_date=DateUtils.format(start_d,"yyyy.MM.dd");
			flag="1";//调用的时间在本考勤期间以前
		}else if(diff2<0)
		{
			r_date=DateUtils.format(end_d,"yyyy.MM.dd");
			flag="2";//调用的时间在本考勤期间之后
		}else
		{
			r_date=DateUtils.format(cur_d,"yyyy.MM.dd");
			flag="0";
		}
		this.getFormHM().put("date",r_date);
		this.getFormHM().put("flag",flag);
	}
}
