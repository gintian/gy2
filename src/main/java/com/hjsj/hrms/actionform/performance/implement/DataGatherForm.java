package com.hjsj.hrms.actionform.performance.implement;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * <p>Title:DataGatherForm.java</p>
 * <p>Description:考核实施/数据采集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-12-17 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class DataGatherForm extends FrameForm 
{
	
	private ArrayList planList=new ArrayList();
	private String planId="";
	private String gather_type="0";         // 0:手动 1:机读 2:网上+机读
	private String gradeHtml="";
	private ArrayList mainbodyList=new ArrayList();
	private String perPointNoGrade="0";      //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
	private String isEntireysub="False";     //提交是否必填
	private String isScoreMainbody="0";  //是否是必打分的考核主体
	private String objectType="2";
	private String object_id="";
	private String mainbody_id="";
	private String vote="";          //票数总人数；
	private ArrayList fullvoteList = new ArrayList();
	
	private String scoreflag="1";    //=2混合，=1标度(默认值=混合)
	private ArrayList objectsList=new ArrayList();
	
	private String fromUrl="0";  // 0:绩效实施模块进入  1:菜单进入
	private Hashtable planParamSet = new Hashtable();
	private String picSrc="";
	private String picWidth="800";  // 图片的宽度
	private String picHeight="600";  // 图片的高度
	private ArrayList planBodys = new ArrayList();//计划的主体类别
	private String busitype="";//0 绩效  1 能力素质
	

	@Override
    public void inPutTransHM()
	{
		
		this.getFormHM().put("picWidth",this.getPicWidth());
		this.getFormHM().put("picHeight",this.getPicHeight());
		this.getFormHM().put("planId",this.getPlanId());
		this.getFormHM().put("vote", this.getVote());
		this.getFormHM().put("fullvoteList", this.getFullvoteList());
		this.getFormHM().put("planParamSet", this.getPlanParamSet());
		this.getFormHM().put("picSrc", this.getPicSrc());
		this.getFormHM().put("planBodys", this.getPlanBodys());
		this.getFormHM().put("busitype", this.getBusitype());
	}

	@Override
    public void outPutFormHM()
	{
		
		this.setPicWidth((String)this.getFormHM().get("picWidth"));
		this.setPicHeight((String)this.getFormHM().get("picHeight"));
		this.setPlanBodys((ArrayList)this.getFormHM().get("planBodys"));
		this.setPicSrc((String)this.getFormHM().get("picSrc"));
		this.setPlanParamSet((Hashtable)this.getFormHM().get("planParamSet"));
		this.setFromUrl((String)this.getFormHM().get("fromUrl"));
		this.setMainbodyList((ArrayList)this.getFormHM().get("mainbodyList"));
		this.setGather_type((String)this.getFormHM().get("gather_type"));
		this.setPlanList((ArrayList)this.getFormHM().get("planList"));
		this.setPlanId((String)this.getFormHM().get("planId"));
		this.setGradeHtml((String)this.getFormHM().get("gradeHtml"));
		this.setPerPointNoGrade((String)this.getFormHM().get("perPointNoGrade"));
		this.setIsEntireysub((String)this.getFormHM().get("isEntireysub"));
		this.setObject_id((String)this.getFormHM().get("object_id"));
		this.setMainbody_id((String)this.getFormHM().get("mainbody_id"));
		this.setVote((String)this.getFormHM().get("vote"));
		this.setFullvoteList((ArrayList) this.getFormHM().get("fullvoteList"));
		this.setGather_type((String)this.getFormHM().get("gather_type"));
		this.setObjectType((String)this.getFormHM().get("objectType"));
		this.setIsScoreMainbody((String)this.getFormHM().get("isScoreMainbody"));
		this.setScoreflag((String)this.getFormHM().get("scoreflag"));
		this.setObjectsList((ArrayList)this.getFormHM().get("objectsList"));
		this.setIsScoreMainbody((String)this.getFormHM().get("isScoreMainbody"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
	}

	public String getGradeHtml() {
		return gradeHtml;
	}

	public void setGradeHtml(String gradeHtml) {
		this.gradeHtml = gradeHtml;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public ArrayList getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList planList) {
		this.planList = planList;
	}

	public String getPerPointNoGrade() {
		return perPointNoGrade;
	}

	public void setPerPointNoGrade(String perPointNoGrade) {
		this.perPointNoGrade = perPointNoGrade;
	}

	public String getMainbody_id() {
		return mainbody_id;
	}

	public void setMainbody_id(String mainbody_id) {
		this.mainbody_id = mainbody_id;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getIsEntireysub() {
		return isEntireysub;
	}

	public void setIsEntireysub(String isEntireysub) {
		this.isEntireysub = isEntireysub;
	}

	public String getGather_type() {
		return gather_type;
	}

	public void setGather_type(String gather_type) {
		this.gather_type = gather_type;
	}
	
	public String getVote() {
		return vote;
	}

	public void setVote(String vote) {
		this.vote = vote;
	}
	
	public ArrayList getFullvoteList() {
		return fullvoteList;
	}
	public void setFullvoteList(ArrayList fullvoteList) {
		this.fullvoteList = fullvoteList;
	}

	public ArrayList getMainbodyList() {
		return mainbodyList;
	}

	public void setMainbodyList(ArrayList mainbodyList) {
		this.mainbodyList = mainbodyList;
	}

	public ArrayList getObjectsList() {
		return objectsList;
	}

	public void setObjectsList(ArrayList objectsList) {
		this.objectsList = objectsList;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getScoreflag() {
		return scoreflag;
	}

	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}

	public String getIsScoreMainbody() {
		return isScoreMainbody;
	}

	public void setIsScoreMainbody(String isScoreMainbody) {
		this.isScoreMainbody = isScoreMainbody;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public Hashtable getPlanParamSet() {
		return planParamSet;
	}

	public void setPlanParamSet(Hashtable planParamSet) {
		this.planParamSet = planParamSet;
	}

	public String getPicSrc() {
		return picSrc;
	}

	public void setPicSrc(String picSrc) {
		this.picSrc = picSrc;
	}

	public ArrayList getPlanBodys() {
		return planBodys;
	}

	public void setPlanBodys(ArrayList planBodys) {
		this.planBodys = planBodys;
	}

	public String getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(String picWidth) {
		this.picWidth = picWidth;
	}

	public String getPicHeight() {
		return picHeight;
	}

	public void setPicHeight(String picHeight) {
		this.picHeight = picHeight;
	}
	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
}
