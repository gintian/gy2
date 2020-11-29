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
public class AddProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		String filed = (String)reqhm.get("fieldsetid");
		
		
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("fieldsetid");
		reqhm.remove("type");
		ArrayList list = projectset.addusedList(dao,filed,type,this.frameconn);
		for(int i=0;i<list.size();i++){
			CommonData obj = (CommonData)list.get(i);
			if(obj!=null){
				String itemid = obj.getDataValue();
				if(itemid.trim().length()>1){
					if(!"2".equals(this.userView.analyseFieldPriv(itemid))){
						list.remove(i);
						i--;
					}
				}
			}
		}
		hm.put("usedlist",list);
		hm.put("type",type);
		
		if("3".equals(type)&& "K".equalsIgnoreCase(filed.substring(0,1))){
			ArrayList fieldlist = new ArrayList();
			CommonData obj=new CommonData("","");
			fieldlist.add(obj);
			String param = (String)hm.get("param");
			fieldlist.addAll(projectset.fieldSetList(dao,filed,param,this.frameconn));
			hm.put("targetsetlist",fieldlist);
			hm.put("checkfalg","1");
		}else{
			hm.put("checkfalg","0");
		}
	}
}
