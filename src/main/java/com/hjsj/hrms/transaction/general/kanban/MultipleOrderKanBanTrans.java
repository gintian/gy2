package com.hjsj.hrms.transaction.general.kanban;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 看板管理组合排序
 * @author xujian 2009-11-5
 *
 */
public class MultipleOrderKanBanTrans extends IBusiness {


	public void execute() throws GeneralException {
		ArrayList orderlist = new ArrayList();//排序指标
		String sortitem = null;
		try{
		sortitem= (String)this.getFormHM().get("sortitem");
		sortitem = sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
		CommonData tempobj = new CommonData("","");//xuj 2009-11-5  发单人、接单人、任务审核人进行排序
		if(sortitem.toUpperCase().indexOf("A0101")==-1){
			tempobj = new CommonData("A0101:发单人","发单人");
			orderlist.add(tempobj);
		}
		if(sortitem.toUpperCase().indexOf("A0101_0")==-1){
			tempobj = new CommonData("A0101_0:接单人","接单人");
			orderlist.add(tempobj);
		}
		if(sortitem.toUpperCase().indexOf("A0101_1")==-1){
			tempobj = new CommonData("A0101_1:任务审核人","任务审核人");
			orderlist.add(tempobj);
		}
		ArrayList list = DataDictionary.getFieldList("p05",Constant.USED_FIELD_SET);

		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if("p0501".equalsIgnoreCase(fielditem.getItemid())
					|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setItemlength(20);
			}
			if(!fielditem.isVisible())
				continue;
			if("N".equalsIgnoreCase(fielditem.getItemtype())&&fielditem.getDecimalwidth()>2)
				fielditem.setDecimalwidth(2);
			if("M".equalsIgnoreCase(fielditem.getItemtype()))
				continue;
			if("p0500".equalsIgnoreCase(fielditem.getItemid())){
				continue;
			}
			if(sortitem.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
				tempobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(), fielditem.getItemdesc());
				orderlist.add(tempobj);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("orderlist", orderlist);
			this.getFormHM().put("sortitem", sortitem);
		}
	}

}
