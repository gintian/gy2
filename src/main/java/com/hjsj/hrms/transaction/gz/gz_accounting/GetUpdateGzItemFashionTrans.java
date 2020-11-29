package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 6, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetUpdateGzItemFashionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String sets=((String)hm.get("sets")).substring(1);
			if(salaryid==null||salaryid.trim().length()==0)
			{	salaryid=(String)hm.get("salaryid");
				hm.remove(salaryid);
			}
			/**薪资类别*/
			sets=sets.replaceAll("／", "/");
			//如果用户没有当前薪资类别的资源权限   20140926  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList gzItemList=gzbo.getUpdateItemList(sets.split("/"));
			this.getFormHM().put("gzItemList",gzItemList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
