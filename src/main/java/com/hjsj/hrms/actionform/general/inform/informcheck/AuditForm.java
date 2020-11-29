package com.hjsj.hrms.actionform.general.inform.informcheck;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class AuditForm extends FrameForm {
	private String tablestr;
	private String infor;
	private String dbname;
	private String formulastr;
	private String formulaarr;
	private ArrayList fieldlist = new ArrayList();
	private String fieldid;
	private String[] itemid_arr;
	private String[] codearr;
	private String[] itemarr;
	private ArrayList listfield = new ArrayList();
	private String field;
	private String formula;
	private String itemidarr;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTablestr((String)this.getFormHM().get("tablestr"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setFormulastr((String)this.getFormHM().get("formulastr"));
		this.setFormulaarr((String)this.getFormHM().get("formulaarr"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setCodearr((String[])this.getFormHM().get("codearr"));
		this.setField((String)this.getFormHM().get("field"));
		this.setItemarr((String[])this.getFormHM().get("itemarr"));
		this.setListfield((ArrayList)this.getFormHM().get("listfield"));
		this.setItemid_arr((String[])this.getFormHM().get("itemid_arr"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setItemidarr((String)this.getFormHM().get("itemidarr"));
		this.setDbname((String)this.getFormHM().get("dbname"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public String getFormulaarr() {
		return formulaarr;
	}

	public void setFormulaarr(String formulaarr) {
		this.formulaarr = formulaarr;
	}

	public String getFormulastr() {
		return formulastr;
	}

	public void setFormulastr(String formulastr) {
		this.formulastr = formulastr;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String[] getCodearr() {
		return codearr;
	}

	public void setCodearr(String[] codearr) {
		this.codearr = codearr;
	}

	public String[] getItemarr() {
		return itemarr;
	}

	public void setItemarr(String[] itemarr) {
		this.itemarr = itemarr;
	}

	public ArrayList getListfield() {
		return listfield;
	}

	public void setListfield(ArrayList listfield) {
		this.listfield = listfield;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String[] getItemid_arr() {
		return itemid_arr;
	}

	public void setItemid_arr(String[] itemid_arr) {
		this.itemid_arr = itemid_arr;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getItemidarr() {
		return itemidarr;
	}

	public void setItemidarr(String itemidarr) {
		this.itemidarr = itemidarr;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
}
