package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
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
public class MaintenanTaxMxTrans extends IBusiness{
	public void execute()throws GeneralException 
	{
		try
		{
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			String right_fields = (String)this.getFormHM().get("maintenanfields");
			String deptid=(String)this.getFormHM().get("deptid");
			this.getFormHM().remove("maintenanfields");		
			String[] fields = this.getstr(right_fields);
			taxbo.syncTaxTable(fields);
			taxbo.updateTaxMxField(fields,deptid);
			String fieldstr=this.getStrs(right_fields);
			taxbo.synData(fieldstr);
			
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn(),this.userView);
			bo.syncSalaryTaxArchiveStrut();
			
		}catch(Exception e){
			e.printStackTrace();
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
	private String getStrs(String right_fields)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			if(right_fields==null|| "".equals(right_fields))
				return buf.toString();
			String[] arr=right_fields.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				buf.append(arr[i]+",");
			}
			if(buf.toString().length()>0)
				buf.setLength(buf.length()-1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
}

