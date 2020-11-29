package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveReport4Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String sava_type=(String)hm.get("sava_type");  // 1：保存  2:提交
			String id=(String)this.getFormHM().get("id");
			String opt=(String)this.getFormHM().get("opt");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String current_values=(String)this.getFormHM().get("current_values");

			
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			ab.saveU04Values(unitcode,id,vo.getString("theyear"),current_values);	
			
			
			int flag=0;
			if("2".equals(sava_type))
				flag=1;
			ab.saveReportStatus(unitcode,"U04",id,flag);
			ArrayList dataHeadList=ab.getDataHeadList_U04();
			ArrayList u04DataList=ab.getU04DataList(id,unitcode,dataHeadList);
			this.getFormHM().put("reportStatus",String.valueOf(flag));
			this.getFormHM().put("dataHeadList", dataHeadList);
			this.getFormHM().put("u04DataList", u04DataList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
