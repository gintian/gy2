package com.hjsj.hrms.businessobject.performance.batchGrade;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.ComputFormulaBo;
import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveEvaluateBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.OrgPerformanceBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:BatchGradeBo.java</p>
 * <p>Description:多人考评</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BatchGradeBo
{

    private Connection conn = null;
    private UserView userView = null;
    private int columnWidth = 100;
    private int firstColumnWidth=120;   
    private int columnWidth_lower = 60;
    private StringBuffer span_ids = new StringBuffer("");
    private HashMap objectTotalScoreMap = new HashMap();
    private ArrayList objectList = new ArrayList();
    private HashMap userNumberPointResultMap = new HashMap();           
    public static  HashMap planLoadXmlMap=new HashMap();
    public static  HashMap pointListMap=new HashMap();
    
    static  HashMap plan_perPointMap=new HashMap();
    static  HashMap plan_perPointMap2=new HashMap();
    private boolean isLoadStaticValue=false;  //是否读取静态变量里的值    
    private boolean isBatchGradeRadio=false;  //潍柴 多人打分采用单选按钮的形式
    private HashMap perPointGradedescMap=new HashMap(); //计划中每个指标的标度
    private LoadXml loadxml = null;
    private String DegreeShowType = "1"; // 1-标准标度 2-指标标度
    private String BlankScoreOption = "0"; // 指标未打分时，0 按未打分处理，1
                                                // 计为最高分，默认值为按未打分处理 2用下面的参数

    private String BlankScoreUseDegree = ""; // 指标未打分，按用户定义的标度, 具体选自标准标度中,
                                                // 如果指标中没有所定义标度，按未打分处理。A|B|C…

    private String showOneMark = "False"; // BS打分时显示统一打分的指标，以便参考 Boolean, 默认为False
    private boolean noShowOneMark=false;  //程序控制不显示统一打分指标    
    private String pointContrl = ""; // 指标打分控制 当取消不打分选项时，为1的指标恢复打分权限 为0的仍然置灰
    private String object_type = "2"; // 1:部门 2：人员
    private String isShowTotalScore = "false"; // 是否现实总分
    private String isShowOrder = "true"; // 是否显示排名
    private String isEntiretySub = "true"; // 提交是否需要必填
    private String scoreNumPerPage = "0"; // BS打分时每页的人数，0为不限制
    private String NodeKnowDegree = ""; // 了解程度
    private  String totalAppFormula=""; // 总体评价的计算公式，默认为空
    private String ShowSumRow="False";  //显示合计行
    private String WholeEval = ""; // 总体评价
    private String DescriptiveWholeEval = "True"; // 显示描述性总体评价，默认为 True
    private String GradeClass = ""; // 等级分类ID
    private String limitation = ""; // =-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)
    private String ShowEmployeeRecord="False";  //显示员工日志
    private String showDayWeekMonth = "1,2,3"; //查看日志填报情况
	private LinkedHashMap<Integer,String> mainBodyMap=null;//查看多人评分使用 存储当前考核对象的所有考核主体评分 zhanghua
	private String showHistoryScore = "False";
    private String WholeEvalMode="0";
    public String getWholeEvalMode() {
		return WholeEvalMode;
	}

	public void setWholeEvalMode(String wholeEvalMode) {
		WholeEvalMode = wholeEvalMode;
	}

	public String getShowHistoryScore() {
		return showHistoryScore;
	}

	public void setShowHistoryScore(String showHistoryScore) {
		this.showHistoryScore = showHistoryScore;
	}

	public String getShowDayWeekMonth() {
		return showDayWeekMonth;
	}

	public void setShowDayWeekMonth(String showDayWeekMonth) {
		this.showDayWeekMonth = showDayWeekMonth;
	}

	private String SummaryFlag = ""; // 个人总结评价作为评分标准
    private String scoreflag = ""; // =2混合，=1标度
    private String showNoMarking = ""; // 显示不打分
    private String scaleToDegreeRule = ""; // 分值转标度规则1-就高 2-就低）
    private String isShowSubmittedPlan = ""; // （提交后的计划是否需要显示True|False
    private String isAutoCountTotalOrder = ""; // 是否自动计算总分和排名
    private String mitiScoreMergeSelfEval = "False"; // 多人打分时同时显示自我评价
    private String allowSeeLowerGrade = "False"; // 允许查看下级对考核对象评分 默认为False
    private boolean haveLower = false;
    private HashMap objectsLowerMap = new HashMap(); 
    private String noteIdioGoal = "false"; // 显示个人目标
    private String relatingTargetCard = "1"; // 关联目标卡 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
    private String showYPTargetCard = "0";
    private String DynaBodyToPoint="False";  //动态主体权重控制到指标/任务True, False, 默认为 False
    private String KeepDecimal = "0";
    private String selfScoreInDirectLeader = ""; // 上级领导给下级打分时是否
                                                        // 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False
                                                        // 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
    private String FineRestrict = "";
    private HashMap fineMaxMap = new HashMap();
    private HashMap objectInfoMap = new HashMap();
    private HashMap dynaRankInfoMap = new HashMap();
    private HashMap objDynaRankMap = new HashMap(); // 动态指标权重
    private HashMap pointMaxValueMap = new HashMap();
    private String planid = ""; // 考核计划id
    private String performanceType = "0"; // 考核形式 0：绩效考核 1：民主评测
    private int constructType = 0; // 构造方法 0：BatchGradeBo(Connection conn)
                                        // 1：BatchGradeBo(Connection conn,String
                                        // planid,String examineKind)
    private RecordVo planVo = null;
    private ArrayList per_pointLists = null; // getPerPointList2(templateID,plan_id);
                                                // 方法得到指标的信息集
    private StringBuffer script_code = new StringBuffer(" var obj_result= new Array();\r\n");
    private String auto_createTable = "false";
    private boolean isSelfScoreColumn = false; // 是否显示自我评分列
    private HashMap objectSelfScoreMap = new HashMap(); // 考核主体是否对考核对象具有查看自我评分值

    private String batchGradeOthField="";
    private ArrayList basicFieldList=new ArrayList();   //基本信息指标
    private String    LockMGradeColumn="True";          //锁定指标列
    private String object_id = "";
    
    private String voteFlag = "";
    private String hireState="";//当前的测评状态 初试或者复试，如果没有设置高级测评状态这个为空
    public String getHireState() {
		return hireState;
	}

	public void setHireState(String hireState) {
		this.hireState = hireState;
	}

	public String getVoteFlag() {
		return voteFlag;
	}

	public void setVoteFlag(String voteFlag) {
		this.voteFlag = voteFlag;
	}

	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	 Calendar calendar = Calendar.getInstance();				
	 String historyDate = sdf.format(calendar.getTime());
    public BatchGradeBo(Connection conn)
    {
	
		this.conn = conn;
		this.constructType = 0;
    }
    
    /**
     * 获取当前主体考核对象涉及到的主体类别
     * @return
     * @author zhanghua
     * @date 2017年9月12日 下午4:11:31
     */
    public LinkedHashMap<Integer, String> getMainBodyMap() {
    	
    	if(mainBodyMap==null){
    		LinkedHashMap<Integer,String> map=new LinkedHashMap();
        	ContentDAO dao = new ContentDAO(this.conn);
        	RowSet rowSet = null;
        	try{
        		String cloumn="level";
    			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    				cloumn="level_o";
        		StringBuffer strSql=new StringBuffer();
        		strSql.append(" select per_mainbodyset.body_id,per_mainbodyset.name from per_mainbodyset ");
        		strSql.append(" where body_id in ( select distinct body_id from per_mainbody where object_id in");
        		strSql.append(" (select distinct object_id from per_mainbody where mainbody_id ='");
        		strSql.append(this.userView.getA0100());
        		strSql.append("' and  plan_id=");
        		strSql.append(this.getPlanid());
        		strSql.append(") and plan_id=");
        		strSql.append(this.getPlanid());
        		strSql.append(")");
        		if(!this.isSelfScoreColumn) {
                    strSql.append(" and per_mainbodyset." + cloumn + " <> 5 ");
                }
                strSql.append(" and (per_mainbodyset." + cloumn + " = 5 ");
                strSql.append("or per_mainbodyset."+cloumn+">(select min("+cloumn+") from per_mainbodyset ");
                strSql.append("where body_id in ( select distinct(body_id) from per_mainbody where mainbody_id = '"+this.userView.getA0100()+"' and plan_id = "+this.getPlanid()+")))");

        		strSql.append( " order by case when per_mainbodyset."+cloumn+"=5 then -100 else per_mainbodyset."+cloumn+" end ");
        		rowSet=dao.search(strSql.toString());
        		while(rowSet.next()){
        			map.put(rowSet.getInt("body_id"), rowSet.getString("name"));
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	this.setMainBodyMap(map);
    	}
		return mainBodyMap;
	}

	public void setMainBodyMap(LinkedHashMap<Integer, String> mainBodyMap) {
		this.mainBodyMap = mainBodyMap;
	}
    /**
     * @param conn
     * @param planid
     * 考核计划
     */
    public BatchGradeBo(Connection conn, String planid)
    {
		this.conn = conn;
		this.planid = planid;		
		this.constructType = 1;
		this.planVo = getPlanVo(planid);
		this.object_type = String.valueOf(this.planVo.getInt("object_type"));
		initParamSet();
		if(SystemConfig.getPropertyValue("batchGradeOthField")!=null&&SystemConfig.getPropertyValue("batchGradeOthField").trim().length()>0)
		{
			batchGradeOthField=SystemConfig.getPropertyValue("batchGradeOthField").trim();		
			FieldItem itemfield=DataDictionary.getFieldItem(batchGradeOthField);
			if(!(itemfield!=null&& "A01".equalsIgnoreCase(itemfield.getFieldsetid())&&planVo.getInt("object_type")==2))
				batchGradeOthField="";
		}
    }
    
    public BatchGradeBo(Connection conn,UserView userView,String planid)
    {
    	
		this.conn = conn;
		this.userView = userView;
		this.planid = planid;
		
		this.constructType = 0;
		this.planVo = getPlanVo(planid);
		this.object_type = String.valueOf(this.planVo.getInt("object_type"));
		initParamSet();
		if(SystemConfig.getPropertyValue("batchGradeOthField")!=null&&SystemConfig.getPropertyValue("batchGradeOthField").trim().length()>0)
		{
			batchGradeOthField=SystemConfig.getPropertyValue("batchGradeOthField").trim();		
			FieldItem itemfield=DataDictionary.getFieldItem(batchGradeOthField);
			if(!(itemfield!=null&& "A01".equalsIgnoreCase(itemfield.getFieldsetid())&&planVo.getInt("object_type")==2))
				batchGradeOthField="";
		}
    }
    
    public RecordVo getPlanVo(String planid)
    {

		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    ContentDAO dao = new ContentDAO(this.conn);
		    if(planid!=null && !"0".equals(planid))
		        vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }

    public void initParamSet()
    {
		try
		{
			if(planLoadXmlMap.get(this.planid)==null)
			{
				loadxml = new LoadXml(this.conn, this.planid);
				planLoadXmlMap.put(this.planid,loadxml);
			}
			else
				loadxml=(LoadXml)planLoadXmlMap.get(this.planid);
			
			
			Hashtable htxml = new Hashtable();
			htxml = loadxml.getDegreeWhole();
			 
		    this.BlankScoreOption = (String) htxml.get("BlankScoreOption");
			this.BlankScoreUseDegree = (String) htxml.get("BlankScoreUseDegree"); // 指标未打分，按用户定义的标度,    
	       // 如果指标中没有所定义标度，按未打分处理。A|B|C…
			this.isShowTotalScore = (String) htxml.get("ShowTotalScoreSort");
			this.DynaBodyToPoint=(String)htxml.get("DynaBodyToPoint");
			this.showOneMark = (String) htxml.get("ShowOneMark"); // BS打分时显示统一打分的指标，以便参考  默认为False
			this.isShowOrder = (String) htxml.get("isShowOrder");
			this.isEntiretySub = (String) htxml.get("isEntireysub");
			this.NodeKnowDegree = (String) htxml.get("NodeKnowDegree"); // 了解程度
			this.totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			String EvalClass = (String)htxml.get("EvalClass");            //在计划参数中的等级分类ID
			this.GradeClass = "";
			 if(EvalClass==null||EvalClass.trim().length()<0|| "0".equals(EvalClass.trim()))
			   this.GradeClass=(String)htxml.get("GradeClass");					//等级分类ID
			 else 
			   this.GradeClass=(String)htxml.get("EvalClass");
			this.DescriptiveWholeEval = (String) htxml.get("DescriptiveWholeEval");
			this.GradeClass = (String) htxml.get("GradeClass"); // 等级分类ID
			this.limitation = (String) htxml.get("limitation"); // =-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)
			this.performanceType = (String) htxml.get("performanceType"); // 考核形式  1：民主评测
			this.SummaryFlag = (String) htxml.get("SummaryFlag"); // 个人总结评价作为评分标准
		    this.scoreflag = (String) htxml.get("scoreflag"); // =2混合，=1标度
			this.showNoMarking = (String) htxml.get("ShowNoMarking");
			this.scoreNumPerPage = (String) htxml.get("ScoreNumPerPage"); // BS打分时每页的人数，0为不限制
			this.scaleToDegreeRule = (String) htxml.get("limitrule"); // 分值转标度规则1-就高
		          //            htxml.put("PointEvalType","1");                                                  // 2-就低）
			this.ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志  
			this.showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth");
			this.showHistoryScore=(String)htxml.get("ShowHistoryScore");//显示历次得分
			this.isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan"); // //提交后的计划是否需要显示True|False
			this.FineRestrict = (String) htxml.get("FineRestrict");
			this.fineMaxMap = (HashMap) htxml.get("fineMaxMap");
			this.isAutoCountTotalOrder = (String) htxml.get("AutoCalcTotalScoreAndOrder"); // 是否自动计算总分和排名
			this.mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");
			this.allowSeeLowerGrade = (String)htxml.get("allowSeeLowerGrade");
			this.DegreeShowType = (String) htxml.get("DegreeShowType");// 1-标准标度
		                                                                        // 2-指标标度
			this.noteIdioGoal = ((String) htxml.get("noteIdioGoal")).toLowerCase(); // 显示个人目标
			this.relatingTargetCard = (String) htxml.get("relatingTargetCard"); // 关联目标卡 1-不关联 2-查看对象目标卡 3-查看本人对考核对象的目标卡评分
			this.showYPTargetCard = (String)htxml.get("showYPTargetCard");//True:显示已自评目标卡 False：不显示
			if(showYPTargetCard==null || "False".equalsIgnoreCase(showYPTargetCard)){
				this.showYPTargetCard = "0";
			}else if(showYPTargetCard!=null && "True".equalsIgnoreCase(showYPTargetCard)){
				this.showYPTargetCard = "1";
			}
			this.KeepDecimal = (String) htxml.get("KeepDecimal"); // 小数位
			this.selfScoreInDirectLeader = (String) htxml.get("SelfScoreInDirectLeader"); // 上级领导给下级打分时是否
		                                                                                                // 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False
		                                                                                                // 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体.
			this.ShowSumRow=(String)htxml.get("ShowSumRow");// 显示合计行		   
			if ("1".equals(this.performanceType))
		    {
			    this.SummaryFlag = "True";
			}
			
		/*
			if(this.object_type.equals("2"))
			{
				htxml.put("ShowBasicInfo", "True");
				htxml.put("BasicInfoItem", "A0107");
				htxml.put("LockMGradeColumn", "True");
			}
			else
			{
				htxml.put("ShowBasicInfo", "True");
				htxml.put("BasicInfoItem", "b0145,B0120,B0130");
				htxml.put("LockMGradeColumn", "True");
			}
		*/
			String ShowBasicInfo=(String)htxml.get("ShowBasicInfo");
		    String BasicInfoItem=(String)htxml.get("BasicInfoItem");
		    LockMGradeColumn=(String)htxml.get("LockMGradeColumn"); 
		    if("True".equalsIgnoreCase(ShowBasicInfo)&&BasicInfoItem.trim().length()>0)
		    	this.basicFieldList=getFieldList(BasicInfoItem);
			
			
			
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
    }
   
    /***
     * 联通的单独门户绩效考评接口  该类的构造函数用  BatchGradeBo(Connection conn)
     * @param userView
     * @return
     */
    public HashMap get_LT_plansMap(UserView userView)
    {
    	HashMap map=new HashMap();
    	RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn);
    		RowSet frowset=null;
    		CommonData vo=null;
			String noFinishedPlanID="";
			String noSub="1";
			StringBuffer str2=new StringBuffer(",");
			boolean table1_flag=false;
			boolean table2_flag=false;
			int D0150=1;
			String tableType="0";  // 0:省级用表 1:总部用表
    		if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
    		{
		    	CommendTableBo bo=new CommendTableBo(this.conn,userView) ;
		    	tableType=bo.getTableType();
		    	if(SystemConfig.getPropertyValue("recommend_table")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("recommend_table").trim()))
		    	{
	        		if("0".equals(tableType))
			    	{
					
			    		int limitNum=0;         //限定人数
			     		String un_limit_item="";   
			    		String recommend_unit="";    //推荐单位
			    		String recommend_flag_item="";   //推荐班子指标
			    		if(SystemConfig.getPropertyValue("recommend_unit_item")!=null&&SystemConfig.getPropertyValue("recommend_unit_item").trim().length()>0)
			    		{
			     			String item_id=SystemConfig.getPropertyValue("recommend_unit_item").trim();
			     			rowSet=dao.search("select "+item_id+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'");
			    			if(rowSet.next())
					    		recommend_unit=rowSet.getString(1)!=null?rowSet.getString(1):"";
			    		}
			    		if(recommend_unit.length()>0)
			    		{
				    		if(SystemConfig.getPropertyValue("un_limit_item")!=null&&SystemConfig.getPropertyValue("un_limit_item").trim().length()>0)
				    			un_limit_item=SystemConfig.getPropertyValue("un_limit_item").trim();
						
				    		if(un_limit_item.length()>0)
				    		{
				    			rowSet=dao.search("select "+un_limit_item+",D0150 from B01 where b0110='"+recommend_unit+"'");
							
					    		if(rowSet.next())
					    		{
					    			if(rowSet.getString(1)!=null)
						    			limitNum=rowSet.getInt(1);
						    		if(rowSet.getString(2)!=null)
						    			D0150=rowSet.getInt(2);
					    		}
						    	if(limitNum>0)
					    		{
						    		rowSet=dao.search("select distinct flag from per_recommend_result  where a0100='"+userView.getA0100()+"' and upper(nbase)='"+userView.getDbname().toUpperCase()+"'   and c11=1");
						    		int flag=0;
						    		int num=0;
						    		if(rowSet.next())
						    		{	flag=rowSet.getInt("flag");
						    			num++;
						     		}
						     		String desc=ResourceFactory.getProperty("performance.batchgrade.unRecommend");
					    			if(num>0)
						    		{
						    			if(flag==0)
						    				desc=ResourceFactory.getProperty("performance.batchgrade.recommending");
							    		else if(flag==1)
							     		{
							     			desc=ResourceFactory.getProperty("performance.batchgrade.recommended");
							        		table1_flag=true;
						    			}
						    			else if(flag==2)
							    		{	
							    			desc=ResourceFactory.getProperty("performance.batchgrade.submited");
							    			table1_flag=true;
							    		}
							    	}
								
						    		if(!desc.equals(ResourceFactory.getProperty("performance.batchgrade.submited")))
						    			noSub="0";
								
						    		vo=new CommonData();
							    	if(D0150==2)
							    		vo.setDataName(ResourceFactory.getProperty("performance.batchgrade.democraticRecommendChart1")+" "+desc);
						    		else
							    		vo.setDataName(ResourceFactory.getProperty("performance.batchgrade.democraticRecommendChart2")+" "+desc);
						 		
						    		if(D0150==2)
							    		vo.setDataValue("/performance/commend_table/commend_table.do?b_init1=query&D0150=2");
							    	else
						      			vo.setDataValue("/performance/commend_table/commend_table.do?b_init1=query&D0150=1");
						     		map.put("table1",vo);
						    	}
						    	else
						    		table1_flag=true;
				    		}
				     		else
				    			table1_flag=true;
			    		}
				    	else
				    		table1_flag=true;
		       		}
		    		else
			    		table1_flag=true;
		    	}
		     	else
		     		table1_flag=true;
			
         	}
    		ArrayList classTypeList = new ArrayList();
    		if(D0150!=2)
    		{
    			if("".equals(SystemConfig.getPropertyValue("per_visible_type"))|| "score".equalsIgnoreCase(SystemConfig.getPropertyValue("per_visible_type")))
    			{
	        		String perPlanSql = "select plan_id,name,status,parameter_content,content,"+Sql_switcher.isnull("a0000", "999999")+" as norder from per_plan where ( status=4 or status=6 ) ";
		     		if (!userView.isSuper_admin())
			    		perPlanSql += "and plan_id in (select plan_id from per_mainbody where   mainbody_id='"
				    			+ userView.getA0100() + "' )";
			    	perPlanSql += "  and ( Method=1 or method is null )   order by norder asc,plan_id desc";
			    	frowset = dao.search(perPlanSql);
			     	LoadXml aloadxml=null;
			    	while (frowset.next()) 
			    	{
				    	String name = frowset.getString("name");
			    		String plan_id = frowset.getString("plan_id");
				      	String content=Sql_switcher.readMemo(frowset,"content");
				    	String key="kpi";
					/*if(content.trim().equalsIgnoreCase("KPI"))
						key="kpi";
					else if(content.trim().equals("贡献度测评"))
						key="gxd";
					else if(content.trim().equals("胜任度测评"))
						key="srd";
					else if(content.trim().equals("道德测评"))
						key="dd";
					
					if(key.length()==0)
						continue;*/
				    	/**北京公安计划分类 支部评党委、 党员评书记、民警评干部*/
				    	if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				    	{
				    		/*if(content.trim().equalsIgnoreCase("支部评党委"))
								key="1";
							else if(content.trim().equals("党员评书记"))
								key="2";
							else if(content.trim().equals("民警评干部"))
								key="3";*/
				    		key=content.toUpperCase();
				    	}
				     	if(this.planLoadXmlMap.get(plan_id)==null)
				    	{
				    		aloadxml = new LoadXml(this.conn,plan_id);
				    		BatchGradeBo.getPlanLoadXmlMap().put(plan_id,aloadxml);
				    	}
			     	   	else
			    		{
			     			aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			    		}
			     		Hashtable htxml = aloadxml.getDegreeWhole();
			     		String performanceType=(String)htxml.get("performanceType");
			    		String HandEval=(String)htxml.get("HandEval");//是否手工打分
				    	if("true".equalsIgnoreCase(HandEval))
				    		continue;
	                    if("0".equals(performanceType))
	                    {
					    	vo = new CommonData(plan_id, name);
				    		ArrayList dblist=new ArrayList();
				    		if(map.get(key)!=null)
				    		{
				     			dblist=(ArrayList)map.get(key);
				    		}else{
				    			classTypeList.add(key);
				    		}
				     		dblist.add(vo);
				     		map.put(key,dblist);
	                    }
			    	}
    			}
	    		
				Set keySet=map.keySet();
				HashMap subMap =new HashMap();
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String key=(String)t.next();
					if("table1".equalsIgnoreCase(key))
						continue;
					
					ArrayList dbList=(ArrayList)map.get(key);
					ArrayList a_dbList=addGradeStaus(dbList,userView.getA0100(),3);		
					for(int i=0;i<a_dbList.size();i++)
					{
						vo=(CommonData)a_dbList.get(i);
						String plan_id=vo.getDataValue();
						String plan_name=vo.getDataName();
						str2.append(plan_id+",");	
						
						if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
						{
							if(plan_name.indexOf("("+ResourceFactory.getProperty("performance.batchgrade.graded")+")")==-1&&plan_name.indexOf("("+ResourceFactory.getProperty("performance.batchgrade.evaluated")+")")==-1)
								noFinishedPlanID+=","+plan_id;
						}
						else
						{
							if(plan_name.indexOf("("+ResourceFactory.getProperty("performance.batchgrade.finished")+")")==-1&&plan_name.indexOf("("+ResourceFactory.getProperty("performance.batchgrade.evaluated")+")")==-1)
								noFinishedPlanID+=","+plan_id;
						}
						
						if(plan_name.indexOf(ResourceFactory.getProperty("performance.batchgrade.evaluated"))==-1)
							noSub="0";
						if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
						{
							if(key.equalsIgnoreCase(ResourceFactory.getProperty("performance.batchgrade.info6"))||key.equalsIgnoreCase(ResourceFactory.getProperty("performance.batchgrade.info7")))
							{
								int count = this.getCountObject(plan_id, userView);
								if(count>1)
								{
									vo.setDataValue("/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=liantong&planContext=all&operate=aaa"+plan_id);
								}
								else
								{
									vo.setDataValue("/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id="+plan_id);
								}
							}
							else
							{
								vo.setDataValue("/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=liantong&planContext=all&operate=aaa"+plan_id);
							}
						}else
						{
				    		if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("performancePanelSingleGrade")))
				    		{
				    			vo.setDataValue("/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id="+plan_id);
			    			}
			    			else
			    			{
			        			vo.setDataValue("/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=liantong&planContext=all&operate=aaa"+plan_id);
			    			}
						}
					}
					if("bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
						subMap.put(key,a_dbList);
					else
			    		map.put(key,a_dbList);
				}
				if("bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					map.put("subMap", subMap);
				//民主推荐表
				if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))&&SystemConfig.getPropertyValue("recommend_table")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("recommend_table").trim()))
				{
					rowSet=dao.search("select distinct flag from per_recommend_result  where a0100='"+userView.getA0100()+"' and upper(nbase)='"+userView.getDbname().toUpperCase()+"'   and  ( c11 is null or c11=0 ) ");
					int flag=0;
					int num=0;
					if(rowSet.next())
					{	flag=rowSet.getInt("flag");
						num++;
					}
					String desc=ResourceFactory.getProperty("performance.batchgrade.unRecommend");
					if(num>0)
					{
						if(flag==0)
							desc=ResourceFactory.getProperty("performance.batchgrade.recommending");
						else if(flag==1)
						{
							desc=ResourceFactory.getProperty("performance.batchgrade.recommended");
							table2_flag=true;
						}
						else if(flag==2)
						{	desc=ResourceFactory.getProperty("performance.batchgrade.submited");
							table2_flag=true;
						}
					}
					
					if(!desc.equals(ResourceFactory.getProperty("performance.batchgrade.submited")))
						noSub="0";
					
					vo=new CommonData();
					vo.setDataName(ResourceFactory.getProperty("performance.batchgrade.democraticRecommendChart3")+" "+desc);
					vo.setDataValue("/performance/commend_table/commend_table.do?b_init=query");
					map.put("table2",vo);
				}
				else
					table2_flag=true;
			
    		}
    		else
    			table2_flag=true;
			
			if(table1_flag&&table2_flag)
				map.put("tableFlag","2");
			else
				map.put("tableFlag","0");
			if(noFinishedPlanID.length()>0)
				map.put("noFinishedPlanID",noFinishedPlanID.substring(1));
			else
				map.put("noFinishedPlanID",noFinishedPlanID);
			map.put("planIDs", str2.toString());
			map.put("tableType",tableType);
			map.put("noSub", noSub);
			map.put("classTypeList",classTypeList);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(rowSet!=null)
    		{
    			try
    			{
    				rowSet.close();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return map;
    }
    /**
     * 得到考核对象总数
     * @param plan_id
     * @param userView
     * @return
     */
    public int getCountObject(String plan_id,UserView userView)
    {
    	int count=0;
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao  = new ContentDAO(this.conn);
    		rs = dao.search("select count(id) from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+userView.getA0100()+"'");
    		while(rs.next())
    		{
    			count=rs.getInt(1);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try
    		{
    			if(rs!=null)
    			{
    				rs.close();
    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return count;
    }
    public boolean isHaveSubmitPlan(String a0100,Connection conn)
    {
    	boolean flag = false;
    	RowSet rs = null;
    	HashMap map=new HashMap();
    	try
    	{
    		ContentDAO dao = new ContentDAO(conn);
    		String perPlanSql = "select plan_id,name,status,parameter_content,content,"+Sql_switcher.isnull("a0000", "999999")+" as norder from per_plan where ( status=4 or status=6 ) ";
     		
	    	perPlanSql += "and plan_id in (select plan_id from per_mainbody where   mainbody_id='"+ a0100+"' )";
	    	perPlanSql += "  and ( Method=1 or method is null )   order by norder asc,plan_id desc";
	    	rs = dao.search(perPlanSql);
	    	LoadXml aloadxml=null;
	    	CommonData vo = null;
	    	while (rs.next()) 
	    	{
		    	String name = rs.getString("name");
	    		String plan_id = rs.getString("plan_id");
		      	String content=Sql_switcher.readMemo(rs,"content");
		    	String key="kpi";
		    	aloadxml = new LoadXml(conn,plan_id);
	     		Hashtable htxml = aloadxml.getDegreeWhole();
	     		String performanceType=(String)htxml.get("performanceType");
	    		String HandEval=(String)htxml.get("HandEval");//是否手工打分
		    	if("true".equalsIgnoreCase(HandEval))
		    		continue;
                if("0".equals(performanceType))
                {
			    	
                	vo = new CommonData(plan_id, name);
		    		ArrayList dblist=new ArrayList();
		    		if(map.get(key)!=null)
		     			dblist=(ArrayList)map.get(key);
		     		dblist.add(vo);
		     		map.put(key,dblist);
                }
	    	}
	    	Set keySet=map.keySet();
			
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if("table1".equalsIgnoreCase(key))
					continue;
				
				ArrayList dbList=(ArrayList)map.get(key);
				ArrayList a_dbList=addGradeStaus2(dbList,a0100,3,conn);		
				for(int i=0;i<a_dbList.size();i++)
				{
					vo=(CommonData)a_dbList.get(i);
					String plan_id=vo.getDataValue();
					String plan_name=vo.getDataName();	
					if(plan_name.indexOf(ResourceFactory.getProperty("performance.batchgrade.evaluated"))==-1)
						flag=true;
				}
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return flag;
    }
    /**
     * 取得登录用户可以操作的考核计划
     * @param userView
     * @return
     */
    public HashMap getCanOperatorPlan(UserView userView)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try
    	{
    		MyObjectiveBo mbo = new MyObjectiveBo(this.conn,userView);
    		OrgPerformanceBo obo = new OrgPerformanceBo(this.conn);
    		HashMap welcomMap = obo.getOrgPlanWelcomeList(userView.getA0100(), "-1", "-1", "-1", "-1", userView);
    		SetUnderlingObjectiveBo sulob=new SetUnderlingObjectiveBo(this.conn);
    		ObjectiveEvaluateBo oeb = new ObjectiveEvaluateBo(this.conn);
    		String per_visible_type = SystemConfig.getPropertyValue("per_visible_type");
    		/**目标设定（我的目标）人员*/
    		ArrayList myPlanList01_1 =null;
    		if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060702")))&&("".equals(per_visible_type)|| "approve".equalsIgnoreCase(per_visible_type)))
    			myPlanList01_1=mbo.getOrgPlanList(userView.getA0100(), "-1", "-1", "-1", "-1","pp.status=8");
    		else
    			myPlanList01_1=new ArrayList();
    		/**目标设定（我的目标）团队*/
    		ArrayList myPlanList01_2 =null;
    		if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060701")))&&("".equals(per_visible_type)|| "approve".equalsIgnoreCase(per_visible_type)))
    			myPlanList01_2=(ArrayList)welcomMap.get("list");
    		else
    			myPlanList01_2=new ArrayList();
    	    /**目标完成情况（我的目标）*/
    		ArrayList myPlanList02_01 = null;
    		if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060702")))&&("".equals(per_visible_type)|| "approve".equalsIgnoreCase(per_visible_type)))
    			myPlanList02_01=mbo.getOrgPlanList(userView.getA0100(), "-1", "-1", "-1", "-1", "po.sp_flag='03' and pp.status<>4 and pp.status<>7");
    		else
    			myPlanList02_01=new ArrayList();
    		 /**目标完成情况（团队）*/
    		ArrayList myPlanList02_02=null;
    		if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060701")))&&("".equals(per_visible_type)|| "approve".equalsIgnoreCase(per_visible_type)))
    			myPlanList02_02=(ArrayList)welcomMap.get("list2");
    		else
    			myPlanList02_02=new ArrayList();
    		/**员工目标*/
    		ArrayList employlist_01   = new ArrayList();
    		ArrayList employlist_02 = new ArrayList();
//员工KPI指标审批要调用现程序的链接，就不需要单独处理了。郭峰   		
//    		ArrayList employlist_01   = null;
//    		if((SystemConfig.getPropertyValue("clientName").equals("zglt")||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060703")))&&(per_visible_type.equals("")||per_visible_type.equalsIgnoreCase("approve")))
//    			employlist_01=sulob.getPlanList("-1", userView);
//    		else
//    			employlist_01=new ArrayList();
//    		 /**员工目标（团队）*/
//    		ArrayList employlist_02=null;
//    		if((SystemConfig.getPropertyValue("clientName").equals("zglt")||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060703")))&&(per_visible_type.equals("")||per_visible_type.equalsIgnoreCase("approve")))
//    			employlist_02=(ArrayList)welcomMap.get("list3");
//    		else
//    			employlist_02=new ArrayList();
    		
    		/**考核打分*/
    		ArrayList scoreList_01    =  new ArrayList();
    		ContentDAO dao = new ContentDAO(this.conn);
    		if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060704")))&&("".equals(per_visible_type)|| "score".equalsIgnoreCase(per_visible_type)))
			{
    	    	HashMap info=oeb.getPlanWhereSQL("-1", "-1", "-1", "-2", userView, dao,"-1");
		    	String planSQL=(String)info.get("whereSQL");
		    	String orgPlanSQL=(String)welcomMap.get("scoreplan");
		    	HashMap amap=new HashMap();
		    	if(planSQL.length()>0)
		    	{
		    		planSQL=planSQL.substring(3);
			     	StringBuffer sql = new StringBuffer();
			    	sql.append("select distinct pm.plan_id,pm.status,"+Sql_switcher.isnull("pp.a0000", "999999")+" as norder ");
				    sql.append(",pp.object_type from per_mainbody pm,per_plan pp where pm.plan_id=pp.plan_id and  ((mainbody_id='"+userView.getA0100()+"' ");
			    	sql.append(" and ("+planSQL+")) ");
				 //if(orgPlanSQL.length()>0)
					 //sql.append(" or (pp.plan_id in ("+orgPlanSQL+"))");
			    	sql.append(")");
			    	sql.append(" order by norder asc,pm.plan_id desc");
			    	rs = dao.search(sql.toString());
			    	while(rs.next())
			    	{
				    	LazyDynaBean bean = new LazyDynaBean();
			      		int plan_id=rs.getInt("plan_id");
				    	if(amap.get(plan_id+"")!=null)
				    		continue;
				    	amap.put(plan_id+"","1");
				    	RecordVo vo = new RecordVo("per_plan");
				    	vo.setInt("plan_id",plan_id);
				     	vo=dao.findByPrimaryKey(vo);
				     	bean.set("plan_id",rs.getString("plan_id"));
				    	bean.set("name",vo.getString("name"));
				    	bean.set("status",rs.getString("status")==null?"":rs.getString("status"));
				    	bean.set("object_type", rs.getString("object_type"));
				    	scoreList_01.add(bean);
			    	}
	    		}
			}
			if(rs!=null)
				rs.close();
			/**考核打分（团队）*/
	    	ArrayList scoreList_02=null;
	    	if(("zglt".equals(SystemConfig.getPropertyValue("clientName"))||!userView.isBAgent()||(userView.isBAgent()&&userView.hasTheFunction("060701")))&&("".equals(per_visible_type)|| "score".equalsIgnoreCase(per_visible_type)))
	    		scoreList_02=(ArrayList)welcomMap.get("list4");//new ArrayList();
	    	else
	    		scoreList_02=new ArrayList();
	    	//对 scoreList_01，scoreList_02进行处理，加上评分状态
	    	scoreList_01 = addGradeStaus3(scoreList_01,userView.getA0100());
	    	scoreList_02 = addGradeStaus3(scoreList_02,userView.getA0100());
	    	map.put("myPlanList01_1", myPlanList01_1);
			map.put("myPlanList01_2", myPlanList01_2);
			map.put("myPlanList02_01", myPlanList02_01);
			map.put("myPlanList02_02", myPlanList02_02);
			map.put("employlist_01", employlist_01);
			map.put("employlist_02", employlist_02);
			map.put("scoreList_01", scoreList_01);
			map.put("scoreList_02", scoreList_02);
			if(("".equals(per_visible_type)|| "approve".equalsIgnoreCase(per_visible_type)))
			{
	    		StringBuffer sql = new StringBuffer("");
		    	sql.append("select distinct per_plan.plan_id from per_mainbody,per_plan where per_mainbody.plan_id=per_plan.plan_id  and  ");
		    	sql.append(" per_mainbody.mainbody_id='"+userView.getA0100()+"'");
		    	sql.append(" and per_plan.method=2  and  (per_mainbody.status<>2 or per_mainbody.status is null) ");
		    	RowSet rowSet = dao.search(sql.toString());
		    	StringBuffer ids=new StringBuffer("");
		    	while(rowSet.next())
		    	{
			    	ids.append(rowSet.getString("plan_id")+",");
	     		}
	    		if(ids.length()>0)
			    	ids.setLength(ids.length()-1);
		    	map.put("objectids", ids.toString());
			}
			else
			{
				map.put("objectids", "");
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return map;
    }       
    
    //	将考核对象文档记录设为提交状态
	public void setArticleState2(String a0100,String nbase,String plan_id)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("update per_article set state=1 where    plan_id="+planid+" and a0100='"+a0100+"'   and lower(nbase)='"+nbase.toLowerCase()+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

   //	将考核对象文档记录设为提交状态
	public void setArticleState(String plan_id,String a0100,String nbase,int article_type)
	{
		RowSet rs= null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			sql.append("select article_id,content,affix from per_article where");
			sql.append(" Article_type="+article_type+" and plan_id="+plan_id+" and ");
			sql.append(" a0100='"+a0100+"' and fileflag=1 and ");
			sql.append(" lower(nbase)='"+nbase.toLowerCase()+"' ");
			rs= dao.search(sql.toString());
			ArrayList list = new ArrayList();
			String content="";
			int article_id=0;
			if(rs.next())
			{
				 content= Sql_switcher.readMemo(rs, "content");
				 article_id=rs.getInt("article_id");
			}else
			{
				 article_id=this.insertPerArticleRecord(plan_id,article_type, 1,nbase,a0100,0);
			}
			RecordVo vo=new RecordVo("per_article");
			vo.setInt("article_id",article_id);
			vo=dao.findByPrimaryKey(vo);
			vo.setString("content", content);
			dao.update("delete from per_article where plan_id="
					+ plan_id + " and a0100='"
					+ a0100
					+ "' and lower(nbase)='"
					+nbase.toLowerCase()
					+ "'  and article_type="+article_type+" and fileflag=1 and article_id!="+article_id);
			boolean isSub=true;
			isSub=isSub(content,plan_id,a0100,nbase,article_type);
			if(isSub)
				vo.setInt("state",1);
			else
				vo.setInt("state",0);
			vo.setString("description", "");
			dao.updateValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	public boolean isSub(String context,String plan_id,String a0100,String nbase,int article_type)
	{
		boolean flag=true;
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select count(*) from per_article where article_type="+article_type+" and  plan_id="+plan_id+" and fileflag=2 and a0100='"+a0100+ "' and lower(nbase)='"+nbase.toLowerCase()+"'");
			int n=0;
			if(rowSet.next()) {
                n=rowSet.getInt(1);
            }
			if(n==0&&(context==null||context.trim().length()==0)) {
                flag=false;
            }
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				
				e.printStackTrace();
			}
		}
		return flag;
	}
	
    
    /**
     * 提交绩效报告和目标
     * @param a0100
     * @param nbase
     * @param planid
     */
    public void subArticleRecord(String a0100,String nbase,String planid)
    {
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn);
    		dao.update("update per_article set state=1 where    plan_id="+planid+" and a0100='"+a0100+"'   and lower(nbase)='"+nbase.toLowerCase()+"'");
    		String sql="select article_type from per_article where plan_id="+planid+" and a0100='"+a0100+"'  and lower(nbase)='"+nbase.toLowerCase()+"'  and fileflag=2 "
    				+" and article_type not in ( select article_type from per_article where plan_id="+planid+" and a0100='"+a0100+"'  and lower(nbase)='"+nbase.toLowerCase()+"'  and fileflag=1  )";
    		RowSet rowSet=dao.search(sql);
    		while(rowSet.next())
    		{
    			int article_type=rowSet.getInt("article_type");
    			insertPerArticleRecord(planid,article_type, 1,nbase,a0100,1);
    			
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    
  //新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String nbase,String a0100,int state)
	{
		int article_id=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
	 
			RecordVo avo=new RecordVo("per_article");
			article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.conn);
			avo.setInt("article_id", article_id);
			avo.setInt("plan_id",Integer.parseInt(planid));
			String b0110="";String e0122="";String e01a1="";
			RowSet rowSet=dao.search("select b0110,e0122,e01a1,a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
			if(rowSet.next())
			{
				b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
				e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
				e01a1=rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
				avo.setString("a0101",rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase",nbase);
			avo.setString("a0100",a0100);
			avo.setInt("article_type", article_type);
			avo.setInt("fileflag",fileflag);
			avo.setInt("state",state);
			dao.addValueObject(avo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return article_id;
	}
   
    /**
	 * 取得考核主体是考核对象的主体类别
	 * @param object_id
	 * @param mainbody_id
	 * @param planid
	 * @return
	 */
	public HashMap getMainBodyLevel(String mainbody_id,String planid)
	{
		HashMap objectslevel=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			String sql="select pm.object_id,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql+=" pms.level_o";
            } else {
                sql+=" pms.level ";
            }
			sql+=" from per_mainbodyset pms,per_mainbody pm where pm.body_id=pms.body_id and pm.plan_id";
			sql+="="+planid+"   and mainbody_id='"+mainbody_id+"'";
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				if(rowSet.getString(2)!=null) {
                    objectslevel.put(rowSet.getString("object_id"),rowSet.getString(2));
                } else {
                    objectslevel.put(rowSet.getString("object_id"),"");
                }
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectslevel;
	}
    
	
	/**
	 * 给出已提交自我评分的考核对象
	 * @param objectList
	 * @return
	 */
	public HashMap getObjIsSubedMap(ArrayList objectList)
	{
		HashMap map=new HashMap();
		try
		{
			String obj_str="";
			for (int i = 0; i < objectList.size(); i++)
			{
			    String[] temp = (String[]) objectList.get(i);
			    obj_str+=",'"+temp[0]+"'";
			}
			if(obj_str.length()>0)
			{
				ContentDAO dao = new ContentDAO(this.conn);
				String sql="";
				if("2".equals(this.object_type))
				{
					sql="select * from per_mainbody where plan_id="+this.planid+" and status=2 and object_id=mainbody_id and object_id in ("+obj_str.substring(1)+") ";
				}
				else
				{
					String _str="";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                        _str="level_o";
                    } else {
                        _str="level";
                    }
					sql="select pmb.* from per_mainbody pmb,per_mainbodyset pms  where pmb.body_id=pms.body_id and pmb.status=2 and pms."+_str+"=5 and pmb.plan_id="+this.planid+"   and pmb.object_id in ("+obj_str.substring(1)+") ";
				}
				RowSet rowSet=dao.search(sql);
				while(rowSet.next()) {
                    map.put(rowSet.getString("object_id"),"1");
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

    // 处理多人打分 自我评分选项
    public void dealwithSelfScore(ArrayList objectList, String userID)
    {
    	
    	HashMap mainbodyLevelMap=getMainBodyLevel(userID,this.planid); 
    	
    	HashMap objIsSubedMap=getObjIsSubedMap(objectList);
    	
		if (!"0".equalsIgnoreCase(selfScoreInDirectLeader) && !"False".equalsIgnoreCase(selfScoreInDirectLeader))
		{
		    DirectUpperPosBo directUpperPosBo = new DirectUpperPosBo(this.conn);
		    if ("3".equals(selfScoreInDirectLeader))
		    {
		    	isSelfScoreColumn = true;
				for (int i = 0; i < objectList.size(); i++)
				{
				    String[] temp = (String[]) objectList.get(i);
				    String level=(String)mainbodyLevelMap.get(temp[0]);
				    if("5".equals(level)) {
                        this.objectSelfScoreMap.put(temp[0], "0");
                    } else if(objIsSubedMap.get(temp[0])!=null) {
                        this.objectSelfScoreMap.put(temp[0], "1");
                    }
				}
		    } 
		    else
		    {
				boolean tempBoolean = false;
				for (int i = 0; i < objectList.size(); i++)
				{
				    String[] temp = (String[]) objectList.get(i);
				    String level=(String)mainbodyLevelMap.get(temp[0]);
				    if ("True".equalsIgnoreCase(selfScoreInDirectLeader) || "1".equals(selfScoreInDirectLeader))
				    {
				    	if("1".equals(level)&&objIsSubedMap.get(temp[0])!=null)
				    	{
				    		tempBoolean=true;
				    		this.objectSelfScoreMap.put(temp[0], "1");
				    	}
				    	else {
                            this.objectSelfScoreMap.put(temp[0], "0");
                        }
				    }
				    if ("2".equals(selfScoreInDirectLeader))
				    {
				    	if(objIsSubedMap.get(temp[0])!=null&&("1".equals(level)|| "0".equals(level)|| "-2".equals(level)|| "-1".equals(level)))
				    	{
				    		tempBoolean=true;
				    		this.objectSelfScoreMap.put(temp[0], "1");
				    	}
				    	else {
                            this.objectSelfScoreMap.put(temp[0], "0");
                        }
				    }
				}
				if (tempBoolean)
				{
				    isSelfScoreColumn = true;
				}
	
		    }
	
		}

    }

    // 处理多人打分 下属评分选项
    public void dealwithLowerScore(String plan_id,String mainbody_id,ArrayList objectList)
    {    	    	
    	this.objectsLowerMap = getLowerGradeListMap(plan_id,mainbody_id,objectList);    	
    }
    
    /**
	 * 取得下级考核主体的信息列表
	 * @param property // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
	 * @return
	 */
	public HashMap getLowerGradeListMap(String plan_id,String mainbody_id,ArrayList objectList)
	{
		HashMap objectsLowerMap = new HashMap();
		RowSet rowSet = null;
		try
		{
			for (int i = 0; i < objectList.size(); i++)
			{
			    String[] temp = (String[]) objectList.get(i);
			    			    			
				ArrayList list = new ArrayList();
				int property = 10;
				ContentDAO dao = new ContentDAO(this.conn);
				String _str="level";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    _str="level_o";
                }
				String _sql="select per_mainbodyset."+_str+" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id"
						   +" and plan_id="+plan_id+" and object_id='"+temp[0]+"' and mainbody_id='"+mainbody_id+"'";
				rowSet = dao.search(_sql);
				if(rowSet.next())
				{
					if(rowSet.getString(1)!=null) {
                        property=rowSet.getInt(1);
                    }
				}				
				if(property==5) {
                    continue;
                }
				
				String level_str = "";
				switch (property)
				{
					case 1:
					//	level_str="5,2";
						level_str="2";
						break;
					case 0:
						level_str="1,2";
						break;
					case -1:
						level_str="1,0,2";
						break;
					case -2:
						level_str="1,0,-1,2";
						break;
				}				
				if(level_str.length()<=0) {
                    continue;
                }
				
				StringBuffer sql = new StringBuffer("");
				sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
				sql.append(" and pm.plan_id="+plan_id+" and pm.object_id='"+temp[0]+"' and ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql.append(" pms.level_o");
                } else {
                    sql.append(" pms.level ");
                }
				sql.append(" in ("+level_str+")");
				String cloumn="level";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    cloumn="level_o";
                }
				sql.append(" order by "+cloumn+" desc ");
				rowSet=dao.search(sql.toString());
				LazyDynaBean abean = null;
				while(rowSet.next())
				{
					abean = new LazyDynaBean();
					abean.set("a0100", rowSet.getString("mainbody_id"));
					abean.set("a0101", rowSet.getString("a0101"));
					abean.set("bodyname", rowSet.getString("name"));
					abean.set("status", rowSet.getString("status"));
					
					String _status=rowSet.getString("status");
		 			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
                    {
                        continue;
                    }
					list.add(abean);
				}
				
				objectsLowerMap.put(temp[0],list);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectsLowerMap;
	}        
    
    public BatchGradeBo()
    {

    }
       
    /**
     * 取得考核计划说明内容
     * @param plan_id
     * @return
     */
    public String getDescript(String plan_id)
	{
		String descript="";
		try
		{
			String sql = " select descript from per_plan where plan_id="+plan_id;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				descript=Sql_switcher.readMemo(rs, "descript");
			}
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return descript;
	}
    
    
    /*
     * 设了“动态主体权重控制到指标”且为必打分的，有权限的指标必须评分。
     */
    public boolean isMustScoreByPriv(ArrayList pointList,String[] userid, String mainbody_id, String plan_id, HashMap usersValueMap) throws GeneralException
    {
    	boolean flag=true;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer objects = new StringBuffer("");
    		    //this.DynaBodyToPoint动态主体权重控制到指标/任务True, False, 默认为 False
    	    for (int i = 0; i < userid.length; i++) {
                objects.append(",'" + userid[i] + "'");
            }
    	    HashMap userPointMap=new HashMap();
    		RowSet rowSet=dao.search("select * from per_pointpriv_"+plan_id+" where object_id in ("+objects.substring(1)+") and mainbody_id='"+mainbody_id+"'");
    		while(rowSet.next())
    		{
    			LazyDynaBean abean=new LazyDynaBean();
    			String object_id=rowSet.getString("object_id");
    			for (int t = 0; t < pointList.size(); t++)
 				{
 						String[] temp = (String[]) pointList.get(t);
 						if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7])) // 如果为定量指标，并且统一打分，则不保存
                        {
                            continue;
                        }
    				
 						
 						if(rowSet.getString("C_"+temp[0])!=null) {
                            abean.set(temp[0].toLowerCase(),rowSet.getString("C_"+temp[0]));
                        } else {
                            abean.set(temp[0].toLowerCase(),"0");
                        }
 				}
    			userPointMap.put(object_id,abean);
    		}
    		
    		 ArrayList fillObjectList=new ArrayList();
    		 String sql = "select per_mainbody.object_id,per_object.a0101 from per_mainbody,per_object where  per_object.object_id=per_mainbody.object_id and per_mainbody.object_id in ("
    			    + objects.substring(1) + ") and per_mainbody.fillctrl=1 and per_mainbody.mainbody_id='" + mainbody_id + "'  and per_object.plan_id=" + plan_id + " and per_mainbody.plan_id="
    			    + plan_id;
    		 RowSet frowset = dao.search(sql);
    		 while (frowset.next())
    		 {
    				String object_id = frowset.getString(1);
    				fillObjectList.add(object_id+"/"+frowset.getString(2));
    		 }
    		 LazyDynaBean pointPriBean=null;
    		 for(int i=0;i<fillObjectList.size();i++)
    		 {
    			 String[] str=((String)fillObjectList.get(i)).split("/");
    			 String object_id=str[0];
    			 String object_name=str[1];
    			 
    			 pointPriBean=(LazyDynaBean)userPointMap.get(object_id);
    			 if (usersValueMap.get(object_id) != null&&pointPriBean!=null)
    			 {
    				 String value = (String) usersValueMap.get(object_id);
    				 String[] values = value.split("/");
    				    
    				 for (int t = 0; t < pointList.size(); t++)
    				 {
    					String[] temp = (String[]) pointList.get(t);
    					if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7])) // 如果为定量指标，并且统一打分，则不保存
                        {
                            continue;
                        }
    						
    					if(pointPriBean.get(temp[0].toLowerCase())!=null&& "1".equals((String)pointPriBean.get(temp[0].toLowerCase())))
    					{
    						if ("null".equals(values[t]))
    						{
    							throw GeneralExceptionHandler.Handle(new GeneralException(object_name+":"+ " " + temp[1] + " "+ResourceFactory.getProperty("performance.batchgrade.info8")+ "!"));
    						}   									
    					}
    				 }
    			 }
    		 }    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	
    	return flag;
    }
       
    /**
         * 判断提交时是否有必打分对象
         * 
         * @param userid
         * @param mainbody_id
         * @param plan_id
         * @param usersValueMap
         * @return
         */
    public String isMustScore(String[] userid, String mainbody_id, String plan_id, HashMap usersValueMap)
    {

		StringBuffer info = new StringBuffer("");
		try
		{
		    StringBuffer objects = new StringBuffer("");
		    //this.DynaBodyToPoint动态主体权重控制到指标/任务True, False, 默认为 False
		    for (int i = 0; i < userid.length; i++) {
                objects.append(",'" + userid[i] + "'");
            }
		    String sql = "select per_mainbody.object_id,per_object.a0101 from per_mainbody,per_object where  per_object.object_id=per_mainbody.object_id and per_mainbody.object_id in ("
			    + objects.substring(1) + ") and per_mainbody.fillctrl=1 and per_mainbody.mainbody_id='" + mainbody_id + "'  and per_object.plan_id=" + plan_id + " and per_mainbody.plan_id="
			    + plan_id;
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet frowset = dao.search(sql);
		    while (frowset.next())
		    {
				String object_id = frowset.getString(1);
				String objectName = frowset.getString(2);
				if (usersValueMap.get(object_id) != null)
				{
				    String value = (String) usersValueMap.get(object_id);
				    String[] values = value.split("/");
				    int length = values.length;
				    int n = 0;
				    if ("true".equalsIgnoreCase(this.NodeKnowDegree)) {
                        n++;
                    }
				    if ("true".equalsIgnoreCase(this.WholeEval)) {
                        n++;
                    }
				    length = length - n;
				    boolean isValue = false;
				    for (int i = 0; i < length; i++)
				    {
						if (!"null".equalsIgnoreCase(values[i]))
						{
						    isValue = true;
						    break;
						}
				    }
				    if (!isValue)
				    {
				    	info.append("\r\n " + objectName +ResourceFactory.getProperty("performance.batchgrade.info8")+"!");
				    }		
				}
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return info.toString();
    }

    /**
         * 判断考核对象是否填写了个人总结
         * 
         * @param objectIDs
         * @param plan_id
         * @return
         */
    public String isWriteSummary(String objectIDs, String plan_id)
    {

	StringBuffer info = new StringBuffer("");
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	try
	{
		String ids=objectIDs;
		HashMap map=new HashMap();
	    
	    if("1".equals(this.object_type)|| "3".equals(this.object_type)|| "4".equals(this.object_type))
	    {
	    		frowset=dao.search("select mainbody_id,object_id from per_mainbody where plan_id="+plan_id+" and body_id=-1 and object_id in ("+objectIDs+") ");
	    		String str="";
	    		while(frowset.next())
	    		{
	    			str+=",'"+frowset.getString(1)+"'";
	    			map.put(frowset.getString(1),frowset.getString(2));
	    		}
	    		
	    		if(str.length()>0) {
                    ids=str.substring(1);
                }
	    }
	    
			HashMap menMap = new HashMap();
			frowset = dao.search("select * from per_article where plan_id=" + plan_id + " and Article_type=2 and (state=1 or  state=2) and a0100 in (" + ids + ") and lower(nbase)='usr'  ");
			while (frowset.next())
			{
				String a0100=frowset.getString("a0100");
				if("1".equals(this.object_type)|| "3".equals(this.object_type)|| "4".equals(this.object_type))
				{
					if(map.get(a0100)!=null) {
                        a0100=(String)map.get(a0100);
                    }
				}
			    if (frowset.getInt("fileflag") == 1)
			    {
			    	menMap.put(a0100, "1");
			    } else {
                    menMap.put(a0100, "1");
                }
			}
	
			frowset = dao.search("select * from per_object where plan_id=" + plan_id + " and object_id in (" + objectIDs + ")");
			int count = 0;
			StringBuffer sb = new StringBuffer();
			while (frowset.next())
			{
			    String a0101 = frowset.getString("a0101");
			    String a0100 = frowset.getString("object_id");
			    if (menMap.get(a0100) == null)
			    {
				if(count%4==3) {
                    sb.append(a0101+"<br>");
                } else {
                    sb.append(a0101+",");
                }
				/*if (this.performanceType.equals("0"))
				{
				    String ainfo = SystemConfig.getPropertyValue("per_examineInfo");
				    if (ainfo == null || ainfo.length() == 0)
					info.append(ResourceFactory.getProperty("performance.batchgrade.noPersonalSummary") + "！");
				    else
					info.append(ResourceFactory.getProperty("performance.batchgrade.noFill") + new String(ainfo.getBytes("ISO-8859-1"), "GBK") + "！");
				} else
				    info.append(ResourceFactory.getProperty("performance.batchgrade.noFillReport") + "！");*/
			    }
			    count++;
			}
			if(sb!=null && sb.length()>0) {  //2013.11.28 pjf
			    String ObjectType = getObjectType(plan_id);
				info.append(ResourceFactory.getProperty("performance.batchgrade.personNotSub1"));
				info.append(ObjectType);
				info.append(ResourceFactory.getProperty("performance.batchgrade.personNotSub2"));
				info.append("<br>");
				info.append(sb);
				if(count%4==1) {
                    info = new StringBuffer(info.substring(0, info.length()-1));
                }
			}

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	/*if (info.length() > 1)
	{
	    info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.notSub"));
	}*/

	return info.toString();
    }

    public void setObjectType(String planID)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	try
	{
	    String objectType = "2"; // 1:部门 2：人员
	    frowset = dao.search("select * from per_plan where plan_id=" + planID);
	    if (frowset.next())
	    {
		objectType = frowset.getString("object_type");
	    }
	    this.object_type = objectType;

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public HashMap getUserNumberPointResultMap(ArrayList pointList, String planID)
    {
    	DecimalFormat myformat1 = new DecimalFormat("########.####");//
	HashMap map = new HashMap();
	StringBuffer sqll = new StringBuffer("");
	StringBuffer columns = new StringBuffer("");
	for (int i = 0; i < pointList.size(); i++)
	{
	    String[] temp = (String[]) pointList.get(i);
	    if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7]) && "true".equalsIgnoreCase(this.showOneMark))
	    {
		sqll.append(",C_" + temp[0]);
		columns.append(",C_" + temp[0]);
	    }
	}
	sqll.append(" from per_result_" + planID);
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	try
	{
	    if (columns.length() > 0)
	    {
		String[] columnArr = columns.substring(1).split(",");
		frowset = dao.search(" select object_id" + sqll.toString());
		while (frowset.next())
		{
		    HashMap userMap = new HashMap();
		    for (int i = 0; i < columnArr.length; i++)
		    {
			String aa = columnArr[i].substring(2);
			String str=frowset.getString(columnArr[i]) != null ? frowset.getString(columnArr[i]) : "";
			if(str.length()>0)
			{
				str=myformat1.format(Double.parseDouble(str));
			}
			userMap.put(aa,str);
			
			

		    }
		    map.put(frowset.getString("object_id"), userMap);
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	return map;
    }

    /**
         * 给出其他没提交的考核计划列表
         * 
         * @param dbList
         * @param mainbodyID
         * @return
         */
    public String getOtherGradeStatus(ArrayList dbList, String mainbodyID)
    {

	StringBuffer otherInfo = new StringBuffer("");
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	String id = "";
	int no = 0;
	try
	{
	    for (int i = 0; i < dbList.size(); i++)
	    {
			CommonData vo = (CommonData) dbList.get(i);
			if ("0".equals(vo.getDataValue()))
			{
			    continue;
			}
			
			RecordVo _vo = new RecordVo("per_plan");
			_vo.setInt("plan_id", Integer.parseInt(vo.getDataValue()));
			_vo = dao.findByPrimaryKey(_vo);
			LoadXml loadxml=null;
			if(BatchGradeBo.planLoadXmlMap.get(vo.getDataValue())==null)
			{
				loadxml = new LoadXml(this.conn,vo.getDataValue());
				BatchGradeBo.planLoadXmlMap.put(vo.getDataValue(),loadxml);
			}
			else {
                loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(vo.getDataValue());
            }
			Hashtable htxml = new Hashtable();
			htxml = loadxml.getDegreeWhole();
			String mitiScoreMergeSelfEval=(String)htxml.get("mitiScoreMergeSelfEval"); 
			
			String _str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                _str="pms.level_o";
            } else {
                _str="pms.level ";
            }
			String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and  pm.plan_id=" + vo.getDataValue() + " and pm.mainbody_id='" + mainbodyID + "' "; // and pm.status<>4   ";
			if (!"True".equalsIgnoreCase(mitiScoreMergeSelfEval))
			{
				if (_vo.getInt("object_type")==2) // 考核人员
                {
                    sql += " and pm.object_id<>'" +mainbodyID+ "'";
                } else {
                    sql += " and ( "+_str+" is null or "+_str+"<>5 ) ";
                }
			} 
			 
			
			frowset = dao.search(sql);
			boolean isNoMark = false;
			boolean isMarking = false;
			boolean isMarked = false;
			int n = 0;
			while (frowset.next())
			{
	
			    int a_status = frowset.getInt("status");
			    if (a_status == 0) {
                    isNoMark = true;
                } else if (a_status == 1||a_status == 4) {
                    isMarking = true;
                } else if (a_status == 2 || a_status == 7) {
                    isMarked = true;
                }
			    n++;
			}
			if (n > 0)
			{
			    if (!isNoMark && !isMarking && isMarked)
			    {
	
			    } else
			    {
					if (no == 0) {
                        id = vo.getDataValue();
                    }
					otherInfo.append("#  " + ResourceFactory.getProperty("kh.field.plan") + "：" + vo.getDataName() + "  " + ResourceFactory.getProperty("performance.batchgrade.notSubed") + "!");
					no++;
					
					if(vo.getDataValue().equals(this.planid)) {
                        return "";
                    }
			    }
			}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	if (otherInfo.length() > 4) {
        return id + "~" + otherInfo.toString();
    }
	return otherInfo.toString();
    }

    /**
         * 给考核计划添加 填报状态
         * 
         * @param dbList
         * @param mainbodyID
         * @param model=
         *                1:自我评价 2:单人打分 3:多人打分
         * @return
         */
    public ArrayList addGradeStaus(ArrayList dbList, String mainbodyID, int model, int opt)
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	try
	{
	    LoadXml aloadxml = null;
	    for (int i = 0; i < dbList.size(); i++)
		{
			CommonData vo = (CommonData) dbList.get(i);
			if ("0".equals(vo.getDataValue()))
			{
			    list.add(vo);
			    continue;
	
			}
			
			if(planLoadXmlMap.get(vo.getDataValue())==null)
			{
						aloadxml = new LoadXml(this.conn, vo.getDataValue());
						planLoadXmlMap.put(vo.getDataValue(),aloadxml);
			}
			else {
                aloadxml=(LoadXml)planLoadXmlMap.get(vo.getDataValue());
            }
			//loadxml = new LoadXml(this.conn, vo.getDataValue());
			Hashtable htxml = new Hashtable();
			htxml = aloadxml.getDegreeWhole();
			String a_isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan"); // //提交后的计划是否需要显示True|False
			String a_mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");
			String _str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                _str="level_o";
            } else {
                _str="level";
            }
			String sql = "select pmb.status,pp.object_type,pms."+_str+" from per_mainbody pmb,per_mainbodyset pms,per_plan pp where pmb.body_id=pms.body_id and pmb.plan_id=pp.plan_id and pmb.plan_id=" + vo.getDataValue() + "  and pmb.mainbody_id='" + mainbodyID + "' "; // and pmb.status<>4  20141204 dengcan
			if (opt == 1)
			{
			 //   if ((a_mitiScoreMergeSelfEval.equalsIgnoreCase("False") && model == 3) || model == 2)
				if ("False".equalsIgnoreCase(a_mitiScoreMergeSelfEval) &&( model == 3 || model == 2)) {
                    sql += " and pmb.object_id<>'" + mainbodyID + "'";
                }
			}
			frowset = dao.search(sql);
	
			boolean isNoMark = false;
			boolean isMarking = false;
			boolean isMarked = false;
			int n = 0;
			while (frowset.next())
			{
			    int object_type=frowset.getInt("object_type");
			    String level=frowset.getString(_str)!=null?frowset.getString(_str):"";
				if(( model == 3 || model == 2)&&object_type!=2&& "5".equals(level.trim())&& "false".equalsIgnoreCase(a_mitiScoreMergeSelfEval)) {
                    continue;
                }
				
			    int a_status = frowset.getInt("status");
//			    if(a_status!=0) //郭峰添加 用于测试
//			    	continue;
			    if (a_status == 0) {
                    isNoMark = true;
                } else if (a_status == 1||a_status == 4) //20141204 dengcan 考虑不打分没提交状态
                {
                    isMarking = true;
                } else if (a_status == 2 || a_status == 7) {
                    isMarked = true;
                }
			    
			    n++;
			}
	
			
	         if(n==0&&model!=1) {
                 continue;
             }
	               
			if (isNoMark && !isMarking && !isMarked) {
                vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.unEvaluate") + ")");
            } else if (!isNoMark && !isMarking && isMarked)
			{
	
			    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan)) {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluated") + ")");
                } else {
                    continue;
                }
			} else {
                vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluating") + ")");
            }
	
			list.add(vo);
		}
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    /**
         * 给考核计划添加 填报状态
         * 
         * @param dbList
         * @param mainbodyID
         * @param model=
         *                1:自我评价 2:单人打分 3:多人打分
         * @return
         */
    public ArrayList addGradeStaus(ArrayList dbList, String mainbodyID, int model)
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet frowset = null;
	try
	{
	    LoadXml aloadxml = null;
	    for (int i = 0; i < dbList.size(); i++)
		{
			CommonData vo = (CommonData) dbList.get(i);
			if ("0".equals(vo.getDataValue()))
			{
			    list.add(vo);
			    continue;
	
			}
			if(planLoadXmlMap.get(vo.getDataValue())==null)
			{
				aloadxml = new LoadXml(this.conn,vo.getDataValue());
				planLoadXmlMap.put(vo.getDataValue(),aloadxml);
			}
			else {
                aloadxml=(LoadXml)planLoadXmlMap.get(vo.getDataValue());
            }
			
		//	aloadxml = new LoadXml(this.conn, vo.getDataValue());
			
			
			  RecordVo _vo = new RecordVo("per_plan");
			  _vo.setInt("plan_id", Integer.parseInt(vo.getDataValue().trim()));
			  _vo = dao.findByPrimaryKey(_vo);
	    	  String _str="";
	    	  if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                  _str="pms.level_o";
              } else {
                  _str="pms.level ";
              }
			
			
			Hashtable htxml = new Hashtable();
			htxml = aloadxml.getDegreeWhole();
			String a_isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan"); // //提交后的计划是否需要显示True|False
			String a_mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");
	
			String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and  pm.plan_id=" + vo.getDataValue() + " and pm.mainbody_id='" + mainbodyID + "' "; // and pm.status<>4   ";
			if (("False".equalsIgnoreCase(a_mitiScoreMergeSelfEval) && model == 3) || model == 2)
			{
			//	sql += " and pm.object_id<>'" + mainbodyID + "'";
				if (_vo.getInt("object_type")==2) // 考核人员
                {
                    sql += " and pm.object_id<>'" +mainbodyID+ "'";
                } else {
                    sql += " and ( "+_str+" is null or "+_str+"<>5 ) ";
                }
			}else if(model == 1) {
				sql += " and "+_str+"=5";
			}
			frowset = dao.search(sql);
	
			boolean isNoMark = false;
			boolean isMarking = false;
			boolean isMarked = false;
			boolean isFinished=false;
			int n = 0;
			while (frowset.next())
			{
			    n++;
			    int a_status = frowset.getInt("status");
			    if (a_status == 0) {
                    isNoMark = true;
                } else if (a_status == 1|| a_status == 4) {
                    isMarking = true;
                } else if (a_status == 2 || a_status == 7) {
                    isMarked = true;
                } else if (a_status == 8) //已完成，针对多人打分。
                {
                    isFinished=true;
                }
			} 
			
			if(n==0&&model==3) {
                continue;
            }
			
			/*
	                 * if(n==0&&model!=1) continue;
	                 */
			if (isNoMark && !isMarking && !isMarked&& !isFinished)
			{   
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                {
                    vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.unGrade")+")");
                } else {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.unEvaluate") + ")");
                }
			
			}
 			else if (!isNoMark && !isMarking && isMarked&&!isFinished)
			{
	
			    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan)) {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluated") + ")");
                } else {
                    continue;
                }
			}
			else if (!isNoMark && !isMarking && isFinished)
			{
	
			    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan))
			    {
			    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                    {
                        vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.graded")+")");
                    } else {
                        vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.finished")+")");
                    }
			    
			    }
			    else {
                    continue;
                }
			}
			
			else
			{
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                {
                    vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.grading")+")");
                } else {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluating") + ")");
                }
			}
	
			list.add(vo);
		 }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }
    public ArrayList addGradeStaus2(ArrayList dbList, String mainbodyID, int model,Connection conn)
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(conn);
	RowSet frowset = null;
	try
	{
	    LoadXml aloadxml = null;
	    for (int i = 0; i < dbList.size(); i++)
		{
			CommonData vo = (CommonData) dbList.get(i);
			if ("0".equals(vo.getDataValue()))
			{
			    list.add(vo);
			    continue;
	
			}
			
			aloadxml = new LoadXml(this.conn,vo.getDataValue());
			 frowset=dao.search("select object_type from per_plan where plan_id="+vo.getDataValue());
			 int object_type=2;
			 while(frowset.next()) {
                 object_type=frowset.getInt(1);
             }
	    	  String _str="";
	    	  if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                  _str="pms.level_o";
              } else {
                  _str="pms.level ";
              }
			
			
			Hashtable htxml = new Hashtable();
			htxml = aloadxml.getDegreeWhole();
			String a_isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan"); // //提交后的计划是否需要显示True|False
			String a_mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");
	
			String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and  pm.plan_id=" + vo.getDataValue() + " and pm.status<>4   and pm.mainbody_id='" + mainbodyID + "' ";
			if (("False".equalsIgnoreCase(a_mitiScoreMergeSelfEval) && model == 3) || model == 2)
			{
			//	sql += " and pm.object_id<>'" + mainbodyID + "'";
				if (object_type==2) // 考核人员
                {
                    sql += " and pm.object_id<>'" +mainbodyID+ "'";
                } else {
                    sql += " and ( "+_str+" is null or "+_str+"<>5 ) ";
                }
			}
			frowset = dao.search(sql);
	
			boolean isNoMark = false;
			boolean isMarking = false;
			boolean isMarked = false;
			boolean isFinished=false;
			int n = 0;
			while (frowset.next())
			{
			    n++;
			    int a_status = frowset.getInt("status");
			    if (a_status == 0) {
                    isNoMark = true;
                } else if (a_status == 1) {
                    isMarking = true;
                } else if (a_status == 2 || a_status == 7) {
                    isMarked = true;
                } else if (a_status == 8) //已完成，针对多人打分。
                {
                    isFinished=true;
                }
			}
			
			if(n==0&&model==3) {
                continue;
            }
			
			/*
	                 * if(n==0&&model!=1) continue;
	                 */
			if (isNoMark && !isMarking && !isMarked&& !isFinished)
			{   
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                {
                    vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.unGrade")+")");
                } else {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.unEvaluate") + ")");
                }
			
			}
			else if (!isNoMark && !isMarking && isMarked&&!isFinished)
			{
	
			    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan)) {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluated") + ")");
                } else {
                    continue;
                }
			}
			else if (!isNoMark && !isMarking && isFinished)
			{
	
			    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan))
			    {
			    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                    {
                        vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.graded")+")");
                    } else {
                        vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.finished")+")");
                    }
			    
			    }
			    else {
                    continue;
                }
			}
			
			else
			{
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                {
                    vo.setDataName(vo.getDataName() + " ("+ResourceFactory.getProperty("performance.batchgrade.grading")+")");
                } else {
                    vo.setDataName(vo.getDataName() + " (" + ResourceFactory.getProperty("performance.batchgrade.evaluating") + ")");
                }
			}
	
			list.add(vo);
		 }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }
    
    /*
     * 让 scoreList_01，scoreList_02加上打分状态
     * */
	public ArrayList addGradeStaus3(ArrayList dbList, String mainbodyID)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset = null;
		try
		{
		    LoadXml aloadxml = null;
		    for (int i = 0; i < dbList.size(); i++)
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean = (LazyDynaBean)dbList.get(i);
				String tempPlanId = (String)bean.get("plan_id");
				String tempPlanName = (String)bean.get("name");
				if ("0".equals(tempPlanId))
				{
				    bean.set("status", "");
				    continue;
				}
				if(planLoadXmlMap.get(tempPlanId)==null)
				{
					aloadxml = new LoadXml(this.conn,tempPlanId);
					planLoadXmlMap.put(tempPlanId,aloadxml);
				}
				else {
                    aloadxml=(LoadXml)planLoadXmlMap.get(tempPlanId);
                }
				
			//	aloadxml = new LoadXml(this.conn, vo.getDataValue());
				
				
				  RecordVo _vo = new RecordVo("per_plan");
				  _vo.setInt("plan_id", Integer.parseInt(tempPlanId.trim()));
				  _vo = dao.findByPrimaryKey(_vo);
		    	  String _str="";
		    	  if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                      _str="pms.level_o";
                  } else {
                      _str="pms.level ";
                  }
				
				
				Hashtable htxml = new Hashtable();
				htxml = aloadxml.getDegreeWhole();
				String a_isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan"); // //提交后的计划是否需要显示True|False
				String a_mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");
		
				String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and  pm.plan_id=" + tempPlanId + " and pm.mainbody_id='" + mainbodyID + "' "; // and pm.status<>4   ";
				frowset = dao.search(sql);
		
				boolean isNoMark = false;
				boolean isMarking = false;
				boolean isMarked = false;
				boolean isFinished=false;
				int n = 0;
				while (frowset.next())
				{
				    n++;
				    int a_status = frowset.getInt("status");
				    if (a_status == 0) {
                        isNoMark = true;
                    } else if (a_status == 1|| a_status == 4) {
                        isMarking = true;
                    } else if (a_status == 2 || a_status == 7) {
                        isMarked = true;
                    } else if (a_status == 8) //已完成，针对多人打分。
                    {
                        isFinished=true;
                    }
				} 
				
				if(n==0){
					continue;
				}
					
				
				/*
		                 * if(n==0&&model!=1) continue;
		                 */
				if (isNoMark && !isMarking && !isMarked&& !isFinished)
				{   
					if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                    {
                        bean.set("status",ResourceFactory.getProperty("performance.batchgrade.unGrade"));
                    } else {
                        bean.set("status",ResourceFactory.getProperty("performance.batchgrade.unEvaluate"));
                    }
				
				}
				else if (!isNoMark && !isMarking && isMarked&&!isFinished)
				{
		
				    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan)) {
                        bean.set("status",ResourceFactory.getProperty("performance.batchgrade.evaluated"));
                    } else{
				    	continue;
				    }
					
				}
				else if (!isNoMark && !isMarking && isFinished)
				{
		
				    if ("true".equalsIgnoreCase(a_isShowSubmittedPlan))
				    {
				    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                        {
                            bean.set("status",ResourceFactory.getProperty("performance.batchgrade.graded"));
                        } else {
                            bean.set("status",ResourceFactory.getProperty("performance.batchgrade.finished"));
                        }
				    
				    }
				    else{
				    	continue;
				    }
				    	
				}
				
				else
				{
					if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
                    {
                        bean.set("status",ResourceFactory.getProperty("performance.batchgrade.grading"));
                    } else {
                        bean.set("status",ResourceFactory.getProperty("performance.batchgrade.evaluating"));
                    }
				}
				
				list.add(bean);
			 }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
    
    public String getTh(String name, int lays)
    {

	StringBuffer sb = new StringBuffer("");
	sb.append("<td id='a' class='cell_locked2'  valign='middle' align='center'  rowspan='");
	sb.append(lays);
	sb.append("'  width='60' nowrap > ");
	sb.append(name);
	sb.append("</td>");
	return sb.toString();

    }

    public boolean isHaveReasonsCloumn(String plan_id)
    {

	boolean flag = false;
	try
	{
	    String sql = "select * from per_table_" + plan_id;
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search(sql);
	    ResultSetMetaData rsmd = rowSet.getMetaData();
	   
	    LoadXml aloadxml=null;
	    if(planLoadXmlMap.get(plan_id)==null)
	    {
	    	aloadxml = new LoadXml(this.conn,plan_id);
	    	planLoadXmlMap.put(plan_id,aloadxml);
	    }
	    else {
            aloadxml=(LoadXml)planLoadXmlMap.get(plan_id);
        }
	    
	//    if(loadxml!=null)
	//    	loadxml= new LoadXml(this.conn, plan_id);
		
	    
	    Hashtable params = aloadxml.getDegreeWhole();
		// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
		String  showDeductionCause=(String)params.get("showDeductionCause"); 
	    for (int i = 1; i <= rsmd.getColumnCount(); i++)
	    {
		String cloumnName = rsmd.getColumnName(i);
		if ("reasons".equalsIgnoreCase(cloumnName)&& "true".equalsIgnoreCase(showDeductionCause))
		{
		    flag = true;
		    break;
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return flag;
    }

    /**
         * 得到表头的html
         * 
         * @param template_id
         * @param plan_id
         * @param flag
         *                1:总界面 2:对象界面 3:主体界面
         * @return
         */
    public String getTableHeaderHtml(String template_id, String plan_id, String flag, String modelType)
    {

	StringBuffer tableHeaderHtml = new StringBuffer("");
	StringBuffer tableHtml = new StringBuffer("");
	try
	{
	    ArrayList list = getPerformanceStencilList2(template_id);
	    ArrayList pointList = getPerPointList(template_id, plan_id);

	    ArrayList items = (ArrayList) list.get(0); // 模版项目列表
	    HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
	    int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
	    HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数
	    ArrayList bottomItemList = (ArrayList) list.get(4); // 模版最底层的项目
	    ArrayList tempColumnList = new ArrayList();

	    /* 画第一层表头 */
	    int a_cols = 1;
	    StringBuffer a_tableHtml = new StringBuffer("");
	    a_tableHtml.append("<tr> ");

	    if ("1".equals(flag))
	    {
		if (!"UN".equalsIgnoreCase(modelType))
		{
		    a_tableHtml.append(getTh(ResourceFactory.getProperty("columns.archive.unit"), lays));
		    a_tableHtml.append(getTh(ResourceFactory.getProperty("columns.archive.um"), lays));
		}
		a_tableHtml.append(getTh(ResourceFactory.getProperty("jx.datacol.khobj"), lays));
		a_tableHtml.append(getTh(ResourceFactory.getProperty("lable.performance.perMainBody"), lays));
		boolean isDeductMark = this.isHaveReasonsCloumn(plan_id);
		if (isDeductMark) {
            a_tableHtml.append(getTh(ResourceFactory.getProperty("lable.performance.DeductMark"), lays));
        }

	    } else if ("2".equals(flag))
	    {
		a_tableHtml.append(getTh(ResourceFactory.getProperty("lable.performance.perMainBody"), lays));
	    } else if ("3".equals(flag))
	    {
		a_tableHtml.append(getTh(ResourceFactory.getProperty("jx.datacol.khobj"), lays));
	    }

	    for (Iterator t = items.iterator(); t.hasNext();)
	    {
		String[] temp = (String[]) t.next();
		if (temp[1] == null)
		{
		    a_tableHtml.append("<td valign='middle' align='center' class='header_locked'  colspan='");
		    a_tableHtml.append((String) map.get(temp[0]));
		    a_tableHtml.append("'");
		    a_tableHtml.append(" height='50'   > ");

		    a_tableHtml.append(temp[3]);
		    a_tableHtml.append("</td>");
		    tempColumnList.add(temp);
		    a_cols += Integer.parseInt((String) map.get(temp[0]));
		}
	    }
	    a_tableHtml.append("</tr> \n ");

	    tableHtml.append(a_tableHtml.toString());
	    ArrayList perPointList = (ArrayList) pointList.get(1);
	    SingleGradeBo singleGradeBo = new SingleGradeBo(this.conn);
	    HashMap pointItemMap = singleGradeBo.getPointItemList((ArrayList) pointList.get(1), items);
	    // 画表头的中间层
	    tableHtml.append(getMidHeadHtml(lays, tempColumnList, items, map, perPointList, pointItemMap));

	    tableHtml.append("<tr>");

	    for (Iterator t = perPointList.iterator(); t.hasNext();)
	    {
		String[] temp = (String[]) t.next();
		tableHtml.append("<td valign='top' align='center' id='" + temp[0] + "' nowrap");
		tableHtml.append(" class='header_locked' width='" + columnWidth + "' height='150'   >");
		tableHtml.append(temp[1]);
		tableHtml.append("</td>");
	    }
	    tableHtml.append("</tr> \n");

	    tableHeaderHtml.append("<thead>" + tableHtml.toString() + "<thead>");
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return tableHeaderHtml.toString();
    }
    
    /**
     * 考核计划内 判断主体是否为所有考核对象的直接上级 true 是 false 不是  	    //当考核计划的主体是所有考核对象的直接领导时 即使勾选了允许查看下属对对象评分也不出现下属评分列
     * @param plan_id
     * @return
     * @throws GeneralException
     */
    public boolean AllowSeeBelowScore() throws GeneralException{
    	boolean bool=true;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try{
    	String sql="select * from per_mainbody where plan_id='"+this.planid+"' and mainbody_id='"+this.userView.getA0100()+"' and body_id <>'1'";
    	rowSet=dao.search(sql);
    	if(rowSet.next()){
    		bool=false;
    	}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
			try {
	    		if(rowSet!=null){
						rowSet.close();
					}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
    		}
    	}
    	return bool;
    }

    /**
         * 生成提交表单 && 是否有附加选项 && 最大标度限制
         * 
         * @param template_id
         *                模版id
         * @param plan_id
         *                考核计划id
         * @param mainBodyID
         *                考核主体id
         * @param status
         *                权重分值标识 0：分值 １：权重
         * @return
         */
    public ArrayList getBatchGradeHtml(String template_id, int plan_id, String mainBodyID, String status, String titleName, int current) throws GeneralException
    {

	ArrayList resultList = new ArrayList();
	try
	{
		if(SystemConfig.getPropertyValue("batchGradeColumnWidth")!=null&&SystemConfig.getPropertyValue("batchGradeColumnWidth").length()>0) {
            columnWidth=Integer.parseInt(SystemConfig.getPropertyValue("batchGradeColumnWidth").trim());
        }
		if(SystemConfig.getPropertyValue("batchGradefirstColumnWidth")!=null&&SystemConfig.getPropertyValue("batchGradefirstColumnWidth").length()>0) {
            firstColumnWidth=Integer.parseInt(SystemConfig.getPropertyValue("batchGradefirstColumnWidth").trim());
        }

		Hashtable htxml= loadxml.getDegreeWhole();
		String pointEvalType=(String)htxml.get("PointEvalType");
		String MutiScoreOnePageOnePoint=(String)htxml.get("MutiScoreOnePageOnePoint");
		
		if((SystemConfig.getPropertyValue("batchgrade_radiotype")!=null && "multiple".equalsIgnoreCase(SystemConfig.getPropertyValue("batchgrade_radiotype").trim())) ||( "1".equals(pointEvalType)&& "True".equalsIgnoreCase(MutiScoreOnePageOnePoint) ))
	    {
	    	if("1".equals(this.scoreflag)) {
                isBatchGradeRadio=true;
            }
	    }
		
	    StringBuffer html = new StringBuffer("");
	    this.setNoShowOneMark(true);
	    this.setLoadStaticValue(true);
	    ArrayList pointList = getPerPointList(template_id, String.valueOf(plan_id));
		ArrayList sList=(ArrayList) pointList.get(0);//标准标度内容  zzk
		ArrayList pList=(ArrayList) pointList.get(1);//指标内容
		HashMap pMap=new HashMap();
		if(sList.size()==0){
			//如果没有定性指标，也没有定量非统一指标，需要判断此模板是否全部是定量统一指标  2013.11.20 pjf
			boolean isByModelFlag  = SingleGradeBo.getByModel(String.valueOf(plan_id),this.conn);
			boolean isHasPoint = SingleGradeBo.isHaveMatchByModel(object_id, this.conn);
			boolean isByModel = false;
			if(isByModelFlag && isHasPoint) {
                isByModel = true;
            }
			RowSet rowSet = null;
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			HashMap map2 = new HashMap();
			String sql = "select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
				+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id  and pg.gradecode=pgt.grade_template_id   and template_id='" + template_id + "' "; // pi.seq,
			if(isByModel){
				////能力素质支持一个评估计划适应多个岗位进行评估
				sql = "select case when (pp.point_type is null or pp.point_type='')  then '-9999' else pp.point_type end as item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc from per_competency_modal pp,per_point po ,per_grade pg,"+per_comTable+" pgt where  pp.point_id=po.point_id and  po.point_id=pg.point_id  and pg.gradecode=pgt.grade_template_id  and "+Sql_switcher.dateValue(historyDate)+" between pp.start_date and pp.end_date and object_type='3' and object_id = '"+getE01a1(this.object_id)+"' ";
			}
			if(isByModel){
				sql+="order by pp.point_type,pg.grade_id";
			}else{
				sql += "  order by pp.seq,pg.grade_id"; //  gradecode";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search(sql);
			if(rowSet.next()){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.performance.templateAllUniformPoint")));
			} else{
				throw GeneralExceptionHandler.Handle(new Exception("没有设置标准标度!"));
			}
		}
		for(int i=0;i<sList.size();i++){
			String[] temp = new String[14];
			temp=(String[]) sList.get(i);
			pMap.put(temp[1], "");
		}
		for(int i=0;i<pList.size();i++){
			String[] temp = new String[14];
			temp=(String[]) pList.get(i);
			String pointId=temp[0];
			String pointDesc=temp[1];
			if(pMap.get(pointId)==null){
				throw GeneralExceptionHandler.Handle(new Exception("指标:"+pointDesc+"没有设置标度!"));
			}
		}
		
	    ArrayList list = getPerformanceStencilList(template_id, this.noShowOneMark); // 2008-11-5  2010/10/29
	   
	    this.setUserNumberPointResultMap(getUserNumberPointResultMap((ArrayList) pointList.get(1), String.valueOf(plan_id)));
	    if ("1".equals(this.BlankScoreOption)) {
            this.pointMaxValueMap = getMaxPointValue(template_id); // 指标未打分时，0
        }
                                                                        // 按未打分处理，1
                                                                        // 计为最高分，默认值为按未打分处理
	    ArrayList objectList = getPerPlanObjects(plan_id, mainBodyID, current, this.scoreNumPerPage);
	   
	    
	    // 处理多人打分 是否显示考核对象自我评分列
	    dealwithSelfScore(objectList, mainBodyID);
	    // 处理多人评分 是否显示下属评分列
	    if (this.allowSeeLowerGrade!=null && this.allowSeeLowerGrade.trim().length()>0 && "True".equalsIgnoreCase(this.allowSeeLowerGrade))
		{
	    	dealwithLowerScore(String.valueOf(plan_id), mainBodyID, objectList);
		}

	    ArrayList arrayList = getTableHeaderHtml(String.valueOf(plan_id), template_id, list, pointList, status, titleName);
	    String isKnowWhole = (String) arrayList.get(1);
	    String fineMax = (String) arrayList.get(2);
	    String dataArea = (String) arrayList.get(3); // 定量指标的数值范围
	    String scoreflag = (String) arrayList.get(4); // =2混合，=1标度
	    String dataArea2 = (String) arrayList.get(5); // 当为混合打分时，表示各指标的数值范围
	    ArrayList a_perPointList = (ArrayList) arrayList.get(6); // 取得最底层的指标格（按顺序）
	    String pointDeformity = (String) arrayList.get(7);
	    String noGradeItem = (String) arrayList.get(8);
	    if (dataArea2.length() > 2) {
            dataArea2 = dataArea2.substring(1);
        }

	    /* 写表头 */
	    html.append((String) arrayList.get(0));
	    /* 判断打分状态 */

	    int gradeStatus = 0;
	    boolean isNoMark = false;
	    boolean isMarking = false;
	    boolean isMarked = false;
	    for (Iterator t = objectList.iterator(); t.hasNext();)
	    {
		String[] temp = (String[]) t.next();
		if (Integer.parseInt(temp[2]) == 0) {
            isNoMark = true;
        } else if (Integer.parseInt(temp[2]) == 1||Integer.parseInt(temp[2]) ==4) {
            isMarking = true;
        } else if (Integer.parseInt(temp[2]) == 2||Integer.parseInt(temp[2]) == 7) {
            isMarked = true;
        }
	    }

	    if (isNoMark && !isMarking && !isMarked) {
            gradeStatus = 0;
        } else if (!isNoMark && !isMarking && isMarked) {
            gradeStatus = 2;
        } else {
            gradeStatus = 1;
        }

	    /* 写表体内容 */
	    html.append(getTableBodyHtml(objectList, plan_id, mainBodyID, template_id, pointList, status, scoreflag, a_perPointList));
	    resultList.add(html.toString());
	    resultList.add(isKnowWhole);
	    resultList.add(fineMax);
	    resultList.add(dataArea);
	    resultList.add(String.valueOf(gradeStatus));
	    resultList.add(scoreflag);
	    resultList.add(dataArea2);
	    resultList.add(this.WholeEval);
	    resultList.add(pointDeformity);
	    resultList.add(noGradeItem);
	    this.setLoadStaticValue(false);
	    this.setNoShowOneMark(false);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return resultList;
    }

    /**
         * 得到某计划某人的考评对象个数
         * 
         * @param plan_id
         *                考核计划
         * @param mainBodyID
         *                考核主体
         * @author dengc
         * @return
         */
    public int getPerPlanObjects(int plan_id, String mainBodyID) throws GeneralException
    {

	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	int num = 0;
	String sql = "select count(po.object_id) num from per_mainbody pm,per_object po,per_mainbodyset pms  where pm.object_id=po.object_id  and pm.body_id=pms.body_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id
		+ " and pm.mainbody_id='" + mainBodyID + "'";
	if ( "False".equalsIgnoreCase(this.mitiScoreMergeSelfEval) )
	{
		String _str="";
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            _str="pms.level_o";
        } else {
            _str="pms.level ";
        }
		if ("2".equals(this.object_type)) // 考核人员
        {
            sql += " and pm.object_id<>'" +mainBodyID+ "'";
        } else {
            sql += " and ( "+_str+" is null or "+_str+"<>5 ) ";
        }
	}
	try
	{

	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
		num = rowSet.getInt("num");
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return num;
    }

    /**
         * 得到某计划某人的考评对象集合
         * 
         * @param plan_id
         *                考核计划
         * @param mainBodyID
         *                考核主体
         * @author dengc
         * @return
         */
    public ArrayList getPerplanObjects(int plan_id, String mainBodyID, String model) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	String _str="";
	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
        _str="pms.level_o";
    } else {
        _str="pms.level ";
    }
	
	String sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id;
	if(planVo!=null&&planVo.getInt("object_type")!=2) {
        sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_mainbodyset pms,per_object po  where pm.object_id=po.object_id  and pm.body_id=pms.body_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id;
    }
	
	if (model != null && ("2".equals(model) || "3".equals(model) || "4".equals(model)))
	{
		if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval) &&( "3".equals(model)|| "2".equals(model)))
		{
			if(planVo!=null&&planVo.getInt("object_type")!=2) {
                sql += " and  ("+_str+" is null or  "+_str+"<>5 ) ";
            } else {
                sql += " and pm.object_id<>'" + mainBodyID + "'";
            }
		}
		
		
	} 
	else
	{
		if(planVo!=null&&planVo.getInt("object_type")!=2) {
            sql += " and  ("+_str+" is null or  "+_str+"<>5 ) ";
        } else {
            sql += " and pm.object_id<>'" + mainBodyID + "' ";
        }
	}
	sql += " and  pm.mainbody_id='" + mainBodyID + "'";
	
	 if ("2".equals(this.object_type)) // 考核人员
     {
         sql += " order by po.a0000,po.b0110,po.e0122,po.object_id ";
     } else {
         sql += " order by po.a0000,po.b0110,po.object_id ";
     }
	  
	
	
	try
	{
 
	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
			String[] temp = new String[3];
			temp[0] = rowSet.getString(1);
			temp[1] = rowSet.getString(2);
			temp[2] = rowSet.getString(3);
			list.add(temp);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return list;
    }
    /**
     * Description: 单人考评支持拼音简写查询
     * @Version1.0 
     * Nov 19, 2012 10:34:38 AM Jianghe created
     * @param plan_id
     * @param mainBodyID
     * @param model
     * @return
     * @throws GeneralException
     */
    public ArrayList getUserList(String name,int plan_id, String mainBodyID, String model) throws GeneralException
    {
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
//    	if(pinyin_field.equals("")){
//    		pinyin_field = "c0103";
//    	}
    	//唯一性指标查询
    	InfoUtils iu = new InfoUtils();
    	FieldItem item = iu.getOnlyFieldItem(conn);
    	ArrayList list = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	String _str="";
    	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            _str="pms.level_o";
        } else {
            _str="pms.level ";
        }
    	
    	String sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id;
    	
    	if(planVo!=null&&planVo.getInt("object_type")!=2) {
            sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_mainbodyset pms,per_object po  where pm.object_id=po.object_id  and pm.body_id=pms.body_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id;
        }
    		sql+= "and (po.a0101 like '"+name+"%' or po.object_id in (select a0100 from usra01 where 1=2 ";
    		if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
	    		sql+=" or "+pinyin_field+" like '"+name+"%'";
	    	}
    		if(item!=null){
    			sql +=" or "+item.getItemid()+" like '"+name+"%'";
    		}
    		sql+=" ))";
    	if (model != null && ("2".equals(model) || "3".equals(model) || "4".equals(model)))
    	{
    		if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval) &&( "3".equals(model)|| "2".equals(model)))
    		{
    			if(planVo!=null&&planVo.getInt("object_type")!=2) {
                    sql += " and  ("+_str+" is null or  "+_str+"<>5 ) ";
                } else {
                    sql += " and pm.object_id<>'" + mainBodyID + "'";
                }
    		}
    		
    		
    	} 
    	else
    	{
    		if(planVo!=null&&planVo.getInt("object_type")!=2) {
                sql += " and  ("+_str+" is null or  "+_str+"<>5 ) ";
            } else {
                sql += " and pm.object_id<>'" + mainBodyID + "' ";
            }
    	}
    	sql += " and  pm.mainbody_id='" + mainBodyID + "'";
    	
    	if ("2".equals(this.object_type)) // 考核人员
        {
            sql += " order by po.a0000,po.b0110,po.e0122,po.object_id ";
        } else {
            sql += " order by po.a0000,po.b0110,po.object_id ";
        }
    	
    	
    	
    	try
    	{
    		
    		rowSet = dao.search(sql);
    		while (rowSet.next())
    		{
    			CommonData cd = new CommonData();
    			String[] temp = new String[3];
    			temp[0] = rowSet.getString(1);
    			temp[1] = rowSet.getString(2);
    			temp[2] = rowSet.getString(3);
				cd.setDataName(temp[1]);
				cd.setDataValue(temp[0]+"/"+temp[2]);
				list.add(cd);
    		}
    		
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return list;
    }
    /**
     * Description: 多人考评支持拼音简写查询
     * @Version1.0 
     * Nov 20, 2012 10:03:21 AM Jianghe created
     * @param name
     * @param plan_id
     * @param mainBodyID
     * @param current
     * @return
     * @throws GeneralException
     */
    public ArrayList getUserList1(String name,int plan_id,String mainBodyID,String current) throws GeneralException{
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String scoreNumPerPage = this.scoreNumPerPage;
    	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
//    	if(pinyin_field.equals("")){
//    		pinyin_field = "c0103";
//    	}
    	//唯一性指标查询
    	InfoUtils iu = new InfoUtils();
    	FieldItem item = iu.getOnlyFieldItem(conn);
    	ArrayList list = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;

    	// String sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from
            // per_mainbody pm,per_object po where pm.object_id=po.object_id and
            // pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and
            // pm.mainbody_id='"+mainBodyID+"'";

    	try
    	{
    		boolean isExist = false;
    		rowSet = dao.search("select * from usra01 where 1=2");
			ResultSetMetaData mt = rowSet.getMetaData();
			for (int i = 0; i < mt.getColumnCount(); i++) {
				String columnName = mt.getColumnName(i + 1);
				if (columnName.equalsIgnoreCase(pinyin_field)) {
					isExist = true;
				}
			}
			if(!isExist) {
                pinyin_field = "";
            }
    		String _str="";
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                _str="pms.level_o";
            } else {
                _str="pms.level ";
            }

    	    StringBuffer sql = new StringBuffer("select po.object_id,po.a0101,pm.status,pm.fillctrl ");
    	    sql.append(" from per_mainbody pm,per_object po,per_mainbodyset pms ");  
    	    sql.append(" where pm.object_id=po.object_id  and pm.body_id=pms.body_id   and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id + " and pm.mainbody_id='" + mainBodyID + "' ");
    	   
    	    	sql.append("and (po.a0101 like '"+name+"%' or po.object_id in (select a0100 from usra01 where 1=2");
    	    	if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
    	    		sql.append(" or "+pinyin_field+" like '"+name+"%'");
    	    	}	
    	    	if(item!=null){
        			sql.append(" or "+item.getItemid()+" like '"+name+"%'");
        		}
    	    	sql.append(" ))");
    	    if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval))
    	    {
    	    	if ("2".equals(this.object_type)) // 考核人员
                {
                    sql.append(" and pm.object_id<>'" + mainBodyID + "'");
                } else {
                    sql.append(" and ( "+_str+" is null or "+_str+"<>5 ) ");
                }
    	    }

    	    if ("2".equals(this.object_type)) // 考核人员
    	    {
    		// sql.append(" order by po.b0110,po.e0122,po.a0000 ");
    	    	sql.append(" order by po.a0000,po.b0110,po.e0122,po.object_id ");
    	    } else
    	    // 考核部门
    	    {
    		// sql.append(" order by po.b0110,po.object_id ");
    	    	sql.append(" order by po.a0000,po.b0110,po.object_id ");
    	    }
    	    rowSet = dao.search(sql.toString());
    	    while (rowSet.next())
    		{
    			CommonData cd = new CommonData();
    			String[] temp = new String[3];
    			temp[0] = rowSet.getString(1);
    			temp[1] = rowSet.getString(2);
    			temp[2] = rowSet.getString(3);
				cd.setDataName(temp[1]);
				cd.setDataValue(temp[0]);
				list.add(cd);
    		}

    	    

    	    /*if (!scoreNumPerPage.equals("0"))
    	    {
    		ArrayList newList = new ArrayList();
    		for (int i = 1; i <= list.size(); i++)
    		{

    		    if (i > (current - 1) * Integer.parseInt(scoreNumPerPage) && i <= (current) * Integer.parseInt(scoreNumPerPage))
    		    {
    			String[] temp = (String[]) list.get(i - 1);
    			newList.add(temp);
    		    }
    		}
    		list = newList;
    	    }*/
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}

    	return list;
        
    }

    /**
         * 得到某计划某人的考评对象集合
         * 
         * @param plan_id
         *                考核计划
         * @param mainBodyID
         *                考核主体
         * @author dengc
         * @return
         */
    public ArrayList getPerplanObjects(int plan_id, String mainBodyID) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;

	String sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id
		+ " and pm.object_id<>'" + mainBodyID + "' and  pm.mainbody_id='" + mainBodyID + "'";
	try
	{

	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
		String[] temp = new String[3];
		temp[0] = rowSet.getString(1);
		temp[1] = rowSet.getString(2);
		temp[2] = rowSet.getString(3);
		list.add(temp);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return list;
    }

    
    
    
    /**
     * 获取考核对象信息
     * @param plan_id
     * @param mainbodyID
     * @return
     * @throws GeneralException
     */
    public HashMap getPerPlanObjectInfos(int plan_id,String mainbodyID)  throws GeneralException
    {
    	HashMap map=new HashMap();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer("select po.object_id,po.a0101,po.b0110,po.e0122,po.body_id ");
    	    sql.append(" from per_mainbody pm,per_object po ");
    		sql.append(" where pm.object_id=po.object_id   and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id + " and pm.mainbody_id='" + mainbodyID + "' ");
    		RowSet rowSet=dao.search(sql.toString());
    		LazyDynaBean bean=new LazyDynaBean();
    		while(rowSet.next())
    		{
    			String object_id=rowSet.getString("object_id");
    			String b0110=rowSet.getString("b0110");
    			String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
    			String body_id = rowSet.getString("body_id") != null ? rowSet.getString("body_id") : "";
    			String b0110_desc="";
    			String e0122_desc="";
    			if(b0110!=null&&b0110.length()>0) {
                    b0110_desc=AdminCode.getCodeName("UN",b0110);
                }
    			if(e0122!=null&&e0122.length()>0) {
                    e0122_desc=AdminCode.getCodeName("UM",e0122);
                }
    			bean=new LazyDynaBean();
    			bean.set("b0110",b0110_desc);
    			bean.set("e0122",e0122_desc);
    			bean.set("e0122_code",e0122);
    			bean.set("body_id",body_id);
    			map.put(object_id,bean);
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    
    
    /**
         * 得到某计划某人的考评对象集合
         * 
         * @param plan_id
         *                考核计划
         * @param mainBodyID
         *                考核主体
         * @author dengc
         * @return
         */
    public ArrayList getPerPlanObjects(int plan_id, String mainBodyID, int current, String scoreNumPerPage) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;

	// String sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from
        // per_mainbody pm,per_object po where pm.object_id=po.object_id and
        // pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and
        // pm.mainbody_id='"+mainBodyID+"'";

	try
	{
		String _str="";
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            _str="pms.level_o";
        } else {
            _str="pms.level ";
        }

	    StringBuffer sql = new StringBuffer("select po.object_id,po.a0101,pm.status,pm.fillctrl ");
	    sql.append(" from per_mainbody pm,per_object po,per_mainbodyset pms ");  
	    sql.append(" where pm.object_id=po.object_id  and pm.body_id=pms.body_id   and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id + " and pm.mainbody_id='" + mainBodyID + "' ");
	    if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval))
	    {
	    	if ("2".equals(this.object_type)) // 考核人员
            {
                sql.append(" and pm.object_id<>'" + mainBodyID + "'");
            } else {
                sql.append(" and ( "+_str+" is null or "+_str+"<>5 ) ");
            }
	    }

	    if ("2".equals(this.object_type)) // 考核人员
	    {
		// sql.append(" order by po.b0110,po.e0122,po.a0000 ");
	    	sql.append(" order by po.a0000,po.b0110,po.e0122,po.object_id ");
	    } else
	    // 考核部门
	    {
		// sql.append(" order by po.b0110,po.object_id ");
	    	sql.append(" order by po.a0000,po.b0110,po.object_id ");
	    }
	    HashMap listMap = new HashMap();
	    ArrayList aList = new ArrayList();
	    rowSet = dao.search(sql.toString());
	    while (rowSet.next())
	    {
		String[] temp = new String[4];
		temp[0] = rowSet.getString(1);
		temp[1] = rowSet.getString(2)==null?"":rowSet.getString(2);
		temp[2] = rowSet.getString(3)!=null?rowSet.getString(3):"0";
		if (rowSet.getString("fillctrl") == null)
		{
		    temp[3] = "0";
		} else {
            temp[3] = rowSet.getString("fillctrl");
        }
		listMap.put(rowSet.getString(1), temp);
		aList.add(temp);
	    }

	    if ("true".equalsIgnoreCase(this.isShowOrder))
	    {
	    	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())))
	    	{	
				ArrayList objectList_order = (ArrayList) objectTotalScoreMap.get("objectList_order");
				if (objectList_order != null) {
                    list = orderObjectList(objectList_order, listMap);
                } else {
                    list = aList;
                }
	    	}
	    	else {
                list=aList;
            }
	    } 
	    else {
            list = aList;
        }

	    if (!"0".equals(scoreNumPerPage))
	    {
		ArrayList newList = new ArrayList();
		for (int i = 1; i <= list.size(); i++)
		{

		    if (i > (current - 1) * Integer.parseInt(scoreNumPerPage) && i <= (current) * Integer.parseInt(scoreNumPerPage))
		    {
			String[] temp = (String[]) list.get(i - 1);
			newList.add(temp);
		    }
		}
		list = newList;
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}

	this.objectList = list;
	return list;
    }
    public ArrayList getObjectsList(int plan_id,String mainBodyID,String current) throws GeneralException{
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String scoreNumPerPage = this.scoreNumPerPage;
    	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
//    	if(pinyin_field.equals("")){
//    		pinyin_field = "c0103";
//    	}
    	//唯一性指标查询
    	InfoUtils iu = new InfoUtils();
    	FieldItem item = iu.getOnlyFieldItem(conn);
    	ArrayList list = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	
    	// String sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from
    	// per_mainbody pm,per_object po where pm.object_id=po.object_id and
    	// pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and
    	// pm.mainbody_id='"+mainBodyID+"'";
    	
    	try
    	{
    		String _str="";
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                _str="pms.level_o";
            } else {
                _str="pms.level ";
            }
    		
    		StringBuffer sql = new StringBuffer("select po.object_id,po.a0101,pm.status,pm.fillctrl ");
    		sql.append(" from per_mainbody pm,per_object po,per_mainbodyset pms ");  
    		sql.append(" where pm.object_id=po.object_id  and pm.body_id=pms.body_id   and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id + " and pm.mainbody_id='" + mainBodyID + "' ");
    		
//    		sql.append("and (po.a0101 like '"+name+"%' or po.object_id in (select a0100 from usra01 where 1=2");
//    		if(!(pinyin_field==null || pinyin_field.equals("") || pinyin_field.equals("#") )){
//    			sql.append(" or "+pinyin_field+" like '"+name+"%'");
//    		}	
//    		if(item!=null){
//    			sql.append(" or "+item.getItemid()+" like '"+name+"%'");
//    		}
//    		sql.append(" ))");
    		if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval))
    		{
    			if ("2".equals(this.object_type)) // 考核人员
                {
                    sql.append(" and pm.object_id<>'" + mainBodyID + "'");
                } else {
                    sql.append(" and ( "+_str+" is null or "+_str+"<>5 ) ");
                }
    		}
    		
    		if ("2".equals(this.object_type)) // 考核人员
    		{
    			// sql.append(" order by po.b0110,po.e0122,po.a0000 ");
    			sql.append(" order by po.a0000,po.b0110,po.e0122,po.object_id ");
    		} else
    			// 考核部门
    		{
    			// sql.append(" order by po.b0110,po.object_id ");
    			sql.append(" order by po.a0000,po.b0110,po.object_id ");
    		}
    		rowSet = dao.search(sql.toString());
    		CommonData vo2 = new CommonData("", " ");
			list.add(vo2);
    		while (rowSet.next())
    		{
    			CommonData cd = new CommonData();
    			String[] temp = new String[3];
    			temp[0] = rowSet.getString(1);
    			temp[1] = rowSet.getString(2);
    			temp[2] = rowSet.getString(3);
    			cd.setDataName(temp[1]);
    			cd.setDataValue(temp[0]);
    			list.add(cd);
    		}
    		
    		
    		
    		/*if (!scoreNumPerPage.equals("0"))
    	    {
    		ArrayList newList = new ArrayList();
    		for (int i = 1; i <= list.size(); i++)
    		{

    		    if (i > (current - 1) * Integer.parseInt(scoreNumPerPage) && i <= (current) * Integer.parseInt(scoreNumPerPage))
    		    {
    			String[] temp = (String[]) list.get(i - 1);
    			newList.add(temp);
    		    }
    		}
    		list = newList;
    	    }*/
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	
    	return list;
    	
    }
    /**
         * 得到某计划某人的考评对象集合2
         * 
         * @param plan_id
         *                考核计划
         * @param mainBodyID
         *                考核主体
         * @author dengc
         * @return
         */
    public HashMap getPerPlanObjects2(int plan_id, String mainBodyID) throws GeneralException
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	String sql = "select po.object_id,po.a0101,pm.status from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id=" + plan_id + " and po.plan_id=" + plan_id
		+ " and pm.mainbody_id='" + mainBodyID + "'";
	try
	{
	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
		String[] temp = new String[3];
		temp[0] = rowSet.getString(1);
		temp[1] = rowSet.getString(2);
		temp[2] = rowSet.getString(3);
		map.put(temp[0], temp);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return map;
    }

  
    
    
    /**
         * 按总分循序排列
         * 
         * @param orderList
         * @param objectList
         * @return
         */
    public ArrayList orderObjectList(ArrayList orderList, HashMap listMap)
    {

	ArrayList list = new ArrayList();
	for (int i = 0; i < orderList.size(); i++)
	{
	    String temp = (String) orderList.get(i);
	    if (listMap.get(temp) != null) {
            list.add((String[]) listMap.get(temp));
        }
	}
	return list;
    }
    
    
    
    
    
    /**
     * 
     * @param objList
     * @return
     */
    public HashMap getOtherInfoMap(ArrayList objList)
    {
    	HashMap map=new HashMap();
    	try
    	{
    		if(objList.size()==0) {
                return map;
            }
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer whl=new StringBuffer("");
    		for (Iterator t = objList.iterator(); t.hasNext();)
    		{
    				String[] temp = (String[]) t.next();
    				whl.append(",'"+temp[0]+"'");
    		}
    		
    		
    		StringBuffer sub_str=new StringBuffer("a0100");
    		String key="a0100";
    		String tab="usra01";
    		if (!"2".equals(this.object_type))
    		{
    			sub_str.setLength(0);
    			sub_str.append("b0110");
    			key="b0110";
    			tab="b01";
    		}
    		LazyDynaBean abean=null;
    		for(int i=0;i<basicFieldList.size();i++)
    		{
    			abean=(LazyDynaBean)basicFieldList.get(i);
    			String item_id=(String)abean.get("item_id");
    			if(DataDictionary.getFieldItem(item_id.toLowerCase())!=null) {
                    sub_str.append(","+item_id);
                }
    		}
    		 
    		RowSet rowSet=dao.search("select "+sub_str.toString()+" from "+tab+" where "+key+" in ("+whl.substring(1)+")"); 
			DecimalFormat myformat1 = new DecimalFormat("##########.#####");
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fmym=new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat fmy=new SimpleDateFormat("yyyy");
    		while(rowSet.next())
    		{
    			LazyDynaBean _bean=new LazyDynaBean();
    			for(int i=0;i<basicFieldList.size();i++)
        		{
        			abean=(LazyDynaBean)basicFieldList.get(i);
        			String item_id=(String)abean.get("item_id");
        			String itemtype=(String)abean.get("itemtype");
        			String codesetid=(String)abean.get("codesetid");
        			String itemlength=(String)abean.get("itemlength");
		    		if("A".equalsIgnoreCase(itemtype))
		    		{
		    			if(rowSet.getString(item_id)!=null&&rowSet.getString(item_id).trim().length()>0)
		    			{
		    				if(!"0".equals(codesetid))
		    				{
		    					//如果是部门考核计划，显示单位名称是需要找到其直接上级
		    					String codeitemid = rowSet.getString(item_id).trim();
		    					if("b0110".equals(item_id)){
		    						codeitemid = getUnitCode(codeitemid);//递归查找
		    					}
		    					_bean.set(item_id,AdminCode.getCodeName(codesetid,codeitemid));
		    				}
		    				else
		    				{
		    					_bean.set(item_id,rowSet.getString(item_id).trim());
		    				}	
		    			}
		    		}
		    		else if("N".equalsIgnoreCase(itemtype))
		    		{
		    			if(rowSet.getString(item_id)!=null&&rowSet.getString(item_id).trim().length()>0)
		    			{
		    				_bean.set(item_id,myformat1.format(rowSet.getDouble(item_id)));
		    			}
		    		}
		    		else if("D".equalsIgnoreCase(itemtype))
		    		{
		    			if(rowSet.getDate(item_id)!=null)
		    			{
		    				if("4".equalsIgnoreCase(itemlength)) // 年类型
                            {
                                _bean.set(item_id,fmy.format(rowSet.getDate(item_id)));
                            } else if("7".equalsIgnoreCase(itemlength)) // 年月类型
                            {
                                _bean.set(item_id,fmym.format(rowSet.getDate(item_id)));
                            } else {
                                _bean.set(item_id,fm.format(rowSet.getDate(item_id))); // 年月日类型
                            }
		    			}
		    		} 
        		}
    			map.put(rowSet.getString(key),_bean);
    		}
    		if(rowSet!=null) {
                rowSet.close();
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    
    
    /**
     * @param objList
     * @return
     */
    public HashMap getObjectsResultMap(String plan_id)
    {
    	HashMap map = new HashMap();
    	RowSet rowSet = null;
    	try
    	{
    		Hashtable htxml = new Hashtable();   	    
    	    if(loadxml==null)
    	    {
    	    	if(planLoadXmlMap.get(String.valueOf(plan_id))==null)
    	    	{
    	    		loadxml = new LoadXml(this.conn, String.valueOf(plan_id));
    	    		planLoadXmlMap.put(String.valueOf(plan_id),loadxml);
    	    	}
    	    	else {
                    loadxml=(LoadXml)planLoadXmlMap.get(String.valueOf(plan_id));
                }
    	    }   
    	    LoadXml  loadxml1 = new LoadXml(this.conn, String.valueOf(plan_id));
    	    htxml = loadxml1.getDegreeWhole();
    	    int KeepDecimal = Integer.parseInt((String) htxml.get("KeepDecimal")); // 小数位
			String deviationScoreUsed=(String) htxml.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是  zzk 
			String total_score="";
			if("1".equals(deviationScoreUsed)){
				total_score=",pr.reviseScore";
			}    	    
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer("");   		
    		sql.append("select pr.object_id,pr.a0101,pr.original_score,pr.score"+total_score+",pr.grade_id,pr.resultdesc,pr.exX_object,pr.ordering "); 
    		sql.append(" from per_result_"+plan_id+" pr,per_mainbody pm where pm.plan_id = "+plan_id+" ");
    		sql.append(" and pr.object_id=pm.object_id and pm.mainbody_id = '" + this.userView.getA0100() + "' "); 
    		sql.append(" order by pr.a0000 "); 
    		rowSet = dao.search(sql.toString());     		    		
    		while(rowSet.next())
    		{
    			String object_id = Null(rowSet.getString("object_id"));
    			LazyDynaBean abean = new LazyDynaBean();
    			abean.set("object_id",object_id);
    			
    			if (rowSet.getString("original_score") == null || rowSet.getString("original_score").trim().length()<=0) {
                    abean.set("original_score", "");
                } else {
                    abean.set("original_score", PubFunc.round(Null(rowSet.getString("original_score")), KeepDecimal));
                }
    			if (rowSet.getString("score") == null || rowSet.getString("score").trim().length()<=0) {
                    abean.set("score", "");
                } else {
                    abean.set("score", PubFunc.round(Null(rowSet.getString("score")), KeepDecimal));
                }
    			if("1".equals(deviationScoreUsed)){
    				
        			if (rowSet.getString("reviseScore") == null || rowSet.getString("reviseScore").trim().length()<=0) {
                        abean.set("reviseScore", "");
                    } else {
                        abean.set("reviseScore", PubFunc.round(Null(rowSet.getString("reviseScore")), KeepDecimal));
                    }
    			}
    			
    			if (rowSet.getString("exX_object") == null || rowSet.getString("exX_object").trim().length()<=0) {
                    abean.set("exX_object", "");
                } else {
                    abean.set("exX_object", PubFunc.round(Null(rowSet.getString("exX_object")), KeepDecimal));
                }
    			if (rowSet.getString("ordering") == null || rowSet.getString("ordering").trim().length()<=0) {
                    abean.set("ordering", "");
                } else {
                    abean.set("ordering", PubFunc.round(Null(rowSet.getString("ordering")), 0));
                }
    			
    			abean.set("grade_id",Null(rowSet.getString("grade_id")));
    			abean.set("resultdesc",Null(rowSet.getString("resultdesc")));
    			
    			map.put(object_id,abean);
    		}
    		if(rowSet!=null) {
                rowSet.close();
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    public String Null(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            return "";
        } else {
            return str;
        }
    }
    
    
    private String getFrontColumnContext(String[] objs,HashMap otherInfoMap,int anum,String org_cardid,String cardid)
    {
    	StringBuffer str=new StringBuffer(""); 
    	String url="";
		if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4)
		{
			if(org_cardid!=null&&org_cardid.trim().length()>0&&!"-1".equalsIgnoreCase(org_cardid))  //如果定义了单位登记表，则给出链接
			{
				
			}
		}
		else
		{
			if(cardid!=null&&cardid.trim().length()>0&&!",＃,#,-1,".contains(","+cardid+","))//liuy 2015-5-6 9424  //如果定义了人员登记表，则给出链接
			{
				//【6698】多人考评，安全问题及界面问题   jingq upd 2015.01.19
				//String temp="~"+SafeCode.encode(PubFunc.convertTo64Base(objs[0]));
				//url="<a href=\"javascript:openwin('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=Usr&a0100="+temp+"&inforkind=1&flag=nopriv&tabid="+cardid+"&multi_cards=-1');\">";
				String temp = "encryptParam="+PubFunc.encrypt("userbase=Usr&a0100="+PubFunc.encrypt(objs[0])+"&inforkind=1&module=per&flag=nopriv&tabid="+cardid+"&multi_cards=-1");
				url="<a href=\"javascript:openwin('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&"+temp+"');\">";
			}
		}
    	 
		LazyDynaBean abean=null;
		LazyDynaBean valueBean=null;
		if(otherInfoMap.get(objs[0])!=null) {
            valueBean=(LazyDynaBean)otherInfoMap.get(objs[0]);
        }
		double a0101DiaplayWidth = getA0101DiaplayWidth();//指标长度
   	    if(basicFieldList.size()==0||(basicFieldList.size()>0&& "False".equalsIgnoreCase(this.LockMGradeColumn)))
   	    {
   	    	
    		
   	    	str.append("<td style='display:none'>"+objs[0]+"</td>");
   	    	str.append("<td id='a0' align='center'  class='cell_locked common_background_color common_border_color'  width='30' >"+anum+"</td>  ");
   	    	str.append("   <td id='a'  width='"+a0101DiaplayWidth+"'    align='left'  class='cell_locked common_background_color common_border_color'  >"); 
   	    	str.append(url);
			str.append("<font  class='fontStyle_self'   >"+objs[1]+"</font>");
			if(url.length()>0) {
                str.append("</a>");
            }
			str.append("</td>");
			if(basicFieldList.size()>0&& "False".equalsIgnoreCase(this.LockMGradeColumn))
			{
				for(int i=0;i<basicFieldList.size();i++)
	    		{
					
	    			abean=(LazyDynaBean)basicFieldList.get(i);
	    			double displaywidth = Double.parseDouble((String)abean.get("displaywidth"));
	    			String itemid=(String)abean.get("item_id"); 
	    			if("2".equals(this.object_type) && "a0101".equalsIgnoreCase(itemid)) {
                        continue;
                    }
				    String context="&nbsp;";
	    			if(valueBean!=null&&valueBean.get(itemid)!=null) {
                        context=(String)valueBean.get(itemid);
                    }
	    			str.append("<td  id='a' class='RecordRow common_border_color' align='left'  width='"+displaywidth+"'  nowrap >"+context+"</td>");
	    		}
			}
   	    }
   	    else
   	    { 
   	    	
   	    	for(int i=0;i<basicFieldList.size();i++)
    		{
    			abean=(LazyDynaBean)basicFieldList.get(i);
    			String itemid=(String)abean.get("item_id");    
			    String context="&nbsp;";
			    double displaywidth = Double.parseDouble((String)abean.get("displaywidth"));
    			if(valueBean!=null&&valueBean.get(itemid)!=null) {
                    context=(String)valueBean.get(itemid);
                }
    			if("".equals(context)) {
                    context="&nbsp;";
                }
    			if(i==0)
    			{ 
    				str.append("<td style='display:none'>"+objs[0]+"</td>");
    				if("2".equals(this.object_type) && "a0101".equalsIgnoreCase(itemid))
	    			{
    					str.append("<td id='a0' align='center'  class='cell_locked common_border_color'  width='30' >"+anum+"</td>  ");
    		   	    	str.append("   <td id='a'  width='"+a0101DiaplayWidth+"'    align='left'  class='cell_locked common_border_color'  >"); 
    		   	    	str.append(url);
    					str.append("<font  class='fontStyle_self'   >"+objs[1]+"</font>");
    					if(url.length()>0) {
                            str.append("</a>");
                        }
    					str.append("</td>");
	    			}else
	    			{
	    				str.append("<td id='a0' align='center'  class='cell_locked common_border_color'  width='30' >"+anum+"</td>  ");
	    	   	    	str.append("   <td id='a'  width='"+displaywidth+"'    align='left'  class='cell_locked common_border_color'  >"+context+"</td>");
	    			}    				 
    			}
    			else
    			{
    				if("2".equals(this.object_type) && "a0101".equalsIgnoreCase(itemid))
	    			{	    					
    		   	    	str.append("   <td id='a'  width='"+a0101DiaplayWidth+"'    align='left'  class='cell_locked common_border_color'  >"); 
    		   	    	str.append(url);
    					str.append("<font  class='fontStyle_self'   >"+objs[1]+"</font>");
    					if(url.length()>0) {
                            str.append("</a>");
                        }
    					str.append("</td>");
	    			}else
	    			{
	    				str.append("<td  id='a' class='cell_locked common_border_color' align='left'  width='"+displaywidth+"'  nowrap >"+context+"</td>");
	    			}   				
    			}
    		}
   	    	if(!"2".equals(this.object_type))
    		{
	   	    	str.append("   <td id='a'  width='"+a0101DiaplayWidth+"'    align='left'  class='cell_locked common_border_color'  >"); 
	   	    	str.append(url);
				str.append("<font  class='fontStyle_self'   >"+objs[1]+"</font>");
				if(url.length()>0) {
                    str.append("</a>");
                }
				str.append("</td>"); 
    		}
   	    } 
    	return str.toString();
    }
    
    
    /**
     * 获得对象引入计划的分值
     * @param htxml
     * @param plan_id
     * @return
     */
    public HashMap getObjRelatePlanValue(Hashtable htxml,int plan_id)
    {
    	HashMap objValueMap=new HashMap();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		DbWizard dbWizard=new DbWizard(this.conn);
    		ArrayList planlist = loadxml.getRelatePlanValue("Plan");
    		String KeepDecimal = (String) htxml.get("KeepDecimal"); // 小数位
	    	LazyDynaBean abean=null;
	    	LazyDynaBean abean1=null;
	    	StringBuffer subStr=new StringBuffer("select object_id");
	    	for(int i=0;i<planlist.size();i++)
	    	{
	    		abean=(LazyDynaBean)planlist.get(i);
	    		String id=(String)abean.get("id");
	    		String Name=(String)abean.get("Name"); 	
	    		if(dbWizard.isExistField("per_result_"+plan_id,"G_"+id,false)) {
                    subStr.append(",G_"+id);
                }
	    	}
	    	RowSet rowSet=dao.search(subStr.toString()+" from per_result_"+plan_id);
	    	while(rowSet.next())
	    	{
	    		String object_id=rowSet.getString("object_id");
	    		abean=new LazyDynaBean(); 
	    		for(int i=0;i<planlist.size();i++)
		    	{
	    			abean1=(LazyDynaBean)planlist.get(i);
		    		String id=(String)abean1.get("id");
		    		
		    		if(!dbWizard.isExistField("per_result_"+plan_id,"G_"+id,false))
		    		{
		    			abean.set("G_"+id, "");
		    		}
		    		else
		    		{
			    		String Name=(String)abean1.get("Name"); 	
			    		if(rowSet.getString("G_"+id)!=null)
			    		{
			    			abean.set("G_"+id,PubFunc.round(rowSet.getString("G_"+id), Integer.parseInt(KeepDecimal)));
			    		}
			    		else {
                            abean.set("G_"+id, "");
                        }
		    		}
		    	} 
	    		objValueMap.put(object_id, abean);
	    	}
	    	if(rowSet!=null) {
                rowSet.close();
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return objValueMap;
    }
    /**得到职位*/
	public String getE01a1(String object_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	String e01a1 = "";
    	try
	    {
	    	RowSet rs0 = dao.search("select e01a1 from usra01 where a0100='"+object_id+"'");
	    	if(rs0.next()) {
                e01a1 = rs0.getString(1);
            }
	    } catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return e01a1;
    	
    }

    /**
         * 生成表体html
         * 
         * @param objectList
         *                考核对象集合
         * @param plan_id
         *                考核计划
         * @param templateID
         *                模版id
         * @param nodeKnowDegree
         *                了解程度
         * @param wholeEval
         *                总体评价
         * @param GradeClass
         *                等级分类ID
         * @param summaryFlag
         *                总结报告
         * @param status
         *                权重分值标识 0：分值 1：权重
         * @param scoreflag
         *                2:混合 1。标度
         * @author dengc
         * @return
         */
    public String getTableBodyHtml(ArrayList objectList, int plan_id, String mainBodyID, String templateID, ArrayList pointList, String status, String scoreflag, ArrayList a_perPointList)
	    throws GeneralException
    {

	StringBuffer bodyHtml = new StringBuffer("");
	try
	{
		 HashMap objectInfoMap=getPerPlanObjectInfos(plan_id,mainBodyID);
		 boolean isByModelFlag = SingleGradeBo.getByModel(String.valueOf(plan_id),this.conn);
		 
		 HashMap pointMap = new HashMap();
		 if(isByModelFlag && SingleGradeBo.isHaveMatchByModel(object_id, this.conn)){
			 ////能力素质支持一个评估计划适应多个岗位进行评估
			 pointMap = this.getCompetencyPointprivMap(String.valueOf(plan_id),mainBodyID);
		 }else{
			 pointMap=this.getPointprivMap(String.valueOf(plan_id),mainBodyID);   //得到指标权限信息
		 }
	    //HashMap pointMap = getPointprivMap(String.valueOf(plan_id), mainBodyID); // 得到指标权限信息
	    ArrayList pointGradeList = (ArrayList) pointList.get(0); // 详细信息的绩效指标集和
	    
	    Hashtable htxml = new Hashtable();  
		htxml = loadxml.getDegreeWhole();
		HashMap objRelatePlanValue=new HashMap();
		String ScoreShowRelatePlan=(String)htxml.get("ScoreShowRelatePlan"); //多人评分显示引入计划得分
		if(ScoreShowRelatePlan==null) {
            ScoreShowRelatePlan="";
        }
		if("True".equalsIgnoreCase(ScoreShowRelatePlan))
		{
			
			objRelatePlanValue=getObjRelatePlanValue(htxml,plan_id);
		}
		
	    
	    /* 得到某计划考核主体给对象的评分结果hashMap */
	    HashMap perTableMap = getPerTableXXX(plan_id, mainBodyID, objectList);
	    /* 了解程度 && 总体评价 */
	    ArrayList nodeKnowDegreeList = new ArrayList();
	    ArrayList wholeEvalList = new ArrayList();
	    if ("true".equals(this.NodeKnowDegree))
	    {
		nodeKnowDegreeList = getExtendInfoValue("1", "");
	    }
	    if ("0".equals(this.WholeEvalMode)&& "true".equals(this.WholeEval))
	    {
		wholeEvalList = getExtendInfoValue("2", this.GradeClass);
	    }
	    int no = 0;
	    StringBuffer users = new StringBuffer("");
	    StringBuffer points = new StringBuffer("");
	    int anum = 0;
	    
	    ArrayList perPointList=new ArrayList();
	    
	    
	    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
	    String cardid="";
	    String org_cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org"); 
		String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
            display_e0122="0";
        }
	    ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao
					.search("select str_value from constant where constant='PER_PARAMETERS'");
			if (rs.next()) {
				String str_value = rs.getString("str_value");
				if (str_value == null
						|| (str_value != null && "".equals(str_value))) {

				} else {
					Document doc = PubFunc.generateDom(str_value);
					String xpath = "//Per_Parameters";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					Element child;
					if (ele != null) {
						child = ele.getChild("Plan");
						if (child != null) {
							cardid = child.getAttributeValue("NameLinkCard");
						}
					}
				}
			}
		if(cardid==null||cardid.length()==0){//为空或无则取其他参数中的人员登记表  
			cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
		}
		cardid= "#".equals(cardid)?"-1":cardid;//如果为“#” 则不显示 即为 -1  zhaoxg 2014-4-23
		String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
		if(" ".equals(seprartor)) {
            seprartor="&nbsp;";
        }

		LazyDynaBean timeBean=this.getPlanKhTime();
		HashMap otherInfoMap=new HashMap();
//		if(this.batchGradeOthField.length()>0)
		if(this.basicFieldList.size()>0) {
            otherInfoMap=getOtherInfoMap(objectList);
        }
		
		// 最终评估结果
		HashMap objectsResultMap = new HashMap();
		if(this.userView.hasTheFunction("06060103"))
		{
			objectsResultMap = getObjectsResultMap(String.valueOf(plan_id));
		}
		boolean isAllowSeeLowerGrade=false;//是否显示下属评分
		if(this.allowSeeLowerGrade!=null && this.allowSeeLowerGrade.trim().length()>0 && "True".equalsIgnoreCase(this.allowSeeLowerGrade)) {
            isAllowSeeLowerGrade=true;
        }
		HashMap mainBodydata=new HashMap();
		if(isAllowSeeLowerGrade||this.isSelfScoreColumn) {
            mainBodydata=this.getmainBodyScoreMap(mainBodyID,isAllowSeeLowerGrade);//获取下属打分总分 zhanghua 2017-9-13
        }
		//int k = 0;
		
		// 表头层级
		int lays = this.a_lays;
	    lays++;
	    lays++;
	    for (Iterator t = objectList.iterator(); t.hasNext();)
		{
	    	
			String[] temp = (String[]) t.next(); // temp{
	                                                        // object,姓名,status:打分状态,fillctrl:是否必打分}
			users.append("/" + temp[0]);
			// String score_order=(String)objectTotalScoreMap.get(temp[0]);
			HashMap map = (HashMap) pointMap.get("huicong"+temp[0]); // 得到具有某考核对象的指标权限map   慧聪网需求   此处取的是真实的指标权限  zhaoxg 2014-6-20
			HashMap objectResultMap = null; // 考核对象的考核结果
			String value = temp[1];
			value = value.replaceAll("\n", "");
			this.script_code.append("\r\n obj_result[obj_result.length]={objectid:\"" + temp[0] + "\",name:\"" + value + "\",status:\""+temp[2]+"\"");
	
			if (!"0".equals(temp[2])) {
                objectResultMap = (HashMap) perTableMap.get(temp[0]);
            }
			bodyHtml.append("\r\n   <tr >");
			// 基本信息指标
			bodyHtml.append(getFrontColumnContext(temp,otherInfoMap,(anum+1),org_cardid,cardid));
			
			
			// 北京市监狱局要求把总分列和排名列放到姓名后面 JinChunhai 2012.09.13
			if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
			{
				// 总分
				if ("true".equalsIgnoreCase(this.isShowTotalScore))
				{
				    String score_order = "";
				    if ("true".equalsIgnoreCase(this.isAutoCountTotalOrder)) {
                        score_order = (String) objectTotalScoreMap.get(temp[0]);
                    } else {
                        score_order = "*/*";
                    }
				    bodyHtml.append("<td class='RecordRow common_background_color common_border_color' ");
				    bodyHtml.append(" align='center' id='zf" + no + "'  width='");
				    bodyHtml.append(columnWidth);
				    bodyHtml.append("'  nowrap ><font  color='#2E67B9'  >");
				    bodyHtml.append(score_order.split("/")[0]);
				    bodyHtml.append("</font> </td>");
				    
				    if (anum == 0) {
                        perPointList.add("##");
                    }
				}
				// 排名
				if ("true".equalsIgnoreCase(this.isShowOrder))
				{
				    String score_order = "";
				    if ("true".equalsIgnoreCase(this.isAutoCountTotalOrder)) {
                        score_order = (String) objectTotalScoreMap.get(temp[0]);
                    } else {
                        score_order = "*/*";
                    }
		
				    bodyHtml.append("<td class='RecordRow common_border_color' align='center'  id='pm" + no + "'   width='");
				    bodyHtml.append(columnWidth);
				    bodyHtml.append("'  nowrap ><font  color='#2E67B9'   >");
				    bodyHtml.append(score_order.split("/")[1]);
				    bodyHtml.append("</font> </td>");
				    
				    if (anum == 0) {
                        perPointList.add("##");
                    }
		
				}								
			}			
						
			if(SystemConfig.getPropertyValue("goalAtFirst")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("goalAtFirst").trim()))
			{
				
				// 个人目标作为评分标准
				if ("True".equalsIgnoreCase(this.noteIdioGoal))
				{
				   // if (this.planVo.getInt("object_type") == 2)
				    {
					bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
					bodyHtml.append(PubFunc.round(String.valueOf(this.columnWidth/1.5),0));
					bodyHtml.append("'  nowrap >");
					if ("False".equalsIgnoreCase(relatingTargetCard) || "1".equalsIgnoreCase(relatingTargetCard))
					{
		
					    bodyHtml.append("<a href='/selfservice/performance/view_summary.do?b_query=link&planNum=");
					    bodyHtml.append(plan_id);
					    bodyHtml.append("&objectId=");
					    bodyHtml.append("~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])));
					    if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4) {
                            bodyHtml.append("&optUrl=goal2' ");
                        } else {
                            bodyHtml.append("&optUrl=goal' ");
                        }
					    bodyHtml.append(" target='_blank'>");
					    bodyHtml.append(ResourceFactory.getProperty("lable.performance.perGoal"));
					    bodyHtml.append("</a>");
		
					} else
					{
					    bodyHtml.append(getGoalCardUrl(String.valueOf(plan_id), temp[0]));
					}
					bodyHtml.append("</td>");
				    }
				    if (anum == 0) {
                        perPointList.add("##");
                    }
				    
				}
				/* 个人总结评价作为评分标准 */
				if ("True".equals(this.SummaryFlag))
				{
				   // if (this.planVo.getInt("object_type") == 2)
				    {
					bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
					bodyHtml.append(PubFunc.round(String.valueOf(this.columnWidth/1.5),0));
					bodyHtml.append("'  nowrap >");
					
					String url="/selfservice/performance/view_summary.do?b_query=link&planNum="+plan_id+"&objectId="+"~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0]));
					if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4) {
                        url+="&optUrl=summary2";
                    } else {
                        url+="&optUrl=summary";
                    }
					 
					
					bodyHtml.append("<a href='"+url+"' "); 
					bodyHtml.append(" target='_blank'>");
					if ("0".equals(this.performanceType))
					{
					    String info = SystemConfig.getPropertyValue("per_examineInfo");
					    if (info == null || info.length() == 0) {
                            bodyHtml.append(ResourceFactory.getProperty("lable.performance.perSummary"));
                        } else {
                            bodyHtml.append(new String(info.getBytes("ISO-8859-1"), "GBK"));
                        }
		
					} else if ("1".equals(this.performanceType)) {
                        bodyHtml.append(ResourceFactory.getProperty("label.reportwork.report"));
                    }
					bodyHtml.append("</a>");
					bodyHtml.append("</td>");
				    }
				    
				    if (anum == 0) {
                        perPointList.add("##");
                    }
				}
				
			}
			
			 if("True".equalsIgnoreCase(ScoreShowRelatePlan))
			    {
			    	LazyDynaBean abean=(LazyDynaBean)objRelatePlanValue.get(temp[0]);
			    	ArrayList planlist = loadxml.getRelatePlanValue("Plan");
			    	LazyDynaBean abean0=null;
			    	for(int i=0;i<planlist.size();i++)
			    	{
			    		abean0=(LazyDynaBean)planlist.get(i);
			    		String id=(String)abean0.get("id");
			    		String Name=(String)abean0.get("Name"); 			
			    		 
			    		
			    		bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
						bodyHtml.append(PubFunc.round(String.valueOf(this.columnWidth/1.5),0));
						bodyHtml.append("'  nowrap >");
						
						bodyHtml.append((String)abean.get("G_"+id));
						  
						bodyHtml.append("</td>");
			    	}
			    }
			
			
			ArrayList tempList = new ArrayList(); // 指标标度值
			String[] a_temp = null;
			if (pointGradeList.size() > 0)
			{
			    a_temp = (String[]) pointGradeList.get(0);
			    tempList.add(a_temp);
			} else
			{
			    a_temp = new String[] { "", "", "", "", "", "" };
			}
			String point_id = a_temp[1];
			String point_kind = a_temp[3];
			String pointID = a_temp[1];
			int num = 0;
	
			for (int i = 1; i < pointGradeList.size(); i++)
			{
	
			    String[] temp1 = (String[]) pointGradeList.get(i);
	
			    if (point_id.equalsIgnoreCase(temp1[1]))
			    {
					point_kind = temp1[3]; // 要素类型 0:定性要点；1:定量要点
					tempList.add(temp1);
					pointID = temp1[1];
			    } else
			    {
					if (anum == 0)
					{
						points.append("/p" + pointID);
						perPointList.add(pointID);
					}
						
					bodyHtml.append(getPointTD(tempList, point_kind, temp, objectResultMap, status, map, pointID, scoreflag, a_perPointList, num));
					tempList.clear();
					point_id = temp1[1];
					pointID = temp1[1];
					tempList.add(temp1);
					num++;
			    }
			}
			
			if(pointGradeList.size() > 0)
			{
				if (anum == 0)
				{
					points.append("/p" + pointID);
					perPointList.add(pointID);
				}
				bodyHtml.append(getPointTD(tempList, point_kind, temp, objectResultMap, status, map, pointID, scoreflag, a_perPointList, num));
			}
			/* 是否有了解程度 */
			String select_id = " ";
			if ("true".equalsIgnoreCase(this.NodeKnowDegree))
			{
			    num++;
			    if (objectResultMap != null && objectResultMap.get("know_id") != null) {
                    select_id = (String) objectResultMap.get("know_id");
                }
			    bodyHtml.append(getExtendTd(nodeKnowDegreeList, temp[0], select_id, temp, num));
			    if (anum == 0)
				{
			    	points.append("/per_know");
			    	perPointList.add("##");
				}
			}
			/* 是否有总体评价 */
			select_id = " ";
			if ("true".equalsIgnoreCase(this.WholeEval) || "True".equalsIgnoreCase(this.DescriptiveWholeEval))
			{
				if("0".equals(this.WholeEvalMode)){
				    num++;
				    if (objectResultMap != null && objectResultMap.get("whole_grade_id") != null) {
                        select_id = (String) objectResultMap.get("whole_grade_id");
                    }
				    bodyHtml.append(getExtendTd2(wholeEvalList, temp[0], select_id, temp, num, String.valueOf(plan_id), mainBodyID,no));
				    if (anum == 0 && "True".equalsIgnoreCase(this.WholeEval))
				    {
				    	points.append("/per_degreedesc");
				    	perPointList.add("##");
				    }
				    else if(anum == 0) {
                        perPointList.add("##");
                    }
				} 
				else if("1".equals(this.WholeEvalMode)){
					String topStr = "";
					String wholeEvalScore = "";
					String tempstatus = "";
				    topStr = " select whole_score,status from per_mainbody where plan_id="+plan_id+" and object_id='"+temp[0]+"' and mainbody_id='"+mainBodyID+"' " ;

					RowSet rowSet = null;
					rowSet=dao.search(topStr);
					if(rowSet.next())
					{
						wholeEvalScore = rowSet.getString("whole_score");
						if(wholeEvalScore==null || "".equals(wholeEvalScore)) {
                            wholeEvalScore = "0";
                        }
						tempstatus = rowSet.getString("status");
					}
					String tempScore = PubFunc.round(wholeEvalScore, Integer.parseInt(KeepDecimal));
					//String tempScore = wholeEvalScore;
                	if("0".equals(tempstatus)) {
                        tempScore = "";
                    }
					bodyHtml.append("<td width=\""+this.columnWidth+"\" class='RecordRow common_border_color'>&nbsp;&nbsp;&nbsp;<input type='text' id='wholeEvalScoreId_"+temp[0]+"' value='"+tempScore+"'  style='width: 60.0px' name='wholeEvalScore_"+temp[0]+"' ");
					if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2])) {
                        bodyHtml.append(" disabled='true'");
                    }
					bodyHtml.append(" />");
					if (!"1".equals(this.performanceType) && "True".equalsIgnoreCase(this.DescriptiveWholeEval))
					{
						String object_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0]));
						String mainbody_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainBodyID));
						bodyHtml.append("&nbsp;&nbsp;<img src=\"/images/table.gif\"  style=\"cursor:hand\"  onclick=\"javascript:showWindow('" + plan_id + "','" + object_id + "','" + mainbody_id + "')\" ></TD>");
					}
					if(anum == 0) {
                        perPointList.add("##");
                    }
				}
			}
			//k++;
			if(!(SystemConfig.getPropertyValue("goalAtFirst")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("goalAtFirst").trim())))
			{
				// 个人目标作为评分标准
				if ("True".equalsIgnoreCase(this.noteIdioGoal))
				{
				   // if (this.planVo.getInt("object_type") == 2)
				    {
					bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
					bodyHtml.append((this.columnWidth));
					bodyHtml.append("'  nowrap >");
					if ("False".equalsIgnoreCase(relatingTargetCard) || "1".equalsIgnoreCase(relatingTargetCard))
					{
		
					    bodyHtml.append("<a href='/selfservice/performance/view_summary.do?b_query=link&planNum=");
					    bodyHtml.append(plan_id);
					    bodyHtml.append("&objectId=");
					    bodyHtml.append("~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])));
					    if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4) {
                            bodyHtml.append("&optUrl=goal2' ");
                        } else {
                            bodyHtml.append("&optUrl=goal' ");
                        }
					    bodyHtml.append(" target='_blank'>");
					    bodyHtml.append(ResourceFactory.getProperty("lable.performance.perGoal"));//绩效目标
					    bodyHtml.append("</a>");
		
					} else
					{
					    bodyHtml.append(getGoalCardUrl(String.valueOf(plan_id), temp[0]));
					}
					bodyHtml.append("</td>");
				    }
				    if (anum == 0) {
                        perPointList.add("##");
                    }
				    
				}
				/* 个人总结评价作为评分标准 */
				if ("True".equals(this.SummaryFlag))
				{
				   // if (this.planVo.getInt("object_type") == 2)
				    {
					bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
					bodyHtml.append((this.columnWidth));
					bodyHtml.append("'  nowrap >");
					bodyHtml.append("<a href='/selfservice/performance/view_summary.do?b_query=link&planNum=");
					bodyHtml.append(plan_id);
					bodyHtml.append("&objectId=");
					bodyHtml.append("~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])));
					if(this.planVo.getInt("object_type")==1||this.planVo.getInt("object_type")==3||this.planVo.getInt("object_type")==4) {
                        bodyHtml.append("&optUrl=summary2' ");
                    } else {
                        bodyHtml.append("&optUrl=summary' ");
                    }
					
					bodyHtml.append(" target='_blank'>");
					if ("0".equals(this.performanceType))
					{
					    String info = SystemConfig.getPropertyValue("per_examineInfo");
					    if (info == null || info.length() == 0) {
                            bodyHtml.append(ResourceFactory.getProperty("lable.performance.perSummary"));//绩效报告
                        } else {
                            bodyHtml.append(new String(info.getBytes("ISO-8859-1"), "GBK"));
                        }
		
					} else if ("1".equals(this.performanceType)) {
                        bodyHtml.append(ResourceFactory.getProperty("label.reportwork.report"));
                    }
					bodyHtml.append("</a>");
					bodyHtml.append("</td>");
				    }
				    
				    if (anum == 0) {
                        perPointList.add("##");
                    }
				}
			}
			
			
			
			//显示员工日志 2011-01-24
			if("True".equalsIgnoreCase(this.ShowEmployeeRecord))
			{
				
				String a0100=getA0100(temp[0],String.valueOf(plan_id));
				a0100=SafeCode.encode(PubFunc.convertTo64Base(a0100));
		    	String desc="查看日志";
		    	 
				bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
				bodyHtml.append(columnWidth);
				bodyHtml.append("'  nowrap >");
				if(a0100.length()>0&&this.showDayWeekMonth !=null&&this.showDayWeekMonth.trim().length()>0) {
                    bodyHtml.append("<a href='javascript:showWordDiary(\""+String.valueOf(plan_id)+"\",\""+a0100+"\",\""+(String)timeBean.get("start_date")+"\",\""+(String)timeBean.get("end_date")+"\")'   >"+desc+"</a>");
                } else {
                    bodyHtml.append("&nbsp;");
                }
				bodyHtml.append("</td>");
				
				if (anum == 0) {
                    perPointList.add("##");
                }
			}
//			//自我评分
//			if (this.isSelfScoreColumn)
//			{
//			  //  if (this.planVo.getInt("object_type") == 2)
//			    {
//				bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
//				bodyHtml.append(this.columnWidth_lower);
//				bodyHtml.append("'  nowrap >");
//	 
//				if (objectSelfScoreMap.get(temp[0]) != null && ((String)objectSelfScoreMap.get(temp[0])).equals("1")&&!temp[0].equalsIgnoreCase(mainBodyID))
//				{
//				    bodyHtml.append("<a href='/selfservice/performance/batchGrade.do?b_objectScore=query&object_id=" + "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])) + "&plan_id=" + this.planid + "' target='_blank'>");
//				    bodyHtml.append("<img border='0' src='/images/table.gif' />");
//				    bodyHtml.append("</a>");
//				} else
//				{
//				    bodyHtml.append("&nbsp;");
//				}
//				bodyHtml.append("</td>");
//				
//				if (anum == 0)
//			    	perPointList.add("##");
//	
//			    }
//			}
			
			// 处理多人评分 是否显示下属评分列
		    if (isAllowSeeLowerGrade||this.isSelfScoreColumn)
			{
				Map<Integer,String> mainBodyMap = this.getMainBodyMap();
		    	if(mainBodyMap.size()>0){
		    		String KeepDecimal=(String)htxml.get("KeepDecimal");  //保留的小数位	
					//插入主体html
					
					Iterator iter = mainBodyMap.entrySet().iterator();
					HashMap tempMap=null ;
					if(mainBodydata.containsKey(temp[0])) {
                        tempMap=(HashMap) mainBodydata.get(temp[0]);
                    }
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						//不显示自我评分时跳过
						if("5".equals(key)&&!this.isSelfScoreColumn){
                            continue;
                        }
                        //不现实下属打分是跳过
                        if(!"5".equals(key) && !isAllowSeeLowerGrade){
						    continue;
                        }
						bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
						bodyHtml.append(this.columnWidth_lower+30);
						bodyHtml.append("'  nowrap >");
						if(tempMap!=null&&tempMap.containsKey(key)){
							if("5".equals(key)&&this.isSelfScoreColumn){//自我评分
								bodyHtml.append("<a href='/selfservice/performance/batchGrade.do?b_objectScore=query&object_id=" + "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0])) + "&plan_id=" + this.planid + "' target='_blank'>");
							    bodyHtml.append( PubFunc.round(tempMap.get(key).toString(), Integer.parseInt(KeepDecimal)));
							    bodyHtml.append("</a>");
							}else if(!"5".equals(key)){
								bodyHtml.append("<a href='/selfservice/performance/batchGrade.do?b_lowerScore=query&object_id=" + PubFunc.encryption(temp[0]) + "&plan_id=" + PubFunc.encryption(this.planid) + "&body_id="+key+" ' target='_blank'>");
								bodyHtml.append(PubFunc.round(tempMap.get(key).toString(), Integer.parseInt(KeepDecimal)));
								bodyHtml.append("</a>");
							}
						}else{
							bodyHtml.append("&nbsp");
						}
						bodyHtml.append("</td>");
                        if (anum == 0) {
                            perPointList.add("##");
                        }

					}

				}
//		    	if(this.haveLower)
//		    	{
//					bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
//					bodyHtml.append(this.columnWidth_lower);
//					bodyHtml.append("'  nowrap >");
//
//					if (this.objectsLowerMap.get(temp[0]) != null && ((ArrayList)this.objectsLowerMap.get(temp[0])).size()>0)
//					{
//					    bodyHtml.append("<a href='/selfservice/performance/batchGrade.do?b_lowerScore=query&object_id=" + PubFunc.encryption(temp[0]) + "&plan_id=" + PubFunc.encryption(this.planid) + "' target='_blank'>");
//					    bodyHtml.append("<img border='0' src='/images/table.gif' />");
//					    bodyHtml.append("</a>");
//					} else
//					{
//					    bodyHtml.append("&nbsp;");
//					}
//					bodyHtml.append("</td>");
//
//					if (anum == 0)
//				    	perPointList.add("##");
//		    	}
			}

			if (showNoMarking != null && "true".equalsIgnoreCase(showNoMarking))
			{
			    String tempPid = "~"+SafeCode.encode(PubFunc.convertTo64Base(String.valueOf(plan_id)));
		        String tempOid = "~"+SafeCode.encode(PubFunc.convertTo64Base(temp[0]));
		        String tempUid = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainBodyID));
			    bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
			    bodyHtml.append(columnWidth);
			    bodyHtml.append("'  nowrap >");

			    bodyHtml.append(" <table ><tr><td width='5' nowrap  valign='bottom' >    <input type='checkbox' tile='sdfasdf' onclick='setStatus(this)'   name='b" + temp[0] + "_" + plan_id + "_"
				    + mainBodyID + "' ");
			    String display_desc = "block";
			    if ("4".equals(temp[2]) || "7".equals(temp[2]))
			    {
				bodyHtml.append(" checked ");
				display_desc = "block";
			    }
			    if ("1".equals(temp[3])) // 必打分项
			    {
				bodyHtml.append(" style='display:none' ");
			    }
			    if ("2".equals(temp[2]) || "7".equals(temp[2])) {
                    bodyHtml.append(" disabled ");
                }
			    bodyHtml.append(" >  </td><td width='70'  valign='bottom' nowrap >  ");
			    bodyHtml.append("<div style='display:" + display_desc + "' id='b" + temp[0] + "' >");
			    bodyHtml.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&type=0&status=" + temp[2] + "&planID=" + tempPid
				    + "&objectID=" + tempOid + "&mainbodyID=" + tempUid);
			    bodyHtml.append("')\" >");
			    if ("0".equals(this.performanceType)) {
                    bodyHtml.append(ResourceFactory.getProperty("performance.batchgrade.donotSubed"));
                } else if ("1".equals(this.performanceType)) {
                    bodyHtml.append(ResourceFactory.getProperty("performance.batchgrade.forfeit"));
                }
			    bodyHtml.append("</a>");
			    bodyHtml.append("</div></td></tr></table>");

			    bodyHtml.append(" </td>");

			    if (anum == 0) {
                    perPointList.add("##");
                }
			}

			if ("1".equals(this.performanceType))
			{
			    bodyHtml.append("<td class='RecordRow common_border_color' align='center'  width='");
			    bodyHtml.append(columnWidth);
			    bodyHtml.append("'  nowrap >");
			    bodyHtml.append("<a  href=\"javascript:windowOpen('/performance/markStatus/markStatusList.do?b_edit2=edit&operater=3&type=1&status=" + temp[2] + "&planID=" + plan_id
				    + "&objectID=" + temp[0] + "&mainbodyID=" + mainBodyID);
			    bodyHtml.append("')\" >");
			    bodyHtml.append(ResourceFactory.getProperty("kh.field.helpcontent"));
			    bodyHtml.append("</a>");
			    bodyHtml.append(" </td>");

			    if (anum == 0) {
                    perPointList.add("##");
                }
			}

			// 北京市监狱局要求把总分列和排名列放到姓名后面 JinChunhai 2012.09.13
			if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
			{}
			else
			{
				// 总分
				if ("true".equalsIgnoreCase(this.isShowTotalScore))
				{
				    String score_order = "";
				    //if (this.isAutoCountTotalOrder.equalsIgnoreCase("true"))
					score_order = (String) objectTotalScoreMap.get(temp[0]);
				    //else
					//score_order = "*/*";
				    bodyHtml.append("<td class='RecordRow common_border_color' ");
				    bodyHtml.append(" align='center' id='zf" + no + "'  width='");
				    bodyHtml.append(this.columnWidth_lower);
				    bodyHtml.append("'  nowrap >");
				    bodyHtml.append(score_order.split("/")[0]);
				    bodyHtml.append("</td>");

				    if (anum == 0) {
                        perPointList.add("##");
                    }
				}
				// 排名
				if ("true".equalsIgnoreCase(this.isShowOrder))
				{
				    String score_order = "";
				    //if (this.isAutoCountTotalOrder.equalsIgnoreCase("true"))
					score_order = (String) objectTotalScoreMap.get(temp[0]);
				    //else
					//score_order = "*/*";

				    bodyHtml.append("<td class='RecordRow common_border_color' align='center'  id='pm" + no + "'   width='");
				    bodyHtml.append(this.columnWidth_lower);
				    bodyHtml.append("'  nowrap >");
				    bodyHtml.append(score_order.split("/")[1]);
				    bodyHtml.append("</td>");

				    if (anum == 0) {
                        perPointList.add("##");
                    }

				}
			}
			// 最终评估结果。层级为2层或者三层时显示，与列头显示规则一致 chent 20171220 modify
			if(this.userView.hasTheFunction("06060103") && (lays == 2 || lays == 3))
			{
				LazyDynaBean valueBean = null;
				String score = "";
				String resultdesc = "";
				String exX_object = "";
				String ordering = "";
	    	    LoadXml  loadxml1 = new LoadXml(this.conn, String.valueOf(plan_id));
	    	    Hashtable htxml1 = new Hashtable();
	    	    htxml1 = loadxml1.getDegreeWhole();
				String deviationScoreUsed=(String) htxml1.get("deviationScoreUsed");//是否使用纠偏总分 0不是  1是  zzk
				String total_score="score";
				if("1".equals(deviationScoreUsed)){
					total_score="reviseScore";
				}
				if(objectsResultMap.get(temp[0])!=null)
				{
					valueBean = (LazyDynaBean)objectsResultMap.get(temp[0]);
					if(valueBean!=null)
					{
						score = (String)valueBean.get(total_score);
						resultdesc = (String)valueBean.get("resultdesc");
						exX_object = (String)valueBean.get("exX_object");
						ordering = (String)valueBean.get("ordering");
					}
				}
			    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+this.columnWidth_lower+"' nowrap >");
			    bodyHtml.append(score + "</td>");
			    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+(this.columnWidth_lower+20)+"' nowrap >");
			    bodyHtml.append(resultdesc + "</td>");
			//  bodyHtml.append("<td class='RecordRow' align='center' width='80' nowrap >");
			//  bodyHtml.append("<font color='#2E67B9' >" + exX_object + "</font> </td>");
			    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+this.columnWidth_lower+"' nowrap >");
			    bodyHtml.append(ordering + "</td>");

			    if (anum == 0)
			    {
			    	perPointList.add("##");
			    	perPointList.add("##");
			    //	perPointList.add("##");
			    	perPointList.add("##");
			    }
			}
			if(anum == 0 && this.basicFieldList!=null && this.basicFieldList.size()>0) {
                perPointList.add("##");
            }

			no++;
			bodyHtml.append("</tr> \n ");
			this.script_code.append("};");
			anum++;
		}



	    //增加指标分值合计行 混合打分
 	    if("2".equals(this.scoreflag))
	    {

		    if("true".equalsIgnoreCase(this.ShowSumRow))
		    {
		    	ArrayList planlist = loadxml.getRelatePlanValue("Plan");
		    	int n =  0;
		    	if("True".equalsIgnoreCase(ScoreShowRelatePlan)){//显示引入计划评分
		    		n = this.basicFieldList.size()+planlist.size()+2;
		    		if(this.basicFieldList!=null && this.basicFieldList.size()>0) {
			    		n = this.basicFieldList.size()+planlist.size()+2;
			    	}
		    	} else{
		    		n = this.basicFieldList.size()+2;
		    		if(this.basicFieldList!=null && this.basicFieldList.size()>0) {
			    		n = this.basicFieldList.size()+2;
			    	}
		    	}
		    	bodyHtml.append("\r\n   <tr><td id='a' colspan='2'  width='");
				bodyHtml.append(columnWidth);
				bodyHtml.append("' align='center'  class='cell_lockedLast common_border_color'  >");
				bodyHtml.append("<font  class='fontStyle_self'   >");
				bodyHtml.append("合计");
				bodyHtml.append("</font>");
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) //中国联通
				{
					if(objectList.size()>0)
					{
						String _value="100";
						if(SystemConfig.getPropertyValue("totalRowValue")!=null&&SystemConfig.getPropertyValue("totalRowValue").trim().length()>0) {
                            _value=SystemConfig.getPropertyValue("totalRowValue").trim();
                        }
						String avg=PubFunc.divide(_value,String.valueOf(objectList.size()),1);
						bodyHtml.append("<br>("+ResourceFactory.getProperty("performance.batchgrade.avgPoint")+":"+avg+")");
					}
				}
				bodyHtml.append("</td>");
				if(n>2)
				{
					//haosl 2018-3-7 此处之前不知道为什么被注释掉了，导致合计行的列错位 bug 34972
					if("2".equals(object_type))//人员
                    {
                        bodyHtml.append("<td id='a' colspan='" + (n-3) + "'  width='");
                    } else//团队
                    {
                        bodyHtml.append("<td id='a' colspan='" + (n-2) + "'  width='");
                    }
					bodyHtml.append(columnWidth);
					if("True".equalsIgnoreCase(this.LockMGradeColumn)) //锁定指标列
                    {
                        bodyHtml.append("' align='center'  class='cell_lockedLast common_border_color'  >");
                    } else {
                        bodyHtml.append("' align='center'  class='RecordRow common_border_color'  >");
                    }
					bodyHtml.append(" &nbsp;</td>");
				}

				String temp="";
				int pointNum = perPointList.size();
				if(n>2) {
                    pointNum = perPointList.size()-1;
                }
				for(int j=0;j<pointNum;j++)
				{
					temp=(String)perPointList.get(j);

					 bodyHtml.append("<td class='RecordRow common_border_color' align='left'  id='sum_p" + temp+ "'   width='");
					 bodyHtml.append("");
					 bodyHtml.append("'  nowrap >");

					 bodyHtml.append("</td>");
				}
				//此处和几行的列已经加过了，每加的列是查看下级打分的列 haosl 2019-7-1
				// 最终评估结果。层级为2层时显示，与列头显示规则一致 chent 20171220 modify
				/*if(this.userView.hasTheFunction("06060103") && (lays == 2 || lays == 3)) {
				    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+this.columnWidth_lower+"' nowrap >");
				    bodyHtml.append("<font color='#2E67B9' ></font> </td>");
				    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+(this.columnWidth_lower+20)+"' nowrap >");
				    bodyHtml.append("<font color='#2E67B9' ></font> </td>");
				    bodyHtml.append("<td class='RecordRow common_border_color' align='center' width='"+this.columnWidth_lower+"' nowrap >");
				    bodyHtml.append("<font color='#2E67B9' ></font> </td>");
				}*/
		    }
	    }


	    // write script_code;
	    if (users.length() > 0)
	    {
			this.script_code.append("\r\n for( var i=0 ; i<obj_result.length ; i++){");
			this.script_code.append("\r\n obj_result[\"_\"+obj_result[i].objectid]=obj_result[i];");
			this.script_code.append("\r\n 	}");

			this.script_code.append("\r\n var users=\"" + users.substring(1) + "\"");
			String _str=points.toString();
			if(_str.length()>0) {
                _str=_str.substring(1);
            }
			this.script_code.append("\r\n var points=\"" + _str + "\"");
			this.script_code.append("\r\n var obj_values=new Array();");
			this.script_code.append("\r\n var user_arr=users.split(\"/\")");
			this.script_code.append("\r\n var point_arr=points.split(\"/\");");

			if(_str.trim().length()>0)
			{
				this.script_code.append("\r\n for(var i=0;i<user_arr.length;i++)");
				this.script_code.append("\r\n { ");
				this.script_code.append("\r\n 	var tempArray=new Array();");
				this.script_code.append("\r\n 	tempArray[\"objectid\"]=user_arr[i];");
				this.script_code.append("\r\n 	var temp_arr=obj_result[\"_\"+user_arr[i]];");
				this.script_code.append("\r\n 	for(var j=0;j<point_arr.length;j++)");
				this.script_code.append("\r\n   {");
				this.script_code.append("\r\n       tempArray[point_arr[j]]=temp_arr[point_arr[j]].split(\"/\")[0];");
				this.script_code.append("\r\n   }");
				this.script_code.append("\r\n 	obj_values[\"_\"+user_arr[i]]=tempArray;");
				this.script_code.append("\r\n }");
			}

//			增加指标分值合计行 混合打分
		   if("2".equals(this.scoreflag))
		    {
		    	if("true".equalsIgnoreCase(this.ShowSumRow))
			    {
			    	this.script_code.append("\r\n 	for(var j=0;j<point_arr.length;j++)");
					this.script_code.append("\r\n   {");


					this.script_code.append("\r\n       if(point_arr[j]!='per_know'&&point_arr[j]!='per_degreedesc'){");

					this.script_code.append("\r\n   		var sum=0;");
					this.script_code.append("\r\n           for(var i=0;i<user_arr.length;i++){");
					this.script_code.append("\r\n              if(obj_result[\"_\"+user_arr[i]][point_arr[j]].split(\"/\")[0]!='null') ");
					this.script_code.append("\r\n                 	sum+=obj_result[\"_\"+user_arr[i]][point_arr[j]].split(\"/\")[0]*1; ");
					this.script_code.append("\r\n            }   if(sum!=0){ document.getElementById('sum_'+point_arr[j]).innerHTML=cheng(sum,2);}  } ");
					this.script_code.append("\r\n   }");
			    }
		    }




		this.auto_createTable = "true";
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return "<tbody>" + bodyHtml.toString() + "</tbody>";
    }


    /**
     * 获取所有考核主体类别对考核对象打分的平均分
     * @return
     * @author zhanghua
     * @date 2017年9月12日 下午5:15:56
     */
    private HashMap getmainBodyScoreMap(String mainbodyid,boolean isAllowSeeLowerGrade){
    	RowSet rs=null;
    	HashMap<String,HashMap> mainBodyScoreMap=new HashMap<String, HashMap>();
    	try{

    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer strSql=new StringBuffer();
    		String cloumn="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                cloumn="level_o";
            }
    		strSql.append("select t1.object_id,mainset.body_id,sum(t1.score)/count(1) as score from per_mainbody t1 inner join ");
    		strSql.append("per_mainbodyset mainset on t1.body_id=mainset.body_id inner join ");
    		strSql.append("(select object_id,per_mainbodyset.body_id,per_mainbodyset."+cloumn+" from per_mainbody inner join ");
    		strSql.append("per_mainbodyset on per_mainbody.body_id=per_mainbodyset.body_id where mainbody_id='"+mainbodyid+"' and plan_id="+this.planid+") t2 on ");
			//此处不限制只查下级评分了，因为调用的地方有自己的判断，如果这里加上限制，
			// 查不到“允许查看考核对象自评（所有考核主体）”这种情况的得分，导致“一般”级别的主体没法看见员工自评
    		/*if(isAllowSeeLowerGrade)
    			strSql.append(" mainset."+cloumn+">t2."+cloumn);
    		else
    			strSql.append(" mainset."+cloumn+"=5 ");*/
    		strSql.append(" t1.object_id=t2.object_id where t1.status=2 and t1.plan_id="+this.planid+" group by mainset.body_id,t1.object_id ");

    		rs=dao.search(strSql.toString());

    		while(rs.next()){
    			String object_id=rs.getString("object_id");
    			if(org.apache.commons.lang.StringUtils.isBlank(object_id)) {
                    continue;
                }

    			if(mainBodyScoreMap.containsKey(object_id)){
    				mainBodyScoreMap.get(object_id).put(rs.getString("body_id"), rs.getString("score"));
    			}else{
    				HashMap<String,Float> tempMap=new HashMap<String,Float>();
    				tempMap.put(rs.getString("body_id"), rs.getFloat("score"));
    				mainBodyScoreMap.put(object_id, tempMap);
    			}
    		}

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return mainBodyScoreMap;
    }


    //如果计划考核对象类型为团队，需得到负责人
    public String getA0100(String object_id,String plan_id)
    {
    	String a0100=object_id;
    	RowSet rowSet=null;
    	try
    	{
    		 ContentDAO dao = new ContentDAO(this.conn);
    		if(this.planVo==null) {
                this.planVo=getPlanVo(plan_id);
            }
    		if(this.planVo.getInt("object_type")!=2)
    		{
    			String sql="select pmb.mainbody_id from  per_mainbody pmb,per_mainbodyset pms where "
    				      +" pmb.plan_id="+plan_id+" and pmb.object_id='"+object_id+"' and pmb.body_id=pms.body_id and  ";
    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql+=" pms.level_o=5 ";
                } else {
                    sql+=" pms.level=5 ";
                }
    			rowSet=dao.search(sql);
    			if(rowSet.next()) {
                    a0100=rowSet.getString("mainbody_id");
                } else {
                    return "";
                }

    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		try
    		{
    			if(rowSet!=null) {
                    rowSet.close();
                }
    		}
    		catch(Exception ee)
    		{

    		}
    	}
    	return "Usr"+a0100;
    }


    //获得当前计划的起始时间和结束时间
    public LazyDynaBean getPlanKhTime()
    {
    	LazyDynaBean abean=new LazyDynaBean();
    	Calendar calendar = Calendar.getInstance();
    	try
    	{
    		this.planVo=getPlanVo(this.planid);
    		int cycle=this.planVo.getInt("cycle");
    		String theyear=this.planVo.getString("theyear");
    		String themonth=this.planVo.getString("themonth");
    		String thequarter=this.planVo.getString("thequarter");
    		if (cycle == 0) // 年度
    		{
    			abean.set("start_date", theyear+"-01-01");
    			calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
        		calendar.set(Calendar.MONTH, 11);
        		calendar.set(Calendar.DATE, 1);
        		int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    			abean.set("end_date", theyear+"-12-"+day);
    		}
    		else if (cycle == 1) // 半年
    		{
    			 if ("01".equals(thequarter)|| "1".equals(thequarter))
    			 {
    				abean.set("start_date", theyear+"-01-01");

    				calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
            		calendar.set(Calendar.MONTH, 5);
            		calendar.set(Calendar.DATE, 1);
            		int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
 	    			abean.set("end_date", theyear+"-06-"+day);
    			 }
    			 else
    			 {
	    			abean.set("start_date", theyear+"-07-01");
	    			calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
	        		calendar.set(Calendar.MONTH, 11);
	        		calendar.set(Calendar.DATE, 1);
	        		int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    			abean.set("end_date", theyear+"-12-"+day);
    			 }
    		}
    		else if (cycle == 2) // 季度
    		{
    			 if ("01".equals(thequarter)|| "1".equals(thequarter))
    			 {
    				 abean.set("start_date", theyear+"-01-01");

    				 calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
    				 calendar.set(Calendar.MONTH, 2);
    				 calendar.set(Calendar.DATE, 1);
    				 int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    				 abean.set("end_date", theyear+"-03-"+day);
    			  } else if ("02".equals(thequarter)|| "2".equals(thequarter))
    			  {
    				  abean.set("start_date", theyear+"-04-01");

    				  calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
    	        	  calendar.set(Calendar.MONTH, 5);
    	        	  calendar.set(Calendar.DATE, 1);
    	        	  int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	    		  abean.set("end_date", theyear+"-06-"+day);

    			  } else if ("03".equals(thequarter)|| "3".equals(thequarter))
    			  {
    				  abean.set("start_date", theyear+"-07-01");

    				  calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
    	        	  calendar.set(Calendar.MONTH, 8);
    	        	  calendar.set(Calendar.DATE, 1);
    	        	  int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	    		  abean.set("end_date", theyear+"-09-"+day);
    			  } else if ("04".equals(thequarter)|| "4".equals(thequarter))
    			  {
    				  abean.set("start_date", theyear+"-10-01");
    				  calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
    	        	  calendar.set(Calendar.MONTH, 11);
    	        	  calendar.set(Calendar.DATE, 1);
    	        	  int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	    		  abean.set("end_date", theyear+"-12-"+day);
    			  }
    		}
    		else if (cycle == 3) // 月
    		{
    			 if(themonth.length()==1) {
                     themonth="0"+themonth;
                 }
    			 abean.set("start_date", theyear+"-"+themonth+"-01");

    			 calendar.set(Calendar.YEAR,Integer.parseInt(theyear));
	        	 calendar.set(Calendar.MONTH,(Integer.parseInt(themonth)-1));
	        	 calendar.set(Calendar.DATE, 1);
	        	 int day=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    	     abean.set("end_date", theyear+"-"+themonth+"-"+day);
    		}
    		else if (cycle == 7)
    		{
    			Date startDate=this.planVo.getDate("start_date");
    			Date endDate=this.planVo.getDate("end_date");
    			abean.set("start_date",PubFunc.FormatDate(startDate,"yyyy-MM-dd"));
    			abean.set("end_date",PubFunc.FormatDate(endDate,"yyyy-MM-dd"));

    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return abean;
    }



    /**
         * 取得对象关联的目标卡
         *
         * @param planid
         * @param object_id
         * @return
         */
    public String getGoalCardUrl(String planid, String object_id)
    {

    	StringBuffer str = new StringBuffer("");
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rowSet = dao.search("select * from per_plan where plan_id=" + planid);
    		int cycle = 0;
    		String theyear = "";
    		String themonth = "";
    		String thequarter = "";
    		if (rowSet.next())
    		{
				cycle = rowSet.getInt("cycle");
				theyear = rowSet.getString("theyear");
				themonth = rowSet.getString("themonth");
				thequarter = rowSet.getString("thequarter");
		    }

    		StringBuffer sql = new StringBuffer("");
		    if (cycle != 7)
			{
		    //	sql.append("select distinct per_plan.plan_id from per_plan,per_object,per_mainbody ");
		    	sql.append("select per_plan.plan_id,per_object.sp_flag posp_flag,per_mainbody.sp_flag pmsp_flag from per_plan,per_object,per_mainbody ");
		    	sql.append(" where per_plan.plan_id=per_object.plan_id and per_object.object_id=per_mainbody.object_id ");
		    	sql.append(" and per_plan.plan_id=per_mainbody.plan_id and method=2 ");
		    //	sql.append(" and ( per_object.sp_flag='03' or per_object.sp_flag='06' ) and (per_mainbody.sp_flag='03') ");
		    	sql.append(" and per_object.object_id='"+object_id+"' ");

		    	if(relatingTargetCard!=null && relatingTargetCard.trim().length()>0 && "3".equalsIgnoreCase(relatingTargetCard))
				{
		    		sql.append(" and per_mainbody.mainbody_id = '"+ this.userView.getA0100()+"' ");
			    	sql.append(" and (per_mainbody.status is not null and per_mainbody.status<>'0' )");
				}
		    	else if(relatingTargetCard!=null && relatingTargetCard.trim().length()>0 && "2".equals(relatingTargetCard) && "1".equals(showYPTargetCard))//如果是 查看对象目标卡，则要保证考核对象已经自评
				{
					if("2".equals(object_type)){//如果是人员
					    if(Sql_switcher.searchDbServer()!=2) {//如果不是oracle库
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level='5') and per_mainbody.object_id=per_mainbody.mainbody_id)");//自评
					    } else {
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level_o='5') and per_mainbody.object_id=per_mainbody.mainbody_id)");//自评
					    }
					}else{//如果是团队
					    if(Sql_switcher.searchDbServer()!=2) {//如果不是oracle库
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level='5') and per_mainbody.object_id<>per_mainbody.mainbody_id )");//自评
					    } else {
					        sql.append(" and (per_mainbody.body_id in (select body_id from per_mainbodyset where level_o='5') and per_mainbody.object_id<>per_mainbody.mainbody_id )");//自评
					    }
					}

					sql.append(" and (per_mainbody.status is not null and per_mainbody.status='2' )");//提交了
				}

		    	if(cycle==0)  //年度
				{
					sql.append(" and theyear='"+theyear+"' ");
				}
		    	else if(cycle==1)  //半年
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and ( ( cycle=1 and Thequarter='"+thequarter+"' ) ");
					if("01".equals(thequarter)|| "1".equals(thequarter))
					{
						sql.append(" or (cycle=2 and ( Thequarter='01' or Thequarter='02') ) ");
						sql.append(" or (cycle=3 and Themonth in ('01','02','03','04','05','06') ) ");
					}
					else
					{
						sql.append(" or (cycle=2 and ( Thequarter='03' or Thequarter='04') ) ");
						sql.append(" or (cycle=3 and Themonth in ('07','08','09','10','11','12') ) ");
					}
					sql.append(" )");
				}
		    	else if(cycle==2)  //季度
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and ( ( cycle=2 and Thequarter='"+thequarter+"' ) ");
					if("01".equals(thequarter)|| "1".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('01','02','03') ) ");
					}
					else if("02".equals(thequarter)|| "2".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('04','05','06') ) ");
					}
					else if("03".equals(thequarter)|| "3".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('07','08','09') ) ");
					}
					else if("04".equals(thequarter)|| "4".equals(thequarter))
					{
						sql.append(" or (cycle=3 and Themonth in ('10','11','12') ) ");
					}
					sql.append(" )");
				}
		    	else if(cycle==3)  //月
				{
					sql.append(" and theyear='"+theyear+"' ");
					sql.append(" and cycle=3 and Themonth='"+themonth+"' ");
				}
				rowSet = dao.search(sql.toString());
				HashMap planidMap = new HashMap();
				String plan_id = "";
				while (rowSet.next())
				{
					String pid = rowSet.getString("plan_id");
					String posp_flag = rowSet.getString("posp_flag");
					String pmsp_flag = rowSet.getString("pmsp_flag");
					LoadXml loadxml = new LoadXml(this.conn, pid);
					Hashtable htxml = loadxml.getDegreeWhole();
					String noApproveTargetCanScore = (String)htxml.get("NoApproveTargetCanScore"); // 目标卡未审批也允许打分 True, False, 默认为 False

					if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
					{
						if((posp_flag!=null && posp_flag.trim().length()>0 && ("03".equalsIgnoreCase(posp_flag) || "06".equalsIgnoreCase(posp_flag))))
						{}
						else {
                            continue;
                        }
					}

					if(planidMap.get(pid)==null) {
                        plan_id += "," + pid;
                    }
				    planidMap.put(pid,"1");
				}
				if (plan_id.length() > 0)
				{
				    str.append("<a href='javascript:openWin(\"/performance/objectiveManage/objectiveCard.do?b_query2=query&from=batchGrade&operator=1&planids=" + plan_id + "&relatingTargetCard=" + relatingTargetCard + "&object_id=" + "~"+SafeCode.encode(PubFunc.convertTo64Base(object_id)) + "\")' ");
				    str.append(" >");

				    String desc=ResourceFactory.getProperty("lable.performance.perGoal");
			    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) {
                        desc=ResourceFactory.getProperty("performance.batchgrade.info9");
                    }
				    str.append(desc);
				    str.append("</a>");
				}
			}

		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return str.toString();
    }

    /**
         * 得到 了解程度 或 总体评价的选项信息
         *
         * @param flag
         *                1:了解程度 2：总体评价
         * @param gradeClass
         *                等级分类ID
         * @return
         */
    public ArrayList getExtendInfoValue(String flag, String gradeClass) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    if ("1".equals(flag)) {
            rowSet = dao.search("select know_id,name from per_know where status=1 order by seq ");
        } else if ("2".equals(flag)) {
            rowSet = dao.search("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id=" + gradeClass);
        }
	    while (rowSet.next())
	    {
		String[] temp = new String[2];
		temp[0] = rowSet.getString(1);
		temp[1] = rowSet.getString(2);
		list.add(temp);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return list;
    }

    /**
         * 生成 扩展的评分选项
         *
         * @param list
         *                选项信息
         * @return
         */
    public String getExtendTd(ArrayList list, String userID, String selectid, String[] a_temp)
    {

	StringBuffer td = new StringBuffer("<td class='RecordRow' align='center'  width='" + columnWidth + "'  nowrap >");
	td.append("<table border='0'><tr><td width='30'> ");
	td.append("<select name='a" + userID + "' ");
	if ("2".equals(a_temp[2])) {
        td.append(" disabled='false'");
    }
	td.append(" >");
	for (int i = 0; i < list.size(); i++)
	{
	    String[] temp = (String[]) list.get(i);
	    td.append("<option value='");
	    td.append(temp[0]);
	    td.append("' ");
	    if (temp[0].equals(selectid)) {
            td.append("selected");
        }
	    td.append(" >");
	    td.append(temp[1]);
	    td.append("</option>");
	}
	td.append("</select>");
	td.append("</td></tr></table>    </td>");
	return td.toString();
    }

    /**
         * 生成 总体评价
         *
         * @param list
         *                选项信息
         * @return
         */
    public String getExtendTd2(ArrayList list, String userID, String selectid, String[] a_temp, int num, String planID, String mainbodyID, int objNum)
    {
	//(this.columnWidth_lower+30 总体评价适当加宽
	StringBuffer td = new StringBuffer("<td  class='RecordRow' align='center'  width='" + this.columnWidth + "'    nowrap > <table><TR>");
	StringBuffer per_degreedesc = new StringBuffer("");

	StringBuffer a_td = new StringBuffer("");
	boolean isValue = false;
	if ("True".equalsIgnoreCase(this.WholeEval))
	{
        td.append("<td>");
		if(this.totalAppFormula!=null && this.totalAppFormula.trim().length()>0)
		{
			a_td.append(" <input type='hidden' name='a" + userID + "' value='");
			String wholeVale = "";
			for(int i=0;i<list.size();i++)
    		{
    			String[] temp=(String[])list.get(i);
    			if(temp[0].equals(selectid))
    			{
    				a_td.append(temp[0]);
    				wholeVale = temp[1];
    				break;
    			}
    		}
			a_td.append("'> ");
			a_td.append(" <span id='totalAppValue" + objNum + "'> ");
			a_td.append(wholeVale);
			a_td.append("</span> ");
		}
		else
		{
		    a_td.append("<select name='a" + userID + "' onchange='setValue3(this,\"" + a_temp[0] + "\",\"per_degreedesc\")' ");
		    if ("2".equals(a_temp[2]) || "4".equals(a_temp[2]) || "7".equals(a_temp[2])) {
                a_td.append(" disabled='false'");
            }
		//  if(this.totalAppFormula!=null && this.totalAppFormula.trim().length()>0)
		//    	a_td.append(" disabled ");
		    a_td.append(" >");
	
		    a_td.append("<option value='null'></option>");
		    for (int i = 0; i < list.size(); i++)
		    {
				String[] temp = (String[]) list.get(i);
				a_td.append("<option value='");
				a_td.append(temp[0]);
				a_td.append("' ");
				if (temp[0].equals(selectid))
				{
				    a_td.append("selected");
				    per_degreedesc.append(temp[0]);
				    isValue = true;
				}
				a_td.append(" >");
				a_td.append(temp[1]);
				a_td.append("</option>");
		    }
		    a_td.append("</select>");
		}
	}

	td.append(a_td.toString());
	if (!isValue) {
        per_degreedesc.append("null");
    }
	if ("2".equals(a_temp[2]) || "4".equals(a_temp[2]) || "7".equals(a_temp[2]))
	{
	    per_degreedesc.append("/0");
	} else {
        per_degreedesc.append("/1");
    }
	if ("True".equalsIgnoreCase(this.WholeEval)) {
        td.append("&nbsp;</TD>");
    }

	if (!"1".equals(this.performanceType) && "True".equalsIgnoreCase(this.DescriptiveWholeEval))
	{
		String object_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(userID));
		String mainbody_id = "~"+SafeCode.encode(PubFunc.convertTo64Base(mainbodyID));
	    td.append("<TD><img src=\"/images/table.gif\"  style=\"cursor:hand\"  onclick=\"javascript:showWindow('" + planID + "','" + object_id + "','" + mainbody_id + "')\" ></TD>");
	}
	if ("True".equalsIgnoreCase(this.WholeEval)) {
        this.script_code.append(",per_degreedesc:\"" + per_degreedesc.toString() + "\"");
    }

	td.append("</TR></TABLE>  </td>");
	return td.toString();
    }

    /**
         * 生成 扩展的评分选项
         * 
         * @param list
         *                选项信息
         * @return
         */
    public String getExtendTd(ArrayList list, String userID, String selectid, String[] a_temp, int num)
    {

	StringBuffer td = new StringBuffer("<td   id='" + a_temp[0] + "_" + "per_know' class='RecordRow' onclick='showSetBox(this,\"per_know\",\"" + a_temp[0] + "\")'   align='center'  width='"
		+ columnWidth + "'    nowrap >");
	StringBuffer per_know = new StringBuffer("");
	boolean isValue = false;
	for (int i = 0; i < list.size(); i++)
	{
	    String[] temp = (String[]) list.get(i);
	    if (temp[0].equals(selectid))
	    {
		td.append(temp[1]);
		per_know.append(temp[0]);
		isValue = true;
	    }
	}
	if (!isValue) {
        per_know.append("null");
    }

	if ("2".equals(a_temp[2]) || "4".equals(a_temp[2]) || "7".equals(a_temp[2]))
	{
	    per_know.append("/0");
	} else {
        per_know.append("/1");
    }

	this.script_code.append(",per_know:\"" + per_know.toString() + "\"");
	td.append("   </td>");
	return td.toString();
    }

    /**
         * 根据关联子集里指标的数据 过滤定性指标
         * 
         * @param tempList
         * @param a_fieldItem
         *                控制上限指标
         * @param a_fieldItem_1
         *                控制下限指标
         * @param objectid
         * @param point_kind
         * @return
         */
    public ArrayList getFiltrateDate(ArrayList tempList, String a_fieldItem, String objectid, String point_kind, String a_fieldItem_1)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	String a_value_up = ""; // 上限值
	String a_value_down = ""; // 下限值
	try
	{
	    String temp = getup_down_value(a_fieldItem, a_fieldItem_1, objectid);
	    a_value_up = temp.split("/")[0].substring(1);
	    a_value_down = temp.split("/")[1].substring(1);

	    // 如果 下限值大于上限值 则返回空
	    if (a_value_up != null && a_value_down != null && isDigital(a_value_up) && isDigital(a_value_down) && Float.parseFloat(a_value_down) > Float.parseFloat(a_value_up))
	    {
		return new ArrayList();
	    } else
	    {
		if (isDigital(a_value_down) || isDigital(a_value_up))
		{
		    ArrayList a_tempList = new ArrayList();
		    for (int i = 0; i < tempList.size(); i++)
		    {
			String[] a_point = (String[]) tempList.get(i);

			if (isDigital(a_value_down)) // 有下限
			{
			    if ((Float.parseFloat(a_point[8]) * Float.parseFloat(a_point[6])) < Float.parseFloat(a_value_down))
			    {
				break;
			    }
			}

			if (isDigital(a_value_up)) // 有上限
			{
			    if (Float.parseFloat(a_value_up) >= (Float.parseFloat(a_point[8]) * Float.parseFloat(a_point[7])))
			    {
				a_tempList.add(a_point);

			    }

			} else {
                a_tempList.add(a_point);
            }

		    }
		    tempList = a_tempList;
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	return tempList;

    }

    public boolean isDigital(String value)
    {

	if (value == null || "".equals(value)) {
        return false;
    }
	java.util.regex.Pattern p = java.util.regex.Pattern.compile("^\\d+$|^\\d+\\.\\d+$");
	java.util.regex.Matcher m = p.matcher(value);
	return m.matches();
    }

    /**
         * 按条件生成
         * <td>中的内容
         * 
         * @param tempList
         *                指标标度值
         * @param point_kind
         *                要素类型 0:定性要点；1:定量要点
         * @param temp
         *                考核对象信息 id\姓名\状态 考核主体对考核对象数据采集的状态： （0，1，2，3,
         *                4）=（未打分，正在编辑，已提交，部分提交, 不打分） PerObject_Blank = 0;
         *                PerObject_Editing = 1; PerObject_Commit = 2;
         *                PerObject_PartCommit = 3; PerObject_NoMarking = 4;
         *                PerObject_CommitNoMarking = 7; // 不打分且已提交了（BS不能再编辑了）
         * 
         * @param objectResultMap
         *                对象的考核结果
         * @param status
         *                权重分值标识 0：分值 1：权重
         * @param pointMap
         *                具有某人的指标权限map
         * @param scoreflag
         *                1:标度 2:混合
         * @return
         */
    public String getPointTD(ArrayList tempList, String point_kind, String[] temp, HashMap objectResultMap, String status, HashMap pointMap, String pointID, String scoreflag, ArrayList a_pointList,
	    int num)
    {

	StringBuffer td = new StringBuffer("");

	HashMap userNumberResultMap = (HashMap) this.getUserNumberPointResultMap().get(temp[0]);
	StringBuffer point_desc = new StringBuffer("");
	String a_fieldItem = "";
	String a_fieldItem_1 = "";
	if (tempList.size() > 0)
	{
	    String[] pointGrade = (String[]) tempList.get(0);
	    a_fieldItem = pointGrade[10]; // 控制上限指标
	    a_fieldItem_1 = pointGrade[11]; // 控制下限指标
	    if ((a_fieldItem != null && a_fieldItem.trim().length() > 0 && "0".equals(point_kind)) || (a_fieldItem_1 != null && a_fieldItem_1.trim().length() > 0 && "0".equals(point_kind)))
	    {
		tempList = getFiltrateDate(tempList, a_fieldItem, temp[0], point_kind, a_fieldItem_1);
	    }
	}
	//已提交的不显示的点击评分 20150105 wangrd
	String tdHint=ResourceFactory.getProperty("performance.batchgrade.info10");
	String tdCursorStyle="cursor:pointer;";
	if ( "2".equals(temp[2]) || "4".equals(temp[2])|| "7".equals(temp[2]))
    {
	    tdHint="";
	    tdCursorStyle="";
    }
	
	if ("1".equals(scoreflag) && "0".equals(point_kind))
	{
	    if (pointMap.get(pointID)!=null && "0".equals((String) pointMap.get(pointID)))
	    {
	    	if(this.isBatchGradeRadio)
	    	{
	    		for (int i=0;i<tempList.size();i++)
	    		{
	    			td.append("<td  class='RecordRow common_border_color'  align='center'    nowrap >");
					td.append("<hr style='color:black size:1px' width='20'/>");
					td.append("</td>");
	    		}
	    	}
	    	else
	    	{
				td.append("<td  class='RecordRow common_border_color'  align='center'  width='" + columnWidth + "'   nowrap >");
				td.append("<hr style='color:black size:1px' width='20'/>");
	    	}
			point_desc.append("null/0/0");
	    } 
	    else
	    {
	
			String defaultValue = "";
			if ("1".equals(temp[2]) || "2".equals(temp[2]) || "3".equals(temp[2])|| "8".equals(temp[2]))
			{
			    String[] avalues = (String[]) objectResultMap.get(pointID);
			    if (avalues != null && avalues[6] != null) {
                    point_desc.append(avalues[6] + "/1");
                } else {
                    point_desc.append("null/1");
                }
			} else
			{
			    if ("1".equals(this.BlankScoreOption))
			    {
						if (!"4".equals(temp[2]) && !"7".equals(temp[2]))
						{
						    // LazyDynaBean
			                                // abean=(LazyDynaBean)this.pointMaxValueMap.get(temp[0]);
						    LazyDynaBean abean = (LazyDynaBean) this.pointMaxValueMap.get(pointID);
						    point_desc.append((String) abean.get("gradecode") + "/1");
						    defaultValue = (String) abean.get("gradecode");
						} else
						{
						    point_desc.append("null/1");
						}
	
			    } else if ("2".equals(this.BlankScoreOption))
			    {
					boolean isValue = false;
					for (Iterator tt = tempList.iterator(); tt.hasNext();)
					{
					    String[] temp1 = (String[]) tt.next();
		
					    if (temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree))
					    {
					    	isValue = true;
					    }
					}
					if (isValue && !"4".equals(temp[2]) && !"7".equals(temp[2]))
					{
					    defaultValue = this.BlankScoreUseDegree;
					    point_desc.append(this.BlankScoreUseDegree + "/1");
					} else {
                        point_desc.append("null/1");
                    }
			    } else {
                    point_desc.append("null/1");
                }
			}
			
			Hashtable htxml = loadxml.getDegreeWhole();
			 if("1".equals((String)htxml.get("PointEvalType"))&&!this.isBatchGradeRadio)  //单选按钮
			 {
				 if("1".equals((String)htxml.get("PointEvalType"))&& "QQZSHGJZG_4J".equalsIgnoreCase(pointID)) //北京公安特殊要求
				 {
					 
				 }
				 else{
						 td.append("<td style='cursor:pointer'  valign='top'    class='RecordRow common_border_color'  id='" + temp[0] + "_" + pointID + "' width='"+ columnWidth + "'          nowrap >"); 
				 }
					
			 }
			 else if(!this.isBatchGradeRadio)
			 {
				 if("1".equals((String)htxml.get("PointEvalType"))&& "QQZSHGJZG_4J".equalsIgnoreCase(pointID)) //北京公安特殊要求
				 {
					 
				 }
				 else {
                     td.append("<td style='"+tdCursorStyle+"'  title='"+tdHint+"' class='RecordRow common_border_color'  id='" + temp[0] + "_" + pointID + "'  onclick='showSetBox(this,\"" + pointID + "\",\"" + temp[0] + "\")'  width='"
                        + columnWidth + "'       nowrap >");
                 }
			 }
			
			if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2]))
			{
			    point_desc.append("/0");
			} else {
                point_desc.append("/1");
            }
			
			int n=0;
			for (Iterator t = tempList.iterator(); t.hasNext();)
			{
			    String[] a_temp = (String[]) t.next();
			    
			   if(this.isBatchGradeRadio)
			    {
			    	td.append("<td   class='RecordRow common_border_color'   align='center'  nowrap >");
			    	
			    	String checked_str="";
			    	if ("1".equals(temp[2]) || "2".equals(temp[2]) || "3".equals(temp[2])|| "8".equals(temp[2]))
					{
						String[] values = (String[]) objectResultMap.get(a_temp[1]);
						if (values != null)
						{
						    if (values[6] != null && values[6].equals(a_temp[5]))
						    {
								if ("1".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                } else if ("2".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                } else if ("3".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                }
						    }
						}
					} 
				    else if ("1".equals(this.BlankScoreOption) || "2".equals(this.BlankScoreOption))
				    {
						if (defaultValue != null && defaultValue.length() > 0)
						{
						    if (defaultValue.equals(a_temp[5]))
						    {
								if ("1".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                } else if ("2".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                } else if ("3".equals(this.DegreeShowType)) {
                                    checked_str="checked";
                                }
						    }
						}
				    }
			    	String disabled="";
			    	if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2])) {
                        disabled=" disabled ";
                    }
			    	
			    	td.append("<input type='radio'  "+disabled+"  onclick='setScorevalue(this)' "+checked_str+" name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' /> ");
			    	
			    	td.append("</td>");
			    }
			    else
			    {
			    	if("1".equals((String)htxml.get("PointEvalType"))&& "QQZSHGJZG_4J".equalsIgnoreCase(a_temp[1])) //北京公安特殊要求
			    	{
			    		String disabled="";
				    	if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2])) {
                            disabled=" disabled ";
                        }
			    		if ("1".equals(temp[2]) || "2".equals(temp[2]) || "3".equals(temp[2])|| "8".equals(temp[2]))
						{
							String[] values = (String[]) objectResultMap.get(a_temp[1]); 
								String checked_str="";
								if (values != null&&(values[6] != null && values[6].equals(a_temp[5]))) {
                                    checked_str="checked";
                                }
								td.append("<td   class='RecordRow common_border_color'   align='center'  nowrap >"); 
								td.append("<input type='radio' "+disabled+" "+checked_str+"  onclick='setScorevalue(this)'  name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
							 	if ("1".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                } else if ("2".equals(this.DegreeShowType)) {
                                    td.append(a_temp[4]);
                                } else if ("3".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);// td.append(a_temp[4]);
                                }
							 	td.append("</td>");
							 
							 
						} 
					    else if ("1".equals(this.BlankScoreOption) || "2".equals(this.BlankScoreOption))
					    {
					    	 
					    		String checked_str="";
					    		if (defaultValue != null && defaultValue.length() > 0&&defaultValue.equals(a_temp[5])) {
                                    checked_str="checked";
                                }
					    		td.append("<td   class='RecordRow common_border_color'   align='center'  nowrap >");
								td.append("<input type='radio' "+disabled+"  "+checked_str+"  onclick='setScorevalue(this)'  name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
								if ("1".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                } else if ("2".equals(this.DegreeShowType)) {
                                    td.append(a_temp[4]);
                                } else if ("3".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                }
								td.append("</td>");
							 
					    }
					    else if("1".equals((String)htxml.get("PointEvalType")))  //单选按钮
					    {
					    	td.append("<td   class='RecordRow common_border_color'   align='center'  nowrap >");
					    	
					    	td.append("<input type='radio' "+disabled+"  onclick='setScorevalue(this)'   name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
							if ("1".equals(this.DegreeShowType)) {
                                td.append(a_temp[13]);
                            } else if ("2".equals(this.DegreeShowType)) {
                                td.append(a_temp[4]);
                            } else if ("3".equals(this.DegreeShowType)) {
                                td.append(a_temp[13]);
                            }
							td.append("</td>");
					    }
			    	}
			    	else
			    	{
			    		String disabled="";
			    		if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2])) {
                            disabled=" disabled ";
                        }
			    		
			    		
					    if ("1".equals(temp[2]) || "2".equals(temp[2]) || "3".equals(temp[2])|| "8".equals(temp[2]))
						{
							String[] values = (String[]) objectResultMap.get(a_temp[1]);
							if("1".equals((String)htxml.get("PointEvalType")))  //单选按钮
							{
								String checked_str="";
								if (values != null&&(values[6] != null && values[6].equals(a_temp[5]))) {
                                    checked_str="checked";
                                }
								if(n!=0)
								{	
									if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                                        td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                                    } else{
										if("1".equals((String)htxml.get("RadioDirection")))//排列方式 1 横排RadioDirection
										{
											td.append(""); 
										}else{
											td.append("<BR>"); 
										}
									}
										 
								}
								td.append("<input type='radio' "+disabled+"  "+checked_str+"  onclick='setScorevalue(this)'  name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
							 	if ("1".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                } else if ("2".equals(this.DegreeShowType)) {
                                    td.append(a_temp[4]);
                                } else if ("3".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);// td.append(a_temp[4]);
                                }
								
							}
							else
							{
								if (values != null)
								{
								    if (values[6] != null && values[6].equals(a_temp[5]))
								    {
								    	 	if ("1".equals(this.DegreeShowType)) {
                                                td.append(a_temp[13]);
                                            } else if ("2".equals(this.DegreeShowType)) {
                                                td.append(a_temp[4]);
                                            } else if ("3".equals(this.DegreeShowType)) {
                                                td.append(a_temp[13]);// td.append(a_temp[4]);
                                            }
								    }
								}
							}
						} 
					    else if ("1".equals(this.BlankScoreOption) || "2".equals(this.BlankScoreOption))
					    {
					    	if("1".equals((String)htxml.get("PointEvalType")))  //单选按钮
							{
					    		String checked_str="";
					    		if (defaultValue != null && defaultValue.length() > 0&&defaultValue.equals(a_temp[5])) {
                                    checked_str="checked";
                                }
					    		if(n!=0)
					    		{
					    			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                                        td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                                    } else
										if("1".equals((String)htxml.get("RadioDirection")))//排列方式 1 横排RadioDirection
										{
											
										}else{
											td.append("<BR>");
										}
					    		}
								td.append("<input type='radio'  "+disabled+" "+checked_str+"  onclick='setScorevalue(this)'  name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
								if ("1".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                } else if ("2".equals(this.DegreeShowType)) {
                                    td.append(a_temp[4]);
                                } else if ("3".equals(this.DegreeShowType)) {
                                    td.append(a_temp[13]);
                                }
							}
							else
							{
								if (defaultValue != null && defaultValue.length() > 0)
								{
								    if (defaultValue.equals(a_temp[5]))
								    {
										if ("1".equals(this.DegreeShowType)) {
                                            td.append(a_temp[13]);
                                        } else if ("2".equals(this.DegreeShowType)) {
                                            td.append(a_temp[4]);
                                        } else if ("3".equals(this.DegreeShowType)) {
                                            td.append(a_temp[13]);// td.append(a_temp[4]);
                                        }
								    }
								}
							}
					    }
					    else if("1".equals((String)htxml.get("PointEvalType")))  //单选按钮
					    {
					    	if(n!=0)
					    	{
					    		if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                                    td.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                                } else{
									
									if("1".equals((String)htxml.get("RadioDirection")))//排列方式 1 横排RadioDirection
									{
										
									}else{
										td.append("<BR>");
									}
								}
									  
					    	}
					    	 
					    	td.append("<input type='radio' "+disabled+"  onclick='setScorevalue(this)'   name='" + temp[0] + "~" + pointID + "' value='"+a_temp[5]+"' />");
							if ("1".equals(this.DegreeShowType)) {
                                td.append(a_temp[13]);
                            } else if ("2".equals(this.DegreeShowType)) {
                                td.append(a_temp[4]);
                            } else if ("3".equals(this.DegreeShowType)) {
                                td.append(a_temp[13]);
                            }
					    }
				    
			    	}
				    
			    }
			   n++;
			}
	
		  }
	} else
	{

	    String[] a_temp = null;
	    if (tempList.size() > 0) {
            a_temp = (String[]) tempList.get(0);
        }

	    if (pointMap.get(pointID) != null && "0".equals((String) pointMap.get(pointID)))
	    {
		td.append("<td  class='RecordRow common_border_color'    align='center'  width='" + columnWidth + "' ");
		// if(a_temp[12]!=null&&a_temp[12].equals("1")&&this.showOneMark.equalsIgnoreCase("false"))
                // //不显示,考核主体不用打分，在绩效评估过程中统一打分。
		// td.append(" style='display:none'");
		td.append(" nowrap ><hr style='color:black size:1px' width='20'/> ");
		point_desc.append("null/0/0");
	    } else
	    {

		td.append("<td style='"+tdCursorStyle+"'  title='"+tdHint+"'  class='RecordRow common_border_color' ");
		td.append(" id='" + temp[0] + "_" + pointID + "'  onclick='showSetBox(this,\"" + pointID + "\",\"" + temp[0] + "\")'  width='" + columnWidth + "'");
		td.append(" nowrap >");

		if (tempList.size() > 0)
		{

		    if ("1".equals(point_kind) && a_temp[12] != null && "1".equals(a_temp[12]) && "true".equalsIgnoreCase(this.showOneMark))
		    {
			if (userNumberResultMap != null)
			{
			    td.append(PubFunc.round((String) userNumberResultMap.get(pointID), 1));
			    point_desc.append((String) userNumberResultMap.get(pointID));
			} else {
                point_desc.append("null");
            }

		    } else
		    {
			if ("1".equals(temp[2]) || "2".equals(temp[2]) || "3".equals(temp[2])|| "8".equals(temp[2]))
			{
			    String[] values = (String[]) objectResultMap.get(a_temp[1]);
			    if (values != null && (values[4] != null || values[3] != null))
			    {
				if ("1".equals(status))
				{
				    if ("0".equals(point_kind))
				    {
					td.append(values[3] != null ? values[3] : "");
					point_desc.append(values[3]);
				    } else
				    {
					td.append(values[4] != null ? values[4] : "");
					point_desc.append(values[4]);
				    }

				} else
				{
				    if (values[4] != null && "1".equals(point_kind))
				    {
					td.append(values[4]);
					point_desc.append(values[4]);

				    } else if (values[3] != null && "0".equals(point_kind))
				    {

					td.append(values[3]);
					point_desc.append(values[3]);
				    }
				}
			    } else {
                    point_desc.append("null");
                }
			} else
			{
			    if ("1".equals(this.BlankScoreOption))
			    {
				if (!"4".equals(temp[2]) && !"7".equals(temp[2]))
				{
				    LazyDynaBean abean = (LazyDynaBean) this.pointMaxValueMap.get(a_temp[1]);
				    if ("0".equals(point_kind))
				    {
					td.append(PubFunc.multiple((String) abean.get("score"), (String) abean.get("gradevalue"), 1));
					point_desc.append(PubFunc.multiple((String) abean.get("score"), (String) abean.get("gradevalue"), 1));
				    } else
				    {
					td.append((String) abean.get("top_value"));
					point_desc.append((String) abean.get("top_value"));
				    }
				} else
				{
				    point_desc.append("null");
				}
			    } else if ("2".equals(this.BlankScoreOption))
			    {
				boolean isValue = false;
				String[] t = null;
				for (Iterator tt = tempList.iterator(); tt.hasNext();)
				{
				    String[] temp1 = (String[]) tt.next();

				    if (temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree))
				    {
					isValue = true;
					t = temp1;
				    }
				}
				if (isValue && !"4".equals(temp[2]) && !"7".equals(temp[2]))
				{
				    if ("0".equals(point_kind))
				    {
					String d_value = PubFunc.round(String.valueOf(Float.parseFloat(t[9]) * Float.parseFloat(t[8])), 1);
					td.append(d_value);
					point_desc.append(d_value);
				    } else
				    {
					td.append(t[6]);
					point_desc.append(t[6]);
				    }
				} else {
                    point_desc.append("null");
                }
			    } else {
                    point_desc.append("null");
                }
			}
		    }
		}

	    }

	    // /////////////////////
	    if (tempList.size() > 0)
	    {
		if ("1".equals(point_kind) && a_temp[12] != null && "1".equals(a_temp[12]) && "false".equalsIgnoreCase(this.showOneMark)) // 考核主体不用打分，在绩效评估过程中统一打分。
        {
            point_desc.append("/0");
        } else {
            point_desc.append("/1");
        }

		if ("2".equals(temp[2]) || "4".equals(temp[2]) || "7".equals(temp[2])
			|| ("1".equals(point_kind) && "true".equalsIgnoreCase(this.showOneMark) && a_temp[12] != null && "1".equals(a_temp[12]))) {
            point_desc.append("/0");
        } else {
            point_desc.append("/1");
        }
	    }
	    // //////////////////////

	}

	if ("1".equals(scoreflag) && "0".equals(point_kind))
	{
	    if (tempList.size() == 0) {
            point_desc.append("/0");
        } else if ((a_fieldItem != null && a_fieldItem.trim().length() > 0 && "0".equals(point_kind)) || (a_fieldItem_1 != null && a_fieldItem_1.trim().length() > 0 && "0".equals(point_kind))) {
            point_desc.append("/" + ((String[]) tempList.get(0))[5] + "~" + ((String[]) tempList.get(tempList.size() - 1))[5]);
        } else {
            point_desc.append("/#");
        }
	} else {
        point_desc.append("/#");
    }
	 
	if(!this.isBatchGradeRadio) {
        td.append("</td>");
    }

	/*
         * td.setLength(0); td.append("<td  class='RecordRow'    width='"+columnWidth+"' >" );
         * td.append(" <input type='text' value='' class='TEXT_NB'
         * style='height: 17.5px; width: 50px;font-size:12;text-align= right;' />
         * "); td.append("</td>");
         */
	this.script_code.append(",p" + pointID + ":\"" + point_desc.toString() + "\"");
	return td.toString();
    }

    /**
     * 获得模板下指标信息值
     * @param template_id
     * @return
     */
    public HashMap getTemplatePointInfo(String template_id)
    {
    	HashMap map=new HashMap();
    	try
    	{
    		 DecimalFormat myformat1 = new DecimalFormat("########.####");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rowSet=dao.search("select ptp.* from per_template_point ptp,per_template_item pti where ptp.item_id=pti.item_id and pti.template_id='"+template_id+"'");
    		LazyDynaBean bean=new LazyDynaBean();
    		while(rowSet.next())
    		{
    			bean=new LazyDynaBean();
    			bean.set("score",rowSet.getString("score")!=null?myformat1.format(rowSet.getDouble("score")):"0");
    			bean.set("rank",rowSet.getString("rank")!=null?myformat1.format(rowSet.getDouble("rank")):"0");
    			map.put(rowSet.getString("point_id").toUpperCase(),bean);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }
    /**
     * Description: object_type：2，获取a0101的指标长度  else团队：获取a0101的指标长度
     * @Version1.0 
     * Dec 7, 2012 10:38:09 AM Jianghe created
     * @return
     */
    public double getA0101DiaplayWidth(){
    	double length = 0.0;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    		String sql = null;
    		if("2".equals(this.object_type)){
    			sql = "select displaywidth from fielditem where itemid = 'A0101'";
    		}else{
    			sql = "select str_value from constant where constant='UNIT_LEN'";
    		}
    		if(sql!=null){
    			rowSet = dao.search(sql);
    			if(rowSet.next()){
    				if("2".equals(this.object_type)){
    					length = rowSet.getInt("displaywidth")*8.0;
    	    		}else{
    	    			length = Integer.parseInt(Sql_switcher.readMemo(rowSet, "str_value"))*8.0;
    	    		}
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return length;
    }
    public double getB0110DiaplayWidth(){
    	double length = 0.0;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    		String sql = null;
    			sql = "select str_value from constant where constant='UNIT_LEN'";
    		if(sql!=null){
    			rowSet = dao.search(sql);
    			if(rowSet.next()){
    					length = Integer.parseInt(Sql_switcher.readMemo(rowSet, "str_value"))*8.0;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return length;
    }
    public double getE01A1DiaplayWidth(){
    	double length = 0.0;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    		String sql = null;
    		sql = "select str_value from constant where constant='POS_LEN'";
    		if(sql!=null){
    			rowSet = dao.search(sql);
    			if(rowSet.next()){
    				length = Integer.parseInt(Sql_switcher.readMemo(rowSet, "str_value"))*8.0;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return length;
    }
    /**
     * 获得基本指标列
     * @param BasicInfoItem
     * @return
     */
    public ArrayList getFieldList(String BasicInfoItem)
    {
    	
    	ArrayList list=new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    		String itemIds = "";
    		String[] temps=BasicInfoItem.split(",");
    		String item_ids = "";
    		for(int i=0;i<temps.length;i++)
    		{    			
    			if("b0110".equalsIgnoreCase(temps[i]) || "e0122".equalsIgnoreCase(temps[i]) || "e01a1".equalsIgnoreCase(temps[i])) {
                    itemIds += ","+temps[i];
                } else {
                    item_ids += ",'"+temps[i]+"'";
                }
    		}
    		if("2".equals(this.object_type)) {
                item_ids += ",'a0101'";
            }
    		
    		if(item_ids!=null && item_ids.trim().length()>0)
    		{
	    		String sql = "select itemid from fielditem where Lower(itemid) in ("+item_ids.substring(1).toLowerCase()+") order by displayid";
	    		rowSet = dao.search(sql);     	   
	    	    while (rowSet.next())
	    	    {
	    	    	String itemid = rowSet.getString("itemid");
	    	    	itemIds += ","+itemid;
	    	    }
    		}
    		if(itemIds!=null && itemIds.trim().length()>0) {
                itemIds = itemIds.substring(1);
            }
    	    temps = itemIds.split(",");
    		
    		LazyDynaBean a_bean=null;
    		FieldItem item=null;
    		boolean flag=false;
    		for(int i=0;i<temps.length;i++)
    		{
    			if(temps[i]==null||temps[i].trim().length()==0) {
                    continue;
                }
    		//	if(temps[i].toLowerCase().equalsIgnoreCase("a0101"))
    		//		continue;
    			item=DataDictionary.getFieldItem(temps[i].toLowerCase());
    			if(item==null|| "0".equals(item.getUseflag())) {
                    continue;
                }
    			
    			
    			a_bean=new LazyDynaBean();
    			a_bean.set("item_id",item.getItemid());
    			a_bean.set("itemdesc",item.getItemdesc());
    			a_bean.set("itemtype",item.getItemtype());
    			a_bean.set("codesetid",item.getCodesetid());
    			a_bean.set("itemlength",String.valueOf(item.getItemlength()));
    			double displaywidth = item.getDisplaywidth()*8.0;
    			if("B0110".equalsIgnoreCase(item.getItemid())){
    				displaywidth = getB0110DiaplayWidth();//[b0110, e0122, e01a1, A0101, C0101]
    			}
    			if("E01A1".equalsIgnoreCase(item.getItemid())){
    				displaywidth = getE01A1DiaplayWidth();//[b0110, e0122, e01a1, A0101, C0101]
    			}
    			a_bean.set("displaywidth",String.valueOf(displaywidth));
    			//a_bean.set("displaywidth","136");
    			if("2".equals(this.object_type))
    			{
    				if("B".equalsIgnoreCase(item.getFieldsetid().substring(0,1))) {
                        flag=true;
                    }
    			}
    			else
    			{
    				if(!"B".equalsIgnoreCase(item.getFieldsetid().substring(0,1)) && !"B0110".equalsIgnoreCase(item.getItemid())) {
                        flag=true;
                    }
    			}
    			list.add(a_bean);
    		}
    		if(flag) {
                list=new ArrayList();
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    	
    }
    
    
    
    
    /**
         * 生成表头html && 是否有了解程度 && 是否有总体评价 && 等级分类id
         * 
         * @param templateID
         * @param status
         *                权重分值标识
         * @param list
         */
    public ArrayList getTableHeaderHtml(String plan_id, String templateID, ArrayList list, ArrayList pointList, String status, String titleName) throws GeneralException
    {

	ArrayList arraylist = new ArrayList();
	try
	{
		HashMap templatePointInfo=getTemplatePointInfo(templateID);
	    ArrayList items = (ArrayList) list.get(0); // 模版项目列表
	    HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
	    int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
	    HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数
	    ArrayList bottomItemList = (ArrayList) list.get(4); // 模版最底层的项目

	    StringBuffer tempColumn = new StringBuffer(""); // 临时变量集合
	    ArrayList tempColumnList = new ArrayList();

	    StringBuffer tableHtml = new StringBuffer("");
	    boolean isData = false; // 控制变量 判断是否有定量的指标
	    int isKnowWhole = 0; // 1;有了结程度 2:总评选项 3：两者都有
	    float fineMax = -1;
	    int a = 0; // 控制变量
	    String pointDeformity = "0"; // 指标是否没有设上下限

	    Hashtable htxml = new Hashtable();
	    double a0101DiaplayWidth = getA0101DiaplayWidth();//指标长度
		if(loadxml==null)
		{
			
			if(planLoadXmlMap.get(plan_id)==null)
			{
						loadxml = new LoadXml(this.conn,plan_id);
						planLoadXmlMap.put(plan_id,loadxml);
			}
			else {
                loadxml=(LoadXml)planLoadXmlMap.get(plan_id);
            }
		}
		htxml = loadxml.getDegreeWhole();
	    
	    /* 查找参数表 */
	    if (this.constructType == 0)
	    {
			
			this.NodeKnowDegree = (String) htxml.get("NodeKnowDegree"); // 了解程度
			this.WholeEval = (String) htxml.get("WholeEval"); // 总体评价
			this.WholeEvalMode = (String) htxml.get("WholeEvalMode");
			if("False".equalsIgnoreCase(this.WholeEval)) {
                this.WholeEvalMode = "0";
            }
			this.DescriptiveWholeEval = (String) htxml.get("DescriptiveWholeEval");
			this.totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			String EvalClass = (String)htxml.get("EvalClass");            //在计划参数中的等级分类ID
			this.GradeClass = "";
			 if(EvalClass==null||(EvalClass.trim().length()<0|| "0".equals(EvalClass.trim()))) {
                 this.GradeClass=(String)htxml.get("GradeClass");					//等级分类ID
             } else {
                 this.GradeClass=(String)htxml.get("EvalClass");
             }

			this.limitation = (String) htxml.get("limitation"); // =-1不转换,模板中最高标度的数目
		                                                                        // (大于0小于1为百分比，大于1为绝对数)
			this.SummaryFlag = (String) htxml.get("SummaryFlag"); // 个人总结评价作为评分标准
			this.scoreflag = (String) htxml.get("scoreflag"); // =2混合，=1标度
			this.showNoMarking = (String) htxml.get("ShowNoMarking");
			this.scoreNumPerPage = (String) htxml.get("ScoreNumPerPage"); // BS打分时每页的人数，0为不限制
			this.performanceType = (String) htxml.get("performanceType"); // 考核形式
		                                                                                // 0：绩效考核
		                                                                                // 1：民主评测
			this.scaleToDegreeRule = (String) htxml.get("limitrule"); // 分值转标度规则1-就高                                                                        // 2-就低）
			this.isShowSubmittedPlan = (String) htxml.get("isShowSubmittedPlan");
			this.isAutoCountTotalOrder = (String) htxml.get("AutoCalcTotalScoreAndOrder"); // 是否自动计算总分和排名
			this.BlankScoreOption = (String) htxml.get("BlankScoreOption");
			this.mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 多人打分时同时显示自我评价
			this.allowSeeLowerGrade = (String)htxml.get("allowSeeLowerGrade"); // 允许查看下级对考核对象评分 默认为False
			this.DegreeShowType = (String) htxml.get("DegreeShowType");// 1-标准标度
			this.ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志                                                                            // 2-指标标度
			this.showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth");
			this.showHistoryScore=(String) htxml.get("ShowHistoryScore");
			this.noteIdioGoal = ((String) htxml.get("noteIdioGoal")).toLowerCase(); // 显示个人目标
		
			FineRestrict = (String) htxml.get("FineRestrict");
			fineMaxMap = (HashMap) htxml.get("fineMaxMap");
			if ("1".equals(this.performanceType))
			{
				    // this.showNoMarking="True";
				    this.SummaryFlag = "True";
			}
			this.ShowSumRow=(String)htxml.get("ShowSumRow");	
	    }
	    /* 画第一层表头 */
	    StringBuffer sequence = new StringBuffer("");
	    sequence.append("\r\n<tr><td id='a' class='cell_locked2 common_background_color common_border_color'  valign='middle' align='center' colspan='2' width='90' nowrap  > &nbsp;</td>");
	    
	    int a_cols = 1;
	    StringBuffer a_tableHtml = new StringBuffer("");
	    a_tableHtml.append("\r\n<tr>");
	    
	    /*
	    if(SystemConfig.getPropertyValue("isVisibleUN")!=null&&SystemConfig.getPropertyValue("isVisibleUN").trim().length()>0&&this.object_type.equals("2"))
	    {
	    	String _name="单位|部门";
	    	if(SystemConfig.getPropertyValue("isVisibleUN").equalsIgnoreCase("false"))
	    			_name="部门";
	    	 a_tableHtml.append("\r\n<td id='a' class='cell_locked2'  colspan='2'  valign='middle' align='center'  rowspan='");
	 	     a_tableHtml.append(lays+"'  width='"+(firstColumnWidth+70)+"' nowrap > <font class='fontStyle_self'  >");
	 	     a_tableHtml.append(_name+"</font></td>");
	 	    sequence.append("<td id='a' class='cell_locked2'  valign='middle' align='center'  width='90' nowrap  > &nbsp;</td>");
	    }
	    
	    a_tableHtml.append(" <td id='a' class='cell_locked2' ");
	    if(!(SystemConfig.getPropertyValue("isVisibleUN")!=null&&SystemConfig.getPropertyValue("isVisibleUN").trim().length()>0&&this.object_type.equals("2")))
	    	a_tableHtml.append(" colspan='2' ");
	    a_tableHtml.append(" valign='middle' align='center'  rowspan='");
	    a_tableHtml.append(lays);
	    
	    int awidth=firstColumnWidth;
	    if(this.batchGradeOthField.length()>0)
	    	awidth+=100;
	    if(!(SystemConfig.getPropertyValue("isVisibleUN")!=null&&SystemConfig.getPropertyValue("isVisibleUN").trim().length()>0&&this.object_type.equals("2")))
	    	a_tableHtml.append("'  width='"+awidth+"' ");
	    else
	    	a_tableHtml.append("'  width='"+(awidth-30)+"' ");
	    */
	    String a0101=ResourceFactory.getProperty("performance.batchgrade.title.name");
	    if ("1".equals(this.object_type))//团队
        {
            a0101=ResourceFactory.getProperty("org.performance.unorum");
        } else if ("3".equals(this.object_type))//单位
        {
            a0101=ResourceFactory.getProperty("tree.unroot.undesc");
        } else if ("4".equals(this.object_type))//部门
        {
            a0101=ResourceFactory.getProperty("column.sys.dept");
        }
	    
	    
	    if(basicFieldList.size()==0)
	    {
		    int awidth=firstColumnWidth;  
		    a_tableHtml.append(" <td id='a' class='cell_locked2 common_background_color common_border_color' "); 
		    a_tableHtml.append(" valign='middle' align='center'  colspan='2' rowspan='"+lays+"'  width='"+(a0101DiaplayWidth+30)+"' ");
	        a_tableHtml.append(" nowrap > <font class='fontStyle_self'  >"+a0101+"</font></td>"); 
		   
	    }
	    else
	    {
	    	LazyDynaBean abean=null;
	    	if("True".equalsIgnoreCase(this.LockMGradeColumn)) //锁定指标列
	    	{ 
	    		for(int i=0;i<basicFieldList.size();i++)
	    		{
	    			abean=(LazyDynaBean)basicFieldList.get(i);
	    			String itemdesc=(String)abean.get("itemdesc");
	    			double displaywidth = Double.parseDouble((String)abean.get("displaywidth"));
	    			String _str="";
	    			double awidth=displaywidth; //this.columnWidth;
	    			if(i==0)
	    			{
	    				_str=" colspan='2' ";
	    				awidth=displaywidth+30;
	    			} 
	    			
	    	    	a_tableHtml.append("\r\n<td id='a' class='cell_locked2 common_background_color common_border_color'  "+_str+"  valign='middle' align='center'  rowspan='");
	    	 	    a_tableHtml.append(lays+"'  width='"+awidth+"' nowrap > <font class='fontStyle_self'  >");
	    	 	    a_tableHtml.append(itemdesc+"</font></td>");
	    	 	    sequence.append("<td id='a' class='cell_locked2'  valign='middle' align='center'   nowrap  > &nbsp;</td>");
	    		} 
	    		if(!"2".equals(this.object_type))
	    		{
	    			
		    		a_tableHtml.append(" <td id='a' class='cell_locked2 common_background_color common_border_color' "); 
		 		    a_tableHtml.append(" valign='middle' align='center' rowspan='"+lays+"'  width='"+a0101DiaplayWidth+"' "); //this.columnWidth+"' ");
		 	        a_tableHtml.append(" nowrap > <font class='fontStyle_self'  >"+a0101+"</font></td>"); 
	    		}
	    	}
	    	else
	    	{
	    		
	    		a_tableHtml.append(" <td id='a' class='cell_locked2 common_background_color common_border_color' "); 
	 		    a_tableHtml.append(" valign='middle' align='center' rowspan='"+lays+"' colspan='2'  width='"+(a0101DiaplayWidth+30)+"' "); // width='"+this.columnWidth+"' ");
	 	        a_tableHtml.append(" nowrap > <font class='fontStyle_self'  >"+a0101+"</font></td>"); 
	 	        
	 	        for(int i=0;i<basicFieldList.size();i++)
	    		{
	 	        	
	    			abean=(LazyDynaBean)basicFieldList.get(i);
	    			String itemid=(String)abean.get("item_id"); 
	    			double displaywidth = Double.parseDouble((String)abean.get("displaywidth"));
	    			if("2".equals(this.object_type) && "a0101".equalsIgnoreCase(itemid)) {
                        continue;
                    }
	    			String itemdesc=(String)abean.get("itemdesc");
	    			String _str="";
	    			int awidth=this.firstColumnWidth; //this.columnWidth;
	    	    	a_tableHtml.append("\r\n<td id='a' class='header_locked common_background_color common_border_color'  "+_str+"  valign='middle' align='center'  rowspan='");
	    	 	    a_tableHtml.append(lays+"'  width='"+displaywidth+"' nowrap > <font class='fontStyle_self'  >");
	    	 	    a_tableHtml.append(itemdesc+"</font></td>");
	    	 	    sequence.append("<td id='a' class='header_locked common_background_color common_border_color'  valign='middle' align='center'   nowrap  > &nbsp;</td>");
	    		} 
	 	        
	 	        
	    	}
	    }
	    
	    // 北京市监狱局要求把总分列和排名列放到姓名后面 JinChunhai 2012.09.13
		if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
		{
			// 总分
			if ("true".equalsIgnoreCase(this.isShowTotalScore))
			{

				a_tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center'  ");
				a_tableHtml.append(" rowspan='" + lays + "'");
				a_tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
			    
				a_tableHtml.append("&nbsp;<font class='fontStyle_self' >" + ResourceFactory.getProperty("label.kh.stateScore.totalScore")+"</font>");
				// if(this.isAutoCountTotalOrder.equalsIgnoreCase("false"))
				// a_tableHtml.append("+");
			    
				a_tableHtml.append("</td>");
			}

			// 排名
			if ("true".equalsIgnoreCase(this.isShowOrder))
			{
				a_tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' ");
				a_tableHtml.append(" rowspan='" + lays + "'");
				a_tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
			    
				a_tableHtml.append(" &nbsp;&nbsp;<font class='fontStyle_self' >" + ResourceFactory.getProperty("kh.field.pm")+"</font>");
				// if(this.isAutoCountTotalOrder.equalsIgnoreCase("false"))
				// a_tableHtml.append("+");
			    
				a_tableHtml.append("</td>");

			}			
		}
	    	    	    
	    
	    int sequenceNum = 0;	    	    
	     
	    if(SystemConfig.getPropertyValue("goalAtFirst")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("goalAtFirst").trim()))
		{
			if ("True".equalsIgnoreCase(this.noteIdioGoal))
			{ 
			    	a_tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' "); 
			    	a_tableHtml.append(" rowspan='" + lays + "'");
			    	a_tableHtml.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' ");
			    	
			    	String desc=ResourceFactory.getProperty("lable.performance.perGoal");
			    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) {
                        desc=ResourceFactory.getProperty("performance.batchgrade.info11");
                    } else {
                        a_tableHtml.append(" nowrap ");
                    }
			    	a_tableHtml.append(" > ");
			    	a_tableHtml.append("<font class='fontStyle_self' >"+desc+"</font>");
			    	a_tableHtml.append("</td>");
				
			    	sequence.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' "); 
			    	sequence.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > ");
					if(!this.isBatchGradeRadio) {
                        sequence.append("<font  size='1' color='#97C8F9' >" + (++sequenceNum) + "</font>");
                    } else {
                        sequence.append("&nbsp;");
                    }
					sequence.append("</td>");
			}
		
			if ("True".equalsIgnoreCase(SummaryFlag))
			{
			   
				a_tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' ");
				a_tableHtml.append(" rowspan='" + lays + "'");
				a_tableHtml.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > ");
				if ("0".equals(this.performanceType))
				 {
						try
						{
						    String info = SystemConfig.getPropertyValue("per_examineInfo");
						    if (info == null || info.length() == 0) {
                                a_tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("lable.performance.perSummary")+"</font>");
                            } else {
                                a_tableHtml.append("<font class='fontStyle_self' >"+(new String(info.getBytes("ISO-8859-1"), "GBK"))+"</font>");
                            }
						} catch (Exception e)
						{
			
						}
				 }
				  else if ("1".equals(this.performanceType)) {
                    a_tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.reportwork.report")+"</font>");
                }
				a_tableHtml.append("</td>");
				
				sequence.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' "); 
				sequence.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > "); 
				if(!this.isBatchGradeRadio) {
                    sequence.append("<font  size='1' color='#97C8F9' >" + (++sequenceNum) + "</font>");
                } else {
                    sequence.append("&nbsp;");
                }
				sequence.append("</td>");
			}
		
		}
	    
	    
	    
	    String ScoreShowRelatePlan=(String)htxml.get("ScoreShowRelatePlan"); //多人评分显示引入计划得分
	    if(ScoreShowRelatePlan==null) {
            ScoreShowRelatePlan="";
        }
	    if("True".equalsIgnoreCase(ScoreShowRelatePlan))
	    {
	    	ArrayList planlist = loadxml.getRelatePlanValue("Plan");
	    	LazyDynaBean abean=null;
	    	for(int i=0;i<planlist.size();i++)
	    	{
	    		abean=(LazyDynaBean)planlist.get(i);
	    		String id=(String)abean.get("id");
	    		String Name=(String)abean.get("Name"); 			
	    		 
	    		
	    		a_tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' "); 
		    	a_tableHtml.append(" rowspan='" + lays + "'");
		    	a_tableHtml.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' ");
		    	
		    	String desc=ResourceFactory.getProperty("lable.performance.perGoal");
		    	a_tableHtml.append(" nowrap ");
		    	a_tableHtml.append(" > ");
		    	a_tableHtml.append("<font class='fontStyle_self' >"+Name+"</font>");
		    	a_tableHtml.append("</td>");
			
		    	
		    	
		    	
		    	
		    	sequence.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' "); 
		    	sequence.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > ");
				
		    	if(!this.isBatchGradeRadio) {
                    sequence.append("<font  size='1' color='#97C8F9' >" + (++sequenceNum) + "</font>");
                } else {
                    sequence.append("&nbsp;");
                }
		    	
				sequence.append("</td>");
				
				
	    	//	a_tableHtml.append(getTh(Name, colspanValue, 2, null,"header_locked"));//引入计划的名称
	    	}
	    }
	    
	    
	    
	    ArrayList perPointList = (ArrayList) pointList.get(1);
	    
	    
	    
	    
	    
	    
	    
	    for (Iterator t = items.iterator(); t.hasNext();)
	    {
			String[] temp = (String[]) t.next();
			if (temp[1] == null)
			{
				tempColumnList.add(temp);
				if("0".equals((String) map.get(temp[0]))&&this.noShowOneMark) {
                    continue;
                }
			    a_tableHtml.append("<td valign='middle' align='center' class='header_locked common_background_color common_border_color'  colspan='");
			    if(perPointList.size()==1&& "QQZSHGJZG_4J".equalsIgnoreCase(((String[])perPointList.get(0))[0])) //北京公安特殊要求
                {
                    a_tableHtml.append("5");
                } else {
                    a_tableHtml.append((String) map.get(temp[0]));
                }
			    
			    a_tableHtml.append("'");
			    a_tableHtml.append(" height='35'   > <font class='fontStyle_self'  >");
			    a_tableHtml.append(temp[3]);
			    a_tableHtml.append("</font></td>");
			    
			    a_cols += Integer.parseInt((String) map.get(temp[0]));
			}
			a++;
	    }
	    if (!"-1".equals(limitation)) {
            fineMax = Float.parseFloat(limitation);
        }

	    if (!"False".equals(FineRestrict) && "-1".equals(limitation))
	    {
			if (fineMaxMap.get("whole_grade") != null&&((String)fineMaxMap.get("whole_grade")).trim().length()>0) {
                fineMax = Float.parseFloat((String) fineMaxMap.get("whole_grade"));
            } else {
                fineMax = -1;
            }

	    }

	    if ("true".equals(NodeKnowDegree))
	    {
			isKnowWhole = 1;
			a_cols++;
	    }
	    if ("true".equals(WholeEval) || "True".equalsIgnoreCase(this.DescriptiveWholeEval))
	    {
			isKnowWhole = 2;
			a_cols++;
	    }
	    if ("true".equals(NodeKnowDegree) && (("0".equals(this.WholeEvalMode)&& "true".equals(WholeEval)) || "True".equalsIgnoreCase(this.DescriptiveWholeEval))) {
            isKnowWhole = 3;
        }

	    if ("True".equals(SummaryFlag))
	    {
	    	a_cols++;
	    }
	    if (showNoMarking != null && "true".equalsIgnoreCase(showNoMarking)) {
            a_cols++;
        }

	    a_tableHtml.append(getExtendItem(NodeKnowDegree, WholeEval, SummaryFlag, lays, 0, 1, showNoMarking));//表头考核要素之后内容

	    a_tableHtml.append("</tr> \n ");

	    // 写标题

	    tableHtml.append(a_tableHtml.toString());

	   
	    SingleGradeBo singleGradeBo = new SingleGradeBo(this.conn);
	    HashMap pointItemMap = singleGradeBo.getPointItemList((ArrayList) pointList.get(1), items);

	    // 画表头的中间层
	    tableHtml.append(getMidHeadHtml(lays, tempColumnList, items, map, perPointList, pointItemMap));
	    // 画指标列

	   
	    tableHtml.append("\r\n<tr>");
	    
	    HashMap perPointScore = getPerPointScore(templateID); // 得到各指标的分值范围
	    ArrayList a_perPointList = new ArrayList(); // 指标项（按顺序显示 包括空指标项）
	    for (int i = 0; i < bottomItemList.size(); i++)
	    {
			String[] a_term = (String[]) bottomItemList.get(i);
			int is_thing = 0;
			for (Iterator t = perPointList.iterator(); t.hasNext();)
			{
			    String[] aa = (String[]) t.next();
			    if (aa[3].equals(a_term[0]))
			    {
				aa[4] = "1";
				a_perPointList.add(aa);
				is_thing = 1;
			    }
			}
			if (is_thing == 0)
			{
			    String[] aa = new String[5];
			    aa[4] = "0";
			    a_perPointList.add(aa);
			}
		 }

	   

	    this.script_code.append("\r\n var pointScore={");
	    StringBuffer pointScore = new StringBuffer("");

	    // 分析指标高度
	    int character_num = 0;
	    for (Iterator t = perPointList.iterator(); t.hasNext();)
	    {
		String[] temp = (String[]) t.next();
		if (temp[1].length() > character_num) {
            character_num = temp[1].length();
        }
	    }
	    int hs = 1;
	    if (character_num > 4)
	    {
		hs = character_num / 4;
		if (hs % 4 != 0) {
            hs++;
        }
	    }
	    hs++;
	    String[] _temp=null;
	    DecimalFormat myformat1 = new DecimalFormat("########.####");//
	    boolean isRk=false;
	    for (Iterator t = perPointList.iterator(); t.hasNext();)
	    {
			sequenceNum++;
			String[] temp = (String[]) t.next();
			ArrayList per_gradeList=(ArrayList)this.perPointGradedescMap.get(temp[0].toLowerCase());
			if(!this.isBatchGradeRadio)
			{
				if("1".equals((String)htxml.get("PointEvalType"))&&perPointList.size()==1&& "QQZSHGJZG_4J".equalsIgnoreCase(((String[])perPointList.get(0))[0])) //北京公安特殊要求
				{
					sequence.append("<td valign='middle' align='center'  class='header_locked common_background_color common_border_color' colspan='2'  height='20' >认可</td>");
					sequence.append("<td valign='middle' align='center'  class='header_locked common_background_color common_border_color' colspan='2'    height='20' >不认可</td>");
					sequence.append("<td valign='middle' align='center'  class='header_locked common_background_color common_border_color'   height='20' >不了解</td>");
					isRk=true;
				}
				else {
                    sequence.append("<td valign='middle' align='center'  class='header_locked common_background_color common_border_color' width='" + columnWidth + "' height='20'  ");
                }
			}
			
			tableHtml.append("<td valign='top' align='center' id='" + temp[0] + "' ");
			if(this.isBatchGradeRadio&&per_gradeList!=null&&per_gradeList.size()>0) {
                tableHtml.append(" colspan='"+per_gradeList.size()+"' ");
            }
			
			if("1".equals((String)htxml.get("PointEvalType"))&&perPointList.size()==1&& "QQZSHGJZG_4J".equalsIgnoreCase(((String[])perPointList.get(0))[0])) //北京公安特殊要求
            {
                tableHtml.append(" colspan='5' ");
            }
			
			if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7]) && "false".equalsIgnoreCase(this.showOneMark))
			{
			    // tableHtml.append(" style='display:none' ");
			    // sequence.append(" style='display:none' ");
			    temp[1] = "";
			} else if (temp[5] == null || "1".equals(temp[5]) || "2".equals(temp[5]))
			{
			    if (temp[5] == null || "2".equals(temp[5])) {
                    tableHtml.append(" onclick='showDateSelectBox2(this);' style='cursor:hand'   onmouseout='hiddenData();'");
                } else {
                    tableHtml.append(" onclick='showDateSelectBox(this);' style='cursor:hand'   onmouseout='hiddenData();'");
                }
			}
			if(!this.isBatchGradeRadio)
			{
				if("1".equals((String)htxml.get("PointEvalType"))&&perPointList.size()==1&& "QQZSHGJZG_4J".equalsIgnoreCase(((String[])perPointList.get(0))[0])) //北京公安特殊要求
				{
					
				}
				else {
                    sequence.append(" ><font size='1' color='#97C8F9' >");
                }
			
			}
			tableHtml.append(" class='header_locked common_background_color common_border_color' width='" + columnWidth + "' height='" + (hs * 15) + "'   >");
			String clientName = SystemConfig.getPropertyValue("clientName")==null?"":SystemConfig.getPropertyValue("clientName");
			if(clientName!=null&& "gw".equalsIgnoreCase(clientName)) {
                tableHtml.append("<font class='fontStyle_self'    >" + temp[1] + "</font>");
            } else {
                tableHtml.append("<font class='fontStyle_self' color='#0158AF' onmouseover='changeColor(this)' onmouseout='changeColor2(this)'   >" + temp[1] + "</font>");
            }
				
	
			if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7]) && "false".equalsIgnoreCase(this.showOneMark))
			{
	
			} else if (temp[5] == null || "1".equals(temp[5]) || "2".equals(temp[5]))
			{
			    // tableHtml.append("<font color='red'>*</font>");
			}
			tableHtml.append("<br>");
			String[] temp2 = (String[]) perPointScore.get(temp[0]);
			// if(temp2[7].equals("0")&&temp2[8].equals("0"))
			// pointDeformity="1";
			
			if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("showTemplatePointValue")))
			{
				if(templatePointInfo.get(temp[0].toUpperCase())!=null)
				{
					LazyDynaBean _pointBean=(LazyDynaBean)templatePointInfo.get(temp[0].toUpperCase());
					if ("0".equals(status)) //分值
					{
						tableHtml.append("<font face=宋体 style='font-weight:normal;font-size:8pt'>" +(String)_pointBean.get("score") + "分</font>");
					}
					else
					{
						String rank=PubFunc.multiple((String)_pointBean.get("rank"), "100", 5);
						tableHtml.append("<font face=宋体 style='font-weight:normal;font-size:8pt'>" +myformat1.format(Double.parseDouble(rank))+"%</font>");
					}
				}
			}
			else
			{
				if ("0".equals(status) || "2".equals(scoreflag))
				{
				    if ("1".equals(temp[2])) // 定量指标
				    {
		
					isData = true;
					if (temp[7] == null || !"1".equals(temp[7]))
					{
					    if (temp2!=null && temp2[3] != null && temp2[2] != null)
						{
					    	if(!(SystemConfig.getPropertyValue("hiddenPointScore")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("hiddenPointScore")))) {
                                tableHtml.append("<font face=宋体 style='font-weight:normal;font-size:8pt'>" + PubFunc.round(temp2[3], 1) + "~" + PubFunc.round(temp2[2], 1) + "</font>");
                            }
						
						}
					}
				    } else
				    // 定性指标
				    {
					if ("2".equals(scoreflag))
					{
						if(!(SystemConfig.getPropertyValue("hiddenPointScore")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("hiddenPointScore")))) {
                            tableHtml.append("<font face="+ResourceFactory.getProperty("performance.batchgrade.songFont")+" style='font-weight:normal;font-size:8pt'>" + ResourceFactory.getProperty("jx.param.mark") + ":" +(temp2!=null&&temp2[1]!=null&&temp2[1].length()>0?myformat1.format(Double.parseDouble(temp2[1])):"")+ "</font>");
                        }
					    pointScore.append(",p" + temp[0] + ":\"" +(temp2!=null&&temp2[1]!=null&&temp2[1].length()>0?myformat1.format(Double.parseDouble(temp2[1])):"")+ "\"");
		
					}
				    }
				} else
				{
		
				    isData = true;
				    if ("1".equals(temp[2])) // 定量指标
				    {
				    	if (temp[7] == null || !"1".equals(temp[7]))
				    	{
				    		if(!(SystemConfig.getPropertyValue("hiddenPointScore")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("hiddenPointScore")))) {
                                tableHtml.append("<font face="+ResourceFactory.getProperty("performance.batchgrade.songFont")+" style='font-weight:normal;font-size:8pt'>" + PubFunc.round(temp2[3], 0) + "~" + PubFunc.round(temp2[2], 0) + "</font>");
                            }
				    	}
				    }
		
				}
			}
			tableHtml.append("</td>");
			if(this.isBatchGradeRadio)
			{
				for(int i=0;i<per_gradeList.size();i++)
				{
					_temp=(String[])per_gradeList.get(i);
					
					sequence.append("<td valign='middle' align='center'  class='header_locked common_background_color common_border_color' width='" + (columnWidth/per_gradeList.size()) + "' height='20' > ");
					sequence.append("<font class='fontStyle_self' color='#0158AF' >");
					sequence.append(_temp[4]);
					sequence.append("</font>");
					sequence.append("</td>");
				}
			}
			else
			{
				if("1".equals((String)htxml.get("PointEvalType"))&&perPointList.size()==1&& "QQZSHGJZG_4J".equalsIgnoreCase(((String[])perPointList.get(0))[0])) //北京公安特殊要求
				{
					
				}
				else {
                    sequence.append(sequenceNum + "</font></td>");
                }
			}
	
		 }
	    if (pointScore.length() > 0) {
            this.script_code.append(pointScore.substring(1));
        }
	    this.script_code.append("}\r\n");

	    
	    if(lays==2)
	    {
	    	if(this.userView.hasTheFunction("06060103"))
			{
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		//此处lays应为lays-1才对，lays为表头总层数，第一层表头为“最终评估结果”，应该去掉这一层才对。否则firefox浏览器下，表头没有下边框  haosl 2018-3-30
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='"+this.columnWidth_lower+"' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.kh.stateScore.lastScore")+"</font>");	//总分	
	    		tableHtml.append("</td>");
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='"+(this.columnWidth_lower+20)+"' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("jx.param.degreepro")+"</font>");	//等级	
	    		tableHtml.append("</td>");
	    	//	tableHtml.append("<td class='header_locked' valign='middle' align='center' ");
	    	//	tableHtml.append(" rowspan='" + lays + "' width='80' nowrap > ");		
	    	//	tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("jx.param.xishu")+"</font>");		
	    	//	tableHtml.append("</td>");
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='"+this.columnWidth_lower+"' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.kh.stateScore.lastRanking")+"</font>");//排名		
	    		tableHtml.append("</td>");
			}
	    }	    
	    tableHtml.append("</tr> \n");
	    sequence.append(getExtendItem(NodeKnowDegree, WholeEval, SummaryFlag, lays, 1, sequenceNum, showNoMarking));
	    sequence.append("</tr> \n");
	    
	     
	    if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())&&!isRk)
	    {
	    	
	    }
	    else
	    {
	    	if(isRk) {
                tableHtml.append(sequence.toString());
            }
	    }
	    // String title="\r\n <tr height='80' > <th
                // style='background-image:url(/images/mainbg.jpg)'
                // valign='middle' align='center' height='50'
                // colspan='"+a_cols+"' > <font
                // face="+ResourceFactory.getProperty("font_family.song")+"
                // style='font-weight:bold;font-size:15pt'> "+titleName+"
                // </font> </th> </tr> ";
	    arraylist.add("<thead>" + tableHtml.toString() + "</thead>");
	    arraylist.add(String.valueOf(isKnowWhole));
	    arraylist.add(String.valueOf(fineMax));
	    arraylist.add("");
	    arraylist.add(scoreflag);
	    arraylist.add("");
	    arraylist.add(a_perPointList);

	    pointDeformity = (String) pointList.get(2);
	    String nogradeItem = "";
	    nogradeItem = (String) pointList.get(3);
	    arraylist.add(pointDeformity);
	    arraylist.add(nogradeItem);

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return arraylist;
    }

    // 生成表头中间层html
    public String getMidHeadHtml(int lays, ArrayList tempColumnList, ArrayList items, HashMap map, ArrayList perPointList, HashMap pointItemMap)
    {

	StringBuffer tableHtml = new StringBuffer("");
	for (int b = 2; b < lays; b++)
	{
	    ArrayList tempList = new ArrayList();
	    tableHtml.append("\r\n<tr>");
	    int d = 0;
	    for (int i = 0; i < tempColumnList.size(); i++)
		{
			String[] temp1 = (String[]) tempColumnList.get(i);
			if (temp1[0] == null)
			{
			    tableHtml.append("<td valign='middle' align='center' class='header_locked common_background_color common_border_color' colspan='1' ");
			    // tableHtml.append(" width='"+columnWidth+"' ");
			    tableHtml.append(" height='35'   >");
			    tableHtml.append("&nbsp;");
			    tableHtml.append("</td>");
	
			    tempList.add(temp1);
			    d++;
			} else
			{
			    int pointNum = Integer.parseInt((String) map.get(temp1[0]));
			    
			    
			    int isNullItem = 0;
			    for (Iterator t1 = items.iterator(); t1.hasNext();)
			    {
				String[] temp2 = (String[]) t1.next();
				if (temp2[1] != null && temp2[1].equals(temp1[0]))
				{
				    int pointNum2 = Integer.parseInt((String) map.get(temp2[0]));
				    if(this.noShowOneMark&&pointNum2==0) {
                        continue;
                    }
				    int selfnum = 0;
				    isNullItem++;
				    while (d < perPointList.size())
				    {
					String[] point = null;
	
					/*
	                                 * if(this.showOneMark.equalsIgnoreCase("false")) {
	                                 * while(((String[])perPointList.get(d))[2].equals("1")&&((String[])perPointList.get(d))[7].equals("1")) {
	                                 * d++; } }
	                                 */
					point = (String[]) perPointList.get(d);
	
					ArrayList pointItemList = (ArrayList) pointItemMap.get(point[0]);
					int flag = 0;
					for (Iterator t2 = pointItemList.iterator(); t2.hasNext();)
					{
					    String[] tempItem = (String[]) t2.next();
					    if (tempItem[0].equals(temp2[0])) {
                            flag++;
                        }
					}
	
					if (flag == 0)
					{
					    tableHtml.append("<td valign='middle' align='center' class='header_locked common_background_color common_border_color' colspan='1' ");
					    // tableHtml.append("
	                                        // width='"+columnWidth+"' ");
					    tableHtml.append(" height='35'   >");
					    tableHtml.append("&nbsp;");
					    tableHtml.append("</td>");
	
					    String[] ttt = new String[5];
					    tempList.add(ttt);
					    d++;
					    selfnum++;
					} else
					{
					    tableHtml.append("<td valign='middle' align='center' class='header_locked common_background_color common_border_color' colspan='" + (String) map.get(temp2[0]) + "' ");
					    // tableHtml.append("
	                                        // width='"+(columnWidth*Integer.parseInt((String)map.get(temp2[0])))+"'
	                                        // ");
					    tableHtml.append(" height='35'   > <font class='fontStyle_self'  >");
					    tableHtml.append(temp2[3]);
					    tableHtml.append("</font></td>");
	
					    d += pointNum2;
					    selfnum += pointNum2;
					    tempList.add(temp2);
					    break;
					}
				    }
	
				}
			    }
			    if (isNullItem == 0)
			    {
				for (int a = 0; a < pointNum; a++)
				{
				    tableHtml.append("<td valign='middle' align='center' class='header_locked common_background_color common_border_color' colspan='1' ");
				    // tableHtml.append(" width='"+columnWidth+"'");
				    tableHtml.append(" height='35'   >");
				    tableHtml.append("&nbsp;");
				    tableHtml.append("</td>");
	
				    String[] ttt = new String[5];
				    tempList.add(ttt);
				    d++;
				}
	
			    }
	
			}
		}

	    if (b == 2)
	    {
	    	if(this.userView.hasTheFunction("06060103"))
			{
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='80' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.kh.stateScore.lastScore")+"</font>");		
	    		tableHtml.append("</td>");
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='80' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("jx.param.degreepro")+"</font>");		
	    		tableHtml.append("</td>");
	    	//	tableHtml.append("<td class='header_locked' valign='middle' align='center' ");
	    	//	tableHtml.append(" rowspan='" + lays + "' width='80' nowrap > ");		
	    	//	tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("jx.param.xishu")+"</font>");		
	    	//	tableHtml.append("</td>");
	    		tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
	    		tableHtml.append(" rowspan='" + (lays-1) + "' width='80' nowrap > ");		
	    		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.kh.stateScore.lastRanking")+"</font>");		
	    		tableHtml.append("</td>");
			}
	    }	    
	    tableHtml.append("</tr>");
	    tempColumnList = tempList;
	}

	return tableHtml.toString();
    }

    /**
         * 根据考核模版得到相应指标信息
         * 
         * @param template_id
         *                模版id
         * @return
         */
    public HashMap getPerPointByTemplateid(String template_id)
    {

	//ArrayList list = new ArrayList();
	HashMap map=new HashMap();
    ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{   //boolean isByModelFlag = SingleGradeBo.getByModel(this.planid,this.conn);
		////能力素质支持一个评估计划适应多个岗位进行评估
		String bymodel ="";
		if(this.planVo!=null) {
            bymodel = String.valueOf(this.planVo.getInt("bymodel"));
        }
	    String sql = "select po.point_id,po.pointname,pp.rank,po.pointkind,po.status from per_template_item pi,per_template_point pp,per_point po "
		    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + template_id + "'  order by pp.seq";
	    if(bymodel!=null && bymodel.trim().length()>0 && "1".equals(bymodel)&& SingleGradeBo.isHaveMatchByModel(this.object_id, this.conn)){
	    	sql = " select po.point_id,po.pointname,pp.rank,po.pointkind,po.status from per_competency_modal pp,per_point po where pp.point_id=po.point_id and object_type='3' and object_id = '"+this.getE01a1(this.getObject_id())+"' and "+Sql_switcher.dateValue(historyDate)+" between pp.start_date and pp.end_date order by pp.point_type";
	    }
	    rowSet = dao.search(sql);
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		abean = new LazyDynaBean();
		abean.set("point_id", rowSet.getString("point_id"));
		abean.set("rank", rowSet.getString("rank"));
		abean.set("pointkind", rowSet.getString("pointkind"));
		if (rowSet.getString("status") == null) {
            abean.set("status", "0");
        } else {
            abean.set("status", rowSet.getString("status"));
        }
		//list.add(abean);
	      map.put(rowSet.getString("point_id").toLowerCase(), abean);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }

    /**
         * 得到动态权重表里的信息
         * 
         * @param planID
         * @return map (要素号：List)
         */
    public HashMap getDynaRankInfoMap(String planID)
    {

		HashMap map = new HashMap();
		HashMap rangkMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
		    String point_id = "";
		    ArrayList tempList = new ArrayList();
		    rowSet = dao.search("select * from per_dyna_rank where plan_id=" + planID + " order by point_id,dyna_obj_type desc,dyna_obj desc");
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();
				String a_point_id = rowSet.getString("point_id");
				if (!a_point_id.equals(point_id) && !"".equals(point_id))
				{
				    map.put(point_id, tempList);
				    tempList = new ArrayList();
				}
				abean.set("point_id", rowSet.getString("point_id"));
				abean.set("plan_id", rowSet.getString("plan_id"));
				abean.set("dyna_obj_type", rowSet.getString("dyna_obj_type"));
				abean.set("dyna_obj", rowSet.getString("dyna_obj"));
				abean.set("rank", rowSet.getString("rank"));
				tempList.add(abean);
				rangkMap.put(rowSet.getString("point_id")+"_"+rowSet.getString("dyna_obj"),Double.toString(rowSet.getDouble("rank")));
				
				point_id = a_point_id;
		    }
		    map.put(point_id, tempList);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		this.setDynaRankInfoMap(map);
		this.setObjDynaRankMap(rangkMap);
		return map;
    }

    public HashMap getObjectInfoMap(String plan_id)
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    rowSet = dao.search("select per_object.*,per_plan.object_type from per_object,per_plan  where per_object.plan_id=per_plan.plan_id and per_object.plan_id=" + plan_id);
	    while (rowSet.next())
	    {
		LazyDynaBean abean = new LazyDynaBean();
		abean.set("object_id", rowSet.getString("object_id"));
		String b0110 = rowSet.getString("b0110") != null ? rowSet.getString("b0110") : "";
		String e0122 = rowSet.getString("e0122") != null ? rowSet.getString("e0122") : "";
		String e01a1 = rowSet.getString("e01a1") != null ? rowSet.getString("e01a1") : "";
		String body_id = rowSet.getString("body_id") != null ? rowSet.getString("body_id") : "";
		abean.set("b0110", b0110);
		abean.set("e0122", e0122); // 部门
		abean.set("e01a1", e01a1); // 岗位
		abean.set("body_id", body_id); // 对象类别
		abean.set("object_type", rowSet.getString("object_type")); // 2:人员
                                                                                // 1：部门

		map.put(rowSet.getString("object_id"), abean);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	this.setObjectInfoMap(map);
	return map;
    }

    /**
         * 得到对象对于某指标的动态权重
         * 
         * @param defaultRank
         *                指标默认权重
         * @param objectID
         *                对象id
         * @param planID
         *                计划id
         * @param pointid
         *                指标id
         * @return
         */
    public String getRankByObjectID(String defaultRank, String objectID, String pointid)
    {

		String a_rank = "";
		LazyDynaBean objectInfo = (LazyDynaBean) this.objectInfoMap.get(objectID);
		ArrayList perDynaRankList = (ArrayList) this.dynaRankInfoMap.get(pointid);
		if (objectInfo == null || perDynaRankList == null) {
            return defaultRank;
        }
	
		String b0110 = (String) objectInfo.get("b0110");
		String e0122 = (String) objectInfo.get("e0122"); // 部门
		String e01a1 = (String) objectInfo.get("e01a1"); // 岗位
		String body_id = (String) objectInfo.get("body_id"); // 考核对象类别
								
		//  人>考核对象类别>组织机构节点
		String objRank = (String)this.objDynaRankMap.get(pointid+"_"+objectID);
		String bodRank = (String)this.objDynaRankMap.get(pointid+"_"+body_id);
		String e01Rank = (String)this.objDynaRankMap.get(pointid+"_"+e01a1);
		String e02Rank = (String)this.objDynaRankMap.get(pointid+"_"+e0122);
		String b01Rank = (String)this.objDynaRankMap.get(pointid+"_"+b0110);
		if(objRank!=null && objRank.trim().length()>0 && !"0".equalsIgnoreCase(objRank) && !"0.0".equalsIgnoreCase(objRank)) {
            a_rank = (String)this.objDynaRankMap.get(pointid+"_"+objectID); // 人
        } else if(bodRank!=null && bodRank.trim().length()>0 && !"0".equalsIgnoreCase(bodRank) && !"0.0".equalsIgnoreCase(bodRank)) {
            a_rank = (String)this.objDynaRankMap.get(pointid+"_"+body_id); // 考核对象类别
        } else if(e01Rank!=null && e01Rank.trim().length()>0 && !"0".equalsIgnoreCase(e01Rank) && !"0.0".equalsIgnoreCase(e01Rank)) {
            a_rank = (String)this.objDynaRankMap.get(pointid+"_"+e01a1); // 岗位
        } else if(e02Rank!=null && e02Rank.trim().length()>0 && !"0".equalsIgnoreCase(e02Rank) && !"0.0".equalsIgnoreCase(e02Rank)) {
            a_rank = (String)this.objDynaRankMap.get(pointid+"_"+e0122); // 部门
        } else if(b01Rank!=null && b01Rank.trim().length()>0 && !"0".equalsIgnoreCase(b01Rank) && !"0.0".equalsIgnoreCase(b01Rank)) {
            a_rank = (String)this.objDynaRankMap.get(pointid+"_"+b0110); // 单位
        }
						
	/*	
		for (int i = 0; i < perDynaRankList.size(); i++)
		{
		    LazyDynaBean abean = (LazyDynaBean) perDynaRankList.get(i);
		    String dyna_obj_type = (String) abean.get("dyna_obj_type");
		    String dyna_obj = (String) abean.get("dyna_obj");
		    String rank = (String) abean.get("rank");
		    if (dyna_obj_type.equals("4")) // 对象id
		    {
				if (objectID.equals(dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
		    } else if (dyna_obj_type.equals("5")) // 考核对象类别
		    {
		    	if (body_id.equals(dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
	
		    }else if (dyna_obj_type.equals("3")) // 职位
		    {
				if (isFitObj(e01a1, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
	
		    } else if (dyna_obj_type.equals("2")) // 部门
		    {
				if (isFitObj(e0122, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}
	
		    } else if (dyna_obj_type.equals("1")) // 单位
		    {
				if (isFitObj(b0110, dyna_obj))
				{
				    a_rank = rank;
				    break;
				}	
		    }	
		}
	*/
		
		if ("".equals(a_rank)) {
            a_rank = defaultRank;
        }
		return a_rank;
    }

    public boolean isFitObj(String obj, String obj2)
    {

	boolean flag = false;
	if (obj == null || "".equals(obj)) {
        return flag;
    } else if (obj.trim().length() == obj2.trim().length())
	{
	    if (obj.equals(obj2)) {
            flag = true;
        }
	} else if (obj.trim().length() > obj2.trim().length())
	{
	    if (obj.substring(0, obj2.length()).equals(obj2)) {
            flag = true;
        }
	}
	return flag;
    }

    /**
         * 取得某考核计划下 主体 给考核对象 打分的分值
         * 
         * @param plan_id
         *                计划id
         * @param mainbody_id
         *                考核主体id
         * @param template_id
         *                模版id
         * @param object_id
         *                考核对象 id
         * @param KeepDecimal
         *                小数位
         * @return
         */
    public float getObjectTotalScore(int plan_id, String mainbody_id, String template_id, String a_objectID,UserView userView)
    {

	double score = 0d;
	String avalue="";
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
		
		Hashtable htxml = new Hashtable();
		if(loadxml==null)
		{
			if(planLoadXmlMap.get(String.valueOf(plan_id))==null)
			{
						loadxml = new LoadXml(this.conn,String.valueOf(plan_id));
						planLoadXmlMap.put(String.valueOf(plan_id),loadxml);
			}
			else {
                loadxml=(LoadXml)planLoadXmlMap.get(String.valueOf(plan_id));
            }
			
		//	loadxml = new LoadXml(this.conn, String.valueOf(plan_id));
		}
		htxml =loadxml.getDegreeWhole();
		this.showOneMark = (String) htxml.get("ShowOneMark"); // BS打分时显示统一打分的指标，以便参考
		int KeepDecimal = Integer.parseInt((String) htxml.get("KeepDecimal")); // 小数位
		
	    HashMap pointMap = getPerPointByTemplateid(template_id);
	    String sql="select * from per_table_"+plan_id+" where  mainbody_id='"+mainbody_id+"' and object_id='"+a_objectID+"'";
	    RowSet rowSet2=dao.search(sql);
	    String a_score="0";
	    String pointid="";
	    while(rowSet2.next())
	    {
	    	pointid=rowSet2.getString("point_id").toLowerCase();
	    	if(pointMap.get(pointid)!=null)
	    	{
	    		LazyDynaBean abean = (LazyDynaBean) pointMap.get(pointid);
	    		String rank = (String) abean.get("rank");
	    		String point_id = (String) abean.get("point_id");
	    		String arank = getRankByObjectID(rank, a_objectID, point_id);
	    		String pointScore="0";
	    		if(rowSet2.getString("score")!=null) {
                    pointScore=rowSet2.getString("score");
                }
	    		pointScore=PubFunc.multiple(pointScore,arank, KeepDecimal);
	    		a_score=PubFunc.add(a_score,pointScore, KeepDecimal);
	    	}
	    }
	    score=Double.parseDouble(a_score);
	    /*
	    StringBuffer sql = new StringBuffer("select permain.object_id,");
	    StringBuffer sql_select = new StringBuffer("");
	    StringBuffer sql_from = new StringBuffer(" from (");
	    sql_from.append("select distinct object_id  from per_mainbody where object_id='" + a_objectID + "' and plan_id="+plan_id);
	    sql_from.append(") permain");
	    for (int i = 0; i < pointList.size(); i++)
	    {
		LazyDynaBean abean = (LazyDynaBean) pointList.get(i);
		String point_id = (String) abean.get("point_id");
		String rank = (String) abean.get("rank");
		String arank = getRankByObjectID(rank, a_objectID, point_id);
		sql_select.append("+" + Sql_switcher.isnull("t_" + point_id + ".score", "0") + "*" + arank);
		sql_from.append(" left join (select * from per_table_" + plan_id + " where  mainbody_id='" + mainbody_id + "' and point_id='" + point_id + "' ) t_" + point_id
			+ " on permain.object_id=t_" + point_id + ".object_id ");

	    }
	    sql.append(sql_select.substring(1) + " score ");
	    // sql.append(sql_from.toString()+" where
                // permain.object_id='"+a_objectID+"' ");
	    sql.append(sql_from.toString());
	    RowSet rowSet2 = dao.search(sql.toString());
	    if (rowSet2.next())
	    {
	    	score = rowSet2.getFloat("score");
	    }
	    */
	    
	    
	    
	    
	   
	    //将统一打分定量指标的值加进来
	    ArrayList apointList = new ArrayList();
	    ArrayList aapointList=new ArrayList();
	    Set keySet=pointMap.keySet();
	//	for (int i = 0; i < pointList.size(); i++)
		for(Iterator t=keySet.iterator();t.hasNext();)
	    {
			String key=(String)t.next();
		    LazyDynaBean abean = (LazyDynaBean) pointMap.get(key);
		    String[] aa = new String[8];
		    aa[0] = (String) abean.get("point_id");
		    aa[2] = (String) abean.get("pointkind");
		    aa[7] = (String) abean.get("status");
		    apointList.add(aa);
		    if ("1".equals(aa[2]) && aa[7] != null && "1".equals(aa[7])) {
                aapointList.add(aa[0]);
            }
		}
		
		
		this.setUserNumberPointResultMap(getUserNumberPointResultMap(apointList, String.valueOf(plan_id)));
		HashMap userMap = (HashMap) this.userNumberPointResultMap.get(a_objectID);
		for (int i = 0; i < aapointList.size() && userMap!=null; i++)
		{
		    String point_id = (String) aapointList.get(i);
		    if (userMap.get(point_id) != null && !"".equals((String) userMap.get(point_id)))
		    {
		    	score += Double.parseDouble((String) userMap.get(point_id));
		    }
		}
		avalue = PubFunc.round(String.valueOf(score), KeepDecimal);	    
		
		//zzk 2014/2/07 评分时是否引入计算公式算总分
		String batchScoreImportFormula=(String) htxml.get("BatchScoreImportFormula");
		if("1".equals(batchScoreImportFormula)||"True".equalsIgnoreCase(batchScoreImportFormula)){
			String formulaSql=getFormulaSql(userView,this.loadxml,String.valueOf(plan_id)); 
			if(formulaSql.length()>0) {
                avalue=PubFunc.round(getFinalScore(String.valueOf(plan_id),formulaSql,a_objectID,avalue,dao), KeepDecimal);
            }
		}

	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
		return Float.parseFloat(avalue);
    }

    public HashMap getObjectResultMap(int plan_id, String mainbody_id)
    {

	HashMap map = new HashMap();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search("select * from per_table_" + plan_id + " where mainbody_id='" + mainbody_id + "' order by object_id");
	    ArrayList list = new ArrayList();
	    String object_id = "";
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		String a_objectid = rowSet.getString("object_id");
		String score = "0";
		if (rowSet.getString("score") != null) {
            score = rowSet.getString("score");
        }
		String point_id = rowSet.getString("point_id");

		if ("".equals(object_id)) {
            object_id = a_objectid;
        }
		if (!object_id.equals(a_objectid))
		{
		    map.put(object_id, list);
		    object_id = a_objectid;
		    list = new ArrayList();

		}

		abean = new LazyDynaBean();
		abean.set("point_id", point_id);
		abean.set("score", score);
		list.add(abean);
	    }
	    if (!"".equals(object_id)) {
            map.put(object_id, list);
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }
    
    
    //获得绩效评估里定义的计算公式
    public String getFormulaSql(UserView userView,LoadXml _loadxml,String planId)
    {
    	String formulaSql="";
    	ContentDAO dao = new ContentDAO(this.conn);
    	try
    	{
	  	    String formula = "";
	  	    ArrayList formulalist = _loadxml.getRelatePlanValue("Formula", "Caption");
	  		
	  		if (formulalist.size() > 0) {
                formula = formulalist.get(0).toString();
            } else {
                formula = "["+ResourceFactory.getProperty("performance.batchgrade.thisScore")+"]";
            }
	  	    if(formula!=null&&formula.trim().length()>0&&!formula.equals("["+ResourceFactory.getProperty("performance.batchgrade.thisScore")+"]"))
	  	    {
	  	    
	  	    	String tablename = "per_result_" + planId;


	  	    	Table table = new Table(tablename);
	  			DbWizard dbWizard = new DbWizard(this.conn);
	  			DBMetaModel dbmodel = new DBMetaModel(this.conn);
	  			boolean flag = false;
                //解决计算公式列缺失问题 haosl 20190726
	  			if (!dbWizard.isExistField(tablename, "A0100", false))
	  			{
	  				Field obj = new Field("A0100");
	  				obj.setDatatype(DataType.STRING);
	  				obj.setLength(50);
	  				obj.setKeyable(false);
	  				table.addField(obj);
                    flag = true;
	  			}
                //解决计算公式列缺失问题 haosl 20190726
                ArrayList planlist = loadxml.getRelatePlanValue("Plan");
                LazyDynaBean abean = null;
                for (int i = 0; i < planlist.size(); i++) {

                    abean = (LazyDynaBean) planlist.get(i);
                    String id = (String) abean.get("id");
                    // String Name=(String)abean.get("Name");
                    // String Type=(String)abean.get("Type");

                    String Menus = (String) abean.get("Menus");
                    if (Menus != null && Menus.trim().length() > 0) {
                        String[] temps = Menus.split(",");
                        for (int j = 0; j < temps.length; j++) {
                            String temp = temps[j].trim();
                            if (temp.length() == 0) {
                                continue;
                            }
                            if ("score".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id,
                                        false)) {
                                    Field obj = new Field("G_" + id);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Grade".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Grade", false)) {
                                    Field obj = new Field("G_" + id + "_Grade");
                                    obj.setDatatype(DataType.STRING);
                                    obj.setLength(50);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Avg".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Avg", false)) {
                                    Field obj = new Field("G_" + id + "_Avg");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Max".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Max", false)) {
                                    Field obj = new Field("G_" + id + "_Max");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Min".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Min", false)) {
                                    Field obj = new Field("G_" + id + "_Min");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("XiShu".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_XiShu", false)) {
                                    Field obj = new Field("G_" + id + "_XiShu");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Order".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Order", false)) {
                                    Field obj = new Field("G_" + id + "_Order");
                                    obj.setDatatype(DataType.INT);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("UMOrd".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_UMOrd", false)) {
                                    Field obj = new Field("G_" + id + "_UMOrd");
                                    obj.setDatatype(DataType.INT);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Mark".equalsIgnoreCase(temp)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Mark", false)) {
                                    Field obj = new Field("G_" + id + "_Mark");
                                    obj.setDatatype(DataType.STRING);
                                    obj.setLength(50);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if (temp.indexOf("Body") != -1) {
                                String bodyid = temp.replaceAll("Body", "");
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_B_" + ("-1".equals(bodyid)?"X1":bodyid), false)) {
                                    Field obj = new Field("G_" + id + "_B_"
                                            + ("-1".equals(bodyid)?"X1":bodyid));
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if (temp.indexOf("Item") != -1) {
                                String itemid = temp.replaceAll("Item", "");
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Item" + itemid, false)) {
                                    Field obj = new Field("G_" + id + "_Item"
                                            + itemid);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_" + temp, false)) {
                                    Field obj = new Field("G_" + id + "_" + temp);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            }
                        }
                    } else {
                        if (!dbWizard.isExistField(tablename, "G_" + id, false)) {
                            Field obj = new Field("G_" + id);
                            obj.setDatatype(DataType.FLOAT);
                            obj.setLength(12);
                            obj.setDecimalDigits(6);
                            obj.setKeyable(false);
                            table.addField(obj);
                            flag = true;
                        }
                    }

                    String HZMenus = (String) abean.get("HZMenus");
                    String temp2 = "";
                    String temp3 = "";
                    if (HZMenus != null && HZMenus.trim().length() > 0) {
                        String[] temps = HZMenus.split(",");
                        for (int j = 0; j < temps.length; j++) {
                            String temp = temps[j].trim();
                            String temp1 = "";

                            if (temp.indexOf(":") != -1) {
                                temp1 = temp.substring(0, temp.indexOf(":"));
                                temp2 = temp.substring(temp.indexOf(":") + 1);
                            }
                            if (temp1.length() == 0) {
                                continue;
                            }
                            temp3 = "_Z" + temp2;
                            if ("score".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + temp3, false)) {
                                    Field obj = new Field("G_" + id + temp3);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Grade".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Grade" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Grade"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.STRING);
                                    obj.setLength(50);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Avg".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Avg" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Avg"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Max".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Max" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Max"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Min".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Min" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Min"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("XiShu".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_XiShu" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_XiShu"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Order".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Order" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Order"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.INT);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("UMOrd".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_UMOrd" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_UMOrd"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.INT);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if ("Mark".equalsIgnoreCase(temp1)) {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Mark" + temp3 + "", false)) {
                                    Field obj = new Field("G_" + id + "_Mark"
                                            + temp3 + "");
                                    obj.setDatatype(DataType.STRING);
                                    obj.setLength(50);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if (temp1.indexOf("Body") != -1) {
                                String bodyid = temp1.replaceAll("Body", "");
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_B_" + ("-1".equals(bodyid)?"X1":bodyid) + temp3, false)) {
                                    Field obj = new Field("G_" + id + "_B_"
                                            + ("-1".equals(bodyid)?"X1":bodyid) + temp3);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else if (temp1.indexOf("Item") != -1) {
                                String itemid = temp1.replaceAll("Item", "");
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_Item" + itemid + temp3, false)) {
                                    Field obj = new Field("G_" + id + "_Item"
                                            + itemid + temp3);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            } else {
                                if (!dbWizard.isExistField(tablename, "G_" + id
                                        + "_" + temp1 + temp3, false)) {
                                    Field obj = new Field("G_" + id + "_" + temp1
                                            + temp3);
                                    obj.setDatatype(DataType.FLOAT);
                                    obj.setLength(12);
                                    obj.setDecimalDigits(6);
                                    obj.setKeyable(false);
                                    table.addField(obj);
                                    flag = true;
                                }
                            }
                        }
                    } else {
                        if (!dbWizard.isExistField(tablename, "G_" + id + temp3,
                                false)) {
                            Field obj = new Field("G_" + id + temp3);
                            obj.setDatatype(DataType.FLOAT);
                            obj.setLength(12);
                            obj.setDecimalDigits(6);
                            obj.setKeyable(false);
                            table.addField(obj);
                            flag = true;
                        }
                    }

                }
                if(flag){
                    dbWizard.addColumns(table);// 更新列
                    dbmodel.reloadTableModel(tablename);
                }
	  			String sqlstr = "update " + tablename + " set a0100=object_id";
	  			dao.update(sqlstr);
	  	    	try
	  	    	{
		  			YksjParser yp = new YksjParser(userView,getSelectList(planId,1,userView), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
		  			yp.setVerify(false);
		  			yp.run(formula.trim(), this.conn, "", tablename); 
		  			formulaSql = yp.getSQL(); 
	  	    	}
	  	    	catch(Exception ee)
	  	    	{
	  	    		
	  	    	}
	  			
	  	    }
	  	   
  	    }
    	catch(Exception e)
	    {
	  	    	e.printStackTrace();
	  	}
  	    return formulaSql;
    	
    }
    
    
    // 控制总分相同对象个数， JinChunhai 2012.07.05
	public String validateSameScoreAllNumLess(String plan_id,String object_id,String mainbody_id,String flag,String SameAllScoreNumLess)
	{
		String info="";
		RowSet rs = null;
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(SameAllScoreNumLess!=null && SameAllScoreNumLess.trim().length()>0 && !"0".equals(SameAllScoreNumLess))
			{				
				BigDecimal one = new BigDecimal("1");
				BigDecimal zero = new BigDecimal("0");
				BigDecimal sasn = new BigDecimal(SameAllScoreNumLess);
				rowSet = dao.search("select count(*) as num,"+Sql_switcher.isnull("score","0")+" score from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"' group by score");
				rs = dao.search("select count(*) as num,"+Sql_switcher.isnull("score","0")+" score from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"' group by score");
				String alertstr="";
				if(sasn.compareTo(one)>0)//按个数
				{
					
				}else
				{
					alertstr="%";
					rowSet = dao.search("select count(*) as num from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"'");
					while(rowSet.next())
					{
						String all=rowSet.getString(1);
						BigDecimal allbig=new BigDecimal(all);
						sasn = allbig.multiply(sasn).divide(one, 0,BigDecimal.ROUND_HALF_UP);
					}
				}
				while(rs.next())
				{
					String score=rs.getString(2);
					BigDecimal scoreBig = new BigDecimal(score);
					if(scoreBig.compareTo(zero)==0)//
                    {
                        continue;
                    }
					BigDecimal allSame = new BigDecimal(rs.getString(1));
					if(allSame.compareTo(new BigDecimal("1"))==0)//只有一个人不控制
                    {
                        continue;
                    }
					if(allSame.compareTo(sasn)>=0)
					{
						if("".equals(alertstr))
						{
					    	info=ResourceFactory.getProperty("performance.batchgrade.info12")+SameAllScoreNumLess+ResourceFactory.getProperty("performance.batchgrade.info13");
						}else
						{
							BigDecimal temp=new BigDecimal(SameAllScoreNumLess);
						    BigDecimal tmp = temp.multiply(new BigDecimal("100"));
							info=ResourceFactory.getProperty("performance.batchgrade.info12")+(tmp.toString())+alertstr+ResourceFactory.getProperty("performance.batchgrade.info14");
						}
						break;
					}
				}
			}
			if(info.length()>0 && "2".equals(flag))
			{
				if(object_id.indexOf("/")!=-1)
				{
					String sqlStr = "update per_mainbody set status=? where plan_id=? and mainbody_id=? and object_id=?";
					ArrayList mainInfoList = new ArrayList();
					String[] userid = object_id.split("/");
					for(int i=0;i<userid.length;i++)
					{								
						ArrayList tempList = new ArrayList();
						tempList.add("1");
						tempList.add(new Integer(plan_id));
						tempList.add(mainbody_id);
						tempList.add(userid[i]);	
						mainInfoList.add(tempList);
					}	
					dao.batchUpdate(sqlStr, mainInfoList);
				}	
				else {
                    dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
                }
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return info;
	}

	// 获得主体的打分状态
    public HashMap getObjStatusMap(String plan_id,String mainbody_id,String objectIDs)
    {

		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
		    rowSet = dao.search("select object_id,status from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+ mainbody_id +"' and object_id in ("+objectIDs+") ");
		    while (rowSet.next())
		    {
				map.put(rowSet.getString("object_id"), isNull(rowSet.getString("status")));
		    }		    		    
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}	
		return map;
    }
	
    public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "0";
        }
		return str;
    }
    /**
         * 取得某考核计划下 主体 给各考核对象 打分的分值/排名（考核对象的总分） 以及 按总分排名的考核对象顺序列表
         * 
         * @param plan_id
         *                计划id
         * @param mainbody_id
         *                考核主体id
         * @param template_id
         *                模版id
         * @param KeepDecimal
         *                小数位
         * @return
         */
    public HashMap getObjectTotalScore(int plan_id, String mainbody_id, String template_id,UserView userView)
    {

	HashMap map = new HashMap();
	ArrayList objectList = new ArrayList();

	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    // long a_time=System.currentTimeMillis();
	    HashMap pointMap = getPerPointByTemplateid(template_id);
	    ArrayList list = new ArrayList();
	    execute_torder_table(this.object_type,mainbody_id,String.valueOf(plan_id));

	    Hashtable htxml = new Hashtable();
	    
	    if(loadxml==null)
	    {
	    	if(planLoadXmlMap.get(String.valueOf(plan_id))==null)
	    	{
	    				loadxml = new LoadXml(this.conn, String.valueOf(plan_id));
	    				planLoadXmlMap.put(String.valueOf(plan_id),loadxml);
	    	}
	    	else {
                loadxml=(LoadXml)planLoadXmlMap.get(String.valueOf(plan_id));
            }
	    }
	    
	    htxml = loadxml.getDegreeWhole();
	    String KeepDecimal = (String) htxml.get("KeepDecimal"); // 小数位

	    /*
                 * rowSet=dao.search("select distinct object_id from
                 * per_mainbody where mainbody_id<>object_id and
                 * plan_id="+plan_id); RowSet rowSet2=null; while(rowSet.next()) {
                 * String a_objectID=rowSet.getString("object_id");
                 * 
                 * StringBuffer sql = new StringBuffer("select
                 * permain.object_id,"); StringBuffer sql_select = new
                 * StringBuffer(""); StringBuffer sql_from = new StringBuffer("
                 * from (");
                 * 
                 * sql_from.append("select object_id from per_mainbody where
                 * plan_id=" + plan_id + " and mainbody_id='" + mainbody_id + "'
                 * and object_id<>'" + mainbody_id + "' "); sql_from.append(")
                 * permain"); if(this.object_type.equals("2")) sql_from.append("
                 * left join per_object po on permain.object_id=po.object_id and
                 * po.plan_id="+plan_id);
                 * 
                 * for (int i = 0; i < pointList.size(); i++) { LazyDynaBean
                 * abean = (LazyDynaBean) pointList.get(i); String point_id =
                 * (String) abean.get("point_id"); String rank = (String)
                 * abean.get("rank");
                 * 
                 * String arank=getRankByObjectID(rank,a_objectID,point_id);
                 * sql_select.append("+" + Sql_switcher.isnull("t_" + point_id +
                 * ".score", "0") + "*" + arank); sql_from.append(" left join
                 * (select * from per_table_" + plan_id + " where mainbody_id='" +
                 * mainbody_id + "' and point_id='" + point_id + "' ) t_" +
                 * point_id + " on permain.object_id=t_" + point_id +
                 * ".object_id ");
                 *  }
                 * 
                 * sql.append(sql_select.substring(1) + " score ");
                 * if(this.object_type.equals("2")) //人员
                 * sql.append(",po.b0110,po.e0122,po.a0000"); else
                 * if(this.object_type.equals("1")) //部门
                 * sql.append(",permain.object_id ");
                 * sql.append(sql_from.toString()+" where
                 * permain.object_id='"+a_objectID+"' ");
                 * System.out.println(sql.toString());
                 * rowSet2=dao.search(sql.toString()); if(rowSet2.next()) {
                 * String tableName="tSortDepart"; //部门
                 * if(object_type.equals("2")) //人员 { tableName="tSortPerson"; }
                 * 
                 * RecordVo vo=new RecordVo(tableName);
                 * vo.setString("object_id",rowSet2.getString("object_id"));
                 * vo.setDouble("score",rowSet2.getDouble("score"));
                 * 
                 * if(this.object_type.equals("2")) //人员 { String
                 * b0110=rowSet2.getString("b0110")!=null?rowSet2.getString("b0110"):"";
                 * String
                 * e0122=rowSet2.getString("e0122")!=null?rowSet2.getString("e0122"):"";
                 * String
                 * a0000=rowSet2.getString("a0000")!=null?rowSet2.getString("a0000"):"0";
                 * vo.setString("b0110",b0110); vo.setString("e0122",e0122);
                 * vo.setInt("a0000",Integer.parseInt(a0000));
                 *  } list.add(vo); } }
                 */
	    // /////////////////////////////////////////////////////////////////////////
	    HashMap resultMap = getObjectResultMap(plan_id, mainbody_id);
	    String sql = "select distinct per_mainbody.object_id,per_object.a0000,per_object.b0110,per_object.e0122  from per_mainbody,per_object where per_mainbody.object_id=per_object.object_id ";
	    if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval)) {
            sql += " and  per_mainbody.mainbody_id<>per_mainbody.object_id ";
        }
	    sql += " and per_object.plan_id=" + plan_id + "  and per_mainbody.plan_id=" + plan_id;
	    sql+=" and per_mainbody.mainbody_id='"+mainbody_id+"'";
	    rowSet = dao.search(sql);
	    while (rowSet.next())
		{
			String a_objectID = rowSet.getString("object_id");
			ArrayList resultList = (ArrayList) resultMap.get(a_objectID);
	
			String score = "0";
			if (resultList != null && resultList.size() > 0)
			{
			    //for (int i = 0; i < pointList.size(); i++)
				Set keySet=pointMap.keySet();
				for(Iterator t=keySet.iterator();t.hasNext();)
			    {
					String key=(String)t.next();
					LazyDynaBean abean = (LazyDynaBean) pointMap.get(key);
					String point_id = (String) abean.get("point_id");
					String rank = (String) abean.get("rank");
					String arank = getRankByObjectID(rank, a_objectID, point_id);
		
					LazyDynaBean a_bean = null;
					for (int j = 0; j < resultList.size(); j++)
					{
					    a_bean = (LazyDynaBean) resultList.get(j);
					    String apoint_id = (String) a_bean.get("point_id");
					    String ascore = (String) a_bean.get("score");
					    if (apoint_id.equals(point_id))
					    {
						BigDecimal a = new BigDecimal(ascore);
						BigDecimal b = new BigDecimal(arank);
						score = (new BigDecimal(score)).add(a.multiply(b)).toString();
						break;
					    }
		
					}
			    }
			}
			String tableName = "tSortDepart"; // 部门
			if ("2".equals(object_type)) // 人员
			{
			    tableName = "tSortPerson";
			}
	
			RecordVo vo = new RecordVo(tableName);
			vo.setString("object_id", a_objectID);
			vo.setDouble("score", Double.parseDouble(score));
	
			if ("2".equals(this.object_type)) // 人员
			{
			    String b0110 = rowSet.getString("b0110") != null ? rowSet.getString("b0110") : "";
			    String e0122 = rowSet.getString("e0122") != null ? rowSet.getString("e0122") : "";
			    String a0000 = rowSet.getString("a0000") != null ? rowSet.getString("a0000") : "0";
			    vo.setString("b0110", b0110);
			    vo.setString("e0122", e0122);
			    vo.setInt("a0000", Integer.parseInt(a0000));
	
			}
			vo.setInt("plan_id",plan_id);
			vo.setString("mainbody_id", mainbody_id);
			list.add(vo);
		 }

	    // ///////////////////////////////////////////////////////////////////////
	    // System.out.println("time0="+(System.currentTimeMillis()-a_time));

	    String tableName = insertValue(list, this.object_type);

	    StringBuffer ssqq = new StringBuffer("");
	    ssqq.append("select * from " + tableName+" where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"'");
	    if ("2".equals(this.object_type)) // 人员
        {
            ssqq.append(" order by score desc,a0000,b0110,e0122");
        } else if ("1".equals(this.object_type)|| "3".equals(this.object_type)|| "4".equals(this.object_type)) // 部门
        {
            ssqq.append(" order by score desc,object_id ");
        }
	    
	    
	    
	    
	    String formulaSql=getFormulaSql(userView,this.loadxml,String.valueOf(plan_id)); 
	    if ("false".equalsIgnoreCase(this.showOneMark))
	    {
	    	if(formulaSql.length()>0)
	    	{
		    	rowSet=dao.search(ssqq.toString());
		    	while (rowSet.next())
				{
				    String object_id = rowSet.getString("object_id");  
				    String value=PubFunc.round(rowSet.getString("score"), Integer.parseInt(KeepDecimal));
				    value=PubFunc.round(getFinalScore(String.valueOf(plan_id),formulaSql,object_id,value,dao), Integer.parseInt(KeepDecimal)); 
				    dao.update("update " + tableName + " set score="+value+" where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'");
				}
	    	}
	    	
			rowSet = dao.search(ssqq.toString());
			double a_score = -1;
			int num = 0;
			while (rowSet.next())
			{
			    String object_id = rowSet.getString("object_id");
			    objectList.add(object_id);
			    double value = rowSet.getDouble("score");
			    if (a_score == -1 || value != a_score)
			    {
				num++;
				a_score = value;
			    }
	
			    map.put(object_id, PubFunc.round(rowSet.getString("score"), Integer.parseInt(KeepDecimal)) + "/" + num);
			}
			map.put("objectList_order", objectList);
	    } else
	    {
			ArrayList apointList = new ArrayList();
			ArrayList aapointList = new ArrayList();
			
			Set keySet=pointMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
		    {
					String key=(String)t.next();
				    LazyDynaBean abean = (LazyDynaBean) pointMap.get(key);
				    String[] aa = new String[8];
				    aa[0] = (String) abean.get("point_id");
				    aa[2] = (String) abean.get("pointkind");
				    aa[7] = (String) abean.get("status");
				    apointList.add(aa);
		
				    if ("1".equals(aa[2]) && aa[7] != null && "1".equals(aa[7])) {
                        aapointList.add(aa[0]);
                    }
	
			}
	
			this.setUserNumberPointResultMap(getUserNumberPointResultMap(apointList, String.valueOf(plan_id)));
			map = getAdditionScoreMap(String.valueOf(plan_id),ssqq.toString(), aapointList, Integer.parseInt(KeepDecimal),formulaSql);

	    }
	     
	    
	    /*
	    
	    
	    
	    
	    String formula = "";
	    ArrayList formulalist = loadxml.getRelatePlanValue("Formula", "Caption");
		
		if (formulalist.size() > 0)
			formula = formulalist.get(0).toString();
		else
			formula = "[本次得分]"; 
	    if(formula!=null&&formula.trim().length()>0&&!formula.equals("[本次得分]"));
	    {
	    
	    	String tablename = "per_result_" + this.planid;
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.conn);
			DBMetaModel dbmodel = new DBMetaModel(this.conn);
			if (!dbWizard.isExistField(tablename, "A0100", false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(50);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}
			String sqlstr = "update " + tablename + " set a0100=object_id";
			dao.update(sqlstr);
			YksjParser yp = new YksjParser(userView,getSelectList(this.planid,1,userView), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
			yp.setVerify(false);
			yp.run(formula.trim(), this.conn, "", tablename); 
			sql = yp.getSQL();
			
			
			for(Iterator t=map.keySet().iterator();t.hasNext();)
			{
				String objectid=(String)t.next();
				if(objectid.equals("objectList_order"))
					continue;
				String values=(String)map.get(objectid);
				System.out.println("="+values);
				sql=sql.replaceAll("ISNULL(score,0)","0");
		    	rowSet=dao.search("select "+sql+" as totalscore,object_id from per_result_"+this.planid+" where object_id='"+objectid+"'");
		    	if(rowSet.next())
		    	{
		    		
		    	}
		    	
			}
	    	
	    }
	    */
	    
	    // System.out.println("time1="+(System.currentTimeMillis()-a_time));
	    if(resultMap.size()==0) {
            map.remove("objectList_order");
        }
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	return map;
    }
    
    
    
    public String getFinalScore(String plan_id,String formulaSql,String object_id,String score,ContentDAO dao)
    {
    	try
    	{
    		if(formulaSql.length()>0)
    		{
	    		formulaSql=formulaSql.replaceAll("ISNULL\\(score,0\\)",score);
	    	//	System.out.println("select "+formulaSql+" as totalscore  from per_result_"+this.planid+" where object_id='"+object_id+"'");
		    	RowSet rowSet=dao.search("select "+formulaSql+" as totalscore  from per_result_"+plan_id+" where object_id='"+object_id+"'");
		    	if(rowSet.next())
		    	{
		    		score=rowSet.getString("totalscore");
		    	}
		    	if(rowSet!=null) {
                    rowSet.close();
                }
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return score;
    }
    
    
    
    
    

    public ArrayList getSelectList(String planid,int flag,UserView userView)
	{
    	ComputFormulaBo bo=new  ComputFormulaBo("custom_formula",this.conn,planid,userView);	 
		return bo.getSelfFields(planid, flag);
	}
    

    public String insertValue(ArrayList valueList, String object_type)
    {

	String tableName = "tSortDepart"; // 部门
	if ("2".equals(object_type)) // 人员
	{
	    tableName = "tSortPerson";
	}
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
	    dao.addValueObject(valueList);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return tableName;
    }

    // 判断是否存在排序临时表，如果没有则产生一个
    public void execute_torder_table(String object_type,String mainbody_id,String plan_id) throws GeneralException
    {

	try
	{
	    String tableName = "tSortDepart"; // 部门
	    if ("2".equals(object_type)) // 人员
	    {
	    	tableName = "tSortPerson";
	    }
	    
	    DbWizard dbWizard = new DbWizard(this.conn);
	    if (!dbWizard.isExistTable(tableName, false))
	    {
	
			Table table = new Table(tableName);
			ArrayList fieldList = getTableFields(object_type);
			for (Iterator t = fieldList.iterator(); t.hasNext();)
			{
			    Field temp = (Field) t.next();
			    table.addField(temp);
			}
			table.setCreatekey(false);
			dbWizard.createTable(table);
			DBMetaModel dbmodel = new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(table.getName());
		} 
	    else
	    {
			
			RecordVo vo=new RecordVo(tableName);
			if(!(vo.hasAttribute("mainbody_id")||vo.hasAttribute("MAINBODY_ID")))
			{
				Table table=new Table(tableName);
				Field field=new Field("mainbody_id","mainbody_id");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				table.addField(field);
				DbWizard dbw=new DbWizard(this.conn);
				dbw.addColumns(table);	
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(table.getName());
			}
			if(!(vo.hasAttribute("plan_id")||vo.hasAttribute("PLAN_ID")))
			{
				Table table=new Table(tableName);
				Field field=new Field("plan_id","plan_id");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				DbWizard dbw=new DbWizard(this.conn);
				dbw.addColumns(table);	
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(table.getName());
			}
		//	dao.delete("delete from " + tableName+" where mainbody_id='"+mainbody_id+"'", new ArrayList());
	    }
	 //   System.out.println("delete from " + tableName+" where mainbody_id='"+mainbody_id+"' and plan_id="+plan_id);
	    dbWizard.execute("delete from " + tableName+" where mainbody_id='"+mainbody_id+"' and plan_id="+plan_id);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);

	}

    }

    public ArrayList getTableFields(String object_type)
    {

	ArrayList fieldsList = new ArrayList();
	fieldsList.add(getField("object_id", ResourceFactory.getProperty("lable.performance.perObject"), "A", 30));
	fieldsList.add(getField("score", ResourceFactory.getProperty("lable.performance.perScore"), "N", 0));
	if ("2".equals(object_type))
	{
	    fieldsList.add(getField("b0110", ResourceFactory.getProperty("hmuster.label.unitNo"), "A", 30));
	    fieldsList.add(getField("e0122", ResourceFactory.getProperty("hmuster.label.departmentNo"), "A", 30));
	    fieldsList.add(getField("a0000", ResourceFactory.getProperty("hmuster.label.personSerial"), "I", 0));
	}
	fieldsList.add(getField("mainbody_id", ResourceFactory.getProperty("lable.performance.perMainBody"), "A", 30));
	fieldsList.add(getField("plan_id", ResourceFactory.getProperty("lable.performance.perPlan"), "I", 30));
	
	return fieldsList;
    }

    public Field getField(String fieldname, String desc, String type, int length)
    {

	Field obj = new Field(fieldname, desc);
	if ("A".equals(type))
	{
	    obj.setDatatype(DataType.STRING);
	    obj.setKeyable(false);
	    obj.setVisible(false);
	    obj.setLength(length);
	    obj.setAlign("left");
	} else if ("M".equals(type))
	{
	    obj.setDatatype(DataType.CLOB);
	    obj.setKeyable(false);
	    obj.setVisible(false);
	    obj.setAlign("left");
	} else if ("D".equals(type))
	{

	    obj.setDatatype(DataType.DATE);
	    obj.setKeyable(false);
	    obj.setVisible(false);
	    obj.setAlign("right");
	} else if ("N".equals(type))
	{
	    obj.setDatatype(DataType.FLOAT);
	    obj.setDecimalDigits(6);
	    obj.setLength(15);
	    obj.setKeyable(false);
	    obj.setVisible(false);
	    obj.setAlign("left");

	} else if ("I".equals(type))
	{
	    obj.setDatatype(DataType.INT);
	    obj.setKeyable(false);
	    obj.setVisible(false);

	}
	return obj;
    }

    public HashMap getAdditionScoreMap(String plan_id,String sql,ArrayList aapointList,int KeepDecimal,String formulaSql)
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	ArrayList aobjectList = new ArrayList();
	try
	{
	    rowSet = dao.search(sql);
	    ArrayList list = new ArrayList();
	    while (rowSet.next())
	    {
		String object_id = rowSet.getString("object_id");
		double value = rowSet.getDouble("score");
		HashMap userMap = (HashMap) this.userNumberPointResultMap.get(object_id);
		for (int i = 0; i < aapointList.size() && userMap!=null; i++)
		{
		    String point_id = (String) aapointList.get(i);
		    if (userMap.get(point_id) != null && !"".equals((String) userMap.get(point_id)))
		    {
			value += Double.parseDouble((String) userMap.get(point_id));
		    }
		}
		String avalue = PubFunc.round(String.valueOf(value), KeepDecimal);
		
		
		if(formulaSql.length()>0)
    	{ 
			avalue=PubFunc.round(getFinalScore(plan_id,formulaSql,object_id,avalue,dao), KeepDecimal);   
    	}
		
		String[] aa = { object_id, avalue };
		list.add(aa);
	    }
	    LinkedList alist = getOrderList(list);

	    String a_score = "-1";
	    int num = 0;
	    for (int j = 0; j < alist.size(); j++)
	    {
		String[] a1 = (String[]) alist.get(j);
		aobjectList.add(a1[0]);
		if ("-1".equals(a_score) || !a1[1].equals(a_score))
		{
		    num++;
		    a_score = a1[1];
		}

		map.put(a1[0], a1[1] + "/" + num);
	    }
	    map.put("objectList_order", aobjectList);

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	return map;
    }

    /**
         * 插入排序
         * 
         * @return
         */
    public LinkedList getOrderList(ArrayList fff)
    {

	LinkedList tempList = new LinkedList();
	for (int j = 0; j < fff.size(); j++)
	{
	    String[] aa = (String[]) fff.get(j);
	    if (tempList.size() > 0)
	    {
		int num = 0;
		for (int i = 0; i < tempList.size(); i++)
		{
		    String[] a1 = (String[]) tempList.get(i);
		    if (Double.parseDouble(a1[1]) < Double.parseDouble(aa[1]))
		    {
			num++;
			tempList.add(i, aa);
			break;
		    }
		}
		if (num == 0) {
            tempList.add(aa);
        }
	    } else
	    {
		tempList.add(aa);
	    }
	}
	return tempList;
    }
    
    
    
    
    /**
     * 表头考核要素后内容
     * @param NodeKnowDegree
     * @param WholeEval
     * @param SummaryFlag
     * @param lays
     * @param flag
     * @param num
     * @param showNoMarking
     * @return
     * @throws GeneralException 
     */
    private String getExtendItem(String NodeKnowDegree, String WholeEval, String SummaryFlag, int lays, int flag, int num, String showNoMarking) throws GeneralException
    {

	StringBuffer tableHtml = new StringBuffer("");
	if ("true".equals(NodeKnowDegree))
	{
	    tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' ");
	    if (flag == 0) {
            tableHtml.append("rowspan='" + lays + "' ");
        }
	    tableHtml.append(" width='"+this.columnWidth+"' nowrap > ");
	    if (flag == 0) {
            tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("lable.statistic.knowdegree")+"</font>");
        } else
	    {
	    	if(!this.isBatchGradeRadio) {
                tableHtml.append("<font size='1' color='#97C8F9' >" + (++num) + "</font>");
            } else {
                tableHtml.append("&nbsp;");
            }
	    }

	    tableHtml.append("</td>");
	}
	//总体评价
	if ("true".equals(WholeEval) || "True".equalsIgnoreCase(this.DescriptiveWholeEval))
	{
	    tableHtml.append("<td class='header_locked  common_background_color common_border_color' id='b'  valign='middle' align='center' ");
	    if (flag == 0) {
            tableHtml.append("rowspan='" + lays + "' ");
        }
	    tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
	    if (flag == 0) {
            tableHtml.append("&nbsp;<font class='fontStyle_self' >"+ ResourceFactory.getProperty("lable.statistic.wholeeven")+"</font>");
        } else
		{
	    	if(!this.isBatchGradeRadio) {
                tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
            } else {
                tableHtml.append("&nbsp;");
            }
		}
	    tableHtml.append("</td>");
	}

	if(!(SystemConfig.getPropertyValue("goalAtFirst")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("goalAtFirst").trim())))
	{
		if ("True".equalsIgnoreCase(this.noteIdioGoal))
		{
		 //   if (this.planVo.getInt("object_type") == 2)
		    {
			tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
			if (flag == 0) {
                tableHtml.append(" rowspan='" + lays + "'");
            }
			//tableHtml.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > ");
			tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
			if (flag == 0)
			{
				String desc=ResourceFactory.getProperty("lable.performance.perGoal");//绩效目标
		    	if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) {
                    desc=ResourceFactory.getProperty("performance.batchgrade.info11");
                }
			    tableHtml.append("<font class='fontStyle_self' >"+desc+"</font>");
			} else
			{
				if(!this.isBatchGradeRadio) {
                    tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
                } else {
                    tableHtml.append("&nbsp;");
                }
			}
			tableHtml.append("</td>");
		    }
	
		}
	
		if ("True".equalsIgnoreCase(SummaryFlag))//绩效报告
		{
		 //   if (this.planVo.getInt("object_type") == 2)
		    {
			tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
			if (flag == 0) {
                tableHtml.append(" rowspan='" + lays + "'");
            }
			//tableHtml.append("width='"+PubFunc.round(String.valueOf(this.columnWidth/1.5),0)+"' nowrap > ");
			tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
			if (flag == 0)
			{
			    if ("0".equals(this.performanceType))
			    {
					try
					{
					    String info = SystemConfig.getPropertyValue("per_examineInfo");
					    if (info == null || info.length() == 0) {
                            tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("lable.performance.perSummary")+"</font>");
                        } else {
                            tableHtml.append("<font class='fontStyle_self' >"+(new String(info.getBytes("ISO-8859-1"), "GBK"))+"</font>");
                        }
					} catch (Exception e)
					{
		
					}
			    }
			    else if ("1".equals(this.performanceType)) {
                    tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.reportwork.report")+"</font>");
                }
			} else
			{
				if(!this.isBatchGradeRadio) {
                    tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
                } else {
                    tableHtml.append("&nbsp;");
                }
			}
			tableHtml.append("</td>");
		    }
	
		}
	
	}
	
	//显示员工日志 2011-01-24
	if("True".equalsIgnoreCase(this.ShowEmployeeRecord))
	{
		tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
		if (flag == 0) {
            tableHtml.append(" rowspan='" + lays + "'");
        }
		tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
		if (flag == 0)
		{ 
				String desc=ResourceFactory.getProperty("label.performance.staffDiary");
		    	if(!"2".equals(object_type)) {
                    desc=ResourceFactory.getProperty("label.performance.managerDiary");
                }
		    	tableHtml.append("<font class='fontStyle_self' >"+desc+"</font>");
		} else
		{
			if(!this.isBatchGradeRadio) {
                tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
            } else {
                tableHtml.append("&nbsp;");
            }
		}
		tableHtml.append("</td>");
	}
	
//	//自我评分
//	if (this.isSelfScoreColumn)
//	{
//	//    if (this.planVo.getInt("object_type") == 2)
//	    {
//			tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
//			if (flag == 0)
//			    tableHtml.append(" rowspan='" + lays + "'");
//			tableHtml.append("width='"+this.columnWidth_lower+"' nowrap > ");
//			if (flag == 0)
//			{
//			    tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.selfEvaluate")+"</font>");
//			} else
//			{
//				if(!this.isBatchGradeRadio)
//					tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
//				else
//		    		tableHtml.append("&nbsp;");
//			}
//			tableHtml.append("</td>");
//	    }
//
//	}
	boolean isallowSeeLowerGrade=false;
	if(this.allowSeeLowerGrade!=null && this.allowSeeLowerGrade.trim().length()>0 && "True".equalsIgnoreCase(this.allowSeeLowerGrade)) {
        isallowSeeLowerGrade=true;
    }
	
	//插入标题
	if(isallowSeeLowerGrade||this.isSelfScoreColumn){
		if(this.getMainBodyMap().size()>0){
			Iterator iter = mainBodyMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if((isallowSeeLowerGrade&&!"5".equals(key.toString()))||(this.isSelfScoreColumn&& "5".equals(key.toString()))){
					tableHtml.append("<td class='header_locked common_background_color common_border_color'  valign='middle' align='center' ");
				    if (flag == 0) {
                        tableHtml.append("rowspan='" + lays + "' ");
                    }
				    tableHtml.append(" width='"+(this.columnWidth_lower+30)+"' nowrap > ");
				    if (flag == 0){
				    	if("5".equals(key.toString())) {
                            tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.selfEvaluate")+"</font>");
                        } else {
                            tableHtml.append("<font class='fontStyle_self' >"+val+"</font>");
                        }
				    }
				    else
				    {
				    	if(!this.isBatchGradeRadio) {
                            tableHtml.append("<font size='1' color='#97C8F9' >" + (++num) + "</font>");
                        } else {
                            tableHtml.append("&nbsp;");
                        }
				    }
		
				    tableHtml.append("</td>");
				}
			}
		}
	}
	
	
	
//	if (this.allowSeeLowerGrade!=null && this.allowSeeLowerGrade.trim().length()>0 && this.allowSeeLowerGrade.equalsIgnoreCase("True"))
//	{		
//		if(this.objectList!=null && this.objectList.size()>0)
//		{
//			for (Iterator t = this.objectList.iterator(); t.hasNext();)
//			{	    	
//				String[] temp = (String[]) t.next();
//				if (this.objectsLowerMap.get(temp[0]) != null && ((ArrayList)this.objectsLowerMap.get(temp[0])).size()>0)
//				{
//					this.haveLower = true;
//					break;
//				}
//			}
//		}
//					
//	    if (this.haveLower)
//	    {
//			tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
//			if (flag == 0)
//			    tableHtml.append(" rowspan='" + lays + "'");
//			tableHtml.append("width='"+this.columnWidth_lower+"' nowrap > ");
//			if (flag == 0)
//			{
//			    tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("label.kh.stateScore.LowerEvaluate")+"</font>");
//			} else
//			{
//				if(!this.isBatchGradeRadio)
//					tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
//				else
//		    		tableHtml.append("&nbsp;");
//			}
//			tableHtml.append("</td>");
//	    }
//
//	}
	//不作评价 /弃权
	if (showNoMarking != null && "true".equalsIgnoreCase(showNoMarking))
	{
	    tableHtml.append("<td class='header_locked  common_background_color common_border_color' id='b'  valign='middle' align='center' ");
	    if (flag == 0) {
            tableHtml.append(" rowspan='" + lays + "'");
        }
	    tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
	    if (flag == 0)
	    {
		if ("0".equals(this.performanceType)) {
            tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("lable.performnace.noMarkCause")+"</font>");
        } else if ("1".equals(this.performanceType)) {
            tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.forfeit")+"</font>");
        }
	    } else
		{
	    	if(!this.isBatchGradeRadio) {
                tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
            } else {
                tableHtml.append("&nbsp;");
            }
		}
	    tableHtml.append("</td>");

	}
//其它意见和建议
	if ("1".equals(this.performanceType))
	{
	    tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
	    if (flag == 0) {
            tableHtml.append(" rowspan='" + lays + "'");
        }
	    tableHtml.append("width='"+this.columnWidth+"' nowrap > ");
	    if (flag == 0)
	    {
		tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.otherInfo")+"</font>");
	    } else
		{
	    	if(!this.isBatchGradeRadio) {
                tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
            } else {
                tableHtml.append("&nbsp;");
            }
		}
	    tableHtml.append("</td>");

	}

	// 北京市监狱局要求把总分列和排名列放到姓名后面 JinChunhai 2012.09.13
	if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
	{}
	else
	{
		// 总分
		if ("true".equalsIgnoreCase(this.isShowTotalScore))
		{
	
		    tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center'  ");
		    if (flag == 0) {
                tableHtml.append(" rowspan='" + lays + "'");
            }
		    tableHtml.append("width='"+this.columnWidth_lower+"' nowrap > ");
		    if (flag == 0)
		    {
			tableHtml.append("&nbsp;<font class='fontStyle_self' >" + ResourceFactory.getProperty("label.kh.stateScore.totalScore")+"</font>");
			// if(this.isAutoCountTotalOrder.equalsIgnoreCase("false"))
			// tableHtml.append("+");
		    } else
			{
		    	if(!this.isBatchGradeRadio) {
                    tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
                } else {
                    tableHtml.append("&nbsp;");
                }
			}
		    tableHtml.append("</td>");
		}
	
		// 排名
		if ("true".equalsIgnoreCase(this.isShowOrder))
		{
		    tableHtml.append("<td class='header_locked  common_background_color common_border_color'  valign='middle' align='center' ");
		    if (flag == 0) {
                tableHtml.append(" rowspan='" + lays + "'");
            }
		    tableHtml.append("width='"+this.columnWidth_lower+"' nowrap > ");
		    if (flag == 0)
		    {
			tableHtml.append(" &nbsp;&nbsp;<font class='fontStyle_self' >" + ResourceFactory.getProperty("kh.field.pm")+"</font>");
			// if(this.isAutoCountTotalOrder.equalsIgnoreCase("false"))
			// tableHtml.append("+");
		    } else
			{
		    	if(!this.isBatchGradeRadio) {
                    tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
                } else {
                    tableHtml.append("&nbsp;");
                }
			}
		    tableHtml.append("</td>");
	
		}
	}
	//最终评估结果
	if(this.userView.hasTheFunction("06060103"))
	{
	//  if (this.planVo.getInt("object_type") == 2)
	    {
			tableHtml.append("<td class='header_locked common_background_color common_border_color' valign='middle' align='center' ");
			if (flag == 0) {
                tableHtml.append(" colspan='4' height='35' ");  // rowspan='" + (lays-1) + "'
            }
			tableHtml.append(" nowrap > ");  // width='"+(this.columnWidth*3-20)+"'
			if (flag == 0)
			{
			    tableHtml.append("<font class='fontStyle_self' >"+ResourceFactory.getProperty("performance.batchgrade.LastEvaluateResult")+"</font>");
			} else
			{
				if(!this.isBatchGradeRadio) {
                    tableHtml.append("<font  size='1' color='#97C8F9' >" + (++num) + "</font>");
                } else {
                    tableHtml.append("&nbsp;");
                }
			}
			tableHtml.append("</td>");
	    }
	}

	return tableHtml.toString();
    }

    /**
         * 得到模版下各指标的分值及最大上限值和最小下限值
         * 
         * @param templateID
         *                模版id
         * @return
         */
    public HashMap getPerPointScore(String templateID) throws GeneralException
    {

	HashMap hashMap = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    StringBuffer sql1 = new StringBuffer(
		    "select po.point_id,pp.score,max(pg.top_value) top_value,min(pg.bottom_value) bottom_value ,min(pg.gradecode) min_gradecode,max(pg.gradecode) max_gradecode,po.pointkind ,tt.t,tt.b ");
	    sql1
		    .append("from per_template_item pi,per_template_point pp,per_point po ,per_grade pg ,(select a.point_id,a.top_value t ,a.bottom_value b from per_grade a where a.top_value=(select max(top_value) from per_grade b  where a.point_id=b.point_id)) tt ");
	    sql1.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and po.point_id=tt.point_id  and template_id='");
	    sql1.append(templateID);
	    sql1.append("' ");
	    sql1.append(" group by po.point_id,po.pointkind,pp.score,tt.t,tt.b ");
	    rowSet = dao.search(sql1.toString());
	    while (rowSet.next())
	    {
		String[] temp = new String[9];
		for (int i = 0; i < 9; i++)
		{
		    temp[i] = rowSet.getString(i + 1);
		}
		hashMap.put(temp[0], temp);
	    }

	    StringBuffer sql2 = new StringBuffer("select po.point_id,po.pointname,po.pointkind,pi.item_id ");
	    sql2.append(" from per_template_item pi,per_template_point pp,per_point po ");
	    sql2.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID + "'  order by pp.seq");
	    int i = 0;
	    rowSet = dao.search(sql2.toString());
	    while (rowSet.next()) {
            i++;
        }
	    if (i != hashMap.size())
	    {
		HashMap tempMap = new HashMap();
		StringBuffer sql = new StringBuffer(
			"select po.point_id,pp.score,max(pg.top_value) top_value,min(pg.bottom_value) bottom_value ,min(pg.gradecode) min_gradecode,max(pg.gradecode) max_gradecode,po.pointkind,0 t,0 b ");
		sql.append("from per_template_item pi,per_template_point pp,per_point po ,per_grade pg  ");
		sql.append(" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id   and template_id='");
		sql.append(templateID);
		sql.append("' ");
		sql.append(" group by po.point_id,po.pointkind,pp.score");

		RowSet rowSet2 = dao.search(sql.toString());
		while (rowSet2.next())
		{
		    String[] temp2 = new String[9];

		    temp2[0] = rowSet2.getString("point_id");
		    temp2[1] = rowSet2.getString("score");
		    temp2[2] = rowSet2.getString("top_value");
		    temp2[3] = rowSet2.getString("bottom_value");
		    temp2[4] = rowSet2.getString("min_gradecode");
		    temp2[5] = rowSet2.getString("max_gradecode");
		    temp2[6] = rowSet2.getString("pointkind");
		    temp2[7] = rowSet2.getString("t");
		    temp2[8] = rowSet2.getString("b");

		    tempMap.put(temp2[0], temp2);
		}

		return tempMap;
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	/*
         * finally { try { if(rowSet!=null) { rowSet.close(); } }
         * catch(Exception ee) { ee.printStackTrace(); } }
         */
	return hashMap;
    }
    
    /**
     * 按岗位素质模型测评  得到模版下各指标的分值及最大上限值和最小下限值  郭峰
     * 
     */
    
    public HashMap getCompetencyPerPointScore(String e01a1) throws GeneralException
    {

    	HashMap hashMap = new HashMap();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    	    StringBuffer sql1 = new StringBuffer("select po.point_id,pcm.score,max(pg.top_value) top_value,min(pg.bottom_value) bottom_value ,min(pg.gradecode) min_gradecode,max(pg.gradecode) max_gradecode,po.pointkind ,tt.t,tt.b ");
    	    sql1.append("from per_competency_modal pcm,per_point po ,per_grade pg ,(select a.point_id,a.top_value t ,a.bottom_value b from per_grade a where a.top_value=(select max(top_value) from per_grade b  where a.point_id=b.point_id)) tt ");
    	    sql1.append(" where pcm.point_id=po.point_id and  po.point_id=pg.point_id and po.point_id=tt.point_id  and pcm.object_type='3' and pcm.object_id ='"+e01a1+"' ");
			sql1.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
    	    sql1.append(" group by po.point_id,po.pointkind,pcm.score,tt.t,tt.b ");
    	    rowSet = dao.search(sql1.toString());
    	    ArrayList storeList = new ArrayList();
    	    while (rowSet.next()){
	    		String[] temp = new String[9];
	    		for (int i = 0; i < 9; i++)
	    		{
	    		    temp[i] = rowSet.getString(i + 1);
	    		}
	    		if(temp[1]==null){
	    			temp[1] = "0";
	    		}
	    		hashMap.put(temp[0], temp);
	    		storeList.add(temp[0]);
	    	}
    	    sql1.setLength(0);
    	    sql1.append("select point_id,score from per_competency_modal where "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date and object_type='3' and object_id ='"+e01a1+"' ");
    	    rowSet = dao.search(sql1.toString());
    	    while(rowSet.next()){
    	    	String temppoint_id = rowSet.getString("point_id");
    	    	if(storeList.contains(temppoint_id)){
    	    		continue;
    	    	}
    	    	String tempScore = rowSet.getString("score");
    	    	if(tempScore==null || "".equals(tempScore)){
    	    		tempScore = "0";
    	    	}
    	    	String[] temp = new String[9];
    	    	temp[0] = temppoint_id;
    	    	temp[1] = tempScore;
    	    	temp[2] = "1.0";
    	    	temp[3] = "1.0";
    	    	temp[4] = "A";
    	    	temp[5] = "A";
    	    	temp[6] = "0";
    	    	temp[7] = "1.0";
    	    	temp[8] = "1.0";
    	    	hashMap.put(temp[0], temp);
    	    }
    	} catch (Exception e){
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}
    	return hashMap;
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////
    /**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList(String templateID)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+templateID+"'  order by seq");
		    LazyDynaBean abean=null;
			while(rowSet.next())
		    {
				abean=new LazyDynaBean();
		    	abean.set("item_id",rowSet.getString("item_id"));
		    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
		    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
		    	abean.set("template_id",rowSet.getString("template_id"));
		    	abean.set("itemdesc",rowSet.getString("itemdesc"));
		    	abean.set("seq",rowSet.getString("seq"));
		    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"1");	
		    	list.add(abean);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
    
	
	
    
	/**
	 * 叶子项目列表
	 *
	 */
	public void get_LeafItemList(String templateID,ArrayList pointList ,ArrayList seqList)
	{
		try
		{
			ArrayList itemList=getTemplateItemList(templateID);
			LazyDynaBean abean=null;
			for(int i=0;i<itemList.size();i++)
			{
				abean=(LazyDynaBean)itemList.get(i);
				String parent_id=(String)abean.get("parent_id");
				if(parent_id.length()==0)
				{
					setLeafItemFunc(abean,pointList,itemList,seqList);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
   //	递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean,ArrayList pointList,ArrayList itemList,ArrayList seqList)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		//判断项目下是否有指标
		
		
		
		if(child_id.length()==0)
		{
		  //this.leafItemList.add(abean);
			String itemid=(String)abean.get("item_id");
			for(int i=0;i<pointList.size();i++)
			{
				String[] temp=(String[])pointList.get(i);
				String a_itemid=temp[3];
				if(itemid.equals(a_itemid))
				{
					seqList.add(temp[0].toLowerCase());
				}
			}
			return;
		}
		LazyDynaBean a_bean=null;
		
		for(int i=0;i<pointList.size();i++)
		{
			String[] temp=(String[])pointList.get(i);
			String a_itemid=temp[3];
			if(item_id.equals(a_itemid))
			{
				seqList.add(temp[0].toLowerCase());
			}
		}
		
		for(int j=0;j<itemList.size();j++)
		{
				a_bean=(LazyDynaBean)itemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    setLeafItemFunc(a_bean,pointList,itemList,seqList);
                }
		}
	}

    
    
    
    
    /////////////////////////////////////////////////////////////////

    /**
         * 返回 某绩效模版的所有指标集
         * 
         * @param templateID
         * @return
         */
    public ArrayList getPerPointList(String templateID, String plan_id) throws GeneralException
    {
	boolean isByModelFlag  = SingleGradeBo.getByModel(plan_id,this.conn);
	boolean isHasPoint = SingleGradeBo.isHaveMatchByModel(object_id, this.conn);
	boolean isByModel = false;
	if(isByModelFlag && isHasPoint) {
        isByModel = true;
    }
	 HashMap pointMap = new HashMap();
	ArrayList list = new ArrayList();
	if(plan_perPointMap.get(plan_id)==null||!isLoadStaticValue||isByModelFlag)//按岗位素质模型测评 由于各岗位的岗位素质指标不同 故每次都得重查
	{
		ArrayList pointGrageList = new ArrayList();
		ArrayList a_pointGrageList = new ArrayList();
		ArrayList pointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String isNull = "0"; // 判断模版中指标标度上下限值是否设置
		RowSet rowSet = null;
		StringBuffer noGradeItem = new StringBuffer(",");
	
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
            per_comTable = "per_grade_competence"; // 能力素质标准标度
        }
		HashMap map2 = new HashMap();
		String sql = "select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
			+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id  and pg.gradecode=pgt.grade_template_id   and template_id='" + templateID + "' "; // pi.seq,
		if(isByModel){
			////能力素质支持一个评估计划适应多个岗位进行评估
			sql = "select case when (pp.point_type is null or pp.point_type='')  then '-9999' else pp.point_type end as item_id,po.point_id,po.pointname,po.pointkind,pg.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem,po.status,pgt.gradedesc from per_competency_modal pp,per_point po ,per_grade pg,"+per_comTable+" pgt where  pp.point_id=po.point_id and  po.point_id=pg.point_id  and pg.gradecode=pgt.grade_template_id  and "+Sql_switcher.dateValue(historyDate)+" between pp.start_date and pp.end_date and object_type='3' and object_id = '"+getE01a1(this.object_id)+"' ";
		}
		if ((noShowOneMark&& "false".equalsIgnoreCase(this.showOneMark) && this.planVo.getInt("method") != 2) || isByModel) //  2010/10/29  dengcan
        {
            sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
        }
		if(isByModel){
			sql+="order by pp.point_type,pg.grade_id";
		}else{
			sql += "  order by pp.seq,pg.grade_id"; //  gradecode";
		}
		try
		{ 
		    HashMap map = new HashMap();
		    rowSet = dao.search(sql);
		    HashMap per_grade_Map=new HashMap();
		    while (rowSet.next())
		    {
				String[] temp = new String[14];
				if(per_grade_Map.get(rowSet.getString("point_id").toUpperCase())==null) {
                    per_grade_Map.put(rowSet.getString("point_id").toUpperCase(), rowSet.getString("pointname"));
                }
				for (int i = 0; i < 14; i++)
				{
				    if (i == 2) {
                        temp[i] = Sql_switcher.readMemo(rowSet, "pointname");
                    } else if (i == 4) {
                        temp[i] = Sql_switcher.readMemo(rowSet, "gradedesc");
                    } else {
                        temp[i] = rowSet.getString(i + 1);
                    }
				    if (i == 6 || i == 7)
				    {
						if (temp[i] == null)
						{
						    isNull = "1";
						    if (map.get(rowSet.getString("point_id")) == null)
						    {
								noGradeItem.append(rowSet.getString("point_id") + ",");
								map.put(rowSet.getString("point_id"), "1");
						    }
						}
				    }
		
				}
				a_pointGrageList.add(temp);
		    }
		    sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.status,pp.score,po.Kh_content,po.Gd_principle,po.Description from per_template_item pi,per_template_point pp,per_point po "
			    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID + "' "; // pi.seq,
		    if(isByModel){
		    	////能力素质支持一个评估计划适应多个岗位进行评估
		    	sql = "select  po.point_id,po.pointname,po.pointkind,case when (pp.point_type is null or pp.point_type='')  then '-9999' else pp.point_type end as item_id,po.visible,po.fielditem,po.status,pp.score,po.status,pp.score,po.Kh_content,po.Gd_principle,po.Description from per_competency_modal pp,per_point po where pp.point_id=po.point_id and "+Sql_switcher.dateValue(historyDate)+" between pp.start_date and pp.end_date and object_type='3' and object_id = '"+getE01a1(this.object_id)+"' ";
		    }
		    if (noShowOneMark&& "false".equalsIgnoreCase(this.showOneMark) && this.planVo.getInt("method") != 2) //  2010/10/29  dengcan
            {
                sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
            }
		    
		    if(isByModel){
				sql+="order by pp.point_type,pp.object_id";
			}else{
				sql += " order by pp.seq";
			}
		    rowSet = dao.search(sql);
	
		    // 解决排列顺序问题
		    ArrayList seqList = new ArrayList();
		    ArrayList tempPointList=new ArrayList();
		    while (rowSet.next())
		    {
				String[] temp = new String[12];
				temp[0] = rowSet.getString(1);
				temp[1] = Sql_switcher.readMemo(rowSet, "pointname");
				temp[2] = rowSet.getString(3);
				temp[3] = rowSet.getString(4);
				temp[4] = "";
				temp[5] = rowSet.getString("visible");
				temp[6] = rowSet.getString("fielditem");
				temp[7] = rowSet.getString("status") != null ? rowSet.getString("status") : "0";
				temp[8] = rowSet.getString("score");
		
				temp[9] = Sql_switcher.readMemo(rowSet, "kh_content");
				temp[10] = Sql_switcher.readMemo(rowSet, "gd_principle"); 
				temp[11]=Sql_switcher.readMemo(rowSet, "description"); 
				tempPointList.add(temp);
				map2.put(temp[0].toLowerCase(), temp);
				////能力素质支持一个评估计划适应多个岗位进行评估
				if(isByModel) {
                    seqList.add(temp[0].toLowerCase());
                }
//				if(per_grade_Map.size()>0&&per_grade_Map.get(temp[0].toUpperCase())==null){
//					
//					throw GeneralExceptionHandler.Handle(new Exception("指标:"+temp[1]+"没有设置标度!"));
//				}
//				if(per_grade_Map.size()==0){
//					
//					throw GeneralExceptionHandler.Handle(new Exception("没有设置标准标度!"));
//				}
		    }
		    //这段代码不能去掉啦，否则会影响打分界面的显示问题－－－－dengcan-2009-6-8------
		    if(!isByModel) {
                get_LeafItemList(templateID,tempPointList ,seqList);
            }
		    
		    for (Iterator t = seqList.iterator(); t.hasNext();)
		    {
				String temp = (String) t.next();
				String[] atemp = (String[]) map2.get(temp); 
				pointList.add(atemp);
		    }
	
		    for (Iterator t = seqList.iterator(); t.hasNext();)
		    {
			String temp = (String) t.next();
			for (Iterator t1 = a_pointGrageList.iterator(); t1.hasNext();)
			{
			    String[] tt = (String[]) t1.next();
			    if (tt[1].toLowerCase().equals(temp)) {
                    pointGrageList.add(tt);
                }
			}
		    }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		/*
	         * finally { try { if(rowSet!=null) { rowSet.close(); } }
	         * catch(Exception ee) { ee.printStackTrace(); } }
	         */
		
		list.add(pointGrageList);
		list.add(pointList);
		list.add(isNull);
		list.add(noGradeItem.toString());
	
		if(isLoadStaticValue) {
            plan_perPointMap.put(plan_id,list);
        }
	}
	else
	{
		list=(ArrayList)plan_perPointMap.get(plan_id);
	}
	
	ArrayList t_list=(ArrayList)list.clone();
	list=t_list;
	/*
	 * 取得每个指标的标度
	 */
	perPointGradedescMap=new HashMap();
	ArrayList pointGradeList=(ArrayList)list.get(0);
	String[] temp=null;
	String pointid="";
	ArrayList tempList=new ArrayList();
	for(int i=0;i<pointGradeList.size();i++)
	{
		temp=(String[])pointGradeList.get(i);
		if(pointid.length()==0) {
            pointid=temp[1];
        }
		
		if(pointid.equalsIgnoreCase(temp[1])) {
            tempList.add(temp);
        } else
		{
			perPointGradedescMap.put(pointid.toLowerCase(),tempList);
			tempList=new ArrayList();
			tempList.add(temp);
			pointid=temp[1];
		}
	}
	perPointGradedescMap.put(pointid.toLowerCase(),tempList);
	
	
	
	 ArrayList pointList=(ArrayList)list.get(1);
	 this.pointContrl = "";
	 for (Iterator t = pointList.iterator(); t.hasNext();)
	 {
			String[] atemp = (String[]) t.next();		
			if ("1".equals(atemp[2])) // 定量
			{
			    if (atemp[7] != null && "1".equals(atemp[7]) && "true".equalsIgnoreCase(this.showOneMark)) {
                    this.pointContrl += "/0";
                } else {
                    this.pointContrl += "/1";
                }
			} else
			{
			    this.pointContrl += "/1";
			}
	 } 
	return list;
	
    }

    /**
         * 用于 考评分数查询 模版项目列表 && 最底层项目的指标个数集合 && 表头的层数 &&
         * HashMap各项目包含的指标个数&&最底层的项目
         * 
         * @param templateID
         * @return
         */
    public ArrayList getPerformanceStencilList2(String templateID) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	int lays = 0; // 表头的层数
	HashMap map = new HashMap();
	try
	{

	    String item_id = "0";
	    String sql = "";
	    ArrayList bottomItemList = new ArrayList();
	    /* 按循序得到模版项目列表 */
	    ArrayList items = getItems(templateID);
	    /* 取得表头的层数 */
	    getLays(items);
	    lays = this.a_lays;
	    lays++;
	    lays++;
	    list.add(items);
	    /* 得到各最底层项目的指标个数集合 */

	    StringBuffer sql_ = new StringBuffer("select pp.item_id,count(pp.item_id) count  from  per_template_item pi,per_template_point pp,per_point  where pi.item_id=pp.item_id");
	    sql_.append(" and  pp.point_id=per_point.point_id ");
	    sql_.append(" and pi.template_id='" + templateID + "' group by pp.item_id  ");

	    sql = sql_.toString();
	    rowSet = dao.search(sql);
	    HashMap itemsCountMap = new HashMap();
	    while (rowSet.next())
	    {
		itemsCountMap.put(rowSet.getString("item_id"), rowSet.getString("count"));
	    }

	    /* 求得map值 */
	    for (Iterator t = items.iterator(); t.hasNext();)
	    {
		int count = 0;
		String[] temp = (String[]) t.next();
		this.leafNodes = "";
		getleafCounts(temp, items, itemsCountMap);
		if (this.leafNodes.substring(1).contains(temp[0])) {
            bottomItemList.add(temp);
        }
		this.leafNodes += "/";

		String[] a = this.leafNodes.substring(1).split("/");

		for (int i = 0; i < a.length; i++)
		{
		    if (itemsCountMap.get(a[i]) != null) {
                count += Integer.parseInt((String) itemsCountMap.get(a[i]));
            }
		    // else
		    // count++;
		}
		if (!a[0].equals(temp[0]) && itemsCountMap.get(temp[0]) != null)
		{
		    count += Integer.parseInt((String) itemsCountMap.get(temp[0]));
		}
		map.put(temp[0], String.valueOf(count));
	    }

	    list.add(itemsCountMap);
	    list.add(new Integer(lays));
	    list.add(map);
	    list.add(bottomItemList);

	    for (int i = 0; i < bottomItemList.size(); i++)
	    {
		String[] tt = (String[]) bottomItemList.get(i);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}

	return list;
    }

    /**
         * 模版项目列表 && 最底层项目的指标个数集合 && 表头的层数 && HashMap各项目包含的指标个数&&最底层的项目
         * 
         * @param templateID
         * @return
         */
    public ArrayList getPerformanceStencilList(String templateID, boolean flag) throws GeneralException
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	int lays = 0; // 表头的层数
	HashMap map = new HashMap();
	try
	{

	    String item_id = "0";
	    String sql = "";
	    ArrayList bottomItemList = new ArrayList();
	    /* 按循序得到模版项目列表 */
	    ArrayList items = getItems(templateID);
	    /* 取得表头的层数 */
	    getLays(items);
	    lays = this.a_lays;
	    lays++;
	    lays++;
	    list.add(items);
	    
	    /* 得到各最底层项目的指标个数集合 */
	    HashMap itemsCountMap = new HashMap();
	    /** 潍柴 多人打分采用单选按钮的形式 */
	    if(isBatchGradeRadio)
	    {
	    	String temp_sql="select pp.item_id,pp.point_id from  per_template_item pi,per_template_point pp,per_point ";
	    		   temp_sql+=" where pi.item_id=pp.item_id and  pp.point_id=per_point.point_id  and pi.template_id='" + templateID + "' order by pp.item_id ";
	    	rowSet=dao.search(temp_sql);
	    	String _itemid="";
	    	int num=0;
	    	while(rowSet.next())
	    	{
	    		String itemid=rowSet.getString("item_id");
	    		String point_id=rowSet.getString("point_id");
	    		if(_itemid.length()==0)
	    		{
	    			_itemid=itemid;
	    		}
	    	
	    		if(!itemid.equalsIgnoreCase(_itemid))
	    		{
	    			
	    			itemsCountMap.put(_itemid, String.valueOf(num));
	    			_itemid=itemid;
	    			num=0;
	    			ArrayList gradeList=new ArrayList();
		    		if(this.perPointGradedescMap!=null&&this.perPointGradedescMap.get(point_id.toLowerCase())!=null) {
                        gradeList=(ArrayList)this.perPointGradedescMap.get(point_id.toLowerCase());
                    }
		    		num+=gradeList.size();
	    		}
	    		else
	    		{
		    		ArrayList gradeList=new ArrayList();
		    		if(this.perPointGradedescMap!=null&&this.perPointGradedescMap.get(point_id.toLowerCase())!=null) {
                        gradeList=(ArrayList)this.perPointGradedescMap.get(point_id.toLowerCase());
                    }
		    		num+=gradeList.size();
	    		}
	    	}
	    	itemsCountMap.put(_itemid, String.valueOf(num));
	    	
	    }
	    else
	    {
	    
		    StringBuffer sql_ = new StringBuffer("select pp.item_id,count(pp.item_id) count  from  per_template_item pi,per_template_point pp,per_point  where pi.item_id=pp.item_id");
		    sql_.append(" and  pp.point_id=per_point.point_id ");
	
		    if (flag && "false".equalsIgnoreCase(this.showOneMark) && this.planVo.getInt("method") != 2) {
                sql_.append(" and (( pointkind='1' and ( per_point.status<>1 or per_point.status is null )  ) or pointkind='0'  )  ");
            }
	
		    sql_.append(" and pi.template_id='" + templateID + "' group by pp.item_id  ");
	
		    sql = sql_.toString();
		    rowSet = dao.search(sql);
		    while (rowSet.next())
		    {
		    	itemsCountMap.put(rowSet.getString("item_id"), rowSet.getString("count"));
		    }
	    }
	    
	    
	    /* 求得map值 */
	    for (Iterator t = items.iterator(); t.hasNext();)
	    {
			int count = 0;
			String[] temp = (String[]) t.next();
			this.leafNodes = "";
			getleafCounts(temp, items, itemsCountMap);
			if (this.leafNodes.substring(1).contains(temp[0])) {
                bottomItemList.add(temp);
            }
			this.leafNodes += "/";
	
			String[] a = this.leafNodes.substring(1).split("/");
	
			for (int i = 0; i < a.length; i++)
			{
			    if (itemsCountMap.get(a[i]) != null) {
                    count += Integer.parseInt((String) itemsCountMap.get(a[i]));
                } else
			    {
//			    	if(this.planVo!=null&&planVo.getInt("method")==2)//计划方法是可以随意调的 在此这样限定没有意义
//			    	{
						for (int j = 0; j < items.size(); j++)
						{
						    String[] atemp = (String[]) items.get(j);
						    if (atemp[0].equalsIgnoreCase(a[i]) && "2".equals(atemp[5])) {
                                count++;
                            }
						    
						}
//			    	}
			    }
			}
			if (!a[0].equals(temp[0]) && itemsCountMap.get(temp[0]) != null)
			{
			    count += Integer.parseInt((String) itemsCountMap.get(temp[0]));
			}
			map.put(temp[0], String.valueOf(count));
	    }

	    list.add(itemsCountMap);
	    list.add(new Integer(lays));
	    list.add(map);
	    list.add(bottomItemList);

	    for (int i = 0; i < bottomItemList.size(); i++)
	    {
		String[] tt = (String[]) bottomItemList.get(i);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	/*
         * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
         * ee) { ee.printStackTrace(); } }
         */
	return list;
    }

    /**
         * 取得表头的层数
         */
    int a_lays = 0;

    public void getLays(ArrayList items)
    {

	for (Iterator t = items.iterator(); t.hasNext();)
	{
	    String[] item = (String[]) t.next();
	    if (item[1] == null)
	    {
		int lay = CountLevel(item, items);
		if (a_lays < lay)
		{
		    a_lays = lay;
		}
	    }
	}
    }

    int CountLevel(String[] MyNode, ArrayList list)
    {

	if (MyNode == null) {
        return -1;
    }
	int iLevel = 1;
	int iMaxLevel = 0;
	ArrayList subNodeList = new ArrayList();
	for (int i = 0; i < list.size(); i++)
	{
	    String[] temp = (String[]) list.get(i);
	    if (temp[1] != null && temp[1].equals(MyNode[0])) {
            subNodeList.add(temp);
        }
	}

	for (int i = 0; i < subNodeList.size(); i++)
	{
	    iLevel = CountLevel((String[]) subNodeList.get(i), list) + 1;
	    if (iMaxLevel < iLevel) {
            iMaxLevel = iLevel;
        }
	}
	return iMaxLevel;
    }

    /** 按岗位素质模型测评   得到指标分类  郭峰*/
    public ArrayList getCompentencyPointSet(String e01a1){
    	ArrayList list = new ArrayList();
    	try{
    		RowSet rs = null;
    		StringBuffer sbsql = new StringBuffer("");
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    ArrayList controllist = new ArrayList();
    	    sbsql.append("select pcm.point_type,ci.codeitemdesc from per_competency_modal pcm left join codeitem ci on pcm.point_type=ci.codeitemid and ci.codesetid='70' where pcm.object_type='3' and pcm.object_id = '"+e01a1+"'");
    	    sbsql.append(" and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date");
    	    rs = dao.search(sbsql.toString());
    	    int seq=0;
    	    while(rs.next()){
    	    	String point_type = rs.getString("point_type");
    	    	if(point_type==null || "".equals(point_type)){
    	    		point_type = "-9999";//空项目用这个符号代替
    	    	}
    	    	String itemdesc = rs.getString("codeitemdesc");
    	    	if(itemdesc==null || "".equals(itemdesc)){
    	    		//itemdesc = "无指标分类";    //岗位素质模型指标没有指标分类  2013.11.30 pjf
    	    		itemdesc = "岗位素质模型指标";
    	    	}
    	    	if(!controllist.contains(point_type)){
    	    		seq++;
    	    		String[] temp = new String[6];
    	    		//temp[0]:item_id temp[1]:parent_id  temp[2]:child_id  temp[3]:itemdesc temp[4]:seq  temp[5]:kind
        			temp[0] = point_type;
        			temp[1] = "null";
        			temp[2] = "null";
        			temp[3] = itemdesc;
        			temp[4] = String.valueOf(seq);
        			temp[5] = "1";
        	    	list.add(temp);
        	    	controllist.add(point_type);
    	    	}
    	    }
    	    if(rs!=null) {
                rs.close();
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
         * 按顺序显示表项
         */
    public ArrayList getItems(String template_id)
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
		String sql = "";
		if(!"".equals(voteFlag) && voteFlag !=null && "voteFlag".equals(voteFlag)) {
			sql = "select * from per_template_item where template_id='" + template_id + "' and kind=1 order by seq";
		} else {
			sql = "select * from per_template_item where template_id='" + template_id + "'  order by seq";
		}
	    rowSet = dao.search(sql);
	    ArrayList items = new ArrayList();
	    ArrayList bottomItemList = new ArrayList();
	    ArrayList parentList = new ArrayList();
	    String item_id = "0";
	    while (rowSet.next())
	    {
		String[] temp = new String[6];
		temp[0] = rowSet.getString("item_id");
		temp[1] = rowSet.getString("parent_id");
		temp[2] = rowSet.getString("child_id");
		temp[3] = rowSet.getString("itemdesc");
		temp[4] = rowSet.getString("seq");
		temp[5] = rowSet.getString("kind") != null ? rowSet.getString("kind") : "1";
		items.add(temp);
		if (temp[1] == null) {
            parentList.add(temp);
        }
	    }
	    String node = null;
	    for (int i = 0; i < parentList.size(); i++)
	    {
		String[] temp = (String[]) parentList.get(i);
		list.add(temp);
		searchIterms(items, list, temp[0]);

	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	/*
         * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
         * ee) { ee.printStackTrace(); } }
         */

	return list;
    }

    public void searchIterms(ArrayList items, ArrayList list, String node)
    {

	for (Iterator t = items.iterator(); t.hasNext();)
	{
	    String[] temp1 = (String[]) t.next();
	    if (temp1[1] != null && temp1[1].equals(node))
	    {
		list.add(temp1);

		searchIterms(items, list, temp1[0]);
	    }
	}
    }

    /**
         * 求得某节点的所有叶子节点的id串
         * 
         * @param node
         * @param items
         */
    String leafNodes = "";

    public void getleafCounts(String[] node, ArrayList items, HashMap itemsCountMap)
    {

	int i = 0;
	for (Iterator t = items.iterator(); t.hasNext();)
	{
	    String[] temp = (String[]) t.next();
	    if (node[0].equals(temp[1]))
	    {
		i++;
	    }
	}
	if (i == 0) {
        leafNodes += "/" + node[0];
    } else
	{
		
	    for (Iterator t = items.iterator(); t.hasNext();)
	    {
			String[] temp = (String[]) t.next();
			if (node[0].equals(temp[1]))
			{
				if (itemsCountMap.get(node[0]) != null && leafNodes.indexOf("/" + node[0]) == -1) {
                    leafNodes += "/" + node[0];
                } else if (itemsCountMap.get(temp[0]) != null && leafNodes.indexOf("/" + node[0]) == -1) {
                    leafNodes += "/" + node[0];
                }
			    
			    getleafCounts(temp, items, itemsCountMap); // 递归
	
			}
	    }
	}
    }

    /**
         * 得到某计划考核主体给对象的评分结果hashMap
         * 
         * @param plan_id
         *                考核计划id
         * @param mainbodyID
         *                考核主体id
         * @param object_id
         *                考核对象列表
         * @return HashMap
         */
    public HashMap getPerTableXXX(int plan_id, String mainbodyID, ArrayList object_id) throws GeneralException
    {

	HashMap hashMap = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    DecimalFormat myformat1 = new DecimalFormat("##########.#####");//
	    DbWizard dbWizard = new DbWizard(this.conn);
	    for (Iterator t = object_id.iterator(); t.hasNext();)
	    {
			String[] temp0 = (String[]) t.next();
	//		if (temp0[2].equals("1") || temp0[2].equals("2") || temp0[2].equals("3")|| temp0[2].equals("8") || (this.performanceType.equals("1") && (temp0[2].equals("4") || temp0[2].equals("7"))))
			{
			    String objectid = temp0[0];
			    HashMap map = new HashMap();
			    String sql = "";
			    if (dbWizard.isExistTable("per_table_" + plan_id))
			    {
				sql = "select * from per_table_" + plan_id + "  where mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
				rowSet = dao.search(sql);
				int cols = rowSet.getMetaData().getColumnCount();
				while (rowSet.next())
				{
				    String[] temp = new String[cols];
				    // String[] temp=new String[8];
				    for (int i = 0; i < cols; i++)
				    {
					if (i == 3 || i==4)
					{
					    if (rowSet.getString(i + 1) != null)
					    {
						if(i == 3) {
                            temp[i] = myformat1.format(rowSet.getDouble(i + 1));
                        }
						if(i == 4) {
                            temp[i] = PubFunc.round(Double.toString(rowSet.getDouble(i + 1)),1);
                        }
					    } else {
                            temp[i] = rowSet.getString(i + 1);
                        }
					} else {
                        temp[i] = rowSet.getString(i + 1) != null ? rowSet.getString(i + 1) : "";
                    }
				    }
				    map.put(temp[5], temp);// temp[5]取得是point_id字段
				}
	
			    }
			    sql = "select know_id,whole_grade_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
			    rowSet = dao.search(sql);
			    if (rowSet.next())
			    {
				map.put("know_id", rowSet.getString(1));
				map.put("whole_grade_id", rowSet.getString(2));
			    }
			    hashMap.put(objectid, map);
			}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	/*
         * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
         * ee) { ee.printStackTrace(); } }
         */
	return hashMap;
    }
    
    
    /**
     * 得到某计划考核主体给对象的评分结果hashMap
     * 
     * @param plan_id
     *                考核计划id
     * @param mainbodyID
     *                考核主体id
     * @param object_id
     *                考核对象列表
     * @return HashMap
     */
    
    /*
public HashMap getPerTableXXX2(int plan_id,String object_id) throws GeneralException
{

HashMap hashMap = new HashMap();
ContentDAO dao = new ContentDAO(this.conn);
RowSet rowSet = null;
try
{
    DecimalFormat myformat1 = new DecimalFormat("##########.#####");//
    DbWizard dbWizard = new DbWizard(this.conn);
	HashMap map = new HashMap();
	String sql = "";
	LazyDynaBean abean=new LazyDynaBean();
	if (dbWizard.isExistTable("per_table_" + plan_id))
    {
		sql = "select * from per_table_" + plan_id + "  where   object_id='" + object_id + "' order by mainbody_id ";
		rowSet = dao.search(sql);
		int cols = rowSet.getMetaData().getColumnCount();
		while (rowSet.next())
		{
			abean=new LazyDynaBean();
			abean.set("id", myformat1.format(rowSet.getDouble(i + 1));)
		 

	    }
	    sql = "select know_id,whole_grade_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + mainbodyID + "' and object_id='" + objectid + "' ";
	    rowSet = dao.search(sql);
	    if (rowSet.next())
	    {
		map.put("know_id", rowSet.getString(1));
		map.put("whole_grade_id", rowSet.getString(2));
	    }
	    hashMap.put(objectid, map);
	
} catch (Exception e)
{
    e.printStackTrace();
    throw GeneralExceptionHandler.Handle(e);
}

return hashMap;
}
    */
    
    
    
    
    
    
    
    
    

    /**
         * 得到指标权限信息
         * 
         * @param plan_id
         *                考核计划id
         * @param per_mainbody
         *                考核主体
         * @return
         */
    public HashMap getPointprivMap(String plan_id, String per_mainbody) throws GeneralException
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	ArrayList list=new ArrayList();
	String tempid="";
	try
	{
	    ArrayList pointList = new ArrayList();
	    
	    if(pointListMap.get(plan_id)==null) 
	    {
		    String sql = "select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
			    + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + plan_id;
		    rowSet = dao.search(sql);
		    while (rowSet.next())
		    {
		    	pointList.add(rowSet.getString(1)); 
		    }
		    pointListMap.put(plan_id,pointList);
	    }
	    else
	    {
	    	pointList=(ArrayList)pointListMap.get(plan_id);
	    }
	    
	    
	    String sql = "select * from per_pointpriv_" + plan_id + " where mainbody_id='" + per_mainbody + "'";
	    rowSet = dao.search(sql);
	    int num = 0;
	    while (rowSet.next()) 
	    {
		num++;
		String object_id = rowSet.getString("object_id");
		list.add(object_id);
		HashMap pointMap = new HashMap();
		for (Iterator t = pointList.iterator(); t.hasNext();)
		{
		    String temp = (String) t.next();
		    String _value=rowSet.getString("C_" + temp);
		    //haosl update 20170316  数据库中_value的值有可能为null
		    if(StringUtils.isEmpty(_value)|| "0.0".equals(_value)|| "0".equals(_value)) {
                _value="0";
            } else if("1.0".equals(_value)) {
                _value="1";
            }
		    pointMap.put(temp,_value);

		}
		map.put(object_id, pointMap);
		map.put("huicong"+object_id, pointMap);
	    }
	    if (num == 0)
	    {
		rowSet = dao.search("select distinct object_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + per_mainbody + "' ");
		while (rowSet.next())
		{
		    String object_id = rowSet.getString("object_id");
		    HashMap pointMap = new HashMap();
		    for (Iterator t = pointList.iterator(); t.hasNext();)
		    {
			String temp = (String) t.next();
			pointMap.put(temp, "1");
		    }
		    map.put(object_id, pointMap);
		    map.put("huicong"+object_id, pointMap);
		}
	    }
	    for(int i=0;i<list.size();i++){
	    	tempid=(String) list.get(i);
		    String mainbody=getPriv(per_mainbody,plan_id,tempid);
		    boolean Temp=islook(per_mainbody,plan_id,tempid);
		    if(mainbody.length()>0||Temp){
		    	if(Temp&&mainbody.length()>0){//既能看下级主体又能看对象
		    		mainbody+=","+per_mainbody+","+tempid;
		    	}else if(Temp){//只能看对象
		    		mainbody=per_mainbody+","+tempid;
		    	}else if(mainbody.length()>0){//只能看下级主体
		    		mainbody+=","+per_mainbody;
		    	}
			    String sql1 = "select * from per_pointpriv_" + plan_id + " where mainbody_id in ("+mainbody+")";
			    rowSet = dao.search(sql1);
			    HashMap pointMap = new HashMap();
			    while (rowSet.next())
			    {
				
				for (Iterator t = pointList.iterator(); t.hasNext();)
				{
				    String temp = (String) t.next();
				    String _value=rowSet.getString("C_" + temp);
				    if(pointMap.get(temp)!=null&&"1".equals(pointMap.get(temp))){
				    	continue;
				    }
				    if("0.0".equals(_value)|| "0".equals(_value)){
				    	pointMap.put(temp,"0");
				    }else if("1.0".equals(_value)|| "1".equals(_value)) {
                        pointMap.put(temp,"1");
                    }

				}			
			    }
			    map.put(tempid, pointMap);
		    }
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	/*
         * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
         * ee) { ee.printStackTrace(); } }
         */
	return map;
    }
    /**
     * 判断哪个级别的主体可以查看考核对象评分   zhaoxg 2014-6-20 慧聪网需求
     * @return
     */
    public boolean islook(String userID,String plan_id,String object_id){
		Hashtable htxml = new Hashtable();
		boolean isLookObjectScore = false;
		LoadXml loadxml = null;
		try {
			if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id) == null) {
				loadxml = new LoadXml(this.conn, plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id, loadxml);
			} else {
				loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(
						plan_id);
			}
			htxml = loadxml.getDegreeWhole();
			// 上级领导给下级打分时是否 显示考核对象的自我打分分数整型（Boolean为兼容）：0和False
			// 为不能查看，1（True）为直接上级可查看，2为所有上级，3为所有考核主体. zhaoxg标记
			String mitiScoreMergeSelfEval = (String) htxml
					.get("mitiScoreMergeSelfEval"); // 显示自我评价
													// 显示自我评价和允许哪类主体可以查看自我评价必须一块使用
													// zzk 2014/1/28
			if (!"0".equalsIgnoreCase(selfScoreInDirectLeader)
					&& !"False".equalsIgnoreCase(selfScoreInDirectLeader)) {
				String level = "";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet = null;
				String sql = "select ";
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    sql += " level_o";
                } else {
                    sql += " level ";
                }
				sql += " from per_mainbodyset where body_id=(select body_id from per_mainbody where plan_id";
				sql += "=" + plan_id + " and object_id='" + object_id
						+ "' and mainbody_id='" + userID + "')";
				rowSet = dao.search(sql);
				if (rowSet.next()) {
					if (rowSet.getString(1) != null) {
                        level = rowSet.getString(1);
                    }
				}
				if (rowSet != null) {
                    rowSet.close();
                }

				if (("True".equalsIgnoreCase(mitiScoreMergeSelfEval)
						&& "True".equalsIgnoreCase(selfScoreInDirectLeader) || "1"
						.equals(selfScoreInDirectLeader))
						&& "1".equals(level)) {
                    isLookObjectScore = true;
                } else if ("True".equalsIgnoreCase(mitiScoreMergeSelfEval)
						&& "2".equals(selfScoreInDirectLeader)
						&& ("1".equals(level) || "0".equals(level)
								|| "-2".equals(level) || "-1".equals(level))) {
                    isLookObjectScore = true;
                } else if ("True".equalsIgnoreCase(mitiScoreMergeSelfEval)
						&& "3".equals(selfScoreInDirectLeader)
						&& !userID.equalsIgnoreCase(object_id)) {
                    isLookObjectScore = true;
                }

				// isLeader=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isLookObjectScore;
    }
    /**
     * 获取下级主体或者对象的指标权限 zhaoxg 2014-6-20 慧聪网需求 zhaoxg标记
     * @return
     */
    public String getPriv(String userid,String plan_id,String object_id){
    	StringBuffer priv=new StringBuffer();
    	String temp="";
    	String allowSeeLowerGrade="false";
    	try{
    		Hashtable htxml = new Hashtable();
			if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id) == null) {
				loadxml = new LoadXml(this.conn, plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id, loadxml);
			} else {
				loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(
						plan_id);
			}
			htxml = loadxml.getDegreeWhole();
    		allowSeeLowerGrade=(String)htxml.get("allowSeeLowerGrade");
    		if("false".equals(allowSeeLowerGrade)){
    			return temp;
    		}
			int property=10;
			ContentDAO dao = new ContentDAO(this.conn);
			String _str="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                _str="level_o";
            }
			String _sql="select per_mainbodyset."+_str+" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id"
					+" and plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+userid+"'";
			RowSet rowSet=dao.search(_sql);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null) {
                    property=rowSet.getInt(1);
                }
			}
			
			if(property==5) {
                return temp;
            }
			
			String level_str="";
			switch (property)
			{
				case 1:
					level_str="5,2";
					break;
				case 0:
					level_str="5,1,2";
					break;
				case -1:
					level_str="5,1,0,2";
					break;
				case -2:
					level_str="5,1,0,-1,2";
					break;
			}
			
			if(level_str.length()==0) {
                return temp;
            }
			
			StringBuffer  sql=new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
			sql.append(" and pm.plan_id="+plan_id+" and pm.object_id='"+object_id+"' and  ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql.append("  pms.level_o");
            } else {
                sql.append("  pms.level ");
            }
			sql.append(" in ("+level_str+")");
			String cloumn="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                cloumn="level_o";
            }
			sql.append(" order by "+cloumn+" desc ");
			rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				String mainbody_id=rowSet.getString("mainbody_id");				
				String _status=rowSet.getString("status");
	 			if(_status==null||!"2".equalsIgnoreCase(_status))  //如果没有提交过分数，则不显示此打分人列啦
                {
                    continue;
                }
	 			priv.append(","+mainbody_id);
			}
			
			if(priv.length()>0){
			 temp=priv.toString().substring(1);
			}
			rowSet.close();
		
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	}
    	return temp;
    }
    /**得到指标权限 对于能力素质来说，不需要指标权限，所以就不需要从per_pointpriv_plan_id表中查询了，直接认为有权限。 郭峰*/
    public HashMap getCompetencyPointprivMap(String plan_id, String per_mainbody) throws GeneralException
    {
    	
    	HashMap map = new HashMap();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rowSet = null;
    	try
    	{
    		ArrayList pointList = new ArrayList();
			rowSet = dao.search("select distinct object_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + per_mainbody + "' ");
			while (rowSet.next())
			{
				String object_id = rowSet.getString("object_id");
				StringBuffer sb = new StringBuffer("");
				sb.append("select point_id from per_competency_modal where object_type='3' and object_id = (select "+Sql_switcher.isnull("e01a1", "null")+" from usra01 where a0100='"+object_id+"')");
				sb.append(" and "+Sql_switcher.dateValue(historyDate)+" between start_date and end_date");
				RowSet rs = dao.search(sb.toString());
			    while(rs.next()){
			    	pointList.add(rs.getString(1));
			    }
				HashMap pointMap = new HashMap();
				for (Iterator t = pointList.iterator(); t.hasNext();)
				{
					String temp = (String) t.next();
					pointMap.put(temp, "1");
				}
				map.put(object_id, pointMap);
			}
    		
    		
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	/*
    	 * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
    	 * ee) { ee.printStackTrace(); } }
    	 */
    	return map;
    }
    
    public HashMap getPointprivMap2(String plan_id, String object_id) throws GeneralException
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	RowSet rowSet = null;
	try
	{
	    ArrayList pointList = new ArrayList();
	    String sql = "select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
		    + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + plan_id;
	    rowSet = dao.search(sql);
	    while (rowSet.next())
	    {
		pointList.add(rowSet.getString(1));
	    }

	   
		    HashMap pointMap = new HashMap();
		    for (Iterator t = pointList.iterator(); t.hasNext();)
		    {
			String temp = (String) t.next();
			pointMap.put(temp, "0");
		    }
		    map.put(object_id, pointMap);
		
	    

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	/*
         * finally { try { if(rowSet!=null) rowSet.close(); } catch(Exception
         * ee) { ee.printStackTrace(); } }
         */
	return map;
    }
    /** ***************************** 分值验证 **************************** */

    /**
         * 返回 某绩效模版的所有指标集
         * 
         * @param templateID
         * @return
         */
    public ArrayList getPerPointList2(String templateID, String plan_id)
    {

    	ArrayList list = new ArrayList();
    	if(plan_perPointMap2.get(plan_id)==null||!isLoadStaticValue)
    	{
		
			HashMap pointGradeMap = new HashMap();
			ArrayList a_pointGrageList = new ArrayList();
			ArrayList pointList = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
		
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			HashMap map2 = new HashMap();
			String sql = "select pp.item_id,po.point_id,po.pointname,po.pointkind,pgt.gradedesc,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,po.fielditem,po.l_fielditem  from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt "
				+ " where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id and template_id='" + templateID + "' ";
			if (noShowOneMark&& "false".equalsIgnoreCase(this.showOneMark) && this.planVo.getInt("method") != 2) //  2010/10/29  dengcan
            {
                sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
            }
			sql+="  order by pp.seq,pg.gradevalue desc"; // pi.seq,
			 
			
			try
			{
			    rowSet = dao.search(sql);
			    while (rowSet.next())
			    {
				String[] temp = new String[12];
				for (int i = 0; i < 12; i++)
				{
				    if (i == 2) {
                        temp[i] = Sql_switcher.readMemo(rowSet, "pointname");
                    } else if (i == 4) {
                        temp[i] = Sql_switcher.readMemo(rowSet, "gradedesc");
                    } else {
                        temp[i] = rowSet.getString(i + 1);
                    }
		
				}
				a_pointGrageList.add(temp);
			    }
			    String _sql="select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.l_fielditem,po.status from per_template_item pi,per_template_point pp,per_point po "
				    + " where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + templateID+"'";
			    if (noShowOneMark&& "false".equalsIgnoreCase(this.showOneMark) && this.planVo.getInt("method") != 2) //  2010/10/29  dengcan
                {
                    _sql +=" and (( po.pointkind='1' and ( po.status<>1 or po.status is null )  ) or po.pointkind='0'  )  ";
                }
			    _sql+= "   order by pp.seq";
			    rowSet = dao.search(_sql); // pi.seq,
			    while (rowSet.next())
			    {
				String[] temp = new String[9];
				temp[0] = rowSet.getString(1);
				temp[1] = Sql_switcher.readMemo(rowSet, "pointname");
				temp[2] = rowSet.getString(3);
				temp[3] = rowSet.getString(4);
				temp[4] = "";
				temp[5] = rowSet.getString("visible");
				temp[6] = rowSet.getString("fielditem");
				temp[7] = rowSet.getString("l_fielditem");
				temp[8] = rowSet.getString("status");
				map2.put(temp[0].toLowerCase(), temp);
			    }
			    // 解决排列顺序问题
			    ArrayList seqList = new ArrayList();
				DbWizard dbWizard = new DbWizard(this.conn);
				if (dbWizard.isExistTable("PER_RESULT_" + plan_id, false))
				{
			    rowSet = dao.search("select * from per_result_" + plan_id);
			    ResultSetMetaData metadata = rowSet.getMetaData();
			    int i = 1;
			    while (i <= metadata.getColumnCount())
			    {
				String tempName = metadata.getColumnName(i);
				if ("C_".equals(tempName.substring(0, 2)))
				{
				    seqList.add(tempName.substring(2).toLowerCase());
		
				}
				i++;
			    }
		
			    for (int a = 0; a < seqList.size(); a++)
			    {
					String temp = (String) seqList.get(a);
					if( map2.get(temp)!=null) {
                        pointList.add((String[]) map2.get(temp));
                    }
			    }
		
			    for (int j = 0; j < seqList.size(); j++)
			    {
					String temp = (String) seqList.get(j);
					ArrayList tempList = new ArrayList();
					int a = 0;
					for (int z = 0; z < a_pointGrageList.size(); z++)
					{
					    String[] tt = (String[]) a_pointGrageList.get(z);
					    if (tt[1].toLowerCase().equals(temp))
					    {
						tempList.add(tt);
						a++;
					    } else if (a != 0 && !tt[1].toLowerCase().equals(temp))
					    {
						break;
					    }
					}
					pointGradeMap.put(temp, tempList);
		
			    }
				}
			} catch (Exception e)
			{
			    e.printStackTrace();
		
			}
			list.add(pointList);
			list.add(pointGradeMap);
			if(isLoadStaticValue) {
                plan_perPointMap2.put(plan_id, list);
            }
    	}
    	else
    	{
    		list=(ArrayList)plan_perPointMap2.get(plan_id);
    	}
	return list;
    }

    public HashMap getUserNameMap(String[] userid)
    {

	HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
	    StringBuffer whl = new StringBuffer("");
	    for (int i = 0; i < userid.length; i++)
	    {
		whl.append(",'" + userid[i] + "'");
	    }
	    RowSet rowSet = null;
	    if ("2".equals(this.object_type))
	    {
		rowSet = dao.search("select a0100,a0101 from usra01 where a0100 in (" + whl.substring(1) + ")");
		while (rowSet.next())
		{
		    map.put(rowSet.getString("a0100"), rowSet.getString("a0101"));
		}
	    } else
	    {
		// System.out.println("select codeitemid,codeitemdesc from
                // organization where codeitemid in ("+whl.substring(1)+")");
		rowSet = dao.search("select codeitemid,codeitemdesc from organization where codeitemid in (" + whl.substring(1) + ")");
		while (rowSet.next())
		{
		    map.put(rowSet.getString("codeitemid"), rowSet.getString("codeitemdesc"));
		}

	    }
	} catch (Exception e)
	{

	}
	return map;
    }

    /**
         * 得到 标准标度表中 最大的标度代码
         * 
         * @param flag
         *                1: 最大值 2:最小值
         * @return
         */
    public String getMaxPointGrade()
    {

	String grade_temp_id = "";
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
            per_comTable = "per_grade_competence"; // 能力素质标准标度
        }
	    RowSet rowSet = dao.search("select grade_template_id from "+per_comTable+" order by gradevalue desc");
	    if (rowSet.next()) {
            grade_temp_id = rowSet.getString("grade_template_id");
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return grade_temp_id;
    }
    
    
    /**
     * 获得绩效标准标度
     * @return
     */
    public ArrayList getGradeDesc()
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rowSet=dao.search("select * from per_grade_template order by gradevalue desc");
    		LazyDynaBean abean=null;
    		while(rowSet.next())
    		{
    			abean=new LazyDynaBean();
    			abean.set("grade_template_id",rowSet.getString("grade_template_id"));
    			abean.set("gradevalue",rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"");
    			abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");
    			abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"");
    			abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"");
    			
    			list.add(abean);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 获得能力素质标准标度
     * @return
     */
    public ArrayList getCompeGradeDesc()
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rowSet=dao.search("select * from per_grade_competence order by gradevalue desc");
    		LazyDynaBean abean=null;
    		while(rowSet.next())
    		{
    			abean=new LazyDynaBean();
    			abean.set("grade_template_id",rowSet.getString("grade_template_id"));
    			abean.set("gradevalue",rowSet.getString("gradevalue")!=null?rowSet.getString("gradevalue"):"");
    			abean.set("gradedesc",rowSet.getString("gradedesc")!=null?rowSet.getString("gradedesc"):"");
    			abean.set("top_value",rowSet.getString("top_value")!=null?rowSet.getString("top_value"):"");
    			abean.set("bottom_value",rowSet.getString("bottom_value")!=null?rowSet.getString("bottom_value"):"");
    			
    			list.add(abean);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    
    
    /**
     * 获得绩效或能力素质标准标度
     * @return
     */
    public ArrayList getGradeOrCompeDesc()
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		String per_comTable = "per_grade_template"; // 绩效标准标度
    		if(String.valueOf(this.planVo.getInt("busitype"))!=null && String.valueOf(this.planVo.getInt("busitype")).trim().length()>0 && this.planVo.getInt("busitype")==1) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
    		RowSet rowSet=dao.search("select * from "+per_comTable+" order by gradevalue desc");
    		CommonData vo = new CommonData("null", "  ");
    	    list.add(vo);
    	    while (rowSet.next())
    	    {
	    		vo = new CommonData(rowSet.getString("grade_template_id"), rowSet.getString("gradedesc"));
	    		list.add(vo);
    	    }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    
    /**
     * 获得干警考核系统标准标度
     * @return
     */
    public ArrayList getGradeForgjkhxt()
    {
    	ArrayList list = new ArrayList();
    	try
    	{   		   	    	    
    	    list.add(new CommonData("null", "  "));	    		   	    
	    	list.add(new CommonData(ResourceFactory.getProperty("performance.batchgrade.best"), ResourceFactory.getProperty("performance.batchgrade.best")));
	    	list.add(new CommonData(ResourceFactory.getProperty("performance.batchgrade.good"), ResourceFactory.getProperty("performance.batchgrade.good")));
	    	list.add(new CommonData(ResourceFactory.getProperty("performance.batchgrade.mid"), ResourceFactory.getProperty("performance.batchgrade.mid")));
	    	list.add(new CommonData(ResourceFactory.getProperty("performance.batchgrade.less"), ResourceFactory.getProperty("performance.batchgrade.less")));
	    	list.add(new CommonData(ResourceFactory.getProperty("performance.batchgrade.bad"), ResourceFactory.getProperty("performance.batchgrade.bad")));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }

    /**
         * 得到打分的标度号
         * 
         * @param value
         *                值
         * @param gradeList
         *                标度范围
         * @return
         */
    public String getDegreeID(String value, ArrayList gradeList)
    {

	String avalue = "";
	String[] tempGrade = (String[]) gradeList.get(0);
	if ("0123456789.".indexOf(value.charAt(0)) != -1) // 为数字
	{
	    float f_value = Float.parseFloat(value);
	    float top = Float.parseFloat(tempGrade[6]);
	    float min = Float.parseFloat(tempGrade[7]);
	    float score = Float.parseFloat(tempGrade[8]);
	    if ("1".equals(this.scaleToDegreeRule) || "3".equals(this.scaleToDegreeRule))
	    {
		if (f_value <= (score * top) && f_value >= (score * min))
		{
		    avalue = tempGrade[5];
		} else {
            avalue = "#";
        }
	    } else
	    {
		if (f_value <= (score * top) && f_value > (score * min))
		{
		    avalue = tempGrade[5];
		} else {
            avalue = "#";
        }
	    }
	} else
	    // 字母
    {
        avalue = value;
    }
	return avalue;
    }

    /**
         * 得到某计划某考核主体 考核的所有的对象数
         * 
         * @param mainbodyid
         * @param planid
         * @return
         */
    public int getObjectNums(String mainbodyid, String planid)
    {

	int nums = 0;
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
	    RowSet rowSet = dao.search("select count(object_id) num from per_mainbody where mainbody_id='" + mainbodyid + "' and plan_id=" + planid);
	    if (rowSet.next()) {
            nums = rowSet.getInt("num");
        }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return nums;
    }

    /**
         * 检验 打分中 是否超过了预设 的每个指标的最高表度数。
         * 
         * @param templateID
         *                模版id
         * @param plan_id
         *                计划id
         * @param usersValue
         *                用户分值结果
         * @param userid
         *                用户id
         * @param pointMaxValueMap
         *                每个指标的最高标度数
         * @param totalUserNum
         *                计划下 某主体 对应的考核对象数
         * @param flag
         *                1: 最大值 2:最小值
         * @return
         */
    public String validateMaxvalueNum(String fineMax, String templateID, String plan_id, HashMap usersValue, String[] userid, HashMap pointMaxValueMap, int totalUserNum, String mainbodyid, int flag,
	    String WholeEval, String GradeClass)
    {

	String maxGradeId = getMaxPointGrade(); // 最大的标度值
	StringBuffer info = new StringBuffer("");
	// ArrayList pointList=getPerPointList2(templateID,plan_id); //得到指标的信息集
	if (this.per_pointLists == null) {
        this.per_pointLists = getPerPointList2(templateID, plan_id); // 得到指标的信息集
    }
	ArrayList a_pointList = (ArrayList) this.per_pointLists.get(0);
	HashMap pointGradeMap = (HashMap) this.per_pointLists.get(1);

	StringBuffer userIds = new StringBuffer("");
	for (int i = 0; i < userid.length; i++)
	{
	    userIds.append(",'" + userid[i] + "'");
	}
	/* 自己是否未打分 */
	int noMarkNum = getNOSelfMarkNum(mainbodyid, plan_id);

	Connection a_conn = null;
	ResultSet rt = null;
	String sql = "select count(id) nums from per_table_" + plan_id + " where point_id=? and mainbody_id='" + mainbodyid + "' and degree_id=?  and object_id not in (" + userIds.substring(1) + ")";
	try
	{
	    a_conn = AdminDb.getConnection();
	    ContentDAO dao = new ContentDAO(a_conn);

	    for (int j = 0; j < a_pointList.size(); j++)
	    {
		int maxNum = 0; // 最大指标数
		String minGradeId = "";
		String[] tempPoint = (String[]) a_pointList.get(j);
		ArrayList gradeList = (ArrayList) pointGradeMap.get(tempPoint[0].toLowerCase()); // 指标对应的标度
		// String[] temp=(String[])gradeList.get(gradeList.size()-1);
		// minGradeId=temp[5];
		if ("0".equals(tempPoint[2])) // 如果是定性指标
		{

		    String maxCount = null;
		    if ("-1".equals(fineMax)) {
                maxCount = (String) pointMaxValueMap.get(tempPoint[0]);
            } else {
                maxCount = fineMax;
            }
		    if (maxCount != null)
		    {
			if ("-1".equals(maxCount)) // 不限制最大数
            {
                continue;
            }
			float f_maxcount = Float.parseFloat(maxCount);
			if (f_maxcount > 0 && f_maxcount < 1)
			{
			    f_maxcount = (totalUserNum * f_maxcount);
			    f_maxcount = Float.parseFloat(PubFunc.round(String.valueOf(f_maxcount), 0));
			}
			for (int i = 0; i < userid.length; i++)
			{
			    String user_value = (String) usersValue.get(userid[i]);
			    String[] user_result = user_value.split("/");
			    if ("null".equals(user_result[j])) // 如果用户没填值，则continue
                {
                    continue;
                }
			    String gradeID = getDegreeID(user_result[j], gradeList); // 得到打分的标度号
			    if (gradeID.equalsIgnoreCase(maxGradeId)) {
                    maxNum++;
                }
			    /*
                                 * if(flag==1&&gradeID.equalsIgnoreCase(maxGradeId))
                                 * maxNum++; else
                                 * if(flag==2&&gradeID.equalsIgnoreCase(minGradeId))
                                 * maxNum++;
                                 */
			}
			ArrayList list = new ArrayList();
			list.add(tempPoint[0]);
			// if(flag==1)
			list.add(maxGradeId);
			// else if(flag==2)
			// pr.setString(2,minGradeId);
			rt = dao.search(sql,list);
			if (rt.next())
			{
			    maxNum += rt.getInt("nums");
			}
			if (flag == 1 && maxNum > f_maxcount) {
                info.append("<br>"+ResourceFactory.getProperty("performance.implement.kh_point")+"：" + tempPoint[1] + ResourceFactory.getProperty("performance.batchgrade.info1")
                    + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
            } else if (flag == 2)
			{
			    if (maxNum < f_maxcount - noMarkNum) {
                    info.append("<br>"+ResourceFactory.getProperty("performance.implement.kh_point")+"：" + tempPoint[1] + ResourceFactory.getProperty("performance.batchgrade.info2")
                        + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
                }
			}

		    }
		}
	    }

	    if ("true".equalsIgnoreCase(WholeEval)&& "-1".equals(fineMax) && pointMaxValueMap != null)//20141105 dengcan 没有设置按指标控制的方式，不走下列代码
	    {
		String maxCount = (String) pointMaxValueMap.get("whole_grade");
		if (maxCount != null && !"-1".equals(maxCount))
		{
		    float f_maxcount = Float.parseFloat(maxCount);
		    if (f_maxcount > 0 && f_maxcount < 1)
		    {
			f_maxcount = (totalUserNum * f_maxcount);
			f_maxcount = Float.parseFloat(PubFunc.round(String.valueOf(f_maxcount), 0));
		    }
		    ArrayList wholeEvalList = getExtendInfoValue("2", GradeClass);
		    String[] t = null;
		    t = (String[]) wholeEvalList.get(0);
		    int num = 0;
		    for (int i = 0; i < userid.length; i++)
		    {
			String user_value = (String) usersValue.get(userid[i]);
			String[] user_result = user_value.split("/");
			if ("null".equals(user_result[user_result.length - 1])) // 如果用户没填值，则continue
            {
                continue;
            }
			if (user_result[user_result.length - 1].equalsIgnoreCase(t[0])) {
                num++;
            }
		    }
		    sql = "select whole_grade_id from per_mainbody where  plan_id=" + plan_id + " and mainbody_id='" + mainbodyid + "' and object_id='" + mainbodyid + "'";
		    rt = dao.search(sql);
		    if (rt.next())
		    {
			if (rt.getString(1) != null && rt.getString(1).equals(t[0])) {
                num++;
            }
		    }

		    if (flag == 1 && num > f_maxcount)
		    {
			info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.info3") + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
		    }
		    if (flag == 2)
		    {

			if (num < f_maxcount - noMarkNum)
			{
			    info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.info4") + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
			}
		    }

		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	} finally
	{
	    try
	    {
		if (rt != null) {
            rt.close();
        }
		if (a_conn != null) {
            a_conn.close();
        }

	    } catch (Exception ee)
	    {
		ee.printStackTrace();
	    }
	}

	// 如果打分值通过验证则返回成功。
	if (info.toString().trim().length() == 0) {
        info.append("success");
    }
	return info.toString();
    }

    /**
         * 取得考核主体在该计划下还有多少未打分的考核对象
         * 
         * @param mainbody_id
         * @param plan_id
         * @return
         */
    public int getNOSelfMarkNum(String mainbody_id, String plan_id)
    {

	int num = 0;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search("select count(object_id) from per_mainbody where plan_id=" + plan_id + " and  object_id='" + mainbody_id + "'  and mainbody_id='" + mainbody_id
		    + "' and status=0");
	    if (rowSet.next()) {
            num = rowSet.getInt(1);
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return num;
    }

    /**
         * 取得考核主体在该计划下还有多少未打分的考核对象
         * 
         * @param mainbody_id
         * @param plan_id
         * @return
         */
    public int getNOMarkNum(String mainbody_id, String plan_id)
    {

	int num = 0;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search("select count(object_id) from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + mainbody_id + "' and status=0");
	    if (rowSet.next()) {
            num = rowSet.getInt(1);
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return num;
    }

    /**
         * 检验 打分中 是否超过了预设 的每个指标的最高表度数。
         * 
         * @param templateID
         *                模版id
         * @param plan_id
         *                计划id
         * @param usersValue
         *                用户分值结果
         * @param userid
         *                用户id
         * @param pointMaxValueMap
         *                每个指标的最高标度数
         * @param totalUserNum
         *                计划下 某主体 对应的考核对象数
         * @param flag
         *                1:最高 2:最低
         * @return
         */
    public String validateMaxvalueNum(String fineMax, String object_id, String gradeClass, String templateID, String plan_id, HashMap usersValue, String[] userid, HashMap pointMaxValueMap,
	    int totalUserNum, String mainbodyid, String WholeEval, String wholeEval_value, int flag)
    {

	String maxGradeId = getMaxPointGrade(); // 最大的标度值
	StringBuffer info = new StringBuffer("");
	// ArrayList pointList=getPerPointList2(templateID,plan_id); //得到指标的信息集
	if (this.per_pointLists == null) {
        this.per_pointLists = getPerPointList2(templateID, plan_id); // 得到指标的信息集
    }
	ArrayList a_pointList = (ArrayList) this.per_pointLists.get(0);
	HashMap pointGradeMap = (HashMap) this.per_pointLists.get(1);
	/* 未打分人数 */
	int noMarkNum = getNOMarkNum(mainbodyid, plan_id);

	StringBuffer userIds = new StringBuffer("");
	for (int i = 0; i < userid.length; i++)
	{
	    userIds.append(",'" + userid[i] + "'");
	}

	Connection a_conn = null;
	
	ResultSet rt = null;
	String sql = "select count(id) nums from per_table_" + plan_id + " where point_id=? and mainbody_id='" + mainbodyid + "' and degree_id=?  and object_id not in (" + userIds.substring(1) + ")";
	try
	{
	    a_conn = AdminDb.getConnection();
	    ContentDAO dao = new ContentDAO(a_conn);

	    for (int j = 0; j < a_pointList.size(); j++)
	    {
		int maxNum = 0; // 最大指标数
		// String minGradeId=""; //最小标度
		String[] tempPoint = (String[]) a_pointList.get(j);
		ArrayList gradeList = (ArrayList) pointGradeMap.get(tempPoint[0].toLowerCase()); // 指标对应的标度

		// String[] temp=(String[])gradeList.get(gradeList.size()-1);
		// minGradeId=temp[5];
		if ("0".equals(tempPoint[2])) // 如果是定性指标
		{
		    String maxCount = null;
		    if ("-1".equals(fineMax)) {
                maxCount = (String) pointMaxValueMap.get(tempPoint[0]);
            } else {
                maxCount = fineMax;
            }
		    if (maxCount != null)
		    {
			if ("-1".equals(maxCount)) // 不限制最大数
            {
                continue;
            }
			float f_maxcount = Float.parseFloat(maxCount);
			if (f_maxcount > 0 && f_maxcount < 1)
			{
			    f_maxcount = (totalUserNum * f_maxcount);
			    f_maxcount = Float.parseFloat(PubFunc.round(String.valueOf(f_maxcount), 0));
			}
			for (int i = 0; i < userid.length; i++)
			{
			    String user_value = (String) usersValue.get(userid[i]);
			    String[] user_result = user_value.split("/");
			    if ("null".equals(user_result[j])) // 如果用户没填值，则continue
                {
                    continue;
                }
			    String gradeID = getDegreeID(user_result[j], gradeList); // 得到打分的标度号
			    /*
                                 * if(flag==1&&gradeID.equalsIgnoreCase(maxGradeId))
                                 * maxNum++; else
                                 * if(flag==2&&gradeID.equalsIgnoreCase(minGradeId))
                                 * maxNum++;
                                 */
			    if (gradeID.equalsIgnoreCase(maxGradeId)) {
                    maxNum++;
                }
			}
			ArrayList list = new ArrayList();
			list.add(tempPoint[0]);
			// if(flag==1).
			list.add(maxGradeId);
			// else if(flag==2)
			// pr.setString(2,minGradeId);
			rt = dao.search(sql,list);
			if (rt.next())
			{
			    maxNum += rt.getInt("nums");
			}
			if (flag == 1 && maxNum > f_maxcount) {
                info.append("<br>"+ResourceFactory.getProperty("performance.implement.kh_point")+"：" + tempPoint[1] + ResourceFactory.getProperty("performance.batchgrade.info1")
                    + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
            } else if (flag == 2)
			{
			    if (maxNum < f_maxcount && f_maxcount - maxNum >= noMarkNum) {
                    info.append("<br>"+ResourceFactory.getProperty("performance.implement.kh_point")+"：" + tempPoint[1] + ResourceFactory.getProperty("performance.batchgrade.info2")
                        + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
                }

			}
		    }
		}
	    }

	    if ("true".equalsIgnoreCase(WholeEval))
	    {
		String maxCount = (String) pointMaxValueMap.get("whole_grade");
		if (maxCount != null && !"-1".equals(maxCount)&&maxCount.length()>0)//如果是“”也要卡住，否则下面Float.parseFloat也会报错  zhaoxg add 2014-10-23
		{

		    /* 得到某计划某人的考评对象集合 */
		    ArrayList objectList = getPerplanObjects(Integer.parseInt(plan_id), mainbodyid);
		    /* 得到某计划考核主体给对象的评分结果hashMap */
		    HashMap perTableMap = getPerTableXXX(Integer.parseInt(plan_id), mainbodyid, objectList);
		    float f_maxcount = Float.parseFloat(maxCount);
		    if (f_maxcount > 0 && f_maxcount < 1)
		    {
			f_maxcount = (totalUserNum * f_maxcount);
			f_maxcount = Float.parseFloat(PubFunc.round(String.valueOf(f_maxcount), 0));
		    }

		    ArrayList wholeEvalList = getExtendInfoValue("2", gradeClass);
		    String[] t = null;
		    // if(flag==1)
		    t = (String[]) wholeEvalList.get(0);
		    // else if(flag==2)
		    // t=(String[])wholeEvalList.get(wholeEvalList.size()-1);
		    int num = 0;

		    for (int i = 0; i < userid.length; i++)
		    {
			String user_value = (String) usersValue.get(userid[i]);
			String[] user_result = user_value.split("/");
			if ("null".equals(user_result[user_result.length - 1])) // 如果用户没填值，则continue
            {
                continue;
            }
			if (user_result[user_result.length - 1].equalsIgnoreCase(t[0])) {
                num++;
            }
		    }

		    for (int j = 0; j < objectList.size(); j++)
		    {
			String[] temp = (String[]) objectList.get(j);
			HashMap objectResultMap = (HashMap) perTableMap.get(temp[0]);
			if (objectResultMap != null)
			{
			    String temp2 = (String) objectResultMap.get("whole_grade_id");
			    if (temp2 != null && !object_id.equals(temp[0]) && temp2.equals(t[0])) {
                    num++;
                }
			}
		    }
		    if (flag == 1 && num >= f_maxcount)
		    {
			if (wholeEval_value!=null&&wholeEval_value.equals(t[0]))
			{
			    info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.info3") + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
			}
		    }
		    if (flag == 2)
		    {
			if (wholeEval_value!=null&&wholeEval_value.equals(t[0]) && num + 1 < f_maxcount && f_maxcount - num > noMarkNum)
			{
			    info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.info4") + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
			} else if (num < f_maxcount && f_maxcount - num > noMarkNum)
			{
			    info.append("<br>" + ResourceFactory.getProperty("performance.batchgrade.info4") + String.valueOf(f_maxcount).substring(0, String.valueOf(f_maxcount).indexOf(".")) + "！ ");
			}
		    }

		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	} finally
	{
	    try
	    {
		if (rt != null) {
            rt.close();
        }
		if (a_conn != null) {
            a_conn.close();
        }

	    } catch (Exception ee)
	    {
		ee.printStackTrace();
	    }
	}

	// 如果打分值通过验证则返回成功。
	if (info.toString().trim().length() == 0) {
        info.append("success");
    }
	return info.toString();
    }

    /**
         * 检查考核对象的打分值是否符合引用指标的范围
         * 
         * @param templateID
         *                模版id
         * @param plan_id
         *                计划id
         * @param usersValue
         *                用户分值结果
         * @param userid
         *                用户id
         * @param scoreflag
         *                2混合，1标度
         * @return
         */
    public String validateScore(String templateID, String plan_id, HashMap usersValue, String[] userid)
    {

	StringBuffer info = new StringBuffer("");
	if (this.per_pointLists == null) {
        this.per_pointLists = getPerPointList2(templateID, plan_id); // 得到指标的信息集
    }
	ArrayList a_pointList = (ArrayList) this.per_pointLists.get(0);
	HashMap pointGradeMap = (HashMap) this.per_pointLists.get(1);
	HashMap userMap = getUserNameMap(userid);
	/*
         * for(int i=0;i<userid.length;i++) { String
         * userName=(String)userMap.get(userid[i]); String
         * user_value=(String)usersValue.get(userid[i]); String[]
         * user_result=user_value.split("/");
         */
	for (int j = 0; j < a_pointList.size(); j++)
	{
	    String[] tempPoint = (String[]) a_pointList.get(j);

	    if ("1".equals(tempPoint[2]) && tempPoint[8] != null && "1".equals(tempPoint[8])) // 如果为定量指标，并且统一打分
        {
            continue;
        }
	    String fieldItem = tempPoint[6]; // 打分指标的引用指标
	    String fieldItem_1 = tempPoint[7]; // 打分指标 下限引用指标
	    if ((fieldItem != null && !"".equals(fieldItem)) || (fieldItem_1 != null && !"".equals(fieldItem_1)))
	    {
		ArrayList gradeList = (ArrayList) pointGradeMap.get(tempPoint[0].toLowerCase()); // 指标对应的标度
		for (int i = 0; i < userid.length; i++)
		{
		    String userName = (String) userMap.get(userid[i]);
		    String user_value = (String) usersValue.get(userid[i]);
		    String[] user_result = user_value.split("/");
		    info.append(validateValue(tempPoint, gradeList, user_result[j], userName, userid[i].trim()));
		}
	    }
	}
	// }
	// 如果打分值通过验证则返回成功。
	if (info.toString().trim().length() == 0) {
        info.append("success");
    }
	return info.toString();
    }

    /**
         * 验证某对象在某指标下打得分是否符合标度限制
         * 
         * @param point
         * @param gradeList
         * @param scoreflag
         * @param value
         * @param userName
         * @return
         */
    public String validateValue(String[] point, ArrayList gradeList, String value, String userName, String userid)
    {

	String info = "";
	if (!"null".equalsIgnoreCase(value))
	{
	    // 指标标度过滤
	    if ((point[6] != null && point[6].trim().length() > 0) || (point[7] != null && point[7].trim().length() > 0))
	    {
		gradeList = getFiltrateDate2(gradeList, point[6], userid, point[2], point[7]);
	    }
	    if (gradeList.size() > 0)
	    {
		String[] tempGrade = (String[]) gradeList.get(0);
		String[] tempGrade2 = (String[]) gradeList.get(gradeList.size() - 1);
		if ("0123456789.".indexOf(value.charAt(0)) != -1) // 为数字
		{
		    if ("1".equals(point[2])) // 定量
		    {
			if (Float.parseFloat(value) > Float.parseFloat(tempGrade[6]))
			{
			    info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
			}
			if (Float.parseFloat(value) < Float.parseFloat(tempGrade2[7]))
			{
			    info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
			}
		    } else
		    {
			if (Float.parseFloat(value) > (Float.parseFloat(tempGrade[8]) * Float.parseFloat(tempGrade[6])))
			{
			    info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
			}
			if (Float.parseFloat(value) < (Float.parseFloat(tempGrade2[8]) * Float.parseFloat(tempGrade2[7])))
			{
			    info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
			}
		    }

		} else
		// 为字母
		{
		    if (value.toLowerCase().charAt(0) < tempGrade[5].toLowerCase().charAt(0)) {
                info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
            }
		    if (value.toLowerCase().charAt(0) > tempGrade2[5].toLowerCase().charAt(0)) {
                info = "<br>" + userName + " " + point[1] + ResourceFactory.getProperty("label.performance.scoreInfo");
            }

		}
	    } else {
            info = info = "<br>" + userName + " " + point[1] + " " + ResourceFactory.getProperty("performance.batchgrade.info5") + "！";
        }
	}
	return info;
    }

    /**
         * 指标标度过滤
         * 
         * @param gradeList
         * @param fielditemid
         *                上限指标
         * @param fielditemid1
         *                下限指标
         * @param userid
         * @param pointKind
         *                指标类型 0：定性 1：定量
         * @return
         */
    public ArrayList getFiltrateDate2(ArrayList gradeList, String fielditemid, String userid, String pointKind, String fielditemid1)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	String a_value_up = ""; // 上限值
	String a_value_down = ""; // 下限值
	try
	{
	    String temp = getup_down_value(fielditemid, fielditemid1, userid);
	    a_value_up = temp.split("/")[0].substring(1);
	    a_value_down = temp.split("/")[1].substring(1);

	    // 如果 下限值大于上限值 则返回空
	    if (a_value_up != null && a_value_down != null && isDigital(a_value_up) && isDigital(a_value_down) && Float.parseFloat(a_value_down) > Float.parseFloat(a_value_up))
	    {
		return new ArrayList();
	    } else
	    {
		if (isDigital(a_value_down) || isDigital(a_value_up))
		{

		    ArrayList a_tempList = new ArrayList();
		    for (int i = 0; i < gradeList.size(); i++)
		    {
			String[] a_point = (String[]) gradeList.get(i);

			if ("1".equals(pointKind)) // 定量
			{

			    if (isDigital(a_value_down)) // 有下限
			    {
				if ((Float.parseFloat(a_point[6])) < Float.parseFloat(a_value_down))
				{
				    break;
				}
			    }

			    if (isDigital(a_value_up)) // 有上限
			    {
				if (Float.parseFloat(a_value_up) >= Float.parseFloat(a_point[7]))
				{
				    a_tempList.add(a_point);

				}
			    } else {
                    a_tempList.add(a_point);
                }
			} else
			{

			    if (isDigital(a_value_down)) // 有下限
			    {
				if ((Float.parseFloat(a_point[8]) * Float.parseFloat(a_point[9])) < Float.parseFloat(a_value_down))
				{

				    break;
				}
			    }
			    if (isDigital(a_value_up)) // 有上限
			    {

				if (Float.parseFloat(a_value_up) >= (Float.parseFloat(a_point[8]) * Float.parseFloat(a_point[7])))
				{
				    a_tempList.add(a_point);

				}
			    } else {
                    a_tempList.add(a_point);
                }
			}
		    }
		    gradeList = a_tempList;
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return gradeList;
    }
 
    

    /**
         * 取得用户 得上限值 和下限值
         * 
         * @param fielditemid
         * @param fielditemid1
         * @param userid
         * @return
         */
    public String getup_down_value(String fielditemid, String fielditemid1, String userid)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	String a_value_up = ""; // 上限值
	String a_value_down = ""; // 下限值
	try
	{
	    String up_fieldSetID = ""; // 上限子集
	    String down_fieldSetID = ""; // 下限子集
	    StringBuffer where_sql = new StringBuffer("");
	    if (fielditemid != null && fielditemid.trim().length() > 1) {
            where_sql.append(" or itemid='" + fielditemid + "'");
        }
	    if (fielditemid1 != null && fielditemid1.trim().length() > 1) {
            where_sql.append(" or itemid='" + fielditemid1 + "'");
        }

	    RowSet rowSet = dao.search("select * from fielditem where " + where_sql.substring(3));
	    while (rowSet.next())
	    {
		if (rowSet.getString("itemid").equalsIgnoreCase(fielditemid)) {
            up_fieldSetID = rowSet.getString("fieldsetid");
        }
		if (rowSet.getString("itemid").equalsIgnoreCase(fielditemid1)) {
            down_fieldSetID = rowSet.getString("fieldsetid");
        }
	    }

	    String a_sql = "";
	    for (int i = 0; i < 2; i++)
	    {
		String fieldSetID = "";
		String fielditem = "";
		if (i == 0)
		{
		    fieldSetID = up_fieldSetID;
		    fielditem = fielditemid;
		} else
		{
		    fieldSetID = down_fieldSetID;
		    fielditem = fielditemid1;
		}
		if (!"".equals(fieldSetID))
		{
		    if ("A01".equalsIgnoreCase(fieldSetID))
		    {
			a_sql = "select " + fielditem + " from usr" + fieldSetID + "  where a0100='" + userid + "'";
		    } else
		    {
			a_sql = "select " + fielditem + " from usr" + fieldSetID + " a where a.i9999 =(select max(I9999) from usr" + fieldSetID + " b where a0100='" + userid
				+ "' and a.a0100=b.a0100)";
		    }
		    rowSet = dao.search(a_sql);
		    if (rowSet.next())
		    {
			if (rowSet.getString(1) != null)
			{
			    if (i == 0) {
                    a_value_up = rowSet.getString(1);
                } else {
                    a_value_down = rowSet.getString(1);
                }
			}
		    }
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return "a" + a_value_up + "/a" + a_value_down;

    }

    /** ******************************************************************** */

    /**
         * 取得模版下指标的最高标度
         */
    public HashMap getMaxPointValue(String template_id)
    {

	HashMap map = new HashMap();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    String sql = "select ptp.point_id,ptp.score,ptp.rank,pp.pointkind,b.gradecode,b.gradevalue,b.top_value,b.bottom_value from per_template_item pti,per_template_point ptp,per_point pp,"
		    + " (select a.* from per_grade a where a.gradevalue=(select max(b.gradevalue) from per_grade b where a.point_id=b.point_id  ) )b"
		    + " where ptp.item_id=pti.item_id and pp.point_id=ptp.point_id and ptp.point_id=b.point_id  and pti.template_id='" + template_id + "' ";
	    RowSet rowSet = dao.search(sql);
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		abean = new LazyDynaBean();
		String point_id = rowSet.getString("point_id");
		String score = rowSet.getString("score");
		String rank = rowSet.getString("rank");
		String pointkind = rowSet.getString("pointkind");
		String gradecode = rowSet.getString("gradecode");
		String gradevalue = rowSet.getString("gradevalue");
		String top_value = rowSet.getString("top_value");
		abean.set("score", score);
		abean.set("rank", rank);
		abean.set("pointkind", pointkind);
		abean.set("gradecode", gradecode);
		abean.set("gradevalue", gradevalue);
		abean.set("top_value", top_value);
		map.put(point_id, abean);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }
    /**
     * 按岗位素质模型 取定义指标标度最大值
     * @return
     */
    public HashMap getMaxPointValueByModel()
    {

	HashMap map = new HashMap();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
//	    String sql = "select ptp.point_id,ptp.score,ptp.rank,pp.pointkind,b.gradecode,b.gradevalue,b.top_value,b.bottom_value from per_template_item pti,per_template_point ptp,per_point pp,"
//		    + " (select a.* from per_grade a where a.gradevalue=(select max(b.gradevalue) from per_grade b where a.point_id=b.point_id  ) )b"
//		    + " where ptp.item_id=pti.item_id and pp.point_id=ptp.point_id and ptp.point_id=b.point_id  and pti.template_id='" + template_id + "' ";
//	    
	    String sql = "select pcm.point_id,pcm.score,pcm.rank,pp.pointkind,b.gradecode,b.gradevalue,b.top_value,b.bottom_value " +
	    	  " from per_competency_modal pcm,per_point pp,(select a.* from per_grade a where a.gradevalue =(select max(b.gradevalue) from per_grade b where a.point_id = b.point_id)) b  " +
	    	  " where pcm.point_id=pp.point_id and pcm.point_id=b.point_id and "+Sql_switcher.dateValue(historyDate)+" between pcm.start_date and pcm.end_date and object_type='3' and object_id = '"+getE01a1(this.object_id)+"' ";
	    RowSet rowSet = dao.search(sql);
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		abean = new LazyDynaBean();
		String point_id = rowSet.getString("point_id");
		String score = rowSet.getString("score")==null ? "0" : rowSet.getString("score");//2013.12.3 pjf
		String rank = rowSet.getString("rank")==null ? "0" : rowSet.getString("rank");
		String pointkind = rowSet.getString("pointkind");
		String gradecode = rowSet.getString("gradecode");
		String gradevalue = rowSet.getString("gradevalue");
		String top_value = rowSet.getString("top_value");
		abean.set("score", score);
		abean.set("rank", rank);
		abean.set("pointkind", pointkind);
		abean.set("gradecode", gradecode);
		abean.set("gradevalue", gradevalue);
		abean.set("top_value", top_value);
		map.put(point_id, abean);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }
    public HashMap getTemplatePointDetail(ArrayList pointList)
    {

	HashMap map = new HashMap();
	String pointId = "";
	ArrayList tempList = new ArrayList();
	for (int i = 0; i < pointList.size(); i++)
	{

	    String[] temp = (String[]) pointList.get(i);
	    if ("".equals(pointId)) {
            pointId = temp[1];
        }
	    if (!temp[1].equals(pointId))
	    {
		map.put(pointId.trim().toLowerCase(), tempList);
		pointId = temp[1];
		tempList = new ArrayList();
	    }
	    tempList.add(temp);
	}
	map.put(pointId.trim().toLowerCase(), tempList);
	return map;
    }

    // 将考核对象文档记录设为提交状态
    public void setArticleState(String plan_id, String a0100, String nbase)
    {

	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    dao.update("update per_article set state=1 where   Article_type in (1,2) and plan_id=" + plan_id + " and a0100='" + a0100 + "' and fileflag=1 and lower(nbase)='" + nbase.toLowerCase()
		    + "'");
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    String a_dgree_id = "";
    double a_score=0;

    HashMap pointReasons = null; // 赋分原则

    public HashMap getPointReasonsMap(String[] object_ids, String mainbody_id, String plan_id)
    {

	HashMap map = new HashMap();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer objectIDs = new StringBuffer("");
	    for (int i = 0; i < object_ids.length; i++)
	    {
		objectIDs.append(",'" + object_ids[i] + "'");
	    }
	    RowSet rowSet = dao.search("select * from per_table_" + plan_id + " where object_id in (" + objectIDs.substring(1) + ") and mainbody_id='" + mainbody_id + "' order by object_id");
	    ResultSetMetaData data = rowSet.getMetaData();
	    boolean isReasons = false;
	    for (int i = 0; i < data.getColumnCount(); i++)
	    {
		String name = data.getColumnName(i + 1).toLowerCase();
		if ("Reasons".equalsIgnoreCase(name)) {
            isReasons = true;
        }
	    }
	    if (!isReasons)
	    {
			Table table = new Table("per_table_" + plan_id);
			Field obj = new Field("Reasons", "Reasons");
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);
			obj.setVisible(false);
			obj.setAlign("left");
			table.addField(obj);
			DbWizard dbWizard = new DbWizard(this.conn);
			dbWizard.addColumns(table);
			rowSet = dao.search("select * from per_table_" + plan_id + " where object_id in (" + objectIDs.substring(1) + ") and mainbody_id='" + mainbody_id + "' order by object_id");
	    }
	    while (rowSet.next())
	    {
		String scoreCause = Sql_switcher.readMemo(rowSet, "reasons");
		String point_id = rowSet.getString("point_id");
		String object_id = rowSet.getString("object_id");
		map.put(object_id + "/" + point_id.toLowerCase(), scoreCause);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
    }

    
    /**	
	 *  向根据计算公式计算出总体评价的临时表中写入各考核指标评分 JinChunhai 2012.11.13
	 */
	public void setTempWholeEvalTableScore(String object_id,String mainbody_id,String totalscore)
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);			 
			String tablename = "t#_per_app_"+this.planid;
			DbWizard dbWizard = new DbWizard(this.conn);
			
			// 此临时表若存在
			if(dbWizard.isExistTable(tablename,false))
			{
				// 获得当前考核计划关联考核模板的所有指标
				ArrayList pointList = getPointidList();	
				HashMap pointScore = getPointidScoreMap(object_id,mainbody_id);
				
				ArrayList scoreList = new ArrayList();				
				ArrayList list = new ArrayList();
				list.add(totalscore);
				// 向临时表中写入考核指标评分
				StringBuffer updatesql = new StringBuffer("update "+tablename+" set totalscore=?");
			    for (int i = 0; i < pointList.size(); i++)
			    {
			    	String point_id = (String) pointList.get(i);				
			    	updatesql.append(","+point_id.toUpperCase()+"=?");
			    	
			    	if(pointScore!=null && pointScore.get(point_id)!=null) {
                        list.add(pointScore.get(point_id));
                    } else {
                        list.add("0");
                    }
			    }
			    updatesql.append(" where object_id=? and mainbody_id=? ");
															
				list.add(object_id);
				list.add(mainbody_id);
				scoreList.add(list);
				
				if(scoreList.size()>0)
				{							  			  
					dao.batchUpdate(updatesql.toString(), scoreList);		
				}
			}
																						
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	// 判断是否有等级不同分数相同的考核对象  JinChunhai 2013.06.18
	public boolean isHaveSameScoreObjects(String plan_id,String objectIDs)
    {
    	boolean flag = false;
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		// 不按考核对象类别
    		StringBuffer sql = new StringBuffer("select score,resultdesc from per_result_"+plan_id+" ");
    		sql.append(" where object_id in ("+objectIDs+") ");
    		sql.append(" group by score,resultdesc order by score desc ");
    	/*	
    		// 按考核对象类别,并且排除其中的三个类别 吉林电力特殊要求
    		StringBuffer sql = new StringBuffer("select body_id,score,resultdesc from per_result_"+plan_id+" ");
    		sql.append(" where object_id in ("+objectIDs+") and body_id not in ('73','74','82') ");
    		sql.append(" group by body_id,score,resultdesc order by score desc ");
    	*/	
	    	rs = dao.search(sql.toString());
	    	while (rs.next()) 
	    	{
	    		// 不按考核对象类别
		    	String score = rs.getString("score")!=null?rs.getString("score"):"";						      	
				if(rs.next()!=false)
				{
					if(score!=null && score.length()>0)
					{
						if(score.equalsIgnoreCase(rs.getString("score")!=null?rs.getString("score"):""))
						{
							flag = true;
							break;
						}
					}					
				}				
				rs.previous();          
				
			/*	
	    		// 按考核对象类别
	    		String body_id = rs.getString("body_id")!=null?rs.getString("body_id"):"";	
	    		String score = rs.getString("score")!=null?rs.getString("score"):"";
				if(rs.next()!=false)
				{
					if(body_id!=null && body_id.length()>0)
					{
						if(body_id.equalsIgnoreCase(rs.getString("body_id")!=null?rs.getString("body_id"):""))
						{
							if(score!=null && score.length()>0)
							{
								if(score.equalsIgnoreCase(rs.getString("score")!=null?rs.getString("score"):""))
								{
									flag = true;
									break;
								}
							}
						}
					}
				}				
				rs.previous(); 		
			*/	
				
	    	}	    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return flag;
    }
	/**
     * 获得指标评分
     * @return
     */
    public HashMap getPointidScoreMap(String object_id,String mainbody_id)
    {

    	HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{		    
			StringBuffer serchsql = new StringBuffer("");
			serchsql.append("select score,point_id from per_table_"+this.planid+" ");
			serchsql.append(" where object_id = '"+object_id+"' and mainbody_id = '"+mainbody_id+"' ");
			serchsql.append(" order by point_id ");
			rowSet = dao.search(serchsql.toString());			
			while(rowSet.next())
			{		
				String point_id = rowSet.getString("point_id");
				String score = rowSet.getString("score")!=null?rowSet.getString("score"):"";				
				map.put(point_id.toUpperCase(), score);             												
			}
			
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
    }
	
	/**
     * 获得当前考核计划关联考核模板的所有指标
     * @return
     */
    public ArrayList getPointidList()
    {

    	ArrayList pointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{		    
			String sql = "select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
				       + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + this.planid + " order by e.point_id ";
			rowSet = dao.search(sql);
			while (rowSet.next())
			{
				pointList.add(rowSet.getString(1).toUpperCase()); 
			}
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return pointList;
    }
    
    // 定义公式可选择的指标
	public ArrayList getSelectList()
	{
		ArrayList filelist = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			//加指标
			String sql = "select e.point_id,e.pointname from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
			       + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + this.planid + " order by e.point_id ";
						       
			rowSet = dao.search(sql);
			while(rowSet.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rowSet.getString("point_id"));
				item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("pointname")));
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				filelist.add(item);
			}
			FieldItem item = new FieldItem();
			item.setItemid("totalscore");
			item.setItemdesc("总分");
			item.setItemtype("N");
			item.setDecimalwidth(2);
			item.setItemlength(12);
			filelist.add(item);	
			
			item = new FieldItem();
			item.setItemid("pointnumber");
			item.setItemdesc("指标个数");
			item.setItemtype("N");
			item.setDecimalwidth(0);
			item.setItemlength(10);
			filelist.add(item);		
			if (rowSet != null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return filelist;
	}
	
    /**	
	 *  根据计算公式计算出总体评价并保存入库
	 */
	public String getFormulaSqlToValue(UserView userView,String object_ids,String mainbody_id,String totalAppFormula,String gradeClass,String[] userid)
	{		
		String tablename = "t#_per_app_"+this.planid;		
		RowSet rowSet = null;
		HashMap map = new HashMap();
		String totalAppValue = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);

			// 此临时表若存在
			DbWizard dbWizard = new DbWizard(this.conn);	
			if(dbWizard.isExistTable(tablename,false))
			{			
				YksjParser yp = new YksjParser(userView, this.getSelectList(), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");
				yp.setVerify(false);
				yp.run(totalAppFormula.trim(), this.conn, "", tablename);	
								
				StringBuffer sql = new StringBuffer("");
				sql.append("select "+yp.getSQL()+" wholeEval,object_id from "+tablename+" ");
				sql.append(" where object_id in ("+object_ids+") and mainbody_id = '"+mainbody_id+"' ");
				rowSet = dao.search(sql.toString());	
				while(rowSet.next())
				{	
					String object_id = rowSet.getString("object_id");
					String wholeEval = rowSet.getString("wholeEval")!=null?rowSet.getString("wholeEval"):"";				
					map.put(object_id.toUpperCase(), wholeEval);					
				}	    		
				
	    		for(int i=0;i<userid.length;i++)
	    		{
	    			String object_id = userid[i];
	    			String wholeEval = (String)map.get(object_id);
	    			//totalAppValue += "#"+wholeEval;
	    			
					// 更改临时表
					StringBuffer updatesql = new StringBuffer("update "+tablename+" set gradedesc='"+wholeEval+"' ");
					updatesql.append(" where object_id = '"+object_id+"' and mainbody_id = '"+mainbody_id+"' ");
				//	updatesql.append("  ");
					dao.update(updatesql.toString(), new ArrayList());
					
					// 更改考核主体表中的总体评价字段
					StringBuffer upMaisql = new StringBuffer("update per_mainbody set whole_grade_id= ");
					upMaisql.append(" (select pds.id from per_degree pd,per_degreedesc pds ");
					upMaisql.append(" where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass+" and pds.itemname='"+wholeEval+"') ");
					upMaisql.append(" where plan_id = "+this.planid+" and object_id = '"+object_id+"' and mainbody_id = '"+mainbody_id+"' ");
					dao.update(upMaisql.toString(), new ArrayList());
					String ssql="select pds.id from per_degree pd,per_degreedesc pds  where pd.degree_id=pds.degree_id and pd.degree_id=13 and pds.itemname='"+wholeEval+"'";
					rowSet=dao.search(ssql);
					while(rowSet.next()){
						totalAppValue += "#"+rowSet.getString("id");
					}
				
	    		}
	    		if(totalAppValue!=null && totalAppValue.trim().length()>0) {
                    totalAppValue = totalAppValue.substring(1);
                }
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return totalAppValue;
	}
    	
	public void SetDescription(String planid,String objectid,String mainbodyID,ArrayList appraiseArrayList)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String desc = "";
		try
		{			
			StringBuffer xml = new StringBuffer(""); 
			xml.append("<?xml version='1.0' encoding = 'GB2312' ?>");
			xml.append("<descriptive_evaluate>");
			for(int x=0;x<appraiseArrayList.size();x++)
			{
				String tempStr = (String)appraiseArrayList.get(x);
				String[] matters = tempStr.split("~");
				String contant = "";
				if(!"null".equalsIgnoreCase(matters[1])) {
                    contant = matters[1];
                }
				
				contant = contant.replaceAll("brbr","br");				
				xml.append("<option id='"+matters[0]+"'>"+contant+"</option>");				
			}
			xml.append("</descriptive_evaluate>");
			
			String sql="update per_mainbody set description=? where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'";
			ArrayList list = new ArrayList();
			list.add(xml.toString());
			dao.update(sql,list);
			 		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
    
    /**
         * 将评分结果插入数据库
         * 
         * @param userid
         *                考核对象
         * @param flag
         *                操作类型 1：保存 2：提交
         * @param plan_id
         *                考核计划
         * @param template_id
         *                考核计划相应模版id
         * @param usersValue
         *                各对象的评分结果
         * @param status
         *                权重分值标识
         * @param scoreflag
         *                2混合，1标度
         * @return
         */
    
    String error_info="";
    
    public String insertGradeResult(String[] userid, String flag, String plan_id, String template_id, HashMap usersValue, String mainbody_id, String status, String scoreflag) throws GeneralException
    {

	String isSuccess = "1"; // 保存结果 1：保存成功 0：保存失败 2：指标范围为空不予保存
	ContentDAO dao = new ContentDAO(this.conn);
	/* 查找参数表 */
	try
	{

	    DBMetaModel dbmodel = new DBMetaModel(this.conn);
	    if (!dbmodel.isHaveTheTable("per_table_" + plan_id)) {
            dbmodel.reloadTableModel("per_table_" + plan_id);
        }

	    HashMap perPlanObjectMap = getPerPlanObjects2(Integer.parseInt(plan_id), mainbody_id);
	    /* 删除表中用户已有的纪录 */
	    String sql = "delete from PER_TABLE_" + plan_id + " where object_id=? and mainbody_id=?";
	    ArrayList deleteList = new ArrayList();
	    for (int i = 0; i < userid.length; i++)
	    {
			String[] temp = (String[]) perPlanObjectMap.get(userid[i]);
			if (temp == null) // 并发数太大时，取的值为空,原因待查
			{
			    // System.out.println(userid[i]+" -----
	                        // "+mainbody_id+"----"+perPlanObjectMap.size());
			    continue;
	
			}
			if (!"2".equals(temp[2]))
			{
			    ArrayList tempList = new ArrayList();
			    tempList.add(userid[i]);
			    tempList.add(mainbody_id);
			    deleteList.add(tempList);
			}
	    }

	    Hashtable htxml = new Hashtable();
	    if(loadxml==null)
	    {
	    	if(planLoadXmlMap.get(plan_id)==null)
	    	{
	    				loadxml = new LoadXml(this.conn,plan_id);
	    				planLoadXmlMap.put(plan_id,loadxml);
	    	}
	    	else {
                loadxml=(LoadXml)planLoadXmlMap.get(plan_id);
            }
	    }
	    htxml = loadxml.getDegreeWhole();
	    String ScaleToDegreeRule = (String) htxml.get("limitrule"); // 分值转标度规则（1-就高
                                                                        // 2-就低
                                                                        // 3-就近就高（默认值））
	    String NodeKnowDegree = (String) htxml.get("NodeKnowDegree"); // 了解程度
	    String WholeEval = (String) htxml.get("WholeEval"); // 总体评价
	    String WholeEvalMode = (String) htxml.get("WholeEvalMode");
	    String MustFillWholeEval=(String)htxml.get("MustFillWholeEval");  //总体评价必填
	    String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
	    String SameResultsOption = (String) htxml.get("SameResultsOption");// 考核对象指标结果全部相同时的选项
                                                                                // 1: 可以保存, 2: 不能保存 ,3 指定不能保存的标度	
	    String NoCanSaveDegrees=","+((String)htxml.get("NoCanSaveDegrees")).toUpperCase()+",";	
	    
	    //	  相同时能否保存(Ture, False),默认为 True
		String CanSaveAllObjsScoreSame= (String) htxml.get("CanSaveAllObjsScoreSame");
		
		
	    ArrayList pointList0 = getPerPointList(template_id, plan_id);
	    ArrayList pointInfoList = (ArrayList) pointList0.get(0); // 指标详细集
	    ArrayList pointList = (ArrayList) pointList0.get(1); // 指标集
	 //   HashMap pointDetailMap = getTemplatePointDetail(pointInfoList);
	    
	    String isNull = (String) pointList0.get(2); // 指标范围是否为空 0：不为空 1：为空
	    String showOneMark = (String) htxml.get("ShowOneMark"); // BS打分时显示统一打分的指标，以便参考 // Boolean, // 默认为False
	   
	    
	    
	    this.setShowOneMark(showOneMark);

	    HashMap pointprivMap = getPointprivMap(plan_id, mainbody_id); // 取得该考核主体的指标权限

	   // if (this.BlankScoreOption.equals("1"))
	   //	this.pointMaxValueMap = getMaxPointValue(template_id); // 指标未打分时，0
                                                                        // 按未打分处理，1
                                                                        // 计为最高分，默认值为按未打分处理

	    // ////////////////////////
	    ArrayList recordLists = new ArrayList();
	    ArrayList mainInfoList = new ArrayList();

	    boolean a_isChange = false; // 控制 变量
	    StringBuffer aup_sql = new StringBuffer("update per_mainbody set ");
//	    if (NodeKnowDegree.equals("true") && !WholeEval.equals("true"))
//	    {
//		aup_sql.append(" know_id=?");
//		a_isChange = true;
//	    } else if (!NodeKnowDegree.equals("true") && WholeEval.equals("true")&&"0".equals(WholeEvalMode))
//	    {
//		aup_sql.append(" whole_grade_id=?");
//		a_isChange = true;
//	    } else if (NodeKnowDegree.equals("true") && WholeEval.equals("true")&&"0".equals(WholeEvalMode))
//	    {
//		aup_sql.append(" know_id=?");
//		aup_sql.append(", whole_grade_id=?");
//		a_isChange = true;
//	    }
/*************************上面写法复杂且有问题改为下  zzk 2014/1/28******************************/
	    if ("true".equals(NodeKnowDegree)){
			aup_sql.append(" know_id=?,");
	    }
	    if( "true".equals(WholeEval)&&"0".equals(WholeEvalMode)){
	    	aup_sql.append(" whole_grade_id=?,");
	    }
	     
//	    if (a_isChange)
//		aup_sql.append(" ,");

	    aup_sql.append("status=?");

	    StringBuffer temp_sql = new StringBuffer("");
	    temp_sql.append(" where object_id=");
	    temp_sql.append("?");
	    temp_sql.append(" and mainbody_id=?");
	    temp_sql.append(" and plan_id=?");
	    aup_sql.append(temp_sql.toString());
	    
	    LazyDynaBean abean=null;
	    
		if(!"0".equals(this.BlankScoreOption)&&userid.length>1&&("1".equals(scoreflag)|| "2".equals(scoreflag))) // 指标未打分时，0 按未打分处理，1  计为最高分，默认值为按未打分处理 2用下面的参数
		{
			// BlankScoreUseDegree = ""; // 指标未打分，按用户定义的标度, 具体选自标准标度中,
			 if (!"0".equals(scoreNumPerPage)) //分页显示
			 {
				 StringBuffer obj_tmp_str0=new StringBuffer(",");
				 RowSet rowSet=dao.search("select distinct object_id from per_table_"+plan_id+" where mainbody_id='"+mainbody_id+"' ");
				
				 while(rowSet.next())
				 {
					 String object_id=rowSet.getString("object_id");
					 obj_tmp_str0.append(object_id+",");
					 
				 }
				 StringBuffer subStr=new StringBuffer("");
				 for(Iterator t=perPlanObjectMap.keySet().iterator();t.hasNext();)
				 {
					 String object_id=(String)t.next();
					 subStr.append(",'"+object_id+"'");
				 }
				 HashMap kw_map=new HashMap();
				 rowSet=dao.search("select know_id,whole_grade_id,object_id from per_mainbody where object_id in ("+subStr.substring(1)+") and mainbody_id='"+mainbody_id+"' and plan_id="+plan_id);
				 while(rowSet.next())
				 {
					String _object_id=rowSet.getString("object_id");
					String know_id=rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"null";
					String whole_grade_id=rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"null";
					abean=new LazyDynaBean();
					abean.set("know_id",know_id);
					abean.set("whole_grade_id",whole_grade_id);
					kw_map.put(_object_id, abean);
					
				 }
				 
				 
				 ArrayList list=new ArrayList();
				 StringBuffer useridStr=new StringBuffer(",");
				 for (int i = 0; i < userid.length; i++)
				 {
					 useridStr.append(userid[i]+",");
					 list.add(userid[i]);
				 }
				  if ("1".equals(this.BlankScoreOption)) {
                      this.pointMaxValueMap = getMaxPointValue(template_id); // 指标未打分时，0  按未打分处理，1 计为最高分，默认值为按未打分处理
                  }
				
				 StringBuffer obj_tmp_str=new StringBuffer("");
				 for(Iterator t=perPlanObjectMap.keySet().iterator();t.hasNext();)
				 {
					 String object_id=(String)t.next();
					 if (!"True".equalsIgnoreCase(this.mitiScoreMergeSelfEval))
					 {
						 if(object_id.equalsIgnoreCase(mainbody_id)) {
                             continue;
                         }
					 } 
					 if(useridStr.indexOf(","+object_id+",")==-1&&obj_tmp_str0.indexOf(","+object_id+",")==-1)
					 {
						 String[] temp = (String[]) perPlanObjectMap.get(object_id);
						 if (temp == null) // 并发数太大时，取的值为空,原因待查
						 {
							    continue;
						 }
						 if (!"2".equals(temp[2])&&!"7".equals(temp[2])&&!"4".equals(temp[2]))
						 { 
					//		 userid[userid.length]=object_id;
							 list.add(object_id);
							 StringBuffer userValueStr=new StringBuffer("");
							 for (int e = 0; e < pointList.size(); e++)
							 {
									String[] temp0 = (String[]) pointList.get(e);
									if ("1".equals(temp0[2]) && temp0[7] != null && "1".equals(temp0[7])) // 如果为定量指标，并且统一打分，则不保存
									{
										userValueStr.append("/null");
									}
									else
									{
										 if ("1".equals(this.BlankScoreOption))
										 { 
												    abean = (LazyDynaBean) this.pointMaxValueMap.get(temp0[0]); 
												    if(abean!=null)
												    {
												    	if("1".equals(scoreflag)&& "0".equals(temp0[2])) //标度
                                                        {
                                                            userValueStr.append("/"+(String) abean.get("gradecode"));
                                                        } else //混合
												    	{
												    		if ("0".equals(temp0[2]))
														    {
												    			userValueStr.append("/"+PubFunc.multiple((String) abean.get("score"), (String) abean.get("gradevalue"), 1)); 
														    } else
														    {
														    	userValueStr.append("/"+(String) abean.get("top_value")); 
														    }	
												    	}
												    }
												    else {
                                                        userValueStr.append("/null");
                                                    }
												    	 
										  } 
										  else if ("2".equals(this.BlankScoreOption))
										  {
											  if("1".equals(scoreflag)&& "0".equals(temp0[2])) //标度
                                              {
                                                  userValueStr.append("/"+BlankScoreUseDegree.toUpperCase());
                                              } else
											  {
												  ArrayList tempList=(ArrayList) this.perPointGradedescMap.get(temp0[0].toLowerCase().trim());
												  boolean isValue = false;
												  String[] m = null;
												  for (Iterator tt = tempList.iterator(); tt.hasNext();)
												  {
													    String[] temp1 = (String[]) tt.next();
													    if (temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree))
													    {
															isValue = true;
															m = temp1;
													    }
												  }
												  
												  
													if (isValue)
													{
													    if ("0".equals(temp0[2]))
													    {
													    	
															String d_value = PubFunc.round(String.valueOf(Float.parseFloat(m[9]) * Float.parseFloat(m[8])), 1);
														    userValueStr.append("/"+d_value);
															 
													    } else
													    {
													    	 userValueStr.append("/"+m[6]); 
													    }
													}
												  
											  }
										  } 
									}
							 }
							 
							 if ("true".equals(NodeKnowDegree))
							 {
								 if(kw_map.get(object_id)!=null)
								 {
									  abean=(LazyDynaBean)kw_map.get(object_id);
									  String know_id=(String)abean.get("know_id");
									  userValueStr.append("/"+know_id);
								 }
								 else {
                                     userValueStr.append("/null");
                                 }
							 }
							 if("true".equals(WholeEval)&&"0".equals(WholeEvalMode))
							 {
								 
								 if(kw_map.get(object_id)!=null)
								 {
									  abean=(LazyDynaBean)kw_map.get(object_id);
									  String whole_grade_id=(String)abean.get("whole_grade_id");
									  userValueStr.append("/"+whole_grade_id);
								 }
								 else {
                                     userValueStr.append("/null");
                                 }
							 }
							 usersValue.put(object_id, userValueStr.substring(1));
						 }
						 
					 }
				 }
				 
				 userid=new String[list.size()];
				 for(int i=0;i<list.size();i++) {
                     userid[i]=(String)list.get(i);
                 }
				 
			 }
		}
	    
	    
	    
	    
	    
	    // ///////////////////
	    if ("0".equals(isNull) || ("1".equals(status) && "1".equals(scoreflag)))
	    {

		pointReasons = getPointReasonsMap(userid, mainbody_id, plan_id);

		boolean isSame_all = true; // 考核对象指标结果是否全部相同
	    String a_temp_all = "";
	    double a_temp_all_score=0d;
	    boolean is_Null_all = false; // 是否有未填项
	    boolean is_Null_wholeEval=false; //总体评价是否有未填项
	    boolean isPfPoint=false;  //是否有评分指标
	    StringBuffer obj_str=new StringBuffer("");
	    int num=0;
	    //设了“动态主体权重控制到指标”且为必打分的，有权限的指标必须评分。
	    if("true".equalsIgnoreCase(this.DynaBodyToPoint)&& "2".equals(flag)) {
            isMustScoreByPriv(pointList,userid,mainbody_id,plan_id,usersValue);
        }
	    
	    ArrayList userResultList=new ArrayList();
	    int id_num=userid.length*pointList.size();
	    if(id_num>20) {
            id_num=20;
        }
	    IDGenerator idg = new IDGenerator(2, this.conn);
	    ArrayList idlist = idg.getId("per_table_xxx.id",id_num);
	    
	    int index=0;
	    
	    StringBuffer wholeEvalMustFillBuffer=new StringBuffer();// zzk 2014/2/20  总体评级必填  提示出具体人
		for (int i = 0; i < userid.length; i++)
		{
			userResultList=new ArrayList();
		    String[] temp0 = (String[]) perPlanObjectMap.get(userid[i]);
		    HashMap pointMap = (HashMap) pointprivMap.get(userid[i]);
		    if (temp0 == null)// 并发数太大时，取的值为空，待查
		    {
			// System.out.println(userid[i]+" -----
                        // "+mainbody_id+"----"+perPlanObjectMap.size());
		    	continue;
		    }
		    
		    
		    if (!"2".equals(temp0[2]) && !"7".equals(temp0[2]))
		    {
				String user_id = userid[i];
				String user_value = (String) usersValue.get(user_id);
				String[] user_result = user_value.split("/");
				if (!"4".equals(temp0[2]))
				{
				    // ArrayList recordList=new ArrayList();
					num++;
				    boolean isSame = true; // 考核对象指标结果是否全部相同
				    String a_temp = "";
				    double a_temp_score=0d;
				    boolean is_Null = false; // 是否有未填项
				    for (int t = 0; t < pointList.size(); t++)
				    {
						String[] temp = (String[]) pointList.get(t);
						if ("1".equals(temp[2]) && temp[7] != null && "1".equals(temp[7])) // 如果为定量指标，并且统一打分，则不保存
                        {
                            continue;
                        }
						// RecordVo vo=getPerTableVo(plan_id,user_id,mainbody_id,temp,user_result,t,pointInfoList,ScaleToDegreeRule,status,scoreflag);
						a_dgree_id = "";
						a_score=0d;
						
						
						 ArrayList tempList=null;
						 if (!"null".equals(user_result[t]))
						 { 
								String _id="";
								if(index<20) {
                                    _id=(String)idlist.get(index++);
                                } else
								{
									idlist= idg.getId("per_table_xxx.id",id_num);
									index=0;
									_id=(String)idlist.get(index++);
								}
								tempList = getPerTableVo2(_id,plan_id, user_id, mainbody_id, temp, user_result, t, (ArrayList) this.perPointGradedescMap.get(temp[0].toLowerCase().trim()),
									ScaleToDegreeRule, status, scoreflag);
						 }	
						
				//		ArrayList tempList = getPerTableVo2(plan_id, user_id, mainbody_id, temp, user_result, t, (ArrayList) this.perPointGradedescMap.get(temp[0].toLowerCase().trim()),
				//			ScaleToDegreeRule, status, scoreflag);
						// 判断考核对象指标结果是否全部相同
						isPfPoint=true;
						if(("1".equals(scoreflag)|| "3".equals(SameResultsOption))&&"2".equals(flag))  //标度
						{
						
							if (a_dgree_id != null && !"".equals(a_dgree_id) && a_dgree_id.trim().length() > 0)
							{
							    if (a_temp == null || "".equals(a_temp)) {
                                    a_temp = a_dgree_id;
                                } else
							    {
									if (!a_temp.equals(a_dgree_id)) {
                                        isSame = false;
                                    }
										
							    }
							    
							    if (a_temp_all == null || "".equals(a_temp_all))
							    {
							    	a_temp_all= a_dgree_id;
							    }
							    else
							    {
									if (!a_temp_all.equals(a_dgree_id))
									{  
										isSame_all=false;
									}
							    }
							    
							} else
							{
							    if (pointMap!=null && ((String) pointMap.get(temp[0]))!=null && "1".equals((String) pointMap.get(temp[0])))
							    {
							    	is_Null = true;
							    	is_Null_all=true;
							    }
							}
						}
						else
						{
							
							if (a_score!=0)
							{
							    if (a_temp_score==0) {
                                    a_temp_score = a_score;
                                } else
							    {
									if (a_temp_score!=a_score) {
                                        isSame = false;
                                    }
										
							    }
							    
							    if (a_temp_all_score==0)
							    {
							    	a_temp_all_score=a_score;
							    }
							    else
							    {
									if (a_temp_all_score!=a_score)
									{  
										isSame_all=false;
									}
							    }
							    
							} else
							{
							    if (pointMap!=null && ((String) pointMap.get(temp[0]))!=null && "1".equals((String) pointMap.get(temp[0])))
							    {
							    	is_Null = true;
							    	is_Null_all=true;
							    }
							}
							
						}
		
						if (tempList != null)
						{
							recordLists.add(tempList);
							userResultList.add(tempList);
						}
				    }
				    
				    
				    
				    
				    if (("2".equals(SameResultsOption)|| "3".equals(SameResultsOption))&&userResultList.size()>1 &&"2".equals(flag))
				    {
						if (isSame && !is_Null)
						{
							if("2".equals(SameResultsOption))
							{
								error_info=ResourceFactory.getProperty("performance.batchgrade.info15")+"！";
								throw (new GeneralException(ResourceFactory.getProperty("performance.batchgrade.info15")+"！"));
							}
							else
							{
								if(NoCanSaveDegrees.indexOf(","+a_dgree_id.toUpperCase()+",")!=-1)
								{
									error_info=ResourceFactory.getProperty("performance.batchgrade.info16")+"！";
									throw (new GeneralException(ResourceFactory.getProperty("performance.batchgrade.info16")+"！"));
								}
							}
						}
				    }
	
				}
	
				ArrayList tempList = new ArrayList();
	
				boolean isChange = false; // 控制 变量
//				if (NodeKnowDegree.equals("true") && !WholeEval.equals("true"))
//				{
//				    if (user_result[(user_result.length - 1)].equalsIgnoreCase("null") || user_result[(user_result.length - 1)].trim().length() == 0)
//					tempList.add(new Integer(-1));
//				    else
//					tempList.add(new Integer(user_result[(user_result.length - 1)]));
//				    isChange = true;
//				} else if (!NodeKnowDegree.equals("true") && WholeEval.equals("true")&&"0".equals(WholeEvalMode))
//				{
//				    if (user_result[(user_result.length - 1)].equalsIgnoreCase("null") || user_result[(user_result.length - 1)].trim().length() == 0)
//					{
//				    	tempList.add(new Integer(-1));
//				    	if (!temp0[2].equals("4"))
//				    		is_Null_wholeEval=true;
//					}
//				    else
//				    	tempList.add(new Integer(user_result[(user_result.length - 1)]));
//				    isChange = true;
//				} else if (NodeKnowDegree.equals("true") && WholeEval.equals("true")&&"0".equals(WholeEvalMode))
//				{
//				    if (user_result[(user_result.length - 2)].equalsIgnoreCase("null") || user_result[(user_result.length - 2)].trim().length() == 0)
//					tempList.add(new Integer(-1));
//				    else
//					tempList.add(new Integer(user_result[(user_result.length - 2)]));
//	
//				    if (user_result[(user_result.length - 1)].equalsIgnoreCase("null") || user_result[(user_result.length - 1)].trim().length() == 0)
//					{
//				    	tempList.add(new Integer(-1));
//				    	if (!temp0[2].equals("4"))
//				    		is_Null_wholeEval=true;
//					}
//				    else
//					tempList.add(new Integer(user_result[(user_result.length - 1)]));
//				    isChange = true;
//				}
				/*************************上面写法复杂且有问题改为下  zzk 2014/1/28******************************/
				if ("true".equals(NodeKnowDegree)&& "true".equals(WholeEval)){
				    if ("null".equalsIgnoreCase(user_result[(user_result.length - 2)]) || user_result[(user_result.length - 2)].trim().length() == 0) {
                        tempList.add(new Integer(-1));
                    } else {
                        tempList.add(new Integer(user_result[(user_result.length - 2)]));
                    }
				}
				if ("true".equals(NodeKnowDegree)&&!"true".equals(WholeEval)){
				    if ("null".equalsIgnoreCase(user_result[(user_result.length - 1)]) || user_result[(user_result.length - 1)].trim().length() == 0) {
                        tempList.add(new Integer(-1));
                    } else {
                        tempList.add(new Integer(user_result[(user_result.length - 1)]));
                    }
				}
				if ( "true".equals(WholeEval)&&"0".equals(WholeEvalMode)){
				    if ("null".equalsIgnoreCase(user_result[(user_result.length - 1)]) || user_result[(user_result.length - 1)].trim().length() == 0)
					{
				    	tempList.add(new Integer(-1));
				    	if (!"4".equals(temp0[2])){
				    		is_Null_wholeEval=true;
				    		wholeEvalMustFillBuffer.append(temp0[1]+",");
				    	}
				    		
					}
				    else {
                        tempList.add(new Integer(user_result[(user_result.length - 1)]));
                    }
				}
				if (!"3".equals(flag))
				{
				    if ("4".equals(temp0[2]) && "2".equals(flag)) // 如果为不提交状态，并且点击了提交按钮
				    {
				    	tempList.add(new Integer("7"));
				    } else if ("4".equals(temp0[2]) && ("1".equals(flag)|| "8".equals(flag))) // 如果为不提交状态，并且点击了保存按钮
				    {
				    	tempList.add(new Integer("4"));
				    } else {
                        tempList.add(new Integer(flag));
                    }
				    ; // 修改状态
				} else
				{
				    if ("4".equals(temp0[2])) {
                        tempList.add(new Integer("4"));
                    } else {
                        tempList.add(new Integer("1"));
                    }
				}
				tempList.add(user_id);
				tempList.add(mainbody_id);
				tempList.add(new Integer(plan_id));
				if (!"4".equals(temp0[2])) {
                    obj_str.append(",'"+user_id+"'");
                }
				mainInfoList.add(tempList);
	
			  }
		}
		
		if("true".equalsIgnoreCase(isEntiretySub)&& "true".equalsIgnoreCase(this.WholeEval)&&"0".equals(WholeEvalMode)&&("2".equals(flag)|| "8".equals(flag)))
		{
			if(is_Null_wholeEval && (totalAppFormula==null || totalAppFormula.trim().length()<=0))
			{
				error_info=ResourceFactory.getProperty("performance.batchgrade.info17");
				throw (new GeneralException(ResourceFactory.getProperty("performance.batchgrade.info17")));
			}
		}
		
		if("true".equalsIgnoreCase(MustFillWholeEval)&&("2".equals(flag)|| "8".equals(flag)))
		{
			String Object_type = getObjectType(plan_id);
			
			if(is_Null_wholeEval && (totalAppFormula==null || totalAppFormula.trim().length()<=0))
			{
//				error_info=ResourceFactory.getProperty("lable.statistic.wholeeven")+ResourceFactory.getProperty("performance.batchgrade.info18");
//				throw (new GeneralException(ResourceFactory.getProperty("lable.statistic.wholeeven")+ResourceFactory.getProperty("performance.batchgrade.info18")));
				error_info=ResourceFactory.getProperty("jx.khplan.param1.overallevaluation1") + Object_type + ResourceFactory.getProperty("jx.khplan.param1.overallevaluation2") + "\r\n"+wholeEvalMustFillBuffer.toString().substring(0, wholeEvalMustFillBuffer.toString().length()-1);
				throw (new GeneralException(error_info));
			}
			boolean descWholEval=false;
			wholeEvalMustFillBuffer.setLength(0);
			if("true".equalsIgnoreCase((String)htxml.get("DescriptiveWholeEval"))&&obj_str.length()>0)
			{
				 
				String desc="";
				RowSet rowSet=dao.search("select pm.description,po.a0101 from per_mainbody pm,per_object po where pm.plan_id="+planid+" and pm.plan_id=po.plan_id and pm.mainbody_id='"+mainbody_id+"'" +
						" and  pm.object_id=po.object_id and pm.object_id in ( "+obj_str.substring(1)+" ) order by pm.id");
				while(rowSet.next())
				{
					desc=Sql_switcher.readMemo(rowSet,"description");
					if(desc==null||desc.trim().length()==0)
					{	
//						error_info=ResourceFactory.getProperty("lable.statistic.wholeeven")+ResourceFactory.getProperty("performance.batchgrade.info19");
//						throw (new GeneralException(ResourceFactory.getProperty("lable.statistic.wholeeven")+ResourceFactory.getProperty("performance.batchgrade.info19")));
						descWholEval=true;
						wholeEvalMustFillBuffer.append(rowSet.getString("a0101")+",");
						
					}
				}
				if(descWholEval){
					error_info=ResourceFactory.getProperty("jx.khplan.param1.desc.overallevaluation1") + Object_type + ResourceFactory.getProperty("jx.khplan.param1.desc.overallevaluation2") + "\r\n"+wholeEvalMustFillBuffer.toString().substring(0, wholeEvalMustFillBuffer.toString().length()-1);
					throw (new GeneralException(error_info));
				}
				
			}
		}
		
		//多人打分时所有考核对象指标分数全相同时，不予保存。
		if("False".equalsIgnoreCase(CanSaveAllObjsScoreSame)&&!"1".equals(flag))
		{
			if(userid.length==1) //&&pointList.size()==1)
			{
				
			}	
			else
			{
				if (num>0&&isSame_all && !is_Null_all&&isPfPoint)
				{
					error_info=ResourceFactory.getProperty("performance.batchgrade.info15")+"！";
				    throw (new GeneralException(ResourceFactory.getProperty("performance.batchgrade.info18")+"！"));
				}
			}
			
		}
		
		
		
		
		// dao.addValueObject(recordLists);
		// String sql0="insert into per_table_"+plan_id+"
                // (id,object_id,mainbody_id,point_id,score,amount,degree_id)values(?,?,?,?,?,?,?)";

		String sql0 = "insert into per_table_" + plan_id + " (id,object_id,mainbody_id,point_id,score,amount,degree_id,reasons)values(?,?,?,?,?,?,?,?)";

		dao.batchUpdate(sql, deleteList);
		dao.batchInsert(sql0, recordLists);
		dao.batchUpdate(aup_sql.toString(), mainInfoList);
		
		if(!"0".equals(this.BlankScoreOption)&&userid.length>1&& "2".equals(flag))
		{
			  String _sql="update per_mainbody set status=2 where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"'";
			  if ("False".equalsIgnoreCase(this.mitiScoreMergeSelfEval))
			  {
			    	if ("2".equals(this.object_type)) // 考核人员
                    {
                        _sql+=" and  object_id<>'" + mainbody_id + "'";
                    }
			  }
			  dao.update(_sql);
		}
		
	    }
	    if ("1".equals(isNull) && (("0".equals(status) || "2".equals(scoreflag)))) {
            isSuccess = "2";
        }
	} catch (Exception e)
	{
	    isSuccess = "0";
	    e.printStackTrace();
	    if(this.error_info.length()==0) {
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	return isSuccess;
    }

    /**
         * 将评分结果插入数据库(zp_test_template 招聘考评打分)
         * 
         * @param userid
         *                考核对象
         * @param template_id
         *                考核计划相应模版id
         * @param usersValue
         *                各对象的评分结果
         * @param status
         *                权重分值标识
         * @return
         */
    public String insertGradeResult(String userid, String template_id, String usersValue, String mainbody_id, String status, String z0101, String scoreFlag) throws GeneralException
    {

	String isSuccess = "1"; // 保存结果 1：保存成功 0：保存失败 2：指标范围为空不予保存
	ContentDAO dao = new ContentDAO(this.conn);
	/* 查找参数表 */
	try
	{

	    String ScaleToDegreeRule = "3"; // 分值转标度规则（1-就高 2-就低
                                                // 3-就近就高（默认值））
	    SingleGradeBo singleGradeBo = new SingleGradeBo(this.conn);
//	    String privPoint = singleGradeBo.anaysePrivPoint(mainbody_id, "4");//有权限的指标
	    /*************zzk 加上考官指标权限控制************/
	    String id=mainbody_id.substring(3);//考官人员编号
	    String name="";//考官登录名
	    String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
	    String sql="select * from "+mainbody_id.substring(0,3)+"A01 where A0100='"+id+"'";
	    ResultSet res=null;
	    
	    res=dao.search(sql);
	    while(res.next()){
	    	name=res.getString(loguser);
	    }
	    UserView userView1=new UserView(name,this.conn);
	    String privPoint="";
		if(userView1.canLogin(false)) {
            privPoint=this.getFieldPriv(dao,userView1, template_id, mainbody_id);
        }
		singleGradeBo.setPrivPointStr(privPoint);
		singleGradeBo.setFromType("2");
	    ArrayList pointList0 = singleGradeBo.getPerPointList(template_id);
	    ArrayList pointInfoList = (ArrayList) pointList0.get(0); // 指标详细集
	    ArrayList pointList = (ArrayList) pointList0.get(1); // 指标集
	    String isNull = (String) pointList0.get(2); // 指标范围是否为空 0：不为空 1：为空

	    if ("0".equals(isNull))
	    {

		String[] user_value = usersValue.split("/");
		ArrayList recordList = new ArrayList();
		for (int t = 0; t < pointList.size(); t++)
		{
		    String[] temp = (String[]) pointList.get(t);
		    RecordVo vo = getPerTableVo(userid, mainbody_id.substring(3), temp, user_value, t, pointInfoList, ScaleToDegreeRule, status, z0101, scoreFlag);
		    recordList.add(vo);
		}
	    /* 删除表中用户已有的纪录 *///不采用高级测评的记录都要删除，才用高级测评的不用删除
		if(this.hireState!=null&&this.hireState.trim().length()>0){
			dao.delete("delete from zp_test_template where a0100='" + userid + "' and a0100_1='" + mainbody_id.substring(3) + "' and  interview="+this.hireState, new ArrayList());
		}else{
			dao.delete("delete from zp_test_template where a0100='" + userid + "' and a0100_1='" + mainbody_id.substring(3) + "' and  interview=0", new ArrayList());
		}
		dao.addValueObject(recordList);

	    }
	    if ("1".equals(isNull)) {
            isSuccess = "2";
        }
	} catch (Exception e)
	{
	    isSuccess = "0";
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return isSuccess;
    }

	/**
	 * 获得考官对应考核模板的指标权限
	 * @param dao
	 * @param userView
	 * @param template_id
	 * @param mainBodyID
	 * @return
	 */
	
	private String getFieldPriv(ContentDAO dao,UserView userView,String template_id,String mainBodyID){
		String privPoints="";
		String sql="select point_id from per_template_point where item_id in(select item_id from  per_template_item where template_id='"+template_id+"')";
		String point_id="";
		try {
			ResultSet res=null;
			res=dao.search(sql);
			while(res.next()){
				point_id=res.getString("point_id");
				if(userView.isSuper_admin()||userView.isHaveResource(IResourceConstant.KH_FIELD,point_id)){
					privPoints+=point_id+",";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(privPoints.length()>0){
			privPoints=privPoints.substring(0, privPoints.length()-1);
		}
		return privPoints;
	}
    /**
         * 按条件生成评分结果表的纪录
         */
    public RecordVo getPerTableVo(String user_id, String mainbody_id, String[] temp, String[] user_result, int t, ArrayList pointInfoList, String ScaleToDegreeRule, String status, String z0101,
	    String scoreFlag) throws GeneralException
    {

	RecordVo vo = new RecordVo("zp_test_template");
	try
	{
	    vo.setString("a0100_1", mainbody_id);
	    vo.setString("a0100", user_id);
	    vo.setString("point_id", temp[0]);
	    vo.setString("z0101", z0101);
	    if(this.hireState!=null&&this.hireState.trim().length()>0){//如果是高级测评，设置高级测评的方式
	    	vo.setInt("interview", Integer.parseInt(this.hireState));
	    }else{//没有采用高级测评，设置为0
	    	vo.setInt("interview", 0);
	    }
	    if(user_result.length<=t) {
            return vo;
        }
	    if ("0".equals(temp[2])) // 定性
	    {
		if (!"null".equals(user_result[t]))
		{
		    if ("2".equals(scoreflag))
		    {
			if (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1) {
                vo.setString("degree_id", user_result[t]);
            }
		    } else
		    {
			vo.setString("degree_id", user_result[t]);
		    }
		} else {
            vo.setString("degree_id", "");
        }
		String[] temp2 = null;
		if (!"null".equals(user_result[t]))
		{
		    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
		    {
				String[] temp1 = (String[]) tt.next();
				if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1))
				{
				    if (temp[0].equals(temp1[1]) && user_result[t].equals(temp1[5]))
				    {
						temp2 = temp1;
						break;
				    }
				} else
				{
				    if (temp[0].equals(temp1[1]))
				    {
						if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
						{
						    if (temp[0].equals(temp1[1]) && Float.parseFloat(user_result[t]) >=Float.parseFloat(PubFunc.multiple(temp1[7],temp1[8],2))   /* Float.parseFloat(temp1[7]) * Float.parseFloat(temp1[8]) */
							    && Float.parseFloat(user_result[t]) <=Float.parseFloat(PubFunc.multiple(temp1[6],temp1[8],2))   /*Float.parseFloat(temp1[6]) * Float.parseFloat(temp1[8])*/ )
						    {
							temp2 = temp1;
							break;
						    }
						} else
						// 就低
						{
						    if (temp[0].equals(temp1[1]) && Float.parseFloat(user_result[t]) >=Float.parseFloat(PubFunc.multiple(temp1[7],temp1[8],2)) /*Float.parseFloat(temp1[7]) * Float.parseFloat(temp1[8])*/
							    && Float.parseFloat(user_result[t]) <=Float.parseFloat(PubFunc.multiple(temp1[6],temp1[8],2))  /*Float.parseFloat(temp1[6]) * Float.parseFloat(temp1[8])*/ )
						    {
							temp2 = temp1;
						    }
						}
				    }
				}
		    }

		    if (temp2 == null) {
                throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
                + ResourceFactory.getProperty("label.performance.doNotGetGrade") + "(注意指标标度上下限是否闭合)!"));
            }

		    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1))
		    {
		    	vo.setDouble("score", Double.parseDouble(PubFunc.round(String.valueOf(Float.parseFloat(temp2[9]) * Float.parseFloat(temp2[8])), 1)));
		    } else
		    {
		    	vo.setString("degree_id", temp2[5]);
		    	vo.setDouble("score", Double.parseDouble(PubFunc.round(user_result[t], 1)));
		    }
		}

		/*
                 * String[] temp2 = null; if (!user_result[t].equals("null")) {
                 * vo.setString("degree_id", user_result[t]); for (Iterator tt =
                 * pointInfoList.iterator(); tt.hasNext();) { String[] temp1 =
                 * (String[]) tt.next(); if (temp[0].equals(temp1[1]) &&
                 * user_result[t].equals(temp1[5])) { temp2 = temp1; break; } }
                 * vo.setDouble("score", Double.parseDouble(String
                 * .valueOf(Float.parseFloat(temp2[9])
                 * Float.parseFloat(temp2[8])))); }
                 */
	    } else
	    // 定量
	    {
		if (!"null".equals(user_result[t]))
		{
		    String[] temp2 = null;
		    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
		    {
			String[] temp1 = (String[]) tt.next();
			if (temp[0].equals(temp1[1]))
			{
			    float topValue = Float.parseFloat(temp1[6]);
			    float bottomValue = Float.parseFloat(temp1[7]);
			    if (Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]))
			    {
				temp2 = temp1;
				break;
			    }
			}
		    }
		    if ((user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) != -1))
		    {
			vo.setString("degree_id", temp2[5]);
			String score = String.valueOf(Float.parseFloat(temp2[9]) * Float.parseFloat(temp2[8]));
			vo.setDouble("score", Double.parseDouble(score));
			vo.setDouble("amount", Double.parseDouble(user_result[t]));
		    }
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
		    + ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
	}

	return vo;
    }

    /**
         * 
         * if(temp[2].equals("0")) //定性 { if(!user_result[t].equals("null")) {
         * if(scoreflag.equals("2")) {
         * if(user_result[t].length()>=1&&"0123456789.".indexOf(user_result[t].charAt(0))==-1)
         * vo.setString("degree_id",user_result[t]); } else {
         * vo.setString("degree_id",user_result[t]); } } else
         * vo.setString("degree_id",""); String[] temp2=null;
         * if(!user_result[t].equals("null")) { for(Iterator
         * tt=pointInfoList.iterator();tt.hasNext();) { String[]
         * temp1=(String[])tt.next();
         * 
         * if(scoreflag.equals("1")||(user_result[t].length()>=1&&"0123456789.".indexOf(user_result[t].charAt(0))==-1)) {
         * if(temp[0].equals(temp1[1])&&user_result[t].equals(temp1[5])) {
         * temp2=temp1; break; } } else { if(temp[0].equals(temp1[1])) {
         * if(ScaleToDegreeRule.equals("1")||ScaleToDegreeRule.equals("3")) //就高 {
         * if(temp[0].equals(temp1[1])&&Float.parseFloat(user_result[t])>=Float.parseFloat(temp1[7])*Float.parseFloat(temp1[8])&&Float.parseFloat(user_result[t])<=Float.parseFloat(temp1[6])*Float.parseFloat(temp1[8])) {
         * temp2=temp1; break; } } else //就低 {
         * if(temp[0].equals(temp1[1])&&Float.parseFloat(user_result[t])>=Float.parseFloat(temp1[7])*Float.parseFloat(temp1[8])&&Float.parseFloat(user_result[t])<=Float.parseFloat(temp1[6])*Float.parseFloat(temp1[8])) {
         * temp2=temp1; } } } } }
         * 
         * if(temp2==null) throw GeneralExceptionHandler.Handle(new
         * GeneralException(ResourceFactory.getProperty("kq.wizard.target")+"："+temp[1]+"
         * "+ResourceFactory.getProperty("label.performance.doNotGetGrade")+"!"));
         * 
         * 
         * if(scoreflag.equals("1")||(user_result[t].length()>=1&&"0123456789.".indexOf(user_result[t].charAt(0))==-1)) {
         * vo.setDouble("score",Double.parseDouble(PubFunc.round(String.valueOf(Float.parseFloat(temp2[9])*Float.parseFloat(temp2[8])),1))); }
         * else { vo.setString("degree_id",temp2[5]);
         * vo.setDouble("score",Double.parseDouble(PubFunc.round(user_result[t],1))); } } }
         * else //定量 { if(!user_result[t].equals("null")) { String[] temp2=null;
         * for(Iterator tt=pointInfoList.iterator();tt.hasNext();) { String[]
         * temp1=(String[])tt.next(); if(temp[0].equals(temp1[1])) { float
         * topValue=Float.parseFloat(temp1[6]); float
         * bottomValue=Float.parseFloat(temp1[7]);
         * if(scoreflag.equals("1")||(user_result[t].length()>=1&&"0123456789.".indexOf(user_result[t].charAt(0))!=-1)) {
         * 
         * if(ScaleToDegreeRule.equals("1")||ScaleToDegreeRule.equals("3")) //就高 {
         * if(Float.parseFloat(user_result[t])>=Float.parseFloat(temp1[7])&&Float.parseFloat(user_result[t])<=Float.parseFloat(temp1[6])) {
         * temp2=temp1; break; } } else //就低 {
         * if(Float.parseFloat(user_result[t])>=Float.parseFloat(temp1[7])&&Float.parseFloat(user_result[t])<=Float.parseFloat(temp1[6])) {
         * temp2=temp1; } } } else { if(temp1[5].equals(user_result[t])) {
         * String
         * score=PubFunc.round(String.valueOf(Float.parseFloat(temp1[9])*Float.parseFloat(temp1[8])),1);
         * vo.setDouble("score",Double.parseDouble(score));
         * vo.setString("degree_id",temp1[5]);
         * 
         * if(ScaleToDegreeRule.equals("1")||ScaleToDegreeRule.equals("3")) //就高 {
         * vo.setDouble("amount",Double.parseDouble(temp1[6])); } else {
         * vo.setDouble("amount",Double.parseDouble(temp1[7])); } break; } } } }
         * if(temp2==null) throw GeneralExceptionHandler.Handle(new
         * GeneralException(ResourceFactory.getProperty("kq.wizard.target")+"："+temp[1]+"
         * "+ResourceFactory.getProperty("label.performance.doNotGetGrade")+"!"));
         * 
         * if(scoreflag.equals("1")||(user_result[t].length()>=1&&"0123456789.".indexOf(user_result[t].charAt(0))!=-1)) {
         * vo.setString("degree_id",temp2[5]); String
         * score=PubFunc.round(String.valueOf(Float.parseFloat(temp2[9])*Float.parseFloat(temp2[8])),1);
         * vo.setDouble("score",Double.parseDouble(score));
         * vo.setDouble("amount",Double.parseDouble(user_result[t])); } } }
         * 
         * 
         */

    // insert per_table_1
        // (id,object_id,mainbody_id,point_id,score,amount,degree_id)values(?,?,?,?,?,?,?)
    /**
         * 按条件生成评分结果表的纪录
         */
    public ArrayList getPerTableVo2(String _id,String plan_id, String user_id, String mainbody_id, String[] temp, String[] user_result, int t, ArrayList pointInfoList, String ScaleToDegreeRule, String status,
	    String scoreflag) throws GeneralException
    {
    
    Hashtable	htxml = loadxml.getDegreeWhole();
    String EvalOutLimitStdScore=(String)htxml.get("EvalOutLimitStdScore");  //评分时得分不受标准分限制True, False, 默认为 False;都加
    String addSubtractType=(String)htxml.get("addSubtractType");   //加扣分处理方式  1:加扣分  2:加分  3：扣分
 
    String  _KeepDecimal = (String) htxml.get("KeepDecimal"); // 小数位
    if(_KeepDecimal==null||_KeepDecimal.trim().length()==0) {
        _KeepDecimal="1";
    }
    
    
    // 干警考核系统 JinChunhai 2012.10.09 
	if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
	{
		for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
		{
			String[] temp1 = (String[]) tt.next();
			if (temp[0].equalsIgnoreCase(temp1[1]))
			{
			    if (user_result[t].length() >= 1 && !user_result[t].matches("-?[\\d]*[.]?[\\d]+"))
			    {
					if (temp1[4].indexOf(user_result[t])!=-1)
					{
						Arrays.fill(user_result,t,t+1,temp1[5]);
					    break;
					}
			    }
			}
		}				
	}   
    
	ArrayList templist = new ArrayList();
	Double score = new Double(0);
	Double amount = new Double(0);
	String degree_id = null;
	boolean isSpecialException=false;
	try
	{

	    if ("null".equals(user_result[t])) // &&this.BlankScoreOption.equals("0"))
        {
            return null;
        }
//	    IDGenerator idg = new IDGenerator(2, this.conn);
	    String id =_id; // idg.getId("per_table_xxx.id");
	    templist.add(new Integer(id));
	    templist.add(user_id);
	    templist.add(mainbody_id);
	    templist.add(temp[0]);
	    
	    
	    double _maxScore=0;
		String[] maxtemp=null;
		double _minScore=-10000;
		String[] mintemp=null;
		
	//	String _var="0123456789.";
	//	if(EvalOutLimitStdScore.equalsIgnoreCase("true"))
	//		_var="-0123456789.";
	    if ("0".equals(temp[2])) // 定性
		{
			if (!"null".equals(user_result[t]))
			{
			    if ("2".equals(scoreflag))  // =2混合，=1标度(默认值=混合)
			    {
					if (user_result[t].length() >= 1 &&  !user_result[t].matches("-?[\\d]*[.]?[\\d]+")) {
                        degree_id = user_result[t];
                    }
			    } else
			    {
			    	degree_id = user_result[t];
			    }
			} else {
                degree_id = null;
            }
			String[] temp2 = null;
			if (!"null".equals(user_result[t]))
			{
				if("4".equals(scoreflag)&&user_result[t].matches("-?[\\d]*[.]?[\\d]+"))//支持扣分
				{
					 
					
					double d=Double.parseDouble(user_result[t]);
					if(addSubtractType!=null&& "2".equalsIgnoreCase(addSubtractType))
					{
						if(d<0)
						{  
							isSpecialException=true;
							error_info=ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "+ResourceFactory.getProperty("performance.batchgrade.info20");
							throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
									+ResourceFactory.getProperty("performance.batchgrade.info20")));
						}
					}
					if(addSubtractType!=null&& "3".equalsIgnoreCase(addSubtractType))
					{
						if(d>0)
						{
							isSpecialException=true;
							error_info=ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "+ResourceFactory.getProperty("performance.batchgrade.info21");
							throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
									+ResourceFactory.getProperty("performance.batchgrade.info21"))); 
						}
					}
					
					 if("true".equalsIgnoreCase(EvalOutLimitStdScore))
					 {
						 score = new Double(PubFunc.round(user_result[t],Integer.parseInt(_KeepDecimal)));
					 }
					 else
					 {
						 
						 	if(Float.parseFloat(temp[8])!=0&&(Float.parseFloat(user_result[t])>Float.parseFloat(temp[8])||Float.parseFloat(user_result[t])<-Float.parseFloat(temp[8])))
							{
						 		error_info=ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!";
						 		throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
										+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
							}
						 	else {
                                score = new Double(PubFunc.round(user_result[t],Integer.parseInt(_KeepDecimal)));
                            }
					 }
					 degree_id="";
				}
				else
				{
					int n=0;
				    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
					{
						String[] temp1 = (String[]) tt.next();
						if (temp[0].equalsIgnoreCase(temp1[1]))
						{
			
						    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && !user_result[t].matches("-?[\\d]*[.]?[\\d]+") ))//_var.indexOf(user_result[t].charAt(0)) == -1))
						    {
								if (user_result[t].equalsIgnoreCase(temp1[5]))
								{
								    temp2 = temp1;
								    break;
								}
						    } 
						    else
						    {
						    	String top=temp1[6];
						    	String bot=temp1[7];
								String topValue = PubFunc.multiple(top, temp1[8], 6);
								String bottomValue = PubFunc.multiple(bot, temp1[8], 6);
								String selfValue = PubFunc.round(user_result[t], 6);
								
								if("true".equalsIgnoreCase(EvalOutLimitStdScore))
								{
									if(Double.parseDouble(topValue)>_maxScore||n==0)
									{
										_maxScore=Double.parseDouble(topValue);
										maxtemp= temp1;
									}
									if((Double.parseDouble(bottomValue)<_minScore)||n==0)
									{
										_minScore=Double.parseDouble(bottomValue);
										mintemp= temp1;
									}
								}
								
								
								if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
								{
								    if (temp[0].equalsIgnoreCase(temp1[1]) && Float.parseFloat(selfValue) >= Float.parseFloat(bottomValue) && Float.parseFloat(selfValue) <= Float.parseFloat(topValue))
								    {
										temp2 = temp1;
										break;
								    }
								} else
								// 就低
								{
								    if (temp[0].equalsIgnoreCase(temp1[1]) && Float.parseFloat(selfValue) >= Float.parseFloat(bottomValue) && Float.parseFloat(selfValue) <= Float.parseFloat(topValue))
								    {
								    	temp2 = temp1;
								    }
								}
						    }
			
						}
						n++;
					}
				    if (temp2 == null&& !("2".equals(scoreflag)&&user_result[t].matches("-?[\\d]*[.]?[\\d]+")&& "true".equalsIgnoreCase(EvalOutLimitStdScore)))
				    {
				    	error_info=ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!";
				    	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
							+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
				    }
				    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 &&  !user_result[t].matches("-?[\\d]*[.]?[\\d]+") ))//_var.indexOf(user_result[t].charAt(0)) == -1))
				    {
				    	String bb=temp2[9];
				    	
				    	score = new Double(PubFunc.round(String.valueOf(Float.parseFloat(bb) * Float.parseFloat(temp2[8])),Integer.parseInt(_KeepDecimal)));
				    	
				    	//20150623 dengcan 自动减1分不符合业务要求 ，统一评分给了 A等级，按常理应该是满分，结果 硬减了1分，客户不能理解；
				    	/*
				    	String bb_low = temp2[7]; // 币制定标度低一级的标度
				    	Double score_low = new Double(PubFunc.round(String.valueOf(Float.parseFloat(bb_low) * Float.parseFloat(temp2[8])),Integer.parseInt(_KeepDecimal)));
				    	if (Math.abs(score.doubleValue() - score_low.doubleValue()) >= 1) {
				    		score = Double.valueOf(score.doubleValue() - 1);
				    	}*/
				    } else
				    {
				    	if(temp2!=null) {
                            degree_id = temp2[5];
                        } else if("2".equals(scoreflag)&&user_result[t].matches("-?[\\d]*[.]?[\\d]+")&& "true".equalsIgnoreCase(EvalOutLimitStdScore))
				    	{
				    		float _value=Float.parseFloat(user_result[t]);
							if(_value>_maxScore)
							{
								temp2=maxtemp;
								degree_id=maxtemp[5];
							}
							if(_value<_minScore)
							{
								temp2=mintemp;
								degree_id=mintemp[5];
							}
				    		
				    	}
						score = new Double(PubFunc.round(user_result[t],Integer.parseInt(_KeepDecimal)));
				    }
				}
			} else
			{/*
	                         * if(this.BlankScoreOption.equals("1")) { LazyDynaBean
	                         * abean=(LazyDynaBean)this.pointMaxValueMap.get(temp[0]);
	                         * score=new Double((String)abean.get("score"));
	                         * degree_id=(String)abean.get("gradecode"); }
	                         * if(this.BlankScoreOption.equals("2")) { boolean
	                         * isValue=false; for (Iterator tt =
	                         * pointInfoList.iterator(); tt.hasNext();) { String[]
	                         * temp1 = (String[]) tt.next();
	                         * 
	                         * if(temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree)) {
	                         * score = new Double(PubFunc.round(String.valueOf(Float
	                         * .parseFloat(temp1[9]) Float.parseFloat(temp1[8])),
	                         * 1)); degree_id = temp1[5]; isValue=true; } }
	                         * if(!isValue) return null;
	                         *  }
	                         */
			}
		} else
	    // 定量
		{
			if (!"null".equals(user_result[t]))
			{
			    String[] temp2 = null;
			    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
			    {
				String[] temp1 = (String[]) tt.next();
				if (temp[0].equalsIgnoreCase(temp1[1]))
				{
				    float topValue = Float.parseFloat(temp1[6]);
				    float bottomValue = Float.parseFloat(temp1[7]);
				    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) != -1))
				    {
	
					if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
					{
					    if (Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]))
					    {
						temp2 = temp1;
						break;
					    }
					} else
					// 就低
					{
					    if (Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]))
					    {
						temp2 = temp1;
					    }
					}
				    } else
				    {
					if (temp1[5].equalsIgnoreCase(user_result[t]))
					{
					    score = new Double(PubFunc.round(String.valueOf(Float.parseFloat(temp1[9]) * Float.parseFloat(temp1[8])),Integer.parseInt(_KeepDecimal)));
					    degree_id = temp1[5];
	
					    if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
					    {
						amount = new Double(temp1[6]);
					    } else
					    {
						amount = new Double(temp1[7]);
					    }
					    break;
					}
				    }
				}
			    }
			    if (temp2 == null)
				{
			    	error_info=ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!";
			    	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
					+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
				}
			    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) != -1))
			    {
	
				degree_id = temp2[5];
				score = new Double(PubFunc.round(String.valueOf(Float.parseFloat(temp2[9]) * Float.parseFloat(temp2[8])),Integer.parseInt(_KeepDecimal)));
				amount = new Double(user_result[t]);
			    }
			} else
			{/*
	                         * if(this.BlankScoreOption.equals("1")) { LazyDynaBean
	                         * abean=(LazyDynaBean)this.pointMaxValueMap.get(temp[0]);
	                         * score=new Double((String)abean.get("score"));
	                         * degree_id=(String)abean.get("gradecode"); amount=new
	                         * Double((String)abean.get("top_value")); }
	                         * 
	                         * if(this.BlankScoreOption.equals("2")) { boolean
	                         * isValue=false; for (Iterator tt =
	                         * pointInfoList.iterator(); tt.hasNext();) { String[]
	                         * temp1 = (String[]) tt.next();
	                         * if(temp1[5].equalsIgnoreCase(this.BlankScoreUseDegree)) {
	                         * score = new Double(PubFunc.round(String.valueOf(Float
	                         * .parseFloat(temp1[9]) Float.parseFloat(temp1[8])),
	                         * 1)); degree_id = temp1[5]; amount=new
	                         * Double(temp1[6]); isValue=true; } } if(!isValue)
	                         * return null;
	                         *  }
	                         */
			}
		}

	} catch (Exception e)
	{
	    // e.printStackTrace();
		if(isSpecialException) {
            throw GeneralExceptionHandler.Handle(e);
        } else
		{	
			error_info=ResourceFactory.getProperty("sys.res.khfield") + "：" + temp[1] + "  "+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!";
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("sys.res.khfield") + "：" + temp[1] + "  "
					+ ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
		}
	}
	a_dgree_id = degree_id;
	a_score=score.doubleValue();
	/*
         * if(score.floatValue()==0) //2008-11-06 应bug0012964,要求改
         * templist.add(null); else
         */
	templist.add(score);
	templist.add(amount);
	templist.add(degree_id);

	if (pointReasons != null)
	{
	    String key = user_id + "/" + temp[0].toLowerCase();
	    if (pointReasons.get(key) != null) {
            templist.add((String) pointReasons.get(key));
        } else {
            templist.add("");
        }

	} else {
        templist.add("");
    }

	return templist;
	// return vo;
    }

    /**
         * 按条件生成评分结果表的纪录
         */
    public RecordVo getPerTableVo(String plan_id, String user_id, String mainbody_id, String[] temp, String[] user_result, int t, ArrayList pointInfoList, String ScaleToDegreeRule, String status,
	    String scoreflag) throws GeneralException
    {

	RecordVo vo = new RecordVo("PER_TABLE_" + plan_id);
	try
	{
	    IDGenerator idg = new IDGenerator(2, this.conn);
	    String id = idg.getId("per_table_xxx.id");
	    vo.setInt("id", Integer.parseInt(id));
	    vo.setString("object_id", user_id);
	    vo.setString("mainbody_id", mainbody_id);
	    vo.setString("point_id", temp[0]);

	    // if(status.equals("0")||scoreflag.equals("2"))
	    // {
	    if ("0".equals(temp[2])) // 定性
	    {
		if (!"null".equals(user_result[t]))
		{
		    if ("2".equals(scoreflag))
		    {
			if (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1) {
                vo.setString("degree_id", user_result[t]);
            }
		    } else
		    {
			vo.setString("degree_id", user_result[t]);
		    }
		} else {
            vo.setString("degree_id", "");
        }
		String[] temp2 = null;
		if (!"null".equals(user_result[t]))
		{
		    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
		    {
			String[] temp1 = (String[]) tt.next();

			if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1))
			{
			    if (temp[0].equalsIgnoreCase(temp1[1]) && user_result[t].equalsIgnoreCase(temp1[5]))
			    {
				temp2 = temp1;
				break;
			    }
			} else
			{
			    if (temp[0].equalsIgnoreCase(temp1[1]))
			    {
				if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
				{
				    if (temp[0].equalsIgnoreCase(temp1[1]) && Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) * Float.parseFloat(temp1[8])
					    && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]) * Float.parseFloat(temp1[8]))
				    {
					temp2 = temp1;
					break;
				    }
				} else
				// 就低
				{
				    if (temp[0].equalsIgnoreCase(temp1[1]) && Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) * Float.parseFloat(temp1[8])
					    && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]) * Float.parseFloat(temp1[8]))
				    {
					temp2 = temp1;
				    }
				}
			    }
			}
		    }

		    if (temp2 == null) {
                throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
                    + ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
            }

		    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) == -1))
		    {
			vo.setDouble("score", Double.parseDouble(PubFunc.round(String.valueOf(Float.parseFloat(temp2[9]) * Float.parseFloat(temp2[8])), 1)));
		    } else
		    {
			vo.setString("degree_id", temp2[5]);
			vo.setDouble("score", Double.parseDouble(PubFunc.round(user_result[t], 1)));
		    }
		}
	    } else
	    // 定量
	    {
		if (!"null".equals(user_result[t]))
		{
		    String[] temp2 = null;
		    for (Iterator tt = pointInfoList.iterator(); tt.hasNext();)
		    {
			String[] temp1 = (String[]) tt.next();
			if (temp[0].equalsIgnoreCase(temp1[1]))
			{
			    float topValue = Float.parseFloat(temp1[6]);
			    float bottomValue = Float.parseFloat(temp1[7]);
			    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) != -1))
			    {

				if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
				{
				    if (Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]))
				    {
				    	temp2 = temp1;
				    	break;
				    }
				} else
				// 就低
				{
				    if (Float.parseFloat(user_result[t]) >= Float.parseFloat(temp1[7]) && Float.parseFloat(user_result[t]) <= Float.parseFloat(temp1[6]))
				    {
				    	temp2 = temp1;
				    }
				}
			    } else
			    {
				if (temp1[5].equalsIgnoreCase(user_result[t]))
				{
				    String score = PubFunc.round(String.valueOf(Float.parseFloat(temp1[9]) * Float.parseFloat(temp1[8])), 1);
				    vo.setDouble("score", Double.parseDouble(score));
				    vo.setString("degree_id", temp1[5]);

				    if ("1".equals(ScaleToDegreeRule) || "3".equals(ScaleToDegreeRule)) // 就高
				    {
					vo.setDouble("amount", Double.parseDouble(temp1[6]));
				    } else
				    {
					vo.setDouble("amount", Double.parseDouble(temp1[7]));
				    }
				    break;
				}
			    }
			}
		    }
		    if (temp2 == null) {
                throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
                    + ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
            }

		    if ("1".equals(scoreflag) || (user_result[t].length() >= 1 && "0123456789.".indexOf(user_result[t].charAt(0)) != -1))
		    {
			vo.setString("degree_id", temp2[5]);
			String score = PubFunc.round(String.valueOf(Float.parseFloat(temp2[9]) * Float.parseFloat(temp2[8])), 1);
			vo.setDouble("score", Double.parseDouble(score));
			vo.setDouble("amount", Double.parseDouble(user_result[t]));
		    }
		}
	    }
	    // }
	    /*
                 * else { if(!user_result[t].equals("null"))
                 * vo.setDouble("score",Double.parseDouble(user_result[t])); }
                 */
	} catch (Exception e)
	{
	    // e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("kq.wizard.target") + "：" + temp[1] + "  "
		    + ResourceFactory.getProperty("label.performance.doNotGetGrade") + "!"));
	}
	a_dgree_id = vo.getString("degree_id");

	return vo;
    }
    
    public String getObjectType(String planid) {

        String Object_type = "";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search("select Object_type from per_plan where Plan_id=" + planid);
            if (rs.next()) {
                String type = rs.getString("Object_type");
                if ("1".equalsIgnoreCase(type)) {
                    Object_type = ResourceFactory.getProperty("jx.khplan.team");
                }

                if ("2".equalsIgnoreCase(type)) {
                    Object_type = ResourceFactory.getProperty("label.query.employ");
                }

                if ("3".equalsIgnoreCase(type)) {
                    Object_type = ResourceFactory.getProperty("label.query.unit");
                }

                if ("4".equalsIgnoreCase(type)) {
                    Object_type = ResourceFactory.getProperty("columns.archive.um");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return Object_type;
    }

    public StringBuffer getSpan_ids()
    {

	return span_ids;
    }

    public void setSpan_ids(StringBuffer span_ids)
    {

	this.span_ids = span_ids;
    }

    public HashMap getObjectTotalScoreMap()
    {

	return objectTotalScoreMap;
    }

    public void setObjectTotalScoreMap(HashMap objectTotalScoreMap)
    {

	this.objectTotalScoreMap = objectTotalScoreMap;
    }

    public ArrayList getObjectList()
    {

	return objectList;
    }

    public void setObjectList(ArrayList objectList)
    {

	this.objectList = objectList;
    }

    public String getShowOneMark()
    {

	return showOneMark;
    }

    public void setShowOneMark(String showOneMark)
    {

	this.showOneMark = showOneMark;
    }

    public String getPointContrl()
    {

	return pointContrl;
    }

    public void setPointContrl(String pointContrl)
    {

	this.pointContrl = pointContrl;
    }

    public HashMap getUserNumberPointResultMap()
    {

	return userNumberPointResultMap;
    }

    public void setUserNumberPointResultMap(HashMap userNumberPointResultMap)
    {

	this.userNumberPointResultMap = userNumberPointResultMap;
    }

    public String getObject_type()
    {

	return object_type;
    }

    public void setObject_type(String object_type)
    {

	this.object_type = object_type;
    }

    public HashMap getDynaRankInfoMap()
    {

	return dynaRankInfoMap;
    }

    public void setDynaRankInfoMap(HashMap dynaRankInfoMap)
    {

	this.dynaRankInfoMap = dynaRankInfoMap;
    }

    public HashMap getObjectInfoMap()
    {

	return objectInfoMap;
    }

    public void setObjectInfoMap(HashMap objectInfoMap)
    {

	this.objectInfoMap = objectInfoMap;
    }

    public String getIsEntiretySub()
    {

	return isEntiretySub;
    }

    public void setIsEntiretySub(String isEntiretySub)
    {

	this.isEntiretySub = isEntiretySub;
    }

    public String getIsShowOrder()
    {

	return isShowOrder;
    }

    public void setIsShowOrder(String isShowOrder)
    {

	this.isShowOrder = isShowOrder;
    }

    public String getIsShowTotalScore()
    {

	return isShowTotalScore;
    }

    public void setIsShowTotalScore(String isShowTotalScore)
    {

	this.isShowTotalScore = isShowTotalScore;
    }

    public String getScoreNumPerPage()
    {

	return scoreNumPerPage;
    }

    public void setScoreNumPerPage(String scoreNumPerPage)
    {

	this.scoreNumPerPage = scoreNumPerPage;
    }

    public String getGradeClass()
    {

	return GradeClass;
    }

    public void setGradeClass(String gradeClass)
    {

	GradeClass = gradeClass;
    }

    public String getLimitation()
    {

	return limitation;
    }

    public void setLimitation(String limitation)
    {

	this.limitation = limitation;
    }

    public String getNodeKnowDegree()
    {

	return NodeKnowDegree;
    }

    public void setNodeKnowDegree(String nodeKnowDegree)
    {

	NodeKnowDegree = nodeKnowDegree;
    }

    public String getScoreflag()
    {

	return scoreflag;
    }

    public void setScoreflag(String scoreflag)
    {

	this.scoreflag = scoreflag;
    }

    public String getShowNoMarking()
    {

	return showNoMarking;
    }

    public void setShowNoMarking(String showNoMarking)
    {

	this.showNoMarking = showNoMarking;
    }

    public String getSummaryFlag()
    {

	return SummaryFlag;
    }

    public void setSummaryFlag(String summaryFlag)
    {

	SummaryFlag = summaryFlag;
    }

    public String getWholeEval()
    {

	return WholeEval;
    }

    public void setWholeEval(String wholeEval)
    {

	WholeEval = wholeEval;
    }

    public String getPerformanceType()
    {

	return performanceType;
    }

    public void setPerformanceType(String performanceType)
    {

	this.performanceType = performanceType;
    }

    public String getIsShowSubmittedPlan()
    {

	return isShowSubmittedPlan;
    }

    public void setIsShowSubmittedPlan(String isShowSubmittedPlan)
    {

	this.isShowSubmittedPlan = isShowSubmittedPlan;
    }

    public LoadXml getLoadxml()
    {

	return loadxml;
    }

    public void setLoadxml(LoadXml loadxml)
    {

	this.loadxml = loadxml;
    }

    public String getIsAutoCountTotalOrder()
    {

	return isAutoCountTotalOrder;
    }

    public void setIsAutoCountTotalOrder(String isAutoCountTotalOrder)
    {

	this.isAutoCountTotalOrder = isAutoCountTotalOrder;
    }

    public StringBuffer getScript_code()
    {

	return script_code;
    }

    public void setScript_code(StringBuffer script_code)
    {

	this.script_code = script_code;
    }

    public String getAuto_createTable()
    {

	return auto_createTable;
    }

    public void setAuto_createTable(String auto_createTable)
    {

	this.auto_createTable = auto_createTable;
    }

    public RecordVo getPlanVo()
    {

	return planVo;
    }

    public void setPlanVo(RecordVo planVo)
    {

	this.planVo = planVo;
    }

    public String getBlankScoreOption()
    {

	return BlankScoreOption;
    }

    public void setBlankScoreOption(String blankScoreOption)
    {

	BlankScoreOption = blankScoreOption;
    }

    public String getDegreeShowType()
    {

	return DegreeShowType;
    }

    public void setDegreeShowType(String degreeShowType)
    {

	DegreeShowType = degreeShowType;
    }

	public static HashMap getPlanLoadXmlMap() {
		return planLoadXmlMap;
	}

	public static void setPlanLoadXmlMap(HashMap planLoadXmlMap) {
		BatchGradeBo.planLoadXmlMap = planLoadXmlMap;
	}

	public static HashMap getPlan_perPointMap() {
		return plan_perPointMap;
	}

	public static void setPlan_perPointMap(HashMap plan_perPointMap) {
		BatchGradeBo.plan_perPointMap = plan_perPointMap;
	}

	public static HashMap getPlan_perPointMap2() {
		return plan_perPointMap2;
	}

	public static void setPlan_perPointMap2(HashMap plan_perPointMap2) {
		BatchGradeBo.plan_perPointMap2 = plan_perPointMap2;
	}




	public String getDynaBodyToPoint() {
		return DynaBodyToPoint;
	}




	public void setDynaBodyToPoint(String dynaBodyToPoint) {
		DynaBodyToPoint = dynaBodyToPoint;
	}




	public boolean isNoShowOneMark() {
		return noShowOneMark;
	}


	//递归查找部门的直接单位
	public String getUnitCode(String codeitemid){
		ResultSet resultSet = null;
		String unitCode = "";
		String codesetid = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			resultSet = dao.search(" select codesetid,parentid from organization where codeitemid='"+codeitemid+"' ");
			while(resultSet.next()){
				unitCode = resultSet.getString("parentid");
				codesetid = resultSet.getString("codesetid");
			}
			if(codesetid!=null && "UM".equalsIgnoreCase(codesetid)){
				unitCode = getUnitCode(unitCode);
			} else{
				unitCode = codeitemid;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(resultSet!=null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return unitCode;
	}

	public void setNoShowOneMark(boolean noShowOneMark) {
		this.noShowOneMark = noShowOneMark;
	}




	public boolean isLoadStaticValue() {
		return isLoadStaticValue;
	}




	public void setLoadStaticValue(boolean isLoadStaticValue) {
		this.isLoadStaticValue = isLoadStaticValue;
	}




	public String getPlanid() {
		return planid;
	}




	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public ArrayList getBasicFieldList() {
		return basicFieldList;
	}

	public void setBasicFieldList(ArrayList basicFieldList) {
		this.basicFieldList = basicFieldList;
	}

	public String getLockMGradeColumn() {
		return LockMGradeColumn;
	}

	public void setLockMGradeColumn(String lockMGradeColumn) {
		LockMGradeColumn = lockMGradeColumn;
	}

	public String getError_info() {
		return error_info;
	}

	public void setError_info(String error_info) {
		this.error_info = error_info;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public HashMap getObjDynaRankMap() {
		return objDynaRankMap;
	}

	public void setObjDynaRankMap(HashMap objDynaRankMap) {
		this.objDynaRankMap = objDynaRankMap;
	}

}
