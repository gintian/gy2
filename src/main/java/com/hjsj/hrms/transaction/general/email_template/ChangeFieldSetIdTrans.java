package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeFieldSetIdTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
		try
		{
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String fieldsetid="";
			String itemid="";
			ArrayList fieldsetlist= bo.getPersonSubset(2,this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
			if(map!=null)
			{
				if(map.get("fieldsetid")!=null&&!"".equals((String)map.get("fieldsetid")))
				{
					 fieldsetid=(String)map.get("fieldsetid");
					 itemid=(String)map.get("itemid");
				}
				else
				{
					fieldsetid=((CommonData)fieldsetlist.get(0)).getDataValue();
				}
			}
			else
			{
				fieldsetid=(String)this.getFormHM().get("fieldsetid");
			}
			
			
			ArrayList itemlist = bo.getFieldItemList(2,fieldsetid,this.userView);
			this.getFormHM().put("fieldsetlist",fieldsetlist);
			this.getFormHM().put("fieldsetid",fieldsetid);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("itemid",itemid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			if(map!=null)
			{
				map.remove("fieldsetid");
				map.remove("itemid");
			}
			
		}
		
	}

}
