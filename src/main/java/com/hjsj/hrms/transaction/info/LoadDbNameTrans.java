package com.hjsj.hrms.transaction.info;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class LoadDbNameTrans extends IBusiness {

	public void execute() throws GeneralException {  /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        userbase=(String) this.getFormHM().get("userbase");
        if(userbase==null||userbase.length()<0){
        	userbase=(String)dblist.get(0);
        }
//        if(dblist.size()>0){
//        	userbase=dblist.get(0).toString();      
//        }
//        else
//        	userbase="usr";
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库前缀过滤条件*/
        cat.debug("-----userbase------>" + userbase);
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());}

}
