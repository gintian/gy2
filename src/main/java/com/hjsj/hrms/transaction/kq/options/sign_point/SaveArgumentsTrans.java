package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveArgumentsTrans extends IBusiness {

	public void execute() throws GeneralException {
       
		String pointRadius = (String)this.getFormHM().get("pointRadius");
		
        try{
			
			String sql = "delete constant where constant='KQ_POINT_RADIUS'";
			
			ArrayList sqllist = new ArrayList();
			sqllist.add(sql);
			sql = "insert into constant(constant,describe,str_value) values('KQ_POINT_RADIUS','单位（米）','"+pointRadius+"')";
			sqllist.add(sql);
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.batchUpdate(sqllist);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
