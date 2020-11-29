package com.hjsj.hrms.transaction.gz.tempvar;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 *<p>Title:</p> 
 *<p>Description:设置临时变量共享</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class ShareSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String nid=(String)this.getFormHM().get("nid");
		nid=nid!=null?nid:"";
		
		String cstate=(String)this.getFormHM().get("cstate");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"null";

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if("null".equals(cstate)){
				dao.update("update midvariable set cState="+cstate+" where nId="+nid);
			}else{
				dao.update("update midvariable set cState='"+cstate+"' where nId="+nid);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
