package com.hjsj.hrms.actionform.hire.zp_options.totalstat;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class TotalStatForm extends FrameForm{
	private String startime;
//	开始时间
	private String endtime;
//	结束时间
	private String orgid;
//	机构id
	private String org;
//	机构名称
	private ArrayList showresumelist=new ArrayList();
//  所有简历	
	private ArrayList showfiresumelist=new ArrayList();
//  第一志愿简历
	
	private String column;		
	private String selectsql;		
	private String wheresql;
	private String notes;
	private ArrayList allList = new ArrayList();
	private String schoolPosition;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSchoolPosition((String)this.getFormHM().get("schoolPosition"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setAllList((ArrayList)this.getFormHM().get("allList"));
		this.setStartime((String) hm.get("startime"));
		this.setEndtime((String) hm.get("endtime"));
		this.setOrgid((String) hm.get("orgid"));
		this.setShowresumelist((ArrayList) hm.get("showresumelist"));
		this.setShowfiresumelist((ArrayList) hm.get("showfiresumelist"));
		this.setColumn((String) hm.get("column"));
		this.setSelectsql((String) hm.get("selectsql"));
		this.setWheresql((String) hm.get("wheresql"));
		this.setNotes((String) hm.get("notes"));
		this.setOrg((String) hm.get("org"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("returnflag", this.getReturnflag());
		hm.put("showresumelist",this.getShowresumelist());
		hm.put("showfiresumelist",this.getShowfiresumelist());
		hm.put("startime",this.getStartime());
		hm.put("endtime",this.getEndtime());
		hm.put("orgid",this.getOrgid());
		hm.put("org",this.getOrg());
		hm.put("column",this.getColumn());
		hm.put("selectsql", this.getSelectsql());
		hm.put("wheresql", this.getWheresql());
		hm.put("notes", this.getNotes());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/zp_options/stat/totalstat/showtotalstatresult".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getStartime() {
		return startime;
	}
	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public ArrayList getShowresumelist() {
		return showresumelist;
	}

	public void setShowresumelist(ArrayList showresumelist) {
		this.showresumelist = showresumelist;
	}

	public ArrayList getShowfiresumelist() {
		return showfiresumelist;
	}

	public void setShowfiresumelist(ArrayList showfiresumelist) {
		this.showfiresumelist = showfiresumelist;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public String getWheresql() {
		return wheresql;
	}

	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public ArrayList getAllList() {
		return allList;
	}

	public void setAllList(ArrayList allList) {
		this.allList = allList;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

}
