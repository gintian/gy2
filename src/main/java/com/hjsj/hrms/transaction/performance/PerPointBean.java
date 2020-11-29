/*
 * 创建日期 2005-6-24
 *
 *
 */
package com.hjsj.hrms.transaction.performance;

import java.util.ArrayList;

/**
 * @author luangaojiong
 *
 * 考核指标要素表Bean
 */
public class PerPointBean {
	ArrayList perGradelist=new ArrayList();	//标度表对象
	double maxScore=0;						//最大上限分值
	String point_id="0";					//要素号
	int pointkind=0;						//要素类型
	String value="0";						//显示的值
	String type="0";						//定性与定量标识
	int 	topNum=0;						//定量上限值
	int bottomNum=0;						//定量下限值
	String gradecode="";					//标准代码值
	double score=0;							//定量指标计算出来的分值
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getGradecode() {
		return gradecode;
	}
	public void setGradecode(String gradecode) {
		this.gradecode = gradecode;
	}
	public int getTopNum() 
	{
	    return topNum;
	}
	  public void setTopNum(int topNum)
	{
	    this.topNum = topNum;
	}
	  public int getBottomNum() 
	{
	    return bottomNum;
    }
	  public void setBottomNum(int bottomNum) 
	{
	    this.bottomNum = bottomNum;
	}
	public void setType(String type)
	{
		this.type=type;
	}
	public String getType()
	{
		return this.type;
	}
	public void setPerGradelist(ArrayList perGradelist)
	{
		this.perGradelist=perGradelist;
	}
	public ArrayList getPerGradelist()
	{
		return this.perGradelist;
	}
	
	public void setMaxScore(double maxScore)
	{
		this.maxScore=maxScore;
	}
	
	public double getMaxScore()
	{
		return this.maxScore;
	}
	
	public void setPoint_id(String point_id)
	{
		this.point_id=point_id;
	}
	
	public String getPoint_id()
	{
		return this.point_id;
	}
	
	public void setPointkind(int pointkind)
	{
		this.pointkind=pointkind;
	}
	
	public int getPointkind()
	{
		return this.pointkind;
	}
	public void setValue(String value)
	{
		this.value=value;
	}
	public String getValue()
	{
		return this.value;
	}
	

}
