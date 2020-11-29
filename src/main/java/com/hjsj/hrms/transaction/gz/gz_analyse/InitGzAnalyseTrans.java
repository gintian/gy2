package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitGzAnalyseTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn());
			String gz_module =(String)map.get("gz_module");
			String returnflag=(String)map.get("returnflag"); 
			this.getFormHM().put("returnflag",returnflag);
			/*SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,Integer.parseInt(gz_module));
			ArrayList list=pgkbo.searchGzSetList("0");*/
			ArrayList preList=bo.getDbList(this.userView.getPrivDbList());
			ArrayList salarySetList =bo.getSalarySetList(gz_module,this.userView);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("preList", preList);
			this.getFormHM().put("salarySetList",salarySetList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
