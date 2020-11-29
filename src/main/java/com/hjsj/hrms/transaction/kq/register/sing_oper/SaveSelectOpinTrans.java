package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveSelectOpinTrans extends IBusiness {
	
	public void execute() throws GeneralException 
	{
		
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");		
		if(selectedinfolist==null||selectedinfolist.size()==0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));
		
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		CommonData vo_date=(CommonData)datelist.get(0);
    	String start_date=vo_date.getDataValue();
    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
   	    String end_date=vo_date.getDataValue();
   	    String cur_date=getDate(start_date,end_date);
   	    ArrayList vo_list=new ArrayList();
   	    for(int i=0;i<selectedinfolist.size();i++)
   	    {
   	    	LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);
   	    	String nbase = rec.get("nbase").toString();
   	    	String a0100 = rec.get("a0100").toString();
   	    	
   	    	if (haveRecord(vo_list, nbase, a0100))
   	    		continue;
   	    	
   	    	SingOpinVo one_vo = new SingOpinVo();   	    	
   	    	one_vo.setNbase(nbase);
   	    	one_vo.setB0110(rec.get("b0110").toString());
   	    	one_vo.setE0122(rec.get("e0122").toString());
   	    	one_vo.setE01a1(rec.get("e01a1").toString());
   	    	one_vo.setA0100(a0100);
   	    	one_vo.setA0101(rec.get("a0101").toString());
   	    	one_vo.setQ03z0(cur_date);
   	    	vo_list.add(one_vo);
   	    }
   	   this.getFormHM().put("vo_list",vo_list);
   	   this.getFormHM().put("error_flag","0");
	}
	
	private boolean haveRecord(ArrayList voList, String nbase, String a0100) {
		boolean has = false;
		
		for (int i = 0; i < voList.size(); i++) {
			SingOpinVo vo = (SingOpinVo)voList.get(i);
			if (vo.getA0100().equalsIgnoreCase(a0100) && vo.getNbase().equalsIgnoreCase(nbase)) 
				return true;
		}
		
		return has;
	}
	
	private String getDate(String statr_date,String end_date)
	{
		Date  end_d=DateUtils.getDate(end_date,"yyyy.MM.dd");  
		Date start_d=DateUtils.getDate(statr_date,"yyyy.MM.dd");  
		Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		int diff1=DateUtils.dayDiff(start_d,cur_d);
		int diff2=DateUtils.dayDiff(cur_d,end_d);
		String r_date="";
	    if(diff1<=0)
		{
			r_date=DateUtils.format(start_d,"yyyy-MM-dd");
			
		}else if(diff2<=0)
		{
			r_date=DateUtils.format(end_d,"yyyy-MM-dd");
			//调用的时间在本考勤期间之后
		}else
		{
			r_date=DateUtils.format(cur_d,"yyyy-MM-dd");
			
		}
		return r_date;
	}
}
