package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InitComplexConditionCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String unitcode=(String)hm.get("unitcode");
		String sortid_str=(String)hm.get("sortid_str");
		HashSet sortidSet=new HashSet();
		String[] a_sort=sortid_str.split(",");
		//得到指定的表类id串
		for(int i=0;i<a_sort.length;i++)
			sortidSet.add(a_sort[i]);
		
		
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		//得到与填报信息单位相关联的所有表类涉及到的代码型全局参数和指定的表类涉及到的表类参数
		ArrayList commonsParam=reportCollectBo.getCommonsParam(unitcode,sortidSet);
		this.getFormHM().put("commonsParam",commonsParam);
		ArrayList   rightFieldsList=new ArrayList();
		this.getFormHM().put("rightFieldsList",rightFieldsList);
	}

}
