package com.hjsj.hrms.transaction.gz.premium.param;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class AlertUseFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String useflag = (String)this.getFormHM().get("useflag");
		useflag=useflag!=null&&useflag.trim().length()>0?useflag:"";
		
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String setid = (String)this.getFormHM().get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		
		StringBuffer strsql = new StringBuffer();
		strsql.append("update bonusformula set useflag=");
		strsql.append(useflag);
		strsql.append(" where setid=");
		strsql.append("'"+setid+"'");
		strsql.append(" and itemid=");
		strsql.append(itemid);
		strsql.append("");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.update(strsql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
