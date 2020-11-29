package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
/**
 * 数据处理
 *<p>Title:DataAnalyseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 25, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchDataAnalyseTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	
    	String a_code=(String)this.getFormHM().get("a_code");
    	String start_date=(String)this.getFormHM().get("start_date");
	    String start_hh=(String)this.getFormHM().get("start_hh");
	    String start_mm=(String)this.getFormHM().get("start_mm");
	    String end_date=(String)this.getFormHM().get("end_date");
	    String end_hh=(String)this.getFormHM().get("end_hh");
	    String end_mm=(String)this.getFormHM().get("end_mm");
    	if(a_code==null||a_code.length()<=0)
    	{
    		String privcode=RegisterInitInfoData.getKqPrivCode(userView);
			String codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
    		if("UN".equalsIgnoreCase(privcode))
    		{
    			a_code=privcode+codevalue;
    		}else if("UM".equalsIgnoreCase(privcode))
    		{
    			a_code=privcode+codevalue;
    		}else if("@K".equalsIgnoreCase(privcode))
    		{
    			a_code=privcode+codevalue;
    		}else
    		{
    			a_code="EP"+this.userView.getA0100();
    		}   		
    	}
    	Date now = new Date();
    	ArrayList kqduration = RegisterDate.getKqDayList(frameconn);
    	if (kqduration == null || kqduration.size() <= 0) 
            throw new GeneralException(ResourceFactory.getProperty("kq.register.session.nosave"));
    	String kqStart = ((String) kqduration.get(0)).replace(".", "-") + " 00:00";
    	String kqEnd = ((String) kqduration.get(1)).replace(".", "-") + " 24:00";
    	if((OperateDate.strToDate(kqStart, "yyyy-MM-dd HH:mm").getTime() > now.getTime() || 
				OperateDate.strToDate(kqEnd, "yyyy-MM-dd HH:mm").getTime() < now.getTime()))//当前日期不在考勤期间内
		{
    		start_date = (String) kqduration.get(0);
			end_date = (String) kqduration.get(1);
		}else 
		{
			start_date=(String) kqduration.get(0);	
			end_date=PubFunc.getStringDate("yyyy.MM.dd");
		}
		
		
    	if(start_hh==null||start_hh.length()<=0)
			start_hh="00";
		if(start_mm==null||start_mm.length()<=0)
			start_mm="00";
		
		if(end_hh==null||end_hh.length()<=0)
			end_hh="23";
		if(end_mm==null||end_mm.length()<=0)
			end_mm="59";
    	this.getFormHM().put("a_code",a_code);  
    	this.getFormHM().put("start_date",start_date); 
    	this.getFormHM().put("start_hh",start_hh); 
    	this.getFormHM().put("start_mm",start_mm); 
    	this.getFormHM().put("end_date",end_date); 
    	this.getFormHM().put("end_mm",end_mm); 
    	this.getFormHM().put("end_hh",end_hh); 
    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
    	String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel",uplevel); 
	}

}
