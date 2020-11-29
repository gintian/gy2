package com.hjsj.hrms.transaction.selfinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;

public class AppealSelfDeatilInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		String setname=(String)this.getFormHM().get("setname");
		String userbase=(String)this.getFormHM().get("userbase");
	    if(selfinfolist==null||selfinfolist.size()==0)
            return;       
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	StringBuffer updatesql=new StringBuffer();
        	updatesql.append("update ");
        	updatesql.append(userbase);
        	updatesql.append(setname);
        	updatesql.append(" set state='1' where (1=2) ");
        	for(int i=0;i<selfinfolist.size();i++)
        	{
        		if("a00".equalsIgnoreCase(setname))
        		{
        			DynaBean vo=(DynaBean)selfinfolist.get(i);
        			if(i==0)
	        		{
	        			updatesql.append(" or a0100='");
	        			updatesql.append(vo.get("a0100"));
	        			updatesql.append("' and (i9999=");
	        			updatesql.append(vo.get("i9999"));
	        		}else
	        		{
	        			updatesql.append(" or i9999=");
	            		updatesql.append(vo.get("i9999"));
	           		}
        		}        			
        		else
        		{
        			RecordVo vo=(RecordVo)selfinfolist.get(i);
	        		if(i==0)
	        		{
	        			updatesql.append(" or a0100='");
	        			updatesql.append(vo.getString("a0100"));
	        			updatesql.append("' and (i9999=");
	        			updatesql.append(vo.getInt("i9999"));
	        		}else
	        		{
	        			updatesql.append(" or i9999=");
	            		updatesql.append(vo.getInt("i9999"));
	           		}
        		}
           	}
        	if(selfinfolist.size()>0)
        		updatesql.append(")");
        	
          //System.out.println(updatesql.toString());
          dao.update(updatesql.toString());
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }

}
