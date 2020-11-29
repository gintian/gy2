package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class ViewFeastTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String feast_id=(String)hm.get("feast_ida");
        String flag=(String)this.getFormHM().get("flag");
        if("1".equals(flag))
            return;
           ContentDAO dao=new ContentDAO(this.getFrameconn());
           RecordVo vo=new RecordVo("kq_feast");
          try
          {
             vo.setString("feast_id",feast_id);
             vo=dao.findByPrimaryKey(vo);
             vo.setString("item_color","#FF3300");
          }
          catch(SQLException sqle)
          {
  	        sqle.printStackTrace();
	        throw GeneralExceptionHandler.Handle(sqle);            
          }
          finally
          {
             this.getFormHM().put("feast",vo);
       
          }
	}

}
