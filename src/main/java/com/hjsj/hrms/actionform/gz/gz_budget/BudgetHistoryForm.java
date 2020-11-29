package com.hjsj.hrms.actionform.gz.gz_budget;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class BudgetHistoryForm extends FrameForm {

	private ArrayList list = new ArrayList();
	private PaginationForm budgethistoryForm = new PaginationForm();
	@Override
    public void inPutTransHM() {

		this.getFormHM().put("selectedlist",(ArrayList) this.getBudgethistoryForm().getSelectedList());
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getBudgethistoryForm().setList((ArrayList) this.getFormHM().get("list"));
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public PaginationForm getBudgethistoryForm() {
		return budgethistoryForm;
	}

	public void setBudgethistoryForm(PaginationForm budgethistoryForm) {
		this.budgethistoryForm = budgethistoryForm;
	}
	

}
