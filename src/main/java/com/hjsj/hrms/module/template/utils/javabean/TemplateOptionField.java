package com.hjsj.hrms.module.template.utils.javabean;

import java.util.HashMap;

public class TemplateOptionField {
	private String approverName;//审批人姓名
	private String approverUnit;//审批人单位
	private String approverDepartment;//审批人部门
	private String approverAnnotation;//审批人批注
	private String approverOpinion;//审批人意见  同意 不同意
	private String approverType;//审批人类型   申请人  审批人
	private String approvalTime;//审批时间
	private String approverRole;//审批人角色
	//构造对象时默认所有值置为空
	public TemplateOptionField(){
		 this.approverName="";
		 this.approverUnit="";
		 this.approverDepartment="";
		 this.approverAnnotation="";
		 this.approverOpinion="";
		 this.approverType="";
		 this.approvalTime="";
		 this.approverRole="";
	}
	public String getApproverName() {
		return  this.approverName;
	}
	public void setApproverName(String approverName) {
		 this.approverName = approverName;
	}
	public String getApproverUnit() {
		return  this.approverUnit;
	}
	public void setApproverUnit(String approverUnit) {
		 this.approverUnit = approverUnit;
	}
	public String getApproverDepartment() {
		return  this.approverDepartment;
	}
	public void setApproverDepartment(String approverDepartment) {
		 this.approverDepartment = approverDepartment;
	}
	public String getApproverAnnotation() {
		return  this.approverAnnotation;
	}
	public void setApproverAnnotation(String approverAnnotation) {
		 this.approverAnnotation = approverAnnotation;
	}
	public String getApproverOpinion() {
		return  this.approverOpinion;
	}
	public void setApproverOpinion(String approverOpinion) {
		 this.approverOpinion = approverOpinion;
	}
	public String getApproverType() {
		return  this.approverType;
	}
	public void setApproverType(String approverType) {
		 this.approverType = approverType;
	}
	public String getApprovalTime() {
		return  this.approvalTime;
	}
	public void setApprovalTime(String approvalTime) {
		 this.approvalTime = approvalTime;
	}
	public String getApproverRole() {
		return  this.approverRole;
	}
	public void setApproverRole(String approverRole) {
		 this.approverRole = approverRole;
	}	
	//将对象转换成map，用于生成json数据。
	public HashMap changeObjectToMap(){
		HashMap map=new HashMap();
		map.put("approverName", this.approverName);
		//map.put("approverUnit", this.approverUnit);
		//map.put("approverDepartment", this.approverDepartment);
		map.put("approverAnnotation", this.approverAnnotation);
		map.put("approverOpinion", this.approverOpinion);
		map.put("approverType", this.approverType);
		map.put("approvalTime", this.approvalTime);
		map.put("approverRole", this.approverRole);
		return map;
	}
}
