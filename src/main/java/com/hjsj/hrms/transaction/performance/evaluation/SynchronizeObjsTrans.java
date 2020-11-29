package com.hjsj.hrms.transaction.performance.evaluation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:UpdateKhMethodTrans.java</p>
 * <p>Description:绩效评估同步对象顺序</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-05-25 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 */
public class SynchronizeObjsTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String planid=(String)this.getFormHM().get("planid");
		
		StringBuffer strSql = new StringBuffer();
		strSql.append("update per_result_"+planid);
		strSql.append(" set a0000 = ");
		strSql.append("(select per_object.a0000 from per_object where plan_id="+planid);
		strSql.append(" and per_object.object_id=");
		strSql.append("per_result_"+planid+".object_id)");
		this.getFormHM().put("order_str", "");//同步对象顺序时，清空排序字符串    pjf 2014.01.06
		
		try
		{
		    dao.update(strSql.toString());
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}
}
