package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class LoadArgumentsTrans extends IBusiness{

	public void execute() throws GeneralException {
		String pointRadius="";
		try{
			
			String sql = "select str_value from constant where constant='KQ_POINT_RADIUS'";
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				pointRadius = this.frowset.getString("str_value");
			}
			
			if(pointRadius == null || pointRadius.trim().length()<1)
				pointRadius = "100";
			
			this.getFormHM().put("pointRadius", pointRadius);
				
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
