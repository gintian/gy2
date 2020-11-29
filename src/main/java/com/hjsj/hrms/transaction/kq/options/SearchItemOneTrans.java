package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchItemOneTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		
	    ArrayList list=DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
	    Field field=null;
 	    ArrayList fieldlist=new ArrayList();
 	    ArrayList setlist=new ArrayList();
		  for(int i=0;i<list.size();i++){
			  if(list.get(i)==null){
				  continue;
			  }
			  FieldItem fi=(FieldItem)list.get(i);
			  if("a0100".equalsIgnoreCase(fi.getItemid()) || "i9999".equalsIgnoreCase(fi.getItemid()))
				  continue;
		       CommonData datavo=new CommonData(fi.getItemid()+":"+fi.getItemdesc(),fi.getItemid()+":"+fi.getItemdesc());
		       setlist.add(datavo);
		       field=new Field(fi.getItemdesc(),fi.getItemtype());
		       field.setDatatype("string");
		       fieldlist.add(field);
		  }
	          this.getFormHM().put("setlist",setlist);
	          this.getFormHM().put("fieldlist",fieldlist);
           
	  }
	  

}
