package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * 将打分方式标记 设置到 userView.
 * @author Owner
 *
 */
public class SetGradeFashinTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.getFrameconn());
		Hashtable table=bo.analyseParameterXml();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测
		this.getFormHM().put("model",model);
		String flag="1";
		if(table.get("MarkingMode")!=null)
			flag=(String)table.get("MarkingMode");   //1:下拉框方式  2：平铺方式		
		this.getUserView().getHm().put("gradeFashion",flag);
		
	}

}
