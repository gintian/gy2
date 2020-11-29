package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
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
public class SearchFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		
		String filedName = (String)hm.get("fieldname");
		filedName = SafeCode.decode(filedName);
		filedName = filedName!=null&&filedName.length()>0?filedName:"";
		String[] arr = filedName.split("-");
		ArrayList itemlist = new ArrayList();
		if(arr!=null&&arr.length==2){
			if("4".equals(arr[0])){
				itemlist = fieldList(arr[1]);
			}else if("3".equals(arr[0])&& "K".equalsIgnoreCase(arr[1].substring(0,1))){
				itemlist = projectset.usedSummayList(dao,arr[1]);
			}else{
				itemlist = projectset.usedList(dao,arr[1],arr[0]);
			}
		}else{
			CommonData obj=new CommonData("","");
			itemlist.add(obj);
		}
		for(int i=0;i<itemlist.size();i++){
			CommonData obj = (CommonData)itemlist.get(i);
			if(obj!=null){
				String itemid = obj.getDataValue();
				if(itemid.trim().length()>1){
					if(!"2".equals(this.userView.analyseFieldPriv(itemid))){
						itemlist.remove(i);
						i--;
					}
				}
			}
		}
		hm.put("usedlist",itemlist);
	}
	private ArrayList fieldList(String fieldsetid){
		ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		ArrayList list = new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())){
//				if(!fielditem.getItemdesc().equalsIgnoreCase(ResourceFactory.getProperty("hmuster.label.counts"))){
				CommonData obj=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
				list.add(obj);
//				}
			}
		}
		return list;
	}

}
