package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditTurnRestTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
         String it=(String)hm.get("t_id");
         
         ContentDAO dao=new ContentDAO(this.getFrameconn());
         RecordVo vo=new RecordVo("kq_turn_rest");
         String rt = "";
         String td="";
         try
         {
   	      	vo.setString("turn_id",it);
   	      	vo=dao.findByPrimaryKey(vo);

   	      	 rt=vo.getString("week_date");
   	         td=vo.getString("turn_date");

         }
         catch(Exception sqle)
         {
  	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);            
         }
         finally
         {
              this.getFormHM().put("rdate",rt);
              this.getFormHM().put("tdate",td);
              this.getFormHM().put("tid",it);
        }

	}

}
