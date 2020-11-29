package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyIntegral extends IBusiness {

	public void execute() throws GeneralException {
	    try {
	        ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
	        String tabid = constantbo.getNodeAttributeValue("/param/em_point_tab", "id");
	        
	        if(tabid == null || "".equals(tabid) || "#".equals(tabid))
	            throw new Exception(ResourceFactory.getProperty("train.setparam.integral.hint"));
	        ArrayList yearlist = new ArrayList();
	        yearlist = getyearlist();
	        Calendar ca = Calendar.getInstance();
			int year = ca.get(Calendar.YEAR);
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String syear = (String)hm.get("year");
			hm.remove("year");
			
			year = syear==null || "".equals(syear) ? year : Integer.parseInt(syear);
			
			String bizdate = year+"-01-01";
	        String a0100=this.userView.getA0100();
	        this.getFormHM().put("tabid",tabid);
	        this.getFormHM().put("a0100",a0100);
	        this.getFormHM().put("currentpage","0");
	        this.getFormHM().put("userpriv", "selfinfo");
	        this.getFormHM().put("cardtype", "cardscore");  
	        this.getFormHM().put("yearlist", yearlist);
	        this.getFormHM().put("bizDate", bizdate);
	        this.getFormHM().put("styear", year+"");
	        this.getFormHM().put("year", syear);
	        
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	
	public ArrayList getyearlist(){
		ArrayList yearlist = new ArrayList();
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		CommonData cd = new CommonData();
		for(int i=0;i<5;i++){
			cd = new CommonData();
			cd.setDataName(String.valueOf(year-i));
			cd.setDataValue(String.valueOf(year-i));
			yearlist.add(cd);
		}
		//Collections.reverse(yearlist);
		return yearlist;
		
	}
}
