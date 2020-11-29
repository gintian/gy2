package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 同步唯一性指标
 * <p>Title:SearchHrSyncOnlyFiled.java</p>
 * <p>Description>:SearchHrSyncOnlyFiled.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 28, 2010 2:07:30 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class SearchHrSyncOnlyFiled extends IBusiness {

	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);	
		String fields = hsb.getTextValue(hsb.FIELDS);
		String[] field = fields.split(",");
		String rfileds = ""; 
		for(int i=0;i<field.length;i++){
			FieldItem item=DataDictionary.getFieldItem(field[i]);
			if(item==null)
				continue;
			if("0".equalsIgnoreCase(item.getUseflag()))
				continue;
			if(item.isCode())
				continue;
			if(!item.isFillable())//必填项
				continue;
			rfileds += field[i]+",";
		}
		if(rfileds.length()>0)
			rfileds = rfileds.substring(0,rfileds.length()-1);
		ArrayList onlyfieldlist=hsb.getSimpleFields(rfileds);
		this.getFormHM().put("onlyfieldlist",onlyfieldlist);
		String onlyfield = hsb.getAttributeValue(hsb.HR_ONLY_FIELD);		
		this.getFormHM().put("onlyfield",onlyfield);
		
	}


}
