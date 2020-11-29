package com.hjsj.hrms.transaction.gz.premium.premium_allocate;

import com.hjsj.hrms.businessobject.gz.premium.PremiumBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:部门月奖计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 27, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class GetPremiumFormulaTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			PremiumBo bo=new PremiumBo(this.getFrameconn(),this.getUserView());
			ArrayList formulaList=bo.getFormulaList(new ArrayList());
			this.getFormHM().put("formulaList",formulaList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
