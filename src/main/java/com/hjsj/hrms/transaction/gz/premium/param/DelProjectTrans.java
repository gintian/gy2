package com.hjsj.hrms.transaction.gz.premium.param;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class DelProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		
		String base = "no";
		
		String itemid= (String)hm.get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		String fmode= (String)hm.get("fmode");
		fmode=fmode!=null&&fmode.trim().length()>0?fmode:"";
		
		String setid= (String)hm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		if(itemid.indexOf(",")!=-1)
			itemid = itemid.substring(0,itemid.length()-1);
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("delete from  bonusformula where setid=");
		strsql.append("'"+setid+"'");
		strsql.append(" and itemid in (");
		strsql.append(itemid);
		strsql.append(") and fmode=");
		strsql.append(fmode);
		
		try {
			dao.update(strsql.toString());
			base = "ok";
		} catch(SQLException e) {
			// TODO Auto-generated catch block
			base = "no";
			e.printStackTrace();
		}
		hm.put("base",base);
	}

}
