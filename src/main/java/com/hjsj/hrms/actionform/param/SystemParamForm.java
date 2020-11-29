package com.hjsj.hrms.actionform.param;

import com.hrms.struts.action.FrameForm;

import java.util.HashMap;
import java.util.Map;

public class SystemParamForm extends FrameForm {

	private Map paramMap=new HashMap();
	private String module;
	private String encryPwd;
	private String oldEncryPwd;

	public String getEncryPwd() {
		return encryPwd;
	}

	public void setEncryPwd(String encryPwd) {
		this.encryPwd = encryPwd;
	}

	public String getOldEncryPwd() {
		return oldEncryPwd;
	}

	public void setOldEncryPwd(String oldEncryPwd) {
		this.oldEncryPwd = oldEncryPwd;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("module",module);
		this.getFormHM().put("paramMap", paramMap);
		
		this.getFormHM().put("oldEncryPwd", oldEncryPwd);
		this.getFormHM().put("encryPwd", encryPwd);
	}

	@Override
    public void outPutFormHM() {
		Map paramMap = (Map)this.getFormHM().get("paramMap");
		if(paramMap.get("passwordlength")==null||((String)paramMap.get("passwordlength")).length()==0)
			paramMap.put("passwordlength", "8");
		this.setParamMap(paramMap);
		this.setEncryPwd((String)this.getFormHM().get("encryPwd"));
		this.setOldEncryPwd((String)this.getFormHM().get("oldEncryPwd"));
		
	}

	
	public Map getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
}
