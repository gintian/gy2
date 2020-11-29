package com.hjsj.hrms.module.recruitment.recruitprocess.actionform;


import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ArrangementInfoForm extends FrameForm{
	//选择人员信息
	private LazyDynaBean resumeInfo = new LazyDynaBean();
	//邮件信息
	private LazyDynaBean emailInfo = new LazyDynaBean();
	//面试安排信息
	private LazyDynaBean arrangementInfo = new LazyDynaBean();
	//面试官信息
	private ArrayList interviewerInfoList = new ArrayList();
	
	@Override
    public void outPutFormHM() {
		this.setResumeInfo((LazyDynaBean)this.getFormHM().get("resumeInfo"));
		this.setEmailInfo((LazyDynaBean)this.getFormHM().get("emailInfo"));
		this.setArrangementInfo((LazyDynaBean)this.getFormHM().get("arrangementInfo"));
		this.setInterviewerInfoList((ArrayList)this.getFormHM().get("interviewerInfoList"));
	}
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("resumeInfo",resumeInfo );
		this.getFormHM().put("emailInfo",emailInfo );
		this.getFormHM().put("arrangementInfo",arrangementInfo );
		this.getFormHM().put("interviewerInfoList",interviewerInfoList );
	}
	
	public void setResumeInfo(LazyDynaBean resumeInfo) {
		this.resumeInfo = resumeInfo;
	}

	public LazyDynaBean getResumeInfo() {
		return resumeInfo;
	}

	public void setEmailInfo(LazyDynaBean emailInfo) {
		this.emailInfo = emailInfo;
	}

	public LazyDynaBean getEmailInfo() {
		return emailInfo;
	}

	public void setArrangementInfo(LazyDynaBean arrangementInfo) {
		this.arrangementInfo = arrangementInfo;
	}

	public LazyDynaBean getArrangementInfo() {
		return arrangementInfo;
	}

	public void setInterviewerInfoList(ArrayList interviewerInfoList) {
		this.interviewerInfoList = interviewerInfoList;
	}

	public ArrayList getInterviewerInfoList() {
		return interviewerInfoList;
	}
	
}
