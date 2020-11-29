package com.hjsj.hrms.actionform.gz.gz_amount.tax;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class PayrollForm extends FrameForm {
	private String gz_table;
	private String salaryid;
	private HttpSession session2;  //xieguiquan add 20100830
	public HttpSession getSession2() {
		return session2;
	}

	public void setSession2(HttpSession session2) {
		this.session2 = session2;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setGz_table((String)this.getFormHM().get("gz_table"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSession2(this.session2);
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("session2",this.session2);
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.setSession2(arg1.getSession());
		return super.validate(arg0, arg1);
	}
	public String getGz_table() {
		return gz_table;
	}

	public void setGz_table(String gz_table) {
		this.gz_table = gz_table;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
}
