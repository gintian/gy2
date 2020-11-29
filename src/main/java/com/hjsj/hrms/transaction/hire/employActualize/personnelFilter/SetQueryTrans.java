package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SetQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		String[] selectedField=(String[])this.getFormHM().get("rightFields");
		ArrayList selectedFieldList=new ArrayList();
		
		for(int i=0;i<selectedField.length;i++)
		{
			String temp=selectedField[i];
			String[] array=temp.split("%%");
			DynaBean bean = new LazyDynaBean();
			bean.set("itemid",array[0]);
			bean.set("itemdesc",array[1]);
			bean.set("itemtype",array[2]);
			bean.set("itemsetid",array[3]);
			bean.set("fieldsetid",array[4]);
			selectedFieldList.add(bean);			
		}
		this.getFormHM().put("selectedFieldList",selectedFieldList);


	}

}