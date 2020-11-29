package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetNeedsFildsTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=new ArrayList();
		ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
		for(int i=0;i<z03list.size();i++)
		{
			FieldItem item=(FieldItem)z03list.get(i);
 			if("A".equalsIgnoreCase(item.getItemtype())|| "N".equalsIgnoreCase(item.getItemtype()))
 			{
 				if("z0101".equalsIgnoreCase(item.getItemid())|| "z0301".equalsIgnoreCase(item.getItemid())|| "z0335".equalsIgnoreCase(item.getItemid()))
 					continue;
 				ArrayList chanellsit=new ArrayList();
 				if("z0336".equalsIgnoreCase(item.getItemid())){
 					String codesetid=item.getCodesetid();
 					chanellsit=AdminCode.getCodeItemList(codesetid);
 					ArrayList chanelList=new ArrayList();
 					CommonData cd1 = new CommonData();
					cd1.setDataName("请选择");
					cd1.setDataValue("-1");
					chanelList.add(cd1);
 					for(int k=0;k<chanellsit.size();k++){
 						CodeItem item1=(CodeItem)chanellsit.get(k);
 						CommonData cd = new CommonData();
 						cd.setDataName(item1.getCodename());
 						cd.setDataValue(item1.getCcodeitem()+"/"+item1.getCodename());
 						chanelList.add(cd);
 					}
 					this.getFormHM().put("chanelList", chanelList);
 					continue;
 				}
 				boolean vi=item.isVisible();
 				if(vi){
 					
 				}else{
 					continue;
 				}
 				if("0".equals(item.getState()))
					continue;
 				CommonData cd = new CommonData();
				cd.setDataName(item.getItemdesc());
				cd.setDataValue(item.getItemid()+"/"+item.getItemtype().toUpperCase());
 				list.add(cd);
 			}
		}
		this.getFormHM().put("zpchanel", "-1");
		this.getFormHM().put("zpNeedsFielsList", list);
	}
}
