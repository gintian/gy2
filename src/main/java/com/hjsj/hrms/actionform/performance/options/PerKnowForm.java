package com.hjsj.hrms.actionform.performance.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PerKnowForm extends FrameForm {
//	private String knowId;
//	private String name;
//	private String status;
//	private String Seq;
	
    private RecordVo perknowvo=new RecordVo("per_know");
	
	
	/** 标识操作信息*/
	private String info;
	/** 删除字符串*/
	private String deletestr;
	/** 代表上移和下移*/
	private String num;
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
		this.getFormHM().put("info",this.getInfo());
		this.getFormHM().put("num",this.getNum());		
		this.getFormHM().put("deletestr",this.getDeletestr());
		this.getFormHM().put("perknowvo",this.getPerknowvo());
	}

	@Override
    public void outPutFormHM() {
		this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));		
		this.setInfo((String)this.getFormHM().get("info"));		
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setPerknowvo((RecordVo)this.getFormHM().get("perknowvo"));		

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

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public RecordVo getPerknowvo() {
		return perknowvo;
	}

	public void setPerknowvo(RecordVo perknowvo) {
		this.perknowvo = perknowvo;
	}

}