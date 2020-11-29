package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;
/**
 * 刷卡纪录过滤初始条件
 * <p>Title:FilterInitTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 7, 2007 3:40:19 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class FilterInitTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	Calendar now = Calendar.getInstance();
    	Date cur_d=now.getTime();
    	String filter_date_s=DateUtils.format(cur_d,"yyyy.MM.dd");
    	String filter_date_e=DateUtils.format(cur_d,"yyyy.MM.dd");
    	String filter_hh_s="00";
    	String filter_mm_s="00";
    	String filter_hh_e="23";
    	String filter_mm_e="59";
    	String filter_card="";
    	this.getFormHM().put("filter_date_s",filter_date_s);
    	this.getFormHM().put("filter_date_e",filter_date_e);
    	this.getFormHM().put("filter_hh_s",filter_hh_s);
    	this.getFormHM().put("filter_mm_s",filter_mm_s);
    	this.getFormHM().put("filter_hh_e",filter_hh_e);
    	this.getFormHM().put("filter_mm_e",filter_mm_e);
    	this.getFormHM().put("filter_card",filter_card);
	}

}
