package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			ArrayList fielditemlist=this.getUserView().getPrivFieldList(setname);
			if(fielditemlist==null)
				fielditemlist = new ArrayList();
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	continue;
		      if(this.userView.analyseFieldPriv(fielditem.getItemid())==null)
			        continue;
		      if(this.userView.analyseFieldPriv(fielditem.getItemid()).length()<1)
			        continue;
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
		    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
		    		  	fielditem.getItemdesc());
		      
		      list.add(dataobj);
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
