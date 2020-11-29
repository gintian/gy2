package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class DragNodesTrans extends IBusiness {
	public void execute() throws GeneralException {
		String fromid = (String)this.getFormHM().get("fromid");
		fromid = PubFunc.decrypt(SafeCode.decode(fromid));
		String toid = (String)this.getFormHM().get("toid");
		toid = PubFunc.decrypt(SafeCode.decode(toid));
		String table =(String)this.getFormHM().get("table");
		String primarykey_column_name =(String)this.getFormHM().get("primarykey_column_name");
		String father_column_name =(String)this.getFormHM().get("father_column_name");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("root".equalsIgnoreCase(toid)){
			String sql = "update "+table+" set "+father_column_name+" = '"+fromid+"' where "+primarykey_column_name +" = '"+fromid+"'";
			try {
				dao.update(sql);
			} catch (SQLException e) {e.printStackTrace();}
		}else{
			String sql ="update "+table+" set "+father_column_name +" = '"+toid+"' where "+primarykey_column_name+" = '"+fromid+"'";
			try {
				dao.update(sql);
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
}
