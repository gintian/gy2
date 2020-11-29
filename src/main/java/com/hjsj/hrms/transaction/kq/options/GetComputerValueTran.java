package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class GetComputerValueTran extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
		String items=(String)hm.get("akq_item");
		String expr_flag=(String)hm.get("expr_flag");//区分是日计算公式还是月计算公式
	    if(expr_flag==null||expr_flag.length()<=0)
	    	expr_flag="day";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String rest="";
		StringBuffer list = new StringBuffer();
		try{
			 list.append("select c_expr  from kq_item where item_id='");
			 list.append(items);
			 list.append("'");
			  this.frowset =dao.search(list.toString());
			  if (this.frowset.next())
			 { 
				  rest=Sql_switcher.readMemo(this.frowset,"c_expr");
				  if(rest==null||rest.length()<=0)
				  {
					  rest="";
				  }
			 }
		  }
         catch(SQLException sqle)
		 {
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);
		  }
         int s=rest.indexOf("^");
         String c_expr="";
         if("day".equals(expr_flag))
         {
        	 if(s>0)
        	 {
         		c_expr=rest.substring(0,s);
         	 }
         }else if("mo".equals(expr_flag))
         {
        	 if(s>0||rest.indexOf("^")==0)
        	 {
         		c_expr=rest.substring(s+1);
         	 }
         }         
         this.getFormHM().put("c_expr",c_expr);
         this.getFormHM().put("expr_flag",expr_flag);
	}

}
