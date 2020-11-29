package com.hjsj.hrms.transaction.gz.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		
		String[] fitem = sort.split(",");;
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		if(salaryid.length()>0){
			for(int i=0;i<fitem.length;i++){
				String arr[] = fitem[i].split(":");
				if(arr.length==2){
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("update salaryformula set sortid="+i+" where itemname='");
					sqlstr.append(arr[0]);
					sqlstr.append("' and itemid=");
					sqlstr.append(arr[1]);
					sqlstr.append(" and salaryid=");
					sqlstr.append(salaryid);
					try {
						dao.update(sqlstr.toString());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		hm.put("info","ok");
	}

}
