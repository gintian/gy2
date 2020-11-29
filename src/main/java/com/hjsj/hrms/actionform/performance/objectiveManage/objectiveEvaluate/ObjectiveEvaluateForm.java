package com.hjsj.hrms.actionform.performance.objectiveManage.objectiveEvaluate;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

public class ObjectiveEvaluateForm extends FrameForm{

    private String year;
    private ArrayList yearList = new ArrayList();
    private String quarter;
    private ArrayList quarterList = new ArrayList(); 
    private String month;
    private ArrayList monthList = new ArrayList();
    private String status;
    private ArrayList statusList = new ArrayList();
    private PaginationForm personListForm = new PaginationForm();
    private String entranceType;//进入入口：=0为正常的从目标管理进入，=5为从首页进入
    private String plan_id;
    private String isSort;//是否按总分排序=0不排，=1按总分排序
    private String isOrder;
    //田野添加判断前台是否显示‘总体评价’标记属性
    private String showWholeEvaluate;
    private String showAccumulativRank;
    private Map optMap; // 打分确认标识 by 刘蒙
    
    private String pendingCode ;//代办任务编号  2013.12.28 pjf
    
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isOrder", this.getIsOrder());
		this.getFormHM().put("isSort", this.getIsSort());
		this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("entranceType", this.getEntranceType());
		this.getFormHM().put("selectedList",this.getPersonListForm().getSelectedList());
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("month",this.getMonth());
		this.getFormHM().put("quarter",this.getQuarter());
		this.getFormHM().put("showWholeEvaluate", this.getShowWholeEvaluate());
		this.getFormHM().put("showAccumulativRank", this.getShowAccumulativRank());
		this.getFormHM().put("pendingCode", this.getPendingCode());
		
	}


	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setIsOrder((String)this.getFormHM().get("isOrder"));
		this.setIsSort((String)this.getFormHM().get("isSort"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setEntranceType((String)this.getFormHM().get("entranceType"));
		this.getPersonListForm().setList((ArrayList)this.getFormHM().get("personList"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setMonthList((ArrayList)this.getFormHM().get("monthList"));
		this.setQuarter((String)this.getFormHM().get("quarter"));
		this.setQuarterList((ArrayList)this.getFormHM().get("quarterList"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.setShowWholeEvaluate((String)this.getFormHM().get("showWholeEvaluate"));
		this.setShowAccumulativRank((String)this.getFormHM().get("showAccumulativRank"));
		this.setPendingCode((String)this.getFormHM().get("pendingCode"));
		this.optMap = (Map)this.getFormHM().get("optMap"); // 打分确认标识
		}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=2
		if("/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null)&& "init".equalsIgnoreCase(arg1.getParameter("b_init")))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();      
            if(this.getPersonListForm()!=null)
			{
				this.getPersonListForm().getPagination().firstPage();
			}
        }
		if((arg1.getParameter("b_init")!=null&& "init".equals(arg1.getParameter("b_init"))))
		{
			if(this.getPersonListForm()!=null)
			{
				this.getPersonListForm().getPagination().firstPage();
			}
		}
		return super.validate(arg0, arg1);
	}

	public String getShowAccumulativRank() {
		return showAccumulativRank;
	}


	public void setShowAccumulativRank(String showAccumulativRank) {
		this.showAccumulativRank = showAccumulativRank;
	}


	public String getShowWholeEvaluate() {
		return showWholeEvaluate;
	}


	public void setShowWholeEvaluate(String showWholeEvaluate) {
		this.showWholeEvaluate = showWholeEvaluate;
	}


	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public ArrayList getMonthList() {
		return monthList;
	}


	public void setMonthList(ArrayList monthList) {
		this.monthList = monthList;
	}


	public PaginationForm getPersonListForm() {
		return personListForm;
	}


	public void setPersonListForm(PaginationForm personListForm) {
		this.personListForm = personListForm;
	}


	public String getQuarter() {
		return quarter;
	}


	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}


	public ArrayList getQuarterList() {
		return quarterList;
	}


	public void setQuarterList(ArrayList quarterList) {
		this.quarterList = quarterList;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList getStatusList() {
		return statusList;
	}


	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public ArrayList getYearList() {
		return yearList;
	}


	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}


	public String getEntranceType() {
		return entranceType;
	}


	public void setEntranceType(String entranceType) {
		this.entranceType = entranceType;
	}


	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}


	public String getIsSort() {
		return isSort;
	}


	public void setIsSort(String isSort) {
		this.isSort = isSort;
	}


	public String getIsOrder() {
		return isOrder;
	}


	public void setIsOrder(String isOrder) {
		this.isOrder = isOrder;
	}


	/**
	 * @return the pendingCode
	 */
	public String getPendingCode() {
		return pendingCode;
	}


	/**
	 * @param pendingCode the pendingCode to set
	 */
	public void setPendingCode(String pendingCode) {
		this.pendingCode = pendingCode;
	}
	
	public Map getOptMap() {
		return optMap;
	}
	
	public void setOptMap(Map optMap) {
		this.optMap = optMap;
	}

}
