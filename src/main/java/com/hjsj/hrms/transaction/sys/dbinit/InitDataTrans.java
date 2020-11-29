package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.businessobject.sys.dbinit.InitDatajob;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:InitDataTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 1, 2008:5:33:13 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class InitDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			this.frameconn.setAutoCommit(false);
			ContentDAO dao = new ContentDAO(this.frameconn);
			InitDatajob idj = new InitDatajob(dao,this.frameconn);
			//ArrayList dblist = userView.getPrivDbList();
			ArrayList dblist = com.hrms.hjsj.sys.DataDictionary.getDbpreList();
			idj.InitData(dblist,"TRUNCATE");
			this.frameconn.commit();
		} catch (SQLException e) {
			try {
				this.frameconn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				this.frameconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
