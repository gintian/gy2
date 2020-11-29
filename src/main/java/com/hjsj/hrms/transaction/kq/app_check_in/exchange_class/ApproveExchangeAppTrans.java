package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 签批调班申请
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 23, 2007:9:31:54 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ApproveExchangeAppTrans extends IBusiness {
	private String table_name="q19";
	public void execute() throws GeneralException 
	{
		RecordVo ex_co=(RecordVo)this.getFormHM().get("ex_co");		
		String flag=(String)this.getFormHM().get("flagsturt");
		if(flag==null||flag.length()<=0)
			flag="01";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		boolean isCorrect=true;
   	    try
   	    {
   	    	if("03".equals(flag))
   	    	{
   	    		ex_co.setString(this.table_name+"z5","03");
   	   	    	ex_co.setString(this.table_name+"z0","01");
   	   	    	up_kq_employ_shift(this.table_name,ex_co);
   	    	}else if("07".equals(flag))
   	    	{
   	    		ex_co.setString(this.table_name+"z5","07");
   	   	    	ex_co.setString(this.table_name+"z0","01");
   	    	}else
   	    	{
   	    		ex_co.setString(this.table_name+"z5","01");
   	   	    	ex_co.setString(this.table_name+"z0","03");
   	    	}   	    		   	    	
   	    	dao.updateValueObject(ex_co);
   	    }catch(Exception e)
   	    {
   	    	e.printStackTrace();
   	    	isCorrect=false;
   	    }	
   	    if(isCorrect&& "03".equals(flag))
  	      this.getFormHM().put("spFlag","批准成功！");
  	    else if(!isCorrect&& "03".equals(flag))
  	      this.getFormHM().put("spFlag","批准失败！");
        else if(isCorrect&& "07".equals(flag))
    	  this.getFormHM().put("spFlag","驳回成功！");
    	else if(!isCorrect&& "07".equals(flag))
    	  this.getFormHM().put("spFlag","驳回失败！");				
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
		      String q19z9=ex_co.getString("q19z9");
		      cur_list.add(q19z9);
		      //cur_list.add("0");    	      
    	      cur_list.add(ex_co.getString("a0100").toString());
    	      cur_list.add(ex_co.getString("nbase").toString());
    	      cur_list.add(ex_co.getString(table+"z1").toString());
    	      up_list.add(cur_list);    	      
    	      ex_list.add(q19z7);
    	      ex_list.add(ex_co.getString("q19a0").toString());
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
