package com.hjsj.hrms.transaction.general.statics.singlestatic;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchStaticBySetNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;		      
		      if("N".equals(fielditem.getItemtype())|| "D".equals(fielditem.getItemtype()))
		      {
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
		      list.add(dataobj);
		      }
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("fieldlist",list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}

}
