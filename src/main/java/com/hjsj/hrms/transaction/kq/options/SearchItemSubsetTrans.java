package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchItemSubsetTrans extends IBusiness {

	public void execute() throws GeneralException {
		  String items=(String)this.getFormHM().get("tablename");
          StringBuffer sts=new StringBuffer();
          ArrayList list = new ArrayList();
          Field field=null;
  	     ArrayList fieldlist=new ArrayList();
          ContentDAO dao=new ContentDAO(this.getFrameconn());
          try
          {
       	     sts.append("select * from t_hr_busifield where fieldsetid='");
       	     sts.append(items);
       	     sts.append("' and useflag<>'0' and keyflag<>'1'");
	         this.frowset = dao.search(sts.toString());
	         while(this.frowset.next())
	          {
                 CommonData datavo=new CommonData(this.frowset.getString("itemdesc"),this.frowset.getString("itemdesc"));
        	     list.add(datavo);
        	     field=new Field(this.frowset.getString("itemdesc"),this.frowset.getString("itemtype"));
        		 field.setDatatype("string");
        		 fieldlist.add(field);
	           }

           }
           catch(Exception sqle)
           {
 	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);            
           }
           finally
           {
               
              this.getFormHM().put("klist",list);
       	      this.getFormHM().put("fieldlist",fieldlist);
           }

	}

}
