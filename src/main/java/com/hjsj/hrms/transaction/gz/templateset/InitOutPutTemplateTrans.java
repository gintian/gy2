package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:InitOutPutTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitOutPutTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList setList=new ArrayList();
			
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,0);
			setList=pgkbo.searchGzSetList("0");
			
			
			this.getFormHM().put("salarySetList",setList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		

	}

}
