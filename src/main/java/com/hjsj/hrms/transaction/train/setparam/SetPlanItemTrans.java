package com.hjsj.hrms.transaction.train.setparam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>Title:GzAmountXMLBo.java</p>
 * <p>Description:常量表xml参数解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class SetPlanItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String viewname = constantbo.getValue("plan_mx");
		viewname=viewname!=null&&viewname.trim().length()>3?viewname:"";
		
		ArrayList selectlist = new ArrayList();
		ArrayList itemlist = new ArrayList();
		ArrayList list=DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			boolean b=true;
			FieldItem item=(FieldItem)list.get(i);
			if("B0110".equalsIgnoreCase(item.getItemid()))
				continue;
			else if("E0122".equalsIgnoreCase(item.getItemid()))
				continue;
			else if("r3101".equalsIgnoreCase(item.getItemid()))
				continue;
			else if("r3130".equalsIgnoreCase(item.getItemid()))
				continue;
			else if("R3127".equalsIgnoreCase(item.getItemid()))
				continue;
			else if("R3118".equalsIgnoreCase(item.getItemid()))
				continue;
			else{
				String[] viewnames = viewname.split(",");
				if (viewnames.length > 0) {
					for (int j = 0; j < viewnames.length; j++) {
						if (item.getItemid().equalsIgnoreCase(viewnames[j])) {
							b=false;
						}
					}
				}
			}
			if(b){
				CommonData dataobj = new CommonData(item.getItemid().toUpperCase(),item.getItemdesc());
				itemlist.add(dataobj);
			}
		}
		String[] itemarr = viewname.split(",");
		for(int i=0;i<itemarr.length;i++){
			FieldItem item = DataDictionary.getFieldItem(itemarr[i]);
			if(item!=null){
				if("0".equals(item.getUseflag()))
					continue;
				String itemid = item.getItemid().toUpperCase();
				String itemdesc = item.getItemdesc().toUpperCase();
				if("B0110".equalsIgnoreCase(item.getItemid()))
					continue;
				else if("E0122".equalsIgnoreCase(item.getItemid()))
					continue;
				else if("r3101".equalsIgnoreCase(item.getItemid()))
					continue;
				else if("r3130".equalsIgnoreCase(item.getItemid()))
					continue;
				else if("R3127".equalsIgnoreCase(item.getItemid()))
					continue;
				else if("R3118".equalsIgnoreCase(item.getItemid()))
					continue;
				CommonData dataobj = new CommonData(itemid,itemdesc); 
				selectlist.add(dataobj);
			}
		}
//		if(itemlist.size()<1){
//			itemlist.addAll(selectlist);
//			selectlist.clear();
//		}
		
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("selectlist",selectlist);
	}
}
