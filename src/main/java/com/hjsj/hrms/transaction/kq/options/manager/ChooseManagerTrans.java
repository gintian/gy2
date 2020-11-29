package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseManagerTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		
		String code=(String)this.getFormHM().get("code");
		ArrayList findlist=newFieldList(code);
		this.getFormHM().put("findlist",findlist);		
	}
	
	/**
	 * 返回显示的所有选择字段
	 * */
	public  ArrayList newFieldList(String code)
    {
		
		KqParameter kq_paramter = new KqParameter(this.userView,code,this.getFrameconn());  
		HashMap hashmap =kq_paramter.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String kq_cardno=(String)hashmap.get("cardno");
		String kq_gno=(String)hashmap.get("g_no");
		
		UserManager userManager=new UserManager(this.userView,this.getFrameconn());
		ArrayList fieldlist=userManager.getFieldList(kq_type,kq_cardno,kq_gno);
		CommonData fieldvo=null;
    	ArrayList list=new ArrayList();
    	/*fieldvo = new CommonData("nbase","人员库");
		list.add(fieldvo);
		fieldvo = new CommonData("b0110","单位名称");
		list.add(fieldvo);
		fieldvo = new CommonData("e0122","部门");
		list.add(fieldvo);
		fieldvo = new CommonData("e01a1","职位");
		list.add(fieldvo);
		fieldvo = new CommonData("a0101","人员姓名");
		list.add(fieldvo);*/	
    	for(int i=0;i<fieldlist.size();i++)
    	{
    		FieldItem fielditem=(FieldItem)fieldlist.get(i);
    		if(!fielditem.isVisible())
    			continue;
    		String itemid=fielditem.getItemid();
    		if("t1".equalsIgnoreCase(itemid))
    			itemid=kq_cardno;
    		if("t2".equalsIgnoreCase(itemid))
    			itemid=kq_gno;
    		if("t3".equalsIgnoreCase(itemid))
    			itemid=kq_type;
    		String itemdesc=fielditem.getItemdesc();
    		fieldvo = new CommonData(itemid,itemdesc);
    		list.add(fieldvo);
    	}
        	
    	return list;
    }
}



