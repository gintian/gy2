package com.hjsj.hrms.transaction.general.chkformula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class DelFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String chkid = (String)this.getFormHM().get("chkid");
		chkid=chkid!=null&&chkid.trim().length()>0?chkid:"";
		if(chkid.trim().length()>0){
			chkid=chkid.replace(",","','");
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				dao.update("delete from hrpChkformula where chkid in('"+chkid+"')");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
