package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataFormulaBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class DefinePointsRangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		hm.remove("b_saverange");
		String planid=(String)hm.get("planid");
		DataFormulaBo db0=new DataFormulaBo(this.frameconn,this.userView,planid);
		String isvalidate=(String)hm.get("isvalidate");
		ArrayList pointlist=new ArrayList();
		HashMap hasDefine=new HashMap();
		pointlist=db0.getPointList();
		PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
		Hashtable planParamSet=pe.getPlanParamSet();
		ArrayList scoreRangeList=(ArrayList)planParamSet.get("scoreRangeList");
		hasDefine=db0.hasDefine(scoreRangeList);
		pointlist=db0.getlist(planid,hasDefine);
		ArrayList show=new ArrayList();
		if(pointlist!=null&&pointlist.size()>0){
			show=(ArrayList)pointlist.get(0);
		}
		
		this.getFormHM().put("rangelist", show);
		this.getFormHM().put("isvalidate", isvalidate);
	}
}
