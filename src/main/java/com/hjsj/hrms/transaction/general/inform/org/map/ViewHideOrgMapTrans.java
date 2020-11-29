package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ViewHideOrgMapTrans extends IBusiness {

	public void execute() throws GeneralException {
		String msg = "ok";
		try{
			String codeitemid = (String)this.getFormHM().get("codeitemid");
			String flag = (String)this.getFormHM().get("flag");
			String orgtype=(String)this.getFormHM().get("orgtype");
			String sql = "update organization set view_chart="+flag+" where codeitemid like '"+codeitemid+"%'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.update(sql);
			sql = "update vorganization set view_chart="+flag+" where codeitemid like '"+codeitemid+"%'";
			dao.update(sql);
		}catch(Exception e){
			msg = "error";
			e.printStackTrace();
		}finally{
			this.formHM.put("msg", msg);
		}
	}

}
