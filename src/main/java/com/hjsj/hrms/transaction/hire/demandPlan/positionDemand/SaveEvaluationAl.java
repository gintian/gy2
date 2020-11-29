package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveEvaluationAl  extends IBusiness  {

	public void execute() throws GeneralException {
		String testid=(String)this.getFormHM().get("testid");
		testid = PubFunc.keyWord_reback(testid);
		String z0301=(String)this.getFormHM().get("z0301");
		String valid=(String)this.getFormHM().get("valid");	
		ContentDAO dao=new ContentDAO(this.getFrameconn());  
		ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		boolean flag=parameterSetBo.createEvaluatingTable(testid);
		DemandCtrlParamXmlBo DemandCtrlParamXmlBo = new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
		DemandCtrlParamXmlBo.setAttributeValue("/content/template","type",valid);
		DemandCtrlParamXmlBo.setAttributeValue("/content/template","id",testid);
		DemandCtrlParamXmlBo.saveStrValue();

	}

}
