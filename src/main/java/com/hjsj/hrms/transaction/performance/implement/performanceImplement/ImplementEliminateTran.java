package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * JinChunhai:
 * </p>
 * <p>
 * Description:考核实施/清除主体分数
 * </p>
 */

public class ImplementEliminateTran extends IBusiness
{

	public void execute() throws GeneralException
	{
		ArrayList list = null;
		String planid = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			planid = (String) hm.get("planid");

			if (planid != null)
			{
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
				String whl = pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
				
				list = new ArrayList();
				StringBuffer sql = new StringBuffer();
				sql.append("select distinct mainbody_id,a0101 from per_mainbody where plan_id=" + planid);
				sql.append(" and object_id in (");
				sql.append("select object_id from per_object where plan_id="+planid);
				sql.append(whl);
				sql.append(")");				
				
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next())
				{
					CommonData vo = new CommonData(this.frowset.getString("mainbody_id"), this.frowset.getString("a0101"));
					list.add(vo);
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if (planid != null)
		{
			this.getFormHM().put("planidselect", list);
		}
	}
}