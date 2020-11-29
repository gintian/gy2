package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class GetInsertFieldListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String nmodule="1";
			String type="1";
			if(map!=null&&map.get("type")!=null)
			{
				type=(String)map.get("type");
				map.remove("type");
			}
			if(map!=null&&map.get("nmodule")!=null)
			{
				nmodule=(String)map.get("nmodule");
				map.remove("nmodule");
			}
			else
			{
				nmodule=(String)this.getFormHM().get("nmodule");
			}
			if("2".equals(nmodule))
			{
				EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
		    	ArrayList fieldsetlist= bo.getPrivSalarySetList(this.userView);
		    	String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		    	if("0".equals(type))//初次进入
		    	{
		    		if(fieldsetlist.size()>0)
		    		{
		         	  fieldsetid=((CommonData)fieldsetlist.get(0)).getDataValue();
		    		}
		    		else
		    			fieldsetid="#";
	    		}else//改变子集id
	    		{
	    			fieldsetid=(String)this.getFormHM().get("fieldsetid");
		    	}
		    	ArrayList dateFormatList=bo.getDateFormatList();
		    	ArrayList itemlist = bo.getSalaryItem(fieldsetid, this.userView);
		       	this.getFormHM().put("fieldsetlist",fieldsetlist);
		    	this.getFormHM().put("fieldsetid",fieldsetid);
		    	this.getFormHM().put("itemlist",itemlist);
		    	this.getFormHM().put("dateFormat","1");
		    	this.getFormHM().put("dateFormatList",dateFormatList);
			}
			else
			{
	    		EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
		    	ArrayList fieldsetlist= bo.getPersonSubset(2,this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
		    	String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		    	if("0".equals(type))//初次进入
		    	{
		     	  fieldsetid= bo.getFirstFieldSetid(this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
	    		}else//改变子集id
	    		{
	    			fieldsetid=(String)this.getFormHM().get("fieldsetid");
		    	}
		    	ArrayList dateFormatList=bo.getDateFormatList();
		    	ArrayList itemlist = bo.getFieldItemList(2,fieldsetid,this.userView);
		       	this.getFormHM().put("fieldsetlist",fieldsetlist);
		    	this.getFormHM().put("fieldsetid",fieldsetid);
		    	this.getFormHM().put("itemlist",itemlist);
		    	this.getFormHM().put("dateFormat","1");
		    	this.getFormHM().put("dateFormatList",dateFormatList);
			}
			this.getFormHM().put("nmodule", nmodule);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}