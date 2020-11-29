package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitReportCollectTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.frameconn);
		
		try
		{
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			//	取得本人的填报单位信息
			tt_organization.setValidedateflag("1");
			RecordVo selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			if(selfVo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));
			if(selfVo.getString("reporttypes").trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info11")+"！"));
				
			ArrayList sortIdList=new ArrayList();
			String    sortid="0";
			this.frowset=dao.search("select * from tsort where tsortid in ("+selfVo.getString("reporttypes").substring(0,selfVo.getString("reporttypes").lastIndexOf(","))+") order by tsortid");
			int i=0;
			while(this.frowset.next())
			{
				if(i==0)
					sortid=this.frowset.getString("tsortid");
				CommonData vo=new CommonData(this.frowset.getString("tsortid"),this.frowset.getString("tsortid")+":"+this.frowset.getString("name"));
				sortIdList.add(vo);
				i++;
			}
			
			String a_sortid=(String)hm.get("sortid");
			if(!"@".equals(a_sortid))
				sortid=a_sortid;
			
			// 得到直属单位信息
			ArrayList underUnitList=tt_organization.getUnderUnitList(selfVo.getString("unitcode"),sortid);
			ArrayList tempList=tt_organization.getUnderUnitList(selfVo.getString("unitcode"));
			if(tempList.size()>=1){
				this.getFormHM().put("isLeafUnit","0");
				
			}
			else{
				this.getFormHM().put("isLeafUnit","1");
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report_collect.info")));
			}
			

			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.getFrameconn());
			//ArrayList tableList=tnameExtendBo.getTableNameList(selfVo.getString("reporttypes").substring(0,selfVo.getString("reporttypes").lastIndexOf(",")),this.getUserView());
			ArrayList tableList=tnameExtendBo.getTableNameList2(sortid,this.getUserView());
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			this.getFormHM().put("returnflag", dxt);
			this.getFormHM().put("sortid",sortid);
			this.getFormHM().put("sortIdList",sortIdList);
			this.getFormHM().put("tableList",tableList);
			this.getFormHM().put("unitcode",selfVo.getString("unitcode"));
			this.getFormHM().put("underUnitList",underUnitList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}

	


	
}
