package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchParamTrans.java</p>
 * <p>Description:list页面编辑考核计划参数初始化</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchParamTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String planId = (String) hm.get("plan_id");
			String method = (String) hm.get("method");
			String object_type = (String) hm.get("object_type");
			String templateId = (String) hm.get("templateId");
	
			// paramOper = list: 列表页面编辑参数 detail:详细信息页面编辑参数
			String paramOper = (String) hm.get("paramOper");
			hm.remove("paramOper");
			this.getFormHM().put("paramOper", paramOper);
	
			ExamPlanBo bo = new ExamPlanBo(planId,this.frameconn);
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
	        if(!_bo.isHavePriv(this.userView, planId)){	
	        	return;
	        }
	/*        if(!_bo.isTempAndPlan(templateId, planId)){
	        	throw GeneralExceptionHandler.Handle(new GeneralException("该计划与所关联的模板不匹配！"));
	        }
	        */
			LoadXml loadxml = new LoadXml(this.getFrameconn(), planId);
			if ("list".equalsIgnoreCase(paramOper))
			{
				Hashtable params = loadxml.getDegreeWhole();
				//--------------首钢加“按条件引入岗位职责指标” zhaoxg add 2017-2-22-------
				String dutyRule = (String) params.get("DutyRule");
				if(dutyRule!=null&&dutyRule.length()>0){
					this.getFormHM().put("dutyRuleid", "True");
				}else{
					this.getFormHM().put("dutyRuleid", "False");
				}
				this.getFormHM().put("dutyRule", dutyRule);
				ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
				String setid = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "SubSet");
				if(setid!=null&&setid.length()>0){
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset!=null){
						this.getFormHM().put("setid", setid);
						this.getFormHM().put("setdesc", fieldset.getFieldsetdesc());
					}
				}else{
					this.getFormHM().put("setid", "");
					this.getFormHM().put("setdesc", "");
				}
				//---------------end---------------------------------
				// 初始化计划的主体类别
				String bodyTypeIds = bo.getBodyTypeIds(planId);
				this.getFormHM().put("bodyTypeIds", bodyTypeIds);
				//按岗位素质模型
				String byModel = bo.getByModelById(planId);
				this.getFormHM().put("byModel", byModel);
				this.getFormHM().put("templateId", templateId);
				/*
				 * 考核主体类别
				 */
				this.getFormHM().put("bodysFromCard", params.get("BodysFromCard"));
				ArrayList setlist = bo.searchCheckBody2(planId, object_type, "");
				this.getFormHM().put("MainbodyTypeList", setlist);
				// 标准标度
				String busitype = (String) this.getFormHM().get("busitype");
				String per_comTable = "per_grade_template"; // 绩效标准标度
				if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
					per_comTable = "per_grade_competence"; // 能力素质标准标度
				ArrayList grade_template = bo.getGradeTemplate(per_comTable);
				this.getFormHM().put("grade_template", grade_template);
				String templateType = bo.getTemplateType(templateId);
				this.getFormHM().put("templateType", templateType);
				
				this.getFormHM().put("mainbodybodyid", params.get("MainbodyGradeCtl"));
				this.getFormHM().put("allmainbodybody", params.get("AllMainbodyGradeCtl"));
				this.getFormHM().put("wholeEvalMode", params.get("WholeEvalMode"));//总体评价录分方式0：录入等级1：录入分值
				this.getFormHM().put("batchScoreImportFormula", params.get("BatchScoreImportFormula"));//多人评分引入总分计算公式 pjf 2014.01.03
				/*
				 * 打分控制
				 */
				// 部分指标分别设置的情况
				String parameter_content = bo.getParameter_content(planId);
				ArrayList badly_partRestrict = new ArrayList();
				ArrayList fine_partRestrict = new ArrayList();
				try
				{
					if (bo.isExists(parameter_content, "BadlyMax"))
						badly_partRestrict = bo.getRestrictList(parameter_content, "BadlyMax",templateId);
					else
						badly_partRestrict = bo.notExists(templateId);
	
					if (bo.isExists(parameter_content, "FineMax"))
						fine_partRestrict = bo.getRestrictList(parameter_content, "FineMax",templateId);
					else
						fine_partRestrict = bo.notExists(templateId);
	
				} catch (Exception e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
				this.getFormHM().put("Badly_partRestrict", badly_partRestrict);
				this.getFormHM().put("Fine_partRestrict", fine_partRestrict);
	
				// 数据采集录入方式(1-标度 2-混合 3-打分按加扣分处理)
				this.getFormHM().put("dataGatherMode", params.get("scoreflag"));
				this.getFormHM().put("addSubtractType", params.get("addSubtractType"));
				// 分值转标度规则(1-就高 2-就低）
				this.getFormHM().put("scaleToDegreeRule", params.get("limitrule"));
				// 标度显示形式(1-标准标度内容 2-指标标度内容）
				this.getFormHM().put("degreeShowType", params.get("degreeShowType"));
				// 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
				this.getFormHM().put("sameAllScoreNumLess", params.get("SameAllScoreNumLess"));
				// 是否限制 指标得分为A(优秀)的数目和总体评价最高等级数目（true|false）
				this.getFormHM().put("fineRestrict", params.get("FineRestrict"));
				// 限制 指标得分为A(优秀)的数目和总体评价最高等级数目
				this.getFormHM().put("fineMax", params.get("fineMax"));
				this.getFormHM().put("badlyRestrict", params.get("BadlyRestrict"));
				this.getFormHM().put("badlyMax", params.get("BadlyMax"));
				// 考核对象指标结果全部相同时的选项 1: 可以保存, 2: 不能保存
				this.getFormHM().put("sameResultsOption", params.get("SameResultsOption"));
				this.getFormHM().put("noCanSaveDegrees", params.get("NoCanSaveDegrees"));
				// 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
				this.getFormHM().put("blankScoreOption", params.get("BlankScoreOption"));
				this.getFormHM().put("mailTogoLink", params.get("MailTogoLink"));
				this.getFormHM().put("radioDirection", params.get("RadioDirection"));
				//String allowUploadFile= (String) params.get("AllowUploadFile");
				this.getFormHM().put("allowUploadFile", params.get("AllowUploadFile"));
				////目标卡填写完整才允许提交（个性化任务、绩效报告）参数
				this.getFormHM().put("targetCompleteThenGoOn", params.get("TargetCompleteThenGoOn"));
				//单题打分
				this.getFormHM().put("mutiScoreOnePageOnePoint", params.get("MutiScoreOnePageOnePoint"));
				// 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
				this.getFormHM().put("scoreWay", params.get("scoreWay"));
				this.getFormHM().put("blankScoreUseDegree", params.get("BlankScoreUseDegree"));
				this.getFormHM().put("basicInfoItem", params.get("BasicInfoItem"));
				this.getFormHM().put("departmentLevel", params.get("DepartmentLevel"));
				ArrayList departmentLeveList = bo.getDepartmentLeveList();
				this.getFormHM().put("departmentLeveList", departmentLeveList);
				/*
				 * BS控制
				 */
				if("2".equals(object_type))
				{
					ArrayList itemfilterlist = bo.getItemFilterList();
					this.getFormHM().put("itemlist", itemfilterlist);
				}else{
					ArrayList itemfilterlist = bo.getItemFilterDWList();
					this.getFormHM().put("itemlist", itemfilterlist);
				}
							
				// 评分说明必填高级规则
				ArrayList mustFillOptionsList = (ArrayList)params.get("MustFillOptionsList");			
				String upIsValid = "";
				String downIsValid = "";
				String upDegreeId = "";
				String downDegreeId = "";
				String excludeDegree = "";
				String requiredFieldStr = "";
				
				if(mustFillOptionsList!=null && mustFillOptionsList.size()>0)
				{
					for (int i = 0; i < mustFillOptionsList.size(); i++)
			    	{
			    		LazyDynaBean bean = (LazyDynaBean) mustFillOptionsList.get(i);
			    		String flag = (String) bean.get("Flag");
			    		if(flag!=null && flag.trim().length()>0 && "up".equalsIgnoreCase(flag))
			    		{
			    			upIsValid = (String) bean.get("IsValid");
			    			upDegreeId = (String) bean.get("DegreeId"); 
			    			
			    		}else if(flag!=null && flag.trim().length()>0 && "down".equalsIgnoreCase(flag))
			    		{
			    			downIsValid = (String) bean.get("IsValid");
			    			downDegreeId = (String) bean.get("DegreeId"); 
			    		}else if(flag!=null && flag.trim().length()>0 && "exclude".equalsIgnoreCase(flag))
			    		{
			    			excludeDegree = (String) bean.get("DegreeId"); 
			    		}else if(flag!=null && flag.trim().length()>0 && "required".equalsIgnoreCase(flag))
			    		{
			    			requiredFieldStr = (String) bean.get("PointId"); 
			    		} 		   		   		
			    	}
				}
				this.getFormHM().put("upIsValid", upIsValid);
				this.getFormHM().put("showDayWeekMonth", "False");
				this.getFormHM().put("upDegreeId", upDegreeId);
				this.getFormHM().put("downIsValid", downIsValid);
				this.getFormHM().put("downDegreeId", downDegreeId);
				this.getFormHM().put("excludeDegree", excludeDegree);
				this.getFormHM().put("requiredFieldStr", requiredFieldStr);
				
				
				//初始化考核指标说明文件
				this.getFormHM().put("scoreShowRelatePlan", params.get("ScoreShowRelatePlan"));	
				this.getFormHM().put("file", null);
				this.getFormHM().put("isBrowse", bo.getIsBrowse(planId,this.userView));
				
				this.getFormHM().put("menRefDeptTmpl", params.get("MenRefDeptTmpl"));
				this.getFormHM().put("showIndicatorDesc", params.get("ShowIndicatorDesc"));		
				this.getFormHM().put("totalAppFormula", params.get("TotalAppFormula"));
				this.getFormHM().put("showOneMark", params.get("ShowOneMark"));
				this.getFormHM().put("idioSummary", params.get("SummaryFlag"));
				this.getFormHM().put("showBasicInfo", params.get("ShowBasicInfo"));
				this.getFormHM().put("lockMGradeColumn", params.get("LockMGradeColumn"));
				this.getFormHM().put("showTotalScoreSort", params.get("ShowTotalScoreSort"));
				this.getFormHM().put("isShowSubmittedPlan", params.get("isShowSubmittedPlan"));
				this.getFormHM().put("showNoMarking", params.get("ShowNoMarking"));
				this.getFormHM().put("isEntireysub", params.get("isEntireysub"));
				this.getFormHM().put("scoreBySumup", params.get("ScoreBySumup"));
				this.getFormHM().put("isShowSubmittedScores", params.get("isShowSubmittedScores"));
				this.getFormHM().put("selfScoreInDirectLeader", params.get("SelfScoreInDirectLeader"));
				this.getFormHM().put("scoreNumPerPage", params.get("ScoreNumPerPage"));
				this.getFormHM().put("isShowOrder", params.get("isShowOrder"));
				this.getFormHM().put("autoCalcTotalScoreAndOrder", params.get("AutoCalcTotalScoreAndOrder"));
							
				if(params.get("PerSet")!=null && params.get("PerSet").toString().trim().length()>0)
					this.getFormHM().put("performanceDate", "True");
				else
					this.getFormHM().put("performanceDate", "False");
				
				this.getFormHM().put("perSet", params.get("PerSet"));
				this.getFormHM().put("perSetShowMode", params.get("PerSetShowMode"));
				this.getFormHM().put("perSetStatMode", params.get("PerSetStatMode"));
				this.getFormHM().put("statCustomMode", params.get("StatCustomMode"));
										
				String statStartDate = (String) params.get("StatStartDate");
				if(statStartDate==null || statStartDate.trim().length()<=0)				
					statStartDate = PubFunc.getStringDate("yyyy-MM-dd");	
				String statEndDate = (String) params.get("StatEndDate");
				if(statEndDate==null || statEndDate.trim().length()<=0)				
					statEndDate = PubFunc.getStringDate("yyyy-MM-dd");			
				this.getFormHM().put("statStartDate", statStartDate);
				this.getFormHM().put("statEndDate", statEndDate);
				this.getFormHM().put("mutiScoreGradeCtl", params.get("MutiScoreGradeCtl"));
				this.getFormHM().put("mitiScoreMergeSelfEval", params.get("mitiScoreMergeSelfEval"));
				this.getFormHM().put("checkGradeRange", params.get("CheckGradeRange"));
				this.getFormHM().put("noteIdioGoal", params.get("noteIdioGoal"));
				this.getFormHM().put("selfEvalNotScore", params.get("SelfEvalNotScore"));
	
				this.getFormHM().put("showIndicatorContent", params.get("showIndicatorContent"));
				this.getFormHM().put("showIndicatorRole", params.get("showIndicatorRole"));
				this.getFormHM().put("showIndicatorDegree", params.get("showIndicatorDegree"));
				String relatingTargetCard = (String)params.get("relatingTargetCard"); // 关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
				if(relatingTargetCard==null || relatingTargetCard.trim().length()<=0 || "False".equalsIgnoreCase(relatingTargetCard))
					relatingTargetCard = "1";
				else if("True".equalsIgnoreCase(relatingTargetCard))
					relatingTargetCard = "2";
				this.getFormHM().put("relatingTargetCard", relatingTargetCard);
				String showYPTargetCard = (String)params.get("showYPTargetCard");
				if(!"2".equals(relatingTargetCard)){
					showYPTargetCard = "False";
				}
							
				String showDayWeekMonth = (String)params.get("ShowDayWeekMonth");
				String showDay = "0";
				String showWeek = "0";
				String showMonth = "0";
				if(showDayWeekMonth !=null && !"".equals(showDayWeekMonth)) {
					String[] empRecordType = showDayWeekMonth.split(",");
					for(int i=0;i<empRecordType.length;i++){
						if("1".equals(empRecordType[i]))
							showDay = "1";
						if("2".equals(empRecordType[i]))
							showWeek = "2";
						if("3".equals(empRecordType[i]))
							showMonth = "3";
							
					}
				}
				this.getFormHM().put("showDay", showDay);
				this.getFormHM().put("showWeek", showWeek);
				this.getFormHM().put("showMonth", showMonth);
				String gradeSameNotSubmit = (String)params.get("GradeSameNotSubmit");
				this.getFormHM().put("gradeSameNotSubmit", gradeSameNotSubmit);			
				String showHistoryScore = (String)params.get("ShowHistoryScore");
				this.getFormHM().put("showHistoryScore", showHistoryScore);			
				
				this.getFormHM().put("showYPTargetCard", showYPTargetCard);
				this.getFormHM().put("showDeductionCause", params.get("showDeductionCause"));
				this.getFormHM().put("mustFillCause", params.get("MustFillCause"));
				this.getFormHM().put("canSaveAllObjsScoreSame", params.get("CanSaveAllObjsScoreSame"));
				this.getFormHM().put("showSumRow", params.get("ShowSumRow"));
				
				/*
				 * 其它参数
				 */
				
				// 预警提醒设置
				HashMap roleMap = bo.getRoleMap(); // 系统角色集合
				ArrayList warnRoleScopeList = (ArrayList)params.get("WarnRoleScopeList");	
				String warnOpt1 = "False"; // 目标卡制定及审批参数  
			    String warnOpt2 = "False"; // 考核评分参数
			    String delayTime1 = "1"; // 目标卡制定及审批延期多少天预警
			    String delayTime2 = "1";  // 考核评分延期多少天预警   
			    String roleScope1 = ""; // 目标卡制定及审批预警对象编号（角色）
			    String roleScope1Desc = ""; // 目标卡制定及审批预警对象（角色）
			    String roleScope2 = ""; // 考核评分预警对象编号（角色）
			    String roleScope2Desc = ""; // 考核评分预警对象（角色）
				
			    
				ArrayList perGradeSetList=new ArrayList();
				String perDegree="";
				perDegree=(String)params.get("GradeClass");					//等级分类ID
				perGradeSetList=bo.getPlanPerDegreeList(perDegree,busitype);
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					for (int i = 0; i < warnRoleScopeList.size(); i++)
			    	{
			    		LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
			    		String opt = (String) bean.get("opt");
			    		if(opt!=null && opt.trim().length()>0 && "1".equalsIgnoreCase(opt))
			    		{
			    			warnOpt1 = "True";
			    			delayTime1 = (String) bean.get("delayTime");
			    			roleScope1 = (String) bean.get("roleScope"); 
			    			if(roleScope1!=null && roleScope1.trim().length()>0)
			    			{
				    			String[] matters = roleScope1.split(",");
				    			StringBuffer roleName = new StringBuffer();
				    			for (int j = 0; j < matters.length; j++)
				    			{
				    				roleName.append(",");
				    				roleName.append((String)roleMap.get(matters[j]));			    				
				    			}
				    			roleScope1Desc = roleName.toString().substring(1);
			    			}
			    		}else if(opt!=null && opt.trim().length()>0 && "2".equalsIgnoreCase(opt))
			    		{
			    			warnOpt2 = "True";
			    			delayTime2 = (String) bean.get("delayTime");
			    			roleScope2 = (String) bean.get("roleScope"); 
			    			if(roleScope2!=null && roleScope2.trim().length()>0)
			    			{
				    			String[] matters = roleScope2.split(",");
				    			StringBuffer roleName = new StringBuffer();
				    			for (int j = 0; j < matters.length; j++)
				    			{
				    				roleName.append(",");
				    				roleName.append((String)roleMap.get(matters[j]));			    				
				    			}
				    			roleScope2Desc = roleName.toString().substring(1);
			    			}
			    		}   		   		   		
			    	}
				}
				this.getFormHM().put("warnOpt1", warnOpt1);
				this.getFormHM().put("warnOpt2", warnOpt2);
				this.getFormHM().put("delayTime1", delayTime1);
				this.getFormHM().put("delayTime2", delayTime2);
				this.getFormHM().put("roleScope1", roleScope1);
				this.getFormHM().put("roleScope1Desc", roleScope1Desc);
				this.getFormHM().put("roleScope2", roleScope2);
				this.getFormHM().put("roleScope2Desc", roleScope2Desc);
				
				
				this.getFormHM().put("objsFromCard", params.get("ObjsFromCard"));
				this.getFormHM().put("wholeEval", params.get("WholeEval"));
				this.getFormHM().put("evalClass", params.get("EvalClass"));
				this.getFormHM().put("perGradeSetList", perGradeSetList);
				this.getFormHM().put("mustFillWholeEval", params.get("MustFillWholeEval"));
				this.getFormHM().put("nodeKnowDegree", params.get("NodeKnowDegree"));
				this.getFormHM().put("showAppraiseExplain", params.get("showAppraiseExplain"));
				this.getFormHM().put("gatiShowDegree", params.get("GATIShowDegree"));
				this.getFormHM().put("performanceType", params.get("performanceType"));
				this.getFormHM().put("descriptiveWholeEval", params.get("DescriptiveWholeEval"));
				/*
				 * 目标管理
				 */
				this.getFormHM().put("pointEvalType", params.get("PointEvalType"));
				this.getFormHM().put("taskSupportAttach", params.get("TaskSupportAttach"));
							
				this.getFormHM().put("spByBodySeq", params.get("SpByBodySeq"));
				this.getFormHM().put("gradeByBodySeq", params.get("GradeByBodySeq"));
				this.getFormHM().put("allowSeeAllGrade", params.get("AllowSeeAllGrade"));
				
				this.getFormHM().put("showEmployeeRecord", params.get("ShowEmployeeRecord"));
				this.getFormHM().put("readerType", params.get("ReaderType"));
				this.getFormHM().put("scoreFromItem", params.get("ScoreFromItem"));
				this.getFormHM().put("adjustEvalGradeStep", params.get("AdjustEvalGradeStep"));
				this.getFormHM().put("verifySameScore", params.get("VerifySameScore"));
				this.getFormHM().put("showEvalDirector", params.get("ShowEvalDirector"));
				this.getFormHM().put("showGrpOrder", params.get("ShowGrpOrder"));
				this.getFormHM().put("adjustEvalDegreeType", params.get("AdjustEvalDegreeType"));
				this.getFormHM().put("adjustEvalDegreeNum", params.get("AdjustEvalDegreeNum"));
				this.getFormHM().put("calcMenScoreRefDept", params.get("CalcMenScoreRefDept"));
				this.getFormHM().put("adjustEvalRange", params.get("AdjustEvalRange"));
				this.getFormHM().put("allowAdjustEvalResult", params.get("AllowAdjustEvalResult"));
				this.getFormHM().put("keyEventEnabled", params.get("KeyEventEnabled"));
				this.getFormHM().put("publicPointCannotEdit", params.get("PublicPointCannotEdit"));
				this.getFormHM().put("targetMakeSeries", params.get("targetMakeSeries"));
				this.getFormHM().put("taskAdjustNeedNew", params.get("taskAdjustNeedNew"));
				this.getFormHM().put("taskCanSign", params.get("taskCanSign"));
				this.getFormHM().put("taskNeedReview", params.get("taskNeedReview"));
				this.getFormHM().put("targetAppMode", params.get("targetAppMode"));
				this.getFormHM().put("TargetAllowAdjustAfterApprove", params.get("TargetAllowAdjustAfterApprove"));
				this.getFormHM().put("allowSeeLowerGrade", params.get("allowSeeLowerGrade"));
				this.getFormHM().put("allowLeadAdjustCard", params.get("allowLeadAdjustCard"));
				this.getFormHM().put("evalCanNewPoint", params.get("EvalCanNewPoint"));
				this.getFormHM().put("targetTraceEnabled", params.get("TargetTraceEnabled"));
				this.getFormHM().put("noShowTargetAdjustHistory", params.get("NoShowTargetAdjustHistory"));
				this.getFormHM().put("allowLeaderTrace", params.get("AllowLeaderTrace"));
				this.getFormHM().put("isLimitPointValue", params.get("IsLimitPointValue"));
				this.getFormHM().put("sortitem", params.get("TargetCalcItem"));
				// 目标卡参数			
				
				String targetItem = (String)params.get("TargetItem");				
				String targetCalcItems = (String) params.get("TargetCalcItem");
				String targetTraceItems = (String) params.get("TargetTraceItem");
				String targetCollectItems = (String) params.get("TargetCollectItem");
				String targetDefineItem = (String) params.get("TargetDefineItem");		
				
				
				String ccccc = (String)params.get("ccccccc");	//null
				if (ccccc==null)//""
				{
					
					
				}
				// dml 2011年9月9日9:52:13
				String targetMustFillitems="";
				if(params.get("TargetMustFillItem")!=null)
					targetMustFillitems=(String) params.get("TargetMustFillItem");
				String targetUsePrevious = (String)params.get("TargetUsePrevious");
				
				String taskNameDesc="";
				if(params.get("TaskNameDesc")!=null)
					taskNameDesc=(String)params.get("TaskNameDesc");
				this.getFormHM().put("taskNameDesc",taskNameDesc);
				ArrayList targetCollectItemList = new ArrayList();
				ArrayList targetTraceItemList =  new ArrayList();
				ArrayList targetDefineItemList = bo.getTargetDefineItemList(targetDefineItem,"0");
				ArrayList targetCalcItemList=new ArrayList();
				ArrayList targetMustFillItemList=new ArrayList();
				ArrayList targetUsePreviousList=new ArrayList();
				
				if(targetDefineItem.trim().length()==0)
				{
					ArrayList tempList = new ArrayList();
					for(int i=0;i<targetDefineItemList.size();i++)
					{
						LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);
						abean.set("selected","1");
						tempList.add(abean);
						targetDefineItem+=","+(String)abean.get("itemid");
					}
					targetDefineItemList=tempList;
					targetDefineItem=targetDefineItem.substring(1);
					targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetMustFillItemList=bo.getTargetItemList(targetDefineItem,targetMustFillitems,"0");
					targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
				}else if(",".equals(targetDefineItem))//客户将目标卡指标一个也不选点击了保存按钮
				{
					
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
				}else
				{
					targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
					targetMustFillItemList=bo.getTargetItemList(targetDefineItem,targetMustFillitems,"0");
					targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
				}
				
				
				
				String evaluate_str="";//考核结果显示项	 zhaoxg add 2014-6-25 慧聪网需求
				evaluate_str = (String)params.get("evaluate_str");			
				evaluate_str=evaluate_str==null|| "".equals(evaluate_str.trim())?"":(","+evaluate_str+",");
				ArrayList evaluateList=ConfigParamBo.getConfigDrawList(Integer.parseInt(method),evaluate_str);			
				this.getFormHM().put("evaluate_str",evaluate_str);
				this.getFormHM().put("evaluateList", evaluateList); 			
				String blind_point=(String)params.get("blind_point");		
				this.getFormHM().put("blind_point", blind_point);
	
				
				
				this.getFormHM().put("targetCalcItemList", targetCalcItemList);
				this.getFormHM().put("targetCollectItemList", targetCollectItemList);
				this.getFormHM().put("targetTraceItemList", targetTraceItemList);
				this.getFormHM().put("targetDefineItemList", targetDefineItemList);
				this.getFormHM().put("targetItem", "");
				this.getFormHM().put("targetCalcItem", params.get("TargetCalcItem"));
				this.getFormHM().put("targetTraceItem", params.get("TargetTraceItem"));
				this.getFormHM().put("targetCollectItem", params.get("TargetCollectItem"));
				this.getFormHM().put("targetDefineItem", params.get("TargetDefineItem"));
				this.getFormHM().put("targetMustFillItem", params.get("TargetMustFillItem"));
				this.getFormHM().put("targetMustFillItemList", targetMustFillItemList);
				this.getFormHM().put("targetUsePrevious", params.get("TargetUsePrevious"));
				this.getFormHM().put("targetUsePreviousList", targetUsePreviousList);
	
				this.getFormHM().put("evalOutLimitStdScore", params.get("EvalOutLimitStdScore"));
				this.getFormHM().put("evalOutLimitScoreOrg", params.get("EvalOutLimitScoreOrg"));
				this.getFormHM().put("showLeaderEval", params.get("ShowLeaderEval"));
				this.getFormHM().put("processNoVerifyAllScore", params.get("ProcessNoVerifyAllScore"));
				this.getFormHM().put("verifyRule", params.get("VerifyRule"));
				this.getFormHM().put("showBackTables", params.get("ShowBackTables"));
				
			} else if ("detail".equalsIgnoreCase(paramOper))
			{
				
				//--------------首钢加“按条件引入岗位职责指标” zhaoxg add 2017-2-22 新建的计划，为不选状态-------
				String dutyRule = (String) this.getFormHM().get("dutyRule");
				String dutyRuleid = (String) this.getFormHM().get("dutyRuleid");
				if(dutyRule!=null&&dutyRule.length()>0){
					this.getFormHM().put("dutyRuleid", "True");
				}else{
					this.getFormHM().put("dutyRuleid", "False");
				}
				this.getFormHM().put("dutyRule", dutyRule);
				ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
				String setid = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "SubSet");
				if(setid!=null&&setid.length()>0){
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					//此处需加非空判断拉，42108 首发设置了岗位职责子集K20 ，后来这个子集删掉了，导致下面 的程序报错了 haosl 2018-11-28
					if(fieldset!=null) {
						this.getFormHM().put("setid", setid);
						this.getFormHM().put("setdesc", fieldset.getFieldsetdesc());
					}else {
						this.getFormHM().put("setid", "");
						this.getFormHM().put("setdesc", "");
					}
				}else{
					this.getFormHM().put("setid", "");
					this.getFormHM().put("setdesc", "");
				}
				//---------------end---------------------------------
				/*
				 * 考核主体类别
				 */
				
				String planName = bo.getPlanName(planId);
				if("".equals(planName) || planName==null)
					this.getFormHM().put("showDayWeekMonth", "True");
				else
					this.getFormHM().put("showDayWeekMonth", "False");
				String bodysFromCard = (String) this.getFormHM().get("bodysFromCard");
				this.getFormHM().put("bodysFromCard", bodysFromCard);			
				String bodyTypeIds = (String) this.getFormHM().get("bodyTypeIds");
				//haosl add bodyTypeIds中如果有全角"／"则替换为半角的"/" 20170510
				bodyTypeIds = PubFunc.keyWord_reback(bodyTypeIds);
				if((method!=null && "1".equals(method)) || (bodyTypeIds.indexOf("/")!=-1))
				{
					ArrayList setlist = bo.searchCheckBody2(planId, object_type, bodyTypeIds);
					this.getFormHM().put("MainbodyTypeList", setlist);
				}else
				{
					ArrayList setlist = bo.searchCheckBody2(planId, object_type, "");
					this.getFormHM().put("MainbodyTypeList", setlist);
				}
				// 标准标度
				ArrayList grade_template = (ArrayList) this.getFormHM().get("grade_template");
				this.getFormHM().put("grade_template", grade_template);
				String templateType = bo.getTemplateType(templateId);
				this.getFormHM().put("templateType", templateType);
				//按岗位素质模型
				String byModel = (String) this.getFormHM().get("byModel");
				this.getFormHM().put("byModel", byModel);
				this.getFormHM().put("templateId", templateId);
				/*
				 * 打分控制
				 */
				// 部分指标分别设置的情况 在详细页面不进行初始化
				String tempTemplateId = (String) this.getFormHM().get("tempTemplateId");
				this.getFormHM().put("tempTemplateId", tempTemplateId);
				ArrayList badly_partRestrict = (ArrayList) this.getFormHM().get("Badly_partRestrict");
				ArrayList fine_partRestrict = (ArrayList) this.getFormHM().get("Fine_partRestrict");
				this.getFormHM().put("Badly_partRestrict", badly_partRestrict);
				this.getFormHM().put("Fine_partRestrict", fine_partRestrict);
	
				// 数据采集录入方式(1-标度 2-混合 3-打分按加扣分处理)
				String scoreflag = (String) this.getFormHM().get("dataGatherMode");
				this.getFormHM().put("dataGatherMode", scoreflag);
				String addSubtractType = (String) this.getFormHM().get("addSubtractType");
				this.getFormHM().put("addSubtractType", addSubtractType);
				// 分值转标度规则(1-就高 2-就低）
				String scaleToDegreeRule = (String) this.getFormHM().get("scaleToDegreeRule");
				this.getFormHM().put("scaleToDegreeRule", scaleToDegreeRule);
				// 标度显示形式(1-标准标度内容 2-指标标度内容）
				String degreeShowType = (String) this.getFormHM().get("degreeShowType");
				this.getFormHM().put("degreeShowType", degreeShowType);	
				String sameScoreNumLessValue = (String) this.getFormHM().get("sameScoreNumLessValue");
				// 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
				String sameAllScoreNumLess = (String) this.getFormHM().get("sameAllScoreNumLess");
				if(sameAllScoreNumLess==null || sameAllScoreNumLess.trim().length()<=0 || "0".equalsIgnoreCase(sameScoreNumLessValue))
					sameAllScoreNumLess = "0";
				this.getFormHM().put("sameAllScoreNumLess", sameAllScoreNumLess);			
				// 是否限制 指标得分为A(优秀)的数目和总体评价最高等级数目（true|false）
				String fineRestrict = (String) this.getFormHM().get("fineRestrict");
				this.getFormHM().put("fineRestrict", fineRestrict);
				// 限制 指标得分为A(优秀)的数目和总体评价最高等级数目
				String fineMax = (String) this.getFormHM().get("fineMax");
				this.getFormHM().put("fineMax", fineMax);
				String badlyRestrict = (String) this.getFormHM().get("badlyRestrict");
				this.getFormHM().put("badlyRestrict", badlyRestrict);
				String badlyMax = (String) this.getFormHM().get("badlyMax");
				this.getFormHM().put("badlyMax", badlyMax);
				// 考核对象指标结果全部相同时的选项 1: 可以保存, 2: 不能保存
				String sameResultsOption = (String) this.getFormHM().get("sameResultsOption");
				this.getFormHM().put("sameResultsOption", sameResultsOption);
				String noCanSaveDegrees = (String) this.getFormHM().get("noCanSaveDegrees");
				this.getFormHM().put("noCanSaveDegrees", noCanSaveDegrees);
				// 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理
				String blankScoreOption = (String) this.getFormHM().get("blankScoreOption");
				this.getFormHM().put("blankScoreOption", blankScoreOption);
				String mailTogoLink = (String) this.getFormHM().get("mailTogoLink");
				boolean existPlan = bo.isExist(planId);
				if(!existPlan) // 此处有个bug,暂时无法解决，等想到好的办法再解决
				{
					if((method!=null && method.trim().length()>0 && "2".equals(method)) && (!"3".equalsIgnoreCase(mailTogoLink)))
						mailTogoLink = "2";				
				}
				this.getFormHM().put("mailTogoLink", mailTogoLink);
				String radioDirection = (String) this.getFormHM().get("radioDirection");
				String allowUploadFile = (String) this.getFormHM().get("allowUploadFile");
				String targetCompleteThenGoOn = (String) this.getFormHM().get("targetCompleteThenGoOn");
				String mutiScoreOnePageOnePoint = (String) this.getFormHM().get("mutiScoreOnePageOnePoint");
				this.getFormHM().put("targetCompleteThenGoOn", targetCompleteThenGoOn);
				this.getFormHM().put("radioDirection", radioDirection);
				this.getFormHM().put("allowUploadFile", allowUploadFile);
				this.getFormHM().put("mutiScoreOnePageOnePoint", mutiScoreOnePageOnePoint);
				
				
				this.getFormHM().put("mainbodybodyid", this.getFormHM().get("mainbodybodyid"));//强制分布主体类别
				this.getFormHM().put("allmainbodybody", this.getFormHM().get("allmainbodybody"));//强制分布主体类别
				this.getFormHM().put("wholeEvalMode", this.getFormHM().get("wholeEvalMode"));//总体评价录分方式0：录入等级1：录入分值
				this.getFormHM().put("batchScoreImportFormula", this.getFormHM().get("batchScoreImportFormula"));//多人评分引入总分计算公式 pjf 2014.01.03
				// 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
				String scoreWay = (String) this.getFormHM().get("scoreWay");
				this.getFormHM().put("scoreWay", scoreWay);
				String blankScoreUseDegree = (String) this.getFormHM().get("blankScoreUseDegree");
				this.getFormHM().put("blankScoreUseDegree", blankScoreUseDegree);
				
				ArrayList departmentLeveList = (ArrayList) this.getFormHM().get("departmentLeveList");
				this.getFormHM().put("departmentLeveList", departmentLeveList);
				String departmentLevel = (String) this.getFormHM().get("departmentLevel");
				this.getFormHM().put("departmentLevel", departmentLevel);			
				/*
				 * BS控制
				 */
				if("2".equals(object_type))
				{
					ArrayList itemfilterlist = bo.getItemFilterList();
					this.getFormHM().put("itemlist", itemfilterlist);
				}else{
					ArrayList itemfilterlist = bo.getItemFilterDWList();
					this.getFormHM().put("itemlist", itemfilterlist);
				}
				
				//初始化考核指标说明文件
				String scoreShowRelatePlan = (String) this.getFormHM().get("scoreShowRelatePlan");
				this.getFormHM().put("scoreShowRelatePlan", scoreShowRelatePlan);
				
				this.getFormHM().put("file", null);
				this.getFormHM().put("isBrowse", bo.getIsBrowse(planId,this.userView));
				
				String menRefDeptTmpl = (String) this.getFormHM().get("menRefDeptTmpl");
				this.getFormHM().put("menRefDeptTmpl", menRefDeptTmpl);
				String showIndicatorDesc = (String) this.getFormHM().get("showIndicatorDesc");
				this.getFormHM().put("showIndicatorDesc", showIndicatorDesc);
				String totalAppFormula = (String) this.getFormHM().get("totalAppFormula");
				this.getFormHM().put("totalAppFormula", totalAppFormula);
				String showOneMark = (String) this.getFormHM().get("showOneMark");
				this.getFormHM().put("showOneMark", showOneMark);
				String idioSummary = (String) this.getFormHM().get("idioSummary");
				this.getFormHM().put("idioSummary", idioSummary);
				String showBasicInfo = (String) this.getFormHM().get("showBasicInfo");
				this.getFormHM().put("showBasicInfo", showBasicInfo);
				String lockMGradeColumn = (String) this.getFormHM().get("lockMGradeColumn");
				this.getFormHM().put("lockMGradeColumn", lockMGradeColumn);
				String basicInfoItem = (String) this.getFormHM().get("basicInfoItem");
				this.getFormHM().put("basicInfoItem", basicInfoItem);
				String showTotalScoreSort = (String) this.getFormHM().get("showTotalScoreSort");
				this.getFormHM().put("showTotalScoreSort", showTotalScoreSort);
				String isShowSubmittedPlan = (String) this.getFormHM().get("isShowSubmittedPlan");
				this.getFormHM().put("isShowSubmittedPlan", isShowSubmittedPlan);
				String showNoMarking = (String) this.getFormHM().get("showNoMarking");
				this.getFormHM().put("showNoMarking", showNoMarking);
				String isEntireysub = (String) this.getFormHM().get("isEntireysub");
				this.getFormHM().put("isEntireysub", isEntireysub);
				String scoreBySumup = (String) this.getFormHM().get("scoreBySumup");
				this.getFormHM().put("scoreBySumup", scoreBySumup);
				String isShowSubmittedScores = (String) this.getFormHM().get("isShowSubmittedScores");
				this.getFormHM().put("isShowSubmittedScores", isShowSubmittedScores);
				String selfScoreInDirectLeader = (String) this.getFormHM().get("selfScoreInDirectLeader");
				this.getFormHM().put("selfScoreInDirectLeader", selfScoreInDirectLeader);
				String scoreNumPerPage = (String) this.getFormHM().get("scoreNumPerPage");
				this.getFormHM().put("scoreNumPerPage", scoreNumPerPage);
				String isShowOrder = (String) this.getFormHM().get("isShowOrder");
				this.getFormHM().put("isShowOrder", isShowOrder);
				String autoCalcTotalScoreAndOrder = (String) this.getFormHM().get("autoCalcTotalScoreAndOrder");
				this.getFormHM().put("autoCalcTotalScoreAndOrder", autoCalcTotalScoreAndOrder);
				String perSet = (String) this.getFormHM().get("perSet");
				if(perSet!=null && perSet.trim().length()>0)
					this.getFormHM().put("performanceDate", "True");
				else
					this.getFormHM().put("performanceDate", "False");
				this.getFormHM().put("perSet", perSet);
				String perSetShowMode = (String) this.getFormHM().get("perSetShowMode");
				this.getFormHM().put("perSetShowMode", perSetShowMode);
				String perSetStatMode = (String) this.getFormHM().get("perSetStatMode");
				this.getFormHM().put("perSetStatMode", perSetStatMode);
				String statCustomMode = (String) this.getFormHM().get("statCustomMode");
				this.getFormHM().put("statCustomMode", statCustomMode);
				String statStartDate = (String) this.getFormHM().get("statStartDate");
				if(statStartDate==null || statStartDate.trim().length()<=0)				
					statStartDate = PubFunc.getStringDate("yyyy-MM-dd");	
				this.getFormHM().put("statStartDate", statStartDate);
				String statEndDate = (String) this.getFormHM().get("statEndDate");
				if(statEndDate==null || statEndDate.trim().length()<=0)				
					statEndDate = PubFunc.getStringDate("yyyy-MM-dd");
				this.getFormHM().put("statEndDate", statEndDate);
				String mutiScoreGradeCtl = (String) this.getFormHM().get("mutiScoreGradeCtl");
				this.getFormHM().put("mutiScoreGradeCtl", mutiScoreGradeCtl);
				String mitiScoreMergeSelfEval = (String) this.getFormHM().get("mitiScoreMergeSelfEval");
				this.getFormHM().put("mitiScoreMergeSelfEval", mitiScoreMergeSelfEval);
				String checkGradeRange = (String) this.getFormHM().get("checkGradeRange");
				this.getFormHM().put("checkGradeRange", checkGradeRange);
				String noteIdioGoal = (String) this.getFormHM().get("noteIdioGoal");
				this.getFormHM().put("noteIdioGoal", noteIdioGoal);
				String selfEvalNotScore = (String) this.getFormHM().get("selfEvalNotScore");
				this.getFormHM().put("selfEvalNotScore", selfEvalNotScore);
				String showIndicatorContent = (String) this.getFormHM().get("showIndicatorContent");
				this.getFormHM().put("showIndicatorContent", showIndicatorContent);
				String showIndicatorRole = (String) this.getFormHM().get("showIndicatorRole");
				this.getFormHM().put("showIndicatorRole", showIndicatorRole);
				String showIndicatorDegree = (String) this.getFormHM().get("showIndicatorDegree");
				this.getFormHM().put("showIndicatorDegree", showIndicatorDegree);
				String relatingTargetCard = (String) this.getFormHM().get("relatingTargetCard");
				if(relatingTargetCard==null || relatingTargetCard.trim().length()<=0 || "False".equalsIgnoreCase(relatingTargetCard))
					relatingTargetCard = "1";
				else if("True".equalsIgnoreCase(relatingTargetCard))
					relatingTargetCard = "2";
				this.getFormHM().put("relatingTargetCard", relatingTargetCard);
				String showYPTargetCard = (String)this.getFormHM().get("showYPTargetCard");//是否显示已评
				if(!"2".equals(relatingTargetCard)){
					showYPTargetCard = "False";
				}
				this.getFormHM().put("showYPTargetCard", showYPTargetCard);
				
				this.getFormHM().put("showDay", (String)this.getFormHM().get("showDay"));
				this.getFormHM().put("showWeek", (String)this.getFormHM().get("showWeek"));
				this.getFormHM().put("showMonth", (String)this.getFormHM().get("showMonth"));
				String gradeSameNotSubmit = (String)this.getFormHM().get("gradeSameNotSubmit");
				this.getFormHM().put("gradeSameNotSubmit", gradeSameNotSubmit);
				String showHistoryScore = (String)this.getFormHM().get("showHistoryScore");
				this.getFormHM().put("showHistoryScore", showHistoryScore);			
				
				String showDeductionCause = (String) this.getFormHM().get("showDeductionCause");
				this.getFormHM().put("showDeductionCause", showDeductionCause);
				String mustFillCause = (String) this.getFormHM().get("mustFillCause");
				this.getFormHM().put("mustFillCause", mustFillCause);
				String canSaveAllObjsScoreSame = (String) this.getFormHM().get("canSaveAllObjsScoreSame");
				this.getFormHM().put("canSaveAllObjsScoreSame", canSaveAllObjsScoreSame);
				String showSumRow = (String) this.getFormHM().get("showSumRow");
				this.getFormHM().put("showSumRow", showSumRow);
				/*
				 * 其它参数
				 */
				String warnOpt1 = (String) this.getFormHM().get("warnOpt1");
				String warnOpt2 = (String) this.getFormHM().get("warnOpt2");
				String delayTime1 = (String) this.getFormHM().get("delayTime1");
				String delayTime2 = (String) this.getFormHM().get("delayTime2");
				String roleScope1 = (String) this.getFormHM().get("roleScope1");
				String roleScope1Desc = (String) this.getFormHM().get("roleScope1Desc");
				String roleScope2 = (String) this.getFormHM().get("roleScope2");
				String roleScope2Desc = (String) this.getFormHM().get("roleScope2Desc");
				ArrayList perGradeSetList=new ArrayList();
				String busitype = (String) this.getFormHM().get("busitype");//等级分类ID
				perGradeSetList=bo.getPlanPerDegreeList("1",busitype);
				this.getFormHM().put("warnOpt1", warnOpt1);
				this.getFormHM().put("warnOpt2", warnOpt2);
				this.getFormHM().put("delayTime1", delayTime1);
				this.getFormHM().put("delayTime2", delayTime2);
				this.getFormHM().put("roleScope1", roleScope1);
				this.getFormHM().put("roleScope1Desc", roleScope1Desc);
				this.getFormHM().put("roleScope2", roleScope2);
				this.getFormHM().put("roleScope2Desc", roleScope2Desc);
				this.getFormHM().put("perGradeSetList", perGradeSetList);
				
				String objsFromCard = (String) this.getFormHM().get("objsFromCard");
				this.getFormHM().put("objsFromCard", objsFromCard);
				String wholeEval = (String) this.getFormHM().get("wholeEval");
				this.getFormHM().put("wholeEval", wholeEval);
				String evalClass = (String) this.getFormHM().get("evalClass");
				this.getFormHM().put("evalClass", evalClass);
				String mustFillWholeEval = (String) this.getFormHM().get("mustFillWholeEval");
				this.getFormHM().put("mustFillWholeEval", mustFillWholeEval);
				String nodeKnowDegree = (String) this.getFormHM().get("nodeKnowDegree");
				this.getFormHM().put("nodeKnowDegree", nodeKnowDegree);
				String showAppraiseExplain = (String) this.getFormHM().get("showAppraiseExplain");
				this.getFormHM().put("showAppraiseExplain", showAppraiseExplain);
				String gatiShowDegree = (String) this.getFormHM().get("gatiShowDegree");
				this.getFormHM().put("gatiShowDegree", gatiShowDegree);
				String performanceType = (String) this.getFormHM().get("performanceType");
				this.getFormHM().put("performanceType", performanceType);
				String descriptiveWholeEval = (String) this.getFormHM().get("descriptiveWholeEval");
				this.getFormHM().put("descriptiveWholeEval", descriptiveWholeEval);
				
				String evaluate_str="";//考核结果显示项	 zhaoxg add 2014-6-25 慧聪网需求
				evaluate_str = (String)this.getFormHM().get("evaluate_str");			
				evaluate_str=evaluate_str==null|| "".equals(evaluate_str.trim())?"":(","+evaluate_str+",");
				ArrayList evaluateList=ConfigParamBo.getConfigDrawList(Integer.parseInt(method),evaluate_str);			
				this.getFormHM().put("evaluate_str",evaluate_str);
				this.getFormHM().put("evaluateList", evaluateList);
				String blind_point=(String)this.getFormHM().get("blind_point");		
				this.getFormHM().put("blind_point", blind_point);
	
				
				/*
				 * 目标管理
				 */			
				String spByBodySeq = (String) this.getFormHM().get("spByBodySeq");
				this.getFormHM().put("spByBodySeq", spByBodySeq);
				String gradeByBodySeq = (String) this.getFormHM().get("gradeByBodySeq");
				this.getFormHM().put("gradeByBodySeq", gradeByBodySeq);
				String allowSeeAllGrade = (String) this.getFormHM().get("allowSeeAllGrade");
				this.getFormHM().put("allowSeeAllGrade", allowSeeAllGrade);						
				String pointEvalType = (String) this.getFormHM().get("pointEvalType");
				this.getFormHM().put("pointEvalType", pointEvalType);
				String taskSupportAttach = (String) this.getFormHM().get("taskSupportAttach");
				this.getFormHM().put("taskSupportAttach", taskSupportAttach);
				String showEmployeeRecord = (String) this.getFormHM().get("showEmployeeRecord");
				this.getFormHM().put("showEmployeeRecord", showEmployeeRecord);
				String readerType = (String) this.getFormHM().get("readerType");
				this.getFormHM().put("readerType", readerType);
				String scoreFromItem = (String) this.getFormHM().get("scoreFromItem");
				this.getFormHM().put("scoreFromItem", scoreFromItem);
				String adjustEvalGradeStep = (String) this.getFormHM().get("adjustEvalGradeStep");
				this.getFormHM().put("adjustEvalGradeStep", adjustEvalGradeStep);
				String verifySameScore = (String) this.getFormHM().get("verifySameScore");
				this.getFormHM().put("verifySameScore", verifySameScore);
				String showEvalDirector = (String) this.getFormHM().get("showEvalDirector");
				this.getFormHM().put("showEvalDirector", showEvalDirector);
				String showGrpOrder = (String) this.getFormHM().get("showGrpOrder");
				this.getFormHM().put("showGrpOrder", showGrpOrder);
				String adjustEvalDegreeType = (String) this.getFormHM().get("adjustEvalDegreeType");
				this.getFormHM().put("adjustEvalDegreeType", adjustEvalDegreeType);
				String adjustEvalDegreeNum = (String) this.getFormHM().get("adjustEvalDegreeNum");
				this.getFormHM().put("adjustEvalDegreeNum", adjustEvalDegreeNum);
				String calcMenScoreRefDept = (String) this.getFormHM().get("calcMenScoreRefDept");
				this.getFormHM().put("calcMenScoreRefDept", calcMenScoreRefDept);
				String allowAdjustEvalResult = (String) this.getFormHM().get("allowAdjustEvalResult");
				this.getFormHM().put("allowAdjustEvalResult", allowAdjustEvalResult);
				String adjustEvalRange = (String) this.getFormHM().get("adjustEvalRange");
				this.getFormHM().put("adjustEvalRange", adjustEvalRange);
				String keyEventEnabled = (String) this.getFormHM().get("keyEventEnabled");
				this.getFormHM().put("keyEventEnabled", keyEventEnabled);
				String publicPointCannotEdit = (String) this.getFormHM().get("publicPointCannotEdit");
				this.getFormHM().put("publicPointCannotEdit", publicPointCannotEdit);
				String targetMakeSeries = (String) this.getFormHM().get("targetMakeSeries");
				this.getFormHM().put("targetMakeSeries", targetMakeSeries);
				String taskAdjustNeedNew = (String) this.getFormHM().get("taskAdjustNeedNew");
				this.getFormHM().put("taskAdjustNeedNew", taskAdjustNeedNew);
				String taskCanSign = (String) this.getFormHM().get("taskCanSign");
				this.getFormHM().put("taskCanSign", taskCanSign);
				String taskNeedReview = (String) this.getFormHM().get("taskNeedReview");
				this.getFormHM().put("taskNeedReview", taskNeedReview);
				String targetAppMode = (String) this.getFormHM().get("targetAppMode");
				this.getFormHM().put("targetAppMode", targetAppMode);
				String TargetAllowAdjustAfterApprove = (String) this.getFormHM().get("TargetAllowAdjustAfterApprove");
				this.getFormHM().put("TargetAllowAdjustAfterApprove", TargetAllowAdjustAfterApprove);
				String allowSeeLowerGrade = (String) this.getFormHM().get("allowSeeLowerGrade");
				this.getFormHM().put("allowSeeLowerGrade", allowSeeLowerGrade);
				String allowLeadAdjustCard = (String) this.getFormHM().get("allowLeadAdjustCard");
				this.getFormHM().put("allowLeadAdjustCard", allowLeadAdjustCard);
				String evalCanNewPoint = (String) this.getFormHM().get("evalCanNewPoint");
				this.getFormHM().put("evalCanNewPoint", evalCanNewPoint);
				String targetTraceEnabled = (String) this.getFormHM().get("targetTraceEnabled");
				this.getFormHM().put("targetTraceEnabled", targetTraceEnabled);
				String noShowTargetAdjustHistory = (String) this.getFormHM().get("noShowTargetAdjustHistory");
				this.getFormHM().put("noShowTargetAdjustHistory", noShowTargetAdjustHistory);
				String allowLeaderTrace = (String) this.getFormHM().get("allowLeaderTrace");
				this.getFormHM().put("allowLeaderTrace", allowLeaderTrace);
				String isLimitPointValue = (String) this.getFormHM().get("isLimitPointValue");
				this.getFormHM().put("isLimitPointValue", isLimitPointValue);
				// 目标卡参数
				String targetItem = (String)this.getFormHM().get("targetItem");	
				String targetCalcItems = (String) this.getFormHM().get("targetCalcItem");			
				String targetTraceItems = (String) this.getFormHM().get("targetTraceItem");
				String targetCollectItems = (String) this.getFormHM().get("targetCollectItem");
				this.getFormHM().put("targetCollectItem", targetCollectItems);
				String targetCollectItemMust = "";
				if(targetCollectItems!=null && targetCollectItems.length()>0) {
	        		String[] items = targetCollectItems.split(",");
	        		targetCollectItems="";
	                for (int i = 0; i < items.length; i++){
	                    String[] temps = items[i].split(":");
	                    targetCollectItems += temps[0]+",";
	                    if(temps.length>1) {
	                        if("1".equals(temps[1])){
	                            targetCollectItemMust += temps[0]+",";
	                        }
	                    }
	                }
	                bo.setTargetCollectItemMust(targetCollectItemMust);
				}
				String targetDefineItem = (String) this.getFormHM().get("targetDefineItem");
				String targetMustFillitems = (String) this.getFormHM().get("targetMustFillItem");
				String targetUsePrevious = (String)this.getFormHM().get("targetUsePrevious");
							
				ArrayList targetDefineItemList = bo.getTargetDefineItemList(targetDefineItem,"0");
				ArrayList targetCollectItemList = new ArrayList();
				ArrayList targetTraceItemList =  new ArrayList();
				ArrayList targetCalcItemList = new ArrayList();
				ArrayList targetMustFillItemList=new ArrayList();
				ArrayList targetUsePreviousList=new ArrayList();
				
				if(targetDefineItem.trim().length()==0)
				{
					ArrayList tempList = new ArrayList();
					for(int i=0;i<targetDefineItemList.size();i++)
					{
						LazyDynaBean abean =  (LazyDynaBean)targetDefineItemList.get(i);
						abean.set("selected","1");
						tempList.add(abean);
						targetDefineItem+=","+(String)abean.get("itemid");
					}
					targetDefineItemList=tempList;
					targetDefineItem=targetDefineItem.substring(1);
					targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
					targetMustFillItemList=bo.getTargetItemList(targetDefineItem,targetMustFillitems,"0");
					targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
					
				}else if(",".equals(targetDefineItem))//客户将目标卡指标一个也不选点击了保存按钮
				{
					
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
				}else
				{
					targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
					targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
					targetMustFillItemList=bo.getTargetItemList(targetDefineItem,targetMustFillitems,"0");
					targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
				}
				
				this.getFormHM().put("targetCalcItemList", targetCalcItemList);
				this.getFormHM().put("targetCollectItemList", targetCollectItemList);
				this.getFormHM().put("targetMustFillItemList", targetMustFillItemList);
				this.getFormHM().put("targetUsePreviousList", targetUsePreviousList);												
				this.getFormHM().put("targetTraceItemList", targetTraceItemList);
				this.getFormHM().put("targetDefineItemList", targetDefineItemList);
				
				this.getFormHM().put("targetCalcItem", targetCalcItems);
				this.getFormHM().put("targetTraceItem", targetTraceItems);
				this.getFormHM().put("targetDefineItem", targetDefineItem);
				this.getFormHM().put("targetMustFillItem", targetMustFillitems);			
				this.getFormHM().put("targetUsePrevious", targetUsePrevious);
	
				String evalOutLimitStdScore = (String) this.getFormHM().get("evalOutLimitStdScore");
				this.getFormHM().put("evalOutLimitStdScore", evalOutLimitStdScore);
				String evalOutLimitScoreOrg = (String) this.getFormHM().get("evalOutLimitScoreOrg");
				this.getFormHM().put("evalOutLimitScoreOrg", evalOutLimitScoreOrg);
				String showLeaderEval = (String) this.getFormHM().get("showLeaderEval");
				this.getFormHM().put("showLeaderEval", showLeaderEval);
				String processNoVerifyAllScore = (String) this.getFormHM().get("processNoVerifyAllScore");
				this.getFormHM().put("processNoVerifyAllScore", processNoVerifyAllScore);
				String verifyRule = (String) this.getFormHM().get("verifyRule");
				this.getFormHM().put("verifyRule", verifyRule);
				String showBackTables = (String) this.getFormHM().get("showBackTables");
				this.getFormHM().put("showBackTables", showBackTables);
				this.getFormHM().put("taskNameDesc",(String)this.getFormHM().get("taskNameDesc"));
	
				// 计划明细页传递进来的必填指标 modify by 刘蒙
				this.getFormHM().put("requiredFieldStr", hm.get("requiredFieldStr"));
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
	}		
}
