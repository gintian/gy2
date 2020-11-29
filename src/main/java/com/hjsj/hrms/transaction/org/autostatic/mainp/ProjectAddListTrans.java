package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
public class ProjectAddListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		String fielditemid = (String)hm.get("fieldname");
		fielditemid = SafeCode.decode(fielditemid);
		fielditemid = fielditemid!=null&&fielditemid.length()>0?fielditemid:"";
		String[] arr = fielditemid.split("-");
		if(arr!=null&&arr.length==3){
			if(arr[1].trim().length()>6&& "3".equals(arr[2])){
				ArrayList list = projectset.usedSummayList(dao,arr[0]);
				String itemArr[] = arr[1].split("::");
				FieldItem fielditem = DataDictionary.getFieldItem(itemArr[0]);
				
				if(fielditem!=null){
					CommonData obj=new CommonData(fielditem.getItemid(),
							fielditem.getItemdesc()+"  [目标子集:"+itemArr[2]+"    目标指标:"+itemArr[4]+"]");
					list.add(obj);
				}
				hm.put("usedlist",list);
				hm.put("itemid",fielditem.getItemid());
			}else{
				ArrayList list = projectset.usedList(dao,this.userView,arr[0],arr[2]);
				FieldItem fielditem = DataDictionary.getFieldItem(arr[1]);
				CommonData obj=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
				list.add(obj);
				hm.put("usedlist",list);
				hm.put("itemid",fielditem.getItemid());
			}
			
		}
	}

}
