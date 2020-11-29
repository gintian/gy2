package com.hjsj.hrms.transaction.gz.sort;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
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
public class ItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldid = (String)this.getFormHM().get("fieldid");
		fieldid=fieldid!=null&&fieldid.trim().length()>0?fieldid:"";
		
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
		
		String arr[] = sortitem.split("`");
		ArrayList itemlist = new ArrayList();
		for(int i=0;i<arr.length;i++){
			String arr_item[] = arr[i].split(":");
			if(arr_item.length==3){
				itemlist.add(arr_item[0]);
			}
		}
		ArrayList sortitemlist = new ArrayList();
		ArrayList list = this.userView.getPrivFieldList(fieldid,Constant.USED_FIELD_SET);
		if(list!=null){
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)list.get(i);
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				int flag = 0 ;
				for(int j=0;j<itemlist.size();j++){
					if(fielditem.getItemid().equalsIgnoreCase((String)itemlist.get(j))){
						flag=1;
					}
				}
				if(flag==0){
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),
							fielditem.getItemdesc());
					sortitemlist.add(dataobj);
				}
			}
		}
		this.getFormHM().put("itemlist",sortitemlist);
	}

}
