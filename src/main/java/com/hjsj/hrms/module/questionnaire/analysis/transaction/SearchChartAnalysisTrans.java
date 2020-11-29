package com.hjsj.hrms.module.questionnaire.analysis.transaction;

import com.hjsj.hrms.module.questionnaire.analysis.businessobject.AnalysisBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title: SearchChartAnalysisTrans </p>
 * <p>Description: 查询图表分析数据</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-14 上午10:28:04</p>
 * @author jingq
 * @version 1.0
 */
public class SearchChartAnalysisTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String qnid = (String) this.getFormHM().get("qnid");
		String planid = (String) this.getFormHM().get("planid");
		String subobject = (String) this.getFormHM().get("subobject");
		AnalysisBo bo = new AnalysisBo();
		String data = bo.getChartAnalysisData(qnid, planid, subobject);
		this.getFormHM().put("results", data);
	}

}
