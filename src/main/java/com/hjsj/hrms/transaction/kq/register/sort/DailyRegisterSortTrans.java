package com.hjsj.hrms.transaction.kq.register.sort;

import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class DailyRegisterSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String checkflag = (String) hm.get("checkflag");
		String setid = (String) hm.get("setid");
		String sortitem = "";
		SortBo bo = new SortBo(this.frameconn, this.userView);
		if (bo.isExistSort()) {
			sortitem = bo.querrySort();
  			sortitem = sortitem.replaceAll(",", "`");
		}
//		String sortitem = (String) this.getFormHM().get("sortitem");
//		sortitem = sortitem == null ? "" : sortitem;
		ArrayList list = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			FieldItem item = (FieldItem) list.get(i);			
			if (!"M".equalsIgnoreCase(item.getItemtype())&&!"a0100".equalsIgnoreCase(item.getItemid())&&!"nbase".equalsIgnoreCase(item.getItemid())) {
				CommonData data = new CommonData();
				data.setDataValue(item.getItemid() + ":" + item.getItemdesc());
				data.setDataName(item.getItemdesc());
				itemlist.add(data);
			}
		}

		this.getFormHM().put("checkflag", checkflag);
		this.getFormHM().put("itemid", "");
		this.getFormHM().put("itemlist", itemlist);
		this.getFormHM().put("sortitem", sortitem);
		
	}

}
