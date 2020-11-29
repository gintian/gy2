package com.hjsj.hrms.transaction.hire.employActualize.employPosition;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitPositionConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**招聘岗位中,岗位中的一系列连接z0301都已经加密,解密回来**/
			String z0301=PubFunc.decrypt((String)hm.get("z0301"));
			PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
			//			简历筛选条件列表
			ArrayList conditionList=positionDemand.getResetPosConditionList("0",z0301);
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
			ArrayList list= bo.getComplexTemplateList();
			this.getFormHM().put("complexTemplateList",list);
			this.getFormHM().put("posConditionList",conditionList);
			this.getFormHM().put("z0301",z0301);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
