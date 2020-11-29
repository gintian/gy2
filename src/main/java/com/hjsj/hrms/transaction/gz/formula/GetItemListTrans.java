package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetItemListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList itemlist=null;
		String salaryid = (String)this.getFormHM().get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		
		FormulaBo formulsbo = new FormulaBo(this.userView);
		if("-2".equals(salaryid)){
			itemlist = formulsbo.subStandardList(this.frameconn,salaryid,fieldsetid);
		}else{
			itemlist = formulsbo.subStandardList(this.frameconn,salaryid);
		}
		
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("itemid",itemDesc(itemid,itemlist));
	}
	private String itemDesc(String itemid,ArrayList itemlist){
		String itemdesc = "";
		for(int i=0;i<itemlist.size();i++){
			CommonData obj = (CommonData)itemlist.get(i);
			String desc = obj.getDataName();
			String arr[] = desc.split(":");
			if(arr.length==2){
				if(arr[0].equalsIgnoreCase(itemid)){
					itemdesc=arr[0].toLowerCase()+":"+arr[1];
				}
			}
		}
		return itemdesc;
	}
}
