/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpjobTrans</p>
 * <p>Description:保存招聘活动,zp_job</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpjobvo");
		String status = (String)this.getFormHM().get("status");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增招聘活动
             */
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String zp_job_id = idg.getId("zp_job.zp_job_id");
               vo.setString("zp_job_id",zp_job_id); 
               String sql="";
               if(vo.getString("resource_id")!=null && vo.getString("resource_id").trim().length()>0)
                  sql = "insert into zp_job (zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount) values ('"+zp_job_id+"','"+vo.getString("name")+"',"+PubFunc.DateStringChange(vo.getString("start_date"))+","+PubFunc.DateStringChange(vo.getString("end_date"))+",'"+vo.getString("principal")+"','"+vo.getString("attendee")+"','01','"+vo.getString("plan_id")+"',"+vo.getString("resource_id")+",'',0)";
               else
               	  sql = "insert into zp_job (zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount) values ('"+zp_job_id+"','"+vo.getString("name")+"',"+PubFunc.DateStringChange(vo.getString("start_date"))+","+PubFunc.DateStringChange(vo.getString("end_date"))+",'"+vo.getString("principal")+"','"+vo.getString("attendee")+"','01','"+vo.getString("plan_id")+"',null,'',0)";
               ArrayList list = new ArrayList();
               dao.update(sql,list);
               this.getFormHM().put("zp_job_id_value", String.valueOf(zp_job_id));
               this.getFormHM().put("flag","0");
            }catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(e); 
            }
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_job set name='"+vo.getString("name")+"',start_date="+PubFunc.DateStringChange(vo.getString("start_date"))+",end_date="+PubFunc.DateStringChange(vo.getString("end_date"))+",principal='"+vo.getString("principal")+"',attendee='"+vo.getString("attendee")+"',plan_id='"+vo.getString("plan_id")+"',resource_id="+vo.getString("resource_id")+" where zp_job_id='"+vo.getString("zp_job_id")+"'";
	        	dao.update(sql,list);
	        	this.getFormHM().put("zp_job_id_value", vo.getString("zp_job_id"));       	
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
