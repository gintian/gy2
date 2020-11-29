package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ChangeSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sortid=(String)this.getFormHM().get("sortid");		
		TnameExtendBo tnameExtendBo=new TnameExtendBo(this.getFrameconn());
		ArrayList tableList=tnameExtendBo.getTableNameList2(sortid,this.getUserView());
		
		TTorganization tt_organization=new TTorganization(this.getFrameconn());
		RecordVo selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());
		ArrayList underUnitList=tt_organization.getUnderUnitList(selfVo.getString("unitcode"),sortid);
		StringBuffer unit_str=new StringBuffer("");
		for(int i=0;i<underUnitList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)underUnitList.get(i);
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
