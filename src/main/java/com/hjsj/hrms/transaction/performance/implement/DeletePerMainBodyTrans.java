package com.hjsj.hrms.transaction.performance.implement;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Iterator;

public class DeletePerMainBodyTrans extends IBusiness {


	public void execute() throws GeneralException {
		ArrayList perObjectlist=(ArrayList)this.getFormHM().get("selectedList");
		String plan_id=(String)this.getFormHM().get("dbpre");
        if(perObjectlist==null||perObjectlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list=new ArrayList();
        
        
        StringBuffer sql_str=new StringBuffer("select object_id,mainbody_id  from per_mainbody where ");
        StringBuffer whl_str=new StringBuffer("");
        try
        {
        	for(Iterator t=perObjectlist.iterator();t.hasNext();)
        	{
        		LazyDynaBean a=(LazyDynaBean)t.next();
        		ArrayList temList=new ArrayList();
        		temList.add(a.get("id"));
        		
        		whl_str.append(" or id="+a.get("id").toString());
        		
        		list.add(temList);
        	}
     
        	
        	sql_str.append(whl_str.substring(4));
        	this.frowset=dao.search(sql_str.toString());
        	ArrayList perPointList=new ArrayList();
        	
        	while(this.frowset.next())
        	{
        		
        		ArrayList temp2=new ArrayList();
        		temp2.add(this.frowset.getString("object_id"));
        		temp2.add(this.frowset.getString("mainbody_id"));
        		perPointList.add(temp2);
        		
        	}
        	
        	dao.batchUpdate("delete from per_mainbody where id=?",list);
        	/** 删除考核主体要素权限 */
        	dao.batchUpdate("delete from per_pointpriv_"+plan_id+" where object_id=? and mainbody_id=?",perPointList);

        	
        }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	  
	}

}
