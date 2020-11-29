package com.hjsj.hrms.actionform.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PerParamForm extends FrameForm {
	
	
	private String id;
	private String kind;
	private String content;
	private String username;
	private String paramName;
	
	/** 标识操作信息*/
	private String info;
	/** 删除字符串*/
	private String deletestr;
	/** 参考项目*/
	private String project;
	
	private int current=1;
	private PaginationForm setlistform = new PaginationForm();
	
	private ArrayList setlist=new ArrayList();
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("username",this.getUsername());
		this.getFormHM().put("paramName",this.getParamName());
		
		this.getFormHM().put("info",this.getInfo());
		this.getFormHM().put("deletestr",this.getDeletestr());
		
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		
		this.setId((String)this.getFormHM().get("id"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setParamName((String)this.getFormHM().get("param_name"));
		
		this.setInfo((String)this.getFormHM().get("info"));
		
	}




	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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

	public String getDeletestr() {
		return deletestr;
	}

	public void setDeletestr(String deletestr) {
		this.deletestr = deletestr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}




}
