package com.hjsj.hrms.transaction.general.salarychange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		
		String tabid = (String)hm.get("tableid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String[] fitem = sort.split("`");
		
		String sqlstr = "update gzAdj_formula set nsort=? where tabid=? and id=?";
		ArrayList list = new ArrayList();
		for(int i=0;i<fitem.length;i++){
			ArrayList fitemlist = new ArrayList();
			fitemlist.add(i+"");
			fitemlist.add(tabid);
			fitemlist.add(fitem[i]);
			list.add(fitemlist);
		}
		try {
			dao.batchUpdate(sqlstr,list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("info","ok");
	}

}
