package com.hjsj.hrms.actionform.train.report;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 * <p>
 * Title:TrainReportForm.java
 * </p>
 * <p>
 * Description:培训报表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-07 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainReportForm extends FrameForm {
	private String a_code = ""; // 机构代码

	private String year;

	private String quarter;

	private String reportId;

	private ArrayList titles;

	private ArrayList yearList = new ArrayList();

	// list页面用
	private PaginationForm setlistform = new PaginationForm();

	// list页面用
	private ArrayList setlist = new ArrayList();

	private String strSql;

	private String returnvalue;

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("quarter", this.getQuarter());
		this.getFormHM().put("reportId", this.getReportId());
		this.getFormHM().put("titles", this.getTitles());
		this.getFormHM().put("yearList", this.getYearList());
		this.getFormHM().put("strSql", this.getStrSql());
	}

	@Override
    public void outPutFormHM() {
		if (this.getFormHM().get("pageform") != null) {
			this.setSetlistform((PaginationForm) this.getFormHM().get(
					"pageform"));
		}

		this.setA_code((String) this.getFormHM().get("a_code"));
		this.setYear((String) this.getFormHM().get("year"));
		this.setQuarter((String) this.getFormHM().get("quarter"));
		this.getSetlistform().setList(
				(ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setReportId((String) this.getFormHM().get("reportId"));
		this.setTitles((ArrayList) this.getFormHM().get("titles"));
		this.setYearList((ArrayList) this.getFormHM().get("yearList"));
		this.setStrSql((String) this.getFormHM().get("strSql"));

	}

	public String getA_code() {

		return a_code;
	}

	public void setA_code(String a_code) {

		this.a_code = a_code;
	}

	public String getQuarter() {

		return quarter;
	}

	public void setQuarter(String quarter) {

		this.quarter = quarter;
	}

	public String getYear() {

		return year;
	}

	public void setYear(String year) {

		this.year = year;
	}

	public ArrayList getSetlist() {

		return setlist;
	}

	public void setSetlist(ArrayList setlist) {

		this.setlist = setlist;
	}

	public PaginationForm getSetlistform() {

		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform) {

		this.setlistform = setlistform;
	}

	public String getReportId() {

		return reportId;
	}

	public void setReportId(String reportId) {

		this.reportId = reportId;
	}

	public ArrayList getTitles() {

		return titles;
	}

	public void setTitles(ArrayList titles) {

		this.titles = titles;
	}

	public ArrayList getYearList() {

		return yearList;
	}

	public void setYearList(ArrayList yearList) {

		this.yearList = yearList;
	}

	public String getStrSql() {

		return strSql;
	}

	public void setStrSql(String strSql) {

		this.strSql = strSql;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

}
