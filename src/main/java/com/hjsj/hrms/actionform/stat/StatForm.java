/*
 * Created on 2005-6-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.stat;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatForm extends FrameForm {


	private String strfactor;
	private String strlexpr;
	private String strsql;
	private String dbcond;
	private String order_by;
	private String infokind;
	private String isshowstatcond;
	private String home;
	private String isonetwo;
	private String istwostat;    //表示初始化显示时是一维得还是2维得
	private String preresult;    //表示1。上次查询结果。2常用查询
	private String curr_id[];  //当前的常用查询id
	private String history;  //历史纪录
	/**常用条件列表*/
    private String default_stat_id;
	private ArrayList statlist=new ArrayList();
    private ArrayList condlist=new ArrayList();
    
    /**主集选中的字段值对列表*/
    private ArrayList fieldlist=new ArrayList();  
    private String columns="";
    private String distinct="";
    /**浏览信息所用的卡片号*/
    private String tabid="-1";  
	private String uplevel;
	
	/****历史数据***/
	private String cyc_Sdate="";//开始周期时间
	private String cyc_Edate="";//结束周期时间
	private String graph_style="1";//图形样式
	private String reportHtml="";
	private String acode="";
	private String code="";
	private String kind="";
	private String orgs="";
	private ArrayList jfreelist=new ArrayList();
	private HashMap  jfreemap=new HashMap();
	private ArrayList graph_list=new ArrayList(); 
	private String chartTitle;
	private String chartType;
	private String chartWidth;
	private String chartHeight;
	private String init;
	private String cyc_year="";
	private String cyc_year_e="";
	private String cyc_moth="";
	private ArrayList yylist=new ArrayList();
	private ArrayList mmlist=new ArrayList();
	private String archive_type="";
	private String showflag="";
	private String showLegend="";
	private ArrayList orderlist=new ArrayList();
	private String returnvalue;
	private String archive_set;
	private String moreun="";
	private String userbases;
	private String viewuserbases;
	private String categories;
	private String extjsitems;
	private HashMap activetab=new HashMap();
	private String sformula;
	private ArrayList sformulalist = new ArrayList();
	private String showsformula;
	private String decimalwidth;
	/** x轴旋转角度(增加注释) xiaoyun 2014-7-8 */
	private String xangle; 
	
	private String isneedsum;
	private String label_enabled="true";
	private String vtotal="0";
	private String htotal="0";
	
	private String notem="0";
	
	/* 组织机构是否可选开关（领导桌面）xiaoyun 2014-5-15 start */
	// 如果配置项中没有设置，则设置为可见（跟原来保持一致）
	private String isHideBiPanelOrg = "false";
	/* 组织机构是否可选开关（领导桌面）xiaoyun 2014-5-15 end */
	
	/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 start */
	/** x轴说明总字数 xiaoyun 2014-7-9 start */
	private String total;
	//仪表盘、温度计用参数 begin
	/**状态良好值*/
	private ArrayList minvalue = new ArrayList();
	/**状态正常值*/
	private ArrayList maxvalue = new ArrayList();
	/**预警值*/
	private ArrayList valves = new ArrayList();
	/**当前值*/
	private ArrayList cvalues = new ArrayList();
	/**当前值集合*/
	private ArrayList cvaluelist = new ArrayList();
	
	private String subtitle;
	
	private String onlychart;
	private String fromwhere;
	private String filterId; //筛选机构id
	private String filterName; //筛选机构名称
	private String org_filter; //机构筛选  =1 是 =0否
	public String getFromwhere() {
        return fromwhere;
    }
    public void setFromwhere(String fromwhere) {
        this.fromwhere = fromwhere;
    }
    //仪表盘、温度计用参数 end
	//总裁桌面需要联动的页面  =pcw： 人均工资同期比较 |=rate：投资回报率 |=pcle：人均劳效比较
	private String page;
	
	public String getOnlychart() {
		return onlychart;
	}
	public void setOnlychart(String onlychart) {
		this.onlychart = onlychart;
	}
	public ArrayList getCvaluelist() {
		return cvaluelist;
	}
	public void setCvaluelist(ArrayList cvaluelist) {
		this.cvaluelist = cvaluelist;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 end */
	
	
	public ArrayList getOrderlist() {
		return orderlist;
	}
	public void setOrderlist(ArrayList orderlist) {
		this.orderlist = orderlist;
	}
	public String getCyc_year() {
		return cyc_year;
	}
	public void setCyc_year(String cyc_year) {
		this.cyc_year = cyc_year;
	}
	public ArrayList getYylist() {
		return yylist;
	}
	public void setYylist(ArrayList yylist) {
		this.yylist = yylist;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
	public String getChartTitle() {
		return chartTitle;
	}
	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	public String getCyc_Sdate() {
		return cyc_Sdate;
	}
	public void setCyc_Sdate(String cyc_Sdate) {
		this.cyc_Sdate = cyc_Sdate;
	}
	public String getCyc_Edate() {
		return cyc_Edate;
	}
	public void setCyc_Edate(String cyc_Edate) {
		this.cyc_Edate = cyc_Edate;
	}
	public String getGraph_style() {
		return graph_style;
	}
	public void setGraph_style(String graph_style) {
		this.graph_style = graph_style;
	}
	public ArrayList getGraph_list() {
		return graph_list;
	}
	public void setGraph_list(ArrayList graph_list) {
		this.graph_list = graph_list;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	/**
	 * @return Returns the order_by.
	 */
	public String getOrder_by() {
		return order_by;
	}
	/**
	 * @param order_by The order_by to set.
	 */
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	/**
	 * @return Returns the dbcond.
	 */
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond The dbcond to set.
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	/**
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}
	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	/**
	 * @return Returns the strlexpr.
	 */
	public String getStrlexpr() {
		return strlexpr;
	}
	/**
	 * @param strlexpr The strlexpr to set.
	 */
	public void setStrlexpr(String strlexpr) {
		this.strlexpr = strlexpr;
	}
	/**
	 * @return Returns the strFactor.
	 */
	public String getStrfactor() {
		return strfactor;
	}
	/**
	 * @param strFactor The strFactor to set.
	 */
	public void setStrfactor(String strfactor) {
		this.strfactor = strfactor;
	}
	private String userbase="";
	private String querycond="";
	private ArrayList dblist=new ArrayList();
	private ArrayList nbaselist=new ArrayList();
	private String orgName="";
	private String statid="1";
	private String lexprId="";
	private String snamedisplay;
    private String setname;
	private String a0100;
	private String treeCode;
	private String stattreeCode;
	private String chart_type="12";
	private int[][] statdoublevalues;
	private double[][] statdoublevaluess;
	private List varraylist=new ArrayList();
	private List harraylist=new ArrayList();
	private int v;
	private int h;
	private String flag;
	private String totalvalue;
	private String result;
	private String isoneortwo;
	
	private String commlexr = "";
	private String commfacor = "";
	
	private String crossshow = "";//控制显示多维统计图树  liuy 2014-12-6
	
	public String getCrossshow() {
		return crossshow;
	}
	public void setCrossshow(String crossshow) {
		this.crossshow = crossshow;
	}
	public String getCommlexr() {
		return commlexr;
	}
	public void setCommlexr(String commlexr) {
		this.commlexr = commlexr;
	}
	public String getCommfacor() {
		return commfacor;
	}
	public void setCommfacor(String commfacor) {
		this.commfacor = commfacor;
	}
	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return Returns the h.
	 */
	public int getH() {
		return h;
	}
	/**
	 * @param h The h to set.
	 */
	public void setH(int h) {
		this.h = h;
	}
	/**
	 * @return Returns the v.
	 */
	public int getV() {
		return v;
	}
	/**
	 * @param v The v to set.
	 */
	public void setV(int v) {
		this.v = v;
	}
	/**
	 * @return Returns the harraylist.
	 */
	public List getHarraylist() {
		return harraylist;
	}
	/**
	 * @param harraylist The harraylist to set.
	 */
	public void setHarraylist(List harraylist) {
		this.harraylist = harraylist;
	}
	/**
	 * @return Returns the varraylist.
	 */
	public List getVarraylist() {
		return varraylist;
	}
	/**
	 * @param varraylist The varraylist to set.
	 */
	public void setVarraylist(List varraylist) {
		this.varraylist = varraylist;
	}
	/**
	 * @return Returns the chart_type.
	 */
	public String getChart_type() {
		return chart_type;
	}
	/**
	 * @param chart_type The chart_type to set.
	 */
	public void setChart_type(String chart_type) {
		chart_type=chart_type!=null&&chart_type.length()>0?chart_type:"12";
		this.chart_type = chart_type;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
    ArrayList infodetailfieldlist=new ArrayList();
    ArrayList infofieldlist=new ArrayList();
    ArrayList infosetlist=new ArrayList();
    private String photoname;
	/**
	 * @return Returns the infosetlist.
	 */
	public ArrayList getInfosetlist() {
		return infosetlist;
	}
	/**
	 * @param infosetlist The infosetlist to set.
	 */
	public void setInfosetlist(ArrayList infosetlist) {
		this.infosetlist = infosetlist;
	}
	/**
	 * @return Returns the photoname.
	 */
	public String getPhotoname() {
		return photoname;
	}
	/**
	 * @param photoname The photoname to set.
	 */
	public void setPhotoname(String photoname) {
		this.photoname = photoname;
	}
	/**
	 * @return Returns the setname.
	 */
	public String getSetname() {
		return setname;
	}
	/**
	 * @param setname The setname to set.
	 */
	public void setSetname(String setname) {
		this.setname = setname;
	}
	 /**
     * 查询条件串
     */
    private String cond_str="";

    /**
     * 角色对象列表
     */

    private PaginationForm selfInfoForm=new PaginationForm();    
	public StatForm(){
	  super();
	}
	 private ArrayList list=new ArrayList();

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
	     this.setFromwhere((String)this.getFormHM().get("fromwhere"));
		 this.setNotem((String)this.getFormHM().get("notem"));
		 this.setSubtitle((String)this.getFormHM().get("subtitle"));
		 this.setOnlychart((String)this.getFormHM().get("onlychart"));
		 this.setList((ArrayList)this.getFormHM().get("list"));
		 this.setQuerycond((String)this.getFormHM().get("querycond"));
		 this.setUserbase((String)this.getFormHM().get("userbase"));
		 this.setStatid((String)this.getFormHM().get("statid"));
		 this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
	     this.setCond_str((String)this.getFormHM().get("cond_str"));
		 this.getSelfInfoForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
		 this.setA0100((String)this.getFormHM().get("a0100"));
		 this.setInfodetailfieldlist((ArrayList)this.getFormHM().get("infodetailfieldlist"));
	     this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
	     this.setInfosetlist((ArrayList)this.getFormHM().get("infosetlist"));
	 	 this.setTreeCode((String)this.getFormHM().get("treeCode"));
	 	 this.setStrsql((String)this.getFormHM().get("strsql"));
	 	 this.setStatdoublevalues((int[][])this.getFormHM().get("statdoublevalues"));
	 	 this.setStatdoublevaluess((double[][])this.getFormHM().get("statdoublevaluess"));
	 	 this.setVarraylist((List)this.getFormHM().get("varraylist"));
	 	 this.setHarraylist((List)this.getFormHM().get("harraylist"));
         this.setTotalvalue((String)this.getFormHM().get("totalvalue"));
         this.setDbcond((String)this.getFormHM().get("dbcond"));
         this.setOrder_by((String)this.getFormHM().get("order_by"));
         this.setInfokind((String)this.getFormHM().get("infokind"));
         this.setIsshowstatcond((String)this.getFormHM().get("isshowstatcond"));
         this.setIsonetwo((String)this.getFormHM().get("isonetwo"));
         this.setIstwostat((String)this.getFormHM().get("istwostat"));
         this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
         /**主集指定的结果指标,chenmengqing added at 20070712*/
         this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
         this.setColumns((String)this.getFormHM().get("columns"));
         this.setTabid((String)this.getFormHM().get("tabid"));   
         this.setDefault_stat_id((String)this.getFormHM().get("default_stat_id"));
         this.setStatlist((ArrayList)this.getFormHM().get("statlist"));
         this.setStattreeCode((String)this.getFormHM().get("stattreeCode"));
         this.setHome((String)this.getFormHM().get("home"));
         this.setUplevel((String)this.getFormHM().get("uplevel"));
         /***2009.12.23 sunx add**/
         this.setPreresult((String)this.getFormHM().get("preresult"));
         this.setResult((String)this.getFormHM().get("result"));
         /**历史数据***/
         this.setCyc_Sdate((String)this.getFormHM().get("cyc_Sdate"));
         this.setCyc_Edate((String)this.getFormHM().get("cyc_Edate"));
         this.setGraph_list((ArrayList)this.getFormHM().get("graph_list"));
         this.setReportHtml((String)this.getFormHM().get("reportHtml"));
         this.setAcode((String)this.getFormHM().get("acode"));
         this.setJfreelist((ArrayList)this.getFormHM().get("jfreelist"));
         this.setJfreemap((HashMap)this.getFormHM().get("jfreemap"));
         this.setChartTitle((String)this.getFormHM().get("chartTitle"));
         this.setChartType((String)this.getFormHM().get("chartType"));
         this.setChartHeight((String)this.getFormHM().get("chartHeight"));
         this.setChartWidth((String)this.getFormHM().get("chartWidth"));
         this.setInit((String)this.getFormHM().get("init"));
         this.setYylist((ArrayList)this.getFormHM().get("yylist"));
         this.setCyc_year((String)this.getFormHM().get("cyc_year"));
         this.setCyc_year_e((String)this.getFormHM().get("cyc_year_e"));
         this.setArchive_type((String)this.getFormHM().get("archive_type"));         
         this.setOrderlist((ArrayList)this.getFormHM().get("orderlist"));
         //显示类型1:通过点击图形传送的数据 传送的是 统计项名称（汉字）;0 or null 是点击树形节点现实的 传送的是id
         this.setShowflag((String)this.getFormHM().get("showflag"));
         //常用统计id
         this.setLexprId((String)this.getFormHM().get("lexprId"));
         this.setNbaselist((ArrayList)this.getFormHM().get("nbaselist"));
         this.setChart_type((String)this.getFormHM().get("chart_type"));	
         this.setArchive_set((String)this.getFormHM().get("archive_set"));
         this.setMoreun((String)this.getFormHM().get("moreun"));//多个部门时只能选择一个时间段
         this.setCyc_moth((String)this.getFormHM().get("cyc_moth"));
         this.setDistinct((String)this.getFormHM().get("distinct"));
         this.setUserbases((String)this.getFormHM().get("userbases"));
         this.setViewuserbases((String)this.getFormHM().get("viewuserbases"));
         this.setExtjsitems((String)this.getFormHM().get("extjsitems"));
         this.setSformulalist((ArrayList)this.getFormHM().get("sformulalist"));
         this.setShowsformula((String)this.getFormHM().get("showsformula"));
         this.setSformula((String)this.getFormHM().get("sformula"));
         this.setDecimalwidth((String)this.getFormHM().get("decimalwidth"));
         this.setXangle((String)this.getFormHM().get("xangle"));
         this.setOrgName((String)this.getFormHM().get("orgName"));
         this.setIsneedsum((String)this.getFormHM().get("isneedsum"));
         if(this.getFormHM().get("label_enabled")!=null&&this.getFormHM().get("label_enabled").toString().length()>0)
        	 this.setLabel_enabled((String)this.getFormHM().get("label_enabled"));
         this.setVtotal((String)this.getFormHM().get("vtotal"));
         this.setHtotal((String)this.getFormHM().get("htotal"));
         /* 组织机构是否可选开关（领导桌面）xiaoyun 2014-5-15 start */
         this.setIsHideBiPanelOrg((String)this.getFormHM().get("isHideBiPanelOrg"));
         /* 组织机构是否可选开关（领导桌面）xiaoyun 2014-5-15 end */
         /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 start */
         this.setTotal((String)this.getFormHM().get("total"));
         /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 end */
         this.setMinvalue((ArrayList)this.getFormHM().get("minvalue"));
         this.setMaxvalue((ArrayList)this.getFormHM().get("maxvalue"));
         this.setValves((ArrayList)this.getFormHM().get("valves"));
         this.setCvalues((ArrayList)this.getFormHM().get("cvalues"));
         this.setCvaluelist((ArrayList)this.getFormHM().get("cvaluelist"));
         this.setCrossshow((String)this.getFormHM().get("crossshow"));
         this.setFilterId((String)this.getFormHM().get("filterId"));
         this.setFilterName((String)this.getFormHM().get("filterName"));
         this.setOrg_filter((String)this.getFormHM().get("org_filter"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
	    this.getFormHM().put("fromwhere",fromwhere);
		this.getFormHM().put("notem",notem);
		this.getFormHM().put("subtitle",subtitle);
		this.getFormHM().put("onlychart",onlychart);
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("querycond",this.getQuerycond());
		this.getFormHM().put("statid",statid);	
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("strfactor",strfactor);
		this.getFormHM().put("strlexpr",strlexpr);
		this.getFormHM().put("v",String.valueOf(v));
		this.getFormHM().put("h",String.valueOf(h));
		this.getFormHM().put("infokind",infokind);
		this.getFormHM().put("result",result);
		this.getFormHM().put("curr_id",curr_id);
		this.getFormHM().put("history",history);
		this.getFormHM().put("preresult",preresult);
		this.getFormHM().put("default_stat_id",this.getDefault_stat_id());
		this.getFormHM().put("home", this.home);
		this.getFormHM().put("isshowstatcond", this.isshowstatcond);
		this.getFormHM().put("cyc_Sdate", this.getCyc_Sdate());
		this.getFormHM().put("cyc_Edate",this.getCyc_Edate());
		this.getFormHM().put("graph_style", this.getGraph_style());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("kind", this.getKind());
		this.getFormHM().put("acode", this.getAcode());
		this.getFormHM().put("orgs", this.getOrgs());
		this.getFormHM().put("cyc_year", this.cyc_year);
		this.getFormHM().put("cyc_year_e", this.getCyc_year_e());
		this.getFormHM().put("cyc_moth", this.getCyc_moth());
		// //显示类型1:通过点击图形传送的数据 传送的是 统计项名称（汉字）;0 or null 是点击树形节点现实的 传送的是id
		this.getFormHM().put("showflag", this.getShowflag());
		this.getFormHM().put("showLegend", this.getShowLegend());
		this.getFormHM().put("lexprId", this.getLexprId());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("chart_type", this.getChart_type());
		this.getFormHM().put("userbases", userbases);
		this.getFormHM().put("viewuserbases", viewuserbases);
		this.getFormHM().put("categories", categories);
		this.getFormHM().put("activetab", activetab);
		this.getFormHM().put("sformula", sformula);
		this.getFormHM().put("orgName", orgName);
		this.getFormHM().put("label_enabled", label_enabled);
		this.getFormHM().put("vtotal",this.vtotal);
		this.getFormHM().put("htotal", htotal);
		/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 start */
		this.getFormHM().put("total", this.total);
		/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-8 end */
		this.getFormHM().put("minvalue", this.getMinvalue());
		this.getFormHM().put("maxvalue", this.getMaxvalue());
		this.getFormHM().put("valves", this.getValves());
		this.getFormHM().put("cvalues", this.getCvalues());
		this.getFormHM().put("cvaluelist", this.getCvaluelist());
		// zgd 2014-8-13 二维统计点击数字穿透传入人员范围（分类） start
		this.getFormHM().put("commlexr", commlexr);
		this.getFormHM().put("commfacor", commfacor);
		// zgd 2014-8-13 二维统计点击数字穿透传入人员范围（分类） end
		this.getFormHM().put("crossshow", this.getCrossshow());
		this.getFormHM().put("filterId", this.getFilterId());
		this.getFormHM().put("filterName", this.getFilterName());
		this.getFormHM().put("org_filter", this.getOrg_filter());
	}

	/**
	 * @return Returns the list.
	 */
	public ArrayList getList() {
		return list;
	}
	/**
	 * @param list The list to set.
	 */
	public void setList(ArrayList list) {
		this.list = list;
	}
	/**
	 * @return Returns the querycond.
	 */
	public String getQuerycond() {
		return querycond;
	}
	/**
	 * @param querycond The querycond to set.
	 */
	public void setQuerycond(String querycond) {
		this.querycond = querycond;
	}
	/**
	 * @return Returns the statid.
	 */
	public String getStatid() {
		return statid;
	}
	/**
	 * @param statid The statid to set.
	 */
	public void setStatid(String statid) {
		this.statid = statid;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	/**
	 * @return Returns the snamedisplay.
	 */
	public String getSnamedisplay() {
		return snamedisplay;
	}
	/**
	 * @param snamedisplay The snamedisplay to set.
	 */
	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}
	/**
	 * @return Returns the cond_str.
	 */
	public String getCond_str() {
		return cond_str;
	}
	/**
	 * @param cond_str The cond_str to set.
	 */
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}	
	/**
	 * @return Returns the infodetailfieldlist.
	 */
	public ArrayList getInfodetailfieldlist() {
		return infodetailfieldlist;
	}
	/**
	 * @param infodetailfieldlist The infodetailfieldlist to set.
	 */
	public void setInfodetailfieldlist(ArrayList infodetailfieldlist) {
		this.infodetailfieldlist = infodetailfieldlist;
	}
	/**
	 * @return Returns the infofieldlist.
	 */
	public ArrayList getInfofieldlist() {
		return infofieldlist;
	}
	/**
	 * @param infofieldlist The infofieldlist to set.
	 */
	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}
	/**
	 * @return Returns the selfInfoForm.
	 */
	public PaginationForm getSelfInfoForm() {
		return selfInfoForm;
	}
	/**
	 * @param selfInfoForm The selfInfoForm to set.
	 */
	public void setSelfInfoForm(PaginationForm selfInfoForm) {
		this.selfInfoForm = selfInfoForm;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	

	/**
	 * @return Returns the statdoublevalues.
	 */
	public int[][] getStatdoublevalues() {
		return statdoublevalues;
	}
	/**
	 * @param statdoublevalues The statdoublevalues to set.
	 */
	public void setStatdoublevalues(int[][] statdoublevalues) {
		this.statdoublevalues = statdoublevalues;
	}
	
	
	/**
	 * @return Returns the totalvalue.
	 */
	public String getTotalvalue() {
		return totalvalue;
	}
	
	
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		//liuy 2015-1-14 6701：常用统计，各部门人员统计，统计项有20多项，统计图上不出来数字，但是点进去后再返回，图上方的数字就可以显示出来了。start
		//this.label_enabled="true";
		//liuy 2015-1-14 end
		this.setCategories("");
		this.setCrossshow("");
	}
	/**
	 * @param totalvalue The totalvalue to set.
	 */
	public void setTotalvalue(String totalvalue) {
		this.totalvalue = totalvalue;
	}
	//liuy 2015-2-28 7725:领导桌面-机构弹框选择后点击确认后，页面的文本框中没有把所选择的机构显示出来 start 
	@Override
    public void reset(ActionMapping mapping, ServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		if("/general/static/commonstatic/statshowmsgchart".equals(mapping.getPath())&&request.getParameter("b_msgchart")!=null)
		{
			this.getFormHM().put("querycond", "");
			this.getFormHM().put("lexprId","");    
			this.setQuerycond("");
			this.setLexprId("");
			this.getFormHM().put("orgName", "");
			this.setOrgName("");    	     
			this.setChart_type(request.getParameter("chart_type"));
		}
	}
	//liuy 2015-2-28 end
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
        if("/workbench/stat/statshow".equals(arg0.getPath())&&arg1.getParameter("b_data")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        if("/general/static/commonstatic/statshow".equals(arg0.getPath())&&arg1.getParameter("b_data")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        if("/general/static/commonstatic/statshow".equals(arg0.getPath())&&arg1.getParameter("b_double")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        if("/general/static/commonstatic/statshow".equals(arg0.getPath())&&arg1.getParameter("b_chart")!=null)
        {
        	
     	
        }
        if("/general/static/history/statshow".equals(arg0.getPath())&&arg1.getParameter("b_tree")!=null)
        {
        	this.setAcode("");
     	    this.getFormHM().put("acode", "");
        }
        if("/general/static/history/searchstaticdata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
        	this.setCyc_Edate("");
        	this.setCyc_Sdate("");
        	this.setCyc_year("");
        	this.setCyc_year_e("");
        	this.getFormHM().put("cyc_Sdate", "");
			this.getFormHM().put("cyc_Edate", "");
			this.getFormHM().put("cyc_year", "");
			this.getFormHM().put("cyc_year_e", "");
        }
        if("/workbench/stat/statshow".equals(arg0.getPath())&&arg1.getParameter("b_ini")!=null)
        {
        	this.getFormHM().put("curr_id",null);
     	    this.getFormHM().put("preresult","");     	   
     	    this.setCurr_id(null);
     	    this.setPreresult("");
     	    this.setHome("");
     	    this.setDefault_stat_id("");
     	    this.getFormHM().put("home", "");     	   
        }
        if("/general/static/commonstatic/statshow".equals(arg0.getPath())&&arg1.getParameter("b_ini")!=null)
        {
        	 this.getFormHM().put("isshowstatcond", "");
        	 arg1.removeAttribute("isshowstatcond");
      	     this.setIsshowstatcond("");
      	     this.setCurr_id(null);
    	     this.setPreresult("");
    	     this.setDefault_stat_id("");
    	     this.getFormHM().put("preresult","");    
    	     this.getFormHM().put("curr_id",null);
    	     this.getFormHM().put("default_stat_id", "");
    	     this.setResult("");
    	     this.getFormHM().put("result", "");
    	     this.setLexprId("");//liuy 2015-7-7 10828：员工管理-常用统计-学历分布（显示条数与反查条数不一致问题）
        }
        if("/general/static/commonstatic/statshow".equals(arg0.getPath())&&arg1.getParameter("b_msgchart")!=null)
        {
        	 this.getFormHM().put("querycond", "");
        	 this.getFormHM().put("lexprId","");    
    	     this.setQuerycond("");
    	     this.setLexprId("");
    	     this.getFormHM().put("orgName", "");
    	     this.setOrgName("");
        }
        /*常用条件范围，设置上就左边一直用它做过滤条件,可考虑再加一个全部人员的统计条件
        if(arg0.getPath().equals("/workbench/stat/statshow")&&(arg1.getParameter("b_doubledata")!=null||arg1.getParameter("b_chart")!=null))
        {
        	this.setCurr_id(new String[0]);
        }*/
        return super.validate(arg0, arg1);
    }
	/**
	 * @return Returns the infokind.
	 */
	public String getInfokind() {
		return infokind;
	}
	/**
	 * @param infokind The infokind to set.
	 */
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	public String getIsshowstatcond() {
		return isshowstatcond;
	}
	public void setIsshowstatcond(String isshowstatcond) {
		this.isshowstatcond = isshowstatcond;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getIsonetwo() {
		return isonetwo;
	}
	public void setIsonetwo(String isonetwo) {
		this.isonetwo = isonetwo;
	}
	public String getIstwostat() {
		return istwostat;
	}
	public void setIstwostat(String istwostat) {
		this.istwostat = istwostat;
	}
	public String getIsoneortwo() {
		return isoneortwo;
	}
	public void setIsoneortwo(String isoneortwo) {
		this.isoneortwo = isoneortwo;
	}
	public String getPreresult() {
		return preresult;
	}
	public void setPreresult(String preresult) {
		this.preresult = preresult;
	}
	public String[] getCurr_id() {
		return curr_id;
	}
	public void setCurr_id(String[] curr_id) {
		this.curr_id = curr_id;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public ArrayList getCondlist() {
		return condlist;
	}
	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	
	public ArrayList getStatlist() {
		return statlist;
	}
	public void setStatlist(ArrayList statlist) {
		this.statlist = statlist;
	}
	public String getDefault_stat_id() {
		return default_stat_id;
	}
	public void setDefault_stat_id(String default_stat_id) {
		this.default_stat_id = default_stat_id;
	}
	public String getStattreeCode() {
		return stattreeCode;
	}
	public void setStattreeCode(String stattreeCode) {
		this.stattreeCode = stattreeCode;
	}
	public String getReportHtml() {
		return reportHtml;
	}
	public void setReportHtml(String reportHtml) {
		this.reportHtml = reportHtml;
	}
	public String getAcode() {
		return acode;
	}
	public void setAcode(String acode) {
		this.acode = acode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getOrgs() {
		return orgs;
	}
	public void setOrgs(String orgs) {
		this.orgs = orgs;
	}
	public ArrayList getJfreelist() {
		return jfreelist;
	}
	public void setJfreelist(ArrayList jfreelist) {
		this.jfreelist = jfreelist;
	}
	public HashMap getJfreemap() {
		return jfreemap;
	}
	public void setJfreemap(HashMap jfreemap) {
		this.jfreemap = jfreemap;
	}
	public String getChartWidth() {
		return chartWidth;
	}
	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}
	public String getChartHeight() {
		return chartHeight;
	}
	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}
	public String getArchive_type() {
		return archive_type;
	}
	public void setArchive_type(String archive_type) {
		this.archive_type = archive_type;
	}
	public String getCyc_year_e() {
		return cyc_year_e;
	}
	public void setCyc_year_e(String cyc_year_e) {
		this.cyc_year_e = cyc_year_e;
	}
	public String getShowflag() {
		return showflag;
	}
	public void setShowflag(String showflag) {
		this.showflag = showflag;
	}
	public String getShowLegend() {
		return showLegend;
	}
	public void setShowLegend(String showLegend) {
		this.showLegend = showLegend;
	}
	public String getLexprId() {
		return lexprId;
	}
	public void setLexprId(String lexprId) {
		this.lexprId = lexprId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public ArrayList getNbaselist() {
		return nbaselist;
	}
	public void setNbaselist(ArrayList nbaselist) {
		this.nbaselist = nbaselist;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getArchive_set() {
		return archive_set;
	}
	public void setArchive_set(String archive_set) {
		this.archive_set = archive_set;
	}
	public String getMoreun() {
		return moreun;
	}
	public void setMoreun(String moreun) {
		this.moreun = moreun;
	}
	public String getCyc_moth() {
		return cyc_moth;
	}
	public void setCyc_moth(String cyc_moth) {
		this.cyc_moth = cyc_moth;
	}
	public ArrayList getMmlist() {
		mmlist.clear();
		CommonData da=new CommonData("1","1");
		mmlist.add(da);
		da=new CommonData("2","2");
		mmlist.add(da);
		da=new CommonData("3","3");
		mmlist.add(da);
		da=new CommonData("4","4");
		mmlist.add(da);
		da=new CommonData("5","5");
		mmlist.add(da);
		da=new CommonData("6","6");
		mmlist.add(da);
		da=new CommonData("7","7");
		mmlist.add(da);
		da=new CommonData("8","8");
		mmlist.add(da);
		da=new CommonData("9","9");
		mmlist.add(da);
		da=new CommonData("10","10");
		mmlist.add(da);
		da=new CommonData("11","11");
		mmlist.add(da);
		da=new CommonData("12","12");
		mmlist.add(da);
		return mmlist;
	}
	public void setMmlist(ArrayList mmlist) {
		this.mmlist = mmlist;
	}
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}
	public String getUserbases() {
		return userbases;
	}
	public void setUserbases(String userbases) {
		this.userbases = userbases;
	}
	public String getViewuserbases() {
		return viewuserbases;
	}
	public void setViewuserbases(String viewuserbases) {
		this.viewuserbases = viewuserbases;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public String getExtjsitems() {
		return extjsitems;
	}
	public void setExtjsitems(String extjsitems) {
		this.extjsitems = extjsitems;
	}
	public HashMap getActivetab() {
		return activetab;
	}
	public void setActivetab(HashMap activetab) {
		this.activetab = activetab;
	}
	public String getSformula() {
		return sformula;
	}
	public void setSformula(String sformula) {
		this.sformula = sformula;
	}
	public ArrayList getSformulalist() {
		return sformulalist;
	}
	public void setSformulalist(ArrayList sformulalist) {
		this.sformulalist = sformulalist;
	}
	public String getShowsformula() {
		return showsformula;
	}
	public void setShowsformula(String showsformula) {
		this.showsformula = showsformula;
	}
	public double[][] getStatdoublevaluess() {
		return statdoublevaluess;
	}
	public void setStatdoublevaluess(double[][] statdoublevaluess) {
		this.statdoublevaluess = statdoublevaluess;
	}
	public String getDecimalwidth() {
		return decimalwidth;
	}
	public void setDecimalwidth(String decimalwidth) {
		this.decimalwidth = decimalwidth;
	}
	public String getXangle() {
		return xangle;
	}
	public void setXangle(String xangle) {
		this.xangle = xangle;
	}
	public String getIsneedsum() {
		return isneedsum;
	}
	public void setIsneedsum(String isneedsum) {
		this.isneedsum = isneedsum;
	}
	public String getLabel_enabled() {
		return label_enabled;
	}
	public void setLabel_enabled(String label_enabled) {
		this.label_enabled = label_enabled;
	}
	public String getVtotal() {
		return vtotal;
	}
	public void setVtotal(String vtotal) {
		this.vtotal = vtotal;
	}
	public String getHtotal() {
		return htotal;
	}
	public void setHtotal(String htotal) {
		this.htotal = htotal;
	}
	public String getIsHideBiPanelOrg() {
		return isHideBiPanelOrg;
	}
	public void setIsHideBiPanelOrg(String isHideBiPanelOrg) {
		this.isHideBiPanelOrg = isHideBiPanelOrg;
	}
	public ArrayList getMinvalue() {
		return minvalue;
	}
	public void setMinvalue(ArrayList minvalue) {
		this.minvalue = minvalue;
	}
	public ArrayList getMaxvalue() {
		return maxvalue;
	}
	public void setMaxvalue(ArrayList maxvalue) {
		this.maxvalue = maxvalue;
	}
	public ArrayList getCvalues() {
		return cvalues;
	}
	public void setCvalues(ArrayList cvalues) {
		this.cvalues = cvalues;
	}
	public ArrayList getValves() {
		return valves;
	}
	public void setValves(ArrayList valves) {
		this.valves = valves;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getNotem() {
		return notem;
	}
	public void setNotem(String notem) {
		this.notem = notem;
	}
	public String getFilterId() {
		return filterId;
	}
	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getOrg_filter() {
		return org_filter;
	}
	public void setOrg_filter(String org_filter) {
		this.org_filter = org_filter;
	}
	

}
