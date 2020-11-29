package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.valueobject.common.FeastType;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFeastTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
 
      StringBuffer sb = new StringBuffer();
      ContentDAO cdao =new ContentDAO(this.getFrameconn());
      ArrayList feastList=new ArrayList();
      try{
    	  if (this.getFormHM().get("need_search") != null && "no".equalsIgnoreCase(this.getFormHM().get("need_search").toString()))
    		  return;
    	  
	      HashMap hashMap = (HashMap)this.getFormHM().get("requestPamaHM");
	      String gw_flag = (String)hashMap.get("flag");
	      hashMap.remove("flag");
	      this.getFormHM().put("gw_flag", gw_flag);
    	   sb.append("select * from kq_feast order by feast_id");
             this.frowset = cdao.search(sb.toString());
             String fnames =null;
             String fdate =null;
             String fid = null;
             int i = 1;
             while(this.frowset.next()){
            	 fid = this.frowset.getString("feast_id");
            	 fnames=this.frowset.getString("feast_name");
            	 fdate=this.frowset.getString("feast_dates");                
            	 FeastType feasty= new FeastType(fid,fnames,fdate,i);
                 feastList.add(feasty);
            	 i++;
      
             }
        }catch(Exception se){
    	  se.printStackTrace();
 	      throw GeneralExceptionHandler.Handle(se);
        }finally{
    	  
 	         this.getFormHM().put("feastList",feastList);
             this.getFormHM().put("feast_id","");
        	 this.getFormHM().put("feast_name","");
        	 this.getFormHM().put("sdate","");
        }
		
	}

}
