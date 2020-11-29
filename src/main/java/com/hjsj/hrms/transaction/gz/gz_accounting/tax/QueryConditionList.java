package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class QueryConditionList extends IBusiness{
	public void execute()throws GeneralException 
	{
		try
		{
			HashMap hm = (HashMap)this.getFormHM();
			TaxMxBo taxbo = new TaxMxBo(this.getFrameconn());
			ArrayList tempfieldlist = taxbo.getFieldlist();
			ArrayList congzmxprolist = taxbo.getitemlist(tempfieldlist);
			hm.put("congzmxprolist",congzmxprolist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	
}

