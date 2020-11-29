package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 得到初始子集
 * <p>Title:InitGetSubsetTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 24, 2006 10:23:49 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class InitGetSubsetTrans  extends IBusiness {
   
    private String constant="SYS_OTH_PARAM";
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String subset="";
		String tempflag=(String)hm.get("tempflag");
		if(tempflag!=null&& "kq".equalsIgnoreCase(tempflag))
		{
			subset=SystemConfig.getPropertyValue("appsubset");
			this.getFormHM().put("collectflag", "other");
		}else
		{
			Sys_Oth_Parameter sys_Oth_Parameter=new Sys_Oth_Parameter(this.getFrameconn());
			subset=sys_Oth_Parameter.getValue(Sys_Oth_Parameter.GOBROADSUBSET,"setname");
			this.getFormHM().put("collectflag", "");
		}
		
		if(subset==null||subset.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.no.subset"),"",""));
		}else
		{
			this.getFormHM().put("subset",subset);
			this.userView.getHm().put("goboard_subset", subset);
		}
	}


}
