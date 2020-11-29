package com.hjsj.hrms.actionform.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PerDegreedescForm extends FrameForm {
	
	
	private String id;
	private String degreeId;
	private String itemname;
	private String topscore;
	private String bottomscore;
	private String itemdesc;
	private String percentvalue;
	private String strict;
	private String flag;
	
	/** 标识操作信息*/
	private String info;
	/** 删除字符串*/
	private String deletestr;
	/** 代表上移和下移*/
	private String num;
	
	private int current=1;
	private PaginationForm setlistform = new PaginationForm();
	
	private ArrayList setlist=new ArrayList();
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("degreeId",this.getDegreeId());
		this.getFormHM().put("itemname",this.getItemname());
		this.getFormHM().put("topscore",this.getTopscore());
		this.getFormHM().put("bottomscore",this.getBottomscore());
		this.getFormHM().put("itemdesc",this.getItemdesc());
		this.getFormHM().put("percentvalue",this.getPercentvalue());
		this.getFormHM().put("strict",this.getStrict());
		this.getFormHM().put("flag",this.getFlag());
		
		this.getFormHM().put("num",this.getNum());
		
		this.getFormHM().put("deletestr",this.getDeletestr());
		
		this.getFormHM().put("info",this.getInfo());
		
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		
		this.setId((String)this.getFormHM().get("id"));
		this.setDegreeId((String)this.getFormHM().get("degreeId"));
		this.setItemname((String)this.getFormHM().get("itemname"));
		this.setTopscore((String)this.getFormHM().get("topscore"));
		this.setBottomscore((String)this.getFormHM().get("bottomscore"));
		this.setItemdesc((String)this.getFormHM().get("itemdesc"));
		this.setPercentvalue((String)this.getFormHM().get("percentvalue"));
		this.setStrict((String)this.getFormHM().get("strict"));
		this.setFlag((String)this.getFormHM().get("flag"));
		
		this.setInfo((String)this.getFormHM().get("info"));
		
	}




	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDegreeId() {
		return degreeId;
	}

	public void setDegreeId(String degreeId) {
		this.degreeId = degreeId;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getTopscore() {
		return topscore;
	}

	public void setTopscore(String topscore) {
		this.topscore = topscore;
	}

	public String getBottomscore() {
		return bottomscore;
	}

	public void setBottomscore(String bottomscore) {
		this.bottomscore = bottomscore;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getPercentvalue() {
		return percentvalue;
	}

	public void setPercentvalue(String percentvalue) {
		this.percentvalue = percentvalue;
	}

	public String getStrict() {
		return strict;
	}

	public void setStrict(String strict) {
		this.strict = strict;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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



}
