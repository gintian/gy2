package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionJobsServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;
/**
 * @Description 竞聘岗位详情交易类
 * @Author wangb
 * @Date 2019/08/12 11:17
 * @Version V1.0
 **/
public class SearchCompetitionPostInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		Map returnStr = new HashMap();
		try {
			String id = (String) this.formHM.get("id");
			CompetitionJobsService competitionJobsService = new CompetitionJobsServiceImpl(this.frameconn, this.userView);
			returnStr.put("return_data", competitionJobsService.getCompetitionJobDetailData(id));
			returnStr.put("return_code", "success");
		} catch (GeneralException e) {
			e.printStackTrace();
			returnStr.put("return_code", "fail");
			returnStr.put("return_msg", e.getErrorDescription());
		} catch(Exception e){
			e.printStackTrace();
			returnStr.put("return_code", "fail");
			returnStr.put("return_msg", e.getMessage());
		}
		this.formHM.put("returnStr", returnStr);
		
		
	}

}
