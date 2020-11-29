package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectBudgetingFldItemTrans extends IBusiness {

	public void execute() throws GeneralException {				
		try
		{
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String setid=(String)requestPamaHM.get("setid");
			ArrayList list=new ArrayList();
			if(setid==null|| "".equalsIgnoreCase(setid))
				return;
	
			list= DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
			ArrayList itemList=new ArrayList();
			String strExceptFld="nbase,sc010,tab_id,budget_id,b0110,e0122,beginmonth,endmonth,A0100,A0000,A0101,".toUpperCase();
			DynaBean abean;
			for(int i=0;i<list.size();i++)
			{
				FieldItem item = (FieldItem)list.get(i);
				String itemid= item.getItemid();
				if (strExceptFld.indexOf(itemid.toUpperCase()+',')>-1) continue;
				abean =new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", item.getItemdesc());
				
				itemList.add(abean);

			}
			this.getFormHM().put("itemlist", itemList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

		


	}

}