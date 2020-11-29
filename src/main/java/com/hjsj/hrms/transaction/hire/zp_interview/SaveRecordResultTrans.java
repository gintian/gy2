/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_interview;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SaveZpreleasePosTrans</p>
 * <p>Description:保存面试纪录,zp_process_log</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveRecordResultTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String a0100 = (String)hm.get("a_a0100id");
        String description = (String)this.getFormHM().get("description");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        ExecuteSQL executeSQL = new ExecuteSQL();
	    try
	    {
	       String strsql = "select * from zp_process_log where a0100 = '"+a0100+"' and staff_name = '"+this.userView.getUserName()+"'";
	       List rs = ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
	       if(rs!=null && rs.size()>0){
              String sql = "update zp_process_log set description = '"+description+"' where a0100 = '"+a0100+"' and staff_name = '"+this.userView.getUserName()+"'";
              executeSQL.execUpdate(sql);
              return;
	       }
	       IDGenerator idg=new IDGenerator(2,this.getFrameconn());
           int log_id = Integer.parseInt(idg.getId("zp_process_log.log_id"));
	       String sql="insert into zp_process_log (log_id,a0100,staff_name,description) values("+log_id+",'"+a0100+"','"+this.userView.getUserName()+"','"+description+"')";
	       executeSQL.execUpdate(sql);
	
	    }	    
	    catch(Exception sqle)
	    {
	       	 sqle.printStackTrace();
	    	 throw GeneralExceptionHandler.Handle(sqle);            
        }

	}

}
