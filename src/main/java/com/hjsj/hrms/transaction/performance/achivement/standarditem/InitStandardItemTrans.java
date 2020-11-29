package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitStandardItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			
			String point_id=(String)map.get("point_id");
			String type=(String)map.get("type");
			StandardItemBo bo = new StandardItemBo(this.getFrameconn());
			String html = bo.getStandardItemHTML(point_id);
			this.getFormHM().put("point_id", point_id);
			this.getFormHM().put("tableHtml", html);
			this.getFormHM().put("isHaveItem",bo.getIsHaveItem());
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
        }		
	}

}
