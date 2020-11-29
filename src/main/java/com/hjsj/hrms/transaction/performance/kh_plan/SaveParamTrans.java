package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveParamTrans.java</p>
 * <p>Description:保存参数设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveParamTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("plan_id");
		CheckPrivSafeBo csbo = new CheckPrivSafeBo(this.frameconn,this.userView);
		String theStatus=(String)hm.get("status");
		boolean flag = csbo.isHavePriv(this.userView, planId);
		if(!flag){
			return;
		}
		String bodyIds = PubFunc.keyWord_reback((String) this.getFormHM().get("bodyTypeIds"));
		String gather_typeJ = (String) hm.get("gather_type");
		
		// paramOper = list: 列表页面编辑参数 detail:详细信息页面编辑参数
		String paramOper = (String) this.getFormHM().get("paramOper");
		if ("list".equalsIgnoreCase(paramOper)||"5".equals(theStatus))
		{
			String targetDefineItem = (String) this.getFormHM().get("targetDefineItem");
			String targetTraceEnabled = (String) this.getFormHM().get("targetTraceEnabled");
			// 保存考核主体类别设置
			if (bodyIds != null && !"".equals(bodyIds))
			{
				String[] bodyids = bodyIds.split(",");
				
				if("1".equals(targetTraceEnabled)&&targetDefineItem.toLowerCase().indexOf("rater")>-1&&!isHaveLevelOne(bodyids)){
					throw GeneralExceptionHandler.Handle(new Throwable("若在目标卡指标里设置了'评价人'指标，则必须指定直属领导！"));
				}
				
				ExamPlanBo bo = new ExamPlanBo(planId, this.frameconn);
				RecordVo vo = bo.getPlanVo();
				bo.saveMainBodyType(bodyids, planId);
				// 对于暂停状态的计划 调整设置的考核主体类别
				if (vo != null)
				{
					String status = vo.getString("status");
					String method = vo.getString("method") == null ? "1" : (vo.getString("method").length()==0?"1":vo.getString("method"));
					String object_type = vo.getString("object_type");
					// 如果设置了本人考核主体类别 相应在考核主体表 主体指标权限表 同步本人类别 360和目标计划都有
					if ("2".equalsIgnoreCase(object_type) && ("5".equals(status) || "0".equals(status))) // 暂停 和 另存的 起草状态的纪录都要同步主体类别
						bo.synchronizeSelPerson(planId);// 考核主体表 主体指标权限表中删掉主体类别中原来有的类别纪录 如果新增了本人主体类别就加上相应主体类别的纪录
					if ("2".equals(method) && "5".equals(status)) // 目标管理 应该相应的增加或者删除项目权限表纪录
						bo.updateItemPriv(planId);
				}
			}			

			// DaFenParam
			String addSubtractType = (String) this.getFormHM().get("addSubtractType");
			String dataGatherMode = (String) this.getFormHM().get("dataGatherMode");
			String degreeShowType = (String) this.getFormHM().get("degreeShowType");
			String scaleToDegreeRule = (String) this.getFormHM().get("scaleToDegreeRule");
			String sameScoreNumLessValue = (String) this.getFormHM().get("sameScoreNumLessValue");
			String sameAllScoreNumLess = (String) this.getFormHM().get("sameAllScoreNumLess");
			if(sameAllScoreNumLess==null || sameAllScoreNumLess.trim().length()<=0 || "0".equalsIgnoreCase(sameScoreNumLessValue))
				sameAllScoreNumLess = "0";
			String fineRestrict = (String) this.getFormHM().get("fineRestrict");
			String fineMax = (String) this.getFormHM().get("fineMax");
			String badlyRestrict = (String) this.getFormHM().get("badlyRestrict");
			String badlyMax = (String) this.getFormHM().get("badlyMax");
			String sameResultsOption = (String) this.getFormHM().get("sameResultsOption");
			String noCanSaveDegrees = (String) this.getFormHM().get("noCanSaveDegrees");
			String blankScoreOption = (String) this.getFormHM().get("blankScoreOption");
			String mailTogoLink = (String) this.getFormHM().get("mailTogoLink");
			String radioDirection = (String) this.getFormHM().get("radioDirection");
			String scoreWay = (String) this.getFormHM().get("scoreWay");
			String blankScoreUseDegree = (String) this.getFormHM().get("blankScoreUseDegree");
			String departmentLevel = (String) this.getFormHM().get("departmentLevel");
			// BSParam
			String scoreShowRelatePlan = (String) this.getFormHM().get("scoreShowRelatePlan");
			String menRefDeptTmpl = (String) this.getFormHM().get("menRefDeptTmpl");
			String showIndicatorDesc = (String) this.getFormHM().get("showIndicatorDesc");
			String totalAppFormula = (String) this.getFormHM().get("totalAppFormula");
			totalAppFormula=totalAppFormula!=null?totalAppFormula:"";
			totalAppFormula=SafeCode.decode(totalAppFormula);
			totalAppFormula = totalAppFormula.replaceAll("'", "\"");
			totalAppFormula=PubFunc.keyWord_reback(totalAppFormula);
			String idioSummary = (String) (String) this.getFormHM().get("idioSummary");
			String showOneMark = (String) this.getFormHM().get("showOneMark");
			String showTotalScoreSort = (String) this.getFormHM().get("showTotalScoreSort");
			String isShowSubmittedPlan = (String) this.getFormHM().get("isShowSubmittedPlan");
			String showNoMarking = (String) this.getFormHM().get("showNoMarking");
			String isEntireysub = (String) this.getFormHM().get("isEntireysub");
			String scoreBySumup = (String) this.getFormHM().get("scoreBySumup");
			String isShowSubmittedScores = (String) this.getFormHM().get("isShowSubmittedScores");
			String selfScoreInDirectLeader = (String) this.getFormHM().get("selfScoreInDirectLeader");
			String scoreNumPerPage = (String) this.getFormHM().get("scoreNumPerPage");
			String isShowOrder = (String) this.getFormHM().get("isShowOrder");
			String autoCalcTotalScoreAndOrder = (String) this.getFormHM().get("autoCalcTotalScoreAndOrder");
			String perSet = (String) this.getFormHM().get("perSet");
			String performanceDate = (String) this.getFormHM().get("performanceDate");
			performanceDate = zeroToFalse(performanceDate);
			if(performanceDate==null || performanceDate.trim().length()<=0 || "false".equalsIgnoreCase(performanceDate))
				perSet = "";			
			String perSetShowMode = (String) this.getFormHM().get("perSetShowMode");
			String perSetStatMode = (String) this.getFormHM().get("perSetStatMode");
			String statCustomMode = (String) this.getFormHM().get("statCustomMode");
			String statStartDate = (String) this.getFormHM().get("statStartDate");
			if(statStartDate!=null && statStartDate.trim().length()>0 && statStartDate.indexOf("-")!=-1)				
				statStartDate = statStartDate.replaceAll("-",".");
			String statEndDate = (String) this.getFormHM().get("statEndDate");
			if(statEndDate!=null && statEndDate.trim().length()>0 && statEndDate.indexOf("-")!=-1)				
				statEndDate = statEndDate.replaceAll("-",".");	
			
			if("9".equalsIgnoreCase(perSetStatMode))
			{}
			else
			{
				statStartDate = "";
				statEndDate = "";
			}
			
			String mutiScoreGradeCtl = (String) this.getFormHM().get("mutiScoreGradeCtl");
			String mitiScoreMergeSelfEval = (String) this.getFormHM().get("mitiScoreMergeSelfEval");
			String checkGradeRange = (String) this.getFormHM().get("checkGradeRange");
			String noteIdioGoal = (String) this.getFormHM().get("noteIdioGoal");
			String selfEvalNotScore = (String) this.getFormHM().get("selfEvalNotScore");
			String showIndicatorContent = (String) this.getFormHM().get("showIndicatorContent");
			String showIndicatorRole = (String) this.getFormHM().get("showIndicatorRole");
			String showIndicatorDegree = (String) this.getFormHM().get("showIndicatorDegree");
			String relatingTargetCard = (String) this.getFormHM().get("relatingTargetCard");
			if(noteIdioGoal!=null && noteIdioGoal.trim().length()>0 && ("False".equalsIgnoreCase(noteIdioGoal) || "0".equalsIgnoreCase(noteIdioGoal)))
				relatingTargetCard = "1";
			String showYPTargetCard = (String)this.getFormHM().get("showYPTargetCard");//是否显示已评
			if("2".equals(relatingTargetCard)){
				if(showYPTargetCard==null || "False".equalsIgnoreCase(showYPTargetCard) || "0".equalsIgnoreCase(showYPTargetCard)){
					showYPTargetCard = "False";
				}else if("True".equalsIgnoreCase(showYPTargetCard) || "1".equalsIgnoreCase(showYPTargetCard)){
					showYPTargetCard = "True";
				}
			}else{
				showYPTargetCard = "False";
			}
			String showDeductionCause = (String) this.getFormHM().get("showDeductionCause");
			String mustFillCause = (String) this.getFormHM().get("mustFillCause");
			String showSumRow = (String) this.getFormHM().get("showSumRow");
			String basicInfoItem = (String) this.getFormHM().get("basicInfoItem");
			String lockMGradeColumn = (String)this.getFormHM().get("lockMGradeColumn");
			String showBasicInfo = (String) this.getFormHM().get("showBasicInfo");
			if(showBasicInfo!=null&&showBasicInfo.trim().length()>0&&("False".equalsIgnoreCase(showBasicInfo)|| "0".equalsIgnoreCase(showBasicInfo))){
				basicInfoItem = "";
				lockMGradeColumn = "false";
			}
			String showDayWeekMonth ="";//查看员工日志
			String showDay = (String) this.getFormHM().get("showDay");
			String showWeek = (String) this.getFormHM().get("showWeek");
			String showMonth = (String) this.getFormHM().get("showMonth");
			if("1".equals(showDay))
				showDayWeekMonth = showDayWeekMonth+"1"+",";
			if("2".equals(showWeek))
				showDayWeekMonth = showDayWeekMonth+"2"+",";
			if("3".equals(showMonth))
				showDayWeekMonth = showDayWeekMonth+"3"+",";
			if(showDayWeekMonth != null && !"".equals(showDayWeekMonth))
				showDayWeekMonth=showDayWeekMonth.substring(0, showDayWeekMonth.length()-1);
			
			String gradeSameNotSubmit=(String) this.getFormHM().get("gradeSameNotSubmit");//等级不同分数相同不能提交
			String showHistoryScore = (String) this.getFormHM().get("showHistoryScore");//显示历次得分表
			
			// OtherParam
			String objsFromCard = (String) this.getFormHM().get("objsFromCard");
			String wholeEval = (String) this.getFormHM().get("wholeEval");
			String evalClass = (String) this.getFormHM().get("evalClass");
			if(wholeEval!=null&&wholeEval.trim().length()>0&&("False".equalsIgnoreCase(wholeEval)|| "0".equalsIgnoreCase(wholeEval)))
				evalClass = "";
			String mustFillWholeEval = (String) this.getFormHM().get("mustFillWholeEval");
			String nodeKnowDegree = (String) this.getFormHM().get("nodeKnowDegree");
			String showAppraiseExplain = (String) this.getFormHM().get("showAppraiseExplain");
			String gatiShowDegree = (String) this.getFormHM().get("gatiShowDegree");
			String performanceType = (String) this.getFormHM().get("performanceType");
			String descriptiveWholeEval = (String) this.getFormHM().get("descriptiveWholeEval");
			String canSaveAllObjsScoreSame = (String) this.getFormHM().get("canSaveAllObjsScoreSame");
			// 目标管理
			String pointEvalType = (String) this.getFormHM().get("pointEvalType");
			String taskSupportAttach = (String) this.getFormHM().get("taskSupportAttach");
			String spByBodySeq = (String) this.getFormHM().get("spByBodySeq");
			String gradeByBodySeq = (String) this.getFormHM().get("gradeByBodySeq");
			String allowSeeAllGrade = (String) this.getFormHM().get("allowSeeAllGrade");
			
			
			String showEmployeeRecord = (String) this.getFormHM().get("showEmployeeRecord");
			String bodysFromCard = (String) this.getFormHM().get("bodysFromCard");
			String readerType = (String) this.getFormHM().get("readerType");
			String scoreFromItem = (String) this.getFormHM().get("scoreFromItem");
			String adjustEvalGradeStep = (String) this.getFormHM().get("adjustEvalGradeStep");
			String verifySameScore = (String) this.getFormHM().get("verifySameScore");
			String showEvalDirector = (String) this.getFormHM().get("showEvalDirector");
			String showGrpOrder = (String) this.getFormHM().get("showGrpOrder");
			String adjustEvalDegreeType = (String) this.getFormHM().get("adjustEvalDegreeType");
			String adjustEvalDegreeNum = (String) this.getFormHM().get("adjustEvalDegreeNum");
			String calcMenScoreRefDept = (String) this.getFormHM().get("calcMenScoreRefDept");
			String adjustEvalRange = (String) this.getFormHM().get("adjustEvalRange");
			String allowAdjustEvalResult = (String) this.getFormHM().get("allowAdjustEvalResult");
			String keyEventEnabled = (String) this.getFormHM().get("keyEventEnabled");
			String publicPointCannotEdit = (String) this.getFormHM().get("publicPointCannotEdit");
			String targetMakeSeries = (String) this.getFormHM().get("targetMakeSeries");
			String taskAdjustNeedNew = (String) this.getFormHM().get("taskAdjustNeedNew");
			String taskCanSign = (String) this.getFormHM().get("taskCanSign");
			String taskNeedReview = (String) this.getFormHM().get("taskNeedReview");
			String targetAppMode = (String) this.getFormHM().get("targetAppMode");
			String targetAllowAdjustAfterApprove = (String) this.getFormHM().get("TargetAllowAdjustAfterApprove");
			String allowLeadAdjustCard = (String) this.getFormHM().get("allowLeadAdjustCard");
			String allowSeeLowerGrade = (String) this.getFormHM().get("allowSeeLowerGrade");
			String evalCanNewPoint = (String) this.getFormHM().get("evalCanNewPoint");
			
			String targetTraceItem = (String) this.getFormHM().get("targetTraceItem");
			String targetCollectItem = (String) this.getFormHM().get("targetCollectItem");
			String targetCalcItem = (String) this.getFormHM().get("targetCalcItem");

			

			String noShowTargetAdjustHistory = (String) this.getFormHM().get("noShowTargetAdjustHistory");
			String allowLeaderTrace = (String) this.getFormHM().get("allowLeaderTrace");

			String processNoVerifyAllScore = (String) this.getFormHM().get("processNoVerifyAllScore");
			String verifyRule = (String) this.getFormHM().get("verifyRule");
			verifyRule = PubFunc.keyWord_reback(verifyRule);
			String evalOutLimitStdScore = (String) this.getFormHM().get("evalOutLimitStdScore");
			String evalOutLimitScoreOrg = (String) this.getFormHM().get("evalOutLimitScoreOrg");
			String showLeaderEval = (String) this.getFormHM().get("showLeaderEval");
			String showBackTables = (String) this.getFormHM().get("showBackTables");
			String isLimitPointValue = (String) this.getFormHM().get("isLimitPointValue");
			String targetMustFillItem = (String) this.getFormHM().get("targetMustFillItem");
			String targetUsePrevious = (String) this.getFormHM().get("targetUsePrevious");
			String taskNameDesc=(String)this.getFormHM().get("taskNameDesc");
			String allowUploadFile=(String)this.getFormHM().get("allowUploadFile");
			String mutiScoreOnePageOnePoint=(String)this.getFormHM().get("mutiScoreOnePageOnePoint");
			String targetCompleteThenGoOn=(String)this.getFormHM().get("targetCompleteThenGoOn");
			
			String mainbodybodyid = (String)this.getFormHM().get("mainbodybodyid");//强制分布主体类别
			String allmainbodybody = (String)this.getFormHM().get("allmainbodybody");//强制分布主体类别

			//勾选强制百分比分布后，默认选中所有主体类别,与新建时一直  haosl
			if(StringUtils.isEmpty(mainbodybodyid) && "true".equalsIgnoreCase(zeroToFalse(mutiScoreGradeCtl)) && StringUtils.isNotBlank(bodyIds)) {
				mainbodybodyid = bodyIds.substring(0, bodyIds.length()-1);
			}
			String wholeEvalMode = (String)this.getFormHM().get("wholeEvalMode");//总体评价录分方式0：录入等级1：录入分值
			String batchScoreImportFormula = (String)this.getFormHM().get("batchScoreImportFormula");//多人评分引入总分计算公式 pjf 2014.01.03
			String dutyRuleid = (String)this.getFormHM().get("dutyRuleid");
			String dutyRule = (String)this.getFormHM().get("dutyRule");
			if(!("1".equals(dutyRuleid)||"true".equalsIgnoreCase(dutyRuleid))){
				dutyRule = "";
			}
			dutyRule = PubFunc.keyWord_reback(dutyRule);
			if("1".equals(gather_typeJ))
			{
				keyEventEnabled = "False";  //积分修正[true|false]
				evalCanNewPoint = "False";//评估打分允许新增考核指标 (True, False默认为False)
				allowAdjustEvalResult = "False";//允许调整评估结果 (True, False默认为False)
				adjustEvalRange = "0";//调整范围：0=指标，1=总分.默认为0
				adjustEvalDegreeType = "0";//调整使用标度0=指标标度，1=等级标度.默认为0
				adjustEvalDegreeNum = "0";//调整浮动等级：整数值
				adjustEvalGradeStep ="";//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
				calcMenScoreRefDept = "False";//个人考核评分=个人指标得分*部门指标得分的权重和（目标考核和360°）True, False, 默认为 False
				scoreFromItem = "False";//按项目权重逐级计算总分，True，False 默认False；
				showGrpOrder = "True";//评分调整  显示排名：True, False, 默认为 True
				menRefDeptTmpl = ""; //本次评分=个人指标得分*部门指标得分的权重和时，部门模板。为空表示与当前计划相同。
				
			}else if("0".equals(gather_typeJ))
			{
				bodysFromCard = "False"; //考核主体从机读卡读取(主体类别自动对应)
				objsFromCard = "False"; //考核对象是否从机读卡读取(考核实施中不需要选择考核对象)
				readerType = "0"; // 机读类型:0光标阅读机(默认),1扫描仪
			}
			if("False".equalsIgnoreCase(zeroToFalse(showEmployeeRecord)))
				showDayWeekMonth="";
			
			
			HashMap rootAttributes = new HashMap();
			rootAttributes.put("IsLimitPointValue", zeroToFalse(isLimitPointValue));
			rootAttributes.put("NoCanSaveDegrees", noCanSaveDegrees);
			rootAttributes.put("ShowBackTables", showBackTables);
			rootAttributes.put("EvalOutLimitStdScore", zeroToFalse(evalOutLimitStdScore));
			rootAttributes.put("EvalOutLimitScoreOrg", zeroToFalse(evalOutLimitScoreOrg));
			rootAttributes.put("ShowLeaderEval", zeroToFalse(showLeaderEval));
			rootAttributes.put("ProcessNoVerifyAllScore", zeroToFalse(processNoVerifyAllScore));
			rootAttributes.put("AllowLeaderTrace", zeroToFalse(allowLeaderTrace));
			rootAttributes.put("NoShowTargetAdjustHistory", zeroToFalse(noShowTargetAdjustHistory));
			rootAttributes.put("ShowSumRow", zeroToFalse(showSumRow));
			rootAttributes.put("CanSaveAllObjsScoreSame", zeroToFalse(canSaveAllObjsScoreSame));
			rootAttributes.put("AllowLeadAdjustCard", zeroToFalse(allowLeadAdjustCard));
			rootAttributes.put("AllowSeeLowerGrade", zeroToFalse(allowSeeLowerGrade));
			rootAttributes.put("TargetAllowAdjustAfterApprove", zeroToFalse(targetAllowAdjustAfterApprove));
			rootAttributes.put("DataGatherMode", dataGatherMode);
			rootAttributes.put("addSubtractType", addSubtractType);
			rootAttributes.put("DegreeShowType", degreeShowType);
			rootAttributes.put("ScaleToDegreeRule", scaleToDegreeRule);
			rootAttributes.put("SameAllScoreNumLess", sameAllScoreNumLess);
			rootAttributes.put("FineRestrict", zeroToFalse(fineRestrict));
			rootAttributes.put("FineMax", fineMax);
			rootAttributes.put("BadlyRestrict", zeroToFalse(badlyRestrict));
			rootAttributes.put("BadlyMax", badlyMax);
			rootAttributes.put("SameResultsOption", sameResultsOption);
			rootAttributes.put("BlankScoreOption", blankScoreOption);
			rootAttributes.put("MailTogoLink", mailTogoLink);
			rootAttributes.put("RadioDirection", radioDirection);
			rootAttributes.put("ScoreWay", scoreWay);
			rootAttributes.put("BlankScoreUseDegree", blankScoreUseDegree == null ? "A" : blankScoreUseDegree);
			rootAttributes.put("DepartmentLevel", departmentLevel == null ? "" : departmentLevel);
			rootAttributes.put("ScoreShowRelatePlan", zeroToFalse(scoreShowRelatePlan));
			rootAttributes.put("ShowIndicatorDesc", zeroToFalse(showIndicatorDesc));
			rootAttributes.put("TotalAppFormula", totalAppFormula);
			rootAttributes.put("ShowOneMark", zeroToFalse(showOneMark));
			rootAttributes.put("IdioSummary", zeroToFalse(idioSummary));
			rootAttributes.put("ShowTotalScoreSort", zeroToFalse(showTotalScoreSort));
			rootAttributes.put("isShowSubmittedPlan", zeroToFalse(isShowSubmittedPlan));
			rootAttributes.put("ShowNoMarking", zeroToFalse(showNoMarking));
			rootAttributes.put("isEntireysub", zeroToFalse(isEntireysub));
			rootAttributes.put("ScoreBySumup", zeroToFalse(scoreBySumup));
			rootAttributes.put("isShowSubmittedScores", zeroToFalse(isShowSubmittedScores));
			rootAttributes.put("SelfScoreInDirectLeader", selfScoreInDirectLeader);
			rootAttributes.put("ScoreNumPerPage", scoreNumPerPage);
			rootAttributes.put("isShowOrder", zeroToFalse(isShowOrder));
			rootAttributes.put("AutoCalcTotalScoreAndOrder", zeroToFalse(autoCalcTotalScoreAndOrder));
			rootAttributes.put("PerSet", perSet == null ? "" : perSet);
			rootAttributes.put("PerSetShowMode", perSetShowMode);
			rootAttributes.put("PerSetStatMode", perSetStatMode);
			rootAttributes.put("StatCustomMode", zeroToFalse(statCustomMode));
			rootAttributes.put("StatStartDate", statStartDate);
			rootAttributes.put("StatEndDate", statEndDate);
			rootAttributes.put("WholeEval", zeroToFalse(wholeEval));
			rootAttributes.put("EvalClass", evalClass);
			rootAttributes.put("MustFillWholeEval", zeroToFalse(mustFillWholeEval));
			rootAttributes.put("NodeKnowDegree", zeroToFalse(nodeKnowDegree));
			rootAttributes.put("ShowAppraiseExplain", zeroToFalse(showAppraiseExplain));
			rootAttributes.put("GATIShowDegree", zeroToFalse(gatiShowDegree));
			rootAttributes.put("performanceType", performanceType);
			rootAttributes.put("MutiScoreGradeCtl", zeroToFalse(mutiScoreGradeCtl));
			rootAttributes.put("CheckGradeRange", checkGradeRange);
			rootAttributes.put("KeyEventEnabled", zeroToFalse(keyEventEnabled));
			rootAttributes.put("MitiScoreMergeSelfEval", zeroToFalse(mitiScoreMergeSelfEval));
			rootAttributes.put("NoteIdioGoal", zeroToFalse(noteIdioGoal));
			rootAttributes.put("SelfEvalNotScore", zeroToFalse(selfEvalNotScore));
			rootAttributes.put("DescriptiveWholeEval", zeroToFalse(descriptiveWholeEval));
			rootAttributes.put("PublicPointCannotEdit", zeroToFalse(publicPointCannotEdit));
			rootAttributes.put("TargetMakeSeries", targetMakeSeries);
			rootAttributes.put("TaskAdjustNeedNew", zeroToFalse(taskAdjustNeedNew));
			rootAttributes.put("TaskCanSign", zeroToFalse(taskCanSign));
			rootAttributes.put("TaskNeedReview", zeroToFalse(taskNeedReview));
			rootAttributes.put("ShowIndicatorContent", zeroToFalse(showIndicatorContent));
			rootAttributes.put("ShowIndicatorRole", zeroToFalse(showIndicatorRole));
			rootAttributes.put("ShowIndicatorDegree", zeroToFalse(showIndicatorDegree));
			rootAttributes.put("TargetAppMode", targetAppMode);
			rootAttributes.put("VerifyRule", verifyRule);
		//	rootAttributes.put("RelatingTargetCard", zeroToFalse(relatingTargetCard));
			rootAttributes.put("RelatingTargetCard", relatingTargetCard);
			rootAttributes.put("ShowYPTargetCard", showYPTargetCard);//郭峰新增
			rootAttributes.put("ShowDeductionCause", zeroToFalse(showDeductionCause));
			rootAttributes.put("EvalCanNewPoint", zeroToFalse(evalCanNewPoint));
			rootAttributes.put("TargetTraceEnabled", zeroToFalse(targetTraceEnabled));
			rootAttributes.put("TargetTraceItem", targetTraceItem);
			rootAttributes.put("TargetCollectItem", targetCollectItem);
			rootAttributes.put("TargetCalcItem", targetCalcItem);
			rootAttributes.put("TargetDefineItem", targetDefineItem);
			rootAttributes.put("MustFillCause", zeroToFalse(mustFillCause));
			rootAttributes.put("AllowAdjustEvalResult", zeroToFalse(allowAdjustEvalResult));
			rootAttributes.put("AdjustEvalRange", adjustEvalRange);
			rootAttributes.put("AdjustEvalDegreeType", adjustEvalDegreeType);
			rootAttributes.put("AdjustEvalDegreeNum", adjustEvalDegreeNum);
			rootAttributes.put("CalcMenScoreRefDept", zeroToFalse(calcMenScoreRefDept));
			rootAttributes.put("ShowGrpOrder", zeroToFalse(showGrpOrder));
			rootAttributes.put("VerifySameScore", zeroToFalse(verifySameScore));
			rootAttributes.put("ShowEvalDirector", zeroToFalse(showEvalDirector));
			rootAttributes.put("AdjustEvalGradeStep", adjustEvalGradeStep);
			rootAttributes.put("ScoreFromItem", zeroToFalse(scoreFromItem));
			rootAttributes.put("ReaderType", readerType);
			rootAttributes.put("BodysFromCard", zeroToFalse(bodysFromCard));
			rootAttributes.put("MenRefDeptTmpl", menRefDeptTmpl);
			rootAttributes.put("ObjsFromCard", zeroToFalse(objsFromCard));
			rootAttributes.put("ShowEmployeeRecord", zeroToFalse(showEmployeeRecord));
			rootAttributes.put("ShowDayWeekMonth", showDayWeekMonth);	
			rootAttributes.put("TaskSupportAttach", zeroToFalse(taskSupportAttach));
			rootAttributes.put("SpByBodySeq", zeroToFalse(spByBodySeq));
			rootAttributes.put("GradeByBodySeq", zeroToFalse(gradeByBodySeq));
			rootAttributes.put("AllowSeeAllGrade", zeroToFalse(allowSeeAllGrade));
			rootAttributes.put("PointEvalType", pointEvalType);
			rootAttributes.put("TargetMustFillItem", targetMustFillItem);
			rootAttributes.put("TargetUsePrevious", targetUsePrevious);
			rootAttributes.put("TaskNameDesc", taskNameDesc);
			rootAttributes.put("BasicInfoItem", basicInfoItem);
			rootAttributes.put("LockMGradeColumn", zeroToFalse(lockMGradeColumn));
			rootAttributes.put("ShowBasicInfo", zeroToFalse(showBasicInfo));
			rootAttributes.put("AllowUploadFile", zeroToFalse(allowUploadFile));
			rootAttributes.put("TargetCompleteThenGoOn", zeroToFalse(targetCompleteThenGoOn));
			rootAttributes.put("MutiScoreOnePageOnePoint", zeroToFalse(mutiScoreOnePageOnePoint));
			
			rootAttributes.put("GradeSameNotSubmit", zeroToFalse(gradeSameNotSubmit));
			rootAttributes.put("ShowHistoryScore", zeroToFalse(showHistoryScore));
			
			rootAttributes.put("MainbodyGradeCtl", mainbodybodyid);//强制分布主体类别
			rootAttributes.put("AllMainbodyGradeCtl", allmainbodybody);//强制分布主体类别
			rootAttributes.put("WholeEvalMode", wholeEvalMode);
			rootAttributes.put("BatchScoreImportFormula", batchScoreImportFormula);
			rootAttributes.put("DutyRule", dutyRule);
			

			
			
			// 取得BadlyMax和FineMax的属性
			HashMap fineAttributesMap = new HashMap();
			HashMap badlyAttributesMap = new HashMap();
			ArrayList badly_partRestricts = (ArrayList) this.getFormHM().get("Badly_partRestrict");
			ArrayList fine_partRestricts = (ArrayList) this.getFormHM().get("Fine_partRestrict");
			for (int i = 0; i < badly_partRestricts.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) badly_partRestricts.get(i);
				String point_id = (String) bean.get("point_id");
				String value = (String) bean.get("value");
				badlyAttributesMap.put(point_id, value);
			}
			for (int i = 0; i < fine_partRestricts.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) fine_partRestricts.get(i);
				String point_id = (String) bean.get("point_id");
				String value = (String) bean.get("value");
				fineAttributesMap.put(point_id, value);
			}

			// 评分说明必填高级规则
			String upIsValid = (String) this.getFormHM().get("upIsValid");
			String downIsValid = (String) this.getFormHM().get("downIsValid");
			String upDegreeId = (String) this.getFormHM().get("upDegreeId");
			String downDegreeId = (String) this.getFormHM().get("downDegreeId");
			String excludeDegree = (String) this.getFormHM().get("excludeDegree");
			String requiredFieldStr = (String) this.getFormHM().get("requiredFieldStr");
			requiredFieldStr = requiredFieldStr != null && !"".equals(requiredFieldStr) ? requiredFieldStr : "";
											
			ArrayList mustFillList = new ArrayList();	
			if((upIsValid!=null && upIsValid.trim().length()>0) && (downIsValid!=null && downIsValid.trim().length()>0))
			{
				LazyDynaBean up = new LazyDynaBean();
				up.set("Flag", "Up");
				up.set("IsValid", zeroToFalse(upIsValid));
				up.set("DegreeId", upDegreeId);
				mustFillList.add(up);
				
				LazyDynaBean down = new LazyDynaBean();
				down.set("Flag", "Down");
				down.set("IsValid", zeroToFalse(downIsValid));
				down.set("DegreeId", downDegreeId);
				mustFillList.add(down);

				LazyDynaBean exclude = new LazyDynaBean();
				exclude.set("Flag", "Exclude");
				exclude.set("IsValid", "True");
				if(excludeDegree==null)
					excludeDegree="";
				exclude.set("DegreeId", excludeDegree);
				mustFillList.add(exclude);
				
				LazyDynaBean requiredField = new LazyDynaBean();
				requiredField.set("Flag", "Required");
				requiredField.set("IsValid", "True");
				requiredField.set("PointId", requiredFieldStr);
				mustFillList.add(requiredField);
			}
			
			// 预警提醒设置
			String warnOpt1 = (String) this.getFormHM().get("warnOpt1");
			String warnOpt2 = (String) this.getFormHM().get("warnOpt2");
			String delayTime1 = (String) this.getFormHM().get("delayTime1");
			String delayTime2 = (String) this.getFormHM().get("delayTime2");
			String roleScope1 = (String) this.getFormHM().get("roleScope1");
			String roleScope2 = (String) this.getFormHM().get("roleScope2");
											
			ArrayList warnRoleScopeList = new ArrayList();	
			for(int i=0;i<2;i++)
			{
				LazyDynaBean bean = new LazyDynaBean();
				if(i==0 && (warnOpt1!=null && warnOpt1.trim().length()>0 && "true".equalsIgnoreCase(zeroToFalse(warnOpt1))))
				{
					bean.set("opt", "1");
					bean.set("delayTime", delayTime1);					
					if(roleScope1!=null && roleScope1.trim().length()>0 && roleScope1.indexOf("RL")!=-1)
	    			{
		    			String[] matters = roleScope1.split(",");
		    			StringBuffer roleName = new StringBuffer();
		    			for (int j = 0; j < matters.length; j++)
		    			{
		    				roleName.append(",");
		    				roleName.append(matters[j].substring(2));			    				
		    			}
		    			roleScope1 = roleName.toString().substring(1);
	    			}					
					bean.set("roleScope", roleScope1);
					warnRoleScopeList.add(bean);
					
				}else if(i==1 && (warnOpt2!=null && warnOpt2.trim().length()>0 && "true".equalsIgnoreCase(zeroToFalse(warnOpt2))))
				{
					bean.set("opt", "2");
					bean.set("delayTime", delayTime2);					
					if(roleScope2!=null && roleScope2.trim().length()>0 && roleScope2.indexOf("RL")!=-1)
	    			{
		    			String[] matters = roleScope2.split(",");
		    			StringBuffer roleName = new StringBuffer();
		    			for (int j = 0; j < matters.length; j++)
		    			{
		    				roleName.append(",");
		    				roleName.append(matters[j].substring(2));			    				
		    			}
		    			roleScope2 = roleName.toString().substring(1);
	    			}					
					bean.set("roleScope", roleScope2);
					warnRoleScopeList.add(bean);
				}
			}				
			
			String evaluate_str=(String)this.getFormHM().get("evaluate_str");
			String blind_point=(String)this.getFormHM().get("blind_point");
			this.getFormHM().put("evaluate_str", "");//清空form中的这两项  防止新增计划等  初始时从form中取值 影响展现   zhaoxg add 2014-6-26
			this.getFormHM().put("blind_point", "0");
			
			HashMap evaluate_map=new HashMap();
			if(evaluate_str!=null&&evaluate_str.trim().length()>0)
			{
				evaluate_map.put("evaluate_str", evaluate_str);
				if(blind_point!=null)
					evaluate_map.put("blind_point", blind_point);
				else
					evaluate_map.put("blind_point", "0");
			}
			else
				evaluate_map=null; 
			LoadXml parameter_content = new LoadXml();
			parameter_content.saveAttributes(this.getFrameconn(), rootAttributes, fineAttributesMap, badlyAttributesMap, mustFillList, warnRoleScopeList, evaluate_map, planId);
			// 加载动态参数
			LoadXml loadxml = new LoadXml(this.frameconn, planId);
			BatchGradeBo.getPlanLoadXmlMap().put(planId, loadxml);
			String byModel = (String) this.getFormHM().get("byModel");
			String updateSql = "update per_plan set ByModel = "+Integer.valueOf(byModel)+" where plan_id = "+planId;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.update(updateSql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		} else
		// 详细信息页面 修改一些内存中发生变化的参数变量
		{	
			String e_str="";//360考核结果显示项
			String o_str="";//目标考核结果显示项
		
			o_str = (String)this.getFormHM().get("o_str");
			e_str = (String)this.getFormHM().get("e_str");
			

			e_str=e_str==null|| "".equals(e_str.trim())?"":(","+e_str+",");
			o_str=o_str==null|| "".equals(o_str.trim())?"":(","+o_str+",");
			ArrayList evaluateList=ConfigParamBo.getConfigDrawList(1,e_str);
			ArrayList objectiveList = ConfigParamBo.getConfigDrawList(2,o_str);
			
			this.getFormHM().put("e_str",e_str);
			this.getFormHM().put("o_str", o_str);
			this.getFormHM().put("evaluateList", evaluateList);
			this.getFormHM().put("objectiveList", objectiveList);
			
		}
		

			

	}

	public String zeroToFalse(String str)
	{
		if ("0".equals(str))
			return "False";
		else if ("1".equals(str))
			return "True";
		else
			return str;
	}
	
	
	/**
	 * 是否包含直属领导
	 * @param bodyids
	 * @return
	 * @author zhanghua
	 * @date 2017年11月11日
	 */
	private boolean isHaveLevelOne(String[] bodyids){
		boolean ishave=false;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer strSql=new StringBuffer("");
			for(String str :bodyids){
				String [] list=str.split("/");
				if(list.length>0){
					strSql.append(list[0]+",");
				}
			}
			String level="level";
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				level="level_o";
			if(strSql.length()==0)
				ishave= false;
			else{
				strSql.deleteCharAt(strSql.length()-1);
				RowSet rs=dao.search("select 1 from per_mainbodyset where "+level+"=1 and body_id in ( "+strSql.toString()+")");
				if(rs.next()){
					ishave= true;
				}else
					ishave= false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ishave;
		
	}
	
	
	
}
