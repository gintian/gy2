package com.hjsj.hrms.transaction.browse.history;


import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description>:ExportExcelTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:6 20, 2012 11:56:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: xuj</p>
 */
public class IsSumtypeTrans extends IBusiness{

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(type==null){
			type = (String)hm.get("type");
		}
		if("judge".equals(type)){
			String msg = "no";
			String itemid=(String)this.getFormHM().get("itemid");
			FieldItem item = DataDictionary.getFieldItem(itemid);
			if(item!=null){
				String fieldtype=item.getItemtype();
				if(!item.isMainSet()&&"N".equals(fieldtype)){
					FieldSet fieldset = DataDictionary.getFieldSetVo(item.getFieldsetid());
					if(fieldset!=null){
						String changeflag = fieldset.getChangeflag();
						if("1".equals(changeflag)||"2".equals(changeflag)){
							msg=item.getItemdesc();
						}
					}
				}
			}
			this.getFormHM().put("msg",msg);
			
		}else if("query".equals(type)){
			String itemid = (String)hm.get("itemid");
			ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
			String HzMenus =xml.getTextValue("/Emp_HisPoint/HzMenus").toUpperCase();
			int index = HzMenus.indexOf(itemid.toUpperCase());
			String sumtype="";
			if(index!=-1){
				sumtype=HzMenus.substring(index+6,index+9);
			}
			this.getFormHM().put("sumtype", sumtype);
		}else if("save".equals(type)){
			String itemid = (String)this.getFormHM().get("itemid");
			String sumtype = (String)this.getFormHM().get("sumtype");
			ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
			String HzMenus =xml.getTextValue("/Emp_HisPoint/HzMenus").toUpperCase();
			int index = HzMenus.indexOf(itemid.toUpperCase());
			if(index!=-1){
				if("no".equals(sumtype)){
					HzMenus=HzMenus.substring(0,index)+HzMenus.substring(index+9);
				}else{
					HzMenus=HzMenus.substring(0,index+6)+sumtype+HzMenus.substring(index+9);
				}
			}else{
					if(HzMenus.endsWith(",")||HzMenus.length()==0){
						HzMenus+=itemid.toUpperCase()+":"+sumtype;
					}else{
						HzMenus+=","+itemid.toUpperCase()+":"+sumtype;
					}
			}
			xml.setTextValue("/Emp_HisPoint/HzMenus", HzMenus);
			xml.saveStrValue();
		}
	}
}
