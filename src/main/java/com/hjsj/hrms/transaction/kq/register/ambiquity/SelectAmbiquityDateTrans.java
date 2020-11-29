package com.hjsj.hrms.transaction.kq.register.ambiquity;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectAmbiquityDateTrans extends IBusiness{
    public void execute() throws GeneralException
    {
	   ArrayList courselist=RegisterDate.sessionDate(this.frameconn);
	   this.getFormHM().put("courselist", courselist);
	   String kq_duration=(String)this.getFormHM().get("kq_duration");
	   if(kq_duration==null||kq_duration.length()<=0)
	   {
		   
		   CommonData vo =(CommonData)courselist.get(0);
		   kq_duration=vo.getDataValue();		   
	   }
	   ArrayList list = RegisterDate.getKqDayList(this.frameconn);
	   String kq_start = "";
	   String kq_end = "";
	   if (list != null || list.size() > 0) {
		   kq_start = list.get(0).toString(); // 考勤期间开始日期
		   kq_end = list.get(1).toString(); // 考勤期间结束日期
	   }
	   this.getFormHM().put("kq_end", kq_end);
	   this.getFormHM().put("count_type","1");
	   this.getFormHM().put("courselist", courselist);
	   this.getFormHM().put("count_duration", kq_duration);
	   String stat_type=(String)this.getFormHM().get("stat_type");
	   if(stat_type==null||stat_type.length()<=0)
	   {
		   stat_type="0";
	   }
	   this.getFormHM().put("stat_type",stat_type);
	}
}
