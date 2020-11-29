package com.hjsj.hrms.module.system.regothersys;

import java.util.Date;

public class SysRegBean {

	/**
	 * 系统编号
	 */
	private String syscode;
	
	/**
	 * 系统名称
	 */
	/*private String sysname;*/
	
	/**
	 * 认证标识
	 */
	private String sysetoken;
	
	/**
	 * 是否启用 
	 * 1:启用, 0停用
	 */
	private String valid;
	
	/**
	 * 服务列表及sql的取值范围
	 */
	private String servicedetail;
	
	/**
	 * 验证方式
	 * 1:动态验证, 0:固定认证码验证
	 */
	private String validateway;
	
	/**
	 * 动态验证码
	 */
	private String dynamicEtoken;
	
	/**
	 * 动态验证码生成时间
	 */
	private Date etokencreatetime;

	public String getSyscode() {
		return syscode;
	}

	public void setSyscode(String syscode) {
		this.syscode = syscode;
	}

	public String getSysetoken() {
		return sysetoken;
	}

	public void setSysetoken(String sysetoken) {
		this.sysetoken = sysetoken;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getServicedetail() {
		return servicedetail;
	}

	public void setServicedetail(String servicedetail) {
		this.servicedetail = servicedetail;
	}

	public String getValidateway() {
		return validateway;
	}

	public void setValidateway(String validateway) {
		this.validateway = validateway;
	}

	public String getDynamicEtoken() {
		return dynamicEtoken;
	}

	public void setDynamicEtoken(String dynamicEtoken) {
		this.dynamicEtoken = dynamicEtoken;
	}

	public Date getEtokencreatetime() {
		return etokencreatetime;
	}

	public void setEtokencreatetime(Date etokencreatetime) {
		this.etokencreatetime = etokencreatetime;
	}
	
	
	
}
