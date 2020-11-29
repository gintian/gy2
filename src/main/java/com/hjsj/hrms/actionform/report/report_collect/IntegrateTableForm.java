package com.hjsj.hrms.actionform.report.report_collect;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class IntegrateTableForm extends FrameForm {
	private String unitcode = "";

	private String tabid = "";

	private ArrayList provisionTermList = new ArrayList(); // 待选条件列表

	private ArrayList schemeList = new ArrayList(); // 方案列表

	private ArrayList defaultItemList = new ArrayList(); // 默认单位选项

	private String nums = "";

	private String cols = "0";

	private String html = "";

	/** 选中的数组 */
	private String right_fields[];

	private String condition = "";
	private String integrateValues="";
	
	private String rowSerialNo="";
	private String colSerialNo="";
	private String num2s = "";//兼容cs打印 xgq2010 03 15
	
	@Override
    public void outPutFormHM() {
		this.setUnitcode((String) this.getFormHM().get("unitcode"));
		this.setTabid((String) this.getFormHM().get("tabid"));
		this.setProvisionTermList((ArrayList) this.getFormHM().get(
				"provisionTermList"));
		this.setSchemeList((ArrayList) this.getFormHM().get("schemeList"));
		this.setDefaultItemList((ArrayList) this.getFormHM().get(
				"defaultItemList"));
		this.setNums((String) this.getFormHM().get("nums"));
		this.setCols((String) this.getFormHM().get("cols"));
		this.setHtml((String) this.getFormHM().get("html"));
		this.setIntegrateValues((String)this.getFormHM().get("integrateValues"));
		this.setRowSerialNo((String)this.getFormHM().get("rowSerialNo"));
		this.setColSerialNo((String)this.getFormHM().get("colSerialNo"));
		this.setNum2s((String) this.getFormHM().get("num2s"));
	}

	@Override
    public void inPutTransHM() {
		condition="";
		this.getFormHM().put("right_fields", this.getRight_fields());
		if (right_fields != null) {
			for (int i = 0; i < right_fields.length; i++) {
				if (i == 0) {
					condition += right_fields[i];
				} else {
					condition += '`' + right_fields[i];
				}
			}
		}

	}

	public ArrayList getProvisionTermList() {
		return provisionTermList;
	}

	public void setProvisionTermList(ArrayList provisionTermList) {
		this.provisionTermList = provisionTermList;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public ArrayList getSchemeList() {
		return schemeList;
	}

	public void setSchemeList(ArrayList schemeList) {
		this.schemeList = schemeList;
	}

	public ArrayList getDefaultItemList() {
		return defaultItemList;
	}

	public void setDefaultItemList(ArrayList defaultItemList) {
		this.defaultItemList = defaultItemList;
	}

	public String getNums() {
		return nums;
	}

	public void setNums(String nums) {
		this.nums = nums;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getCols() {
		return cols;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getIntegrateValues() {
		return integrateValues;
	}

	public void setIntegrateValues(String integrateValues) {
		this.integrateValues = integrateValues;
	}

	public String getColSerialNo() {
		return colSerialNo;
	}

	public void setColSerialNo(String colSerialNo) {
		this.colSerialNo = colSerialNo;
	}

	public String getRowSerialNo() {
		return rowSerialNo;
	}

	public void setRowSerialNo(String rowSerialNo) {
		this.rowSerialNo = rowSerialNo;
	}

	public String getNum2s() {
		return num2s;
	}

	public void setNum2s(String num2s) {
		this.num2s = num2s;
	}

}
