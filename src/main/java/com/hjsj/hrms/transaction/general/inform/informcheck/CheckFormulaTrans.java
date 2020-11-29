package com.hjsj.hrms.transaction.general.inform.informcheck;

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
public class CheckFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null?itemid:"";
		String itemdesc = (String)this.getFormHM().get("itemdesc");
		itemdesc=itemdesc!=null?itemdesc:"";
		
		String infor = "no";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select AuditingFormula,AuditingInformation from fielditem where ");
			buf.append("  itemid='");
			buf.append(itemid);
			buf.append("'");
			String AuditingFormula="";
			String AuditingInformation="";
			this.frowset = dao.search(buf.toString());
			while(this.frowset.next()){
				AuditingFormula=this.frowset.getString("AuditingFormula");
				AuditingInformation=this.frowset.getString("AuditingInformation");
			}
			AuditingFormula=AuditingFormula!=null&&AuditingFormula.trim().length()>0?AuditingFormula:"";
			AuditingInformation=AuditingInformation!=null&&AuditingInformation.trim().length()>0?AuditingInformation:"";
			if(AuditingInformation.trim().length()>0||AuditingInformation.trim().length()>0){
				infor = "ok";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("itemdesc",itemdesc);
		this.getFormHM().put("itemid",itemid);
	}

}
