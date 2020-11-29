package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveAndContinueAddDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String isVisable="new";
			String cname=(String)this.getFormHM().get("cname");
			String nitemid=(String)this.getFormHM().get("nitemid");
			String nstyleid=(String)this.getFormHM().get("nstyleid");
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("insert into moneyitem (nstyleid,nitemid,cname,nflag) values (");
			sql.append("'"+nstyleid+"','");
			sql.append(nitemid+"','");
			sql.append(cname+"','1')");
			dao.insert(sql.toString(),new ArrayList());
			this.getFormHM().put("cname","");
			this.getFormHM().put("nitemid","");
			this.getFormHM().put("nstyleid",nstyleid);
			this.getFormHM().put("isVisable",isVisable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
