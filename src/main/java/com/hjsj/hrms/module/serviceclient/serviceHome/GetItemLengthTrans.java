package com.hjsj.hrms.module.serviceclient.serviceHome;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetItemLengthTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String fieldName = (String) this.formHM.get("fieldName");
			String[] fieldInfoArr = fieldName.split("`");
			FieldItem fieldItem = DataDictionary.getFieldItem(fieldInfoArr[1]);
			String itemLen = String.valueOf(fieldItem.getItemlength());
			this.getFormHM().put("itemLen", itemLen);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
