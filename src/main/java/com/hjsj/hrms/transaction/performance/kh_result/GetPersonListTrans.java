package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
public class GetPersonListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String code = (String)map.get("a_code");
			String distinctionFlag=(String)map.get("distinctionFlag");
			String model=(String)map.get("model");
			String opt = (String)map.get("opt");
			ResultBo bo = new ResultBo(this.getFrameconn());
			String nbase = "USR";
			ArrayList dbList = bo.getPrivDblist(this.userView.getPrivDbList());
			if(dbList==null||dbList.size()<=0)
				throw GeneralExceptionHandler.Handle(new Exception("没有您管理范围内的人员库!"));
			if("0".equals(opt)&&dbList.size()>0)
				nbase= ((CommonData)dbList.get(0)).getDataValue();
			else if(!"0".equals(opt))
				nbase=(String)map.get("nbase");
			HashMap hm=bo.getSql(code, nbase, this.userView);
			String isCloseButton=(String)this.getFormHM().get("isCloseButton");
			
			this.getFormHM().put("dbList", dbList);
			this.getFormHM().put("nbase",nbase);
			this.getFormHM().put("selectSql",(String)hm.get("1"));
			this.getFormHM().put("whereSql",(String)hm.get("2"));
			this.getFormHM().put("orderSql",(String)hm.get("3"));
			this.getFormHM().put("columns",(String)hm.get("4"));
			this.getFormHM().put("distinctionFlag",distinctionFlag);
			this.getFormHM().put("model", model);
			this.getFormHM().put("code",code);
			this.getFormHM().put("isCloseButton", isCloseButton);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
