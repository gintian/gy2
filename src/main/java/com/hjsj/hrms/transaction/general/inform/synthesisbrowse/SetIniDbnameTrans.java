package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SetIniDbnameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList dblist=userView.getPrivDbList();
		 if(dblist.size()>0)
			 this.getFormHM().put("dbpre",dblist.get(0));
		 else
			 this.getFormHM().put("dbpre","Usr");
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
			/*初始化人员库*/
		   setDblist(dao);
	}
	private void setDblist(ContentDAO dao) throws GeneralException
	{
		 /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        ArrayList dbdesclist=new ArrayList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
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
        try{
        	  this.frowset=dao.search(cond.toString());
        	  while(this.frowset.next())
        	  {
        		  LazyDynaBean dbbean=new LazyDynaBean();
        		  dbbean.set("pre",this.frowset.getString("pre"));
        		  dbbean.set("dbname",this.frowset.getString("dbname"));
        		  //System.out.println(dbbean.get("pre"));
        		  dbdesclist.add(dbbean);
        		/*  CommonData da=new CommonData();
        		  da.setDataName(this.frowset.getString("dbname"));
        		  da.setDataValue(this.frowset.getString("pre"));
        		  dbdesclist.add(da);*/
        	  }        	  this.getFormHM().put("dblist",dbdesclist);
			
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e); 
		 }
      
	}

}
