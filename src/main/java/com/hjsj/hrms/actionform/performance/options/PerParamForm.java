package com.hjsj.hrms.actionform.performance.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 * <p>Title:PerParamForm.java</p>
 * <p> Description:</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-18 10:14:15</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class PerParamForm extends FrameForm 
{
	
    private RecordVo perparamvo=new RecordVo("per_param");
	
	/** 标识操作信息*/
	private String info;
	/** 删除字符串*/
	private String deletestr;
	/** 参考项目*/
	private String project;	
	private int current=1;
	private PaginationForm setlistform = new PaginationForm();	
	private ArrayList setlist=new ArrayList();	
	private String from_eval="1";
	
	
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("perparamvo",this.getPerparamvo());
		this.getFormHM().put("info",this.getInfo());
		this.getFormHM().put("deletestr",this.getDeletestr());
		this.getFormHM().put("from_eval",this.getFrom_eval());
	}

	@Override
    public void outPutFormHM()
	{
		this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setPerparamvo((RecordVo)this.getFormHM().get("perparamvo"));
		this.setFrom_eval((String)this.getFormHM().get("from_eval"));
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

	public RecordVo getPerparamvo() {
		return perparamvo;
	}

	public void setPerparamvo(RecordVo perparamvo) {
		this.perparamvo = perparamvo;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getFrom_eval()
	{
		return from_eval;
	}

	public void setFrom_eval(String from_eval)
	{
		this.from_eval = from_eval;
	}

}
