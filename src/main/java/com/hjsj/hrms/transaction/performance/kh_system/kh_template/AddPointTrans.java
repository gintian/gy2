package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddPointTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String templateID = (String)this.getFormHM().get("id");
			String pointid = (String)this.getFormHM().get("point");
			String pointids = (String)this.getFormHM().get("pointids");
			String itemid=(String)this.getFormHM().get("itemid");
			String beforetype=(String)this.getFormHM().get("beforetype");
			/**=1增加=2插入*/
			String type=(String)this.getFormHM().get("type");
			String subsys_id=(String)this.getFormHM().get("subsys_id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			if("-1".equals(itemid)/*type.equals("1")&&beforetype.equals("2")*/)
				itemid=bo.getItemidByPointid(pointid, templateID);
			bo.addPoint(itemid, templateID, pointids, Integer.parseInt(type), pointid);
			/**为项目增加指标后，将该项目设置为共性项目*/
			bo.setKind(itemid, "1");
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("templateid",templateID);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
