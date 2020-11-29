package com.hjsj.hrms.actionform.hire.jp_contest.personinfo;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class ShowJpPersonForm extends FrameForm {

	private String state;
	private String statestr;
	private String appstate;
	private String appstatestr;
	private String jp_station;
	private ArrayList stationlist = new ArrayList();;
	private ArrayList columns = new ArrayList();
	private ArrayList columnlist  = new ArrayList();
	private String ye;
	private String userpriv;
	private ArrayList typelist = new ArrayList();
	private ArrayList templatelist = new ArrayList();
	private String template;
	
	
	private PaginationForm roleListForm=new PaginationForm();
	private PaginationForm stuffListForm=new PaginationForm();
	
	public String getJp_station() {
		return jp_station;
	}

	public void setJp_station(String jp_station) {
		this.jp_station = jp_station;
	}

	public ArrayList getStationlist() {
		return stationlist;
	}

	public void setStationlist(ArrayList stationlist) {
		this.stationlist = stationlist;
	}

	public String getAppstate() {
		return appstate;
	}

	public void setAppstate(String appstate) {
		this.appstate = appstate;
	}

	public String getAppstatestr() {
		return appstatestr;
	}

	public void setAppstatestr(String appstatestr) {
		this.appstatestr = appstatestr;
	}

	public String getStatestr() {
		return statestr;
	}

	public void setStatestr(String statestr) {
		this.statestr = statestr;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setState((String)this.getFormHM().get("state"));
		this.setStatestr((String)this.getFormHM().get("statestr"));
		this.setAppstate((String)this.getFormHM().get("appstate"));
		this.setAppstatestr((String)this.getFormHM().get("appstatestr"));
		this.setJp_station((String)this.getFormHM().get("jp_station"));
		this.setStationlist((ArrayList)this.getFormHM().get("stationlist"));
		this.setColumns((ArrayList)this.getFormHM().get("columns"));
		this.setColumnlist((ArrayList)this.getFormHM().get("columnlist"));
		this.setYe((String)this.getFormHM().get("ye"));
		this.setUserpriv((String)this.getFormHM().get("userpriv"));
		this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
		
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.getStuffListForm().setList((ArrayList)this.getFormHM().get("stufflist"));
		this.setTemplatelist((ArrayList)this.getFormHM().get("templatelist"));
		this.setTemplate((String)this.getFormHM().get("tamplate"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("appstate",this.getAppstate());
		this.getFormHM().put("jp_station",this.getJp_station());
		this.getFormHM().put("userpriv", this.getUserpriv());
		this.getFormHM().put("selectedlist",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("selectstufflist",this.getStuffListForm().getSelectedList());
		this.getFormHM().put("tamplate",this.getTemplate());
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public ArrayList getColumns() {
		return columns;
	}

	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}

	public ArrayList getColumnlist() {
		return columnlist;
	}

	public void setColumnlist(ArrayList columnlist) {
		this.columnlist = columnlist;
	}

	public String getYe() {
		return ye;
	}

	public void setYe(String ye) {
		this.ye = ye;
	}

	public PaginationForm getStuffListForm() {
		return stuffListForm;
	}

	public void setStuffListForm(PaginationForm stuffListForm) {
		this.stuffListForm = stuffListForm;
	}

	public String getUserpriv() {
		return userpriv;
	}

	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public ArrayList getTemplatelist() {
		return templatelist;
	}

	public void setTemplatelist(ArrayList templatelist) {
		this.templatelist = templatelist;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
