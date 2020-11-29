/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 *<p>Title:DeleteMultMediaInfoTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class DeleteMultMediaInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String kind=(String)this.getFormHM().get("kind");
		String code=(String)this.getFormHM().get("code");
	    ArrayList mediainfolist=(ArrayList)this.getFormHM().get("selectedlist");
        if(mediainfolist==null||mediainfolist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql="";
        if("0".equalsIgnoreCase(kind))
        	sql="delete from k00 ";
        else //if(kind.equalsIgnoreCase("1"))
        	sql="delete from b00 ";
         try
	     {
        	if(mediainfolist.size()>0)
        	{
        		LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(0);
        		System.out.println(rec.get("title"));
        		if("0".equalsIgnoreCase(kind))
        			sql +=" where  e01a1='"  + rec.get("a0100").toString() + "' and i9999 in(";
                else //if(kind.equalsIgnoreCase("1"))
                	sql +=" where  b0110='"  + rec.get("a0100").toString() + "' and i9999 in(";
        	}
        	for(int i=0;i<mediainfolist.size();i++)
           {
        		if(i!=0)
        			sql+= ",";
            	LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(i); 
           	   sql+= rec.get("i9999").toString(); 
           }        	
           sql+=")";
           //System.out.println(sql);
           if(mediainfolist.size()>0)
            	dao.delete(sql, new ArrayList());
        }
         catch(SQLException sqle)
		 {
		    sqle.printStackTrace();
		    throw GeneralExceptionHandler.Handle(sqle);
		 }
	}

}
