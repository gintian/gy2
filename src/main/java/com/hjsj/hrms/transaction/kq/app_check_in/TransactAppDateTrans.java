package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * 找到对应班次的上下班信息
 * <p>Title:TransactAppDateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 22, 2007 4:10:14 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class TransactAppDateTrans  extends IBusiness{
	public void execute() throws GeneralException 
	{
		HashMap hm=(HashMap)this.getFormHM();
		String flag=(String)hm.get("flag");
		String date_str=(String)hm.get("date");
		String re_date=date_str;		
		String a0100=this.userView.getA0100();
		String nbase=this.userView.getDbname();
		Date dateT=null;
		if(re_date==null||re_date.length()<=0)
		{
			this.getFormHM().put("re_date","");
			return;
		}
		if(flag==null||flag.length()<=0)
		{
			this.getFormHM().put("re_date",re_date);
			return ;
		}
			
		if(!"z1".equals(flag)&&!"z3".equals(flag))
		{
			this.getFormHM().put("re_date",re_date);
			return;
		}
			
		try
		{
			 //dateT=DateUtils.getDate(date_str,"yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat localTime = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
			dateT=localTime.parse(date_str);
		}catch(Exception e)
		{
			//e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("","输入时间出错，请检查格式：yyyy-MM-dd HH:mm","",""));
		}
		String class_time="";
	    String q03z0=DateUtils.format(dateT,"yyyy.MM.dd");
	    String q03z0_r=DateUtils.format(dateT,"yyyy-MM-dd");
	    
	    String sql="select class_id from kq_employ_shift where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+q03z0+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			String class_id="";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				class_id=this.frowset.getString("class_id");
			}
			if(class_id!=null&&!"0".equals(class_id))
			{
				StringBuffer class_sql=new StringBuffer();					
				class_sql.append("select "+kqClassShiftColumns());
				class_sql.append(" from kq_class where class_id='"+class_id+"'");
				this.frowset=dao.search(class_sql.toString());
				if(this.frowset.next())
				{
					if("z1".equals(flag))
					{
						class_time=this.frowset.getString("onduty_1");
						if(class_time==null||class_time.length()<=0)
							class_time="00:00";
						Date on_Date=DateUtils.getDate(q03z0_r+" "+class_time+":00","yyyy-MM-dd HH:mm:ss");
						re_date=DateUtils.format(on_Date,"yyyy-MM-dd HH:mm");
					}
					if("z3".equals(flag))
					{
						String onduty_1=this.frowset.getString("onduty_1");
						if(onduty_1==null||onduty_1.length()<=0)
							onduty_1="00:00";
						class_time=this.frowset.getString("offduty_4");
						if(class_time==null||class_time.length()<=0)
							class_time=this.frowset.getString("offduty_3");
						if(class_time==null||class_time.length()<=0)
							class_time=this.frowset.getString("offduty_2");
						if(class_time==null||class_time.length()<=0)
							class_time=this.frowset.getString("offduty_1");
						if(class_time==null||class_time.length()<=0)
							class_time="00:00";
						Date off_Date=DateUtils.getDate(q03z0_r+" "+class_time+":00","yyyy-MM-dd HH:mm:ss");
						Date on_Time=DateUtils.getDate(onduty_1,"HH:mm");
						Date off_Time=DateUtils.getDate(class_time,"HH:mm");
						float diff=KQRestOper.getPartMinute(on_Time,off_Time);
						/*if(diff<=0)
						{
							off_Date=DateUtils.addDays(off_Date,1);
						}*/
						re_date=DateUtils.format(off_Date,"yyyy-MM-dd HH:mm");
					}
				}
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}		
		this.getFormHM().put("re_date",re_date);		
	}
	 private String kqClassShiftColumns()
	    {
	          StringBuffer columns=new StringBuffer();
	    	  columns.append("class_id,onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");	
			  columns.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,"); 
			  columns.append("onduty_start_1,onduty_1,be_late_for_1,absent_work_1,onduty_end_1,");
			  columns.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
			  columns.append("offduty_1,offduty_end_1,"); 
			  //2
			  columns.append("onduty_start_2,onduty_2,be_late_for_2,absent_work_2,onduty_end_2,");
			  columns.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
			  columns.append("offduty_2,offduty_end_2,");
			  //3
			  columns.append("onduty_start_3,onduty_3,be_late_for_3,absent_work_3,onduty_end_3,");
			  columns.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
			  columns.append("offduty_3,offduty_end_3,");
			  //4
			  columns.append("onduty_start_4,onduty_4,be_late_for_4,absent_work_4,onduty_end_4,");
			  columns.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
			  columns.append("offduty_4,offduty_end_4,");
			  //other
			  columns.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
	          return columns.toString();
	    }
}
