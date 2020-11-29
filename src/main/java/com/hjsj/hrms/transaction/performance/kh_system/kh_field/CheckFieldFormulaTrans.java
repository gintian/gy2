package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:CheckFieldFormulaTrans.java</p>
 * <p>Description:指标计算公式检查</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2011-08-06</p>
 * @author JinChunhai
 * @version 5.0
 */

public class CheckFieldFormulaTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
				
		String errorInfo="ok";
		
		String formula = (String) this.getFormHM().get("formula");
		formula = formula != null && formula.trim().length() > 0 ? formula : "";
		formula = SafeCode.decode(formula);	
		formula = PubFunc.keyWord_reback(formula);
		KhFieldBo bo = new KhFieldBo(this.getFrameconn(),this.userView);
		errorInfo = bo.testformula(formula);
				
		errorInfo=SafeCode.encode(errorInfo);
		this.getFormHM().put("errorInfo", errorInfo);

	}
	
}
