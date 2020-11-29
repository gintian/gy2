package com.hjsj.hrms.actionform.performance.implement;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DynaMainRankForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 4, 2008:10:45:56 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DynaMainRankForm extends FrameForm
{
    private String planid;
    private ArrayList rolelist=new ArrayList();
    private String codeid;
    private String isCanSave="0"; //管理权限范围外的机构节点不许修改动态主体权重不显示保存按钮
    private String successflag="1";
    public String getSuccessflag() {
		return successflag;
	}

	public void setSuccessflag(String successflag) {
		this.successflag = successflag;
	}

	@Override
    public void inPutTransHM()
    {
    	this.getFormHM().put("planid",this.getPlanid());
    	this.getFormHM().put("rolelist",this.getRolelist());
    	this.getFormHM().put("successflag",this.getSuccessflag());
    	this.getFormHM().put("codeid",this.getCodeid());
	this.getFormHM().put("isCanSave", this.getIsCanSave());
    }

    @Override
    public void outPutFormHM()
    {
	this.setIsCanSave((String) this.getFormHM().get("isCanSave"));
    	this.setPlanid((String) this.getFormHM().get("planid"));
    	this.setRolelist((ArrayList)this.getFormHM().get("rolelist"));
    	this.setCodeid((String)this.getFormHM().get("codeid"));
    	this.setSuccessflag((String)this.getFormHM().get("successflag"));
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

	public String getIsCanSave()
	{
	
	    return isCanSave;
	}

	public void setIsCanSave(String isCanSave)
	{
	
	    this.isCanSave = isCanSave;
	}
	

  

}
