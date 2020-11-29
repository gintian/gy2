package com.hjsj.hrms.transaction.browse.history;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:SnapFieldTrans.java</p>
 * <p>Description>:SnapFieldTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 19, 2010 5:17:47 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SnapFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub	
        ArrayList list=new ArrayList();
		
		String setname=(String)this.getFormHM().get("tablename");
		//ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		ArrayList fielditemlist=this.getUserView().getPrivFieldList(setname);
		try
		{
			if(fielditemlist!=null)
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	      	  if("Nbase".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("A0100".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("A0000".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("B0110".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("E0122".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("E01A1".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
			  if("A0101".equalsIgnoreCase(fielditem.getItemid()))
				  continue;
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid(),  fielditem.getItemdesc());
		      list.add(dataobj);
		    }
		    this.getFormHM().clear();		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
		this.getFormHM().put("fieldlist",list);
	}

}
