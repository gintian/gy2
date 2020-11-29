package com.hjsj.hrms.actionform.stat;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class InfoSetupForm extends FrameForm {

	private String strsql;
	private String cond_str;
	private String columns;
	private String order_by;
	private String inforkind;
	private ArrayList unitdatalist=new ArrayList();//按月变化单位子集
	private String unit="";
	private String seasonal="";
	private ArrayList seansonaldatalist=new ArrayList();
	private ArrayList volist=new ArrayList();
	private ArrayList unittargetlist=new ArrayList();
	private String anydate;
	private String anyunit;
	private String treeCode;
	private PaginationForm infoSetupForm = new PaginationForm();
	private PaginationForm setinfoSetupForm = new PaginationForm();
	
	/**人员库*/
	private String dbname;
	/**人员库集合*/
	private ArrayList dbList;
	/**分类查询条件*/
	private String condition;
	/**分类查询条件集合*/
	private ArrayList condList;
	/**分类查询条件集合*/
	private ArrayList tempCondList;
	/**常用条件设置完成后的标志*/
	private String flag;
	/**选择框的html代码*/
	private String html;
	
	
	/**来自常用统计报表类型*/
	private String chart_type;
	private String minvalue;
	private String maxvalue;
	private String valve;
	private String outsminvalue;
	private String outsmaxvalue;
	
	

	private ArrayList noitems = new ArrayList();
	private ArrayList yesitems = new ArrayList();
	private String title;
	private String sformula;
	private String itemid;
	private ArrayList fieldsetlist= new ArrayList();
	private String setid;
	private String decimalwidth="2";
	private String unit_level;
	private ArrayList unit_levellist=new ArrayList();
	private String dept_level;
	private ArrayList dept_levellist=new ArrayList();
	private String auto;
	private String ctrl;
	/**type=1或type为空是一维，type=2是二维*/
	private String type = "";
	
	private String snameid = "";
	
	public String getSnameid() {
		return snameid;
	}

	public void setSnameid(String snameid) {
		this.snameid = snameid;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public ArrayList getDbList() {
		return dbList;
	}

	public void setDbList(ArrayList dbList) {
		this.dbList = dbList;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public ArrayList getCondList() {
		return condList;
	}

	public void setCondList(ArrayList condList) {
		this.condList = condList;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("snameid", snameid);
		this.getFormHM().put("minvalue", minvalue);
		this.getFormHM().put("maxvalue", maxvalue);
		this.getFormHM().put("outsminvalue", outsminvalue);
		this.getFormHM().put("outsmaxvalue", outsmaxvalue);
		this.getFormHM().put("valve", valve);
		this.getFormHM().put("chart_type", chart_type);
		this.getFormHM().put("inforkind", inforkind);
		this.getFormHM().put("unit", unit);
		this.getFormHM().put("seasonal", seasonal);
		this.getFormHM().put("volist", volist);
		this.getFormHM().put("dbname", dbname);
		this.getFormHM().put("condition", condition);
		this.setFlag("0");
		this.getFormHM().put("selectList", this.getSetinfoSetupForm().getSelectedList());
		this.getFormHM().put("itemid",itemid);
	}

	@Override
    public void outPutFormHM() {
		this.setSnameid((String)this.getFormHM().get("snameid"));
		this.setOutsminvalue((String)this.getFormHM().get("outsminvalue"));
		this.setOutsmaxvalue((String)this.getFormHM().get("outsmaxvalue"));
		this.setMinvalue((String)this.getFormHM().get("minvalue"));
		this.setMaxvalue((String)this.getFormHM().get("maxvalue"));
		this.setValve((String)this.getFormHM().get("valve"));
		this.setChart_type((String)this.getFormHM().get("chart_type"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setUnitdatalist((ArrayList)this.getFormHM().get("unitdatalist"));
		this.setUnit((String)this.getFormHM().get("unit"));
		this.setSeasonal((String)this.getFormHM().get("seasonal"));
		this.setSeansonaldatalist((ArrayList)this.getFormHM().get("seansonaldatalist"));
		this.setVolist((ArrayList)this.getFormHM().get("volist"));
		this.setUnittargetlist((ArrayList)this.getFormHM().get("unittargetlist"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.getInfoSetupForm().setList((ArrayList)this.getFormHM().get("list"));
		this.setDbList((ArrayList) this.getFormHM().get("dbList"));
		this.setDbname((String) this.getFormHM().get("dbname"));
		this.setCondition((String) this.getFormHM().get("condition"));
		this.setCondList((ArrayList) this.getFormHM().get("condList"));
		if (this.getFormHM().get("tempCondList") == null) {
			this.setTempCondList(new ArrayList());
		} else {
			this.setTempCondList((ArrayList) this.getFormHM().get("tempCondList"));
		}
		this.getSetinfoSetupForm().setList(this.getCondList());
		this.setFlag((String) this.getFormHM().get("flag"));
		this.getFormHM().remove("flag");
		this.setHtml((String) this.getFormHM().get("html"));
		this.setInforkind((String) this.getFormHM().get("inforkind"));
		
		this.setNoitems((ArrayList)this.getFormHM().get("noitems"));
		this.setYesitems((ArrayList)this.getFormHM().get("yesitems"));
		this.setSformula((String)this.getFormHM().get("sformula"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setUnit_levellist((ArrayList)this.getFormHM().get("unit_levellist"));
		this.setDept_levellist((ArrayList)this.getFormHM().get("dept_levellist"));
		this.setAuto((String)this.getFormHM().get("auto"));
		this.setUnit_level((String)this.getFormHM().get("unit_level"));
		this.setDept_level((String)this.getFormHM().get("dept_level"));
		this.setCtrl((String)this.getFormHM().get("ctrl"));
	}

	
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public String getCond_str() {
		return cond_str;
	}
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	public String getInforkind() {
		return inforkind;
	}
	public void setInforkind(String inforkind) {
		this.inforkind = inforkind;
	}

	public ArrayList getUnitdatalist() {
		return unitdatalist;
	}

	public void setUnitdatalist(ArrayList unitdatalist) {
		this.unitdatalist = unitdatalist;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSeasonal() {
		return seasonal;
	}

	public void setSeasonal(String seasonal) {
		this.seasonal = seasonal;
	}

	public ArrayList getSeansonaldatalist() {
		return seansonaldatalist;
	}

	public void setSeansonaldatalist(ArrayList seansonaldatalist) {
		this.seansonaldatalist = seansonaldatalist;
	}

	public ArrayList getVolist() {
		return volist;
	}

	public void setVolist(ArrayList volist) {
		this.volist = volist;
	}

	public ArrayList getUnittargetlist() {
		return unittargetlist;
	}

	public void setUnittargetlist(ArrayList unittargetlist) {
		this.unittargetlist = unittargetlist;
	}

	public String getAnydate() {
		return anydate;
	}

	public void setAnydate(String anydate) {
		this.anydate = anydate;
	}

	public String getAnyunit() {
		return anyunit;
	}

	public void setAnyunit(String anyunit) {
		this.anyunit = anyunit;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public PaginationForm getInfoSetupForm() {
		return infoSetupForm;
	}

	public void setInfoSetupForm(PaginationForm infoSetupForm) {
		this.infoSetupForm = infoSetupForm;
	}

	public PaginationForm getSetinfoSetupForm() {
		return setinfoSetupForm;
	}

	public void setSetinfoSetupForm(PaginationForm setinfoSetupForm) {
		this.setinfoSetupForm = setinfoSetupForm;
	}

	public ArrayList getTempCondList() {
		return tempCondList;
	}

	public void setTempCondList(ArrayList tempCondList) {
		this.tempCondList = tempCondList;
	}

	public ArrayList getNoitems() {
		return noitems;
	}

	public void setNoitems(ArrayList noitems) {
		this.noitems = noitems;
	}

	public ArrayList getYesitems() {
		return yesitems;
	}

	public void setYesitems(ArrayList yesitems) {
		this.yesitems = yesitems;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getSformula() {
		return sformula;
	}

	public void setSformula(String sformula) {
		this.sformula = sformula;
	}

	public String getDecimalwidth() {
		return decimalwidth;
	}

	public void setDecimalwidth(String decimalwidth) {
		this.decimalwidth = decimalwidth;
	}

	public String getUnit_level() {
		return unit_level;
	}

	public void setUnit_level(String unit_level) {
		this.unit_level = unit_level;
	}

	public String getDept_level() {
		return dept_level;
	}

	public void setDept_level(String dept_level) {
		this.dept_level = dept_level;
	}

	public String getAuto() {
		return auto;
	}

	public void setAuto(String auto) {
		this.auto = auto;
	}

	public ArrayList getUnit_levellist() {
		return unit_levellist;
	}

	public void setUnit_levellist(ArrayList unit_levellist) {
		this.unit_levellist = unit_levellist;
	}

	public ArrayList getDept_levellist() {
		return dept_levellist;
	}

	public void setDept_levellist(ArrayList dept_levellist) {
		this.dept_levellist = dept_levellist;
	}

	public String getCtrl() {
		return ctrl;
	}

	public void setCtrl(String ctrl) {
		this.ctrl = ctrl;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChart_type() {
		return chart_type;
	}

	public void setChart_type(String chart_type) {
		this.chart_type = chart_type;
	}

	public String getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(String minvalue) {
		this.minvalue = minvalue;
	}

	public String getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(String maxvalue) {
		this.maxvalue = maxvalue;
	}

	public String getValve() {
		return valve;
	}

	public void setValve(String valve) {
		this.valve = valve;
	}

	public String getOutsminvalue() {
		return outsminvalue;
	}

	public void setOutsminvalue(String outsminvalue) {
		this.outsminvalue = outsminvalue;
	}

	public String getOutsmaxvalue() {
		return outsmaxvalue;
	}

	public void setOutsmaxvalue(String outsmaxvalue) {
		this.outsmaxvalue = outsmaxvalue;
	}




}
