package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:手工引入人员信息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 19, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ImportMenInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag=(String)map.get("importtype");
			String[] right_fields=new String[0];
			String isSalaryManager=(String)this.getFormHM().get("isSalaryManager");
			String expr=(String)this.getFormHM().get("expr");
			String factor=(String)this.getFormHM().get("factor");
			String isHistory=(String)this.getFormHM().get("isHistory");
			String querytype=(String)this.getFormHM().get("querytype");
			if("0".equals(flag))
				right_fields=(String[])this.getFormHM().get("right_fields");
			else
			{
				String allRightField=(String)this.getFormHM().get("allRightField");
				right_fields=allRightField.split(",");
			}
			String  salaryid=(String)this.getFormHM().get("salaryid");
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			templatebo.setImportMen(true);
			if(querytype!=null&& "1".equals(querytype)&& "1".equals(flag))
				templatebo.importHandSelectedMenHQuery(expr, factor, "1".equals(isHistory)?true:false, isSalaryManager);
			else
	    		templatebo.importHandSelectedMen(right_fields);
			this.getFormHM().put("right_fields",null);
	 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
