package com.hjsj.hrms.transaction.attestation.unicom;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SearchItemListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String items=SystemConfig.getPropertyValue("bjga_items");
			String[] arr = items.split(",");
			ArrayList itemsList=new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from usra01 where a0100='"+this.getUserView().getA0100()+"'");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(this.frowset.next())
			{
	    		for(int i=0;i<arr.length;i++)
	    		{
		    		if(arr[i]==null|| "".equals(arr[i]))
			    		continue;
		    		FieldItem fieldItem = DataDictionary.getFieldItem(arr[i].toLowerCase());
		        	if(fieldItem==null||!"A01".equalsIgnoreCase(fieldItem.getFieldsetid())|| "M".equalsIgnoreCase(fieldItem.getItemtype()))//只支持主集
		        		continue;
		        	String value="";
		        	if("N".equals(fieldItem.getItemtype()))
		        	{
		        		if(this.frowset.getString(fieldItem.getItemid())!=null)
		        		{
		            		if(fieldItem.getDecimalwidth()==0)
		            			value=this.frowset.getInt(fieldItem.getItemid())+"";
		            		else
		            			value=PubFunc.round(this.frowset.getDouble(fieldItem.getItemid())+"",fieldItem.getDecimalwidth());
		        		}
		        	}
		        	else if("D".equalsIgnoreCase(fieldItem.getItemtype()))
		        	{
		        		if(this.frowset.getDate(fieldItem.getItemid())!=null)
		        		{
		        			value=format.format(this.frowset.getDate(fieldItem.getItemid()));
		        		}
		        	}
		        	else
		        	{
		        		if(this.frowset.getString(fieldItem.getItemid())!=null)
		        		{
		        			value=this.frowset.getString(fieldItem.getItemid());
		        		}
		        	}
		        	LazyDynaBean abean = new LazyDynaBean();
		         	abean.set("itemdesc",fieldItem.getItemdesc());
		        	abean.set("itemid",fieldItem.getItemid());
		        	abean.set("value",value);
		        	abean.set("itemtype", fieldItem.getItemtype());
		        	abean.set("codesetid",fieldItem.getCodesetid());
		        	abean.set("itemlength", fieldItem.getItemlength()+"");
		        	abean.set("deciwidth",fieldItem.getDecimalwidth()+"");
		        	abean.set("state", fieldItem.getState());
		        	if(!"0".equals(fieldItem.getCodesetid()))
		        	{
		        		ArrayList alist = AdminCode.getCodeItemList(fieldItem.getCodesetid());
		        		abean.set("codelist",alist);
		        	}
		        	itemsList.add(abean);
	    		}
			}
			this.getFormHM().put("itemsList", itemsList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
