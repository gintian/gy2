package com.etong.webclient.actionform;

import java.util.ArrayList;
import com.hrms.struts.action.*;
import com.hrms.struts.valueobject.*;
import org.apache.struts.action.*;
import javax.servlet.http.*;

import com.hrms.frame.dao.RecordVo;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: etong
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 */

public class NodeForm extends FrameForm {
	public NodeForm() {
	}

	private String name;

	private String org_id;

	private String content;

	private String theday;

	private RecordVo money_vo = new RecordVo("t_demo_money");

	/**
	 * 请假单
	 */
	private RecordVo leave_vo = new RecordVo("t_demo_leave");

	private RecordVo advice_vo = new RecordVo("t_bpm_advice");

	@Override
    public void outPutFormHM() {
		this.setAdvice_vo((RecordVo) this.getFormHM().get("advice_vo"));
		this.setLeave_vo((RecordVo) this.getFormHM().get("leave_vo"));
		this.setMoney_vo((RecordVo) this.getFormHM().get("money_vo"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("org_id", this.getOrg_id());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("theday", this.getTheday());
		this.getFormHM().put("leave_vo", this.getLeave_vo());
		this.getFormHM().put("money_vo", this.getMoney_vo());
	}

	public RecordVo getAdvice_vo() {
		return advice_vo;
	}

	public void setAdvice_vo(RecordVo advice_vo) {
		this.advice_vo = advice_vo;
	}

	public String getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public String getOrg_id() {
		return org_id;
	}

	public String getTheday() {
		return theday;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public void setTheday(String theday) {
		this.theday = theday;
	}

	public RecordVo getLeave_vo() {
		return leave_vo;
	}

	public void setLeave_vo(RecordVo leave_vo) {
		this.leave_vo = leave_vo;
	}

	@Override
    public void reset(ActionMapping parm1, HttpServletRequest parm2) {

		super.reset(parm1, parm2);
	}

	public RecordVo getMoney_vo() {
		return money_vo;
	}

	public void setMoney_vo(RecordVo money_vo) {
		this.money_vo = money_vo;
	}

}