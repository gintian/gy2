package com.hjsj.hrms.actionform.gz.gz_analyse;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class GzFilterForm extends FrameForm {
	private String tabID; //指标id
	private String salaryitemid; //指标id
    private ArrayList salaryitemlist = new ArrayList(); //指标列表
    private String seiveid; //条件id
    private String factor;//条件
    private String expr; //因子表达试
    private String flag; //判断是新增还是修改
    private String name; //条件名称
    private ArrayList seivelist = new ArrayList(); //条件列表
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTabID((String)this.getFormHM().get("tabID"));
		this.setSalaryitemid((String)this.getFormHM().get("salaryitemid"));
		this.setSeiveid((String)this.getFormHM().get("seiveid"));
		this.setSalaryitemlist((ArrayList)this.getFormHM().get("salaryitemlist"));
		this.setSeivelist((ArrayList)this.getFormHM().get("seivelist"));
		this.setFactor((String)this.getFormHM().get("factor"));
		this.setExpr((String)this.getFormHM().get("expr"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setName((String)this.getFormHM().get("name"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getSalaryitemid() {
		return salaryitemid;
	}

	public void setSalaryitemid(String salaryitemid) {
		this.salaryitemid = salaryitemid;
	}

	public ArrayList getSalaryitemlist() {
		return salaryitemlist;
	}

	public void setSalaryitemlist(ArrayList salaryitemlist) {
		this.salaryitemlist = salaryitemlist;
	}

	public String getSeiveid() {
		return seiveid;
	}

	public void setSeiveid(String seiveid) {
		this.seiveid = seiveid;
	}

	public void setSeivelist(ArrayList seivelist) {
		this.seivelist = seivelist;
	}

	public ArrayList getSeivelist() {
		return seivelist;
	}

	public String getTabID() {
		return tabID;
	}

	public void setTabID(String tabID) {
		this.tabID = tabID;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {
		this.factor = factor;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
