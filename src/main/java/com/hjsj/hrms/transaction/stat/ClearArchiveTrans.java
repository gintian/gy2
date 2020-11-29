package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ClearArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String id=(String)this.getFormHM().get("id");;
		String sql = "update sname set archive_type=null,archive_set=null,condid=null,nbase=null,archive=null where id="+id;
		String msg="error";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.update(sql);
			sql = "update SLegend set archive_field=null where id="+id;
			dao.update(sql);
			msg = "ok";
		}catch(Exception e){
			e.printStackTrace();
			msg = "error";
		}finally{
			this.getFormHM().put("msg", msg);
		}
	}

}
