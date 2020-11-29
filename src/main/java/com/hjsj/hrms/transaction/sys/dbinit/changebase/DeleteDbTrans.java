package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hjsj.hrms.businessobject.sys.dbinit.InitDatajob;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteDbTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 6, 2008:2:01:47 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DeleteDbTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = (ArrayList) this.getFormHM().get("dbselectedlist");
		ContentDAO dao = new ContentDAO(this.frameconn);
		InitDatajob datajob = new InitDatajob(dao,this.frameconn);
		for(int i=0;i<list.size();i++){
			RecordVo dbvo = (RecordVo)list.get(i);
			String pre = dbvo.getString("pre");
			datajob.DeleteDB(pre);
		}
	}

}
