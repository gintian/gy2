package com.hjsj.hrms.actionform.train.resource.course;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class CourseForm1 extends FrameForm {
	private String sqlstr = "";
	private String searchstr;
	private List sortlist=new ArrayList();
	private String id;
	private String orgparentcode;//LiWeichao 对应单位
	private String returnvalue;
	
	private String isP;//是否为上级分类下的课程 1:是
	private String r5022;//审批状态
	
	private String strsql;
	private String columns;
	private String strwhere;
	 private String primaryField;
	 
	 
	// 培训公共代码树的代码类 
	private String trainsetid;
	
	private String codeSetId;
	public String getCodeSetId() {
		return codeSetId;
	}

	public void setCodeSetId(String codeSetId) {
		this.codeSetId = codeSetId;
	}

	 
	private String order_by; 
	//岗位课程
	private ArrayList itemlist1 = new ArrayList();
	private String columns1;
	private String codesetid;
	private String codesetdesc;
	private String codeitemid;
	private String state;
	private String backdate;
	private String validateflag;
	private String checked;
	
	private String itemize;//分类编码
	private String itemizevalue;//课程分类
	private String coursename;//课程名称
	private String courseintro;//课程内容
	 
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.setSearchstr(null);
		this.getFormHM().put("id", this.getId());
		this.setId(null);
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("columns1", this.getColumns1());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("primaryField", this.getPrimaryField());
		this.getFormHM().put("isP", "");
		
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		
		this.getFormHM().put("state", state);
		this.getFormHM().put("backdate", backdate);
		this.getFormHM().put("itemize", this.getItemize());
		this.getFormHM().put("coursename", this.getCoursename());
		this.getFormHM().put("courseintro", this.getCourseintro());
	}

	@Override
    public void outPutFormHM() {
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setSortlist((List)this.getFormHM().get("sortlist"));
		this.setId((String)this.getFormHM().get("id"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setColumns1((String)this.getFormHM().get("columns1"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setPrimaryField((String)this.getFormHM().get("primaryField"));
		this.setIsP((String)this.getFormHM().get("isP"));
		this.setR5022((String)this.getFormHM().get("r5022"));
		this.setTrainsetid((String) this.getFormHM().get("trainsetid"));
		this.setCodeSetId((String) this.getFormHM().get("codesetid"));
		
		this.setItemlist1((ArrayList) this.getFormHM().get("itemlist1"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setState((String)this.getFormHM().get("state"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setBackdate((String)this.getFormHM().get("backdate"));
		this.setValidateflag((String)this.getFormHM().get("validateflag"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/resource/course".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }else if ("/train/resource/course/pos".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
	    }else if ("/train/resource/course/posrel".equals(arg0.getPath()) && arg1.getParameter("b_search") != null)
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



	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}


	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
	}

	public List getSortlist() {
		return sortlist;
	}

	public void setSortlist(List sortlist) {
		this.sortlist = sortlist;
	}

	public String getId() {
		String idtemp = this.id;
		this.id = null;
		return idtemp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
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

	public String getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(String primaryField) {
		this.primaryField = primaryField;
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

	public String getTrainsetid() {
		return trainsetid;
	}

	public void setTrainsetid(String trainsetid) {
		this.trainsetid = trainsetid;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public ArrayList getItemlist1() {
		return itemlist1;
	}

	public void setItemlist1(ArrayList itemlist1) {
		this.itemlist1 = itemlist1;
	}

	public String getColumns1() {
		return columns1;
	}

	public void setColumns1(String columns1) {
		this.columns1 = columns1;
	}

	public String getItemize() {
		return itemize;
	}

	public void setItemize(String itemize) {
		this.itemize = itemize;
	}

	public String getItemizevalue() {
		return itemizevalue;
	}

	public void setItemizevalue(String itemizevalue) {
		this.itemizevalue = itemizevalue;
	}

	public String getCoursename() {
		return coursename;
	}

	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}

	public String getCourseintro() {
		return courseintro;
	}

	public void setCourseintro(String courseintro) {
		this.courseintro = courseintro;
	}

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

}
