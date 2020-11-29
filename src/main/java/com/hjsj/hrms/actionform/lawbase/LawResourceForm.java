package com.hjsj.hrms.actionform.lawbase;

import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-11:14:22:03
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class LawResourceForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
	private DateStyle first_date = new DateStyle();

	private String flag = "0";

	
	private RecordVo lawResourcevo = new RecordVo("law_base_file");

	

	private PaginationForm lawResourceForm = new PaginationForm();

	@Override
    public void outPutFormHM() {
		this.setLawResourcevo((RecordVo) this.getFormHM().get("lawResourceTb"));
		this.getLawResourceForm().setList(
				(ArrayList) this.getFormHM().get("lawResourcelist"));
		this.setFirst_date((DateStyle) this.getFormHM().get("first_date"));

	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getLawResourceForm().getSelectedList());
		this.getFormHM().put("lawResourceov", this.getLawResourcevo());
		this.getFormHM().put("lawResourceTb", this.getLawResourcevo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("first_date", this.getFirst_date());

	}

	/**
	 * @return Returns the lawResourceForm.
	 */
	public PaginationForm getLawResourceForm() {
		return lawResourceForm;
	}

	/**
	 * @param lawResourceForm
	 *            The lawResourceForm to set.
	 */
	public void setLawResourceForm(PaginationForm lawResourceForm) {
		this.lawResourceForm = lawResourceForm;
	}

	/**
	 * @return Returns the lawResourcevo.
	 */
	public RecordVo getLawResourcevo() {
		return lawResourcevo;
	}

	/**
	 * @param lawResourcevo
	 *            The lawResourcevo to set.
	 */
	public void setLawResourcevo(RecordVo lawResourcevo) {
		this.lawResourcevo = lawResourcevo;
	}

	/*
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
	}

	/*
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		/**
		 * 执行搜索
		 */
		if ("/selfservice/lawresource/lawsearch".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {

			this.setFlag("1");
			//重置操作

			this.getFormHM().put("flag", this.getFlag());

		}
		/**
		 * 进入查询录入页面
		 */

		if ("/selfservice/lawresource/lawresource".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {

			this.setFlag("0");
			this.getFormHM().put("flag", this.getFlag());
			this.getLawResourcevo().clearValues();

		}

		/**
		 * 进入搜索结果主页面
		 */
		if ("/selfservice/lawresource/lawresource".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("0");
			this.getFormHM().put("flag", this.getFlag());
		}

		return super.validate(arg0, arg1);
	}

	public String getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            The flag to set.
	 */

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public DateStyle getFirst_date() {
		return first_date;
	}

	public void setFirst_date(DateStyle first_date) {
		this.first_date = first_date;
	}

}