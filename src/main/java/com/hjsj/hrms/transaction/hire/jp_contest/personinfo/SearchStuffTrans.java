package com.hjsj.hrms.transaction.hire.jp_contest.personinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchStuffTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 24, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchStuffTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String nbase = (String)hm.get("nbase");
		String a0100 = (String)hm.get("a0100");
		String z0700 = (String)hm.get("z0700");
		ArrayList list = new ArrayList();
		String sql = "select fileid,name from zp_apply_file where id =(select id from zp_apply_jobs where nbase='"+nbase+"' and a0100='"+a0100+"' and z0700='"+z0700+"') order by id";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id",this.frowset.getString("fileid"));
				bean.set("name",this.frowset.getString("name"));
				list.add(bean);
			}
		} catch (SQLException e) {e.printStackTrace();}
		this.getFormHM().put("stufflist",list);
	}

}
