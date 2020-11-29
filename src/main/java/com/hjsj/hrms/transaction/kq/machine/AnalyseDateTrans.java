package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;
/**
 * 分析数据的时间
 * <p>Title:AnalyseDateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 8, 2007 4:58:46 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class AnalyseDateTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		int year=0;
		Calendar now = Calendar.getInstance();
		String a_code=(String)this.getFormHM().get("a_code");
		String nbase=(String)this.getFormHM().get("nbase");
		Date cur_d=now.getTime();		
		
		String start_date=DateUtils.format(cur_d,"yyyy-MM-dd");
		String end_date=DateUtils.format(cur_d,"yyyy-MM-dd");
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);		
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("nbase",nbase);
	}


}
