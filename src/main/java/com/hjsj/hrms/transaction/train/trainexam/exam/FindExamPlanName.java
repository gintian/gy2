package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class FindExamPlanName extends IBusiness {

	
	public void execute() throws GeneralException {
		try{
			String planName = (String) this.getFormHM().get("planName");
			String r5400 = (String) this.getFormHM().get("r5400");
			r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
			String isexist = "no";
			String sql="select r5400,r5401 from r54 where r5401='"+planName+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs=dao.search(sql);
			while(rs.next()){
			    String planid = rs.getString("r5400");
			    if(!planid.equalsIgnoreCase(r5400))
			        isexist = "yes";
			}
			
			this.getFormHM().put("isexist", isexist);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
