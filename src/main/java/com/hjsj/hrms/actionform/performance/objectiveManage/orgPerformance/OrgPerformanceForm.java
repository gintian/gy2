package com.hjsj.hrms.actionform.performance.objectiveManage.orgPerformance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class OrgPerformanceForm extends FrameForm{
	/**团队绩效计划列表*/
	//private ArrayList orgPlanList = new ArrayList();
	private PaginationForm planListForm = new PaginationForm();
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
    private String plan_id;
    /**考核对象的目标卡审批状态*/
    private String spStatus="";
    private ArrayList spStatusList = new ArrayList();
    private String isTargetCardTemp="false";
    
    /**
     * @return the isTargetCardTemp
     */
    public String getIsTargetCardTemp() {
        return isTargetCardTemp;
    }

    /**
     * @param isTargetCardTemp the isTargetCardTemp to set
     */
    public void setIsTargetCardTemp(String isTargetCardTemp) {
        this.isTargetCardTemp = isTargetCardTemp;
    }
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("spStatus", this.getSpStatus());
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("quarter",this.getQuarter());
		this.getFormHM().put("month",this.getMonth());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("selectedList",this.getPlanListForm().getSelectedList());
		this.getFormHM().put("module",this.getModule());
		this.getFormHM().put("isTargetCardTemp", this.getIsTargetCardTemp());
	}


	@Override
    public void outPutFormHM() {
		this.setSpStatus((String)this.getFormHM().get("spStatus"));
		this.setSpStatusList((ArrayList)this.getFormHM().get("spStatusList"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		//this.setOrgPlanList((ArrayList)this.getFormHM().get("orgPlanList"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setQuarterList((ArrayList)this.getFormHM().get("quarterList"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.getPlanListForm().setList((ArrayList)this.getFormHM().get("orgPlanList"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setQuarter((String)this.getFormHM().get("quarter"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setModule((String)this.getFormHM().get("module"));
		this.setIsTargetCardTemp((String)this.getFormHM().get("isTargetCardTemp"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/performance/objectiveManage/orgPerformance/org_performance_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null)&& "init".equalsIgnoreCase(arg1.getParameter("b_init")))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/performance/objectiveManage/orgPerformance/org_performance_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null)&& "init".equalsIgnoreCase(arg1.getParameter("b_init")))
		{
			if(this.getPlanListForm()!=null)
				this.getPlanListForm().getPagination().firstPage();
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


	public PaginationForm getPlanListForm() {
		return planListForm;
	}


	public void setPlanListForm(PaginationForm planListForm) {
		this.planListForm = planListForm;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}


	public String getSpStatus() {
		return spStatus;
	}


	public void setSpStatus(String spStatus) {
		this.spStatus = spStatus;
	}


	public ArrayList getSpStatusList() {
		return spStatusList;
	}


	public void setSpStatusList(ArrayList spStatusList) {
		this.spStatusList = spStatusList;
	}

}
