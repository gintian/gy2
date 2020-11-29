package com.hjsj.hrms.actionform.performance.implement;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DynaTargetRankForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 4, 2008:2:34:01 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DynaTargetRankForm extends FrameForm
{
    private String planid;
    private ArrayList rolelist=new ArrayList();
    private String codeid;
    private String template_id;
    
    @Override
    public void inPutTransHM()
    {
    	this.getFormHM().put("planid",this.getPlanid());
    	this.getFormHM().put("rolelist",this.getRolelist());
    	this.getFormHM().put("codeid",this.getCodeid());
    	this.getFormHM().put("template_id",this.getTemplate_id());

    }

    @Override
    public void outPutFormHM()
    {
    	this.setPlanid((String) this.getFormHM().get("planid"));
    	this.setRolelist((ArrayList)this.getFormHM().get("rolelist"));
    	this.setCodeid((String)this.getFormHM().get("codeid"));
    }

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public ArrayList getRolelist() {
		return rolelist;
	}

	public void setRolelist(ArrayList rolelist) {
		this.rolelist = rolelist;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

   
	

  

}
