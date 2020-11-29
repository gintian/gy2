package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPersonMessageTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String Item = (String) hm.get("Item");
		Item = Item.toLowerCase();
		String lockMGradeColumn = (String)hm.get("lockMGradeColumn");
		String object_type = (String)hm.get("object_type");
		hm.remove("Item");
		String type="";
		if("2".equals(object_type)){
			type="A01";
		}else{
			type="B01";
		}
		try
		{   
			ArrayList list = DataDictionary.getFieldList(type, Constant.USED_FIELD_SET);
			ArrayList messagelist=new ArrayList();
			HashMap map = new HashMap();
			String[] items = Item.split(",");
			for (int i = 0; i < items.length; i++)
				map.put(items[i], "");
			for(int i=0;i<list.size();i++)	
			{   
				
				LazyDynaBean abean=new LazyDynaBean();
				FieldItem item=(FieldItem)list.get(i);
				if(item.getItemid()!=null&& "a0101".equalsIgnoreCase(item.getItemid()))
					continue;
				abean.set("itemid",item.getItemid());
				abean.set("itemdesc",item.getItemdesc());
				abean.set("select", map.get(item.getItemid()) == null ? "0" : "1");
				messagelist.add(abean);	
			}
			this.getFormHM().put("messagelist", messagelist);
			this.getFormHM().put("lockMGradeColumn", lockMGradeColumn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		
	}


}
