package com.hjsj.hrms.actionform.kq.kqself.annual;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MyAnnualForm extends FrameForm {


	private String sql;
	
	private String where;
	
	private String com;
	private String table;
	private ArrayList slist=new ArrayList();
    private int cols=0;
    private ArrayList tlist =new ArrayList();
    private String kq_year;
	private PaginationForm myAnnualForm=new PaginationForm(); 
	private ArrayList typelist=new ArrayList();
	private String type;
	private String isshow;
	//调休假期显示范围
	private String leaveActiveTime;
	
	
	public String getIsshow() {
		return isshow;
	}

	public void setIsshow(String isshow) {
		this.isshow = isshow;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
    public void outPutFormHM() {
		 this.setWhere((String)this.getFormHM().get("where"));
		 this.setCom((String)this.getFormHM().get("com"));
		 this.setSql((String)this.getFormHM().get("sql"));
		 this.setTlist((ArrayList)this.getFormHM().get("tlist"));
		 this.setSlist((ArrayList)this.getFormHM().get("slist"));
		 this.setKq_year((String)this.getFormHM().get("kq_year"));		 
		 this.setTable((String)this.getFormHM().get("table"));
		 this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
		 this.setType((String)this.getFormHM().get("type"));
		 this.setIsshow((String)this.getFormHM().get("isshow"));
		 this.setLeaveActiveTime((String)this.getFormHM().get("leaveActiveTime"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("kq_year",this.getKq_year());
		this.getFormHM().put("table",this.getTable());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("tlist", this.getTlist());
	}
	
	@Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)    {
        request.setAttribute("targetWindow", "0");//0不显示按钮 |1关闭|默认为返回
        return super.validate(mapping, request);
    }

	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
		this.cols=tlist.size();			
	}



	public PaginationForm getMyAnnualForm() {
		return myAnnualForm;
	}

	public void setMyAnnualForm(PaginationForm myAnnualForm) {
		this.myAnnualForm = myAnnualForm;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
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

	public String getKq_year() {
		return kq_year;
	}

	public void setKq_year(String kq_year) {
		this.kq_year = kq_year;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public String getLeaveActiveTime() {
		return leaveActiveTime;
	}

	public void setLeaveActiveTime(String leaveActiveTime) {
		this.leaveActiveTime = leaveActiveTime;
	}

}
