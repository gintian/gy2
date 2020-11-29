package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 刷卡纪录过滤
 * <p>Title:FiltrateCardTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 7, 2007 3:41:23 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class FiltrateCardTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String filter_date_s=(String)hm.get("filter_date_s");
    	String filter_date_e=(String)hm.get("filter_date_e");
    	String filter_hh_s=(String)hm.get("filter_hh_s");
    	String filter_hh_e=(String)hm.get("filter_hh_e");
    	String filter_mm_s=(String)hm.get("filter_mm_s");
    	String filter_mm_e=(String)hm.get("filter_mm_e");
    	String filter_card=(String)hm.get("filter_card");
    	
    	this.getFormHM().put("filter_date_s",filter_date_s);
    	this.getFormHM().put("filter_date_e",filter_date_e);
    	this.getFormHM().put("filter_hh_s",filter_hh_s);
    	this.getFormHM().put("filter_hh_e",filter_hh_e);
    	this.getFormHM().put("filter_mm_s",filter_mm_s);
    	this.getFormHM().put("filter_mm_e",filter_mm_e);
    	
    	this.getFormHM().put("start_date",filter_date_s);
    	this.getFormHM().put("end_date",filter_date_e);
    	this.getFormHM().put("start_hh",filter_hh_s);
    	this.getFormHM().put("end_hh",filter_hh_e);
    	this.getFormHM().put("start_mm",filter_mm_s);
    	this.getFormHM().put("end_mm",filter_mm_e);
    
		this.getFormHM().put("select_pre", "");
		this.getFormHM().put("sp_flag", "");
		this.getFormHM().put("into_flag", "");
		this.getFormHM().put("datafrom", "");
		this.getFormHM().put("select_type", "2");
		this.getFormHM().put("select_name", filter_card);
    	KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
    	filter_date_s=filter_date_s.replaceAll("-","\\.");
    	filter_date_e=filter_date_e.replaceAll("-","\\.");
    	String filter_time_s=filter_hh_s+":"+filter_mm_s;
    	String filter_time_e=filter_hh_e+":"+filter_mm_e;
    	ArrayList dblist=this.userView.getPrivDbList();    	
  
    	String where_c = "";
    	if(filter_card != null && filter_card.length() > 0){
    		where_c = " AND card_no='"+filter_card.trim() + "'";
    	}
        String sqlstr = kqCardData.getSQL1(dblist,"",filter_date_s,filter_date_e,filter_time_s,filter_time_e,where_c,""); 
    	this.getFormHM().put("sqlstr",sqlstr);
    	
	    String a_code=(String)this.getFormHM().get("a_code");
		String inoutCardCountInfo = kqCardData.colectInOutemps(dblist,a_code,filter_date_s,filter_date_e,filter_time_s,filter_time_e,where_c);
        this.getFormHM().put("inout_str", inoutCardCountInfo);

	}
}
