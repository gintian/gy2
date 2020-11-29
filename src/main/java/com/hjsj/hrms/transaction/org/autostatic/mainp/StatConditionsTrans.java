package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.DecExpresion;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class StatConditionsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fielditemid = (String)reqhm.get("fielditemid");
		reqhm.remove("fielditemid");
		String[] fitem = null;
		if(fielditemid!=null&&fielditemid.length()>0){
			DecExpresion dec = new DecExpresion(this.frameconn,fielditemid);
			fitem = dec.decArrayItemid();
		}else{
				fitem = (String[])hm.get("right_fields");
		}
		ArrayList selectlist = new ArrayList();
		if(fitem.length>0){
			for(int i=0;i<fitem.length;i++){
				if(fitem[i]!=null&&fitem[i].length()>0){
					FieldItem item=DataDictionary.getFieldItem(fitem[i]);
					CommonData dataobj = new CommonData(fitem[i], item.getItemdesc());
					selectlist.add(dataobj);
				}
			}
		}
		hm.put("selectedlist",selectlist);
		String setname =(String)hm.get("setname");
		hm.remove("setname");
		setname=setname!=null?setname:"A01";
		hm.put("setname",setname);
	}

}
