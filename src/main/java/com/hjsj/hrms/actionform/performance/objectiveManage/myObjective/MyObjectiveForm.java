package com.hjsj.hrms.actionform.performance.objectiveManage.myObjective;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MyObjectiveForm extends FrameForm{
	/**团队绩效计划列表*/
	private PaginationForm myListForm = new PaginationForm();
	/**评估期年份列表*/
	private ArrayList yearList = new ArrayList();
	/**评估期季度列表*/
	private ArrayList quarterList = new ArrayList();
	/**评估期月份列表*/
	private ArrayList monthList = new ArrayList();
	/**计划状态列表*/
	private ArrayList statusList = new ArrayList();
	/**评估期年份*/
	private String year;
	/**评估期季度*/
	private String quarter;
	/**评估期月份*/
	private String month;
	/**计划状态*/
	private String status;
	/**=0团队绩效，=2我的目标*/
    private String module;
    private String rejectreason;
    private String opt;
    private ArrayList tasklist=new ArrayList();
	private PaginationForm taskListForm = new PaginationForm();
	private String startdate="";
	private String enddate="";
	private String latest="";
	private String record="";
	public String getStartdate() {
		return startdate;
	}


	public void setStartdate(String startDate) {
		this.startdate = startDate;
	}


	public String getEnddate() {
		return enddate;
	}


	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}


	public String getLatest() {
		return latest;
	}


	public void setLatest(String latest) {
		this.latest = latest;
	}


	public String getRecord() {
		return record;
	}


	public void setRecord(String record) {
		this.record = record;
	}


	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("quarter",this.getQuarter());
		this.getFormHM().put("month",this.getMonth());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("selectedList",this.getMyListForm().getSelectedList());
		this.getFormHM().put("module",this.getModule());
		this.getFormHM().put("record", this.getRecord());
		this.getFormHM().put("startdate", this.getStartdate());
		this.getFormHM().put("enddate", this.getEnddate());
		this.getFormHM().put("latest", this.getLatest());
	}


	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		//this.setOrgPlanList((ArrayList)this.getFormHM().get("orgPlanList"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setRejectreason((String)this.getFormHM().get("rejectreason"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setQuarterList((ArrayList)this.getFormHM().get("quarterList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.getMyListForm().setList((ArrayList)this.getFormHM().get("myPlanList"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setQuarter((String)this.getFormHM().get("quarter"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setModule((String)this.getFormHM().get("module"));
		this.setTasklist((ArrayList)this.getFormHM().get("tasklist"));
		this.getTaskListForm().setList((ArrayList)this.getTasklist());
		this.setEnddate((String)this.getFormHM().get("enddate"));
		this.setStartdate((String)this.getFormHM().get("startdate"));
		this.setLatest((String)this.getFormHM().get("latest"));
		this.setRecord((String)this.getFormHM().get("record"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=2
		if("performance/objectiveManage/myObjective/my_objective_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null)&& "init".equalsIgnoreCase(arg1.getParameter("b_init")))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();      
            if(this.getMyListForm()!=null)
			{
				this.getMyListForm().getPagination().firstPage();
			}
        }
		if((arg1.getParameter("b_init")!=null&& "init".equals(arg1.getParameter("b_init"))))
		{
			if(this.getMyListForm()!=null)
			{
				this.getMyListForm().getPagination().firstPage();
			}
		}
		if("/performance/objectiveManage/myObjective/searchmytask".equals(arg0.getPath())&&(arg1.getParameter("b_searchtas")!=null)&& "link".equalsIgnoreCase(arg1.getParameter("b_searchtas")))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();      
            if(this.getTaskListForm()!=null)
			{
				this.getTaskListForm().getPagination().firstPage();
			}
        }
		if((arg1.getParameter("b_searchtas")!=null&& "link".equals(arg1.getParameter("b_searchtas"))))
		{
			if(this.getTaskListForm()!=null)
			{
				this.getTaskListForm().getPagination().firstPage();
			}
		}
		return super.validate(arg0, arg1);
	}
	public ArrayList getMonthList() {
		return monthList;
	}


	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}


	public ArrayList getQuarterList() {
		return quarterList;
	}


	public void setQuarterList(ArrayList quarterList) {
		this.quarterList = quarterList;
	}


	public ArrayList getStatusList() {
		return statusList;
	}


	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}


	public ArrayList getYearList() {
		return yearList;
	}


	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}


	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public String getQuarter() {
		return quarter;
	}


	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public PaginationForm getMyListForm() {
		return myListForm;
	}


	public void setMyListForm(PaginationForm myListForm) {
		this.myListForm = myListForm;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getRejectreason() {
		return rejectreason;
	}


	public void setRejectreason(String rejectreason) {
		this.rejectreason = rejectreason;
	}


	public String getOpt() {
		return opt;
	}


	public void setOpt(String opt) {
		this.opt = opt;
	}


	public ArrayList getTasklist() {
		return tasklist;
	}


	public void setTasklist(ArrayList tasklist) {
		this.tasklist = tasklist;
	}


	public PaginationForm getTaskListForm() {
		return taskListForm;
	}


	public void setTaskListForm(PaginationForm taskListForm) {
		this.taskListForm = taskListForm;
	}

}
