/*
 * Created on 2005-10-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ZppersonForm extends FrameForm {
	CardTagParamView cardparam=new CardTagParamView();
	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	/**
     * 工作地点
     */
    private String domain_value = "";
    /**
     * 发布日期
     */
    private String valid_date = "";
    /**
     * 职位
     */
    private String pos_id = "";
    /**
     * 隐藏工作地点
     */
    private String hidden_domain = "";
    /**
     * 隐藏发布日期
     */
    private String hidden_valid_date = "";
    /**
     * 隐藏职位
     */
    private String hidden_pos_id = "";
    /**
     * 紧急招聘岗位列表
     */
    ArrayList urgentzpposlist = new ArrayList();
	/**
	 * @return Returns the zp_pos_id.
	 */
	public String getZp_pos_id() {
		return zp_pos_id;
	}
	/**
	 * @param zp_pos_id The zp_pos_id to set.
	 */
	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}
    private String zp_pos_id = "";
    private String zp_pos_id_value = "";
    /**
     * 紧急招聘岗位列表
     */
    private String loginFlag = "";
	/**
     * 招聘岗位对象列表
     */
    private PaginationForm zppersonForm=new PaginationForm(); 
    private PaginationForm zppositionForm=new PaginationForm(); 
	
	@Override
    public void outPutFormHM() {
		this.getZppersonForm().setList((ArrayList)this.getFormHM().get("zppersonlist"));
		this.getZppositionForm().setList((ArrayList)this.getFormHM().get("zppositionlist"));
		this.setZp_pos_id((String)this.getFormHM().get("zp_pos_id"));
		this.setUrgentzpposlist((ArrayList)this.getFormHM().get("urgentzpposlist"));
		this.setLoginFlag((String)this.getFormHM().get("loginflag"));
		this.setZp_pos_id_value((String)this.getFormHM().get("zp_pos_id_value"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("domain_value",this.getDomain_value());
	    this.getFormHM().put("valid_date",this.getValid_date());
	    this.getFormHM().put("pos_id",this.getPos_id());
	    this.getFormHM().put("hidden_domain",this.getHidden_domain());
	    this.getFormHM().put("hidden_valid_date",this.getHidden_valid_date());
	    this.getFormHM().put("hidden_pos_id",this.getHidden_pos_id());
	}
	 @Override
     public void reset(ActionMapping arg0, HttpServletRequest arg1)
	 {
	    	
	      super.reset(arg0, arg1);
	        
	  }
	/**
	 * @return Returns the domain_value.
	 */
	public String getDomain_value() {
		return domain_value;
	}
	/**
	 * @param domain_value The domain_value to set.
	 */
	public void setDomain_value(String domain_value) {
		this.domain_value = domain_value;
	}
	/**
	 * @return Returns the pos_id.
	 */
	public String getPos_id() {
		return pos_id;
	}
	/**
	 * @param pos_id The pos_id to set.
	 */
	public void setPos_id(String pos_id) {
		this.pos_id = pos_id;
	}
	/**
	 * @return Returns the valid_date.
	 */
	public String getValid_date() {
		return valid_date;
	}
	/**
	 * @param valid_date The valid_date to set.
	 */
	public void setValid_date(String valid_date) {
		this.valid_date = valid_date;
	}
	/**
	 * @return Returns the zppersonForm.
	 */
	public PaginationForm getZppersonForm() {
		return zppersonForm;
	}
	/**
	 * @param zppersonForm The zppersonForm to set.
	 */
	public void setZppersonForm(PaginationForm zppersonForm) {
		this.zppersonForm = zppersonForm;
	}
	/**
	 * @return Returns the urgentzpposlist.
	 */
	public ArrayList getUrgentzpposlist() {
		return urgentzpposlist;
	}
	/**
	 * @param urgentzpposlist The urgentzpposlist to set.
	 */
	public void setUrgentzpposlist(ArrayList urgentzpposlist) {
		this.urgentzpposlist = urgentzpposlist;
	}
	/**
	 * @return Returns the zppositionForm.
	 */
	public PaginationForm getZppositionForm() {
		return zppositionForm;
	}
	/**
	 * @param zppositionForm The zppositionForm to set.
	 */
	public void setZppositionForm(PaginationForm zppositionForm) {
		this.zppositionForm = zppositionForm;
	}
	/**
	 * @return Returns the hidden_domain.
	 */
	public String getHidden_domain() {
		return hidden_domain;
	}
	/**
	 * @param hidden_domain The hidden_domain to set.
	 */
	public void setHidden_domain(String hidden_domain) {
		this.hidden_domain = hidden_domain;
	}
	/**
	 * @return Returns the hidden_pos_id.
	 */
	public String getHidden_pos_id() {
		return hidden_pos_id;
	}
	/**
	 * @param hidden_pos_id The hidden_pos_id to set.
	 */
	public void setHidden_pos_id(String hidden_pos_id) {
		this.hidden_pos_id = hidden_pos_id;
	}
	/**
	 * @return Returns the hidden_valid_date.
	 */
	public String getHidden_valid_date() {
		return hidden_valid_date;
	}
	/**
	 * @param hidden_valid_date The hidden_valid_date to set.
	 */
	public void setHidden_valid_date(String hidden_valid_date) {
		this.hidden_valid_date = hidden_valid_date;
	}
	/**
	 * @return Returns the loginFlag.
	 */
	public String getLoginFlag() {
		return loginFlag;
	}
	/**
	 * @param loginFlag The loginFlag to set.
	 */
	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}
	/**
	 * @return Returns the zp_pos_id_value.
	 */
	public String getZp_pos_id_value() {
		return zp_pos_id_value;
	}
	/**
	 * @param zp_pos_id_value The zp_pos_id_value to set.
	 */
	public void setZp_pos_id_value(String zp_pos_id_value) {
		this.zp_pos_id_value = zp_pos_id_value;
	}
}
