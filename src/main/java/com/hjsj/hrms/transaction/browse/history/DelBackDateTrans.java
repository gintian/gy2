package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:BackDateTrans.java</p>
 * <p>Description>:BackDateTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 23, 2010 12:01:20 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class DelBackDateTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String strId=(String) this.getFormHM().get("strId");
		strId=strId==null||strId.length()<1?"-1":strId.substring(0,strId.length()-1);
		//System.out.println(strId);
		if(!"-1".equalsIgnoreCase(strId)){
			List sqllist=new ArrayList();
			sqllist.add("delete hr_emp_hisdata where id in ("+strId+")");
			sqllist.add("delete hr_hisdata_list where id in ("+strId+")");
			try {
				dao.batchUpdate(sqllist);
				this.getFormHM().put("mess", "ok");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
