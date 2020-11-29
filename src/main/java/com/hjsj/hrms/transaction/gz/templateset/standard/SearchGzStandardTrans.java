package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchGzStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			SalaryStandardBo bo=new SalaryStandardBo(this.getFrameconn());
			bo.setUserView(userView);
			ArrayList standardlist=bo.getSalaryStandardList((String)hm.get("pkg_id"));
			this.getFormHM().put("standardlist",standardlist);
			GzStandardItemBo gzStandardItemBo=new GzStandardItemBo(this.getFrameconn(),this.userView);
			this.getFormHM().put("pkgIsActive",String.valueOf(gzStandardItemBo.getFlag((String)hm.get("pkg_id"))));
			this.getFormHM().put("pkg_id",(String)hm.get("pkg_id"));
			String isOperOrManage = bo.isOperOrManager();
			this.getFormHM().put("isOperOrManage", isOperOrManage);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
