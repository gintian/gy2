package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SetHrSyncDbTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 2, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SetHrSyncDbTrans extends IBusiness {
	

	 public void execute() throws GeneralException {
		 //System.out.println(this.getFormHM().get("field_falg"));
		 ArrayList dbprelist=new ArrayList();
			StringBuffer strsql=new StringBuffer();
		    strsql.append("select * from dbname ");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    HrSyncBo hsb = new HrSyncBo(this.frameconn);
		    String dbnamestr = hsb.getTextValue(hsb.BASE);
		    String[] dbnames = dbnamestr.split(",");
		    try
		    {
		      this.frowset = dao.search(strsql.toString());
		      while(this.frowset.next())
		      {
		    	  LazyDynaBean bean = new LazyDynaBean();
		    	  bean.set("dbid",this.getFrowset().getInt("dbid")+"");
		    	  bean.set("dbname",this.getFrowset().getString("dbname"));
		    	  bean.set("flag",this.getFrowset().getString("flag"));
		    	  bean.set("pre",this.getFrowset().getString("pre"));
		    	  for(int i=0;i<dbnames.length;i++){
		    		  if(this.getFrowset().getString("pre").equalsIgnoreCase(dbnames[i])){
		    			  bean.set("check","1");
		    			  break;
		    		  }
		    		  else
		    			  bean.set("check","0");
		    	  }
		    	  dbprelist.add(bean);
		      }
		      int index = dbprelist.size();
		      if(dbprelist.size()<6){
		    	  for(int i=0;i<6-index;i++){
		    		  LazyDynaBean bean = new LazyDynaBean();
		    		  bean.set("dbid","");
			    	  bean.set("dbname","");
			    	  bean.set("flag","");
			    	  bean.set("pre","");
			    	  dbprelist.add(bean);
		    	  }
		      }
		    	  
		    } catch(SQLException sqle)
		    {
			      sqle.printStackTrace();
			      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    finally
			{
			       this.getFormHM().put("dbprelist",dbprelist);
			}
			
		}


}
