package com.hjsj.hrms.transaction.gz.premium.param;


import com.hrms.frame.codec.SafeCode;
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
public class SaveCalculaCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String item = (String)hm.get("item");
		item=item!=null&&item.trim().length()>0?item:"";

		String setid = (String)hm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		
		String conditions = (String)hm.get("conditions");
		conditions=conditions!=null&&conditions.trim().length()>0?conditions:"";
		conditions = SafeCode.decode(conditions);
		
		
		String sqlstr = "update bonusformula set cond='"+conditions+"' where setid='"+setid+"' and itemid="+item;
		try {
			dao.update(sqlstr);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("info","ok");
	}

}
