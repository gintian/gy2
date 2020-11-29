package com.hjsj.hrms.actionform.sys.sms;


import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class MailListForm extends FrameForm {
	
	private String state;
	
	private String str;
	
	private String where;
	
	private String conum;
	
	private ArrayList selist=new ArrayList();
	
	private RecordVo mail=new RecordVo("kq_item");
	
	private String startime;
	
	private String endtime;
	
	private String tabid;
	
	// 登记表id
	private String cardid;
	
	// 排序
	private String order;
	private PaginationForm mailForm=new PaginationForm();

	@Override
    public void outPutFormHM() {
		this.setStr((String)this.getFormHM().get("str"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setConum((String)this.getFormHM().get("conum"));
		this.setStartime((String)this.getFormHM().get("startime"));
		this.setEndtime((String)this.getFormHM().get("endtime"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setCardid((String) this.getFormHM().get("cardid"));
		this.setOrder((String) this.getFormHM().get("order"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if(this.getPagination()!=null)
            this.getPagination().firstPage();		
		return super.validate(arg0, arg1);
	}

	@Override
    public void inPutTransHM() {
		if(this.getPagination()!=null)
		    this.getFormHM().put("selist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("state",(String)this.getState());
		this.getFormHM().put("startime",(String)this.getStartime());
		this.getFormHM().put("endtime",(String)this.getEndtime());
		
	}

	public RecordVo getMail() {
		return mail;
	}

	public void setMail(RecordVo mail) {
		this.mail = mail;
	}

	public PaginationForm getMailForm() {
		return mailForm;
	}

	public void setMailForm(PaginationForm mailForm) {
		this.mailForm = mailForm;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getConum() {
		return conum;
	}

	public void setConum(String conum) {
		this.conum = conum;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public ArrayList getSelist() {
		return selist;
	}

	public void setSelist(ArrayList selist) {
		this.selist = selist;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
