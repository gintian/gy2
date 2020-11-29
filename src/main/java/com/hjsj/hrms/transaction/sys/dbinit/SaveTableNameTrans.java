package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:构库，保存表名称</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 29, 2009:1:32:42 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveTableNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		String tablename = (String)this.getFormHM().get("tablename");
		this.savetable(tableid, tablename);
	}
	
	public void savetable(String id , String name){
		try {
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			String sql = "Update fieldset set customdesc = '"+name+"' where fieldsetid = '"+id+"'";
			dao.update(sql, new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
