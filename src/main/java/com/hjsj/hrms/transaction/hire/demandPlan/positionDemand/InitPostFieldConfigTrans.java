package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitPostFieldConfigTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			PositionDemand pd = new PositionDemand(this.getFrameconn(),this.getUserView());
			ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn(),"1");
			String param=bo.getParam();
			String tableStr=pd.getDemand_post(param);
			ArrayList demandFieldList=pd.getZ03Field(param);
			ArrayList postFieldSetList=pd.getKSetList();
			ArrayList postFieldItemList=pd.getKItemList("","",param);
			this.getFormHM().put("demandFieldList", demandFieldList);
			this.getFormHM().put("postFieldSetList", postFieldSetList);
			this.getFormHM().put("postFieldItemList", postFieldItemList);
			this.getFormHM().put("demandFieldId", "");
			this.getFormHM().put("postFieldSetId", "");
			this.getFormHM().put("postFieldItemId", "");
			this.getFormHM().put("tableStr", tableStr);
			this.getFormHM().put("param", param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
