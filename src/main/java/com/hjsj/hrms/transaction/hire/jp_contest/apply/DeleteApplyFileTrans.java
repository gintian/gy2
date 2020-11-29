package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 *<p>Title:DeleteApplyFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class DeleteApplyFileTrans extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			ArrayList deletefilelist=(ArrayList)this.getFormHM().get("selectedlist");
	        if(deletefilelist==null||deletefilelist.size()==0)
	            return;
	        String sql="";	        
			sql="delete from zp_apply_file ";			
	        sql = sql.toUpperCase();
	        /*Connection conn = null;
	        Statement stmt = null;*/
	        ContentDAO dao = new ContentDAO(this.frameconn);
	         try
		     {
	         	/*conn=this.getFrameconn();
	         	stmt=conn.createStatement();*/
	        	if(deletefilelist.size()>0)
	        	{
	        		LazyDynaBean rec=(LazyDynaBean)deletefilelist.get(0); 
	        		sql +="  where  FILEID in(";
	    			
	        	}
	        	for(int i=0;i<deletefilelist.size();i++)
	           {
	        		if(i!=0)
	        			sql+= ",";
	            	LazyDynaBean rec=(LazyDynaBean)deletefilelist.get(i); 
	           	   sql+= rec.get("fileid").toString(); 
	           }        	
	           sql+=")";
	           //System.out.println(sql);
	           if(deletefilelist.size()>0)
	           {
	        	   dao.update(sql);
	           }
	            	
	        }
	         catch(SQLException sqle)
			 {
			    sqle.printStackTrace();
			    throw GeneralExceptionHandler.Handle(sqle);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


}