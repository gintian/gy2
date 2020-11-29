package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitKhFIeldFrameTrans.java</p>
 * <p>Description:展现考核指标及标度内容</p>
 * <p>Company:HJHJ</p>
 * <p>create time:2008-11-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class InitKhFIeldFrameTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String pointsetid=(String)map.get("pointsetid");
			String subsys_id = (String)map.get("subsys_id");
			
			String point_id = "-1";
			ArrayList fieldinfolist=null;
			if("root".equalsIgnoreCase(pointsetid)|| "-1".equals(pointsetid))
			{
				 fieldinfolist = new ArrayList();
				 pointsetid="-1";
			}
			else
			{
				KhFieldBo bo = new KhFieldBo(this.getFrameconn());
	    	    fieldinfolist = bo.getKhFieldInfo(pointsetid,this.userView);
	    		
	    		if(fieldinfolist.size()>0)
	    		{
		    		point_id=(String)(((LazyDynaBean)fieldinfolist.get(0)).get("point_id"));
		    	}
			}
			String pointid=(String)map.get("pointid");
			map.remove("pointid");
			if(pointid!=null && pointid.trim().length()>0)
				point_id = pointid;			
			
			this.getFormHM().put("fieldinfolist",fieldinfolist);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("point_id", point_id);
			this.getFormHM().put("subsys_id",subsys_id);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
