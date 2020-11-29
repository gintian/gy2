package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class GetUnitsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String flag=(String)this.getFormHM().get("flag");   // 1:操作表类  2：操作单位范围
			String operater=(String)this.getFormHM().get("operater");  //1:所有单位  2：直属单位  3：基层单位
			String sortid=(String)this.getFormHM().get("sortid"); 
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			//	取得本人的填报单位信息
			tt_organization.setValidedateflag("1");
			RecordVo selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			if(selfVo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));
			if(selfVo.getString("reporttypes").trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info11")+"！"));
			String unitcode=selfVo.getString("unitcode");
			
			
			ArrayList list=new ArrayList(); 
			list=tt_organization.getAllUnitInfo(unitcode,operater,sortid);
			StringBuffer unit_str=new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				unit_str.append("#"+(String)abean.get("unitcode")+"~"+(String)abean.get("unitname"));
			}
			if(unit_str.length()>1)
			{
				this.getFormHM().put("unit_str",SafeCode.encode(unit_str.substring(1)));
			}
			else
			{
				this.getFormHM().put("unit_str","");
			}	 
			
			
			if("1".equals(flag))
			{
				TnameExtendBo tnameExtendBo=new TnameExtendBo(this.getFrameconn());
				ArrayList tableList=tnameExtendBo.getTableNameList2(sortid,this.getUserView());
				StringBuffer tab_str=new StringBuffer("");
				for(int i=0;i<tableList.size();i++)
				{
					DynaBean abean=(DynaBean)tableList.get(i);
					tab_str.append("#"+(String)abean.get("tabid")+"~"+(String)abean.get("tsortid")+"~"+(String)abean.get("name"));
				}
				if(tab_str.length()>1)
				{
					this.getFormHM().put("tab_str",SafeCode.encode(tab_str.substring(1)));
				}
				else
				{
					this.getFormHM().put("tab_str","");
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
