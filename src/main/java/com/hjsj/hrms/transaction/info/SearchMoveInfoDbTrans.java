/*
 * Created on 2005-8-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchMoveInfoDbTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    ArrayList dblist=userView.getPrivDbList();
	    ArrayList moveinfodata=(ArrayList)this.getFormHM().get("selectedinfolist");
	    this.getFormHM().put("movedinfolist", moveinfodata);
	    String userbase=(String)this.getFormHM().get("userbase");
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        boolean ishave=false;
        for(int i=0,j=0;i<dblist.size();i++)
        {
         if(!userbase.equalsIgnoreCase((String)dblist.get(i)))  
         {
        	if(j!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
            j++;
            ishave=true;
         }
        }
        if(ishave==false)
        {
            cond.append("''");
            this.getFormHM().put("ismove","no");
        }else
        {
        	this.getFormHM().put("ismove","yes");
        }
       
        cond.append(")");
        cond.append(" order by dbid");
        this.getFormHM().put("todbcond",cond.toString());
	}

}
