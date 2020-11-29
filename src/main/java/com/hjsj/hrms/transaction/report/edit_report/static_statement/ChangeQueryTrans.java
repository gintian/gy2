package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class ChangeQueryTrans extends IBusiness{
	
	public void execute() throws GeneralException{
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String position=(String)hm.get("position");
		String method=(String)hm.get("method");
		hm.remove("position");
		hm.remove("method");
		String scopeid =(String)hm.get("scopeid");
		String scopeunitsids="";
		String temp[];
		String newunits="";
		String sql="select * from tscope where scopeid="+scopeid;
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				scopeunitsids=Sql_switcher.readMemo(this.frowset, "units");
			}
			if(scopeunitsids.trim().length()!=0){
				temp=scopeunitsids.split("`");
				int k=Integer.parseInt(position)-1;
			
				
				if("up".equalsIgnoreCase(method.trim())){
					
					String tm;
					tm=temp[k-1];
					temp[k-1]=temp[k];
					temp[k]=tm;
				}
				if("down".equalsIgnoreCase(method.trim())){
					
					String tm;
					tm=temp[k];
					temp[k]=temp[k+1];
					temp[k+1]=tm;
				}
				
				for(int i=0;i<temp.length;i++){
					newunits+=temp[i]+"`";
				}
			}
			newunits=newunits.substring(0,newunits.length()-1);
			sql="update tscope set units='"+newunits+"' where scopeid="+scopeid;
			dao.update(sql);
			} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
