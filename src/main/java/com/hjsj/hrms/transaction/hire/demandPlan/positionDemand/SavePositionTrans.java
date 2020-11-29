package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePositionTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String z0301=(String)hm.get("z0301");
			String opt=(String)hm.get("opt");   //  0:添加  1：修改
		
			if("1".equalsIgnoreCase(opt)&&z0301!=null&&z0301.length()>0){
			    z0301 = PubFunc.decrypt(z0301);
			}
			
			ArrayList positionDemandDescList=(ArrayList)this.getFormHM().get("positionDemandDescList");
			String    isRevert=(String)this.getFormHM().get("isRevert");
			String    mailTemplateID=(String)this.getFormHM().get("mailTemplateID");
			ArrayList posConditionList=(ArrayList)this.getFormHM().get("posConditionList");
			
			PositionDemand bo=new PositionDemand(this.getFrameconn());
			String az0301=bo.addPositionDemand(opt,positionDemandDescList,isRevert,mailTemplateID,z0301);
			
			ArrayList list=bo.getParamConditionList(posConditionList);
			DemandCtrlParamXmlBo xmlBo=new DemandCtrlParamXmlBo(this.getFrameconn(),az0301);
			HashMap map=new HashMap();
			map.put("simple",list);
			xmlBo.updateNode("simple",map,az0301);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
