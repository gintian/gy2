package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
public class SearchReportListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String tsortid=(String)hm.get("tsortid");
			String unitcode=(String)hm.get("unitcode");
			
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			RecordVo a_selfVo=tt_organization.getSelfUnit2(unitcode);
			String report=a_selfVo.getString("report");
			String analysereports = a_selfVo.getString("analysereports");
			String analysereportflag = (String)this.getFormHM().get("analysereportflag");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select tabid,name from tname where tsortid="+tsortid+" order by tsortid,tabid  ");
			ArrayList list=new ArrayList();
			while(this.frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				String tabid=this.frowset.getString("tabid");
				abean.set("tabid",tabid);
				abean.set("name",this.frowset.getString("name"));
				if(analysereportflag!=null&& "1".equals(analysereportflag)){//&&analysereports.length()>0
					if(analysereports.indexOf(","+tabid+",")==-1)
					{
						abean.set("flag","0");
					}
					else
					{
						abean.set("flag","1");
					}
				}else{
				if(report.indexOf(","+tabid+",")!=-1)
				{
					abean.set("flag","0");
				}
				else
				{
					abean.set("flag","1");
				}
				}
				
				list.add(abean);
			}
			
			this.getFormHM().put("reportList",list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
