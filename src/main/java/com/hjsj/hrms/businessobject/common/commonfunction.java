/*
 * Created on 2006-2-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.common;

import java.util.ArrayList;
/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  class   commonfunction {
	public static String getDbcondString(ArrayList dblist)
	{
		 StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname where pre in (");
	        String userbase="";
	        if(dblist.size()>0){
	        	userbase=dblist.get(0).toString();      
	        }
	        else {
                userbase="usr";
            }
	        for(int i=0;i<dblist.size();i++)
	        {
	        	
	        	if(i!=0) {
                    cond.append(",");
                }
	            cond.append("'");
	            cond.append((String)dblist.get(i));
	            cond.append("'");
	        }
	        if(dblist.size()==0) {
                cond.append("''");
            }
	        
	        //cond.append(")");
	        cond.append(") order by DbId");//liuy 2014-10-23 在没有主键的情况下，人员库排序会乱
	        return cond.toString();
	}

}
