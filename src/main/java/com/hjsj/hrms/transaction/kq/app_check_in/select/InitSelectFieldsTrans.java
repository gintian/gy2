package com.hjsj.hrms.transaction.kq.app_check_in.select;

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
		ArrayList fielditemlist = DataDictionary.getFieldList(table.toUpperCase(),
				Constant.USED_FIELD_SET);
		ArrayList fieldlist=newFieldList(fielditemlist,table);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("table",table);
		this.getFormHM().put("selectedlist",new ArrayList());
	}
	/**
	 * 返回显示的所有选择字段
	 * */
	public static ArrayList newFieldList(ArrayList fielditemlist,String table)
    {
		boolean isPost = true;
		KqParameter para = new KqParameter();
    	if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
    		isPost = false;
    	} else {
    		isPost = true;
    	}
    	
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
			
			if(!"i9999".equals(fielditem.getItemid()))
			{
				if("e01a1".equalsIgnoreCase(fielditem.getItemid())&&!isPost)
					continue;
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
