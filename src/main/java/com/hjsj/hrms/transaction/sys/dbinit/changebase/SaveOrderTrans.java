package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveOrderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 4, 2008:3:06:31 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dbnamelist = (ArrayList)this.getFormHM().get("dbnamelist");
		String sql = "select * from dbname order by dbid";
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList dblist = new ArrayList();
		try {
			this.frowset = dao.search(sql.toString());
			int index = 0;
			String[] dbinfo = new String[2];
			while(this.frowset.next()){
				dbinfo = dbnamelist.get(index).toString().split("`");
				RecordVo vo = new RecordVo("dbName");
				vo.setInt("dbid",this.frowset.getInt("dbid"));
				vo.setString("dbname",dbinfo[0]);
				vo.setString("pre",dbinfo[1]);
				dblist.add(vo);
				index++;
			}
			dao.updateValueObject(dblist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
