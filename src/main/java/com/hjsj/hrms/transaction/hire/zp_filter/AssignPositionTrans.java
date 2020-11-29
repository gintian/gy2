/*
 * Created on 2005-9-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:AssignPositionTrans</p>
 * <p>Description:分配岗位</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class AssignPositionTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList a0100list=(ArrayList)this.getFormHM().get("selecteda0100list");
        String pos_id_value = (String)this.getFormHM().get("pos_id_value");
        if(a0100list==null||a0100list.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	ArrayList templst=new ArrayList();
        	String zp_pos_id = "";  
            String strsql = "select zp_pos_id from zp_position where pos_id = '"+pos_id_value+"'";
            this.frowset = dao.search(strsql);
            while(this.frowset.next()){
         	   zp_pos_id = this.frowset.getString("zp_pos_id");
             } 
            for(int i=0;i<a0100list.size();i++)
        	{
        		LazyDynaBean rec=(LazyDynaBean)a0100list.get(i); 
        		String a0100 = rec.get("a0100").toString();
        		String sql = "select zp_pos_id from zp_pos_tache where a0100 = '"+a0100+"' and zp_pos_id = '"+zp_pos_id+"'";
        		this.frowset = dao.search(sql);
        		while(this.frowset.next()){
        			PreparedStatement pstmt=null;
        			try{
        			   String ssql = "update zp_pos_tache set apply_date = ? where a0100 = ? and zp_pos_id = ?";
        			   templst.add(DateUtils.getSqlDate(new Date()));
        			   templst.add(a0100);
        			   templst.add(zp_pos_id);
        			   dao.update(ssql, templst);
        			   templst.clear();
        			   /*pstmt=this.getFrameconn().prepareStatement(ssql); 
       			       pstmt.setDate(1,DateUtils.getSqlDate(new Date()));
       			       pstmt.setString(2,a0100);
       			       pstmt.setString(3,zp_pos_id);
       			       pstmt.executeUpdate();*/
        			}catch(Exception ee)
					{
        				ee.printStackTrace();
        				throw GeneralExceptionHandler.Handle(ee);				
        			}
        			finally
        			{
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
        		PreparedStatement pst=null;
        		try{
        		   sql="insert into zp_pos_tache (a0100,zp_pos_id,tache_id,thenumber,apply_date,status) values (?,?,1,0,?,'0') ";
    			   templst.add(a0100);
    			   templst.add(zp_pos_id);
    			   templst.add(DateUtils.getSqlDate(new Date()));
    			   dao.update(sql, templst);
    			   templst.clear();
        		  /* pst=this.getFrameconn().prepareStatement(sql);
        		   pst.setString(1,a0100);
        		   pst.setString(2,zp_pos_id);
        		   pst.setDate(3,DateUtils.getSqlDate(new Date()));
        		   pst.executeUpdate();*/
        		}catch(Exception ee)
				{
    				ee.printStackTrace();
    				throw GeneralExceptionHandler.Handle(ee);				
    			}
    			finally
    			{
    				try
    				{
    					if(pst!=null)
    						pst.close();
    				}
    				catch(SQLException ee)
    				{
    					ee.printStackTrace();
    				}
    			}
        	}
        } 
        catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	}

}
