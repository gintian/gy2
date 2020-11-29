package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class EditFeastTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String fid=(String)hm.get("a_id");
        fid = PubFunc.decrypt(fid);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
         RecordVo vo=new RecordVo("kq_feast");
           try
           {
        	    vo.setString("feast_id",fid);
      	      	vo=dao.findByPrimaryKey(vo);
            }
            catch(SQLException sqle)
            {
  	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);            
            }
            finally
            {
                this.getFormHM().put("feast_name",vo.getString("feast_name"));
                this.getFormHM().put("feast_id",fid);
                String date=vo.getString("feast_dates");
                this.getFormHM().put("sdate",date);
                
            }

	}

}
