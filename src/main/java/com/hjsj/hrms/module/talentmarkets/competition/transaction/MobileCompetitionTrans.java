package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionJobsService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionJobsServiceImpl;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @Description 移动端内部竞聘首页面交易类
 * @Author wangb
 * @Date 2019/08/12 11:17
 * @Version V1.0
 **/
public class MobileCompetitionTrans extends IBusiness{

	private enum TransType{
		/**首次加载*/
		mobile_main,
		/**我的竞聘*/
		myapplication,
		/**竞聘岗位*/
		competitive_jobs,
		/**历史记录*/
		history_application
	}
	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		Map returnDataHM = new HashMap();
		Map returnStrHM = new HashMap();
		String transType = (String) this.formHM.get("type");
		String competition_type = transType;
		String state ="executing"; //进行中
		List list = new ArrayList();
		String orgId = (String) this.formHM.get("orgId");
		String jobName = (String) this.formHM.get("jobName");
		CompetitionService competitionService =  new CompetitionServiceImpl(this.userView, this.frameconn);
		CompetitionJobsService competitionJobsService = new CompetitionJobsServiceImpl(this.frameconn, this.userView);
		try {
			if(transType.equals(TransType.mobile_main.toString())){
				boolean flag = competitionService.checkMobileMyCompetitionPost(this.userView.getGuidkey());
				if(flag){
					competition_type = "myapplication";
					state = "executing";
					list = competitionService.listMobileMyCompetitionPost(state,this.userView.getGuidkey());
				}else{
					competition_type = "competitive_jobs";
					int pageIndex = (Integer) this.formHM.get("pageIndex");
					int pageSize = (Integer) this.formHM.get("pageSize");
					returnDataHM.put("total", competitionJobsService.getReleaseCompetitionJobsTotal(orgId, jobName));
					list = competitionJobsService.listReleaseCompetitionJobs(orgId, jobName, pageIndex, pageSize);
				}
			}else if(transType.equals(TransType.myapplication.toString())){
				state = "executing";
				list = competitionService.listMobileMyCompetitionPost(state,this.userView.getGuidkey());
			}else if(transType.equals(TransType.competitive_jobs.toString())){
				competition_type = "competitive_jobs";
				int pageIndex = (Integer) this.formHM.get("pageIndex");
				int pageSize = (Integer) this.formHM.get("pageSize");
				returnDataHM.put("total", competitionJobsService.getReleaseCompetitionJobsTotal(orgId, jobName));
				list = competitionJobsService.listReleaseCompetitionJobs(orgId, jobName, pageIndex, pageSize);
			}else if(transType.equals(TransType.history_application.toString())){
				state = "end";
				list = competitionService.listMobileMyCompetitionPost(state,this.userView.getGuidkey());
			}
		}catch(GeneralException e){
			returnStrHM.put("return_code", "fail");
			returnStrHM.put("return_msg", e.getErrorDescription());
			this.formHM.put("returnStr", returnStrHM);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			returnStrHM.put("return_code", "fail");
			returnStrHM.put("return_msg", e.getMessage());
			this.formHM.put("returnStr", returnStrHM);
			return;
		}
		returnDataHM.put("competition_type", competition_type);
		returnDataHM.put("postList", list);
		
		returnStrHM.put("return_code", "success");
		returnStrHM.put("return_msg", "");
		returnStrHM.put("return_data", returnDataHM);
		
		this.formHM.put("returnStr", returnStrHM);
	}

}
