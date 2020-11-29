package com.hjsj.hrms.transaction.gz.templateset.spformula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

public class EditSpFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String optType=(String)hm.get("optType");
			String salaryid=(String)hm.get("salaryid");
			String gz_module=(String)hm.get("gz_module");
			String spAlert ="";
			String spFormulaName="";
			String chkid="";
			/**new*/
			if("1".equals(optType))
			{
				
			}
			/**edit*/
			else
			{
				chkid=(String)hm.get("chkid");
				SalaryTemplateBo stb = new SalaryTemplateBo(this.getFrameconn());
				LazyDynaBean bean=stb.getSpFormulaInfo(chkid);
				spAlert = (String)bean.get("information");
				spFormulaName=(String)bean.get("name");
				
			}
			this.getFormHM().put("spFormulaId", chkid);
			this.getFormHM().put("optType", optType);
			this.getFormHM().put("spFormulaName", spFormulaName);
			this.getFormHM().put("spAlert", spAlert);
			this.getFormHM().put("salaryid", salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
