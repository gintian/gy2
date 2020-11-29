package com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class BudgetSysForm extends FrameForm{

	private PaginationForm budgetSysListform=new PaginationForm();
	
	String dblist_name = "";//存储人员库的汉字，dblist存储的是代码
	String units_name = "";//存储的是单位汉字，units存储的是代码
	
	String kindstr = "";//预算表分类
	String rylb_codeset = "";//人员类别代码类
	String unitmenu = ""; //归属单位指标
	String dblist = "";//参与预算的人员库
	String range = "";//参与预算的人员范围
	String units = "";//参与预算的单位 
	String createTXrecord = "";//是否生成退休记录0=否，1=是
	String datatoze = "";//预算数据到总额
	
	String txCode="";//退休人员编码
	String ysze_set = "";//预算总额子集
	String ysze_idx_menu = "";//预算索引指标
	String ysze_ze_menu = "";//预算总额指标
	String ysze_status_menu = "";//状态
	
	String ysparam_set = "";//预算参数子集
	String ysparam_idx_menu = "";//预算索引指标
	String ysparam_newmonth_menu = "";//新员工入职月份指标
	
	ArrayList txrecordList = new ArrayList();
	ArrayList rylbList = new ArrayList();
	ArrayList unitList = new ArrayList();
	ArrayList budgetSetList = new ArrayList();
	ArrayList budgetIndexList = new ArrayList();
	ArrayList budgetTotalList = new ArrayList();
	ArrayList spStatusList = new ArrayList();
	ArrayList budgetParamSetList = new ArrayList();
	ArrayList budgetIndexFieldList = new ArrayList();
	ArrayList employeeList = new ArrayList();
	
	ArrayList selectDblist = new ArrayList(); 
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dblist_name", this.getDblist_name());
		this.getFormHM().put("units_name", this.getUnits_name());
		
		this.getFormHM().put("kindstr",this.getKindstr());
		this.getFormHM().put("rylb_codeset",this.getRylb_codeset());
		this.getFormHM().put("unitmenu",this.getUnitmenu());
		this.getFormHM().put("dblist",this.getDblist());
		this.getFormHM().put("txCode",this.getTxCode());
		this.getFormHM().put("range",this.getRange());
		this.getFormHM().put("units",this.getUnits());
		this.getFormHM().put("createTXrecord",this.getCreateTXrecord());
		this.getFormHM().put("datatoze",this.getDatatoze());
		
		this.getFormHM().put("ysze_set",this.getYsze_set());
		this.getFormHM().put("ysze_idx_menu",this.getYsze_idx_menu());
		this.getFormHM().put("ysze_ze_menu",this.getYsze_ze_menu());
		this.getFormHM().put("ysze_status_menu",this.getYsze_status_menu());
		
		this.getFormHM().put("ysparam_set",this.getYsparam_set());
		this.getFormHM().put("ysparam_idx_menu",this.ysparam_idx_menu);
		this.getFormHM().put("ysparam_newmonth_menu",this.getYsparam_newmonth_menu());
		
		this.getFormHM().put("rylbList",this.getRylbList());
		this.getFormHM().put("txrecordList",this.getTxrecordList());
		this.getFormHM().put("unitList",this.getUnitList());
		this.getFormHM().put("budgetSetList",this.getBudgetSetList());
		this.getFormHM().put("budgetIndexList",this.getBudgetIndexList());
		
		this.getFormHM().put("budgetTotalList",this.getBudgetTotalList());
		this.getFormHM().put("spStatusList",this.getSpStatusList());
		this.getFormHM().put("budgetParamSetList",this.getBudgetParamSetList());
		this.getFormHM().put("budgetIndexFieldList",this.getBudgetIndexFieldList());
		this.getFormHM().put("employeeList",this.getEmployeeList());
		this.getFormHM().put("selectDblist", this.getSelectDblist());
	}

	@Override
    public void outPutFormHM() {
		this.setDblist_name((String)this.getFormHM().get("dblist_name"));
		this.setUnits_name((String)this.getFormHM().get("units_name"));
		
		this.getBudgetSysListform().setList((ArrayList)this.getFormHM().get("selectDblist"));
		this.setKindstr((String)this.getFormHM().get("kindstr"));
		this.setRylb_codeset((String)this.getFormHM().get("rylb_codeset"));
		this.setUnitmenu((String)this.getFormHM().get("unitmenu"));
		this.setDblist((String)this.getFormHM().get("dblist"));
		this.setRange((String)this.getFormHM().get("range"));
		this.setUnits((String)this.getFormHM().get("units"));
		this.setCreateTXrecord((String)this.getFormHM().get("createTXrecord"));
		this.setDatatoze((String)this.getFormHM().get("datatoze"));
		
		this.setYsze_set((String)this.getFormHM().get("ysze_set"));
		this.setYsze_idx_menu((String)this.getFormHM().get("ysze_idx_menu"));
		this.setYsze_ze_menu((String)this.getFormHM().get("ysze_ze_menu"));
		this.setYsze_status_menu((String)this.getFormHM().get("ysze_status_menu"));
		this.setTxCode((String)this.getFormHM().get("txCode"));
		
		this.setYsparam_set((String)this.getFormHM().get("ysparam_set"));
		this.setYsparam_idx_menu((String)this.getFormHM().get("ysparam_idx_menu"));
		this.setYsparam_newmonth_menu((String)this.getFormHM().get("ysparam_newmonth_menu"));
		
		this.setRylbList((ArrayList)this.getFormHM().get("rylbList"));
		this.setUnitList((ArrayList)this.getFormHM().get("unitList"));
		this.setBudgetSetList((ArrayList)this.getFormHM().get("budgetSetList"));
		this.setBudgetIndexList((ArrayList)this.getFormHM().get("budgetIndexList"));
		
		this.setBudgetTotalList((ArrayList)this.getFormHM().get("budgetTotalList"));
		this.setTxrecordList((ArrayList)this.getFormHM().get("txrecordList"));
		this.setSpStatusList((ArrayList)this.getFormHM().get("spStatusList"));
		this.setBudgetParamSetList((ArrayList)this.getFormHM().get("budgetParamSetList"));
		this.setBudgetIndexFieldList((ArrayList)this.getFormHM().get("budgetIndexFieldList"));
		this.setEmployeeList((ArrayList)this.getFormHM().get("employeeList"));
		this.setSelectDblist((ArrayList)this.getFormHM().get("selectDblist"));
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1){
    	super.reset(arg0, arg1);
    	this.setCreateTXrecord("0");
    	this.setDatatoze("0");
    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    return super.validate(arg0, arg1);
	    }
	
	public String getKindstr() {
		return kindstr;
	}

	public void setKindstr(String kindstr) {
		this.kindstr = kindstr;
	}

	public String getRylb_codeset() {
		return rylb_codeset;
	}

	public void setRylb_codeset(String rylb_codeset) {
		this.rylb_codeset = rylb_codeset;
	}

	public String getUnitmenu() {
		return unitmenu;
	}

	public void setUnitmenu(String unitmenu) {
		this.unitmenu = unitmenu;
	}
	
	public String getDblist() {
		return dblist;
	}

	public void setDblist(String dblist) {
		this.dblist = dblist;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getCreateTXrecord() {
		return createTXrecord;
	}

	public void setCreateTXrecord(String createTXrecord) {
		this.createTXrecord = createTXrecord;
	}

	public String getDatatoze() {
		return datatoze;
	}

	public void setDatatoze(String datatoze) {
		this.datatoze = datatoze;
	}

	public String getYsze_set() {
		return ysze_set;
	}

	public void setYsze_set(String ysze_set) {
		this.ysze_set = ysze_set;
	}

	public String getYsze_idx_menu() {
		return ysze_idx_menu;
	}

	public void setYsze_idx_menu(String ysze_idx_menu) {
		this.ysze_idx_menu = ysze_idx_menu;
	}

	public String getYsze_ze_menu() {
		return ysze_ze_menu;
	}

	public void setYsze_ze_menu(String ysze_ze_menu) {
		this.ysze_ze_menu = ysze_ze_menu;
	}

	public String getYsze_status_menu() {
		return ysze_status_menu;
	}

	public void setYsze_status_menu(String ysze_status_menu) {
		this.ysze_status_menu = ysze_status_menu;
	}

	public String getYsparam_set() {
		return ysparam_set;
	}

	public void setYsparam_set(String ysparam_set) {
		this.ysparam_set = ysparam_set;
	}

	public String getYsparam_idx_menu() {
		return ysparam_idx_menu;
	}

	public void setYsparam_idx_menu(String ysparam_idx_menu) {
		this.ysparam_idx_menu = ysparam_idx_menu;
	}


	public String getYsparam_newmonth_menu() {
		return ysparam_newmonth_menu;
	}

	public void setYsparam_newmonth_menu(String ysparam_newmonth_menu) {
		this.ysparam_newmonth_menu = ysparam_newmonth_menu;
	}

	public ArrayList getRylbList() {
		return rylbList;
	}

	public void setRylbList(ArrayList rylbList) {
		this.rylbList = rylbList;
	}

	public ArrayList getUnitList() {
		return unitList;
	}

	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}

	public ArrayList getBudgetSetList() {
		return budgetSetList;
	}

	public void setBudgetSetList(ArrayList budgetSetList) {
		this.budgetSetList = budgetSetList;
	}

	public ArrayList getBudgetIndexList() {
		return budgetIndexList;
	}

	public void setBudgetIndexList(ArrayList budgetIndexList) {
		this.budgetIndexList = budgetIndexList;
	}

	public ArrayList getBudgetTotalList() {
		return budgetTotalList;
	}

	public void setBudgetTotalList(ArrayList budgetTotalList) {
		this.budgetTotalList = budgetTotalList;
	}

	public ArrayList getSpStatusList() {
		return spStatusList;
	}

	public void setSpStatusList(ArrayList spStatusList) {
		this.spStatusList = spStatusList;
	}

	public ArrayList getBudgetParamSetList() {
		return budgetParamSetList;
	}

	public void setBudgetParamSetList(ArrayList budgetParamSetList) {
		this.budgetParamSetList = budgetParamSetList;
	}

	public ArrayList getBudgetIndexFieldList() {
		return budgetIndexFieldList;
	}

	public void setBudgetIndexFieldList(ArrayList budgetIndexFieldList) {
		this.budgetIndexFieldList = budgetIndexFieldList;
	}

	public ArrayList getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(ArrayList employeeList) {
		this.employeeList = employeeList;
	}

	public ArrayList getSelectDblist() {
		return selectDblist;
	}

	public void setSelectDblist(ArrayList selectDblist) {
		this.selectDblist = selectDblist;
	}

	public PaginationForm getBudgetSysListform() {
		return budgetSysListform;
	}

	public void setBudgetSysListform(PaginationForm budgetSysListform) {
		this.budgetSysListform = budgetSysListform;
	}

	public String getDblist_name() {
		return dblist_name;
	}

	public void setDblist_name(String dblist_name) {
		this.dblist_name = dblist_name;
	}

	public String getUnits_name() {
		return units_name;
	}

	public void setUnits_name(String units_name) {
		this.units_name = units_name;
	}

	public String getTxCode() {
		return txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}

	public ArrayList getTxrecordList() {
		return txrecordList;
	}

	public void setTxrecordList(ArrayList txrecordList) {
		this.txrecordList = txrecordList;
	}
	
}
