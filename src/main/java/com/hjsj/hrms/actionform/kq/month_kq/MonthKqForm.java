package com.hjsj.hrms.actionform.kq.month_kq;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MonthKqForm extends FrameForm{
	private String riqi = "";//动态显示列的长度
	private String clos = ""; //合并列的长度
	private String sql_str = "";
	private String where_str = "";
	private String cols_str = "";
	private ArrayList list = new ArrayList();
	private ArrayList list2 = new ArrayList();
	
	private String userCode = "";
	
	private String a0100 = "";
	private String field = "";
	private String codeid = "";
	private String info = "";
	
	private ArrayList setRelationList = new ArrayList();//设置审批关系
	private ArrayList codeitemList = new ArrayList();
	private ArrayList yearList = new ArrayList();
	private ArrayList monthList = new ArrayList();
	private ArrayList currUserList = new ArrayList();
	
	private String type = "";
	private String years = "";
	private String months = "";
	
	private String defValue = "";
	private String kqdefValue = "";
	private String relation = "";
	
	private PaginationForm recordListForm=new PaginationForm(); 
	
	private String codes = "";
	
	private String isShowButton = "";

	private String details = "";
	
	private String nbase = "";
	


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("userCode", this.getUserCode());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("field", this.getField());
		this.getFormHM().put("codeid", this.getCodeid());
		this.getFormHM().put("type", type);
		this.getFormHM().put("years", this.getYears());
		this.getFormHM().put("months", this.getMonths());
		this.getFormHM().put("currUserList", this.getCurrUserList());
		this.getFormHM().put("codes", this.getCodes());
		this.getFormHM().put("kqdefValue", this.getKqdefValue());
	}

	@Override
    public void outPutFormHM() {
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setYears((String)this.getFormHM().get("years"));
		this.setMonths((String)this.getFormHM().get("months"));
		this.setRiqi((String)this.getFormHM().get("riqi"));
		this.setClos((String)this.getFormHM().get("clos"));
		this.setSql_str((String)this.getFormHM().get("sql"));
		this.setWhere_str((String)this.getFormHM().get("where"));
		this.setCols_str((String)this.getFormHM().get("cols"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setList2((ArrayList)this.getFormHM().get("list2"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setField((String)this.getFormHM().get("field"));
		this.setSetRelationList((ArrayList)this.getFormHM().get("setRelationList"));
		this.setCodeitemList((ArrayList)this.getFormHM().get("codeItemList"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setDefValue((String)this.getFormHM().get("defValue"));
		this.setRelation((String)this.getFormHM().get("relation"));
		this.setCurrUserList((ArrayList)this.getFormHM().get("currUserList"));
		this.setIsShowButton((String)this.getFormHM().get("isShowButton"));
		this.setDetails((String)this.getFormHM().get("details"));
		this.setKqdefValue((String)this.getFormHM().get("kqdefValue"));
	}

	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	    {
		try
		{
		    if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
		    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {
			/** 定位到首页, */
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
		    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {
			/** 定位到首页, */
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
		    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {
			/** 定位到首页, */
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	    }
	
	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

	public String getWhere_str() {
		return where_str;
	}

	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getCols_str() {
		return cols_str;
	}

	public void setCols_str(String cols_str) {
		this.cols_str = cols_str;
	}

	public String getClos() {
		return clos;
	}

	public void setClos(String clos) {
		this.clos = clos;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getList2() {
		return list2;
	}

	public void setList2(ArrayList list2) {
		this.list2 = list2;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public ArrayList getSetRelationList() {
		return setRelationList;
	}

	public void setSetRelationList(ArrayList setRelationList) {
		this.setRelationList = setRelationList;
	}

	public ArrayList getCodeitemList() {
		return codeitemList;
	}

	public void setCodeitemList(ArrayList codeitemList) {
		this.codeitemList = codeitemList;
	}

	public String getRiqi() {
		return riqi;
	}

	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}

	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}

	public ArrayList getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

	public String getMonths() {
		return months;
	}

	public void setMonths(String months) {
		this.months = months;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public ArrayList getCurrUserList() {
		return currUserList;
	}

	public void setCurrUserList(ArrayList currUserList) {
		this.currUserList = currUserList;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getKqdefValue() {
		return kqdefValue;
	}

	public void setKqdefValue(String kqdefValue) {
		this.kqdefValue = kqdefValue;
	}
	
	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getIsShowButton() {
		return isShowButton;
	}

	public void setIsShowButton(String isShowButton) {
		this.isShowButton = isShowButton;
	}

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}
}
