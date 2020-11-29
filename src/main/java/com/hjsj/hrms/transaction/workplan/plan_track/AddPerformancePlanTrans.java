package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_track.RelatePerformancePlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;



/**
 * <p>Title:AddPerformancePlanTrans.java</p>
 * <p>Description>:</p>新建考核计划
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-11-26 下午01:12:59</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class AddPerformancePlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{	
		
		String orgCode = "";
		HashMap hm = this.getFormHM();
		String oprType=(String)hm.get("oprType");
        //计划期间 
        String periodType =(String)hm.get("periodType");
        periodType=(periodType==null)?"":periodType;                           
        String periodYear =(String)hm.get("periodYear");
        periodYear=(periodYear==null)?"":periodYear; 
        String periodMonth =(String)hm.get("periodMonth");
        periodMonth=(periodMonth==null)?"":periodMonth; 
        String periodWeek =(String)hm.get("periodWeek"); 
        periodWeek=(periodWeek==null)?"":periodWeek; 
        String planType =(String)hm.get("planType");                
        planType=(planType==null)?"":planType;
        planType="undefined".equals(planType)?"":planType;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if ("initPlan".equals(oprType))
			{
			    //取得默认名
			    String cycle=String.valueOf((Integer.parseInt(periodType)-1));
                periodMonth = Integer.valueOf(periodMonth)<10?"0"+periodMonth:periodMonth;
    		    String periodDesc =WorkPlanUtil.getKhPlanPeriodDesc(cycle, periodYear, periodMonth, periodMonth);
    		    periodDesc=periodDesc+"考核计划"; 
    		    if ("2".equals(planType)){
                     periodDesc=periodDesc+"(部门)";                   
                }
                else {
                     periodDesc=periodDesc+"(个人)";
                }
			    this.getFormHM().put("plan_name", periodDesc);
			}
			else if ("savePlan".equals(oprType)){
			    if (WorkPlanConstant.Cycle.WEEK.equals(periodType)
                        ){
                   return;
                }
			    String creator = this.getUserView().getUserName();
                String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
                RecordVo vo = new RecordVo("per_plan");
                String planId="";
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                planId = idg.getId("per_plan.plan_id");
                Integer planid = new Integer(planId);
                vo.setString("plan_id", planid.toString());
                vo.setString("status", "2");
                vo.setString("plan_type", "1");// 默认为记名
               
                String cycle=String.valueOf((Integer.parseInt(periodType)-1));
                String planname =(String)hm.get("planName"); 
                if (planname==null || planname.length()<1){
                    String periodDesc =WorkPlanUtil.getKhPlanPeriodDesc(cycle, periodYear, periodMonth, periodMonth);
                    periodDesc=periodDesc+"考核计划"; 
                    if ("2".equals(planType)){
                         periodDesc=periodDesc+"(部门)";                   
                    }
                    else {
                         periodDesc=periodDesc+"(个人)";
                    }
                }
                vo.setString("name",planname);
                //计划所属机构
                String operOrg = this.userView.getUnitIdByBusi("5");
                if (operOrg != null && operOrg.length() > 3) {
                    StringBuffer tempSql = new StringBuffer("");
                    String[] temp = operOrg.split("`");
                    for (int i = 0; i < temp.length; i++) {   
                        orgCode= temp[i].substring(2);
                        break;
                    }
                }                
                if ("".equals(orgCode)) orgCode = "HJSJ";
                vo.setString("b0110", orgCode);

                //考核对象类型
                if("2".equals(planType)){
                    vo.setString("object_type", "4");//
                }
                else {
                    vo.setString("object_type", "2");//部门
                }

                //计划期间类型
                vo.setString("cycle", cycle);
                vo.setString("theyear", periodYear);
                periodMonth = Integer.valueOf(periodMonth)<10?"0"+periodMonth:periodMonth;
                if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)){
                    vo.setString("thequarter", periodMonth);
                } else if(WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
                	vo.setString("thequarter", periodMonth);
                }
                if (WorkPlanConstant.Cycle.MONTH.equals(periodType)
                        ||WorkPlanConstant.Cycle.WEEK.equals(periodType)){
                    vo.setString("themonth", periodMonth);
                }
                vo.setString("start_date", "");
                vo.setString("end_date", "");

                vo.setString("gather_type", "0");// 默认为网上
                //考核方法
                vo.setString("method", "2");
                String template_id =(String)hm.get("templateId"); 
                vo.setString("template_id", template_id);

                vo.setString("agree_user", this.userView.getUserFullName());
                vo.setString("parameter_content", "");
                vo.setString("agree_date", creatDate);
                vo.setString("approve_result", "1");
                vo.setString("agree_idea", "直批");
                vo.setString("descript", "");
                vo.setString("create_user", creator);
                vo.setDate("create_date", creatDate);

                vo.setString("target", "");
                vo.setString("content", "");
                vo.setString("flow", "");
                vo.setString("result", "");
                vo.setString("plan_visibility", "0");
                // 单位名称用于在添加页面显示中文名称
                String codeName = AdminCode.getCodeName("UN", orgCode);
                if (codeName == null || codeName.length() == 0)
                    codeName = AdminCode.getCodeName("UM", orgCode);
                if ("HJSJ".equals(orgCode))
                    codeName = "公共资源";

                this.getFormHM().put("codeName", codeName);
                this.getFormHM().put("statusName", "已批");
                this.getFormHM().put("templateName", "");
                // 设置默认参数
                setPlanParameters("","2","2");
                this.getFormHM().put("plan_visibility", "0");
              //  this.getFormHM().put("evaluate_str", "");//清空form中的这两项  防止新增计划等  初始时从form中取值 影响展现   zhaoxg add 2014-6-26
                this.getFormHM().put("blind_point", "0");
                this.getFormHM().put("planSelect", new Integer(planId).toString());
                this.getFormHM().put("khplanvo", vo);                
                //保存
                save(vo);
                //发布
                RelatePerformancePlanBo relateBo = new RelatePerformancePlanBo(this.frameconn,
                        this.userView);
                relateBo.publishPlan(planid.toString(), "0");
                this.getFormHM().put("plan_id", planid.toString());  
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally
		{
		}

	}


	public String isNull(String str)
	{
		if (str == null)
			str = "";
		return str;
	}


	
	   public void save(RecordVo votemp) throws GeneralException
	    {
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
	        

	        try
	        {
	         
                dao.addValueObject(vo);
                String sql = "update per_plan set a0000=a0000+1  where a0000 is not null";
                dao.update(sql);
                sql = "update per_plan set a0000=((select min(a0000)-1 from per_plan))  where plan_id=" + planId;
                dao.update(sql);	        
	            
	            //初始化计划的主体类别
                RelatePerformancePlanBo relateBo = new RelatePerformancePlanBo(this.frameconn,this.userView);
	            String bodyTypeIds = relateBo.getSuperBodySet();
	            if ("2".equals(object_type)){
	                if (bodyTypeIds.length()>0) 
	                    bodyTypeIds=bodyTypeIds+",";
	                bodyTypeIds=bodyTypeIds+"5"; 
	            }
	            else {
	                if (bodyTypeIds.length()>0) 
                        bodyTypeIds=bodyTypeIds+",";
                    bodyTypeIds=bodyTypeIds+"-1";
	            }
	            this.getFormHM().put("bodyTypeIds", bodyTypeIds);
	            
	            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	            updateParameter_content(hm, planId, gather_type);

	            String bodyid =relateBo.getBodyIdByBodyType("1");
                //更新上级权重为1
	            if(StringUtils.isNotEmpty(bodyid)){
                    sql = "update per_plan_body set rank=1  where  plan_id=" + planId
                            +" and body_id = "+bodyid;
                    dao.update(sql);
                }

	           
	        } catch (SQLException e)
	        {
	            e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        }
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
	            String evaluate_str="";//考核结果显示项    
	            AnalysePlanParameterBo bo1=new AnalysePlanParameterBo(this.frameconn);
	            Hashtable ht_table=bo1.analyseParameterXml();
	            if(ht_table.get("objective")!=null)
	                evaluate_str=(String)ht_table.get("objective");
	            evaluate_str=evaluate_str==null?"":evaluate_str;
	            ArrayList evaluateList=ConfigParamBo.getConfigDrawList(Integer.parseInt(method),evaluate_str);          
	            this.getFormHM().put("evaluate_str",evaluate_str);
	            this.getFormHM().put("evaluateList", evaluateList);  
	            
	            String blind_point="0";       
	            if(ht_table.get("blind_goal")!=null)
	                blind_point=(String)ht_table.get("blind_goal");
	            blind_point=blind_point==null?"0":blind_point;
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
	        //this.getFormHM().put("targetAppMode", params.get("targetAppMode"));
	        this.getFormHM().put("targetAppMode", "1");//汇报关系
	        
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
	            targetDefineItem=targetDefineItem.substring(1);
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
	        this.getFormHM().put("targetDefineItem", targetDefineItem);

	        this.getFormHM().put("evalOutLimitStdScore", params.get("EvalOutLimitStdScore"));
	        this.getFormHM().put("evalOutLimitScoreOrg", params.get("EvalOutLimitScoreOrg"));
	        this.getFormHM().put("showLeaderEval", params.get("ShowLeaderEval"));
	        this.getFormHM().put("processNoVerifyAllScore", params.get("ProcessNoVerifyAllScore"));
	        this.getFormHM().put("verifyRule", params.get("VerifyRule"));
	        this.getFormHM().put("showBackTables", params.get("ShowBackTables"));
	        this.getFormHM().put("taskNameDesc",params.get("TaskNameDesc"));
	    
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
	        if(sameAllScoreNumLess==null || sameAllScoreNumLess.trim().length()<=0 || (sameScoreNumLessValue==null)|| "0".equalsIgnoreCase(sameScoreNumLessValue))
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
	        if (batchScoreImportFormula==null){batchScoreImportFormula="";}
	        
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
	    //  rootAttributes.put("RelatingTargetCard", zeroToFalse(relatingTargetCard));
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
	        //wangrd 反馈参数为空
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
