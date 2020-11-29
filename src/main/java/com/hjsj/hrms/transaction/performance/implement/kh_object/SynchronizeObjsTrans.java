package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:UpdateKhMethodTrans.java</p>
 * <p>Description:考核实施同步对象顺序</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-01-18 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SynchronizeObjsTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String planid=(String)this.getFormHM().get("planid");
		PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
		
		StringBuffer strSql = new StringBuffer();
		RecordVo vo=pb.getPerPlanVo(planid);
		if(vo.getInt("object_type")==2)
		{
			strSql.append("update per_object ");
			strSql.append(" set a0000=");
			strSql.append("(select usra01.a0000 from usra01 where ");
			strSql.append("  per_object.object_id=usra01.a0100 and per_object.plan_id="+planid);
			strSql.append(")");
		}else
		{
			strSql.append("update per_object ");
			strSql.append(" set a0000=");
			strSql.append("(select organization.a0000 from organization where ");
			strSql.append("  per_object.object_id=organization.codeitemid and per_object.plan_id="+planid);
			strSql.append(")");
		}
		
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
