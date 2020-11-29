package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PartyFeesFieldInfoTrans extends  IBusiness{

	@Override
	public void execute() throws GeneralException {
		//加载子集指标数据
		String fieldsetid = (String) this.formHM.get("fieldsetid");
		ArrayList fielditemlist = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
		if(fielditemlist == null)
			fielditemlist = new ArrayList();
		ArrayList feesFieldlist = new ArrayList();
		ArrayList payStatusFieldlist = new ArrayList();
		ArrayList payTimeFieldlist = new ArrayList();
		for(int i = 0 ; i < fielditemlist.size() ; i++){
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if("0".equalsIgnoreCase(fielditem.getUseflag()))//未构库指标不能出现在页面
				continue;
			HashMap map = new HashMap();
			if("N".equalsIgnoreCase(fielditem.getItemtype()) && !"次数".equalsIgnoreCase(fielditem.getItemdesc()) && fielditem.getDecimalwidth() == 2){
				map.put("itemid", fielditem.getItemid());
				map.put("itemdesc", fielditem.getItemdesc());
				feesFieldlist.add(map);
				continue;
			}
			if("A".equalsIgnoreCase(fielditem.getItemtype()) && "45".equalsIgnoreCase(fielditem.getCodesetid())){
				map.put("itemid", fielditem.getItemid());
				map.put("itemdesc", fielditem.getItemdesc());
				payStatusFieldlist.add(map);
				continue;
			}
			if("D".equalsIgnoreCase(fielditem.getItemtype()) && fielditem.getItemlength()==10 && !"年月标识".equalsIgnoreCase(fielditem.getItemdesc())){
				map.put("itemid", fielditem.getItemid());
				map.put("itemdesc", fielditem.getItemdesc());
				payTimeFieldlist.add(map);
			}
		}
		this.formHM.put("feesFieldlist",feesFieldlist );
		this.formHM.put("payStatusFieldlist", payStatusFieldlist);
		this.formHM.put("payTimeFieldlist", payTimeFieldlist);
		
	}

}
