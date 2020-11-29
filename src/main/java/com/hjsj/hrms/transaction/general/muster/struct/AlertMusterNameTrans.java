package com.hjsj.hrms.transaction.general.muster.struct;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class AlertMusterNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String mustername = (String)this.getFormHM().get("mustername");
		mustername=mustername!=null?mustername:"";
		
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null?tabid:"";
		if(tabid.trim().length()>0&&mustername.trim().length()>0){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			if(tabid.indexOf("X")!=-1){
				sqlstr.append(" lstyle set styledesc='");
				sqlstr.append(mustername);
				sqlstr.append("' where styleid='");
				sqlstr.append(tabid.substring(1));
				sqlstr.append("'");
			}else{
				sqlstr.append(" lname set hzname='");
				sqlstr.append(mustername);
				sqlstr.append("',title='");
				sqlstr.append(mustername);
				sqlstr.append("' where tabid='");
				sqlstr.append(tabid);
				sqlstr.append("'");
			}
			try {
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
