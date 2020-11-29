package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = this.getFormHM();
		String info = (String) hm.get("object");

		//System.out.println("info = " + info);
		
		if (info == null || "".equals(info)) {
			info = "A";
		}

		ArrayList sulist = new ArrayList();
		ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,
				Constant.ALL_FIELD_SET);

		for (int i = 0; i < fieldsetlist.size(); i++) {
			
			FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
			String temp = fieldset.getFieldsetid();
			if ("A".equals(info)) {
				if (!"A00".equals(temp)&&!"B".equals(temp.substring(0, 1))&& !"K".equals(temp.substring(0, 1))) {
					CommonData dataobj = new CommonData(temp, fieldset.getFieldsetdesc());
					sulist.add(dataobj);
				}
			} else if ("B".equals(info)) {
				if ("B".equals(temp.substring(0, 1))) {
					CommonData dataobj = new CommonData(temp, fieldset.getFieldsetdesc());
					sulist.add(dataobj);
				}
			} else {
				if ("K".equals(temp.substring(0, 1))) {
					CommonData dataobj = new CommonData(temp, fieldset.getFieldsetdesc());
					sulist.add(dataobj);
				}
			} 
			
			/*if(info.equals("ALL")){
				CommonData dataobj = new CommonData(temp, fieldset.getFieldsetdesc());
				sulist.add(dataobj);
			}*/
		}
		this.getFormHM().put("sulist", sulist);

	}

}
