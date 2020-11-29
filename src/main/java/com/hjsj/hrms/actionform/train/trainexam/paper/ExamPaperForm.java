package com.hjsj.hrms.actionform.train.trainexam.paper;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ExamPaperForm extends FrameForm {

	private ArrayList itemlist=new ArrayList();
	private String strsql;
	private String strwhere;
	private String columns;
	private String order_by;
	
	private String r5301;//试卷名称  用于查询
	private String r5307;//试卷类型  用于查询
	private String r5308;//组卷方法  用于查询
	
	private String r5311;//发布状态
	private String r5300;
	private String orgparentcode;
	private String start;//用于排序 起始id
	private String end;//用于排序 终止id
	
	/**关联课程*/
	private String itemize;//分类编码
	private String itemizevalue;//课程分类
	private String coursename;//课程名称
	private String courseintro;//课程内容
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("r5301", this.getR5301());
		this.getFormHM().put("r5307", this.getR5307());
		this.getFormHM().put("r5308", this.getR5308());
		
		this.getFormHM().put("r5300", this.getR5300());
		this.getFormHM().put("itemlist", this.getItemlist());
		
		this.getFormHM().put("itemize", this.getItemize());
		this.getFormHM().put("coursename", this.getCoursename());
		this.getFormHM().put("courseintro", this.getCourseintro());
	}

	@Override
    public void outPutFormHM() {

		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		this.setR5300((String)this.getFormHM().get("r5300"));
		this.setR5311((String)this.getFormHM().get("r5311"));
		this.setStart((String)this.getFormHM().get("start"));
		this.setEnd((String)this.getFormHM().get("end"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/trainexam/paper".equals(arg0.getPath()) && arg1.getParameter("b_query") != null
	            && "link".equalsIgnoreCase(arg1.getParameter("b_query")))
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }
	    if ("/train/trainexam/paper/relcourse".equals(arg0.getPath()) && arg1.getParameter("b_relcourse") != null)
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

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getR5300() {
		return r5300;
	}

	public void setR5300(String r5300) {
		this.r5300 = r5300;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getR5301() {
		return r5301;
	}

	public void setR5301(String r5301) {
		this.r5301 = r5301;
	}

	public String getR5307() {
		return r5307;
	}

	public void setR5307(String r5307) {
		this.r5307 = r5307;
	}

	public String getR5308() {
		return r5308;
	}

	public void setR5308(String r5308) {
		this.r5308 = r5308;
	}

	public String getR5311() {
		return r5311;
	}

	public void setR5311(String r5311) {
		this.r5311 = r5311;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
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

}
