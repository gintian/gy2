package com.hjsj.hrms.transaction.general.statics;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchInitStaticTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String infor=(String)hm.get("base");
		if(infor==null|| "".equals(infor))
		{
			String mm=(String)this.getFormHM().get("infor_Flag");
			infor=mm;
			if(mm==null|| "".equals(mm))
				infor="1";
		}
		
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else if("h".equals(infor)){
	    	fieldsetlist = new ArrayList();
	    } else
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())) || "A00".equalsIgnoreCase(fieldset.getFieldsetid()))
	        continue;
	      if("b00".equalsIgnoreCase(fieldset.getFieldsetid())|| "k00".equalsIgnoreCase(fieldset.getFieldsetid())|| "a00".equalsIgnoreCase(fieldset.getFieldsetid()))
	    	  continue;
          CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc()/*getFieldsetdesc()*/);
          list.add(dataobj);
	    }
	    if("h".equals(infor)){
	    	CommonData dataobj = new CommonData("hr_emp_hisdata","人员历史信息集" );
	          list.add(dataobj);
	    }
	    
	    String[] right_fields = (String[]) this.getFormHM().get("right_fields");
	    ArrayList<CommonData> rightFieldList = new ArrayList<CommonData>();
	    if(right_fields != null && right_fields.length > 0) {
	        for(int i = 0; i < right_fields.length; i++) {
	            FieldItem fi = DataDictionary.getFieldItem(right_fields[i]);
	            if(fi != null) {
	                CommonData datavo=new CommonData(fi.getItemid(),fi.getItemdesc());
	                rightFieldList.add(datavo);
	            }
	        }
	    }
	    
//	    this.getFormHM().clear();
	    this.getFormHM().put("setlist",list);
	    this.getFormHM().put("infor_Flag",infor);
	    this.getFormHM().put("right_fields",right_fields);
	    this.getFormHM().put("rightFieldList",rightFieldList);
	}

}
