package com.hjsj.hrms.actionform.performance.warnPlan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:WarnPlanForm.java</p>
 * <p>Description:预警绩效计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-24 11:11:11</p> 
 * @author JinChunhai
 * @version 6.0
 */

public class WarnPlanForm extends FrameForm
{

    private String plan_id = "";
    private String plan_name = "";
    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    private ArrayList setlist = new ArrayList();
    
    private ArrayList leaderList = new ArrayList();
    private String level;
    
    private ArrayList planList = new ArrayList();
    
    
    @Override
    public void inPutTransHM()
    { 
    	
    	this.getFormHM().put("plan_id", this.getPlan_id());
    	this.getFormHM().put("plan_name", this.getPlan_name());
    	this.getFormHM().put("leaderList", this.getLeaderList());
    	this.getFormHM().put("level", this.getLevel());
    	this.getFormHM().put("planList", this.getPlanList());
    }
    
    @Override
    public void outPutFormHM()
    {
    	
    	this.setPlan_id((String)this.getFormHM().get("plan_id"));
    	this.setPlan_name((String)this.getFormHM().get("plan_name"));
    	this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
    	this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
    	this.setLeaderList((ArrayList)this.getFormHM().get("leaderList"));
    	this.setLevel((String)this.getFormHM().get("level"));
    	this.setPlanList((ArrayList)this.getFormHM().get("planList"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
			if("/performance/warnPlan/noScorePersonList".equals(arg0.getPath()) && (arg1.getParameter("b_query")!=null) && arg1.getParameter("firstpage")!=null)
			{
				/**定位到首页,*/
				if(this.getPagination()!=null)
					this.getPagination().firstPage();              
			}			
		    if ("/performance/warnPlan/noScorePersonList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && ("link".equals(arg1.getParameter("b_query"))))
		    {		
				if (this.setlistform.getPagination() != null)				
				    this.setlistform.getPagination().firstPage();												
		    }
		    
		    if("/performance/warnPlan/noAppCardPersonList".equals(arg0.getPath()) && (arg1.getParameter("b_query")!=null) && arg1.getParameter("firstpage")!=null)
			{
				/**定位到首页,*/
				if(this.getPagination()!=null)
					this.getPagination().firstPage();              
			}
		    if ("/performance/warnPlan/noAppCardPersonList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && ("link".equals(arg1.getParameter("b_query"))))
		    {		
				if (this.setlistform.getPagination() != null)				
				    this.setlistform.getPagination().firstPage();												
		    }		    		    
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public PaginationForm getSetlistform() {
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform) {
		this.setlistform = setlistform;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getPlan_name() {
		return plan_name;
	}

	public void setPlan_name(String plan_name) {
		this.plan_name = plan_name;
	}

	public ArrayList getLeaderList() {
		return leaderList;
	}

	public void setLeaderList(ArrayList leaderList) {
		this.leaderList = leaderList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}
    
}