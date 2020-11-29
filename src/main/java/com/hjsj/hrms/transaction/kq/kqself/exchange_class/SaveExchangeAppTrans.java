package com.hjsj.hrms.transaction.kq.kqself.exchange_class;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;

public class SaveExchangeAppTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RecordVo ex_co=(RecordVo)this.getFormHM().get("ex_co");
		String flagsturt=(String)this.getFormHM().get("flagsturt");
		if(flagsturt==null||flagsturt.length()<=0)
			flagsturt="01";
		String app_flag=(String)this.getFormHM().get("app_flag");
		if(app_flag==null||app_flag.length()<=0)
		{
			return;
		}
		if("02".equals(flagsturt))
		{
			ex_co.setString("q19z5","02");
		}else
		{
			ex_co.setString("q19z5","01");
		}
		if("add".equalsIgnoreCase(app_flag))
		{
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	   	    String insertid=idg.getId(("q19.q1901").toUpperCase());
	   	    ex_co.setString("q1901",insertid);
	   	    //ex_co.setString("q19z5","03");
	   	    ex_co.setString("q19z0","03");
	   	    //ord库中 q1905 时间类型不能直接写入需要转换
	   	    Calendar now = Calendar.getInstance();
			Date cur_d=now.getTime();
			ex_co.setDate("q1905",DateUtils.format(cur_d,"yyyy-MM-dd"));
	   	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	   	    try
	   	    {
	   	    	//System.out.println(ex_co);	   	    	
	   	    	dao.addValueObject(ex_co);
	   	    }catch(Exception e)
	   	    {
	   	    	e.printStackTrace();
	   	    }
		}else if("edit".equalsIgnoreCase(app_flag))
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
	   	    try
	   	    {
	   	    	//ex_co.setString("q19z5","02");
	   	    	ex_co.setString("q19z0","03");
	   	    	dao.updateValueObject(ex_co);
	   	    }catch(Exception e)
	   	    {
	   	    	e.printStackTrace();
	   	    }
		}				
	}

}
