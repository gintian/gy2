package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class GetData_SrcTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String itemId=(String)hm.get("akq_item");
        ArrayList list=new ArrayList();
        String str="";
	    StringBuffer st=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
        {
	    	CommonData datav=new CommonData("","");
	          list.add(datav);
	       st.append("select fieldsetid,fieldsetdesc from t_hr_busitable where id='30'");
	       this.frowset = dao.search(st.toString());
	       while(this.frowset.next())
	       {
	    	  if("Q11".equals(this.frowset.getString("fieldsetid"))|| "Q13".equals(this.frowset.getString("fieldsetid"))|| "Q15".equals(this.frowset.getString("fieldsetid")))
	    	  {
	            CommonData datavo=new CommonData(this.frowset.getString("fieldsetid"),this.frowset.getString("fieldsetdesc"));
	            list.add(datavo);
	    	  }
	        }
	       st.delete(0,st.length());
	       st.append("select sdata_src from kq_item where item_id='");
	       st.append(itemId);
	       st.append("'");
	       this.frowset = dao.search(st.toString());
	       while(this.frowset.next())
	    		  str=this.frowset.getString("sdata_src");
        }
        catch(Exception sqle)
        {
	        sqle.printStackTrace();
	        throw GeneralExceptionHandler.Handle(sqle);            
        }finally{
        	this.getFormHM().put("sdata_src",str);
	        this.getFormHM().put("klist",list);
	        this.getFormHM().put("items",itemId);
        }

	}

}
