package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SelectProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
//		 TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		
		String filedName = (String)hm.get("fieldname");
		filedName = filedName!=null&&filedName.length()>0?filedName:"";
		String[] arr = filedName.split("-");
		if(arr!=null&&arr.length==2){
			hm.put("fielditemlist",projectset.fielditemList(dao,arr[1])); 
		}else{
			ArrayList list = new ArrayList();
			CommonData obj=new CommonData("","");
			list.add(obj);
			hm.put("fielditemlist",list);
		}

	}

}
