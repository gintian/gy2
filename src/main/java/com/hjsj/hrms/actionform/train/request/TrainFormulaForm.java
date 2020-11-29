package com.hjsj.hrms.actionform.train.request;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class TrainFormulaForm extends FrameForm {

	private PaginationForm trainFormulaForm = new PaginationForm();
	private ArrayList fieldlist = new ArrayList();
	/** 审核公式列表 */
	private ArrayList trainFormulaList = new ArrayList();
	/** 审核公式主键 */
	private String trainFormulaId;
	/** 审核公式名称 */
	private String trainFormulaName;
	/** 审核公式提示信息 */
	private String trainAlert;
	/** 审核公式表达式 */
	private String formula;
	private String optType;
	private String itemid = "";

	public PaginationForm getTrainFormulaForm() {
		return trainFormulaForm;
	}

	public void setTrainFormulaForm(PaginationForm trainFormulaForm) {
		this.trainFormulaForm = trainFormulaForm;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getTrainFormulaList() {
		return trainFormulaList;
	}

	public void setTrainFormulaList(ArrayList trainFormulaList) {
		this.trainFormulaList = trainFormulaList;
	}

	public String getTrainFormulaId() {
		return trainFormulaId;
	}

	public void setTrainFormulaId(String trainFormulaId) {
		this.trainFormulaId = trainFormulaId;
	}

	public String getTrainFormulaName() {
		return trainFormulaName;
	}

	public void setTrainFormulaName(String trainFormulaName) {
		this.trainFormulaName = trainFormulaName;
	}

	public String getTrainAlert() {
		return trainAlert;
	}

	public void setTrainAlert(String trainAlert) {
		this.trainAlert = trainAlert;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	@Override
    public void inPutTransHM() {
		this.getTrainFormulaForm().setList(
				(ArrayList) this.getFormHM().get("itemlist"));
		this.getFormHM().put("formula", this.getFormula());
		this.getFormHM().put("trainFormulaId", this.getTrainFormulaId());
		this.getFormHM().put("trainFormulaName", this.getTrainFormulaName());
		this.getFormHM().put("trainAlert", this.getTrainAlert());
		this.getFormHM().put("optType", this.getOptType());
		this.getFormHM().put("itemid", this.getItemid());

	}

	@Override
    public void outPutFormHM() {
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setFormula((String) this.getFormHM().get("formula"));
		this.setTrainAlert((String) this.getFormHM().get("trainAlert"));
		this.setTrainFormulaId((String) this.getFormHM().get("trainFormulaId"));
		this.setTrainFormulaName((String) this.getFormHM().get("trainFormulaName"));
		this.setTrainFormulaList((ArrayList) this.getFormHM().get("trainFormulaList"));
		this.setItemid((String) this.getFormHM().get("itemid"));
	}

}
