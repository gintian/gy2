package com.hjsj.hrms.actionform.kq.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class KqFormulaForm extends FrameForm {

	

	private PaginationForm kqFormulaForm = new PaginationForm();
	private ArrayList fieldlist = new ArrayList();
	/** 审核公式列表 */
	private ArrayList kqFormulaList = new ArrayList();
	/** 审核公式主键 */
	private String kqFormulaId;
	/** 审核公式名称 */
	private String kqFormulaName;
	/** 审核公式提示信息 */
	private String kqAlert;
	/** 审核公式表达式 */
	private String formula;
	private String optType;
	private String itemid="";

	public String getOptType() {
		return optType;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}



	public PaginationForm getKqFormulaForm() {
		return kqFormulaForm;
	}

	public void setKqFormulaForm(PaginationForm kqFormulaForm) {
		this.kqFormulaForm = kqFormulaForm;
	}
	public ArrayList getKqFormulaList() {
		return kqFormulaList;
	}

	public void setKqFormulaList(ArrayList kqFormulaList) {
		this.kqFormulaList = kqFormulaList;
	}

	public String getKqFormulaId() {
		return kqFormulaId;
	}

	public void setKqFormulaId(String kqFormulaId) {
		this.kqFormulaId = kqFormulaId;
	}

	public String getKqFormulaName() {
		return kqFormulaName;
	}

	public void setKqFormulaName(String kqFormulaName) {
		this.kqFormulaName = kqFormulaName;
	}

	public String getKqAlert() {
		return kqAlert;
	}

	public void setKqAlert(String kqAlert) {
		this.kqAlert = kqAlert;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}


	@Override
    public void inPutTransHM() {
		this.getKqFormulaForm().setList(
				(ArrayList) this.getFormHM().get("itemlist"));
		this.getFormHM().put("formula", this.getFormula());
		this.getFormHM().put("kqFormulaId", this.getKqFormulaId());
		this.getFormHM().put("kqFormulaName", this.getKqFormulaName());
		this.getFormHM().put("kqAlert", this.getKqAlert());
		this.getFormHM().put("optType", this.getOptType());
		this.getFormHM().put("itemid", this.getItemid());
		
	}

	@Override
    public void outPutFormHM() {
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setKqAlert((String)this.getFormHM().get("kqAlert"));
		this.setKqFormulaId((String)this.getFormHM().get("kqFormulaId"));
		this.setKqFormulaName((String)this.getFormHM().get("kqFormulaName"));
		this.setKqFormulaList((ArrayList)this.getFormHM().get("kqFormulaList"));
		this.setItemid((String)this.getFormHM().get("itemid"));
	}

}
