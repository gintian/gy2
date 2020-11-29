package com.hjsj.hrms.actionform.general.statics;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * 
 * Title:
 * Description:
 * Company:hjsj
 * Create time:Aug 15, 2014:3:21:55 PM
 * @author zhaogd
 * @version 6.x
 */
public class CrossStaticForm extends FrameForm{

	private String type;
	private HashMap options = new HashMap();
	
	/** 界面选项根据type值分别保存 */
	private class Option {
	    private String showdbname;
	    //二维交叉表指定链接参数
	    private String stat;
	    private String dbname;
	    private String userbases;//已选人员库前缀
	    private String viewuserbases;//已选人员库名称	    
	    private ArrayList dblist=new ArrayList();//权限范围了的所有人员库
	    private String showcond="1";
	    private String cond;
	    /* 维度选取参数*/
	    private String[] select_dimension;//可选维度
	    private String[] lengthways_dimension;//纵向维度
	    private String[] crosswise_dimension;//横向维度
	    private ArrayList all_select_dimension_list = new ArrayList();
	    private ArrayList select_dimension_list=new ArrayList();
    	private ArrayList lengthways_dimension_list=new ArrayList();
    	private ArrayList crosswise_dimension_list=new ArrayList();
	    /* 人员范围参数*/
	    private String complex_id="";//常用查询
	    private ArrayList condlist=new ArrayList();

	    // 多个统计图同时显示时的参数
	    private ArrayList statIdslist=new ArrayList();
	    private ArrayList decimalwidthlist=new ArrayList();
	    private ArrayList isneedsumlist=new ArrayList();
	    private ArrayList listlist=new ArrayList();
	    private ArrayList label_enabledlist=new ArrayList();
	    private ArrayList xanglelist=new ArrayList();
	    private ArrayList snamedisplaylist=new ArrayList();

	    private String vtotal="0";
	    private String htotal="0";
	    private String vnull="0";
	    private String hnull="0";
	    private String commlexr = "";
	    private String commfacor = "";
	    private int[][] statdoublevalues;
	    private double[][] statdoublevaluess;
	    private List varrayfirstlist=new ArrayList();
	    private List varraysecondlist=new ArrayList();
	    private List harrayfirstlist=new ArrayList();
	    private List harraysecondlist=new ArrayList();
	    // 二维交叉统计表显示时的参数
	    private String home="0";
	    private String totalvalue;
	    private String lengthways;
	    private String crosswise;
	    private String querycond="";
	    private String sformula;
	    
	    private String showChart;//是否显示统计图，=1显示，=0隐藏
	    
	}
	
	/* 统计图参数*/
	// 单个统计图显示时的参数
	private String isneedsum;
	private String xangle;
	private String snamedisplay;
	private String label_enabled="true";
	private String decimalwidth;
	private ArrayList list=new ArrayList();
	
	private String statidFlag;//是否是配置好的统计图
	private String filterId; //筛选机构id
	private String filterName; //筛选机构名称
	private String org_filter; //机构筛选  =1 是 =0否
	private String statid; //统计条件id

	public String getStatid() {
		return statid;
	}

	public void setStatid(String statid) {
		this.statid = statid;
	}
	
	//人员库列表
	private PaginationForm dbNameListForm=new PaginationForm();
	
	private Option getOption() {
	    Option o = (Option)options.get(type);
	    if(o == null) {
	        o = new Option();
	        options.put(type, o);
	    }
	    return o;
	}

	
	
	public String getUserbases() {
		return getOption().userbases;
	}

	public void setUserbases(String userbases) {
		getOption().userbases = userbases;
	}

	public String getViewuserbases() {
		return getOption().viewuserbases;
	}

	public void setViewuserbases(String viewuserbases) {
		getOption().viewuserbases = viewuserbases;
	}

	public ArrayList getDblist() {
		return getOption().dblist;
	}

	public void setDblist(ArrayList dblist) {
		getOption().dblist = dblist;
	}

	public String getCommlexr() {
		return getOption().commlexr;
	}

	public void setCommlexr(String commlexr) {
	    getOption().commlexr = commlexr;
	}

	public String getCommfacor() {
		return getOption().commfacor;
	}

	public void setCommfacor(String commfacor) {
	    getOption().commfacor = commfacor;
	}
	
    @Override
    public void reset(ActionMapping actionmapping, HttpServletRequest httpservletrequest)
    {
        super.reset(actionmapping, httpservletrequest);
    }

	@Override
    public void inPutTransHM() {
	    this.getFormHM().put("type", this.getType());
		this.getFormHM().put("lengthways_dimension",this.getLengthways_dimension());
		this.getFormHM().put("crosswise_dimension",this.getCrosswise_dimension());
		
		this.getFormHM().put("select_dimension_list",this.getSelect_dimension_list());
		this.getFormHM().put("all_select_dimension_list",this.getAll_select_dimension_list());
		this.getFormHM().put("lengthways_dimension_list",this.getLengthways_dimension_list());
		this.getFormHM().put("crosswise_dimension_list",this.getCrosswise_dimension_list());
		
		this.getFormHM().put("isneedsum",this.getIsneedsum());
		this.getFormHM().put("xangle",this.getXangle());
		this.getFormHM().put("snamedisplay",this.getSnamedisplay());
		this.getFormHM().put("decimalwidth",this.getDecimalwidth());
		this.getFormHM().put("list",this.getList());
		this.getFormHM().put("label_enabled",this.getLabel_enabled());
		
        this.getFormHM().put("statIdslist", this.getStatIdslist());
        this.getFormHM().put("isneedsumlist", this.getIsneedsumlist());
        this.getFormHM().put("xanglelist", this.getXanglelist());
        this.getFormHM().put("snamedisplaylist", this.getSnamedisplaylist());
        this.getFormHM().put("label_enabledlist", this.getLabel_enabledlist());
        this.getFormHM().put("decimalwidthlist", this.getDecimalwidthlist());
        this.getFormHM().put("listlist", this.getListlist());
		
		this.getFormHM().put("home", getHome());
		this.getFormHM().put("vtotal", getVtotal());
		this.getFormHM().put("htotal", getHtotal());
		this.getFormHM().put("vnull", getVnull());
		this.getFormHM().put("hnull", getHnull());

		this.getFormHM().put("statdoublevalues", this.getStatdoublevalues());
		this.getFormHM().put("statdoublevaluess", this.getStatdoublevaluess());
		this.getFormHM().put("varrayfirstlist", this.getVarrayfirstlist());
		this.getFormHM().put("varraysecondlist", this.getVarraysecondlist());
		this.getFormHM().put("harrayfirstlist", this.getHarrayfirstlist());
		this.getFormHM().put("harraysecondlist", this.getHarraysecondlist());
		this.getFormHM().put("totalvalue", this.getTotalvalue());

		this.getFormHM().put("querycond",this.getQuerycond());
		this.getFormHM().put("sformula", this.getSformula());
		this.getFormHM().put("lengthways", getLengthways());
		this.getFormHM().put("crosswise", getCrosswise());
		this.getFormHM().put("dbname",getDbname());
		this.getFormHM().put("userbases",getUserbases());
		this.getFormHM().put("viewuserbases",getViewuserbases());
		this.getFormHM().put("dblist",getDblist());		
		this.getFormHM().put("stat",getStat());
        this.getFormHM().put("showdbname",getShowdbname());
		this.getFormHM().put("cond",getCond());
        this.getFormHM().put("showcond",getShowcond());
		this.getFormHM().put("commfacor", getCommfacor());
		this.getFormHM().put("commlexr", getCommlexr());
		this.getFormHM().put("complex_id", this.getComplex_id());
		
		this.getFormHM().put("statidFlag", getStatidFlag());
		this.getFormHM().put("showChart", getShowChart());
		this.getFormHM().put("filterId", getFilterId());
		this.getFormHM().put("filterName", getFilterName());
		this.getFormHM().put("org_filter", getOrg_filter());
		this.getFormHM().put("statid", getStatid());
	}

	@Override
    public void outPutFormHM() {
		this.setType((String)this.getFormHM().get("type"));
		this.setShowdbname((String)this.getFormHM().get("showdbname"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setUserbases((String)this.getFormHM().get("userbases"));
		this.setViewuserbases((String)this.getFormHM().get("viewuserbases"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setStat((String)this.getFormHM().get("stat"));
		this.setShowcond((String)this.getFormHM().get("showcond"));
		this.setCond((String)this.getFormHM().get("cond"));
		this.setSelect_dimension((String[])this.getFormHM().get("select_dimension"));
		this.setLengthways_dimension((String[])this.getFormHM().get("lengthways_dimension"));
		this.setCrosswise_dimension((String[])this.getFormHM().get("crosswise_dimension"));
		
		this.setAll_select_dimension_list((ArrayList)this.getFormHM().get("all_select_dimension_list"));
		this.setSelect_dimension_list((ArrayList)this.getFormHM().get("select_dimension_list"));
		this.setLengthways_dimension_list((ArrayList)this.getFormHM().get("lengthways_dimension_list"));
		this.setCrosswise_dimension_list((ArrayList)this.getFormHM().get("crosswise_dimension_list"));
		
		this.setComplex_id((String)this.getFormHM().get("complex_id"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
		this.setIsneedsum((String)this.getFormHM().get("isneedsum"));
		this.setXangle((String)this.getFormHM().get("xangle"));
		this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
		if(this.getFormHM().get("label_enabled")!=null&&this.getFormHM().get("label_enabled").toString().length()>0)
			this.setLabel_enabled((String)this.getFormHM().get("label_enabled"));
		this.setDecimalwidth((String)this.getFormHM().get("decimalwidth"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setStatIdslist((ArrayList)this.getFormHM().get("statIdslist"));
		this.setIsneedsumlist((ArrayList)this.getFormHM().get("isneedsumlist"));
		this.setXanglelist((ArrayList)this.getFormHM().get("xanglelist"));
		this.setSnamedisplaylist((ArrayList)this.getFormHM().get("snamedisplaylist"));
		if(this.getFormHM().get("label_enabledlist")!=null&&this.getFormHM().get("label_enabledlist").toString().length()>0)
			this.setLabel_enabledlist((ArrayList)this.getFormHM().get("label_enabledlist"));
		this.setDecimalwidthlist((ArrayList)this.getFormHM().get("decimalwidthlist"));
		this.setListlist((ArrayList)this.getFormHM().get("listlist"));
		
		this.setHome((String)this.getFormHM().get("home"));
		this.setVtotal((String)this.getFormHM().get("vtotal"));
        this.setHtotal((String)this.getFormHM().get("htotal"));
        this.setVnull((String)this.getFormHM().get("vnull"));
        this.setHnull((String)this.getFormHM().get("hnull"));
        this.setStatdoublevalues((int[][])this.getFormHM().get("statdoublevalues"));
        this.setStatdoublevaluess((double[][])this.getFormHM().get("statdoublevaluess"));
		this.setVarrayfirstlist((List)this.getFormHM().get("varrayfirstlist"));
		this.setVarraysecondlist((List)this.getFormHM().get("varraysecondlist"));
		this.setHarrayfirstlist((List)this.getFormHM().get("harrayfirstlist"));
		this.setHarraysecondlist((List)this.getFormHM().get("harraysecondlist"));
		this.setTotalvalue((String)this.getFormHM().get("totalvalue"));
		this.setQuerycond((String)this.getFormHM().get("querycond"));
		this.setSformula((String)this.getFormHM().get("sformula"));
		this.setLengthways((String)this.getFormHM().get("lengthways"));
		this.setCrosswise((String)this.getFormHM().get("crosswise"));
		
		this.setCommfacor((String)this.getFormHM().get("commfacor"));
		this.setCommlexr((String)this.getFormHM().get("commlexr"));
		this.setStatidFlag((String)this.getFormHM().get("statidFlag"));
		this.setShowChart((String)this.getFormHM().get("showChart"));
		this.setOrg_filter((String)this.getFormHM().get("org_filter"));
		this.setFilterId((String)this.getFormHM().get("filterId"));
		this.setFilterName((String)this.getFormHM().get("filterName"));
		this.setStatid((String)this.getFormHM().get("statid"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/deci/statics/crosstab".equals(arg0.getPath())&&arg1.getParameter("b_show")!=null){
			if(arg1.getParameter("home")==null||"".equals(arg1.getParameter("home"))){				
				this.setHome("0");
				this.getFormHM().put("home", "0");
			}
        }
		return super.validate(arg0, arg1);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShowdbname() {
		return getOption().showdbname;
	}

	public void setShowdbname(String showdbname) {
	    getOption().showdbname = showdbname;
	}
	
	public String getStat() {
		return getOption().stat;
	}
	public void setStat(String stat) {
		getOption().stat = stat;
	}
	
	public String getDbname() {
		return getOption().dbname;
	}

	public void setDbname(String dbname) {
	    getOption().dbname = dbname;
	}

	public String getShowcond() {
		return getOption().showcond;
	}

	public void setShowcond(String showcond) {
	    getOption().showcond = showcond;
	}

	public String getCond() {
		return getOption().cond;
	}

	public void setCond(String cond) {
	    getOption().cond = cond;
	}

	public String[] getSelect_dimension() {
		return getOption().select_dimension;
	}

	public void setSelect_dimension(String[] select_dimension) {
	    getOption().select_dimension = select_dimension;
	}

	public String[] getLengthways_dimension() {
		return getOption().lengthways_dimension;
	}

	public void setLengthways_dimension(String[] lengthways_dimension) {
	    getOption().lengthways_dimension = lengthways_dimension;
	}

	public String[] getCrosswise_dimension() {
		return getOption().crosswise_dimension;
	}

	public void setCrosswise_dimension(String[] crosswise_dimension) {
	    getOption().crosswise_dimension = crosswise_dimension;
	}
	
	public ArrayList getAll_select_dimension_list() {
		return getOption().all_select_dimension_list;
	}

	public void setAll_select_dimension_list(ArrayList all_select_dimension_list) {
	    getOption().all_select_dimension_list = all_select_dimension_list;
	}
	
	public ArrayList getSelect_dimension_list() {
		return getOption().select_dimension_list;
	}

	public void setSelect_dimension_list(ArrayList select_dimension_list) {
	    getOption().select_dimension_list = select_dimension_list;
	}

	public ArrayList getLengthways_dimension_list() {
		return getOption().lengthways_dimension_list;
	}

	public void setLengthways_dimension_list(ArrayList lengthways_dimension_list) {
	    getOption().lengthways_dimension_list = lengthways_dimension_list;
	}

	public ArrayList getCrosswise_dimension_list() {
		return getOption().crosswise_dimension_list;
	}

	public void setCrosswise_dimension_list(ArrayList crosswise_dimension_list) {
	    getOption().crosswise_dimension_list = crosswise_dimension_list;
	}

	public ArrayList getCondlist() {
		return getOption().condlist;
	}

	public void setCondlist(ArrayList condlist) {
	    getOption().condlist = condlist;
	}

	public String getComplex_id() {
		return getOption().complex_id;
	}

	public void setComplex_id(String complex_id) {
	    getOption().complex_id = complex_id;
	}

	public ArrayList getDecimalwidthlist() {
		return getOption().decimalwidthlist;
	}

	public void setDecimalwidthlist(ArrayList decimalwidthlist) {
	    getOption().decimalwidthlist = decimalwidthlist;
	}

	public ArrayList getIsneedsumlist() {
		return getOption().isneedsumlist;
	}

	public void setIsneedsumlist(ArrayList isneedsumlist) {
	    getOption().isneedsumlist = isneedsumlist;
	}

	public ArrayList getListlist() {
		return getOption().listlist;
	}

	public void setListlist(ArrayList listlist) {
	    getOption().listlist = listlist;
	}

	public ArrayList getLabel_enabledlist() {
		return getOption().label_enabledlist;
	}

	public void setLabel_enabledlist(ArrayList label_enabledlist) {
	    getOption().label_enabledlist = label_enabledlist;
	}

	public ArrayList getXanglelist() {
		return getOption().xanglelist;
	}

	public void setXanglelist(ArrayList xanglelist) {
	    getOption().xanglelist = xanglelist;
	}

	public ArrayList getSnamedisplaylist() {
		return getOption().snamedisplaylist;
	}

	public void setSnamedisplaylist(ArrayList snamedisplaylist) {
	    getOption().snamedisplaylist = snamedisplaylist;
	}

	public String getIsneedsum() {
		return isneedsum;
	}

	public void setIsneedsum(String isneedsum) {
		this.isneedsum = isneedsum;
	}

	public String getXangle() {
		return xangle;
	}

	public void setXangle(String xangle) {
		this.xangle = xangle;
	}

	public String getSnamedisplay() {
		return snamedisplay;
	}

	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}

	public String getLabel_enabled() {
		return label_enabled;
	}

	public void setLabel_enabled(String label_enabled) {
		this.label_enabled = label_enabled;
	}

	public String getDecimalwidth() {
		return decimalwidth;
	}

	public void setDecimalwidth(String decimalwidth) {
		this.decimalwidth = decimalwidth;
	}
	
	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public ArrayList getStatIdslist() {
		return getOption().statIdslist;
	}

	public void setStatIdslist(ArrayList statIdslist) {
	    getOption().statIdslist = statIdslist;
	}

	public String getHome() {
		return getOption().home;
	}

	public void setHome(String home) {
	    getOption().home = home;
	}

	public String getVtotal() {
		return getOption().vtotal;
	}

	public void setVtotal(String vtotal) {
	    getOption().vtotal = vtotal;
	}

	public String getHtotal() {
		return getOption().htotal;
	}

	public void setHtotal(String htotal) {
	    getOption().htotal = htotal;
	}

	public int[][] getStatdoublevalues() {
		return getOption().statdoublevalues;
	}

	public void setStatdoublevalues(int[][] statdoublevalues) {
	    getOption().statdoublevalues = statdoublevalues;
	}

	public double[][] getStatdoublevaluess() {
		return getOption().statdoublevaluess;
	}

	public void setStatdoublevaluess(double[][] statdoublevaluess) {
	    getOption().statdoublevaluess = statdoublevaluess;
	}

	public List getVarrayfirstlist() {
		return getOption().varrayfirstlist;
	}

	public void setVarrayfirstlist(List varrayfirstlist) {
	    getOption().varrayfirstlist = varrayfirstlist;
	}

	public List getVarraysecondlist() {
		return getOption().varraysecondlist;
	}

	public void setVarraysecondlist(List varraysecondlist) {
	    getOption().varraysecondlist = varraysecondlist;
	}

	public List getHarrayfirstlist() {
		return getOption().harrayfirstlist;
	}

	public void setHarrayfirstlist(List harrayfirstlist) {
	    getOption().harrayfirstlist = harrayfirstlist;
	}

	public List getHarraysecondlist() {
		return getOption().harraysecondlist;
	}

	public void setHarraysecondlist(List harraysecondlist) {
	    getOption().harraysecondlist = harraysecondlist;
	}

	public String getTotalvalue() {
		return getOption().totalvalue;
	}

	public void setTotalvalue(String totalvalue) {
	    getOption().totalvalue = totalvalue;
	}
	
	public String getQuerycond() {
		return getOption().querycond;
	}

	public void setQuerycond(String querycond) {
	    getOption().querycond = querycond;
	}

	public String getSformula() {
		return getOption().sformula;
	}

	public void setSformula(String sformula) {
	    getOption().sformula = sformula;
	}

	public String getLengthways() {
		return getOption().lengthways;
	}

	public void setLengthways(String lengthways) {
	    getOption().lengthways = lengthways;
	}

	public String getCrosswise() {
		return getOption().crosswise;
	}

	public void setCrosswise(String crosswise) {
	    getOption().crosswise = crosswise;
	}

	public String getVnull() {
		return getOption().vnull;
	}

	public void setVnull(String vnull) {
	    getOption().vnull = vnull;
	}

	public String getHnull() {
		return getOption().hnull;
	}

	public void setHnull(String hnull) {
	    getOption().hnull = hnull;
	}

	public PaginationForm getDbNameListForm() {
		return dbNameListForm;
	}

	public void setDbNameListForm(PaginationForm dbNameListForm) {
		this.dbNameListForm = dbNameListForm;
	}

	public String getStatidFlag() {
		return statidFlag;
	}

	public void setStatidFlag(String statidFlag) {
		this.statidFlag = statidFlag;
	}

	public String getShowChart() {
		return getOption().showChart;
	}

	public void setShowChart(String showChart) {
		getOption().showChart = showChart;
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
