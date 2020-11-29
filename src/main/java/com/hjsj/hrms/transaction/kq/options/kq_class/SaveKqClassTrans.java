package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class SaveKqClassTrans extends IBusiness 
{
	public void execute() throws GeneralException 
	{
		RecordVo ro = (RecordVo)this.getFormHM().get("class_vo");
		 String orgId=(String)this.getFormHM().get("orgId");
		//String class_id=ro.getString("class_id");
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		try
		{
			double work_hours=0;
			String onduty="";
			String offduty="";
			String rest_start="";
			String rest_end="";
			String onduty_card="";
			for(int i=1;i<4;i++)
			{
				onduty_card="onduty_card_"+i;
				onduty="onduty_"+i;
				offduty="offduty_"+i;
				rest_start="rest_start_"+i;
				rest_end="rest_end_"+i;				
				work_hours=work_hours+getPassageTime(ro.getString(onduty),ro.getString(offduty),ro.getString(rest_start),ro.getString(rest_end));	
			}	
			String zero_absent=ro.getString("zero_absent");
			if(zero_absent==null||zero_absent.length()<=0)
				zero_absent="0";
			ro.setDouble("zero_absent",Double.parseDouble(zero_absent));
			String one_absent=ro.getString("one_absent");
			if(one_absent==null||one_absent.length()<=0)
				one_absent="0";
			ro.setDouble("one_absent",Double.parseDouble(one_absent));
			ro.setDouble("work_hours",work_hours);
			ro.setString("org_id", orgId);
			dao.updateValueObject(ro);
			// 33131 保存班次后没回传classid
			this.getFormHM().put("class_id",ro.getString("class_id"));
			this.getFormHM().put("save_flag","ok");
			
		}catch(Exception e)
		{
			this.getFormHM().put("save_flag","eor");
			e.printStackTrace();
		}
		
	}
	/**
	 * 返回一个时间段的工作时间（按分钟计算）
	 * @param onduty
	 * @param offduty
	 * @param rest_start
	 * @param rest_end
	 * @return
	 */
	public int getPassageTime(String onduty,String offduty,String rest_start,String rest_end)
	{
		float time_f=0;
		float time_r=0;
		int time_i=0;
		int time_re=0;
		if(onduty==null||onduty.length()<=0||offduty==null||offduty.length()<=0)
		{
			return 0;
		}		
		Date on_d=DateUtils.getDate(onduty,"HH:mm");
		Date off_d=DateUtils.getDate(offduty,"HH:mm");
		time_f=KQRestOper.getPartMinute(on_d,off_d);
		if(time_f<=0)
		{
			Date zone_d=DateUtils.getDate("24:00","HH:mm");
			time_f=KQRestOper.getPartMinute(on_d,zone_d);
			zone_d=DateUtils.getDate("00:00","HH:mm");
			time_f=KQRestOper.getPartMinute(zone_d,off_d)+time_f;
		}
			
		time_i=Math.round(time_f);
		time_i=Math.abs(time_i);
		/*if(rest_start!=null&&rest_start.length()>0&&rest_end!=null&&rest_end.length()>0)
		{
			Date on_rs=DateUtils.getDate(rest_start,"HH:mm");
			Date off_re=DateUtils.getDate(rest_end,"HH:mm");
			time_r=KQRestOper.getPartMinute(on_rs,off_re);	
			time_re=Math.round(time_r);
			time_re=Math.abs(time_re);
		}	*/		
		return time_i-time_re;
	}
	

}
