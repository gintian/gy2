package com.hjsj.hrms.actionform.org.gzdatamaint;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GzDataMaintForm extends FrameForm {
	private String selectsql;
	private String a_code;
	private String tablename;
	private String checkflag;
	private ArrayList fieldlist = new ArrayList();
	private ArrayList itemlist = new ArrayList();
	private String viewdata;
	private String fieldsetid;
	private String[] left_fields;
	private String[] right_fields;
	private ArrayList selectsubclass;
	private ArrayList subclasslist;
	private String sort_str;
	private String infor;
	private String unit_type;
	private String tagname;
	private String gzflag; //1.外部培训 2.薪资 3.保险 
    private String hasParam;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setHasParam((String)this.getFormHM().get("hasParam"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setSelectsql((String)this.getFormHM().get("selectsql"));
		this.setViewdata((String)this.getFormHM().get("viewdata"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSelectsubclass((ArrayList)this.getFormHM().get("selectsubclass"));
		this.setSubclasslist((ArrayList)this.getFormHM().get("subclasslist"));
		this.setSort_str((String)this.getFormHM().get("sort_str"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setUnit_type((String)this.getFormHM().get("unit_type"));
		this.setTagname((String)this.getFormHM().get("tagname"));
		this.setGzflag((String)this.getFormHM().get("gzflag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("viewdata",this.getViewdata());
		this.getFormHM().put("left_fields",this.getLeft_fields());
		this.getFormHM().put("right_fileds",this.getRight_fields());
		this.getFormHM().put("fieldsetid",this.getFieldsetid());
		this.getFormHM().put("sort_str",this.getSort_str());
		this.getFormHM().put("unit_type",this.getUnit_type());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**定位到首页,*/
        if(this.getPagination()!=null)
        	this.getPagination().firstPage();              
        return super.validate(arg0, arg1);
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getViewdata() {
		return viewdata;
	}

	public void setViewdata(String viewdata) {
		this.viewdata = viewdata;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
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

	public ArrayList getSelectsubclass() {
		return selectsubclass;
	}

	public void setSelectsubclass(ArrayList selectsubclass) {
		this.selectsubclass = selectsubclass;
	}

	public ArrayList getSubclasslist() {
		return subclasslist;
	}

	public void setSubclasslist(ArrayList subclasslist) {
		this.subclasslist = subclasslist;
	}

	public String getSort_str() {
		return sort_str;
	}

	public void setSort_str(String sort_str) {
		this.sort_str = sort_str;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public String getUnit_type() {
		return unit_type;
	}

	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public String getGzflag() {
		return gzflag;
	}

	public void setGzflag(String gzflag) {
		this.gzflag = gzflag;
	}

	public String getHasParam() {
		return hasParam;
	}

	public void setHasParam(String hasParam) {
		this.hasParam = hasParam;
	}

}
