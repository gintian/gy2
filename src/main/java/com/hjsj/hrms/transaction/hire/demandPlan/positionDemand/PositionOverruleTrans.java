package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:PositionOverruleTrans.java</p>
 * <p>Description:驳回</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 25, 2006 1:25:47 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class PositionOverruleTrans extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer sql_whl=new StringBuffer("");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String[]  z0301s=((String)hm.get("z0301")).split("/");
		for(int i=0;i<z0301s.length;i++)
			sql_whl.append(",'"+z0301s[i]+"'");
		String rejectCause=(String)this.getFormHM().get("rejectCause");
		String url_p=(String)hm.get("url_p");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String moreLevelSP=(String)this.getFormHM().get("moreLevelSP");
		String isSendMessage=(String)this.getFormHM().get("isSendMessage");
		try
		{
			String sql="update z03 set z0319='07',z0327=?  where z0301 in ("+sql_whl.substring(1)+")";
			/*PreparedStatement pt=this.getFrameconn().prepareStatement(sql);	
			pt.setString(1,rejectCause);
			pt.execute();*/
			ArrayList values = new ArrayList();
			values.add(rejectCause);
			dao.update(sql, values);
		
				PositionDemand pd = new PositionDemand(this.getFrameconn());
				for(int i=0;i<z0301s.length;i++)
				{
					if(z0301s[i]==null|| "".equals(z0301s[i]))
						continue;
					String xml=pd.createXML(this.getUserView(), rejectCause, z0301s[i], "07");
					pd.saveXML(z0301s[i], xml);
					if("1".equals(moreLevelSP))
					{
						if(isSendMessage==null||(isSendMessage!=null&& "0".equalsIgnoreCase(isSendMessage)))
							pd.rejectByLayer(z0301s[i], this.getUserView(), url_p);
						if(isSendMessage!=null&& "1".equalsIgnoreCase(isSendMessage)){
							pd.rejectByMessage(z0301s[i], this.getUserView());
						}
					}
				}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		


	}

}
