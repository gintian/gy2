package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExecuteStateTrans extends IBusiness{
	public void execute () throws GeneralException{
		try{
			
			String ids = "";
			if(((String)this.getFormHM().get("selectIds")) != null && ((String)this.getFormHM().get("selectIds")).trim().length()>0)
				ids = (String)this.getFormHM().get("selectIds");
			if(ids.indexOf(",") != -1){
				ids=ids.substring(1);
			}
			String[] str_arr=ids.split(",");
			for(int i=0;i<str_arr.length;i++){
				String have=isHaveCandidate(str_arr[i]);
				if("no".equals(have)){
					String name=getCommendName(str_arr[i]);
					this.getFormHM().put("have",have);
					this.getFormHM().put("name",name);
					return;
				}
			}
			
			String sql = "update p02 set p0209 ='05' where p0201 in("+ids+")";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql);
			this.getFormHM().put("have","yes");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public String isHaveCandidate(String id){
		String have="no";
		String sql="select * from p03 where p0201="+id;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				have="yes";
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return have;
	}
	public String getCommendName(String id){
		String name="";
		String sql="select p0203 from p02 where p0201="+id;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				name=this.frowset.getString("p0203");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
		
	}

	

}
