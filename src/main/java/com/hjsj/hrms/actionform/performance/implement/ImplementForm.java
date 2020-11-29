package com.hjsj.hrms.actionform.performance.implement;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.Pagination;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:ImplementForm.java</p>
 * <p>Description>:考核实施</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 05, 2007 09:15:57 AM</p>
 * <p>@author: JinChunhai </p>
 * <p>@version: 1.0</p>
 */

public class ImplementForm extends FrameForm
{
	
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private String[] objectIDs = null;
    private String[] mainbodyIDs = null;
    private String scoreWay = "";
    private ArrayList planList = new ArrayList(); // 绩效计划
    private String planid = "";
    private String encrptPlanid="";//加密的planid wangrd 20141215
    private String planStatus = "";
    private String templateid = "";
    private String templateStatus = "0"; // 模版权重分值标识 0:分值 1:权重
    private String object_id = "";
    private String deleteplanid = "";
    private String orderSql = "";
    private String sqlString = "";
    private String object_type = "2"; // 1:部门 2:人员
    private String method="1";        // 1:360度考核计划 2:目标评估
    private ArrayList objectTypeList = new ArrayList(); // 对象类别
    private ArrayList perObjectDataList = new ArrayList(); // 考核对象数据列表
    private PaginationForm perObjectDataListform=new PaginationForm();
    private ArrayList perGradeSetList=new ArrayList();  //考核等级分类列表
    private String perDegree="";
    private String optString="";
    private String a0101s;
    private String dbpre="";//人员库
    private ArrayList dblist=new ArrayList();//人员库表
    
    private String gradeByBodySeq="False"; //按考核主体顺序号控制评分流程(True, False默认为False)
    private ArrayList perMainBodyList = new ArrayList(); // 考核主体数据列表
    private ArrayList pointPowerHeadList = new ArrayList(); // 指标权限表头
    private ArrayList pointPowerList = new ArrayList(); // 指标权限列表
    private ArrayList planidselect = new ArrayList(); // 查询打分主体
    private ArrayList purviewList = new ArrayList(); // 设置权重purviewList
    private ArrayList taxisList = new ArrayList(); // 排序
    private String taxisid = "";

    /** 记录集名称 */
    private String setname = "A01";
    private ArrayList tablelist = new ArrayList();
    /** 选中的字段名数组 */
    private String left_fields[];
    /** 选中的字段名数组 */
    private String right_fields[];
    private ArrayList leftlist = new ArrayList();
    private ArrayList selectedFieldList = new ArrayList();
    private String str_sql;
    private String khObject;
    private ArrayList khObjectList = new ArrayList();

    private String khKey;

	private ArrayList khKeyClassList = new ArrayList();
    private ArrayList khObjectClassList = new ArrayList();
    
    private ArrayList mainbodys = new ArrayList(); // 考核主体数据列表
    private HashMap mainBodyCopyed; // 记录被复制的考核主体信息    
    private String HandEval="";
    /** 考核主体权重树 */
    private String treeCode;
    /** 考核指标权重树 */
    private String tartreeCode;
    private String paramStr="";
    private String  plan_b0110="";
    
    private ArrayList khRelaMainbody = new ArrayList();//标准考核关系主体
    private String isDistribute="";
    private ArrayList rightlist = new ArrayList();
    private ArrayList itemprivList = new ArrayList();//项目权限列表
    private ArrayList pointItemList = new ArrayList();//指标项目列表
    private String isBachGenerateTarget = "0";//是否批量生成目标卡
    private Map optMap = new HashMap(); // 计划中主体对应的评分确认关系 by 刘蒙

    
    private String code = "";
    private String codeset = "";
    private String power_type = "";//权限划分类别 point-指标权限划分 item-项目权限划分
    private String queryA0100="";
   
    private String codeid ="";
    private String noApproveTargetCanScore = "False"; //目标卡未审批也允许打分 True, False, 默认为 False    
    //动态项目权重/分值
    private String dynaItemHtml="";
    private String objTypeId = "";
    private String minTaskCount = "";
    private String maxTaskCount = "";
    private String maxScore = "";
    private String minScore = "";
    private ArrayList itemList=new ArrayList();
    
    //条件选择是否需要按部门对应标志
    private String accordByDepartmentFlag="";
    
    //目标卡制定
    private String objCode="";
    private String targetCardHtml="";
    private String importPoint_value="";
    private String targetCardTestStr="";
    private ArrayList lastRelaPlans=new ArrayList();//可以引入的上期目标计划
    private ArrayList allObjs=new ArrayList();//登录用户范围内的所有考核对象
    private RecordVo planVo=null;
    private String objInfo="";//目标卡制定定位考核对象用
    private String targetMemoField="";
    private String targetPointCol="";  //限制备注型指标的长度用
    private String scope="";//设置动态项目权重|分值 分值范围
    private String flag="";// 设置动态项目权重|分值 加扣分标志
    private String canshow="";//当考核计划 数据采集方式为 标度或打分时才显示 加扣分指标
    private String to_scope=""; // 设置动态项目权重|分值 分值范围
    private String beforeItemid=""; // 选中的项目ID
    
    /** 评价关系明细及权重 */
    private String detailHeadHtml = ""; // 评价关系明细表头HTML
    private ArrayList objectidTypeList=new ArrayList();  // 取得计划对应的对象类别列表
    private ArrayList mainbodyTypeList=new ArrayList();  // 取得主体类别列表
	private HashMap mainbodyMap = new HashMap();        // 取得范围内考核对象对应的考核主体
	private HashMap mainbodyDefaultRankMap = new HashMap(); // 取得设置的主体默认权重
	private HashMap mainbodyRankMap = new HashMap();     // 取得设置的动态主体权重
    
	// list自动生成考核主体页面用
    private PaginationForm setaotolistform = new PaginationForm();
    // list自动生成考核主体页面用
    private ArrayList setaotolist = new ArrayList(); 
    private String setinfomation="";//考核主体没有设置范围和条件的提示信息
    
    private ArrayList gradeScopeList = new ArrayList(); // 对象类别的主体评分范围
    
    private String scrollValue = "";//记录事件列表滚动条的位置
    //zgd 2015-1-13 start
    private String expression = "";//通用查询表达式因子  
    private String selectType = "";//条件选择类型 general=通用查询
	//zgd 2015-1-13 end
	
	public String getScrollValue() {
		return scrollValue;
	}

	public void setScrollValue(String scrollValue) {
		this.scrollValue = scrollValue;
	}
    
    
    @Override
    public void inPutTransHM()
    {
    	
    	this.getFormHM().put("a0101s", this.getA0101s());
    	this.getFormHM().put("gradeByBodySeq", this.getGradeByBodySeq());
    	this.getFormHM().put("busitype", this.getBusitype());
    	this.getFormHM().put("gradeScopeList", this.getGradeScopeList());
    	this.getFormHM().put("to_scope", this.getTo_scope());
    	this.getFormHM().put("mainbodyTypeList", this.getMainbodyTypeList());
    	this.getFormHM().put("objectidTypeList", this.getObjectidTypeList());
    	this.getFormHM().put("mainbodyMap", this.getMainbodyMap());
    	this.getFormHM().put("mainbodyDefaultRankMap", this.getMainbodyDefaultRankMap());
    	this.getFormHM().put("mainbodyRankMap", this.getMainbodyRankMap());   	
    	this.getFormHM().put("detailHeadHtml", this.getDetailHeadHtml());
    	this.getFormHM().put("flag", this.getFlag());
    	this.getFormHM().put("scope", this.getScope());
    	this.getFormHM().put("canshow", this.getCanshow());
    	this.getFormHM().put("targetPointCol", this.getTargetPointCol());
       	this.getFormHM().put("targetMemoField", this.getTargetMemoField());
       	this.getFormHM().put("objInfo", this.getObjInfo());
    	this.getFormHM().put("planVo", this.getPlanVo());
    	this.getFormHM().put("allObjs", this.getAllObjs());
    	this.getFormHM().put("lastRelaPlans", this.getLastRelaPlans());
     	this.getFormHM().put("targetCardTestStr", this.getTargetCardTestStr());
     	this.getFormHM().put("importPoint_value", this.getImportPoint_value());
    	this.getFormHM().put("objCode", this.getObjCode());
    	this.getFormHM().put("beforeItemid", this.getBeforeItemid());
    	this.getFormHM().put("targetCardHtml", this.getTargetCardHtml());
    	this.getFormHM().put("accordByDepartmentFlag", this.getAccordByDepartmentFlag());
    	this.getFormHM().put("objTypeId", this.getObjTypeId());
    	this.getFormHM().put("maxTaskCount", this.getMaxTaskCount());
    	this.getFormHM().put("maxScore", this.getMaxScore());
    	this.getFormHM().put("minScore", this.getMinScore());
    	this.getFormHM().put("minTaskCount", this.getMinTaskCount());
    	this.getFormHM().put("dynaItemHtml", this.getDynaItemHtml());
     	this.getFormHM().put("itemList", this.getItemList());
    	
    	this.getFormHM().put("noApproveTargetCanScore", this.getNoApproveTargetCanScore());
    	this.getFormHM().put("codeid", this.getCodeid());
    	this.getFormHM().put("queryA0100", this.getQueryA0100());	
    	this.getFormHM().put("power_type", this.getPower_type());	
    	this.getFormHM().put("code", this.getCode());
    	this.getFormHM().put("codeset", this.getCodeset());
		this.getFormHM().put("isBachGenerateTarget", this.getIsBachGenerateTarget());
		this.getFormHM().put("pointItemList", this.getPointItemList());
		this.getFormHM().put("itemprivList", this.getItemprivList());
		this.getFormHM().put("mainbodyIDs", this.getMainbodyIDs());
		this.getFormHM().put("objectIDs", this.getObjectIDs());
		this.getFormHM().put("planid", this.getPlanid());
		this.getFormHM().put("deleteplanid", this.getDeleteplanid());
		this.getFormHM().put("paramStr", this.getParamStr());
		this.getFormHM().put("setname", this.getSetname());
		this.getFormHM().put("left_fields", this.getLeft_fields());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("str_sql", this.getStr_sql());
		this.getFormHM().put("khObject", this.getKhObject());
		this.getFormHM().put("khKey", this.getKhKey());//khKey
		this.getFormHM().put("MainBodyCopyed", this.getMainBodyCopyed());
		this.getFormHM().put("orderSql", this.getOrderSql());
		this.getFormHM().put("purviewList", this.getPurviewList());
		this.getFormHM().put("taxisList", this.getTaxisList());
		this.getFormHM().put("plan_b0110", this.getPlan_b0110());
		this.getFormHM().put("khRelaMainbody", this.getKhRelaMainbody());
		this.getFormHM().put("isDistribute", this.getIsDistribute());
		this.getFormHM().put("rightlist", this.getRightlist());
		this.getFormHM().put("sqlString", this.getSqlString());
		this.getFormHM().put("optString", this.getOptString());
		this.getFormHM().put("dbpre", this.getDbpre());
		this.getFormHM().put("dblist", this.getDblist());
		this.getFormHM().put("scrollValue", this.getScrollValue());
		
		this.getFormHM().put("expression", this.getExpression());
		this.getFormHM().put("selectType", this.getSelectType());
    }

    
    @Override
    public void outPutFormHM()
    {   this.setA0101s((String)this.getFormHM().get("a0101s"));
    	this.setOptString((String)this.getFormHM().get("optString"));
    	this.setGradeByBodySeq((String)this.getFormHM().get("gradeByBodySeq"));
    	this.setBusitype((String)this.getFormHM().get("busitype"));
    	this.setGradeScopeList((ArrayList) this.getFormHM().get("gradeScopeList"));
    	this.setTo_scope((String)this.getFormHM().get("to_scope"));
    	this.setMainbodyTypeList((ArrayList) this.getFormHM().get("mainbodyTypeList"));
    	this.setObjectidTypeList((ArrayList) this.getFormHM().get("objectidTypeList"));
		this.setMainbodyMap((HashMap) this.getFormHM().get("mainbodyMap"));
		this.setMainbodyDefaultRankMap((HashMap) this.getFormHM().get("mainbodyDefaultRankMap"));
		this.setMainbodyRankMap((HashMap) this.getFormHM().get("mainbodyRankMap"));    	
    	this.setDetailHeadHtml((String)this.getFormHM().get("detailHeadHtml"));
    	this.setFlag((String)this.getFormHM().get("flag"));
    	this.setScope((String)this.getFormHM().get("scope"));
    	this.setCanshow((String)this.getFormHM().get("canshow"));
    	this.setTargetPointCol((String)this.getFormHM().get("targetPointCol")); 
    	this.setTargetMemoField((String)this.getFormHM().get("targetMemoField")); 
    	this.setObjInfo((String)this.getFormHM().get("objInfo")); 
    	this.setPlanVo((RecordVo) this.getFormHM().get("planVo")); 
    	this.setAllObjs((ArrayList) this.getFormHM().get("allObjs")); 
    	this.setLastRelaPlans((ArrayList) this.getFormHM().get("lastRelaPlans")); 
    	this.setTargetCardTestStr((String)this.getFormHM().get("targetCardTestStr")); 
    	this.setImportPoint_value((String)this.getFormHM().get("importPoint_value")); 
    	this.setObjCode((String)this.getFormHM().get("objCode")); 
    	this.setBeforeItemid((String)this.getFormHM().get("beforeItemid")); 
    	this.setTargetCardHtml((String)this.getFormHM().get("targetCardHtml")); 
    	this.setAccordByDepartmentFlag((String)this.getFormHM().get("accordByDepartmentFlag")); 
    	this.setDynaItemHtml((String)this.getFormHM().get("dynaItemHtml")); 
    	this.setMaxScore((String)this.getFormHM().get("maxScore")); 
    	this.setMinScore((String)this.getFormHM().get("minScore")); 
    	this.setMinTaskCount((String)this.getFormHM().get("minTaskCount")); 
    	this.setMaxTaskCount((String)this.getFormHM().get("maxTaskCount")); 
    	this.setObjTypeId((String)this.getFormHM().get("objTypeId")); 
    	this.setItemList((ArrayList) this.getFormHM().get("itemList")); 
    	
    	this.setNoApproveTargetCanScore((String)this.getFormHM().get("noApproveTargetCanScore")); 
    	this.setReturnflag((String)this.getFormHM().get("returnflag")); 
    	this.setCodeid((String)this.getFormHM().get("codeid"));
    	this.setQueryA0100((String)this.getFormHM().get("queryA0100"));
    	this.setPower_type((String)this.getFormHM().get("power_type"));
    	this.setCode((String)this.getFormHM().get("code"));
    	this.setCodeset((String)this.getFormHM().get("codeset"));
    	
		this.setPointItemList((ArrayList)this.getFormHM().get("pointItemList"));
		this.setItemprivList((ArrayList)this.getFormHM().get("itemprivList"));
		this.optMap = (Map)this.getFormHM().get("optMap");
		this.setKhRelaMainbody((ArrayList)this.getFormHM().get("khRelaMainbody"));
	    this.setHandEval((String)this.getFormHM().get("HandEval"));
		this.setPerGradeSetList((ArrayList)this.getFormHM().get("perGradeSetList"));
		this.setPerDegree((String)this.getFormHM().get("perDegree"));
		
		this.setTemplateStatus((String) this.getFormHM().get("templateStatus"));
		this.setScoreWay((String) this.getFormHM().get("scoreWay"));
		this.setOrderSql((String) this.getFormHM().get("orderSql"));
		this.setPlanStatus((String) this.getFormHM().get("planStatus"));
		this.setObject_id((String) this.getFormHM().get("object_id"));
		this.setPlanid((String) this.getFormHM().get("planid"));
		String _planid=(String) this.getFormHM().get("planid");
		if (_planid!=null && _planid.length()>0){
		    this.setEncrptPlanid(PubFunc.encrypt(_planid)); 
		}
		else {
		    this.setEncrptPlanid(""); 
		}
		
		this.setTemplateid((String) this.getFormHM().get("template_id"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setMethod((String)this.getFormHM().get("method"));
		this.setObjectTypeList((ArrayList) this.getFormHM().get("objectTypeList"));
		this.setPlanList((ArrayList) this.getFormHM().get("planList"));
		this.setPerObjectDataList((ArrayList) this.getFormHM().get("perObjectDataList"));
		this.getPerObjectDataListform().setList((ArrayList) this.getFormHM().get("perObjectDataList"));
	
		this.setPerMainBodyList((ArrayList) this.getFormHM().get("perMainBodyList"));
		this.setPointPowerHeadList((ArrayList) this.getFormHM().get("pointPowerHeadList"));
		this.setPointPowerList((ArrayList) this.getFormHM().get("pointPowerList"));
		this.setPlanidselect((ArrayList) this.getFormHM().get("planidselect"));
		this.setDeleteplanid((String) this.getFormHM().get("deleteplanid"));
		this.setPurviewList((ArrayList) this.getFormHM().get("purviewList"));
		this.setTaxisList((ArrayList) this.getFormHM().get("taxisList"));
	
		this.setSetname((String) this.getFormHM().get("setname"));
		this.setTablelist((ArrayList) this.getFormHM().get("tablelist"));
		this.setLeftlist((ArrayList) this.getFormHM().get("leftlist"));
		this.setLeft_fields((String[]) this.getFormHM().get("left_fields"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setSelectedFieldList((ArrayList) this.getFormHM().get("selectedFieldList"));
		this.setStr_sql((String) this.getFormHM().get("str_sql"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setTartreeCode((String) this.getFormHM().get("tartreeCode"));
		this.setKhObject((String) this.getFormHM().get("khObject"));
		this.setKhObjectList((ArrayList) this.getFormHM().get("khObjectList"));
		this.setKhKey((String) this.getFormHM().get("khKey"));//khKey
		this.setKhKeyClassList((ArrayList) this.getFormHM().get("khKeyClassList"));
		this.setKhObjectClassList((ArrayList) this.getFormHM().get("khObjectClassList"));
		
		this.setMainbodys((ArrayList) this.getFormHM().get("mainbodys"));
		this.setMainBodyCopyed((HashMap) this.getFormHM().get("MainBodyCopyed"));
		this.setTaxisid((String) this.getFormHM().get("taxisid"));
		this.setParamStr((String) this.getFormHM().get("paramStr"));
		this.setPlan_b0110((String) this.getFormHM().get("plan_b0110"));
		this.setIsDistribute((String) this.getFormHM().get("isDistribute"));
		if(this.getFormHM().get("rightlist")!=null)
		     this.setRightlist((ArrayList) this.getFormHM().get("rightlist"));
		this.setIsBachGenerateTarget((String) this.getFormHM().get("isBachGenerateTarget"));
		this.getSetaotolistform().setList((ArrayList) this.getFormHM().get("setaotolist"));
		this.setSetinfomation((String) this.getFormHM().get("setinfomation"));
		this.setSqlString((String)this.getFormHM().get("sqlString"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setDblist((ArrayList) this.getFormHM().get("dblist"));
		this.setScrollValue((String)this.getFormHM().get("scrollValue"));
		
		this.setExpression((String)this.getFormHM().get("expression"));
		this.setSelectType((String)this.getFormHM().get("selectType"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		if (arg1.getParameter("operate") != null && ("init0".equals(arg1.getParameter("operate")) || "init".equals(arg1.getParameter("operate")) || "query".equals(arg1.getParameter("operate"))))
		{
			Pagination pagination = this.perObjectDataListform.getPagination();
			/** 定位到首页, */
		    if (pagination != null && pagination.getCount() < 2) {
		    	pagination.firstPage();
		    }
		}
		return super.validate(arg0, arg1);
    }
    
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {

		if("/performance/implement/kh_object/condition_select".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
		{
			if(this.rightlist!=null)
				this.rightlist.clear();
			this.right_fields=new String[0];
		}
        if("/performance/implement/kh_mainbody/mainbodySel".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null))
        {
            arg1.setAttribute("targetWindow","1");
        }

    }        
    
    public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCanshow() {
		return canshow;
	}

	public void setCanshow(String canshow) {
		this.canshow = canshow;
	}

	public ArrayList getPerMainBodyList()
    {

	return perMainBodyList;
    }

    public void setPerMainBodyList(ArrayList perMainBodyList)
    {

	this.perMainBodyList = perMainBodyList;
    }

    public ArrayList getPerObjectDataList()
    {

	return perObjectDataList;
    }

    public String getNoApproveTargetCanScore()
	{
		return noApproveTargetCanScore;
	}

	public void setNoApproveTargetCanScore(String noApproveTargetCanScore)
	{
		this.noApproveTargetCanScore = noApproveTargetCanScore;
	}

	public void setPerObjectDataList(ArrayList perObjectDataList)
    {

	this.perObjectDataList = perObjectDataList;
    }

    public ArrayList getPlanList()
    {

	return planList;
    }

    public void setPlanList(ArrayList planList)
    {

	this.planList = planList;
    }

    public ArrayList getPointPowerHeadList()
    {

	return pointPowerHeadList;
    }

    public void setPointPowerHeadList(ArrayList pointPowerHeadList)
    {

	this.pointPowerHeadList = pointPowerHeadList;
    }

    public ArrayList getPointPowerList()
    {

	return pointPowerList;
    }

    public void setPointPowerList(ArrayList pointPowerList)
    {

	this.pointPowerList = pointPowerList;
    }

    public String[] getObjectIDs()
    {

	return objectIDs;
    }

    public void setObjectIDs(String[] objectIDs)
    {

	this.objectIDs = objectIDs;
    }

    public String[] getMainbodyIDs()
    {

	return mainbodyIDs;
    }

    public void setMainbodyIDs(String[] mainbodyIDs)
    {

	this.mainbodyIDs = mainbodyIDs;
    }

    public String getPlanid()
    {

	return planid;
    }

    public void setPlanid(String planid)
    {

	this.planid = planid;
    }

    public String getObject_type()
    {

	return object_type;
    }

    public void setObject_type(String object_type)
    {

	this.object_type = object_type;
    }

    public String getTemplateid()
    {

	return templateid;
    }

    public void setTemplateid(String templateid)
    {

	this.templateid = templateid;
    }

    public ArrayList getObjectTypeList()
    {

	return objectTypeList;
    }

    public void setObjectTypeList(ArrayList objectTypeList)
    {

	this.objectTypeList = objectTypeList;
    }

    public String getObject_id()
    {

	return object_id;
    }

    public void setObject_id(String object_id)
    {

	this.object_id = object_id;
    }

    public String getPlanStatus()
    {

	return planStatus;
    }

    public void setPlanStatus(String planStatus)
    {

	this.planStatus = planStatus;
    }

    public String[] getLeft_fields()
    {

	return left_fields;
    }

    public void setLeft_fields(String[] left_fields)
    {

	this.left_fields = left_fields;
    }

    public String[] getRight_fields()
    {

	return right_fields;
    }

    public void setRight_fields(String[] right_fields)
    {

	this.right_fields = right_fields;
    }

    public String getSetname()
    {

	return setname;
    }

    public void setSetname(String setname)
    {

	this.setname = setname;
    }

    public ArrayList getTablelist()
    {

	return tablelist;
    }

    public void setTablelist(ArrayList tablelist)
    {

	this.tablelist = tablelist;
    }

    public ArrayList getLeftlist()
    {

	return leftlist;
    }

    public void setLeftlist(ArrayList leftlist)
    {

	this.leftlist = leftlist;
    }

    public ArrayList getSelectedFieldList()
    {

	return selectedFieldList;
    }

    public void setSelectedFieldList(ArrayList selectedFieldList)
    {

	this.selectedFieldList = selectedFieldList;
    }

    public String getStr_sql()
    {

	return str_sql;
    }

    public void setStr_sql(String str_sql)
    {

	this.str_sql = str_sql;
    }

    public ArrayList getPlanidselect()
    {

	return planidselect;
    }

    public void setPlanidselect(ArrayList planidselect)
    {

	this.planidselect = planidselect;
    }

    public String getDeleteplanid()
    {

	return deleteplanid;
    }

    public void setDeleteplanid(String planid)
    {

	this.deleteplanid = deleteplanid;
    }

    public String getTreeCode()
    {

	return treeCode;
    }

    public void setTreeCode(String treeCode)
    {

	this.treeCode = treeCode;
    }

    public ArrayList getPurviewList()
    {

	return purviewList;
    }

    public void setPurviewList(ArrayList purviewList)
    {

	this.purviewList = purviewList;
    }

    public String getKhObject()
    {

	return khObject;
    }

    public void setKhObject(String khObject)
    {

	this.khObject = khObject;
    }

	public String getKhKey() {
		return khKey;
	}

	public void setKhKey(String khKey) {
		this.khKey = khKey;
	}
	
	
    public ArrayList getKhObjectList()
    {

	return khObjectList;
    }

    public void setKhObjectList(ArrayList khObjectList)
    {

	this.khObjectList = khObjectList;
    }
    
    
    public ArrayList getKhKeyClassList() {
		return khKeyClassList;
	}

	public void setKhKeyClassList(ArrayList khKeyClassList) {
		this.khKeyClassList = khKeyClassList;
	}

	public ArrayList getKhObjectClassList() {
		return khObjectClassList;
	}

	public void setKhObjectClassList(ArrayList khObjectClassList) {
		this.khObjectClassList = khObjectClassList;
	}

	
    public ArrayList getMainbodys()
    {

	return mainbodys;
    }

    public void setMainbodys(ArrayList mainbodys)
    {

	this.mainbodys = mainbodys;
    }

    public HashMap getMainBodyCopyed()
    {

	return mainBodyCopyed;
    }

    public void setMainBodyCopyed(HashMap mainBodyCopyed)
    {

	this.mainBodyCopyed = mainBodyCopyed;
    }

    public ArrayList getTaxisList()
    {

	return taxisList;
    }

    public void setTaxisList(ArrayList taxisList)
    {

	this.taxisList = taxisList;
    }

    public String getTaxisid()
    {

	return taxisid;
    }

    public void setTaxisid(String taxisid)
    {

	this.taxisid = taxisid;
    }

    public String getOrderSql()
    {

	return orderSql;
    }

    public void setOrderSql(String orderSql)
    {

	this.orderSql = orderSql;
    }

    public String getTartreeCode()
    {

	return tartreeCode;
    }

    public void setTartreeCode(String tartreeCode)
    {

	this.tartreeCode = tartreeCode;
    }

    public String getScoreWay()
    {

	return scoreWay;
    }

    public void setScoreWay(String scoreWay)
    {

	this.scoreWay = scoreWay;
    }

    public String getTemplateStatus()
    {

	return templateStatus;
    }

    public void setTemplateStatus(String templateStatus)
    {

	this.templateStatus = templateStatus;
    }

    public PaginationForm getPerObjectDataListform()
    {

	return perObjectDataListform;
    }

    public void setPerObjectDataListform(PaginationForm perObjectDataListform)
    {

	this.perObjectDataListform = perObjectDataListform;
    }



	public ArrayList getPerGradeSetList() {
		return perGradeSetList;
	}

	public void setPerGradeSetList(ArrayList perGradeSetList) {
		this.perGradeSetList = perGradeSetList;
	}

	public String getPerDegree() {
		return perDegree;
	}

	public void setPerDegree(String perDegree) {
		this.perDegree = perDegree;
	}

	public String getHandEval() {
		return HandEval;
	}

	public void setHandEval(String handEval) {
		HandEval = handEval;
	}

	public String getParamStr()
	{
	
	    return paramStr;
	}

	public void setParamStr(String paramStr)
	{
	
	    this.paramStr = paramStr;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPlan_b0110()
	{
	
	    return plan_b0110;
	}

	public void setPlan_b0110(String plan_b0110)
	{
	
	    this.plan_b0110 = plan_b0110;
	}

	public ArrayList getKhRelaMainbody()
	{
	
	    return khRelaMainbody;
	}

	public void setKhRelaMainbody(ArrayList khRelaMainbody)
	{
	
	    this.khRelaMainbody = khRelaMainbody;
	}

	public String getIsDistribute()
	{
	
	    return isDistribute;
	}

	public void setIsDistribute(String isDistribute)
	{
	
	    this.isDistribute = isDistribute;
	}

	public ArrayList getRightlist()
	{
	
	    return rightlist;
	}

	public void setRightlist(ArrayList rightlist)
	{
	
	    this.rightlist = rightlist;
	}

	public ArrayList getItemprivList()
	{
	
	    return itemprivList;
	}

	public void setItemprivList(ArrayList itemprivList)
	{
	
	    this.itemprivList = itemprivList;
	}

	public ArrayList getPointItemList()
	{
	
	    return pointItemList;
	}

	public void setPointItemList(ArrayList pointItemList)
	{
	
	    this.pointItemList = pointItemList;
	}

	public String getIsBachGenerateTarget()
	{
	
	    return isBachGenerateTarget;
	}

	public void setIsBachGenerateTarget(String isBachGenerateTarget)
	{
	
	    this.isBachGenerateTarget = isBachGenerateTarget;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeset() {
		return codeset;
	}

	public void setCodeset(String codeset) {
		this.codeset = codeset;
	}

	public String getPower_type()
	{
		return power_type;
	}

	public void setPower_type(String power_type)
	{
		this.power_type = power_type;
	}

	public String getQueryA0100()
	{
		return queryA0100;
	}

	public void setQueryA0100(String queryA0100)
	{
		this.queryA0100 = queryA0100;
	}

	public String getCodeid()
	{
		return codeid;
	}

	public void setCodeid(String codeid)
	{
		this.codeid = codeid;
	}

	public String getDynaItemHtml()
	{
		return dynaItemHtml;
	}

	public void setDynaItemHtml(String dynaItemHtml)
	{
		this.dynaItemHtml = dynaItemHtml;
	}

	public String getMaxScore()
	{
		return maxScore;
	}

	public void setMaxScore(String maxScore)
	{
		this.maxScore = maxScore;
	}

	public String getMaxTaskCount()
	{
		return maxTaskCount;
	}

	public void setMaxTaskCount(String maxTaskCount)
	{
		this.maxTaskCount = maxTaskCount;
	}

	public String getMinTaskCount()
	{
		return minTaskCount;
	}

	public void setMinTaskCount(String minTaskCount)
	{
		this.minTaskCount = minTaskCount;
	}

	public String getObjTypeId()
	{
		return objTypeId;
	}

	public void setObjTypeId(String objTypeId)
	{
		this.objTypeId = objTypeId;
	}

	public ArrayList getItemList()
	{
		return itemList;
	}

	public void setItemList(ArrayList itemList)
	{
		this.itemList = itemList;
	}

	public String getAccordByDepartmentFlag()
	{
		return accordByDepartmentFlag;
	}

	public void setAccordByDepartmentFlag(String accordByDepartmentFlag)
	{
		this.accordByDepartmentFlag = accordByDepartmentFlag;
	}

	public String getObjCode()
	{
		return objCode;
	}

	public void setObjCode(String objCode)
	{
		this.objCode = objCode;
	}

	public String getTargetCardHtml()
	{
		return targetCardHtml;
	}

	public void setTargetCardHtml(String targetCardHtml)
	{
		this.targetCardHtml = targetCardHtml;
	}

	public String getImportPoint_value()
	{
		return importPoint_value;
	}

	public void setImportPoint_value(String importPoint_value)
	{
		this.importPoint_value = importPoint_value;
	}

	public String getTargetCardTestStr() {
		return targetCardTestStr;
	}

	public void setTargetCardTestStr(String targetCardTestStr) {
		this.targetCardTestStr = targetCardTestStr;
	}

	public ArrayList getLastRelaPlans() {
		return lastRelaPlans;
	}

	public void setLastRelaPlans(ArrayList lastRelaPlans) {
		this.lastRelaPlans = lastRelaPlans;
	}

	public ArrayList getAllObjs() {
		return allObjs;
	}

	public void setAllObjs(ArrayList allObjs) {
		this.allObjs = allObjs;
	}

	public RecordVo getPlanVo() {
		return planVo;
	}

	public void setPlanVo(RecordVo planVo) {
		this.planVo = planVo;
	}

	public String getObjInfo() {
		return objInfo;
	}

	public void setObjInfo(String objInfo) {
		this.objInfo = objInfo;
	}

	public String getTargetMemoField() {
		return targetMemoField;
	}

	public void setTargetMemoField(String targetMemoField) {
		this.targetMemoField = targetMemoField;
	}

	public String getTargetPointCol() {
		return targetPointCol;
	}

	public void setTargetPointCol(String targetPointCol) {
		this.targetPointCol = targetPointCol;
	}

	public String getMinScore() {
		return minScore;
	}

	public void setMinScore(String minScore) {
		this.minScore = minScore;
	}

	public String getDetailHeadHtml() {
		return detailHeadHtml;
	}

	public void setDetailHeadHtml(String detailHeadHtml) {
		this.detailHeadHtml = detailHeadHtml;
	}

	public ArrayList getObjectidTypeList() {
		return objectidTypeList;
	}

	public void setObjectidTypeList(ArrayList objectidTypeList) {
		this.objectidTypeList = objectidTypeList;
	}

	public HashMap getMainbodyMap() {
		return mainbodyMap;
	}

	public void setMainbodyMap(HashMap mainbodyMap) {
		this.mainbodyMap = mainbodyMap;
	}

	public HashMap getMainbodyDefaultRankMap() {
		return mainbodyDefaultRankMap;
	}

	public void setMainbodyDefaultRankMap(HashMap mainbodyDefaultRankMap) {
		this.mainbodyDefaultRankMap = mainbodyDefaultRankMap;
	}

	public HashMap getMainbodyRankMap() {
		return mainbodyRankMap;
	}

	public void setMainbodyRankMap(HashMap mainbodyRankMap) {
		this.mainbodyRankMap = mainbodyRankMap;
	}

	public ArrayList getMainbodyTypeList() {
		return mainbodyTypeList;
	}

	public void setMainbodyTypeList(ArrayList mainbodyTypeList) {
		this.mainbodyTypeList = mainbodyTypeList;
	}

	public PaginationForm getSetaotolistform() {
		return setaotolistform;
	}

	public void setSetaotolistform(PaginationForm setaotolistform) {
		this.setaotolistform = setaotolistform;
	}

	public ArrayList getSetaotolist() {
		return setaotolist;
	}

	public void setSetaotolist(ArrayList setaotolist) {
		this.setaotolist = setaotolist;
	}

	public String getSetinfomation() {
		return setinfomation;
	}

	public void setSetinfomation(String setinfomation) {
		this.setinfomation = setinfomation;
	}

	public String getTo_scope() {
		return to_scope;
	}

	public void setTo_scope(String to_scope) {
		this.to_scope = to_scope;
	}

	public ArrayList getGradeScopeList() {
		return gradeScopeList;
	}

	public void setGradeScopeList(ArrayList gradeScopeList) {
		this.gradeScopeList = gradeScopeList;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getSqlString() {
		return sqlString;
	}

	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}

	public String getBeforeItemid() {
		return beforeItemid;
	}

	public void setBeforeItemid(String beforeItemid) {
		this.beforeItemid = beforeItemid;
	}

	public String getGradeByBodySeq() {
		return gradeByBodySeq;
	}

	public void setGradeByBodySeq(String gradeByBodySeq) {
		this.gradeByBodySeq = gradeByBodySeq;
	}


	public String getOptString() {
		return optString;
	}


	public void setOptString(String optString) {
		this.optString = optString;
	}


	public String getA0101s() {
		return a0101s;
	}


	public void setA0101s(String a0101s) {
		this.a0101s = a0101s;
	}


	public String getDbpre() {
		return dbpre;
	}


	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}


	public ArrayList getDblist() {
		return dblist;
	}


	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public Map getOptMap() {
		return optMap;
	}

	public void setOptMap(Map optMap) {
		this.optMap = optMap;
	}

    public String getEncrptPlanid() {
        return encrptPlanid;
    }

    public void setEncrptPlanid(String encrptPlanid) {
        this.encrptPlanid = encrptPlanid;
    }

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}
	
}
