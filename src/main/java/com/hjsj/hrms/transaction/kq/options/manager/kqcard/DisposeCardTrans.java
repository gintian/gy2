package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DisposeCardTrans  extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	ArrayList opinlist=(ArrayList)this.getFormHM().get("selectedinfolist");	
		if(opinlist==null||opinlist.size()==0)
            return;	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList updatelist=new ArrayList();
		String update_sql="update kq_cards set status=? where card_no=?";
	    try
	    {
	    	for(int i=0;i<opinlist.size();i++)
	        {
				
	    	    LazyDynaBean rec=(LazyDynaBean)opinlist.get(i);   
	       	    String card_no=rec.get("card_no").toString();
	       	    ArrayList list=new ArrayList();
	       	    list.add("-1");
	       	    list.add(card_no);	       	    
	       	    updatelist.add(list );  	    
	       	          	              	
	        }
	    	dao.batchUpdate(update_sql.toString(),updatelist);	 
	    }catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(e);
	    }
    }

}
