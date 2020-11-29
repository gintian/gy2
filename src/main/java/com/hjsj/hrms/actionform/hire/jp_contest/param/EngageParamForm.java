package com.hjsj.hrms.actionform.hire.jp_contest.param;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 *<p>Title:EngageParamForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class EngageParamForm extends FrameForm {

	private String field_falg;
	private String app_count;
	private String base;
	private String maxpos;
	private ArrayList attendlist = new ArrayList();
	private ArrayList employlist = new ArrayList();
	
	private String[] left_fields;
	private String[] right_fields;
	
	private String card_mess;
	private String app_view_mess;
	private String attent_view_mess;
	
	private ArrayList setlist = new ArrayList();
	private ArrayList itemlist = new ArrayList();
	private ArrayList rnamelist = new ArrayList();
	private ArrayList selectrname = new ArrayList();
	private String template;
	private String strTemplate;

	public String getApp_view_mess() {
		return app_view_mess;
	}

	public void setApp_view_mess(String app_view_mess) {
		this.app_view_mess = app_view_mess;
	}

	public String getAttent_view_mess() {
		return attent_view_mess;
	}

	public void setAttent_view_mess(String attent_view_mess) {
		this.attent_view_mess = attent_view_mess;
	}

	public String getCard_mess() {
		return card_mess;
	}

	public void setCard_mess(String card_mess) {
		this.card_mess = card_mess;
	}

	public String getApp_count() {
		return app_count;
	}

	public void setApp_count(String app_count) {
		this.app_count = app_count;
	}

	public ArrayList getAttendlist() {
		return attendlist;
	}

	public void setAttendlist(ArrayList attendlist) {
		this.attendlist = attendlist;
	}

	public ArrayList getEmploylist() {
		return employlist;
	}

	public void setEmploylist(ArrayList employlist) {
		this.employlist = employlist;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setField_falg((String)this.getFormHM().get("field_falg"));
		this.setApp_count((String)this.getFormHM().get("app_count"));
		this.setAttendlist((ArrayList)this.getFormHM().get("attendlist"));
		this.setEmploylist((ArrayList)this.getFormHM().get("Employlist"));
		this.setApp_view_mess((String)this.getFormHM().get("app_view_mess"));
		this.setAttent_view_mess((String)this.getFormHM().get("attent_view_mess"));
		this.setCard_mess((String)this.getFormHM().get("card_mess"));
		this.setLeft_fields((String[])this.getFormHM().get("left_fields"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setBase((String)this.getFormHM().get("base"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setRnamelist((ArrayList)this.getFormHM().get("rnamelist"));
		this.setSelectrname((ArrayList)this.getFormHM().get("selectrname"));
		this.setMaxpos((String)this.getFormHM().get("maxpos"));
		this.setTemplate((String)this.getFormHM().get("template"));
		this.setStrTemplate((String)this.getFormHM().get("strTemplate"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("field_falg",this.getField_falg());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("maxpos",this.getMaxpos());
		this.getFormHM().put("strTemplate",this.getStrTemplate());
	}

	public String getField_falg() {
		return field_falg;
	}

	public void setField_falg(String field_falg) {
		this.field_falg = field_falg;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public ArrayList getRnamelist() {
		return rnamelist;
	}

	public void setRnamelist(ArrayList rnamelist) {
		this.rnamelist = rnamelist;
	}

	public ArrayList getSelectrname() {
		return selectrname;
	}

	public void setSelectrname(ArrayList selectrname) {
		this.selectrname = selectrname;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String getMaxpos() {
		return maxpos;
	}

	public void setMaxpos(String maxpos) {
		this.maxpos = maxpos;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getStrTemplate() {
		return strTemplate;
	}

	public void setStrTemplate(String strTemplate) {
		this.strTemplate = strTemplate;
	}
}
