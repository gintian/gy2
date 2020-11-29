package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

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
public class SaveLockViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		String tabid = (String)hm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String hirecontent = (String)hm.get("hirecontent");
		hirecontent=hirecontent!=null&&hirecontent.trim().length()>0?hirecontent.substring(0,hirecontent.length()-1):"";
		
		String viewcontent = (String)hm.get("viewcontent");
		viewcontent=viewcontent!=null&&viewcontent.trim().length()>0?viewcontent.substring(0,viewcontent.length()-1):"";

		//hirecontent=hirecontent.replaceAll(",","','");
		//viewcontent=viewcontent.replaceAll(",","','");
		hm.put("info",hirecontent);
///		if(updateHide(hirecontent,tabid)){
///			if(updateView(viewcontent,tabid)){
///				hm.put("info","ok");
///			}
///		}else{
///			hm.put("info","no");
///		}
		
	}

}
