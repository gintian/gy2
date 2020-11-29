package com.hjsj.hrms.actionform.performance.commend.choosecandidate;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>title:CandidateForm.java</p>
 * <p>description:推荐候选人的form</p>
 * <p>company:HJSJ</p>
 * <p>create time: 2007-06-04 08:51:02 am</p>
 * @author lizhenwei
 * @version 4.0
 */

public class ChooseCandidateForm extends FrameForm{
	/**
	 * 最多推荐人数
	 */
	private String ctrl_param="";
	/**
	 * 控制提交按钮是否出现
	 */
	private String isSubmit="";
	/**
	 * 后备推荐记录列表
	 */
	private ArrayList commendList = new ArrayList();
	/**
	 * 对应登录用户的候选人列表
	 */
	private ArrayList candidateList = new ArrayList();
	/**               
	 * 候选人的A0100
	 */
    private String A0100="";
    /**
     * 后备推荐的id
     */
    private String p0201="";
    /**
     * 只有一条后备推荐,控制前台显示格式
     */
    private String onlyOne="";
    /**
     * 应用库前缀
     */
    private String dbpre="";
    private String p0203="";
    /**
     * 选择的人员
     */
    private String[] choose_per = new String[0];
    private String size;
    /**推荐职务指标的codesetid*/
    private String codesetid;
	private String isNull;
    

	@Override
    public void outPutFormHM() {
		this.setCandidateList((ArrayList)this.getFormHM().get("candidateList"));
		this.setCommendList((ArrayList)this.getFormHM().get("commendList"));
		this.setCtrl_param((String)this.getFormHM().get("ctrl_param"));
		this.setIsSubmit((String)this.getFormHM().get("isSubmit"));
		this.setOnlyOne((String)this.getFormHM().get("onlyOne"));
		this.setP0203((String)this.getFormHM().get("p0203"));
		this.setChoose_per((String[])this.getFormHM().get("choose_per"));
		this.setP0201((String)this.getFormHM().get("p0201"));
		this.setSize((String)this.getFormHM().get("size"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setIsNull((String)this.getFormHM().get("isNull"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("p0201",this.getP0201());
		this.getFormHM().put("ctrl_param",this.getCtrl_param());
		this.getFormHM().put("choose_per",this.getChoose_per());
		this.getFormHM().put("codesetid",this.getCodesetid());
		this.getFormHM().put("isNull",this.getIsNull());
	}

	

	public String getA0100() {
		return A0100;
	}

	public void setA0100(String a0100) {
		A0100 = a0100;
	}

	public ArrayList getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(ArrayList candidateList) {
		this.candidateList = candidateList;
	}

	public ArrayList getCommendList() {
		return commendList;
	}

	public void setCommendList(ArrayList commendList) {
		this.commendList = commendList;
	}

	public String getCtrl_param() {
		return ctrl_param;
	}

	public void setCtrl_param(String ctrl_param) {
		this.ctrl_param = ctrl_param;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getIsSubmit() {
		return isSubmit;
	}

	public void setIsSubmit(String isSubmit) {
		this.isSubmit = isSubmit;
	}

	public String getOnlyOne() {
		return onlyOne;
	}

	public void setOnlyOne(String onlyOne) {
		this.onlyOne = onlyOne;
	}

	public String getP0201() {
		return p0201;
	}

	public void setP0201(String p0201) {
		this.p0201 = p0201;
	}

	public String getP0203() {
		return p0203;
	}

	public void setP0203(String p0203) {
		this.p0203 = p0203;
	}

	public String[] getChoose_per() {
		return choose_per;
	}

	public void setChoose_per(String[] choose_per) {
		this.choose_per = choose_per;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

}
