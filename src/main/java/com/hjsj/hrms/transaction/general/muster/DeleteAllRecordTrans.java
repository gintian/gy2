package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class DeleteAllRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String dbpre=dbpre=(String)this.getFormHM().get("dbpre");
		dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
		
		String infor_kind=(String)this.getFormHM().get("infor_Flag");
		infor_kind=infor_kind!=null&&infor_kind.trim().length()>0?infor_kind:"1";
		
		String flag = "no";
		
		if(tabid.trim().length()>0){
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			String tabname=musterbo.getTableName(infor_kind,dbpre,tabid,this.userView.getUserName());
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.update("delete from "+tabname);
				flag = "ok";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("flag",flag);
	}

}
