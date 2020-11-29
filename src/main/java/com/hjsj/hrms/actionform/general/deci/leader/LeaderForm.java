package com.hjsj.hrms.actionform.general.deci.leader;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class LeaderForm extends FrameForm {

	private String a_code;
	private String code;
	private String kind;
	private String dbpre;
	private ArrayList dbprelist;
	private ArrayList fieldlist=new ArrayList();
	private String statid;
	private ArrayList statlist=new ArrayList();
	private String char_type="12";     //图例形状特征
	private ArrayList datalist=new ArrayList(); //统计结果数据
	private String isonetwostat;  //是一维或者二维统计
	private int[][] statdoublevalues;    //二维数据
	private List varraylist=new ArrayList();
	private List harraylist=new ArrayList();
	private String totalvalue;    //总数
	private String snamedisplay;  //统计项标题名
	private String v;
	private String h;
	private String analyse_setid;
	private String analyse_codesetid;
	private String analyse_value;
	private String order_by;
	private String strsql;
	private String cond_str;
	private String leader_type;
	private String card_id;	
	private String unit_card;
	private String is_view_card;
	private String columns;
	private ArrayList unitlist = new ArrayList();
	private ArrayList unitfilelist = new ArrayList();
	private PaginationForm roleListForm=new PaginationForm();
	private String select_file;
	private String unitcard;
	private String loadtype;
	private String display;
	private String gcond;
	private String display_field;
	private String decimal;
	private String sformula;
	private double[][] statdoublevaluess;
	private String param;
	private String returnvalue = "1";
	private String onlychart = "0";
	
	private String xangle;  //add by wangchaoqun on 2014-10-14
	
	CardTagParamView cardparam=new CardTagParamView();
	public CardTagParamView getCardparam() {
		return cardparam;
	}

	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	public String getIs_view_card() {
		return is_view_card;
	}

	public void setIs_view_card(String is_view_card) {
		this.is_view_card = is_view_card;
	}

	public String getUnit_card() {
		return unit_card;
	}

	public void setUnit_card(String unit_card) {
		this.unit_card = unit_card;
	}

	public String getLeader_type() {
		return leader_type;
	}

	public void setLeader_type(String leader_type) {
		this.leader_type = leader_type;
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

	public ArrayList getDatalist() {
		return datalist;
	}

	public void setDatalist(ArrayList datalist) {
		this.datalist = datalist;
	}

	public String getH() {
		return h;
	}

	public void setH(String h) {
		this.h = h;
	}

	public List getHarraylist() {
		return harraylist;
	}

	public void setHarraylist(List harraylist) {
		this.harraylist = harraylist;
	}

	public String getIsonetwostat() {
		return isonetwostat;
	}

	public void setIsonetwostat(String isonetwostat) {
		this.isonetwostat = isonetwostat;
	}

	public int[][] getStatdoublevalues() {
		return statdoublevalues;
	}

	public void setStatdoublevalues(int[][] statdoublevalues) {
		this.statdoublevalues = statdoublevalues;
	}

	public String getTotalvalue() {
		return totalvalue;
	}

	public void setTotalvalue(String totalvalue) {
		this.totalvalue = totalvalue;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public List getVarraylist() {
		return varraylist;
	}

	public void setVarraylist(List varraylist) {
		this.varraylist = varraylist;
	}

	public String getChar_type() {
		return char_type;
	}

	public void setChar_type(String char_type) {
		this.char_type = char_type;
	}

	public String getStatid() {
		return statid;
	}

	public void setStatid(String statid) {
		this.statid = statid;
	}

	public ArrayList getStatlist() {
		return statlist;
	}

	public void setStatlist(ArrayList statlist) {
		this.statlist = statlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setOnlychart((String)this.getFormHM().get("onlychart"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setKind((String)this.getFormHM().get("kind"));
	    this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	    this.setStatid((String)this.getFormHM().get("statid"));
	    this.setStatlist((ArrayList)this.getFormHM().get("statlist"));
	    this.setDbpre((String)this.getFormHM().get("dbpre"));
	    this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
	    this.setDatalist((ArrayList)this.getFormHM().get("datalist"));
		this.setIsonetwostat((String)this.getFormHM().get("isonetwostat"));
		this.setStatdoublevalues((int[][])this.getFormHM().get("statdoublevalues"));
	 	this.setVarraylist((List)this.getFormHM().get("varraylist"));
	 	this.setHarraylist((List)this.getFormHM().get("harraylist"));
	 	this.setTotalvalue((String)this.getFormHM().get("totalvalue"));
	 	this.setSnamedisplay((String)this.getFormHM().get("snamedisplay"));
	 	this.setAnalyse_setid((String)this.getFormHM().get("analyse_setid"));
	 	this.setAnalyse_codesetid((String)this.getFormHM().get("analyse_codesetid"));
	 	this.setAnalyse_value((String)this.getFormHM().get("analyse_value"));
	 	this.setOrder_by((String)this.getFormHM().get("order_by"));
	 	this.setCond_str((String)this.getFormHM().get("cond_str"));
	 	this.setStrsql((String)this.getFormHM().get("strsql"));
	 	this.setLeader_type((String)this.getFormHM().get("leader_type"));
	 	this.setCard_id((String)this.getFormHM().get("card_id"));
	 	this.setUnit_card((String)this.getFormHM().get("unit_card"));
	 	this.setIs_view_card((String)this.getFormHM().get("is_view_card"));
	 	this.setColumns((String)this.getFormHM().get("columns"));
	 	this.setUnitlist((ArrayList)this.getFormHM().get("unitlist"));
	 	this.setUnitfilelist((ArrayList)this.getFormHM().get("unitfilelist"));
	 	this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
	 	this.setSelect_file((String)this.getFormHM().get("select_file"));
	 	this.setUnitcard((String)this.getFormHM().get("unitcard"));
	 	this.setLoadtype((String)this.getFormHM().get("loadtype"));
	 	this.setDisplay((String)this.getFormHM().get("display"));
	 	this.setGcond((String)this.getFormHM().get("gcond"));
	 	this.setDisplay_field((String)this.getFormHM().get("display_field"));
	 	this.setDecimal((String)this.getFormHM().get("decimal"));
	 	this.setSformula((String)this.getFormHM().get("sformula"));
	 	this.setXangle((String)this.getFormHM().get("xangle"));
	 	this.setStatdoublevaluess((double[][])this.getFormHM().get("statdoublevaluess"));
	 	
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("onlychart",this.getOnlychart());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("statid",this.getStatid());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("char_type",this.getChar_type());
		this.getFormHM().put("dbpre",this.getDbpre());
		this.getFormHM().put("v",this.v);
		this.getFormHM().put("h",this.h);
		this.getFormHM().put("analyse_setid",this.getAnalyse_setid());
		this.getFormHM().put("analyse_codesetid",this.getAnalyse_codesetid());
		this.getFormHM().put("analyse_value",this.getAnalyse_value());
		this.getFormHM().put("leader_type",this.getLeader_type());
		this.getFormHM().put("selectedList",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("param",param);
	}

	/**
	 * @param args
	 */
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/general/deci/leader/leaderframe".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
           this.getFormHM().clear();
           this.setA_code("");
           this.getFormHM().put("a_code","");
        }
        if("/general/deci/leader/analysedata".equals(arg0.getPath())&&arg1.getParameter("b_double")!=null){
        	 if(this.getPagination()!=null)
             	this.getPagination().firstPage();
        }
        return super.validate(arg0, arg1);
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

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public ArrayList getDbprelist() {
		return dbprelist;
	}

	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}

	public String getSnamedisplay() {
		return snamedisplay;
	}

	public void setSnamedisplay(String snamedisplay) {
		this.snamedisplay = snamedisplay;
	}

	

	public String getAnalyse_codesetid() {
		return analyse_codesetid;
	}

	public void setAnalyse_codesetid(String analyse_codesetid) {
		this.analyse_codesetid = analyse_codesetid;
	}

	public String getAnalyse_setid() {
		return analyse_setid;
	}

	public void setAnalyse_setid(String analyse_setid) {
		this.analyse_setid = analyse_setid;
	}

	public String getAnalyse_value() {
		return analyse_value;
	}

	public void setAnalyse_value(String analyse_value) {
		this.analyse_value = analyse_value;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public ArrayList getUnitlist() {
		return unitlist;
	}

	public void setUnitlist(ArrayList unitlist) {
		this.unitlist = unitlist;
	}

	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public ArrayList getUnitfilelist() {
		return unitfilelist;
	}

	public void setUnitfilelist(ArrayList unitfilelist) {
		this.unitfilelist = unitfilelist;
	}

	public String getSelect_file() {
		return select_file;
	}

	public void setSelect_file(String select_file) {
		this.select_file = select_file;
	}

	public String getUnitcard() {
		return unitcard;
	}

	public void setUnitcard(String unitcard) {
		this.unitcard = unitcard;
	}

	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getGcond() {
		return gcond;
	}

	public void setGcond(String gcond) {
		this.gcond = gcond;
	}

	public String getDisplay_field() {
		return display_field;
	}

	public void setDisplay_field(String display_field) {
		this.display_field = display_field;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public String getSformula() {
		return sformula;
	}

	public void setSformula(String sformula) {
		this.sformula = sformula;
	}

	public double[][] getStatdoublevaluess() {
		return statdoublevaluess;
	}

	public void setStatdoublevaluess(double[][] statdoublevaluess) {
		this.statdoublevaluess = statdoublevaluess;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

	public String getXangle() {
		return xangle;
	}

	public void setXangle(String xangle) {
		this.xangle = xangle;
	}

	public String getOnlychart() {
		return onlychart;
	}

	public void setOnlychart(String onlychart) {
		this.onlychart = onlychart;
	}
}
