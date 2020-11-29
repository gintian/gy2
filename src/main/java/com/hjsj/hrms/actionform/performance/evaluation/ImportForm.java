package com.hjsj.hrms.actionform.performance.evaluation;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ImportForm extends FrameForm {
	private String planid;
	private ArrayList relatelist;
	private ArrayList choicelist;
	private String formula;
	private ArrayList exprrelatelist;
	private String expression;
	private String flag;
	
	public ArrayList getChoicelist() {
		return choicelist;
	}

	public void setChoicelist(ArrayList choicelist) {
		this.choicelist = choicelist;
	}

	public ArrayList getRelatelist() {
		return relatelist;
	}

	public void setRelatelist(ArrayList relatelist) {
		this.relatelist = relatelist;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("planid",this.getPlanid());
		this.getFormHM().put("formula",this.getFormula());
		this.getFormHM().put("flag",this.getFlag());
	}

	@Override
    public void outPutFormHM() {
		this.setPlanid((String)this.getFormHM().get("planid"));
		this.setRelatelist((ArrayList)this.getFormHM().get("relatelist"));
		this.setChoicelist((ArrayList)this.getFormHM().get("choicelist"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setExprrelatelist((ArrayList)this.getFormHM().get("exprrelatelist"));
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public ArrayList getExprrelatelist() {
		return exprrelatelist;
	}

	public void setExprrelatelist(ArrayList exprrelatelist) {
		this.exprrelatelist = exprrelatelist;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


}
