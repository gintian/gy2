package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitTowDimTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 HashMap hm=this.getFormHM();
		 String info=(String)hm.get("base");
		 if(info==null|| "".equals(info))
				info="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		ArrayList list=new ArrayList();
		try{
				 sql.append("select name,id from sname where InfoKind=");
	             sql.append((int)Integer.parseInt(info));
	             sql.append(" and Type='1'");
	             this.frowset=dao.search(sql.toString());
           	     CommonData sel=new CommonData("#",ResourceFactory.getProperty("label.select.dot"));
           	     list.add(sel);	             
	             while(this.frowset.next())
	             {
	          	   CommonData cdata=new CommonData(this.frowset.getString("id"),(this.frowset.getString("Name")));
	          	   if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
	    		   {
	          		  list.add(cdata);
	    		   }
	             }       
		}
		catch(Exception exx)
	    {
		       exx.printStackTrace();
		       throw GeneralExceptionHandler.Handle(exx);
		    }finally{
			 //初始化标题
		    	this.getFormHM().put("logiclist",list);
		    }

	}

}
