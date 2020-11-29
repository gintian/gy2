package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SetDbTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Feb 25, 2009:7:31:48 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SetDbTrans extends IBusiness {
	 public void execute() throws GeneralException {
		 String type = (String)this.getFormHM().get("field_falg");
		 ArrayList dblist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from dbname  order by dbid");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String dbvalue = sysoth.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,type,"db");
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	    	  LazyDynaBean bean = new LazyDynaBean();
	    	  String pre = this.getFrowset().getString("pre");
	    	  bean.set("dbid",this.getFrowset().getInt("dbid")+"");
	    	  bean.set("dbname",this.getFrowset().getString("dbname"));
	    	  bean.set("flag",this.getFrowset().getString("flag"));
	    	  bean.set("pre",pre);
	    	  if(dbvalue!=null&&dbvalue.indexOf(pre)!=-1)
	    		  bean.set("check","checked");
	    	  else
	    		  bean.set("check","");
	          dblist.add(bean);
	      }
	      int index = dblist.size();
	      if(dblist.size()<6){
	    	  for(int i=0;i<6-index;i++){
	    		  LazyDynaBean bean = new LazyDynaBean();
	    		  bean.set("dbid","");
		    	  bean.set("dbname","");
		    	  bean.set("flag","");
		    	  bean.set("pre","");
		    	  bean.set("check","");
		    	  dblist.add(bean);
	    	  }
	      }
	    	  
	    } catch(SQLException sqle)
	    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
		{
		       this.getFormHM().put("dbprelist",dblist);
		}
		
	}
}
