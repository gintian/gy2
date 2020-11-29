package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:SaveRecordTrans
 * </p>
 * <p>
 * Description:保存的记录
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-5-12:下午03:48:28
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class SaveRecordTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = this.getFormHM();
	ArrayList list = (ArrayList) hm.get("data_table_record");	

	try
	{
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    dao.updateValueObject(list);

	} catch (Exception ex)
	{
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }
}
