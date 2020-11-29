package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class SaveHireViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String hirecontent = (String)hm.get("hirecontent");
		String hiddenItem = (String)hm.get("hiddenItem");
		if(hiddenItem==null)
			hiddenItem="";
		if(!hiddenItem.startsWith(",")){
			hiddenItem=","+hiddenItem;
		}
		if(!hiddenItem.endsWith(",")){
			hiddenItem=hiddenItem+",";
		}
		hirecontent=hirecontent!=null&&hirecontent.trim().length()>0?hirecontent.substring(0,hirecontent.length()-1):"";
		
		String viewcontent = (String)hm.get("viewcontent");
		viewcontent=viewcontent!=null&&viewcontent.trim().length()>0?viewcontent.substring(0,viewcontent.length()-1):"";
//		System.out.println(hirecontent);
//		System.out.println(viewcontent);
		//更新fielditem库中的displaywidth的值
		String hirestr [] = hirecontent.split(",");
		String viewstr [] = viewcontent.split(",");
		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
		for(int i = 0;i<hirestr.length;i++){
			String fielditem = hirestr[i];
			if(hirestr[i].indexOf("_")!=-1){
				 fielditem = hirestr[i].substring(0,hirestr[i].indexOf("_"));
			}
				 FieldItem item=DataDictionary.getFieldItem(fielditem);
				 if(item!=null&&hiddenItem.indexOf(","+hirestr[i]+",")==-1){//把临时变量给过滤掉
					 ArrayList listtemp = new ArrayList();
					 String fieldset = item.getFieldsetid();
					 listtemp.add("0");
					 listtemp.add(fielditem);
					 listtemp.add(fieldset);
					 item.setDisplayid(0);
					 list.add(listtemp);
				 }
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.batchUpdate("update fielditem set displaywidth = ? where itemid = ? and fieldsetid = ? ",list);
			
			for(int i = 0;i<viewstr.length;i++){
				String fielditem = viewstr[i];
				if(viewstr[i].indexOf("_")!=-1){
					 fielditem = viewstr[i].substring(0,viewstr[i].indexOf("_"));
				}
					 FieldItem item=DataDictionary.getFieldItem(fielditem);
					 if(item!=null&&hiddenItem.indexOf(","+viewstr[i]+",")!=-1){
						 ArrayList listtemp = new ArrayList();
						 String fieldset = item.getFieldsetid();
						 listtemp.add(""+item.getItemlength());
						 listtemp.add(fielditem);
						 listtemp.add(fieldset);
						 item.setDisplayid(0);
						 list2.add(listtemp);
						 
					 }
			}
			dao.batchUpdate("update fielditem set displaywidth = ? where itemid = ? and fieldsetid = ? ",list2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		hm.put("info",hirecontent);

		
	}

	
}
