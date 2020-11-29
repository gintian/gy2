package com.hjsj.hrms.actionform.general.muster.hmuster;

import com.hrms.hjsj.sys.Des;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class HmusterForm extends FrameForm {
	
	/**
	 * 
	  MOD_BXMUSTER     =  1;  // 保险台帐
	  MOD_HTMUSTER     =  2;  // 合同台帐
	  MOD_PERSONMUSTER =  3;  // 人员名册
	  MOD_GZMUSTER     =  4;  // 工资台帐
	  MOD_TEMPLATE     =  5;  // 模板花名册
	  MOD_GZANALYZE    =  6;  // 工资分析名册
	  MOD_BPMUSTER     =  7;  // 报批花名册
	  MOD_BXANALYZE    =  8;  // 保险分析花名册
	  MOD_BXXZMUSTER   = 11;  // 保险自定义花名册
	  MOD_GZTAOMUSTER  = 14;  // 工资自定义报表
	  MOD_TAXMUSTER    = 15;  // 个人所得税
	  MOD_TMPLARCHIVE  = 16;  // 模板归档
	  MOD_PER          = 17;  // 绩效评估花名册
	  MOD_ORGMUSTER    = 21;  // 机构花名册
	  MOD_POSMUSTER    = 41;  // 职位花名册
	  MOD_STDPOSMUSTER = 51;  // 基准岗位花名册
	  MOD_TRAINMUSTER  = 61;  // 培训花名册
	  MOD_KQMUSTER     = 81;  // 考勤花名册
	  3,6,8,11,14,21,41取数时支持选择排序指标
	 */
    private String modelFlag="";         //模块标识
    private String relatTableid="";  //关联表（只针对考勤模块）
    private String condition="";     //查询条件（只针对考勤模块）
    private String returnURL="";     //返回页面的连接地址
    
	/**信息群标识*/
	private String infor_Flag="1";   //1人员, 2机构, 3职位, 5基准岗位
    /**应用库表前缀*/
    private String dbpre; 
    /**应用库表前缀列表, 撇号(`)分隔用于取数查询*/
    private String dbprelist; 
	/**人员库列表*/
    private ArrayList dblist=new ArrayList();
    /**花名册列表*/
    private ArrayList hmusterlist=new ArrayList();
    /**选中的花名册id */
    private String tabID="";
    /**查询的范围 */
    private String queryScope="1";  //1：查询结果  2：全部记录
    
    private String flag="0";        //"0":无  "1"有子集指标无年月标识,可按最后一条历史纪录查  "2"有子集指标无年月标识,可按取部分历史纪录查   "3"有子集指标和年月标识，可按某次的历史纪录查//4:按某年某次取
    /**对历史记录进行查询*/
    private String history="0";    //1:最后一条历史纪录  3：某次历史纪录 2：部分历史纪录  
    private String year;  //年
    private String month; //月
    private String count; //次
    
    private String fromScope;  
    private String toScope;
    
    private ArrayList subPointList=new ArrayList();   //取部分历史纪录 的 候选指标集
    private String    selectedPoint;
    
    private String no_manager_priv;       //true：不按管理范围  false：按管理范围
    private String isAutoCount="0";    	  //0:为自动计算  1:用户指定
    private String pageRows;    	      //n:为用户指定的每页行数
    private String zeroPrint="0"; 		  //0:不为零打印  1：零打印
    private String emptyRow="0"; 		  //0：空行不打印  1：空行打印
    private String column="0";   		  //0:不分栏 1：横向分栏  2：纵向分栏
    private String dataarea="0";   		  //0:单行数据区 1：多行数据区
    private String pix="0";        		  //栏间距的像素
    private String columnLine="";         //分隔线  1：为有分隔线
    private ArrayList groupPointList=new ArrayList();  //分组指标集
    private String isGroupPoint="0";      //是否选用分组指标  1:选用
    private String groupPoint;            //已选的分组指标
    private ArrayList layerlist = new ArrayList(); //层级汇总
    private String layerid="";            //层级汇总
    private String groupOrgCodeSet="";    
    /**第二分组指标层级*/
    private ArrayList layerlist2 = new ArrayList();
    private String layerid2="";
    /**第二分组指标*/
    private ArrayList groupPointList2 = new ArrayList();
    private String isGroupPoint2="0";
    private String groupPoint2;
    private String groupOrgCodeSet2="";
    
    private String multigroups;            //分组分页
    private String tableHeader="";  	  //花名册的表头
    private String tableBody="";    	  //花名册的表体
    private String tableTitleTop="";  	  //花名册的标题头
    private String tableTitleBottom="";   //花名册的标题尾
    private String turnPage="";           //翻页
    private String currpage="0";		  //当前页
    
    private String isRecords="0";         //判断库中该表是否已有记录  0:没有  1：有
    private String isResultTable="0";     //判断是否有查询结果表      0：没有 1：有
    private String historyRecord="1";     //0:重新取数  1：上次取数
    private String printGrid="1";		  //打印格线     0:不打印  1：打印
    
    private ArrayList photoList=new ArrayList();   // 临时文件夹中的图片文件列表
    private String operateMethod="";      // "direct":直接生成  "next":设置生成
    
    private String searchResultFlag;
    private String paperRows="21";
    private String checkflag;
    private String treeCode;
    private String conditions; //过滤条件
    private String selecttime; //时间范围 1.所有 2.某年 3.某月 4.某次 5.区间
    private ArrayList conditionslist = new ArrayList(); ;
    private String summary; //汇总
    private String startime; //开始时间
    private String endtime; //结束时间
    private String spflag; //审核
    private String sortitem; //排序指标
    private String countflag=""; //汇总
    private String sql="";//人事异动进入花名册，限制人员
    private String isReData;
    private String divHeight;
    private String divWidth;
    private String isCloseButton;  // 0没有关闭按钮, 1有关闭按钮
    private String filterByMdule;
    private String combineField;
    private ArrayList combineFieldList= new ArrayList();
    private String historyFlag;//调用cs插件参数，历史记录取值方式:0当前,1年月次,2条件
    private String dateStart;//调用cs插件参数,取数起始时间
    private String dateEnd;//调用cs插件参数,取数结束时间
    /** =1,2关闭,=0,4,5返回,其他值无按钮 */
    private String closeWindow;
    /**所得税管理页面的查询语句*/
    private String conSQL;
    private String kqtable;
    
    private String cardid;//xuj add 2011-1-30 人员登记表格号
    private String isPrint="";
    private String fromtable;//所得税花名册，数据来源
    private String returnType;
    /**是否显示兼职人员*/
    private String showPartJob;
    
    /* 哈药领导桌面  xiaoyun 2014-8-13 start */
    /** 按钮（高级花名册页面的“取数”、“输出Excel”、“输出PDF”、“打印预演”等按钮）是否显示开关，1显示按钮(默认值)，0不显示 */
    private String showbuttons;
	/** 设置的常用查询集合 add on 2014-8-14 */
	private List mainParamCondList = new ArrayList();
	/** 参数标题 add on 2014-8-14 */
	private String mainParamTitle;
	/** 查询条件(针对基础模块 员工、机构、岗位)  将首页进入高级花名册和基础模块进入高级花名册设置的一致的 add on 2014-8-16 */
	private String conditionBase;
	/** 是否在名字后面显示能力匹配连接的小图标 1-显示 其他-不显示 add on 2014-8-20 */
	private String linktype;
	
	public String getShowbuttons() {
		return showbuttons;
	}

	public void setShowbuttons(String showbuttons) {
		this.showbuttons = showbuttons;
	}

	public List getMainParamCondList() {
		return mainParamCondList;
	}

	public void setMainParamCondList(List mainParamCondList) {
		this.mainParamCondList = mainParamCondList;
	}

	public String getMainParamTitle() {
		return mainParamTitle;
	}

	public void setMainParamTitle(String mainParamTitle) {
		this.mainParamTitle = mainParamTitle;
	}
	
	public String getLinktype() {
		return linktype;
	}

	public void setLinktype(String linktype) {
		this.linktype = linktype;
	}

	/* 哈药领导桌面 xiaoyun 2014-8-13 end */

	public String getConditionBase() {
		return conditionBase;
	}

	public void setConditionBase(String conditionBase) {
		this.conditionBase = conditionBase;
	}

	public String getShowPartJob() {
		return showPartJob;
	}

	public void setShowPartJob(String showPartJob) {
		this.showPartJob = showPartJob;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public HmusterForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
    public void outPutFormHM() {

		
		//**  由于form属性为session，所以返回格式页面时清空该页面的属性值    **/
		if(this.getFormHM().get("clearFormat")!=null&& "1".equals((String)this.getFormHM().get("clearFormat")))
		{
			this.setIsAutoCount("0");
			this.setZeroPrint("0");
			this.setEmptyRow("0");
			this.setIsGroupPoint("0");
			this.setPageRows("");
			this.setColumn("0");
			this.setIsResultTable("0");
			this.setColumnLine("");
			this.setPix("0");			
			this.setHistoryRecord("1");
			this.setCurrpage("0");
			this.setPrintGrid("1");
			this.getFormHM().remove("clearFormat");
		}
			
			
		if(this.getFormHM().get("clears")!=null&& "1".equals((String)this.getFormHM().get("clears")))
		{
			/* 由于form范围为session，所以将一些参数初始化 */	
			this.setIsAutoCount("0");
			this.setColumnLine("0");
			this.setPageRows("");
			this.setGroupPoint("");
			this.setCurrpage("0");
			this.setZeroPrint("0");
			this.setPix("0");
			this.setEmptyRow("0");
			this.setColumn("0");
			this.setColumnLine("");
			this.setIsGroupPoint("0");
			this.setHistoryRecord("1");
			this.setIsRecords("0");
			this.setIsResultTable("0");
			this.setPrintGrid("1");
			this.getFormHM().remove("clears");
		
	
		}
		this.setShowPartJob((String)this.getFormHM().get("showPartJob"));
		this.setReturnType((String)this.getFormHM().get("returnType"));
		this.setFromtable((String)this.getFormHM().get("fromtable"));
		this.setIsPrint((String)this.getFormHM().get("isPrint"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setConSQL((String)this.getFormHM().get("conSQL"));
		this.setCloseWindow((String)this.getFormHM().get("closeWindow"));
		this.setDateEnd((String)this.getFormHM().get("dateEnd"));
		this.setDateStart((String)this.getFormHM().get("dateStart"));
	    this.setHistoryFlag((String)this.getFormHM().get("historyFlag"));
		this.setCombineField((String)this.getFormHM().get("combineField"));
		this.setCombineFieldList((ArrayList)this.getFormHM().get("combineFieldList"));
		this.setFilterByMdule((String)this.getFormHM().get("filterByMdule"));
		this.setIsCloseButton((String)this.getFormHM().get("isCloseButton"));
		this.setDivHeight((String)this.getFormHM().get("divHeight"));
		this.setDivWidth((String)this.getFormHM().get("divWidth"));
        this.setIsReData((String)this.getFormHM().get("isReData"));
        /**这里涉及到人事异动安全的sql，不向前台中存放,后台交易类若用到,可以从useview中获取**/
		//this.setSql((String)this.getFormHM().get("sql"));
		this.setPaperRows((String)this.getFormHM().get("paperRows"));
		this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
		this.setCondition((String)this.getFormHM().get("condition"));
		this.setReturnURL((String)this.getFormHM().get("returnURL"));
		this.setModelFlag((String)this.getFormHM().get("modelFlag"));
		
		this.setOperateMethod((String)this.getFormHM().get("operateMethod"));
		this.setIsResultTable((String)this.getFormHM().get("isResultTable"));
		this.setPhotoList((ArrayList)this.getFormHM().get("photoList"));	
		this.setInfor_Flag((String)this.getFormHM().get("inforkind"));
		this.setHmusterlist((ArrayList)this.getFormHM().get("hmusterlist"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setTabID((String)this.getFormHM().get("tabID"));
		this.setEmptyRow((String)this.getFormHM().get("emptyRow"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setDataarea((String)this.getFormHM().get("dataarea"));
		this.setPageRows((String)this.getFormHM().get("pageRows"));
		this.setPix((String)this.getFormHM().get("pix"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setToScope((String)this.getFormHM().get("toScope"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setZeroPrint((String)this.getFormHM().get("zeroPrint"));
		this.setIsGroupPoint((String)this.getFormHM().get("isGroupPoint"));
		this.setGroupPoint((String)this.getFormHM().get("groupPoint"));
		this.setIsGroupPoint2((String)this.getFormHM().get("isGroupPoint2"));
		this.setGroupPoint2((String)this.getFormHM().get("groupPoint2"));
		this.setEmptyRow((String)this.getFormHM().get("emptyRow"));
		this.setColumnLine((String)this.getFormHM().get("columnLine"));
		this.setHistoryRecord((String)this.getFormHM().get("historyRecord"));
		this.setPrintGrid((String)this.getFormHM().get("printGrid"));
		this.setIsAutoCount((String)this.getFormHM().get("isAutoCount"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setConditionslist((ArrayList)this.getFormHM().get("conditionslist"));
		this.setSelecttime((String)this.getFormHM().get("selecttime"));
		this.setSummary((String)this.getFormHM().get("summary"));
		this.setSubPointList((ArrayList)this.getFormHM().get("subPointList"));
		this.setStartime((String)this.getFormHM().get("startime"));
		this.setEndtime((String)this.getFormHM().get("endtime"));
		this.setSpflag((String)this.getFormHM().get("spflag"));
		this.setCountflag((String)this.getFormHM().get("countflag"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setMultigroups((String)this.getFormHM().get("multigroups"));
		this.setLayerid((String)this.getFormHM().get("layerid"));
		this.setLayerlist((ArrayList)this.getFormHM().get("layerlist"));
		this.setLayerid2((String)this.getFormHM().get("layerid2"));
		this.setLayerlist2((ArrayList)this.getFormHM().get("layerlist2"));
		this.setGroupOrgCodeSet((String)this.getFormHM().get("groupOrgCodeSet"));
		this.setGroupOrgCodeSet2((String)this.getFormHM().get("groupOrgCodeSet2"));
		if(this.getFormHM().get("isRecords")!=null)
			this.setIsRecords((String)this.getFormHM().get("isRecords"));
		if(this.getFormHM().get("tableHeader")!=null)
		{
			this.setTableHeader((String)this.getFormHM().get("tableHeader"));
			this.setTableBody((String)this.getFormHM().get("tableBody"));
			this.setTableTitleTop((String)this.getFormHM().get("tableTitleTop"));
			this.setTableTitleBottom((String)this.getFormHM().get("tableTitleBottom"));
			this.setTurnPage((String)this.getFormHM().get("turnPage"));

		}
		
		if(this.getFormHM().get("groupPointList")!=null);
		{
			this.setGroupPointList((ArrayList)this.getFormHM().get("groupPointList"));
		} 
		if(this.getFormHM().get("groupPointList2")!=null)
		{
			this.setGroupPointList2((ArrayList)this.getFormHM().get("groupPointList2"));
		}
	    this.setSearchResultFlag((String)this.getFormHM().get("srnflag"));
	    this.setKqtable((String)this.getFormHM().get("kqtable"));
	    /** 哈药领导桌面 xiaoyun 2014-8-13 start */
	    this.setShowbuttons((String)this.getFormHM().get("showbuttons"));
	    this.setMainParamCondList((List)this.getFormHM().get("mainParamCondList"));
	    this.setMainParamTitle((String)this.getFormHM().get("mainParamTitle"));
	    this.setConditionBase((String)this.getFormHM().get("conditionBase"));
	    this.setLinktype((String)this.getFormHM().get("linktype"));
	    /** 哈药领导桌面 xiaoyun 2014-8-13 end */
	    
	    this.setNo_manager_priv((String)this.getFormHM().get("no_manager_priv"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("showPartJob", this.getShowPartJob());
		this.getFormHM().put("returnType", this.getReturnType());
		this.getFormHM().put("fromtable", this.getFromtable());
		this.getFormHM().put("isPrint", this.getIsPrint());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("conSQL",this.getConSQL());
		this.getFormHM().put("closeWindow", this.getCloseWindow());
		this.getFormHM().put("historyFlag", this.getHistoryFlag());
		this.getFormHM().put("dateStart", this.getDateStart());
		this.getFormHM().put("dateEnd", this.getDateEnd());
		this.getFormHM().put("combineField", this.getCombineField());
		this.getFormHM().put("combineFieldList", this.getCombineFieldList());
		this.getFormHM().put("filterByMdule", this.getFilterByMdule());
		this.getFormHM().put("isCloseButton", this.getIsCloseButton());
		this.getFormHM().put("divHeight", this.getDivHeight());
		this.getFormHM().put("divWidth", this.getDivWidth());
		this.getFormHM().put("isReData", this.getIsReData());
		/**由于人事异动的这个sql被放在userview中了,所以这里获取不到可能会影响前台数据**/
		//this.getFormHM().put("sql", this.getSql());这里不会向前台存放sql了
		if(this.getPhotoList()!=null)
			this.getFormHM().put("photoList",this.getPhotoList());
		if(this.getQueryScope()!=null)
			this.getFormHM().put("queryScope",this.getQueryScope());
		if(this.getHistory()!=null)
			this.getFormHM().put("history",this.getHistory());
		if(this.getYear()!=null)
			this.getFormHM().put("year",this.getYear());
		if(this.getMonth()!=null)
			this.getFormHM().put("month",this.getMonth());
		if(this.getCount()!=null)
			this.getFormHM().put("count",this.getCount());
		if(this.getTabID()!=null)
			this.getFormHM().put("tabID",this.getTabID());
		if(this.getFromScope()!=null)
			this.getFormHM().put("fromScopt",this.getFromScope());
		if(this.getToScope()!=null)
			this.getFormHM().put("toScope",this.getToScope());
		if(this.getSelectedPoint()!=null)
			this.getFormHM().put("selectedPoint",this.getSelectedPoint());
		if(this.getIsAutoCount()!=null)
			this.getFormHM().put("isAutoCount",this.getIsAutoCount());
		if(this.getPageRows()!=null)
			this.getFormHM().put("pageRows",this.getPageRows());
		if(this.getZeroPrint()!=null)
			this.getFormHM().put("zeroPrint",this.getZeroPrint());
		if(this.getEmptyRow()!=null)
			this.getFormHM().put("emptyRow",this.getEmptyRow());
		if(this.getColumn()!=null)
			this.getFormHM().put("column",this.getColumn());
		if(this.getDataarea()!=null)
			this.getFormHM().put("dataarea",this.getDataarea());
		if(this.getPix()!=null)
			this.getFormHM().put("pix",this.getPix());
		if(this.getColumnLine()!=null)
			this.getFormHM().put("columnLine",this.getColumnLine());
		if(this.getIsGroupPoint()!=null)
			this.getFormHM().put("isGroupPoint",this.getIsGroupPoint());
		if(this.getGroupPoint()!=null)
			this.getFormHM().put("groupPoint",this.getGroupPoint());
		if(this.getIsGroupPoint2()!=null)
			this.getFormHM().put("isGroupPoint2", this.getIsGroupPoint2());
		if(this.getGroupPoint2()!=null)
			this.getFormHM().put("groupPoint2", this.getGroupPoint2());
		if(this.getInfor_Flag()!=null)
			this.getFormHM().put("infor_Flag",this.getInfor_Flag());
        if(this.getModelFlag()!=null)
            this.getFormHM().put("modelFlag",this.getModelFlag());
		if(this.getDbpre()!=null)
			this.getFormHM().put("dbpre",this.getDbpre());
		if(this.getHistoryRecord()!=null)
			this.getFormHM().put("historyRecord",this.getHistoryRecord());
		if(this.getCurrpage()!=null)
		{
			this.getFormHM().put("currpage",this.getCurrpage());
		}
		if(this.getPrintGrid()!=null)
			this.getFormHM().put("printGrid",this.getPrintGrid());
		if(this.getStartime()!=null)
			this.getFormHM().put("startime",this.getStartime());
		if(this.getEndtime()!=null)
			this.getFormHM().put("endtime",this.getEndtime());
		if(this.getMultigroups()!=null)
			this.getFormHM().put("multigroups",this.getMultigroups());
		this.getFormHM().put("returnURL",this.getReturnURL());
		this.getFormHM().put("condition",this.getCondition());
		this.getFormHM().put("spflag",this.getSpflag());
		this.getFormHM().put("countflag",this.getCountflag());
		if(this.getSelecttime()!=null)
			this.getFormHM().put("selecttime",this.getSelecttime());
		
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(hm.get("clears")!=null&& "1".equals((String)hm.get("clears")))
		{
			this.getFormHM().put("isAutoCount","0");
			this.getFormHM().put("column","0");
			this.getFormHM().put("columnLine","0");
		}
		this.getFormHM().put("cardid", cardid);
		this.getFormHM().put("kqtable", this.getKqtable());
		this.getFormHM().put("dblist",this.getDblist());
		/** 哈药领导桌面 xiaoyun 2014-8-13 start */
		this.getFormHM().put("showbuttons", this.getShowbuttons());
		this.getFormHM().put("conditionBase", this.getConditionBase());
		this.getFormHM().put("linktype", this.getLinktype());
		//this.getFormHM().put("mainParamCondList", mainParamCondList);
		//this.getFormHM().put("mainParamTitle", mainParamTitle);
		/** 哈药领导桌面 xiaoyun 2014-8-13 end */
		this.getFormHM().put("no_manager_priv", this.getNo_manager_priv());
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
	    super.reset(mapping, request);
	}
	
	public String getSearchResultFlag() {
		return searchResultFlag;
	}

	public void setSearchResultFlag(String searchResultFlag) {
		this.searchResultFlag = searchResultFlag;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}

	

	
	public ArrayList getHmusterlist() {
		return hmusterlist;
	}

	public void setHmusterlist(ArrayList hmusterlist) {
		this.hmusterlist = hmusterlist;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getFromScope() {
		return fromScope;
	}

	public void setFromScope(String fromScope) {
		this.fromScope = fromScope;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getQueryScope() {
		return queryScope;
	}

	public void setQueryScope(String queryScope) {
		this.queryScope = queryScope;
	}

	public String getToScope() {
		return toScope;
	}

	public void setToScope(String toScope) {
		this.toScope = toScope;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTabID() {
		return tabID;
	}

	public void setTabID(String tabID) {
		this.tabID = tabID;
	}

	public String getSelectedPoint() {
		return selectedPoint;
	}

	public void setSelectedPoint(String selectedPoint) {
		this.selectedPoint = selectedPoint;
	}

	public ArrayList getSubPointList() {
		return subPointList;
	}

	public void setSubPointList(ArrayList subPointList) {
		this.subPointList = subPointList;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumnLine() {
		return columnLine;
	}

	public void setColumnLine(String columnLine) {
		this.columnLine = columnLine;
	}

	public String getEmptyRow() {
		return emptyRow;
	}

	public void setEmptyRow(String emptyRow) {
		this.emptyRow = emptyRow;
	}

	public String getGroupPoint() {
		return groupPoint;
	}

	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}

	public ArrayList getGroupPointList() {
		return groupPointList;
	}

	public void setGroupPointList(ArrayList groupPointList) {
		this.groupPointList = groupPointList;
	}

	public String getIsGroupPoint() {
		return isGroupPoint;
	}

	public void setIsGroupPoint(String isGroupPoint) {
		this.isGroupPoint = isGroupPoint;
	}

	public String getPageRows() {
		return pageRows;
	}

	public void setPageRows(String pageRows) {
		this.pageRows = pageRows;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public String getZeroPrint() {
		return zeroPrint;
	}

	public void setZeroPrint(String zeroPrint) {
		this.zeroPrint = zeroPrint;
	}
	
	public String getNo_manager_priv() {
		return no_manager_priv;
	}

	public void setNo_manager_priv(String no_manager_priv) {
		this.no_manager_priv = no_manager_priv;
	}

	public String getIsAutoCount() {
		return isAutoCount;
	}

	public void setIsAutoCount(String isAutoCount) {
		this.isAutoCount = isAutoCount;
	}

	public String getTableBody() {
		return tableBody;
	}

	public void setTableBody(String tableBody) {
		this.tableBody = tableBody;
	}

	public String getTableHeader() {
		return tableHeader;
	}

	public void setTableHeader(String tableHeader) {
		this.tableHeader = tableHeader;
	}

	public String getTableTitleBottom() {
		return tableTitleBottom;
	}

	public void setTableTitleBottom(String tableTitleBottom) {
		this.tableTitleBottom = tableTitleBottom;
	}

	public String getTableTitleTop() {
		return tableTitleTop;
	}

	public void setTableTitleTop(String tableTitleTop) {
		this.tableTitleTop = tableTitleTop;
	}

	public String getHistoryRecord() {
		return historyRecord;
	}

	public void setHistoryRecord(String historyRecord) {
		this.historyRecord = historyRecord;
	}

	public String getIsRecords() {
		return isRecords;
	}

	public void setIsRecords(String isRecords) {
		this.isRecords = isRecords;
	}

	public String getTurnPage() {
		return turnPage;
	}

	public void setTurnPage(String turnPage) {
		this.turnPage = turnPage;
	}

	public String getCurrpage() {
		return currpage;
	}

	public void setCurrpage(String currpage) {
		this.currpage = currpage;
	}

	public ArrayList getPhotoList() {
		return photoList;
	}

	public void setPhotoList(ArrayList photoList) {
		this.photoList = photoList;
	}

	public String getIsResultTable() {
		return isResultTable;
	}

	public void setIsResultTable(String isResultTable) {
		this.isResultTable = isResultTable;
	}

	public String getOperateMethod() {
		return operateMethod;
	}

	public void setOperateMethod(String operateMethod) {
		this.operateMethod = operateMethod;
	}

	public String getPrintGrid() {
		return printGrid;
	}

	public void setPrintGrid(String printGrid) {
		this.printGrid = printGrid;
	}


	public String getRelatTableid() {
		return relatTableid;
	}

	public void setRelatTableid(String relatTableid) {
		this.relatTableid = relatTableid;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getModelFlag() {
		return modelFlag;
	}

	public void setModelFlag(String modelFlag) {
		this.modelFlag = modelFlag;
	}

	

	public String getPaperRows() {
		return paperRows;
	}

	public void setPaperRows(String paperRows) {
		this.paperRows = paperRows;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public ArrayList getConditionslist() {
		return conditionslist;
	}

	public void setConditionslist(ArrayList conditionslist) {
		this.conditionslist = conditionslist;
	}

	public String getSelecttime() {
		return selecttime;
	}

	public void setSelecttime(String selecttime) {
		this.selecttime = selecttime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getMultigroups() {
		return multigroups;
	}

	public void setMultigroups(String multigroups) {
		this.multigroups = multigroups;
	}

	public String getSpflag() {
		return spflag;
	}

	public void setSpflag(String spflag) {
		this.spflag = spflag;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getLayerid() {
		return layerid;
	}

	/**
	 * 用于html option value值
	 * @return
	 */
	public String getLayeridValue() {
		if(groupOrgCodeSet.length()==0)
			return layerid;
		else
			return layerid+","+groupOrgCodeSet;
	}
	
	/**
	 * 用于html option value值
	 * @return
	 */
	public String getLayerid2Value() {
		if(groupOrgCodeSet2.length()==0)
			return layerid2;
		else
			return layerid2+","+groupOrgCodeSet2;
	}	
	
	public void setLayerid(String layerid) {
		this.layerid = layerid;
	}

	public ArrayList getLayerlist() {
		return layerlist;
	}

	public void setLayerlist(ArrayList layerlist) {
		this.layerlist = layerlist;
	}

	public String getDataarea() {
		return dataarea;
	}

	public void setDataarea(String dataarea) {
		this.dataarea = dataarea;
	}

	public String getCountflag() {
		return countflag;
	}

	public void setCountflag(String countflag) {
		this.countflag = countflag;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getIsReData() {
		return isReData;
	}

	public void setIsReData(String isReData) {
		this.isReData = isReData;
	}

	public String getDivHeight() {
		return divHeight;
	}

	public void setDivHeight(String divHeight) {
		this.divHeight = divHeight;
	}

	public String getDivWidth() {
		return divWidth;
	}

	public void setDivWidth(String divWidth) {
		this.divWidth = divWidth;
	}

	public String getIsCloseButton() {
		return isCloseButton;
	}

	public void setIsCloseButton(String isCloseButton) {
		this.isCloseButton = isCloseButton;
	}

	public String getFilterByMdule() {
		return filterByMdule;
	}

	public void setFilterByMdule(String filterByMdule) {
		this.filterByMdule = filterByMdule;
	}

	public String getCombineField() {
		return combineField;
	}

	public void setCombineField(String combineField) {
		this.combineField = combineField;
	}

	public ArrayList getCombineFieldList() {
		return combineFieldList;
	}

	public void setCombineFieldList(ArrayList combineFieldList) {
		this.combineFieldList = combineFieldList;
	}

	public String getHistoryFlag() {
		return historyFlag;
	}

	public void setHistoryFlag(String historyFlag) {
		this.historyFlag = historyFlag;
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getCloseWindow() {
		return closeWindow;
	}

	public void setCloseWindow(String closeWindow) {
		this.closeWindow = closeWindow;
	}

	public String getConSQL() {
		return conSQL;
	}

	public void setConSQL(String conSQL) {
		this.conSQL = conSQL;
	}

	public ArrayList getLayerlist2() {
		return layerlist2;
	}

	public void setLayerlist2(ArrayList layerlist2) {
		this.layerlist2 = layerlist2;
	}

	public String getLayerid2() {
		return layerid2;
	}

	public void setLayerid2(String layerid2) {
		this.layerid2 = layerid2;
	}

	public ArrayList getGroupPointList2() {
		return groupPointList2;
	}

	public void setGroupPointList2(ArrayList groupPointList2) {
		this.groupPointList2 = groupPointList2;
	}

	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}

	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}

	public String getGroupPoint2() {
		return groupPoint2;
	}

	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getKqtable() {
		return kqtable;
	}

	public void setKqtable(String kqtable) {
		this.kqtable = kqtable;
	}

	public String getIsPrint() {
		return isPrint;
	}

	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}

	public String getFromtable() {
		return fromtable;
	}

	public void setFromtable(String fromtable) {
		this.fromtable = fromtable;
	}
	
	public String getGroupOrgCodeSet() {
		return groupOrgCodeSet;
	}

	public void setGroupOrgCodeSet(String groupOrgCodeSet) {
		this.groupOrgCodeSet = groupOrgCodeSet;
	}	
	public String getGroupOrgCodeSet2() {
		return groupOrgCodeSet2;
	}

	public void setGroupOrgCodeSet2(String groupOrgCodeSet2) {
		this.groupOrgCodeSet2 = groupOrgCodeSet2;
	}

    public String getDbprelist() {
        dbprelist = "";
        if(dblist!=null){
            for(int i=0;i<dblist.size();i++){
                CommonData d=(CommonData)dblist.get(i);
                if(!"ALL".equals(d.getDataValue())){
                    if(dbprelist.length()>0)
                        dbprelist+="`";
                    dbprelist+=d.getDataValue();
                }
            }
        }
        return dbprelist;
    }
    
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    	if ("/general/muster/hmuster/select_muster_name".equals(mapping.getPath()) && request.getParameter("b_next2") != null) {
    	    if("1".equals(closeWindow) || "2".equals(closeWindow))
    	        request.setAttribute("targetWindow", "1");  // 异常提示信息中显示关闭按钮
    	    else
    	        request.setAttribute("targetWindow", "0");  // 由于经过processBar.jsp跳转，需要history.go(-2)才返回正确，因此去掉按钮
		}
    	return super.validate(mapping, request);
    }
    
    public String getMusterPreviewPluginParams() {
        String s = "<TABID>"+getTabID()+"</TABID>";
        s+="<RowCountMode>"+getIsAutoCount()+"</RowCountMode>";
        s+="<RowCount>"+getPageRows()+"</RowCount>"; 
        if("0".equals(getZeroPrint()))
            s+="<ShowZero>False</ShowZero>";
        else
            s+="<ShowZero>True</ShowZero>";
        if("0".equals(getPrintGrid()))
            s+="<ShowLines>False</ShowLines>";
        else
            s+="<ShowLines>True</ShowLines>";
        	//<!-- 取子集记录方式：0当前记录(默认值),1某次历史记录,2根据条件取历史记录 --> 33276
        if("3".equals(history)) {//打印预演插件，年月默认取当前业务日期。 取某次历史记录 传次数等条件信息
        	s+="<HistoryFlag>1</HistoryFlag>";
        	//<Year>年(默认为0)</Year><Month>月(默认为0)</Month><Times>次(默认为0)</Times>
        	s+="<Year>"+year+"</Year><Month>"+month+"</Month><Times>"+count+"</Times>";
        }
        
        if("5".equals(getModelFlag()))
            s+="<TemplateId>"+getRelatTableid()+"</TemplateId>";
        else
            s+="<NBASE>"+getDbpre()+"</NBASE>";
        s = new Des().EncryPwdStr(s);  // 加密
        s = s.replaceAll(System.getProperty("line.separator"), "");  //
        return s;
    }

}
