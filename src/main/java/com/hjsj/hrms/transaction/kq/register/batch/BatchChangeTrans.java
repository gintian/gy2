package com.hjsj.hrms.transaction.kq.register.batch;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 员工日明细批量修改
 * @author Owner
 *	@author wangyao
 */
public class BatchChangeTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table = (String) hm.get("table");
		ArrayList list=new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		list = newFieldItemList(fieldlist,table);
		this.getFormHM().put("batchlist", list);
	}
	public static ArrayList newFieldItemList(ArrayList fieldlist,String table)
	{
		ArrayList list = new ArrayList();
		CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("##");		
		list.add(dataobj);
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if("Q05".equalsIgnoreCase(table) && "q03z1".equals(fielditem.getItemid()))
				continue;
			if("1".equals(fielditem.getState()))
			{
						
						fielditem.setVisible(true);
			}else
			{
						fielditem.setVisible(false);
			}
			if(fielditem.isVisible())
			{
				if(!"nbase".equals(fielditem.getItemid())&&!"a0100".equals(fielditem.getItemid())&&!"b0110".equals(fielditem.getItemid())&&!"e0122".equals(fielditem.getItemid())&&!"e01a1".equals(fielditem.getItemid())&&!"a0101".equals(fielditem.getItemid())&&!"q03z5".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())&&!"i9999".equals(fielditem.getItemid()))
				{
//					System.out.println("222 = "+fielditem.getItemtype());
					if("N".equals(fielditem.getItemtype()))
					{
						dataobj = new CommonData(); 
						dataobj.setDataName(fielditem.getItemdesc());
						dataobj.setDataValue(fielditem.getItemid());
						list.add(dataobj);
					}
					
				}
			}
		}
		return list;
	}
}
