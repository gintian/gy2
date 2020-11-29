package com.hjsj.hrms.transaction.org.autostatic.confset;


import com.hjsj.hrms.businessobject.org.autostatic.confset.SubsetConfsetBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class DataSynchroTrans extends IBusiness{

	public void execute() throws GeneralException{
		SubsetConfsetBo scb = new SubsetConfsetBo();
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		if(reqhm.containsKey("init")){ 
			List confsetlist =  scb.getconfsetlist(); 
			for(int i=0;i<confsetlist.size();i++){
				FieldSet fs=(FieldSet)confsetlist.get(i);
				String fid=fs.getFieldsetid();
				if(!"0".equals(this.userView.analyseTablePriv(fid))){
					confsetlist.remove(i);
					i--;
				}			
			}
			
			hm.put("confsetlist", confsetlist);
			DataDictionary.refresh();
		}else{
			List confsetlist =  scb.getconfsetlist();
			for(int i=0;i<confsetlist.size();i++){
				FieldSet fs=(FieldSet)confsetlist.get(i);
				String fid=fs.getFieldsetid();
				if("0".equals(this.userView.analyseTablePriv(fid))|| "0".equals(this.userView.analyseTablePriv(fid,1))){
					confsetlist.remove(i);
					i--;
				}			
			}
			hm.put("confsetlist", confsetlist);	
		}
		
		int year = Calendar.getInstance().get(Calendar.YEAR);		
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;

		hm.put("monthnum",month+"");
		hm.put("yearnum",year+"");


		
		
	}

}
