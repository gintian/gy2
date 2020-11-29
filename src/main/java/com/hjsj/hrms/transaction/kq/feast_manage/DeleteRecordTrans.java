package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 String hols_status=(String)this.getFormHM().get("hols_status");
		 if(selectedinfolist==null||selectedinfolist.size()==0)
	            return;
	     try
		 {
	         	 ContentDAO dao=new ContentDAO(this.getFrameconn()); 
	         	 StringBuffer sql=new StringBuffer();
	         	 sql.append("delete from Q17");
	         	 sql.append(" where a0100=? and nbase=? and q1701=?");
	         	 sql.append(" and q1709=?");
	         	 ArrayList list=new ArrayList();
	         	 String a0100=null;
	    		 String nbase=null;
	    		 String q1701=null;//年
	    		 String q1709=null;
	         	 for(int i=0;i<selectedinfolist.size();i++)
	             {
	         		  LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
	         		  ArrayList one_list=new ArrayList();
	         		  a0100=rec.get("a0100").toString()!=null?rec.get("a0100").toString():"";
		    		  nbase=rec.get("nbase").toString()!=null?rec.get("nbase").toString():"";
		    		  q1701=rec.get("q1701").toString()!=null?rec.get("q1701").toString():"";//年
		    		  q1709=hols_status;
		    		  one_list.add(a0100);
		    		  one_list.add(nbase);
		    		  one_list.add(q1701);
		    		  one_list.add(q1709);
		    		  list.add(one_list);
	             }
	         	 dao.batchUpdate(sql.toString(),list);
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}

}
