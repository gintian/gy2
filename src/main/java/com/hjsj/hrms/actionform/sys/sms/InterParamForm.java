package com.hjsj.hrms.actionform.sys.sms;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class InterParamForm extends  FrameForm {
	
	private String com;
	
	private String pwd;
	
	private String valid="0";
	  
	private String qy="0";
	
	private String service;
	
	private String userName;
	
	private String password;
	
	private String upUrl;
	
	private String downUrl;
	
	private String channelId;
	
	// 业务接口列表
	private ArrayList ywList;
	
	// 业务接口代码
	private String ywCode;
	
	// 业务接口描述
	private String ywDesc;
	
	// 业务类
	private String ywClasses;
	
	// 业务类是否启用，1为启用，0为未启用
	private String ywStatus;
	
	// 是否为更新,1为更新，0为新增
	private String isUpdate;
	
	// 企业签名
	private String spname;
	
	public String getSpname() {
		return spname;
	}

	public void setSpname(String spname) {
		this.spname = spname;
	}
	public String getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUpUrl() {
		return upUrl;
	}

	public void setUpUrl(String upUrl) {
		this.upUrl = upUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
		this.setValid("0");
		this.setQy("0");
	}

	@Override
    public void outPutFormHM() {
		this.setCom((String)this.getFormHM().get("com"));
		this.setPwd((String)this.getFormHM().get("pwd"));
		this.setValid((String)this.getFormHM().get("valid"));
		this.setQy((String)this.getFormHM().get("qy"));
		
		this.setService((String)this.getFormHM().get("service"));
		this.setUserName((String)this.getFormHM().get("userName"));
		this.setPassword((String)this.getFormHM().get("password"));
		this.setUpUrl((String)this.getFormHM().get("upUrl"));
		this.setDownUrl((String)this.getFormHM().get("downUrl"));
		this.setChannelId((String)this.getFormHM().get("channelId"));
		
		this.setYwCode((String)this.getFormHM().get("ywCode"));
		this.setYwList((ArrayList)this.getFormHM().get("ywList"));
		this.setYwStatus((String)this.getFormHM().get("ywStatus"));
		this.setYwDesc((String)this.getFormHM().get("ywDesc"));
		this.setYwClasses((String)this.getFormHM().get("ywClasses"));
		this.setIsUpdate((String) this.getFormHM().get("isUpdate"));
		this.setSpname((String) this.getFormHM().get("spname"));
		
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("com",(String)this.getCom());
		this.getFormHM().put("pwd",(String)this.getPwd());
		this.getFormHM().put("valid",(String)this.getValid());
		this.getFormHM().put("qy",(String)this.getQy());
		
		this.getFormHM().put("service",(String)this.getService());
		this.getFormHM().put("userName",(String)this.getUserName());
		this.getFormHM().put("password",(String)this.getPassword());
		this.getFormHM().put("upUrl",(String)this.getUpUrl());
		this.getFormHM().put("downUrl",(String)this.getDownUrl());
		this.getFormHM().put("channelId",(String)this.getChannelId());	
		
		this.getFormHM().put("ywCode", this.getYwCode());
		this.getFormHM().put("ywDesc", this.getYwDesc());
		this.getFormHM().put("ywStatus", this.getYwStatus());
		this.getFormHM().put("ywClasses", this.getYwClasses());
		this.getFormHM().put("spname", this.getSpname());
		
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;  
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getQy() {
		return qy;
	}

	public void setQy(String qy) {
		this.qy = qy;
	}

	public ArrayList getYwList() {
		return ywList;
	}

	public void setYwList(ArrayList ywList) {
		this.ywList = ywList;
	}

	public String getYwCode() {
		return ywCode;
	}

	public void setYwCode(String ywCode) {
		this.ywCode = ywCode;
	}

	public String getYwDesc() {
		return ywDesc;
	}

	public void setYwDesc(String ywDesc) {
		this.ywDesc = ywDesc;
	}

	public String getYwClasses() {
		return ywClasses;
	}

	public void setYwClasses(String ywClasses) {
		this.ywClasses = ywClasses;
	}

	public String getYwStatus() {
		return ywStatus;
	}

	public void setYwStatus(String ywStatus) {
		this.ywStatus = ywStatus;
	}

}
