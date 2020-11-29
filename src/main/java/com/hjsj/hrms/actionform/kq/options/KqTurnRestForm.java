package com.hjsj.hrms.actionform.kq.options;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class KqTurnRestForm   extends FrameForm{
	
	private String rdate;
	
	private String tdate;
	
	private String tid;
	
	private String mess;
	private String turnRest_flag;
	private String returnvalue="1";
	private PaginationForm kqTurnRestForm=new PaginationForm();
	
	private String gw_flag;
	private ArrayList selist;
	private ArrayList dlist;

	@Override
    public void outPutFormHM() {
	    this.getKqTurnRestForm().setList((ArrayList)this.getFormHM().get("selist"));
		this.setTdate((String)this.getFormHM().get("tdate"));
		this.setRdate((String)this.getFormHM().get("rdate"));
		this.setTid((String)this.getFormHM().get("tid"));
		this.setMess((String)this.getFormHM().get("mess"));
		this.setTurnRest_flag((String)this.getFormHM().get("turnRest_flag"));
		this.setGw_flag((String)this.getFormHM().get("gw_flag"));
	}

	@Override
    public void inPutTransHM() {
	    this.getFormHM().put("selist",(ArrayList)this.getSelist());
        this.getFormHM().put("dlist",(ArrayList)this.getKqTurnRestForm().getSelectedList());
		this.getFormHM().put("rdate",(String)this.getRdate());
		this.getFormHM().put("tdate",(String)this.getTdate());
		this.getFormHM().put("tid",(String)this.getTid());
		this.getFormHM().put("mess",(String)this.getMess());
		this.getFormHM().put("gw_flag", (String)this.getGw_flag());
	}
	

	public PaginationForm getKqTurnRestForm() {
		return kqTurnRestForm;
	}

	public void setKqTurnRestForm(PaginationForm kqTurnRestForm) {
		this.kqTurnRestForm = kqTurnRestForm;
	}

	public String getRdate() {
		return rdate;
	}

	public void setRdate(String rdate) {
		this.rdate = rdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public String getTurnRest_flag() {
		return turnRest_flag;
	}

	public void setTurnRest_flag(String turnRest_flag) {
		this.turnRest_flag = turnRest_flag;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getGw_flag() {
		return gw_flag;
	}

	public void setGw_flag(String gwFlag) {
		gw_flag = gwFlag;
	}
	
	public ArrayList getSelist() {
        return selist;
    }

    public void setSelist(ArrayList selist) {
        selist = selist;
    }
	

}
