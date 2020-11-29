package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchFieldSetIdAndItemTypeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			String nmodule=(String)this.getFormHM().get("nmodule");
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			if("1".equalsIgnoreCase(type))
			{
				String itemid=(String)this.getFormHM().get("itemid");
				if("B0110".equalsIgnoreCase(itemid)|| "E0122".equalsIgnoreCase(itemid)|| "E01a1".equalsIgnoreCase(itemid))
				{
					this.getFormHM().put("itemtype","A");
		    		this.getFormHM().put("codesetid", "B0110".equalsIgnoreCase(itemid)?"UN": "E0122".equalsIgnoreCase(itemid)?"UM":"@K");
			    	this.getFormHM().put("itemlength","8");
			    	this.getFormHM().put("decimalwidth","0");
				}else
				{
		    		HashMap map =null;
		    		if("2".equals(nmodule))
		    		{
		    			map=bo.getItemInfoFromSalary(itemid, fieldsetid);
		    		}
		    		else
		    		{
		        		map=bo.getItemInfo(itemid);
		    		}
		    		this.getFormHM().put("itemtype",(String)map.get("itemtype"));
		    		this.getFormHM().put("codesetid",(String)map.get("codesetid"));
			    	this.getFormHM().put("itemlength",(String)map.get("itemlength"));
			    	this.getFormHM().put("decimalwidth",(String)map.get("decimalwidth"));
				}
				
			}
			else if("2".equalsIgnoreCase(type))
			{
				
			}
			else if("3".equalsIgnoreCase(type))
			{
				
			}
			this.getFormHM().put("type",type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
