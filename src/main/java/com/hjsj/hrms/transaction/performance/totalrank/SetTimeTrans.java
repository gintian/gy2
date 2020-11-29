package com.hjsj.hrms.transaction.performance.totalrank;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SetTimeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String setid = (String)this.getFormHM().get("setid");
		setid=setid!=null?setid:"";
		
		String timeitemid=(String)this.getFormHM().get("timeitemid");
		timeitemid=timeitemid!=null?timeitemid:"";
		
		String fromScope = (String)this.getFormHM().get("fromScope");
		fromScope=fromScope!=null?fromScope:"";
		
		String toScope = (String)this.getFormHM().get("toScope");
		toScope=toScope!=null?toScope:"";
		
		ArrayList timeFieldList=new ArrayList();
		timeFieldList.add(new CommonData("no",""));
		ArrayList list=this.userView.getPrivFieldList(setid,Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if(fielditem!=null){
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				
				if("D".equalsIgnoreCase(fielditem.getItemtype()))
				{
					timeFieldList.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
				}
			}
		}
		this.getFormHM().put("timeFieldList",timeFieldList);
		this.getFormHM().put("toScope",toScope);
		this.getFormHM().put("fromScope",fromScope);
		this.getFormHM().put("timeitemid",timeitemid);
	}

}
