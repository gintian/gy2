package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:InfoBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 4, 2008:9:35:29 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class InfoBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sql = "select * from dbname order by dbid";
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList dblist = new ArrayList();
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				RecordVo vo = new RecordVo("dbName");
				vo.setInt("dbid",this.frowset.getInt("dbid"));
				vo.setString("dbname",this.frowset.getString("dbname"));
				vo.setString("pre",this.frowset.getString("pre"));
				dblist.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.formHM.put("dblist",dblist);
	}

}
