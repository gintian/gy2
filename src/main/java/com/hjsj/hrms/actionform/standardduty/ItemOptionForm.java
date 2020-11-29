package com.hjsj.hrms.actionform.standardduty;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemOptionForm extends FrameForm {

	ArrayList relevantitem = new ArrayList();

	String fieldsetid = "";
	String targetsetid = "";
	String fieldsetdesc="";
	String targetsetdesc="";
    public String getFieldsetdesc() {
		return fieldsetdesc;
	}

	public void setFieldsetdesc(String fieldsetdesc) {
		this.fieldsetdesc = fieldsetdesc;
	}

	public String getTargetsetdesc() {
		return targetsetdesc;
	}

	public void setTargetsetdesc(String targetsetdesc) {
		this.targetsetdesc = targetsetdesc;
	}

	String submitflag= "not";
	public String getSubmitflag() {
		return submitflag;
	}

	public void setSubmitflag(String submitflag) {
		this.submitflag = submitflag;
	}

	String[] saveitems;
	ArrayList targetids=null;
	
	ArrayList duty = new ArrayList();
	ArrayList sduty = new ArrayList();
	HashMap sdutyitem = new HashMap();
	HashMap dutyitem = new HashMap();
	HashMap relevantset = new HashMap();
	ArrayList sourceitems = new ArrayList();
	ArrayList targetitems = new ArrayList();

	String selectset = "";// 没有实际意义，不用管


	

	public ArrayList getTargetids() {
		return targetids;
	}

	public void setTargetids(ArrayList targetids) {
		this.targetids = targetids;
	}

	public String getTargetsetid() {
		return targetsetid;
	}

	public void setTargetsetid(String targetsetid) {
		this.targetsetid = targetsetid;
	}

	public String[] getSaveitems() {
		return saveitems;
	}

	public void setSaveitems(String[] saveitems) {
		this.saveitems = saveitems;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getSelectset() {
		return selectset;
	}

	public void setSelectset(String selectset) {
		this.selectset = selectset;
	}

	public ArrayList getSourceitems() {
		return sourceitems;
	}

	public ArrayList getRelevantitem() {
		return relevantitem;
	}

	public void setRelevantitem(ArrayList relevantitem) {
		this.relevantitem = relevantitem;
	}

	public void setSourceitems(ArrayList sourceitems) {
		this.sourceitems = sourceitems;
	}

	public ArrayList getTargetitems() {
		return targetitems;
	}

	public void setTargetitems(ArrayList targetitems) {
		this.targetitems = targetitems;
	}

	public HashMap getRelevantset() {
		return relevantset;
	}

	public void setRelevantset(HashMap relevantset) {
		this.relevantset = relevantset;
	}

	public ArrayList getDuty() {
		return duty;
	}

	public void setDuty(ArrayList duty) {
		this.duty = duty;
	}

	public ArrayList getSduty() {
		return sduty;
	}

	public void setSduty(ArrayList sduty) {
		this.sduty = sduty;
	}

	public HashMap getSdutyitem() {
		return sdutyitem;
	}

	public void setSdutyitem(HashMap sdutyitem) {
		this.sdutyitem = sdutyitem;
	}

	public HashMap getDutyitem() {
		return dutyitem;
	}

	public void setDutyitem(HashMap dutyitem) {
		this.dutyitem = dutyitem;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dutyitem", getDutyitem());
		this.getFormHM().put("sdutyitem", getSdutyitem());
		this.getFormHM().put("fieldsetid", getFieldsetid());
		this.getFormHM().put("saveitems", getSaveitems());
		this.getFormHM().put("targetsetid", getTargetsetid());
		this.getFormHM().put("targetids", getTargetids());
		this.getFormHM().put("duty", getDuty());
		this.getFormHM().put("sduty", getSduty());
	}

	@Override
    public void outPutFormHM() {

		Map forms = this.getFormHM();
		setDuty((ArrayList) forms.get("duty"));
		setSduty((ArrayList) forms.get("sduty"));
		setDutyitem((HashMap) forms.get("dutyitem"));
		setSdutyitem((HashMap) forms.get("sdutyitem"));
		setRelevantset((HashMap) forms.get("relevantset"));
		setSourceitems((ArrayList) forms.get("sourceitems"));
		setTargetitems((ArrayList) forms.get("targetitems"));
		setRelevantitem((ArrayList) forms.get("relevantitem"));
		setFieldsetid((String)forms.get("fieldsetid"));
		setTargetsetid((String)forms.get("targetsetid"));
		setSubmitflag((String)forms.get("submitflag"));
		setFieldsetdesc((String)forms.get("fieldsetdesc"));
		setTargetsetdesc((String)forms.get("targetsetdesc"));
        setTargetids((ArrayList)forms.get("targetids"));
	}

}
