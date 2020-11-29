package com.hjsj.hrms.transaction.general.chkformula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class AddFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String tableid = (String)reqhm.get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		reqhm.remove("tableid");
		
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"0";
		reqhm.remove("flag");
		
		String chkid = (String)reqhm.get("chkid");
		chkid=chkid!=null&&chkid.trim().length()>0?chkid:"no";
		reqhm.remove("chkid");
		
		String name = "";
		String information="";
		
		if(!"no".equals(chkid)){
			StringBuffer buf = new StringBuffer();
			buf.append("select Name,Information from hrpChkformula where");
			buf.append(" chkid='");
			buf.append(chkid);
			buf.append("'");
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(buf.toString());
				while(this.frowset.next()){
					name=this.frowset.getString("Name");
					information=this.frowset.getString("Information");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("tabid",tableid);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("chkid",chkid);
		this.getFormHM().put("name",name);
		this.getFormHM().put("information",information);
	}

}
