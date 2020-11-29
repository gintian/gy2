package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:AddExamPlanTrans.java</p>
 * <p>Description:考核计划添加和编辑初始化类(包括初始化部分参数设置)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class AddExamPlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		String orgCode = (String) this.getFormHM().get("a_code");
		// 过虑机构编码前面的字母标识
		if (orgCode != null && orgCode.length() > 1)
			orgCode = orgCode.substring(2, orgCode.length());
		else if (orgCode == null)
			orgCode = "";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		// 如果为编辑,planId能取到值
		String planId = (String) hm.get("planId");
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, planId)){
        	return;
        }
		hm.remove("planId");

		//设置滚动条的位置   2013.12.24 pjf
		String scrollValue = (String) hm.get("scrollValue");
		this.getFormHM().put("scrollValue", scrollValue);
		hm.remove("scrollValue");

		String scrollTopValue = (String) hm.get("scrollTopValue");
		this.getFormHM().put("scrollTopValue", scrollTopValue);
		hm.remove("scrollTopValue");

		String creator = this.getUserView().getUserName();
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		RecordVo vo = new RecordVo("per_plan");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if (planId == null || "".equals(planId) || planId.trim().length()<=0)
			{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				planId = idg.getId("per_plan.plan_id");
				Integer planid = new Integer(planId);
				vo.setString("plan_id", planid.toString());
				vo.setString("status", "0");

				vo.setString("name", "");
				vo.setString("plan_type", "1");// 默认为记名

				if ("".equals(orgCode))
					orgCode = "HJSJ";
				vo.setString("b0110", orgCode);

				vo.setString("object_type", "2");// 默认为人员

				vo.setString("cycle", "3");// 默认为月度(当前年的一月份)
				vo.setString("theyear", creatDate.substring(0, 4));
				vo.setString("themonth", "01");
				vo.setString("thequarter", "");
				vo.setString("start_date", "");
				vo.setString("end_date", "");

				vo.setString("gather_type", "0");// 默认为网上
				vo.setString("method", "1");
				vo.setString("template_id", "");

				vo.setString("agree_user", "");
				vo.setString("parameter_content", "");

				vo.setString("agree_date", "");
				vo.setString("approve_result", "");

				vo.setString("agree_idea", "");
				vo.setString("descript", "");

				vo.setString("target", "");
				vo.setString("content", "");

				vo.setString("flow", "");
				vo.setString("result", "");
				vo.setString("plan_visibility", "0");

				vo.setString("create_user", creator);
				vo.setDate("create_date", creatDate);

				// 单位名称用于在添加页面显示中文名称
				String codeName = AdminCode.getCodeName("UN", orgCode);
				if (codeName == null || codeName.length() == 0)
					codeName = AdminCode.getCodeName("UM", orgCode);
				if ("HJSJ".equals(orgCode))
					codeName = "公共资源";

				this.getFormHM().put("codeName", codeName);
				this.getFormHM().put("statusName", "起草");
				this.getFormHM().put("templateName", "");
				// 设置默认参数
				setPlanParameters("","2","");
				this.getFormHM().put("plan_visibility", "false");
				this.getFormHM().put("evaluate_str", "");//清空form中的这两项  防止新增计划等  初始时从form中取值 影响展现   zhaoxg add 2014-6-26
				this.getFormHM().put("blind_point", "0");
				bo.deleteTemp("t#des_review");
			} else
			{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select * from per_plan where plan_id=");
				strsql.append(planId);
				String objecttype="";
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next())
				{
					objecttype=this.frowset.getString("object_type");
					vo.setString("plan_id", this.frowset.getString("plan_id"));
					vo.setString("status", this.frowset.getString("status"));
					vo.setString("name", this.frowset.getString("name"));
					vo.setString("plan_type", this.frowset.getString("plan_type"));
					String method= "2".equals(isNull(this.frowset.getString("method"))) ? "2" : "1";
					vo.setString("method",method);

					vo.setString("cycle", this.frowset.getString("cycle"));
					vo.setString("gather_type", this.frowset.getString("gather_type"));
					vo.setString("template_id", this.frowset.getString("template_id"));
					vo.setString("agree_user", this.frowset.getString("agree_user"));

					vo.setString("parameter_content", this.frowset.getString("parameter_content"));
					// String tempDate = this.frowset.getString("agree_date");
					// if (tempDate != null && tempDate.length() > 9)
					// vo.setString("agree_date", tempDate.substring(0, 10));
					vo.setDate("agree_date", this.frowset.getDate("agree_date"));

					vo.setString("approve_result", this.frowset.getString("approve_result"));
					vo.setString("agree_idea", this.frowset.getString("agree_idea"));

					orgCode = this.frowset.getString("b0110");
					vo.setString("b0110", orgCode);

					vo.setString("object_type", this.frowset.getString("object_type"));
					vo.setString("descript", this.frowset.getString("descript"));
					vo.setString("target", this.frowset.getString("target"));
					vo.setString("content", this.frowset.getString("content"));
					vo.setString("flow", this.frowset.getString("flow"));

					vo.setString("result", this.frowset.getString("result"));
					vo.setString("create_user", this.frowset.getString("create_user"));
					// tempDate = this.frowset.getString("create_date");
					// if (tempDate != null && tempDate.length() > 9)
					// vo.setString("create_date", tempDate.substring(0, 10));
					vo.setDate("create_date", this.frowset.getDate("create_date"));

					vo.setString("theyear", this.frowset.getString("theyear"));
					vo.setString("themonth", this.frowset.getString("themonth"));
					vo.setString("thequarter", this.frowset.getString("thequarter"));
					// vo.setString("start_date", PubFunc.DoFormatDate(isNull(this.frowset.getString("start_date")).length()>10?this.frowset.getString("start_date").substring(0, 10):""));
					// vo.setString("end_date", PubFunc.DoFormatDate(isNull(this.frowset.getString("end_date")).length()>10?this.frowset.getString("end_date").substring(0, 10):""));
					vo.setDate("start_date", this.frowset.getDate("start_date"));
					vo.setDate("end_date", this.frowset.getDate("end_date"));
					
					String plan_visibility =  this.frowset.getString("plan_visibility")==null?"0": this.frowset.getString("plan_visibility");
					this.getFormHM().put("plan_visibility", "1".equals(plan_visibility)?"true":"false");
					
					String codeName = AdminCode.getCodeName("UN", orgCode);
					if (codeName == null || codeName.length() == 0)
						codeName = AdminCode.getCodeName("UM", orgCode);
					if ("HJSJ".equals(orgCode))
						codeName = "公共资源";

					this.getFormHM().put("codeName", codeName);
					this.getFormHM().put("statusName", this.getStatusName(isNull(this.frowset.getString("status"))));
					this.getFormHM().put("templateName", this.getTemplateName(isNull(this.frowset.getString("template_id"))));
					
					setPlanParameters(planId,objecttype,method);
				}
			}
//			this.getFormHM().put("taskNameDesc","");
			this.getFormHM().put("planSelect", new Integer(planId).toString());

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally
		{
			this.getFormHM().put("khplanvo", vo);
		}

	}

	public String getTemplateName(String templateId)
	{
		String name = "";
		if (templateId == null && "".equals(templateId))
			return name;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search("select template_id,name from per_template where template_id='" + templateId + "'");
			if (this.frowset.next())
				name = this.frowset.getString("name");

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return name;
	}

	public String isNull(String str)
	{
		if (str == null)
			str = "";
		return str;
	}

	public String getStatusName(String statusCode)
	{
		String status = ResourceFactory.getProperty("hire.jp.pos.draftout");
		if ("1".equals(statusCode))
			status = ResourceFactory.getProperty("info.appleal.state1");
		else if ("2".equals(statusCode))
			status = ResourceFactory.getProperty("label.hiremanage.status3");
		else if ("3".equals(statusCode))
			status = ResourceFactory.getProperty("button.issue");
		else if ("4".equals(statusCode))
			status = ResourceFactory.getProperty("gz.formula.implementation");
		else if ("5".equals(statusCode))
			status = ResourceFactory.getProperty("lable.performance.status.pause");
		else if ("6".equals(statusCode))
			status = ResourceFactory.getProperty("jx.khplan.Appraisal");
		else if ("7".equals(statusCode))
			status = ResourceFactory.getProperty("label.hiremanage.status6");
		else if ("8".equals(statusCode))
			status = ResourceFactory.getProperty("performance.plan.distribute");
		return status;
	}

	/**
	 * 初始化计划参数 如果planId为空串取默认参数设置
	 * 
	 * @throws GeneralException
	 */
	public void setPlanParameters(String planId,String object_type,String method) throws GeneralException
	{
		LoadXml loadxml = new LoadXml(this.getFrameconn(), planId);
		Hashtable params = loadxml.getDegreeWhole();

		ExamPlanBo bo = new ExamPlanBo(planId,this.frameconn);
		
		String template_id = "";
		if(planId.trim().length()>0)
			template_id=bo.getPerPlanVo(planId).getString("template_id");
		this.getFormHM().put("tempTemplateId",template_id);
		
		// 对于考核主体类别的设置 要受到detail页面考核对象类型的限制 所以不在此初始化		
		ArrayList setlist = new ArrayList();
		this.getFormHM().put("MainbodyTypeList", setlist);
		// 对于模板类型要受到选择的模板编号的限制 所以不在此初始化
		String templateType = bo.getTemplateType(template_id);
	    this.getFormHM().put("templateType", templateType);	
		// 标准标度
		String busitype = (String) this.getFormHM().get("busitype");	    
	    String per_comTable = "per_grade_template"; // 绩效标准标度
		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			per_comTable = "per_grade_competence"; // 能力素质标准标度	    
		ArrayList grade_template = bo.getGradeTemplate(per_comTable);
		this.getFormHM().put("grade_template", grade_template);
		
		ArrayList departmentLeveList = bo.getDepartmentLeveList();
		this.getFormHM().put("departmentLeveList", departmentLeveList);
		
		/*
		 * 打分控制
		 */
		 //部分指标分别设置初始化
		ArrayList badly_partRestrict = new ArrayList();
		ArrayList fine_partRestrict = new ArrayList();
		if(planId.trim().length()>0)
		{
			String parameter_content = bo.getParameter_content(planId);			
			try
			{
				if (bo.isExists(parameter_content, "BadlyMax"))
					badly_partRestrict = bo.getRestrictList(parameter_content, "BadlyMax",template_id);
				else
					badly_partRestrict = bo.notExists(template_id);

				if (bo.isExists(parameter_content, "FineMax"))
					fine_partRestrict = bo.getRestrictList(parameter_content, "FineMax",template_id);
				else
					fine_partRestrict = bo.notExists(template_id);

			} catch (Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}			
		}
		String gradeSameNotSubmit = (String)params.get("GradeSameNotSubmit");
		this.getFormHM().put("gradeSameNotSubmit", gradeSameNotSubmit);
		String showHistoryScore = (String)params.get("ShowHistoryScore");
		this.getFormHM().put("showHistoryScore", showHistoryScore);
		this.getFormHM().put("mainbodybodyid", params.get("MainbodyGradeCtl"));
		this.getFormHM().put("allmainbodybody", params.get("AllMainbodyGradeCtl"));
		this.getFormHM().put("wholeEvalMode", params.get("WholeEvalMode"));//总体评价录分方式0：录入等级1：录入分值
		String showDayWeekMonth = (String)params.get("ShowDayWeekMonth");
		String batchScoreImportFormula = (String)params.get("BatchScoreImportFormula");
		if(method!=null&&method.length()>0){
			String evaluate_str="";//考核结果显示项	 zhaoxg add 2014-6-25 慧聪网需求
			evaluate_str = (String)params.get("evaluate_str");			
			evaluate_str=evaluate_str==null|| "".equals(evaluate_str.trim())?"":(","+evaluate_str+",");
			ArrayList evaluateList=ConfigParamBo.getConfigDrawList(Integer.parseInt(method),evaluate_str);			
			this.getFormHM().put("evaluate_str",evaluate_str);
			this.getFormHM().put("evaluateList", evaluateList); 			
			String blind_point=(String)params.get("blind_point");		
			this.getFormHM().put("blind_point", blind_point);
		}

		
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
		//按岗位素质模型
		String byModel = bo.getByModelById(planId);
		if("0".equals(byModel))
			byModel="False";
		this.getFormHM().put("byModel", byModel);
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
		this.getFormHM().put("allowUploadFile", params.get("AllowUploadFile"));
		this.getFormHM().put("targetCompleteThenGoOn", params.get("TargetCompleteThenGoOn"));
		this.getFormHM().put("mutiScoreOnePageOnePoint", params.get("MutiScoreOnePageOnePoint"));
		// 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
		this.getFormHM().put("scoreWay", params.get("scoreWay"));
		this.getFormHM().put("blankScoreUseDegree", params.get("BlankScoreUseDegree"));
		this.getFormHM().put("departmentLevel", params.get("DepartmentLevel"));
		/*
		 * BS控制
		 */
		if("2".equals(object_type))
		{
			ArrayList itemfilterlist = bo.getItemFilterList();
			this.getFormHM().put("itemlist", itemfilterlist);
			
		}else
		{
			ArrayList itemfilterlist = bo.getItemFilterDWList();
			this.getFormHM().put("itemlist", itemfilterlist);
		}
		
		// 评分说明必填高级规则
		ArrayList mustFillOptionsList = (ArrayList)params.get("MustFillOptionsList");			
		String upIsValid = "";
		String downIsValid = "";
		String upDegreeId = "";
		String downDegreeId = "";
		// 计划明细页需要加载必填指标和不打分标度 add by 刘蒙
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
	    		}   		   		   		
	    		else if (flag!=null && flag.trim().length()>0 && "exclude".equalsIgnoreCase(flag)) {
	    			excludeDegree = (String) bean.get("DegreeId");
	    		} else if (flag!=null && flag.trim().length()>0 && "required".equalsIgnoreCase(flag)) {
	    			requiredFieldStr = (String) bean.get("PointId");
	    		}
	    	}
		}							
		this.getFormHM().put("upIsValid", upIsValid);
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
		this.getFormHM().put("perSet", params.get("PerSet"));
		this.getFormHM().put("perSetShowMode", params.get("PerSetShowMode"));
		this.getFormHM().put("perSetStatMode", params.get("PerSetStatMode"));
		this.getFormHM().put("statCustomMode", params.get("StatCustomMode"));
		this.getFormHM().put("statStartDate", params.get("StatStartDate"));
		this.getFormHM().put("statEndDate", params.get("StatEndDate"));
		this.getFormHM().put("mutiScoreGradeCtl", params.get("MutiScoreGradeCtl"));
		this.getFormHM().put("mitiScoreMergeSelfEval", params.get("mitiScoreMergeSelfEval"));
		this.getFormHM().put("checkGradeRange", params.get("CheckGradeRange"));
		this.getFormHM().put("noteIdioGoal", params.get("noteIdioGoal"));
		this.getFormHM().put("selfEvalNotScore", params.get("SelfEvalNotScore"));
		
		if(params.get("PerSet")!=null && params.get("PerSet").toString().trim().length()>0)
			this.getFormHM().put("performanceDate", "True");
		else
			this.getFormHM().put("performanceDate", "False");
		
		this.getFormHM().put("showIndicatorContent", params.get("showIndicatorContent"));
		this.getFormHM().put("showIndicatorRole", params.get("showIndicatorRole"));
		this.getFormHM().put("showIndicatorDegree", params.get("showIndicatorDegree"));
		this.getFormHM().put("relatingTargetCard", params.get("relatingTargetCard"));
		String relatingTargetCard =params.get("relatingTargetCard")==null?"":(String)params.get("relatingTargetCard");
		String showYPTargetCard = (String)params.get("showYPTargetCard");
		if(!"2".equals(relatingTargetCard)){
			showYPTargetCard = "False";
		}
		this.getFormHM().put("showYPTargetCard", showYPTargetCard);
		this.getFormHM().put("showDeductionCause", params.get("showDeductionCause"));
		this.getFormHM().put("mustFillCause", params.get("MustFillCause"));
		this.getFormHM().put("canSaveAllObjsScoreSame", params.get("CanSaveAllObjsScoreSame"));
		this.getFormHM().put("showSumRow", params.get("ShowSumRow"));
		this.getFormHM().put("showBasicInfo", params.get("ShowBasicInfo"));
		this.getFormHM().put("lockMGradeColumn", params.get("LockMGradeColumn"));
		this.getFormHM().put("basicInfoItem", params.get("BasicInfoItem"));
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
		this.getFormHM().put("mustFillWholeEval", params.get("MustFillWholeEval"));
		this.getFormHM().put("nodeKnowDegree", params.get("NodeKnowDegree"));
		this.getFormHM().put("showAppraiseExplain", params.get("showAppraiseExplain"));
		this.getFormHM().put("gatiShowDegree", params.get("GATIShowDegree"));
		this.getFormHM().put("performanceType", params.get("performanceType"));
		this.getFormHM().put("descriptiveWholeEval", params.get("DescriptiveWholeEval"));
		/*
		 * 目标管理
		 */
		this.getFormHM().put("spByBodySeq", params.get("SpByBodySeq"));
		this.getFormHM().put("gradeByBodySeq", params.get("GradeByBodySeq"));
		this.getFormHM().put("allowSeeAllGrade", params.get("AllowSeeAllGrade"));
		this.getFormHM().put("pointEvalType", params.get("PointEvalType"));
		this.getFormHM().put("taskSupportAttach", params.get("TaskSupportAttach"));
		this.getFormHM().put("showEmployeeRecord", params.get("ShowEmployeeRecord"));
		this.getFormHM().put("bodysFromCard", params.get("BodysFromCard"));
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
		this.getFormHM().put("showLeaderEval", params.get("ShowLeaderEval"));
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
		// 目标卡参数
		String targetItem = (String)params.get("TargetItem");
		String targetCalcItems = (String) params.get("TargetCalcItem");
		String targetTraceItems = (String) params.get("TargetTraceItem");
		String targetCollectItems = (String) params.get("TargetCollectItem");
		String targetDefineItem = (String) params.get("TargetDefineItem");
		String targetMustFillItem = (String)params.get("TargetMustFillItem");
		String targetUsePrevious = (String)params.get("TargetUsePrevious");
	
		ArrayList targetDefineItemList = bo.getTargetDefineItemList(targetDefineItem,"0");
		ArrayList targetCollectItemList = new ArrayList();
		ArrayList targetTraceItemList =  new ArrayList();
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
			targetDefineItem=targetDefineItem.length()>0?targetDefineItem.substring(1):targetDefineItem;
			targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
			targetMustFillItemList = bo.getTargetItemList(targetDefineItem,targetMustFillItem,"0");
			targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
			
		}else if(",".equals(targetDefineItem))//客户将目标卡指标一个也不选点击了保存按钮
		{
			
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
		}else
		{
			targetCollectItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetTraceItemList = bo.getTargetItemList(targetDefineItem,targetCollectItems,"0");
			targetCalcItemList = bo.getComputeItemList(targetItem,targetDefineItem,targetCalcItems);
			targetMustFillItemList = bo.getTargetItemList(targetDefineItem,targetMustFillItem,"0");
			targetUsePreviousList = bo.getTargetItemList(targetDefineItem,targetUsePrevious,"0");
			
		}
				
		this.getFormHM().put("targetCalcItemList", targetCalcItemList);
		this.getFormHM().put("targetCollectItemList", targetCollectItemList);
		this.getFormHM().put("targetTraceItemList", targetTraceItemList);
		this.getFormHM().put("targetDefineItemList", targetDefineItemList);
		this.getFormHM().put("targetMustFillItemList", targetMustFillItemList);
		this.getFormHM().put("targetUsePreviousList", targetUsePreviousList);
		this.getFormHM().put("targetItem", "");
		this.getFormHM().put("targetCalcItem", params.get("TargetCalcItem"));
		this.getFormHM().put("targetTraceItem", params.get("TargetTraceItem"));
		this.getFormHM().put("targetCollectItem", params.get("TargetCollectItem"));
		this.getFormHM().put("targetMustFillItem", params.get("TargetMustFillItem"));
		this.getFormHM().put("targetUsePrevious", params.get("TargetUsePrevious"));				
		this.getFormHM().put("targetDefineItem", params.get("TargetDefineItem"));

		this.getFormHM().put("evalOutLimitStdScore", params.get("EvalOutLimitStdScore"));
		this.getFormHM().put("evalOutLimitScoreOrg", params.get("EvalOutLimitScoreOrg"));
		this.getFormHM().put("showLeaderEval", params.get("ShowLeaderEval"));
		this.getFormHM().put("processNoVerifyAllScore", params.get("ProcessNoVerifyAllScore"));
		this.getFormHM().put("verifyRule", params.get("VerifyRule"));
		this.getFormHM().put("showBackTables", params.get("ShowBackTables"));
		this.getFormHM().put("taskNameDesc",params.get("TaskNameDesc"));
		
		String dutyRule = (String) params.get("DutyRule");
		if(dutyRule!=null&&dutyRule.length()>0){
			this.getFormHM().put("dutyRuleid", "True");
		}else{
			this.getFormHM().put("dutyRuleid", "False");
		}
		this.getFormHM().put("dutyRule", dutyRule);
		//初始化计划的主体类别
		String bodyTypeIds = bo.getBodyTypeIds(planId);
		this.getFormHM().put("bodyTypeIds", bodyTypeIds);
	
		
	}
}
