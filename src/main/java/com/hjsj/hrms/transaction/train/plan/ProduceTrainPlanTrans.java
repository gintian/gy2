package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class ProduceTrainPlanTrans extends IBusiness  {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String selectIDs=(String)hm.get("selectIDs");
		
		ArrayList list=DataDictionary.getFieldList("r25",Constant.USED_FIELD_SET);
		TrainPlanBo trainPlanBo=new TrainPlanBo(this.getFrameconn());
		ArrayList planFieldList=trainPlanBo.getPlanFieldList(list,this.getUserView());
		
		this.getFormHM().put("planFieldList",planFieldList);
		this.getFormHM().put("selectIDs",selectIDs);
	}
	
	
}
