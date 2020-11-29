package com.hjsj.hrms.transaction.general.template.goabroad.collect.select;

import com.hjsj.hrms.businessobject.general.template.collect.CollectStat;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;

public class InitSelectFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// String select_flag=(String)this.getFormHM().get("select_flag");
		// if(select_flag==null||select_flag.length()<=0)
		// select_flag="q17";
		// String hols_status=(String)this.getFormHM().get("hols_status");
		// if(hols_status==null||hols_status.length()<=0)
		// hols_status="06";
		String subset = (String) this.getFormHM().get("subset");
		ArrayList fielditemlist = DataDictionary.getFieldList(subset,
				Constant.USED_FIELD_SET);
		String Privtable = this.userView.analyseTablePriv(subset);
		if (!"0".equals(Privtable)) {
			for (Iterator it = fielditemlist.iterator(); it.hasNext();) {
				FieldItem fielditem = (FieldItem) it.next();
				String itemid = fielditem.getItemid();
				String privField = this.userView.analyseFieldPriv(itemid);
				if ("0".equals(privField)) {
					it.remove();
				}
			}
		} else {
			fielditemlist = new ArrayList();
		}
		CollectStat collectStat = new CollectStat(this.getFrameconn());
		fielditemlist = collectStat.getColumnlist(fielditemlist);
		ArrayList filelist = newFieldList(fielditemlist);
		this.getFormHM().put("selectfieldlist", filelist);
		// this.getFormHM().put("hols_status",hols_status);
	}

	public static ArrayList newFieldList(ArrayList fielditemlist) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if (!"i9999".equals(fielditem.getItemid())
					&& !"A0100".equalsIgnoreCase(fielditem.getItemid())) {
				CommonData fieldvo = new CommonData(fielditem.getItemid(),
						fielditem.getItemdesc());
				list.add(fieldvo);
			}
		}
		return list;
	}
}
