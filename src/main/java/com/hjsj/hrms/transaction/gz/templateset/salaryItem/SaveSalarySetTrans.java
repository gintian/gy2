package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSalarySetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] salarySetIDs=(String[])this.getFormHM().get("salarySetIDs");
			String salaryid=(String)this.getFormHM().get("salaryid");
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			bo.saveSalarySet(salarySetIDs);
			
			StringBuffer context = new StringBuffer();
			context.append("新增:"+bo.getSalaryName(salaryid)+"("+salaryid+")<br>");
			for(int i=0;i<salarySetIDs.length;i++)
			{
				FieldItem tempItem=DataDictionary.getFieldItem(salarySetIDs[i].toLowerCase());
				if(i!=0)
					context.append(",");
				context.append(tempItem.getItemdesc());
			}
			this.getFormHM().put("@eventlog", context.toString());
			/**保存后刷新静态变量，不然数据不同步*/
			/* 觉得下面代码没有用 薪资项目多的时候，很慢 wangrd 2015-02-02
			String itemid="";
			SalaryPkgBo pkgbo=new SalaryPkgBo(this.getFrameconn(),null,0);
			pkgbo.setAllitem_hm(null);
			pkgbo.searchItemById(itemid);
			*/
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
