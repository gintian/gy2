/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class DeleteStaticStatementTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
	String scopeid = (String)this.getFormHM().get("scopeid");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	StringBuffer sql = new StringBuffer();
	sql.append("delete from  tscope where scopeid="+scopeid);
	try {
		dao.delete(sql.toString(), new ArrayList());
	} catch (SQLException e) {
		e.printStackTrace();
	}
	this.getFormHM().put("info", "ok");
	}
	
}
