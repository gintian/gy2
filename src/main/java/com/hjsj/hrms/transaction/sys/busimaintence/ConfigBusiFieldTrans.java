package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class ConfigBusiFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RecordVo fieldVo=new RecordVo("t_hr_busifield");
		String fieldsetid=(String) reqhm.get("fieldsetid");
		String itemid=(String) reqhm.get("itemid");
		reqhm.remove("fieldsetid");
		reqhm.remove("itemid");
		fieldVo.setString("fieldsetid",fieldsetid);
		fieldVo.setString("itemid",itemid);
		try {
			fieldVo=dao.findByPrimaryKey(fieldVo);
			if(reqhm.containsKey("state")){
				String state=(String) reqhm.get("state");
				fieldVo.setString("state",state);
				dao.updateValueObject(fieldVo);
				reqhm.remove("state");
			}
			if(reqhm.containsKey("keyflag")){
				String keyflag=(String) reqhm.get("keyflag");
				fieldVo.setString("keyflag",keyflag);
				dao.updateValueObject(fieldVo);
				reqhm.remove("keyflag");
			}
			if(reqhm.containsKey("ownflag")){
				String ownflag=(String) reqhm.get("ownflag");
				fieldVo.setString("ownflag",ownflag);
				dao.updateValueObject(fieldVo);
				reqhm.remove("ownflag");
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
