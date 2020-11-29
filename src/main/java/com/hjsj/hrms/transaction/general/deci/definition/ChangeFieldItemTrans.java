package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ChangeFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String object = (String)this.getFormHM().get("object");
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		String itemid = (String)this.getFormHM().get("itemid");
		
		System.out.println("itemid=" + itemid );
		
		String temp = "";
		if(itemid == null || "".equals(itemid)){
			
		}else{
			temp = DataDictionary.getFieldItem(itemid).getCodesetid();
		}
		
		System.out.println(temp);
		this.getFormHM().put("info",temp);
	}

}
