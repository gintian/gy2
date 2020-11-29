/*
 * 创建日期 2005-8-16
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author luangaojiong
 * 
 * 读取xml文档 得到了解程度，总体评价，等级标识等相关参数
 */
public class LoadXml
{
    Hashtable returnHt = new Hashtable();

    private Connection conn = null;

    private String planid = "";

    Document a_doc = null;

    public LoadXml()
    {

    }

    /**
     * 得到关联计划
     * @param node 节点名称 Plan,Formula
     * @param attributeName 属性名
     * @return
     */
    public ArrayList getRelatePlanValue(String node,String attributeName)
    {
    	ArrayList list = new ArrayList();
    	if(this.a_doc==null)
    		return list;
    	Element root = this.a_doc.getRootElement();
    	Element plannode = root.getChild("RelatePlan");
    	if(plannode!=null)
    	{
    		List planlist = plannode.getChildren(node);
    		Element plan = null;
    		for(int i=0;i<planlist.size();i++)
    		{
    			plan = (Element)planlist.get(i);
    			list.add(plan.getAttributeValue(attributeName));
    		}
    	}
    	return list;
    }
    
    
    /**
     * 得到关联计划的所有设置
     * @param node 节点名称 Plan,Formula
     * @return
     */
    public ArrayList getRelatePlanValue(String node )
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	ContentDAO dao = null;
        Connection connection = this.conn;
    	boolean isNeedClose = false;
        try {
            if(this.a_doc==null)
                return list;
            Element root = this.a_doc.getRootElement();
            Element plannode = root.getChild("RelatePlan");
            //存在链接被关闭的情况，这里兼容处理下，如果链接已经关闭就重新打开一个链接，
            // 并且在方法执行完后手动关闭该链接 haosl 2019年5月5日
            if(connection.isClosed()){
                isNeedClose = true;
                connection = AdminDb.getConnection();
            }
            dao = new ContentDAO(connection);
            if(plannode!=null)
            {
                List planlist = plannode.getChildren(node);
                Element plan = null;
                LazyDynaBean abean=null;
                for(int i=0;i<planlist.size();i++)
                {
                    plan = (Element)planlist.get(i);
                    String Plan_id = plan.getAttributeValue("ID");
                    rs = dao.search("select plan_id as count from per_plan where plan_id='"+Plan_id+"'");//如果关联的计划已不存在，则不添加 chent 20161207
                    if(rs.next()){
                        abean=new LazyDynaBean();
                        abean.set("id", Plan_id);
                        abean.set("Name", plan.getAttributeValue("Name"));
                        abean.set("Type", plan.getAttributeValue("Type"));
                        if(plan.getAttributeValue("Menus")!=null)
                            abean.set("Menus", plan.getAttributeValue("Menus"));
                        else
                            abean.set("Menus", "");
                        if(plan.getAttributeValue("HZMenus")!=null)
                            abean.set("HZMenus", plan.getAttributeValue("HZMenus"));
                        else
                            abean.set("HZMenus", "");
                        list.add(abean);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(isNeedClose){
                PubFunc.closeDbObj(connection);
            }
        }
        return list;
    }
    
    
    
    public String getRelatePlanMenuValue(String relaPlan)
    {
    	String planMenus = "";
    	if(this.a_doc==null)
    		return planMenus;
    	Element root = this.a_doc.getRootElement();
    	Element plannode = root.getChild("RelatePlan");
    	if(plannode!=null)
    	{
			List planlist = plannode.getChildren("Plan");
			Element plan = null;
			for (int i = 0; i < planlist.size(); i++)
			{
				plan = (Element) planlist.get(i);
				String plan_id = plan.getAttributeValue("ID");
				if (plan_id.equals(relaPlan))
				{
					planMenus = plan.getAttributeValue("Menus");
					break;
				}
			}
    	}    	
    	return planMenus;
    }
    
    public String getRelatePlanSubSetMenuValue()
    {
    	String planMenus = "";
    	if(this.a_doc==null)
    		return planMenus;
    	Element root = this.a_doc.getRootElement();
    	Element plannode = root.getChild("RelatePlan");
    	if(plannode!=null)
    	{
    		Element subset = plannode.getChild("Subset");
			if(subset!=null)
				planMenus = subset.getAttributeValue("Menus");								
    	}    	
    	return planMenus;
    }
    
    /**
     * 保存引入计划
     * @param node 节点名称
     * @param idlist 内为attributeName的列表，顺序与list对应
     * @param list 内为LazyDynaBean，保存着attributeName和对应的value
     */
    public void saveRelatePlanValue(String node,ArrayList idlist,ArrayList list)
    {
    	if(this.a_doc!=null)
    	{
    		Element root = this.a_doc.getRootElement();
        	Element plannode = root.getChild("RelatePlan");
        	if(plannode==null)
        	{
        		Element Rplan = new Element("RelatePlan");
        		root.addContent(Rplan);
        		plannode = Rplan;
        	}else
        		plannode.removeChildren(node);
        	
        	for(int i=0;i<list.size();i++)
        	{
        		LazyDynaBean bean = (LazyDynaBean)list.get(i);
        		Element element = new Element(node);
        		for(int j=0;j<idlist.size();j++)
        		{
        			element.setAttribute(idlist.get(j).toString(),bean.get(idlist.get(j).toString()).toString());
        		}
        		plannode.addContent(element);
        	}
    	}
    	XMLOutputter outputter=new XMLOutputter();
    	Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList paramList = new ArrayList();
		try 
		{
			paramList.add(outputter.outputString(this.a_doc));
			paramList.add(Integer.parseInt(this.planid));
		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
		    
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
    }
    
    //  启动时是否手工打分
    public String getHandEval(String parameter_content)
    {

		String HandEval = "FALSE";  
		if (!"".equals(parameter_content.trim()))
		{
		    Element root;
		    try
		    {
				Document doc = PubFunc.generateDom(parameter_content);
				root = doc.getRootElement();
				if (root.getAttributeValue("HandEval") != null && !"".equals(root.getAttributeValue("HandEval")))
				{
					HandEval = root.getAttributeValue("HandEval");
				}
	
		    } catch (Exception ex)
		    {
		    	ex.printStackTrace();
		    }
		}
		return HandEval;
    }
    
    
    public String getPerformanceType(String parameter_content)
    {

	String performanceType = "0"; // 考核形式 0：绩效考核 1：民主评测
	if (!"".equals(parameter_content.trim()))
	{
	    Element root;
	    try
	    {
		Document doc = PubFunc.generateDom(parameter_content);
		root = doc.getRootElement();
		if (root.getAttributeValue("performanceType") != null && !"".equals(root.getAttributeValue("performanceType")))
		{
		    performanceType = root.getAttributeValue("performanceType");
		}

	    } catch (Exception ex)
	    {
		ex.printStackTrace();
	    }
	}
	return performanceType;
    }

    public LoadXml(Connection con, String planId, String flag)
    {

	this.conn = con;
	this.planid = planId;
	String sql = "select parameter_content from per_plan where plan_id=" + planId;
	ContentDAO dao = new ContentDAO(this.conn);
	ResultSet rs = null;
	try
	{
	    rs = dao.search(sql);
	    String xmlContext = "";

	    if (rs.next())
	    {
		xmlContext = Sql_switcher.readMemo(rs, "parameter_content"); // PubFunc.nullToStr(rs.getString("parameter_content"));
	    }

	    if (!"".equals(xmlContext.trim()))
	    {
		this.a_doc = PubFunc.generateDom(xmlContext);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}finally {
		PubFunc.closeResource(rs);
	}
    }
    /**
     * flag参数没有用，主要为了区别构造函数
     * @param conn
     * @param xmlcontent
     * @param flag
     */
    public LoadXml(Connection conn,String xmlcontent,int flag)
    {
    	this.conn=conn;
    	try
    	{
    		if (!"".equals(xmlcontent.trim()))
    	    {
	    		this.a_doc = PubFunc.generateDom(xmlcontent);
	    		Element root;
	    		 root =  this.a_doc.getRootElement();
	 		    Hashtable tempHash = new Hashtable();
	 		    tempHash = getElements(root);
	 		    returnHt = tempHash;
    	    }
    		else
	    	{
	    	    String xiFormula="";    //绩效系数公式
	    	    String DescriptiveWholeEval="True";  //显示描述性总体评价，默认为 True
	    	    String SelfEvalNotScore="False";  //自我评价不显示打分
	    		String strNodeKnowDegree = ""; // 了解程度
	    		String strWholeEval = ""; // 总体评价
	    		String MustFillWholeEval="False";  //总体评价必填
	    		String KeepDecimal = "1"; // 小数位
	    		String ScoreShowRelatePlan="False"; //多人评分显示引入计划得分
	    		String GradeClass = "0";
	    		String SummaryFlag = "False";// 个人总结报告
	    		String noteIdioGoal="False"; //显示个人目标
	    		String showNoMarking = "False"; // 显示不打分
	    		String showIndicatorDesc = "False"; // 显示指标说明
	    		String perSet = ""; // 绩效子集
	    		String evalClass = "0";//总体评价的的等级分类
	    		String sameResultsOption = "1"; // 核对象指标结果是否全部相同 1: 可以保存 2: 不能保存
	    		/** 打分标识 */
	    		String DegreeShowType="1";  //1-标准标度 2-指标标度 3-采集标准标度,显示指标标度内容
	    		String scoreflag = "2";// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
	    		String addSubtractType="1";  //加扣分处理方式  1:加扣分  2:加分  3：扣分
	    		String limitation = "-1";// =-1不转换,模板中最高标度的数目
	    		// (大于0小于1为百分比，大于1为绝对数)
	
	    		String SameAllScoreNumLess = "0"; // 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
	    		String bFineRestrict = "False";// 是否限制高分比例
	    		String fineMax = "-1"; // 指标得分为A(优秀)的个数(大于0小于1为百分比，大于1为绝对数)
	    		// -1表示每个指标分别设置，数据在FineMax节点
	    		HashMap fineMaxMap = new HashMap(); // 表示每个指标得最大数控制
	
	    		String BadlyRestrict = "False";// 是否限制低分比例
	    		String BadlyMax = "-1"; // 指标得低分的个数(大于0小于1为百分比，大于1为绝对数)
	    		// -1表示每个指标分别设置，数据在BadlyMax节点
	    		HashMap BadlyMap = new HashMap(); // 表示每个指标得最大数控制
	
	    		String limitrule = "1";// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
	    		String perSetShowMode = "1"; // 绩效子集显示方式 1-明细项，2-合计项 或 3-两者者显
	    		String perSetStatMode = "0"; // 绩效子集统计方式
	    		// 1-年、2-月、3-季度、4-半年、9-时间段
	    		String StatStartDate = ""; // 统计方式为9有效，绩效子集统计时间段
	    		String StatEndDate = ""; // 统计方式为9有效，绩效子集统计时间段
	    		String statCustomMode = "True"; // 显示绩效子集统计自定义 True
	    		// False
	    		String scoreNumPerPage = "0"; // BS打分时每页的人数，0为不限制
	    		String ShowOneMark = "False"; // BS打分时显示统一打分的指标，以便参考 Boolean,
	    		// 默认为False
	    		String ScoreBySumup = "False"; // BS个人总结没填写，主体为其打分时不能提交
	    		String AllowUploadFile = "True"; // 支持附件上传
	    		String  TargetCompleteThenGoOn = "False"; ////目标卡填写完整才允许提交（个性化任务、绩效报告）参数
	    		String GATIShowDegree = "False"; // BS 综合测评表中指标的评分显示为标度
	    		String SelfScoreInDirectLeader = "False"; // 整型（Boolean为兼容）：0和False 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
	    		String isShowOrder = "false"; // 显示排名
	    		String AutoCalcTotalScoreAndOrder = "false"; // 是否自动计算总分和排名
	    		
	    		String isEntireysub = "false"; // 提交是否需要必填
	    		String performanceType = "0"; // 考核形式 0：绩效考核 1：民主评测
	    		String ReaderType = "0"; // 机读类型:0光标阅读机(默认),1扫描仪

	    		String isShowSubmittedPlan = "True";
	    		String showAppraiseExplain = "true"; // 综合评测表是否显示评测说明
	    		String isShowSubmittedScores = "true"; // 提交后的分数是否显示
	    		String MutiScoreGradeCtl = "FALSE"; // BS多人打分时是否等级控制
	    		String mitiScoreMergeSelfEval = "False"; // 多人打分时同时显示自我评价
	    		String CheckGradeRange="0";  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
	    		//票数统计
	    		String VotesNum="";  //票数
	    		
	    		//计算规则
	    		String UnLeadSingleAvg="False";  //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
	    		//关键事件参与总分修正
	    		String keyEventEnabled="False";
	    		String formulaSql="";//总分公式sql
	    		String formulaDeviationSql="";//纠偏总分公司sql
	    		String deviationScoreUsed="";//是否要纠偏总分
	    		String CheckInvalidGrade="False"; //无效标度代码
	    		String zeroByNull="false";
	    		String InvalidGrade="";           //是否选择使用无效票数, (True, False；默认为False)
	    		String BlankScoreOption = "0"; // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
	    		String BlankScoreUseDegree="A";  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…	    		
	    	    String DepartmentLevel = ""; // 部门层级
	    		
	    		returnHt.put("DegreeShowType", DegreeShowType);
	    		String scoreWay = "1"; // 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
	    		String degreeShowType = "1";// 标度显示形式(1-标准标度内容 2-指标标度内容 3-采集标准标度,显示指标标度内容）
	    		String PublicPointCannotEdit = "False"; // 共性指标员工不能编辑, 默认为 False
	                                                            // 能编辑
	    		//显示员工日志
	    		String ShowEmployeeRecord="True";
	    		// 目标卡制订支持几级审批
	    		String targetMakeSeries = "1";
	    		// 任务调整需新建任务项
	    		String taskAdjustNeedNew = "False";
	    		// 每项任务可签批
	    		String taskCanSign = "False";
	    		// 每项任务需回顾总结
	    		String taskNeedReview = "False";  	
	//    		 显示考核指标内容
	    		String showIndicatorContent = "False";
	    		// 显示考核指标评分原则
	    		String showIndicatorRole = "False";
	    		// 显示考核指标标度说明
	    		String showIndicatorDegree = "False";
	    		// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
	    		String targetAppMode = "0";   
	    		//关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
	    		String relatingTargetCard = "1";
	    		String showYPTargetCard = "False";//显示已自评目标卡：True，     不显示：False（默认）
	    		//显示扣分原因(Ture, False(默认))
	    		String showDeductionCause = "False";
	    		//目标卡批准后允许再调整, (True, False, 默认为True)
	    		String targetAllowAdjustAfterApprove="True";		
	    		//允许领导制定及调整目标卡 默认为False
	    		String  allowLeadAdjustCard = "False"; 		    
	    		//允许查看下级对考核对象评分 默认为False
	    		String  allowSeeLowerGrade = "False";  
	    		//相同时能否保存(Ture, False),默认为 True
	    		String  CanSaveAllObjsScoreSame="True";
	    		String  ShowSumRow="False"; //显示合计行
	    		
	    		String DynaBodyToPoint="False";  //动态主体权重控制到指标/任务True, False, 默认为 False
	    		String ProcessNoVerifyAllScore="True";  //报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)
	    		String VerifyRule = "="; //报批 批准进行总分校验 校验规则 (=,<=)模板总分 默认为=
	    		String EvalOutLimitStdScore="False";     //评分时得分不受标准分限制True, False, 默认为 False;都加
	    		String EvalOutLimitScoreOrg="False";    //评分时不受考核机构限制True, False, 默认为 False;
	    		String voteScoreDecimal ="0";//总分精度		    
	    		String voteDecimal ="2";//权重精度
	    		String NoApproveTargetCanScore="False";     //目标卡未审批也允许打分 True, False, 默认为 False
	    		String TargetCalcItem="";                   //目标卡计算指标属性，P04中指标，以逗号分隔，顺序从前到后
	    		String TargetMustFillItem="";               //目标卡必填指标,   以逗号分隔
	    		String TargetUsePrevious="";                // 引入上期目标卡指标
	    		String ShowLeaderEval="False";              //本人查看绩效面谈显示领导对其的评价
	    		
	    		String AllowAdjustEvalResult="False"; //允许调整评估结果, True, False, 默认为 False
	    		String AdjustEvalDegreeType="0"; //调整使用标度 0=指标标度，1=等级标度.默认为0
	    		String AdjustEvalDegreeNum="0"; //调整浮动等级：整数值
	    		String CalcMenScoreRefDept="False"; //个人考核评分=个人指标得分*部门指标得分的权重和（目标考核和360°） True, False, 默认为 False	 
	    		String AdjustEvalRange = "0"; //调整范围：0=指标，1=总分.默认为0
	    		String ShowGrpOrder ="True";  //评分调整 显示排名
	    		String VerifySameScore = "False";//打分分数相同不能提交：True, False, 默认为 False
	    	    String ShowEvalDirector = "False";//评估结果中显示"负责人"指标：True, False, 默认为 False
	    	    String AdjustEvalGradeStep ="";//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
	    	    String ScoreFromItem = "False";//按项目权重逐级计算总分，True，False 默认False；
	    	    String BodysFromCard = "False"; //考核主体从机读卡读取(主体类别自动对应)
	    	    String MenRefDeptTmpl = ""; //本次评分=个人指标得分*部门指标得分的权重和时，部门模板。为空表示与当前计划相同。
	    	    String ObjsFromCard = "False"; //考核对象是否从机读卡读取(考核实施中不需要选择考核对象)
	    	    String TaskSupportAttach="False"; //目标任务支持附件上传
	    	    
	    	    String GradeByBodySeq="False"; //按考核主体顺序号控制评分流程(True, False默认为False)
	    	    String AllowSeeAllGrade="False"; //允许查看其它主体对考核对象评分(True, False默认为False)
	    	    String TotalAppFormula = ""; // 总体评价的计算公式，默认为空
	    	    String MailTogoLink = "1"; // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件
	    	    String RadioDirection = "0";//“打分指标标度显示方式”设置为单选钮时，后面出现排列方式下拉选项，默认为“纵向”，也可选择“横向”
	    	    
	    	    String PointEvalType="0";  //360指标评分型式  0：下拉（默认）   1：单选
	    	    String MutiScoreOnePageOnePoint="False";  //单题打分
	    	    String TargetGroupByItem="False"; //目标卡按一级项目分值显示，True, False, 默认为 False
	    	    String TargetGroupItems="";  //项目号1,组号1:A1,A3,A4; 项目号2,组号1:A1,A3,A4; 项目号3,组号2:A1,A3,A4;
	    	    
	    	     
	    	    String ShowBasicInfo="False";   //360计划显示基本信息
	    	    String BasicInfoItem="";        //基本信息指标
	    	    String LockMGradeColumn="True"; //多人考评锁定指标列
	    	    
	    	    String PointScoreFromKeyEvent="False"; //指标评分优先取自关键事件
	    	    
	    	    ArrayList MustFillOptionsList = new ArrayList(); // 评分说明必填高级规则
	    	    ArrayList WarnRoleScopeList = new ArrayList(); // 预警提醒设置
	    	    
	    	    
	    	    String SpByBodySeq="False";//是否按考核主体的顺序号，进行审批
	    	    
	    	    String showDayWeekMonth = "1,2,3";//查看员工日志
	    	    String mainbodyGradeCtl = "";//强制分布主体类别
	    	    String allmainbodyGradeCtl = "";//强制分布主体类别
	    	    String wholeEvalMode = "0";//总体评价录分方式0：录入等级1：录入分值
	    	    String gradeSameNotSubmit="False";//等级不同分数相同不能提交
	    	    String showHistoryScore = "False";//显示历次得分表
	    	    String batchScoreImportFormula = "False";  //多人评分引入总分计算公式  pjf 2014.01.03
	    	    
	    	    String evaluate=""; //控制考评反馈方式
	    	    String blind_point="0";    
	    		returnHt.put("blind_point", blind_point);
	    	    returnHt.put("evaluate",evaluate);
	    	    
	    	    
	    	    returnHt.put("BatchScoreImportFormula", batchScoreImportFormula);
	    	    returnHt.put("ShowHistoryScore",showHistoryScore);
	    	    returnHt.put("MainbodyGradeCtl",mainbodyGradeCtl);
	    	    returnHt.put("AllMainbodyGradeCtl",allmainbodyGradeCtl);
	    	    returnHt.put("WholeEvalMode",wholeEvalMode);
	    	    returnHt.put("ShowDayWeekMonth",showDayWeekMonth);
	    	    returnHt.put("GradeSameNotSubmit",gradeSameNotSubmit);
	    	    
	    	    returnHt.put("PointScoreFromKeyEvent",PointScoreFromKeyEvent);
	    	    returnHt.put("MustFillOptionsList",MustFillOptionsList);
	    	    returnHt.put("WarnRoleScopeList",WarnRoleScopeList);
	    	    returnHt.put("CheckInvalidGrade",CheckInvalidGrade);
	    	    returnHt.put("zeroByNull",zeroByNull);
	    	    returnHt.put("InvalidGrade",InvalidGrade);
	    	    returnHt.put("ShowBasicInfo",ShowBasicInfo);
	    	    returnHt.put("BasicInfoItem",BasicInfoItem);
	    	    returnHt.put("LockMGradeColumn",LockMGradeColumn);
	    	    
	    	    returnHt.put("TaskSupportAttach",TaskSupportAttach);
	    	    returnHt.put("GradeByBodySeq",GradeByBodySeq);
	    	    returnHt.put("AllowSeeAllGrade",AllowSeeAllGrade);
	    	    returnHt.put("TotalAppFormula",TotalAppFormula);
	    	    returnHt.put("MailTogoLink",MailTogoLink);
	    	    returnHt.put("RadioDirection",RadioDirection);
	    	    
	    	    returnHt.put("TargetGroupByItem",TargetGroupByItem);
	    	    returnHt.put("TargetGroupItems",TargetGroupItems);
	    	    
	    	    returnHt.put("PointEvalType",PointEvalType);
	    	    returnHt.put("MutiScoreOnePageOnePoint",MutiScoreOnePageOnePoint);
	    	    returnHt.put("ShowEmployeeRecord",ShowEmployeeRecord);
	    	    returnHt.put("ObjsFromCard", ObjsFromCard);
	    	    returnHt.put("MenRefDeptTmpl", MenRefDeptTmpl);
	    	    returnHt.put("BodysFromCard", BodysFromCard);
	    		returnHt.put("ScoreFromItem", ScoreFromItem);
	    	    returnHt.put("AdjustEvalGradeStep",AdjustEvalGradeStep);
	    	    returnHt.put("VerifySameScore",VerifySameScore);
	    		returnHt.put("ShowEvalDirector",ShowEvalDirector);
	    		returnHt.put("ShowGrpOrder",ShowGrpOrder);
	    		returnHt.put("AdjustEvalRange",AdjustEvalRange);
	    		returnHt.put("AllowAdjustEvalResult",AllowAdjustEvalResult);
	    		returnHt.put("AdjustEvalDegreeType",AdjustEvalDegreeType);
	    		returnHt.put("AdjustEvalDegreeNum",AdjustEvalDegreeNum);
	    		returnHt.put("CalcMenScoreRefDept",CalcMenScoreRefDept);	    		
	    		returnHt.put("ShowLeaderEval",ShowLeaderEval);
	    		returnHt.put("TargetCalcItem",TargetCalcItem);
	    		returnHt.put("TargetMustFillItem",TargetMustFillItem);
	    		returnHt.put("TargetUsePrevious",TargetUsePrevious);
	    		returnHt.put("NoApproveTargetCanScore",NoApproveTargetCanScore);
	    		returnHt.put("DynaBodyToPoint",DynaBodyToPoint);
	    		returnHt.put("EvalOutLimitStdScore",EvalOutLimitStdScore);
	    		returnHt.put("EvalOutLimitScoreOrg",EvalOutLimitScoreOrg);
	    		returnHt.put("ProcessNoVerifyAllScore", ProcessNoVerifyAllScore);
	    		returnHt.put("VerifyRule", VerifyRule);
	    		returnHt.put("ShowSumRow",ShowSumRow);
	    		returnHt.put("CanSaveAllObjsScoreSame",CanSaveAllObjsScoreSame);
	    		returnHt.put("allowLeadAdjustCard",allowLeadAdjustCard);
	    		returnHt.put("allowSeeLowerGrade",allowSeeLowerGrade);		
	    		returnHt.put("TargetAllowAdjustAfterApprove",targetAllowAdjustAfterApprove);
	    		returnHt.put("voteDecimal",voteDecimal);
	    		returnHt.put("voteScoreDecimal",voteScoreDecimal);
	    		returnHt.put("showDeductionCause",showDeductionCause);
	    		returnHt.put("relatingTargetCard",relatingTargetCard);
	    		returnHt.put("showYPTargetCard", showYPTargetCard);
	    		returnHt.put("showIndicatorContent",showIndicatorContent);
	    		returnHt.put("showIndicatorRole",showIndicatorRole);
	    		returnHt.put("showIndicatorDegree",showIndicatorDegree);
	    		returnHt.put("targetAppMode",targetAppMode);	
	    		returnHt.put("targetMakeSeries",targetMakeSeries);
	    		returnHt.put("taskAdjustNeedNew",taskAdjustNeedNew);
	    		returnHt.put("taskCanSign",taskCanSign);
	    		returnHt.put("taskNeedReview",taskNeedReview);
	    		
	    		returnHt.put("PublicPointCannotEdit",PublicPointCannotEdit);
	    		returnHt.put("xiFormula",xiFormula);
	    		returnHt.put("DescriptiveWholeEval",DescriptiveWholeEval);
	    		returnHt.put("SelfEvalNotScore",SelfEvalNotScore);
	    		returnHt.put("noteIdioGoal",noteIdioGoal);
	    		returnHt.put("formulaSql",formulaSql);
	    		returnHt.put("deviationScoreUsed",deviationScoreUsed);
	    		returnHt.put("formulaDeviationSql",formulaDeviationSql);
	    		returnHt.put("BlankScoreOption", BlankScoreOption);
	    		returnHt.put("BlankScoreUseDegree",BlankScoreUseDegree);
	    		returnHt.put("DepartmentLevel",DepartmentLevel);
	    		returnHt.put("isShowSubmittedScores", isShowSubmittedScores);
	    		returnHt.put("showAppraiseExplain", showAppraiseExplain);
	    		returnHt.put("isShowSubmittedPlan", isShowSubmittedPlan);
	    		returnHt.put("performanceType", performanceType);
	    		returnHt.put("isShowOrder", isShowOrder);
	    		returnHt.put("isEntireysub", GATIShowDegree);
	    		returnHt.put("SelfScoreInDirectLeader", SelfScoreInDirectLeader);
	    		returnHt.put("GATIShowDegree", GATIShowDegree);
	    		returnHt.put("ScoreBySumup", ScoreBySumup);
	    		returnHt.put("AllowUploadFile", NullToFalse(AllowUploadFile));
	    		returnHt.put("TargetCompleteThenGoOn", NullToFalse(TargetCompleteThenGoOn));
	    		returnHt.put("ShowOneMark", ShowOneMark);
	    		returnHt.put("SameResultsOption", NullToFalse(sameResultsOption));
	    		returnHt.put("PerSet", perSet);
	    		returnHt.put("EvalClass", evalClass);
	    		returnHt.put("ShowIndicatorDesc", NullToFalse(showIndicatorDesc));
	    		returnHt.put("ShowNoMarking", NullToFalse(showNoMarking));
	    		returnHt.put("NodeKnowDegree", NullToFalse(strNodeKnowDegree));
	    		returnHt.put("WholeEval", NullToFalse(strWholeEval));
	    		returnHt.put("MustFillWholeEval",MustFillWholeEval);
	    		returnHt.put("KeepDecimal", KeepDecimal);
	    		returnHt.put("GradeClass", GradeClass);
	    		returnHt.put("ScoreShowRelatePlan",ScoreShowRelatePlan);
	    		returnHt.put("SummaryFlag", SummaryFlag);
	    		returnHt.put("scoreflag", scoreflag);
	    		returnHt.put("addSubtractType", addSubtractType);
	    		returnHt.put("limitation", limitation);
	    		returnHt.put("limitrule", limitrule);
	    		returnHt.put("fineMax", fineMax);
	    		
	    		returnHt.put("SameAllScoreNumLess", SameAllScoreNumLess);
	    		returnHt.put("FineRestrict", bFineRestrict);
	    		returnHt.put("fineMaxMap", fineMaxMap);
	    		returnHt.put("AutoCalcTotalScoreAndOrder", AutoCalcTotalScoreAndOrder);
	    		returnHt.put("MutiScoreGradeCtl", MutiScoreGradeCtl);
	    		returnHt.put("mitiScoreMergeSelfEval", mitiScoreMergeSelfEval);
	    		returnHt.put("CheckGradeRange",CheckGradeRange);
	    		returnHt.put("ReaderType",ReaderType);
	    		returnHt.put("BadlyRestrict", BadlyRestrict);
	    		returnHt.put("BadlyMax", BadlyMax);
	    		returnHt.put("BadlyMap", BadlyMap);
	
	    		returnHt.put("PerSetShowMode", perSetShowMode);
	    		returnHt.put("PerSetStatMode", perSetStatMode);
	    		returnHt.put("StatStartDate", StatStartDate);
	    		returnHt.put("StatEndDate", StatEndDate);
	    		returnHt.put("StatCustomMode", statCustomMode);
	    		returnHt.put("ScoreNumPerPage", scoreNumPerPage);
	    		
	    		
	    		returnHt.put("UnLeadSingleAvg",UnLeadSingleAvg);
	    		
	    		returnHt.put("VotesNum", VotesNum);
	    		returnHt.put("KeyEventEnabled", keyEventEnabled);
	    		returnHt.put("scoreWay", scoreWay);
	    		returnHt.put("degreeShowType", degreeShowType);
	    		
	    		returnHt.put("isvalidate", "false");
	    		returnHt.put("scoreRangeList",new ArrayList());
	    		returnHt.put("SpByBodySeq",SpByBodySeq);
	    	 }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    
    
    /**
     * 保存计算规则
     * @param ruleMap
     */
    public void saveComputeRule(HashMap ruleMap)
    {
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		ArrayList paramList = new ArrayList();
 		    XPath xPath = XPath.newInstance("/PerPlan_Parameter/CalcRule");
 		    Element element=null;
 		    element = (Element) xPath.selectSingleNode(this.a_doc);  
 		    if(element==null)
 		    {
 		    	Element root=this.a_doc.getRootElement();
 		    	Element cal=new Element("CalcRule");
 		    	root.addContent(cal);
 		    	element=cal;
 		    	
 		    }
 		    Set keySet=ruleMap.keySet();
 		    boolean ispointScoreFromKey=false;
 		    for(Iterator t=keySet.iterator();t.hasNext();)
 		    {
 		    	String key=(String)t.next();
 		    	if("PointScoreFromKeyEvent".equalsIgnoreCase(key))
 		    	{
 		    		ispointScoreFromKey=true;
 		    		continue;
 		    	}
	 		    if (element.getAttribute(key) != null)
	 		    {
	 		    	element.getAttribute(key).setValue((String)ruleMap.get(key)==null?"":(String)ruleMap.get(key));
	 		    } else
	 		    {
	 		    	element.setAttribute(key, (String)ruleMap.get(key)==null?"":(String)ruleMap.get(key));//防止塞null报错  zhaoxg add 2014-10-25
	 		    }   
 		    }
 		    
 		    if(ispointScoreFromKey)
 		    {
 		    	  xPath = XPath.newInstance("/PerPlan_Parameter");
 	 		      element=null;
 	 		      element = (Element) xPath.selectSingleNode(this.a_doc); 
 	 		      if( element.getAttribute("PointScoreFromKeyEvent")!=null)
 	 		    	  element.getAttribute("PointScoreFromKeyEvent").setValue((String)ruleMap.get("PointScoreFromKeyEvent"));
 	 		      else
 	 		    	  element.setAttribute("PointScoreFromKeyEvent", (String)ruleMap.get("PointScoreFromKeyEvent"));

 		    }
 		    
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String sql = "update per_plan set parameter_content=? where plan_id=?";
			paramList.add(outputter.outputString(this.a_doc));
			paramList.add(Integer.parseInt(this.planid));
			dao.update(sql, paramList);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    /**
     * 修改节点属性值
     * @param nodepath 节点路径
     * @param value 属性值
     * @param nodename 节点名称
     * 
     *      * */
    public void refreshNodeAttribute(String nodePath,String nodename,String attributename,String value){
    	 XPath xPath;
		try {
			xPath = XPath.newInstance(nodePath);
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList paramList = new ArrayList();
			Element element=null;
			    element = (Element) xPath.selectSingleNode(this.a_doc);  
			    if(element==null)
	 		    {
	 		    	Element root=this.a_doc.getRootElement();
	 		    	Element cal=new Element(nodename);
	 		    	root.addContent(cal);
	 		    	element=cal;
	 		    	
	 		    }
			    if(element.getAttribute(attributename)!=null){
			    	element.getAttribute(attributename).setValue(value);
			    }else{
			    	element.setAttribute(attributename, value);
			    }
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			paramList.add(outputter.outputString(this.a_doc));
			paramList.add(Integer.parseInt(this.planid));
			dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
		} catch (Exception e) {			
			e.printStackTrace();
		}
    	
    }
    
    

    public void saveAttribute(String node_str, String attributeName, String value)
    {

		try
		{
			node_str = node_str.replaceAll("／", "/");
		    ContentDAO dao = new ContentDAO(this.conn);
		    ArrayList paramList = new ArrayList();
		    XPath xPath = XPath.newInstance("/" + node_str);
		    Element element=null;
		    element = (Element) xPath.selectSingleNode(this.a_doc);  
		    if(element==null)
		    {
		    	Element root=this.a_doc.getRootElement();
		    	Element cal=new Element(node_str.split("/")[1]);
		    	root.addContent(cal);
		    	element=cal;
		    	
		    }
		    
		    if (element.getAttribute(attributeName) != null)
		    {
		    	element.getAttribute(attributeName).setValue(value);
		    } else
		    {
		    	element.setAttribute(attributeName, value);
		    }
		    XMLOutputter outputter = new XMLOutputter();
		    Format format = Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
		    paramList.add(outputter.outputString(this.a_doc));
		    paramList.add(Integer.parseInt(this.planid));
		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);;
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
    }
    
    /**
     * 此方法适合在一个xml中添加或更新一个节点下有多个 相同节点的数据，如 计算规则中定义分指标值范围
     * nodename:节点名称
     * nodeAttribute:节点属性值
     * childname：子节点名称
     * childlist 属性节点值
     * 
     * @author JinChunhai
     */
    public void saveHasMoreChildsNode(String nodename,String childname,ArrayList nodeAttribute,ArrayList childlist,String node_str,ArrayList childattribute)
    {   	
    	XPath xPath;
		try 
		{
			ContentDAO dao = new ContentDAO(this.conn);
		    ArrayList paramList = new ArrayList();
			xPath = XPath.newInstance("/"+node_str);
			Element element=null;
		    element = (Element) xPath.selectSingleNode(this.a_doc);  
		    if(element!=null)
		    {
		    	element.removeChildren(childname);
		    }
		    if(element==null)
		    {
		    	Element root=this.a_doc.getRootElement();
		    	Element cal=new Element(nodename);
		    	root.addContent(cal);
		    	element=cal;		    	
		    }
		   
		    for(int i=0;i<nodeAttribute.size();i++)
		    {
		    	LazyDynaBean bean=(LazyDynaBean)nodeAttribute.get(i);
		    	String atributename=(String)bean.get("attributename");
		    	String attributevalue=(String)bean.get("attributevalue");
		    	if (element.getAttribute(atributename) != null)
		 		   element.getAttribute(atributename).setValue(attributevalue);
		 		else
		 		   element.setAttribute(atributename, attributevalue);		 		    
		    }
			for(int i=0;i<childlist.size();i++)
			{
				LazyDynaBean bean=(LazyDynaBean)childlist.get(i);
			//	String id=(String)bean.get("id");
				Element newEl=new Element(childname);
			//	if(id!=null && id.trim().length()>0)
			//		newEl.setAttribute("id", id);
				for(int t=0;t<childattribute.size();t++)
				{
					String butevalue=(String)bean.get((String)childattribute.get(t));
					newEl.setAttribute((String)childattribute.get(t), butevalue);
				}
				element.addContent(newEl);
			}
			
			XMLOutputter outputter = new XMLOutputter();
		    Format format = Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);		   
		    paramList.add(outputter.outputString(this.a_doc));
		    paramList.add(Integer.parseInt(this.planid));
		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);;
		    
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		    
    	
    }
    
    /**
      * 保存在考核计划中设置的参数集合
      * 
      * @author jch
     */
    public void saveAttributes(Connection con, HashMap rootAttributesMap, HashMap fineAttributesMap, HashMap badlyAttributesMap, ArrayList mustFillList, ArrayList warnRoleScopeList,HashMap evaluateMap, String planId)
    {
    	ContentDAO dao = new ContentDAO(con);
	    ArrayList paramList = new ArrayList();
		String sql = "select parameter_content from per_plan where plan_id=" + planId;
		ResultSet rs = null;
		String xmlContext = "";
		String parameter_content = "";
		Element child = null;
		Element root = null;
		Collection keySet = null;
	
		try
		{
		    rs = dao.search(sql);
	
		    if (rs.next())
		    {
		    	xmlContext = Sql_switcher.readMemo(rs, "parameter_content"); // PubFunc.nullToStr(rs.getString("parameter_content"));
		    }
	
		    if (!"".equals(xmlContext.trim()))// update
		    {
				Document doc = PubFunc.generateDom(xmlContext);
				String xpath = "//PerPlan_Parameter";
				XPath xpath_ = XPath.newInstance(xpath);
				root = (Element) xpath_.selectSingleNode(doc);
		
				List attriNames = root.getAttributes();
				for (int i = 0; i < attriNames.size(); i++)
				{
				    Attribute attri = (Attribute) attriNames.get(i);
				    // System.out.println(attri.getName() + "--" +
				    // attri.getValue() + "--" +
				    // rootAttributesMap.get(attri.getName()));
				    if (rootAttributesMap.get(attri.getName()) != null)
				    {
						root.getAttribute(attri.getName()).setValue((String) rootAttributesMap.get(attri.getName()));
						rootAttributesMap.remove(attri.getName());
				    }			
				}
				if(rootAttributesMap.size()>0)//修改时候把新加的参数也保存进去
				{
				    keySet = rootAttributesMap.keySet();
				    for (Iterator iter = keySet.iterator(); iter.hasNext();)
				    {
						String attriKey = (String) iter.next();
						String attriValue = (String) rootAttributesMap.get(attriKey);
						if(attriValue==null || attriValue.trim().length()<=0)
							attriValue = "";
						root.setAttribute(attriKey, attriValue);
				    }
				}
				
		
				if (fineAttributesMap.size() > 0)
				{
				    child = root.getChild("FineMax");
				    if (child != null)
				    	root.removeChild("FineMax");
		
				    child = new Element("FineMax");
				    keySet = fineAttributesMap.keySet();
				    for (Iterator iter = keySet.iterator(); iter.hasNext();)
				    {
						String attriKey = (String) iter.next();
						child.setAttribute(attriKey, (String) fineAttributesMap.get(attriKey));
				    }
				    root.addContent(child);
		
				}
		
				if (badlyAttributesMap.size() > 0)
				{
				    child = root.getChild("BadlyMax");
				    if (child != null)
				    	root.removeChild("BadlyMax");
				    child = new Element("BadlyMax");
				    keySet = badlyAttributesMap.keySet();
				    for (Iterator iter = keySet.iterator(); iter.hasNext();)
				    {
						String attriKey = (String) iter.next();
						child.setAttribute(attriKey, (String) badlyAttributesMap.get(attriKey));
				    }
				    root.addContent(child);
		
				}
				
				if (evaluateMap!=null)//慧聪网需求  保存结果反馈方式参数  zhaoxg 2014-6-25
				{
					if(evaluateMap.get("evaluate_str")!=null)
					{
						child = root.getChild("evaluate");
						if (child != null)
							root.removeChild("evaluate");
						child = new Element("evaluate");
						child.setText((String)evaluateMap.get("evaluate_str"));
						child.setAttribute("blind_point", (String) evaluateMap.get("blind_point"));  
						root.addContent(child);
					}
				}
				
				// 评分说明必填高级规则
				if(mustFillList!=null && mustFillList.size()>0)
				{
					child = root.getChild("MustFillOptions");
		        	if(child!=null)		        	
		        		root.removeChild("MustFillOptions");		       	
		        	child = new Element("MustFillOptions");
		        	for (int i = 0; i < mustFillList.size(); i++)
					{
					    Element element = new Element("MustFillOption");
					    
					    LazyDynaBean bean = (LazyDynaBean) mustFillList.get(i);
					    String flag = (String) bean.get("Flag");
					    String isValid = (String) bean.get("IsValid");
					    String degreeId =null; 
					    if(bean.get("DegreeId")!=null)
					    	degreeId=(String)bean.get("DegreeId");			    
					    String pointId =null;
					    if(bean.get("PointId")!=null)
					    	pointId=(String)bean.get("PointId");			    
					    element.setAttribute("Flag", flag);
					    element.setAttribute("IsValid", isValid);
					    if (pointId == null) {
					    	element.setAttribute("DegreeId", degreeId);
					    } else {
					    	element.setAttribute("PointId", pointId);
					    }
					    child.addContent(element);
					}
		        	root.addContent(child);
				}
				
				// 预警提醒设置
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					child = root.getChild("Warns");
		        	if(child!=null)		        	
		        		root.removeChild("Warns");		       	
		        	child = new Element("Warns");
		        	for (int i = 0; i < warnRoleScopeList.size(); i++)
					{
					    Element element = new Element("Warn");
					    LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
					    String opt = (String) bean.get("opt");
					    String delayTime = (String) bean.get("delayTime");
					    String roleScope = (String) bean.get("roleScope");			    
					    
					    element.setAttribute("opt", opt);
					    element.setAttribute("delayTime", delayTime);
					    element.setAttribute("roleScope", roleScope);			   
					    child.addContent(element);
					}
		        	root.addContent(child);
				}else
				{
					// 删除节点 Warns
	        		root.removeChild("Warns");
				}
				
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				parameter_content = outputter.outputString(doc);
				
		    } else
		    // add
		    {
				root = new Element("PerPlan_Parameter");
		
				keySet = rootAttributesMap.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();)
				{
				    String attriKey = (String) iter.next();
				    root.setAttribute(attriKey, (String) rootAttributesMap.get(attriKey));
				}
		
				// 添加FineMax子节点
				child = new Element("FineMax");
				keySet = fineAttributesMap.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();)
				{
				    String attriKey = (String) iter.next();
				    child.setAttribute(attriKey, (String) fineAttributesMap.get(attriKey));
				}
				if (keySet.size() > 0)
				    root.addContent(child);
		
				// 添加BadlyMax子节点
				child = new Element("BadlyMax");
				keySet = badlyAttributesMap.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();)
				{
				    String attriKey = (String) iter.next();
				    child.setAttribute(attriKey, (String) badlyAttributesMap.get(attriKey));
				}
				if (keySet.size() > 0)
				    root.addContent(child);
				

				if (evaluateMap!=null)//慧聪网需求  保存结果反馈方式参数  zhaoxg 2014-6-25
				{
					if(evaluateMap.get("evaluate_str")!=null)
					{
						child = root.getChild("evaluate");
						if (child != null)
							root.removeChild("evaluate");
						child = new Element("evaluate");
						child.setText((String)evaluateMap.get("evaluate_str"));
						child.setAttribute("blind_point", (String) evaluateMap.get("blind_point"));  
						root.addContent(child);
					}
				}
				
				
				
				// 评分说明必填高级规则
				if(mustFillList!=null && mustFillList.size()>0)
				{							       	
		        	child = new Element("MustFillOptions");
		        	for (int i = 0; i < mustFillList.size(); i++)
					{
					    Element element = new Element("MustFillOption");
					    LazyDynaBean bean = (LazyDynaBean) mustFillList.get(i);
					    String flag = (String) bean.get("Flag");
					    String isValid = (String) bean.get("IsValid");
					    String degreeId =null;
					    if(bean.get("DegreeId")!=null)	
					    	degreeId=(String) bean.get("DegreeId");			    
					    String pointId = (String) bean.get("PointId");			    
					    
					    element.setAttribute("Flag", flag);
					    element.setAttribute("IsValid", isValid);
					    if (degreeId != null) {
					    	element.setAttribute("DegreeId", degreeId);
					    } else {
					    	element.setAttribute("PointId", pointId);
					    }
					    child.addContent(element);
					}
		        	root.addContent(child);
				}
				
				// 预警提醒设置
				if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
				{
					child = root.getChild("Warns");
		        	if(child!=null)		        	
		        		root.removeChild("Warns");		       	
		        	child = new Element("Warns");
		        	for (int i = 0; i < warnRoleScopeList.size(); i++)
					{
					    Element element = new Element("Warn");
					    LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
					    String opt = (String) bean.get("opt");
					    String delayTime = (String) bean.get("delayTime");
					    String roleScope = (String) bean.get("roleScope");			    
					    
					    element.setAttribute("opt", opt);
					    element.setAttribute("delayTime", delayTime);
					    element.setAttribute("roleScope", roleScope);			   
					    child.addContent(element);
					}
		        	root.addContent(child);
				}else
				{
					// 删除节点 Warns
	        		root.removeChild("Warns");
				}
		
				Document myDocument = new Document(root);
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				parameter_content = outputter.outputString(myDocument);
		    }
	
		    // 不管是添加还是更新操作最后都获得parameter_content的新内容对per_plan表进行更新操作
		    paramList.add(parameter_content);
		    paramList.add(Integer.parseInt(planId));
		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
    }

    /**
         * 查询对应的参数表
         * 
         * @param con
         * @param planId
         *                绩效考评计划
         */
    public LoadXml(Connection con, String planId)
    {
	ContentDAO dao = new ContentDAO(con);
	String sql = "select parameter_content from per_plan where plan_id=" + planId;
	ResultSet rs = null;
	try
	{

		this.conn=con;
		this.planid=planId;
	    String xmlContext = "";
	    if(planId!=null && planId.length()>0)//如果planid为空 也可以得到默认的参数 fzg改
	    {
		    rs = dao.search(sql);
		    if (rs.next())		    
		    	xmlContext = Sql_switcher.readMemo(rs, "parameter_content"); 		    
	    }	    
	    if (!"".equals(xmlContext.trim()))
	    {
		Element root;
		try
		{
		    this.a_doc = PubFunc.generateDom(xmlContext);
		    root =  this.a_doc.getRootElement();
		    Hashtable tempHash = new Hashtable();
		    tempHash = getElements(root);
		    returnHt = tempHash;
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	    } else
	    {
	    String xiFormula="";    //绩效系数公式
	    String DescriptiveWholeEval="True";  //显示描述性总体评价，默认为 True
	    String SelfEvalNotScore="False";  //自我评价不显示打分
		String strNodeKnowDegree = ""; // 了解程度
		String strWholeEval = ""; // 总体评价
		String MustFillWholeEval="False";  //总体评价必填
		String KeepDecimal = "1"; // 小数位
		String ScoreShowRelatePlan="False"; //多人评分显示引入计划得分
		String GradeClass = "0";
		String SummaryFlag = "False";// 个人总结报告
		String showTotalScoreSort = "False"; // 不显示总分排名
		String noteIdioGoal="False"; //显示个人目标
		String showNoMarking = "False"; // 显示不打分
		String showIndicatorDesc = "False"; // 显示指标说明
		String perSet = ""; // 绩效子集
		String evalClass = "0";//总体评价的的等级分类
		String sameResultsOption = "1"; // 核对象指标结果是否全部相同 1: 可以保存 2: 不能保存  3 指定不能保存的标度	
		String NoCanSaveDegrees="";  //结果全相同时不能保存的标度,(标度代码)‘A,D…’等. 上面指标为3时有效	
		/** 打分标识 */
		String DegreeShowType="1";  //1-标准标度 2-指标标度 3-采集标准标度,显示指标标度内容
		String scoreflag = "2";// =2混合，=1标度(默认值=混合)
		String addSubtractType="1";  //加扣分处理方式  1:加扣分  2:加分  3：扣分
		String limitation = "-1";// =-1不转换,模板中最高标度的数目
		// (大于0小于1为百分比，大于1为绝对数)

		String bFineRestrict = "False";// 是否限制高分比例
		String fineMax = "-1"; // 指标得分为A(优秀)的个数(大于0小于1为百分比，大于1为绝对数)
		// -1表示每个指标分别设置，数据在FineMax节点
		HashMap fineMaxMap = new HashMap(); // 表示每个指标得最大数控制

		String BadlyRestrict = "False";// 是否限制低分比例
		String BadlyMax = "-1"; // 指标得低分的个数(大于0小于1为百分比，大于1为绝对数)
		// -1表示每个指标分别设置，数据在BadlyMax节点
		HashMap BadlyMap = new HashMap(); // 表示每个指标得最大数控制

		String SameAllScoreNumLess = "0"; // 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
		String limitrule = "1";// 分值转标度规则（1-就高 2-就低 3-就近就高（默认值））
		String perSetShowMode = "1"; // 绩效子集显示方式 1-明细项，2-合计项 或 3-两者者显
		String perSetStatMode = "0"; // 绩效子集统计方式
		// 1-年、2-月、3-季度、4-半年、9-时间段
		String StatStartDate = ""; // 统计方式为9有效，绩效子集统计时间段
		String StatEndDate = ""; // 统计方式为9有效，绩效子集统计时间段
		String statCustomMode = "True"; // 显示绩效子集统计自定义 True
		// False
		String scoreNumPerPage = "0"; // BS打分时每页的人数，0为不限制
		String ShowOneMark = "False"; // BS打分时显示统一打分的指标，以便参考 Boolean,
		// 默认为False
		String ScoreBySumup = "False"; // BS个人总结没填写，主体为其打分时不能提交
		String AllowUploadFile = "True"; // 支持附件上传
		String TargetCompleteThenGoOn = "False";//目标卡填写完整才允许提交（个性化任务、绩效报告）参数
		String GATIShowDegree = "False"; // BS 综合测评表中指标的评分显示为标度
		String SelfScoreInDirectLeader = "False"; // 整型（Boolean为兼容）：0和False 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
		String isShowOrder = "false"; // 显示排名
		String AutoCalcTotalScoreAndOrder = "false"; // 是否自动计算总分和排名
		
		String isEntireysub = "false"; // 提交是否需要必填
		String performanceType = "0"; // 考核形式 0：绩效考核 1：民主评测
		String ReaderType = "0"; // 机读类型:0光标阅读机(默认),1扫描仪
		
		String isShowSubmittedPlan = "True";
		String showAppraiseExplain = "true"; // 综合评测表是否显示评测说明
		String isShowSubmittedScores = "true"; // 提交后的分数是否显示
		String MutiScoreGradeCtl = "FALSE"; // BS多人打分时是否等级控制
		String mitiScoreMergeSelfEval = "False"; // 多人打分时同时显示自我评价
		String CheckGradeRange="0";  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
		//票数统计
		String VotesNum="";  //票数
		
		//计算规则
		String UnLeadSingleAvg="False";  //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
		//关键事件参与总分修正
		String keyEventEnabled="False";
		String formulaSql="";
		String formulaDeviationSql="";
		String deviationScoreUsed="";
		String BlankScoreOption = "0"; // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
		String BlankScoreUseDegree="A";  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…
		String DepartmentLevel = ""; // 部门层级
		
		returnHt.put("DegreeShowType", DegreeShowType);
		String scoreWay = "1"; // 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分
		String degreeShowType = "1";// 标度显示形式(1-标准标度内容 2-指标标度内容 3-采集标准标度,显示指标标度内容）
		String PublicPointCannotEdit = "False"; // 共性指标员工不能编辑, 默认为 False
                                                        // 能编辑
		String gradeFormula = ""; //考核评估 计算公式 等级 eg:"1:KqAnalyseData"  equals: "2:公式"
		// 目标卡制订支持几级审批
		String targetMakeSeries = "1";
		// 任务调整需新建任务项
		String taskAdjustNeedNew = "False";
		// 每项任务可签批
		String taskCanSign = "False";
		// 每项任务需回顾总结
		String taskNeedReview = "False";  	
//		 显示考核指标内容
		String showIndicatorContent = "False";
		// 显示考核指标评分原则
		String showIndicatorRole = "False";
		// 显示考核指标标度说明
		String showIndicatorDegree = "False";
		// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
		String targetAppMode = "0";   
		//关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
		String relatingTargetCard = "1";	
		String showYPTargetCard = "False";//显示已自评目标卡:True，  不显示：False（默认）
		//显示扣分原因(Ture, False(默认)) 现改为启用评分说明
		String showDeductionCause = "False";
		//扣分原因是否必填
		String MustFillCause="False";
		
		//目标卡批准后允许再调整, (True, False, 默认为True)
		String targetAllowAdjustAfterApprove="True";		
		//允许领导制定及调整目标卡 默认为False
		String  allowLeadAdjustCard = "False"; 		    
		//允许查看下级对考核对象评分 默认为False
		String  allowSeeLowerGrade = "False";  
		//相同时能否保存(Ture, False),默认为 True
		String  CanSaveAllObjsScoreSame="True";
		String  ShowSumRow="False"; //显示合计行		
		
		String voteScoreDecimal ="0";//总分精度		    
		String voteDecimal ="2";//权重精度		
		
		String CheckInvalidGrade="False"; //无效标度代码
		String zeroByNull="false";
		String InvalidGrade="";           //是否选择使用无效票数, (True, False；默认为False)
		
		
		String evalCanNewPoint = "False";//评估打分允许新增考核指标 (True, False默认为False)
		String targetTraceEnabled  = "False";//目标卡跟踪显示和采集指标开关
		String targetTraceItem = ""; //目标卡跟踪显示指标
		String targetCollectItem = ""; //目标卡采集指标
		String targetCollectItemMust = ""; //目标卡采集指标
		String DynaBodyToPoint="False";  //动态主体权重控制到指标/任务True, False, 默认为 False
		String TargetDefineItem="";   //目标卡指标
		String NoShowTargetAdjustHistory="False";  //打分时不显示任务调整历史
		String AllowLeaderTrace="False";   //允许领导制定及批准跟踪指标, True(默认) False
		String ProcessNoVerifyAllScore="True";  //报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)
		String VerifyRule = "="; //报批 批准进行总分校验 校验规则 (=,<=)模板总分 默认为=
		String EvalOutLimitStdScore="False";     //评分时得分不受标准分限制True, False, 默认为 False;都加
		String EvalOutLimitScoreOrg="False";     //评分时不受考核机构限制True, False, 默认为 False;都加
		String ShowBackTables="";//考核计划指定登记表
		String IsLimitPointValue="False";//限定目标卡项目下的任务权重|分值之和等于项目的权重|任务 默认为 False;
		String NoApproveTargetCanScore="False";     //目标卡未审批也允许打分 True, False, 默认为 False
		String TargetCalcItem="";                   //目标卡计算指标属性，P04中指标，以逗号分隔，顺序从前到后
		String TargetMustFillItem="";               //目标卡必填指标,   以逗号分隔
		String TargetUsePrevious="";                // 引入上期目标卡指标
		String ShowLeaderEval="False";              //本人查看绩效面谈显示领导对其的评价
		String AllowAdjustEvalResult="False"; //允许调整评估结果, True, False, 默认为 False
		String AdjustEvalDegreeType="0"; //调整使用标度 0=指标标度，1=等级标度.默认为0
		String AdjustEvalDegreeNum="0"; //调整浮动等级：整数值
		String CalcMenScoreRefDept="False"; //本次评分=个人指标得分*部门指标得分的权重和（目标考核和360°） True, False, 默认为 False	
		String AdjustEvalRange = "0"; //调整范围：0=指标，1=总分.默认为0
		String AdjustEvalGradeStep ="";//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
		String PointEvalType="0";  //360指标评分型式  0：下拉（默认）   1：单选
		String TaskSupportAttach="False"; //目标任务支持附件上传
		
		String GradeByBodySeq="False"; //按考核主体顺序号控制评分流程(True, False默认为False)
 	    String AllowSeeAllGrade="False"; //允许查看其它主体对考核对象评分(True, False默认为False)
 	    String TotalAppFormula = ""; // 总体评价的计算公式，默认为空
	    String MailTogoLink = "1"; // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件
	    String RadioDirection = "0";//“打分指标标度显示方式”设置为单选钮时，后面出现排列方式下拉选项，默认为“纵向”，也可选择“横向”
		
		String MutiScoreOnePageOnePoint="False";  //单题打分
		String TargetGroupByItem="False"; //目标卡按一级项目分值显示，True, False, 默认为 False
 	    String TargetGroupItems="";  //项目号1,组号1:A1,A3,A4; 项目号2,组号1:A1,A3,A4; 项目号3,组号2:A1,A3,A4;
 	    
 
		String GrpMenu1="";           //排名指标1，格式：字段名;层级
		String GrpMenu2="";           //排名指标2，格式：字段名;层级
		String Enabled="False";       //排名指标是否启用
		String ShowGrpOrder ="True";  //评分调整 显示排名
		String VerifySameScore = "False";//打分分数相同不能提交：True, False, 默认为 False
	    String ShowEvalDirector = "False";//评估结果中显示"负责人"指标：True, False, 默认为 False
	    String ScoreFromItem="False";//按项目权重逐级计算总分 True，False 默认为False；
		String BodysFromCard = "False"; //考核主体从机读卡读取(主体类别自动对应)
	    String MenRefDeptTmpl = ""; //本次评分=个人指标得分*部门指标得分的权重和时，部门模板。为空表示与当前计划相同。
	    String ObjsFromCard = "False"; //考核对象是否从机读卡读取(考核实施中不需要选择考核对象)
		String HandEval="FALSE";   //TRUE|FALSE 启动时是否手工打分
		String ShowEmployeeRecord="True";
		
		String ShowBasicInfo="False";   //360计划显示基本信息
 	    String BasicInfoItem="";        //基本信息指标
 	    String LockMGradeColumn="True"; //多人考评锁定指标列
 	    ArrayList MustFillOptionsList = new ArrayList(); // 评分说明必填高级规则
 	    ArrayList WarnRoleScopeList = new ArrayList(); // 预警提醒设置
		
 	    String PointScoreFromKeyEvent="False"; //指标评分优先取自关键事件
 	    String taskNameDesc="";
 	    String SpByBodySeq="False";//是否按考核主体顺序号进行审批
 	    
 	    String showDayWeekMonth = "1,2,3";//查看员工日志
 	    String mainbodyGradeCtl = "";//强制分布主体类别
 	    String allmainbodyGradeCtl = "";//强制分布主体类别
 	    String gradeSameNotSubmit="False";//等级不同分数相同不能提交
 	    String showHistoryScore = "False";//显示历次得分表
 	    String wholeEvalMode = "0";//总体评价录分方式0：录入等级1：录入分值
 	    String batchScoreImportFormula = "False" ;
 	    //----------------------------慧聪网需求   zhaoxg------------------------
 		String evaluate_str=""; //控制考评反馈方式
 		String blind_point="0"; //盲点 
 	    
 		returnHt.put("evaluate_str", evaluate_str);
 		returnHt.put("blind_point", blind_point);
 		//-----------------------------end-------------------------------------
 	    returnHt.put("BatchScoreImportFormula",batchScoreImportFormula);
 	    returnHt.put("WholeEvalMode",wholeEvalMode);
	    returnHt.put("ShowHistoryScore",showHistoryScore);
	    returnHt.put("ShowDayWeekMonth",showDayWeekMonth);
	    returnHt.put("MainbodyGradeCtl",mainbodyGradeCtl);
	    returnHt.put("AllMainbodyGradeCtl",allmainbodyGradeCtl);
	    returnHt.put("GradeSameNotSubmit",gradeSameNotSubmit);
 	    
 	    returnHt.put("PointScoreFromKeyEvent",PointScoreFromKeyEvent);
 	    returnHt.put("MustFillOptionsList",MustFillOptionsList);
 	    returnHt.put("WarnRoleScopeList",WarnRoleScopeList);
 	    returnHt.put("CheckInvalidGrade",CheckInvalidGrade);
 	    returnHt.put("zeroByNull",zeroByNull);
 	    returnHt.put("InvalidGrade",InvalidGrade);
 	    
 	    returnHt.put("ShowBasicInfo",ShowBasicInfo);
		returnHt.put("BasicInfoItem",BasicInfoItem);
		returnHt.put("LockMGradeColumn",LockMGradeColumn);
 	    
		returnHt.put("TaskSupportAttach",TaskSupportAttach);
		returnHt.put("GradeByBodySeq",GradeByBodySeq);
		returnHt.put("AllowSeeAllGrade",AllowSeeAllGrade);
		returnHt.put("TotalAppFormula",TotalAppFormula);
	    returnHt.put("MailTogoLink",MailTogoLink);
	    returnHt.put("RadioDirection",RadioDirection);
	    
		returnHt.put("TargetGroupByItem",TargetGroupByItem);
		returnHt.put("TargetGroupItems",TargetGroupItems);
		returnHt.put("MutiScoreOnePageOnePoint",MutiScoreOnePageOnePoint);
		returnHt.put("PointEvalType",PointEvalType);
		returnHt.put("ShowEmployeeRecord",ShowEmployeeRecord);
		returnHt.put("HandEval",HandEval);
	    returnHt.put("ObjsFromCard",ObjsFromCard);
	    returnHt.put("MenRefDeptTmpl",MenRefDeptTmpl);
	    returnHt.put("BodysFromCard",BodysFromCard);
	    returnHt.put("ScoreFromItem",ScoreFromItem);    
	    returnHt.put("AdjustEvalGradeStep",AdjustEvalGradeStep);
	    returnHt.put("VerifySameScore",VerifySameScore);
		returnHt.put("ShowEvalDirector",ShowEvalDirector);
		returnHt.put("ShowGrpOrder",ShowGrpOrder);
		returnHt.put("GrpMenu1",GrpMenu1);
		returnHt.put("GrpMenu2",GrpMenu2);
		returnHt.put("Enabled",Enabled);
		returnHt.put("AdjustEvalRange", AdjustEvalRange);
		returnHt.put("AllowAdjustEvalResult", AllowAdjustEvalResult);
		returnHt.put("AdjustEvalDegreeType", AdjustEvalDegreeType);
		returnHt.put("AdjustEvalDegreeNum", AdjustEvalDegreeNum);
		returnHt.put("CalcMenScoreRefDept", CalcMenScoreRefDept);		
		returnHt.put("ShowLeaderEval", ShowLeaderEval);
		returnHt.put("TargetCalcItem",TargetCalcItem);
		returnHt.put("TargetMustFillItem",TargetMustFillItem);
		returnHt.put("TargetUsePrevious",TargetUsePrevious);
		returnHt.put("NoApproveTargetCanScore",NoApproveTargetCanScore);		
		returnHt.put("TargetDefineItem",TargetDefineItem);
		returnHt.put("ThrowBaseNum", "0");
		returnHt.put("DynaBodyToPoint",DynaBodyToPoint);
		returnHt.put("DynaBodyToPoint",DynaBodyToPoint);
		returnHt.put("EvalOutLimitStdScore", EvalOutLimitStdScore);
		returnHt.put("EvalOutLimitScoreOrg", EvalOutLimitScoreOrg);
		returnHt.put("ProcessNoVerifyAllScore", ProcessNoVerifyAllScore);
		returnHt.put("VerifyRule", VerifyRule);
		returnHt.put("TargetTraceEnabled", NullToFalse(targetTraceEnabled));
		returnHt.put("EvalCanNewPoint", NullToFalse(evalCanNewPoint));
		returnHt.put("TargetTraceItem", targetTraceItem);
		returnHt.put("TargetCollectItem", targetCollectItem);
		returnHt.put("targetCollectItemMust", targetCollectItemMust);
		returnHt.put("NoShowTargetAdjustHistory", NoShowTargetAdjustHistory);
		returnHt.put("ShowSumRow",ShowSumRow);
		returnHt.put("CanSaveAllObjsScoreSame",CanSaveAllObjsScoreSame);
		returnHt.put("allowLeadAdjustCard",allowLeadAdjustCard);
		returnHt.put("allowSeeLowerGrade",allowSeeLowerGrade);		
		returnHt.put("TargetAllowAdjustAfterApprove",targetAllowAdjustAfterApprove);
		returnHt.put("voteDecimal",voteDecimal);
		returnHt.put("voteScoreDecimal",voteScoreDecimal);
		returnHt.put("showDeductionCause",showDeductionCause);
		returnHt.put("MustFillCause",MustFillCause);
		returnHt.put("relatingTargetCard",relatingTargetCard);
		returnHt.put("showYPTargetCard", showYPTargetCard);
		returnHt.put("showIndicatorContent",showIndicatorContent);
		returnHt.put("showIndicatorRole",showIndicatorRole);
		returnHt.put("showIndicatorDegree",showIndicatorDegree);
		returnHt.put("targetAppMode",targetAppMode);	
		returnHt.put("targetMakeSeries",targetMakeSeries);
		returnHt.put("taskAdjustNeedNew",taskAdjustNeedNew);
		returnHt.put("taskCanSign",taskCanSign);
		returnHt.put("taskNeedReview",taskNeedReview);
		returnHt.put("ShowTotalScoreSort", NullToFalse(showTotalScoreSort));
		returnHt.put("PublicPointCannotEdit",PublicPointCannotEdit);
		returnHt.put("GradeFormula",gradeFormula);
		returnHt.put("xiFormula",xiFormula);
		returnHt.put("DescriptiveWholeEval",DescriptiveWholeEval);
		returnHt.put("SelfEvalNotScore",SelfEvalNotScore);
		returnHt.put("noteIdioGoal",noteIdioGoal);
		returnHt.put("formulaSql",formulaSql);
		returnHt.put("formulaDeviationSql",formulaDeviationSql);
		returnHt.put("deviationScoreUsed",deviationScoreUsed);
		returnHt.put("BlankScoreOption", BlankScoreOption);
		returnHt.put("BlankScoreUseDegree",BlankScoreUseDegree);
		returnHt.put("DepartmentLevel",DepartmentLevel);
		returnHt.put("isShowSubmittedScores", isShowSubmittedScores);
		returnHt.put("showAppraiseExplain", showAppraiseExplain);
		returnHt.put("isShowSubmittedPlan", isShowSubmittedPlan);
		returnHt.put("performanceType", performanceType);
		returnHt.put("isShowOrder", isShowOrder);
		returnHt.put("isEntireysub", GATIShowDegree);
		returnHt.put("SelfScoreInDirectLeader", SelfScoreInDirectLeader);
		returnHt.put("GATIShowDegree", GATIShowDegree);
		returnHt.put("ScoreBySumup", ScoreBySumup);
		returnHt.put("AllowUploadFile", NullToFalse(AllowUploadFile));	
		returnHt.put("TargetCompleteThenGoOn", NullToFalse(TargetCompleteThenGoOn));	
		returnHt.put("ShowOneMark", ShowOneMark);
		returnHt.put("SameResultsOption", NullToFalse(sameResultsOption));
		returnHt.put("NoCanSaveDegrees",NoCanSaveDegrees);
		returnHt.put("PerSet", perSet);
		returnHt.put("EvalClass", evalClass);
		returnHt.put("ShowIndicatorDesc", NullToFalse(showIndicatorDesc));
		returnHt.put("ShowNoMarking", NullToFalse(showNoMarking));
		returnHt.put("NodeKnowDegree", NullToFalse(strNodeKnowDegree));
		returnHt.put("WholeEval", NullToFalse(strWholeEval));
		returnHt.put("MustFillWholeEval",MustFillWholeEval);
		returnHt.put("KeepDecimal", KeepDecimal);
		returnHt.put("ScoreShowRelatePlan", ScoreShowRelatePlan);
		returnHt.put("GradeClass", GradeClass);
		returnHt.put("SummaryFlag", SummaryFlag);
		returnHt.put("scoreflag", scoreflag);
		returnHt.put("addSubtractType", addSubtractType);
		returnHt.put("limitation", limitation);
		returnHt.put("limitrule", limitrule);
		returnHt.put("fineMax", fineMax);
		returnHt.put("FineRestrict", bFineRestrict);
		returnHt.put("fineMaxMap", fineMaxMap);
		returnHt.put("AutoCalcTotalScoreAndOrder", AutoCalcTotalScoreAndOrder);
		returnHt.put("MutiScoreGradeCtl", MutiScoreGradeCtl);
		returnHt.put("mitiScoreMergeSelfEval", mitiScoreMergeSelfEval);
		returnHt.put("CheckGradeRange",CheckGradeRange);
		returnHt.put("ReaderType",ReaderType);
		returnHt.put("BadlyRestrict", BadlyRestrict);
		returnHt.put("BadlyMax", BadlyMax);
		returnHt.put("BadlyMap", BadlyMap);

		returnHt.put("PerSetShowMode", perSetShowMode);
		returnHt.put("PerSetStatMode", perSetStatMode);
		returnHt.put("StatStartDate", StatStartDate);
		returnHt.put("StatEndDate", StatEndDate);
		returnHt.put("StatCustomMode", statCustomMode);
		returnHt.put("ScoreNumPerPage", scoreNumPerPage);
		returnHt.put("SameAllScoreNumLess", SameAllScoreNumLess);
		
		returnHt.put("UnLeadSingleAvg",UnLeadSingleAvg);
		
		returnHt.put("VotesNum", VotesNum);
		returnHt.put("KeyEventEnabled", keyEventEnabled);
		returnHt.put("scoreWay", scoreWay);
		returnHt.put("degreeShowType", degreeShowType);
		returnHt.put("AllowLeaderTrace", AllowLeaderTrace);
	    returnHt.put("ShowBackTables", ShowBackTables);
	    returnHt.put("IsLimitPointValue",IsLimitPointValue);
	    
	    returnHt.put("isvalidate", "false");
		returnHt.put("scoreRangeList",new ArrayList());
		returnHt.put("TaskNameDesc", taskNameDesc);
		returnHt.put("SpByBodySeq",SpByBodySeq);
	    }

	} catch (SQLException ex)
	{
	    ex.printStackTrace();
	} finally {
		PubFunc.closeResource(rs);
	}
    }

    /**
         * 返回xml了解程度 总体评价，小数位 标识
         * 
         * @return
         */

    public Hashtable getDegreeWhole()
    {

	return this.returnHt;
    }

    /**
         * 根据定义的参数生成Hashtable
         * 
         * @param root
         * @return
         */
    public Hashtable getElements(Element root)
    {
    String xiFormula="";  //绩效系数公式
    String gradeFormula = "";  //考核评估 计算公式 等级 eg:"1:KqAnalyseData"  equals: "2:公式"
    String DescriptiveWholeEval="True";  //显示描述性总体评价，默认为 True	
	String strNodeKnowDegree = ""; // 了解程度
	String strWholeEval = ""; // 总体评价
	String MustFillWholeEval="False"; //总体评价必填
	String SelfEvalNotScore="False";  //自我评价不显示打分
	String HandEval="FALSE";   //TRUE|FALSE 启动时是否手工打分
	String GradeClass = "0";
	String SummaryFlag = "False";// 个人总结报告
	String noteIdioGoal="False"; //显示个人目标
	String bFineRestrict = "False";// 是否限制高分比例
	String showNoMarking = "False"; // 显示不打分
	String showTotalScoreSort = "False"; // 不显示总分排名
	String showIndicatorDesc = "False"; // 显示指标说明
	String perSet = ""; // 绩效子集
	String evalClass = "0";//总体评价的的等级分类
	String sameResultsOption = "1"; // 核对象指标结果是否全部相同 1: 可以保存 2: 不能保存  3 指定不能保存的标度	
	String NoCanSaveDegrees="";  //结果全相同时不能保存的标度,(标度代码)‘A,D…’等. 上面指标为3时有效	
	/** 打分标识 */
	String scoreflag = "2";// =2混合，=1标度(默认值=混合)  
	String addSubtractType="1";  //加扣分处理方式  1:加扣分  2:加分  3：扣分
	String DegreeShowType="1";  //1-标准标度 2-指标标度 3-采集标准标度,显示指标标度内容
	String limitation = "-1";// =-1不转换,模板中最高标度的数目
	String VotesNum = "";    //统计票数
	// (大于0小于1为百分比，大于1为绝对数)
	String fineMax = "-1"; // 指标得分为A(优秀)的个数(大于0小于1为百分比，大于1为绝对数)
	// -1表示每个指标分别设置，数据在FineMax节点
	String isShowSubmittedScores = "true"; // 提交后的分数是否显示
	HashMap fineMaxMap = new HashMap(); // 表示每个指标得最大数控制

	String BadlyRestrict = "False";// 是否限制低分比例
	String BadlyMax = "-1"; // 指标得低分的个数(大于0小于1为百分比，大于1为绝对数)
	// -1表示每个指标分别设置，数据在BadlyMax节点
	HashMap BadlyMap = new HashMap(); // 表示每个指标得最大数控制

	String SameAllScoreNumLess = "0"; // 总分相同的对象个数，不能等于和多于(等于0为不控制（默认值），大于0小于等于1为百分比，大于等于2为绝对数)
	String limitrule = "1";// 分值转标度规则（1-就高 2-就低 （默认值就高））
	//显示员工日志
	String ShowEmployeeRecord="True";
	String perSetShowMode = "1"; // 绩效子集显示方式 1-明细项，2-合计项 或 3-两者者显
	String perSetStatMode = "0"; // 绩效子集统计方式 1-年、2-月、3-季度、4-半年、9-时间段
	String StatStartDate = ""; // 统计方式为9有效，绩效子集统计时间段
	String StatEndDate = ""; // 统计方式为9有效，绩效子集统计时间段
	String statCustomMode = "True"; // 显示绩效子集统计自定义 True False
	String scoreNumPerPage = "0"; // BS打分时每页的人数，0为不限制
	String ShowOneMark = "False"; // BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
	String ScoreBySumup = "False"; // BS个人总结没填写，主体为其打分时不能提交
	String AllowUploadFile = "True"; // 支持附件上传
	String TargetCompleteThenGoOn = "False";//目标卡填写完整才允许提交（个性化任务、绩效报告）参数
	String GATIShowDegree = "False"; // BS 综合测评表中指标的评分显示为标度
	String SelfScoreInDirectLeader = "False"; // 直接上级可以查看下属“本人”打分
	String isShowOrder = "false"; // 显示排名
	String AutoCalcTotalScoreAndOrder = "false"; // 是否自动计算总分和排名
	String isEntireysub = "false"; // 提交是否需要必填
	String performanceType = "0"; // 考核形式 0：绩效考核 1：民主评测
	String ReaderType = "0"; // 机读类型:0光标阅读机(默认),1扫描仪
	
	String isShowSubmittedPlan = "True"; // 提交后的计划是否需要显示True|False
	String showAppraiseExplain = "true"; // 综合评测表是否显示评测说明
	String BlankScoreOption = "0"; // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
	String BlankScoreUseDegree="A";  //指标未打分，按用户定义的标度, 具体选自标准标度中, 如果指标中没有所定义标度，按未打分处理。A|B|C…
	String DepartmentLevel = ""; // 部门层级
	String MutiScoreGradeCtl = "FALSE"; // BS多人打分时是否等级控制
	String mitiScoreMergeSelfEval = "False"; // 多人打分时同时显示自我评价
	String CheckGradeRange="0";  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
	Hashtable ht = new Hashtable();
	/* fzg add */
	String scoreWay = "1"; // 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分	
	String keyEventEnabled="False";//关键事件参与总分修正
	String degreeShowType = "1";// 标度显示形式(1-标准标度内容 2-指标标度内容 3-采集标准标度,显示指标标度内容）
	String ScoreShowRelatePlan="False"; //多人评分显示引入计划得分
	String KeepDecimal = "1"; // 小数位
	String UnLeadSingleAvg="False"; //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
	String AppUseWeight="False";    //评估中总体评价票数有权重
	String KnowText="";				//保留的了解程度ID
	String EstBodyText="";          //去最值的ID
	String UseKnow="False";         //是否过滤了解程度
	String UseWeight="False";       //是否使用权重
	String ThrowLowCount="0";        //去掉最小值数
	String ThrowHighCount="0";       //去掉最大值数
	String throwBaseNum="0";        //主体类别人数大于
	String CheckInvalidGrade="False"; //无效标度代码
	String zeroByNull="false";
	String InvalidGrade="";           //是否选择使用无效票数, (True, False；默认为False)
	String PublicPointCannotEdit="False";  //共性指标员工不能编辑, 默认为 False 能编辑
	//
	String formulaSql="";           //最后得分 计算规则
	
	String formulaDeviationSql=""; // 总分纠偏公式sql
	String deviationScoreUsed="";
	//目标卡制订支持几级审批
	String targetMakeSeries = "1";
	// 任务调整需新建任务项
	String taskAdjustNeedNew = "False";
	// 每项任务可签批
	String taskCanSign = "False";
	// 每项任务需回顾总结
	String taskNeedReview = "False";

	// 显示考核指标内容
	String showIndicatorContent = "False";
	// 显示考核指标评分原则
	String showIndicatorRole = "False";
	// 显示考核指标标度说明
	String showIndicatorDegree = "False";
	// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
	String targetAppMode = "0";    
	//关联目标卡(显示绩效目标有效才有用) 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
	String relatingTargetCard = "1";	
	String showYPTargetCard = "False";//显示已自评目标卡：True，  不显示：False（默认）
//	显示扣分原因(Ture, False(默认))
	String showDeductionCause = "False";
	//扣分原因是否必填
	String MustFillCause="False";
	
	
//	目标卡批准后允许再调整, (True, False, 默认为True)
	String targetAllowAdjustAfterApprove="True";
	//允许领导制定及调整目标卡 默认为False
	String  allowLeadAdjustCard = "False"; 		    
	//允许查看下级对考核对象评分 默认为False
	String  allowSeeLowerGrade = "False";  
	String voteScoreDecimal ="0";//总分精度
	//相同时能否保存(Ture, False),默认为 True
	String CanSaveAllObjsScoreSame="True";
	String  ShowSumRow="False"; //显示合计行
	String voteDecimal ="2";//权重精度
	
	String DynaBodyToPoint="False";  //动态主体权重控制到指标/任务True, False, 默认为 False
	String evalCanNewPoint = "False";//评估打分允许新增考核指标 (True, False默认为False)
	String targetTraceEnabled  = "False";//目标卡跟踪显示和采集指标开关
	String targetTraceItem = ""; //目标卡跟踪显示指标
	String targetCollectItem = ""; //目标卡采集指标
	String targetCollectItemMust = ""; //目标卡采集指标
	String TargetDefineItem="";   //目标卡指标
	String NoShowTargetAdjustHistory="False";  //打分时不显示任务调整历史
	String AllowLeaderTrace="False";//允许领导制定及批准跟踪指标, True(默认) False
	String ProcessNoVerifyAllScore="True";  //报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)
	String VerifyRule = "="; //报批 批准进行总分校验 校验规则 (=,<=)模板总分 默认为=
	String EvalOutLimitStdScore="False";     //评分时得分不受标准分限制True, False, 默认为 False;都加
	String EvalOutLimitScoreOrg="False";     //评分时不受考核机构限制True, False, 默认为 False;都加
	String ShowBackTables="";//考核计划指定登记表
	String IsLimitPointValue="False";//限定目标卡项目下的任务权重|分值之和等于项目的权重|任务 默认为 False;
	String NoApproveTargetCanScore="False";     //目标卡未审批也允许打分 True, False, 默认为 False
	String TargetCalcItem="";                   //目标卡计算指标属性，P04中指标，以逗号分隔，顺序从前到后
	String TargetMustFillItem="";               //目标卡必填指标,   以逗号分隔
	String TargetUsePrevious="";               // 引入上期目标卡指标
	String ShowLeaderEval="False";              //本人查看绩效面谈显示领导对其的评价
	String AllowAdjustEvalResult="False"; //允许调整评估结果, True, False, 默认为 False
	String AdjustEvalDegreeType="0"; //调整使用标度 0=指标标度，1=等级标度.默认为0
	String AdjustEvalDegreeNum="0"; //调整浮动等级：整数值
	String CalcMenScoreRefDept="False"; //本次评分=个人指标得分*部门指标得分的权重和（目标考核和360°） True, False, 默认为 False	
	String AdjustEvalRange = "0"; //调整范围：0=指标，1=总分.默认为0
	String AdjustEvalGradeStep ="";//调整等级分值步长：十进制（如0.2），为0不处理。调整等级标度才可用。默认为空
	String GrpMenu1="";           //排名指标1，格式：字段名;层级
	String GrpMenu2="";           //排名指标2，格式：字段名;层级
	String Enabled="False";       //排名指标是否启用
	String ShowGrpOrder ="True";  //评分调整 显示排名
	String VerifySameScore = "False";//打分分数相同不能提交：True, False, 默认为 False
    String ShowEvalDirector = "False";//评估结果中显示"负责人"指标：True, False, 默认为 False
	String ScoreFromItem="False";//按项目权重逐级计算总分 True，False 默认为False；
	String BodysFromCard = "False"; //考核主体从机读卡读取(主体类别自动对应)
    String MenRefDeptTmpl = ""; //本次评分=个人指标得分*部门指标得分的权重和时，部门模板。为空表示与当前计划相同。
    String ObjsFromCard = "False"; //考核对象是否从机读卡读取(考核实施中不需要选择考核对象)
    String PointEvalType="0";  //360指标评分型式  0：下拉（默认）  1：单选
    String MutiScoreOnePageOnePoint="False";  //单题打分
    String TargetGroupByItem="False"; //目标卡按一级项目分值显示，True, False, 默认为 False
	String TargetGroupItems="";  //项目号1,组号1:A1,A3,A4; 项目号2,组号1:A1,A3,A4; 项目号3,组号2:A1,A3,A4;
    String TaskSupportAttach="False"; //目标任务支持附件上传
    String GradeByBodySeq="False"; //按考核主体顺序号控制评分流程(True, False默认为False)
    String AllowSeeAllGrade="False"; //允许查看其它主体对考核对象评分(True, False默认为False)
    String TotalAppFormula = ""; // 总体评价的计算公式，默认为空
    String MailTogoLink = "1"; // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件    
    String RadioDirection = "0";
	String ShowBasicInfo="False";   //360计划显示基本信息
	String BasicInfoItem="";        //基本信息指标
	String LockMGradeColumn="True"; //多人考评锁定指标列
    
	 String PointScoreFromKeyEvent="False"; //指标评分优先取自关键事件
	 String taskNameDesc="";//p0407的指标名称，在目标卡指标页面定义，存在xml参数中
	 String SpByBodySeq="False";//是否按考核主体顺序号进行审批
	 
	String showDayWeekMonth = "1,2,3";//查看员工日志
	String mainbodyGradeCtl = "";//强制分布主体类别
	String allmainbodyGradeCtl = "";//强制分布主体类别
	String wholeEvalMode = "0";//总体评价录分方式0：录入等级1：录入分值
	String gradeSameNotSubmit="False";//等级不同分数相同不能提交
	String showHistoryScore = "False";//显示历次得分表
	String batchScoreImportFormula = "False";
	String evaluate_str=""; //控制考评反馈方式
	String blind_point="0"; //盲点
	String dutyRule = "";
	try
	{
		 XPath xPath = XPath.newInstance("/PerPlan_Parameter/RelatePlan/CustomOrderGrp");
		 Element element=null;
		 element = (Element) xPath.selectSingleNode(this.a_doc);  
		 if(element!=null)
		 {
		//	 if (element.getAttributeValue("Enabled") != null &&element.getAttributeValue("Enabled").equalsIgnoreCase("true"))
			 {
				 if (element.getAttributeValue("GrpMenu1") != null && !"".equals(element.getAttributeValue("GrpMenu1")))
					 GrpMenu1=element.getAttributeValue("GrpMenu1");
				 if (element.getAttributeValue("GrpMenu2") != null && !"".equals(element.getAttributeValue("GrpMenu2")))
					 GrpMenu2=element.getAttributeValue("GrpMenu2");
				 
			 }
		 }
		 if(root.getAttributeValue("ShowEmployeeRecord")!=null&&!"".equals(root.getAttributeValue("ShowEmployeeRecord")))
		 {
			 ShowEmployeeRecord=root.getAttributeValue("ShowEmployeeRecord");
		 }
		  
		 if(root.getAttributeValue("ShowDayWeekMonth")!=null&&!"".equals(root.getAttributeValue("ShowDayWeekMonth")))
		 {
			 showDayWeekMonth = root.getAttributeValue("ShowDayWeekMonth");
		 }
		 if("False".equalsIgnoreCase(ShowEmployeeRecord))
			 showDayWeekMonth="";
		 
		 if(root.getAttributeValue("MainbodyGradeCtl")!=null&&!"".equals(root.getAttributeValue("MainbodyGradeCtl")))
		 {
			 mainbodyGradeCtl = root.getAttributeValue("MainbodyGradeCtl");
		 }
		 if(root.getAttributeValue("AllMainbodyGradeCtl")!=null&&!"".equals(root.getAttributeValue("AllMainbodyGradeCtl")))
		 {
			 allmainbodyGradeCtl = root.getAttributeValue("AllMainbodyGradeCtl");
		 }
		 if(root.getAttributeValue("WholeEvalMode")!=null&&!"".equals(root.getAttributeValue("WholeEvalMode")))
		 {
			 wholeEvalMode = root.getAttributeValue("WholeEvalMode");
		 }
		 if(root.getAttributeValue("GradeSameNotSubmit")!=null&&!"".equals(root.getAttributeValue("GradeSameNotSubmit")))
		 {
			 gradeSameNotSubmit = root.getAttributeValue("GradeSameNotSubmit");
		 }
		 if(root.getAttributeValue("ShowHistoryScore")!=null&&!"".equals(root.getAttributeValue("ShowHistoryScore")))
		 {
			 showHistoryScore = root.getAttributeValue("ShowHistoryScore");
		 }
		 if(root.getAttributeValue("BatchScoreImportFormula")!=null&&!"".equals(root.getAttributeValue("BatchScoreImportFormula")))
		 {
			 batchScoreImportFormula = root.getAttributeValue("BatchScoreImportFormula");
		 }
		 if(root.getAttributeValue("DutyRule")!=null&&!"".equals(root.getAttributeValue("DutyRule"))){
			 dutyRule = root.getAttributeValue("DutyRule");
		 }
		 if(root.getAttributeValue("SpByBodySeq")!=null&&!"".equals(root.getAttributeValue("SpByBodySeq")))
		 {
			 SpByBodySeq=root.getAttributeValue("SpByBodySeq");
		 }
		 if(root.getAttributeValue("PointScoreFromKeyEvent")!=null&&!"".equals(root.getAttributeValue("PointScoreFromKeyEvent")))
		 {
			 PointScoreFromKeyEvent=root.getAttributeValue("PointScoreFromKeyEvent");
		 }
		 if(root.getAttributeValue("ScoreShowRelatePlan")!=null&&!"".equals(root.getAttributeValue("ScoreShowRelatePlan")))
		 {
			ScoreShowRelatePlan=root.getAttributeValue("ScoreShowRelatePlan");
		 }
		 if(root.getAttributeValue("ShowBasicInfo")!=null&&!"".equals(root.getAttributeValue("ShowBasicInfo")))
		 {
			 ShowBasicInfo=root.getAttributeValue("ShowBasicInfo");
		 }
		 if(root.getAttributeValue("BasicInfoItem")!=null&&!"".equals(root.getAttributeValue("BasicInfoItem")))
		 {
			 BasicInfoItem=root.getAttributeValue("BasicInfoItem");
		 }
		 if(root.getAttributeValue("LockMGradeColumn")!=null&&!"".equals(root.getAttributeValue("LockMGradeColumn")))
		 {
			 LockMGradeColumn=root.getAttributeValue("LockMGradeColumn");
		 }
		  
		 if(root.getAttributeValue("TaskSupportAttach")!=null&&!"".equals(root.getAttributeValue("TaskSupportAttach")))
		 {
			 TaskSupportAttach=root.getAttributeValue("TaskSupportAttach");
		 }
		 if(root.getAttributeValue("GradeByBodySeq")!=null&&!"".equals(root.getAttributeValue("GradeByBodySeq")))
		 {
			 GradeByBodySeq=root.getAttributeValue("GradeByBodySeq");
		 }
		 if(root.getAttributeValue("AllowSeeAllGrade")!=null&&!"".equals(root.getAttributeValue("AllowSeeAllGrade")))
		 {
			 AllowSeeAllGrade=root.getAttributeValue("AllowSeeAllGrade");
		 }
		 if(root.getAttributeValue("TotalAppFormula")!=null&&!"".equals(root.getAttributeValue("TotalAppFormula")))
		 {
			 TotalAppFormula=root.getAttributeValue("TotalAppFormula");
		 }
		 if(root.getAttributeValue("MailTogoLink")!=null&&!"".equals(root.getAttributeValue("MailTogoLink")))
		 {
			 MailTogoLink=root.getAttributeValue("MailTogoLink");
		 }
		 if(root.getAttributeValue("RadioDirection")!=null&&!"".equals(root.getAttributeValue("RadioDirection")))
		 {
			 RadioDirection=root.getAttributeValue("RadioDirection");
		 }
		 
		 
		 if(root.getAttributeValue("TargetGroupByItem")!=null&&!"".equals(root.getAttributeValue("TargetGroupByItem")))
		 {
			 TargetGroupByItem=root.getAttributeValue("TargetGroupByItem");
		 }
		 if(root.getAttributeValue("TargetGroupItems")!=null&&!"".equals(root.getAttributeValue("TargetGroupItems")))
		 {
			 TargetGroupItems=root.getAttributeValue("TargetGroupItems");
		 }
		  
		 if(root.getAttributeValue("PointEvalType")!=null&&!"".equals(root.getAttributeValue("PointEvalType")))
		 {
			 PointEvalType=root.getAttributeValue("PointEvalType");
		 }
		 
		 if(root.getAttributeValue("MutiScoreOnePageOnePoint")!=null&&!"".equals(root.getAttributeValue("MutiScoreOnePageOnePoint")))
		 {
			 MutiScoreOnePageOnePoint=root.getAttributeValue("MutiScoreOnePageOnePoint");
		 }
		 
		
		 
		 if(root.getAttributeValue("ObjsFromCard")!=null&&!"".equals(root.getAttributeValue("ObjsFromCard")))
		 {
			 ObjsFromCard=root.getAttributeValue("ObjsFromCard");
		 }
		 if(root.getAttributeValue("BodysFromCard")!=null&&!"".equals(root.getAttributeValue("BodysFromCard")))
		 {
			 BodysFromCard=root.getAttributeValue("BodysFromCard");
		 }
		 if(root.getAttributeValue("MenRefDeptTmpl")!=null&&!"".equals(root.getAttributeValue("MenRefDeptTmpl")))
		 {
			 MenRefDeptTmpl=root.getAttributeValue("MenRefDeptTmpl");
		 }
		if(root.getAttributeValue("ScoreFromItem")!=null&&!"".equals(root.getAttributeValue("ScoreFromItem")))
		{
			ScoreFromItem=root.getAttributeValue("ScoreFromItem");
	    }
		if (root.getAttributeValue("AdjustEvalGradeStep") != null && !"".equals(root.getAttributeValue("AdjustEvalGradeStep")))
		{
			AdjustEvalGradeStep = root.getAttributeValue("AdjustEvalGradeStep");
		}
		if (root.getAttributeValue("VerifySameScore") != null && !"".equals(root.getAttributeValue("VerifySameScore")))
		{
			VerifySameScore = root.getAttributeValue("VerifySameScore");
		}
		if (root.getAttributeValue("ShowEvalDirector") != null && !"".equals(root.getAttributeValue("ShowEvalDirector")))
		{
			ShowEvalDirector = root.getAttributeValue("ShowEvalDirector");
		}
		if (root.getAttributeValue("ShowGrpOrder") != null && !"".equals(root.getAttributeValue("ShowGrpOrder")))
		{
			ShowGrpOrder = root.getAttributeValue("ShowGrpOrder");
		}
		if (root.getAttributeValue("AdjustEvalRange") != null && !"".equals(root.getAttributeValue("AdjustEvalRange")))
		{
			AdjustEvalRange = root.getAttributeValue("AdjustEvalRange");
		}
		if (root.getAttributeValue("AllowAdjustEvalResult") != null && !"".equals(root.getAttributeValue("AllowAdjustEvalResult")))
		{
			AllowAdjustEvalResult = root.getAttributeValue("AllowAdjustEvalResult");
		}
		if (root.getAttributeValue("AdjustEvalDegreeType") != null && !"".equals(root.getAttributeValue("AdjustEvalDegreeType")))
		{
			AdjustEvalDegreeType = root.getAttributeValue("AdjustEvalDegreeType");
		}
		if (root.getAttributeValue("AdjustEvalDegreeNum") != null && !"".equals(root.getAttributeValue("AdjustEvalDegreeNum")))
		{
			AdjustEvalDegreeNum = root.getAttributeValue("AdjustEvalDegreeNum");
		}
		if (root.getAttributeValue("CalcMenScoreRefDept") != null && !"".equals(root.getAttributeValue("CalcMenScoreRefDept")))
		{
			CalcMenScoreRefDept = root.getAttributeValue("CalcMenScoreRefDept");
		}	
		if (root.getAttributeValue("ShowLeaderEval") != null && !"".equals(root.getAttributeValue("ShowLeaderEval")))
		{
			ShowLeaderEval = root.getAttributeValue("ShowLeaderEval");
		}	
		if (root.getAttributeValue("TargetCalcItem") != null && !"".equals(root.getAttributeValue("TargetCalcItem")))
		{
			TargetCalcItem = root.getAttributeValue("TargetCalcItem");
		}
		if (root.getAttributeValue("TargetMustFillItem") != null && !"".equals(root.getAttributeValue("TargetMustFillItem")))
		{
			TargetMustFillItem = root.getAttributeValue("TargetMustFillItem");
		}
		if (root.getAttributeValue("TargetUsePrevious") != null && !"".equals(root.getAttributeValue("TargetUsePrevious")))
		{
			TargetUsePrevious = root.getAttributeValue("TargetUsePrevious");
		}
		if (root.getAttributeValue("NoApproveTargetCanScore") != null && !"".equals(root.getAttributeValue("NoApproveTargetCanScore")))
		{
			NoApproveTargetCanScore = root.getAttributeValue("NoApproveTargetCanScore");
		}
		if (root.getAttributeValue("DynaBodyToPoint") != null && !"".equals(root.getAttributeValue("DynaBodyToPoint")))
		{
			DynaBodyToPoint = root.getAttributeValue("DynaBodyToPoint");
		}
		if (root.getAttributeValue("EvalOutLimitStdScore") != null && !"".equals(root.getAttributeValue("EvalOutLimitStdScore")))
		{
			EvalOutLimitStdScore = root.getAttributeValue("EvalOutLimitStdScore");
		}
		if (root.getAttributeValue("EvalOutLimitScoreOrg") != null && !"".equals(root.getAttributeValue("EvalOutLimitScoreOrg")))
        {
		    EvalOutLimitScoreOrg = root.getAttributeValue("EvalOutLimitScoreOrg");
        }
		if (root.getAttributeValue("ProcessNoVerifyAllScore") != null && !"".equals(root.getAttributeValue("ProcessNoVerifyAllScore")))
		{
			ProcessNoVerifyAllScore = root.getAttributeValue("ProcessNoVerifyAllScore");
		}
		if (root.getAttributeValue("VerifyRule") != null && !"".equals(root.getAttributeValue("VerifyRule")))
		{
			VerifyRule = root.getAttributeValue("VerifyRule");
		}		
		if (root.getAttributeValue("EvalCanNewPoint") != null && !"".equals(root.getAttributeValue("EvalCanNewPoint")))
		{
		    evalCanNewPoint = root.getAttributeValue("EvalCanNewPoint");
		}
		if (root.getAttributeValue("TargetTraceEnabled") != null && !"".equals(root.getAttributeValue("TargetTraceEnabled")))
		{
		    targetTraceEnabled = root.getAttributeValue("TargetTraceEnabled");
		}
		
		if (root.getAttributeValue("NoShowTargetAdjustHistory") != null && !"".equals(root.getAttributeValue("NoShowTargetAdjustHistory")))
		{
			NoShowTargetAdjustHistory = root.getAttributeValue("NoShowTargetAdjustHistory");
		}
		
		if (root.getAttributeValue("TargetTraceItem") != null && !"".equals(root.getAttributeValue("TargetTraceItem")))
		{
		    targetTraceItem = root.getAttributeValue("TargetTraceItem");
		}
		if (root.getAttributeValue("TargetCollectItem") != null && !"".equals(root.getAttributeValue("TargetCollectItem")))
		{
		    targetCollectItem = root.getAttributeValue("TargetCollectItem");
		    String[] items = targetCollectItem.split(",");
		    targetCollectItem="";
		    for (int i = 0; i < items.length; i++){
                String[] temps = items[i].split(":");
                targetCollectItem += temps[0]+",";
                if(temps.length>1) {
                    if("1".equals(temps[1])){
                        targetCollectItemMust += temps[0]+",";
                    }
                }
            }
		}
		if (root.getAttributeValue("TargetDefineItem") != null && !"".equals(root.getAttributeValue("TargetDefineItem")))
		{
			TargetDefineItem = root.getAttributeValue("TargetDefineItem");
		}
		if (root.getAttributeValue("ShowSumRow") != null && !"".equals(root.getAttributeValue("ShowSumRow")))
		{
			ShowSumRow = root.getAttributeValue("ShowSumRow");
		}
		
		
		if (root.getAttributeValue("CanSaveAllObjsScoreSame") != null && !"".equals(root.getAttributeValue("CanSaveAllObjsScoreSame")))
		{
			CanSaveAllObjsScoreSame = root.getAttributeValue("CanSaveAllObjsScoreSame");
		}
		
		if (root.getAttributeValue("AllowLeadAdjustCard") != null && !"".equals(root.getAttributeValue("AllowLeadAdjustCard")))
		{
		    allowLeadAdjustCard = root.getAttributeValue("AllowLeadAdjustCard");
		}
		if (root.getAttributeValue("AllowSeeLowerGrade") != null && !"".equals(root.getAttributeValue("AllowSeeLowerGrade")))
		{
		    allowSeeLowerGrade = root.getAttributeValue("AllowSeeLowerGrade");
		}	
		if (root.getAttributeValue("TargetAllowAdjustAfterApprove") != null && !"".equals(root.getAttributeValue("TargetAllowAdjustAfterApprove")))
		{
		    targetAllowAdjustAfterApprove = root.getAttributeValue("TargetAllowAdjustAfterApprove");
		}
		if (root.getAttributeValue("VoteScoreDecimal") != null && !"".equals(root.getAttributeValue("VoteScoreDecimal")))
		{
		    voteScoreDecimal = root.getAttributeValue("VoteScoreDecimal");
		}
		if (root.getAttributeValue("VoteDecimal") != null && !"".equals(root.getAttributeValue("VoteDecimal")))
		{
		    voteDecimal = root.getAttributeValue("VoteDecimal");
		}
		
		if (root.getAttributeValue("MustFillCause") != null && !"".equals(root.getAttributeValue("MustFillCause")))
		{
			MustFillCause = root.getAttributeValue("MustFillCause");
		}
		if (root.getAttributeValue("ShowDeductionCause") != null && !"".equals(root.getAttributeValue("ShowDeductionCause")))
		{
		    showDeductionCause = root.getAttributeValue("ShowDeductionCause");
		}
		if (root.getAttributeValue("RelatingTargetCard") != null && !"".equals(root.getAttributeValue("RelatingTargetCard")))
		{
		    relatingTargetCard = root.getAttributeValue("RelatingTargetCard");
		}
		if (root.getAttributeValue("ShowYPTargetCard") != null && !"".equals(root.getAttributeValue("ShowYPTargetCard")))
		{
			showYPTargetCard = root.getAttributeValue("ShowYPTargetCard");
		}
		if (root.getAttributeValue("TargetAppMode") != null && !"".equals(root.getAttributeValue("TargetAppMode")))
		{
		    targetAppMode = root.getAttributeValue("TargetAppMode");
		}
		if (root.getAttributeValue("ShowIndicatorDegree") != null && !"".equals(root.getAttributeValue("ShowIndicatorDegree")))
		{
		    showIndicatorDegree = root.getAttributeValue("ShowIndicatorDegree");
		}
		if (root.getAttributeValue("ShowIndicatorRole") != null && !"".equals(root.getAttributeValue("ShowIndicatorRole")))
		{
		    showIndicatorRole = root.getAttributeValue("ShowIndicatorRole");
		}
		if (root.getAttributeValue("ShowIndicatorContent") != null && !"".equals(root.getAttributeValue("ShowIndicatorContent")))
		{
		    showIndicatorContent = root.getAttributeValue("ShowIndicatorContent");
		}
		if (root.getAttributeValue("TargetMakeSeries") != null && !"".equals(root.getAttributeValue("TargetMakeSeries")))
		{
		    targetMakeSeries = root.getAttributeValue("TargetMakeSeries");
		}
		if (root.getAttributeValue("TaskAdjustNeedNew") != null && !"".equals(root.getAttributeValue("TaskAdjustNeedNew")))
		{
		    taskAdjustNeedNew = root.getAttributeValue("TaskAdjustNeedNew");
		}
		if (root.getAttributeValue("TaskCanSign") != null && !"".equals(root.getAttributeValue("TaskCanSign")))
		{
		    taskCanSign = root.getAttributeValue("TaskCanSign");
		}
		if (root.getAttributeValue("TaskNeedReview") != null && !"".equals(root.getAttributeValue("TaskNeedReview")))
		{
		    taskNeedReview = root.getAttributeValue("TaskNeedReview");
		}
	
		if (root.getAttributeValue("PublicPointCannotEdit") != null && !"".equals(root.getAttributeValue("PublicPointCannotEdit")))
		{
			PublicPointCannotEdit = root.getAttributeValue("PublicPointCannotEdit");
		}
		if (root.getAttributeValue("xiFormula") != null && !"".equals(root.getAttributeValue("xiFormula")))
		{
			xiFormula = root.getAttributeValue("xiFormula");
		}
		if (root.getAttributeValue("GradeFormula") != null && !"".equals(root.getAttributeValue("GradeFormula")))
		{
			gradeFormula = root.getAttributeValue("GradeFormula");
		}
		if (root.getAttributeValue("DescriptiveWholeEval") != null && !"".equals(root.getAttributeValue("DescriptiveWholeEval")))
		{
			DescriptiveWholeEval = root.getAttributeValue("DescriptiveWholeEval");
		}
		if (root.getAttributeValue("SelfEvalNotScore") != null && !"".equals(root.getAttributeValue("SelfEvalNotScore")))
		{
			SelfEvalNotScore = root.getAttributeValue("SelfEvalNotScore");
		}
		if (root.getAttributeValue("NoteIdioGoal") != null && !"".equals(root.getAttributeValue("NoteIdioGoal")))
		{
			noteIdioGoal = root.getAttributeValue("NoteIdioGoal");
		}
		if (root.getAttributeValue("HandEval") != null && !"".equals(root.getAttributeValue("HandEval")))
		{
			HandEval = root.getAttributeValue("HandEval");
		}
		if (root.getAttributeValue("MutiScoreGradeCtl") != null && !"".equals(root.getAttributeValue("MutiScoreGradeCtl")))
		{
		    MutiScoreGradeCtl = root.getAttributeValue("MutiScoreGradeCtl");
		}
		if (root.getAttributeValue("MitiScoreMergeSelfEval") != null && !"".equals(root.getAttributeValue("MitiScoreMergeSelfEval")))
		{
		    mitiScoreMergeSelfEval = root.getAttributeValue("MitiScoreMergeSelfEval");
		}
		if (root.getAttributeValue("CheckGradeRange") != null && !"".equals(root.getAttributeValue("CheckGradeRange")))
		{
			CheckGradeRange = root.getAttributeValue("CheckGradeRange");
		}
		
		if (root.getAttributeValue("ReaderType") != null && !"".equals(root.getAttributeValue("ReaderType")))
		{
			ReaderType = root.getAttributeValue("ReaderType");
		}
		
		if (root.getAttributeValue("BlankScoreOption") != null && !"".equals(root.getAttributeValue("BlankScoreOption")))
		{
		    BlankScoreOption = root.getAttributeValue("BlankScoreOption").toLowerCase();
		}
		
		
		if (root.getAttributeValue("BlankScoreUseDegree") != null && !"".equals(root.getAttributeValue("BlankScoreUseDegree")))
		{
			BlankScoreUseDegree = root.getAttributeValue("BlankScoreUseDegree");
		}
		if (root.getAttributeValue("DepartmentLevel") != null && !"".equals(root.getAttributeValue("DepartmentLevel")))
		{
			DepartmentLevel = root.getAttributeValue("DepartmentLevel");
		}
		
		if (root.getAttributeValue("AutoCalcTotalScoreAndOrder") != null && !"".equals(root.getAttributeValue("AutoCalcTotalScoreAndOrder")))
		{
		    AutoCalcTotalScoreAndOrder = root.getAttributeValue("AutoCalcTotalScoreAndOrder").toLowerCase();
		}
	
		if (root.getAttributeValue("isShowSubmittedScores") != null && !"".equals(root.getAttributeValue("isShowSubmittedScores")))
		{
		    isShowSubmittedScores = root.getAttributeValue("isShowSubmittedScores").toLowerCase();
		}
	
		if (root.getAttributeValue("ShowAppraiseExplain") != null && !"".equals(root.getAttributeValue("ShowAppraiseExplain")))
		{
		    showAppraiseExplain = root.getAttributeValue("ShowAppraiseExplain").toLowerCase();
		}
	
		if (root.getAttributeValue("isShowSubmittedPlan") != null && !"".equals(root.getAttributeValue("isShowSubmittedPlan")))
		{
		    isShowSubmittedPlan = root.getAttributeValue("isShowSubmittedPlan");
		}
	
		if (root.getAttributeValue("performanceType") != null && !"".equals(root.getAttributeValue("performanceType")))
		{
		    performanceType = root.getAttributeValue("performanceType");
		}
		if (root.getAttributeValue("isShowOrder") != null && !"".equals(root.getAttributeValue("isShowOrder")))
		{
		    isShowOrder = root.getAttributeValue("isShowOrder");
		}
		if (root.getAttributeValue("isEntireysub") != null && !"".equals(root.getAttributeValue("isEntireysub")))
		{
		    isEntireysub = root.getAttributeValue("isEntireysub");
		}
	
		if (root.getAttributeValue("SelfScoreInDirectLeader") != null && !"".equals(root.getAttributeValue("SelfScoreInDirectLeader")))
		{
		    SelfScoreInDirectLeader = root.getAttributeValue("SelfScoreInDirectLeader");
		}
		if (root.getAttributeValue("GATIShowDegree") != null && !"".equals(root.getAttributeValue("GATIShowDegree")))
		{
		    GATIShowDegree = root.getAttributeValue("GATIShowDegree");
		}
		if (root.getAttributeValue("ScoreBySumup") != null && !"".equals(root.getAttributeValue("ScoreBySumup")))
		{
		    ScoreBySumup = root.getAttributeValue("ScoreBySumup");
		}
		if (root.getAttributeValue("AllowUploadFile") != null && !"".equals(root.getAttributeValue("AllowUploadFile")))
		{
			AllowUploadFile = root.getAttributeValue("AllowUploadFile");
		}	
		if (root.getAttributeValue("TargetCompleteThenGoOn") != null && !"".equals(root.getAttributeValue("TargetCompleteThenGoOn")))
		{
			TargetCompleteThenGoOn = root.getAttributeValue("TargetCompleteThenGoOn");
		}
		if (root.getAttributeValue("ShowOneMark") != null && !"".equals(root.getAttributeValue("ShowOneMark")))
		{
		    ShowOneMark = root.getAttributeValue("ShowOneMark");
		}
		if (root.getAttributeValue("ScoreNumPerPage") != null && !"".equals(root.getAttributeValue("ScoreNumPerPage")))
		{
		    scoreNumPerPage = root.getAttributeValue("ScoreNumPerPage");
		}
		if (root.getAttributeValue("PerSetShowMode") != null && !"".equals(root.getAttributeValue("PerSetShowMode")))
		{
		    perSetShowMode = root.getAttributeValue("PerSetShowMode");
		}
		if (root.getAttributeValue("PerSetStatMode") != null && !"".equals(root.getAttributeValue("PerSetStatMode")))
		{
		    perSetStatMode = root.getAttributeValue("PerSetStatMode");
		}
		if (root.getAttributeValue("StatStartDate") != null && !"".equals(root.getAttributeValue("StatStartDate")))
		{
		    StatStartDate = root.getAttributeValue("StatStartDate");
		}
		if (root.getAttributeValue("StatEndDate") != null && !"".equals(root.getAttributeValue("StatEndDate")))
		{
		    StatEndDate = root.getAttributeValue("StatEndDate");
		}
		if (root.getAttributeValue("StatCustomMode") != null && !"".equals(root.getAttributeValue("StatCustomMode")))
		{
		    statCustomMode = root.getAttributeValue("StatCustomMode");
		}
	
		if (root.getAttributeValue("NodeKnowDegree") != null && !"".equals(root.getAttributeValue("NodeKnowDegree")))
		{
		    strNodeKnowDegree = root.getAttributeValue("NodeKnowDegree");
		}
		if (root.getAttributeValue("PerSet") != null && !"".equals(root.getAttributeValue("PerSet")))
		{
		    perSet = root.getAttributeValue("PerSet");
		}
		if (root.getAttributeValue("EvalClass") != null && !"".equals(root.getAttributeValue("EvalClass")))
		{
			evalClass = root.getAttributeValue("EvalClass");
		}
		if (root.getAttributeValue("SameResultsOption") != null && !"".equals(root.getAttributeValue("SameResultsOption")))
		{
		    sameResultsOption = root.getAttributeValue("SameResultsOption");
		}
		
		
		if (root.getAttributeValue("NoCanSaveDegrees") != null && !"".equals(root.getAttributeValue("NoCanSaveDegrees")))
		{
			NoCanSaveDegrees = root.getAttributeValue("NoCanSaveDegrees");
		}
		
		if (root.getAttributeValue("ShowIndicatorDesc") != null && !"".equals(root.getAttributeValue("ShowIndicatorDesc")))
		{
		    showIndicatorDesc = root.getAttributeValue("ShowIndicatorDesc");
		}
		if (root.getAttributeValue("ShowNoMarking") != null && !"".equals(root.getAttributeValue("ShowNoMarking")))
		{
		    showNoMarking = root.getAttributeValue("ShowNoMarking");
		}
		if (root.getAttributeValue("ShowTotalScoreSort") != null && !"".equals(root.getAttributeValue("ShowTotalScoreSort")))
		{
		    showTotalScoreSort = root.getAttributeValue("ShowTotalScoreSort");
		}
		// 总体评价
		if (root.getAttributeValue("WholeEval") != null && !"".equals(root.getAttributeValue("WholeEval")))
		{
		    strWholeEval = root.getAttributeValue("WholeEval");
		}
		if (root.getAttributeValue("MustFillWholeEval") != null && !"".equals(root.getAttributeValue("MustFillWholeEval")))
		{
			MustFillWholeEval = root.getAttributeValue("MustFillWholeEval");
		}
		if (root.getAttributeValue("IdioSummary") != null && !"".equals(root.getAttributeValue("IdioSummary")))
		{
		    SummaryFlag = root.getAttributeValue("IdioSummary");
		}
	
		if (root.getAttributeValue("DegreeShowType") != null && !"".equals(root.getAttributeValue("DegreeShowType")))
		{
			DegreeShowType = root.getAttributeValue("DegreeShowType");
		}
		
		
		if (root.getAttributeValue("DataGatherMode") != null && !"".equals(root.getAttributeValue("DataGatherMode")))
		{
		    scoreflag = root.getAttributeValue("DataGatherMode");
		}
		if (root.getAttributeValue("addSubtractType") != null && !"".equals(root.getAttributeValue("addSubtractType")))
		{
			addSubtractType = root.getAttributeValue("addSubtractType");
		}
		if (root.getAttributeValue("VotesNum") != null && !"".equals(root.getAttributeValue("VotesNum")))
		{
		    VotesNum = root.getAttributeValue("VotesNum");
		}
		if (root.getAttributeValue("ScaleToDegreeRule") != null && !"".equals(root.getAttributeValue("ScaleToDegreeRule")))
		{
		    limitrule = root.getAttributeValue("ScaleToDegreeRule");
		}
		if (root.getAttributeValue("FineMax") != null && !"".equals(root.getAttributeValue("FineMax")))
		{
		    limitation = root.getAttributeValue("FineMax");
		    fineMax = root.getAttributeValue("FineMax");
	
		}
	
		if(root.getAttributeValue("SameAllScoreNumLess")!=null && !"".equals(root.getAttributeValue("SameAllScoreNumLess")))
		{
			SameAllScoreNumLess = root.getAttributeValue("SameAllScoreNumLess");
		}
		if (root.getAttributeValue("FineRestrict") != null && !"".equals(root.getAttributeValue("FineRestrict")))
		{
		    bFineRestrict = root.getAttributeValue("FineRestrict");
		}
	
		if (root.getAttributeValue("BadlyMax") != null && !"".equals(root.getAttributeValue("BadlyMax")))
		{
		    BadlyMax = root.getAttributeValue("BadlyMax");
	
		}
	
		if (root.getAttributeValue("BadlyRestrict") != null && !"".equals(root.getAttributeValue("BadlyRestrict")))
		{
		    BadlyRestrict = root.getAttributeValue("BadlyRestrict");
		}
	
		if ("False".equals(bFineRestrict))
		    limitation = "-1";
	
		if (root.getAttributeValue("GradeClass") != null && !"".equals(root.getAttributeValue("GradeClass")))
		{
		    GradeClass = root.getAttributeValue("GradeClass");
		}
		List list = null;
		if (root.getChildren() != null)
		{
		    list = root.getChildren();
		    for (int i = 0; i < list.size(); i++)
			{
				Element node = (Element) list.get(i);
				if (node == null || !(node instanceof Element))
				    continue;
				String name = node.getName();			
				
				if ("CalcRule".equals(name))
				{
				    if (node.getAttributeValue("KeepDecimal") != null && !"".equals(node.getAttributeValue("KeepDecimal")))
						KeepDecimal = node.getAttributeValue("KeepDecimal");
				    if (node.getAttributeValue("UnLeadSingleAvg") != null && !"".equals(node.getAttributeValue("UnLeadSingleAvg")))
				    	UnLeadSingleAvg=node.getAttributeValue("UnLeadSingleAvg");
				    if (node.getAttributeValue("AppUseWeight") != null && !"".equals(node.getAttributeValue("AppUseWeight")))
				    	AppUseWeight=node.getAttributeValue("AppUseWeight");
				    if (node.getAttributeValue("KnowText") != null && !"".equals(node.getAttributeValue("KnowText")))
				    	KnowText=node.getAttributeValue("KnowText");	
				    if (node.getAttributeValue("EstBodyText") != null && !"".equals(node.getAttributeValue("EstBodyText")))
				    	EstBodyText=node.getAttributeValue("EstBodyText");
				    if (node.getAttributeValue("UseKnow") != null && !"".equals(node.getAttributeValue("UseKnow")))
				    	UseKnow=node.getAttributeValue("UseKnow");
				    if (node.getAttributeValue("UseWeight") != null && !"".equals(node.getAttributeValue("UseWeight")))
				    	UseWeight=node.getAttributeValue("UseWeight");
				    if (node.getAttributeValue("ThrowLowCount") != null && !"".equals(node.getAttributeValue("ThrowLowCount")))
				    	ThrowLowCount=node.getAttributeValue("ThrowLowCount");
				    if (node.getAttributeValue("ThrowHighCount") != null && !"".equals(node.getAttributeValue("ThrowHighCount")))
				    	ThrowHighCount=node.getAttributeValue("ThrowHighCount");
				    if (node.getAttributeValue("ThrowBaseNum") != null && !"".equals(node.getAttributeValue("ThrowBaseNum")))
				    	throwBaseNum=node.getAttributeValue("ThrowBaseNum");
				    
				    if (node.getAttributeValue("CheckInvalidGrade") != null && !"".equals(node.getAttributeValue("CheckInvalidGrade")))
				    	CheckInvalidGrade=node.getAttributeValue("CheckInvalidGrade");
				    if (node.getAttributeValue("zeroByNull") != null && !"".equals(node.getAttributeValue("zeroByNull")))
				    	zeroByNull=node.getAttributeValue("zeroByNull");
				    if (node.getAttributeValue("InvalidGrade") != null && !"".equals(node.getAttributeValue("InvalidGrade")))
				    	InvalidGrade=node.getAttributeValue("InvalidGrade");
				    
				    
				}
				if (!"False".equals(bFineRestrict) && "-1".equals(limitation) && "FineMax".equals(name))
				{
				    List attrList = node.getAttributes();
				    for (int j = 0; j < attrList.size(); j++)
				    {
		
					Attribute aa = (Attribute) attrList.get(j);
					fineMaxMap.put(aa.getName().substring(2), node.getAttributeValue(aa.getName()));
		
				    }
		
				}
		
				if (!"False".equals(BadlyRestrict) && "BadlyMax".equals(name))
				{
				    List attrList = node.getAttributes();
				    for (int j = 0; j < attrList.size(); j++)
				    {
		
					Attribute aa = (Attribute) attrList.get(j);
					BadlyMap.put(aa.getName().substring(2), node.getAttributeValue(aa.getName()));
		
				    }
		
				}
				if("RelatePlan".equals(name))
				{
					List childList=node.getChildren();
					for(int j=0;j<childList.size();j++)
					{
						Element a_node = (Element) childList.get(j);
						if (a_node == null || !(a_node instanceof Element))
						    continue;
						String aname = a_node.getName();
						
						if ("Formula".equals(aname))
						{
							if (a_node.getAttributeValue("Value") != null && !"".equals(a_node.getAttributeValue("Value")))
							{
								formulaSql=a_node.getAttributeValue("Value");
							}
						}
						if ("ReviseScore".equals(aname))
						{
							if (a_node.getAttributeValue("Value") != null && !"".equals(a_node.getAttributeValue("Value")))
							{
								formulaDeviationSql=a_node.getAttributeValue("Value");
							}
							if (a_node.getAttributeValue("Used") != null && !"".equals(a_node.getAttributeValue("Used")))
							{
								deviationScoreUsed=a_node.getAttributeValue("Used");
							}
						}
						
					}
					
				}
				
				
		
			 }
		}
	
		/* fzg add */
		if (root.getAttributeValue("ScoreWay") != null && !"".equals(root.getAttributeValue("ScoreWay")))
		{
		    scoreWay = root.getAttributeValue("ScoreWay").toLowerCase();
		}
		if (root.getAttributeValue("KeyEventEnabled") != null && !"".equals(root.getAttributeValue("KeyEventEnabled")))
		{
		    keyEventEnabled = root.getAttributeValue("KeyEventEnabled").toLowerCase();
		}
		if (root.getAttributeValue("DegreeShowType") != null && !"".equals(root.getAttributeValue("DegreeShowType")))
		{
		    degreeShowType = root.getAttributeValue("DegreeShowType").toLowerCase();
		}
		if(root.getAttributeValue("AllowLeaderTrace")!=null&&!"".equals(root.getAttributeValue("AllowLeaderTrace")))
		{
			AllowLeaderTrace=root.getAttributeValue("AllowLeaderTrace");
		}
		if(root.getAttributeValue("ShowBackTables")!=null&&!"".equals(root.getAttributeValue("ShowBackTables")))
		{
			ShowBackTables=root.getAttributeValue("ShowBackTables");
		}
		if(root.getAttributeValue("IsLimitPointValue")!=null&&!"".equals(root.getAttributeValue("IsLimitPointValue")))
		{
			IsLimitPointValue=root.getAttributeValue("IsLimitPointValue");
		}
		if(root.getAttributeValue("TaskNameDesc")!=null&&!"".equals(root.getAttributeValue("TaskNameDesc")))
		{
			taskNameDesc=root.getAttributeValue("TaskNameDesc");
		}
		//计算规则定义指标分值范围
		String scopeIsValidate="false";
		Element child=null;
		ArrayList scoreRangeList=new ArrayList();
		XPath xPath2 = XPath.newInstance("/PerPlan_Parameter/PointScoreScopes");
		
		child = (Element) xPath2.selectSingleNode(this.a_doc);  
		if(child!=null){
			if(child.getAttribute("IsValid")!=null&&!"".equalsIgnoreCase(child.getAttributeValue("IsValid"))){
				scopeIsValidate=child.getAttributeValue("IsValid");
			}
			
			List childlist=null;
			if(child.getChildren()!=null){
				childlist=child.getChildren();
				for(int i=0;i<childlist.size();i++){
					LazyDynaBean bean=new LazyDynaBean();
					Element node=(Element)childlist.get(i);
					if (node == null || !(node instanceof Element))
					    continue;
					String name = node.getName();			
					String upvalue="";
					String downvalue="";
					String type="";
					 String id="";
					if ("PointScore".equalsIgnoreCase(name))
					{	
						
						 if (node.getAttributeValue("id") != null && !"".equals(node.getAttributeValue("id"))){
							 id =node.getAttributeValue("id") ;
							 bean.set("id", id);
						}
						 if (node.getAttributeValue("maxscore") != null && !"".equals(node.getAttributeValue("maxscore"))){
							upvalue=node.getAttributeValue("maxscore") ; bean.set("maxscore", upvalue);
						}
						 if (node.getAttributeValue("minscore") != null && !"".equals(node.getAttributeValue("minscore"))){
							 downvalue=node.getAttributeValue("minscore") ; bean.set("minscore", downvalue);
						}
						 if (node.getAttributeValue("type") != null && !"".equals(node.getAttributeValue("type"))){
							 type =node.getAttributeValue("type") ;
							 bean.set("type", type);
						}
					}else{
						 bean.set("id", id);
						 bean.set("maxscore", upvalue);
						 bean.set("minscore", downvalue);
						 bean.set("type", type);
					}
					scoreRangeList.add(bean);
				}
			}	
		}
		//评分说明必填规则
		/* <MustFillOptions>
	        Flag：高于Up  低于Down   IsValid: 是否有效，默认为 False 
	      	 <MustFillOption Flag="Up" IsValid="True" DegreeId="A" />
		     <MustFillOption Flag="Down"  IsValid="True" DegreeId="A" />
	     </MustFillOptions>*/

		xPath = XPath.newInstance("/PerPlan_Parameter/MustFillOptions");
		Element MustFillOptions=null;
		ArrayList MustFillOptionsList = null;
		MustFillOptions = (Element) xPath.selectSingleNode(this.a_doc);  
		if(MustFillOptions!=null)
		{			
    		List optionsList = MustFillOptions.getChildren("MustFillOption");
			MustFillOptionsList = new ArrayList();
			for(int i=0;i<optionsList.size();i++)
			{
				Element e=(Element)optionsList.get(i);
				LazyDynaBean abean = new LazyDynaBean();
				
//				String requiredFieldStr = e.getAttributeValue("requiredField");
//				if (requiredFieldStr != null) {
//					abean.set("requiredFieldStr", requiredFieldStr);
//					break;
//				}
				
				String pointId = e.getAttributeValue("PointId"); // 必填指标的属性名称是：PointId
				String degreeId = e.getAttributeValue("DegreeId"); // upDegreeId、downDegreeId或excludeDegree
				
				abean.set("Flag",e.getAttributeValue("Flag"));
				abean.set("IsValid", e.getAttributeValue("IsValid"));
				if (pointId != null) {
					abean.set("PointId", pointId);
				} else {
					abean.set("DegreeId", degreeId);
				}
				MustFillOptionsList.add(abean);
			}
			if(MustFillOptionsList!=null&&MustFillOptionsList.size()>0)
				ht.put("MustFillOptionsList", MustFillOptionsList);
		}
		
		// 预警提醒设置
		/* <Warns > //预警提醒  opt: 1:目标卡制定及审批 2：考核打分  delayTime:延期天数  roleScope:预警对象（角色）
			<Warn opt="1"  delayTime="5"  roleScope="xxx,xxy" />
			<Warn opt="2"  delayTime="5"  roleScope="xxx,xxy" />
		   </Warns>
		*/
		xPath = XPath.newInstance("/PerPlan_Parameter/Warns");
		Element WarnRoleScope=null;
		ArrayList WarnRoleScopeList = null;
		WarnRoleScope = (Element) xPath.selectSingleNode(this.a_doc);  
		if(WarnRoleScope!=null)
		{			
    		List optionsList = WarnRoleScope.getChildren("Warn");
    		WarnRoleScopeList = new ArrayList();
			for(int i=0;i<optionsList.size();i++)
			{
				Element e=(Element)optionsList.get(i);
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("opt",e.getAttributeValue("opt"));
				abean.set("delayTime", e.getAttributeValue("delayTime"));
				abean.set("roleScope", e.getAttributeValue("roleScope"));
				WarnRoleScopeList.add(abean);
			}
			if(WarnRoleScopeList!=null && WarnRoleScopeList.size()>0)
				ht.put("WarnRoleScopeList", WarnRoleScopeList);
		}
		//--------------------慧聪网需求   start zhaoxg -------------------------------
		xPath = XPath.newInstance("/PerPlan_Parameter/evaluate");
		Element evaluate_element=(Element) xPath.selectSingleNode(this.a_doc);  
		if(evaluate_element!=null)
		{
			evaluate_str=evaluate_element.getValue();
			if (evaluate_element.getAttributeValue("blind_point")!= null && !"".equals(evaluate_element.getAttributeValue("blind_point")))
				blind_point=evaluate_element.getAttributeValue("blind_point");
		}	
		
		ht.put("evaluate_str", evaluate_str);
		ht.put("blind_point", blind_point);
		//----------------------end----------------------------------------------------
		
		
		ht.put("PointScoreFromKeyEvent",PointScoreFromKeyEvent);
		
		ht.put("CheckInvalidGrade",CheckInvalidGrade);
		ht.put("zeroByNull",zeroByNull);
		ht.put("InvalidGrade",InvalidGrade);
		
		ht.put("ShowBasicInfo",ShowBasicInfo);
		ht.put("BasicInfoItem",BasicInfoItem);
		ht.put("LockMGradeColumn",LockMGradeColumn);
		ht.put("ScoreShowRelatePlan",ScoreShowRelatePlan);
		ht.put("TaskSupportAttach",TaskSupportAttach);
		ht.put("GradeByBodySeq",GradeByBodySeq);
		ht.put("AllowSeeAllGrade",AllowSeeAllGrade);
		ht.put("TotalAppFormula",TotalAppFormula);
		ht.put("MailTogoLink",MailTogoLink);
		ht.put("RadioDirection",RadioDirection);
				 
		ht.put("TargetGroupByItem",TargetGroupByItem);
		ht.put("TargetGroupItems",TargetGroupItems);
		ht.put("MutiScoreOnePageOnePoint",MutiScoreOnePageOnePoint);
		ht.put("PointEvalType",PointEvalType);
		ht.put("ShowEmployeeRecord",ShowEmployeeRecord);
		ht.put("ObjsFromCard", ObjsFromCard);
		ht.put("BodysFromCard", BodysFromCard);
		ht.put("MenRefDeptTmpl", MenRefDeptTmpl);
	    ht.put("ScoreFromItem", ScoreFromItem);
		ht.put("ShowLeaderEval",ShowLeaderEval);
		ht.put("AllowAdjustEvalResult",AllowAdjustEvalResult);
		ht.put("AdjustEvalDegreeType",AdjustEvalDegreeType);
		ht.put("AdjustEvalDegreeNum",AdjustEvalDegreeNum);
		ht.put("CalcMenScoreRefDept",CalcMenScoreRefDept);
		ht.put("AdjustEvalRange",AdjustEvalRange);
		ht.put("AdjustEvalGradeStep",AdjustEvalGradeStep);
		ht.put("ShowGrpOrder",ShowGrpOrder);
		ht.put("ShowEvalDirector",ShowEvalDirector);
		ht.put("VerifySameScore",VerifySameScore);
		ht.put("TargetCalcItem",TargetCalcItem);
		ht.put("TargetMustFillItem",TargetMustFillItem);
		ht.put("TargetUsePrevious",TargetUsePrevious);
		ht.put("DynaBodyToPoint",DynaBodyToPoint);
		/**
	         * 取得了解程度，总体评价标识Hashtable
	         */
		ht.put("NoApproveTargetCanScore",NoApproveTargetCanScore);
		ht.put("EvalOutLimitStdScore", EvalOutLimitStdScore);
		ht.put("EvalOutLimitScoreOrg", EvalOutLimitScoreOrg);
		ht.put("ProcessNoVerifyAllScore",ProcessNoVerifyAllScore);
		ht.put("VerifyRule",VerifyRule);
		ht.put("TargetTraceEnabled", NullToFalse(targetTraceEnabled));
		ht.put("EvalCanNewPoint", NullToFalse(evalCanNewPoint));
		ht.put("TargetTraceItem", targetTraceItem);
		ht.put("TargetCollectItem", targetCollectItem);
		ht.put("TargetCollectItemMust", targetCollectItemMust);
		ht.put("TargetDefineItem",TargetDefineItem);
		ht.put("NoShowTargetAdjustHistory",NoShowTargetAdjustHistory);	
		ht.put("ShowSumRow",ShowSumRow);
		ht.put("CanSaveAllObjsScoreSame",CanSaveAllObjsScoreSame);
		ht.put("allowLeadAdjustCard",allowLeadAdjustCard);
		ht.put("allowSeeLowerGrade",allowSeeLowerGrade);	
		ht.put("TargetAllowAdjustAfterApprove",targetAllowAdjustAfterApprove);
		ht.put("voteDecimal",voteDecimal);
		ht.put("voteScoreDecimal",voteScoreDecimal);	
		ht.put("showDeductionCause",showDeductionCause);
		ht.put("MustFillCause",MustFillCause);
		ht.put("relatingTargetCard",relatingTargetCard);
		ht.put("showYPTargetCard", showYPTargetCard);
		ht.put("showIndicatorContent",showIndicatorContent);
		ht.put("showIndicatorRole",showIndicatorRole);
		ht.put("showIndicatorDegree",showIndicatorDegree);
		ht.put("targetAppMode",targetAppMode);	
		ht.put("targetMakeSeries",targetMakeSeries);
		ht.put("taskAdjustNeedNew",taskAdjustNeedNew);
		ht.put("taskCanSign",taskCanSign);
		ht.put("taskNeedReview",taskNeedReview);
		ht.put("xiFormula",xiFormula);
		ht.put("GradeFormula",gradeFormula);
		ht.put("DescriptiveWholeEval",DescriptiveWholeEval);
		ht.put("BlankScoreUseDegree",BlankScoreUseDegree);
		ht.put("DepartmentLevel",DepartmentLevel);		
		ht.put("SelfEvalNotScore",SelfEvalNotScore);
		ht.put("noteIdioGoal",noteIdioGoal);
		ht.put("DegreeShowType",DegreeShowType);
		ht.put("HandEval",HandEval);
		ht.put("formulaSql",formulaSql);
		ht.put("formulaDeviationSql",formulaDeviationSql);
		ht.put("deviationScoreUsed",deviationScoreUsed);
		ht.put("CheckGradeRange",CheckGradeRange);
		ht.put("ReaderType",ReaderType);
		ht.put("MutiScoreGradeCtl", MutiScoreGradeCtl);
		ht.put("ShowBackTables", ShowBackTables);
		ht.put("PublicPointCannotEdit",PublicPointCannotEdit);
	//	ht.put("PublicPointCannotEdit","True");
		
		
		ht.put("mitiScoreMergeSelfEval", mitiScoreMergeSelfEval);
		ht.put("BlankScoreOption", BlankScoreOption);
		ht.put("AutoCalcTotalScoreAndOrder", AutoCalcTotalScoreAndOrder);
		ht.put("isShowSubmittedScores", isShowSubmittedScores);
		ht.put("showAppraiseExplain", showAppraiseExplain);
		ht.put("isShowSubmittedPlan", isShowSubmittedPlan);
		ht.put("isShowOrder", isShowOrder.toLowerCase());
		ht.put("isEntireysub", isEntireysub.toLowerCase());
		ht.put("performanceType", performanceType);
		ht.put("SelfScoreInDirectLeader", SelfScoreInDirectLeader);
		ht.put("GATIShowDegree", GATIShowDegree);
		ht.put("ScoreBySumup", ScoreBySumup);
		ht.put("AllowUploadFile", NullToFalse(AllowUploadFile));
		ht.put("TargetCompleteThenGoOn", NullToFalse(TargetCompleteThenGoOn));
		ht.put("ShowOneMark", ShowOneMark);
		ht.put("NoCanSaveDegrees",NoCanSaveDegrees);
		ht.put("SameResultsOption", NullToFalse(sameResultsOption));
		ht.put("PerSet", perSet);
		ht.put("EvalClass", evalClass);
		ht.put("ShowIndicatorDesc", NullToFalse(showIndicatorDesc));
		ht.put("ShowNoMarking", NullToFalse(showNoMarking));
		ht.put("ShowTotalScoreSort", NullToFalse(showTotalScoreSort));
	//	strNodeKnowDegree="True";
	//	strWholeEval="True";
		ht.put("NodeKnowDegree", NullToFalse(strNodeKnowDegree));
		ht.put("WholeEval", NullToFalse(strWholeEval));
		ht.put("MustFillWholeEval",MustFillWholeEval);
		ht.put("GradeClass", GradeClass);
		ht.put("SummaryFlag", SummaryFlag);
		ht.put("scoreflag", scoreflag);
		ht.put("addSubtractType", addSubtractType);
		ht.put("limitation", limitation);
		ht.put("limitrule", limitrule);
		ht.put("fineMax", fineMax);
		ht.put("FineRestrict", bFineRestrict);
		ht.put("fineMaxMap", fineMaxMap);
		ht.put("SameAllScoreNumLess", SameAllScoreNumLess);
		
		ht.put("VotesNum", VotesNum);
	
		ht.put("BadlyMax", BadlyMax);
		ht.put("BadlyRestrict", BadlyRestrict);
		ht.put("BadlyMap", BadlyMap);
	
		ht.put("PerSetShowMode", perSetShowMode);
		ht.put("PerSetStatMode", perSetStatMode);
		ht.put("StatStartDate", StatStartDate);
		ht.put("StatEndDate", StatEndDate);
		ht.put("StatCustomMode", statCustomMode);
		ht.put("ScoreNumPerPage", scoreNumPerPage);
	    //计算规则
		ht.put("KeepDecimal", KeepDecimal);
		ht.put("UnLeadSingleAvg",UnLeadSingleAvg);
		ht.put("AppUseWeight",AppUseWeight);
		ht.put("KnowText",KnowText);
		ht.put("EstBodyText",EstBodyText);
		ht.put("UseKnow",UseKnow);
		ht.put("UseWeight",UseWeight);
		ht.put("ThrowLowCount",ThrowLowCount);
		ht.put("ThrowHighCount",ThrowHighCount);
		ht.put("ThrowBaseNum", throwBaseNum);
		ht.put("scoreWay", scoreWay);
		ht.put("KeyEventEnabled", keyEventEnabled);
		ht.put("degreeShowType",degreeShowType);
		ht.put("AllowLeaderTrace",AllowLeaderTrace);
		ht.put("IsLimitPointValue", IsLimitPointValue);
		
		ht.put("GrpMenu1",GrpMenu1);
		ht.put("GrpMenu2",GrpMenu2);
		ht.put("Enabled",Enabled);
		//定义指标分值范围
		ht.put("isvalidate", scopeIsValidate);
		ht.put("scoreRangeList", scoreRangeList);
		ht.put("TaskNameDesc", taskNameDesc);
		ht.put("SpByBodySeq",SpByBodySeq);
		ht.put("ShowDayWeekMonth",showDayWeekMonth);
		ht.put("MainbodyGradeCtl",mainbodyGradeCtl);
		ht.put("AllMainbodyGradeCtl",allmainbodyGradeCtl);
		ht.put("WholeEvalMode",wholeEvalMode);
		ht.put("GradeSameNotSubmit",gradeSameNotSubmit);
		ht.put("ShowHistoryScore",showHistoryScore);
		ht.put("BatchScoreImportFormula",batchScoreImportFormula);
		ht.put("DutyRule", dutyRule);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return ht;
    }

    /**
         * 转换处里
         * 
         * @param str
         * @return
         */
    public String NullToFalse(String str)
    {

	if ("".equals(str))
	{
	    str = "False";
	}
	str = str.toLowerCase();
	return str;
    }

	public Document getA_doc()
	{
		return a_doc;
	}

	public void setA_doc(Document a_doc)
	{
		this.a_doc = a_doc;
	}
	/**
	 * 获得项目的动态权重和项目任务制定规则
	 * @param planid
	 * @param conn
	 * @return
	 */
	public HashMap getDynaItem(String planid,Connection conn,String itemid,String bodyid)
	{
		HashMap amap = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search("select * from per_dyna_item where plan_id="+planid+" and item_id="+itemid+" and body_id="+bodyid);
			while(rs.next())
			{
				
				String rule=Sql_switcher.readMemo(rs, "rule");
				int body_id=rs.getInt("body_id");
				int item_id=rs.getInt("item_id");
				float dyna_value=rs.getFloat("dyna_value");//?
				amap.put("dyna_value", dyna_value+"");
				if(!"".equals(rule))
				{
					Document doc= PubFunc.generateDom(rule);
					String path="/Task/TaskNumber";
					XPath xpath=XPath.newInstance(path);
					Element element=(Element)xpath.selectSingleNode(doc);
					if(element!=null)
					{
						String maxCount=element.getAttributeValue("MaxCount");
						if(maxCount!=null&&!"".equals(maxCount))
						{
							amap.put("MaxCount", maxCount);
						}
						String minCount=element.getAttributeValue("MinCount");
						if(minCount!=null&&!"".equals(minCount))
						{
							amap.put("MinCount", minCount);
						}
					}
					path="/Task/TaskScore";
					xpath=XPath.newInstance(path);
					Element element2=(Element)xpath.selectSingleNode(doc);
					if(element2!=null)
					{
						String maxValue=element2.getAttributeValue("MaxValue");
						if(maxValue!=null&&!"".equals(maxValue))
						{
							amap.put("MaxValue", maxValue);
						}
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return amap;
	}
    
	/**
	 * 获得项目的动态权重和项目任务制定规则
	 * @param planid
	 * @param conn
	 * @return
	 */
	public HashMap getDynaItem(String planid,Connection conn,String bodyid)
	{
		HashMap amap = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search("select * from per_dyna_item where plan_id="+planid+" and body_id="+bodyid);
			while(rs.next())
			{
				
				String rule=Sql_switcher.readMemo(rs, "task_rule");
				HashMap map = new HashMap();
				int body_id=rs.getInt("body_id");
				int item_id=rs.getInt("item_id");
				float dyna_value=rs.getFloat("dyna_value");//?
				map.put("dyna_value", dyna_value+"");
				if(!"".equals(rule))
				{
					Document doc= PubFunc.generateDom(rule);
					String path="/Task/TaskNumber";
					XPath xpath=XPath.newInstance(path);
					Element element=(Element)xpath.selectSingleNode(doc);
					if(element!=null)
					{
						String maxCount=element.getAttributeValue("MaxCount");
						if(maxCount!=null&&!"".equals(maxCount))
						{
							map.put("MaxCount", maxCount);
						}
						String minCount=element.getAttributeValue("MinCount");
						if(minCount!=null&&!"".equals(minCount))
						{
							map.put("MinCount", minCount);
						}
					}
					path="/Task/TaskScore";
					xpath=XPath.newInstance(path);
					Element element2=(Element)xpath.selectSingleNode(doc);
					if(element2!=null)
					{
						String maxValue=element2.getAttributeValue("MaxValue");
						if(maxValue!=null&&!"".equals(maxValue))
						{
							map.put("MaxValue", maxValue);
						}
						String minValue = element2.getAttributeValue("MinValue");
						if(minValue!=null&&!"".equals(minValue))
						{
							map.put("MinValue", minValue);
						}
					}
					
					path="/Task/AddMinusScore";
					xpath=XPath.newInstance(path);
					Element element3=(Element)xpath.selectSingleNode(doc);
					if(element3!=null)
					{
						String flag=element3.getAttributeValue("flag");
						if(flag!=null&& "1".equals(flag))
						{
							map.put("isAddMinusItem","1");
							String scope=element3.getAttributeValue("scope");
							String to_scope=element3.getAttributeValue("t_scope");
							if(to_scope!=null)
							{
								
								map.put("addMinusToScore",element3.getAttributeValue("t_scope")==null?"":element3.getAttributeValue("t_scope"));
								map.put("addMinusScore",element3.getAttributeValue("f_scope")==null?"":element3.getAttributeValue("f_scope"));
							}else{
						    	if(scope!=null&&!"".equals(scope)){
						    		map.put("addMinusScore","-"+scope);
						    		map.put("addMinusToScore",scope);
						    	}else{
						    		map.put("addMinusScore","");
						    		map.put("addMinusToScore","");
						    	}
							}
							
						}
						else
							map.put("isAddMinusItem","0");
					}
					else
						map.put("isAddMinusItem","0");  //不是加扣分项目
					
					
				}
				amap.put(body_id+""+item_id, map);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return amap;
	}
    
    
	/**
     * 保存等级设置的高级设置  JinChunhai 2011.11.02  
     */
    public void saveGradeHighValue(String used,String toRoundOff,String degreeId,ArrayList list)
    {
    	HashMap degreeMap = new HashMap();
    	if(this.a_doc!=null)
    	{
    		Element root = this.a_doc.getRootElement();
        	Element plannode = root.getChild("AdvancedDegrees");
        	if(plannode==null)
        	{       		
        		Element ele = new Element("AdvancedDegrees");
        		root.addContent(ele);
        		plannode = ele;
        	}else
        	{      
        		// 查出已经存在的等级高级设置 放到 degreeMap 保存起来
        		try
        		{
	        		String extpro = this.getExtpro();
	        		if (extpro == null || "".equals(extpro))
	        		    return;
	        		Document doc = PubFunc.generateDom(extpro);
	    		    String xpath = "//AdvancedDegrees";
	    		    XPath xpath_ = XPath.newInstance(xpath);        	    
	        	   
	        	    Element dele = (Element) xpath_.selectSingleNode(doc); 
	        	    if(dele != null)
	        	    {
	    	    	    List list1 = (List) dele.getChildren("AdvancedDegree");
	    				for (int i = 0; i < list1.size(); i++)
	    				{   					
	    					Element ele = (Element) list1.get(i);;     						   
	    					
	    					if (ele != null)
	    		    	    {	    						
	    						ArrayList degreeList = new ArrayList();
	    		    	    	String cused = ele.getAttributeValue("used");
	    		    	    	String ctoRoundOff = ele.getAttributeValue("toRoundOff");
	    		    	    	String cdegree_id = ele.getAttributeValue("degree_id");
	    		    	    		
	    		    	    	List dlist = (List) ele.getChildren("Degree");
	    		    	    	for (int k = 0; k < dlist.size(); k++)
	    		    	    	{    	    		    
	    		    	    		Element temp = (Element) dlist.get(k);
	    		    	    		if(temp!=null)
	    		    	    		{	    		    	    			   
		    		    	    		LazyDynaBean abean = new LazyDynaBean();
		    		    	    		abean.set("mode", temp.getAttributeValue("Mode"));
		    		    	    		abean.set("oper", temp.getAttributeValue("Oper"));
		    		    	    		abean.set("value", temp.getAttributeValue("Value"));
		    		    	    		abean.set("grouped", temp.getAttributeValue("Grouped"));
		    		    	    		abean.set("actIds", temp.getAttributeValue("ActIds"));
		    		    	    		abean.set("UMGrade", temp.getAttributeValue("UMGrade")==null?"":temp.getAttributeValue("UMGrade"));
		    		    	    		degreeList.add(abean);
	    		    	    		}	    		    	    		
	    		    	    	}
	    		    	    	degreeMap.put(cused+"`"+ctoRoundOff+"`"+cdegree_id, degreeList);	    		    	    		    		    	    	
	    		    	    }
	    		    	}	    					
	    			}	        	
	        	    
        		} catch (Exception e)
        		{
        		    e.printStackTrace();
        		}
        		
        		// 删除节点 AdvancedDegrees
        		root.removeChildren("AdvancedDegrees");  
        		
        	}
        	
        	// 增加已放到 degreeMap 中的等级高级设置  刨除等级编号相等的记录
        	if(degreeMap!=null && degreeMap.size()>0)
        	{
        		Set keySet=degreeMap.keySet();
				java.util.Iterator t=keySet.iterator();
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值	    
					ArrayList degreeList  = (ArrayList)degreeMap.get(strKey);   //value值 
					String cused = strKey.split("`")[0];
					String ctoRoundOff = strKey.split("`")[1];
					String cdegree_id = strKey.split("`")[2];
					//String cused=strKey.substring(0,strKey.indexOf("`")); // used					
					//String cdegree_id=strKey.substring(strKey.indexOf("`")+1); // degree_id 
					
					// 刨除等级编号相等的记录
					if(!cdegree_id.equalsIgnoreCase(degreeId))
					{
						Element plannodeDegree = root.getChild("AdvancedDegrees");
			        	if(plannodeDegree==null)
			        	{       		
			        		Element ele = new Element("AdvancedDegrees");
			        		root.addContent(ele);
			        		plannode = ele;
			        	}else
			        	{
			        	}
			        	Element Rplan = new Element("AdvancedDegree");
			        	Rplan.setAttribute("used", cused);
			        	Rplan.setAttribute("toRoundOff", ctoRoundOff);
			        	Rplan.setAttribute("degree_id", cdegree_id);       		
			        	for (int i = 0; i < degreeList.size(); i++)
			    		{
			        		Element degree = new Element("Degree");
			    			LazyDynaBean bean = (LazyDynaBean) degreeList.get(i);
			    			String mode = (String) bean.get("mode");
			    			String oper = (String) bean.get("oper");
			    			String value = (String) bean.get("value");
			    			String grouped = (String) bean.get("grouped");
			    			if("-1".equalsIgnoreCase(grouped))
						    	grouped = "";
			    			String actIds = (String) bean.get("actIds");
			    			String UMGrade = (String) bean.get("UMGrade");
			    			    
			    			degree.setAttribute("Mode", mode);
			    			degree.setAttribute("Oper", oper);
			    			degree.setAttribute("Value", value);
			    			degree.setAttribute("Grouped", grouped);
			    			degree.setAttribute("ActIds", actIds);
			    			degree.setAttribute("UMGrade", UMGrade);
			    			Rplan.addContent(degree);
			    		}  
			        	plannode.addContent(Rplan);
			        				        	
					}
				}       		
        	}
       	
        	if(root.getChild("AdvancedDegrees")==null)
        	{
        		Element ele = new Element("AdvancedDegrees");
        		root.addContent(ele);
        		plannode = ele;
        	}
        	
        	Element Rplan = new Element("AdvancedDegree");
    		Rplan.setAttribute("used", used);
    		Rplan.setAttribute("toRoundOff", toRoundOff);
    		Rplan.setAttribute("degree_id", degreeId);       		
    		for (int i = 0; i < list.size(); i++)
			{
			    Element degree = new Element("Degree");
			    LazyDynaBean bean = (LazyDynaBean) list.get(i);
			    String mode = (String) bean.get("mode");
			    String oper = (String) bean.get("oper");
			    String value = (String) bean.get("value");
			    String grouped = (String) bean.get("grouped");
			    if("-1".equalsIgnoreCase(grouped))
			    	grouped = "";
			    String actIds = (String) bean.get("actIds");
			    String UMGrade = (String) bean.get("UMGrade");
			    
			    degree.setAttribute("Mode", mode);
			    degree.setAttribute("Oper", oper);
			    degree.setAttribute("Value", value);
			    degree.setAttribute("Grouped", grouped);
			    degree.setAttribute("ActIds", actIds);
			    degree.setAttribute("UMGrade", UMGrade);
			    Rplan.addContent(degree);
			}  
    		plannode.addContent(Rplan);
        	
    	}
    	XMLOutputter outputter=new XMLOutputter();
    	Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		ContentDAO dao = new ContentDAO(this.conn);
	    ArrayList paramList = new ArrayList();
		try 
		{
			paramList.add(outputter.outputString(this.a_doc));
			paramList.add(Integer.parseInt(this.planid));
		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
		    
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
    }
    
    /**
     * 删除等级设置的高级设置  JinChunhai 2011.11.02  
     */
    public void deleteGradeHighValue(String[] strs)
    {
    	HashMap degreeMap = new HashMap();
    	if(this.a_doc!=null)
    	{
    		Element root = this.a_doc.getRootElement();
        	Element plannode = root.getChild("AdvancedDegrees");
        	if(plannode==null)
        	{       		
        		
        	}else
        	{      
        		// 查出已经存在的等级高级设置 放到 degreeMap 保存起来
        		try
        		{
	        		String extpro = this.getExtpro();
	        		if (extpro == null || "".equals(extpro))
	        		    return;
	        		Document doc = PubFunc.generateDom(extpro);
	    		    String xpath = "//AdvancedDegrees";
	    		    XPath xpath_ = XPath.newInstance(xpath);        	    
	        	   
	        	    Element dele = (Element) xpath_.selectSingleNode(doc); 
	        	    if(dele != null)
	        	    {
	    	    	    List list1 = (List) dele.getChildren("AdvancedDegree");
	    				for (int i = 0; i < list1.size(); i++)
	    				{   					
	    					Element ele = (Element) list1.get(i);;     						   
	    					
	    					if (ele != null)
	    		    	    {	    						
	    						ArrayList degreeList = new ArrayList();
	    		    	    	String cused = ele.getAttributeValue("used");
	    		    	    	String ctoRoundOff = ele.getAttributeValue("toRoundOff");
	    		    	    	String cdegree_id = ele.getAttributeValue("degree_id");
	    		    	    	
	    		    	    	boolean logoSign = true;
	    		    	    	// 刨除等级编号相等的记录
	    		    	    	for (int n = 0; n < strs.length; n++)
	    					    {							
	    							String degreeId = (String)strs[n];
	    							if(degreeId.equalsIgnoreCase(cdegree_id))	    							
	    								logoSign = false;	    								    							
	    					    }
	    		    	    	
	    		    	    	if(logoSign)
	    		    	    	{
		    		    	    	List dlist = (List) ele.getChildren("Degree");
		    		    	    	for (int k = 0; k < dlist.size(); k++)
		    		    	    	{    	    		    
		    		    	    		Element temp = (Element) dlist.get(k);
		    		    	    		if(temp!=null)
		    		    	    		{	    		    	    			   
			    		    	    		LazyDynaBean abean = new LazyDynaBean();
			    		    	    		abean.set("mode", temp.getAttributeValue("Mode"));
			    		    	    		abean.set("oper", temp.getAttributeValue("Oper"));
			    		    	    		abean.set("value", temp.getAttributeValue("Value"));
			    		    	    		abean.set("grouped", temp.getAttributeValue("Grouped"));
			    		    	    		abean.set("actIds", temp.getAttributeValue("ActIds"));
			    		    	    		abean.set("UMGrade", temp.getAttributeValue("UMGrade")==null?"":temp.getAttributeValue("UMGrade"));
			    		    	    		degreeList.add(abean);
		    		    	    		}	    		    	    		
		    		    	    	}
		    		    	    	degreeMap.put(cused+"`"+ctoRoundOff+"`"+cdegree_id, degreeList);	
	    		    	    	}
	    		    	    }
	    		    	}	    					
	    			}	        	
	        	    
        		} catch (Exception e)
        		{
        		    e.printStackTrace();
        		}
        		
        		
        		// 删除节点 AdvancedDegrees
        		root.removeChildren("AdvancedDegrees");  
        		       	
	        	
	        	// 增加已放到 degreeMap 中的等级高级设置 
	        	if(degreeMap!=null && degreeMap.size()>0)
	        	{
	        		Set keySet=degreeMap.keySet();
					java.util.Iterator t=keySet.iterator();
					while(t.hasNext())
					{
						String strKey = (String)t.next();  //键值	    
						ArrayList degreeList  = (ArrayList)degreeMap.get(strKey);   //value值   
						String cused=strKey.split("`")[0];					
						String ctoRoundOff=strKey.split("`")[1];					
						String cdegree_id=strKey.split("`")[2];					
						//String cused=strKey.substring(0,strKey.indexOf("`")); // used					
						//String cdegree_id=strKey.substring(strKey.indexOf("`")+1); // degree_id 
												
						Element plannodeDegree = root.getChild("AdvancedDegrees");
				        if(plannodeDegree==null)
				        {       		
				        	Element ele = new Element("AdvancedDegrees");
				        	root.addContent(ele);
				        	plannode = ele;
				        }else
				        {
				        }
				        Element Rplan = new Element("AdvancedDegree");
				        Rplan.setAttribute("used", cused);
				        Rplan.setAttribute("toRoundOff", ctoRoundOff);
				        Rplan.setAttribute("degree_id", cdegree_id);       		
				        for (int i = 0; i < degreeList.size(); i++)
				    	{
				        	Element degree = new Element("Degree");
				    		LazyDynaBean bean = (LazyDynaBean) degreeList.get(i);
				    		String mode = (String) bean.get("mode");
				    		String oper = (String) bean.get("oper");
				    		String value = (String) bean.get("value");
				    		String grouped = (String) bean.get("grouped");
				    		if("-1".equalsIgnoreCase(grouped))
						    	grouped = "";
				    		String actIds = (String) bean.get("actIds");
				    		String UMGrade = (String) bean.get("UMGrade");
				    			    
				    		degree.setAttribute("Mode", mode);
				    		degree.setAttribute("Oper", oper);
				    		degree.setAttribute("Value", value);
				    		degree.setAttribute("Grouped", grouped);
				    		degree.setAttribute("ActIds", actIds);
				    		degree.setAttribute("UMGrade", UMGrade);
				    		Rplan.addContent(degree);
				    	}  
				        plannode.addContent(Rplan);				        				        							
					}       		
	        	}
	        	if(plannode!=null)
	        	{
		        	XMLOutputter outputter=new XMLOutputter();
		        	Format format=Format.getPrettyFormat();
		    		format.setEncoding("UTF-8");
		    		outputter.setFormat(format);
		    		ContentDAO dao = new ContentDAO(this.conn);
		    	    ArrayList paramList = new ArrayList();
		    		try 
		    		{
		    			paramList.add(outputter.outputString(this.a_doc));
		    			paramList.add(Integer.parseInt(this.planid));
		    		    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
		    		    
		    		} catch (SQLException e) 
		    		{
		    			e.printStackTrace();
		    		}
	        	}
	    	}       	
    	}   	
    }   
    
   /**
    * 取得高级设置的xml内容
    */
    public String getExtpro()
    {
   		RowSet rs = null;
		String extpro = "";
		StringBuffer strsql = new StringBuffer();
		if(this.planid!=null && this.planid.trim().length()>0)
		{
			strsql.append("select parameter_content from per_plan where plan_id=");
			strsql.append(this.planid);
		}				
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next())
		    {
				String temp = rs.getString(1);
				if (extpro != null)
				    extpro = temp;
		    }
		    if(rs!=null)
		    	rs.close();
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return extpro;
    }
    
    /**
     * 得到主体评分范围所有设置
     * @param node 节点名称 ScoreScope
     * @return
     */
    public ArrayList getPerGradeScopeList(String node )
    {
    	ArrayList list = new ArrayList();
    	if(this.a_doc==null)
    		return list;
    	Element root = this.a_doc.getRootElement();
    	Element plannode = root.getChild("ScoreScopes");
    	if(plannode!=null)
    	{
    		List planlist = plannode.getChildren(node);
    		for(int i=0;i<planlist.size();i++)
    		{
    			Element plan = (Element)planlist.get(i);   			
    			if(plan!=null)
	    		{
    				LazyDynaBean abean=new LazyDynaBean();
	    			abean.set("BodyId", plan.getAttributeValue("BodyId"));	    			
	    			abean.set("DownScope", plan.getAttributeValue("DownScope"));
	    			abean.set("UpScope", plan.getAttributeValue("UpScope"));
	    			list.add(abean);
	    		}
    		}
    	}
    	return list;
    }
    
    /**
     * 保存描述性评议项设置 
     */
    public void saveAppraiseValue(String tableName,ArrayList list)
    {
    	HashMap degreeMap = new HashMap();
    	if(this.a_doc!=null)
    	{
    		Element root = this.a_doc.getRootElement();
        	Element plannode = root.getChild("descriptive_evaluate");
        	if(plannode!=null)
        	{    
        		// 删除节点 descriptive_evaluate
        		root.removeChildren("descriptive_evaluate");        		
        	}
        	Element ele = new Element("descriptive_evaluate");
    		root.addContent(ele);
    		plannode = ele;        	       	
        	      		
    		for (int i = 0; i < list.size(); i++)
			{
			    Element option = new Element("option");
			    LazyDynaBean bean = (LazyDynaBean) list.get(i);
			    String id = (String) bean.get("id");
			    String seq = (String) bean.get("seq");
			    String value = (String) bean.get("value");
			    
			    option.setAttribute("id", id);
			    option.setAttribute("seq", seq);
			    option.setText(value);

			    plannode.addContent(option);
			}          	
    	}
    	XMLOutputter outputter=new XMLOutputter();
    	Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		ContentDAO dao = new ContentDAO(this.conn);
	    ArrayList paramList = new ArrayList();
		try 
		{
			if(this.a_doc!=null && this.a_doc.toString().trim().length()>0)
			{
				paramList.add(outputter.outputString(this.a_doc));
				paramList.add(Integer.parseInt(this.planid));
			    dao.update("update per_plan set parameter_content=? where plan_id=?",paramList);
			}
		    
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
    }
    
}
