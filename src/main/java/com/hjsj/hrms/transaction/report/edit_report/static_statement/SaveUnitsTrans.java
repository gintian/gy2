package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;


public class SaveUnitsTrans extends IBusiness{
	public void execute() throws GeneralException{
		String scopeid=(String)this.getFormHM().get("scopeid");
		String scopeunitids=SafeCode.decode((String)this.getFormHM().get("scopeunitids"));
		String sql0="select * from tscope where scopeid="+scopeid;
		String sql="";
		String odscopeunitids="";
		String nescopeunitids="";
		String[] temp;
		int k=0;
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql0);
			if(this.frowset.next()){
				
				odscopeunitids=Sql_switcher.readMemo(this.frowset, "units");
				
			}
			
			 if(scopeunitids.trim().length()!=0){
				 nescopeunitids=odscopeunitids;
				 if(nescopeunitids.endsWith("`")){
				 }else{
					 if(nescopeunitids.length()>1)
					 nescopeunitids+="`";
				 }
				 temp=scopeunitids.split("`");
				
						 for(int i=0;i<temp.length;i++){
							 if(odscopeunitids.indexOf(temp[i]+"`")==-1){
								 nescopeunitids+=temp[i]+"`";
							 }
						 }
					 sql="update tscope set units='"+nescopeunitids+"'  where scopeid="+scopeid;
					dao.update(sql);
			 }else{
				 
			 }
			
			 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("info", "ok");
	}
}
