package com.hjsj.hrms.actionform.gz.gz_self.tax;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
import java.util.HashMap;

public class SelfTaxForm extends FrameForm {
	private String sumsds,sumynse;
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private PaginationForm pageListForm = new PaginationForm();
	private String selStr;//下拉选择框
//	private String gssj;//计税时间
	private String tax_date;
	private String selstrtaxdate;
	private String selstrnian;
	private String nian;//按年查询
	private ArrayList fieldlist;
	private String ymd;
	private String startime;
	private String endtime;
	private String modeset;//计税模式
	private String flag;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSelstrtaxdate((String) hm.get("str_tax_date"));
		this.setSelstrnian((String) hm.get("str_nian"));
		this.setColumn((String) hm.get("column"));
		this.setWhere((String) hm.get("where"));
		this.setSql((String) hm.get("sql"));
		this.setOrderby((String) hm.get("orderby"));
		this.setFieldlist((ArrayList) hm.get("fieldlist"));
		this.setYmd((String)hm.get("ymd"));
		this.setStartime((String) hm.get("startime"));
		this.setEndtime((String) hm.get("endtime"));
		this.setModeset((String)hm.get("modeset"));
		this.setSumsds((String) hm.get("sumsds"));
		this.setSumynse((String) hm.get("sumynse"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("nian",this.getNian());
		hm.put("tax_date",this.getTax_date());
		hm.put("flag", flag);
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getTax_date() {
		return tax_date;
	}

	public void setTax_date(String tax_date) {
		this.tax_date = tax_date;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getSelStr() {
		return selStr;
	}

	public void setSelStr(String selStr) {
		this.selStr = selStr;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getSelstrtaxdate() {
		return selstrtaxdate;
	}

	public void setSelstrtaxdate(String selstrtaxdate) {
		this.selstrtaxdate = selstrtaxdate;
	}

	public String getNian() {
		return nian;
	}

	public void setNian(String nian) {
		this.nian = nian;
	}

	public String getSelstrnian() {
		return selstrnian;
	}

	public void setSelstrnian(String selstrnian) {
		this.selstrnian = selstrnian;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getModeset() {
		return modeset;
	}

	public void setModeset(String modeset) {
		this.modeset = modeset;
	}

	public String getSumsds() {
		return sumsds;
	}

	public void setSumsds(String sumsds) {
		this.sumsds = sumsds;
	}

	public String getSumynse() {
		return sumynse;
	}

	public void setSumynse(String sumynse) {
		this.sumynse = sumynse;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
