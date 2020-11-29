package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:UpdateBigFldTrans.java</p>
 * <p>Description:更新大字段</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-09 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class UpdateBigFldTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planID = (String) hm.get("planID");
		String fieldName = (String) hm.get("fieldName");
	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	
		String bigField = (String) this.getFormHM().get("paramStr");
		if (bigField == null)
		    bigField = "";

/*		
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", planID);
		try
		{
		    vo = dao.findByPrimaryKey(vo);
		    vo.setString(fieldName, bigField);
		    dao.updateValueObject(vo);
		} catch (SQLException e)
		{
		    throw GeneralExceptionHandler.Handle(e);
		}
*/
		
		StringBuffer strsql = new StringBuffer();
		strsql.append("update per_plan set ");
		strsql.append(fieldName);
		strsql.append("='");
		strsql.append(bigField);
		strsql.append("' where plan_id=");
		strsql.append(planID);
			
		try
		{
			dao.update(strsql.toString());
			
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
}
