package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchReport5Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ArrayList dataHeadList_u05=new ArrayList();
			ArrayList dataList_u05=new ArrayList();
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)hm.get("id");
			String unitcode=(String)hm.get("unitcode");
			String opt=(String)hm.get("opt");   //1:可操作  0：只读 
			String from_model=(String)hm.get("from_model");
			hm.remove("from_model");
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			dataHeadList_u05=ab.getDataHeadList_U05();
			dataList_u05=ab.getDataList_U05(dataHeadList_u05, id, unitcode,"");
			
			String t5_desc="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    try
		    {
		    	this.frowset=dao.search("select * from u01 where id="+id+" and unitcode='"+unitcode+"'");
				if(this.frowset.next())
					t5_desc=Sql_switcher.readMemo(this.frowset,"t5_desc"); 
		    }
		    catch(Exception ee)
		    {
		    	
		    }
		    String reportStatus="";
		    this.frowset=dao.search("select flag from tt_calculation_ctrl  where unitcode='"+unitcode+"' and id="+id+" and report_id='U05'");
			if(this.frowset.next())
				reportStatus=this.frowset.getString("flag");
			
			 this.getFormHM().put("flagSub",ab.isSub("U05", ab.getSelfUnitCode(), id, "1"));
			  if("1".equals(ab.isRootUnit(this.getUserView().getUserName()))){
					this.getFormHM().put("rootUnit", "1");
				}else{
					this.getFormHM().put("rootUnit", "0");
				}
		    this.getFormHM().put("reportStatus",reportStatus);
			this.getFormHM().put("from_model",from_model);
			this.getFormHM().put("t5_desc",t5_desc);
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("id",id);
			this.getFormHM().put("unitcode",unitcode);
			this.getFormHM().put("selfUnitcode", ab.getSelfUnitCode());
			this.getFormHM().put("cycleStatus", ab.getCycleStatus(id));
			this.getFormHM().put("dataHeadList_u05", dataHeadList_u05);
			this.getFormHM().put("dataList_u05", dataList_u05);
			this.getFormHM().put("isUnderUnit",ab.isUnderUnit(unitcode));
			this.getFormHM().put("report_id","U05");
			this.getFormHM().put("isCollectUnit",ab.isCollectUnit(unitcode));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
