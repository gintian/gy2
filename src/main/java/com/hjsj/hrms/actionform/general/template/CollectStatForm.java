package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class CollectStatForm extends FrameForm {
	private String treeCode;
	private String subset;
	private String strsql;
	private String columns;
	private String where;
	private String orderby;
	private ArrayList filelist = new ArrayList();
	private ArrayList columnlist = new ArrayList();
	private String fileset;
	private String start_date;
	private String end_date;
	private String childset;
	private String nbase;
	private String code;
	private String kind;
	private String flag;
	private String select_stat;
	private String selecthtml;
	private ArrayList nbaselist = new ArrayList();
	private String isWhere;
	private String orderfile;
	private String collectflag;

	private String sort_sign;
	private String sort_field;

	private String left_fields[];
	private String right_fields[];
	private String like;
	private ArrayList factorlist;

	private ArrayList selectfieldlist = new ArrayList();

	private ArrayList selectedlist = new ArrayList();

	/** 关系操作符 */
	private ArrayList operlist = new ArrayList();
	/** 逻辑操作符 */
	private ArrayList logiclist = new ArrayList();

	public CollectStatForm() {
		CommonData vo = new CommonData("=", "=");
		operlist.add(vo);
		vo = new CommonData(">", ">");
		operlist.add(vo);
		vo = new CommonData(">=", ">=");
		operlist.add(vo);
		vo = new CommonData("<", "<");
		operlist.add(vo);
		vo = new CommonData("<=", "<=");
		operlist.add(vo);
		vo = new CommonData("<>", "<>");
		operlist.add(vo);
		vo = new CommonData("*", "并且");
		logiclist.add(vo);
		vo = new CommonData("+", "或");
		logiclist.add(vo);
	}

	// 部门层级
	private String uplevel;

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public String getOrderfile() {
		return orderfile;
	}

	public void setOrderfile(String orderfile) {
		this.orderfile = orderfile;
	}

	public String getIsWhere() {
		return isWhere;
	}

	public void setIsWhere(String isWhere) {
		this.isWhere = isWhere;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public ArrayList getNbaselist() {
		return nbaselist;
	}

	public void setNbaselist(ArrayList nbaselist) {
		this.nbaselist = nbaselist;
	}

	public String getChildset() {
		return childset;
	}

	public void setChildset(String childset) {
		this.childset = childset;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getFileset() {
		return fileset;
	}

	public void setFileset(String fileset) {
		this.fileset = fileset;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	@Override
    public void outPutFormHM() {
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setSubset((String) this.getFormHM().get("subset"));
		this.setStrsql((String) this.getFormHM().get("strsql"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setFilelist((ArrayList) this.getFormHM().get("filelist"));
		this.setColumnlist((ArrayList) this.getFormHM().get("columnlist"));
		this.setFileset((String) this.getFormHM().get("fileset"));
		this.setNbase((String) this.getFormHM().get("nbase"));
		this.setNbaselist((ArrayList) this.getFormHM().get("nbaselist"));
		this.setCode((String) this.getFormHM().get("code"));
		this.setKind((String) this.getFormHM().get("kind"));
		this.setIsWhere((String) this.getFormHM().get("isWhere"));
		this.setSelect_stat((String) this.getFormHM().get("select_stat"));
		this.setSelecthtml((String) this.getFormHM().get("selecthtml"));
		this.setOrderfile((String) this.getFormHM().get("orderfile"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setCollectflag((String) this.getFormHM().get("collectflag"));
		this.setSort_sign((String) this.getFormHM().get("sort_sign"));
		this.setSort_field((String) this.getFormHM().get("sort_field"));
		this.setSelectedlist((ArrayList) this.getFormHM().get("selectedlist"));
		this.setSelectfieldlist((ArrayList) this.getFormHM().get(
				"selectfieldlist"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setFactorlist((ArrayList) this.getFormHM().get("factorlist"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("subset", this.getSubset());
		this.getFormHM().put("fileset", this.getFileset());
		this.getFormHM().put("start_date", this.getStart_date());
		this.getFormHM().put("end_date", this.getEnd_date());
		this.getFormHM().put("childset", this.getChildset());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("kind", this.getKind());
		this.getFormHM().put("select_stat", this.getSelect_stat());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("isWhere", this.getIsWhere());
		this.getFormHM().put("where", this.getWhere());
		this.getFormHM().put("orderfile", this.getOrderfile());
		if (this.getPagination() != null)
			this.getFormHM().put("selectedinfolist",
					(ArrayList) this.getPagination().getSelectedList());
		this.getFormHM().put("sort_sign", this.getSort_sign());
		this.getFormHM().put("like", this.getLike());
		this.getFormHM().put("factorlist", this.getFactorlist());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("selectedlist", this.getSelectedlist());
		this.getFormHM().put("selectfieldlist", this.getSelectedlist());
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public ArrayList getColumnlist() {
		return columnlist;
	}

	public void setColumnlist(ArrayList columnlist) {
		this.columnlist = columnlist;
	}

	public ArrayList getFilelist() {
		return filelist;
	}

	public void setFilelist(ArrayList filelist) {
		this.filelist = filelist;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if ("/general/template/goabroad/collect/searchstat".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setCode("");
			this.setKind("");
			this.setSelect_stat("");
			this.setIsWhere("");
			this.setFileset("");
		}
		if ("/general/template/goabroad/collect/searchstatdata".equals(
                arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		return super.validate(arg0, arg1);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSelect_stat() {
		return select_stat;
	}

	public void setSelect_stat(String select_stat) {
		this.select_stat = select_stat;
	}

	public String getSelecthtml() {
		return selecthtml;
	}

	public void setSelecthtml(String selecthtml) {
		this.selecthtml = selecthtml;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getCollectflag() {
		return collectflag;
	}

	public void setCollectflag(String collectflag) {
		this.collectflag = collectflag;
	}

	public String getSort_sign() {
		return sort_sign;
	}

	public void setSort_sign(String sort_sign) {
		this.sort_sign = sort_sign;
	}

	public String getSort_field() {
		return sort_field;
	}

	public void setSort_field(String sort_field) {
		this.sort_field = sort_field;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	public ArrayList getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(ArrayList selectedlist) {
		this.selectedlist = selectedlist;
	}

	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}

	public ArrayList getLogiclist() {
		return logiclist;
	}

	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}
}
