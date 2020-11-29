package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:SaveExamPlanTrans.java</p>
 * <p>Description:保存考核计划交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class SaveExamPlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		
		RecordVo votemp = (RecordVo) this.getFormHM().get("khplanvo");
		String name = votemp.getString("name");
		if(name!=null && name.trim().length()>0 && name.indexOf("+")!=-1)	
		{
			StringBuffer result = new StringBuffer(name.length());
	        for (int i=0; i<name.length(); ++i) {
	            switch (name.charAt(i)) {		     
		            case '+':
		                result.append("＋");
		                break;
		            default:
		                result.append(name.charAt(i));
		                break;
	            }    
	        }	        	    
	        name = result.toString(); 			
	        name = name.replaceAll("%2B","＋").replaceAll("%2b","＋"); 
		}
		String planId = votemp.getString("plan_id");
		String status = votemp.getString("status");
		if ("起草".equals(status))
			status = "0";
		String plan_type = votemp.getString("plan_type");
		String cycle = votemp.getString("cycle");//考核周期:(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
		String gather_type = votemp.getString("gather_type");
		String method = votemp.getString("method");
		String template_id = votemp.getString("template_id");
		String object_type = votemp.getString("object_type");
		String agree_user = votemp.getString("agree_user");
		// String parameter_content = votemp.getString("parameter_content");
		String agree_date = votemp.getString("agree_date");
		String approve_result = votemp.getString("approve_result");
		String agree_idea = votemp.getString("agree_idea");
		String descript = votemp.getString("descript");
		String target = votemp.getString("target");
		String content = votemp.getString("content");
		String flow = votemp.getString("flow");
		String result = votemp.getString("result");
		String create_user = votemp.getString("create_user");
		String create_date = votemp.getString("create_date");
		String b0110 = votemp.getString("b0110");
		String theyear = votemp.getString("theyear");
		String themonth = votemp.getString("themonth");
		String thequarter = votemp.getString("thequarter");
		String start_date = votemp.getString("start_date");
		String end_date = votemp.getString("end_date");
		String plan_visibility = (String) this.getFormHM().get("plan_visibility");
		String byModel = falseToZero( ( (String) this.getFormHM().get("byModel") ) );
		
		String busitype=(String)this.getFormHM().get("busitype"); // 业务分类 =0(绩效考核); =1(能力素质)
		
		/**除了不定期计划  start_date、end_date均为空**/
		if(!"7".equals(cycle)){
			start_date="";
			end_date="";
		}else{
			//校验日期的有效性
			start_date = start_date.replaceAll("\\.", "-");
			end_date = end_date.replaceAll("\\.", "-");
			if(!validateDate(start_date)|| !validateDate(end_date)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("jx.hjplan.khqujianerror")));
			}
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", planId);
		vo.setString("status", status);
		vo.setString("name", name);
		vo.setString("plan_type", plan_type);
		vo.setString("b0110", b0110);
		vo.setString("object_type", object_type);
		vo.setString("method", method);
		vo.setString("cycle", cycle);
		vo.setString("gather_type", gather_type);

		vo.setString("template_id", template_id);

		vo.setString("agree_user", agree_user);
		// vo.setString("parameter_content", parameter_content);

		if (!"".equals(agree_date))
			vo.setDate("agree_date", agree_date);

		vo.setString("approve_result", approve_result);

		vo.setString("agree_idea", agree_idea);
		vo.setString("descript", descript);

		vo.setString("target", target);
		vo.setString("content", content);

		vo.setString("flow", flow);
		vo.setString("result", result);

		vo.setString("create_user", create_user);
		vo.setDate("create_date", create_date);

		vo.setString("theyear", theyear);
		vo.setString("themonth", themonth);
		vo.setString("thequarter", thequarter);
		vo.setDate("start_date", start_date);
		vo.setDate("end_date", end_date);
		vo.setString("plan_visibility", plan_visibility);
		vo.setInt("bymodel", Integer.valueOf(byModel).intValue());
		vo.setString("busitype", busitype);
		

		ExamPlanBo bo = new ExamPlanBo(this.getFrameconn());
		try
		{
			boolean idEdit = false;
			if (bo.isExist(planId))// 更新操作
			{
				dao.updateValueObject(vo);
				StringBuffer context = new StringBuffer();
				context.append("更新计划：【"+planId+":"+name+"】<br>");
				this.getFormHM().put("@eventlog", context.toString());
				idEdit = true;
			} else
			// 新增操作
			{
				dao.addValueObject(vo);
				StringBuffer context = new StringBuffer();
				context.append("新增计划：【"+planId+":"+name+"】<br>");
				this.getFormHM().put("@eventlog", context.toString());
				String sql = "update per_plan set a0000=a0000+1  where a0000 is not null";

				dao.update(sql);
				sql = "update per_plan set a0000=((select min(a0000)-1 from per_plan))  where plan_id=" + planId;
				dao.update(sql);
			}

			// 更新计划参数字段
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			this.getFormHM().put("templateName", this.getTemplateName(template_id));

			updateParameter_content(hm, planId, gather_type);

			if (idEdit)// 更新操作
			{
				// 在此考虑一种情况 假设计划是人员计划 且已经具备了人员的考核对象 修改考核对象类别为非人员 那么需要将该计划的考核对象和主体都删掉
				boolean delFlag = false;
				String sql = "select * from organization where codeitemid in (select object_id from per_object where plan_id=" + planId + ")";
				this.frowset = dao.search(sql);
				if (this.frowset.next())// 非人员的考核对象
				{
					if ("2".equals(object_type))
					{
						delFlag = true;
						dao.delete("delete from per_plan_body where body_id=-1 and plan_id=" + planId, new ArrayList());// 删掉团队负责人的主体类别
					}						
				}
				sql = "select * from usra01 where a0100 in (select object_id from per_object where plan_id=" + planId + ")";
				this.frowset = dao.search(sql);
				if (this.frowset.next())// 人员的考核对象
				{
					if (!"2".equals(object_type))
					{
						delFlag = true;
						dao.delete("delete from per_plan_body where body_id=5 and plan_id=" + planId, new ArrayList());// 删掉本人的主体类别
//						 由于目标计划目前要求必须选择团队负责人 所以在此要加上
/*						if (method.equals("2"))// 目标管理计划
						{
							String insertSql = "insert into per_plan_body(body_id,rank,plan_id) values (-1,0.0," + planId + ")";
							dao.insert(insertSql, new ArrayList());
						}
*/					}					
				}
				if (delFlag)// 删除考核对象及相关的一系列数据
				{
					dao.delete("delete from per_object where plan_id=" + planId, new ArrayList());
					dao.delete("delete from per_mainbody where plan_id=" + planId, new ArrayList());
					DbWizard dbWizard = new DbWizard(this.getFrameconn());
					if (dbWizard.isExistTable("per_pointpriv_" + planId, false))
						dao.delete("delete from per_pointpriv_" + planId, new ArrayList());
					if (dbWizard.isExistTable("per_table_" + planId, false))
						dao.delete("delete from per_table_" + planId, new ArrayList());
					if (dbWizard.isExistTable("per_result_" + planId, false))
						dao.delete("delete from per_result_" + planId, new ArrayList());
					if (dbWizard.isExistTable("per_gather_score_" + planId, false))// 业绩数据录入里用到的表
						dao.delete("delete from per_gather_score_" + planId, new ArrayList());
					if (dbWizard.isExistTable("per_gather_" + planId, false))
						dao.delete("delete from per_gather_" + planId, new ArrayList());

					dao.delete("DELETE FROM per_interview WHERE plan_id = " + planId + " AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = " + planId + "))", new ArrayList());
					/** fzg add */
					dao.delete("DELETE FROM per_target_evaluation WHERE plan_id = " + planId + " AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = " + planId + "))",
							new ArrayList());
					dao.delete("DELETE FROM per_article WHERE plan_id = " + planId + " AND NOT (A0100  IN (SELECT object_id FROM per_object WHERE plan_id = " + planId + "))", new ArrayList());
					if ("2".equals(method))// 目标管理计划
					{
						dao.delete("DELETE FROM P04 WHERE plan_id = " + planId + " AND NOT (A0100 IN (SELECT object_id FROM per_object WHERE plan_id = " + planId + "))", new ArrayList());
						if (dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
							dao.delete("DELETE FROM PER_ITEMPRIV_" + planId + " where object_id not in (SELECT object_id FROM per_object WHERE plan_id = " + planId + ")", new ArrayList());
					}
					// 删除动态项目权重表的数据
					sql = "DELETE FROM  per_dyna_item where plan_id=" + planId + " and body_id not in (select body_id from per_object where plan_id = " + planId + ")";
					dao.delete(sql, new ArrayList());
				}
			}

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 校验日期是否正确
	 * @return
	 */
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = formatter.parse(datestr);
			return datestr.equals(formatter.format(date));
		} catch (Exception e) {
			bflag = false;
		}
		return bflag;
	}
	// 检查参数设置中考核主体类别和考核对象类别设置的是否一致。以防止保存过的计划修改考核对象类别后没有手动调整参数设置中的主体类别
	public void testMainBodyType(String planId, String object_type)
	{
		try
		{
			HashMap map = new HashMap();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql = "select * from per_plan_body where plan_id=" + planId + " and body_id in (-1,5)";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
				map.put(this.frowset.getString("body_id"), "");

			DbWizard dbWizard = new DbWizard(this.frameconn);
			if ("2".equals(object_type) && map.get("-1") != null)
			{
				dao.delete("delete from per_plan_body where plan_id=" + planId + " and body_id=-1", new ArrayList());

				if (dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
					dao.delete("delete from PER_ITEMPRIV_" + planId + " where body_id=-1", new ArrayList());

				ArrayList list = new ArrayList();
				sql = "select object_id,mainbody_id from per_mainbody where plan_id=" + planId + " and body_id=-1";
				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					ArrayList list1 = new ArrayList();
					list1.add(this.frowset.getString(1));
					list1.add(this.frowset.getString(2));
					list.add(list1);
				}
				if (dbWizard.isExistTable("PER_POINTPRIV_" + planId, false))
					dao.batchUpdate("delete from PER_POINTPRIV_" + planId + " where object_id=? and mainbody_id=?", list);

				dao.delete("delete from per_mainbody where plan_id=" + planId + " and body_id=-1", new ArrayList());

			} else if (!"2".equals(object_type) && map.get("5") != null)
			{
				dao.delete("delete from per_plan_body where plan_id=" + planId + " and body_id=5", new ArrayList());

				if (dbWizard.isExistTable("PER_ITEMPRIV_" + planId, false))
					dao.delete("delete from PER_ITEMPRIV_" + planId + " where body_id=5", new ArrayList());

				ArrayList list = new ArrayList();
				sql = "select object_id,mainbody_id from per_mainbody where plan_id=" + planId + " and body_id=5";
				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					ArrayList list1 = new ArrayList();
					list1.add(this.frowset.getString(1));
					list1.add(this.frowset.getString(2));
					list.add(list1);
				}
				if (dbWizard.isExistTable("PER_POINTPRIV_" + planId, false))
					dao.batchUpdate("delete from PER_POINTPRIV_" + planId + " where object_id=? and mainbody_id=?", list);

				dao.delete("delete from per_mainbody where plan_id=" + planId + " and body_id=5", new ArrayList());

			}

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public String getTemplateName(String templateId)
	{
		String name = "";
		if (templateId == null && "".equals(templateId))
			return name;

		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search("select template_id,name from per_template where template_id='" + templateId + "'");
			if (this.frowset.next())
				name = this.frowset.getString("name");

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return name;
	}

	public void updateParameter_content(HashMap hm, String planId, String gather_typeJ) throws GeneralException
	{
		String bodyIds = (String) this.getFormHM().get("bodyTypeIds");
		bodyIds = PubFunc.keyWord_reback(bodyIds);
		/*
		 * 保存考核主体类别设置
		 */
		if (bodyIds != null && !"".equals(bodyIds))
		{
			String[] bodyids = bodyIds.split(",");
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
		String blankScoreOption = (String) this.getFormHM().get("blankScoreOption");
		String mailTogoLink = (String) this.getFormHM().get("mailTogoLink");
		String radioDirection = (String) this.getFormHM().get("radioDirection");
		String allowUploadFile = (String) this.getFormHM().get("allowUploadFile");
		String mutiScoreOnePageOnePoint=(String)this.getFormHM().get("mutiScoreOnePageOnePoint");
		String targetCompleteThenGoOn=(String)this.getFormHM().get("targetCompleteThenGoOn");
		String scoreWay = (String) this.getFormHM().get("scoreWay");
		String blankScoreUseDegree = (String) this.getFormHM().get("blankScoreUseDegree");
		String noCanSaveDegrees = (String) this.getFormHM().get("noCanSaveDegrees");
		String departmentLevel = (String) this.getFormHM().get("departmentLevel");
		// BSParam
		String scoreShowRelatePlan = (String) this.getFormHM().get("scoreShowRelatePlan");
		String menRefDeptTmpl = (String) this.getFormHM().get("menRefDeptTmpl");
		String showIndicatorDesc = (String) this.getFormHM().get("showIndicatorDesc");
		String totalAppFormula = (String) this.getFormHM().get("totalAppFormula");
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
		String canSaveAllObjsScoreSame = (String) this.getFormHM().get("canSaveAllObjsScoreSame");
		String showSumRow = (String) this.getFormHM().get("showSumRow");
		String mustFillCause = (String) this.getFormHM().get("mustFillCause");
		String basicInfoItem = (String) this.getFormHM().get("basicInfoItem");
		String showBasicInfo = (String) this.getFormHM().get("showBasicInfo");
		String lockMGradeColumn = (String) this.getFormHM().get("lockMGradeColumn");
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
		if(showDayWeekMonth!=null && !"".equals(showDayWeekMonth))
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
		String showBackTables = (String) this.getFormHM().get("showBackTables");
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
		String targetTraceEnabled = (String) this.getFormHM().get("targetTraceEnabled");
		String targetTraceItem = (String) this.getFormHM().get("targetTraceItem");
		String targetCollectItem = (String) this.getFormHM().get("targetCollectItem");
		String targetMustFillItem = (String) this.getFormHM().get("targetMustFillItem");
		String targetUsePrevious = (String) this.getFormHM().get("targetUsePrevious");
		String targetCalcItem = (String) this.getFormHM().get("targetCalcItem");
		String targetItem = (String)this.getFormHM().get("targetItem");
		String targetDefineItem = (String) this.getFormHM().get("targetDefineItem");
		String noShowTargetAdjustHistory = (String) this.getFormHM().get("noShowTargetAdjustHistory");
		String allowLeaderTrace = (String) this.getFormHM().get("allowLeaderTrace");
		String processNoVerifyAllScore = (String) this.getFormHM().get("processNoVerifyAllScore");
		String verifyRule = (String) this.getFormHM().get("verifyRule");
		verifyRule = PubFunc.keyWord_reback(verifyRule);
		String evalOutLimitStdScore = (String) this.getFormHM().get("evalOutLimitStdScore");
		String evalOutLimitScoreOrg = (String) this.getFormHM().get("evalOutLimitScoreOrg");
		String showLeaderEval = (String) this.getFormHM().get("showLeaderEval");
		String isLimitPointValue = (String) this.getFormHM().get("isLimitPointValue");
		
		String mainbodybodyid = (String)this.getFormHM().get("mainbodybodyid");//强制分布主体类别
		String allmainbodybody = (String)this.getFormHM().get("allmainbodybody");//强制分布主体类别
		String wholeEvalMode = (String)this.getFormHM().get("wholeEvalMode");//总体评价录分方式0：录入等级1：录入分值
		String batchScoreImportFormula = (String)this.getFormHM().get("batchScoreImportFormula");//多人评分引入总分计算公式   pjf 2014.01.03

		
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
		
		String taskNameDesc=(String)this.getFormHM().get("taskNameDesc");	
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
		rootAttributes.put("AllowUploadFile", zeroToFalse(allowUploadFile));
		rootAttributes.put("TargetCompleteThenGoOn", zeroToFalse(targetCompleteThenGoOn));
		rootAttributes.put("MutiScoreOnePageOnePoint", zeroToFalse(mutiScoreOnePageOnePoint));
		rootAttributes.put("ScoreWay", scoreWay);
		rootAttributes.put("BlankScoreUseDegree", blankScoreUseDegree);
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
		rootAttributes.put("PerSet", perSet);
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
		rootAttributes.put("MitiScoreMergeSelfEval", zeroToFalse(mitiScoreMergeSelfEval));
		rootAttributes.put("CheckGradeRange", checkGradeRange);
		rootAttributes.put("KeyEventEnabled", zeroToFalse(keyEventEnabled));
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
		rootAttributes.put("TargetMustFillItem", targetMustFillItem);
		rootAttributes.put("TargetUsePrevious", targetUsePrevious);
		rootAttributes.put("TargetCalcItem", targetCalcItem);
		rootAttributes.put("TargetItem", targetItem);
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
		rootAttributes.put("TaskSupportAttach", zeroToFalse(taskSupportAttach));
		rootAttributes.put("SpByBodySeq", zeroToFalse(spByBodySeq));
		rootAttributes.put("GradeByBodySeq", zeroToFalse(gradeByBodySeq));
		rootAttributes.put("AllowSeeAllGrade", zeroToFalse(allowSeeAllGrade));
		
		rootAttributes.put("PointEvalType", pointEvalType);
		rootAttributes.put("TaskNameDesc", taskNameDesc);
		rootAttributes.put("BasicInfoItem", basicInfoItem);
		rootAttributes.put("ShowBasicInfo", zeroToFalse(showBasicInfo));
		rootAttributes.put("LockMGradeColumn", zeroToFalse(lockMGradeColumn));
		rootAttributes.put("ShowDayWeekMonth", showDayWeekMonth);
		rootAttributes.put("GradeSameNotSubmit", zeroToFalse(gradeSameNotSubmit));
		rootAttributes.put("ShowHistoryScore", zeroToFalse(showHistoryScore));
		//勾选强制百分比分布后，默认选中所有主体类别  haosl 2017-12-13
		//xus 19/12/16 【55258】保存时报错：err.system.IndexOutOfBoundsException
		if(StringUtils.isEmpty(mainbodybodyid) && "true".equalsIgnoreCase(zeroToFalse(mutiScoreGradeCtl)) && StringUtils.isNotBlank(bodyIds)) {
			mainbodybodyid = bodyIds.substring(0, bodyIds.length()-1);
		}
		rootAttributes.put("MainbodyGradeCtl", mainbodybodyid);//强制分布主体类别
		rootAttributes.put("AllMainbodyGradeCtl", allmainbodybody);//强制分布主体类别
		rootAttributes.put("WholeEvalMode", wholeEvalMode);
		rootAttributes.put("BatchScoreImportFormula", batchScoreImportFormula);
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
		excludeDegree = StringUtils.isBlank(excludeDegree)?"":excludeDegree;//因为360有评分说明不填，目标考核没有，这样获取的是null导致后面在bean.get()转String错误
		requiredFieldStr = requiredFieldStr != null && !"".equals(requiredFieldStr) ? requiredFieldStr : "";
		
		ArrayList mustFillList = new ArrayList();	
		if((upIsValid!=null && upIsValid.trim().length()>0) && (downIsValid!=null && downIsValid.trim().length()>0))
		{
			// 高于、低于、等于XX无需评分说明以及必填指标 modify by 刘蒙
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
		//------------------慧聪网需求  计划内参数加“结果反馈方式”设置  zhaoxg add 2014-6-26-----------------
		LoadXml parameter_content = new LoadXml();
		String evaluate_str=(String) this.getFormHM().get("evaluate_str");
		String blind_point=(String) this.getFormHM().get("blind_point");
		HashMap map = new HashMap();
		map.put("evaluate_str", evaluate_str);
		map.put("blind_point", blind_point);
		//------------------------------------end--------------------------------------
		parameter_content.saveAttributes(this.getFrameconn(), rootAttributes, fineAttributesMap, badlyAttributesMap, mustFillList, warnRoleScopeList,map, planId);
		
		// 保存描述性评议项设置
		
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		if (dbWizard.isExistTable("t#des_review", false))
		{
			ExamPlanBo ebo = new ExamPlanBo(planId,this.frameconn);
		    ebo.saveHighSet("t#des_review");
		    ebo.deleteTemp("t#des_review");
		}
		// 保存参数指标说明文件
		FormFile form_file = (FormFile) getFormHM().get("file");
		if (form_file != null)
		{
			ExamPlanBo bo = new ExamPlanBo(this.frameconn);
			bo.saveThefile(planId, form_file, this.userView);
			// 清除临时表 
			String tempTable ="t#"+this.getUserView().getUserId()+"_per_file"; // "per_plan_file_" + planId + "_" + this.getUserView().getUserId();
			if (dbWizard.isExistTable(tempTable, false))
				dbWizard.dropTable(tempTable);
		}
		// 加载动态参数
		LoadXml loadxml = new LoadXml(this.frameconn, planId);
		BatchGradeBo.getPlanLoadXmlMap().put(planId, loadxml);
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
	public String falseToZero(String str){
		if("False".equals(str))
			return "0";
		else if ("True".equals(str))
			return "1";
		else
			return str;
	}

}
