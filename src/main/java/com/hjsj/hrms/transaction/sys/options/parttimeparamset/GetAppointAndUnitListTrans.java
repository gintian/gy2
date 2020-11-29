package com.hjsj.hrms.transaction.sys.options.parttimeparamset;

import com.hjsj.hrms.businessobject.sys.options.parttimeparamset.ParttimeSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetAppointAndUnitListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String setid="";
			if(this.getFormHM().get("setid")!= null && ((String)this.getFormHM().get("setid")).trim().length()>0)
				setid=(String)this.getFormHM().get("setid");
			ParttimeSetBo bo = new ParttimeSetBo(this.getFrameconn());
			ArrayList unitList = bo.getUnitList(setid);
			ArrayList appointList=bo.getAppointList(setid);
			ArrayList poslist=bo.getPosList(setid);
			ArrayList itemlist=bo.getCodeitemList(setid,"0");
			ArrayList nitemlist=bo.getNitemList(setid);
			this.getFormHM().put("unitList",unitList);
			this.getFormHM().put("poslist",poslist);
			this.getFormHM().put("appointList",appointList);
			this.getFormHM().put("itemlist", itemlist);
			this.getFormHM().put("nitemlist", nitemlist);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
