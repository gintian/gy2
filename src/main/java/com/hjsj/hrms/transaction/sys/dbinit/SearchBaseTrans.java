package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 11, 2008:2:48:58 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor=(String)this.getFormHM().get("infor");
		ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
		if("A".equalsIgnoreCase(infor))
	    	infor = "1";
	    else if("B".equalsIgnoreCase(infor))
	    	infor = "2";
	    else if("K".equalsIgnoreCase(infor))
	    	infor = "3";
	    else if("Y".equalsIgnoreCase(infor))
	    	infor = "5";
	    else if("V".equalsIgnoreCase(infor))
	    	infor = "6";
	    else if("W".equalsIgnoreCase(infor))
	    	infor = "7";
	    else 
	    	infor="4";
		if("1".equals(infor))
	    {
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    }
	    else if(("2".equals(infor))){
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    }
	    else if(("3".equals(infor))){
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    }
	    else if(("5".equals(infor))){
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.PARTY_FIELD_SET);
	    }
	    else if(("6".equals(infor))){
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
	    }
	    else if(("7".equals(infor))){
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);
	    }
	    else{
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);
	    }
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	    	FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	    	/*if(this.userView.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
	    		continue;*/
	    	if("A00".equals(fieldset.getFieldsetid())|| "B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())|| "A01".equals(fieldset.getFieldsetid())|| "K01".equals(fieldset.getFieldsetid())|| "H00".equals(fieldset.getFieldsetid())|| "H01".equals(fieldset.getFieldsetid())|| "K00".equals(fieldset.getFieldsetid())|| "Y00".equals(fieldset.getFieldsetid())|| "Y01".equals(fieldset.getFieldsetid())|| "V00".equals(fieldset.getFieldsetid())|| "V01".equals(fieldset.getFieldsetid())|| "W00".equals(fieldset.getFieldsetid())|| "W01".equals(fieldset.getFieldsetid()))
	    		continue;
	    	CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
	    	list.add(dataobj);
	    }
	    this.getFormHM().put("fieldsetlist",list);
	}

}
