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
public class SavaFormulaTrnas extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String item = (String)hm.get("item");
		item=item!=null&&item.trim().length()>0?item:"";

		String setid = (String)hm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		
		String formula = (String)hm.get("formula");
		formula=formula!=null&&formula.trim().length()>0?formula:"";
		formula=SafeCode.decode(formula);
		String cond = (String)hm.get("cond");
		cond=cond!=null&&cond.trim().length()>0?cond:"";
		cond=SafeCode.decode(cond);
		String fmode = (String)hm.get("fmode");
		fmode=fmode!=null&&fmode.trim().length()>0?fmode:"";
		String smode = (String)hm.get("smode");
		smode=smode!=null&&smode.trim().length()>0?smode:"0";
		String sqlstr = "";
		//更新条件:itemid
		if("0".equals(fmode)){
			 sqlstr = "update bonusformula set rexpr='"+formula+"' where itemid="+item;
		}else if("1".equals(fmode)){
			 sqlstr = "update bonusformula set rexpr='"+formula+"', smode="+smode+" where itemid="+item;
		}else if("2".equals(fmode)){
			 sqlstr = "update bonusformula set rexpr='"+formula+"',cond='"+cond+"',smode="+smode+" where itemid="+item;
		}
			
		try {
			dao.update(sqlstr);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("info","ok");
	}

}
