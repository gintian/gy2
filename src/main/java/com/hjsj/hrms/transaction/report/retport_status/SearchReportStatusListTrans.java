package com.hjsj.hrms.transaction.report.retport_status;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class SearchReportStatusListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			
			String unitCode="";
			ArrayList reportSetList=new ArrayList();
			ArrayList subUnitList=new ArrayList();
			ArrayList tabDataList=new ArrayList();
			HashMap   setTabCountMap=new HashMap();
			String    tableHtml="";
			
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			//	取得本人的填报单位信息
			RecordVo selfVo=null;
			if("init".equals(opt))
				selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			else
				selfVo=tt_organization.getSelfUnit2(opt);
			if(selfVo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));
			if(selfVo.getString("reporttypes").trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info11")+"！"));
			unitCode=selfVo.getString("unitcode");
			reportSetList=tt_organization.getSelfSortList(this.userView.getUserName());
			{
				RecordVo a_vo=new RecordVo("tsort");
				a_vo.setInt("tsortid",-1);
				a_vo.setString("name","");
				reportSetList.add(a_vo);
			}
			subUnitList=tt_organization.getUnderUnitList(unitCode,1);
			tabDataList=tt_organization.getUnitAppealData(subUnitList,reportSetList);
			setTabCountMap=tt_organization.getSetTabCountMap(selfVo.getString("reporttypes").substring(0,selfVo.getString("reporttypes").trim().length()-1));
			String theadHtml=tt_organization.getTheadHtml(reportSetList,setTabCountMap);
			String tabBodyHtml=tt_organization.getTabBody(subUnitList,tabDataList,reportSetList,unitCode);
			tableHtml=theadHtml+tabBodyHtml;
			
			RecordVo self_Vo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			String selfUnitcode=self_Vo.getString("unitcode");
			this.getFormHM().put("selfUnitcode", selfUnitcode);
			this.getFormHM().put("tableHtml",tableHtml);
			this.getFormHM().put("unitCode",unitCode);
			this.getFormHM().put("unitName", selfVo.getString("unitname"));
			this.getFormHM().put("reportSetList",reportSetList);
			this.getFormHM().put("subUnitList",subUnitList);
			this.getFormHM().put("tabDataList",tabDataList);
			this.getFormHM().put("setTabCountMap",setTabCountMap);
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			this.getFormHM().put("returnflag", dxt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
