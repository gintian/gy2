package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveExchangeAppTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RecordVo ex_co=(RecordVo)this.getFormHM().get("ex_co");
		String app_flag=(String)this.getFormHM().get("app_flag");
		Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		ex_co.setDate("q1905",DateUtils.format(cur_d,"yyyy-MM-dd"));
		if(app_flag==null||app_flag.length()<=0)
		{
			return;
		}
		if("add".equalsIgnoreCase(app_flag))
		{
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	   	    String insertid=idg.getId(("q19.q1901").toUpperCase());
	   	    ex_co.setString("q1901",insertid);
	   	    //ex_co.setString("q19z5","03");
	   	    ex_co.setString("q19z5","02");//申请完改为已报批状态
	   	    ex_co.setString("q19z0","01");
	   	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	   	    try
	   	    {
	   	    	//System.out.println(ex_co);
	   	    	//up_kq_employ_shift(ex_co);//已报批状态不用调整班次
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
	   	    	ex_co.setString("q19z5","02");
	   	    	ex_co.setString("q19z0","");
	   	    	dao.updateValueObject(ex_co);
	   	    }catch(Exception e)
	   	    {
	   	    	e.printStackTrace();
	   	    }
		}				
	}
	public void up_kq_employ_shift(RecordVo ex_co)throws GeneralException
	{
		ArrayList up_list=new ArrayList();		
		StringBuffer sql=new StringBuffer();
		sql.append("update kq_employ_shift set");
		sql.append(" class_id=? ");
		sql.append(" where a0100=? and nbase=? and q03z0=?");
		ArrayList cur_list=new ArrayList();
		ArrayList ex_list=new ArrayList();
    	
    	String q19z9=ex_co.getString("q19z9");
    	cur_list.add(q19z9);    	      
    	cur_list.add(ex_co.getString("a0100").toString());
    	cur_list.add(ex_co.getString("nbase").toString());
    	cur_list.add(ex_co.getString("q19z1").toString());
    	up_list.add(cur_list);
    	String q19z7=ex_co.getString("q19z7");
    	ex_list.add(q19z7);
    	ex_list.add(ex_co.getString("q19a0").toString());
    	ex_list.add(ex_co.getString("nbase").toString());
    	ex_list.add(ex_co.getString("q19z3").toString());
    	up_list.add(ex_list); 
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.batchUpdate(sql.toString(),up_list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		 }
	}	
}
