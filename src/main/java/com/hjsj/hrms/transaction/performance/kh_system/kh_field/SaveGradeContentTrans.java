package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveGradeContentTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String gradeid=(String)this.getFormHM().get("gradeid");
			String gradeContent=(String)this.getFormHM().get("gradeContent");
			RecordVo vo = new RecordVo("per_grade");
			vo.setInt("grade_id", Integer.parseInt(gradeid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo = dao.findByPrimaryKey(vo);
			vo.setString("gradedesc", gradeContent);
			dao.updateValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
