package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class SubInterviewTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		try
		{
			String ins_id=(String)this.getFormHM().get("ins_id");
			String plan_id=(String)this.getFormHM().get("plan_id");
			plan_id = PubFunc.decrypt(plan_id);
			String object_id=(String)this.getFormHM().get("objectid");
			object_id = PubFunc.decrypt(object_id);
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		    String newid = idg.getId("per_interview.id");
		    RecordVo vo = new RecordVo("per_interview");
		    vo.setString("object_id",object_id);
		    vo.setInt("id", Integer.parseInt(newid));
		    vo.setDate("create_date", new Date());
		    vo.setString("mainbody_id",this.userView.getA0100());
		    vo.setInt("plan_id",Integer.parseInt(plan_id));
		    vo.setInt("status",1);
		    vo.setInt("ins_id",Integer.parseInt(ins_id));
		    dao.addValueObject(vo);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
