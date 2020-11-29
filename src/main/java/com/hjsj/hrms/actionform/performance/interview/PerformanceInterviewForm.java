package com.hjsj.hrms.actionform.performance.interview;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PerformanceInterviewForm extends FrameForm{
	 private PaginationForm personListForm = new PaginationForm();
	 private String objectid;
	 private String plan_id;
	 private String id;
	 private String interview;
     private String body;
     private  ArrayList tabList = new ArrayList();
     private String status;
     private ArrayList planList = new ArrayList();
     /**=0从自助进，=1从业务进*/
     private String type;
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("body",this.getBody());
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("interview",this.getInterview());
		this.getFormHM().put("objectid", this.getObjectid());
		this.getFormHM().put("plan_id",this.getPlan_id());
		this.getFormHM().put("tabList",this.getTabList());
		this.getFormHM().put("type", this.getType());
	}


	@Override
    public void outPutFormHM() {
		this.setType((String)this.getFormHM().get("type"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setBody((String)this.getFormHM().get("body"));
		this.setId((String)this.getFormHM().get("id"));
		this.setInterview((String)this.getFormHM().get("interview"));
		this.getPersonListForm().setList((ArrayList)this.getFormHM().get("personList"));
		this.setObjectid((String)this.getFormHM().get("objectid"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setTabList((ArrayList)this.getFormHM().get("tabList"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		
		if("/performance/interview/search_interview_list".equals(arg0.getPath())&&(arg1.getParameter("b_init")!=null))
		{
			if(arg1.getParameter("opt")!=null&& "1".equals(arg1.getParameter("opt")))
			{
		    	if(this.getPersonListForm()!=null)
			    	this.getPersonListForm().getPagination().firstPage();
			}
		}
		return super.validate(arg0, arg1);
	}

	public PaginationForm getPersonListForm() {
		return personListForm;
	}


	public void setPersonListForm(PaginationForm personListForm) {
		this.personListForm = personListForm;
	}


	public String getObjectid() {
		return objectid;
	}


	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}


	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getInterview() {
		return interview;
	}


	public void setInterview(String interview) {
		this.interview = interview;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public ArrayList getTabList() {
		return tabList;
	}


	public void setTabList(ArrayList tabList) {
		this.tabList = tabList;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList getPlanList() {
		return planList;
	}


	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
