package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 
 *<p>Title:SaveRelationTrans.java</p> 
 *<p>Description:保存对应关系方案</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 13, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			
			String name=(String)this.getFormHM().get("schemeName");
			String[] oppositeItem=(String[])this.getFormHM().get("oppositeItem");   //对应指标 
			String[] relationItem=(String[])this.getFormHM().get("relationItem");  //关联指标
			String salaryid=(String)this.getFormHM().get("salaryid");
			
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.saveRelationScheme(name,oppositeItem,relationItem);
			
			ArrayList relationItemList=gzbo.getArrayList(relationItem);
			ArrayList oppositeItemList=gzbo.getArrayList(oppositeItem);
			this.getFormHM().put("relationItemList",relationItemList);
			this.getFormHM().put("oppositeItemList",oppositeItemList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
