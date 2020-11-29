package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SortTrans extends IBusiness {
/**
 * 排序
 */
	public void execute() throws GeneralException {
       
		UserManager userManager=new UserManager(this.userView,this.getFrameconn());
		KqParameter kq_paramter = new KqParameter(this.userView,this.userView.getUserOrgId(),this.getFrameconn());  
		HashMap hashmap =kq_paramter.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String kq_cardno=(String)hashmap.get("cardno");
		String kq_gno=(String)hashmap.get("g_no");
		ArrayList fieldlist=userManager.getFieldList(kq_type,kq_cardno,kq_gno);		
		ArrayList itemlist=new ArrayList();
		CommonData dataobj = new CommonData();
		/*dataobj = new CommonData("nbase:人员库","人员库");
		itemlist.add(dataobj);
		dataobj = new CommonData("b0110:单位","单位");
		itemlist.add(dataobj);
		dataobj = new CommonData("e0122:部门","部门");
		itemlist.add(dataobj);
		dataobj = new CommonData("e01a1:职位","职位");
		itemlist.add(dataobj);
		dataobj = new CommonData("a0101:姓名","姓名");
		itemlist.add(dataobj);*/
		for(int i=0;i<fieldlist.size();i++)
		{ 
			FieldItem item=(FieldItem)fieldlist.get(i);
			if("nbase".equals(item.getItemid())||!item.isVisible())
				continue;
			dataobj = new CommonData(item.getItemid()+":"+item.getItemdesc(),item.getItemdesc());
			itemlist.add(dataobj);
		}
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("flag","0");
		this.getFormHM().put("itemid","");
		this.getFormHM().put("checkflag", "0");
	}

}
