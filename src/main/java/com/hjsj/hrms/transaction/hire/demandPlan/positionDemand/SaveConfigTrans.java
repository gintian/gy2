package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveConfigTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String param=(String)this.getFormHM().get("param");
			PositionDemand pd = new PositionDemand(this.getFrameconn(),this.getUserView());
			pd.saveOrDelParam(param);
			/*ArrayList demandFieldList=pd.getZ03Field(param);
			ArrayList postFieldItemList=pd.getKItemList(fieldsetid,"",param);
			String tableStr=pd.getDemand_post(param);
			this.getFormHM().put("demandFieldList", demandFieldList);
			this.getFormHM().put("postFieldItemList", postFieldItemList);
			this.getFormHM().put("table",SafeCode.encode(tableStr));*/
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
