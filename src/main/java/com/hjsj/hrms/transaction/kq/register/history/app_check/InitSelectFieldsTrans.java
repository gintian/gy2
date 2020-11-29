package com.hjsj.hrms.transaction.kq.register.history.app_check;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitSelectFieldsTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm=(HashMap)this.getFormHM();	
		String table = (String)hm.get("table");
		ArrayList fielditemlist = DataDictionary.getFieldList(table.toUpperCase().substring(0,3),
				Constant.USED_FIELD_SET);
		ArrayList fieldlist=newFieldList(fielditemlist,table);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("table",table);
		HashMap rhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sels=(String) rhm.get("sels");
		rhm.remove("sels");
		if("1".equals(sels))
			this.getFormHM().put("selectedlist",new ArrayList());
	}
	/**
	 * 返回显示的所有选择字段
	 * */
	public static ArrayList newFieldList(ArrayList fielditemlist,String ta)
    {
		String table=ta.toLowerCase().substring(0,3);
    	ArrayList list=new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			if(fielditem.getItemid().equalsIgnoreCase(table+"04"))
				continue;
			if(fielditem.getItemid().equalsIgnoreCase(table+"01"))  //单据序号 隐藏 因为是系统指标
				continue;
			
			// 隐藏人员编号，人员编号是系统指标
			if ("a0100".equalsIgnoreCase(fielditem.getItemid())) {
				continue;
			}
			
			KqParameter kqpr = new KqParameter();
			if("e01a1".equalsIgnoreCase(fielditem.getItemid())&&"1".equals(kqpr.getKq_orgView_post()))
				continue;
			
			if(!"i9999".equals(fielditem.getItemid()))
			{
				if ("e01a1".equalsIgnoreCase(fielditem.getItemid()) && "职位".equals(fielditem.getItemdesc())) {
				 CommonData fieldvo = new CommonData(fielditem.getItemid(), "岗位名称");
				 list.add(fieldvo);
				} else {
					CommonData fieldvo = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
					list.add(fieldvo);
				}
			}
		}
    	
    	return list;
    }

}
