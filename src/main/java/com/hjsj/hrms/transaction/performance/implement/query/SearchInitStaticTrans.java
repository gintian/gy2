package com.hjsj.hrms.transaction.performance.implement.query;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
public class SearchInitStaticTrans extends IBusiness {

	
	public void execute() throws GeneralException {
	
//		 TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
//	    fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
	    fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	     
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	        continue;
          CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getFieldsetdesc());
          list.add(dataobj);
	    }
	  
	    this.getFormHM().clear();
	    this.getFormHM().put("setlist",list);
	}
		



}
