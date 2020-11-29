package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditDefinitionTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
        String it=(String)hm.get("set_ida");
        

        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("ds_key_factortype");
        try
        {
  	      	vo.setString("typeid",it);
  	      	vo=dao.findByPrimaryKey(vo);
        }
        catch(Exception sqle)
        {
 	          sqle.printStackTrace();
	          throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
         	this.getFormHM().put("type",vo.getString("name"));
            this.getFormHM().put("sel",vo.getString("status"));
            this.getFormHM().put("typeid",it);
        }

	}

}
