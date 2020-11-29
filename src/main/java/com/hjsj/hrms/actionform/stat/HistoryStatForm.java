/*
 * Created on 2005-6-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.stat;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

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
public class HistoryStatForm extends FrameForm {


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
	private ArrayList yylist=new ArrayList();
	private String archive_type="";
	private String showflag="";
	private String showLegend="";
	private ArrayList orderlist=new ArrayList();
	private String returnvalue;
	
	private String backdate;
	private String backdates;
	private String allbackdates;
	private ArrayList chart_types = new ArrayList();
	private ArrayList backdateslist = new ArrayList();
	private String scan_table;
	private String filename;
	private String uniqueitem;
	
	private String snap_fields="";
	
	private String html;
	private String bidesk = "false";
	/*
	 * 字体旋转角度
	 * 【7780】员工管理-历史时点，统计分析统计项太多，界面显示不全
	 * jingq add 2015.03.05
	 */
	private String xangle;
	
	public String getXangle() {
		return xangle;
	}
	public void setXangle(String xangle) {
		this.xangle = xangle;
	}
	public String getBidesk() {
		return bidesk;
	}
	public void setBidesk(String bidesk) {
		this.bidesk = bidesk;
	}
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
	private String userbase="Usr";
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
	private String chart_type="11";
	private int[][] statdoublevalues;
	private List varraylist=new ArrayList();
	private List harraylist=new ArrayList();
	private int v;
	private int h;
	private String flag;
	private String totalvalue;
	private String result;
	private String isoneortwo;
	private String nbase;
	private String type;
	
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
		chart_type=chart_type!=null&&chart_type.length()>0?chart_type:"11";
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
	 private ArrayList list=new ArrayList();

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stubbidesk
		 this.setBidesk((String)this.getFormHM().get("bidesk"));
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
         this.setBackdates((String)this.getFormHM().get("backdates"));
         this.setAllbackdates((String)this.getFormHM().get("allbackdates"));
         this.setChart_types((ArrayList)this.getFormHM().get("chart_types"));
         this.setHtml((String)this.getFormHM().get("html"));
         this.setBackdateslist((ArrayList)this.getFormHM().get("backdateslist"));
         this.setScan_table((String)this.getFormHM().get("scan_table"));
         this.setFilename((String)this.getFormHM().get("filename"));
         this.setSnap_fields((String)this.getFormHM().get("snap_fields"));
         this.setXangle((String) this.getFormHM().get("xangle"));
         this.setUniqueitem((String)this.getFormHM().get("uniqueitem"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("bidesk",bidesk);
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
		// //显示类型1:通过点击图形传送的数据 传送的是 统计项名称（汉字）;0 or null 是点击树形节点现实的 传送的是id
		this.getFormHM().put("showflag", this.getShowflag());
		this.getFormHM().put("showLegend", this.getShowLegend());
		this.getFormHM().put("lexprId", this.getLexprId());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("chart_type", this.getChart_type());
		this.getFormHM().put("backdates", backdates);
		this.getFormHM().put("backdate", backdate);
		this.getFormHM().put("nbase", nbase);
		this.getFormHM().put("uniqueitem", uniqueitem);
		this.getFormHM().put("xangle", this.getXangle());
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
	/**
	 * @param totalvalue The totalvalue to set.
	 */
	public void setTotalvalue(String totalvalue) {
		this.totalvalue = totalvalue;
	}
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
        if("/workbench/stat/statshow".equals(arg0.getPath())&&arg1.getParameter("b_data")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        if("/general/static/commonstatic/history/statshow".equals(arg0.getPath())&&arg1.getParameter("b_data")!=null)
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
        if("/general/static/commonstatic/statshowmsgchart".equals(arg0.getPath())&&arg1.getParameter("b_msgchart")!=null)
        {
        	 this.getFormHM().put("querycond", "");
        	 this.getFormHM().put("lexprId","");    
    	     this.setQuerycond("");
    	     this.setLexprId("");
    	     this.getFormHM().put("orgName", "");
    	     this.setOrgName("");    	     
    	     this.setChart_type(arg1.getParameter("chart_type"));
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
	public String getBackdates() {
		return backdates;
	}
	public void setBackdates(String backdates) {
		this.backdates = backdates;
	}
	public String getBackdate() {
		return backdate;
	}
	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
	public String getAllbackdates() {
		return allbackdates;
	}
	public void setAllbackdates(String allbackdates) {
		this.allbackdates = allbackdates;
	}
	public ArrayList getChart_types() {
		return chart_types;
	}
	public void setChart_types(ArrayList chart_types) {
		this.chart_types = chart_types;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public ArrayList getBackdateslist() {
		return backdateslist;
	}
	public void setBackdateslist(ArrayList backdateslist) {
		this.backdateslist = backdateslist;
	}
	public String getScan_table() {
		return scan_table;
	}
	public void setScan_table(String scan_table) {
		this.scan_table = scan_table;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUniqueitem() {
		return uniqueitem;
	}
	public void setUniqueitem(String uniqueitem) {
		this.uniqueitem = uniqueitem;
	}
    public String getSnap_fields() {
        return snap_fields;
    }
    public void setSnap_fields(String snap_fields) {
        this.snap_fields = snap_fields;
    }	
}
