package com.hjsj.hrms.transaction.kq.kqself.redeploy_rest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;

public class SaveSelfRedeployAppTrans extends IBusiness {
	private String table_name="q25";
	public void execute() throws GeneralException 
	{
		RecordVo ex_co=(RecordVo)this.getFormHM().get("ex_co");
		String flag=(String)this.getFormHM().get("flag");
		if(flag==null||flag.length()<=0)
			flag="01";
		if("02".equals(flag))
		{
			ex_co.setString(this.table_name+"z5","02");
		}else
		{
			ex_co.setString(this.table_name+"z5","01");
		}
		String app_flag=(String)this.getFormHM().get("app_flag");
		if(app_flag==null||app_flag.length()<=0)
		{
			return;
		}
		if("add".equalsIgnoreCase(app_flag))
		{
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	   	    String insertid=idg.getId((this.table_name+"."+this.table_name+"01").toUpperCase());
	   	    ex_co.setString(this.table_name+"01",insertid);
	   	   /* ex_co.setString(this.table_name+"z5","03");*/
	   	    ex_co.setString(this.table_name+"z0","");
	   	   //ord库中 q2505 时间类型不能直接写入需要转换
	   	    Calendar now = Calendar.getInstance();
			Date cur_d=now.getTime();
			ex_co.setDate("q2505",DateUtils.format(cur_d,"yyyy-MM-dd"));
	   	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	   	    try
	   	    {
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
	   	    	//ex_co.setString(this.table_name+"z5","02");
	   	    	ex_co.setString(this.table_name+"z0","");
	   	    	dao.updateValueObject(ex_co);
	   	    }catch(Exception e)
	   	    {
	   	    	e.printStackTrace();
	   	    }
		}				
	}}
