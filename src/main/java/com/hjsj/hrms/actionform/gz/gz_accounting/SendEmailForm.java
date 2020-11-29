package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SendEmailForm extends FrameForm{
	/**所有的邮件模板列表*/
	private ArrayList templateList = new ArrayList();
	/**邮件模板的数组*/
	private String[] templateId = new String[0];
	/**模板的ids*/
	private String id;
	/**单位部门过滤代码*/
	private String code;
	/**薪资表*/
	private String salaryid;
	private String input_type;
	private String num;
    
	@Override
    public void outPutFormHM()
	{
		this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
	    this.setTemplateId((String[])this.getFormHM().get("templateId"));
	    this.setId((String)this.getFormHM().get("id"));
	    this.setCode((String)this.getFormHM().get("code"));
	    this.setSalaryid((String)this.getFormHM().get("salaryid"));
	    this.setInput_type((String)this.getFormHM().get("input_type"));
	    this.setNum((String)this.getFormHM().get("num"));
	}

	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("templateId",this.getTemplateId());
		this.getFormHM().put("id",this.getId());
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String[] getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String[] templateId) {
		this.templateId = templateId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
