package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class LoadDbnamesTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        String dbname="";
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            else
            	dbname=(String)dblist.get(i);
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        this.getFormHM().put("dbcond",cond.toString());
	}

}
