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
 *<p>Title:GetFieldSetListTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 9, 2008:11:53:41 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class GetFieldSetListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor=(String)this.getFormHM().get("base");
		String type = (String)this.getFormHM().get("type");
		this.getFormHM().put("type",type);
		if(infor==null|| "".equals(infor))
			infor="1";
		ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    if("A".equalsIgnoreCase(infor))
	    	infor = "1";
	    else if("B".equalsIgnoreCase(infor))
	    	infor = "2";
	    else if("K".equalsIgnoreCase(infor))
	    	infor = "3";
	    else if("Y".equalsIgnoreCase(infor))  //党组织
	    	infor = "5";
	    else if("V".equalsIgnoreCase(infor)) //团组织
	    	infor = "6";
	    else if("W".equalsIgnoreCase(infor)) //工会组织
	    	infor = "7";
	    else
	    	infor = "4";  //职务组织
	    if("1".equals(infor))
	    {
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    }
	    else if(("2".equals(infor))){
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    }
	    else if(("3".equals(infor))){
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.POS_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    }
	    else if(("5".equals(infor))){   //党组织
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.PARTY_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.PARTY_FIELD_SET);
	    }
	    else if(("6".equals(infor))){   //团组织
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
	    }
	    else if(("7".equals(infor))){   //工会组织
	    	DataDictionary.refresh();
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);
	    }
	    else
	    {
	    	DataDictionary.refresh();	    	
	    	if("0".equalsIgnoreCase(type))
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.NOT_USED_FIELD_SET,Constant.JOB_FIELD_SET);
	    	else
	    		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);	    	
	    }
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	    	FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	    	/*if(this.userView.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
	    		continue;*/
	    	if("A00".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())|| "K00".equals(fieldset.getFieldsetid())|| "H00".equals(fieldset.getFieldsetid()))
	    		continue;
	    	CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
	    	list.add(dataobj);
	    }
	    //this.getFormHM().put("base",infor);
	    this.getFormHM().put("setlist",list);
	    this.getFormHM().put("itemlist",new ArrayList());
	}

}
