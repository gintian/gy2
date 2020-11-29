package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowBatchComputeTrans extends IBusiness {
	
	
	public void execute() throws GeneralException {
		
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String unitcode=(String)hm.get("unitcode");
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			
			
			
//			取得本人的填报单位信息
			tt_organization.setValidedateflag("1");
			RecordVo selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			if(selfVo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));
			if(selfVo.getString("reporttypes").trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info11")+"！"));
				
			ArrayList sortIdList=new ArrayList();
			String    sortid="0";
			this.frowset=dao.search("select * from tsort where tsortid in ("+selfVo.getString("reporttypes").substring(0,selfVo.getString("reporttypes").lastIndexOf(","))+")");
			int i=0;
			while(this.frowset.next())
			{
				if(i==0)
					sortid=this.frowset.getString("tsortid");
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("sortid", this.frowset.getString("tsortid"));
				abean.set("sortname", this.frowset.getString("name")); 
				sortIdList.add(abean);
				i++;
			}
			this.getFormHM().put("c_sortIdList",sortIdList);
			TnameExtendBo tnameExtendBo=new TnameExtendBo(this.getFrameconn());
			ArrayList tableList=tnameExtendBo.getTableNameList2(sortid,this.getUserView());
			this.getFormHM().put("c_tableList",tableList);
			
			ArrayList c_unitList=new ArrayList(); 
			this.getFormHM().put("c_unitList",tt_organization.getAllUnitInfo(unitcode,"1",sortid));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
