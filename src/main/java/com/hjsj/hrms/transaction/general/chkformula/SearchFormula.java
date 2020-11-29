package com.hjsj.hrms.transaction.general.chkformula;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SearchFormula extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String chkid = (String)this.getFormHM().get("chkid");
		chkid=chkid!=null&&chkid.trim().length()>0?chkid:"";

		
		String formula="";
		if(chkid.trim().length()>0){
			StringBuffer buf = new StringBuffer();
			buf.append("select formula from hrpChkformula where");
			buf.append(" chkid='");
			buf.append(chkid);
			buf.append("'");
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(buf.toString());
				while(this.frowset.next()){
					formula=this.frowset.getString("formula");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("formula",SafeCode.encode(formula));
	}

}
