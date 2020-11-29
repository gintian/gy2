package com.hjsj.hrms.actionform.gz.tempvar;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TempvarForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 
	 private String fielditem; //指标名称
	 private String fielditemid; //指标id
	 private String code; //代码名称
	 private String codeid;  //代码id
	 private ArrayList codelist = new ArrayList();
	 
	 private String formula; //公式内容
	 
	 private String cstate; //薪资类别
	 
	 private String tempvarname; //变量名
	 private String ntype; //变量类型
	 private String fidlen; //字段最大长度
	 private String fiddec; //小数点位数
	 private String codeset;
	 private String codesetid;
	 
	 private String[] codesetid_arr; //代码id
	 
	 private String fieldsetid; //子集id
	 private ArrayList fieldsetlist = new ArrayList(); 
	 
	 private String[] itemid_arr; //子标id
	 
	 private String nid; //临时变量id

	 /***
	  * 临时中的项目调整顺序
	  * */
	 private String[] sort_fields; 
	 private ArrayList sortlist = new ArrayList(); 
	 
	 private String type; 
	 private String checkclose;
	 private String nflag; //报表为2,工资为0（包括模板）
	 private String showflag;//为了解决在数据采集中新建临时变量时  临时变量界面确定和返回按钮显示的问题  1：表示从计算公式中过来 0 ：从原始界面点击过来
	 
	 /* 薪资总额，新增属性，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start */
	 private String isAddTempVar;
	 /* 薪资总额，新增属性，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end */
	

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setFielditem((String)this.getFormHM().get("fielditem"));
		this.setFielditemid((String)this.getFormHM().get("fielditemid"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setCstate((String)this.getFormHM().get("cstate"));
		this.setTempvarname((String)this.getFormHM().get("tempvarname"));
		this.setNtype((String)this.getFormHM().get("ntype"));
		this.setFidlen((String)this.getFormHM().get("fidlen"));
		this.setFiddec((String)this.getFormHM().get("fiddec"));
		
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		
		this.setType((String)this.getFormHM().get("type"));
		this.setNid((String)this.getFormHM().get("nid"));
		this.setCodeset((String)this.getFormHM().get("codeset"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setItemid_arr((String[])this.getFormHM().get("itemid_arr"));
		this.setCheckclose((String)this.getFormHM().get("checkclose"));
		this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
		this.setNflag((String)this.getFormHM().get("nflag"));
		this.setShowflag((String) this.getFormHM().get("showflag"));
		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start */
		this.setIsAddTempVar((String)this.getFormHM().get("isAddTempVar"));
		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end */
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("formula",this.getFormula());
		this.getFormHM().put("cstate",this.getCstate());
		this.getFormHM().put("tempvarname",this.getTempvarname());
		this.getFormHM().put("ntype",this.getNtype());
		this.getFormHM().put("fidlen",this.getFidlen());
		this.getFormHM().put("fiddec",this.getFiddec());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("nid",this.getNid());
		this.getFormHM().put("codesetid",this.getCodesetid());
		
		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start */
		this.getFormHM().put("isAddTempVar",this.getIsAddTempVar());
		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end */
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);  
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/tempvar/viewtempvar".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getFielditemid() {
		return fielditemid;
	}

	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getFielditem() {
		return fielditem;
	}

	public void setFielditem(String fielditem) {
		this.fielditem = fielditem;
	}

	public String getCstate() {
		return cstate;
	}

	public void setCstate(String cstate) {
		this.cstate = cstate;
	}

	public String getFiddec() {
		return fiddec;
	}

	public void setFiddec(String fiddec) {
		this.fiddec = fiddec;
	}

	public String getFidlen() {
		return fidlen;
	}

	public void setFidlen(String fidlen) {
		this.fidlen = fidlen;
	}

	public String getNtype() {
		return ntype;
	}

	public void setNtype(String ntype) {
		this.ntype = ntype;
	}

	public String getTempvarname() {
		return tempvarname;
	}

	public void setTempvarname(String tempvarname) {
		this.tempvarname = tempvarname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String[] getItemid_arr() {
		return itemid_arr;
	}

	public void setItemid_arr(String[] itemid_arr) {
		this.itemid_arr = itemid_arr;
	}

	public String getCodeset() {
		return codeset;
	}

	public void setCodeset(String codeset) {
		this.codeset = codeset;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCheckclose() {
		return checkclose;
	}

	public void setCheckclose(String checkclose) {
		this.checkclose = checkclose;
	}

	public ArrayList getCodelist() {
		return codelist;
	}

	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}

	public String getNflag() {
		return nflag;
	}

	public void setNflag(String nflag) {
		this.nflag = nflag;
	}
	
	public String getShowflag() {
		return showflag;
	}

	public void setShowflag(String showflag) {
		this.showflag = showflag;
	}

	public String getIsAddTempVar() {
		return isAddTempVar;
	}

	public void setIsAddTempVar(String isAddTempVar) {
		this.isAddTempVar = isAddTempVar;
	}
	
}
