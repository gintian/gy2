package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.SalaryReportBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:SaveReportDefineTrans.java</p> 
 *<p>Description:保存报表定义</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 22, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveReportDefineTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String salaryReportName=(String)this.getFormHM().get("salaryReportName");
			String isPrintWithGroup=(String)this.getFormHM().get("isPrintWithGroup");
			String isGroup=(String)this.getFormHM().get("isGroup");
			String f_groupItem=(String)this.getFormHM().get("f_groupItem");
			String s_groupItem=(String)this.getFormHM().get("s_groupItem");
			String reportStyleID=(String)this.getFormHM().get("reportStyleID");
			String reportDetailID=(String)this.getFormHM().get("reportDetailID");
			String[] right_fields=(String[])this.getFormHM().get("right_fields");
			String ownerType=(String)this.getFormHM().get("ownerType");
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			//ArrayList salaryItemList=gzbo.getSalaryItemList();
			HashMap itemOrderMap=new HashMap();
			int num=0;
			/*for(int i=0;i<salaryItemList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
				String itemid=(String)abean.get("itemid");
				if(itemid.equalsIgnoreCase("NBASE")||itemid.equalsIgnoreCase("A0100")||itemid.equalsIgnoreCase("A0000"))
					continue;
				itemOrderMap.put(itemid.toLowerCase(),String.valueOf(num));
				num++;
				
			}*/
			
			
			SalaryReportBo bo=new SalaryReportBo(this.getFrameconn(),salaryid);
			reportDetailID=bo.saveOrUpdateRecord(reportStyleID,reportDetailID,f_groupItem,s_groupItem,isPrintWithGroup,salaryReportName,right_fields,itemOrderMap,isGroup,ownerType,this.userView);
			
			this.getFormHM().put("salaryReportName",salaryReportName);
			this.getFormHM().put("reportDetailID",reportDetailID);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
