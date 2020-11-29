package com.hjsj.hrms.transaction.train.setparam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SavePlanItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList subclass_value=(ArrayList)this.getFormHM().get("subclass_value");
		String paravalue = "B0110,E0122,R3130,R3127,R3118";
		if(subclass_value!=null&&subclass_value.size()>0){
			for(int i=0;i<subclass_value.size();i++){
				paravalue+=","+(String)subclass_value.get(i);
			}
		}else{
			paravalue="";
			ArrayList list=DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
			for(int i=0;i<list.size();i++){
				FieldItem item=(FieldItem)list.get(i);
				paravalue+=item.getItemid()+",";
			}
		}
	
		if(paravalue!=null&&paravalue.trim().length()>0){
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			constantbo.setValue("plan_mx",paravalue);
			constantbo.saveStrValue();
		}
		
	}

}
