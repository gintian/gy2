package com.hjsj.hrms.actionform.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class CheckBodyObjectForm extends FrameForm {
	
	
	private String bodyId;
	private String name;
	private String status;
	private String Seq;
	private String level;
	private String bodyType;
	
	/** 标识操作信息*/
	private String info;
	/** 删除字符串*/
	private String deletestr;
	/** 代表上移和下移*/
	private String num;
	/** 用于控制显示按钮*/
	private String show;
	
	 /***
	  * 临时中的项目调整顺序
	  * */
	 private String[] sort_fields; 
	 private ArrayList sortlist = new ArrayList(); 
	 
	private int current=1;
	private PaginationForm setlistform = new PaginationForm();
	
	private ArrayList setlist=new ArrayList();
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("bodyId",this.getBodyId());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("bodyType",this.getBodyType());
		this.getFormHM().put("info",this.getInfo());
		
		this.getFormHM().put("seq",this.getSeq());
		this.getFormHM().put("num",this.getNum());
		this.getFormHM().put("show",this.getShow());
		
		this.getFormHM().put("deletestr",this.getDeletestr());
		
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		
		this.setBodyId((String)this.getFormHM().get("bodyId"));
		this.setName((String)this.getFormHM().get("name"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setBodyType((String)this.getFormHM().get("bodyType"));
		this.setInfo((String)this.getFormHM().get("info"));
		
		this.setShow((String)this.getFormHM().get("show"));
		
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		
	}

	public String getBodyId() {
		return bodyId;
	}

	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeq() {
		return Seq;
	}

	public void setSeq(String seq) {
		Seq = seq;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
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

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}



}
