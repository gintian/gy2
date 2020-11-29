package com.hjsj.hrms.transaction.hire.zp_job;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveZpjobDetailsTrans</p>
 * <p>Description:保存招聘活动明细,zp_job_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpjobDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpjobDetailsvo");
		String zp_job_id = vo.getString("zp_job_id");
		String zp_job_id_value = (String)this.getFormHM().get("zp_job_id_value");
        if(vo==null)
            return;
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag_detail))
        {
            /**
             * 新增招聘活动明细
             */
        	try{
        	   IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String detail_id = idg.getId("zp_job_details.detail_id");
               String sql = "insert into zp_job_details (detail_id,zp_job_id,detailname,charge,realcharge) values("+detail_id+",'"+zp_job_id_value+"','"+vo.getString("detailname")+"',"+vo.getString("charge")+",0)";
               ArrayList list = new ArrayList();
               dao.update(sql,list);
               this.getFormHM().put("zp_job_id_value",zp_job_id_value);
            }catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(e); 
            }      
        }
        else if("0".equals(flag_detail))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_job_details set detailname = '"+vo.getString("detailname")+"',charge="+vo.getString("charge")+" where zp_job_id='"+vo.getString("zp_job_id")+"' and detail_id = "+vo.getString("detail_id");
	        	dao.update(sql,list);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
