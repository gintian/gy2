package com.hjsj.hrms.transaction.gz.gz_amount.tax;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		
		String[] fitem = sort.split(",");
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		String sqlstr = "update salaryset set sortid=? where itemid=? and salaryid=?";
		ArrayList list = new ArrayList();
		for(int i=0;i<fitem.length;i++){
			ArrayList fitemlist = new ArrayList();
			fitemlist.add(i+"");
			fitemlist.add(fitem[i]);
			fitemlist.add(salaryid);	
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
