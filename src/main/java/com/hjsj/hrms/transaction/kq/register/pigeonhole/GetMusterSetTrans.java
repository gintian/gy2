package com.hjsj.hrms.transaction.kq.register.pigeonhole;

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
		String destfld=(String)hm.get("destfld");		
		if(infor==null|| "".equals(infor))
			infor="1";
	    ArrayList list=new ArrayList();
	    CommonData dataobj=null;
	    /*CommonData dataobj=new CommonData("#","请选择");
	    list.add(dataobj);*/
	    ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    int r=0;
	    String changeflag="";
	    for(int i=0,s=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);	 
	      changeflag=fieldset.getChangeflag()!=null&&fieldset.getChangeflag().length()>0?fieldset.getChangeflag():"";
	      /*if(this.userView.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
	        continue;*/
	      if("A00".equals(fieldset.getFieldsetid()))
	    	  continue;
	      if(fieldset.getFieldsetid().equals(destfld.trim())&& "1".equals(changeflag))
	      {
	    	  r=s;		     
	      }
	      if("1".equals(changeflag))
	      {
	    	  dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getFieldsetid() + ":"+ fieldset.getFieldsetdesc());
	          list.add(dataobj);
	          s++;
	      }
	    }
	    this.getFormHM().clear();
	    /**为了不让前台弹出提示框*/
	    //System.out.println("---infor=="+infor);
	   // System.out.println("---eeeeeee=="+list.toString());
	    this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	    this.getFormHM().put("r_num",r+"");;
	}

}
