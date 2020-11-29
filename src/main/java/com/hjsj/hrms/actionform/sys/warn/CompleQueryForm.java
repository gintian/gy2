/**
 * 
 */
package com.hjsj.hrms.actionform.sys.warn;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class CompleQueryForm extends FrameForm {

	private ArrayList fieldSetList = new ArrayList();
	private ArrayList fieldItemList = new ArrayList();
	private ArrayList codeItemList = new ArrayList();
	
	
	private String fieldSetId;
	private String fieldItemId;
	private String codeItemId;
	private String formula;
	
	private String fieldItems; //函数向导使用(预警中是所以构库指标)
	private ArrayList salarysetlist = new ArrayList();
	private String salaryid;
	private String type="0";

	private String warntype="0";
	private String setid="";
	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getWarntype() {
		return warntype;
	}

	public void setWarntype(String warntype) {
		this.warntype = warntype;
	}


	private String checkflag="";

	@Override
    public void outPutFormHM() {
		if(this.getFormHM().get("type")==null)
			this.setType("0");
		else
    		this.setType((String)this.getFormHM().get("type"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSalarysetlist((ArrayList)this.getFormHM().get("salarysetlist"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("setlist"));
		this.setFieldItemList((ArrayList)this.getFormHM().get("itemlist"));
		this.setFieldItems((String)this.getFormHM().get("fieldItems"));
		this.setFormula((String)this.getFormHM().get("formula"));

		this.setWarntype((String)this.getFormHM().get("warntype"));

		this.setCheckflag((String)this.getFormHM().get("checkflag"));

	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("type",this.getType());
	    this.getFormHM().put("warntype",this.getWarntype());
	    this.getFormHM().put("setid",this.getSetid());
	}
	
	
	public ArrayList getFieldItemList() {
		return fieldItemList;
	}

	public void setFieldItemList(ArrayList fieldItemList) {
		this.fieldItemList = fieldItemList;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String getFieldItemId() {
		return fieldItemId;
	}

	public void setFieldItemId(String fieldItemId) {
		this.fieldItemId = fieldItemId;
	}

	public String getFieldSetId() {
		return fieldSetId;
	}

	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFieldItems() {
		return fieldItems;
	}

	public void setFieldItems(String fieldItems) {
		this.fieldItems = fieldItems;
	}

	public ArrayList getCodeItemList() {
		return codeItemList;
	}

	public void setCodeItemList(ArrayList codeItemList) {
		this.codeItemList = codeItemList;
	}

	public String getCodeItemId() {
		return codeItemId;
	}

	public void setCodeItemId(String codeItemId) {
		this.codeItemId = codeItemId;
	}

	public ArrayList getSalarysetlist() {
		return salarysetlist;
	}

	public void setSalarysetlist(ArrayList salarysetlist) {
		this.salarysetlist = salarysetlist;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	
	
}
