package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SelectFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String filedName = (String)this.getFormHM().get("fieldname");
		filedName = filedName!=null&&filedName.length()>0?filedName:"";
		if(filedName.indexOf("-")!=-1)
			filedName = filedName.substring(2);
		
		ArrayList fieldlist = new ArrayList();
		if(filedName.trim().length()>0){
			ArrayList list = new ArrayList();
			if("B".equalsIgnoreCase(filedName.substring(0,1)))
				list=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			else if("K".equalsIgnoreCase(filedName.substring(0,1)))
				list = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
			else
				list=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			
//			if(filedName.substring(0,1).equalsIgnoreCase("B")){
//				list.add(DataDictionary.getFieldSetVo("B01"));
//				list.add(DataDictionary.getFieldSetVo(filedName));
//			}else if(filedName.substring(0,1).equalsIgnoreCase("K")){
//				list.add(DataDictionary.getFieldSetVo("K01"));
//				list.add(DataDictionary.getFieldSetVo(filedName));
//			}else{
//				list.add(DataDictionary.getFieldSetVo("B01"));
//				list.add(DataDictionary.getFieldSetVo(filedName));
//			}

			for(int i=0;i<list.size();i++){
				FieldSet fieldset=(FieldSet)list.get(i);
				/**未构库不加进来*/
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				
				CommonData temp=new CommonData(fieldset.getFieldsetid(),
						fieldset.getFieldsetid()+":"+fieldset.getCustomdesc());
				fieldlist.add(temp);
			}
		}
		this.getFormHM().put("fieldsetlist", fieldlist);
	}

}
