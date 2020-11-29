package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchItemInitTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    ArrayList list=new ArrayList();
	    StringBuffer st=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
        {
	    	
	       st.append("select fieldsetid,fieldsetdesc from t_hr_busitable where id='30'");
	       this.frowset = dao.search(st.toString());
	       while(this.frowset.next())
	       {
	          CommonData datavo=new CommonData(this.frowset.getString("fieldsetid"),this.frowset.getString("fieldsetdesc"));
	          list.add(datavo);
	        }
	    
        }
        catch(Exception sqle)
        {
	        sqle.printStackTrace();
	        throw GeneralExceptionHandler.Handle(sqle);            
        }finally{

	        this.getFormHM().put("setlist",list);
        }
	}

}
