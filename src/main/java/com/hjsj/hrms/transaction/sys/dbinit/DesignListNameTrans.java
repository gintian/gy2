package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:构建时生成表名</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 28, 2009:5:05:47 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DesignListNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String tableid = (String)hm.get("tableid");
		String tablename = this.tablename(tableid);
		this.getFormHM().put("tableid", tableid);  
		this.getFormHM().put("tablename", tablename);  
	}
	
	public String tablename(String id){
		String name="";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="select fieldsetid,customdesc from fieldset where fieldsetid='"+id+"'";
			RowSet rowSet = dao.search(sql.toString());
			while(rowSet.next()){
				name = rowSet.getString("customdesc");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
}
