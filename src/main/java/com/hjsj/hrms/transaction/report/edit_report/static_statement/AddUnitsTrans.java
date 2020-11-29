package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class AddUnitsTrans extends IBusiness{
	public void execute()throws GeneralException{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String scopeid=(String)hm.get("scopeid");
		String sql="select * from tscope where scopeid="+scopeid;
		String scopename="";
		String scopeunitids="";
		String scopeownerunit="";
		ContentDAO dao=new ContentDAO(this.frameconn);
		
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				scopename=this.frowset.getString("name");
				scopeunitids=Sql_switcher.readMemo(this.frowset, "units");
				scopeownerunit=this.frowset.getString("owner_unit");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("scopename", scopename);
		this.getFormHM().put("scopeid", scopeid);
		this.getFormHM().put("odscopeunitsids", scopeunitids);
		this.getFormHM().put("scopeunitsids", "");
		this.getFormHM().put("scopeunits", "");
	}
}
