package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.CheckBodyObjectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SortCheckBodyObjectTrans.java</p>
 * <p>Description:主体类别排序</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SortCheckBodyObjectTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String busitype = (String)reqhm.get("busitype");	// 业务分类 =0(绩效考核); =1(能力素质)					
		reqhm.remove("busitype");
		this.getFormHM().put("busitype", busitype);
		
		String noself = (String)reqhm.get("noself");
		hm.remove("noself");
		noself=noself==null?"0":noself;		
		this.getFormHM().put("noself", noself);
				
		String bodyType = (String)reqhm.get("bodyType");
		bodyType=bodyType!=null&&bodyType.trim().length()>0?bodyType:"";
		reqhm.remove("bodyType");
		
		if(bodyType.length()>0)
		{
			CheckBodyObjectBo bo = new CheckBodyObjectBo(this.frameconn);
			hm.put("sortlist",bo.sortList(this.frameconn,bodyType,noself,busitype));
		}
		
		hm.put("bodyType",bodyType);
	}

}
