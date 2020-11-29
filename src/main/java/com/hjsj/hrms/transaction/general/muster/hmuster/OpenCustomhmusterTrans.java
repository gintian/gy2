package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class OpenCustomhmusterTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			//&nFlag=21&a_inforkind=2&result=0机构
			//&nFlag=41&a_inforkind=3&result=0职位
			//&nFlag=3&a_inforkind=1&result=0人
			 String nFlag=(String)hm.get("nFlag");
			 nFlag=nFlag!=null&&nFlag.trim().length()>0?nFlag:"0";
			 hm.remove("nFlag");
			 String tabid=(String)hm.get("costID");
			 hm.remove("costID");
			 this.getFormHM().put("modelFlag",nFlag);
			 String a_inforkind=(String)hm.get("a_inforkind");
			 a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"1";
			 this.getFormHM().put("infor_Flag", a_inforkind);
			 hm.remove("a_inforkind");
			 this.getFormHM().put("inforkind",a_inforkind);
			 String dbpre = (String)hm.get("dbpre");
			 dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"";
			 this.getFormHM().put("dbpre",dbpre);
			 String result=(String)hm.get("result");
			 result=result!=null&&result.trim().length()>0?result:"0";
			 hm.remove("result");
			 this.getFormHM().put("result",result);
			 this.getFormHM().put("tabID",tabid);
			 this.getFormHM().put("isCloseButton", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
