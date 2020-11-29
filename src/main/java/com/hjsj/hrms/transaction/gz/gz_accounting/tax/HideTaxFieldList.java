package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class HideTaxFieldList extends IBusiness {
	
	public void execute() throws GeneralException {
		try
		{
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			/**取得个税明细表结构字段列表*/
			ArrayList tempfieldlist = taxbo.getFieldlist();
			ArrayList hidefieldlist = this.getitemlist(tempfieldlist);
			this.getFormHM().put("hidefieldlist", hidefieldlist);
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 为前台list处理指标
	 * @param fieldlist
	 * @return
	 */
	public ArrayList getitemlist(ArrayList fieldlist)
	{
		ArrayList retlist = new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			Field field = (Field)fieldlist.get(i);
			String itemid = field.getName();
			String itendesc = field.getLabel();
			if("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid) )
			{
				continue;
			}else{
				retlist.add(field);
			}
				
		}
		return retlist;
	}
}
