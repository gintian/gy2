package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchGradeContentTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String gradeid=(String)map.get("gradeid");
			RecordVo vo = new RecordVo("per_grade");
			vo.setInt("grade_id", Integer.parseInt(gradeid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo = dao.findByPrimaryKey(vo);
			this.getFormHM().put("gradeid", gradeid);
			this.getFormHM().put("gradeContent", vo.getString("gradedesc"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
