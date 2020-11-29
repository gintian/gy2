package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class StopStateTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String ids = "";
			if(((String)this.getFormHM().get("selectIds")).trim().length()>0 && this.getFormHM().get("selectIds")!= null)
				ids = (String)this.getFormHM().get("selectIds");
			if(ids == null ||ids.trim().length()<0)
				return;
			ids = ids.substring(1);
			StringBuffer sql = new StringBuffer();
			sql.append("update p02 set p0209 = '09' where p0201 in (");
			sql.append(ids);
			sql.append(")");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	

}
