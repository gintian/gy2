package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SortItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
		
		String nextitemid = (String)this.getFormHM().get("nextitemid");
		nextitemid=nextitemid!=null&&nextitemid.trim().length()>0?nextitemid:"";
		
		String affteritemid = (String)this.getFormHM().get("affteritemid");
		affteritemid=affteritemid!=null&&affteritemid.trim().length()>0?affteritemid:"";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select displayid,itemid from fielditem where itemid in('");
		sqlstr.append(itemid+"'");
		if("down".equalsIgnoreCase(sortitem)){
			sqlstr.append(",'"+nextitemid+"'");
		}else{
			sqlstr.append(",'"+affteritemid+"'");
		}
		sqlstr.append(") order by displayid");
		try {
			this.frowset = dao.search(sqlstr.toString());
			ArrayList list = new ArrayList();
			while(this.frowset.next()){
				ArrayList valuelist = new ArrayList();
				String displayid = this.frowset.getString("displayid");
				String fielditemid = this.frowset.getString("itemid");
				
				if(list.size()>0){
					ArrayList listvalue = (ArrayList)list.get(0);
					String sortid = (String)listvalue.get(0);
					String sortitemid = (String)listvalue.get(1);
					list.clear();
					ArrayList newlist = new ArrayList();
					newlist.add(displayid);
					newlist.add(sortitemid);
					list.add(newlist);
					
					valuelist.add(sortid);
					valuelist.add(fielditemid);
					list.add(valuelist);
				}else{
					valuelist.add(displayid);
					valuelist.add(fielditemid);
					list.add(valuelist);
				}
			}
			dao.batchUpdate("update fielditem set displayid=? where itemid=?", list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
