package com.hjsj.hrms.transaction.org.yfileschart;

import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SearchOrgOptionTrans extends IBusiness {

	public void execute() throws GeneralException {

	      String report_relations = (String)this.formHM.get("report_relations");
	      int type = OrgMapBo.ORGMAP;
	      if("yes".equals(report_relations))
	    	  type = OrgMapBo.POSRELATION;
		
	      OrgMapBo orgmapbo = new OrgMapBo();
	      HashMap map = orgmapbo.getMapOptions(type);
	      
	      Iterator ite = map.keySet().iterator();
	      while(ite.hasNext()){
	    	  String  key = (String)ite.next();
	    	  this.formHM.put(key, map.get(key));
	      }
	      
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
	        /**应用库前缀过滤条件*/
	        this.getFormHM().put("dbcond",cond.toString());
	}

}
