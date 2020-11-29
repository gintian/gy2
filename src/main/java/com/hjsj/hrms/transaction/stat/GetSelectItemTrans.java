package com.hjsj.hrms.transaction.stat;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetSelectItemTrans extends IBusiness
{
	 public void execute() throws GeneralException 
	  {
		 String liststr=(String)this.getFormHM().get("list");
		 if(liststr==null||liststr.length()<=0)
			 return ;
		 String[] listArray=liststr.split(",");
		 String fieldname="";
		 FieldItem item=null;
		 Factor factor=null;
		 int nInform=1;
		 ArrayList list=new ArrayList();
		 for(int i=0;i<listArray.length;i++)
		 {
			 fieldname=listArray[i];
			 item=DataDictionary.getFieldItem(fieldname.toUpperCase());
			 if(item==null)
				 continue;
			 factor=new Factor(nInform);
             factor.setCodeid(item.getCodesetid());
             factor.setHzvalue("");            
             factor.setValue("");
             factor.setFieldname(item.getItemid());
             factor.setHz(item.getItemdesc());
             factor.setFieldtype(item.getItemtype());
             factor.setItemlen(item.getItemlength());
             factor.setItemdecimal(item.getDecimalwidth());
             factor.setOper("=");
             factor.setLog("");
             list.add(factor);
		 }
		 this.getFormHM().put("list", list);
	  }

}
