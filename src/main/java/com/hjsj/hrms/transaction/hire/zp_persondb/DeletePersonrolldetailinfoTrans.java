/*
 * Created on 2005-11-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeletePersonrolldetailinfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase=(String)this.getFormHM().get("userbase");
	    ArrayList mediainfolist=(ArrayList)this.getFormHM().get("selectedlist");
        if(mediainfolist==null||mediainfolist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql="delete from " + userbase + "a00 ";
//        Connection conn = null;
//        Statement stmt = null;
         try
	     {
//         	conn=this.getFrameconn();
//         	stmt=conn.createStatement();
        	if(mediainfolist.size()>0)
        	{
        		LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(0); 
        		sql +="  where  a0100='"  + rec.get("a0100").toString() + "' and i9999 in(";
        	}
        	for(int i=0;i<mediainfolist.size();i++)
           {
        		if(i!=0)
        			sql+= ",";
            	LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(i); 
           	    sql+= rec.get("i9999").toString(); 
           }        	
           sql+=")";
         // System.out.println(sql);
           if(mediainfolist.size()>0)
        	   dao.update(sql);
            	//stmt.executeUpdate(sql);
        }
         catch(SQLException sqle)
		 {
		    sqle.printStackTrace();
		    throw GeneralExceptionHandler.Handle(sqle);
		 }
		 
	}

}
