package com.hjsj.hrms.transaction.kq.app_check_in.redeploy_rest;

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
/**
 * 保存调休
 * <p>Title:SaveRedeployAppTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 23, 2007 9:08:52 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveRedeployAppTrans extends IBusiness {
	private String table_name="q25";
	public void execute() throws GeneralException 
	{
		RecordVo ex_co=(RecordVo)this.getFormHM().get("ex_co");
		String app_flag=(String)this.getFormHM().get("app_flag");
		Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		ex_co.setDate("q2505",DateUtils.format(cur_d,"yyyy-MM-dd"));
		if(app_flag==null||app_flag.length()<=0)
		{
			return;
		}
		if("add".equalsIgnoreCase(app_flag))
		{
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	   	    String insertid=idg.getId((this.table_name+"."+this.table_name+"01").toUpperCase());
	   	    ex_co.setString(this.table_name+"01",insertid);
	   	    //ex_co.setString(this.table_name+"z5","03");
	   	    ex_co.setString(this.table_name+"z5","02");//申请完改为已报批状态
	   	    ex_co.setString(this.table_name+"z0","01");
	   	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	   	    try
	   	    {
	   	    	//System.out.println(ex_co);
	   	    	//up_kq_employ_shift(this.table_name,ex_co);//已报批状态不用调整班次
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
	   	    	ex_co.setString(this.table_name+"z5","02");
	   	    	ex_co.setString(this.table_name+"z0","");
	   	    	dao.updateValueObject(ex_co);
	   	    }catch(Exception e)
	   	    {
	   	    	e.printStackTrace();
	   	    }
		}				
	}
	/**
	 * 调休班次
	 * @param table
	 * @param ex_co
	 * @throws GeneralException
	 */
	private void up_kq_employ_shift(String table,RecordVo ex_co)throws GeneralException
	{
		ArrayList up_list=new ArrayList();		
		StringBuffer sql=new StringBuffer();
		sql.append("update kq_employ_shift set");
		sql.append(" class_id=? ");
		sql.append(" where a0100=? and nbase=? and q03z0=?");
		ArrayList cur_list=new ArrayList();
		ArrayList ex_list=new ArrayList();
    	      
    	      //String q19z7=getFieldValue(table,table+"z7",ex_co.getString(table+"01").toString());
		      String q19z7=ex_co.getString(table+"z7");
		      cur_list.add("0");    	      
    	      cur_list.add(ex_co.getString("a0100").toString());
    	      cur_list.add(ex_co.getString("nbase").toString());
    	      cur_list.add(ex_co.getString(table+"z1").toString());
    	      up_list.add(cur_list);    	      
    	      ex_list.add(q19z7);
    	      ex_list.add(ex_co.getString("a0100").toString());
    	      ex_list.add(ex_co.getString("nbase").toString());
    	      ex_list.add(ex_co.getString(table+"z3").toString());
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
