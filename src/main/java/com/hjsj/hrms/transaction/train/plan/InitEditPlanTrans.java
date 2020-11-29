package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitEditPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String planid=(String)hm.get("planid");
		planid = PubFunc.decrypt(SafeCode.decode(planid));
		
		ArrayList list=DataDictionary.getFieldList("r25",Constant.USED_FIELD_SET);
		TrainPlanBo trainPlanBo=new TrainPlanBo(this.getFrameconn());
		ArrayList planFieldList=trainPlanBo.getPlanFieldList(list,this.getUserView(),planid);
		
		String temp = "";
		TrainCourseBo tb = new TrainCourseBo(this.userView);
		temp = tb.getUnitIdByBusi();
		this.getFormHM().put("orgparentcode", temp);
		
		this.getFormHM().put("planFieldList",planFieldList);
		

	}

}
