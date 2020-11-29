package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class EmpChangeForm  extends FrameForm{

	 private String start_date;
	 private String end_date;
	 private ArrayList kq_dbase_list = new ArrayList();;
	 private String changestatus; 
	 private String sqlstr;
	 private String columns; 
	 private String strwhere;
	 private String orderby;
	 private PaginationForm recordListForm=new PaginationForm();    
	 private ArrayList list=new ArrayList();
	 private String a0100;
	 private String curdate;
	 private String userbase; 
	 private String workcalendar;//**应用库表前缀*/
	 private String TabName;
	 private String add_count;
	 private String base_count;
	 private String leave_count;
	 private String unusal_count;
	 private String change_date;
	 private String re_static;
	 private String uplevel;
	 // 考勤参数中是否设置了部门变动时间,0为未设置，1为已设置
	 private String deptChange;
	 private String ishaveadd="";
	 private String ishavecut="";
	 private String ishavechange="";
	 private String ishaveexce="";
	 public String getDeptChange() {
		return deptChange;
	}
	public void setDeptChange(String deptChange) {
		this.deptChange = deptChange;
	}
	public String getRe_static() {
		return re_static;
	}
	public void setRe_static(String re_static) {
		this.re_static = re_static;
	}
	public String getChange_date() {
		return change_date;
	}
	public void setChange_date(String change_date) {
		this.change_date = change_date;
	}
	public String getAdd_count() {
		return add_count;
	}
	public void setAdd_count(String add_count) {
		this.add_count = add_count;
	}
	public String getBase_count() {
		return base_count;
	}
	public void setBase_count(String base_count) {
		this.base_count = base_count;
	}
	public String getLeave_count() {
		return leave_count;
	}
	public void setLeave_count(String leave_count) {
		this.leave_count = leave_count;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getCurdate() {
		return curdate;
	}
	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public String getUserbase() {
		return userbase;
	}
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	
	private ArrayList datelist=new ArrayList();//日期list
	public ArrayList getDatelist() {
		return datelist;
	}
	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}
	@Override
    public void outPutFormHM()
	{
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setCurdate((String)this.getFormHM().get("curdate"));
		this.setChangestatus((String)this.getFormHM().get("changestatus"));
	    this.setStart_date((String)this.getFormHM().get("start_date"));
	    this.setEnd_date((String)this.getFormHM().get("end_date"));
	 	this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
	 	this.getRecordListForm().setList((ArrayList)this.getFormHM().get("changelist"));
	 	this.setDatelist((ArrayList)this.getFormHM().get("datelist"));	
	    this.setWorkcalendar((String)this.getFormHM().get("workcalendar"));
	    this.setTabName((String)this.getFormHM().get("TabName"));
	    this.setLeave_count((String)this.getFormHM().get("leave_count"));
	    this.setAdd_count((String)this.getFormHM().get("add_count"));
	    this.setBase_count((String)this.getFormHM().get("base_count"));
	    this.setUnusal_count((String) this.getFormHM().get("unusal_count"));
	    this.setChange_date((String)this.getFormHM().get("change_date"));
	    this.setRe_static((String)this.getFormHM().get("re_static"));
	    this.setUplevel((String) this.getFormHM().get("uplevel"));
	    this.setDeptChange((String) this.getFormHM().get("deptChange"));
	    this.setIshaveadd((String)this.getFormHM().get("ishaveadd"));
	    this.setIshavecut((String)this.getFormHM().get("ishavecut"));
	    this.setIshavechange((String)this.getFormHM().get("ishavechange"));
	    this.setIshaveexce((String)this.getFormHM().get("ishaveexce"));		
	}
	@Override
    public void inPutTransHM()
	{   this.getFormHM().put("a0100",a0100);
	    this.getFormHM().put("userbase",userbase);
	    this.getFormHM().put("curdate",curdate);
		this.getFormHM().put("changestatus",changestatus);
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
	    this.getFormHM().put("TabName",this.getTabName());
	    this.getFormHM().put("change_date",this.getChange_date());
	    this.getFormHM().put("re_static", this.getRe_static());
	}
	public String getChangestatus() {
		return changestatus;
	}
	public void setChangestatus(String changestatus) {
		this.changestatus = changestatus;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getStrwhere() {
		return strwhere;
	}
	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}
	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}
	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}
	public String getWorkcalendar() {
		return workcalendar;
	}
	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/register/empchange".equals(arg0.getPath())&&arg1.getParameter("b_empleave")!=null)
	    {
	    	if(this.recordListForm.getPagination()!=null)
		          this.recordListForm.getPagination().firstPage();//?
	    }
	    if("/kq/register/empchange".equals(arg0.getPath())&&arg1.getParameter("b_empadd")!=null)
	    {
	    	if(this.recordListForm.getPagination()!=null)
		          this.recordListForm.getPagination().firstPage();//?
	    }	   
	    if("/kq/register/empchange_add".equals(arg0.getPath())&&arg1.getParameter("b_empadd")!=null)
	    {
	    	if(this.recordListForm.getPagination()!=null)
		          this.recordListForm.getPagination().firstPage();//?
	    }
	    if("/kq/register/empchange_leave".equals(arg0.getPath())&&arg1.getParameter("b_empleave")!=null)
	    {
	        if(this.recordListForm.getPagination()!=null)
	          this.recordListForm.getPagination().firstPage();//?
	    }
	    if("/kq/register/empchangebase".equals(arg0.getPath())&&arg1.getParameter("b_empbase")!=null)
	    {
	    	if(this.recordListForm.getPagination()!=null)
		          this.recordListForm.getPagination().firstPage();//?
	    }
	    return super.validate(arg0, arg1);
	}
	public String getTabName() {
		return TabName;
	}
	public void setTabName(String tabName) {
		TabName = tabName;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getUnusal_count() {
		return unusal_count;
	}
	public void setUnusal_count(String unusal_count) {
		this.unusal_count = unusal_count;
	}
	public String getIshaveadd() {
		return ishaveadd;
	}
	public void setIshaveadd(String ishaveadd) {
		this.ishaveadd = ishaveadd;
	}
	public String getIshavecut() {
		return ishavecut;
	}
	public void setIshavecut(String ishavecut) {
		this.ishavecut = ishavecut;
	}
	public String getIshavechange() {
		return ishavechange;
	}
	public void setIshavechange(String ishavechange) {
		this.ishavechange = ishavechange;
	}
	public String getIshaveexce() {
		return ishaveexce;
	}
	public void setIshaveexce(String ishaveexce) {
		this.ishaveexce = ishaveexce;
	}
}
