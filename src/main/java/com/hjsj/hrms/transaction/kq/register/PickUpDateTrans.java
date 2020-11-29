package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class PickUpDateTrans extends IBusiness{
    public void execute() throws GeneralException
    {
    	ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");	
    	String code=(String) this.getFormHM().get("code");		
		if(code==null||code.length()<=0)
		{
			 code="";
		}
    	if(datelist==null||datelist.size()<=0)
		{
			   datelist =RegisterDate.registerdate(code,this.getFrameconn(),this.userView); 
		} 
		CommonData vo_date=(CommonData)datelist.get(0);
 	    String start_date=vo_date.getDataValue();    	
 	    vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
	    String end_date=vo_date.getDataValue();
	    if(start_date!=null&&start_date.length()>0)
			start_date=start_date.replaceAll("\\.","-");
		if(end_date!=null&&end_date.length()>0)
			end_date=end_date.replaceAll("\\.","-");
	    this.getFormHM().put("pick_type","1");
	    this.getFormHM().put("start_date",start_date);
	    this.getFormHM().put("end_date",end_date);
	}
}

