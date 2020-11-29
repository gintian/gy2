package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList itemlist = new ArrayList();
		String formula ="";
		try{
			HashMap reqMap = (HashMap)this.getFormHM().get("requestPamaHM");
			String setid = (String)this.getFormHM().get("itemsetid");
			setid = setid.toUpperCase();
			ArrayList fieldlist = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
			CommonData data = new CommonData("","");
			itemlist.add(data);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(item==null)
					continue;
				if("M".equals(item.getItemtype()))
					continue;
				data = new CommonData(item.getItemid()+":"+item.getItemdesc(),item.getItemdesc());
				itemlist.add(data);
			}
			if("P04".equalsIgnoreCase(setid)){
				data = new CommonData("task_score:评分","评分");
				itemlist.add(data);
			}
			formula = (String)this.getFormHM().get("formula");
			formula = com.hrms.frame.codec.SafeCode.decode(formula);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("itemlist", itemlist);
			this.getFormHM().put("formula", formula);
		}
	}

}
