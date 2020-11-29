package com.hjsj.hrms.module.questionnaire.analysis.transaction;

import com.hjsj.hrms.module.questionnaire.analysis.businessobject.AnalysisBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title: CreateDataAnalysisTrans </p>
 * <p>Description: 创建生成原始数据分析页面的js</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-10 上午10:08:12</p>
 * @author jingq
 * @version 1.0
 */
public class CreateDataAnalysisTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String qnid = (String) this.getFormHM().get("qnid");
		String planid = (String) this.getFormHM().get("planid");
		String subobject = (String) this.getFormHM().get("subobject");
		AnalysisBo bo = new AnalysisBo(this.frameconn);
		try {
			String str = bo.getDataAnalysis(qnid, planid, subobject, this.userView);
			this.getFormHM().put("returnstr", str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
