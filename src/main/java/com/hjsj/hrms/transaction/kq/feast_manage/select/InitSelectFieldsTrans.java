package com.hjsj.hrms.transaction.kq.feast_manage.select;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class InitSelectFieldsTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String select_flag=(String)this.getFormHM().get("select_flag");
		if(select_flag==null||select_flag.length()<=0)
			select_flag="q17";
		String hols_status=(String)this.getFormHM().get("hols_status");
		if(hols_status==null||hols_status.length()<=0)
			hols_status="06";
		ArrayList fielditemlist = DataDictionary.getFieldList("Q17",
				Constant.USED_FIELD_SET);
		ArrayList fieldlist=newFieldList(fielditemlist,select_flag);
		this.getFormHM().put("selectfieldlist",fieldlist);
		this.getFormHM().put("hols_status",hols_status);
	}
	/**
	 * 返回显示的所有选择字段
	 * */
	public static ArrayList newFieldList(ArrayList fielditemlist,String select_flag)
    {
    	ArrayList list=new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			if(!"i9999".equals(fielditem.getItemid())&&!"nbase".equals(fielditem.getItemid())&&!"q1709".equals(fielditem.getItemid())&& "1".equals(fielditem.getState()))
			{
				CommonData fieldvo=null;
				fieldvo = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
				list.add(fieldvo);
			}
		}    	
    	return list;
    }
}
