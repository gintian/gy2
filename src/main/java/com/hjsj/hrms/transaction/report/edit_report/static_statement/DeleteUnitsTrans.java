package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DeleteUnitsTrans extends IBusiness{
	public void execute()throws GeneralException{
		ArrayList delList=(ArrayList)this.getFormHM().get("selectedList");
		String scopeunitsids="";
		String scopeid="";
		String nescopeunitsids="";
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		scopeid=(String)hm.get("scopeid");
		String sql="select * from tscope where scopeid="+ scopeid;
		ContentDAO dao=new ContentDAO(this.frameconn);
		LazyDynaBean bean;
		String temp[];
		boolean falg;
		String unitid="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				scopeunitsids=Sql_switcher.readMemo(this.frowset, "units");
			}
			if(scopeunitsids!=null && scopeunitsids.startsWith("`")){
				//add by wangchaoqun on 2014-11-4
				scopeunitsids = scopeunitsids.substring(1);
			}
			temp=scopeunitsids.split("`");
				if(temp.length!=0){
				
					for(int k=0;k<temp.length;k++){
						falg=false;
						for(int i=0;i<delList.size();i++){
							bean=(LazyDynaBean)delList.get(i);
							unitid=(String)bean.get("unitcode");
							if(unitid.equalsIgnoreCase(temp[k].substring(2,temp[k].length()))){
								falg=true;
								break;
							}else{
								
							}
						}
						if(!falg){
							nescopeunitsids+=temp[k]+"`";
						}
					}
				}else{
					
				}
				if(nescopeunitsids.trim().length()!=0){
					nescopeunitsids=nescopeunitsids.substring(0,nescopeunitsids.length()-1);
					sql="update tscope set units='"+nescopeunitsids+"' where scopeid="+scopeid;
				}else{
					sql="update tscope set units='' where scopeid="+scopeid;
				}
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
