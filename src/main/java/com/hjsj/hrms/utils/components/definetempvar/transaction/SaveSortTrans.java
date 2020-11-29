package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:SaveSortTrans.java</p>
 * <p>Description>:临时变量调整顺序</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 10, 2017 4:31:55 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class SaveSortTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sort = (String)hm.get("sorting");
			sort=sort!=null&&sort.trim().length()>0?sort:"";
			String[] nid = sort.split(",");
			ArrayList list = new ArrayList();
			for(int i=0;i<nid.length;i++){
				ArrayList sortList = new ArrayList();
				sortList.add(i+1);
				sortList.add(nid[i]);
				list.add(sortList);
			}
			String sql = "update midvariable set sorting=? where nid=?";
			dao.batchUpdate(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
