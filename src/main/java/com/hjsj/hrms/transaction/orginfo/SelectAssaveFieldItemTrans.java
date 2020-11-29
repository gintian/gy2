package com.hjsj.hrms.transaction.orginfo;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectAssaveFieldItemTrans extends IBusiness {

	/**
	 * 选择批量另存为指标
	 */
	
	public void execute() throws GeneralException {
		String setname=(String)this.getFormHM().get("setname");
		if(setname==null||setname.length()<=0)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到子集相关信息！"));
		ArrayList fieldlist=this.userView.getPrivFieldList(setname,Constant.USED_FIELD_SET);   //获得当前子集的所有属性
		if(fieldlist==null)
			throw GeneralExceptionHandler.Handle(new Exception("没有得到子集相关内容信息！"));
		ArrayList list=new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			//System.out.println(fielditem.getItemid()+"--"+fielditem.getItemdesc()+"----"+fielditem.getPriv_status()+"--"+fielditem.getState());
			 if(fielditem.getPriv_status()==2&&fielditem.getItemid().indexOf("z0")==-1&&fielditem.getItemid().indexOf("z1")==-1)
			 {
				 FieldItem item=(FieldItem)fielditem.clone();
				 item.setVisible(true);
				 list.add(fielditem.clone());
			 }
		}
        this.formHM.put("assave_fieldlist", list);
	}

}
