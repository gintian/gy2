package com.hjsj.hrms.actionform.train.hierarchy;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class CourseHierarchyForm extends FrameForm {
	private String a_code;
	private String a_code1;
	private String searchstr;
	private String id;
	private ArrayList ls = new ArrayList();
	
	private String isP;//是否为上级分类下的课程 1:是
	private String r5022;//审批状态
	
	private String strsql;
	private String columns;
	private String strwhere;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.getFormHM().put("id", this.getId());
		this.setId(null);
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("isP", "");
		this.getFormHM().put("ls", this.getLs());
	}

	@Override
    public void outPutFormHM() {
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setId((String)this.getFormHM().get("id"));
		this.setA_code1((String)this.getFormHM().get("a_code1"));
		
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setIsP((String)this.getFormHM().get("isP"));
		this.setR5022((String)this.getFormHM().get("r5022"));
		this.setLs((ArrayList)this.getFormHM().get("ls"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/hierarchy".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }
	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
	}

	public String getId() {
		String idtemp = this.id;
		this.id = null;
		return idtemp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getA_code1() {
		return a_code1;
	}

	public void setA_code1(String a_code1) {
		this.a_code1 = a_code1;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getIsP() {
		return isP;
	}

	public void setIsP(String isP) {
		this.isP = isP;
	}

	public String getR5022() {
		return r5022;
	}

	public void setR5022(String r5022) {
		this.r5022 = r5022;
	}

	public ArrayList getLs() {
		return ls;
	}

	public void setLs(ArrayList ls) {
		this.ls = ls;
	}



}
