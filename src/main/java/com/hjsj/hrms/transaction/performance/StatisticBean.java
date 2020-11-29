/*
 * 创建日期 2005-6-29
 *
 */
package com.hjsj.hrms.transaction.performance;
import java.util.ArrayList;

/**
 * @author luangaojiong
 * 
 * 项目及要素Bean
 */
public class StatisticBean {
	private String pointId = "0"; //要素id

	private String pointName = ""; //项目名称

	private String itemId = "0"; //项目id

	private String score = "0";   //要素分数
	private String pointGrade=""; //要素标度

	private String itemName = ""; //项目名称

	private String bodyId = "0"; //分类编号
	private String bodySetScore=""; //主体分类平均分
	private String bodyName = ""; //所有的分类名称

	private String userCount = "0";//所有的测评级别分类
	private String userPercent="0%";

	private ArrayList pointlist = new ArrayList(); //要素ArrayList 第二个循环
	private String itemScore="0";	//项目分值
	/**
	 * 总体评价属性
	 */
	private String gradeId="0";
	private String gradeName="";
	private String gradeCount="";
	private String gradePercent="0%";
	/**
	 * 了解程度
	 *
	 */
	private String knowId="0";
	private String knowName="";
	private String knowCount="";
	private String knowPercent="0%";

	/**
	 * @return 返回 knowCount。
	 */
	public String getKnowCount() {
		return knowCount;
	}
	/**
	 * @param knowCount 要设置的 knowCount。
	 */
	public void setKnowCount(String knowCount) {
		this.knowCount = knowCount;
	}
	/**
	 * @return 返回 knowId。
	 */
	public String getKnowId() {
		return knowId;
	}
	/**
	 * @param knowId 要设置的 knowId。
	 */
	public void setKnowId(String knowId) {
		this.knowId = knowId;
	}
	/**
	 * @return 返回 knowName。
	 */
	public String getKnowName() {
		return knowName;
	}
	/**
	 * @param knowName 要设置的 knowName。
	 */
	public void setKnowName(String knowName) {
		this.knowName = knowName;
	}
	/**
	 * @return 返回 knowPercent。
	 */
	public String getKnowPercent() {
		return knowPercent;
	}
	/**
	 * @param knowPercent 要设置的 knowPercent。
	 */
	public void setKnowPercent(String knowPercent) {
		this.knowPercent = knowPercent;
	}
	/**
	 * @return 返回 itemScore。
	 */
	public String getItemScore() {
		return itemScore;
	}
	/**
	 * @param itemScore 要设置的 itemScore。
	 */
	public void setItemScore(String itemScore) {
		this.itemScore = itemScore;
	}
	public String getUserPercent()
	{
		return this.userPercent;
	}
	
	public void setUserPercent(String userPercent)
	{
		this.userPercent=userPercent;
	}
	/**
	 * 要素ArrayList属性
	 * 
	 * @return
	 */
	public ArrayList getPointlist() {
		return pointlist;
	}

	public void setPointlist(ArrayList pointlist) {
		this.pointlist = pointlist;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getBodyId() {
		return bodyId;
	}

	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}

	public String getBodyName() {
		return bodyName;
	}

	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
	}

	public String getPointId() {
		return pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return 返回 gradeCount。
	 */
	public String getGradeCount() {
		return gradeCount;
	}
	/**
	 * @param gradeCount 要设置的 gradeCount。
	 */
	public void setGradeCount(String gradeCount) {
		this.gradeCount = gradeCount;
	}
	/**
	 * @return 返回 gradeId。
	 */
	public String getGradeId() {
		return gradeId;
	}
	/**
	 * @param gradeId 要设置的 gradeId。
	 */
	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}
	/**
	 * @return 返回 gradeName。
	 */
	public String getGradeName() {
		return gradeName;
	}
	/**
	 * @param gradeName 要设置的 gradeName。
	 */
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}
	/**
	 * @return 返回 gradePercent。
	 */
	public String getGradePercent() {
		return gradePercent;
	}
	/**
	 * @param gradePercent 要设置的 gradePercent。
	 */
	public void setGradePercent(String gradePercent) {
		this.gradePercent = gradePercent;
	}
	public String getPointGrade() {
		return pointGrade;
	}
	public void setPointGrade(String pointGrade) {
		this.pointGrade = pointGrade;
	}
	public String getBodySetScore() {
		return bodySetScore;
	}
	public void setBodySetScore(String bodySetScore) {
		this.bodySetScore = bodySetScore;
	}
}