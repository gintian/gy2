package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchShFormulaTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)map.get("salaryid");
			String condid=(String)map.get("condid");
			String a_code=(String)map.get("a_code");
			SalaryTemplateBo bo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid));
			ArrayList 	shFormulaList=bo.getSpFormulaList2(salaryid);//,"-1");
			this.getFormHM().put("shFormulaList",shFormulaList);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("condid",condid);
			this.getFormHM().put("a_code",a_code);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
