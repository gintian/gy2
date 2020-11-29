package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 薪资类别另存为
 *<p>Title:SaveAsSalaryTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 24, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveAsSalaryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
			String salarySetName=SafeCode.decode((String)this.getFormHM().get("salarySetName"));
			String gz_module=(String)this.getFormHM().get("gz_module");
			if(gz_module==null|| "".equalsIgnoreCase(gz_module))
				gz_module="0";
			int imodule=Integer.parseInt(gz_module);
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(selectedList!=null&&selectedList.size()>0)
			{
				String salaryid="";
				for(int i=0;i<selectedList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
					salaryid=(String)abean.get("salaryid");
					
					safeBo.isSalarySetResource(salaryid,gz_module);
				}
				
				SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,imodule);
				pgkbo.reSaveSalaryTemplate(salaryid,salarySetName);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
