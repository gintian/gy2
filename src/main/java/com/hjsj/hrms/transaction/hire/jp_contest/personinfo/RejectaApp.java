package com.hjsj.hrms.transaction.hire.jp_contest.personinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:RejectaApp.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 22, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class RejectaApp extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selectedlist = (ArrayList)this.getFormHM().get("selectedlist");
		ArrayList list = new ArrayList();
		for(int i=0;i<selectedlist.size();i++){
			LazyDynaBean bean = (LazyDynaBean)selectedlist.get(i);
			String a0100 = (String)bean.get("a0100");
			String z0700 = (String)bean.get("zp_z0700");
			ArrayList templist = new ArrayList();
			templist.add("07");
			templist.add(a0100);
			templist.add(z0700);
			list.add(templist);
		}
		String sql = "update zp_apply_jobs set state=? where a0100 =? and z0700 =?";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.batchUpdate(sql,list);
		} catch (SQLException e) {e.printStackTrace();}
	}

}
