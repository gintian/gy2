package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchUnitPosTrans extends IBusiness {

	public void execute() throws GeneralException {

		EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
		String hireChannel=(String)this.getFormHM().get("hireChannel");
		hireChannel=PubFunc.getReplaceStr(hireChannel);
		ArrayList conditionFieldList=employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_query");
		ArrayList unitList=new ArrayList();
		HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap(conditionFieldList,unitList,hireChannel);
		this.getFormHM().put("unitPosMap",unitPosMap);
		this.getFormHM().put("conditionFieldList",conditionFieldList);

	}

}
