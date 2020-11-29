package com.hjsj.hrms.actionform.performance;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title:HandSelForm.java</p>
 * <p>Description:绩效手工选择通用</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-07-17 11:11:11</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class HandSelForm extends FrameForm
{
	
	// 业务分类字段 =0(绩效考核); =1(能力素质)
    String busitype = "0";
	String objName="";
	String right_fields="";
	String objsStr="";
	String opt="";
	String aplanid="";
	String object_type="";
	String planName="";//显示计划名称
	
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("busitype", this.getBusitype());
		this.getFormHM().put("objName", this.getObjName());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("objsStr", this.getObjsStr());
		this.getFormHM().put("opt", this.getOpt());
		this.getFormHM().put("object_type", this.getObject_type());
		this.getFormHM().put("aplanid", this.getAplanid());
		this.getFormHM().put("planName",this.getPlanName());
	}

	@Override
    public void outPutFormHM()
	{
		this.setBusitype((String)this.getFormHM().get("busitype"));
		this.setObjName((String) this.getFormHM().get("objName"));
		this.setRight_fields((String) this.getFormHM().get("right_fields"));
		this.setObjsStr((String) this.getFormHM().get("objsStr"));
		this.setOpt((String) this.getFormHM().get("opt"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setAplanid((String) this.getFormHM().get("aplanid"));	
		this.setPlanName((String) this.getFormHM().get("planName"));	
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	    {
			try
			{
				arg1.setAttribute("targetWindow", "1");
			} catch (Exception e)
			{
			    e.printStackTrace();
			}
			return super.validate(arg0, arg1);
	    }

	public String getObject_type(){
		return object_type;
	}

	public void setObject_type(String object_type){
		this.object_type = object_type;
	}

	public String getObjName(){
		return objName;
	}

	public void setObjName(String objName){
		this.objName = objName;
	}

	public String getObjsStr(){
		return objsStr;
	}

	public void setObjsStr(String objsStr){
		this.objsStr = objsStr;
	}

	public String getOpt(){
		return opt;
	}

	public void setOpt(String opt){
		this.opt = opt;
	}

	public String getAplanid(){
		return aplanid;
	}

	public void setAplanid(String aplanid){
		this.aplanid = aplanid;
	}

	public String getRight_fields(){
		return right_fields;
	}

	public void setRight_fields(String right_fields){
		this.right_fields = right_fields;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
	
}
