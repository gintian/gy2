package com.hjsj.hrms.transaction.orginfo.leader;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetLeaderMainItem extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String setName = PubFunc.nullToStr((String)this.getFormHM().get("setName"));
		//领导班子主集关联72代码类的指标
		ArrayList leaderTypeList = new ArrayList();
		leaderTypeList.add(new CommonData("", ""));
		//领导班子主集字符或数字指标
		ArrayList sessionitemList = new ArrayList();
		sessionitemList.add(new CommonData("", ""));
		if(setName.length()>0){
			ArrayList fieldList = DataDictionary.getFieldList(setName, Constant.USED_FIELD_SET);
			for(int i=0;fieldList!=null && i<fieldList.size();i++){
				FieldItem fi = (FieldItem)fieldList.get(i);
				if("72".equals(fi.getCodesetid())){
					CommonData cm = new CommonData(fi.getItemid(),fi.getItemdesc());
					leaderTypeList.add(cm);
				}else if("N".equals(fi.getItemtype()) || ("0".equals(fi.getCodesetid())&& "A".equals(fi.getItemtype()))){
					CommonData cm = new CommonData(fi.getItemid(),fi.getItemdesc());
					sessionitemList.add(cm);
				}
			}
		}
		
		this.getFormHM().put("leaderTypeList", leaderTypeList);
		this.getFormHM().put("sessionitemList", sessionitemList);
		
	}
}
