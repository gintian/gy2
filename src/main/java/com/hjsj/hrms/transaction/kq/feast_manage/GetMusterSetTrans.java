package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class GetMusterSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String infor=(String)hm.get("base");
		if(infor==null|| "".equals(infor))
			infor="1";
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	        continue;
	      if("A00".equals(fieldset.getFieldsetid()))
	    	  continue;
	      CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getFieldsetid() + ":"+ fieldset.getFieldsetdesc());
          list.add(dataobj);
	    }
	    this.getFormHM().clear();
	    /**为了不让前台弹出提示框*/
	    //System.out.println("---infor=="+infor);
	   // System.out.println("---eeeeeee=="+list.toString());
	    this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	}
    
}

