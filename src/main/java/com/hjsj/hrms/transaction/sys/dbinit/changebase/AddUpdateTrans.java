package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class AddUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String vflag = (String)hm.get("vflag");
		hm.remove("vflag");
		this.getFormHM().put("vflag",vflag);
		RecordVo dbvo = new RecordVo("dbname");
		if("0".equalsIgnoreCase(vflag))
			this.getFormHM().put("dbvo",dbvo);
		else{
			String dbid = (String)hm.get("dbid");
			hm.remove("dbid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			dbvo.setInt("dbid",Integer.parseInt(dbid));
			try {
				dbvo = dao.findByPrimaryKey(dbvo);
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("dbvo",dbvo);
		}
	}

}
