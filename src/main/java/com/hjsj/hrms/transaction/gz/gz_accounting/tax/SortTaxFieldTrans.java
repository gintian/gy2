package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class SortTaxFieldTrans extends IBusiness {
	
	public void execute() throws GeneralException 
	{	
		TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
		String sortfields = (String)this.getFormHM().get("sortfields");
		this.getFormHM().put("sortfields",sortfields);
		if(sortfields!=null && sortfields.length()>0)
		{
			String[] sortfield = this.getstr(sortfields);
			taxbo.sort(sortfield);
		}
	}

	public String[] getstr (String fields)
	{
		String[] field = null;
		int tempnum = fields.split(",").length;
		if(tempnum>0)
		{
			field = fields.split(",");
		}
		return field;
	}
}
