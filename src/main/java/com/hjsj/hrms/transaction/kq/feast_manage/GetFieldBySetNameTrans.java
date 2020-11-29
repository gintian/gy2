package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetFieldBySetNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			ArrayList onefiledlist=new ArrayList();
			Field field=null;
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
			  CommonData dataobj = new CommonData();
		      dataobj.setDataName("");
			  dataobj.setDataValue("");
			  list.add(dataobj);
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	  continue;
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		    
		      dataobj = new CommonData(fielditem.getItemid(),   fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
		      list.add(dataobj);
		      field=new Field(fielditem.getItemdesc(),fielditem.getItemtype());
		      field.setDatatype("string");
		      onefiledlist.add(field);
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("fieldlist",list);
		    this.getFormHM().put("onefiledlist",onefiledlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}


}
