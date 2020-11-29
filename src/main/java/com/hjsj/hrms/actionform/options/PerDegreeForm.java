package com.hjsj.hrms.actionform.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PerDegreeForm extends FrameForm {
	
	
	private String degreeId;
	private String degreename;
	private String degreedesc;
	private String topscore;
	private String used;
	private String flag;
	private String domainflag;
	private String B0110;
	
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
		this.getFormHM().put("degreeId",this.getDegreeId());
		this.getFormHM().put("degreename",this.getDegreename());
		this.getFormHM().put("degreedesc",this.getDegreedesc());
		this.getFormHM().put("topscore",this.getTopscore());
		this.getFormHM().put("used",this.getUsed());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("domainflag",this.getDomainflag());
		this.getFormHM().put("B0110",this.getB0110());
		
		this.getFormHM().put("num",this.getNum());
		this.getFormHM().put("info",this.getInfo());
		
		this.getFormHM().put("deletestr",this.getDeletestr());
		
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		
		this.setDegreeId((String)this.getFormHM().get("degreeId"));
		this.setDegreename((String)this.getFormHM().get("degreename"));
		this.setDegreedesc((String)this.getFormHM().get("degreedesc"));
		this.setTopscore((String)this.getFormHM().get("topscore"));
		this.setUsed((String)this.getFormHM().get("used"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setDomainflag((String)this.getFormHM().get("domainflag"));
		this.setB0110((String)this.getFormHM().get("B0110"));
		
		
		
		this.setInfo((String)this.getFormHM().get("info"));
		
	}




	public String getDegreeId() {
		return degreeId;
	}

	public void setDegreeId(String degreeId) {
		this.degreeId = degreeId;
	}

	public String getDegreename() {
		return degreename;
	}

	public void setDegreename(String degreename) {
		this.degreename = degreename;
	}

	public String getDegreedesc() {
		return degreedesc;
	}

	public void setDegreedesc(String degreedesc) {
		this.degreedesc = degreedesc;
	}

	public String getTopscore() {
		return topscore;
	}

	public void setTopscore(String topscore) {
		this.topscore = topscore;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDomainflag() {
		return domainflag;
	}

	public void setDomainflag(String domainflag) {
		this.domainflag = domainflag;
	}

	public String getB0110() {
		return B0110;
	}

	public void setB0110(String b0110) {
		B0110 = b0110;
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
