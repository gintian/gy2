package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class DelProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();

		String filedName = (String)hm.get("fieldname");
		filedName = SafeCode.decode(filedName);
		filedName = filedName!=null&&filedName.length()>0?filedName:"";
		
		String[] arr = filedName.split("-");
		
		if(arr!=null&&arr.length==3){
			String expre = expRe(dao,arr[1]);
			if(expre!=null&&expre.trim().length()>0){
				if("4".equals(expre.substring(0,1))){
					if("3".equals(arr[2])){
						String[] exprArr = expre.split("::");
						if(exprArr.length==5){
							String lev = exprArr[4].substring(0,exprArr[4].indexOf("-"));
							lev=lev!=null&&lev.trim().length()>0?lev:"0";
							expre="2::"+exprArr[1]+"::"+exprArr[2]+"::"+exprArr[3]+"::"+lev;
						}else{
							expre = "";
						}
					}else if("2".equals(arr[2])){
						String[] exprArr = expre.split("::");
						if(exprArr.length==5){
							expre="3::"+exprArr[4].substring(1);
						}else{
							expre = "";
						}
					}else{
						expre = "";
					}
				}else{
					expre = "";
				}
			}else{
				expre = "";
			}
			StringBuffer sql = new StringBuffer();
			sql.append("update fielditem set expression='"+expre+"' where itemid='");
			sql.append(arr[1]);
			sql.append("'");
			
			try {
				dao.update(sql.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if("3".equals(arr[2])&& "K".equalsIgnoreCase(arr[0].substring(0,1))){
				hm.put("usedlist",projectset.usedSummayList(dao,this.userView,arr[0]));
			}else{
				hm.put("usedlist",projectset.usedList(dao,this.userView,arr[0],arr[2]));
			}
		}
	}
	private String expRe(ContentDAO dao,String fielditemid){
		String expre = "";
		try {
			RowSet rs = dao.search("select expression from fielditem where itemid='"+fielditemid+"'");
			if(rs.next()){
				expre = rs.getString("expression");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return expre;
	}

}
