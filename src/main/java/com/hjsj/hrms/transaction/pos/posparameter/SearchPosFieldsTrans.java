package com.hjsj.hrms.transaction.pos.posparameter;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchPosFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			ArrayList b0110list = new ArrayList();
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			String flag = (String)this.getFormHM().get("flag");
			this.getFormHM().remove("flag");
			if("leader".equals(flag)){
				b0110list.add(new CommonData("#", ""));
				list.add(new CommonData("#", ""));
			}
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
			if(fielditemlist!=null)
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("leader".equals(flag)){
		    	  if("N".equals(fielditem.getItemtype())){
		    		  CommonData dataobj = new CommonData();
				      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
				      
				      list.add(dataobj);
		    	  }else if("UN".equals(fielditem.getCodesetid())
							|| "UM".equals(fielditem.getCodesetid())){
		    		  CommonData dataobj = new CommonData();
				      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
				      
				      b0110list.add(dataobj);
	    		  }
		      }else{
			      CommonData dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
			      
			      list.add(dataobj);
		      }
		    }
		    this.getFormHM().clear();
		    //System.out.println(list.size());
		    this.getFormHM().put("fieldlist",list);
		    this.getFormHM().put("b0110list",b0110list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}

	}

}
