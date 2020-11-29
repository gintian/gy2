package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SetDbTrans extends IBusiness {
	 public void execute() throws GeneralException {
		 String dbpre = (String)this.getFormHM().get("dbpre");
		 HashMap requestPamaHM = (HashMap) this.getFormHM().get("requestPamaHM");//获取请求人员库类型标识  bz 班子 | hb 后背干部 人员库  wangb 20190514 45993
		 String field_falg = (String) requestPamaHM.get("field_falg");
		 LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		 String dbvalue="";
		 //通过标识获取对应人员库  wangb 20190514 45993
		 if("bz".equalsIgnoreCase(field_falg))
			 dbvalue=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);
		 else if("hb".equalsIgnoreCase(field_falg))
			 dbvalue=leadarParamXML.getTextValue(LeadarParamXML.HBDBPRE);
		 String[] dbs = null;
		 if(!(dbpre==null|| "".equalsIgnoreCase(dbpre))){
			 dbs = dbpre.split(",");
		 }
		 ArrayList dblist=new ArrayList();
			StringBuffer strsql=new StringBuffer();
		    strsql.append("select * from dbname order by dbid");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
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
		    	  bean.set("pre",this.getFrowset().getString("pre"));
		    	  if(dbvalue!=null&&dbvalue.indexOf(pre)!=-1)
		    		  bean.set("check","checked");
		    	  else
		    		  bean.set("check","");
		    	  if(dbs!=null)
		    		  for(int i=0;i<dbs.length;i++){
		    			  if(dbs[i].equalsIgnoreCase(this.getFrowset().getString("pre"))){
		    				  bean.set("choose","1");
		    				  break;
		    			  }
		    			  else
		    				  bean.set("choose","0");
		    		  }
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
