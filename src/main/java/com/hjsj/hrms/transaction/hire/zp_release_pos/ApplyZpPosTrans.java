/*
 * Created on 2005-11-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_release_pos;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplyZpPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String zp_pos_id=(String)hm.get("zp_pos_id_value");
        if(this.userView.getUserId() != null && !"".equals(this.userView.getUserId()) && !"su".equals(this.userView.getUserId())){
        	PreparedStatement pstmt=null;
        	try{
        	   String ssql = "select a0100 from zp_pos_tache where a0100 = '"+this.userView.getUserId()+"'";
        	   this.frowset = dao.search(ssql);
        	   while(this.frowset.next()){
        	   	  return;
        	   }
        	   String sql = "insert into zp_pos_tache (a0100,zp_pos_id,tache_id,thenumber,apply_date,status) values (?,?,1,1,?,0)";
        	   ArrayList values = new ArrayList();
        	   values.add(this.userView.getUserId());
        	   values.add(zp_pos_id);			   
        	   values.add(DateUtils.getSqlDate(new Date()));
        	   dao.insert(sql, values);
        	   /*pstmt=this.getFrameconn().prepareStatement(sql);
			   pstmt.setString(1,this.userView.getUserId());
			   pstmt.setString(2,zp_pos_id);			   
			   pstmt.setDate(3,DateUtils.getSqlDate(new Date()));
			   pstmt.executeUpdate();*/
        	}catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(e);
            }finally{  	

    			try
    			{
    				if(pstmt!=null)
    					pstmt.close();
    			}
    			catch(SQLException ee)
    			{
    				ee.printStackTrace();
    			}
    		}
        }
 	}
}
