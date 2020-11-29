package com.hjsj.hrms.transaction.general.statics.singlestatic;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SelectStaticSetTrans extends IBusiness {

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
	      if("A00".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())|| "K00".equals(fieldset.getFieldsetid()))
	    	  continue;
	     if("1".equals(infor))
	     {
	       if(!"A00".equals(fieldset.getFieldsetid())&&!"B".equals(fieldset.getFieldsetid().substring(0,1))&&!"K".equals(fieldset.getFieldsetid().substring(0,1)))
		     {
		         CommonData dataobj = new CommonData();
	             dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
	             list.add(dataobj);
		     }
	    	 
	     }else{
	          CommonData dataobj = new CommonData();
              dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc()/*getFieldsetdesc()*/);
              list.add(dataobj);

	     }
	    }
	    this.getFormHM().clear();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd"); 
	    Date date = new Date(); 
	    String data = formatter.format(date);
	    /**为了不让前台弹出提示框*/
	    this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	    this.getFormHM().put("data",data);
	    
	    this.getFormHM().put("infor_Flag",infor);
	   
	}

}
