package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ReviewsForm extends FrameForm {
	private String person_type="";
	private String title="";	
	private String content="";
	private String level="";
	private String a0100="";
	private ArrayList levelList=new ArrayList();
	 private String info_id;
	@Override
    public void outPutFormHM() {
		this.setInfo_id((String)this.getFormHM().get("info_id"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setLevelList((ArrayList)this.getFormHM().get("levelList"));
		this.setPerson_type((String)this.getFormHM().get("person_type"));
		this.setLevel((String)this.getFormHM().get("level"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("info_id", this.getInfo_id());
		this.getFormHM().put("title",this.getTitle());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("level",this.getLevel());
		this.getFormHM().put("a0100",this.getA0100());
		
	}

	
	
	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public ArrayList getLevelList() {
		return levelList;
	}

	public void setLevelList(ArrayList levelList) {
		this.levelList = levelList;
	}

	public String getPerson_type() {
		return person_type;
	}

	public void setPerson_type(String person_type) {
		this.person_type = person_type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo_id() {
		return info_id;
	}

	public void setInfo_id(String info_id) {
		this.info_id = info_id;
	}
	

}
