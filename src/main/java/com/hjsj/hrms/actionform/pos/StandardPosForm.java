/*
 * Created on 2005-12-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.pos;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StandardPosForm extends FrameForm {

	private String validateflag;
	private String codesetdesc;
	private String codesetid;
	private String backdate;
	private String checked;
	private String param;
	private ArrayList selectfieldlist = new ArrayList();
	private String cardID;
	private String ps_card_attach;
	private String codemess;
	private String root;
	private String isShowCondition="none";
	private String orglike;
	private String sqlstr;
	private String wherestr;
	private String columnstr;
	private String orderby;
	private ArrayList fieldList = new ArrayList();
	private String a_code;
	private String kind;
	private String querylike;
	private String fieldstr;
	private String edit_flag;
	private ArrayList infoSetList = new ArrayList();
	private ArrayList infofieldlist= new ArrayList();
	private String edittype;
	private String first;
	private String setprv;
	public String getEdittype() {
		return edittype;
	}

	public void setEdittype(String edittype) {
		this.edittype = edittype;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getSetprv() {
		return setprv;
	}

	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setValidateflag((String)this.getFormHM().get("validateflag"));
		this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
		this.setOrglike((String)this.getFormHM().get("orglike"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setColumnstr((String)this.getFormHM().get("columnstr"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setCodemess((String)this.getFormHM().get("codemess"));
		this.setInfoSetList((ArrayList)this.getFormHM().get("infosetlist"));
		this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
		this.setSetprv((String)this.getFormHM().get("setprv"));
		this.setFirst((String)this.getFormHM().get("first"));
		this.setEdittype((String)this.getFormHM().get("edittype"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("param", param);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("fieldstr", fieldstr);
		this.getFormHM().put("fieldList", fieldList);
		this.getFormHM().put("orglike", orglike);
		this.getFormHM().put("querylike", querylike);
		this.getFormHM().put("selectfieldlist", selectfieldlist);
		this.getFormHM().put("a_code", a_code);
		this.getFormHM().put("edit_flag", edit_flag);
	}

	
	
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}

	public String getPs_card_attach() {
		return ps_card_attach;
	}

	public void setPs_card_attach(String ps_card_attach) {
		this.ps_card_attach = ps_card_attach;
	}

	public String getCodemess() {
		return codemess;
	}

	public void setCodemess(String codemess) {
		this.codemess = codemess;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getIsShowCondition() {
		return isShowCondition;
	}

	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}

	public String getOrglike() {
		return orglike;
	}

	public void setOrglike(String orglike) {
		this.orglike = orglike;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getColumnstr() {
		return columnstr;
	}

	public void setColumnstr(String columnstr) {
		this.columnstr = columnstr;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getQuerylike() {
		return querylike;
	}

	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}

	public String getFieldstr() {
		return fieldstr;
	}

	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}

	public String getEdit_flag() {
		return edit_flag;
	}

	public void setEdit_flag(String edit_flag) {
		this.edit_flag = edit_flag;
	}

	public ArrayList getInfoSetList() {
		return infoSetList;
	}

	public void setInfoSetList(ArrayList infoSetList) {
		this.infoSetList = infoSetList;
	}

	public ArrayList getInfofieldlist() {
		return infofieldlist;
	}

	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}

}
