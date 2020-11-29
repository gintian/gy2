package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:GetFieldItemListTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 9, 2008:4:20:51 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class GetFieldItemListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tablename = (String)this.getFormHM().get("tablename");
		//String type = (String)this.getFormHM().get("type");
		ArrayList fieldlist = DataDictionary.getFieldList(tablename,Constant.NOT_USED_FIELD_SET);
		ArrayList fieldlistu = DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
		ArrayList list = getList(fieldlist);
		ArrayList listu = getList(fieldlistu);
		this.getFormHM().put("fieldlist",list);
		this.getFormHM().put("itemlist",listu);
	}
	private ArrayList getList(ArrayList fieldlist){
		ArrayList list = new ArrayList();
		if(fieldlist==null)
			return list;
		for(int i=0;i<fieldlist.size();i++)
	    {
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
	    	/*if(this.userView.analyseTablePriv(fielditem.getFieldsetid()).equals("0"))
	    		continue;*/
			String itemid = fielditem.getItemid();
			//54374 A01Z0是系统内置薪资用的停发标识，此处过滤掉，不允许取消构库 guodd 2019-10-22
			if("B0110".equalsIgnoreCase(itemid)||"E01A1".equalsIgnoreCase(itemid)||"A01Z0".equalsIgnoreCase(itemid))
				continue;
	    	CommonData dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
	    	list.add(dataobj);
	    }
		return list;
	}
}
