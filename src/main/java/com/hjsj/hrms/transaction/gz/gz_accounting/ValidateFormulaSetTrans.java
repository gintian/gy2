package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:验证是否设置了计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 7, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ValidateFormulaSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");	
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList list=gzbo.getFormulaList(1);
			if(list.size()>0)
				this.getFormHM().put("flag","1");
			else
				this.getFormHM().put("flag","0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
