package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetPointListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String pointsetid=(String)this.getFormHM().get("pointsetid");
			this.frowset=dao.search("select * from per_point where pointkind=1 and  status=1 and (  pointtype=0 or pointtype is null ) and validflag=1 and pointsetid="+pointsetid);
			ArrayList list=new ArrayList();
			while(this.frowset.next())
			{
				String Pointctrl=Sql_switcher.readMemo(this.frowset,"Pointctrl");
				HashMap map=PointCtrlXmlBo.getAttributeValues(Pointctrl);
				String point_id=this.frowset.getString("point_id");
				String computeRule=(String)map.get("computeRule");
				if(computeRule==null|| "0".equals(computeRule))
					continue;
				if(!(this.userView.isSuper_admin()))
				{
					if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id))
					{
						continue;
					}
				}
				list.add(new CommonData(point_id,this.frowset.getString("pointname")));
			}
			this.getFormHM().put("fieldlist",list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
