package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * <p>Title:AppointPlanTrans.java</p>
 * <p>Description:为职位需求指定计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 27, 2006 3:24:17 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class AppointPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		String[] selectIDs=(String[])this.getFormHM().get("selectIDs");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		/**安全平台管理,z0101被加密,现在解密回来**/
		String z0101=PubFunc.decrypt((String)hm.get("z0101"));
		hm.remove("z0101");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.update("update z03 set z0101='',z0317='0' where z0301 in (select z0301 from z03 where z0101='"+z0101+"')");
			StringBuffer sql_whl=new StringBuffer("");
			for(int i=0;i<selectIDs.length;i++)
			{
				sql_whl.append(",'"+selectIDs[i]+"'");
			}
			dao.update("update z03 set z0101='"+z0101+"',z0317='1' where z0301 in("+sql_whl.substring(1)+")");			
			this.frowset=dao.search("select sum(z0315) from  z03 where z0301 in("+sql_whl.substring(1)+")");
			if(this.frowset.next())
			{
				dao.update("update z01 set z0115="+this.frowset.getString(1)+" where z0101='"+z0101+"'");
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}
