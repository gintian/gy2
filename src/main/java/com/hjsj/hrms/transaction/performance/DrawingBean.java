/*
 * 创建日期 2005-7-7
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

/**
 * @author luangaojiong
 *	统计图对象Bean
 */
public class DrawingBean {
	
	  private String selfFraction="0";		//个人分数
	  private String maxFraction="0";		//最大分
	  private String averageFraction="0";	//平均分
	  private String pointId="0";			//要素id	
	  private String pointName="";			//要素名称
	  public String getSelfFraction() {
	    return selfFraction;
	  }
	  public void setSelfFraction(String selfFraction) {
	    this.selfFraction = selfFraction;
	  }
	  public String getMaxFraction() {
	    return maxFraction;
	  }
	  public void setMaxFraction(String maxFraction) {
	    this.maxFraction = maxFraction;
	  }
	  public String getAverageFraction() {
	    return averageFraction;
	  }
	  public void setAverageFraction(String averageFraction) {
	    this.averageFraction = averageFraction;
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


}
