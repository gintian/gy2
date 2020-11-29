package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class EditDurationTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		// TODO Auto-generated method stub
		
		    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
	        String kq_year=(String)this.getFormHM().get("kq_year");
	        String kq_duration=(String)hm.get("akq_durations");
	        String star=(String)hm.get("start");
	        
	        if(kq_year==null|| "".equals(kq_year))
	        	kq_year=star.substring(0,4);
	        
	           ContentDAO dao=new ContentDAO(this.getFrameconn());
	           RecordVo vo=new RecordVo("kq_duration");
	           
	           try
	           {
	               vo.setString("kq_year",kq_year);
	               vo.setString("kq_duration",kq_duration);
	               vo=dao.findByPrimaryKey(vo);
	            }
	            catch(SQLException sqle)
	            {
	  	          sqle.printStackTrace();
		          throw GeneralExceptionHandler.Handle(sqle);            
	            }
	            finally
	            {
	            	this.getFormHM().put("end",vo.getString("kq_end"));
	            	this.getFormHM().put("yue",vo.getString("gz_duration"));
	            	this.getFormHM().put("start",vo.getString("kq_start"));
	                this.getFormHM().put("duration",vo);
	            }
	 }

}
