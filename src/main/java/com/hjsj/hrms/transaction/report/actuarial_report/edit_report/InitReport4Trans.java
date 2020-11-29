package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitReport4Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   //1:可操作  0：只读 
			String id=(String)hm.get("id");
			String unitcode=(String)hm.get("unitcode");
			String reportStatus="-1";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			ArrayList dataHeadList=ab.getDataHeadList_U04();
			ArrayList u04DataList=ab.getU04DataList(id,unitcode,dataHeadList);
			
			this.frowset=dao.search("select flag from tt_calculation_ctrl  where unitcode='"+unitcode+"' and id="+id+" and report_id='U04'");
			if(this.frowset.next())
				reportStatus=this.frowset.getString("flag");
			
			 this.getFormHM().put("flagSub",ab.isSub("U05", ab.getSelfUnitCode(), id, "1"));
			  if("1".equals(ab.isRootUnit(this.getUserView().getUserName()))){
					this.getFormHM().put("rootUnit", "1");
				}else{
					this.getFormHM().put("rootUnit", "0");
				}
			this.getFormHM().put("cycleStatus", ab.getCycleStatus(id));
			this.getFormHM().put("reportStatus",reportStatus);
			this.getFormHM().put("dataHeadList", dataHeadList);
			this.getFormHM().put("u04DataList",u04DataList);
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("id",id);
			this.getFormHM().put("selfUnitcode", ab.getSelfUnitCode());
			this.getFormHM().put("unitcode",unitcode);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
