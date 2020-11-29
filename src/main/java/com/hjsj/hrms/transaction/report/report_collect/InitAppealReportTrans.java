package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitAppealReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			hm.remove("a_code");
			this.getUserView().getHm().remove("statusInfo");
			this.getUserView().getHm().remove("lookInfo");
			
			
			String isSubNode="false";
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			tt_organization.setValidedateflag("1");
			RecordVo a_selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			if(a_selfVo==null)
				throw new Exception(ResourceFactory.getProperty("edit_report.info11")+"!");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select * from tt_organization where parentid=";
			sql+=" (select t.unitcode from tt_organization t,operuser o where o.unitcode=t.unitcode  and o.username='"+this.getUserView().getUserName()+"')";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				isSubNode="true";
			this.getFormHM().put("isSubNode", isSubNode);
			String unitcode=a_selfVo.getString("unitcode");
			this.getFormHM().put("unitcode",unitcode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
