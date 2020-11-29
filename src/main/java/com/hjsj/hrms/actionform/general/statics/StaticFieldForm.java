package com.hjsj.hrms.actionform.general.statics;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class StaticFieldForm extends FrameForm
{
	private String flag="0"; 
	
	private String infor_Flag="";
	
	private String[] right_fields;
	private String home;
	private String a0100;
	
	private String mess;
	
	private String sno;
	
	private String names;
	
	private String gvalue;
	
	private String titles;
	
	private String ititle;

	private String dbpre;
	
    private String flist;
    
    private String hvalue;
    
    private String texts;
    
    private String[] selects;
    private String[] selectstitle;
    private String history;
    
    private String find;
	private String findlike;
	private String dbcond="''";
	private String distinct="";
	private String mes;
	
	private String selOne;
	
	private String selTwo;
	
	private String query_type="1";
	
	private String tovalue;
	
	private int[][] doublevalues;
	
	private String vv;
	
	private String hh;
	
	private String result;
	
	private String strsql;
	
	private String cond_str;
	
	private String order_by;
	
	private String treeCode;
	
	private String display;
	
	private String disp;
	
	 private String returnvalue;
	
	 private ArrayList hlist=new ArrayList();
	 
	 private ArrayList dlist=new ArrayList();
	
	private ArrayList alist=new ArrayList();
	
    private ArrayList factorlist=new ArrayList();
    
    private ArrayList operlist=new ArrayList();
    
    private ArrayList logiclist=new ArrayList();
    
    private ArrayList list=new ArrayList();
    
    private ArrayList rlist=new ArrayList();
    
    private ArrayList snamelist=new ArrayList();

    private ArrayList lexprlist=new ArrayList();
    private String userbase="Usr";
    
	private String querycond;
	
	private String statid="2";
	private HashMap jfreemap=new HashMap();
	private String chart_type="12";
	
	private String org_filter; //是否按组织机构筛选 =1 是    =0 否
	
	//二维统计表
	private ArrayList histogramlist = null;
	//update by xiegh on 20170905 之前是hashmap，
	//因为hashmap无序，导致折线图 图例内容顺序变化，当点击穿透查询时，数据不正确
	private LinkedHashMap dataMap = new LinkedHashMap();
	private String chartTitle = "";
	private String chartType = ""; //29柱状图  11折线图
	private String chartWidth = "750";
	private String chartHeight = "120";
	private String chartFlag ="";
	private String photo = "";
	private String realPath = "";
	private List iconList = new ArrayList();

	private String snamedisplay;
	private String photo_other_view;
	private String photolength="";
	private PaginationForm staticFieldForm=new PaginationForm();  
	private String uplevel;
	private String editid="";
	private String opflag="";
	private String stat_name="";
	private ArrayList orderlist=new ArrayList(); 
	private String order_fields="";
    private String showLegend="";
    private String stat_type="";
    private ArrayList fieldlist=new ArrayList();
    private String tabid="";
    private String columns="";
    
    private String categories;
    private ArrayList catelist=new ArrayList();
    private String viewtype;
    private ArrayList viewtypelist=new ArrayList();
    private String hidcategories;
    private String sformula;
    private ArrayList itemlist=new ArrayList();
    private String fielditemid;
    private String stype;
    
    private String type;//单维统计1 多为统计2
    
    private String hformulatable;
    private String vformulatable;
    private String hv;
    private String sbase;
    private String sbasehtml;
    private String userbases;
    private String init_userbases;//二维统计 重置用到此参数
	private String viewuserbases;
	private String init_viewuserbases;//二位统计 重置用到此参数
	private String vtotal="0";
	private String htotal="0";
	private String xangle;
	private ArrayList<CommonData> rightFieldList = new ArrayList<CommonData>();
	
	private ArrayList selectedlist = new ArrayList();
	
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getOrder_fields() {
		return order_fields;
	}
	public void setOrder_fields(String order_fields) {
		this.order_fields = order_fields;
	}
	public ArrayList getOrderlist() {
		return orderlist;
	}
	public void setOrderlist(ArrayList orderlist) {
		this.orderlist = orderlist;
	}
	public String getStat_name() {
		return stat_name;
	}
	public void setStat_name(String stat_name) {
		this.stat_name = stat_name;
	}
	public String getOpflag() {
		return opflag;
	}
	public void setOpflag(String opflag) {
		this.opflag = opflag;
	}
	public String getEditid() {
		return editid;
	}
	public void setEditid(String editid) {
		this.editid = editid;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public StaticFieldForm()
	{
		 
		CommonData vo=new CommonData("=","=");
        operlist.add(vo);
        vo=new CommonData(">",">");
        operlist.add(vo);  
        vo=new CommonData(">=",">=");
        operlist.add(vo); 
        vo=new CommonData("<","<");
        operlist.add(vo);
        vo=new CommonData("<=","<=");
        operlist.add(vo);   
        vo=new CommonData("<>","<>");
        operlist.add(vo);
      
	}
	@Override
    public void outPutFormHM() {
		//二位统计
		this.setDataMap((LinkedHashMap) this.getFormHM().get("dataMap"));
		this.setHistogramlist((ArrayList) this.getFormHM().get("histogramlist"));
		this.setChartTitle((String) this.getFormHM().get("chartTitle"));
		this.setChartWidth((String) this.getFormHM().get("chartWidth"));
		this.setChartHeight((String) this.getFormHM().get("chartHeight"));
		this.setChartFlag((String) this.getFormHM().get("chartFlag"));
		this.setChartType((String) this.getFormHM().get("chartType"));

		// TODO Auto-generated method stub
		//System.out.println("---"+this.getFormHM().get("disp"));
		this.setInfor_Flag((String)this.getFormHM().get("infor_Flag"));
		this.setDbcond((String)this.getFormHM().get("dbcond"));
		this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
		this.setSno((String)this.getFormHM().get("sno"));
		this.setTitles((String)this.getFormHM().get("title"));
		this.setAlist((ArrayList)this.getFormHM().get("setlist"));
		this.setHvalue((String)this.getFormHM().get("hvalue"));
		this.setNames((String)this.getFormHM().get("names"));
		this.setAlist((ArrayList)this.getFormHM().get("dblist"));
		this.setFlist((String)this.getFormHM().get("flist"));
		this.setMes((String)this.getFormHM().get("mes"));
		this.setDisplay((String)this.getFormHM().get("display"));
		this.setLogiclist((ArrayList)this.getFormHM().get("logiclist"));
		this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		//【8665】员工管理-统计分析-通用统计，设置统计范围后，反查进去，人数不对  jingq upd 2015.04.14
   	    this.setQuerycond((String)this.getFormHM().get("querycond"));
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setStatid((String)this.getFormHM().get("statid"));
		this.setSnamelist((ArrayList)this.getFormHM().get("namelist"));
		this.setRlist((ArrayList)this.getFormHM().get("rlist"));
		this.setDisp((String)this.getFormHM().get("disp"));
        this.setDistinct((String)this.getFormHM().get("distinct"));
		this.setDoublevalues((int[][])this.getFormHM().get("doublevalues"));
		this.setHlist((ArrayList)this.getFormHM().get("hlist"));
		this.setDlist((ArrayList)this.getFormHM().get("dlist"));
		this.setTovalue((String)this.getFormHM().get("tovalue"));
		
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		
		this.setHistory((String)this.getFormHM().get("history"));
		this.setResult((String)this.getFormHM().get("result"));
		this.setMess((String)this.getFormHM().get("mess"));
		this.setSelTwo((String)this.getFormHM().get("selTwo"));
		this.setSelOne((String)this.getFormHM().get("selOne"));
		
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
		String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	    this.setPhotolength(photolength);
		this.setEditid((String)this.getFormHM().get("editid"));
		this.setLexprlist((ArrayList)this.getFormHM().get("lexprlist"));
		this.setTexts((String)this.getFormHM().get("texts"));
		this.setOpflag((String)this.getFormHM().get("opflag"));
		this.setStat_name((String)this.getFormHM().get("stat_name"));
		this.setFindlike((String)this.getFormHM().get("findlike"));
		this.setOrderlist((ArrayList)this.getFormHM().get("orderlist"));	
		
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setColumns((String)this.getFormHM().get("columns"));
        this.setTabid((String)this.getFormHM().get("tabid"));   
        
        this.setCatelist((ArrayList)this.getFormHM().get("catelist"));
        this.setCategories((String)this.getFormHM().get("categories"));
        this.setViewtypelist((ArrayList)this.getFormHM().get("viewtypelist"));
        this.setViewtype((String)this.getFormHM().get("viewtype"));
        this.setHidcategories((String)this.getFormHM().get("hidcategories"));
        this.setJfreemap((HashMap)this.getFormHM().get("jfreemap"));
        this.setSformula((String)this.getFormHM().get("sformula"));
        this.setType((String)this.getFormHM().get("type"));
        this.setHformulatable((String)this.getFormHM().get("hformulatable"));
        this.setVformulatable((String)this.getFormHM().get("vformulatable"));
        this.setHv((String)this.getFormHM().get("hv"));
        this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
        this.setStype((String)this.getFormHM().get("stype"));
        this.setSbasehtml((String)this.getFormHM().get("sbasehtml"));
        this.setUserbases((String)this.getFormHM().get("userbases"));
        this.setInit_userbases((String)this.getFormHM().get("init_userbases"));
        this.setViewuserbases((String)this.getFormHM().get("viewuserbases"));
        this.setInit_viewuserbases((String)this.getFormHM().get("init_viewuserbases"));
        this.setVtotal((String)this.getFormHM().get("vtotal"));
        this.setHtotal((String)this.getFormHM().get("htotal"));
        this.setXangle((String)this.getFormHM().get("xangle"));
        this.setRight_fields((String[])this.getFormHM().get("right_fields"));
        this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
        this.setRightFieldList((ArrayList)this.getFormHM().get("rightFieldList"));
        this.setPhoto((String)this.getFormHM().get("photo"));
        this.setRealPath((String)this.getFormHM().get("realPath"));
        this.setIconList((ArrayList)this.getFormHM().get("iconList"));
        this.setOrg_filter((String)this.getFormHM().get("org_filter"));
	}

	@Override
    public void inPutTransHM() {
		//二位统计
		this.getFormHM().put("chartType", (String)this.getChartType());
		this.getFormHM().put("chartWidth", (String)this.getChartWidth());
		this.getFormHM().put("chartHeight", (String)this.getChartHeight());
		this.getFormHM().put("chartFlag", (String)this.getChartFlag());
		this.getFormHM().put("dataMap", (HashMap)this.getDataMap());

		// TODO Auto-generated method stub
		this.getFormHM().put("infor_Flag",(String)this.getInfor_Flag());
		this.getFormHM().put("factorlist",(ArrayList)this.getFactorlist());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("find",(String)this.getFind());
		this.getFormHM().put("history",(String)this.getHistory());
		this.getFormHM().put("sno",(String)this.getSno());
		this.getFormHM().put("title",(String)this.getTitles());
		this.getFormHM().put("hvalue",(String)this.getHvalue());
		this.getFormHM().put("flag",(String)this.getFlag());
	    this.getFormHM().put("selects",this.getSelects());
	    this.getFormHM().put("names",(String)this.getNames());
	    this.getFormHM().put("flist",(String)this.getFlist());
	    this.getFormHM().put("texts",(String)this.getTexts());
	    this.getFormHM().put("mess",this.getMess());
	    this.getFormHM().put("logiclist",(ArrayList)this.getLogiclist());
	    this.getFormHM().put("gvalue",this.getGvalue());
	    this.getFormHM().put("sno",(String)this.getSno());
		this.getFormHM().put("userbase",(String)this.getUserbase());
		this.getFormHM().put("querycond",(String)this.getQuerycond());
		this.getFormHM().put("statid",(String)this.getStatid());
		this.getFormHM().put("result",(String)this.getResult());
		this.getFormHM().put("selOne",this.getSelOne());
		this.getFormHM().put("selTwo",this.getSelTwo());
		this.getFormHM().put("vv",(String)this.getVv());
		this.getFormHM().put("hh",(String)this.getHh());	
		this.getFormHM().put("opflag", this.getOpflag());
		this.getFormHM().put("findlike", this.getFindlike());		
		this.getFormHM().put("editid", this.getEditid());
		this.getFormHM().put("stat_type", this.getStat_type());//统计类型
		this.getFormHM().put("showLegend", this.getShowLegend());//统计条件名称
		this.getFormHM().put("categories", categories);
		this.getFormHM().put("viewtype", viewtype);
		this.getFormHM().put("sformula", sformula);
		this.getFormHM().put("type", type);
		this.getFormHM().put("hv", hv);
		this.getFormHM().put("sbase", sbase);
		this.getFormHM().put("userbases", userbases);
		this.getFormHM().put("init_userbases", init_userbases);
		this.getFormHM().put("viewuserbases", viewuserbases);
		this.getFormHM().put("init_viewuserbases", init_viewuserbases);
		this.getFormHM().put("vtotal",this.vtotal);
		this.getFormHM().put("htotal", htotal);
		this.getFormHM().put("xangle", this.getXangle());
		this.getFormHM().put("selectedlist", this.getSelectedlist());
		this.getFormHM().put("rightFieldList", this.getRightFieldList());
		this.getFormHM().put("photo",this.getPhoto());
		this.getFormHM().put("realPath",this.getRealPath());
		this.getFormHM().put("iconList",this.getIconList());
		this.getFormHM().put("org_filter",this.getOrg_filter());
	}
	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String[] getSelects() {
		return selects;
	}
	public void setSelects(String[] selects) {
		this.selects = selects;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getDbcond() {
		return dbcond;
	}

	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}

	public String getFlist() {
		return flist;
	}

	public void setFlist(String flist) {
		this.flist = flist;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public String getQuery_type() {
		return query_type;
	}
	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}
	public ArrayList getLogiclist() {
		return logiclist;
	}
	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}
	public String getTexts() {
		return texts;
	}
	public void setTexts(String texts) {
		this.texts = texts;
	}

	public String getFind() {
		return find;
	}
	public void setFind(String find) {
		this.find = find;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getTitles() {
		return titles;
	}
	public void setTitles(String titles) {
		this.titles = titles;
	}
	public String getItitle() {
		return ititle;
	}
	public void setItitle(String ititle) {
		this.ititle = ititle;
	}
	public ArrayList getAlist() {
		return alist;
	}
	public void setAlist(ArrayList alist) {
		this.alist = alist;
	}
	public String getHvalue() {
		return hvalue;
	}
	public void setHvalue(String hvalue) {
		this.hvalue = hvalue;
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/general/static/general_static".equals(arg0.getPath())&&(arg1.getParameter("b_incept")!=null))
        {          
            this.setFlag("4");
        	  
        }
        if("/general/static/add_general".equals(arg0.getPath())&&(arg1.getParameter("b_ok")!=null))
        {          
            this.setFlag("5");
        	  
        }
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
	    if("/general/static/select_field".equals(arg0.getPath())&&arg1.getParameter("b_next")!=null)
        {
	    	this.setHvalue("");
			this.setFind("0");
			this.setResult("0");
			this.setHistory("0");
        }
	    if("/general/static/static_data".equals(arg0.getPath())&&arg1.getParameter("b_data")!=null)
        {
	    	if(this.getPagination()!=null)
	              this.getPagination().firstPage();//?
        }
	     if("/general/static/two_dim_static".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
         {
	    	this.setHvalue("");
			this.setFind("0");
			this.setResult("0");
			this.setHistory("0");
			this.setMess("");
          }
	        return super.validate(arg0, arg1);
	        
	    }
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/static/select_static_fields".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
		  this.setHvalue("");
		  this.setFind("0");		
		  this.setResult("0");
		  this.setHistory("0");
        }
		if("/general/static/select_field".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
		  this.setHvalue("");
		  this.setFind("0");		
		  this.setResult("0");
		  this.setHistory("0");
        }
		if("/general/static/general_static".equals(arg0.getPath())&&arg1.getParameter("br_back")!=null)
        {
		  this.setHvalue("");
		  this.setFind("0");		
		  this.setResult("0");
		  this.setHistory("0");
        }
		/**chenmengqing added note for  两维统计出错，*/
		//this.setSelOne("");
		//this.setSelTwo("");
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getChart_type() {
		return chart_type;
	}
	public void setChart_type(String chart_type) {
		this.chart_type = chart_type;
	}
	public String getQuerycond() {
		return querycond;
	}
	public void setQuerycond(String querycond) {
		this.querycond = querycond;
	}
	public String getSnamedisplay() {
		return snamedisplay;
	}
	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}
	public String getStatid() {
		return statid;
	}
	public void setStatid(String statid) {
		this.statid = statid;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public ArrayList getSnamelist() {
		return snamelist;
	}
	public void setSnamelist(ArrayList snamelist) {
		this.snamelist = snamelist;
	}
	public String getGvalue() {
		return gvalue;
	}
	public void setGvalue(String gvalue) {
		this.gvalue = gvalue;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public ArrayList getRlist() {
		return rlist;
	}
	public void setRlist(ArrayList rlist) {
		this.rlist = rlist;
	}
	public String getSelOne() {
		return selOne;
	}
	public void setSelOne(String selOne) {
		this.selOne = selOne;
	}
	public String getSelTwo() {
		return selTwo;
	}
	public void setSelTwo(String selTwo) {
		this.selTwo = selTwo;
	}
	public ArrayList getDlist() {
		return dlist;
	}
	public void setDlist(ArrayList dlist) {
		this.dlist = dlist;
	}
	public int[][] getDoublevalues() {
		return doublevalues;
	}
	public void setDoublevalues(int[][] doublevalues) {
		this.doublevalues = doublevalues;
	}
	public ArrayList getHlist() {
		return hlist;
	}
	public void setHlist(ArrayList hlist) {
		this.hlist = hlist;
	}
	public String getTovalue() {
		return tovalue;
	}
	public void setTovalue(String tovalue) {
		this.tovalue = tovalue;
	}
	public String getHh() {
		return hh;
	}
	public void setHh(String hh) {
		this.hh = hh;
	}
	public String getVv() {
		return vv;
	}
	public void setVv(String vv) {
		this.vv = vv;
	}
	public String getCond_str() {
		return cond_str;
	}
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public PaginationForm getStaticFieldForm() {
		return staticFieldForm;
	}
	public void setStaticFieldForm(PaginationForm staticFieldForm) {
		this.staticFieldForm = staticFieldForm;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getDisp() {
		return disp;
	}
	public void setDisp(String disp) {
		this.disp = disp;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String[] getSelectstitle() {
		return selectstitle;
	}
	public void setSelectstitle(String[] selectstitle) {
		this.selectstitle = selectstitle;
	}
	public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public ArrayList getLexprlist() {
		return lexprlist;
	}
	public void setLexprlist(ArrayList lexprlist) {
		this.lexprlist = lexprlist;
	}
	public String getFindlike() {
		return findlike;
	}
	public void setFindlike(String findlike) {
		this.findlike = findlike;
	}
	public String getShowLegend() {
		return showLegend;
	}
	public void setShowLegend(String showLegend) {
		this.showLegend = showLegend;
	}
	public String getStat_type() {
		return stat_type;
	}
	public void setStat_type(String stat_type) {
		this.stat_type = stat_type;
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
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public ArrayList getCatelist() {
		return catelist;
	}
	public void setCatelist(ArrayList catelist) {
		this.catelist = catelist;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	public ArrayList getViewtypelist() {
		return viewtypelist;
	}
	public void setViewtypelist(ArrayList viewtypelist) {
		this.viewtypelist = viewtypelist;
	}
	public String getHidcategories() {
		return hidcategories;
	}
	public void setHidcategories(String hidcategories) {
		this.hidcategories = hidcategories;
	}
	public HashMap getJfreemap() {
		return jfreemap;
	}
	public void setJfreemap(HashMap jfreemap) {
		this.jfreemap = jfreemap;
	}
	public String getSformula() {
		return sformula;
	}
	public void setSformula(String sformula) {
		this.sformula = sformula;
	}
	public ArrayList getItemlist() {
		return itemlist;
	}
	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getFielditemid() {
		return fielditemid;
	}
	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}
	public String getStype() {
		return stype;
	}
	public void setStype(String stype) {
		this.stype = stype;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHformulatable() {
		return hformulatable;
	}
	public void setHformulatable(String hformulatable) {
		this.hformulatable = hformulatable;
	}
	public String getVformulatable() {
		return vformulatable;
	}
	public void setVformulatable(String vformulatable) {
		this.vformulatable = vformulatable;
	}
	public String getHv() {
		return hv;
	}
	public void setHv(String hv) {
		this.hv = hv;
	}
	public String getSbase() {
		return sbase;
	}
	public void setSbase(String sbase) {
		this.sbase = sbase;
	}
	public String getSbasehtml() {
		return sbasehtml;
	}
	public void setSbasehtml(String sbasehtml) {
		this.sbasehtml = sbasehtml;
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
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
	public String getXangle() {
		return xangle;
	}
	public void setXangle(String xangle) {
		this.xangle = xangle;
	}
	
	
	//二位统计
	public ArrayList getHistogramlist() {
		return histogramlist;
	}
	public void setHistogramlist(ArrayList histogramlist) {
		this.histogramlist = histogramlist;
	}
	public HashMap getDataMap() {
		return dataMap;
	}
	public void setDataMap(LinkedHashMap dataMap) {
		this.dataMap = dataMap;
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
	public String getChartFlag() {
		return chartFlag;
	}
	public void setChartFlag(String chartFlag) {
		this.chartFlag = chartFlag;
	}
    public ArrayList getSelectedlist() {
        return selectedlist;
    }
    public void setSelectedlist(ArrayList selectedlist) {
        this.selectedlist = selectedlist;
    }
    public ArrayList<CommonData> getRightFieldList() {
        return rightFieldList;
    }
    public void setRightFieldList(ArrayList<CommonData> rightFieldList) {
        this.rightFieldList = rightFieldList;
    }

    public void setPhoto(String photo){
		this.photo = photo;
	}
	public String getPhoto(){
		return this.photo;
	}
	public void setRealPath(String realPath){
		this.realPath = realPath;
	}
	public String getRealPath(){
		return this.realPath;
	}
	public void setIconList(List iconList){
		this.iconList = iconList;
	}
	public List getIconList(){
		return this.iconList;
	}
	public String getOrg_filter() {
		return org_filter;
	}
	public void setOrg_filter(String org_filter) {
		this.org_filter = org_filter;
	}
	public String getInit_userbases() {
		return init_userbases;
	}
	public void setInit_userbases(String init_userbases) {
		this.init_userbases = init_userbases;
	}
	public String getInit_viewuserbases() {
		return init_viewuserbases;
	}
	public void setInit_viewuserbases(String init_viewuserbases) {
		this.init_viewuserbases = init_viewuserbases;
	}
	
}
