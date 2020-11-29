/**
 * 
 */
package com.hjsj.hrms.actionform.kq.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 25, 2007:5:37:29 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class AdjustcodeFrom  extends FrameForm{
	private ArrayList fieldlist=new ArrayList();
	private ArrayList v_h_list=new ArrayList();
	private String re_flag;
	private PaginationForm recordListForm=new PaginationForm();   
	private String[] state;
	private String table;
	private String fashion_flag="0";
	private ArrayList field_list=new ArrayList();
	private String tablemess;
	private String code_fields[];;
	private String flag;
	private String isSave;
	private String returnvalue="1";
	public String getIsSave() {
		return isSave;
	}
	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getFashion_flag() {
		return fashion_flag;
	}
	public void setFashion_flag(String fashion_flag) {
		this.fashion_flag = fashion_flag;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String[] getState() {
		return state;
	}
	public void setState(String[] state) {
		this.state = state;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public ArrayList getV_h_list() {
		return v_h_list;
	}
	public void setV_h_list(ArrayList v_h_list) {
		this.v_h_list = v_h_list;
	}
	@Override
    public void outPutFormHM()
	{
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setV_h_list((ArrayList)this.getFormHM().get("v_h_list"));
		this.setRe_flag((String)this.getFormHM().get("re_flag"));
		this.getRecordListForm().getPagination().gotoPage(1);	
		this.setField_list((ArrayList)this.getFormHM().get("field_list"));
		this.setTablemess((String)this.getFormHM().get("tablemess"));
		//this.setFashion_flag(this.fashion_flag);
		this.setTable((String)this.getFormHM().get("table"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setIsSave((String)this.getFormHM().get("isSave"));
	}
	@Override
    public void inPutTransHM()
	{ 
		this.getFormHM().put("fieldlist",this.getFieldlist());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("re_flag",this.getRe_flag());
		this.getFormHM().put("table",this.getTable());
		this.getFormHM().put("fashion_flag",this.getFashion_flag());
		this.getFormHM().put("code_fields",this.getCode_fields());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("isSave", this.getIsSave());
	}
	public String getRe_flag() {
		return re_flag;
	}
	public void setRe_flag(String re_flag) {
		this.re_flag = re_flag;
	}
	public String[] getCode_fields() {
		return code_fields;
	}
	public void setCode_fields(String[] code_fields) {
		this.code_fields = code_fields;
	}
	public ArrayList getField_list() {
		return field_list;
	}
	public void setField_list(ArrayList field_list) {
		this.field_list = field_list;
	}
	public String getTablemess() {
		return tablemess;
	}
	public void setTablemess(String tablemess) {
		this.tablemess = tablemess;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
}
