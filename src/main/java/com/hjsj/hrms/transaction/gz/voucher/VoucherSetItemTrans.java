package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class VoucherSetItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		String key = (String) this.getFormHM().get("key");
		String value = (String) this.getFormHM().get("value");
		ArrayList list = new  ArrayList();
		String[] keys=key.split(",");
		String[] values=value.split(",");
		for(int i=0;i<keys.length;i++){
			list.add(new CommonData(keys[i],values[i]));
		}
		this.getFormHM().put("key", key);
		this.getFormHM().put("list", list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
