package com.hjsj.hrms.module.system.distributedreporting.drbean;

import java.io.Serializable;
/**
 * @version: 1.0
 * @Description: 用于定义校验规则数据的封装
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:52:04
 */
public class DRValidateBean implements Serializable{
	private int checkId;
	private String checkname;
	private String checkField;
	private String condition;
	private boolean forcestate;
	private boolean valid;
	
	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public String getCheckname() {
		return checkname;
	}

	public void setCheckname(String checkname) {
		this.checkname = checkname;
	}

	public String getCheckField() {
		return checkField;
	}

	public void setCheckField(String checkField) {
		this.checkField = checkField;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public boolean isForcestate() {
		return forcestate;
	}

	public void setForcestate(boolean forcestate) {
		this.forcestate = forcestate;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public DRValidateBean(int checkId, String checkname, String checkField, String condition, boolean forcestate,
			boolean valid) {
		super();
		this.checkId = checkId;
		this.checkname = checkname;
		this.checkField = checkField;
		this.condition = condition;
		this.forcestate = forcestate;
		this.valid = valid;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[checkId=").append(this.checkId);
		buffer.append(",checkname=").append(this.checkname);
		buffer.append(",checkField=").append(this.checkField);
		buffer.append(",condition=").append(this.condition);
		buffer.append(",forcestate=").append(this.forcestate);
		buffer.append(",valid=").append(this.valid);
		buffer.append("]");
		return buffer.toString();
	}
	
}
