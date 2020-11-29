package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class QueryWorkDiaryTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
			String p0100 = (String)this.getFormHM().get("p0100");
			String record_num = (String)this.getFormHM().get("record_num");
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,this.userView.getDbname(),this.userView.getA0100());
			LazyDynaBean bean = bo.queryById(p0100, record_num);
			this.getFormHM().put("p0100", bean.get("p0100"));
			this.getFormHM().put("record_num", bean.get("record_num"));
			this.getFormHM().put("content", SafeCode.encode((String)bean.get("content")));
			this.getFormHM().put("title", bean.get("title"));
			this.getFormHM().put("type", bean.get("type"));
			Date d_start_time = (Date)bean.get("start_time");
			Date d_end_time = (Date)bean.get("end_time");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			String start_time = sdf.format(d_start_time);
			String end_time = sdf.format(d_end_time);
			GregorianCalendar s = new GregorianCalendar();
			s.setTime(d_start_time);
			GregorianCalendar e = new GregorianCalendar();
			e.setTime(d_end_time);
			
			String startHour = String.valueOf(s.get(GregorianCalendar.HOUR_OF_DAY));
			String endHour = String.valueOf(e.get(GregorianCalendar.HOUR_OF_DAY));
			String startMinute = String.valueOf(s.get(GregorianCalendar.MINUTE));
			String endMinute = String.valueOf(e.get(GregorianCalendar.MINUTE));
			this.getFormHM().put("start_time", start_time);
			this.getFormHM().put("end_time", end_time);
			this.getFormHM().put("startHour", startHour);
			this.getFormHM().put("endHour", endHour);
			this.getFormHM().put("startMinute", startMinute);
			this.getFormHM().put("endMinute", endMinute);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
