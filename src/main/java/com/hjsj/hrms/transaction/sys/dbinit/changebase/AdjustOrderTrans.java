package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:AdjustOrderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 4, 2008:3:06:38 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class AdjustOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sql = "select * from dbname order by dbid";
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList dbnamelist = new ArrayList();
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				CommonData data = new CommonData();
				data.setDataName(this.frowset.getString("dbname"));
				data.setDataValue(this.frowset.getString("dbname")+"`"+this.frowset.getString("pre"));
				dbnamelist.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.formHM.put("dbnamelist",dbnamelist);
	}

}
