package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionApplyService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionApplyServiceImpl;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.CompetitionServiceImpl;
import com.hjsj.hrms.transaction.mobileapp.template.MobileTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompetitionApplyTrans extends IBusiness {

	private enum TransType{
		/**首次加载*/
		main,
		/**我的竞聘*/
		myapplication,
		/**竞聘岗位*/
		competitive_jobs,
		/**历史记录*/
		history_application,
		/**查看单子*/
		search_template,
		/**撤回&撤销*/
		retract
	}
	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		Map returnDataHM = new HashMap();
		Map returnStrHM = new HashMap();
		try {
			String transType = (String) this.formHM.get("type");
			String orgId = (String) this.formHM.get("orgId");
			String competition_type = transType;
			String state ="executing"; //进行中
			List list = new ArrayList();
			String tableConfig="";
			CompetitionService competitionService =  new CompetitionServiceImpl(this.userView, this.frameconn);
			CompetitionApplyService competitionApplyService = new CompetitionApplyServiceImpl(this.userView, this.frameconn);
			if(transType.equals(TransType.main.toString())){
				boolean flag = competitionService.checkMobileMyCompetitionPost(this.userView.getGuidkey());
				if(flag){
					competition_type = "myapplication";
					state = "executing";
					tableConfig = competitionApplyService.getTableConfig(state, null);
				}else{
//					String orgId = (String) this.formHM.get("orgId");
					competition_type = "competitive_jobs";
					tableConfig = competitionApplyService.getTableConfig(competition_type, orgId);
				}
			}else if(transType.equals(TransType.myapplication.toString())){
				state = "executing";
				tableConfig = competitionApplyService.getTableConfig(state, null);
			}else if(transType.equals(TransType.competitive_jobs.toString())){
				competition_type = "competitive_jobs";
				tableConfig = competitionApplyService.getTableConfig(competition_type, orgId);
			}else if(transType.equals(TransType.history_application.toString())){
				state = "end";
				tableConfig = competitionApplyService.getTableConfig(state, null);
			}
			
			if(transType.equalsIgnoreCase(TransType.search_template.toString())){//查看单子
				state = (String) this.formHM.get("state_type");
				String z8101 = (String) this.formHM.get("z8101");
				Map templateDataHM = competitionApplyService.getCompetitionApplyTemplateData(state,z8101);
				returnDataHM.put("templateData", templateDataHM);
			}else if(transType.equalsIgnoreCase(TransType.retract.toString())){//撤回&撤销单子
				String param = (String) this.formHM.get("param");
				MobileTemplateBo bo = new MobileTemplateBo(this.frameconn,this.userView);
				String jsonstr = bo.recallOrDeleteTask(param);
				returnDataHM.put("jsonstr", jsonstr.toString());
			}else{
				int competitiveJobsNum = competitionApplyService.getCompetitionApplyPostNum("competitive_jobs", orgId);
				int executingJobsNum = competitionApplyService.getCompetitionApplyPostNum("executing", orgId);
				returnDataHM.put("competitiveJobsNum", competitiveJobsNum); //竞聘岗位数
				returnDataHM.put("executingJobsNum", executingJobsNum); //我的竞聘 岗位数
				Map paramHM = competitionApplyService.getCompetitionApplyOtherParam();
				returnDataHM.put("params", paramHM);
				returnDataHM.put("competition_type", competition_type);
				returnDataHM.put("gridconfig", tableConfig);
			}
			returnDataHM.put("applyFlag", userView.getA0100().length()>0? true:false);//
			returnStrHM.put("return_code", "success");
		}catch(GeneralException e){
			returnStrHM.put("return_code","fail");
			returnStrHM.put("return_msg", e.getErrorDescription());
		} catch (Exception e) {
			String message =e.getMessage();
			returnStrHM.put("return_msg", message);
			e.printStackTrace();
		}
		returnStrHM.put("return_data", returnDataHM);
		this.formHM.put("returnStr", returnStrHM);
	}

}
