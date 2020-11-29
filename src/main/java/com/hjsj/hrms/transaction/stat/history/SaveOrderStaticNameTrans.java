package com.hjsj.hrms.transaction.stat.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveOrderStaticNameTrans extends IBusiness{
	
	 
	  public void execute() throws GeneralException 
	  {
		  ArrayList order_fields=(ArrayList)this.getFormHM().get("order_fields");
		  if(order_fields==null||order_fields.size()<=0)
			  return;
		  String statid = (String)this.getFormHM().get("statid");
		  this.getFormHM().remove("statid");
		  int maxOrder = 0;
		  StringBuffer sql=new StringBuffer();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());    		  
		  if (statid == null || "".equals(statid) || "-1".equals(statid))
		      sql.append("update hr_hisdata_sname set snorder=? where id=?");
		  else {
		      sql.append("update HR_HISDATA_SLEGEND set norder=? where id=? and norder=?");
		      maxOrder = getSlegendMaxOrder(dao, statid);
		  }
		  try{
			  ArrayList list=new ArrayList();
        	  for(int i=1;i<=order_fields.size();i++)
        	  {
        		  ArrayList one_list=new ArrayList();    		  
        		  String id=(String)order_fields.get(i-1);
        		  one_list.add(new Integer(i) + maxOrder); 
        		  if (statid == null || "".equals(statid) || "-1".equals(statid))
        		      one_list.add(id);
        		  else {
                      one_list.add(statid);
                      one_list.add(id);
                  }
        		  list.add(one_list);
        	  }
        	  
			  dao.batchUpdate(sql.toString(),list);
			  this.getFormHM().put("types", "ok");
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  private int getSlegendMaxOrder(ContentDAO dao, String statid) {
	      int maxorder = 0;
          StringBuffer sql = new StringBuffer();
          sql.append("SELECT MAX(norder) maxorder FROM HR_HISDATA_SLEGEND WHERE id=" + statid);
          try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next())
                maxorder = this.frowset.getInt("maxorder");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return maxorder;
	  }
	  
}
