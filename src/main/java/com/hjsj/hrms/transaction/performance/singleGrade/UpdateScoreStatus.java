package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpdateScoreStatus extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String plan_id = (String)this.getFormHM().get("plan_id");
			String object_id = (String)this.getFormHM().get("object_id");
			String mainbody_id = this.userView.getA0100();
			StringBuffer sqlStr = new StringBuffer("");
			sqlStr.append("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'"+" and status=2");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sqlStr.toString());
			this.getFormHM().put("flag", "1");
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
