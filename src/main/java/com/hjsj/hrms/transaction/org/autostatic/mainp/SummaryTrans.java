package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SummaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
//		 TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		
		String filedName = (String)hm.get("fieldname");
		filedName = filedName!=null&&filedName.length()>0?filedName:"";
		
		String fielditemid = (String)hm.get("fielditemid");
		fielditemid = fielditemid!=null&&fielditemid.length()>0?fielditemid:"";

		String param = (String)this.getFormHM().get("param");
		ArrayList fieldlist = (ArrayList)projectset.fieldList(dao,filedName,param,this.frameconn);

		for(int i=0;i<fieldlist.size();i++){
			CommonData obj = (CommonData)fieldlist.get(i);
			if(obj!=null){
				String fieldsetid = obj.getDataValue();
				if("0".equals(this.userView.analyseTablePriv(fieldsetid))){
					fieldlist.remove(i);
					i--;
				}	
			}
		}

		hm.remove("fieldname");
		hm.remove("fielditemid");
		hm.remove("fielditem");
		if(fieldlist.size()>0&&filedName.length()<1){
			CommonData obj = (CommonData)fieldlist.get(0);
			filedName = obj.getDataValue();
		}
		hm.remove("fieldname");
		
		hm.put("fieldname",filedName);
		hm.put("fielditemid",fielditemid);
		hm.put("fieldlist",fieldlist);
	}
	
}
