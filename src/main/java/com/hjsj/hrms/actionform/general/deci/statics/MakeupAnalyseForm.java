/**
 * 
 */
package com.hjsj.hrms.actionform.general.deci.statics;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Owner
 *
 */
public class MakeupAnalyseForm extends FrameForm {

	private String showtitle;//add by xiegh =0不显示总数 ，非0则显示
	private String dbcond;
	private String dbpre;
	private String categories;
	private String statid;        //统计项
	private ArrayList statlist=new ArrayList(); //统计项数据
	private String statlistsize;
	private String result;        //统计查询结果
	private String snamedisplay;  //统计项标题名
	private String char_type="12";     //图例形状特征
	private ArrayList datalist=new ArrayList(); //统计结果数据
	private HashMap jfreemap=new HashMap();
	private String isonetwostat;  //是一维或者二维统计
	private int[][] statdoublevalues;    //二维数据
	private List varraylist=new ArrayList();
	private List harraylist=new ArrayList();
	private String totalvalue;    //总数
	private String a_code;
	private String strsql;
	private String cond_str;
	private String order_by;
	private String v;
	private String h;
	private String filename;
	
	private String a0100;
	private ArrayList infofieldlist=new ArrayList();
	private ArrayList infosetlist=new ArrayList();
	private String setname;
	
	
	private ArrayList detailinfolist=new ArrayList();
	private ArrayList infodetailfieldlist=new ArrayList();
	private String b0110;
	private String e0122;
	private String e01a1;
	private String a0101;	
	private PaginationForm AnalyseForm=new PaginationForm();   
	private ArrayList condlist=new ArrayList();
	private String lexprId="";
	private ArrayList chartTypeList=new ArrayList();
	//private String chart_type;
	private String returnphoto;
	private String returnvalue = "1";
	
	private String showstatname = "";  //zgd 2014-8-12 常用统计链接增加参数，支持showstatname=0不显示常用统计名称。
	private String showcharttype = "";  //zgd 2014-8-27 图形类型选项可隐藏,=0隐藏选项。
	private String commlexr = "";
	private String commfacor = "";
	
	private String basesize = "0"; //zgd 2014-8-25 常用统计信息集设置中指定了人员库则basesize=1。
	
	private String xangle;   //wangcq 2014-9-5  旋转角度标志
	
	
	private String substat;//liuy 钻取穿透设置一维常用统计项
	private String subIndex;//liuy 钻取穿透到的某层
	private ArrayList lexprList = new ArrayList();//钻取穿透表达式
	private ArrayList factorList = new ArrayList();//钻取穿透因子表达式
	private ArrayList showLegendList = new ArrayList();//钻取穿透时，选中的图上的条件
	private ArrayList statIdList = new ArrayList();//钻取穿透时，选中的图上的条件
	private ArrayList subIndexList = new ArrayList();//钻取穿透页面显示导航
	private ArrayList statNameList = new ArrayList();//钻取穿透页面显示导航
	private ArrayList statOptionList = new ArrayList();//钻取穿透页面显示导航
	private String statName;//liuy 钻取穿透到的某层
	private String decimalwidth; // wangb 2018-08-24 统计图却笑小数点位数
	private String isneedsum; // wangb 2018-08-24
	private String totalAvg;//总平均
	private String countType;//统计类型

	public String getCountType() {
		return countType;
	}

	public void setCountType(String countType) {
		this.countType = countType;
	}

	public String getTotalAvg() {
		return totalAvg;
	}

	public void setTotalAvg(String totalAvg) {
		this.totalAvg = totalAvg;
	}

	public String getIsneedsum() {
		return isneedsum;
	}
	
	public void setIsneedsum(String isneedsum) {
		this.isneedsum = isneedsum;
	}
	public String getDecimalwidth(){
		return decimalwidth;
	}
	
	public void setDecimalwidth(String decimalwidth){
		this.decimalwidth = decimalwidth;
	}
	
	public String getShowtitle() {
		return showtitle;
	}

	public void setShowtitle(String showtitle) {
		this.showtitle = showtitle;
	}
	public String getStatName() {
		return statName;
	}

	public void setStatName(String statName) {
		this.statName = statName;
	}

	public ArrayList getStatNameList() {
		return statNameList;
	}

	public void setStatNameList(ArrayList statNameList) {
		this.statNameList = statNameList;
	}

	public ArrayList getStatOptionList() {
		return statOptionList;
	}

	public void setStatOptionList(ArrayList statOptionList) {
		this.statOptionList = statOptionList;
	}

	public ArrayList getSubIndexList() {
		return subIndexList;
	}

	public void setSubIndexList(ArrayList subIndexList) {
		this.subIndexList = subIndexList;
	}

	public ArrayList getStatIdList() {
		return statIdList;
	}

	public void setStatIdList(ArrayList statIdList) {
		this.statIdList = statIdList;
	}

	public ArrayList getShowLegendList() {
		return showLegendList;
	}

	public void setShowLegendList(ArrayList showLegendList) {
		this.showLegendList = showLegendList;
	}

	public ArrayList getLexprList() {
		return lexprList;
	}

	public void setLexprList(ArrayList lexprList) {
		this.lexprList = lexprList;
	}

	public ArrayList getFactorList() {
		return factorList;
	}

	public void setFactorList(ArrayList factorList) {
		this.factorList = factorList;
	}
	
	public String getSubIndex() {
		return subIndex;
	}

	public void setSubIndex(String subIndex) {
		this.subIndex = subIndex;
	}

	public String getSubstat() {
		return substat;
	}

	public void setSubstat(String substat) {
		this.substat = substat;
	}

	public String getXangle() {
		return xangle;
	}

	public void setXangle(String xangle) {
		this.xangle = xangle;
	}

	@Override
    public void outPutFormHM() {
		this.setCountType((String)this.getFormHM().get("countType"));
		this.setTotalAvg((String)this.getFormHM().get("totalAvg"));
		this.setDbcond((String)this.getFormHM().get("dbcond"));
		this.setStatlist((ArrayList)this.getFormHM().get("statlist"));
		this.setDatalist((ArrayList)this.getFormHM().get("datalist"));
		this.setIsonetwostat((String)this.getFormHM().get("isonetwostat"));
		this.setStatdoublevalues((int[][])this.getFormHM().get("statdoublevalues"));
	 	this.setVarraylist((List)this.getFormHM().get("varraylist"));
	 	this.setHarraylist((List)this.getFormHM().get("harraylist"));
	 	this.setTotalvalue((String)this.getFormHM().get("totalvalue"));
	 	this.setFilename((String)this.getFormHM().get("filename"));
	 	this.setStatid((String)this.getFormHM().get("statid"));
	 	this.setDbpre((String)this.getFormHM().get("dbpre"));
	 	this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
	 	this.setStrsql((String)this.getFormHM().get("strsql"));
	 	this.setCond_str((String)this.getFormHM().get("cond_str"));
	 	this.setOrder_by((String)this.getFormHM().get("order_by"));
	 	this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
	 	this.setInfosetlist((ArrayList)this.getFormHM().get("infosetlist"));
	 	this.setB0110((String)this.getFormHM().get("b0110"));
	 	this.setE0122((String)this.getFormHM().get("e0122"));
	 	this.setE01a1((String)this.getFormHM().get("e01a1"));
	 	this.setA0101((String)this.getFormHM().get("a0101"));
	 	this.getAnalyseForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
	 	this.setInfodetailfieldlist((ArrayList)this.getFormHM().get("infodetailfieldlist"));
	 	//常用统计id
        this.setLexprId((String)this.getFormHM().get("lexprId"));
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setJfreemap((HashMap)this.getFormHM().get("jfreemap"));
        this.setCategories((String)this.getFormHM().get("categories"));
		this.setStatlistsize((String)this.getFormHM().get("statlistsize"));
		
		this.setCommfacor((String)this.getFormHM().get("commfacor"));
		this.setCommlexr((String)this.getFormHM().get("commlexr"));
		this.setBasesize((String)this.getFormHM().get("basesize"));
		this.setChartTypeList((ArrayList)this.getFormHM().get("chartTypeList"));
		this.setChar_type((String)this.getFormHM().get("char_type"));
		this.setShowstatname((String)this.getFormHM().get("showstatname"));
		this.setShowcharttype((String)this.getFormHM().get("showcharttype"));
		
		this.setXangle((String)this.getFormHM().get("xangle"));
		this.setSubstat((String)this.getFormHM().get("substat"));
		this.setSubIndex((String)this.getFormHM().get("subIndex"));
		this.setLexprList((ArrayList)this.getFormHM().get("lexprList"));
		this.setFactorList((ArrayList)this.getFormHM().get("factorList"));
		this.setShowLegendList((ArrayList)this.getFormHM().get("showLegendList"));
		this.setStatIdList((ArrayList)this.getFormHM().get("statIdList"));
		this.setSubIndexList((ArrayList)this.getFormHM().get("subIndexList"));
		this.setStatNameList((ArrayList)this.getFormHM().get("statNameList"));
		this.setStatOptionList((ArrayList)this.getFormHM().get("statOptionList"));
		this.setStatName((String)this.getFormHM().get("statName"));
		this.setShowtitle((String)this.getFormHM().get("showtitle"));
		this.setDecimalwidth((String)this.getFormHM().get("decimalwidth"));//小数点
		this.setIsneedsum((String)this.getFormHM().get("isneedsum"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("countType", this.countType);
		this.getFormHM().put("totalAvg",this.totalAvg);
		this.getFormHM().put("statid",this.statid);
		this.getFormHM().put("dbpre",this.dbpre);
		this.getFormHM().put("result",this.result);
		this.getFormHM().put("snamedisplay",this.snamedisplay);
		this.getFormHM().put("a_code",this.a_code);
		this.getFormHM().put("v",this.v);
		this.getFormHM().put("h",this.h);
		this.getFormHM().put("a0100",this.a0100);
		this.getFormHM().put("setname",this.setname);
		this.getFormHM().put("lexprId", this.getLexprId());
		this.getFormHM().put("categories", this.categories);
		this.getFormHM().put("substat", this.substat);
		this.getFormHM().put("subIndex", this.subIndex);
		this.getFormHM().put("statName", this.statName);
		this.getFormHM().put("showtitle", this.showtitle);
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/deci/statics/loademploymakeupanalyse".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
			this.getFormHM().put("dbpre", "");
			this.getFormHM().put("lexprId", "");
			this.getFormHM().put("statid", "");
			this.getFormHM().put("a_code", "");
			this.getFormHM().put("subIndex", "");
			this.getFormHM().put("substat", "");
			this.setA_code("");
			this.setDbpre("");
			this.setStatid("");
			this.setLexprId("");
			this.setSubIndex("");
			this.setSubstat("");
        }else if("/general/deci/statics/loademploymakeupanalyse".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
		{
			this.getFormHM().put("dbpre", "");
			this.getFormHM().put("lexprId", "");
			this.getFormHM().put("statid", "");
			this.getFormHM().put("a_code", "");
			this.getFormHM().put("subIndex", "");
			this.setA_code("");
			this.setDbpre("");
			this.setStatid("");
			this.setLexprId("");
			this.setSubIndex("");
		}
		return super.validate(arg0, arg1);
	}
	public String getDbcond() {
		return this.dbcond;
	}

	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}

	public String getDbpre() {
		return this.dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getStatid() {
		return this.statid;
	}

	public void setStatid(String statid) {
		this.statid = statid;
	}

	public ArrayList getStatlist() {
		return this.statlist;
	}

	public void setStatlist(ArrayList statlist) {
		this.statlist = statlist;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getChar_type() {
		return this.char_type;
	}

	public void setChar_type(String char_type) {
		this.char_type = char_type;
	}

	public ArrayList getDatalist() {
		return this.datalist;
	}

	public void setDatalist(ArrayList datalist) {
		this.datalist = datalist;
	}

	public String getSnamedisplay() {
		return this.snamedisplay;
	}

	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}

	public List getHarraylist() {
		return this.harraylist;
	}

	public void setHarraylist(List harraylist) {
		this.harraylist = harraylist;
	}

	public String getIsonetwostat() {
		return this.isonetwostat;
	}

	public void setIsonetwostat(String isonetwostat) {
		this.isonetwostat = isonetwostat;
	}

	public int[][] getStatdoublevalues() {
		return this.statdoublevalues;
	}

	public void setStatdoublevalues(int[][] statdoublevalues) {
		this.statdoublevalues = statdoublevalues;
	}

	public String getTotalvalue() {
		return this.totalvalue;
	}

	public void setTotalvalue(String totalvalue) {
		this.totalvalue = totalvalue;
	}

	public List getVarraylist() {
		return this.varraylist;
	}

	public void setVarraylist(List varraylist) {
		this.varraylist = varraylist;
	}

	public String getA_code() {
		return this.a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getCond_str() {
		return this.cond_str;
	}

	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}

	public String getOrder_by() {
		return this.order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getStrsql() {
		return this.strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getH() {
		return this.h;
	}

	public void setH(String h) {
		this.h = h;
	}

	public String getV() {
		return this.v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getA0100() {
		return this.a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public ArrayList getInfofieldlist() {
		return this.infofieldlist;
	}

	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}

	public ArrayList getInfosetlist() {
		return this.infosetlist;
	}

	public void setInfosetlist(ArrayList infosetlist) {
		this.infosetlist = infosetlist;
	}

	public String getSetname() {
		return this.setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getA0101() {
		return this.a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getB0110() {
		return this.b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public ArrayList getDetailinfolist() {
		return this.detailinfolist;
	}

	public void setDetailinfolist(ArrayList detailinfolist) {
		this.detailinfolist = detailinfolist;
	}

	public String getE0122() {
		return this.e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getE01a1() {
		return this.e01a1;
	}

	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}

	public ArrayList getInfodetailfieldlist() {
		return this.infodetailfieldlist;
	}

	public void setInfodetailfieldlist(ArrayList infodetailfieldlist) {
		this.infodetailfieldlist = infodetailfieldlist;
	}

	public PaginationForm getAnalyseForm() {
		return this.AnalyseForm;
	}

	public void setAnalyseForm(PaginationForm analyseForm) {
		this.AnalyseForm = analyseForm;
	}

	public String getReturnphoto() {
		return this.returnphoto;
	}

	public void setReturnphoto(String returnphoto) {
		this.returnphoto = returnphoto;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public String getLexprId() {
		return lexprId;
	}

	public void setLexprId(String lexprId) {
		this.lexprId = lexprId;
	}

	public ArrayList getChartTypeList() {
		return chartTypeList;
	}

	public void setChartTypeList(ArrayList chartTypeList) {
		this.chartTypeList = chartTypeList;
	}

	public String getChart_type() {
		return char_type;
	}

	public void setChart_type(String chart_type) {
		this.char_type = chart_type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public HashMap getJfreemap() {
		return jfreemap;
	}

	public void setJfreemap(HashMap jfreemap) {
		this.jfreemap = jfreemap;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getStatlistsize() {
		return statlistsize;
	}

	public void setStatlistsize(String statlistsize) {
		this.statlistsize = statlistsize;
	}

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

	public String getShowstatname() {
		return showstatname;
	}

	public void setShowstatname(String showstatname) {
		this.showstatname = showstatname;
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

	public String getBasesize() {
		return basesize;
	}

	public void setBasesize(String basesize) {
		this.basesize = basesize;
	}

	public String getShowcharttype() {
		return showcharttype;
	}

	public void setShowcharttype(String showcharttype) {
		this.showcharttype = showcharttype;
	}

}
