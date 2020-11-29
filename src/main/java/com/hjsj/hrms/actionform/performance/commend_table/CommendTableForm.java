package com.hjsj.hrms.actionform.performance.commend_table;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class CommendTableForm extends FrameForm {
	/**登录用户看见的表的类型，=0省级单位用表，=1总部用表*/
	private String tableType;
	private String flag;
	private ArrayList commendList = new ArrayList();
	
	/**民主推荐表一 */
	private ArrayList recommend_person_list=new ArrayList();
	private String recommend_unit="";
	private String recommend_flag_item="";
	private String limitNum="0";  //限制数
	private String table1_status="0";
	private String recommend_time;
	private String questionOne;
	private String questionTwo;
	private String questionThree;
	private String questionFour;
	private String questionFive;
	private ArrayList leaderShipList = new ArrayList();
	private String isLeader;
	private ArrayList oneList=new ArrayList();
	private ArrayList twoList=new ArrayList();
	private ArrayList threeList=new ArrayList();
	private ArrayList fourList=new ArrayList();
	private ArrayList fiveList=new ArrayList();
	private String status;
	private String unitCode;
	private String unit;
	private ArrayList  newLeaderList = new ArrayList();
	private String newLeaderStatus;
	private ArrayList fieldList = new ArrayList();
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("newLeaderList", this.getNewLeaderList());
		this.getFormHM().put("newLeaderStatus", this.getNewLeaderStatus());
		this.getFormHM().put("status", this.getStatus());
		this.getFormHM().put("isLeader", this.getIsLeader());
		this.getFormHM().put("questionFive", this.getQuestionFive());
		this.getFormHM().put("leaderShipList", this.getLeaderShipList());
		this.getFormHM().put("questionOne", this.getQuestionOne());
		this.getFormHM().put("questionTwo", this.getQuestionTwo());
		this.getFormHM().put("questionThree", this.getQuestionThree());
		this.getFormHM().put("questionFour", this.getQuestionFour());
		this.getFormHM().put("recommend_time", this.getRecommend_time());
		this.getFormHM().put("tableType", this.getTableType());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("commendList", this.getCommendList());
		this.getFormHM().put("recommend_person_list", this.getRecommend_person_list());
	}

	@Override
    public void outPutFormHM() {
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setNewLeaderList((ArrayList)this.getFormHM().get("newLeaderList"));
		this.setNewLeaderStatus((String)this.getFormHM().get("newLeaderStatus"));
		this.setUnit((String)this.getFormHM().get("unit"));
		this.setUnitCode((String)this.getFormHM().get("unitCode"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setOneList((ArrayList)this.getFormHM().get("oneList"));
		this.setTwoList((ArrayList)this.getFormHM().get("twoList"));
		this.setThreeList((ArrayList)this.getFormHM().get("threeList"));
		this.setFourList((ArrayList)this.getFormHM().get("fourList"));
		this.setFiveList((ArrayList)this.getFormHM().get("fiveList"));
		this.setIsLeader((String)this.getFormHM().get("isLeader"));
		this.setQuestionFive((String)this.getFormHM().get("questionFive"));
		this.setLeaderShipList((ArrayList)this.getFormHM().get("leaderShipList"));
		this.setQuestionFour((String)this.getFormHM().get("questionFour"));
		this.setQuestionOne((String)this.getFormHM().get("questionOne"));
		this.setQuestionThree((String)this.getFormHM().get("questionThree"));
		this.setQuestionTwo((String)this.getFormHM().get("questionTwo"));
		this.setRecommend_time((String)this.getFormHM().get("recommend_time"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setTableType((String)this.getFormHM().get("tableType"));
		this.setCommendList((ArrayList)this.getFormHM().get("commendList"));
		this.setRecommend_person_list((ArrayList)this.getFormHM().get("recommend_person_list"));
		this.setRecommend_flag_item((String)this.getFormHM().get("recommend_flag_item"));
		this.setRecommend_unit((String)this.getFormHM().get("recommend_unit"));
		this.setLimitNum((String)this.getFormHM().get("limitNum"));
		this.setTable1_status((String)this.getFormHM().get("table1_status"));
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	

	public String getRecommend_unit() {
		return recommend_unit;
	}

	public void setRecommend_unit(String recommend_unit) {
		this.recommend_unit = recommend_unit;
	}

	public String getRecommend_flag_item() {
		return recommend_flag_item;
	}

	public void setRecommend_flag_item(String recommend_flag_item) {
		this.recommend_flag_item = recommend_flag_item;
	}

	public ArrayList getRecommend_person_list() {
		return recommend_person_list;
	}

	public void setRecommend_person_list(ArrayList recommend_person_list) {
		this.recommend_person_list = recommend_person_list;
	}

	public String getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(String limitNum) {
		this.limitNum = limitNum;
	}

	public String getTable1_status() {
		return table1_status;
	}

	public void setTable1_status(String table1_status) {
		this.table1_status = table1_status;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ArrayList getCommendList() {
		return commendList;
	}

	public void setCommendList(ArrayList commendList) {
		this.commendList = commendList;
	}

	public String getRecommend_time() {
		return recommend_time;
	}

	public void setRecommend_time(String recommend_time) {
		this.recommend_time = recommend_time;
	}

	public String getQuestionOne() {
		return questionOne;
	}

	public void setQuestionOne(String questionOne) {
		this.questionOne = questionOne;
	}

	public String getQuestionTwo() {
		return questionTwo;
	}

	public void setQuestionTwo(String questionTwo) {
		this.questionTwo = questionTwo;
	}

	public String getQuestionThree() {
		return questionThree;
	}

	public void setQuestionThree(String questionThree) {
		this.questionThree = questionThree;
	}

	public String getQuestionFour() {
		return questionFour;
	}

	public void setQuestionFour(String questionFour) {
		this.questionFour = questionFour;
	}

	public ArrayList getLeaderShipList() {
		return leaderShipList;
	}

	public void setLeaderShipList(ArrayList leaderShipList) {
		this.leaderShipList = leaderShipList;
	}

	public String getQuestionFive() {
		return questionFive;
	}

	public void setQuestionFive(String questionFive) {
		this.questionFive = questionFive;
	}

	public String getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(String isLeader) {
		this.isLeader = isLeader;
	}

	public ArrayList getOneList() {
		return oneList;
	}

	public void setOneList(ArrayList oneList) {
		this.oneList = oneList;
	}

	public ArrayList getTwoList() {
		return twoList;
	}

	public void setTwoList(ArrayList twoList) {
		this.twoList = twoList;
	}

	public ArrayList getThreeList() {
		return threeList;
	}

	public void setThreeList(ArrayList threeList) {
		this.threeList = threeList;
	}

	public ArrayList getFourList() {
		return fourList;
	}

	public void setFourList(ArrayList fourList) {
		this.fourList = fourList;
	}

	public ArrayList getFiveList() {
		return fiveList;
	}

	public void setFiveList(ArrayList fiveList) {
		this.fiveList = fiveList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public ArrayList getNewLeaderList() {
		return newLeaderList;
	}

	public void setNewLeaderList(ArrayList newLeaderList) {
		this.newLeaderList = newLeaderList;
	}

	public String getNewLeaderStatus() {
		return newLeaderStatus;
	}

	public void setNewLeaderStatus(String newLeaderStatus) {
		this.newLeaderStatus = newLeaderStatus;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

}
