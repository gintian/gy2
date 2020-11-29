package com.hjsj.hrms.transaction.sys.options;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetOrgFieldTrans  extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub	
        ArrayList list=new ArrayList();
		
		String setname=(String)this.getFormHM().get("tablename");
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		try
		{
			if(fielditemlist!=null)
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      // 未构库不能在备选指标里出现 27009  wangb1 20170502
		      if("M".equalsIgnoreCase(fielditem.getItemtype()) || "0".equalsIgnoreCase(fielditem.getUseflag()))
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
