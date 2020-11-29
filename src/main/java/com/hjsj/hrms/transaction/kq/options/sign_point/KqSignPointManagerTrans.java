package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class KqSignPointManagerTrans extends IBusiness {

	public void execute() throws GeneralException {
          try{
			  String pid = (String)this.getFormHM().get("pid");
			  String changeFlag = (String)this.getFormHM().get("changeFlag");
			  ContentDAO dao = new ContentDAO(this.frameconn);
			  if("del".equals(changeFlag)){
				  String sql = "delete kq_sign_point where pid="+pid.substring(1);
				  dao.delete(sql, new ArrayList());
				  sql = " delete kq_sign_point_emp where pid="+pid.substring(1);
				  dao.delete(sql, new ArrayList());
				  //删除考勤点时同时删除考勤点下的机构   jingq add 2014.6.19
				  sql = "delete kq_sign_point_org where pid="+pid.substring(1);
				  dao.delete(sql, new ArrayList());
				  this.getFormHM().put("changeRs", "1");
				  
			  }else if("edit".equals(changeFlag)){
				  String point_name = (String)this.getFormHM().get("point_name");
				  String sql = " update kq_sign_point set name='"+point_name+"' where pid="+pid.substring(1);
				  dao.update(sql);
				  this.getFormHM().put("changeRs", "1");
			  }
          }catch(Exception e){
        	  e.printStackTrace();
        	  this.getFormHM().put("changeRs", "0");
          }
	}

}
