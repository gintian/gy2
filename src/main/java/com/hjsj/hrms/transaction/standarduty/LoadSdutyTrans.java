package com.hjsj.hrms.transaction.standarduty;

import com.hjsj.hrms.businessobject.standarduty.DutyXmlBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class LoadSdutyTrans extends IBusiness{

	public void execute() throws GeneralException {
		String itemid = this.getFormHM().get("itemid").toString();
		String targetSetid = this.getFormHM().get("targetsetid").toString();
		FieldItem item = DataDictionary.getFieldItem(itemid);
		
		DutyXmlBo dxb = new DutyXmlBo();
		
		
		ArrayList  itemlist = dxb.getFieldItem(targetSetid, Constant.USED_FIELD_SET+"");
		String ps_c_job="";
		RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_JOB");
		 if(vo!=null)
			 ps_c_job = "#".equals(vo.getString("str_value"))?"":vo.getString("str_value");
		String itemtype = item.getItemtype();
		String codesetid = item.getCodesetid();
		ArrayList newlist = new ArrayList();
		if(codesetid!=null && codesetid.length()>1){
			for(int i=0;i<itemlist.size();i++){
				item = (FieldItem)itemlist.get(i);
				
				if(item.getCodesetid().length()>1 && !item.getItemid().equalsIgnoreCase(ps_c_job)){
					// itemlist.remove(item);
					newlist.add(item.getItemid().toUpperCase()+":"+item.getItemdesc());
				}
			}
		}
		else{
			for(int i=0;i<itemlist.size();i++){
				item = (FieldItem)itemlist.get(i);
				if(item.getItemtype().equalsIgnoreCase(itemtype) && item.getCodesetid().length()<2 &&  !item.getItemid().equalsIgnoreCase(ps_c_job)){
					// itemlist.remove(item);
					newlist.add(item.getItemid().toUpperCase()+":"+item.getItemdesc());
				}
			}
		}
		
		this.getFormHM().put("itemlist", newlist);
	}
    
}
