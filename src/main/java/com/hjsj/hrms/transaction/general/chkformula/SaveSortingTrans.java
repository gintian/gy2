package com.hjsj.hrms.transaction.general.chkformula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		
		String[] fitem = sort.split(",");;
		
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		if(tabid.length()>0){
			ArrayList valuelist = new ArrayList();
			
			for(int i=0;i<fitem.length;i++){
				if(fitem[i]!=null&&fitem[i].length()>0){
					ArrayList list = new ArrayList();
					list.add((i+1)+"");
					list.add(fitem[i]);
					valuelist.add(list);
				}
			}
			String sqlstr = "update hrpChkformula set seq=? where chkId=?";
			try {
				dao.batchUpdate(sqlstr,valuelist);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		hm.put("info","ok");
	}

}
